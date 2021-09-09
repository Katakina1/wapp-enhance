package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.JVStoreEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.entity.UserScanPathEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toffler
 * jvstore
 * */
@Mapper
public interface JVStoreDao {

    /**
     * 根据条件查信息
     * @param query
     * @return
     */
    List<JVStoreEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") JVStoreEntity query);

    /**
     * 根据条件查扫描点总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") JVStoreEntity query);

    /**
     * 删除记录
     * */
    int delete(@Param("schemaLabel") String schemaLabel,@Param("entity") JVStoreEntity query);

    /**
     * 增加数据
     * */
    void save(@Param("schemaLabel") String schemaLabel, @Param("entity") JVStoreEntity entity);

    /**
     * 更新数据
     * */
    void update(@Param("schemaLabel") String schemaLabel, @Param("entity") JVStoreEntity entity);

    /**
     * 查询JV
     * */
    List<String> queryjv();
}
