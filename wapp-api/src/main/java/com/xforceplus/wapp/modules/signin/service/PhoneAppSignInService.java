package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/14.
 **/
public interface PhoneAppSignInService {
    /**
     * 获取抵账表签收状态为已签收（1）签收类型为手机app签收的数据（2）
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query);

    /**
     * 根据查询条件统计数据总数
     * @param schemaLabel
     * @param query
     * @return
     */
    int queryTotal(String schemaLabel, Query query);

    /**
     * 根据登录用户id查询购方税号
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(String schemaLabel, Long userId);

    /**
     * 根据查询条件查询导出所需的数据
     * @param schemaLabel
     * @param params
     * @return
     */
    List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params);

    /**
     * 根据uuid获取图片路径
     * @param schemaLabel
     * @param uuid
     * @return
     */
    String getUrlById(String schemaLabel, String uuid);
}
