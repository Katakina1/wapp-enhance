package com.xforceplus.wapp.modules.posuopei.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.modules.base.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author raymond.yan
 */
public class PoExcelEntity extends BaseRowModel implements Serializable {




    //序号
    @ExcelProperty(value={"序号"},index = 0)
    private String cell0;

    @ExcelProperty(value={"序号"},index = 1)
    private String cell1;

    @ExcelProperty(value={"序号"},index = 2)
    private String cell2;

    @ExcelProperty(value={"序号"},index = 3)
    private String cell3;

    @ExcelProperty(value={"序号"},index = 4)
    private String cell4;

    @ExcelProperty(value={"序号"},index = 5)
    private String cell5;

    @ExcelProperty(value={"序号"},index = 6)
    private String cell6;

    @ExcelProperty(value={"序号"},index = 7)
    private String cell7;

    @ExcelProperty(value={"序号"},index = 8)
    private String cell8;

    @ExcelProperty(value={"序号"},index = 9)
    private String cell9;

    @ExcelProperty(value={"序号"},index = 10)
    private String cell10;

    @ExcelProperty(value={"序号"},index = 11)
    private String cell11;

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

    public String getCell10() {
        return cell10;
    }

    public void setCell10(String cell10) {
        this.cell10 = cell10;
    }

    public String getCell11() {
        return cell11;
    }

    public void setCell11(String cell11) {
        this.cell11 = cell11;
    }
}
