package com.xforceplus.wapp.modules.InformationInquiry.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;


@Mapper
public interface MakeInvoiceSearchDao {

	/**
	 * 查询供应商信息
	 * @param map
	 * @return
	 */
	List<SupplierInformationSearchEntity> queryResult(Map<String, Object> map);
	
	/**
	 * 查询结果数量
	 * @param map
	 * @return
	 */
	Integer count(Map<String, Object> map);
}
