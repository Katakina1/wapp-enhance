package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.redTicket.dao.PrintingOpenRedTicketDataDao;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.PrintingOpenRedTicketDataService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/1 18:36
 */
@Service
public class PrintingOpenRedTicketDataServiceImpl implements PrintingOpenRedTicketDataService {
    private final PrintingOpenRedTicketDataDao printingOpenRedTicketDataDao;
    @Autowired
    public PrintingOpenRedTicketDataServiceImpl(PrintingOpenRedTicketDataDao printingOpenRedTicketDataDao) {
        this.printingOpenRedTicketDataDao = printingOpenRedTicketDataDao;
    }


    @Override
    public Integer getRedTicketMatchListCount( Map<String, Object> map) {
        return printingOpenRedTicketDataDao.getRedTicketMatchListCount(map);
    }

    @Override
    public List<RedTicketMatch> queryOpenRedTicket(Map<String, Object> map) {

        List<RedTicketMatch> redTicketMatches = printingOpenRedTicketDataDao.queryOpenRedTicket(map);

        for (int i = 0; i < redTicketMatches.size(); i++){
            if(redTicketMatches.get(i).getBusinessType().equals("2")){
                BigDecimal taxRate =(redTicketMatches.get(i).getTaxRate()).divide(new BigDecimal(100));
                taxRate = taxRate.add(new BigDecimal(1));
                redTicketMatches.get(i).setRedTotalAmount(redTicketMatches.get(i).getRedTotalAmount().multiply(taxRate));
            }
        }
        return redTicketMatches;
    }

    @Override
    public List<InvoiceDetail> getList(String params) {


        return printingOpenRedTicketDataDao.getList(params);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String params) {

        return printingOpenRedTicketDataDao.queryTotalResult(params);
    }

    @Override
    public void exportPdf(Map<String, Object> map, HttpServletResponse response) {
        String fileName = "";
        String dataSerialNumber=(String)map.get("redTicketDataSerialNumber");
        String userCode=(String)map.get("userCode");
        if(((String)map.get("businessType")).equals("1")){
            fileName = "returnGoodPDF-"+userCode+"-"+dataSerialNumber+".pdf";
        }else if (((String) map.get("businessType")).equals("2")){
            fileName = "agreementPDF-"+userCode+"-"+dataSerialNumber+".pdf";
        }else if (((String)map.get("businessType")).equals("3")){
            fileName = "concessionPDF-"+userCode+"-"+dataSerialNumber+".pdf";
        }



        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        String html= null;
        try {
            if(((String)map.get("businessType")).equals("1")){
                html = PdfUtils.getPdfContent("returnGoodPDF.ftl", map);
            }else if (((String) map.get("businessType")).equals("2")){
                html = PdfUtils.getPdfContent("agreementPDF.ftl", map);
            }else if (((String)map.get("businessType")).equals("3")){
                html = PdfUtils.getPdfContent("concessionPDF.ftl", map);
            }

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
    public InvoiceEntity getPdfDate(String redTicketDataSerialNumber) {
        InvoiceEntity invoiceEntity = printingOpenRedTicketDataDao.getPdfDate(redTicketDataSerialNumber);
        if(invoiceEntity.getPdfDateStart().equals(invoiceEntity.getPdfDateEnd())){
            invoiceEntity.setPdfDateStart(invoiceEntity.getPdfDateStart().substring(0,8)+"01");
            switch(invoiceEntity.getPdfDateStart().substring(5,7)){
                case "01":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "03":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "05":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "07":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "08":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "10":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "12":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"31");
                    break;
                case "02":
                    Integer year = new Integer(invoiceEntity.getPdfDateStart().substring(0,4));
                    if (year % 4 == 0 && year % 100 != 0) {
                        invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"29");
                    } else if (year % 400 == 0) {
                        invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"29");
                    } else {
                        invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0, 8) + "28");
                    }
                    break;
                case "06":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"30");
                    break;
                case "04":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"30");
                    break;
                case "09":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"30");
                    break;
                case "11":
                    invoiceEntity.setPdfDateEnd(invoiceEntity.getPdfDateStart().substring(0,8)+"30");
                    break;
            }

        }

        return  invoiceEntity;
    }

}
