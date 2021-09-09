package com.xforceplus.wapp.modules.analysis.service.imp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.MaterialInvoiceQueryExcelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.analysis.dao.MaterialInvoiceSubmitDetailDao;
import com.xforceplus.wapp.modules.analysis.service.MaterialInvoiceSubmitDetailService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

@Service
public class MaterialInvoiceSubmitDetailServiceImpl implements MaterialInvoiceSubmitDetailService{

	@Autowired
	private MaterialInvoiceSubmitDetailDao materialInvoiceSubmitDetailDao;

	@Override
	public PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryMaterial(Map<String, Object> map) {
		PagedQueryResult<ComprehensiveInvoiceQueryEntity> result = new PagedQueryResult<>();
		Integer count = materialInvoiceSubmitDetailDao.queryCount(map);
		List<ComprehensiveInvoiceQueryEntity> queryCount = materialInvoiceSubmitDetailDao.queryMaterial(map);
		
		result.setResults(queryCount);
		result.setTotalCount(count);
		return result;
	}
    @Override
	public List<MaterialInvoiceQueryExcelEntity> toExcel(List<ComprehensiveInvoiceQueryEntity>list,Map<String, Object> map){
		List<MaterialInvoiceQueryExcelEntity> list2=new ArrayList<>();
		int page=(int)map.get("page");
		int limit=(int)map.get("limit");
		int index = (limit*(page-1))+1;
		for (ComprehensiveInvoiceQueryEntity ce:list ){
			MaterialInvoiceQueryExcelEntity me=new MaterialInvoiceQueryExcelEntity();
			me.setRownumber(""+index++);
			me.setGfName(ce.getGfName());
			me.setInvoiceDate(formatDateString(ce.getInvoiceDate()));
			me.setMatchDate(formatDate(ce.getMatchDate()));
			me.setScanMatchDate(formatDate(ce.getScanMatchDate()));
			me.setTax(ce.getTaxRate()==null?"":ce.getTaxRate().toString());
			me.setTaxAmount(CommonUtil.formatMoney(ce.getTaxAmount()));
			me.setTotalAmount(CommonUtil.formatMoney(ce.getTotalAmount()));
			me.setVenderId(ce.getVenderId());
			me.setVenderName(ce.getVenderName());
			me.setInvoiceNo(ce.getInvoiceNo());
			me.setInvoiceType(formatInvoiceType(ce.getInvoiceType()));
			list2.add(me);
		}
		return list2;
	}
	private String formatDateString(String date){
		return date == null ? "" : date.substring(0, 10);
	}

	private String formatDate(Date source) {
		return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
	}
	private String formatInvoiceType(String status){
		return null==status ? "" :
				"01".equals(status) ? "增值税专用发票" :
						"03".equals(status) ? "机动车销售统一发票" :
								"04".equals(status) ? "增值税普通发票" :
										"10".equals(status) ? "增值税电子普通发票" :
												"11".equals(status) ? "增值税普通发票（卷票）" :
														"14".equals(status) ? "增值税电子普通发票（通行费）" : "";
	}
}
