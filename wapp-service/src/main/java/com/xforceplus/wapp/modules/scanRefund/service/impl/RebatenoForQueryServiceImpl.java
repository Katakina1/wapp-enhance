package com.xforceplus.wapp.modules.scanRefund.service.impl;

import com.xforceplus.wapp.modules.scanRefund.dao.RebatenoForQueryDao;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.RebatenoForQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class RebatenoForQueryServiceImpl implements RebatenoForQueryService {

    @Autowired
    private RebatenoForQueryDao rebatenoForQueryDao;

    @Override
    public List<RebatenoForQueryEntity> queryList(Map<String, Object> map) {
        return rebatenoForQueryDao.queryList(map);
    }
    @Override
    public Integer invoiceMatchCount(Map<String, Object> map){
        return rebatenoForQueryDao.invoiceMatchCount(map);
    }
    @Override
    public List<RebatenoForQueryExcelEntity> queryListAll(Map<String, Object> map) {
        List<RebatenoForQueryEntity>  list =  rebatenoForQueryDao.queryList(map);
        List<RebatenoForQueryExcelEntity> excelList = new LinkedList();
        RebatenoForQueryExcelEntity excel = null;
        int page=(int)map.get("page");
        int limit=(int)map.get("limit");
        int index = (limit*(page-1))+1;
        for(RebatenoForQueryEntity entity : list){
            excel = new RebatenoForQueryExcelEntity();
            excel.setCompanyCode(entity.getCompanyCode());
            excel.setCreateDate( formatDate(entity.getCreateDate()));
            excel.setEpsNo(entity.getEpsNo());
            excel.setFlowType(formateFlowType(entity.getFlowType()));
            excel.setGfName(entity.getGfName());
            try{
                excel.setInvoiceAmount(entity.getInvoiceAmount().toString().substring(0,entity.getInvoiceAmount().toString().length()-2));
            }catch (Exception e){

            }
            excel.setInvoiceCode(entity.getInvoiceCode());
            excel.setInvoiceNo(entity.getInvoiceNo());
            excel.setInvoiceType(entity.getInvoiceType());
            excel.setJvCode(entity.getJvCode());
            excel.setMailCompany(entity.getMailCompany());
            excel.setMailDate(entity.getMailDate());
            excel.setNotes(entity.getNotes());
            excel.setQsDate( formatDate(entity.getQsDate()));
            excel.setQsStatus(formateVenderType(entity.getQsStatus()));
            excel.setRebateDate( formatDate(entity.getRebateDate()));
            excel.setRebateExpressno(entity.getRebateExpressno());
            excel.setRebateNo(entity.getRebateNo());
            excel.setRownumber(""+index++);
            try{
                excel.setTaxAmount(entity.getTaxAmount().toString().substring(0,entity.getTaxAmount().toString().length()-2));
            }catch (Exception e){

            }
            excel.setVenderid(entity.getVenderid());
            excel.setXfName(entity.getXfName());
            excelList.add(excel);
        }
        return excelList;
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String qsStatus){
        String value="";
        if("0".equals(qsStatus)){
            value="签收失败";
        }else if("1".equals(qsStatus)){
            value="签收成功";
        }
        return value;
    }

    private String formateFlowType(String qsStatus){
        String value="";
        if("1".equals(qsStatus)){
            value="商品";
        }else if("2".equals(qsStatus)){
            value="费用";
        }else if("3".equals(qsStatus)){
            value="外部红票";
        }else if("4".equals(qsStatus)){
            value="内部红票";
        }else if("5".equals(qsStatus)){
            value="供应商红票";
        }else if("6".equals(qsStatus)){
            value="租赁";
        }else if("7".equals(qsStatus)){
            value="直接认证";
        }else if("8".equals(qsStatus)){
            value="Ariba";
        }
        return value;
    }
}
