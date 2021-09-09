package com.xforceplus.wapp.modules.einvoice.util;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Date 4/23/2018.
 *
 * @author marvin.zhong
 */
public class RarUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(RarUtil.class);

    /**
     * 根据原始rar文件路径，解压到指定文件夹下.
     *
     * @param srcRarPath       原始rar文件路径
     * @param dstDirectoryPath 解压到的文件夹
     */
    public static void unRarFile(String srcRarPath, String dstDirectoryPath) {
        Archive a = null;
        try {
            a = new Archive(new File(srcRarPath));
            if (a != null) {
                FileHeader fh = a.nextFileHeader();
                while (fh != null) {
                    // 防止文件名中文乱码问题的处理
                    String fileName = fh.getFileNameW().isEmpty() ? fh.getFileNameString() : fh.getFileNameW();
                    // 文件夹
                    if (fh.isDirectory()) {
                        File fol = new File(dstDirectoryPath + File.separator + fileName);
                        fol.mkdirs();
                    } else { // 文件
                        File out = new File(dstDirectoryPath + File.separator + fileName.trim());
                        if (!out.exists()) {
                            // 相对路径可能多级，可能需要创建父目录.
                            if (!out.getParentFile().exists()) {
                                out.getParentFile().mkdirs();
                            }
                            out.createNewFile();
                        }
                        FileOutputStream os = new FileOutputStream(out);
                        a.extractFile(fh, os);
                        os.close();
                    }
                    fh = a.nextFileHeader();
                }
                a.close();
            }
        } catch (Exception e) {
            LOGGER.debug("------解压rar文件出错-----" + e);
        }
    }

    public static void main(String[] args) {
        unRarFile("D://testRar/test01.rar", "D://testRar/");
    }
}
