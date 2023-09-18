package com.xforceplus.wapp.modules.backfill.tools;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.util.CoopFullHalfAngleUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 回填发票信息校验
 * @date : 2022/11/25 9:38
 **/
public class BackFillCheckTools {

    /**
     * 上传蓝票时，校验蓝票同结算单配置字段是否一致
     * @param preInvoiceEntity 预制发票信息（字段同结算单）
     * @param invoiceEntity 发票信息
     */
    public static void checkPurchaserAndSeller(TXfPreInvoiceEntity preInvoiceEntity, TDxRecordInvoiceEntity invoiceEntity) {
        List<String> diffFieldList = Lists.newArrayList();
        for (BackFillCheckFieldEnum fieldEnum : BackFillCheckFieldEnum.values()) {

            String preInvoiceFieldValue = StringUtils.trim(BeanUtil.getObjectValue(preInvoiceEntity, fieldEnum.getSettlementFieldName()));
            String invoiceFieldValue = StringUtils.trim(BeanUtil.getObjectValue(invoiceEntity, fieldEnum.getInvoiceFieldName()));

            if (!CoopFullHalfAngleUtil.compare(preInvoiceFieldValue, invoiceFieldValue)) {
                diffFieldList.add(fieldEnum.getDisplayName());
            }
        }
        if (CollectionUtil.isNotEmpty(diffFieldList)) {
            String displayNameStr = String.join("、", diffFieldList);
            throw new EnhanceRuntimeException(String.format("发票的%s与结算单的%s不一致，请确认无误后重试", displayNameStr, displayNameStr));
        }
    }

    @Getter
    @AllArgsConstructor
    public enum BackFillCheckFieldEnum {
        /**
         * 回填校验字段
         */
        BUYER_NAME("purchaserName", "gfName", "购方名称"),
        SELLER_NAME("sellerName", "xfName", "销方名称"),
        BUYER_TAX_NO("purchaserTaxNo", "gfTaxNo", "购方税号"),
        SELLER_TAX_NO("sellerTaxNo", "xfTaxNo", "销方税号");

        private String settlementFieldName;

        private String invoiceFieldName;

        private String displayName;
    }
}
