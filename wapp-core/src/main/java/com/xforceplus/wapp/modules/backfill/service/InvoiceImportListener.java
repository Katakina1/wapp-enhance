package com.xforceplus.wapp.modules.backfill.service;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.backfill.model.BackfillInvoice;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by SunShiyong on 2021/10/16.
 */

@Slf4j
@Getter
public class InvoiceImportListener extends AnalysisEventListener<BackfillInvoice> {

    private Integer rows;
    private Integer validRows;
    private Integer invalidRows;
    private final List<BackfillInvoice> validInvoices = Lists.newArrayList();
    private final List<BackfillInvoice> invalidInvoices = Lists.newArrayList();

    public InvoiceImportListener() {
        this.rows = 0;
        this.validRows = 0;
        this.invalidRows = 0;
    }

    @Override
    public void invoke(BackfillInvoice data, AnalysisContext context) {
        log.info("解析到一条数据:{}", JSON.toJSONString(data));
        rows++;
        if (checkImportInvoice(data,rows)) {
            validRows++;
            validInvoices.add(data);
        } else {
            invalidRows++;
            invalidInvoices.add(data);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有数据解析完成！");
    }

    private boolean checkImportInvoice(BackfillInvoice data,int rowNum) {
        if(StringUtils.isEmpty(data.getAmount())){
            log.info("发票数据导入异常:第{}行金额为空",rowNum);
            return false;
        }
        if(StringUtils.isEmpty(data.getInvoiceCode())){
            log.info("发票数据导入异常:第{}发票代码为空",rowNum);
            return false;
        }
        if(StringUtils.isEmpty(data.getInvoiceNo())){
            log.info("发票数据导入异常:第{}发票号码为空",rowNum);
            return false;
        }
        if(StringUtils.isEmpty(data.getPaperDrewDate())){
            log.info("发票数据导入异常:第{}开票日期为空",rowNum);
            return false;
        }
        Pattern pattern = Pattern.compile("\\d{8}");
        return pattern.matcher(data.getPaperDrewDate()).matches();
    }



}
