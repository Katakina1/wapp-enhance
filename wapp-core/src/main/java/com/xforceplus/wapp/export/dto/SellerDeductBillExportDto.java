package com.xforceplus.wapp.export.dto;

import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.modules.claim.dto.DeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QuerySellerDeductListRequest;
import lombok.Data;

/**
 * Describe: 供应商业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Data
public class SellerDeductBillExportDto {

    private TXfDeductionBusinessTypeEnum type;
    private QuerySellerDeductListRequest request;
    private Long logId;
    private Long userId;
    private String loginName;
}
