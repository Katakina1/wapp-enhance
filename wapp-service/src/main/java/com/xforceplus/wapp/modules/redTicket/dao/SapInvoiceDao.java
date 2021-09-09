package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.redTicket.entity.SapInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SapInvoiceDao {
    Integer saveSapInvoice(SapInvoiceEntity entity);
    Integer updateSapInvoiceToRecord(SapInvoiceEntity entity);

    
    Integer updateTenUserCode(UserEntity entity);

   
    Integer associateInvoice(SapInvoiceEntity entity);

   
    Integer associateProtocol(SapInvoiceEntity entity);


    String getUserCode(@Param("subject")String subject);
}
