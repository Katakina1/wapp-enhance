package com.xforceplus.wapp.modules.einvoice.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtilRead {

    private static int k = 1; // 定义递归次数变量


    public static void zip(String zipFileName, File inputFile) throws Exception {

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        BufferedOutputStream bo = new BufferedOutputStream(out);
        zip(out, inputFile, inputFile.getName(), bo);
        bo.close();
        out.close(); // 输出流关闭
    }

    private static void zip(ZipOutputStream out, File f, String base,
                            BufferedOutputStream bo) throws Exception {
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            if (fl.length == 0) {
                // 创建zip压缩进入点base
                out.putNextEntry(new ZipEntry(base + "/"));
            }
            for (int i = 0; i < fl.length; i++) {
                // 递归遍历子文件夹
                zip(out, fl[i], base + "/" + fl[i].getName(), bo);
            }
            k++;
        } else {
            // 创建zip压缩进入点base
            out.putNextEntry(new ZipEntry(base));

            FileInputStream in = new FileInputStream(f);
            BufferedInputStream bi = new BufferedInputStream(in);
            int b;
            while ((b = bi.read()) != -1) {
                // 将字节流写入当前zip目录
                bo.write(b);
            }
            // 关闭流
            bi.close();
            in.close();
        }
    }

    /**
     * 解压缩
     *
     * @param outPath 解压后文件路径
     * @throws IOException
     */
    public static void jyZip(String inputPathZipName, String outPath) throws IOException {
        ZipInputStream Zin = new ZipInputStream(new FileInputStream(
                inputPathZipName));
        BufferedInputStream Bin = new BufferedInputStream(Zin);
        File Fout = null;
        ZipEntry entry;
        while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
            Fout = new File(outPath, entry.getName());
            if (!Fout.exists()) {
                (new File(Fout.getParent())).mkdirs();
            }
            FileOutputStream out = new FileOutputStream(Fout);
            BufferedOutputStream Bout = new BufferedOutputStream(out);
            int b;
            while ((b = Bin.read()) != -1) {
                Bout.write(b);
            }
            Bout.close();
            out.close();

        }
        Bin.close();
        Zin.close();
    }

    /**
     * 读取压缩文件
     *
     * @param file 文件路径+文件名
     * @return 解压后的二进制输出流
     * @throws Exception
     */
    public static byte[] readZipFile(String file) throws Exception {
        //输入源zip路径
        ZipInputStream Zin = new ZipInputStream(new FileInputStream(file));
        BufferedInputStream Bin = new BufferedInputStream(Zin);
        ByteArrayOutputStream bos = null;
        byte[] byteArray = null;
        ZipEntry entry;
        while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
            bos = new ByteArrayOutputStream();
            int b;
            while ((b = Bin.read()) != -1) {
                bos.write(b);
                bos.close();
            }
        }
        Bin.close();
        Zin.close();
        byteArray = bos.toByteArray();
        return byteArray;
    }

    public static byte[] readInputStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();
    }

}
