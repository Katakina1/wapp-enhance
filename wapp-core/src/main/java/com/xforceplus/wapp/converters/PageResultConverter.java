package com.xforceplus.wapp.converters;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;

/**
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2021-10-11 21:02
 **/
public abstract class PageResultConverter {

    public static <T> PageResult<T> toPageResult(Page<T> page){
        final PageResult<T> result = PageResult.of(page.getTotal(), page.getRecords());
        result.getSummary().setPages(page.getPages());
        result.getSummary().setSize(page.getSize());
        return result;
    }
}
