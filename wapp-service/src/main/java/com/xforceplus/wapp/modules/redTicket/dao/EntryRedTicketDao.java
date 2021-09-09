package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.ImportEntity;
import com.xforceplus.wapp.modules.redTicket.entity.InvoiceEntity;
import com.xforceplus.wapp.modules.redTicket.entity.OrgEntity;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/27 10:05
 */
@Mapper
public interface EntryRedTicketDao {

    List<RedTicketMatch> selectRedTicketList(@Param("map")Map<String,Object> map);

    Integer selectRedTicketListCount(@Param("map")Map<String,Object> map);

    List<OrgEntity> getGfNameAndTaxNo(@Param("userId")Long userId);

    List<InvoiceEntity> invoiceQueryList(@Param("map")Map<String,Object> map);

    void updateVenderid(@Param("map") Map<String,Object> map);

    RedTicketMatch getRedNoticeNumber(@Param("uuid")String uuid);

    List<InvoiceEntity> ifExist(@Param("map")Map<String,Object> map);

    int saveInvoice(@Param("map")Map<String,Object> map);

    int updateRate(@Param("map")Map<String,Object> map);

    int allUpdate(@Param("map")Map<String,Object> map);

    RedTicketMatch selectRedTicketById(@Param("map")Map<String,Object> map);

    int updateRedNoticeNumber(@Param("map")Map<String,Object> map);

    int updateRed(@Param("map")Map<String,Object> map);

    RedTicketMatch selectNoticeById(@Param("map")Map<String,Object> params);

    RedTicketMatch getRedNoticeMatch(@Param("redNoticeNumber")String redNoticeNumber);

    int saveRedNoticeNumber(@Param("redNoticeNumber")String redNoticeNumber,@Param("uuid") String uuid,@Param("jvcode") String jvcode,@Param("companyCode") String companyCode);

    int saveInvoiceMatch(@Param("map")Map<String,Object> map);

    int saveInvoiceMatchEntity(@Param("map")Map<String,Object> map);

    int allUpdateMatch(@Param("map")Map<String,Object> map);


    int insertRedInvoiceMatch(@Param("map")Map<String,Object> map);

    InvoiceEntity getInvoiceQuery(@Param("uuid")String uuid);
   //Excel导入
    void allUpdateBatchInvoice(ImportEntity invoiceEntity);

    void allUpdateMatchBatch(ImportEntity invoiceEntity);

    void saveInvoiceMatchBath(InvoiceEntity invoiceEntity);

    void insertRedTicketInvoice(ImportEntity importEntity);

    int updateRuteStatu(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);

    void updateProcloStatu(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);

    String getXfTaxno(@Param("orgid")Integer orgid);

    String getOrgCode(@Param("gfTaxNo")String gfTaxNo);

    void insertRedNoticeNumber(@Param("map")Map<String,Object> map);
}
