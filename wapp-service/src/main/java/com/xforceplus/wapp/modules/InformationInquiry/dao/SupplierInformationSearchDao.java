package com.xforceplus.wapp.modules.InformationInquiry.dao;

import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import org.apache.ibatis.annotations.Mapper;

import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface SupplierInformationSearchDao {

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

	/**
	 * 获取供应商类型
	 * @return
	 */
    List<OptionEntity> getSupplierTypeList();
    //修改供应商类型
	int updateSupplierTypeBath(@Param("id") long id, @Param("supplierType")String supplierType);
}
