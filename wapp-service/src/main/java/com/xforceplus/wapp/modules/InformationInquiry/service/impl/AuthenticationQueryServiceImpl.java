package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.InformationInquiry.dao.AuthenticationQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.dao.AuthenticationResultQueryDao;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationQueryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.AuthenticationResultQueryService;
import com.xforceplus.wapp.modules.base.dao.AribaBillTypeDao;
import com.xforceplus.wapp.modules.certification.dao.CertificationQueryDao;
import com.xforceplus.wapp.modules.collect.entity.AribaInvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionTaxExcelInfo;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;

/**
 * Created by 1 on 2018/12/14 23:01
 */
@Service
public class AuthenticationQueryServiceImpl implements AuthenticationQueryService {

    private final AuthenticationQueryDao authenticationQueryDao;
    private final CertificationQueryDao certificationQueryDao;
    private final AribaBillTypeDao aribaBillTypeDao;

    @Autowired
    public AuthenticationQueryServiceImpl(AuthenticationQueryDao authenticationQueryDao, CertificationQueryDao certificationQueryDao,AribaBillTypeDao aribaBillTypeDao) {
        this.authenticationQueryDao = authenticationQueryDao;
        this.certificationQueryDao = certificationQueryDao;
        this.aribaBillTypeDao =aribaBillTypeDao;

    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> queryCertification(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        ReportStatisticsEntity result = authenticationQueryDao.getCertificationListCount(map);

        //需要返回的集合
        List<InvoiceCollectionInfo> infoArrayList = new ArrayList<>();
       if (result.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = authenticationQueryDao.selectCertificationList(map);
            for (InvoiceCollectionInfo entity: infoArrayList) {
                entity.setServiceType(formatServiceType(entity));
                entity.setSmoking("否");
                entity.setFlowType(formatFlowType(entity));
                if(entity.getInvoiceType().equals("04")&&entity.getDkInvoiceAmount()!=null ){
                    if(entity.getDkInvoiceAmount().compareTo(BigDecimal.ZERO)>0){
                        entity.setInvoiceAmount(entity.getDkInvoiceAmount().toString());
                        entity.setTaxAmount(entity.getDeductibleTax().toString());
                        entity.setTaxRate(entity.getDeductibleTaxRate().toString());
                        entity.setTotalAmount((entity.getDkInvoiceAmount().add(entity.getDeductibleTax())).toString());
                    }

                }
                if(entity.getFlowType().equals("1")||entity.getFlowType().equals("7")||entity.getFlowType().equals("6")){
                       String store=authenticationQueryDao.selectStore(entity.getJvCode());
                        entity.setStore(store);
                }else if (entity.getFlowType().equals("2")){
                    entity.setStore(entity.getCostDeptId());
                }
                //获取税码
                entity.setTaxCode(formatTaxCode(entity));
                entity.setJvCode(formatJv(entity));
                entity.setTaxRate(formatTaxRate(entity.getTaxRate(),entity.getFlowType(),entity.getInvoiceNo(),entity.getInvoiceCode()));
            }
        }
        pagedQueryResult.setTotalCount(result.getTotalCount());
        pagedQueryResult.setTotalAmount(result.getTotalAmount());
        pagedQueryResult.setTotalTax(result.getTotalTax());
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public PagedQueryResult<InvoiceCollectionInfo> queryAribaCertification(Map<String, Object> map) {
        final PagedQueryResult<InvoiceCollectionInfo> pagedQueryResult = new PagedQueryResult<>();
        ReportStatisticsEntity result = authenticationQueryDao.getAribaCertificationListCount(map);

        //需要返回的集合
        List<InvoiceCollectionInfo> infoArrayList = new ArrayList<>();
        if (result.getTotalCount() > 0) {
            //根据条件查询符合条件的数据集
            infoArrayList = authenticationQueryDao.selectAribaCertificationList(map);
            for (InvoiceCollectionInfo entity: infoArrayList) {
                //entity.setServiceType(formatServiceType(entity));
                entity.setSmoking("否");
                entity.setCpUserId("GNFR");
                if(entity.getFlowType().equals("0")){
                    entity.setDkTaxAmount(entity.getTaxAmount());
                }
                entity.setTaxRate(formatAribaTaxRate(entity.getTaxRate()));
                //entity.setFlowType(formatFlowType(entity));
//                if(entity.getInvoiceType().equals("04")&&entity.getDkInvoiceAmount()!=null ){
//                    if(entity.getDkInvoiceAmount().compareTo(BigDecimal.ZERO)>0){
//                        entity.setInvoiceAmount(entity.getDkInvoiceAmount().toString());
//                        entity.setTaxAmount(entity.getDeductibleTax().toString());
//                        entity.setTaxRate(entity.getDeductibleTaxRate().toString());
//                        entity.setTotalAmount((entity.getDkInvoiceAmount().add(entity.getDeductibleTax())).toString());
//                    }
//                }
            }
        }
        pagedQueryResult.setTotalCount(result.getTotalCount());
        pagedQueryResult.setTotalAmount(result.getTotalAmount());
        pagedQueryResult.setTotalTax(result.getTotalTax());
        pagedQueryResult.setResults(infoArrayList);
        return pagedQueryResult;
    }

    @Override
    public ReportStatisticsEntity getCertificationListCount(Map<String, Object> map){
        return  authenticationQueryDao.getCertificationListCount(map);
}
    @Override
    public ReportStatisticsEntity getAribaCertificationListCount(Map<String, Object> map){
        return  authenticationQueryDao.getAribaCertificationListCount(map);
    }
    private String formatServiceType(InvoiceCollectionInfo entity) {
        //商品业务类型
        if(entity.getFlowType().equals("1")){
            return  "商品付款类";
        }

        //费用 业务类型
        if(entity.getFlowType().equals("2")){
            //return  "费用付款类";
            String uuid=entity.getInvoiceCode()+entity.getInvoiceNo();
            List<OptionEntity> costTaxCode = certificationQueryDao.getCostTaxCode(uuid);
            if(costTaxCode.size()>0&&costTaxCode.get(0).getValue()!=null){
                String costServiceType = costTaxCode.get(0).getCostTypeName();
                if(StringUtils.isNotEmpty(costServiceType)){
                    return costServiceType;
                }
            }
        }
        if(entity.getFlowType().equals("7")){
            return  "直接认证";
        }
        if(entity.getFlowType().equals("8")){
          String type=  aribaBillTypeDao.queryServiceName(entity.getMccCode(),entity.getGlAccount());
            return  type;
        }
        return  "";
    }
    //费用jv
    private String formatJv(InvoiceCollectionInfo entity) {
        String jv="";
        if(entity.getFlowType().equals("2")){
            if(StringUtils.isNotBlank(entity.getCostDeptId())){
                jv=authenticationQueryDao.selectCostDeptToJv(entity.getCostDeptId());
            }else{
                jv=entity.getJvCode();
            }
        }else{
            jv=entity.getJvCode();
        }
        return jv;
    }
    private OptionEntity queryHostTaxRate(String value){
        return certificationQueryDao.queryHostTaxRate(value);
    }
    //税码获取
    private String formatTaxCode(InvoiceCollectionInfo entity) {
        String invoiceType = entity.getInvoiceType();
        //商品类 host税率
        if(entity.getFlowType().equals("1") && entity.getTaxRate()!=null &&invoiceType!=null){
            String hostTaxRate = (entity.getTaxRate()).substring(0,(entity.getTaxRate()).indexOf('.'));
            if(hostTaxRate.isEmpty()){
                return "";
            }
            String value = invoiceType+'_'+hostTaxRate;
            OptionEntity optionEntity = queryHostTaxRate(value);
            if(optionEntity!=null&&optionEntity.getLabel()!=null){
                if(StringUtils.isNotEmpty(optionEntity.getLabel())){
                    return  optionEntity.getLabel();
                }
            }

           return "";

        }
        if(entity.getFlowType().equals("6")){
            return entity.getTaxCode();
        }
        //直接认证并且是店转店则和商品税码一致
        if(entity.getFlowType().equals("7")){
            String orgType=certificationQueryDao.selectOrgType(entity.getXfTaxNo(),entity.getXfName());
        if(StringUtils.isNotEmpty(orgType)){
            if(orgType.equals("5")){
                //商品类 host税率
                if(entity.getTaxRate()!=null &&invoiceType!=null){
                    String hostTaxRate = (entity.getTaxRate()).substring(0,(entity.getTaxRate()).indexOf('.'));
                    if(hostTaxRate.isEmpty()){
                        return "";
                    }
                    String value = invoiceType+'_'+hostTaxRate;
                    OptionEntity optionEntity = queryHostTaxRate(value);
                    if(optionEntity!=null&&optionEntity.getLabel()!=null){
                        if(StringUtils.isNotEmpty(optionEntity.getLabel())){
                            return  optionEntity.getLabel();
                        }
                    }
                    return "";
                }
            }
        }
        }
        //费用 获取taxCode
        if(entity.getFlowType().equals("2")){
            String uuid=entity.getInvoiceCode()+entity.getInvoiceNo();
            List<OptionEntity> costTaxCode = this.certificationQueryDao.getCostTaxCode(uuid);
            if(costTaxCode.size()>0){
                if(StringUtils.isBlank(entity.getTaxRate())) {
                    List<String> listNew=new ArrayList<>();
                    String taxCode = "";
                    for (OptionEntity oe : costTaxCode) {
                        if(!listNew.contains(oe.getLabel())){
                            listNew.add(oe.getLabel());
                        }
                    }
                    for (String taxCodes:listNew) {
                        taxCode = taxCode + taxCodes + ",";
                    }
                    if(!taxCode.equals("")){
                        taxCode=taxCode.substring(0,taxCode.length()-1);
                    }
                    return taxCode;
                }
            }
            if(costTaxCode.size()>0&&costTaxCode.get(0).getLabel()!=null){
                return  costTaxCode.get(0).getLabel();
            }
           return "";
        }
        return "";
    }
    //店转店大类
    private String formatFlowType(InvoiceCollectionInfo entity) {
        String daLei = "";
        if (entity.getFlowType().equals("7")) {
            String orgType = certificationQueryDao.selectOrgType(entity.getXfTaxNo(), entity.getXfName());
            if (StringUtils.isNotEmpty(orgType)) {
                if (orgType.equals("5")) {
                    daLei="999";
                }else{
                    daLei=entity.getFlowType();
                }
            }else{
                daLei=entity.getFlowType();
            }
        }else{
            daLei=entity.getFlowType();
        }
        return daLei;
    }
    @Override
    public List<InvoiceCollectionTaxExcelInfo> queryCertificationForExcel(Map<String, Object> map) {
        List<InvoiceCollectionInfo> infoArrayList = authenticationQueryDao.selectCertificationList(map);
        List<InvoiceCollectionTaxExcelInfo> excelList = new LinkedList();
        InvoiceCollectionTaxExcelInfo excel = null;
        for(InvoiceCollectionInfo info:infoArrayList){
            if(info.getInvoiceType().equals("04")&&info.getDkInvoiceAmount()!=null ){
                if(info.getDkInvoiceAmount().compareTo(BigDecimal.ZERO)>0){
                    info.setInvoiceAmount(info.getDkInvoiceAmount().toString());
                    info.setTaxAmount(info.getDeductibleTax().toString());
                    info.setTaxRate(info.getDeductibleTaxRate().toString());
                    info.setTotalAmount((info.getDkInvoiceAmount().add(info.getDeductibleTax())).toString());
                }

            }
            info.setTaxCode(formatTaxCode(info));
            info.setServiceType(formatServiceType(info));
            info.setJvCode(formatJv(info));
            excel = new InvoiceCollectionTaxExcelInfo();
            excel.setCertificateNo(info.getCertificateNo());
            excel.setCompanyCode(info.getCompanyCode());
            //excel.setCostDeptId(info.getCostDeptId());
            excel.setEpsNo(info.getEpsNo());
            excel.setFlowType(formatDaLeiType(info.getFlowType(),info.getXfTaxNo(),info.getXfName()));
            excel.setGfName(info.getGfName());
            excel.setInvoiceAmount(info.getInvoiceAmount());
            excel.setInvoiceCode(info.getInvoiceCode());
            excel.setInvoiceDate(formatDate(info.getInvoiceDate()));
            excel.setInvoiceNo(info.getInvoiceNo());
            excel.setIsOver("否");//暂时为空 不确定
            excel.setJvCode(info.getJvCode());
            excel.setQsDate(formatDate(info.getQsDate()));
            excel.setRemark(info.getRemark());
            excel.setRownumber(info.getRownumber());
            excel.setRzhBelongDate(info.getRzhBelongDate());
            excel.setScanName(info.getScanName());
            excel.setServiceType(info.getServiceType());
            if(info.getFlowType().equals("1")||info.getFlowType().equals("7")||info.getFlowType().equals("2")||info.getFlowType().equals("6")){
                if (info.getFlowType().equals("2")){
                    excel.setStoreNo(info.getCostDeptId());
                }else {
                    String store=authenticationQueryDao.selectStore(info.getJvCode());
                    excel.setStoreNo(store);
                }
                 //待确认
            }
            excel.setTaxAmount(info.getTaxAmount());
            excel.setTaxCode(info.getTaxCode());
            excel.setTaxRate(formatTaxRate(info.getTaxRate(),info.getFlowType(),info.getInvoiceNo(),info.getInvoiceCode()));
            excel.setTotalAmount(formatTotalAmount(info.getTotalAmount()));
            excel.setVenderid(info.getVenderid());
            excel.setVendername(info.getVendername());
            excel.setInvoiceType(invResult(info.getInvoiceType()));
            excel.setGfTaxNo(info.getGfTaxNo());
            excel.setGl(formatGl(info.getGl()));
            excelList.add(excel);
        }
        return excelList;
    }

    @Override
    public List<InvoiceCollectionTaxExcelInfo> queryAribaCertificationForExcel(Map<String, Object> map) {
        List<InvoiceCollectionInfo> infoArrayList = authenticationQueryDao.selectAribaCertificationList(map);
        List<InvoiceCollectionTaxExcelInfo> excelList = new LinkedList();
        InvoiceCollectionTaxExcelInfo excel = null;
        for(InvoiceCollectionInfo info:infoArrayList){
            //info.setTaxCode(formatTaxCode(info));
            //info.setServiceType(formatServiceType(info));
            info.setJvCode(formatJv(info));
            excel = new InvoiceCollectionTaxExcelInfo();
            if(info.getFlowType().equals("0")){
                excel.setFlowType("资产类");
                excel.setJxsj(info.getTaxAmount());
            }else{
                excel.setFlowType("费用类");
            }
            excel.setGfName(info.getGfName());
            excel.setGfTaxNo(info.getGfTaxNo());
            excel.setInvoiceCode(info.getInvoiceCode());
            excel.setInvoiceDate(formatDate(info.getInvoiceDate()));
            excel.setInvoiceNo(info.getInvoiceNo());
            excel.setIsOver("否");
            excel.setScanName("GNFR");
            excel.setJvCode(info.getJvCode());
            excel.setRemark(info.getRemark());
            excel.setRownumber(info.getRownumber());
            excel.setServiceType(info.getServiceType());
            excel.setTaxAmount(info.getTaxAmount());
            excel.setTaxCode(info.getTaxCode());
            excel.setTaxRate(formatAribaTaxRate(info.getTaxRate()));
            excel.setTotalAmount(formatTotalAmount(info.getTotalAmount()));
            excel.setVenderid(info.getVenderid());
            excel.setVendername(info.getVendername());
            excel.setMccCode(info.getMccCode());
            excel.setKm(info.getGlAccount());
            excel.setStoreNo(info.getStore());
            excel.setQsDate(formatDate(info.getQsDate()));
            excelList.add(excel);
        }
        return excelList;
    }

    private String formatTotalAmount(String totalAmount) {
        if("".equals(totalAmount)|| totalAmount == null){
            return "--";
        }else {
            return  totalAmount.substring(0, totalAmount.indexOf('.')+3);
        }
    }

    private String formatTaxRate(String taxRate,String type,String invNo,String invCode) {
        if(type.equals("2")){
            if(StringUtils.isBlank(taxRate)){
              List<String> taxRateList= certificationQueryDao.getTaxRateDetail(invNo,invCode);
             String taxRates="";
              if(taxRateList.size()>0){
                  for (String tax:taxRateList) {
                      taxRates= taxRates+tax+"%,";
                  }
                  if(!taxRates.equals("")){
                      taxRates=taxRates.substring(0,taxRates.length()-1);
                  }
                  return taxRates;
              }
            }else{
                return  taxRate.substring(0, taxRate.indexOf('.')) + "%";
            }
        }
        if ("".equals(taxRate)|| taxRate==null) {
            return "--";
        }else {
            return  taxRate.substring(0, taxRate.indexOf('.')) + "%";
        }
    }
    private String formatAribaTaxRate(String taxRate){
        String tax ="";
        if ("".equals(taxRate)|| taxRate==null) {
            return "--";
        }else {
            try {
                tax = taxRate.substring(0, taxRate.indexOf('.')) + "%";
            }catch (Exception e){
                tax=taxRate;
            }
            return  tax;
        }
    }
    private String formatGl(String jpl){
        if(jpl!=null){
            if(jpl.equals("1")){
                return "是";
            }else{
                return "否";
            }
        }else{
            return "——";
        }

    }
    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatRzhType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "线上认证";
        } else  if("2".equals(authStatus)) {
            authStatusName = "线下认证";
        }
        return authStatusName;
    }

