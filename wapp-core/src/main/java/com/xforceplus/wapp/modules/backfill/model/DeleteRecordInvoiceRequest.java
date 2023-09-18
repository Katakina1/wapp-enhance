package com.xforceplus.wapp.modules.backfill.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * Created by SunShiyong on 2021/10/30.
 */
@ApiModel("删除发票请求")
@Data
public class DeleteRecordInvoiceRequest {

    private List<Long> ids;
}
