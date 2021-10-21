package com.xforceplus.wapp.modules.deduct.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by SunShiyong on 2021/10/20.
 */


@ApiModel(description = "修改业务单请求对象")
@Data
public class UpdateBillStatusRequest {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("业务单据类型 1:索赔;2:协议;3:EPD ")
    private Integer deductType;

    @ApiModelProperty("业务单状态" +
            "索赔单:101待匹配明细;102待确认税编;103待确认税差;104待匹配蓝票;105:待匹配结算单;106已匹配结算单;107待审核;108已撤销\n" +
            "协议单:201待匹配结算单;202已匹配结算单;203已锁定;204已取消\n" +
            "EPD单:301待匹配结算单;302已匹配结算单; 1锁定 0解锁")
    private Integer deductStatus;
}
