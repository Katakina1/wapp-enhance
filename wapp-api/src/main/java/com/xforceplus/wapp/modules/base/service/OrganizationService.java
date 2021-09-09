package com.xforceplus.wapp.modules.base.service;

import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/12.
 */
public interface OrganizationService {

    /**
     * 根据机构id查询机构信息
     *
     * @param schemaLabel
     * @param orgid
     * @return
     */
    OrganizationEntity queryObject(String schemaLabel, Long orgid);

    /**
     * 根据条件查询机构信息
     *
     * @param schemaLabel
     * @param org
     * @return
     */
    List<OrganizationEntity> queryList(String schemaLabel, OrganizationEntity org);

    /**
     * 根据条件查询机构记录数
     *
     * @param schemaLabel
     * @param org
     * @return
     */
    int queryTotal(String schemaLabel, OrganizationEntity org);

    /**
     * 保存机构信息
     *
     * @param schemaLabel
     * @param org
     */
    void save(String schemaLabel, OrganizationEntity org);

    /**
     * 更新机构信息
     *
     * @param schemaLabel
     * @param org
     */
    void update(String schemaLabel, OrganizationEntity org);

    /**
     * 根据机构id删除机构信息
     *
     * @param schemaLabel
     * @param orgid
     */
    void delete(String schemaLabel, Long orgid);

    /**
     * 查询子机构ID列表
     *
     * @param parentId 上级机构ID
     */
    List<Long> queryOrgIdList(String schemaLabel, Long parentId);

    /**
     * 获取子机构ID(包含本机构ID)，用于数据过滤
     */
    String getSubOrgIdList(String schemaLabel, Long orgid);

    /**
     * 批量删除
     */
    int deleteBatch(String schemaLabel, Long[] orgids);

    /**
     * 根据company(所属中心企业),获取还未添加的机构信息
     */
    List<OrganizationEntity> getNotAddList(String schemaLabel, List<UserTaxnoEntity> userTaxnoEntities, UserTaxnoEntity userTaxnoEntity, String orgType, String[] orgChildArr);

    /**
     * 根据company(所属中心企业),获取还未添加的机构数
     */
    int getNotAddListTotal(String schemaLabel, List<UserTaxnoEntity> userTaxnoEntities, UserTaxnoEntity userTaxnoEntity, String orgType, String[] orgChildArr);

    /**
     * 根据用户表关联获取对应的机构表信息
     */
    List<OrganizationEntity> getOrgDetail(String schemaLabel, UserTaxnoEntity userTaxnoEntity);

    /**
     * 根据用户表关联获取对应的机构表信息个数
     */
    int getOrgDetailCount(String schemaLabel, UserTaxnoEntity userTaxnoEntity);

    /**
     * 统计组织被授权（绑定用户）的数量
     *
     * @param orgIds 机构id
     * @return 统计结果
     */
    int totalDataAccess(String schemaLabel, Long[] orgIds);

    /**
     * 机构纳税人识别号重名校验（购方企业和销方企业进行校验）
     *
     * @param organizationEntity 机构信息
     * @return true 重名 false 不重名
     */
    Boolean renameCheckTaxNo(OrganizationEntity organizationEntity);

    /**
     * 机构纳税人识别号重名校验（创建中心企业时进行校验）
     *
     * @param organizationEntity 机构信息
     * @return true 重名 false 不重名
     */
    Boolean renameCheckOrglayer(OrganizationEntity organizationEntity);

    /**
     * 查询某一中心企业下机构ID列表
     *
     * @param company 所属中心企业
     */
    List<Long> querySubOrgIdList(String schemaLabel, String company);

}
