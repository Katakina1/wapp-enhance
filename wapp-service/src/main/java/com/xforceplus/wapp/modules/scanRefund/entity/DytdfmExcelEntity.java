package com.xforceplus.wapp.modules.scanRefund.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 抵账表实体(发票签收)
 */
@Getter
@Setter
public class DytdfmExcelEntity extends BaseRowModel implements Serializable {




    @ExcelProperty(value={"退货发票查询"},index = 0)
    private String cell0;

    @ExcelProperty(value={"退货发票查询"},index = 1)
    private String cell1;

    @ExcelProperty(value={"退货发票查询"},index = 2)
    private String cell2;

    @ExcelProperty(value={"退货发票查询"},index = 3)
    private String cell3;

    @ExcelProperty(value={"退货发票查询"},index = 4)
    private String cell4;

    @ExcelProperty(value={"退货发票查询"},index = 5)
    private String cell5;

    @ExcelProperty(value={"退货发票查询"},index = 6)
    private String cell6;





    @ExcelProperty(value={"退货发票查询"},index = 7)
    private String cell7;

    @ExcelProperty(value={"退货发票查询"},index = 8)
    private String cell8;



    @ExcelProperty(value={"退货发票查询"},index = 9)
    private String cell9;



    public String getCell0() {
        return cell0;
    }

    public void setCell0(String cell0) {
        this.cell0 = cell0;
    }

    public String getCell1() {
        return cell1;
    }

    public void setCell1(String cell1) {
        this.cell1 = cell1;
    }

    public String getCell2() {
        return cell2;
    }

    public void setCell2(String cell2) {
        this.cell2 = cell2;
    }

    public String getCell3() {
        return cell3;
    }

    public void setCell3(String cell3) {
        this.cell3 = cell3;
    }

    public String getCell4() {
        return cell4;
    }

    public void setCell4(String cell4) {
        this.cell4 = cell4;
    }

    public String getCell5() {
        return cell5;
    }

    public void setCell5(String cell5) {
        this.cell5 = cell5;
    }

    public String getCell6() {
        return cell6;
    }

    public void setCell6(String cell6) {
        this.cell6 = cell6;
    }

    public String getCell7() {
        return cell7;
    }

    public void setCell7(String cell7) {
        this.cell7 = cell7;
    }

    public String getCell8() {
        return cell8;
    }

    public void setCell8(String cell8) {
        this.cell8 = cell8;
    }

    public String getCell9() {
        return cell9;
    }

    public void setCell9(String cell9) {
        this.cell9 = cell9;
    }




}
