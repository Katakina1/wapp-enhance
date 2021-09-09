package com.xforceplus.wapp.modules.report.entity;

import java.io.Serializable;

/**
 * 问题发票数量及比率
 */
public class QuestionInvoiceQuantityAndRatioEntity implements Serializable {


    private static final long serialVersionUID = -5985635758264154697L;
    private Long Id;
    private String vendername;//供应商名称
    private String venderId;//供应商号
    private Integer problemInvoice;//问题发票数量
    private Integer normalInvoice;//正常发票数量
    private String problemInvoiceRatio;//问题发票比率
    private String rownumber;//序号

    public String getRownumber() {
        return rownumber;
    }

    public void setRownumber(String rownumber) {
        this.rownumber = rownumber;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getProblemInvoice() {
        return problemInvoice;
    }

    public void setProblemInvoice(Integer problemInvoice) {
        this.problemInvoice = problemInvoice;
    }

    public Integer getNormalInvoice() {
        return normalInvoice;
    }

    public void setNormalInvoice(Integer normalInvoice) {
        this.normalInvoice = normalInvoice;
    }

    public String getProblemInvoiceRatio() {
        return problemInvoiceRatio;
    }

    public void setProblemInvoiceRatio(String problemInvoiceRatio) {
        this.problemInvoiceRatio = problemInvoiceRatio;
    }
}
