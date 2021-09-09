//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alibaba.excel;

import com.alibaba.excel.event.WriteHandler;
import com.alibaba.excel.metadata.BaseRowModel;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.parameter.GenerateParam;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.ExcelBuilder;
import com.alibaba.excel.write.ExcelBuilderImpl;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExcelWriter {
    private ExcelBuilder excelBuilder;
    /** @deprecated */
    @Deprecated
    private Class<? extends BaseRowModel> objectClass;

    public ExcelWriter(OutputStream outputStream, ExcelTypeEnum typeEnum) {
        this(outputStream, typeEnum, true);
    }

    /** @deprecated */
    @Deprecated
    public ExcelWriter(GenerateParam generateParam) {
        this(generateParam.getOutputStream(), generateParam.getType(), true);
        this.objectClass = generateParam.getClazz();
    }

    public ExcelWriter(OutputStream outputStream, ExcelTypeEnum typeEnum, boolean needHead) {
        this.excelBuilder = new ExcelBuilderImpl((InputStream)null, outputStream, typeEnum, needHead, (WriteHandler)null);
    }

    public ExcelWriter(InputStream templateInputStream, OutputStream outputStream, ExcelTypeEnum typeEnum, Boolean needHead) {
        this.excelBuilder = new ExcelBuilderImpl(templateInputStream, outputStream, typeEnum, needHead, (WriteHandler)null);
    }

    public ExcelWriter(InputStream templateInputStream, OutputStream outputStream, ExcelTypeEnum typeEnum, Boolean needHead, WriteHandler writeHandler) {
        this.excelBuilder = new ExcelBuilderImpl(templateInputStream, outputStream, typeEnum, needHead, writeHandler);
    }

    public ExcelWriter write(List<? extends BaseRowModel> data, Sheet sheet) {
        this.excelBuilder.addContent(data, sheet);
        return this;
    }

    /** @deprecated */
    @Deprecated
    public ExcelWriter write(List data) {
        return this.objectClass != null ? this.write(data, new Sheet(1, 0, this.objectClass)) : this.write0(data, new Sheet(1, 0, this.objectClass));
    }

    public ExcelWriter write1(List<List<Object>> data, Sheet sheet) {
        this.excelBuilder.addContent(data, sheet);
        return this;
    }

    public ExcelWriter write0(List<List<String>> data, Sheet sheet) {
        this.excelBuilder.addContent(data, sheet);
        return this;
    }

    public ExcelWriter write(List<? extends BaseRowModel> data, Sheet sheet, Table table) {
        this.excelBuilder.addContent(data, sheet, table);
        return this;
    }

    public ExcelWriter write0(List<List<String>> data, Sheet sheet, Table table) {
        this.excelBuilder.addContent(data, sheet, table);
        return this;
    }

    public ExcelWriter merge(int firstRow, int lastRow, int firstCol, int lastCol) {
        this.excelBuilder.merge(firstRow, lastRow, firstCol, lastCol);
        return this;
    }

    public ExcelWriter write1(List<List<Object>> data, Sheet sheet, Table table) {
        this.excelBuilder.addContent(data, sheet, table);
        return this;
    }

    public void finish() {
        this.excelBuilder.finish();
    }

    public Workbook getWorkbook() {
        return excelBuilder.getWorkbook();
    }
}
