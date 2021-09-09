package com.xforceplus.wapp.modules.enterprise.service.impl;

import com.xforceplus.wapp.common.validator.ValidatorUtils;
import com.xforceplus.wapp.modules.enterprise.dao.EnterpriseDao;
import com.xforceplus.wapp.modules.enterprise.entity.EnterpriseEntity;
import com.xforceplus.wapp.modules.enterprise.service.EnterpriseService;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 企业信息Service实现类
 * Created by vito.xing on 2018/4/12
 */
@Service("enterpriseServiceImpl")
public class EnterpriseServiceImpl implements EnterpriseService {

    private static final Logger LOGGER = getLogger(EnterpriseServiceImpl.class);

    private static final String COM_TYPE_COUNTRY = "国家";
    private static final String COM_TYPE_ENTERPRISE = "企业";

    private EnterpriseDao enterpriseDao;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    public EnterpriseServiceImpl(EnterpriseDao enterpriseDao){
        this.enterpriseDao = enterpriseDao;
    }

    @Override
    public List<EnterpriseEntity> queryList(Map<String, Object> map) {
        return enterpriseDao.queryList(map);
    }

    @Override
    public Integer queryTotal(Map<String, Object> map) {
        return enterpriseDao.queryTotal(map);
    }

    @Override
    public List<EnterpriseEntity> queryBlackEnterpriseList(Map<String, Object> map) {
        return enterpriseDao.queryBlackEnterpriseList(map);
    }

    @Override
    public EnterpriseEntity queryEnterpriseById(String schemaLabel,Long enterpriseId) {
        return enterpriseDao.queryEnterpriseById(schemaLabel,enterpriseId);
    }

    @Override
    public EnterpriseEntity queryBlackEnterpriseById(String schemaLabel,Long blackEnterpriseId) {
        return enterpriseDao.queryBlackEnterpriseById(schemaLabel,blackEnterpriseId);
    }

    @Override
    public Integer queryBlackEnterpriseTotal(Map<String, Object> map) {
        return enterpriseDao.queryBlackEnterpriseTotal(map);
    }

    @Override
    @Transactional
    public Boolean deleteBatchBlackEnterprise(String schemaLabel,Long[] orgIds, String lastModifyBy) {
        for(Long orgId : orgIds) {
            enterpriseDao.deleteBlackEnterprise(schemaLabel,orgId,lastModifyBy);
        }
        return Boolean.TRUE;
    }

    @Override
    @Transactional
    public Boolean saveBlackEnterprise(String schemaLabel,EnterpriseEntity enterpriseEntity) {
        ValidatorUtils.validateEntity(enterpriseEntity);
        Boolean result;
        //根据纳税人税号判断企业是否存在，存在则直接修改状态,不存在则保存此企业到黑名单
        Boolean isExist = enterpriseDao.queryEnterpriseByTaxNo(schemaLabel,enterpriseEntity) > 0;

        if (isExist) {
            //修改企业黑名单状态为1
            result = enterpriseDao.updateEnterpriseAsBlack(schemaLabel,enterpriseEntity) > 0;
        } else {
            //根据当前登录账号id获取所属中心企业
            EnterpriseEntity company = enterpriseDao.queryCompanyByUser(schemaLabel,enterpriseEntity);
            //将当前账号所属中心企业赋值到从excel读取的每条数据中
            enterpriseEntity.setCompany(company.getCompany());
            //根据纳税人税号查询不到企业，企业不存在，保存企业信息到黑名单表
            result = enterpriseDao.saveBlackEnterprise(schemaLabel,enterpriseEntity) > 0;
        }

        return result;
    }

    @Override
    @Transactional
    public Integer saveBatchBlackEnterprise(String schemaLabel,List<EnterpriseEntity> enterpriseList) {
        //从excel成功读取的结果数量
        Integer successCount = 0;
        //从excel行数据读取结果
        Boolean result;
        for(EnterpriseEntity enterprise : enterpriseList) {
            //行中纳税人号或纳税人名称为空，则跳过此条，继续下一个循环
            String taxNo = enterprise.getTaxNo();
            String orgName = enterprise.getOrgName();
            if (!(Strings.isNullOrEmpty(taxNo)) && !(Strings.isNullOrEmpty(orgName))) {
                //excel行中单元格数据超过长度限制，则跳过此条，继续下一个循环
                if(!(taxNo.length() > 30) && !(orgName.length() > 50)) {
                    //获取excel行中的公司类型
                    String comType = enterprise.getComType();

                    //为实体类赋值,如果为国家赋值0,如果为企业赋值1
                    if(COM_TYPE_COUNTRY.equals(comType)) {
                        enterprise.setComType("0");
                    } else if(COM_TYPE_ENTERPRISE.equals(comType)) {
                        enterprise.setComType("1");
                    }

                    //公司类型如果不为"国家"或"企业"，跳过此条，继续下一个循环
                    if(COM_TYPE_COUNTRY.equals(comType) || COM_TYPE_ENTERPRISE.equals(comType)) {
                        //根据纳税人税号判断企业是否存在
                        Boolean isExist = enterpriseDao.queryEnterpriseByTaxNo(schemaLabel,enterprise) > 0;

                        if (isExist) {
                            //如果企业已存在，修改企业黑名单状态为1
                            result = enterpriseDao.updateEnterpriseAsBlack(schemaLabel,enterprise) > 0;
                        } else {
                            //根据当前登录账号id获取所属中心企业
                            EnterpriseEntity company = enterpriseDao.queryCompanyByUser(schemaLabel,enterprise);
                            //将当前账号所属中心企业赋值到从excel读取的每条数据中
                            enterprise.setCompany(company.getCompany());
                            //将企业加入黑名单中
                            result = enterpriseDao.saveBlackEnterprise(schemaLabel,enterprise) > 0;
                        }

                        //成功修改或保存企业黑名单,则计数器加1
                        if(result) {
                            ++successCount;
                        }
                    }
                }
            }
        }
        return successCount;
    }

    @Override
    @Transactional
    public Boolean updateBlackEnterprise(String schemaLabel,EnterpriseEntity enterpriseEntity) {
        ValidatorUtils.validateEntity(enterpriseEntity);
        return enterpriseDao.updateBlackEnterprise(schemaLabel,enterpriseEntity) > 0;
    }

    @Override
    public Boolean queryEnterpriseByTaxNo(String schemaLabel,EnterpriseEntity enterpriseEntity) {
        return enterpriseDao.queryEnterpriseByTaxNo(schemaLabel,enterpriseEntity) > 0;
    }

    @Override
    public Boolean queryEnterpriseByOrgName(String schemaLabel,EnterpriseEntity enterpriseEntity) {
        return enterpriseDao.queryEnterpriseByOrgName(schemaLabel,enterpriseEntity) > 0;
    }
}
