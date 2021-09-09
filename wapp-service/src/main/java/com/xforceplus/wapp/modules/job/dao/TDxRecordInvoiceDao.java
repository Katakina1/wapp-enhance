package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.*;
import com.xforceplus.wapp.modules.job.pojo.InvoiceInfo;
import com.xforceplus.wapp.modules.job.pojo.InvoiceSelectInfo;
import com.xforceplus.wapp.modules.job.pojo.RecordInvoice;
import com.xforceplus.wapp.modules.job.pojo.State;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TDxRecordInvoiceDao {

     void insertRecordInvoice(@Param("recordInvoices") TDxRecordInvoice recordInvoices,@Param("linkName")String linkName,@Param("newTaxno")String newTaxno);

     void insertInvoiceLog(@Param("tDxInvoiceLog")TDxInvoiceLog tDxInvoiceLog,@Param("linkName")String linkName);

    void updateInvoiceByState(@Param("state")State state,@Param("linkName")String linkName);

    void insertBySelect(@Param("invoiceSelectInfo") InvoiceSelectInfo invoiceSelectInfo, @Param("linkName") String linkName,@Param("companyCode")String companyCode, @Param("jvcode") String jvcode);

    int findCountInvoiceByCodeAndNo(@Param("invoice_code") String invoice_code, @Param("invoice_no")String invoice_no,@Param("linkName")String linkName);

    void updateInvoiceByCodeAndNo(@Param("invoiceInfo")TDxRecordInvoice invoiceInfo,@Param("linkName")String linkName);

    void updateSignData(@Param("invoiceInfo")TDxRecordInvoice invoiceInfo,@Param("linkName")String linkName);

    InvoiceScanEntity selectSign(@Param("code") String code, @Param("no") String no, @Param("linkName") String linkName);

    String selectLastInvoiceDate(@Param("taxno") String taxno, @Param("linkName") String linkName,@Param("type")String type);

    TDxRecordInvoice findInvoiceByCodeAndNo(@Param("invoice_code") String invoice_code, @Param("invoice_no")String invoice_no,@Param("linkName")String linkName);

    void updateInvoice(@Param("invoiceInfo")TDxRecordInvoice invoice, @Param("linkName") String linkName);

    String selectLastInvoiceDateQPM(@Param("taxno") String taxno, @Param("linkName") String linkName,@Param("type")String type);

    String getGfNameByTaxno(@Param("taxno") String buyerTaxNo);

    TAcOrg findCompanyAndJv(String taxno);
}