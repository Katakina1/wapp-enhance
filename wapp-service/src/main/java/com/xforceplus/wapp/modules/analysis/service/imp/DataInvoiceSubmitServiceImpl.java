package com.xforceplus.wapp.modules.analysis.service.imp;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.analysis.entity.InvoiceDataExcelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.analysis.dao.DataInvoiceSubmitDao;
import com.xforceplus.wapp.modules.analysis.service.DataInvoiceSubmitService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;

@Service
public class DataInvoiceSubmitServiceImpl implements DataInvoiceSubmitService{

	@Autowired
	private DataInvoiceSubmitDao dataInvoiceSubmitDao;
	
	@Override
	public PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit(Map<String, Object> map) {
		PagedQueryResult<ComprehensiveInvoiceQueryEntity> result = new PagedQueryResult<>();
		Integer count = dataInvoiceSubmitDao.queryCount(map);
		List<ComprehensiveInvoiceQueryEntity> queryCount = dataInvoiceSubmitDao.queryInvoiceSubmit(map);
		
		result.setResults(queryCount);
		result.setTotalCount(count);
		return result;
	}

	@Override
	public PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryRealInvoiceSubmit(Map<String, Object> map) {
		PagedQueryResult<ComprehensiveInvoiceQueryEntity> result = new PagedQueryResult<>();
		Integer count = dataInvoiceSubmitDao.queryRealCount(map);
		List<ComprehensiveInvoiceQueryEntity> queryCount = dataInvoiceSubmitDao.queryRealInvoiceSubmit(map);

		result.setResults(queryCount);
		result.setTotalCount(count);
		return result;
	}
    @Override
	public Integer queryRealCount(Map<String,Object> params){
		return dataInvoiceSubmitDao.queryRealCount(params);
	}
	@Override
	public List<InvoiceDataExcelEntity> queryInvoiceSubmitForExcel(Map<String, Object> map) {
		List<InvoiceDataExcelEntity> ExcelList = new LinkedList();
		List<ComprehensiveInvoiceQueryEntity> queryCount=null;
		if(map.get("type").toString()=="1"){
			 queryCount=dataInvoiceSubmitDao.queryInvoiceSubmit(map);
		}else{
			 queryCount = dataInvoiceSubmitDao.queryRealInvoiceSubmit(map);
		}
		int index = 1;
		int total = 0;
		InvoiceDataExcelEntity excel = null;
		for(ComprehensiveInvoiceQueryEntity entity:queryCount){
			excel= new InvoiceDataExcelEntity();
			excel.setRownumber(entity.getRownumber());
			excel.setVendorid(entity.getVenderId());
			excel.setVendorName(entity.getVenderName());
			excel.setTotalNum(entity.getCountNum()+"");
			total = total+entity.getCountNum();
			ExcelList.add(excel);
		}
//		excel = new InvoiceDataExcelEntity();
//		excel.setRownumber("合计");
//		excel.setTotalNum(total+"");
//		ExcelList.add(excel);
		return ExcelList;
	}

	@Override
	public List<InvoiceDataExcelEntity> queryRealInvoiceSubmitForExcel(Map<String, Object> map) {
		List<InvoiceDataExcelEntity> ExcelList = new LinkedList();
		List<ComprehensiveInvoiceQueryEntity> queryCount = dataInvoiceSubmitDao.queryRealInvoiceSubmit(map);
		int index = 1;
		int total = 0;
		InvoiceDataExcelEntity excel = null;
		for(ComprehensiveInvoiceQueryEntity entity:queryCount){
			excel= new InvoiceDataExcelEntity();
			excel.setRownumber(""+index++);
			excel.setVendorid(entity.getVenderId());
			excel.setVendorName(entity.getVenderName());
			excel.setTotalNum(entity.getCountNum()+"");
			total = total+entity.getCountNum();
			ExcelList.add(excel);
		}
		excel = new InvoiceDataExcelEntity();
		excel.setRownumber("合计");
		excel.setTotalNum(total+"");
		ExcelList.add(excel);
		return ExcelList;
	}
}
