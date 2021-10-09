package com.xforceplus.wapp.common.dto;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-09 14:53
 **/

@Data
public class PageResult<T> {
    private List<T> rows;
    private Summary summary;

    @Data
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
