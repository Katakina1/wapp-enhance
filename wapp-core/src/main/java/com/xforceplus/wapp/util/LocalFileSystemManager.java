package com.xforceplus.wapp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

/**
 * @program: wapp-enhance
 * @description: manage local file system
 * @author: Kenny Wong
 * @create: 2021-10-12 16:41
 **/
public class LocalFileSystemManager {

    private LocalFileSystemManager() {
    }

    /**
     * 获取本地文件名列表
     *
     * @param path             文件路径
     * @param fileNameKeyWords 文件名关键字
     * @throws FileNotFoundException
     */
    public static List<String> getFileNames(String path, String fileNameKeyWords) throws FileNotFoundException {
        File directory = FileUtils.getFile(path);
        // 文件夹是否存在
        if (!directory.exists()) {
            // 创建文件夹
            if (!directory.mkdir()) {
                throw new FileNotFoundException();
            }

            // 可写
            if (!directory.canWrite() && !directory.setWritable(true)) {
                throw new FileNotFoundException();
            }
        }
        String[] fileNames = directory.list((dir, name) -> name.contains(fileNameKeyWords));
        return Optional.ofNullable(fileNames).map(Arrays::asList).orElseGet(Collections::emptyList);
    }

    /**
     * 如果目录不存在，那么创建它
     *
     * @param directory 文件夹路径
     * @throws FileNotFoundException
     */
    public static void createFolderIfNonExist(String directory) throws FileNotFoundException {
        File localFolder = FileUtils.getFile(directory);
        // 文件夹是否存在
        if (!localFolder.exists()) {
            // 创建文件夹
            if (!localFolder.mkdirs()) {
                throw new FileNotFoundException();
            }
            // 可写
            if (!localFolder.canWrite() && !localFolder.setWritable(true)) {
                throw new FileNotFoundException();
            }
        }
    }

    /**
     * 文件是否存在
     *
     * @param path
     * @param fileName
     * @return
     */
    public static boolean isFileExists(String path, String fileName) {
        File file = FileUtils.getFile(path, fileName);
        return file.exists();
    }

    /**
     * 删除文件
     *
     * @param path
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String path, String fileName) {
        File file = FileUtils.getFile(path, fileName);
        return file.delete();
    }
}
