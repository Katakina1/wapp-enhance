package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDetailEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/12.
 **/
@Mapper
public interface HandWorkRepository {

    /**
     * 查询结果总数
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    Integer getInvoiceTotal(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 查询符合条件的结果集
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> selectInvoiceList(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 查询符合条件的所有数据 --导出使用
     *
     * @param schemaLabel
     * @param query
     * @return
     */
    List<RecordInvoiceEntity> queryList(@Param("schemaLabel") String schemaLabel,@Param("query") Map<String, Object> query);

    /**
     * 根据id进行签收
     *
     * @param schemaLabel
     * @param id
     * @return
     */
    Boolean receiptInvoice(@Param("schemaLabel") String schemaLabel,@Param("id")  Long id);

    /**
     * 获取详细发票信息
     *
     * @param schemaLabel
     * @param invoiceNo
     * @return
     */
    RecordInvoiceDetailEntity getInvoiceDetail(@Param("schemaLabel") String schemaLabel,@Param("invoiceNo") String invoiceNo);

    /**
     * 根据登录人获取购方名称和税号
     *
     * @param schemaLabel
     * @param userId
     * @return
     */
    List<OptionEntity> searchGf(@Param("schemaLabel") String schemaLabel,@Param("userId") Long userId);

    /**
     * 保存信息到扫描表
     * @param schemaLabel
     * @param r
     */
    Boolean saveData(@Param("schemaLabel") String schemaLabel,@Param("r")  RecordInvoiceDataEntity r);

    /**
     * 根据id获取插入扫描表的数据
     *
     * @param schemaLabel
     * @param id
     * @return RecordInvoiceDataEntity
     */
    RecordInvoiceDataEntity getInvoiceDataById(@Param("schemaLabel") String schemaLabel,@Param("id")  Long id);


}
