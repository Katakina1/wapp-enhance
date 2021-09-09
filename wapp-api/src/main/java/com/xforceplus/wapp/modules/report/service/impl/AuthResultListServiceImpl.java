package com.xforceplus.wapp.modules.report.service.impl;

import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.modules.report.dao.AuthResultListDao;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.report.service.AuthResultListService;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AuthResultListServiceImpl implements AuthResultListService {

    @Autowired
    private AuthResultListDao authResultListDao;

    @Override
    public List<ComprehensiveInvoiceQueryEntity> queryList(String schemaLabel, Map<String, Object> map) {
        return authResultListDao.queryList(schemaLabel,map);
    }

    @Override
    public ReportStatisticsEntity queryTotalResult(String schemaLabel, Map<String, Object> map) {
        return authResultListDao.queryTotalResult(schemaLabel,map);
    }

    @Override
    public List<ComprehensiveInvoiceQueryEntity> getList(String schemaLabel,Map<String, Object> map) {
        return authResultListDao.getList(schemaLabel,map);
    }

    @Override
    public void exportPdf(Map<String, Object> map, HttpServletResponse response) throws IOException,DocumentException,TemplateException {

        String fileName = "authResultList"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        String html= PdfUtils.getPdfContent("authResultList.ftl", map);
        OutputStream out = null;
        ITextRenderer render = null;

        out = response.getOutputStream();


        render = PdfUtils.getRender();
        render.setDocumentFromString(html);
        render.layout();
        render.createPDF(out);
        render.finishPDF();
        render = null;

    }
}
