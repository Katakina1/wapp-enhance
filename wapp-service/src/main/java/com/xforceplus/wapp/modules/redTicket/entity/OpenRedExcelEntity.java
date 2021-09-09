package com.xforceplus.wapp.modules.redTicket.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 费用扫描签收
 */
@Getter
@Setter
public class OpenRedExcelEntity extends BaseRowModel implements Serializable {

    @ExcelProperty(value={"序号"},index = 0)
    private String rownumber;
    @ExcelProperty(value={"供应商号"},index = 1)
    private String venderId;
    @ExcelProperty(value={"红票序列号"},index = 2)
    private String redTicketDataSerialNumber;
    @ExcelProperty(value={"业务类型"},index = 3)
    private String businessType;
    @ExcelProperty(value={"红冲总金额"},index = 4)
    private String redTotalAmount;
    @ExcelProperty(value={"红字通知单号"},index = 5)
    private String redNoticeNumber;
    @ExcelProperty(value={"是否上传资料"},index = 6)
    private String dataStatus;
    @ExcelProperty(value={"是否上传通知单"},index = 7)
    private String noticeStatus;
    @ExcelProperty(value={"审核结果"},index = 8)
    private String examineResult;
    @ExcelProperty(value={"审核备注"},index = 9)
    private String examineRemarks;
    @ExcelProperty(value={"发票代码"},index = 10)
    private String invoiceCode;
    @ExcelProperty(value={"发票号码"},index = 11)
    private String invoiceNo;
    @ExcelProperty(value={"发票金额"},index = 12)
    private String invoiceAmount;
    @ExcelProperty(value={"发票税额"},index = 13)
    private String taxAmount;
    @ExcelProperty(value={"发票税率"},index = 14)
    private String taxReta;
    @ExcelProperty(value={"价税合计"},index = 15)
    private String invoiceTotal;
    @ExcelProperty(value={"扫描匹配状态"},index = 16)
    private String scanMatchStatus;
}
