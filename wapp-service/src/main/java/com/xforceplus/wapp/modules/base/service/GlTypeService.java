package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.GlTypeEntity;

import java.util.List;
import java.util.Map;


/**
 * 业务类型
 * <p>
 * Created by jingsong.mao on 2018/08/10.
 */
public interface GlTypeService {

    /**
     * 根据业务类型id查询业务类型信息
     *
     * @param roleId
     * @return
     */
    GlTypeEntity queryObject(String schemaLabel, Long roleId);

    /**
     * 根据条件查询业务类型信息
     *
     * @param entity
     * @return
     */
    List<GlTypeEntity> queryList(String schemaLabel, GlTypeEntity entity);
    List<GlTypeEntity> queryExcelList(Map<String, Object> params);
    /**
     * 根据条件查询业务类型记录数
     *
     * @param entity
     * @return
     */
    int queryTotal(String schemaLabel, GlTypeEntity entity);

    /**
     * 统计组织下的业务类型数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(String schemaLabel, Long[] orgIds);

    /**
     * 保存业务类型信息
     *
     * @param entity
     */
    void save(String schemaLabel, GlTypeEntity entity);

    /**
     * 更新业务类型信息
     *
     * @param entity
     */
    void update(String schemaLabel, GlTypeEntity entity);

    /**
     * 根据业务类型id删除业务类型信息
     *
     * @param roleid
     */
    void delete(String schemaLabel, Long roleid);

    /**
     * 批量删除
     */
    int deleteBatch(String schemaLabel, Long[] roleids);

    /**
     * 根据业务类型名称或编码查询
     * */
    int queryByNameAndCode(String schemaLabel, String name, String code, String account, String type, String glName, Integer id);

    /**
     * 根据名字或编码查询
     * */
    String queryServiceName(String code, String account);


}
