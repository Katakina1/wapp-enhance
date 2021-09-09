package com.xforceplus.wapp.modules.cost.service;

import com.xforceplus.wapp.modules.cost.entity.*;
import com.xforceplus.wapp.modules.signin.entity.RecordInvoiceEntity;

import java.util.List;
import java.util.Map;

public interface CostQueryQuitService {
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
    List<SettlementExcelEntity> queryAllList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 查询结算明细
     * @param costNo
     * @return
     */
    List<RecordInvoiceEntity> queryDetail(String costNo);

    /**
     * 结算对比
     * @param costNo
     * @return
     */
    List<ContrastEntity> queryDetails(String costNo);

    /**
     * 查询文件明细列表
     * @param costNo
     * @return
     */
    List<SettlementFileEntity> queryFileDetail(String costNo);

    /**
     * 获取审核状态信息
     * @return
     */
    List<SelectionOptionEntity> getStatusOptions();

    Boolean deleteMsgById(String schemaLabel, String costNo, String instanceId, String epsNo, String refundReason, String refundCode, String belongsTo, String payModel);

    void rebackYf(String costNo, String epsNo);
}
