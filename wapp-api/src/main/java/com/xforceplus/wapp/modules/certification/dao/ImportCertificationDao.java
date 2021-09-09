package com.xforceplus.wapp.modules.certification.dao;

import com.xforceplus.wapp.modules.certification.entity.ImportCertificationEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 导入认证数据层
 * @author Colin.hu
 * @date 4/20/2018
 */
@Mapper
public interface ImportCertificationDao {

    /**
     * 根据发票号码，发票代码查询发票是否在抵账表存在
     * @param invoiceNo 发票号码
     * @param invoiceCode 发票代码
     * @return 存在则为1，不存在则为0
     */
    InvoiceCollectionInfo checkInvoiceExist(@Param("schemaLabel")String schemaLabel, @Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode);

    
   /**
    *  根据发票号码，发票代码查询发票税局信息
    * @param schemaLabel
    * @param invoiceNo
    * @param invoiceCode
    * @return
    */
    InvoiceCollectionInfo queryInvoiceInfo(@Param("schemaLabel")String schemaLabel, @Param("invoiceNo") String invoiceNo, @Param("invoiceCode") String invoiceCode);
    
    
    int checkScanPoint(@Param("schemaLabel")String schemaLabel, @Param("userId") String userId, @Param("scanPoint") String scanPoint);
    
    int checkBilltypeCode(@Param("schemaLabel")String schemaLabel, @Param("userId") String userId, @Param("billtypeCode") String billtypeCode);
  
    /**
     * 提交认证，将提交认证的发票状态改为已确认
     * @param entityList 提交认证的发票信息
     * @return 结果
     */
    Integer updateAuthHandleStatus(@Param("schemaLabel")String schemaLabel, @Param("list") List<ImportCertificationEntity> entityList, @Param("userAccount")String userAccount, @Param("userName")String userName);

    /**
     * 获取当前登录人下的税号
     *
     * @param schemaLabel 分库标识
     * @param userId 人员
     * @return 人员下所有税号
     */
    List<String> getTaxNoList(@Param("schemaLabel") String schemaLabel, @Param("userId")Long userId);

    Integer updateAuthHandleStatusAndTaxPeriod(@Param("schemaLabel")String schemaLabel, @Param("entity")ImportCertificationEntity entity, @Param("userAccount")String userAccount, @Param("userName")String userName);
}
