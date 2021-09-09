package com.xforceplus.wapp.modules.transferOut.service;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/11
 * Time:18:36
*/

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
    /**
     * 查询抵账表信息 已认证 未转出
     * @param schemaLabel
     * @param map
     * @return
     */
    List<InvoiceEntity> transferOutQuery(String schemaLabel,Map<String, Object> map);

    /**
     *查询总数 已认证 未转出
     * @param schemaLabel
     * @param map
     * @return
     */
    int transferOutQueryTotal(String schemaLabel,Map<String, Object> map);

    /**
     * 获取所有金额之和，所有税额之和
     * @param query 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getSumAmount(String schemaLabel, Query query);

    /**
     * 获取所有金额之和，所有税额之和
     * @param query 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getOutedSumAmount(String schemaLabel, Query query);


    /**
     *已转出查询
     * @param schemaLabel
     * @param map
     * @return
     */
    List<InvoiceEntity> transferOutedQuery(String schemaLabel,Map<String, Object> map);

    /**
     *
     * @param schemaLabel
     * @param map
     * @return
     */
    int transferOutedQueryTotal(String schemaLabel,Map<String, Object> map);

    /**
     * 设置转出
     * @param schemaLabel
     * @param ids
     * @param outRemark
     * @param outReason
     * @param outTaxAmount
     * @param outInvoiceAmout
     * @param outStatus
     * @return
     */
    int setTransferOut(String schemaLabel,String  ids,String outRemark,String outReason, String outTaxAmount,String outInvoiceAmout,String outStatus,String outBy);

    /**
     *获取税款所属期
     * @param schemaLabel
     * @param gfTaxNo
     * @return
     */
    String  getDqskssq(String schemaLabel,String gfTaxNo);

    /**
     *模糊查询销方名称
     * @param schemaLabel
     * @param queryString
     * @return
     */
    List<String> getXfName(String schemaLabel,String  queryString);

    /**
     *转出窗口待转出信息查询
     * @param schemaLabel
     * @param ids
     * @return
     */
    InvoiceEntity getToOutInformation(String schemaLabel,String  ids);

    /**
     *转出窗口全部转出待转出信息查询
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoiceEntity getToOutInformationAll(String schemaLabel,String id);

    /**
     *获取明细中抵账表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoiceEntity getDetailInfo(String schemaLabel,Long id)throws  Exception;

    /**
     *获取明细中查验表销方购方明细信息
     * @param schemaLabel
     * @param id
     * @return
     */
    InvoiceEntity getCheckDetailInfo(String schemaLabel,Long id)throws  Exception;

    /**
     * 取消转出
     * @param schemaLabel
     * @param id
     * @return
     */
    Boolean cancelTransferOut(String schemaLabel,String[] id);
}
