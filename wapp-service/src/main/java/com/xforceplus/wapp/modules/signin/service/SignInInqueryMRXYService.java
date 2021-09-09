package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
public interface SignInInqueryMRXYService {
    /**
     * 获取当前登录人关联的购方名称
     *
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(String schemaLabel, Long userId);

    /**
     * 分页显示的总数统计
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    int queryTotal(String schemaLabel, Query query);

    /**
     * 获取所有金额之和，所有税额之和
     * @param query 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query);

    /**
     * 获取扫描表符合条件的分页数据
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query);

    /**
     * 获取扫描表符合查询条件的数据 --导出
     *
     * @param schemaLabel
     * @param params
     * @return
     */
    List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params);


    /**
     * 对专用发票的查验
     *
     * @param schemaLabel
     * @param params
     * @return
     */
    Boolean checkedInvoice(String schemaLabel, Map<String, Object> params);

    /**
     * 更新抵账表和扫描表的签收数据
     *
     * @param schemaLabel
     * @param uuid
     * @param user
     * @return
     */
    Boolean updateInvoice(String schemaLabel, String uuid, UserEntity user, Map<String ,Object> params) throws RRException;
    /**
     * 更新抵账表和扫描表的签收数据
     *
     * @param uuIdSet
     * @return
     */
     int batchUpdate(String schemaLabel,UserEntity user,Set<String> uuIdSet);


}
