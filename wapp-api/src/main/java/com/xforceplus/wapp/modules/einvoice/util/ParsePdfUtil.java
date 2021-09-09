package com.xforceplus.wapp.modules.einvoice.util;

import com.ele.parse.entity.FPEntity;
import com.ele.parse.utils.FPUtils;
import com.ele.parse.utils.InvokeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 解析pdf文件
 *
 * @author yzq
 * @version 1.0
 */
public class ParsePdfUtil {
    /**
     * 解析多张pdf文件
     *
     * @param file
     * @return List<FPEntity>
     * @throws Exception
     */
    public static List<FPEntity> parsePdf(List<File> file) throws Exception {
        String ss = "";
        FPEntity fp = null;
        List<FPEntity> list = new ArrayList<>();
        FPUtils fpUtiles = new FPUtils();
        for (File f : file) {
            FileInputStream in = new FileInputStream(f);
            byte[] b = new byte[(int) f.length()];
            int length = in.read(b);
            in.close();
            ss = Base64.encode(b);
            fp = fpUtiles.setFPAttri(ss, null, true, false, false);
            InvokeUtil.trimParse(fp);
            list.add(fp);
        }
        return list;
    }

    /**
     * 解析一张pdf文件
     *
     * @param file
     * @return List<FPEntity>
     * @throws Exception
     */
    public static FPEntity parseOnePdf(File file) throws Exception {
        String ss = "";
        FPEntity fp = null;
        FPUtils fpUtiles = new FPUtils();
        FileInputStream in = new FileInputStream(file);
        byte[] b = new byte[(int) file.length()];
        int length = in.read(b);
        in.close();
        ss = Base64.encode(b);
        fp = fpUtiles.setFPAttri(ss, null, true, false, false);
        InvokeUtil.trimParse(fp);
        return fp;
    }

    /**
     * 扫描多个文件
     *
     * @param path
     * @author yzq
     */
    public static List<File> findFile(String path) {
        File file = new File(path);
        File[] list = file.listFiles();
        List<File> name = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith("pdf")) {
                name.add(list[i]);
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
            if (list[i].isFile() && list[i].getName().endsWith("pdf")) {
                list[i].delete();
            }
            if (list[i].isFile() && list[i].getName().endsWith("zip")) {
                list[i].delete();
            }
        }
    }

    public static void deleteImgFile(String path) {
        File file = new File(path);
        File[] list = file.listFiles();
        List<File> name = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isFile() && list[i].getName().endsWith("png")) {
                list[i].delete();
            }
            if (list[i].isFile() && list[i].getName().endsWith("jpg")) {
                list[i].delete();
            }
            if (list[i].isFile() && list[i].getName().endsWith("zip")) {
                list[i].delete();
            }
        }
    }

    public static String getBase64ByFile(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        byte[] fileByte = new byte[(int) file.length()];
        int length = in.read(fileByte);
        in.close();
        return com.xforceplus.wapp.common.utils.Base64.encode(fileByte);
    }

    public static void main(String[] args) throws Exception {
        FPEntity fp = null;
        String ss = "d:/1/8.pdf";
        //ZipUtil.unzip("d:/1/pdf.zip");
        //List<File> file = findFile(ss);
        File f = new File(ss);
        long time = System.currentTimeMillis();
        fp = parseOnePdf(f);
        long times = System.currentTimeMillis();
        System.err.println(times - time);
        //deleteFile(ss);
        //System.err.println(list.size());
        System.err.println(fp);
        Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(fp.getKprq());
        System.out.println(date);
        System.out.println(fp.getBuyer_nsrsbh());
    }


}
