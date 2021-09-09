package com.xforceplus.wapp.common.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 分页工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月4日 下午12:59:00
 */
public class PageUtils implements Serializable {
    private static final long serialVersionUID = 1L;
    //总记录数
    private int totalCount;
    //每页记录数
    private int pageSize;
    //总页数
    private int totalPage;
    //当前页数
    private int currPage;
    //列表数据
    private List<?> list;

    /**
     * 所有未税金额总计
     */
    private String summationTotalAmount;

    /**
     * 所有税额总计
     */
    private String summationTaxAmount;

    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
    }

    public PageUtils(List<?> list, int totalCount, int pageSize, int currPage, BigDecimal summationTotalAmount, BigDecimal summationTaxAmount) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int) Math.ceil((double) totalCount / pageSize);
        this.summationTotalAmount = summationTotalAmount == null ? "0" : summationTotalAmount.toString();
        this.summationTaxAmount = summationTaxAmount == null ? "0" : summationTaxAmount.toString();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public String getSummationTotalAmount() {
        return summationTotalAmount;
    }

    public void setSummationTotalAmount(String summationTotalAmount) {
        this.summationTotalAmount = summationTotalAmount;
    }

    public String getSummationTaxAmount() {
        return summationTaxAmount;
    }

    public void setSummationTaxAmount(String summationTaxAmount) {
        this.summationTaxAmount = summationTaxAmount;
    }
}
