package com.xforceplus.wapp.modules.redTicket.service.impl;

import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.cost.dao.CostPrintDao;
import com.xforceplus.wapp.modules.redTicket.dao.PrintCoverDao;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.redTicket.service.PrintCoverService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
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
 * Created by 1 on 2018/11/14 9:30
 */
@Service
public class PrintCoverServiceImpl implements PrintCoverService {

    private final PrintCoverDao printCoverDao;
    @Autowired
    private CostPrintDao costPrintDao;
    @Autowired
    public PrintCoverServiceImpl(PrintCoverDao printCoverDao) {
        this.printCoverDao = printCoverDao;
    }

    @Override
    public Integer selectRedTicketListCount(Map<String,Object> map) {
        return printCoverDao.selectRedTicketListCount(map);
    }

    @Override
    public List<RedTicketMatch> selectRedTicketList(Map<String,Object> map) {
        List<RedTicketMatch> redTicketMatches = printCoverDao.selectRedTicketList(map);

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
    public RedTicketMatch getRedTicketMatch(long id) {
        RedTicketMatch redTicketMatch = printCoverDao.getRedTicketMatch(id);
        if(redTicketMatch.getBusinessType().equals("1")){
            redTicketMatch.setBusinessType("退货类型");
        }else if (redTicketMatch.getBusinessType().equals("2")){
            redTicketMatch.setBusinessType("协议类型");
        }else if(redTicketMatch.getBusinessType().equals("3")){
            redTicketMatch.setBusinessType("折让类型");
        }
       /* String taxRate = redTicketMatch.getTaxRate().toString();
        List<OptionEntity> optionEntities = costPrintDao.queryXL("VATRATE");
        for (int i=0;i<optionEntities.size();i++){
            if(taxRate.equals(optionEntities.get(i).getValue())){
                redTicketMatch.setTaxRateOne(optionEntities.get(i).getLabel());
            }
        }*/


        return redTicketMatch;
    }

    @Override
    public void exportRedTicketPdf(Map<String, Object> map, HttpServletResponse response) {
        String fileName = "redTicketPDF"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        try {
            String html = PdfUtils.getPdfContent("redTicketPDF.ftl", map);
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
    public UserEntity getUserName(String userCode) {
        return printCoverDao.getUserName(userCode);
    }
}
