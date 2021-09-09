package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CostApplicationDao {

    /**
     * 获取供应商的信息
     * @param id
     * @return
     */
    SettlementEntity getUserInfo(Long id);

    /**
     * 获取供应商的信息
     * @param venderid
     * @return
     */
    SettlementEntity getUserInfoByCode(String venderid);

    /**
     * 获取费用类型
     * @param venderId
     * @return
     */
    List<SelectionOptionEntity> getCostType(@Param("venderId") String venderId, @Param("businessType") String businessType);

    /**
     * 根据发票代码号码查询抵账表发票
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    RecordInvoiceEntity searchInvoice(@Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    /**
     * 验重
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    int checkInvoice(@Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);
    /**
     * 获取购方信息
     * @return
     */
    List<SelectionOptionEntity> getGfInfo();

    /**
     * 获取购方信息
     * @return
     */
    List<SelectionOptionEntity> getGfInfoByStaff(String staff);

    /**
     * 获取税率信息
     * @return
     */
    List<SelectionOptionEntity> getRateOptions();

    /**
     * 获取成本中心信息
     * @return
     */
    List<SelectionOptionEntity> getDeptInfo(String staffNo);

    /**
     * 获取邮箱与工号对应关系
     * @param vendorNo
     * @return
     */
    List<SelectionOptionEntity> getEmail(String vendorNo);

    /**
     * 保存上传文件的信息
     * @param fileEntity
     * @return
     */
    Integer saveFile(SettlementFileEntity fileEntity);

    /**
     * 获取文件信息
     * @param id
     * @return
     */
    SettlementFileEntity getFileInfo(Long id);

    /**
     * 保存费用申请主信息
     * @param settlementEntity
     * @return
     */
    Integer saveSettlement(SettlementEntity settlementEntity);

    /**
     * 保存发票信息到底账表
     * @param invoice
     * @return
     */
    Integer saveSettlementInvoice(@Param("invoice") RecordInvoiceEntity invoice, @Param("costNo") String costNo);

    /**
     * 保存发票信息到发票表
     * @param invoice
     * @return
     */
    Integer saveSettlementInvoice2(@Param("invoice") RecordInvoiceEntity invoice, @Param("costNo") String costNo);

    /**
     * 更新底账表中的费用号
     * @return
     */
    Integer updateSettlementInvoice(@Param("costNo") String costNo, @Param("coverAmount") BigDecimal coverAmount, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    Integer updateRecordInvoice(@Param("invoice") RecordInvoiceEntity invoice, @Param("costNo") String costNo);

    /**
     * 保存费用号与发票的匹配关系
     * @return
     */
    Integer saveSettlementInvoiceMatch(@Param("costNo") String costNo, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    int getRateCount(@Param("invoiceNo") String invoiceNo);

    /**
     * 保存税率明细信息
     * @return
     */
    Integer saveRate(@Param("rate") RateEntity rate, @Param("costNo") String costNo, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    /**
     * 保存费用信息
     * @return
     */
    Integer saveCost(@Param("cost") CostEntity cost, @Param("rateId") Long rateId);

    /**
     * 更新文件路径,匹配费用号
     * @return
     */
    Integer updateFile(@Param("path") String path, @Param("costNo") String costNo, @Param("id") Long id);

    /**
     * 根据jvcode获取购方税号名称
     * @param orgcode
     * @return
     */
    RecordInvoiceEntity getGf(String orgcode);

    /**
     * 根据成本中心获取购方税号
     * @param dept
     * @return
     */
    String getGfTaxNoByDept(String dept);

    /**
     * 根据员工工号获取购方税号
     * @param staffNo
     * @return
     */
    String getGfTaxNoByStaffNo(String staffNo);
}
