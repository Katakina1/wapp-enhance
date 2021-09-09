package com.xforceplus.wapp.modules.cost.service;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CostPushService {
    /**
     * 获取要推送到BPMS的费用数据
     * @param costNo
     * @return
     */
    List<SettlementEntity> getPushData(String costNo);

    /**
     * 保存流程实例ID
     * @param costNo
     * @param instanceId
     * @return
     */
    Integer saveInstanceId(String costNo, String instanceId);

    /**
     * 保存BPMS费用表id
     * @param id
     * @param instanceId
     * @param bpmsId
     * @return
     */
    Integer saveCostId(Long id, String instanceId, String bpmsId);

    /**
     * 获取已提交,审批中的主数据的实例ID
     * @return
     */
    List<SettlementEntity> getMainInstanceId();

    /**
     * 更新沃尔玛审批状态
     * @param costNo
     * @param status
     * @return
     */
    Integer updateStatus(String costNo, String status);

    /**
     * 根据流程实例id获取该流程中产生的费用信息的id
     * @param instanceId
     * @return
     */
    List<CostEntity> getCostId(String instanceId);

    /**
     * 更新主表信息
     * @param entity
     * @return
     */
    Integer updateMain(SettlementEntity entity);

    /**
     * 更新费用信息
     * @param entity
     * @return
     */
    Integer updateCost(CostEntity entity);

    /**
     * 取消发票与费用申请单的匹配关系(审核不通过时)
     * @param costNo
     * @return
     */
    Integer cancelMatch(String costNo);

    void updateRecord2Confirm(String costNo);

    void updateRebackInfo(String costNo, String msg);

    /**
     * 更新底账表中的成本中心
     * @return
     */
    Integer getCostDeptId(String costDeptId, String invoiceCode, String invoiceNo);
    List<RecordInvoiceEntity> queryInvoicesByCostNos(String costNo);


}
