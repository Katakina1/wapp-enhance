package com.xforceplus.wapp.modules.base.dao;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/12.
 */
@Mapper
public interface OrganizationDao {

    /**
     * 根据id查询机构
     *
     * @param orgid
     * @return
     */
    OrganizationEntity queryObject(@Param("schemaLabel") String schemaLabel, @Param("orgid") Long orgid);

    /**
     * 根据条件查询机构
     *
     * @param query
     * @return
     */
    List<OrganizationEntity> queryList(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity query);

    int queryTotal(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity org);

    /**
     * 保存机构信息
     *
     * @param
     */
    int save(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity entity);

    /**
     * 更新机构信息
     */
    int update(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity entity);

    /**
     * 根据机构id删除机构信息
     *
     * @param orgid
     * @return
     */
    int delete(@Param("schemaLabel") String schemaLabel, @Param("orgid") Long orgid);

    /**
     * 批量删除机构信息
     *
     * @param ids
     * @return
     */
    int deleteBatch(@Param("schemaLabel") String schemaLabel, @Param("ids") Long[] ids);

    /**
     * 查询子机构ID列表
     *
     * @param parentId 上级组织机构ID
     */
    List<Long> queryOrgIdList(@Param("schemaLabel") String schemaLabel, @Param("orgid") Long parentId);

    List<OrganizationEntity> getNotAddList(@Param("schemaLabel") String schemaLabel, @Param("userTaxnoEntities") List<UserTaxnoEntity> userTaxnoEntities, @Param("userTaxnoEntity") UserTaxnoEntity userTaxnoEntity, @Param("orgType") String orgType, @Param("orgChildArr") String[] orgChildArr);

    int getNotAddListTotal(@Param("schemaLabel") String schemaLabel, @Param("userTaxnoEntities") List<UserTaxnoEntity> userTaxnoEntities, @Param("userTaxnoEntity") UserTaxnoEntity userTaxnoEntity, @Param("orgType") String orgType, @Param("orgChildArr") String[] orgChildArr);

    List<OrganizationEntity> getOrgDetail(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity userTaxnoEntity);

    int getOrgDetailCount(@Param("schemaLabel") String schemaLabel, @Param("entity") UserTaxnoEntity userTaxnoEntity);

    /**
     * 统计组织被授权（绑定用户）的数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int totalDataAccess(@Param("schemaLabel") String schemaLabel, @Param("orgIds") Long[] orgIds);

    /**
     * 机构纳税人识别号重名校验（目前只对同一中心企业下的购方企业和销方企业进行校验）
     */
    int renameCheckTaxNo(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity entity);

    /**
     * 机构纳税人识别号重名校验（创建中心企业时进行校验）
     *
     */
    int renameCheckOrglayer(@Param("schemaLabel") String schemaLabel, @Param("entity") OrganizationEntity entity);

    /**
     * 查询某一中心企业下机构ID列表
     *
     * @param company 所属中心企业
     */
    List<Long> querySubOrgIdList(@Param("schemaLabel") String schemaLabel, @Param("company") String company);

}
