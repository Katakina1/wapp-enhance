package com.xforceplus.wapp.modules.exchangeTicket.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.AuthStatusEnum;
import com.xforceplus.wapp.enums.ExchangeTickeyStatusEnum;
import com.xforceplus.wapp.export.dto.ExceptionReportExportDto;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.exchangeTicket.convert.ExchangeTicketConverter;
import com.xforceplus.wapp.modules.exchangeTicket.dto.*;
import com.xforceplus.wapp.modules.exchangeTicket.listener.ExchangeTicketImportListener;
import com.xforceplus.wapp.modules.exchangeTicket.listener.UpdateVoucherNoImportListener;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TDxInvoiceDao;
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfExchangeTicketDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.StaticString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.enums.ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_FAIL;
import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 换票业务逻辑
 */
@Service
@Slf4j
public class ExchangeTicketService extends ServiceImpl<TXfExchangeTicketDao, TXfExchangeTicketEntity> {

    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private ExchangeTicketConverter exchangeTicketConverter;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    ExportCommonService exportCommonService;
    @Autowired
    private ExcelExportLogService excelExportLogService;

    @Value("${wapp.export.tmp}")
    private String tmp;

    @Autowired
    private TXfExchangeTicketDao tXfExchangeTicketDao;

    public R importFile(MultipartFile file) {
        ExchangeTicketImportListener listener = new ExchangeTicketImportListener();
        try {
            EasyExcel.read(file.getInputStream(), ExchangeTicketImportDto.class, listener).sheet().doRead();
            if (CollectionUtils.isEmpty(listener.getValidInvoices()) && CollectionUtils.isEmpty(listener.getInvalidInvoices())) {
                return R.fail("未解析到数据");
            }
            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {

                for (int i = 0; i < listener.getInvalidInvoices().size(); i++) {
                    builder.append("第" + listener.getInvalidInvoices().get(i).getRowNum() + "行");
                    builder.append(listener.getInvalidInvoices().get(i).getErrorMsg());
                }
                return R.fail(builder.toString());
            }
            List<ExchangeTicketImportDto> distctList = listener.getValidInvoices().stream().collect(Collectors.collectingAndThen(
                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(f -> f.getInvoiceNo() + f.getInvoiceCode()))), ArrayList::new)
            );
//            String arr[] = {"B0003", "100", "A0010", "0", "11", "B0004", "B0001"};
            // 11状态代表已处理,符合换票标准
            String arr[] = {"B0003", "100", "A0010", "0", "B0004", "B0001"};
            List<String> list = Arrays.asList(arr);
            String brr[] = {ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_SH.getCode(), ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_PRE.getCode(), ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_UPLOAD.getCode()
                    , ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_DONE.getCode()};
            List<String> exchangeList = Arrays.asList(brr);
            for (int j = 0; j < distctList.size(); j++) {
            	ExchangeTicketImportDto dto = distctList.get(j);
				if (StringUtils.isEmpty(dto.getInvoiceCode())) {
					dto.setInvoiceCode("");
				}
				if (StringUtils.isEmpty(dto.getInvoiceNo())) {
					dto.setInvoiceNo("");
				}
				QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
				wrapper.eq(TDxRecordInvoiceEntity.UUID, dto.getInvoiceCode() + dto.getInvoiceNo());
				TDxRecordInvoiceEntity entity = tDxRecordInvoiceDao.selectOne(wrapper);
                if (Objects.isNull(entity)) {
                    //builder.append("第").append(dto.getRowNum()).append("行");
                    builder.append("发票代码:").append(dto.getInvoiceCode()).append(",发票号码:").append(dto.getInvoiceNo()).append("不存在");
                } else {
                    if (list.contains(entity.getHostStatus())) {
                        builder.append("发票未写屏，不符合换票要求，");
                    }
                    if (StringUtils.isNotEmpty(entity.getVenderid()) && !entity.getVenderid().equals(dto.getVenderId())) {
                        builder.append("供应商6D不一致：正确的供应商6D").append(entity.getVenderid()).append("，");
                    }
                    if (StringUtils.isNotEmpty(entity.getXfName()) && !entity.getXfName().equals(dto.getVenderName())) {
                        builder.append("供应商名称不一致，正确的供应商名称")
                                .append(entity.getXfName())
                                .append("，");
                    }
                    if (StringUtils.isNotEmpty(entity.getJvcode()) && !entity.getJvcode().equals(dto.getJvCode())) {
                        builder.append("JVCODE不一致 正确的JV").append(entity.getJvcode()).append("，");
                    }
                    if (Objects.nonNull(entity.getInvoiceDate())) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        String format = sdf.format(entity.getInvoiceDate());
                        if (!format.equals(dto.getPaperDate())) {
                            builder.append("开票日期不一致 正确的开票日期").append(format).append("，");
                        }
                    }
                    if (entity.getInvoiceAmount().compareTo(new BigDecimal(dto.getAmountWithoutTax())) != 0) {
                        builder.append("不含税金额不一致 正确的不含税金额")
                                .append(entity.getInvoiceAmount().setScale(2, RoundingMode.HALF_UP))
                                .append("，");
                    }
                    if (entity.getTaxAmount().compareTo(new BigDecimal(dto.getTaxAmount())) != 0) {
                        builder.append("税额不一致 正确的税额")
                                .append(entity.getTaxAmount().setScale(2, RoundingMode.HALF_UP))
                                .append("，");
                    }
                    if (entity.getTaxRate().compareTo(new BigDecimal(dto.getTaxRate())) != 0) {
                        builder.append("税率不一致 正确的税率")
                                .append(entity.getTaxRate().setScale(2, RoundingMode.HALF_UP))
                                .append("，");
                    }
                }
                QueryWrapper<TXfExchangeTicketEntity> exchangeTicketWrapper = new QueryWrapper<>();
                exchangeTicketWrapper.eq(StringUtils.isNotEmpty(distctList.get(j).getInvoiceCode()), TXfExchangeTicketEntity.INVOICE_CODE, distctList.get(j).getInvoiceCode());
                exchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_NO, distctList.get(j).getInvoiceNo());
                exchangeTicketWrapper.in(TXfExchangeTicketEntity.EXCHANGE_STATUS, exchangeList);
                List<TXfExchangeTicketEntity> exchangeTicketEntityList = this.list(exchangeTicketWrapper);
                if (CollectionUtils.isNotEmpty(exchangeTicketEntityList)) {
                    builder.append("已存在于换票列表中");
                }
                if (StringUtils.isNotEmpty(builder.toString())) {
                    return R.fail( "第" + dto.getRowNum() + "行;" + builder);
                }
                if ("08".equals(entity.getInvoiceType()) || "10".equals(entity.getInvoiceType())
                        || "16".equals(entity.getInvoiceType()) || "18".equals(entity.getInvoiceType())) {
                    distctList.get(j).setExchangeType("1");
                } else {
                    distctList.get(j).setExchangeType("0");
                }
                distctList.get(j).setExchangeSoource("2");
                distctList.get(j).setFlowType(entity.getFlowType());
                distctList.get(j).setXfTaxNo(entity.getXfTaxNo());
                distctList.get(j).setAmountWithTax(String.valueOf(entity.getTotalAmount()));
                distctList.get(j).setInvoiceId(String.valueOf(entity.getId()));

            }
            List<TXfExchangeTicketEntity> entityList = exchangeTicketConverter.map(distctList);
            entityList.stream().forEach(e -> {
                e.setCreateDate(new Date());
                e.setCreateUser(UserUtil.getUserName());
                e.setExchangeStatus(ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_SH.getCode());
            });
            this.saveBatch(entityList);
        } catch (IOException e) {
            log.error("读取excel异常:{}", e);
            return R.fail("读取excel异常");
        }
        return R.ok("message", "导入成功");
    }

    public List<TXfExchangeTicketEntity> getByIds(List<Long> list) {
        QueryWrapper<TXfExchangeTicketEntity> exchangeTicketWrapper = new QueryWrapper<>();
        exchangeTicketWrapper.in(TXfExchangeTicketEntity.ID, list);
        List<TXfExchangeTicketEntity> exchangeTicketEntityList = this.list(exchangeTicketWrapper);
        return exchangeTicketEntityList;
    }

    public List<TXfExchangeTicketEntity> noPaged(ExchangeTicketQueryDto request) {
        QueryWrapper<TXfExchangeTicketEntity> exchangeTicketWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(request.getJvCode())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.JV_CODE, request.getJvCode());
        }
        if (StringUtils.isNotEmpty(request.getExchangeStatus())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_STATUS, request.getExchangeStatus());
        }
        if (StringUtils.isNotEmpty(request.getFlowType())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.FLOW_TYPE, request.getFlowType());
        }
        if (StringUtils.isNotEmpty(request.getInvoiceCode())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_CODE, request.getInvoiceCode());
        }
        if (StringUtils.isNotEmpty(request.getInvoiceNo())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_NO, request.getInvoiceNo());
        }
        if (StringUtils.isNotEmpty(request.getVenderId())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.VENDER_ID, request.getVenderId());
        }
        List<TXfExchangeTicketEntity> exchangeTicketEntityList = this.list(exchangeTicketWrapper);
        return exchangeTicketEntityList;
    }
    public Integer count(ExchangeTicketQueryDto request) {
        QueryWrapper<TXfExchangeTicketEntity> exchangeTicketWrapper = new QueryWrapper<>();
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getJvCode()), TXfExchangeTicketEntity.JV_CODE, request.getJvCode());
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getExchangeStatus()), TXfExchangeTicketEntity.EXCHANGE_STATUS, request.getExchangeStatus());
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getFlowType()), TXfExchangeTicketEntity.FLOW_TYPE, request.getFlowType());
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getInvoiceCode()), TXfExchangeTicketEntity.INVOICE_CODE, request.getInvoiceCode());
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getInvoiceNo()), TXfExchangeTicketEntity.INVOICE_NO, request.getInvoiceNo());
        exchangeTicketWrapper.eq(StringUtils.isNotEmpty(request.getVenderId()), TXfExchangeTicketEntity.VENDER_ID, request.getVenderId());
        return this.count(exchangeTicketWrapper);
    }

    public PageResult<TXfExchangeTicketEntity> paged(Long current, Long size, ExchangeTicketQueryDto request) {
        Page<TXfExchangeTicketEntity> page = new Page<>(current, size);
        QueryWrapper<TXfExchangeTicketEntity> exchangeTicketWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(request.getJvCode())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.JV_CODE, request.getJvCode());
        }
        if (StringUtils.isNotEmpty(request.getExchangeStatus())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_STATUS, request.getExchangeStatus());
        }
        if (StringUtils.isNotEmpty(request.getExchangeSoource())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_SOOURCE, request.getExchangeSoource());
        }
        if (StringUtils.isNotEmpty(request.getExchangeType())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_TYPE, request.getExchangeType());
        }
        if (StringUtils.isNotEmpty(request.getExchangeStatus())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_STATUS, request.getExchangeStatus());
        }
        if (StringUtils.isNotEmpty(request.getFlowType())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.FLOW_TYPE, request.getFlowType());
        }
        if (StringUtils.isNotEmpty(request.getInvoiceCode())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_CODE, request.getInvoiceCode());
        }
        if (StringUtils.isNotEmpty(request.getInvoiceNo())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.INVOICE_NO, request.getInvoiceNo());
        }
        if (StringUtils.isNotEmpty(request.getVenderId())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.VENDER_ID, request.getVenderId());
        }
        if (StringUtils.isNotEmpty(request.getTaxRate())) {
            exchangeTicketWrapper.eq(TXfExchangeTicketEntity.TAX_RATE, request.getTaxRate());
        }
        exchangeTicketWrapper.orderByDesc(TXfExchangeTicketEntity.CREATE_DATE);
        Page<TXfExchangeTicketEntity> pageResult = tXfExchangeTicketDao.selectPage(page, exchangeTicketWrapper);
        PageResult<TXfExchangeTicketEntity> result = new PageResult<>();
        result.setRows(pageResult.getRecords());
        PageResult.Summary summary = new PageResult.Summary();
        summary.setPages(pageResult.getPages());
        summary.setSize(pageResult.getSize());
        summary.setTotal(pageResult.getTotal());
        result.setSummary(summary);
        return result;
    }

    public R export(List<TXfExchangeTicketEntity> resultList, ExchangeValidSubmitRequest request) {

        final String excelFileName = ExcelExportUtil.getExcelFileName(UserUtil.getUserId(), "换票数据导出");
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        FileInputStream inputStream = null;
        try {
            resultList.forEach(e->{
                e.setPaperDate(StaticString.formatDate(e.getPaperDate()));
                e.setExchangePaperDate(StaticString.formatDate(e.getExchangePaperDate()));
                if(Objects.nonNull(e.getCreateDate())){
                    e.setCreateDateStr(DateUtils.format(e.getCreateDate(),DateUtils.DATE_PATTERN));
                }
                if(Objects.nonNull(e.getLastUpdateDate())){
                    e.setLastUpdateDateStr(DateUtils.format(e.getLastUpdateDate(),DateUtils.DATE_PATTERN));
                }
            });
            //创建一个sheet
            File file = FileUtils.getFile(tmp + ftpPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File excl = FileUtils.getFile(file, excelFileName);
            EasyExcel.write(tmp + ftpPath + "/" + excelFileName, ExchangeTicketExportDto.class)
                    .sheet("sheet1").doWrite(exchangeTicketConverter.exportMap(resultList));
            //推送sftp
            inputStream = FileUtils.openInputStream(excl);
            ftpUtilService.uploadFile(ftpPath, excelFileName, inputStream);
            final Long userId = UserUtil.getUserId();
            ExceptionReportExportDto exportDto = new ExceptionReportExportDto();
            exportDto.setUserId(userId);
            exportDto.setLoginName(UserUtil.getLoginName());
            TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
            excelExportlogEntity.setCreateDate(new Date());
            //这里的userAccount是userid
            excelExportlogEntity.setUserAccount(UserUtil.getUserName());
            excelExportlogEntity.setUserName(UserUtil.getLoginName());
            excelExportlogEntity.setConditions(JSON.toJSONString(request));
            excelExportlogEntity.setStartDate(new Date());
            excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
            excelExportlogEntity.setServiceType(SERVICE_TYPE);
            excelExportlogEntity.setFilepath(ftpPath + "/" + excelFileName);
            this.excelExportLogService.save(excelExportlogEntity);
            exportDto.setLogId(excelExportlogEntity.getId());
            exportCommonService.sendMessage(excelExportlogEntity.getId(), UserUtil.getLoginName(), "换票数据导出成功", exportCommonService.getSuccContent());
        } catch (Exception e) {
            log.error("导出异常:{}", e.getMessage(), e);
            return R.fail("导出异常");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return R.ok();
    }


    public R updateExchangeStatus(ExchangeValidSubmitRequest request) {

        LambdaUpdateChainWrapper<TXfExchangeTicketEntity> wrapper = new LambdaUpdateChainWrapper<>(getBaseMapper());

        if (CollectionUtils.isNotEmpty(request.getIncludes())) {
            wrapper.in(TXfExchangeTicketEntity::getId, request.getIncludes());
        }
        if (StringUtils.isNotEmpty(request.getExcludes().getExchangeStatus())) {
            wrapper.set(TXfExchangeTicketEntity::getExchangeStatus, request.getExcludes().getExchangeStatus());
        }

        if (StringUtils.isNotEmpty(request.getExcludes().getExchangeRemark())) {
            wrapper.set(TXfExchangeTicketEntity::getExchangeRemark, request.getExcludes().getExchangeRemark());
        }
        wrapper.set(TXfExchangeTicketEntity::getLastUpdateUser, UserUtil.getUserName());
        wrapper.set(TXfExchangeTicketEntity::getLastUpdateDate, new Date());
        wrapper.update();
        return R.ok("message", "审核成功");
    }

    public R generateRefund(ExchangeGenerateRefundDto request) {
        List<TXfExchangeTicketEntity> resultList = this.getByIds(request.getIdList());
        if (CollectionUtils.isEmpty(resultList)) {
            return R.fail("未查询到具体的换票信息");
        }
        for (int i = 0; i < resultList.size(); i++) {
            //取消匹配关系
            if ("1".equals(request.getIsCancelMatch())) {
                QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
                wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_CODE, resultList.get(i).getInvoiceCode());
                wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_NO, resultList.get(i).getInvoiceNo());
                TDxRecordInvoiceEntity tDxRecordInvoiceEntity = tDxRecordInvoiceDao.selectOne(wrapper);
                if (Objects.isNull(tDxRecordInvoiceEntity)) {
                    return R.fail("未查询到具体的发票信息");
                }
                if (StringUtils.isEmpty(tDxRecordInvoiceEntity.getMatchno())) {
                    return R.fail("发票无匹配号，匹配无法取消");
                }

                Boolean flag = tXfExchangeTicketDao.cancelMatch(tDxRecordInvoiceEntity.getMatchno()) > 0;
                if (!flag) {
                    return R.fail("匹配关系取消失败");
                }
                tXfExchangeTicketDao.cancelClaim(tDxRecordInvoiceEntity.getMatchno());
                tXfExchangeTicketDao.cancelInvoice(tDxRecordInvoiceEntity.getMatchno());
                List<PoEntity> list = tXfExchangeTicketDao.getPoJiLu(tDxRecordInvoiceEntity.getMatchno());
                BigDecimal changeTotal = new BigDecimal(0);
                for (int k = 0; k < list.size(); k++) {
                    PoEntity poEntity = list.get(k);
                    tXfExchangeTicketDao.cancelPo(poEntity.getId(), poEntity.getChangeAmount(), "6");
                }
            }
            //生成退单号
            tXfExchangeTicketDao.updateRefund(resultList.get(i).getInvoiceNo(), resultList.get(i).getInvoiceCode());

        }

        return R.ok("message", "审核成功");
    }

    public void update(TXfExchangeTicketEntity entity) {
        new LambdaUpdateChainWrapper<>(getBaseMapper())
                .eq(StringUtils.isNotEmpty(entity.getInvoiceCode()), TXfExchangeTicketEntity::getInvoiceCode, entity.getInvoiceCode())
                .eq(TXfExchangeTicketEntity::getInvoiceNo, entity.getInvoiceNo())
                .ne(TXfExchangeTicketEntity::getExchangeStatus, EXCHANGE_TICKEY_STATUS_FAIL.getCode())
                .update(entity);
    }

    public R getExchangeInfoByuuid(String invoiceCode, String invoiceNo) {
        QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(invoiceCode) && !"null".equals(invoiceCode)) {
            wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_CODE, invoiceCode);
        }
        wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_NO, invoiceNo);
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = tDxRecordInvoiceDao.selectOne(wrapper);
        if (Objects.isNull(tDxRecordInvoiceEntity)) {
            return R.fail("未查询到具体的发票信息");
        }
        QueryWrapper<TXfExchangeTicketEntity> exchangeTicketEntityLambdaUpdateChainWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(invoiceCode) && !"null".equals(invoiceCode)) {
            exchangeTicketEntityLambdaUpdateChainWrapper.eq(TXfExchangeTicketEntity.INVOICE_CODE, invoiceCode);
        }
        exchangeTicketEntityLambdaUpdateChainWrapper.eq(TXfExchangeTicketEntity.INVOICE_NO, invoiceNo);
        exchangeTicketEntityLambdaUpdateChainWrapper.eq(TXfExchangeTicketEntity.EXCHANGE_STATUS,ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_PRE.getCode());
        TXfExchangeTicketEntity tXfExchangeTicketEntity = tXfExchangeTicketDao.selectOne(exchangeTicketEntityLambdaUpdateChainWrapper);
        if (Objects.isNull(tXfExchangeTicketEntity)) {
            return R.fail("未查询到具体的换票信息");
        }
        InvoiceDetailResponse response = new InvoiceDetailResponse();
        BeanUtil.copyProperties(tDxRecordInvoiceEntity, response);
        this.convertMain(tDxRecordInvoiceEntity, response);
        response.setExchangeReason(tXfExchangeTicketEntity.getExchangeReason());
        response.setExchangeStatus(Integer.parseInt(tXfExchangeTicketEntity.getExchangeStatus()));
        return R.ok(response);
    }

    public void convertMain(TDxRecordInvoiceEntity entity, InvoiceDetailResponse invoice) {
        invoice.setPurchaserAddressAndPhone(entity.getGfAddressAndPhone());
        invoice.setPurchaserBankAndNo(entity.getGfBankAndNo());
        invoice.setPurchaserName(entity.getGfName());
        invoice.setPurchaserTaxNo(entity.getGfTaxNo());
        invoice.setSellerAddressAndPhone(entity.getXfAddressAndPhone());
        invoice.setSellerBankAndNo(entity.getXfBankAndNo());
        invoice.setSellerName(entity.getXfName());
        invoice.setSellerTaxNo(entity.getXfTaxNo());
        invoice.setSellerNo(entity.getVenderid());
        invoice.setPaperDrewDate(entity.getInvoiceDate());
        invoice.setAmountWithoutTax(entity.getInvoiceAmount());
        invoice.setAmountWithTax(entity.getTotalAmount());
        invoice.setRedNotificationNo(entity.getRedNoticeNumber());
        invoice.setMachineCode(entity.getMachinecode());
        if (entity.getTaxRate() != null) {
            BigDecimal taxRate = entity.getTaxRate().divide(BigDecimal.valueOf(100L), 2, RoundingMode.HALF_UP);
            invoice.setTaxRate(taxRate.toPlainString());
        }
        //判断销货清单，当明细大于8条时 值为1
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(invoice.getItems()) && invoice.getItems().size() > 8) {
            invoice.setGoodsListFlag("1");
        }
    }

    public Page<TXfExchangeTicketEntity> pageAmount(Long current, Long size, TXfExchangeTicketEntity dto) {
        Page<TXfExchangeTicketEntity> page = new Page<>(current, size);
        Page<TXfExchangeTicketEntity> result=tXfExchangeTicketDao.queryList(page, dto);
        List<TXfExchangeTicketEntity> list = result.getRecords();
        //查询换票后的认证状态
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                if (StringUtils.isNotEmpty(list.get(i).getExchangeInvoiceNo())) {
                    QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
                    wrapper.eq(StringUtils.isNotBlank(list.get(i).getExchangeInvoiceCode()), TDxRecordInvoiceDetailEntity.INVOICE_CODE, list.get(i).getExchangeInvoiceCode());
                    wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_NO, list.get(i).getExchangeInvoiceNo());
                    TDxRecordInvoiceEntity tDxRecordInvoiceEntity = tDxRecordInvoiceDao.selectOne(wrapper);
                    if (Objects.nonNull(tDxRecordInvoiceEntity)) {
                        list.get(i).setExchangeAuthStatus(tDxRecordInvoiceEntity.getAuthStatus());
                    }
                }
            }
        }
        result.setRecords(list);
        return result;
    }

    public R updateVoucherNo(Long id, String voucherNo) {
        String err;
        TXfExchangeTicketEntity one = new LambdaQueryChainWrapper<>(getBaseMapper()).eq(TXfExchangeTicketEntity::getId, id).one();
        if (one != null) {
            if (ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_DONE.getCode().equalsIgnoreCase(one.getExchangeStatus())) {
                err = "已确认换票无法修改";
            } else {
                QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
                wrapper.eq(StringUtils.isNotBlank(one.getExchangeInvoiceCode()), TDxRecordInvoiceDetailEntity.INVOICE_CODE, one.getExchangeInvoiceCode());
                wrapper.eq(TDxRecordInvoiceDetailEntity.INVOICE_NO, one.getExchangeInvoiceNo());
                TDxRecordInvoiceEntity invoice = tDxRecordInvoiceDao.selectOne(wrapper);
                if (invoice != null && AuthStatusEnum.AUTH_STATUS_SUCCESS.getCode().equalsIgnoreCase(invoice.getAuthStatus())) {
                    boolean update = new LambdaUpdateChainWrapper<>(getBaseMapper())
                            .eq(TXfExchangeTicketEntity::getId, id)
                            .set(TXfExchangeTicketEntity::getVoucherNo, voucherNo)
                            .update();
                    return update ? R.ok() : R.fail("更新失败");
                } else {
                    err = "发票认证状态不满足条件";
                }
            }
        } else {
            err = "数据不存在";
        }
        return R.fail(err);
    }

    @Transactional(rollbackFor = Exception.class)
    public R updateVoucherNoImport(MultipartFile file) {
        UpdateVoucherNoImportListener listener = new UpdateVoucherNoImportListener();
        try {
            EasyExcel.read(file.getInputStream(), ExchangeTicketExportDto.class, listener).sheet().doRead();
        } catch (IOException e) {
            log.error("读取excel异常:{}", e.getMessage(), e);
            return R.fail("读取excel异常");
        }
        if (listener.getRows() == 0) {
            return R.fail("未解析到数据");
        }

        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(listener.getInvalidInvoices())) {

            for (int i = 0; i < listener.getInvalidInvoices().size(); i++) {
                builder.append("第").append(listener.getInvalidInvoices().get(i)._2).append("行");
                builder.append(listener.getInvalidInvoices().get(i)._3);
            }
            return R.fail(builder.toString());
        }
        int update = 0;
        List<List<ExchangeTicketExportDto>> partition = Lists.partition(listener.getValidInvoices(), 500);
        for (List<ExchangeTicketExportDto> dto : partition) {
            QueryWrapper<TDxRecordInvoiceEntity> wrapper = new QueryWrapper<>();
            dto.forEach(f -> wrapper.or(it -> {
                it.eq(StringUtils.isNotBlank(f.getExchangeInvoiceCode()), TDxRecordInvoiceDetailEntity.INVOICE_CODE, f.getExchangeInvoiceCode());
                it.eq(TDxRecordInvoiceDetailEntity.INVOICE_NO, f.getExchangeInvoiceNo());
            }));
            List<TDxRecordInvoiceEntity> invoices = tDxRecordInvoiceDao.selectList(wrapper);
            Map<String, String> authMap = invoices.stream()
                    .collect(Collectors.toMap(it -> it.getInvoiceNo() + Objects.toString(it.getInvoiceCode(), ""),
                            it -> it.getAuthStatus() + "-" + it.getExchangeStatus()));

            Map<String, List<ExchangeTicketExportDto>> map = dto.stream().collect(Collectors.groupingBy(ExchangeTicketExportDto::getVoucherNo));
            for (Map.Entry<String, List<ExchangeTicketExportDto>> entry : map.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    continue;
                }

                LambdaUpdateChainWrapper<TXfExchangeTicketEntity> set = new LambdaUpdateChainWrapper<>(getBaseMapper())
                        .set(TXfExchangeTicketEntity::getVoucherNo, entry.getKey());
                for (ExchangeTicketExportDto en : entry.getValue()) {
                    String tup = authMap.get(en.getExchangeInvoiceNo() + Objects.toString(en.getExchangeInvoiceCode(), ""));
                    if (StringUtils.isNotBlank(tup) && StringUtils.isNotBlank(tup.split("-")[0])
                            && AuthStatusEnum.AUTH_STATUS_SUCCESS.getCode().equalsIgnoreCase(tup.split("-")[0])
                            && !ExchangeTickeyStatusEnum.EXCHANGE_TICKEY_STATUS_DONE.getCode().equalsIgnoreCase(tup.split("-")[1])) {
                        set.or(it -> it
                                .eq(StringUtils.isNotBlank(en.getExchangeInvoiceCode()), TXfExchangeTicketEntity::getInvoiceCode, en.getInvoiceCode())
                                .eq(TXfExchangeTicketEntity::getInvoiceNo, en.getInvoiceNo()));
                        update++;
                    }
                }
                if (update > 0) {
                    set.update();
                }
            }
        }

        return R.ok("message", "导入成功 " + update + " 条数据");
    }
}
