package com.xforceplus.wapp.modules.enterprise.service;

import com.xforceplus.wapp.modules.enterprise.entity.EnterpriseEntity;

import java.util.List;
import java.util.Map;

/**
 * 企业信息Service接口
 * Created by vito.xing on 2018/4/12
 */
public interface EnterpriseService {

    /**
     * 获取企业信息数据集
     * @param map 查询条件 orgName 公司名称
     *                     taxNo   公司税号
     *                     orgType 机构类型 (0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     *                     isBlack 是否加入黑名单(0-未加入 1-已加入)
     *                     schemaLabel 分库名
     * @return 企业信息数据集
     */
    List<EnterpriseEntity> queryList(Map<String, Object> map);

    /**
     * 获取企业信息数据总数
     * @param map 查询条件 orgName公司名称
     *                      taxNo公司税号
     *                      机构类型 (0-大象慧云;1-中心企业;2-购方虚机构;3-销方虚机构;4-管理机构;5-购方企业;6-购销双方;7-门店;8-销方企业)
     *                     schemaLabel 分库名
     * @return 企业信息数据总数
     */
    Integer queryTotal(Map<String, Object> map);

    /**
     * 获取黑名单企业信息数据集
     * @param map 查询条件 taxName 公司名称
     *                     taxNo   公司税号
     *                     companyType 公司类型
     *                     schemaLabel 分库名
     * @return 企业信息数据集
     */
    List<EnterpriseEntity> queryBlackEnterpriseList(Map<String, Object> map);

    /**
     * 根据企业Id获取企业信息
     * @param enterpriseId 企业Id
     * @return 企业信息
     */
    EnterpriseEntity queryEnterpriseById(String schemaLabel,Long enterpriseId);

    /**
     * 根据企业Id获取黑名单企业信息
     * @param blackEnterpriseId 黑名单企业Id
     * @return 企业信息
     */
    EnterpriseEntity queryBlackEnterpriseById(String schemaLabel,Long blackEnterpriseId);

    /**
     * 获取黑名单企业信息数据总数
     * @param map 查询条件 taxName 公司名称
     *                     taxNo   公司税号
     *                     companyType 公司类型
     *                     schemaLabel 分库名
     * @return 黑名单企业信息数据总数
     */
    Integer queryBlackEnterpriseTotal(Map<String, Object> map);

    /**
     * 批量删除黑名单中的企业 (修改企业信息表中的黑名单字段，设置为0)
     * @param orgIds 勾选中的所有企业
     * @param lastModifyBy 最后修改人
     * @return true-成功 false-失败
     */
    Boolean deleteBatchBlackEnterprise(String schemaLabel,Long[] orgIds, String lastModifyBy);

    /**
     * 新增企业黑名单(企业存在修改状态。企业不存在，添加到企业黑名单表)
     * @param enterpriseEntity 企业信息
     * @return true-成功 false-失败
     */
    Boolean saveBlackEnterprise(String schemaLabel,EnterpriseEntity enterpriseEntity);

    /**
     * 批量从excel文件导入的企业黑名单 (企业存在修改企业黑名单状态为1。企业不存在则添加到企业黑明单表，黑名单状态为1)
     * @param enterpriseList 企业数据集
     * @return 导入成功数量
     */
    Integer saveBatchBlackEnterprise(String schemaLabel,List<EnterpriseEntity> enterpriseList);

    /**
     * 修改企业黑名单信息
     * @param enterpriseEntity 企业信息
     * @return true-成功 false-失败
     */
    Boolean updateBlackEnterprise(String schemaLabel,EnterpriseEntity enterpriseEntity);

    /**
     * 查询税号在企业信息中是否存在
     * @param enterpriseEntity taxNo 纳税人税号
     * @return true-成功 false-失败
     */
    Boolean queryEnterpriseByTaxNo(String schemaLabel,EnterpriseEntity enterpriseEntity);

    /**
     * 查询纳税人名称在企业信息中是否存在
     * @param enterpriseEntity orgName 纳税人名称
     * @return true-成功 false-失败
     */
    Boolean queryEnterpriseByOrgName(String schemaLabel,EnterpriseEntity enterpriseEntity);

}
