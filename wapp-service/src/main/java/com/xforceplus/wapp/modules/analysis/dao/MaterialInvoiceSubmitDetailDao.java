package com.xforceplus.wapp.modules.analysis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;


/**
 * 实物发票提交明细
 *
 */

@Mapper
public interface MaterialInvoiceSubmitDetailDao {

	/**
	 * 查询实物发票提交明细
	 * @param map
	 * @return
	 */
	List<ComprehensiveInvoiceQueryEntity> queryMaterial(Map<String, Object> map);
	
	/**
	 * 实物发票提交总量
	 * @param map
	 * @return
	 */
	Integer queryCount(Map<String, Object> map);
}
