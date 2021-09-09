package com.xforceplus.wapp.modules.scanRefund.service.impl;



import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.dao.EnterPackageNumberDao;
import com.xforceplus.wapp.modules.scanRefund.dao.PrintRefundInformationDao;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberExcelEntity;
import com.xforceplus.wapp.modules.scanRefund.service.CostPrintRefundInformationService;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CostPrintRefundInformationServiceImpl implements CostPrintRefundInformationService {

    @Autowired
    private PrintRefundInformationDao printRefundInformationDao;

    @Override
    public List<EnterPackageNumberEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return printRefundInformationDao.queryCostList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel,Map<String, Object> map) {
        if(map.get("xfName")!=null){
            map.put("xfName", map.get("xfName").toString().replace(" ",""));
        }
        return printRefundInformationDao.queryTotalCostResult(schemaLabel,map);
    }

//    @Override
//    public EnterPackageNumberEntity queryRefundListAll(Long id) {
//        EnterPackageNumberEntity enterPackageNumberEntity = new EnterPackageNumberEntity();
//        enterPackageNumberEntity.setInvoiceEntityList(printRefundInformationDao.queryRefundList(id));
//        return enterPackageNumberEntity;
//    }
    @Override
    public EnterPackageNumberEntity queryRefundList(Long id) {
        return printRefundInformationDao.queryRefundCostList(id);
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

        return printRefundInformationDao.queryListCostAll(map);
    }

    @Override
    public List<EnterPackageNumberExcelEntity> queryListForExcel(String schemaLabel, Map<String, Object> map) {
        List<EnterPackageNumberEntity> list =  printRefundInformationDao.queryCostList(schemaLabel,map);
        List<EnterPackageNumberExcelEntity> excelList = new LinkedList();
        EnterPackageNumberExcelEntity excel = null;
        int index  = 1;
        for(EnterPackageNumberEntity entity:list){
             excel = new EnterPackageNumberExcelEntity();
             excel.setVenderId(entity.getVenderId());
             excel.setRebateNo(entity.getRebateNo());
             excel.setEpsNo(entity.getEpsNo());
            if(entity.getInvoiceAmount()!=null) {
                excel.setInvoiceAmount(entity.getInvoiceAmount().toString().substring(0,entity.getInvoiceAmount().length()-2));
            }
             excel.setInvoiceCode(entity.getInvoiceCode());
            if(entity.getInvoiceDate()!=null&entity.getInvoiceDate()!=""){
                excel.setInvoiceDate(entity.getInvoiceDate().substring(0,10));
            }
             excel.setInvoiceNo(entity.getInvoiceNo());
             excel.setRebateDate(entity.getRebateDate());
             excel.setRownumber(""+index++);
            if(entity.getInvoiceAmount()!=null) {
                excel.setTaxAmount(entity.getTaxAmount().toString().substring(0,entity.getTaxAmount().length()-2));
            }
             excel.setShopNo(entity.getShopNo());
             excel.setApplicantDepartment(entity.getApplicantDepartment());
            excel.setApplicantNo(entity.getApplicantNo());
            excel.setApplicantName(entity.getApplicantName());
            excel.setApplicantCall(entity.getApplicantCall());
            excel.setApplicantSubarea(entity.getApplicantSubarea());
            excel.setImportDate(entity.getImportDate());


             excelList.add(excel);
        }
        return excelList;
    }
}
