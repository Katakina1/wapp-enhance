package com.xforceplus.wapp.modules.enterprise.dao;

import com.xforceplus.wapp.modules.enterprise.entity.EnterpriseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 企业信息Dao
 * Created by vito.xing on 2018/4/12
 */
@Mapper
public interface EnterpriseDao{

    /**
     * 批量删除黑名单中的企业 (修改企业信息表中的黑名单字段，设置为0)
     * @param orgId 企业id
     * @param schemaLabel 分库名
     * @param lastModifyBy 修改人
     * @return 修改条数
     */
    Integer deleteBlackEnterprise(@Param("schemaLabel") String schemaLabel,@Param("orgId") Long orgId,@Param("lastModifyBy") String lastModifyBy);

    /**
     * 新增企业黑名单(企业存在修改状态。企业不存在，添加到企业黑名单表)
     * @param enterpriseEntity 企业信息
     * @param schemaLabel 分库名
     * @return 保存条数
     */
    Integer saveBlackEnterprise(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);

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
     * @param schemaLabel 分库名
     * @return 企业信息
     */
    EnterpriseEntity queryEnterpriseById(@Param("schemaLabel") String schemaLabel,@Param("enterpriseId")Long enterpriseId);

    /**
     * 根据当前登录人id获取所属中心企业
     * @param schemaLabel 分库名
     * @param enterpriseEntity userId 登录人id
     * @return 所属中心企业
     */
    EnterpriseEntity queryCompanyByUser(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);
    /**
     * 根据企业Id获取黑名单企业信息
     * @param blackEnterpriseId 黑名单企业Id
     * @param schemaLabel 分库名
     * @return 企业信息
     */
    EnterpriseEntity queryBlackEnterpriseById(@Param("schemaLabel") String schemaLabel,@Param("blackEnterpriseId") Long blackEnterpriseId);

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
     * 查询税号在企业信息中是否存在
     * @param enterpriseEntity taxNo 纳税人税号
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryEnterpriseByTaxNo(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);

    /**
     * 查询纳税人名称在企业信息中是否存在
     * @param enterpriseEntity orgName 纳税人名称
     * @param schemaLabel 分库名
     * @return 匹配数量
     */
    Integer queryEnterpriseByOrgName(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);

    /**
     * 修改企业黑名单信息
     * @param enterpriseEntity 企业信息
     * @param schemaLabel 分库名
     * @return 修改条数
     */
    Integer updateBlackEnterprise(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);

    /**
     * 根据纳税人税号查询企业,修改企业信息黑名单状态为1 (将企业加入黑名单)
     * @param enterpriseEntity taxNo 纳税人税号
     * @param schemaLabel 分库名
     * @return 修改条数
     */
    Integer updateEnterpriseAsBlack(@Param("schemaLabel") String schemaLabel,@Param("enterprise") EnterpriseEntity enterpriseEntity);

    /**
     *  获取企业信息列表
     * @param map  schemaLabel 分库名
     *                 orgType 企业类型(5-购方企业，8-销方企业)
     *             orgName 企业名称
     *             taxNo 企业税号
     *             isBlack 是否在黑名单(1-在黑名单，0-不在黑名单)
     *             comType 企业类型 (0-国家,1-企业)
     * @return
     */
    List<EnterpriseEntity> queryList(Map<String, Object> map);

    /**
     * 获取企业信息数量
     * @param map   schemaLabel 分库名
     *              orgType 企业类型(5-购方企业，8-销方企业)
     *             orgName 企业名称
     *             taxNo 企业税号
     *             isBlack 是否在黑名单(1-在黑名单，0-不在黑名单)
     *             comType 企业类型 (0-国家,1-企业)
     * @return 总数量
     */
    Integer queryTotal(Map<String, Object> map);
}
