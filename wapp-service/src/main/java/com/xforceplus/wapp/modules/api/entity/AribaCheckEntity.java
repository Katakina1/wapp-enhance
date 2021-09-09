package com.xforceplus.wapp.modules.api.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * TODO
 *
 * @atuthor wyman
 * @date 2020-05-05 10:03
 **/
public class AribaCheckEntity {



    private List<FapiaosBean> fapiaos;

    public List<FapiaosBean> getFapiaos() {
        return fapiaos;
    }

    public void setFapiaos(List<FapiaosBean> fapiaos) {
        this.fapiaos = fapiaos;
    }




    public static class FapiaosBean {
        @JSONField(ordinal =0)
        private String invoiceType;

        private String invoiceTypeCode;
        @JSONField(ordinal =1)
        private String fapiaoCode;
        @JSONField(ordinal =2)
        private String fapiaoNumber;
        @JSONField(ordinal =3)
        private String validationCode;

        private String fapiaoNetAmount;
        private String fapiaoCurrency;

        private String fapiaoDate;

        private String legacySupplierNumber;

        @JSONField(ordinal =4)
        private String supplierNumber;

        private String jvCode;
        @JSONField(ordinal =5)
        private String status;
        private String companyCode;
        @JSONField(ordinal =6)
        private String customField1;
        @JSONField(ordinal =7)
        private String customField2;
        @JSONField(ordinal =8)
        private String customField3;
        @JSONField(ordinal =9)
        private String customField4;
        @JSONField(ordinal =10)
        private String customField5;
        private String purchasingDocumentNumber;
        private String supplierName;
        private String attachmentLink;
        private List<ItemsBean> items;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAttachmentLink() {
            return attachmentLink;
        }

        public void setAttachmentLink(String attachmentLink) {
            this.attachmentLink = attachmentLink;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(String invoiceType) {
            this.invoiceType = invoiceType;
        }

        public String getInvoiceTypeCode() {
            return invoiceTypeCode;
        }

        public void setInvoiceTypeCode(String invoiceTypeCode) {
            this.invoiceTypeCode = invoiceTypeCode;
        }

        public String getFapiaoCode() {
            return fapiaoCode;
        }

        public void setFapiaoCode(String fapiaoCode) {
            this.fapiaoCode = fapiaoCode;
        }

        public String getFapiaoNumber() {
            return fapiaoNumber;
        }

        public void setFapiaoNumber(String fapiaoNumber) {
            this.fapiaoNumber = fapiaoNumber;
        }

        public String getValidationCode() {
            return validationCode;
        }

        public void setValidationCode(String validationCode) {
            this.validationCode = validationCode;
        }

        public String getFapiaoNetAmount() {
            return fapiaoNetAmount;
        }

        public void setFapiaoNetAmount(String fapiaoNetAmount) {
            this.fapiaoNetAmount = fapiaoNetAmount;
        }

        public String getFapiaoCurrency() {
            return fapiaoCurrency;
        }

        public void setFapiaoCurrency(String fapiaoCurrency) {
            this.fapiaoCurrency = fapiaoCurrency;
        }

        public String getFapiaoDate() {
            return fapiaoDate;
        }

        public void setFapiaoDate(String fapiaoDate) {
            this.fapiaoDate = fapiaoDate;
        }

        public String getLegacySupplierNumber() {
            return legacySupplierNumber;
        }

        public void setLegacySupplierNumber(String legacySupplierNumber) {
            this.legacySupplierNumber = legacySupplierNumber;
        }

        public String getSupplierNumber() {
            return supplierNumber;
        }

        public void setSupplierNumber(String supplierNumber) {
            this.supplierNumber = supplierNumber;
        }

        public String getJvCode() {
            return jvCode;
        }

        public void setJvCode(String jvCode) {
            this.jvCode = jvCode;
        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        public String getCustomField1() {
            return customField1;
        }

        public void setCustomField1(String customField1) {
            this.customField1 = customField1;
        }

        public String getCustomField2() {
            return customField2;
        }

        public void setCustomField2(String customField2) {
            this.customField2 = customField2;
        }

        public String getCustomField3() {
            return customField3;
        }

        public void setCustomField3(String customField3) {
            this.customField3 = customField3;
        }

        public String getCustomField4() {
            return customField4;
        }

        public void setCustomField4(String customField4) {
            this.customField4 = customField4;
        }

        public String getCustomField5() {
            return customField5;
        }

        public void setCustomField5(String customField5) {
            this.customField5 = customField5;
        }

        public String getPurchasingDocumentNumber() {
            return purchasingDocumentNumber;
        }

        public void setPurchasingDocumentNumber(String purchasingDocumentNumber) {
            this.purchasingDocumentNumber = purchasingDocumentNumber;
        }

        public String getSupplierName() {
            return supplierName;
        }

        public void setSupplierName(String supplierName) {
            this.supplierName = supplierName;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }

        public static class ItemsBean {
            private String status;
            private String itemNo;
            private String taxCode;
            private String taxRate;
            private String glAccount;
            private String mccCode;
            private String costCenter;
            private String fapiaoTotalAmount;
            private String fapiaoTaxAmount;
            private String fapiaoCurrency;
            private String customField1;
            private String customField2;
            private String customField3;
            private String customField4;
            private String customField5;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getItemNo() {
                return itemNo;
            }

            public void setItemNo(String itemNo) {
                this.itemNo = itemNo;
            }

            public String getTaxCode() {
                return taxCode;
            }

            public void setTaxCode(String taxCode) {
                this.taxCode = taxCode;
            }

            public String getTaxRate() {
                return taxRate;
            }

            public void setTaxRate(String taxRate) {
                this.taxRate = taxRate;
            }

            public String getGlAccount() {
                return glAccount;
            }

            public void setGlAccount(String glAccount) {
                this.glAccount = glAccount;
            }

            public String getMccCode() {
                return mccCode;
            }

            public void setMccCode(String mccCode) {
                this.mccCode = mccCode;
            }

            public String getCostCenter() {
                return costCenter;
            }

            public void setCostCenter(String costCenter) {
                this.costCenter = costCenter;
            }

            public String getFapiaoTotalAmount() {
                return fapiaoTotalAmount;
            }

            public void setFapiaoTotalAmount(String fapiaoTotalAmount) {
                this.fapiaoTotalAmount = fapiaoTotalAmount;
            }

            public String getFapiaoTaxAmount() {
                return fapiaoTaxAmount;
            }

            public void setFapiaoTaxAmount(String fapiaoTaxAmount) {
                this.fapiaoTaxAmount = fapiaoTaxAmount;
            }

            public String getFapiaoCurrency() {
                return fapiaoCurrency;
            }

            public void setFapiaoCurrency(String fapiaoCurrency) {
                this.fapiaoCurrency = fapiaoCurrency;
            }

            public String getCustomField1() {
                return customField1;
            }

            public void setCustomField1(String customField1) {
                this.customField1 = customField1;
            }

            public String getCustomField2() {
                return customField2;
            }

            public void setCustomField2(String customField2) {
                this.customField2 = customField2;
            }

            public String getCustomField3() {
                return customField3;
            }

            public void setCustomField3(String customField3) {
                this.customField3 = customField3;
            }

            public String getCustomField4() {
                return customField4;
            }

            public void setCustomField4(String customField4) {
                this.customField4 = customField4;
            }

            public String getCustomField5() {
                return customField5;
            }

            public void setCustomField5(String customField5) {
                this.customField5 = customField5;
            }
        }

    }
}