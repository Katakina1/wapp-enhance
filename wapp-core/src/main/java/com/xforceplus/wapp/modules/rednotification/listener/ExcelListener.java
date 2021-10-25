package com.xforceplus.wapp.modules.rednotification.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.InvoiceOrigin;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.AddRedNotificationRequest;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationInfo;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationItem;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.excl.ImportInfo;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationItemService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExcelListener extends AnalysisEventListener<ImportInfo> {
    private List<ImportInfo> dataList = new ArrayList<>();
    private static final int BATCH_COUNT = 3000;
    private RedNotificationMainService redNotificationMainService;
    private RedNotificationMainMapper redNotificationMainMapper;

    private ImportInfo pre ;

    public ExcelListener(RedNotificationMainService redNotificationMainService,RedNotificationMainMapper redNotificationMainMapper){
        this.redNotificationMainService = redNotificationMainService;
        this.redNotificationMainMapper = redNotificationMainMapper;
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
        Map<String, List<ImportInfo>> listMap = dataList.stream().collect(Collectors.groupingBy(ImportInfo::getSellerNumber));

        ArrayList<RedNotificationInfo> resultList = Lists.newArrayList();
        listMap.entrySet().stream().forEach(importInfo->{
            RedNotificationInfo redNotificationInfo = new RedNotificationInfo();
            RedNotificationMain redNotificationMain = redNotificationMainMapper.importInfoToMainEntity(importInfo.getValue().get(0));
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
        });
        return resultList;
    }


    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();//确保所有数据都能入库
    }
}
