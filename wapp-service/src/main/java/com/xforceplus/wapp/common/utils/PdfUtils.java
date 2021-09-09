package com.xforceplus.wapp.common.utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.Locale;

public class PdfUtils {
    public static ITextRenderer getRender() throws DocumentException, IOException {

        ITextRenderer render = new ITextRenderer();

        //添加字体，以支持中文
        render.getFontResolver().addFont("/export/font/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        render.getFontResolver().addFont("/export/font/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

        return render;
    }

    //获取要写入PDF的内容
    public static String getPdfContent(String ftlName, Object o) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
        return useTemplate(ftlName, o);
    }

    //使用freemarker得到html内容
    public static String useTemplate(String ftlName, Object o) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {

        String html = null;

        Template tpl = getFreemarkerConfig().getTemplate(ftlName);
        tpl.setEncoding("UTF-8");

        StringWriter writer = new StringWriter();
        tpl.process(o, writer);
        writer.flush();
        html = writer.toString();
        return html;
    }

    /**
     * 获取Freemarker配置
     * @return
     * @throws IOException
     */
    private static Configuration getFreemarkerConfig() throws IOException {
        freemarker.template.Version version = new freemarker.template.Version("2.3.22");
        Configuration config = new Configuration(version);
        config.setClassForTemplateLoading(PdfUtils.class,"/export/report");
        config.setEncoding(Locale.CHINA, "utf-8");
        return config;
    }


    public static void generateToFile(String ftlName,String imageDiskPath,Object data,String outputFile) throws Exception {
        String html = getPdfContent( ftlName, data);
        OutputStream out = null;
        ITextRenderer render = null;
        out = new FileOutputStream(outputFile);
        render = getRender();
        render.setDocumentFromString(html);
        if (imageDiskPath != null && !imageDiskPath.equals("")) {
            //html中如果有图片，图片的路径则使用这里设置的路径的相对路径，这个是作为根路径
            render.getSharedContext().setBaseURL("file:/" + imageDiskPath);
        }
        render.layout();
        render.createPDF(out);
        render.finishPDF();
        render = null;
        out.close();

    }
}
