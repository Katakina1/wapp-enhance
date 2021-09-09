package com.xforceplus.wapp.modules.einvoice.util;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @version 1.0
 * @Title ZipUtil.java
 * @Description zip工具类
 * @date 2017-6-26 下午2:17:19
 */
public class ZipUtil {
    /**
     * @param isDelete 是否删除原文件 true: 删除  false: 不删除
     * @return File
     * @Description 压缩方法
     * @date 2017-6-26 下午2:17:54
     */
    public static File zip(File source, Boolean isDelete) {
        File target = null;
        //File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            String zipName = source.getName().substring(0, source.getName().indexOf(".")) + ".zip";
            target = new File(source.getParent(), zipName);
            if(isDelete){
                if (target.exists()) {
                    target.delete(); // 删除旧的文件
                }
            }

            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }




    public static byte[] zip(byte[] imgbyte)   {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        //添加到zip
        //一定要加目录
        try {
        zip.putNextEntry(new ZipEntry( File.separator + UUID.randomUUID().toString()+".txt"));
                IOUtils.write(imgbyte, zip);

            zip.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }


        IOUtils.closeQuietly(zip);
        byte[] data = outputStream.toByteArray();
        return data;
    }
    /**
     * @param source  原文件
     * @param zipName 压缩后的文件名
     * @return File
     * @Description 压缩方法
     * @date 2017-6-26 下午2:17:54
     */
    public static File zip(File source, String zipName) {
        File target = null;
        //File source = new File(filePath);
        if (source.exists()) {
            // 压缩文件名=源文件名.zip
            target = new File(source.getParent(), zipName);
            if (target.exists()) {
                target.delete(); // 删除旧的文件
            }
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            try {
                fos = new FileOutputStream(target);
                zos = new ZipOutputStream(new BufferedOutputStream(fos));
                // 添加对应的文件Entry
                addEntry("/", source, zos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                IOUtil.closeQuietly(zos, fos);
            }
        }
        return target;
    }

    /**
     * @param base
     * @param source
     * @param zos
     * @Description 扫描添加文件Entry
     * @date 2017-6-26 下午2:18:37
     */
    private static void addEntry(String base, File source, ZipOutputStream zos) throws IOException {
        // 按目录分级
        String entry = base + source.getName();
        if (source.isDirectory()) {
            for (File file : source.listFiles()) {
                // 递归列出目录下的所有文件，添加文件Entry
                addEntry(entry + "/", file, zos);
            }
        } else {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                byte[] buffer = new byte[1024 * 10];
                fis = new FileInputStream(source);
                bis = new BufferedInputStream(fis, buffer.length);
                int read = 0;
                zos.putNextEntry(new ZipEntry(entry));
                while ((read = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, read);
                }
                zos.closeEntry();
            } finally {
                IOUtil.closeQuietly(bis, fis);
            }
        }
    }

    /**
     * @param filePath
     * @throws Exception
     * @Description 解压方法
     * @date 2017-6-26 下午2:19:22
     */
    public static void unzip(String filePath) throws Exception {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            zis = new ZipInputStream(new FileInputStream(source), Charset.forName("gbk"));
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                File target = new File(source.getParent(), entry.getName());
                if (!target.getParentFile().exists()) {
                    // 创建文件父目录
                    target.getParentFile().mkdirs();
                }
                // 写入文件
                bos = new BufferedOutputStream(new FileOutputStream(target));
                int read = 0;
                byte[] buffer = new byte[1024 * 10];
                while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, read);
                }
                bos.flush();
            }
            zis.closeEntry();
            IOUtil.closeQuietly(zis, bos);
        }
    }

    /**
     * @param filePath
     * @throws Exception
     * @Description 修改后的解压方法
     * @date 2017-6-26 下午2:19:22
     */
    public static void unzipNew(String filePath) throws Exception {
        File source = new File(filePath);
        if (source.exists()) {
            ZipInputStream zis = null;
            BufferedOutputStream bos = null;
            zis = new ZipInputStream(new FileInputStream(source));
            ZipEntry entry = zis.getNextEntry();////////////////////////////////////////////////////
            while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
                File target = new File(source.getParent(), entry.getName());
                if (!target.getParentFile().exists()) {
                    // 创建文件父目录
                    target.getParentFile().mkdirs();
                }
                // 写入文件
                bos = new BufferedOutputStream(new FileOutputStream(target));
                int read = 0;
                byte[] buffer = new byte[1024 * 10];
                while ((read = zis.read(buffer, 0, buffer.length)) != -1) {
                    bos.write(buffer, 0, read);
                }
                bos.flush();
            }
            zis.closeEntry();
            IOUtil.closeQuietly(zis, bos);
        }
    }

//    public static void main(String[] args) {
//        try {
//            ZipUtil.unzip("d://zip/1112.zip");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
