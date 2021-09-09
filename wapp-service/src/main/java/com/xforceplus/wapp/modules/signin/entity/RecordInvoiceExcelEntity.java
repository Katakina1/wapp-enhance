package com.xforceplus.wapp.modules.signin.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.xforceplus.wapp.common.safesoft.AbstractBaseDomain;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票签收实体
 * CreateBy leal.liang on 2018/4/12.
 **/
@Getter
@Setter
@ToString
public class RecordInvoiceExcelEntity extends BaseRowModel implements Serializable {



    @ExcelProperty(value={"扫描处理","序号"},index = 0)
    private String cell0;

    @ExcelProperty(value={"扫描处理","扫描日期"},index = 1)
    private String cell1;

    @ExcelProperty(value={"扫描处理","扫描流水号"},index = 2)
    private String cell2;

    @ExcelProperty(value={"扫描处理","文件类型"},index = 3)
    private String cell3;

    @ExcelProperty(value={"扫描处理","JV"},index = 4)
    private String cell4;

    @ExcelProperty(value={"扫描处理","公司代码"},index = 5)
    private String cell5;

    @ExcelProperty(value={"扫描处理","扫描描述"},index = 6)
    private String cell6;

    @ExcelProperty(value={"扫描处理","供应商号"},index = 7)
    private String cell7;

    @ExcelProperty(value={"扫描处理","发票代码"},index = 8)
    private String cell8;



    @ExcelProperty(value={"扫描处理","发票号码"},index = 9)
    private String cell9;

    @ExcelProperty(value={"扫描处理","发票类型"},index = 10)
    private String cell10;

    @ExcelProperty(value={"扫描处理","开票日期"},index = 11)
    private String cell11;

    @ExcelProperty(value={"扫描处理","金额"},index = 12)
    private String cell12;

    @ExcelProperty(value={"扫描处理","税额"},index = 13)
    private String cell13;

    @ExcelProperty(value={"扫描处理","认证结果"},index = 14)
    private String cell14;

    @ExcelProperty(value={"扫描处理","认证日期"},index = 15)
    private String cell15;

    @ExcelProperty(value={"扫描处理","业务类型"},index = 16)
    private String cell16;

    @ExcelProperty(value={"扫描处理","扫描匹配状态"},index = 17)
    private String cell17;

    @ExcelProperty(value={"扫描处理","扫描匹配失败原因"},index = 18)
    private String cell18;

    @ExcelProperty(value={"扫描处理","签收状态"},index = 19)
    private String cell19;
    @ExcelProperty(value={"扫描处理","急票龄"},index = 20)
    private String jpl;
    @ExcelProperty(value={"扫描处理","备注"},index = 21)
    private String bz;
    @ExcelProperty(value={"扫描处理","GL发票"},index = 22)
    private String gl;
    public String getCell0() {
        return cell0;
    }

    public String getGl() {
        return gl;
    }

    public void setGl(String gl) {
        this.gl = gl;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getJpl() {
        return jpl;
    }

    public void setJpl(String jpl) {
        this.jpl = jpl;
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

    public String getCell12() {
        return cell12;
    }

    public void setCell12(String cell12) {
        this.cell12 = cell12;
    }

    public String getCell13() {
        return cell13;
    }

    public void setCell13(String cell13) {
        this.cell13 = cell13;
    }

    public String getCell14() {
        return cell14;
    }

    public void setCell14(String cell14) {
        this.cell14 = cell14;
    }

    public String getCell15() {
        return cell15;
    }

    public void setCell15(String cell15) {
        this.cell15 = cell15;
    }

    public String getCell16() {
        return cell16;
    }

    public void setCell16(String cell16) {
        this.cell16 = cell16;
    }

    public String getCell17() {
        return cell17;
    }

    public void setCell17(String cell17) {
        this.cell17 = cell17;
    }

    public String getCell18() {
        return cell18;
    }

    public void setCell18(String cell18) {
        this.cell18 = cell18;
    }

    public String getCell19() {
        return cell19;
    }

    public void setCell19(String cell19) {
        this.cell19 = cell19;
    }
}
