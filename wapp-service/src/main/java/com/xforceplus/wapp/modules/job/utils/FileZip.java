package com.xforceplus.wapp.modules.job.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author http://javaflex.iteye.com/
 *
 */
public class FileZip {
	
	private static Logger logger = LoggerFactory.getLogger(FileZip.class);
	/**
	 * 
	 * @param srcfile 文件名数组
	 * @param zipfile 压缩后文件
	 */
	public static void zipFiles(java.io.File[] srcfile, java.io.File zipfile) {
		byte[] buf = new byte[1024];
		try {
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
			for (int i = 0; i < srcfile.length; i++) {
				FileInputStream in = new FileInputStream(srcfile[i]);
				out.putNextEntry(new ZipEntry(srcfile[i].getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
		} catch (IOException e) {
			logger.debug("压缩文件异常,异常信息为"+e);
		}
	}
	/**
	 * 
	 * @param srcfile 文件名
	 * @param zipfile 压缩后文件
	 */
	public static void zipFiles(java.io.File srcfile, java.io.File zipfile) {
		
		byte[] buf = new byte[1024];
		try {
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
				FileInputStream in = new FileInputStream(srcfile);
				out.putNextEntry(new ZipEntry(srcfile.getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			    out.close();
			} catch (IOException e) {
				logger.debug("压缩文件异常,异常信息为"+e);
			}
	}
}
