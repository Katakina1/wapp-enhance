package com.xforceplus.wapp.modules.job.generator;

import java.util.List;

/**
 * @program: wapp-enhance
 * @description: create bill job according to the CSV files located in remote SFTP server
 * @author: Kenny Wong
 * @create: 2021-10-12 15:26
 **/
public interface BillJobGenerator {

    void generate();

    List<String> scanFiles(String remotePath);

    void createJob(int jobType, List<String> fileNames);

}
