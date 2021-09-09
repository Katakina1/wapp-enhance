package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcelEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.modules.InformationInquiry.dao.SupplierInformationSearchDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.SupplierInformationSearchService;


@Service
public class SupplierInformationSearchImpl implements SupplierInformationSearchService{

	@Autowired
	private SupplierInformationSearchDao supplierInformationSearchDao;
	
	@Override
	public PagedQueryResult<SupplierInformationSearchEntity> queryResult(Map<String, Object> map) {
		PagedQueryResult<SupplierInformationSearchEntity > result = new PagedQueryResult<>();
		
		Integer count = count(map);
		List<SupplierInformationSearchEntity> queryResult = supplierInformationSearchDao.queryResult(map);
		
		result.setTotalCount(count);
		result.setResults(queryResult);
		return result;
	}

	@Override
	public Integer count(Map<String, Object> map) {
		
		return supplierInformationSearchDao.count(map);
	}

	@Override
	public List<OptionEntity> getSupplierTypeList() {
		return supplierInformationSearchDao.getSupplierTypeList();
	}

	@Override
	public String updateSupplierTypeBath(Map<String, Object> param) {
		boolean flag =false;
		String message = "";
		JSONArray arr = JSONArray.fromObject(param.get("idList"));
		String supplierType = (String) param.get("supplierType");
		for (int i = 0; i < arr.size(); i++) {
			long id = Long.valueOf(String.valueOf(arr.get(i))).longValue();
			flag =supplierInformationSearchDao.updateSupplierTypeBath(id,supplierType)>0;
			if(!flag){
				message= "id:"+id +"修改失败";
			}
		}
		if(null==message  ||"".equals(message)){
			message="success";
		}
		return message;
	}

	@Override
	public List<SupplierInformationSearchExcelEntity> transformExcle(List<SupplierInformationSearchEntity> results) {
		List<SupplierInformationSearchExcelEntity> list2=new ArrayList<>();
		for(int i = 0 ; i < results.size() ; i++) {
			SupplierInformationSearchEntity entity=results.get(i);
			SupplierInformationSearchExcelEntity supplierInformationSearchExcelEntity=new SupplierInformationSearchExcelEntity();

//序号
			supplierInformationSearchExcelEntity.setIndexNo( String.valueOf(i+1));
			//供应商业务类型
			supplierInformationSearchExcelEntity.setUsertype(  fromatUserType(entity.getUsertype()));
			//供应商号码
			supplierInformationSearchExcelEntity.setUserCode(  entity.getUserCode());
			//供应商名字
			supplierInformationSearchExcelEntity.setUserName(  entity.getUserName());
			//供应商税号
			supplierInformationSearchExcelEntity.setTaxno( entity.getTaxno());
			//联系人
			supplierInformationSearchExcelEntity.setFinusername(  entity.getFinusername());
			//联系电话
			supplierInformationSearchExcelEntity.setPhone(  entity.getPhone());
			//传真	
			supplierInformationSearchExcelEntity.setFax( entity.getFax());
			//邮寄地址
			supplierInformationSearchExcelEntity.setEmail(  entity.getEmail());
			//邮寄方式
			supplierInformationSearchExcelEntity.setPostType( entity.getPostType());
			//城市
			supplierInformationSearchExcelEntity.setCity(  entity.getCity());

			//邮寄地址
			supplierInformationSearchExcelEntity.setPostAddress(  entity.getPostAddress());
			//供应闪公告类型
			supplierInformationSearchExcelEntity.setOrgLevel( formatOrgLevel(entity.getOrgLevel()));
			supplierInformationSearchExcelEntity.setType(entity.getUsertype());
			supplierInformationSearchExcelEntity.setExtf0(fromatextf0(entity.getExtf0()));
			supplierInformationSearchExcelEntity.setExtf1(fromatextf1(entity.getExtf1()));
			list2.add(supplierInformationSearchExcelEntity);
		}
		return list2;
	}

	private String  formatOrgLevel(String usertype) {
		if(StringUtils.isEmpty(usertype)){
			return "--";
		}
		if(usertype.equals("0")){
			return "KEY Vendor";
		}else if(usertype.equals("1")){
			return "VIP Vendor";
		}else if(usertype.equals("2")){
			return "其他";
		}else {
			return "--";
		}


	}

	private String fromatUserType(String usertype) {
		if(StringUtils.isEmpty(usertype)){
			return "--";
		}
		if(usertype.equals("P")||usertype.equals("PP")){
			return "商品";
		}else if(usertype.equals("E")||usertype.equals("EJ")){
			return "费用";
		}else{
			return "--";
		}
	}
	private String fromatextf0(String usertype) {
		if(StringUtils.isEmpty(usertype)){
			return "正常";
		}
		if(usertype.equals("0")||usertype.equals("FALSE")){
			return "正常";
		}else if(usertype.equals("1")||usertype.equals("TRUE")){
			return "冻结";
		}else{
			return "正常";
		}
	}
	private String fromatextf1(String usertype) {
		if(StringUtils.isEmpty(usertype)){
			return "正常";
		}
		if(usertype.equals("0")||usertype.equals("FALSE")){
			return "正常";
		}else if(usertype.equals("1")||usertype.equals("TRUE")){
			return "删除";
		}else{
			return "正常";
		}
	}
}
