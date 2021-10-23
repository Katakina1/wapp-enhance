package com.xforceplus.wapp.modules.noneBusiness.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author malong@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-09-30 16:43
 **/
@Slf4j
public class ZipUtil {

    public static void zip(String zipFileName, File inputFile) throws Exception {
        System.out.println("压缩中...");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                zipFileName));
        BufferedOutputStream bo = new BufferedOutputStream(out);
        zip(out, inputFile, inputFile.getName(), bo);
        bo.close();
        out.close(); // 输出流关闭
    }

    private static void zip(ZipOutputStream zos, File f, String base,
                            BufferedOutputStream bo) throws Exception { // 方法重载
//        if (f.isDirectory()) {
//            File[] fl = f.listFiles();
//            if (fl.length == 0) {
//                zos.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base
//                System.out.println(base + "/");
//            }
//            for (int i = 0; i < fl.length; i++) {
//                zip(zos, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹
//            }
//        } else {
//            zos.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base
//            log.info("entry file:{}",base);
//
//            try (FileInputStream in = new FileInputStream(f);
//                    BufferedInputStream bi = new BufferedInputStream(in)) {
//                int b;
//                byte data[] = new byte[1024];
//                while ((b = bi.read(data)) != -1) {
//                    bo.write(data, 0, b); // 将字节流写入当前zip目录
//                }
//            }
//        }

        //判断压缩对象如果是一个文件夹
        if (f.isDirectory()) {
            if (base.endsWith("/")) {
                //如果文件夹是以“/”结尾，将文件夹作为压缩箱放入zipOut压缩输出流
                zos.putNextEntry(new ZipEntry(base));
                zos.closeEntry();
            } else {
                //如果文件夹不是以“/”结尾，将文件夹结尾加上“/”之后作为压缩箱放入zipOut压缩输出流
                zos.putNextEntry(new ZipEntry(base + "/"));
                zos.closeEntry();
            }
            //遍历文件夹子目录，进行递归的zipFile
            File[] children = f.listFiles();
            for (File childFile : children) {
                zip(zos,childFile, base + "/" + childFile.getName(), bo);
            }
            //如果当前递归对象是文件夹，加入ZipEntry之后就返回
            return;
        }
        //如果当前的fileToZip不是一个文件夹，是一个文件，将其以字节码形式压缩到压缩包里面
        try(FileInputStream fis = new FileInputStream(f);){
            ZipEntry zipEntry = new ZipEntry(base);
            zos.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }
        }
    }
}
