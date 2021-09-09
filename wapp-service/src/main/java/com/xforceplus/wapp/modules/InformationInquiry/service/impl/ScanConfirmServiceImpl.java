package com.xforceplus.wapp.modules.InformationInquiry.service.impl;

import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.dao.ScanConfirmDao;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.importTemplate.ScanConfirmImportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanConfirmService;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.SpthpExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.SctdhExcelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

@Service
public class ScanConfirmServiceImpl implements ScanConfirmService {

    @Autowired
    private ScanConfirmDao scanConfirmDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(Map<String, Object> map) {
        return scanConfirmDao.queryList(map);
    }

    @Override
    public int queryCount(Map<String, Object> map) {
        return scanConfirmDao.queryCount(map);
    }

    @Override
    public List<SelectionOptionEntity> getJV(String taxNo) {
        return scanConfirmDao.getJV(taxNo);
    }

    @Override
    public List<SelectionOptionEntity> getVender() {
        return scanConfirmDao.getVender();
    }

    @Override
    @Transactional
    public boolean submit(ConfirmInvoiceEntity entity) {
        entity.setCompanyCode(scanConfirmDao.getCompanyCode(entity.getJvcode()));
        int res1 = scanConfirmDao.updateRecordInvoice(entity);
        int res2 = scanConfirmDao.updateInvoice(entity);
        return (res1>0 && res2 >0);
    }

    @Override
    public R submitBatch(MultipartFile file, Long userId) {
        //进入解析excel方法
        final ScanConfirmImportExcel importExcel= new ScanConfirmImportExcel(file);
        int totalCount = 0;
        int okCount = 0;
        int errorCount = 0;
        List<String> errorList = newArrayList();
        StringBuffer sb = new StringBuffer();
        try {
            //读取excel
            final List<ConfirmInvoiceEntity> list = importExcel.analysisExcel();
            for(ConfirmInvoiceEntity entity : list){
                if(entity.getConfirmReason().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写旧发票.";
                    errorList.add(error);
                    continue;
                }
                if(entity.getJvcode().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写JVCODE.";
                    errorList.add(error);
                    continue;
                }
                if(entity.getVenderid().isEmpty()){
                    String error = "序号"+entity.getId()+": 未填写供应商号.";
                    errorList.add(error);
                    continue;
                }
                //校验jvcode
                if(scanConfirmDao.jvOk(entity.getJvcode(), entity.getGfTaxNo())==0){
                    String error = "序号"+entity.getId()+": JV填写错误或与发票抬头不一致.";
                    errorList.add(error);
                    continue;
                }
                //校验供应商
                if(scanConfirmDao.venderOk(entity.getVenderid())==0){
                    String error = "序号"+entity.getId()+": 供应商不存在或被冻结.";
                    errorList.add(error);
                    continue;
                }
                entity.setConfirmUserId(userId);
                //保存
                if(submit(entity)){
                    okCount++;
                }else{
                    String error = "序号"+entity.getId()+": 确认失败,请检查发票信息是否被篡改.";
                    errorList.add(error);
                    continue;
                }
            }
            totalCount = list.size();
            errorCount = errorList.size();
            sb.append("共计"+totalCount+"条数据,其中确认成功"+okCount+"条,失败"+errorCount+"条.");
            if(errorCount>0){
                sb.append("原因如下:");
                for(String e : errorList){
                    sb.append("<br/>"+e);
                }
            }
        } catch (Exception e) {
            return R.error("导入过程出错,请检查填写内容!");
        }
        return R.ok(sb.toString());
    }
    @Override
    public List<SpthpExcelEntity> transformExcle(List<ComprehensiveInvoiceQueryEntity> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        List<SpthpExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号","发票代码","发票号码","开票日期","购方税号","购方名称","销方税号","销方名称","金额","税额","价税合计","旧发票号","jvcode","供应商号","抵扣税率(%)","抵扣税额"};
        reflect(list2,s);
        for (int i = 0; i < list.size(); i++) {
            SpthpExcelEntity entity2=new SpthpExcelEntity();
            ComprehensiveInvoiceQueryEntity entity=list.get(i);
            //序号
            entity2.setCell0( i+1+"");
            //发票代码
            entity2.setCell1(entity.getInvoiceCode());
            //发票号码
            entity2.setCell2(entity.getInvoiceNo());
            //开票日期
            entity2.setCell3(formatDateString(entity.getInvoiceDate()));
            //购方税号
            entity2.setCell4(entity.getGfTaxNo());
            //购方名称
            entity2.setCell5(entity.getGfName());
            //销方税号
            entity2.setCell6(entity.getXfTaxNo());
            //销方名称
            entity2.setCell7(entity.getXfName());
            //金额
            entity2.setCell8(CommonUtil.formatMoney(entity.getInvoiceAmount()));
            //税额
            entity2.setCell9(CommonUtil.formatMoney(entity.getTaxAmount()));
            //价税合计
            entity2.setCell10(CommonUtil.formatMoney(entity.getTotalAmount()));
            entity2.setCell11(entity.getConfirmReason());

            entity2.setCell12(entity.getJvCode());

            entity2.setCell13(entity.getVenderId());
            entity2.setCell14(CommonUtil.formatMoney(entity.getDeductibleTaxRate()));
            //签收日期
            entity2.setCell15(CommonUtil.formatMoney(entity.getDeductibleTax()));

            list2.add(entity2);
        }


        return list2;
    }
    private void reflect(List<SpthpExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpthpExcelEntity e=new SpthpExcelEntity();
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
    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
}
