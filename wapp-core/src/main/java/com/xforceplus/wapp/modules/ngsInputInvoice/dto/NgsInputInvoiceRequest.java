package com.xforceplus.wapp.modules.ngsInputInvoice.dto;

import com.xforceplus.evat.common.domain.ngs.NgsInputInvoiceQuery;
import com.xforceplus.wapp.modules.customs.dto.CustomsQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Setter
@Getter
public class NgsInputInvoiceRequest implements Serializable {
    private final static long serialVersionUID = 1L;
    @ApiModelProperty("是否全选 0 未全选 1全选")
    private String isAllSelected;
    @ApiModelProperty("包含项 选中的id")
    private List<Long> includes;

    @ApiModelProperty("排除项 界面的搜索条件")
    private NgsInputInvoiceQuery excludes;
}
