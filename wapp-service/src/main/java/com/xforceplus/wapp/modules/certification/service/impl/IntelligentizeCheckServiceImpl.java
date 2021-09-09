package com.xforceplus.wapp.modules.certification.service.impl;


import com.xforceplus.wapp.modules.certification.dao.IntelligentizeCheckDao;
import com.xforceplus.wapp.modules.certification.dao.ManualCertificationDao;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.certification.service.IntelligentizeCheckService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 智能勾选
 * @author kevin.wang
 * @date 4/19/2018
 */
@Service
public class IntelligentizeCheckServiceImpl implements IntelligentizeCheckService {

    private IntelligentizeCheckDao intelligentizeCheckDao;
    @Value("${currentTaxPeriod}")
    private String taxPeriod; // 税款所属期判定日
    @Autowired
    private ManualCertificationDao manualCertificationDao;
    @Autowired
    public IntelligentizeCheckServiceImpl(IntelligentizeCheckDao intelligentizeCheckDao) {
        this.intelligentizeCheckDao = intelligentizeCheckDao;
    }


    /**
     * 智能勾选--返回要操作的数据，金额，税额，发票数量
     */
    @Override
    public InvoiceCertificationEntity selectQueryList(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        InvoiceCertificationEntity  entity=new InvoiceCertificationEntity();
        List<InvoiceCertificationEntity> entitys=intelligentizeCheckDao.queryList(schemaLabel,map);
        if(null==entitys){
            return new  InvoiceCertificationEntity();
        }
        Integer count =0;
        BigDecimal taxAmount= new BigDecimal(0);
        BigDecimal invoiceAmount= new BigDecimal(0);
        List<Long> ids = new ArrayList();
        BigDecimal floatTax;
        BigDecimal maxTax;
        if(map.get("maxTax")!=null && !map.get("maxTax").toString().isEmpty() ){
            maxTax= new BigDecimal((String) map.get("maxTax"));
            if(map.get("floatTax")!=null && !map.get("floatTax").toString().isEmpty() ){
                floatTax= new BigDecimal((String) map.get("floatTax"));
                for(int i=1;i<=entitys.size();i++) {
                    taxAmount = taxAmount.add(entitys.get(i - 1).getTaxAmount());
                    invoiceAmount = invoiceAmount.add(entitys.get(i - 1).getInvoiceAmount());
                    if (taxAmount.compareTo(maxTax.add(floatTax)) > 0) {
                        taxAmount = taxAmount.subtract(entitys.get(i - 1).getTaxAmount());

                        invoiceAmount = invoiceAmount.subtract(entitys.get(i - 1).getInvoiceAmount());
                    } else {
                        count++;
                        ids.add(entitys.get(i - 1).getId());
                        save(entity, ids, taxAmount, invoiceAmount, count);
                    }
                }
            }else {
                for(int i=1;i<=entitys.size();i++) {
                    taxAmount=taxAmount.add(entitys.get(i-1).getTaxAmount());
                    invoiceAmount=invoiceAmount.add(entitys.get(i-1).getInvoiceAmount());
                    if (taxAmount.compareTo(maxTax) > 0) {
                        taxAmount = taxAmount.subtract(entitys.get(i - 1).getTaxAmount());
                        invoiceAmount = invoiceAmount.subtract(entitys.get(i - 1).getInvoiceAmount());
                    } else {
                        count++;
                        ids.add(entitys.get(i - 1).getId());
                        save(entity, ids, taxAmount, invoiceAmount, count);
                    }
                }
            }
        }else {

            for(int i=1;i<=entitys.size();i++){
                taxAmount=taxAmount.add(entitys.get(i-1).getTaxAmount());
                invoiceAmount=invoiceAmount.add(entitys.get(i-1).getInvoiceAmount());
                ids.add(entitys.get(i-1).getId());
                count++;
                save(entity,ids,taxAmount,invoiceAmount,count);
            }

        }

        return entity;
    }

    private   InvoiceCertificationEntity save(InvoiceCertificationEntity entity,List<Long> ids,
                                              BigDecimal taxAmount,BigDecimal invoiceAmount,Integer count){
        entity.setTaxAmount(taxAmount);
        //金额合计
        entity.setInvoiceAmount(invoiceAmount);
        //发票数量
        entity.setCount(count);
        //所选中数据的id
        entity.setIds(ids);
        return   entity;
    }

    /**
     * 智能勾选--具体要操作的数据
     *
     */
    @Override
    public List<InvoiceCertificationEntity> queryList(String schemaLabel,Map<String, Object> map,String loginName,String userName) {

        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        List<InvoiceCertificationEntity> entitys=new ArrayList<InvoiceCertificationEntity>();

        String checkIds= (String) map.get("ids");
        String rzhBelongDate="";
        if (checkIds.split(",").length > 0) {
            final String[] ids = checkIds.split(",");
            for(String id:ids){
                InvoiceCertificationEntity entity=intelligentizeCheckDao.selectCheck(schemaLabel,id);
                    entitys.add(entity);
                rzhBelongDate=getCurrentTaxPeriod(schemaLabel,id);
                if(null==rzhBelongDate){
                    rzhBelongDate="";
                }
                    Boolean flag=intelligentizeCheckDao.intelligentizeCheck(schemaLabel,entity.getId(),loginName,userName,rzhBelongDate)>0;
                    if(flag){
                        entity.setFlag("认证成功");
                    }else {
                        entity.setFlag("认证失败");
                    }

            }
        }
        return entitys;
    }

    @Override
    public int queryTotal(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return intelligentizeCheckDao.queryTotal(schemaLabel,map);
    }

    @Override
    public String selectSwitchStatus() {
        return intelligentizeCheckDao.selectSwitchStatus();
    }

    public String getCurrentTaxPeriod(String schemaLabel,String id){
        String currentTaxPeriod=manualCertificationDao.getCurrentTaxPeriod(schemaLabel,id);
        SimpleDateFormat format =  new SimpleDateFormat("yyyyMM");


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int nowDay = calendar.get(Calendar.DAY_OF_MONTH);

        if(StringUtils.isBlank(currentTaxPeriod)){
            //日期配置文件
            if(nowDay <= Integer.parseInt(taxPeriod)){
                calendar.add(Calendar.MONTH, -1);
                currentTaxPeriod = format.format(calendar.getTime());
            }else{
                currentTaxPeriod = format.format(calendar.getTime());
            }
        }
        return currentTaxPeriod;
    }
}