    private String formatDaLeiType(String authStatus,String xfTaxNo,String xfName) {
        String authStatusName = "";
        //指商品类，资产类，费用类
        if("1".equals(authStatus)) {
            authStatusName = "商品类";
        } else  if("2".equals(authStatus)) {
            authStatusName = "费用类";
        }else  if("6".equals(authStatus)) {
            authStatusName = "租赁类";
        }
        else if("7".equals(authStatus)) {
            //直接认证并且是店转店则和商品税码一致
                String orgType=certificationQueryDao.selectOrgType(xfTaxNo,xfName);
                if(StringUtils.isNotEmpty(orgType)){
                    if(orgType.equals("5")){
                        authStatusName = "商品类";
                    }else{
                        authStatusName = "直接认证类";
                    }
                }
        }
        return authStatusName;
    }
    private String formatAuthStatus(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "已勾选";
        } else  if("2".equals(authStatus)) {
            authStatusName = "已确认";
        } else  if("3".equals(authStatus)) {
            authStatusName = "已发送认证";
        } else  if("4".equals(authStatus)) {
            authStatusName = "认证成功";
        } else if("5".equals(authStatus)) {
            authStatusName = "认证失败";
        }
        return authStatusName;
    }
    private String invResult(String code){
        String value=null;
        if("01".equals(code)){
            value="专票";
        }else if("04".equals(code)){
            value="普票";
        }else if("11".equals(code)){
            value="卷票";
        }else{
            value=null;
        }
        return value;
    }
}
