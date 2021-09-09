package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.modules.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Max.han on 2018/04/12.
 *
 * @author 电票查询实体类
 */
@Getter
@Setter
@ToString
public class EinvoiceQueryEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1841531024607174383L;

    /**
     * 购方税号
     */
    private String gfTaxNo;

    public String getGfTaxNo() {
		return gfTaxNo;
	}

	public void setGfTaxNo(String gfTaxNo) {
		this.gfTaxNo = gfTaxNo;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getQsStartDate() {
		return qsStartDate;
	}

	public void setQsStartDate(String qsStartDate) {
		this.qsStartDate = qsStartDate;
	}

	public String getQsEndDate() {
		return qsEndDate;
	}

	public void setQsEndDate(String qsEndDate) {
		this.qsEndDate = qsEndDate;
	}

	public List<String> getTaxNos() {
		return taxNos;
	}

	public void setTaxNos(List<String> taxNos) {
		this.taxNos = taxNos;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 签收开始日
     */
    private String qsStartDate;

    /**
     * 签收结束日期
     */
    private String qsEndDate;

    /**
     * 用户关联的税号
     */
    private List<String> taxNos;

    private Long userId;

}
