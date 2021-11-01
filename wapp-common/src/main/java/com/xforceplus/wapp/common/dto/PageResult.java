package com.xforceplus.wapp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-09 14:53
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("分页结果")
public class PageResult<T> {
    @ApiModelProperty("分页数据信息") private List<T> rows;
    @ApiModelProperty("分页统计") private Summary summary;

    @ApiModelProperty("扩展返回数据") private Object ext;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("分页统计")
    public static class Summary{
        @ApiModelProperty("总条数")
        private long total;
        @ApiModelProperty("总页数")
        private long pages;
        @ApiModelProperty("每页数量")
        private long size;
    }

    public static <T> PageResult<T> of(long total, List<T> rows) {
        PageResult<T> pageResp = new PageResult<>();
        Summary summary = new Summary();
        summary.setTotal(total);
        if (rows == null) {
            pageResp.setRows(Collections.emptyList());
        } else {
            pageResp.setRows(rows);
        }
        pageResp.setSummary(summary);
        return pageResp;
    }
    public static <T> PageResult<T> of(List<T> rows, long total,long pages,long size) {
        PageResult<T> pageResp = new PageResult<>();
        Summary summary = new Summary();
        summary.setTotal(total);
        summary.setPages(pages);
        summary.setSize(size);
        if (rows == null) {
            pageResp.setRows(Collections.emptyList());
        } else {
            pageResp.setRows(rows);
        }
        pageResp.setSummary(summary);
        return pageResp;
    }
}
