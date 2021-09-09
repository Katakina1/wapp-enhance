package com.xforceplus.wapp.modules.collect.pojo;

import com.xforceplus.wapp.modules.job.pojo.BasePojo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 查验发票响应明细
 * @author Colin.hu
 * @date 4/17/2018
 */
@Getter @Setter @ToString
public class InvoiceDetail extends BasePojo {

    private static final long serialVersionUID = -5224647604590979343L;

    /**
     * 明细编号
     */
    private String detailNo;

    public String getDetailNo() {
		return detailNo;
	}

	public void setDetailNo(String detailNo) {
		this.detailNo = detailNo;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getSpecificationModel() {
		return specificationModel;
	}

	public void setSpecificationModel(String specificationModel) {
		this.specificationModel = specificationModel;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getDetailAmount() {
		return detailAmount;
	}

	public void setDetailAmount(String detailAmount) {
		this.detailAmount = detailAmount;
	}

	public String getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(String taxRate) {
		this.taxRate = taxRate;
	}

	public String getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	public String getCostItem() {
		return costItem;
	}

	public void setCostItem(String costItem) {
		this.costItem = costItem;
	}

	public String getCostAmount() {
		return costAmount;
	}

	public void setCostAmount(String costAmount) {
		this.costAmount = costAmount;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
	}

	public String getCph() {
		return cph;
	}

	public void setCph(String cph) {
		this.cph = cph;
	}

	public String getTxrqq() {
		return txrqq;
	}

	public void setTxrqq(String txrqq) {
		this.txrqq = txrqq;
	}

	public String getTxrqz() {
		return txrqz;
	}

	public void setTxrqz(String txrqz) {
		this.txrqz = txrqz;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格型号
     */
    private String specificationModel;

    /**
     * 计量单位
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
     * 费用项目
     */
    private String costItem;

    /**
     * 费用金额
     */
    private String costAmount;

    /**
     * 类型
     */
    private String lx;

    /**
     * 车牌号
     */
    private String cph;

    /**
     * 通行日期起
     */
    private String txrqq;

    /**
     * 通行日期至
     */
    private String txrqz;
}
