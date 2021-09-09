package com.xforceplus.wapp.modules.InformationInquiry.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface QuestionnaireDao {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<QuestionnaireEntity> questionnairelist(@Param("map") Map<String, Object> map);
    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<QuestionnaireEntity> cpquestionnairelist(@Param("map") Map<String, Object> map);
    /**
     * 查询问题单息条数
     * @param map
     * @return
     */
    Integer questionnairelistCount(@Param("map") Map<String, Object> map);
    /**
     * 查询问题单息条数
     * @param map
     * @return
     */
    Integer cpquestionnairelistCount(@Param("map") Map<String, Object> map);
    /**
     *
     * 查询全部
     * */
    List<QuestionnaireEntity> questionnairelistAll(@Param("map") Map<String, Object> map);


    /**
     * 导入修改
     * */
    void questionnaireUpdate(@Param("map")Map<String,Object> map);

    void saveInvoice(@Param("map")Map<String,Object> map);


    /**
     * 查询uuid
     * @param id
     * @return
     */
    List<QuestionnaireEntity> queryuuid(@Param("id") Long id);

    //int queryuuids(@Param("id") Long id);
    //修改问题单退票状态
    int queryuuids(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("errStatus") String errStatus);

    int cancelQueryuuids(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("errStatus") String errStatus);

    int xqueryuuids(@Param("id") Long id);

    int cancelTheProcess(@Param("id") String id);
    int xqueryuuidss(@Param("id") Long id);

    int cancelTheProcesss(@Param("id") String id);

    int inputrefundyesno(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("errStatus") String errStatus,@Param("invoiceCost") String invoiceCost);

    int cancelTheRefund(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("errStatus") String errStatus,@Param("invoiceCost") String invoiceCost);

    String queryMatchno(@Param("uuid") String uuid);

    int updateIsDel(@Param("isdel") String isdel,@Param("matchno") String matchno);
    String getUuId(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("invoiceCost")String invoiceCost);
    int invoiceCl(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate")String invoiceDate, @Param("errStatus") String errStatus,@Param("invoiceCost") String invoiceCost);
    String getBatchId(@Param("id") Long id);
}
