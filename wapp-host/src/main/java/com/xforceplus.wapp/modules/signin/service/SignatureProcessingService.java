package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.collect.pojo.ResponseInvoice;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/19.
 **/
public interface SignatureProcessingService {
    /**
     * 获取扫描表签收状态为签收成功（1）的分页数据
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query query);

    /**
     * 获取扫描表签收状态为签收成功（1）的数据总数
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
     * 根据前台的查询条件查询扫描表所有符合条件的数据
     * @param schemaLabel
     * @param params
     */
    List<RecordInvoiceEntity> queryAllList(String schemaLabel, Map<String, Object> params);

    /**
     * 根据uuid更改抵账表签收信息 备份扫描表数据 并删除扫描表信息
     * @return
     *
     * @param schemaLabel
     * @param uuid
     * @return
     */
    Boolean deleteByuuid(String schemaLabel, String uuid,UserEntity user) throws Exception;

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
     * 调用查验接口查验普通发票，电子发票，卷票
     * @param schemaLabel
     * @param params
     * @return
     */
    ResponseInvoice checkPlainInvoice(String schemaLabel, Map<String, Object> params) throws Exception;

    RecordInvoiceEntity getDataByuuid(String schemaLabel, Map<String, Object> params);
}
