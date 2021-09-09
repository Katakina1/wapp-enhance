package com.alibaba.excel.write;

import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ExcelBuilder {
    void addContent(List var1, int var2);

    void addContent(List var1, Sheet var2);

    void addContent(List var1, Sheet var2, Table var3);

    void merge(int var1, int var2, int var3, int var4);

    void finish();

    Workbook getWorkbook();
}