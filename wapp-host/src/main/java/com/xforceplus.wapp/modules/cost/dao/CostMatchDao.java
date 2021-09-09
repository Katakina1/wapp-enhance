package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.entity.RecordInvoiceEntity;
import com.xforceplus.wapp.modules.cost.entity.SettlementMatchEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CostMatchDao {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<SettlementMatchEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);

    /**
     * 查询明细
     * @param costNo
     * @return
     */
    List<CostEntity> queryDetail(String costNo);

    /**
     * 查询明细
     * @param costNoArray
     * @return
     */
    List<CostEntity> querySelectDetail(String[] costNoArray);

    /**
     * 更新费用明细的冲销金额
     * @param cost
     * @return
     */
    Integer updateCostAmount(CostEntity cost);

    /**
     * 更新总费用的冲销金额
     */
    Integer updateSettlementAmount(CostEntity cost);

    /**
     * 获取符合条件的发票,用于验证发票是否重复
     * @param entity
     * @return
     */
    int selectInvoice(RecordInvoiceEntity entity);
}
