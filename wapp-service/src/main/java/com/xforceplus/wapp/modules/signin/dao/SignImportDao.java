package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 导入签收业务层接口
 *
 * @author Colin.hu
 * @date 4/23/2018
 */
@Mapper
public interface SignImportDao {

    /**
     * 根据发票代码，发票号码查询抵账信息
     *
     * @param invoiceCode 发票代码
     * @param invoiceNo   发票号码
     * @return 抵账信息
     */
    RecordInvoiceDataEntity getRecordData(@Param("schemaLabel") String schemaLabel, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    /**
     * 将数据保存进扫描表
     *
     * @param invoiceScanList 扫描实体集
     * @return 结果
     */
    Integer insertScanInvoice(@Param("schemaLabel") String schemaLabel, @Param("list") List<InvoiceScan> invoiceScanList);

    /**
     * 根据uuid将抵账表中的信息批量更新为（签收状态（已签收）签收方式（导入签收）签收时间（当前时间））
     *
     * @param invoiceScanList 扫描实体集
     * @return 结果
     */
    Integer updateRecordQsStatus(@Param("schemaLabel") String schemaLabel, @Param("list") List<InvoiceScan> invoiceScanList);

    /**
     * 保存发票图片
     *
     * @param imgMap 发票图片map
     * @return 结果
     */
    Integer insertInvoiceImg(@Param("schemaLabel") String schemaLabel, @Param("list") List<Map<String, String>> imgMap);

    /**
     * 根据uuid获取发票路径
     *
     * @param params 唯一识别号
     * @return 发票路径
     */
    String getImgPath(Map<String, String> params);

    /**
     * 获取当前登录人下的税号
     *
     * @param schemaLabel 分库标识
     * @param userId 人员
     * @return 人员下所有税号
     */
    List<String> getTaxNoList(@Param("schemaLabel") String schemaLabel, @Param("userId")Long userId);

    /**
     * 根据uuid查询数据是否在扫描表存在
     * @param schemaLabel 分库标识
     * @param uuid 发票代码 + 发票号码
     * @return 总数 大于0则存在
     */
    Integer queryCountScan(@Param("schemaLabel") String schemaLabel, @Param("uuid")String uuid);
}
