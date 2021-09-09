package com.xforceplus.wapp.modules.cost.service;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;

import java.util.List;
import java.util.Map;

public interface CostTypeService {
    /**
     * 查询分页数据列表
     * @param map
     * @return
     */
    List<CostEntity> queryList(Map<String, Object> map);

    /**
     * 查询列表总数量
     * @param map
     * @return
     */
    Integer queryCount(Map<String, Object> map);
    /**
     * 保存
     * @param entity
     */
    void save( CostEntity entity);

    /**
     * 批量保存
     */
    Integer saveBatch( List<CostEntity> costList);

    /**
     * 修改
     */
    void update( CostEntity entity);

    /**
     * 删除
     */
    void delete( Long id);

    /**
     * 	查询费用编号是否已存在
     */
    int queryCostType(CostEntity entity);

}
