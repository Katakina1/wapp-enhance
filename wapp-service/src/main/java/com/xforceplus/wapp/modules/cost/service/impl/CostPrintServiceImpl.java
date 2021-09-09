package com.xforceplus.wapp.modules.cost.service.impl;

import com.xforceplus.wapp.common.utils.PdfUtils;
import com.xforceplus.wapp.modules.cost.dao.CostPrintDao;
import com.xforceplus.wapp.modules.cost.service.CostPrintService;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.lowagie.text.DocumentException;
import freemarker.template.TemplateException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.getResource;

/**
 * Created by 1 on 2018/11/9 11:29
 */
@Service
public class CostPrintServiceImpl implements CostPrintService {
	private static Logger log = Logger.getLogger(CostPrintServiceImpl.class);
    @Autowired
    private CostPrintDao costPrintDao;
    @Value("${filePathConstan.erweimaPath}")
    private  String erPath;

    @Override
    public void costProviderExport(Map<String, Object> map, HttpServletResponse response,String costNo) {
        String fileName = "costProviderPDF"+String.valueOf(new Date().getTime())+".pdf";

        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");

        try {
            String html = PdfUtils.getPdfContent("costProviderPDF.ftl", map);
            OutputStream out = null;
            ITextRenderer render = null;
            out = response.getOutputStream();


            render = PdfUtils.getRender();
            render.setDocumentFromString(html);

            render.getSharedContext().setBaseURL("file:"+erPath+costNo+"QRCode.png");
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
    public List<OptionEntity> queryXL(String ss) {
        return costPrintDao.queryXL(ss);
    }
}
