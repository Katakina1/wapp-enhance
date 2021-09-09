package com.xforceplus.wapp.modules.InformationInquiry.dao;

import com.xforceplus.wapp.modules.InformationInquiry.entity.RedNoticeBathEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/11/21 10:36
 */
@Mapper
public interface RedNoticeInvoiceDao {

    RedTicketMatch selectRedNoticeNumbers(@Param("redTicketDataSerialNumber") String redTicketDataSerialNumber);

    int saveRedNoticeInvoiceData(RedNoticeBathEntity redNoticeBathEntity);

    int selectRedNotice(@Param("redNoticeNumber")String redNoticeNumber);

    List<RedNoticeBathEntity> queryList(@Param("map")Map<String,Object> map);

    int queryTotalResult(@Param("map")Map<String,Object> map);

    String gfNames(@Param("redTicketDataSerialNumber") String redTicketDataSerialNumber);

    String gfTaxnos(@Param("redTicketDataSerialNumber") String redTicketDataSerialNumber);

    List<RedNoticeBathEntity> getGfuuid(@Param("redTicketDataSerialNumber") String redTicketDataSerialNumber);
    String gfDxName(@Param("uuid") String uuid);
    String gfDxTaxno(@Param("uuid") String uuid);
}
