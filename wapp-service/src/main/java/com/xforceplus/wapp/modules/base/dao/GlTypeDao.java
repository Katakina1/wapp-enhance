package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.GlTypeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 业务类型管理
 *
 * Created by jingsong.mao on 2018/08/10.
 */
@Mapper
public interface GlTypeDao {

    /**
     * 保存业务类型信息
     * @param entity
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") GlTypeEntity entity);

    /**
     * 更新业务类型信息
     * @param entity
     * @return
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") GlTypeEntity entity);

    /**
     * 根据业务类型id删除业务类型
     * @param roleid
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long roleid);

    /**
     * 批量删除业务类型
     * @param ids
     * @return
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids);

    /**
     *根据业务类型id查业务类型信息
     * @param id
     * @return
     */
    GlTypeEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("roleid") Long id);

    /**
     * 根据条件查业务类型信息
     * @param query
     * @return
     */
    List<GlTypeEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") GlTypeEntity query);
    List<GlTypeEntity> queryExcelList(@Param("entity") Map<String, Object> params);

    /**
     * 根据条件查业务类型总数
     * @param query
     * @return
     */
    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") GlTypeEntity query);

    /**
     * 统计组织下的业务类型数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int roleTotal(@Param("schemaLabel") String schemaLabel, @Param("orgIds") Long[] orgIds);

    /**
     * 根据名字或编码查询
     * */
    int queryByNameAndCode(@Param("schemaLabel") String schemaLabel, @Param("remark") String name, @Param("matchName1") String code, @Param("matchName2") String account,@Param("glType") String type,@Param("glName") String glName, @Param("id") Integer id);
    /**
     * 根据名字或编码查询
     * */
    String queryServiceName(@Param("code") String code, @Param("account") String account);
}
