package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.EnterPackageNumberDao;
import com.xforceplus.wapp.modules.scanRefund.dao.PrintRefundInformationDao;
import com.xforceplus.wapp.modules.scanRefund.entity.DytdfmExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.SctdhExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.EnterPackageNumberService;
import com.xforceplus.wapp.modules.scanRefund.service.PrintRefundInformationService;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PrintRefundInformationServiceImpl implements PrintRefundInformationService {

    @Autowired
    private PrintRefundInformationDao printRefundInformationDao;

    @Override
    public List<EnterPackageNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return printRefundInformationDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return printRefundInformationDao.queryTotalResult(schemaLabel,map);
    }

//    @Override
//    public EnterPackageNumberEntity queryRefundListAll(Long id) {
//        EnterPackageNumberEntity enterPackageNumberEntity = new EnterPackageNumberEntity();
//        enterPackageNumberEntity.setInvoiceEntityList(printRefundInformationDao.queryRefundList(id));
//        return enterPackageNumberEntity;
//    }
    @Override
    public EnterPackageNumberEntity queryRefundList(Long id) {
        return printRefundInformationDao.queryRefundList(id);
    }

    @Override
    public EnterPackageNumberEntity queryPostType(Long id) {
        return printRefundInformationDao.queryPostType(id);
    }
    @Override
    public void exportPoPdf(Map<String, Object> map, HttpServletResponse response) {

        String fileName = "returnMessage"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        try {
            String html = PdfUtils.getPdfContent("returnMessage.ftl", map);
            OutputStream out = null;
            ITextRenderer render = null;
            out = response.getOutputStream();

            render = PdfUtils.getRender();
            render.setDocumentFromString(html);
            render.layout();
            render.createPDF(out);
            render.finishPDF();
            render = null;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<EnterPackageNumberEntity> queryListAll(Map<String, Object> map) {

        return printRefundInformationDao.queryListAll(map);
    }

    @Override
    public List<DytdfmExcelEntity> transformExcle(List<EnterPackageNumberEntity> list)throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        List<DytdfmExcelEntity> list2=new ArrayList<>();
        String[] s=new String[]{"序号","退单时间","退单号","供应商号","发票代码","发票号码","开票日期","金额","税额","退票原因","业务类型"};
        reflect(list2,s);
        for (int i = 0; i < list.size(); i++) {
            DytdfmExcelEntity entity2=new DytdfmExcelEntity();
            EnterPackageNumberEntity entity=list.get(i);
            //序号
            entity2.setCell0( i+1+"");
            //退单时间
            entity2.setCell1(entity.getRebateDate());
            //退单号
            entity2.setCell2(entity.getRebateNo());
            //供应商号
            entity2.setCell3(entity.getVenderId());
            //发票代码
            entity2.setCell4(entity.getInvoiceCode());
            //发票号码
            entity2.setCell5(entity.getInvoiceNo());
            //开票日期
            entity2.setCell6(entity.getInvoiceDate().substring(0,10));
            //金额
            if(entity.getInvoiceAmount()!=null) {
                entity2.setCell7(entity.getInvoiceAmount().toString().substring(0,entity.getInvoiceAmount().length()-2));
            }else {
                entity2.setCell7("");
            }
            //税额
            entity2.setCell8(entity.getTaxAmount().toString().substring(0,entity.getTaxAmount().length()-2));
            //eps单号
            entity2.setCell9(entity.getEpsNo());

            list2.add(entity2);
        }


        return list2;
    }
    private void reflect(List<DytdfmExcelEntity> list2, String[] s) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DytdfmExcelEntity e=new DytdfmExcelEntity();
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

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }
}
