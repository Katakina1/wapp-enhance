package com.xforceplus.wapp.modules.einvoice.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * 图片压缩处理
 * @author yzq
 */
public class ImgCompressUtil {
	private Image img;
	private int width;
	private int height;
	
	public ImgCompressUtil(String fileName) throws IOException {
		File file = new File(fileName);    // 读入文件
		img = ImageIO.read(file);    // 构造Image对象
		width = img.getWidth(null);    // 得到源图宽
		height = img.getHeight(null);  // 得到源图长
	}
	/**
	 * 按照宽度还是高度进行压缩
	 * @param w int 最大宽度
	 * @param h int 最大高度
	 */
	public void resizeFix(int w, int h,String path) throws IOException {
		if (width / height > w / h) {
			resizeByWidth(w,path);
		} else {
			resizeByHeight(h,path);
		}
	}
	/**
	 * 以宽度为基准，等比例放缩图片
	 * @param w int 新宽度
	 */
	public void resizeByWidth(int w,String path) throws IOException {
		int h = height * w / width;
		resize(w, h,path);
	}
	/**
	 * 以高度为基准，等比例缩放图片
	 * @param h int 新高度
	 */
	public void resizeByHeight(int h,String path) throws IOException {
		int w = width * h / height;
		resize(w, h,path);
	}
	/**
	 * 强制压缩/放大图片到固定的大小
	 * @param w int 新宽度
	 * @param h int 新高度
	 */
	public void resize(int w, int h,String path) throws IOException {
		// SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
		BufferedImage image = new BufferedImage(w, h,BufferedImage.TYPE_INT_RGB ); 
		image.getGraphics().drawImage(img, 0, 0, w, h, null); // 绘制缩小后的图
		File destFile = new File(path);
		FileOutputStream out = new FileOutputStream(destFile); // 输出到文件流
		// 可以正常实现bmp、png、gif转jpg
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(image); // JPEG编码
		out.close();
	}

	/**
	 * 判断图片宽高比是否大于1
	 * @return true 大于1 false 小于1
	 */
	public Boolean aspectRatio() {
		final BigDecimal bigWidth = new BigDecimal(width);
		final BigDecimal bigHeight = new BigDecimal(height);
		final Double result = bigWidth.divide(bigHeight,2,BigDecimal.ROUND_HALF_UP).doubleValue();
		return result > 1;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		String path="d:/zip/20170628_43bbb2caf1f64525992ea3574a73fbe8.bmp";
		ImgCompressUtil imgCom = new ImgCompressUtil("d:/zip/20170628_43bbb2caf1f64525992ea3574a73fbe8.bmp");
		imgCom.resizeFix(2840, 1656,path);
	}
}