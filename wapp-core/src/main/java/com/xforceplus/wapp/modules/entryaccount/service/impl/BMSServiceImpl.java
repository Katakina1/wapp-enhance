package com.xforceplus.wapp.modules.entryaccount.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.evat.common.domain.bms.BmsFeedbackConfig;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.SignUtils;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result.BMSResultDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send.SendBMSDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send.SendInvoiceReceipt;
import com.xforceplus.wapp.modules.entryaccount.service.BMSService;
import com.xforceplus.wapp.modules.xforceapi.HttpClientUtils;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: ChenHang
 * @Date: 2023/7/6 17:07
 */
@Service
@Slf4j
public class BMSServiceImpl implements BMSService {

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @Autowired
    public BmsFeedbackConfig bmsFeedbackConfig;

    /**
     * 发票签收推送BMS
     */
    @Override
    public void qsToBms() {
        // 查询待推送BMS的已签收未推送的数据
        List<TDxRecordInvoiceEntity> recordInvoices = recordInvoiceService.getQsToBms("9", "1");
        for (TDxRecordInvoiceEntity recordInvoice : recordInvoices) {
            this.sendBmsQs(recordInvoice);
        }

    }

    /**
     * 发送给BMS签收
     * @param recordInvoice
     * @return
     */
    public String sendBmsQs(TDxRecordInvoiceEntity recordInvoice) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SendInvoiceReceipt sendQueryTaxBill = SendInvoiceReceipt.builder()
                .invoiceNo(recordInvoice.getInvoiceNo())
                .invoiceCode(recordInvoice.getInvoiceCode())
                .invoiceType(recordInvoice.getInvoiceType())
                .qsDate(DateUtils.format(recordInvoice.getQsDate(), DateUtils.DATE_TIME_PATTERN))
                // 后续可能会根据签收状态赋不同的值
                .qsStatus("Y")
                .remark("签收成功")
                .build();
        String sign = SignUtils.getBMSSign(timestamp, JSONObject.toJSONString(sendQueryTaxBill), bmsFeedbackConfig.getAppSecret());
        SendBMSDTO<SendInvoiceReceipt> sendBMSDTO = SendBMSDTO.<SendInvoiceReceipt>builder()
                .source("wapp")
                .appName("wapp")
                .version("1.0")
                .param(sendQueryTaxBill)
                .timestamp(timestamp)
                .format("json")
                .sign(sign)
                .build();
        String queryJson = JSONObject.toJSONString(sendBMSDTO);
        String result = null;
        try {
            log.info("推送BMS非商匹配结果, url:{}, 入参:{}", bmsFeedbackConfig.getBmsInvoiceReceiptUrl(), queryJson);
            result = HttpClientUtils.postJson(bmsFeedbackConfig.getBmsInvoiceReceiptUrl(), queryJson);
        } catch (Exception e) {
            log.error("推送BMS非商匹配结果失败:", e);
        }
        log.info("推送BMS非商匹配结果回参:{}", result);
        BMSResultDTO bmsResultDTO = JSONObject.parseObject(result, BMSResultDTO.class);
        if (bmsResultDTO.isSuccess()) {
            // 设置为已推送签收结果
            recordInvoice.setAribaConfirmStatus("1");
            recordInvoiceService.updateById(recordInvoice);
        }
        return result;
    }

}