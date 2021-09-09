package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DirectAuthDao {
    List<ComprehensiveInvoiceQueryEntity> queryList(@Param("map") Map<String, Object> map);
    int queryCount(@Param("map") Map<String, Object> map);
    Integer updateRecordInvoice(ConfirmInvoiceEntity entity);
    Integer updateInvoice(ConfirmInvoiceEntity entity);
}

