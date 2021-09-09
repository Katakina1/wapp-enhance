package com.xforceplus.wapp.modules.check.dao;

import com.xforceplus.wapp.modules.check.entity.InvoiceCheckDetailModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckMainModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckModel;
import com.xforceplus.wapp.modules.check.entity.InvoiceCheckVehicleDetailModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Bobby
 * @date 2018/4/19
 * 发票查验数据层
 */
@Mapper
public interface InvoiceCheckMapper {

    /**
     * 查验历史列表
     *
     * @param params
     * @return
     */
    List<InvoiceCheckModel> getInvoiceCheckHistoryList(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 查验历史新增
     *
     * @param model
     * @return
     */
    Integer doInvoiceCheckLogAdd(@Param("schemaLabel") String schemaLabel, @Param("paramsData") InvoiceCheckModel model);

    /**
     * 查验主表-新增
     *
     * @param model
     * @return
     */
    Integer doInvoiceCheckMainAdd(@Param("schemaLabel") String schemaLabel, @Param("paramsData") InvoiceCheckMainModel model);

    /**
     * 查验主表-唯一性检查
     *
     * @param model
     * @return
     */
    String doInvoiceCheckMainUniqueCheck(@Param("schemaLabel") String schemaLabel, @Param("paramsData") InvoiceCheckMainModel model);

    /**
     * 查验明细表-新增
     *
     * @param model
     * @return
     */
    Integer doInvoiceCheckDetailAdd(@Param("schemaLabel") String schemaLabel, @Param("model") InvoiceCheckDetailModel model);

    /**
     * 查验明细-新增 invoiceType =03
     *
     * @param model
     * @return
     */
    Integer doInvoiceCheckVehicleDetailAdd(@Param("schemaLabel") String schemaLabel, @Param("paramsData") InvoiceCheckVehicleDetailModel model);


    /**
     * 查验历史列表
     *
     * @param params
     * @return
     */
    Integer getInvoiceCheckHistoryListCount(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);


    /**
     * 查验历史详情
     *
     * @param params
     * @return
     */
    List<InvoiceCheckModel> getInvoiceCheckHistoryDetail(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 查验历史详情记录数
     *
     * @param params
     * @return
     */
    Integer getInvoiceCheckHistoryDetailCount(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 查验历史删除
     *
     * @param params
     * @return
     */
    Boolean getInvoiceCheckHistoryDelete(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 查验-统计查询
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> getInvoiceStatistics(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 查验-统计查询
     *
     * @param params
     * @return
     */
    Integer getInvoiceStatisticsCount(@Param("schemaLabel") String schemaLabel, @Param("paramsData") Map<String, Object> params);

    /**
     * 根据年月查询查验成功的数量
     * @param schemaLabel 分库标识
     * @param checkDate 查验年月
     * @return 数量
     */
    Integer getInvoiceStatisticsCountByMonth(@Param("schemaLabel") String schemaLabel,@Param("checkDate") String checkDate, @Param("userAccount")String userAccount);


    /**
     * 根据uuid获取底帐表id
     *
     * @param uuid
     * @return id
     */
    Integer getInvoiceIdByUuId(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 发票查验删除
     * @param schemaLabel 分库标识
     * @param uuid 发票代码+发票号码
     * @return 删除成功影响的行数
     */
    Integer deleteCheckInvoice(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 根据发票代码号码查询发票查验人
     * @param schemaLabel 分库标识
     * @param invoiceCode 发票代码
     * @param invoiceNo 发票号码
     * @return 查验人
     */
    String getCheckUser(@Param("schemaLabel") String schemaLabel, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);
}
