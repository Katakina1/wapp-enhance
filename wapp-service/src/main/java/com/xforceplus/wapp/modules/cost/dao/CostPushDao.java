package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.InvoiceRateEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CostPushDao {
    /**
     * 获取主表数据
     * @param costNo
     * @return
     */
    List<SettlementEntity> getMainData(@Param("costNo") String costNo);

    /**
     * 获取已提交,审批中的主数据的实例ID
     * @return
     */
    List<SettlementEntity> getMainInstanceId();

    /**
     * 获取发票税率数据
     * @param costNo
     * @return
     */
    List<InvoiceRateEntity> getInvoiceRate(@Param("costNo")String costNo);

    /**
     * 保存流程实例ID
     * @param costNo
     * @param instanceId
     * @return
     */
    Integer saveInstanceId(@Param("costNo")String costNo, @Param("instanceId")String instanceId);

    /**
     * 保存BPMS费用表id
     * @param id
     * @param instanceId
     * @param bpmsId
     * @return
     */
    Integer saveCostId(@Param("id")Long id, @Param("instanceId")String instanceId, @Param("bpmsId")String bpmsId);

    /**
     * 更新沃尔玛审批状态
     * @param costNo
     * @param status
     * @return
     */
    Integer updateStatus(@Param("costNo")String costNo, @Param("status")String status);

    /**
     * 根据流程实例id获取该流程中产生的费用信息的id
     * @param instanceId
     * @return
     */
    List<CostEntity> getCostId(@Param("instanceId")String instanceId);

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

    /**
     * 根据业务字典表code获取值
     * @param dictCode
     * @return
     */
    String getParamByDictCode(String dictCode);

    List<String> queryInvoicesByCostNo(String costNo);

    void updateRecord2Confirm(@Param("list") List<String> uuids);

    void updateReback(@Param("costNo") String costNo, @Param("msg") String msg);
    //清空抵账表费用号
    void updateRebacks(@Param("costNo") String costNo);

    void deleteSettlementInvice(@Param("costNo") String costNo);

    public String getWinid(@Param("costNo") String costNo);
}
