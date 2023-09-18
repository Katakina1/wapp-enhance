package com.xforceplus.wapp.modules.backfill.model;

import lombok.Data;

/**
 * 上传文件服务器，返回结果
 * @author zhaochao@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-9-22 14:37:38
 **/
@Data
public class UploadFileResultData {
	/**
	 * 上传成功返回的附件id,用于下载
	 */
    private String uploadId;
    /**
     * 上传文件路径
     */
    private String uploadPath;
    /**
     * 创建时间
     */
    private String createTime;
    
    private Float size ;
    
    private Integer enabled;
}
