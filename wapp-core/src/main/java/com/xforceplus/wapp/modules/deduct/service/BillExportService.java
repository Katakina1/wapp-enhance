package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.export.dto.BillExportDto;
import com.xforceplus.wapp.modules.deduct.dto.DeductDetailResponse;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.model.ExportAgreementBillModel;
import com.xforceplus.wapp.modules.deduct.model.ExportClaimBillItemModel;
import com.xforceplus.wapp.modules.deduct.model.ExportClaimBillModel;
import com.xforceplus.wapp.modules.deduct.model.ExportEPDBillModel;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.BillExportCallable;
import com.xforceplus.wapp.util.StopWatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;


/**
 * Describe: 沃尔玛侧-业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Service
@Slf4j
public class BillExportService extends ExportService<DeductExportRequest> {

    @Autowired
    private BillQueryService billQueryService;
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    DeductService deductService;


    /**
     * 业务导出
     *
     * @param request
     * @return
     */
    @Override
    public R export(DeductExportRequest request) {
        TXfDeductionBusinessTypeEnum typeEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class, Integer.parseInt(request.getBusinessType())).get();

        // 数据校验
        DeductExportRequest deductExportRequest = com.xforceplus.wapp.util.BeanUtils.copyProperties(request, DeductExportRequest.class);
        deductExportRequest.setTotalFalg(Boolean.TRUE);
        PageResult<QueryDeductListResponse> pageResult = billQueryService.queryPageList(deductExportRequest);
        List<QueryDeductListResponse> queryDeductListResponse = pageResult.getRows();
        if (org.apache.commons.collections.CollectionUtils.isEmpty(queryDeductListResponse)) {
            log.info("沃尔玛-业务单导出--未查到数据");
            return R.fail("未查询到数据");
        }
        // 导出数量上限判断
        if (pageResult.getSummary().getTotal() > billExportLimit) {
            return R.fail(String.format("操作失败,数据导出上限为:%s", billExportLimit));
        }

		try {
			// 单据类型+用户ID
			R checkExportLockRs = checkExportLock(typeEnum, UserUtil.getUserId());
			if (!R.OK.equals(checkExportLockRs.getCode())) {
				return checkExportLockRs;
			}
			BillExportDto<DeductExportRequest> dto = new BillExportDto<>();
			dto.setWaitKey(checkExportLockRs.getResult().toString());
			dto.setType(typeEnum);
			dto.setRequest(request);
			dto.setUserId(UserUtil.getUserId());
			dto.setLoginName(UserUtil.getLoginName());
			TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
			excelExportlogEntity.setCreateDate(new Date());
			excelExportlogEntity.setUserAccount(dto.getUserId().toString());
			excelExportlogEntity.setUserName(dto.getLoginName());
			excelExportlogEntity.setConditions(JSON.toJSONString(dto));
			excelExportlogEntity.setStartDate(new Date());
			excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
			excelExportlogEntity.setServiceType(SERVICE_TYPE);
			excelExportLogService.save(excelExportlogEntity);
			dto.setLogId(excelExportlogEntity.getId());
			BillExportCallable callable = new BillExportCallable(this, dto);
			ThreadPoolManager.submitCustomL1(callable);
		} catch (Exception e) {
			log.error("供应商侧-业务单导出发起异步调用异常:{}", e);
			clearLock(typeEnum, UserUtil.getUserId());
		}
		return R.ok("单据导出正在处理，请在消息中心");
    }

    @Override
    public boolean doExport(BillExportDto exportDto) {
        log.info("供应商侧-业务单导出logId:{}, 请求入参:{}", exportDto.getLogId(), JSONUtil.toJsonStr(exportDto));
        boolean flag = true;
        DeductExportRequest request = (DeductExportRequest) exportDto.getRequest();
        TXfDeductionBusinessTypeEnum typeEnum = exportDto.getType();
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
        messagecontrolEntity.setContent(getSuccContent());
        final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), exportDto.getType().getDes());
        ExcelWriter excelWriter;
        ByteArrayInputStream in = null;
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            excelWriter = EasyExcel.write(out).excelType(ExcelTypeEnum.XLSX).build();
            //创建一个sheet
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "主信息").build();
            WriteSheet itemWriteSheet = null;
            List exportList = new LinkedList<>();

            //只有索赔单有明细信息
            if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)) {
                //创建一个新的sheet
                itemWriteSheet = EasyExcel.writerSheet(1, "明细信息").build();
            }
            String key = String.format(BILL_EXPORT_KEY, typeEnum.getValue(), UserUtil.getUserId());
            StopWatchUtils.createStarted(key);
            for (int i = 1; i <= getPages(); i++) {
                DeductExportRequest exportRequest = com.xforceplus.wapp.util.BeanUtils.copyProperties(request, DeductExportRequest.class);
                exportRequest.setPageNo(i);
                exportRequest.setPageSize(billExportPageSize);
                List<QueryDeductListResponse> queryList = billQueryService.queryPageList(exportRequest).getRows();
                if (CollectionUtils.isEmpty(queryList)) {
                    break;
                }
                if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)) {
                    StopWatchUtils.start("写入Excel主信息");
                    exportList = convertBillExcel(queryList);
                    writeSheet.setClazz(ExportClaimBillModel.class);
                    excelWriter.write(exportList, writeSheet);
                    StopWatchUtils.stop();
                    // 明细处理
                    StopWatchUtils.start("写入Excel明细");
                    if (request.getExportDataType() != null && request.getExportDataType() == 2) {
                        List<ExportClaimBillItemModel> exportItem = getExportItem(queryList.stream().map(QueryDeductListResponse::getId).collect(Collectors.toList()));
                        itemWriteSheet.setClazz(ExportClaimBillItemModel.class);
                        excelWriter.write(exportItem, itemWriteSheet);
                    }
                    StopWatchUtils.stop();
                } else if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum)) {
                    exportList = com.xforceplus.wapp.util.BeanUtils.copyList(queryList, ExportAgreementBillModel.class);
                    writeSheet.setClazz(ExportAgreementBillModel.class);
                    excelWriter.write(exportList, writeSheet);
                } else {
                    exportList = com.xforceplus.wapp.util.BeanUtils.copyList(queryList, ExportEPDBillModel.class);
                    writeSheet.setClazz(ExportEPDBillModel.class);
                    excelWriter.write(exportList, writeSheet);
                }

            }

            excelWriter.finish();
            //推送sftp
            String ftpFilePath = ftpPath + "/" + excelFileName;
            in = new ByteArrayInputStream(out.toByteArray());
            ftpUtilService.uploadFile(ftpPath, excelFileName, in);
            messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
            excelExportlogEntity.setFilepath(ftpFilePath);
            messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出成功");
            StopWatchUtils.stopSumSummary();
        } catch (Exception e) {
            log.error("沃尔玛侧【%s】业务单导出异常：{}", exportDto.getType().getDes(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.toString());
            messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出失败");
            messagecontrolEntity.setContent(exportCommonService.getFailContent(e.getMessage()));
            flag = false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            excelExportlogEntity.setEndDate(new Date());
            excelExportLogService.updateById(excelExportlogEntity);
            commonMessageService.sendMessage(messagecontrolEntity);
            log.info("供应商侧-业务单导出完成，logId:{}, 导出结果:{}, 请求入参：{}", exportDto.getLogId(),
                    excelExportlogEntity.getExportStatus(),
                    JSONUtil.toJsonStr(exportDto));
            // 清理分布式锁
            clearLock(typeEnum, exportDto.getUserId());

        }
        return flag;
    }

    /**
     * 导出明细
     *
     * @param idList
     * @return
     */
    private List<ExportClaimBillItemModel> getExportItem(List<Long> idList) {
        List<ExportClaimBillItemModel> response = new ArrayList<>();
        for (Long id : idList) {
            DeductDetailResponse deductDetailById = deductService.getDeductDetailById(id);
            if (deductDetailById != null && CollectionUtils.isNotEmpty(deductDetailById.getDeductBillItemList())) {
                BeanUtil.copyList(deductDetailById.getDeductBillItemList(), response,
                        ExportClaimBillItemModel.class);
            }
        }
        return response;
    }

    private List<ExportClaimBillModel> convertBillExcel(List<QueryDeductListResponse> queryList) throws BeansException {
        List<ExportClaimBillModel> list = new ArrayList<>();
        for (QueryDeductListResponse item : queryList) {
            ExportClaimBillModel exportClaimBillModel = new ExportClaimBillModel();
            exportClaimBillModel.setQueryTab(item.getQueryTab());
            exportClaimBillModel.setBusinessNo(item.getBusinessNo());
            exportClaimBillModel.setRefSettlementNo(item.getRefSettlementNo());
            exportClaimBillModel.setDeductDate(item.getDeductDate());
            exportClaimBillModel.setPurchaserName(item.getPurchaserName());
            exportClaimBillModel.setSellerNo(item.getSellerNo());
            exportClaimBillModel.setSellerName(item.getSellerName());
            exportClaimBillModel.setAmountWithTax(item.getAmountWithTax());
            exportClaimBillModel.setTaxRate(item.getTaxRate());
            exportClaimBillModel.setAmountWithoutTax(item.getAmountWithoutTax());
            exportClaimBillModel.setTaxAmount(item.getTaxAmount());
            exportClaimBillModel.setInvoiceType(item.getInvoiceType());
            exportClaimBillModel.setVerdictDate(item.getVerdictDate());
            log.info("verdictDate1:{}",item.getVerdictDate());
            exportClaimBillModel.setBatchNo(item.getBatchNo());
            exportClaimBillModel.setRemark(item.getRemark());
            exportClaimBillModel.setDeductInvoice(item.getDeductInvoice());
            exportClaimBillModel.setItemWithoutAmount(item.getItemWithoutAmount());
            exportClaimBillModel.setItemWithAmount(item.getItemWithAmount());
            exportClaimBillModel.setItemTaxAmount(item.getItemTaxAmount());
            exportClaimBillModel.setRedNotificationStatus(item.getRedNotificationStatus());
            exportClaimBillModel.setRedNotificationNos(item.getRedNotificationNos());
            exportClaimBillModel.setExceptionDescription(item.getExceptionDescription());
            list.add(exportClaimBillModel);
        }
        return list;
    }

}
