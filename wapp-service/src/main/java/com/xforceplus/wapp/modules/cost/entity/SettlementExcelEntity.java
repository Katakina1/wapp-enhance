package com.xforceplus.wapp.modules.cost.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Setter
@Getter
public class SettlementExcelEntity extends BaseRowModel {
    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    //供应商号
    @ExcelProperty(value={"供应商号"},index = 3)
    private String venderId;
    //供应商名称
    @ExcelProperty(value={"供应商名称"},index = 4)
    private String venderName;

    //审批人员工号
    @ExcelProperty(value={"审批人邮箱"},index = 2)
    private String approverEmail;

    //结算金额
    @ExcelProperty(value={"费用金额"},index = 5)
    private String settlementAmount;

    //费用号
    @ExcelProperty(value={"费用号"},index = 1)
    private String costNo;
    //创建日期
    @ExcelProperty(value={"申请日期"},index = 8)
    private String createDate;

    //扫描状态
    @ExcelProperty(value={"扫描匹配状态"},index = 14)
    private String scanStatus;

    //BPMS返回的号
    @ExcelProperty(value={"EPS_NO"},index = 6)
    private String epsNo;

    @ExcelProperty(value={"驳回理由"},index = 10)
    private String rejectReason;

    //扫描日期
    @ExcelProperty(value={"walmart审批状态"},index = 9)
    private String walmartStatus;

    //扫描日期
    @ExcelProperty(value={"walmart状态更新日期"},index = 13)
    private String walmartDate;

    //发票号
    @ExcelProperty(value={"发票号码"},index = 7)
    private String invoiceNo;

    //发票号
    @ExcelProperty(value={"价税合计"},index = 11)
    private String totalAmount;

    //发票号
    @ExcelProperty(value={"数据来源"},index = 12)
    private String payModel;

    //扫描日期
    @ExcelProperty(value={"匹配日期"},index = 15)
    private String scanDate;

}
