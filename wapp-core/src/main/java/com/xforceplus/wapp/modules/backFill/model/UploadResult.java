package com.xforceplus.wapp.modules.backFill.model;

import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-17 16:33
 **/
@Data
public class UploadResult {
    /**
     * 1 处理完成，0处理中
     */
    private int step;
    /**
     * 成功的数量
     */
    private int succeedNum;

    /**
     * 失败的数量
     */
    private int failureNum;

    private List<SucceedInvoice> succeedInvoices;

    private List<FailureInvoice> failureInvoices;

    @Setter
    @Getter
    public static class FailureInvoice {
        private String checkCode;
        private BigDecimal invoiceAmount;
        private String invoiceDate;
        private String invoiceNo;
        private String invoiceCode;
        private String msg;
    }

    @Setter
    @Getter
    public static class SucceedInvoice extends InvoiceEntity {
        private String fileType;

        public SucceedInvoice(){

        }
        public SucceedInvoice(InvoiceEntity invoiceEntity){

        }
    }
}
