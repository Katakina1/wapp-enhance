package com.xforceplus.wapp.modules.redTicket.dao;

import com.xforceplus.wapp.modules.redTicket.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by 1 on 2018/10/22 18:26
 */
@Mapper
public interface ExamineAndUploadRedNoticeDao {


    List<RedTicketMatch> queryOpenRedTicket(@Param("map")Map<String,Object> map);

    Integer getRedTicketMatchListCount(@Param("map")Map<String,Object> params);

    Integer saveExamineRemarks(@Param("map")Map<String,Object> para);

    void updateStatus(@Param("id")Integer id,@Param("redNoticeNumber") String redNoticeNumber, @Param("redNoticeAssociation") Integer  redNoticeAssociation);

    List<RedTicketMatchDetail> getRedTicketDetailsById(@Param("map")Map<String,Object> para);

    List<InvoiceEntity> getRedTicketInvoice(@Param("map")Map<String,Object> para);

    void updateTotalAmount(@Param("totalAmount")BigDecimal totalAmount, @Param("invoiceCode")String invoiceCode, @Param("invoiceNo")String invoiceNo);

    void updateRuturnNumber(@Param("map")Map<String,Object> para);

    void updateAgreementNumber(@Param("map")Map<String,Object> para);

    int saveFilePathRed(@Param("fileEntity")FileEntity fileEntity);

    RedTicketMatch selectMatchTableByRedTicketDataSerialNumber(@Param("redTicketDataSerialNumber")String redTicketDataSerialNumber);

    void cancelReturnGoodsStatus(@Param("map")Map<String,Object> map);

    void cancelAgreementStatus(@Param("map")Map<String,Object> map);

    void redRushInformationObsolete(@Param("map")Map<String,Object> map);

    void clearTicketInformationData(@Param("map")Map<String,Object> map);

    List<RedTicketMatchMiddle> queryRedTicketMatchMiddle(@Param("map")Map<String,Object> map);

    void invoiceRedRushAmountBackflush(@Param("map")RedTicketMatchMiddle redTicketMatchMiddle);

    void updateRuturnStatus(@Param("redNoticeNumber")String redNoticeNumber);

    void updateAgreementStatus(@Param("redNoticeNumber")String redNoticeNumber);

    void updateMatchStatus(@Param("id")long id);

    RedTicketMatch getRedTicketMatch(@Param("id")long id);

    int getRedMatchByNo(@Param("redNoticeNumber")String redNoticeNumber);

    String getCopyPerson(String dictcode);

    List<RedTicketMatch> selectOpenRedTicketById(String[] arr);

    int revoke(@Param("id") Long id);

    InvoiceEntity getRedInfo(@Param("uuid") String uuid);

    String selectTaxCode(@Param("taxname") String taxname);
}
