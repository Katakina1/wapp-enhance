package com.xforceplus.wapp.common.dto.customs;

import lombok.Data;

@Data
public class CustomsLogRequest {
    //海关票Id
    private Long id;
    //海关缴款书Id
    private String customsId;
    //海关缴款书号码
    private String customsNo;
    //类型
    private String type;
    //申请时间
    private String checkTime;
    //操作用户ID
    private String userId;
    //操作用户名称
    private String userName;

    private Integer pageNo = 0;

    private Integer pageSize = 20;
}