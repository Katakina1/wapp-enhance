package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.util.UUID;


/**
 * Describe: 历史业务单开票状态同步
 *
 * @Author xiezhongyong
 * @Date 2022/10/18
 */
@ApiModel(description = "历史业务单开票状态同步请求对象")
@Data
public class SyncHistoryBillMakeInvoiceStatusRequest {

    public static void main(String[] args) throws Exception{
        String timeRegex = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

        boolean flag = java.util.regex.Pattern.matches(timeRegex, "2022-04-21 00:00:00");
        System.out.println(flag);
    }

    @ApiModelProperty("结算单创建时间开始时间，YYYY-MM-DD")
    @Pattern(message = "结算单创建开始时间 输入不合法[yyyy-MM-dd HH:mm:ss]",regexp = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")
    private String createTimeBegin;

    @ApiModelProperty("结算单创建时间结束时间，YYYY-MM-DD")
    @Pattern(message = "结算单创建结束时间 输入不合法[yyyy-MM-dd HH:mm:ss]",regexp = "^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s+([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$")
    private String createTimeEnd;

    @ApiModelProperty("结算单号")
    private String settlementNo;

    @ApiModelProperty("操作ID ，便于日志查询，不填系统默认生成")
    private String opId = UUID.randomUUID().toString();


}
