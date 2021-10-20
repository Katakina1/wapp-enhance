package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类描述：
 *
 * @ClassName BuleInvoiceService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/16 18:03
 */
@Slf4j
@Service
public class BlueInvoiceService {

    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum, String settlementNo) {
        switch (deductionEnum) {
            case AGREEMENT_BILL:
                return obtainAgreementInvoices(amount, settlementNo);
            case CLAIM_BILL:
                return obtainClaimInvoices(amount, settlementNo);
            case EPD_BILL:
                return obtainEpdInvoices(amount, settlementNo);
            default:
                log.error("未识别的单据类型{}", deductionEnum);
                return null;
        }
    }

    private List<MatchRes> obtainAgreementInvoices(BigDecimal amount, String settlementNo) {
        return null;
    }

    private List<MatchRes> obtainClaimInvoices(BigDecimal amount, String settlementNo) {
        return null;
    }

    private List<MatchRes> obtainEpdInvoices(BigDecimal amount, String settlementNo) {
        return null;
    }


    class MatchRes {
        String invoiceNo;
        String invoiceCode;
        List<InvoiceItem> invoiceItems;
    }

    class InvoiceItem {
        private String itemNo;
        /**
         * 发票代码
         */
        private String invoiceCode;

        /**
         * 发票号码
         */
        private String invoiceNo;

        /**
         * 明细序号
         */
        private String detailNo;

        /**
         * 货物或应税劳务名称
         */
        private String goodsName;

        /**
         * 规格型号
         */
        private String model;

        /**
         * 单位
         */
        private String unit;

        /**
         * 数量
         */
        private String num;

        /**
         * 单价
         */
        private String unitPrice;

        /**
         * 金额
         */
        private String detailAmount;

        /**
         * 税率
         */
        private String taxRate;

        /**
         * 税额
         */
        private String taxAmount;

        /**
         * 商品编码
         */
        private String goodsNum;
    }
}
