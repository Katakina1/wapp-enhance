package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.InvoiceDetail;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/1 18:37
 */
@Mapper
public interface PrintingOpenRedTicketDataDao {


    Integer getRedTicketMatchListCount(@Param("map") Map<String,Object> map);

    List<RedTicketMatch> queryOpenRedTicket(@Param("map")Map<String,Object> map);

    List<InvoiceDetail> getList(@Param("redTicketDataSerialNumber")String params);

    ReportStatisticsEntity queryTotalResult(@Param("redTicketDataSerialNumber")String params);

    InvoiceEntity getPdfDate(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);
}
