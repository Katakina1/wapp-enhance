package com.xforceplus.wapp.modules.signin.service;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntityApi;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceExcelEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
public interface SignInInqueryService {
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

    void ScanMatch(String schemaLabel,RecordInvoiceEntityApi recordInvoiceEntity);

    Integer selectByuuid(String schemaLabel ,String uuid,String id,String invoiceDate ,String invoiceAmount);

    List<RecordInvoiceExcelEntity> transformExcle(List<RecordInvoiceEntity> invoiceEntityList);

    void updateGl(String schemaLabel, Map<String, Object> params);
}
