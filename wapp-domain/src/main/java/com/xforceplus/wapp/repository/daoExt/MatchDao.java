package com.xforceplus.wapp.repository.daoExt;

import com.xforceplus.wapp.repository.entity.InvoiceEntity;
import com.xforceplus.wapp.repository.entity.OrgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface MatchDao {

    List<InvoiceEntity> invoiceQueryList(Map<String, Object> map);

    List<InvoiceEntity> ifExist(Map<String, Object> map) ;

    Integer update(@Param("id") Long id, @Param("taxRate") BigDecimal taxRate);
    /**
     * 覆盖
     * @param map
     * @return
     */
    Integer allUpdate(Map<String, Object> map);

    Integer allUpdatePP(Map<String, Object> map);


    OrgEntity getXfMessage(@Param("venderid") String venderid);


    Integer updateDkAmount(@Param("amount") BigDecimal amount, @Param("uuid") String uuid);
}
