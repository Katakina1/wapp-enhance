package com.xforceplus.wapp.modules.transferOut.dao;

/*
 * Created by Intellij IDEA
 * User:Jade.xiao
 * Date:2018/4/11
 * Time:18:34
*/

import com.xforceplus.wapp.modules.sys.dao.SysBaseDao;
import com.xforceplus.wapp.modules.transferOut.entity.InvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InvoiceDao extends SysBaseDao<InvoiceEntity>{

    /**
     * 保存到转出历史
     * @param schemaLabel
     * @param invoice
     * @return
     */
    int saveOutHis(@Param("schemaLabel") String schemaLabel,@Param("invoice")InvoiceEntity invoice);

    InvoiceEntity getTransferOutById(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    /**
     * 根据发票代码号码查询信息
     * @param schemaLabel
     * @return
     */
    InvoiceEntity getTransferOutByInvoice(@Param("schemaLabel") String schemaLabel, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    InvoiceEntity getTransferOutHisById(@Param("schemaLabel") String schemaLabel, @Param("id") Long id);

    List<String> getXfName(@Param("schemaLabel") String schemaLabel,@Param("queryString")String  queryString);

    List<InvoiceEntity> transferOutQuery(@Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);

    int transferOutQueryTotal(@Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);

    List<InvoiceEntity> transferOutedQuery(@Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);

    int transferOutedQueryTotal(@Param("schemaLabel") String schemaLabel,@Param("map")Map<String, Object> map);

    int setTransferOut(@Param("schemaLabel") String schemaLabel,@Param("invoiceEntity")InvoiceEntity invoiceEntity);

    /**
     * 获取已转出总数(指定uuid)
     * @param schemaLabel
     * @param uuid
     * @return
     */
    InvoiceEntity getTotalOutAmount(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);
    /**
     * 更新转出状态及金额
     * @param schemaLabel
     * @param invoiceEntity
     * @return
     */
    int updateOutMain(@Param("schemaLabel") String schemaLabel,@Param("invoiceEntity")InvoiceEntity invoiceEntity);

    String  getDqskssq(@Param("schemaLabel") String schemaLabel,@Param("gfTaxNo")String gfTaxNo);

    InvoiceEntity getToOutInformationAll(@Param("schemaLabel") String schemaLabel,@Param("id")String id);

    InvoiceEntity getToOutInformation(@Param("schemaLabel") String schemaLabel,@Param("id")String id);

    InvoiceEntity getDetailInfo(@Param("schemaLabel") String schemaLabel,@Param("id")Long id);

    int cancelTransferOut(@Param("schemaLabel") String schemaLabel,@Param("id")String[] id);
}
