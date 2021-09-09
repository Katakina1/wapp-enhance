package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoice;
import com.xforceplus.wapp.modules.job.entity.TDxRecordInvoiceDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TDxRecordInvoiceDetailDao {

     void insertRecordInvoiceDetail(@Param("invoiceDetailInfo") TDxRecordInvoiceDetail invoiceDetailInfo,@Param("linkName")String linkName);
     void delDetail(@Param("uuid")String uuid,@Param("linkName")String linkName);
     int getDetail(@Param("uuid")String uuid,@Param("linkName")String linkName);
     void updateDetailYesorno(@Param("invoiceInfo") TDxRecordInvoice invoiceInfo, @Param("linkName")String linkName);
     void delDetailTaxRate(@Param("invoiceCode")String invoiceCode,@Param("invoiceNo")String invoiceNO,@Param("linkName")String linkName);
     int  getTaxRate(@Param("invoiceCode")String invoiceCode,@Param("invoiceNo")String invoiceNO,@Param("linkName")String linkName);
     void delDetailJd(@Param("uuid")String uuid,@Param("linkName")String linkName);
     int getDetailJd(@Param("uuid")String uuid,@Param("linkName")String linkName);
     void insertRecordInvoiceDetail1(@Param("invoiceDetailInfo") List<TDxRecordInvoiceDetail> invoiceDetailInfo, @Param("linkName")String linkName);


}