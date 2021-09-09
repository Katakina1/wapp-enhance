package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.modules.signin.entity.InvoiceScan;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceDataEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
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
     * @param uuid uuid

     * @return 抵账信息
     */
    RecordInvoiceDataEntity getRecordData(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid);

    /**
     * 将数据保存进扫描表
     *
     * @param invoiceScanList 扫描实体集
     * @return 结果
     */
    Integer insertScanInvoice(@Param("schemaLabel") String schemaLabel, @Param("item")InvoiceScan invoiceScanList);

    /**
     * 根据uuid将抵账表中的信息批量更新为（签收状态（已签收）签收方式（导入签收）签收时间（当前时间））
     *
     * @param invoiceScanList 扫描实体集
     * @return 结果
     */
    Integer updateRecordQsStatus(@Param("schemaLabel") String schemaLabel, @Param("item") InvoiceScan invoiceScanList);

    /**
     * 保存发票图片
     *
     * @param imgMap 发票图片map
     * @return 结果
     */
    Integer insertInvoiceImg(@Param("schemaLabel") String schemaLabel, @Param("list") List<Map<String, String>> imgMap);
    
    /**
     * 保存发票图片
     *
     * @param imgMap 发票图片map
     * @return 结果
     */
    Integer insertInvoiceImgforCustomer(@Param("schemaLabel") String schemaLabel, @Param("list") List<Map<String, String>> imgMap);
    /**
     * 保存发票图片
     *
     * @param imgMap 发票图片map
     * @return 结果
     */
    Integer insertInvoiceImgforCustomerOne(@Param("schemaLabel") String schemaLabel, @Param("item") Map<String, String> imgMap);
    
    /**
     * 删除发票图片
     * @param imgMap 发票图片map
     * @return 结果
     */
    Integer deleteInvoiceImg(Map<String, String> params);

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
     * 获取所有购方税号
     *
     * @param schemaLabel 分库标识
     * @return 人员下所有税号
     */
    List<String> getGfTaxNoList(@Param("schemaLabel") String schemaLabel);

    /**
     * 根据uuid查询数据是否在扫描表存在
     * @param schemaLabel 分库标识
     * @param uuid 发票代码 + 发票号码
     * @return 总数 大于0则存在
     */
    InvoiceScan queryCountScan(@Param("schemaLabel") String schemaLabel, @Param("uuid")String uuid);
    
    /**
     * 根据UUID查询已上传图片的user_id
     * */
    Long queryImgUserid(@Param("schemaLabel") String schemaLabel, @Param("uuid")String uuid);

    List<String> getComCode(@Param("gfTaxNo") String gfTaxNo);

    List<String> getOrgCode(@Param("gfTaxNo") String gfTaxNo);


    /**
     * 根据id查询数据在扫描表
     * @param schemaLabel 分库标识
     * @param id id
     * @return 总数 大于0则存在
     */
    RecordInvoiceEntity getRecordInvoiceEntityById(@Param("schemaLabel") String schemaLabel, @Param("id")Long id);

    List<String> getNotesByVendorNbr(@Param("schemaLabel") String schemaLabel, @Param("notes")String notes,  @Param("venderid") String venderid);

    void setDeductible( @Param("schemaLabel") String schemaLabel, @Param("item")RecordInvoiceDataEntity dataEntity);

    void updateFlowType(@Param("schemaLabel") String schemaLabel, @Param("item")RecordInvoiceDataEntity dataEntity);

    List<String> getVenderId(@Param("gfTaxNo")String gfTaxNo, @Param("billtypeCode")String billtypeCode);

    void updateCJV(@Param("schemaLabel") String schemaLabel, @Param("item")RecordInvoiceDataEntity dataEntity);

    void updateScanningSeriano(@Param("schemaLabel") String schemaLabel, @Param("item")RecordInvoiceDataEntity dataEntity);
}
