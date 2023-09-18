package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import lombok.Data;

/**
 * Describe: 业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/10/11
 */
@Data
public class BillExportDto<T> {

    private TXfDeductionBusinessTypeEnum type;
    private T request;
    private Long logId;
    private Long userId;
    private String loginName;

    /* 用户操作记录读取用户导出读取缓存key,后续运维使用*/
    private String waitKey;
}
