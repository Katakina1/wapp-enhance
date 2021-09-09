package com.xforceplus.wapp.modules.analysis.service;

import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.MaterialInvoiceQueryExcelEntity;

/**
 *实物发票提交明细
 */
public interface MaterialInvoiceSubmitDetailService {

	/**
	 *查询实物发票提交明细
	 * @param map
	 * @return
	 */
	 PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryMaterial(Map<String, Object> map);
	 
	 List<MaterialInvoiceQueryExcelEntity> toExcel(List<ComprehensiveInvoiceQueryEntity> list,Map<String, Object> map);
}
