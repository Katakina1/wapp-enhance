package com.xforceplus.wapp.modules.cost.dao;

import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CostTypeDao {
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
     * 	保存费用类型信息
     */
    int save( @Param("entity") CostEntity entity);

    /**
     * 	更新费用类型信息
     */
    int update(@Param("entity") CostEntity entity);

    /**
     * 	删除费用类型信息
     */
    int delete( @Param("id") Long id);

    /**
     * 	删除费用类型根据供应商号和费用编号
     */
    int deleteByCostAndVenderId( @Param("venderId") String venderId,@Param("costType") String costType);

    /**
     * 	查询费用编号是否已存在
     */
    int queryCostType( @Param("entity") CostEntity entity);

    /**
     * 	查询费用编号和供应商号是否已存在
     */
    int queryCostTypeAndVenderId( @Param("entity") CostEntity entity);
}
