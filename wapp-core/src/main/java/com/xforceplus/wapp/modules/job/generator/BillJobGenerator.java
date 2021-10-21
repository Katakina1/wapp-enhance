package com.xforceplus.wapp.modules.job.generator;

import java.util.List;

/**
 * @program: wapp-enhance
 * @description: create bill job according to the CSV files located in remote SFTP server
 * @author: Kenny Wong
 * @create: 2021-10-12 15:26
 **/
public interface BillJobGenerator {

    /**
     * 生成单据任务的主方法
     */
    void generate();

    /**
     * 扫描单据的文件列表
     *
     * @param remotePath
     * @return
     */
    List<String> scanFiles(String remotePath);

    /**
     * 创建单据任务
     *
     * @param jobType
     * @param fileNames
     */
    void createJob(int jobType, List<String> fileNames);

}
