package com.xforceplus.wapp.modules.base.service.impl;

import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.modules.base.dao.OrganizationDao;
import com.xforceplus.wapp.modules.base.entity.DictdetaEntity;
import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.base.export.JvAndStoreImport;
import com.xforceplus.wapp.modules.base.export.OrgInformationImport;
import com.xforceplus.wapp.modules.base.service.DictdetaService;
import com.xforceplus.wapp.modules.base.service.MenuService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.google.common.collect.Lists;
import com.qiniu.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Daily.zhang on 2018/04/12.
 */
@Service("organizationService")
public class OrganizationServiceImpl implements OrganizationService {
    private static final Logger LOGGER= getLogger(OrganizationServiceImpl.class);
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
        org.setCreateTime(new Date());
        organizationDao.save(schemaLabel, org);

        //新增数据f分库
        this.insertDataBase(schemaLabel, org);

        //中心企业 - 创建菜单根节点
        this.createRootMenu(schemaLabel, org);
    }

    @Override
    public void update(String schemaLabel, OrganizationEntity org) {
        org.setLastModifyTime(new Date());
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
        if ("8".equals(orgtype)) {
            return organizationDao.renameCheckTaxNo(organizationEntity.getSchemaLabel(), organizationEntity) > 0;
        }
        if ("5".equals(orgtype)) {
            return organizationDao.countTaxnoAndOrgcode(organizationEntity.getSchemaLabel(), organizationEntity) > 0;
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
    public void saveOrgMenu(String schemaLabel, String namestr, Integer orgid) {
        //去掉尾部逗号
        namestr = namestr.substring(0, namestr.length() - 1);
        String[] levelAndNames = namestr.split(",");
        for (String levelAndName : levelAndNames) {
            String name = levelAndName.substring(1);
            Integer level = Integer.valueOf(levelAndName.substring(0, 1));
            //判断该菜单是否已经存在于当前的orgid下
            Integer count = organizationDao.getMenuByNameAndOrgid(schemaLabel, name, orgid, level);
            //存在就跳过
            if (count > 0) {

            } else {
                //否则添加
                organizationDao.saveOrgMenu(schemaLabel, name, orgid, level);
            }
        }
        //调整pid
        for (String levelAndName : levelAndNames) {
            String name = levelAndName.substring(1);
            Integer level = Integer.valueOf(levelAndName.substring(0, 1));
            if ("首页".equals(name)) {
                continue;
            }
            Long pid = organizationDao.getPid(schemaLabel, name, orgid, level);
            String pname = organizationDao.getPname(schemaLabel, pid);
            Long pid2 = organizationDao.getPid2(schemaLabel, pname, orgid, level);
            if (pid2 != null) {
                organizationDao.fitPid(schemaLabel, name, orgid, level, pid2);
            }
        }
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

    public Integer getOrgCodeCount(String schemaLabel,String orgcode){
        return organizationDao.getOrgCodeCount(schemaLabel,orgcode);
    }



    /**
     * 解析excel数据，解析保存入库
     *导入公司代码
     * @param logingName
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> parseExcel(MultipartFile multipartFile, String logingName) {
        //进入解析excel方法
        final JvAndStoreImport redInvoiceImport = new JvAndStoreImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<OrganizationEntity> organizationEntityList = redInvoiceImport.analysisExcel();
            for (int i=organizationEntityList.size()-1;i>=0;i-- ) {
                String orgcode = organizationEntityList.get(i).getOrgcode();
                String companyCode = organizationEntityList.get(i).getCompanyCode();
                map.put("orgcode",orgcode);
                map.put("companyCode",companyCode);
                if(!orgcode.isEmpty()) {
                    if(!companyCode.isEmpty()) {

                    }else{
                        LOGGER.error("读取excel文件公司代码有空数据");
                        map.put("success", Boolean.FALSE);
                        map.put("reason", "读取excel文件公司代码有空数据！");
                        return map;
                    }
                }else{
                    LOGGER.error("读取excel文件JV有空数据");
                    map.put("success", Boolean.FALSE);
                    map.put("reason", "读取excel文件JV有空数据！");
                    return map;
                }
            }
            for (int d=0;d<organizationEntityList.size()-1;d++) {
                for (int k= organizationEntityList.size() - 1; k >d; k--) {
                    if(organizationEntityList.get(d).getOrgcode().equals(organizationEntityList.get(k).getOrgcode())){
                        organizationEntityList.remove(k);
                    }
                }
            }
            if (!organizationEntityList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<OrganizationEntity>> entityMap =RedInvoiceImportData(organizationEntityList,logingName);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
                if(entityMap.get("errorEntityList").size()>0){
                    map.put("errorlist",entityMap.get("errorEntityList"));
                }
            }else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<OrganizationEntity>> RedInvoiceImportData(List<OrganizationEntity> redInvoiceList,String loginname){
        //返回值
        final Map<String, List<OrganizationEntity>> map = newHashMap();
        //导入成功的数据集
        final List<OrganizationEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<OrganizationEntity> errorEntityList = newArrayList();

        redInvoiceList.forEach(organizationEntity -> {
            String companyCode = organizationEntity.getCompanyCode();
            String orgcode = organizationEntity.getOrgcode();


            if (!orgcode.isEmpty() && !companyCode.isEmpty()) {
                successEntityList.add(organizationEntity);
            } else {
                errorEntityList.add(organizationEntity);
            }
        });
            //设置序列号
            Date de = new Date();
            //如果都校验通过，保存入库
            for(int i=0;i<successEntityList.size();i++){
                if (organizationDao.selectJvAndStoreExists(successEntityList.get(i).getOrgcode()) > 0) {
                    organizationDao.updateCompanyCodeExists(successEntityList.get(i).getOrgcode(), successEntityList.get(i).getCompanyCode(), loginname, de);
                }else {
                    errorEntityList.add(successEntityList.get(i));
                    successEntityList.remove(i);

                }
            }

        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);

        return map;
    }


    /**
     * 解析excel数据，解析保存入库
     *导入JV、门店
     * @param logingName
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> parseJvAndStoreExcel(MultipartFile multipartFile, String logingName) {
        //进入解析excel方法
        final OrgInformationImport redInvoiceImport = new OrgInformationImport(multipartFile);
        final Map<String, Object> map = newHashMap();
        try {
            //读取excel
            final List<OrganizationEntity> organizationEntityList = redInvoiceImport.analysisExcel();
            Date de = new Date();
            for (int i=organizationEntityList.size()-1;i>=0;i-- ) {
                String orgcode = organizationEntityList.get(i).getOrgcode();
                String taxno = organizationEntityList.get(i).getTaxno();
                String orgname=organizationEntityList.get(i).getOrgname();
                if(!orgcode.isEmpty()||!orgname.isEmpty()||!taxno.isEmpty()) {
                }else{
                    LOGGER.error("读取excel文件代码、机构名称、纳税人识别号有空数据");
                    map.put("success", Boolean.FALSE);
                    map.put("reason", "读取excel文件JV有空数据！");
                    return map;
                }
            }
            for (int d=0;d<organizationEntityList.size()-1;d++) {
                for (int k= organizationEntityList.size() - 1; k >d; k--) {
                    if(organizationEntityList.get(d).getOrgcode().equals(organizationEntityList.get(k).getOrgcode())){
                        organizationEntityList.remove(k);
                    }
                }
            }
            if (!organizationEntityList.isEmpty()) {
                map.put("success", Boolean.TRUE);
                Map<String, List<OrganizationEntity>> entityMap =orgInformationImportData(organizationEntityList,logingName);
                map.put("reason", entityMap.get("successEntityList"));
                map.put("errorCount", entityMap.get("errorEntityList").size());
            }else {
                LOGGER.info("读取到excel无数据");
                map.put("success", Boolean.FALSE);
                map.put("reason", "读取到excel无数据！");
            }
        } catch (ExcelException e) {
            LOGGER.error("读取excel文件异常:{}", e);
            map.put("success", Boolean.FALSE);
            map.put("reason", "读取excel文件异常！");
        }
        return map;
    }

    private Map<String, List<OrganizationEntity>> orgInformationImportData(List<OrganizationEntity> redInvoiceList,String loginname){
        //返回值
        final Map<String, List<OrganizationEntity>> map = newHashMap();
        //导入成功的数据集
        final List<OrganizationEntity> successEntityList = newArrayList();
        //导入失败的数据集
        final List<OrganizationEntity> errorEntityList = newArrayList();
        final Map<String, Object> orgInformation = newHashMap();

        redInvoiceList.forEach(organizationEntity -> {
            String orgname = organizationEntity.getOrgname();
            String orgcode = organizationEntity.getOrgcode();
            String taxno=organizationEntity.getTaxno();

            if (!orgcode.isEmpty() && !orgname.isEmpty()&&!taxno.isEmpty()) {
                successEntityList.add(organizationEntity);
            } else {
                errorEntityList.add(organizationEntity);
            }
        });
        if(errorEntityList.size()==0){
            //设置序列号
            Date de = new Date();
            Integer parentid=organizationDao.selectOrgTypeSecondExists();

            //如果都校验通过，保存入库
            for(OrganizationEntity red: successEntityList){
                red.setTaxname(red.getOrgname());
                String orgcode = red.getOrgcode();
                red.setOrgid(null);
                if (!red.getStoreNumber().isEmpty()) {
                    String storeNumber[] = red.getStoreNumber().split("/");
                    if (organizationDao.selectJvAndStoreExists(orgcode) > 0) {
                        organizationDao.updateOrgInformationExists(red,loginname,de);
                        Integer orgid=organizationDao.selectAlreadyOrgSecondExists(orgcode);
                        for (int c = 0; c < storeNumber.length; c++) {
                            if(organizationDao.selectStoreExists(orgid,storeNumber[c])>0){
                                organizationDao.updateJvAndStoreExists(red,loginname,de);
                            }else {
                                red.setStoreNumber(storeNumber[c]);
                                organizationDao.insertJvAndStoreExists(red,orgid,de,loginname);
                            }
                        }
                        //organizationDao.updateOrgInformationExists(red,loginname,de);
                    }else{
                        organizationDao.insertJvExists(red,parentid,de,loginname);
                        Integer orgid=Integer.parseInt(String.valueOf(red.getOrgid()));
                        for (int i = 0; i < storeNumber.length; i++) {
                            //判断是否存在该门店
                            if(organizationDao.selectStoreExists(orgid,storeNumber[i])>0){
                                organizationDao.updateJvAndStoreExists(red,loginname,de);
                            }else{
                                red.setStoreNumber(storeNumber[i]);
                                organizationDao.insertJvAndStoreExists(red,orgid,de,loginname);
                            }
                        }
                    }
                }else{
                    if (organizationDao.selectJvAndStoreExists(orgcode) > 0) {
                        organizationDao.updateOrgInformationExists(red,loginname,de);
                    }else{
                        organizationDao.insertJvExists(red,parentid,de,loginname);
                    }
                }
            }

        }
        map.put("successEntityList", successEntityList);
        map.put("errorEntityList", errorEntityList);

        return map;
    }
}


