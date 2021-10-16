package com.xforceplus.wapp.modules.backFill.model;

import lombok.Data;

/**
 * 上传文件服务器，返回结果
 * @author zhaochao@xforceplus.com
 * @program wapp-web
 * @description
 * @create 2021-9-22 14:37:38
 **/
@Data
public class UploadFileResult {
	/**
	 * 200成功 其他失败
	 */
    private String code;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 文件详情
     */
    private UploadFileResultData data;
    
    
    
}
