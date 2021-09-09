package com.xforceplus.wapp.modules.analysis.service;

import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.analysis.entity.InvoiceDataExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

/**
 *数据发票提交统计
 */
public interface DataInvoiceSubmitService {

	/**
	 * 统计供应商发票提交数量
	 * @param map
	 * @return
	 */
	 PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit(Map<String, Object> map);

	/**
	 * 实物发票提交统计数量
	 * @param map
	 * @return
	 */
	PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryRealInvoiceSubmit(Map<String, Object> map);

    Integer queryRealCount(Map<String,Object> params);
	List<InvoiceDataExcelEntity> queryInvoiceSubmitForExcel(Map<String,Object> params);

	List<InvoiceDataExcelEntity> queryRealInvoiceSubmitForExcel(Map<String,Object> params);
}
