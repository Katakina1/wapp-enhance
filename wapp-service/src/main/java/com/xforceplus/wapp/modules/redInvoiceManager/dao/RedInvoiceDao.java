package com.xforceplus.wapp.modules.redInvoiceManager.dao;

import com.xforceplus.wapp.modules.job.pojo.vendorMaster.DictPo;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RedInvoiceDao {
    void insertRedInvoiceData(@Param("invoice") RedInvoiceData invoice);

    String selectLastRedInvoiceData();

    String findTaxMD(@Param("jvcode") String jvcode);

    List<String> findDict();

    int selectIsExists(@Param("jv") String jv, @Param("invoiceDate") String invoiceDate, @Param("invoiceAmount") String invoiceAmount);
}
