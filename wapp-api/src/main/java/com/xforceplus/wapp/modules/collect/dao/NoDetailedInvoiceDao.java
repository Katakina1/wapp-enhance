package com.xforceplus.wapp.modules.collect.dao;

import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import com.xforceplus.wapp.modules.collect.entity.InvoiceDetailInfo;
import com.xforceplus.wapp.modules.collect.entity.RecordInvoiceStatistics;
import com.xforceplus.wapp.modules.signin.entity.GlTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 未补明细发票
 * @author Colin.hu
 * @date 4/11/2018
 */
@Mapper
public interface NoDetailedInvoiceDao {

    /**
     * 获得未补明细发票数据的总行数
     * @param map 查询条件(createDate-采集时间,gfName-购方名称,invoiceNo-发票号码,invoiceType-发票类型)
     * @return 总数
     */
    Integer getNoDetailInvoiceCount(Map<String, Object> map);

    /**
     * 获得未补明细发票集合
     * @param map 查询条件(createDate-采集时间,gfName-购方名称,invoiceNo-发票号码,invoiceType-发票类型)
     * @return 未补明细发票集合
     */
    List<InvoiceCollectionInfo> selectNoDetailedInvoice(Map<String, Object> map);

    /**
     * 保存未补明细（抵账明细表）
     * @param invoiceDetailInfoList 抵账明细
     * @return 执行结果
     */
    Integer insertNoDetailedInvoice(@Param("schemaLabel") String schemaLabel, @Param("list")List<InvoiceDetailInfo> invoiceDetailInfoList);

    /**
     * 保存统计（抵账统计表，相同税率为一条数据）
     * @param recordInvoiceStatisticsList 抵账统计
     * @return 执行结果
     */
    Integer insertStatisticsList(@Param("schemaLabel") String schemaLabel,@Param("list") List<RecordInvoiceStatistics> recordInvoiceStatisticsList);


    /**
     * 更新抵账表
     * @param invoiceCollectionInfo 抵账表实体
     * @return 更新结果
     */
    Integer updateRecordInvoice(InvoiceCollectionInfo invoiceCollectionInfo);

    /**
     * 保存抵账主表 批量
     * @param infoList 主表集
     * @return 结果
     */
    Integer insertRecordInvoice(@Param("schemaLabel") String schemaLabel,@Param("list") List<InvoiceCollectionInfo> infoList);

    /**
     * 保存抵账主表 批量
     * @param infoList 主表集
     * @param cyYoN
     * @return 结果
     */
    Integer insertRecordInvoiceScan(@Param("schemaLabel") String schemaLabel, @Param("item") InvoiceCollectionInfo infoList, @Param("cyYoN") int cyYoN);
    /**
     * 保存抵账主表 批量
     * @param infoList 主表集
     * @return 结果
     */
    Integer insertRecordInvoiceCode(@Param("schemaLabel") String schemaLabel,@Param("list") List<InvoiceCollectionInfo> infoList);

    /**
     * 根据类型查询数据字典表获取对应明细
     * @param params 参数 type
     * @return 参数名和code
     */
    List<Map<String, String>> getParamMapByType(Map<String, String> params);

    /**
     * 获取所有金额，税额合计
     * @param map 参数
     * @return 金额 税额汇总
     */
    Map<String, BigDecimal> getNoDetailSumAmount(Map<String, Object> map);

    /**
     * 插入机动车明细表
     * @param schemaLabel 分表标识
     * @param model 机动车明细集
     * @return 执行结果
     */
    Integer insertVehicleDetailList(@Param("schemaLabel") String schemaLabel, @Param("list") List<InvoiceCheckVehicleDetailModel> model);

    /**
     * 插入前删除明细数据
     * @param schemaLabel
     * @param
     */
    void deleteDetail(@Param("schemaLabel") String schemaLabel, @Param("invoiceDetailInfo") InvoiceDetailInfo invoiceDetailInfo);

    List<String> getDZMXName(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    Integer updateRecordInvoiceScan(@Param("schemaLabel") String schemaLabel, @Param("item") InvoiceCollectionInfo infoList, @Param("cyYoN") int cyYoN);

    Integer selectRecordInvoiceCount(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    List<GlTypeEntity> queryGlTypeList(@Param("glType")String glType);
}
