package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 中间表数据修复
 * @date : 2022/10/19 14:57
 **/
@Data
@NoArgsConstructor
public class DeductPreInvoicePushRequest {

    private List<String> settlementNoList;

}
