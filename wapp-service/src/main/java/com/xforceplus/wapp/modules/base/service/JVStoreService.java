package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.JVStoreEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface JVStoreService {
    /**
     * 根据条件查询信息
     *
     * @param entity
     * @return
     */
    List<JVStoreEntity> queryList(String schemaLabel, JVStoreEntity entity);
    /**
     * 根据条件查询记录总数
     *
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, JVStoreEntity entity);

    /**
     * 删除所选记录
     * */
    int delete(String schemaLabel,JVStoreEntity entity);
    /**
     * 增加信息
     *
     * @param entity
     */
    void save(String schemaLabel, JVStoreEntity entity);

    /**
     * 增加信息
     *
     * @param entity
     */
    void update(String schemaLabel, JVStoreEntity entity);

    /**
     * 查询所有jvcode
     *
     * @return jvcode s
     */
    List<String> queryjv();
    /**
     * 批量导入数据
     * @param list 数据列表
     * @return 成功数量
     */
    Map saveBatchJVStore(List<JVStoreEntity> list, String userCode, HttpServletResponse response);
}
