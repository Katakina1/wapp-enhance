package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface SignininqueryCostQueryDao {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<SettlementEntity> queryList(Map<String, Object> map);

    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<SettlementEntity> queryLists(Map<String, Object> map);

    /**
     * 查询全部数据列表
     * @param map
     * @return
     */
    List<SettlementEntity> queryAllList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCounts(Map<String, Object> map);

    /**
     * 获取结算发票列表
     * @param costNo
     * @return
     */
    List<RecordInvoiceEntity> getInvoice(String costNo);

    /**
     * 获取结算发票对比
     * @param costNo
     * @return
     */
    List<ContrastEntity> getInvoices(String costNo);

    /**
     * 获取结算发票的税率列表
     * @param costNo
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    List<RateEntity> getRate(@Param("costNo") String costNo, @Param("invoiceCode") String invoiceCode, @Param("invoiceNo") String invoiceNo);

    /**
     * 获取结算发票税率的费用列表
     * @param rateId
     * @return
     */
    List<CostEntity> getCost(Long rateId);

    /**
     * 获取结算的文件列表
     * @param costNo
     * @return
     */
    List<SettlementFileEntity> getFile(String costNo);

    /**
     * 获取审核状态信息
     * @return
     */
    List<SelectionOptionEntity> getStatusOptions();


    /**
     * 根据费用号删除扫描表信息
     *
     * @param schemaLabel
     * @param costNo
     * @return
     */
    Boolean deleteInvice(@Param("schemaLabel") String schemaLabel,@Param("costNo") String costNo,@Param("epsNo") String epsNo,@Param("refundReason")String refundReason,@Param("refundCode")String refundCode,@Param("belongsTo")String belongsTo);

    /**
     * 根据费用号删除修改抵账信息
     *
     * @param schemaLabel
     * @param costNo
     * @return
     */
    Boolean deleteInvices(@Param("schemaLabel") String schemaLabel,@Param("costNo") String costNo);


    Boolean deleteMsgById(@Param("schemaLabel")String schemaLabel,@Param("costNo")  String costNo,@Param("refundReason")  String refundReason);

    Boolean deleteMsgByIds(@Param("schemaLabel")String schemaLabel,@Param("costNo")  String costNo);

    List<MatchRebackEntity> searchDetail(@Param("costNo") String costNo);

    String searchMatchDetailAmount(@Param("matchDetailId") String matchDetailId);

    void updateAmountById(@Param("id") String matchDetailId, @Param("bd") BigDecimal bd);

    void updateAmountByUuid(@Param("uuid") String uuid, @Param("invoiceAmount") BigDecimal invoiceAmount);

    String findMatch(@Param("epsNo") String epsNo);

    void updateMatchAmount(@Param("epsNo")String epsNo, @Param("totalAmount")BigDecimal totalAmount);

    void underWay(@Param("costNo") String costNo);
}
