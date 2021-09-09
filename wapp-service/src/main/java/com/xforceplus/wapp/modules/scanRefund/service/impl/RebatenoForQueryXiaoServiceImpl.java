package com.xforceplus.wapp.modules.scanRefund.service.impl;

import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.RebatenoForQueryXiaoDao;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryXiaoEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.RebatenoForQueryXiaoExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.RebatenoForQueryXiaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class RebatenoForQueryXiaoServiceImpl implements RebatenoForQueryXiaoService {

    @Autowired
    private RebatenoForQueryXiaoDao rebatenoForQueryXiaoDao;

    @Override
    public List<RebatenoForQueryXiaoEntity> queryList(Map<String, Object> map) {
        return rebatenoForQueryXiaoDao.queryList(map);
    }
    @Override
    public Integer invoiceMatchCount(Map<String, Object> map){
        return rebatenoForQueryXiaoDao.invoiceMatchCount(map);
    }
    @Override
    public List<RebatenoForQueryXiaoEntity> queryListAll(Map<String, Object> map) {

        return rebatenoForQueryXiaoDao.queryList(map);
    }

    @Override
    public List<RebatenoForQueryXiaoExcelEntity> transformExcle(List<RebatenoForQueryXiaoEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<RebatenoForQueryXiaoExcelEntity> excelList = new LinkedList();

        String[] s=new String[]{"序号","签收状态","签收日期","发票代码","发票号码","开票日期","供应商号","购方名称","销方名称","金额","税额","JVCODE","COMPANYCODE","描述","退单号","邮包号","邮寄公司","邮寄时间","业务类型","退单时间"};
        reflect(excelList,s);
        RebatenoForQueryXiaoExcelEntity excel = null;
        int index = 1;
        for(RebatenoForQueryXiaoEntity entity : list){
            excel = new RebatenoForQueryXiaoExcelEntity();
//序号
            excel.setCell0(entity.getRownumber());
            //签收状态
            excel.setCell1(formateVenderType(entity.getQsStatus()));
            //签收日期
            excel.setCell2(formatDate(entity.getQsDate()));
            //发票代码
            excel.setCell3(entity.getInvoiceCode());
            //发票号码
            excel.setCell4(entity.getInvoiceNo());
            //开票日期
            excel.setCell5(formatDate(entity.getCreateDate()));
            //供应商号
            excel.setCell6(entity.getVenderid());
            //购方名
            excel.setCell7(entity.getGfName());
            //销方名
            excel.setCell8(entity.getXfName());
            //金额
            excel.setCell9(entity.getInvoiceAmount().toString());
            //税额
            excel.setCell10(entity.getTaxAmount().toString());
            //JvCode
            excel.setCell11(entity.getJvCode());

            excel.setCell12(entity.getCompanyCode());

            excel.setCell13(entity.getNotes());

            excel.setCell14(entity.getRebateNo());

            excel.setCell15(entity.getRebateExpressno());

            excel.setCell16(entity.getMailCompany());

            excel.setCell17(entity.getMailDate());

            excel.setCell18(formateFlowType(entity.getFlowType()));

            excel.setCell19(entity.getRebateDate());
            excelList.add(excel);
            index++;
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
            value="签收失败";
        }else if("2".equals(qsStatus)){
            value="签收成功";
        }else if("3".equals(qsStatus)){
            value="签收成功";
        }else if("4".equals(qsStatus)){
            value="签收成功";
        }else if("5".equals(qsStatus)){
            value="签收成功";
        }else if("6".equals(qsStatus)){
            value="签收成功";
        }
        return value;
    }
    private void reflect(List<RebatenoForQueryXiaoExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RebatenoForQueryXiaoExcelEntity e=new RebatenoForQueryXiaoExcelEntity();
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
}
