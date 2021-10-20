package com.xforceplus.wapp.common.utils;

import java.util.Date;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 14:27
 **/
public class ExcelExportUtil {
    /**
     * 文件名后缀
     */
    public static final String FILE_NAME_SUFFIX = ".xlsx";
    /**
     * 文件名中间分隔符
     */
    public static final String FILE_SPLIT = "_";


    /**
     * 获取excel文件名
     * @param userid 用户ID
     * @param fileNamePrefix 文件名前缀
     * @return
     */
    public static String getExcelFileName(Long userid, String fileNamePrefix) {

        // 设置EXCEL名称
        //导出文件名
        StringBuilder ftpFileName = new StringBuilder();
        ftpFileName.append(userid);
        ftpFileName.append(FILE_SPLIT);
        ftpFileName.append(fileNamePrefix);
        ftpFileName.append(FILE_SPLIT);
        ftpFileName.append(DateUtils.getStringDateShort());
        ftpFileName.append(FILE_SPLIT);
        ftpFileName.append((new Date()).getTime());
        ftpFileName.append(FILE_NAME_SUFFIX);
        return ftpFileName.toString();
    }
}
