package com.xforceplus.wapp.modules.rednotification.listener;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.InvoiceOrigin;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.*;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportErrorInfo;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationItemService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.rednotification.validator.CheckMainService;
import com.xforceplus.wapp.modules.sys.entity.UserEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExcelListener extends AnalysisEventListener<ImportInfo> {
    private List<ImportInfo> dataList = new ArrayList<>();
    private List<String> errorNoList = new ArrayList<>();
    private List<ImportErrorInfo> importErrorNoList = new ArrayList<>();
    private static final int BATCH_COUNT = 3000;
    private RedNotificationMainService redNotificationMainService;
    private RedNotificationMainMapper redNotificationMainMapper;
    private CheckMainService checkMainService;
    private Tuple3<Long,Long,String> tuple3;
    private ExportCommonService exportCommonService;

    private ImportInfo pre ;

    public ExcelListener(RedNotificationMainService redNotificationMainService,RedNotificationMainMapper redNotificationMainMapper
            ,CheckMainService checkMainService,Tuple3<Long,Long,String> tuple3 ){
        this.redNotificationMainService = redNotificationMainService;
        this.redNotificationMainMapper = redNotificationMainMapper;
        this.checkMainService = checkMainService;
        this.tuple3 = tuple3;
        exportCommonService = redNotificationMainService.getExportCommonService();
    }

    @Override
    public void invoke(ImportInfo record, AnalysisContext analysisContext) {
        //数据存储到，供批量处理，或后续自己业务逻辑处理。
        dataList.add(record);

        boolean flg = pre!=null && pre.getSellerNumber()!=null && !pre.getSellerNumber().equals(record.getSellerNumber());
        //达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if(flg && dataList.size() >= BATCH_COUNT){
            saveData();
            // 存储完成清理
            dataList.clear();
        }else {
            pre = record;
        }
    }

    // 保存数据
    private void saveData() {
        AddRedNotificationRequest addRedNotificationRequest = new AddRedNotificationRequest();
        List<RedNotificationInfo> redNotificationInfoList = convertToRedNotificationInfo(dataList);
        addRedNotificationRequest.setRedNotificationInfoList(redNotificationInfoList);
        addRedNotificationRequest.setAutoApplyFlag(0);
        redNotificationMainService.add(addRedNotificationRequest);
    }

    // 数据转换
    private List<RedNotificationInfo> convertToRedNotificationInfo(List<ImportInfo> dataList) {
        //流水号为空情况
        List<ImportInfo> filter = dataList.stream().filter(importInfo -> !StringUtils.isEmpty(importInfo.getSellerNumber())).collect(Collectors.toList());
        if (filter.size() != dataList.size()){
            ImportErrorInfo importErrorInfo = new ImportErrorInfo();
            importErrorInfo.setSerialNo("");
            importErrorInfo.setErrorMsg("流水号不能为空");
            importErrorNoList.add(importErrorInfo);
            return Lists.newArrayList();
        }

        Map<String, List<ImportInfo>> listMap = dataList.stream().collect(Collectors.groupingBy(ImportInfo::getSellerNumber));

        ArrayList<RedNotificationInfo> resultList = Lists.newArrayList();

        for (Map.Entry<String, List<ImportInfo>> importInfo : listMap.entrySet()) {

            List<ImportInfo> listImportInfos = importInfo.getValue();
            for (ImportInfo record : listImportInfos) {
                if (!errorNoList.contains(record.getSellerNumber())){
                    String checkResult = checkMainService.checkMainInfo(record);
                    if (!StringUtils.isEmpty(checkResult)){
                        errorNoList.add(record.getSellerNumber());
                        ImportErrorInfo importErrorInfo = new ImportErrorInfo();
                        importErrorInfo.setSerialNo(record.getSellerNumber());
                        importErrorInfo.setErrorMsg(checkResult);
                        importErrorNoList.add(importErrorInfo);
                        continue;
                    }
                }else {
                    continue;
                }
            }
            // 如果该流水号校验失败，不做后续流程
            if (errorNoList.contains(importInfo.getKey())){
                continue;
            }

            RedNotificationInfo redNotificationInfo = new RedNotificationInfo();
            RedNotificationMain redNotificationMain = redNotificationMainMapper.importInfoToMainEntity(importInfo.getValue().get(0));
            if(redNotificationMain.getOilMemo() != null ) {
            	redNotificationMain.setSpecialInvoiceFlag(2);
            }
            //补充而外信息
            redNotificationMain.setInvoiceOrigin(InvoiceOrigin.IMPORT.getValue());
            redNotificationInfo.setRednotificationMain(redNotificationMain);
            redNotificationInfo.setRedNotificationItemList(redNotificationMainMapper.importInfoListToItemEntityList(importInfo.getValue()));
            //更新主信息金额
            List<RedNotificationItem> redNotificationItemList = redNotificationInfo.getRedNotificationItemList();
            BigDecimal sumAmountWithTax = BigDecimal.ZERO;
            BigDecimal sumAmountWithoutTax = BigDecimal.ZERO;
            BigDecimal sumTaxAmount = BigDecimal.ZERO;

            for(RedNotificationItem item : redNotificationItemList){
                sumAmountWithTax = sumAmountWithTax.add(Optional.ofNullable(item.getAmountWithTax()).orElse(BigDecimal.ZERO));
                sumAmountWithoutTax = sumAmountWithoutTax.add(Optional.ofNullable(item.getAmountWithoutTax()).orElse(BigDecimal.ZERO));
                sumTaxAmount = sumTaxAmount.add(Optional.ofNullable(item.getTaxAmount()).orElse(BigDecimal.ZERO));
            }
            redNotificationMain.setAmountWithTax(sumAmountWithTax);
            redNotificationMain.setAmountWithoutTax(sumAmountWithoutTax);
            redNotificationMain.setTaxAmount(sumTaxAmount);


            resultList.add(redNotificationInfo);
        };
        return resultList;
    }


    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();//确保所有数据都能入库
        if (!CollectionUtils.isEmpty(importErrorNoList)){
            //如果存在失败生成错误文件，发送小叮当
            //推送sftp
            final String excelFileName = ExcelExportUtil.getExcelFileName(tuple3._2, "红字信息表导入结果");
            String ftpPath = redNotificationMainService.getFtpUtilService().pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            String ftpFilePath = ftpPath + "/" + excelFileName;
            log.info("文件ftp路径{}",ftpFilePath);

            String localFilePath = ftpFilePath.substring(1);
            File localFile = FileUtils.getFile(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }

            OutputStream out = null;
            try {
                out = new FileOutputStream(localFile);
            } catch (FileNotFoundException fnfException) {
                fnfException.printStackTrace();
                log.error("new FileOutputStream(localFile) err!");
            }
            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);

            Sheet sheet = new Sheet(1, 0, ImportErrorInfo.class);

            sheet.setSheetName("红字信息导入错误信息");

            writer.write(importErrorNoList, sheet);

            writer.finish();
            String s = exportCommonService.putFile(ftpPath,localFilePath, excelFileName);

            try {
                out.close();
            } catch (IOException ioException) {
                log.info(" out.close() err!");
            }

            exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.FAIL, ftpFilePath);
            exportCommonService.sendMessage(tuple3._1,tuple3._3,"红字信息表导入失败",exportCommonService.getFailContent("导入失败,失败详情见附件"));

        }else {
            String userName = exportCommonService.updatelogStatus(tuple3._1, ExcelExportLogService.OK,null);
            exportCommonService.sendMessageWithUrl(tuple3._1,userName,"红字信息表导入成功", "",null);
        }
    }
}
