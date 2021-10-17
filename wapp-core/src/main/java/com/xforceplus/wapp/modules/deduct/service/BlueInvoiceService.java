package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
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
@Service
public class BlueInvoiceService {
    public List<MatchRes> matchInvoiceInfo(BigDecimal amount, XFDeductionBusinessTypeEnum deductionEnum,String settlementNo) {

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
