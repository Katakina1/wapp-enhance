package com.xforceplus.wapp.modules.api.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * TODO
 *
 * @atuthor wyman
 * @date 2020-05-06 11:40
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AribaCheckReturn {



    private List<ResultBean> result;


    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {


        private String invoiceType;
        private String fapiaoCode;
        private String fapiaoNumber;
        private String fapiaoNetAmount;
        private String fapiaoTaxAmount;
        private String fapiaoTotalAmount;
        private String fapiaoCurrency;
        private String supplierTaxId;
        private String walmartTaxId;
        private String customField1;
        private String customField2;
        private String customField3;
        private String customField4;
        private String customField5;
        private String status;
        private List<?> error;
        private String validationCode;
        private String purchasingDocumentNumber;
        private String supplierNumber;
//        private String legacySupplierNumber;
        private String attachmentLink;
//        private String jvCode;

        private String companyCode;
        @JSONField(name = "fapiaoDate")
        private String invoiceDate;

        public String getInvoiceDate() {
            return invoiceDate;
        }

        public void setInvoiceDate(String invoiceDate) {
            this.invoiceDate = invoiceDate;
        }

        public String getAttachmentLink() {
            return attachmentLink;
        }

        public void setAttachmentLink(String attachmentLink) {
            this.attachmentLink = attachmentLink;
        }

//        public String getLegacySupplierNumber() {
//            return legacySupplierNumber;
//        }
//
//        public void setLegacySupplierNumber(String legacySupplierNumber) {
//            this.legacySupplierNumber = legacySupplierNumber;
//        }
//
//        public String getJvCode() {
//            return jvCode;
//        }

//        public void setJvCode(String jvCode) {
//            this.jvCode = jvCode;
//        }

        public String getCompanyCode() {
            return companyCode;
        }

        public void setCompanyCode(String companyCode) {
            this.companyCode = companyCode;
        }

        private List<ItemsBean> items;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getInvoiceType() {
            return invoiceType;
        }

        public void setInvoiceType(String invoiceType) {
            this.invoiceType = invoiceType;
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

        public String getFapiaoNetAmount() {
            return fapiaoNetAmount;
        }

        public void setFapiaoNetAmount(String fapiaoNetAmount) {
            this.fapiaoNetAmount = fapiaoNetAmount;
        }

        public String getFapiaoTaxAmount() {
            return fapiaoTaxAmount;
        }

        public void setFapiaoTaxAmount(String fapiaoTaxAmount) {
            this.fapiaoTaxAmount = fapiaoTaxAmount;
        }

        public String getFapiaoTotalAmount() {
            return fapiaoTotalAmount;
        }

        public void setFapiaoTotalAmount(String fapiaoTotalAmount) {
            this.fapiaoTotalAmount = fapiaoTotalAmount;
        }

        public String getFapiaoCurrency() {
            return fapiaoCurrency;
        }

        public void setFapiaoCurrency(String fapiaoCurrency) {
            this.fapiaoCurrency = fapiaoCurrency;
        }

        public String getSupplierTaxId() {
            return supplierTaxId;
        }

        public void setSupplierTaxId(String supplierTaxId) {
            this.supplierTaxId = supplierTaxId;
        }

        public String getWalmartTaxId() {
            return walmartTaxId;
        }

        public void setWalmartTaxId(String walmartTaxId) {
            this.walmartTaxId = walmartTaxId;
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

        public List<?> getError() {
            return error;
        }

        public void setError(List<?> error) {
            this.error = error;
        }

        public String getValidationCode() {
            return validationCode;
        }

        public void setValidationCode(String validationCode) {
            this.validationCode = validationCode;
        }

        public String getPurchasingDocumentNumber() {
            return purchasingDocumentNumber;
        }

        public void setPurchasingDocumentNumber(String purchasingDocumentNumber) {
            this.purchasingDocumentNumber = purchasingDocumentNumber;
        }

        public String getSupplierNumber() {
            return supplierNumber;
        }

        public void setSupplierNumber(String supplierNumber) {
            this.supplierNumber = supplierNumber;
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
            private List<?> error;

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

            public List<?> getError() {
                return error;
            }

            public void setError(List<?> error) {
                this.error = error;
            }
        }

    }


}
