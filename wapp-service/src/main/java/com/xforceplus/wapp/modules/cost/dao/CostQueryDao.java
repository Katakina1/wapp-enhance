package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CostQueryDao {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<SettlementEntity> queryList(Map<String, Object> map);

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
     * 获取结算发票列表
     * @param costNo
     * @return
     */
    List<RecordInvoiceEntity> getInvoice(String costNo);

    /**
     * 获取结算发票的税率列表
     * @param costNo
     * @param invoiceCode
     * @param invoiceNo
     * @return
     */
    List<RateEntity> getRate(@Param("costNo")String costNo, @Param("invoiceCode")String invoiceCode, @Param("invoiceNo")String invoiceNo);

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

    int updateUser(@Param("rzuserId")String rzuserId, @Param("lzuserId")String lzuserId);
}
