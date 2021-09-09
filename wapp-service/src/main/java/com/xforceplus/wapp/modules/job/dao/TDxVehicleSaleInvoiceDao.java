package com.xforceplus.wapp.modules.job.dao;

import com.xforceplus.wapp.modules.job.entity.TDxVehicleSaleInvoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TDxVehicleSaleInvoiceDao {

    void insertInvoice(@Param("tDxVehicleSaleInvoice") TDxVehicleSaleInvoice tDxVehicleSaleInvoice,@Param("linkName")String linkName);
}