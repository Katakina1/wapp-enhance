package com.xforceplus.wapp.modules.backfill.model;

import lombok.Data;

import java.util.Objects;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-19 16:30
 **/
@Data
public class FileUploadResult {
    public static final String OK="200";
//    {"code":"200","data":{"uploadId":"4832796d-b662-43eb-be2f-38175f9a8341","uploadPath":"/usr/local/wapp/tempImage"}}
    private String code;
    private Result data;

    @Data
    public static class Result{
        private String uploadId;
        private String uploadPath;
    }

    public boolean isOk(){
        return Objects.equals(this.code,OK);
    }
}
