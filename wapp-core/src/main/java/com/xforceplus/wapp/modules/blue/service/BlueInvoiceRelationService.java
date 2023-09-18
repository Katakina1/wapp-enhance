package com.xforceplus.wapp.modules.blue.service;

import com.xforceplus.wapp.modules.backfill.model.BackFillVerifyBean;
import com.xforceplus.wapp.repository.entity.TXfBlueRelationEntity;

import java.util.List;

public interface BlueInvoiceRelationService {

	/**
	 * 批量保存
	 * @param originInvoiceNo
	 * @param originInvoiceCode
	 * @param blueInvoices
	 * @return
	 */
    boolean saveBatch(String originInvoiceNo, String originInvoiceCode, List<BackFillVerifyBean> blueInvoices) ;

    /**
     * 检查蓝字发票是否存在
     * @param blueInvoiceNo
     * @param blueInvoiceCode
     * @return
     */
    boolean existsByBlueInvoice(String blueInvoiceNo, String blueInvoiceCode);
    
    /**
     * 检查蓝字发票是否存在
     * @param redInvoiceNo
     * @param redInvoiceCode
     * @return
     */
    boolean existsByRedInvoice(String redInvoiceNo, String redInvoiceCode);

	/**
	 * 删除红蓝关系(根据红票)
	 * @param redInvoiceNo 红字发票号码
	 * @param redInvoiceCode 红字发票代码
	 * @return
	 */
	boolean deleteByRedInvoice(String redInvoiceNo, String redInvoiceCode);

	/**
	 * 获取红票蓝冲关系
	 * @param redInvoiceNo 红字发票号码
	 * @param redInvoiceCode 红字发票代码
	 * @return
	 */
	List<TXfBlueRelationEntity> getByRedInfo(String redInvoiceNo, String redInvoiceCode);

	/**
	 * 获取红票蓝冲关系
	 * @param blueInvoiceNo 蓝字发票号码
	 * @param blueInvoiceCode 蓝字发票代码
	 * @return
	 */
	TXfBlueRelationEntity getByBlueInfo(String blueInvoiceNo, String blueInvoiceCode);
}
