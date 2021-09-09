package com.xforceplus.wapp.modules.analysis.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;


/**
 * 数据发票提交统计
 *
 */

@Mapper
public interface DataInvoiceSubmitDao {

	/**
	 * 统计供应商发票提交数量
	 * @param map
	 * @return
	 */
	List<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit(Map<String, Object> map);
	
	/**
	 * 供应商提交发票总量
	 * @param map
	 * @return
	 */
	Integer queryCount(Map<String, Object> map);
	/**
	 * 实物发票提交统计
	 * @param map
	 * @return
	 */
	List<ComprehensiveInvoiceQueryEntity> queryRealInvoiceSubmit(Map<String, Object> map);

	/**
	 * 实物发票提交统计总量
	 * @param map
	 * @return
	 */
	Integer queryRealCount(Map<String, Object> map);
}
