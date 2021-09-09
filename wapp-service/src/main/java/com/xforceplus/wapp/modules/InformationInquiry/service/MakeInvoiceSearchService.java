package com.xforceplus.wapp.modules.InformationInquiry.service;

import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcel2Entity;


/**
 * 
 * 供应商信息查询
 *
 */
public interface MakeInvoiceSearchService {

	/**
	 * 查出符合条件的供应商信息
	 * @param map
	 * @return
	 */
	PagedQueryResult<SupplierInformationSearchEntity> queryResult(Map<String, Object> map);
	
	
	/**
	 * 查出的信息数量
	 * @param map
	 * @return
	 */
	Integer count(Map<String, Object> map);

    List<SupplierInformationSearchExcel2Entity> transformExcle(List<SupplierInformationSearchEntity> results);
}
