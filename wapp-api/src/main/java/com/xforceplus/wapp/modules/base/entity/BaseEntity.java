package com.xforceplus.wapp.modules.base.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by Daily.zhang on 2018/04/13.
 */
public abstract class BaseEntity {

    /**
     * 分页查询起始值
     */
    private Integer offset;

    /**
     * 分页查询条数
     */
    private Integer limit;

    //当前页码
    private Integer page;

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
