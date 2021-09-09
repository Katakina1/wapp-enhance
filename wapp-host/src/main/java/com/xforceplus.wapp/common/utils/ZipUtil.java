package com.xforceplus.wapp.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;


public class ZipUtil {
	
	private static int k = 1; // 定义递归次数变量  
	
	
	/** 
     * @param args 
     */  
    public static void main(String[] args) {  
        // TODO Auto-generated method stub  
    	ZipUtil book = new ZipUtil();  
        try {  
            zip("D:\\test\\1.zip",
                    new File("D:\\a\\"));  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        
        /*try {  
            readZipFile("C:\\home\\dxhy\\image\\91441900351266820T\\2017\\20170406_110016213007047854.zip");  
        } catch (Exception e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  */
  
    }  
  
    public static void zip(String zipFileName, File inputFile) throws Exception {  
        System.out.println("压缩中...");  
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(  
                zipFileName));  
        BufferedOutputStream bo = new BufferedOutputStream(out);  
        zip(out, inputFile, inputFile.getName(), bo);  
        bo.close();  
        out.close(); // 输出流关闭  
        System.out.println("压缩完成");  
    }  
  
    private static void zip(ZipOutputStream out, File f, String base,  
            BufferedOutputStream bo) throws Exception { // 方法重载  
        if (f.isDirectory()) {  
            File[] fl = f.listFiles();  
            if (fl.length == 0) {  
                out.putNextEntry(new ZipEntry(base + "/")); // 创建zip压缩进入点base  
                System.out.println(base + "/");  
            }  
            for (int i = 0; i < fl.length; i++) {  
                zip(out, fl[i], base + "/" + fl[i].getName(), bo); // 递归遍历子文件夹  
            }  
            System.out.println("第" + k + "次递归");  
            k++;  
        } else {  
            out.putNextEntry(new ZipEntry(base)); // 创建zip压缩进入点base  
            System.out.println(base);  
            FileInputStream in = new FileInputStream(f);  
            BufferedInputStream bi = new BufferedInputStream(in);  
            int b;  
            while ((b = bi.read()) != -1) {  
                bo.write(b); // 将字节流写入当前zip目录  
            }  
            bi.close();  
            in.close(); // 输入流关闭  
        }  
    }  
    
    /**
     * 解压缩
     * @param outPath 解压后文件路径
     * @throws IOException 
     */
    public static void jyZip(String inputPathZipName,String outPath) throws IOException{
  	  long startTime=System.currentTimeMillis();  
            ZipInputStream Zin=new ZipInputStream(new FileInputStream(  
            		inputPathZipName));//输入源zip路径  
            BufferedInputStream Bin=new BufferedInputStream(Zin);  
            File Fout=null;  
            ZipEntry entry;  
            while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){  
                Fout=new File(outPath,entry.getName());  
                if(!Fout.exists()){  
                    (new File(Fout.getParent())).mkdirs();  
                }  
                FileOutputStream out=new FileOutputStream(Fout);  
                BufferedOutputStream Bout=new BufferedOutputStream(out);  
                int b;  
                while((b=Bin.read())!=-1){  
                    Bout.write(b);  
                }  
                Bout.close();  
                out.close();  
                System.out.println(Fout+"解压成功");      
            }  
            Bin.close();  
            Zin.close();  
        long endTime=System.currentTimeMillis();  
        System.out.println("耗费时间： "+(endTime-startTime)+" ms");
    }
    
    /**
     * 读取压缩文件
     * @param file 文件路径+文件名
     * @return 解压后的二进制输出流
     * @throws Exception
     */
    public static byte[] readZipFile(String file) throws Exception {  
    	ZipInputStream Zin=new ZipInputStream(new FileInputStream(file));//输入源zip路径  
    	BufferedInputStream Bin=new BufferedInputStream(Zin);
    	 ByteArrayOutputStream bos = null;
    	 byte[] byteArray = null;
         ZipEntry entry;  
             while((entry = Zin.getNextEntry())!=null && !entry.isDirectory()){  
                 bos = new ByteArrayOutputStream();
                 int b;  
                 while((b=Bin.read())!=-1){  
                     bos.write(b);
                     bos.close();  
                 } 
             }  
             Bin.close();  
             Zin.close();
             byteArray = bos.toByteArray();
			return byteArray;  
    }  
    
    public static byte[] readInputStream(InputStream inputStream) throws Exception{
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();
    }

}
