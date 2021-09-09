package com.xforceplus.wapp.common.safesoft;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: martin.ou
 * Date: 3/28/12
 * Time: 10:04 AM
 */
public final class PagedQueryResult<T> implements Serializable {

    private static final long serialVersionUID = 7470252877154461619L;

    private Integer totalCount;
    private List<T> results;
    private Object queryCondition;
    private  String msg;

    /**
     * 所有未税金额总计
     */
    private BigDecimal summationTotalAmount;

    /**
     * 所有税额总计
     */
    private BigDecimal summationTaxAmount;

    public Object getQueryCondition() {
        return queryCondition;
    }
    public void setQueryCondition(Object queryCondition) {
        this.queryCondition = queryCondition;
    }

    //配合easyui用查询结果集
    private Integer total;
    private List<T> rows;
    private List<T> footer;

    public PagedQueryResult() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PagedQueryResult(List<T> results, int count, Object queryCondition) {
        this.results = results;
        this.totalCount = count;
        this.queryCondition = queryCondition;
    }

    public PagedQueryResult(List<T> results, int count) {
        this.results = results;
        this.totalCount = count;
    }

    public PagedQueryResult(Integer total, List<T> rows, List<T> footer) {
        this.total = total;
        this.rows = rows;
        this.footer = footer;
    }

    public List<T> getResults() {
        if (this.results == null) {
            return Collections.emptyList();
        }
        return results;
    }

    public void setResults(List<T> list) {
        this.results = list;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer count) {
        this.totalCount = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        if (this.rows == null) {
            return Collections.emptyList();
        }
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public List<T> getFooter() {
        return footer;
    }

    public void setFooter(List<T> footer) {
        this.footer = footer;
    }

    public BigDecimal getSummationTotalAmount() {
        return summationTotalAmount;
    }

    public void setSummationTotalAmount(BigDecimal summationTotalAmount) {
        this.summationTotalAmount = summationTotalAmount;
    }

    public BigDecimal getSummationTaxAmount() {
        return summationTaxAmount;
    }

    public void setSummationTaxAmount(BigDecimal summationTaxAmount) {
        this.summationTaxAmount = summationTaxAmount;
    }
}
