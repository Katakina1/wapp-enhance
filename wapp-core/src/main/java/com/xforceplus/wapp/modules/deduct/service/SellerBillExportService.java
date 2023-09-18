package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.export.dto.BillExportDto;
import com.xforceplus.wapp.modules.deduct.dto.QuerySellerDeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QuerySellerDeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.SellerDeductExportRequest;
import com.xforceplus.wapp.modules.deduct.model.ExportSellerClaimBillModel;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.entity.TDxExcelExportlogEntity;
import com.xforceplus.wapp.repository.entity.TDxMessagecontrolEntity;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.BillExportCallable;
import com.xforceplus.wapp.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Describe: 供应商侧-业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Service
@Slf4j
public class SellerBillExportService extends ExportService<SellerDeductExportRequest> {

    @Autowired
    private SellerBillQueryService sellerBillQueryService;

    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;
    @Autowired
    private CommonMessageService commonMessageService;

    /**
     * 业务单导出
     *
     * @param request
     * @return
     */
    @Override
    public R export(SellerDeductExportRequest request) {
        TXfDeductionBusinessTypeEnum typeEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class,
                Integer.parseInt(request.getBusinessType())).get();
        QuerySellerDeductListRequest querySellerDeductListRequest = BeanUtils.copyProperties(request, QuerySellerDeductListRequest.class);
        PageResult<QuerySellerDeductListResponse> pageResult = sellerBillQueryService.queryPageList(querySellerDeductListRequest);
        log.info("doExport querySellerDeductListResponse:{}", JSON.toJSONString(pageResult));
        List<QuerySellerDeductListResponse> pageResultRows = pageResult.getRows();
        if (CollectionUtils.isEmpty(pageResultRows)) {
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
            BillExportDto dto = new BillExportDto();
            dto.setWaitKey(checkExportLockRs.getResult().toString());
            dto.setType(typeEnum);
            dto.setRequest(request);
            dto.setUserId(UserUtil.getUserId());
            dto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            excelExportlogEntity.setUserAccount(dto.getUserId().toString());
            excelExportlogEntity.setUserName(dto.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
            excelExportlogEntity.setServiceType(ExcelExportLogService.SERVICE_TYPE);
            excelExportLogService.save(excelExportlogEntity);
            dto.setLogId(excelExportlogEntity.getId());
            BillExportCallable callable = new BillExportCallable(this, dto);
            ThreadPoolManager.submitCustomL1(callable);
        }catch (Exception e) {
            log.error("沃尔玛-业务单导出发起异步调用异常:{}", e);
            clearLock(typeEnum, UserUtil.getUserId());
        }
        return R.ok("业务单据导出正在处理，请在消息中心");
    }

    /**
     * 执行导出业务
     *
     * @param exportDto
     * @return
     */
    @Override
    public boolean doExport(BillExportDto exportDto) {
        boolean flag = true;
        QuerySellerDeductListRequest request = (QuerySellerDeductListRequest) exportDto.getRequest();
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
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "业务单信息").build();

            // excel 序号
            int number = 1;
            for (int i = 1; i <= getPages(); i++) {
                QuerySellerDeductListRequest deductListRequest = BeanUtils.copyProperties(request, QuerySellerDeductListRequest.class);
                deductListRequest.setPageNo(i);
                deductListRequest.setPageSize(billExportPageSize);
                PageResult<QuerySellerDeductListResponse> pageRs = sellerBillQueryService.queryPageList(deductListRequest);
                if (CollectionUtils.isEmpty(pageRs.getRows())) {
                    break;
                }
                List<ExportSellerClaimBillModel> exportList = BeanUtils.copyList(pageRs.getRows(), ExportSellerClaimBillModel.class);
                for (ExportSellerClaimBillModel model : exportList) {
                    model.setNum(number);
                    number++;
                }
                writeSheet.setClazz(ExportSellerClaimBillModel.class);
                excelWriter.write(exportList, writeSheet);

            }
            excelWriter.finish();
            // 推送sftp
            String ftpFilePath = ftpPath + "/" + excelFileName;
            in = new ByteArrayInputStream(out.toByteArray());
            ftpUtilService.uploadFile(ftpPath, excelFileName, in);
            messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
            excelExportlogEntity.setFilepath(ftpFilePath);
            messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出成功");
        } catch (Exception e) {
            log.error("供应商侧【%s】业务单导出异常：{}",exportDto.getType().getDes(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
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
            // 清理分布式锁
            clearLock(exportDto.getType(), exportDto.getUserId());
        }
        return flag;
    }


}