package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
@Mapper
public interface PhoneAppSignInDao {

    /**
     * 获取抵账表签收状态为已签收（1）签收类型为手机app签收的数据（2）
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(@Param("schemaLabel") String schemaLabel, @Param("query") Query query);

    /**
     * 根据查询条件统计数据总数
     * @param schemaLabel
     * @param query
     * @return
     */
    int getTotal(@Param("schemaLabel") String schemaLabel, @Param("query") Query query);

    /**
     * 获取所有金额之和，所有税额之和
     * @param query 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getSumAmount(@Param("schemaLabel") String schemaLabel, @Param("query") Query query);


    /**
     * 根据登录用户id查询购方税号
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(@Param("schemaLabel") String schemaLabel, @Param("userId") Long userId);

    /**
     * 根据查询条件查询导出所需的数据
     * @param schemaLabel
     * @param params
     * @return
     */
    List<RecordInvoiceEntity> queryAllList(@Param("schemaLabel") String schemaLabel,@Param("params")  Map<String, Object> params);

    /**
     * 根据uuid获取图片路径
     * @param schemaLabel
     * @param uuid
     * @return
     */
    String getUrlById(@Param("schemaLabel") String schemaLabel,@Param("uuid") String uuid);
}
