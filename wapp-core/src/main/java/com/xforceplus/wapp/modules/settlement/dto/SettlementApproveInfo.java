package com.xforceplus.wapp.modules.settlement.dto;

import com.xforceplus.wapp.repository.entity.InvoiceAudit;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : hujintao
 * @version : 1.0
 * @description : 结算单审核信息
 * @date : 2022/10/13 10:38
 **/
@Data
@NoArgsConstructor
public class SettlementApproveInfo {

    @ApiModelProperty("结算单id")
    private Long settlementId;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("审核类型 1-开票限额修改 2-金额有误 3-蓝冲")
    private Integer approveType;

    @ApiModelProperty("撤销原因")
    private String revertRemark;

    @ApiModelProperty("提交审核时间")
    private Long approveRequestTime;

    @ApiModelProperty("结算单下红字信息表状态")
    private List<Integer> redNotificationStatusList;

    @ApiModelProperty("需要撤销的红字信息表数量")
    private Long needRollbackNum;

    @ApiModelProperty("蓝冲审核的红票信息")
    private List<InvoiceAudit> invoiceAuditList;
}
