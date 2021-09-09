package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcel2Entity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.dao.MakeInvoiceSearchDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.MakeInvoiceSearchService;


@Service
public class MakeInvoiceSearchImpl implements MakeInvoiceSearchService{

	@Autowired
	private MakeInvoiceSearchDao makeInvoiceSearchDao;
	
	@Override
	public PagedQueryResult<SupplierInformationSearchEntity> queryResult(Map<String, Object> map) {
		PagedQueryResult<SupplierInformationSearchEntity > result = new PagedQueryResult<>();
		
		Integer count = count(map);
		List<SupplierInformationSearchEntity> queryResult = makeInvoiceSearchDao.queryResult(map);
		/*for (int i=queryResult.size()-1;i>=0;i--){
			if(!StringUtils.isEmpty(queryResult.get(i).getTaxNo())) {
				if (queryResult.get(i).getTaxNo().equals("0")) {
					queryResult.remove(i);
				}
			}
		}*/
		result.setTotalCount(count);
		result.setResults(queryResult);
		return result;
	}

	@Override
	public Integer count(Map<String, Object> map) {
		
		return makeInvoiceSearchDao.count(map);
	}

	@Override
	public List<SupplierInformationSearchExcel2Entity> transformExcle(List<SupplierInformationSearchEntity> results) {
		List<SupplierInformationSearchExcel2Entity> list2=new ArrayList<>();
		for(int i = 0 ; i < results.size() ; i++) {
			SupplierInformationSearchEntity entity=results.get(i);
			SupplierInformationSearchExcel2Entity supplierInformationSearchExcel2Entity =new SupplierInformationSearchExcel2Entity();

//序号
			supplierInformationSearchExcel2Entity.setIndexNo( String.valueOf(i+1));
			//JV码
			supplierInformationSearchExcel2Entity.setOrgcode( entity.getOrgcode());
			//合资公司名称
			supplierInformationSearchExcel2Entity.setOrgName(  entity.getOrgName());
			//纳税人识别号
			supplierInformationSearchExcel2Entity.setTaxno(  entity.getTaxno());
			//公司地址
			supplierInformationSearchExcel2Entity.setAddress(  entity.getAddress());
			//联系电话
			supplierInformationSearchExcel2Entity.setPhone(  entity.getPhone());
			//开户行
			supplierInformationSearchExcel2Entity.setBank(  entity.getBank());
			//账号
			supplierInformationSearchExcel2Entity.setAccount(  entity.getAccount());
			//备注
			supplierInformationSearchExcel2Entity.setRemark(  entity.getRemark());
			//成本中心
			supplierInformationSearchExcel2Entity.setStoreNumber(  entity.getStoreNumber());

			list2.add(supplierInformationSearchExcel2Entity);
		}

		return list2;
	}

}
