package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.SapInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface SapInvoiceDao {
    Integer saveSapInvoice(SapInvoiceEntity entity);
    Integer updateSapInvoiceToRecord(SapInvoiceEntity entity);

   
    Integer updateTenUserCode(UserEntity entity);

   
    Integer associateInvoice(SapInvoiceEntity entity);

    Integer associateProtocol(SapInvoiceEntity entity);


  
    String getUserCode(@Param("subject")String subject);

    
    List<String> getDictNameByType(@Param("type")String type);
    void delZp();
    Integer selectDsign(@Param("paymentDate")String paymentDate, @Param("referTo")String referTo, @Param("currencyAmount") BigDecimal currencyAmount, @Param("venderid")String venderid);
}
