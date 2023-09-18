package com.xforceplus.wapp.modules.rednotification.util;

import com.xforceplus.wapp.common.utils.Base64;
import com.xforceplus.wapp.modules.backfill.service.VerificationService;
import com.xforceplus.wapp.modules.rednotification.model.ZipContentInfo;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Slf4j
@Service
public class DownloadUrlUtils {

    @Autowired
    private VerificationService verificationService;

    public void commonZipFiles(List<ZipContentInfo> srcFiles, String zipFilePath) {

        File zipFile = FileUtils.getFile(zipFilePath);
        if (!zipFile.exists()) {
            zipFile.getParentFile().mkdirs();
        }

        ZipOutputStream out;
        try {
            out = new ZipOutputStream(new FileOutputStream(zipFilePath));
            BufferedOutputStream bo = new BufferedOutputStream(out);

            for (ZipContentInfo zipInfo : srcFiles) {
                try {
                    if (zipInfo.isFile()) {
                        File file = FileUtils.getFile(zipInfo.getSourceUrl());
                        if (file.exists()) {
                            zip(out, file, file.getName(), bo);
                        }
                    } else {
                        zip(out, zipInfo.getSourceUrl(), zipInfo.getRelativePath(), bo);
                    }

                } catch (Exception e) {
                    //ignore
                }

            }
            bo.close();
        } catch (IOException e) {
            //ignore
        }

    }


    private void zip(ZipOutputStream out, File srcFile, String base, BufferedOutputStream bo) throws Exception {
        if (srcFile.isDirectory()) {
            File[] fileList = srcFile.listFiles();
            if (fileList.length == 0) {
                out.putNextEntry(new ZipEntry(base + "/"));
                out.closeEntry();
            }
            for (int i = 0; i < fileList.length; i++) {
                zip(out, fileList[i], base + "/" + fileList[i].getName(), bo);
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            @Cleanup FileInputStream in = new FileInputStream(srcFile);
            IOUtils.copy(in, out);
//            byte[] buffer = new byte[1024];
//            int len = 0;
//            while ((len = in.read(buffer)) != -1) {
//
//                out.write(buffer, 0, len);
//            }
//            in.close();
        }
    }

    private void zip(ZipOutputStream out, String urlStr, String relativePath, BufferedOutputStream bo) throws Exception {
//        URL url = new URL(urlStr);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setConnectTimeout(3 * 1000);
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        out.putNextEntry(new ZipEntry(relativePath));
//        InputStream inputStream = conn.getInputStream();
        String base64 = verificationService.getBase64ByRealUrl(java.util.Base64.getEncoder().encodeToString(urlStr.getBytes()));
        byte[] buffer = Base64.decode(base64);
        bo.write(buffer);
        bo.flush();
    }


    public static String putFile(String filePath) {
        File localFile = null;
        try {
            localFile = FileUtils.getFile(filePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            String key = "上传路径TODO";
            FileUtils.deleteQuietly(localFile);
            return key;
        } catch (Exception e) {
            log.error("上传文件到oss发生异常", e);
            return null;
        } finally {
            if (localFile != null) {
                localFile.delete();
            }
        }
    }
}
