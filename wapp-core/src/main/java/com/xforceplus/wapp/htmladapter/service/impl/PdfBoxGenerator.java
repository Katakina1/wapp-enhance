package com.xforceplus.wapp.htmladapter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.xforceplus.wapp.htmladapter.dto.XmlToPdf;
import com.xforceplus.wapp.htmladapter.factory.HtmlToPdfRenderBuilder;
import com.xforceplus.wapp.htmladapter.factory.HtmlToPdfRendererFactory;
import com.xforceplus.wapp.htmladapter.service.PdfGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * pdf模板生成服务
 *
 *
 */

@Slf4j
@Service
public class PdfBoxGenerator implements PdfGenerator {
    @Autowired
    private TemplateEngine templateEngine;
    private static final String TEMPLATE_PATH = "element_qd";

    @Override
    public byte[] generatePdfFileByHtmlAndData(XmlToPdf jsonObject) {
        try {
           return generatePdfFileByHtmlStr(jsonObject);
        } catch (Exception e) {
            log.error("PdfFileByHtmlException:【{}】",e.getMessage(), e);
        }
        return null;
    }

    @PostConstruct
    public void init() {
        HtmlToPdfRendererFactory.init();
    }


    public byte[] generatePdfFileByHtmlStr(XmlToPdf data) throws Exception {
        return generatePdfStreamByHtmlStr(htmlStrDataFilling(data)).toByteArray();
    }

    public String htmlStrDataFilling( XmlToPdf data) {
        Context context = new Context();
        context.setVariables(JSON.parseObject(JSON.toJSONString(data), new TypeReference<Map<String, Object>>(){}));
        return templateEngine.process(TEMPLATE_PATH, context);
    }



    private ByteArrayOutputStream generatePdfStreamByHtmlStr(String htmlContent)
            throws Exception {

        Document htmlDoc = Jsoup.parse(htmlContent);

        HtmlToPdfRenderBuilder openhttptopdfRenderBuilder = null;
        PdfRendererBuilder pdfRendererBuilder = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            openhttptopdfRenderBuilder = HtmlToPdfRendererFactory.getPdfRendererBuilderInstance();

            pdfRendererBuilder = openhttptopdfRenderBuilder.getPdfRendererBuilder();

            try {
                //避免使用Jsoup转换字符串进行直接转换w3c,document,因为不是严格的xml格式，转换存在问题
                //PDFCreationListener pdfCreationListener = new XHtmlMetaToPdfInfoAdapter(w3cDoc);
                //使用Jsoup也可以规范相关html
                W3CDom w3cDom = new W3CDom();
                //这里的doc对象指的是jsoup里的Document对象
                org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(htmlDoc);

                pdfRendererBuilder.withW3cDocument(w3cDoc,"/");

                pdfRendererBuilder.toStream(out);

                pdfRendererBuilder.run();

            } catch (Exception e) {
                log.error(htmlDoc.toString());
                throw e;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            HtmlToPdfRendererFactory.returnPdfBoxRenderer(openhttptopdfRenderBuilder);
        }

        PDDocument pdDoc = PDDocument.load(out.toByteArray());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pdDoc.save(baos);
        pdDoc.close();

        return baos;
    }
}
