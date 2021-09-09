package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.GenerateRefundNumberDao;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.SctdhExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.GenerateRefundNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GenerateRefundNumberServiceImpl implements GenerateRefundNumberService {

    @Autowired
    private GenerateRefundNumberDao generateRefundNumberDao;

    @Override
    public List<GenerateRefundNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return generateRefundNumberDao.queryList(schemaLabel,map);
    }

    @Override
    public void rebatenobyId(String schemaLabel, Long id,String rebateNo) {
        generateRefundNumberDao.rebatenobyId(schemaLabel, id, rebateNo);
    }

    @Override
    public GenerateRefundNumberEntity queryrebateno(Long id) {
        return generateRefundNumberDao.queryrebateno(id);
    }

    @Override
    public void rebatenobyuuid(String uuid) {
        generateRefundNumberDao.rebatenobyuuid(uuid);
    }

    @Override
    public GenerateRefundNumberEntity queryuuid1(Long id) {
        return generateRefundNumberDao.queryuuid1(id);
    }


    @Override
    public GenerateRefundNumberEntity querymaxrebateno() {
        return generateRefundNumberDao.querymaxrebateno();
    }


    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return generateRefundNumberDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<GenerateRefundNumberEntity> queryListAll(String schemaLabel, Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return generateRefundNumberDao.queryListAll(schemaLabel,map);
    }
    @Override
    public List<SctdhExcelEntity> transformExcle(List<GenerateRefundNumberEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<SctdhExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号","扫描日期","扫描流水号","供应商号","发票代码","发票号码","开票日期","金额","税额","退票原因","业务类型"};
        reflect(list2,s);
        for (int i = 0; i < list.size(); i++) {
            SctdhExcelEntity entity2=new SctdhExcelEntity();
            GenerateRefundNumberEntity entity=list.get(i);
            //序号
            entity2.setCell0( i+1+"");
            //扫描日期
            entity2.setCell1( formatDate(entity.getCreateDate()));
            //审扫描流水号
            entity2.setCell2( entity.getScanId());
            //供应商号
            entity2.setCell3( entity.getVenderId());
            //发票代码
            entity2.setCell4( entity.getInvoiceCode());
            //发票号码
            entity2.setCell5( entity.getInvoiceNo());
            //开票日期
            entity2.setCell6( formatDate(entity.getInvoiceDate()));
            //金额
            if(entity.getInvoiceAmount()==null){
                entity.setInvoiceAmount(new BigDecimal("0.00"));
            }
            entity2.setCell7( formatAmount(entity.getInvoiceAmount().toString()));
            //税额
            if(entity.getTaxAmount()==null){
                entity.setTaxAmount(new BigDecimal("0.00"));
            }
            entity2.setCell8( formatAmount(entity.getTaxAmount().toString()));
            //退票原因
            entity2.setCell9( entity.getRefundReason());
            //业务类型
            entity2.setCell10( flowType(entity.getFlowType()));

            list2.add(entity2);
        }


        return list2;
    }
    private void reflect(List<SctdhExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SctdhExcelEntity e=new SctdhExcelEntity();
        Class cls = e.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            String name = fields[i].getName(); // 获取属性的名字
            name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
            String type = fields[i].getGenericType().toString(); // 获取属性的类型
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                Method m = e.getClass().getMethod("get" + name);
                String value = (String) m.invoke(e); // 调用getter方法获取属性值
                if (value == null) {
                    m = e.getClass().getMethod("set"+name,String.class);
                    m.invoke(e, s[i]);
                }
            }
        }
        list2.add(e);
    }
    private String formatDate(String source) {
        return source == null ? "" : source.substring(0, 10);
    }
    private String formatAmount(String amount) {
        return amount == null ? "" : amount.substring(0, amount.length()-2);
    }
    private String errCode(String getisdel,String code){
        String value="";
        if("2".equals(getisdel)){
            value="";
        }else{
            value=code;
        }
        return value;
    }
    private String flowType(String type){
        String value="";
        if("1".equals(type)){
            value="商品";
        }else if("2".equals(type)){
            value="费用";
        }
        else if("3".equals(type)){
            value="外部红票";
        }else if("4".equals(type)){
            value="内部红票";
        }else if("5".equals(type)){
            value="供应商红票";
        }else if("6".equals(type)){
            value="租赁";
        }else if("7".equals(type)){
            value="直接认证";
        }else if("8".equals(type)){
            value="Ariba";
        }else{
            value="";
        }
        return value;
    }

}
