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
    @ApiModelProperty("分页条数信息") private Summary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary{
        private long total;
        private long pages;
        private int size;
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
}
