package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDetailEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/12.
 **/
public interface HandWorkService {
    /**
     * 根据条件获取未签收的分页数据
     *
     * @param schemaLabel
     * @param condition
     * @return 未签收的分页数据
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(String schemaLabel, Query condition);

    /**
     * 根据条件获取未签收的数据总数
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    int queryTotal(String schemaLabel, Query query);

    /**
     * 根据条件查询出所有数据导出使用
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> queryList(String schemaLabel, Map<String, Object> query);

    /**
     * 根据id进行发票签收
     *
     * @param schemaLabel
     * @param ids
     * @param user
     * @return
     */
    Boolean receiptInvoice(String schemaLabel, Long[] ids, UserEntity user);

    /**
     * 获取发票明细信息
     *
     * @param schemaLabel
     * @param invoiceNo
     * @return
     */
    RecordInvoiceDetailEntity getRecordIncoiceDetail(String schemaLabel, String invoiceNo);

    /**
     * 查询登录用户关联的购方税号和购方名称
     *
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(String schemaLabel, Long userId);
}
