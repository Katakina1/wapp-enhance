package com.xforceplus.wapp.modules.signin.dao;

import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.modules.cost.entity.InvoiceRateEntity;
import com.xforceplus.wapp.modules.job.entity.SettlementEntity;
import com.xforceplus.wapp.modules.redInvoiceManager.entity.RedInvoiceData;
import com.xforceplus.wapp.modules.redTicket.entity.RedTicketMatch;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * CreateBy leal.liang on 2018/4/18.
 **/
@Mapper
public interface SignInqueryCostDao {
    /**
     * 根据登录人的id获取关联的税号--页面购方税号下拉框数据
     * @param schemaLabel
     * @param userId
     * @return List
     */
    List<OptionEntity> searchGf(@Param("schemaLabel") String schemaLabel,@Param("userId") Long userId);

    /**
     * 根据条件统计所有的数据总数
     * @param schemaLabel
     * @param query
     * @return int
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 获取所有金额之和，所有税额之和
     * @param query 参数
     * @return 金额之和，税额之和
     */
    Map<String, BigDecimal> getSumAmount(@Param("schemaLabel") String schemaLabel, @Param("query") Query query);


    /**
     * 根据条件数据查询扫描表所有符合条件分页数据
     * @param schemaLabel
     * @param query
     * @return List
     */
    List<RecordInvoiceEntity> getRecordIncoiceList(@Param("schemaLabel") String schemaLabel,@Param("query") Query query);

    /**
     * 根据条件数据查询扫描表所有符合条件数据（导出使用）
     * @param schemaLabel
     * @param params
     * @return List
     */
    List<RecordInvoiceEntity> queryAllList(@Param("schemaLabel") String schemaLabel,@Param("query") Map<String, Object> params);

    Integer getMatchid(@Param("schemaLabel")String schemaLabel,@Param("uuid") String uuid);

    SettlementEntity getcostNo(@Param("schemaLabel")String schemaLabel, @Param("invoiceCode")String invoiceCode, @Param("invoiceNo")String invoiceNo);

    List<RedTicketMatch> selectOuterRedInvoice(@Param("schemaLabel")String schemaLabel, @Param("invoiceCode")String invoiceCode, @Param("invoiceNo")String invoiceNo);

    String getSerialNumber(@Param("schemaLabel")String schemaLabel, @Param("invoiceCode")String invoiceCode, @Param("invoiceNo")String invoiceNo);

    RedInvoiceData getRedInvoiceData(@Param("schemaLabel")String schemaLabel, @Param("serialNumber")String serialNumber);

    Integer selectInvoiceCount(@Param("schemaLabel") String schemaLabel, @Param("uuid") String uuid,@Param("id") String id,@Param("invoiceDate") String invoiceDate ,@Param("invoiceAmount") String invoiceAmoun);
    
    int deleteInvoiceDate(@Param("costNo")String costNo);
    
    int updateRecordScanMatchStatus(@Param("uuid")String uuid,@Param("scanMatchStatus")String scanMatchStatus);
    
    int updateSettlementScanMatchStatus(@Param("costNo")String costNo,@Param("scanMatchStatus")String scanMatchStatus);

    int deleteScanInvoice(@Param("schemaLabel") String schemaLabel, @Param("scanId") String scanId);

    int selectInvoice(@Param("costNo") String costNo);

    int updateInvoice(@Param("costNo") String costNo,@Param("noDeduction")String noDeduction);

    List<InvoiceRateEntity> updateRecord(String costNo);

    void underWay(@Param("costNo") String costNo);

    int checkInvoiceZP(@Param("costNo") String costNo);

}
