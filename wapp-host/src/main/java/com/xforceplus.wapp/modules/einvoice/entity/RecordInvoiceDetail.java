package com.xforceplus.wapp.modules.einvoice.entity;

import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created on 2018/04/19.
 * @author marvin
 * 底账表明细实体类
 */
@Getter
@Setter
@ToString
public class RecordInvoiceDetail extends AbstractBaseDomain {

    private static final long serialVersionUID = 227596895014450330L;

    /**
     *  发票代码 + 发票号码
     */
    private String uuid;
    /**
     * 发票代码
     */
    private String invoiceCode;
    public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getInvoiceCode() {
		return invoiceCode;
	}

	public void setInvoiceCode(String invoiceCode) {
		this.invoiceCode = invoiceCode;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
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

	public String getCph() {
		return cph;
	}

	public void setCph(String cph) {
		this.cph = cph;
	}

	public String getLx() {
		return lx;
	}

	public void setLx(String lx) {
		this.lx = lx;
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

	public String getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(String goodsNum) {
		this.goodsNum = goodsNum;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

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
     * 车牌号
     */
    private String cph;
    /**
     * 类型
     */
    private String lx;
    /**
     * 通行日期起
     */
    private String txrqq;
    /**
     * 通行日期止
     */
    private String txrqz;
    /**
     * 商品编码
     */
    private String goodsNum;

    @Override
    public Boolean isNullObject() {
        return null;
    }
}
