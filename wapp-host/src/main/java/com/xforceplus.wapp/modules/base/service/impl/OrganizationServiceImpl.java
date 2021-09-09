package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.modules.base.dao.OrganizationDao;
import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.base.service.DictdetaService;
import com.xforceplus.wapp.modules.base.service.MenuService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.google.common.collect.Lists;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Daily.zhang on 2018/04/12.
 */
@Service("organizationService")
public class OrganizationServiceImpl implements OrganizationService {

    private static final String STRING_TWO = "2";
    private static final String STRING_ONE = "1";

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private DictdetaService dictdetaService;

    @Autowired
    private MenuService menuService;

    @Override
    public OrganizationEntity queryObject(String schemaLabel, Long orgid) {
        return organizationDao.queryObject(schemaLabel, orgid);
    }

    @Override
    public List<OrganizationEntity> queryList(String schemaLabel, OrganizationEntity org) {
        return organizationDao.queryList(schemaLabel, org);
    }

    @Override
    public int queryTotal(String schemaLabel, OrganizationEntity org) {
        return organizationDao.queryTotal(schemaLabel, org);
    }

    @Override
    public void save(String schemaLabel, OrganizationEntity org) {
        organizationDao.save(schemaLabel, org);

        //新增数据f分库
        this.insertDataBase(schemaLabel, org);

        //中心企业 - 创建菜单根节点
        this.createRootMenu(schemaLabel, org);
    }

    @Override
    public void update(String schemaLabel, OrganizationEntity org) {
        organizationDao.update(schemaLabel, org);

        //新增数据f分库
        this.insertDataBase(schemaLabel, org);
    }

    @Override
    public void delete(String schemaLabel, Long orgid) {
        organizationDao.delete(schemaLabel, orgid);
    }

    @Override
    public List<Long> queryOrgIdList(String schemaLabel, Long parentId) {
        return organizationDao.queryOrgIdList(schemaLabel, parentId);
    }

    @Override
    public int deleteBatch(String schemaLabel, Long[] orgids) {
        return organizationDao.deleteBatch(schemaLabel, orgids);
    }

    @Override
    public List<OrganizationEntity> getNotAddList(String schemaLabel, List<UserTaxnoEntity> userTaxnoEntities, UserTaxnoEntity userTaxnoEntity, String orgType, String[] orgChildArr) {
        return organizationDao.getNotAddList(schemaLabel, userTaxnoEntities, userTaxnoEntity, orgType, orgChildArr);
    }

    @Override
    public int getNotAddListTotal(String schemaLabel, List<UserTaxnoEntity> userTaxnoEntities, UserTaxnoEntity userTaxnoEntity, String orgType, String[] orgChildArr) {
        return organizationDao.getNotAddListTotal(schemaLabel, userTaxnoEntities, userTaxnoEntity, orgType, orgChildArr);
    }

    @Override
    public List<OrganizationEntity> getOrgDetail(String schemaLabel, UserTaxnoEntity userTaxnoEntity) {
        return organizationDao.getOrgDetail(schemaLabel, userTaxnoEntity);
    }

    @Override
    public int getOrgDetailCount(String schemaLabel, UserTaxnoEntity userTaxnoEntity) {
        return organizationDao.getOrgDetailCount(schemaLabel, userTaxnoEntity);
    }

    @Override
    public int totalDataAccess(String schemaLabel, Long[] orgIds) {
        return organizationDao.totalDataAccess(schemaLabel, orgIds);
    }

    @Override
    public Boolean renameCheckTaxNo(OrganizationEntity organizationEntity) {
        //获取机构类型
        final String orgtype = organizationEntity.getOrgtype();

        //判断是否为购方企业/销方企业（5：购方企业  8 销方企业）
        if ("5".equals(orgtype) || "8".equals(orgtype)) {
            return organizationDao.renameCheckTaxNo(organizationEntity.getSchemaLabel(), organizationEntity) > 0;
        }

        return Boolean.FALSE;
    }

    @Override
    public Boolean renameCheckOrglayer(OrganizationEntity organizationEntity) {
        //判断是否为中心企业
        if ("1".equals(organizationEntity.getOrgtype())) {
            return organizationDao.renameCheckOrglayer(organizationEntity.getSchemaLabel(), organizationEntity) > 0;
        }
        return Boolean.FALSE;
    }

    @Override
    public List<Long> querySubOrgIdList(String schemaLabel, String company) {
        return organizationDao.querySubOrgIdList(schemaLabel, company);
    }

    @Override
    public String getSubOrgIdList(String schemaLabel, Long orgid) {
        //机构及子机构ID列表
        List<Long> orgIdList = Lists.newArrayList();

        //获取子机构ID
        List<Long> subIdList = queryOrgIdList(schemaLabel, orgid);
        getOrgTreeList(schemaLabel, subIdList, orgIdList);

        //添加本机构
        orgIdList.add(orgid);

        return StringUtils.join(orgIdList, ",");
    }

    /**
     * 递归
     */
    private void getOrgTreeList(String schemaLabel, List<Long> subIdList, List<Long> orgIdList) {
        for (Long orgtId : subIdList) {
            List<Long> list = queryOrgIdList(schemaLabel, orgtId);
            if (!list.isEmpty()) {
                getOrgTreeList(schemaLabel, list, orgIdList);
            }

            orgIdList.add(orgtId);
        }
    }

    /**
     * 新增数据库连接名称
     */
    private void insertDataBase(String schemaLabel, OrganizationEntity org) {

        //1代表新增
        if (STRING_ONE.equals(org.getIsInsert())) {
            final DictdetaEntity dictdetaEntity = new DictdetaEntity();
            //数据字典主表id
            dictdetaEntity.setDicttype(1583);
            dictdetaEntity.setDictname(org.getLinkName());
            dictdetaEntity.setSortno(STRING_ONE);
            dictdetaEntity.setDictcode("DB_001");
            dictdetaService.save(schemaLabel, dictdetaEntity);
        }
    }

    /**
     * 中心企业 - 创建菜单根节点
     */
    private void createRootMenu(String schemaLabel, OrganizationEntity org) {

        //如果是中心企业，创建菜单根节点，为功能菜单做准备
        if (STRING_ONE.equals(org.getOrgtype())) {

            final MenuEntity menuEntity = new MenuEntity();
            menuEntity.setParentid(0);
            menuEntity.setOrgid(org.getOrgid().intValue());
            menuEntity.setMenuname("首页");
            menuEntity.setMenulabel("首页");
            menuEntity.setMenulevel(0);
            menuEntity.setMenuaction("index.html");
            menuEntity.setIsfunc(STRING_ONE);
            menuEntity.setIsbottom(STRING_TWO);

            menuService.save(schemaLabel, menuEntity);
        }
    }
}
