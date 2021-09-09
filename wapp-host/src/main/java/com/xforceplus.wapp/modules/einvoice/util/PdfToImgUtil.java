package com.xforceplus.wapp.modules.einvoice.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ele.parse.entity.FPEntity;

/**
 * pdf转图片工具类
 *
 * @author lenovo
 */
public class PdfToImgUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(PdfToImgUtil.class);

    /**
     * pdf转图片
     *
     * @param imgPath
     * @throws Exception
     */
    public static Map<String, Object> getImg(File file, String imgPath) {
        Map<String, Object> map = new HashMap<>();
        File imgFile = null;
        FPEntity fp = null;
        InputStream in = null;
        PDDocument document = new PDDocument();
        try {
            in = new FileInputStream(file);
            document = PDDocument.load(file);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            String path = "";
            int count = document.getNumberOfPages();
            for (int i = 0; i < count; i++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, 296);
                path = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "_" + UUID.randomUUID().toString().replace("-", "") + ".bmp";
                ImageIO.write(image, "bmp", new File(imgPath + path));
                if (count != 1) {
                    break;
                }
            }
            in.close();
            document.close();
            ImgCompressUtil img = new ImgCompressUtil(imgPath + path);
            img.resize(2840, 1656, imgPath + path);
            imgFile = new File(imgPath + path);
            File zip = ZipUtil.zip(imgFile, Boolean.TRUE);
            fp = ParsePdfUtil.parseOnePdf(new File(imgPath + file.getName()));
            if ("".equals(fp.getBuyer_nsrsbh()) || fp.getBuyer_nsrsbh() == null) {
                fp.setBuyer_nsrsbh("123456789012345");
            }
            imgFile.delete();
            file.delete();
            map.put("zip", zip);
            map.put("gfTaxNo", fp.getBuyer_nsrsbh());
            map.put("invoiceNo", fp.getFphm());
            map.put("invoiceCode", fp.getFpdm());
            map.put("jym", fp.getJym());
            map.put("fp", fp);
        } catch (Exception e) {
            LOGGER.error("解析失败" + e);
            map.put("fileName", file.getName());
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    LOGGER.error("关闭失败" + e);
                }
            }
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e1) {
                    LOGGER.error("关闭失败" + e);
                }
            }

            file.delete();
        }
        return map;
    }

    /**
     * 扫描多个文件
     *
     * @param path
     * @author yzq
     */
    public static List<File> findFile(String path, String type) {
        File file = new File(path);
        File[] list = file.listFiles();
        List<File> name = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith(type)) {
                name.add(list[i]);
            }
        }
        return name;
    }

    /**
     * 扫描多个文件
     *
     * @param path
     * @author sxl
     */
    public static List<File> findFiles(String path, String type) {
        File file = new File(path);
        File[] list = file.listFiles();
        List<File> name = new ArrayList<>();

        File[] lists = null;
        for (int i = 0; i < list.length; i++) {
            lists = list[i].listFiles();
        }
        for (int i = 0; i < lists.length; i++) {
            if (lists[i].isFile() && lists[i].getName().endsWith(type)) {
                name.add(lists[i]);
            }
        }
        return name;
    }

    /**
     * 删除多个文件
     *
     * @param path
     * @author yzq
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        File[] list = file.listFiles();
        List<File> name = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith("bmp")) {
                list[i].delete();
            }
        }
    }

    /**
     * 复制方法zip
     *
     * @param file 源文件
     * @param path 备份文件夹路径
     * @throws Exception
     * @author lenovo
     */
    public static void move(File file, String path) throws Exception {
        String ss = file.getName();
        File files = new File(path + ss);
        FileUtils.moveFile(file, files);
        if (files.exists()) {
            file.delete();
        }
    }


    public static void outImg() throws Exception {
        String ss = "d:/zip/";
        String s = "d:/bmp/";
        List<File> findFile = findFile(ss, "pdf");
        for (File find : findFile) {
            Map<String, Object> map = getImg(find, ss);
            Set set = map.keySet();
            int i = 0;
            if (i == set.hashCode()) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("d:/zip/4.pdf");
        FPEntity fp = ParsePdfUtil.parseOnePdf(file);
        System.out.println(fp);
    }
}
