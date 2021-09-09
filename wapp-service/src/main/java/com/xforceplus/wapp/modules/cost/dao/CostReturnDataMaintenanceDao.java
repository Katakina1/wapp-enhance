package com.xforceplus.wapp.modules.cost.dao;


import com.xforceplus.wapp.modules.InformationInquiry.entity.QuestionnaireEntity;
import com.xforceplus.wapp.modules.cost.entity.ApplicantEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface CostReturnDataMaintenanceDao {

    /**
     * 查询订单信息
     * @param map
     * @return
     */
    List<ApplicantEntity> questionnairelist(@Param("map") Map<String, Object> map);

    /**
     * 查询问题单息条数
     * @param map
     * @return
     */
    Integer questionnairelistCount(@Param("map") Map<String, Object> map);

    /**
     *
     * 查询全部
     * */
    List<QuestionnaireEntity> questionnairelistAll(@Param("map") Map<String, Object> map);


    /**
     * 导入修改
     * */
    void questionnaireUpdate(@Param("map") Map<String, Object> map);

    void saveInvoice(@Param("map") Map<String, Object> map);


    /**
     * 查询uuid
     * @param id
     * @return
     */
    void queryuuid(@Param("id") int id);

    //int queryuuids(@Param("id") Long id);
    //修改问题单退票状态
    int queryuuids(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate") Date invoiceDate, @Param("errStatus") String errStatus);

    int cancelQueryuuids(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate") Date invoiceDate, @Param("errStatus") String errStatus);

    int xqueryuuids(@Param("id") Long id);

    int cancelTheProcess(@Param("id") String id);

    int inputrefundyesno(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate") Date invoiceDate, @Param("errStatus") String errStatus);

    int cancelTheRefund(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate") Date invoiceDate, @Param("errStatus") String errStatus);

    String queryMatchno(@Param("uuid") String uuid);

    int updateIsDel(@Param("isdel") String isdel, @Param("matchno") String matchno);
    String getUuId(@Param("invNo") String invNo, @Param("vendorNo") String vendorNo, @Param("invoiceDate") Date invoiceDate);
}
