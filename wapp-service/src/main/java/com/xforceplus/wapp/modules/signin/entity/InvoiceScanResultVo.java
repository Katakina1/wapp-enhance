package com.xforceplus.wapp.modules.signin.entity;

import java.util.List;


/** 
* @ClassName: InvoiceSignController 
* @Description: 发票扫描返回页面结果对象
* @author yuanlz
* @date 2016年12月5日 下午10:07:21 
*  
*/
public class InvoiceScanResultVo{

	private static final long serialVersionUID = 1L;
	
	
	// 扫描数量
	private int scanNum;
	// 成功数量
	private int successNum;
	// 签收失败数量
	private int wqsNum;
	// 签收失败数量
	private int qsFailNum;
	// 入库失败数量
	private int rkFailNum;
	// 扫描路径
	private String scanPath;
	// 购方名称
	private String gfName;
	//是否为修改
	private int type;
	// 签收结果
	private List<SignedInvoiceVo> invoiceList;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getScanNum() {
		return scanNum;
	}
	public void setScanNum(int scanNum) {
		this.scanNum = scanNum;
	}
	public int getSuccessNum() {
		return successNum;
	}
	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}
	
	public int getWqsNum() {
		return wqsNum;
	}
	public void setWqsNum(int wqsNum) {
		this.wqsNum = wqsNum;
	}
	public int getQsFailNum() {
		return qsFailNum;
	}
	public void setQsFailNum(int qsFailNum) {
		this.qsFailNum = qsFailNum;
	}
	public int getRkFailNum() {
		return rkFailNum;
	}
	public void setRkFailNum(int rkFailNum) {
		this.rkFailNum = rkFailNum;
	}
	public String getScanPath() {
		return scanPath;
	}
	public void setScanPath(String scanPath) {
		this.scanPath = scanPath;
	}
	public String getGfName() {
		return gfName;
	}
	public void setGfName(String gfName) {
		this.gfName = gfName;
	}
	public List<SignedInvoiceVo> getInvoiceList() {
		return invoiceList;
	}
	public void setInvoiceList(List<SignedInvoiceVo> invoiceList) {
		this.invoiceList = invoiceList;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
