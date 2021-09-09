package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.base.service.RoleService;
import com.xforceplus.wapp.modules.certification.export.CertificationTemplateExport;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by sunny.xu on 4/16/2018.
 * 机构管理控制层
 */
@RestController
public class OrganizationController extends AbstractController {
    private final static Logger LOGGER = getLogger(OrganizationController.class);
    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private BaseUserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * 查询机构列表
     */
    @SysLog("机构列表查询")
    @RequestMapping(URI_ORG_LIST)
    public R list(OrganizationEntity organizationEntity) {

        //获取当前页面
        final Integer page = organizationEntity.getPage();

        //分页查询起始值
        organizationEntity.setOffset((page - 1) * organizationEntity.getLimit());

        //分库
        organizationEntity.setSchemaLabel(getCurrentUserSchemaLabel());

     /*  //判断是否为总部人员（总部id为1），不必限制
        if(getUser().getOrgid()!= 1L){
            //当前登录人的所属中心企业
            final String company = getUser().getCompany();

            //中心企业下机构ID列表
            final List<Long> ids = organizationService.querySubOrgIdList(getCurrentUserSchemaLabel(), company);

            organizationEntity.setIds(ids);
        }*/

        List<OrganizationEntity> organizationEntityList = organizationService.queryList(getCurrentUserSchemaLabel(), organizationEntity);

        int total = organizationService.queryTotal(getCurrentUserSchemaLabel(), organizationEntity);

        PageUtils pageUtil = new PageUtils(organizationEntityList, total, organizationEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 获取组织树
     */
    @SysLog("组织树获取")
    @RequestMapping(URI_ORG_GET_ORG_TREE)
    public R select(OrganizationEntity organizationEntity) {
        //用户所在分库名
        final String schemaLabel = getCurrentUserSchemaLabel();

        //分库
        organizationEntity.setSchemaLabel(schemaLabel);
        organizationEntity.setType(8);
        //判断是否为总部人员（总部id为1），不必限制
       /* if(getUser().getOrgid()!= 1L){
            //当前登录人的所属中心企业
            final String company = getUser().getCompany();

            //中心企业下机构ID列表
            final List<Long> ids = organizationService.querySubOrgIdList(schemaLabel, company);

            organizationEntity.setIds(ids);
        }*/

        //用来存放机构id
        List<String> idList = Lists.newArrayList();

        if (StringUtils.isNotBlank(organizationEntity.getOrgldStr())) {
            //需要展开的机构id数组
            final String[] expandOrgIdArr = organizationEntity.getOrgldStr().split(",");

            //将数组转成集合
            idList = Arrays.asList(expandOrgIdArr);
        }

        //机构数据
        List<OrganizationEntity> organizationEntityList = organizationService.queryList(schemaLabel, organizationEntity);

        return R.ok().put("orgList", orgTreeGenerator(organizationEntityList, idList));
    }

    /**
     * 根据组织id获取组织信息
     */
    @SysLog("组织信息获取")
    @RequestMapping(URI_ORG_INFO_GET_BY_ID)
    public R selectSingle(@PathVariable Long orgId) {

        return R.ok().put("orgInfo", organizationService.queryObject(getCurrentUserSchemaLabel(), orgId));
    }

    /**
     * 保存机构信息
     */
    @SysLog("机构信息保存")
    @RequestMapping(URI_ORG_SAVE)
    public R save(@RequestBody OrganizationEntity organizationEntity) {
        //分库
        organizationEntity.setSchemaLabel(getCurrentUserSchemaLabel());
        organizationEntity.setIsBlack(0);
        organizationEntity.setCreateBy(getUser().getUsername());

        //保存之前，先进行机构层级代码校验
        final Boolean orglayerCheck = organizationService.renameCheckOrglayer(organizationEntity);

        //如果为true，说明机构层级代码重名，返回错误信息
        if (orglayerCheck) {
            return R.error("总部代码已存在，请核对");
        }

        //保存之前，先进行税号校验
        final Boolean result = organizationService.renameCheckTaxNo(organizationEntity);

        //如果为true，说明税号重名，返回错误信息
        if (result) {
            return R.error("该纳税人信息已存在，请重新录入");
        }
        if("5".equals(organizationEntity.getOrgtype())){
            Integer num = organizationService.getOrgCodeCount(organizationEntity.getSchemaLabel(),organizationEntity.getOrgcode());
            OrganizationEntity oz=organizationService.queryObject(organizationEntity.getSchemaLabel(),organizationEntity.getOrgid());

            if(num>0){
                return R.error("该JV号已存在,请核对");
            }
        }
        organizationService.save(getCurrentUserSchemaLabel(), organizationEntity);
        R r = R.ok();
        r.put("data", organizationEntity);
        return r;
    }

    /**
     * 更新机构信息
     */
    @SysLog("机构信息更新")
    @RequestMapping(URI_ORG_UPDATE)
    public R update(@RequestBody OrganizationEntity organizationEntity) {
        //分库
        organizationEntity.setSchemaLabel(getCurrentUserSchemaLabel());
        organizationEntity.setLastModifyBy(getUser().getUsername());
        //修改之前，先进行税号校验
        final Boolean result = organizationService.renameCheckTaxNo(organizationEntity);

        //如果为true，说明税号重名，返回错误信息
        if (result) {
            return R.error("该纳税人识别号已存在，请重新录入");
        }
        if("5".equals(organizationEntity.getOrgtype())){
            Integer num = organizationService.getOrgCodeCount(organizationEntity.getSchemaLabel(),organizationEntity.getOrgcode());
            OrganizationEntity oz=organizationService.queryObject(organizationEntity.getSchemaLabel(),organizationEntity.getOrgid());

            if(num>0&!organizationEntity.getOrgcode().equals(oz.getOrgcode())){
                return R.error("该JV号已存在,请核对");
            }
        }
        organizationService.update(getCurrentUserSchemaLabel(), organizationEntity);

        return R.ok();
    }

    /**
     * 删除机构信息
     */
    @SysLog("机构信息删除")
    @RequestMapping(URI_ORG_DELETE)
    public R delete(@RequestBody OrganizationEntity organizationEntity) {
        //分库名称
        final String schemaLabel = getCurrentUserSchemaLabel();

        organizationEntity.setSchemaLabel(schemaLabel);

        //获取要删除的组织id
        final List<Long> ids = Arrays.asList(organizationEntity.getOrgIds());

        //判断是否包含总部和中心企业（总部和中心企业不能被删除
        if (ids.contains(1L) || ids.contains(2L)) {
            return R.error("总部和中心企业不能被删除");
        }

        //子部门数量
        final int total = organizationService.queryTotal(schemaLabel, organizationEntity);

        //判断是否有子部门
        if (total > 0) {
            return R.error("请先删除下级机构");
        }

        //员工数量
        final int totalUser = userService.userTotal(schemaLabel, organizationEntity.getOrgIds());

        //判断是否有用户
        if (totalUser > 0) {
            return R.error("请先删除机构下的用户");
        }

        //角色数量
        final int totalRole = roleService.roleTotal(schemaLabel, organizationEntity.getOrgIds());

        //判断是否有角色
        if (totalRole > 0) {
            return R.error("请先删除机构下的角色");
        }

        //组织被授权（绑定用户）的数量
        final int totalDataAccess = organizationService.totalDataAccess(schemaLabel, organizationEntity.getOrgIds());

        //判断是否被授权绑定
        if (totalDataAccess > 0) {
            return R.error("机构已被授权，不能被删除");
        }

        organizationService.deleteBatch(schemaLabel, organizationEntity.getOrgIds());

        return R.ok();
    }

    /**
     * 机构树数据封装
     */
    private List<OrganizationEntity> orgTreeGenerator(final List<OrganizationEntity> organizationEntityList, final List<String> idList) {
        //机构根节点
        OrganizationEntity rootOrg = null;

        //机构集合（有层次）
        List<OrganizationEntity> orgList = Lists.newArrayList();

        //获取根节点
        if (!organizationEntityList.isEmpty()) {
            rootOrg = organizationEntityList.get(0);
        }

        if (null != rootOrg) {
            //获取子级菜单
            final List<OrganizationEntity> firstMenus = getChildren(organizationEntityList, rootOrg.getOrgid());
            //本级菜单
            rootOrg.setIsLeaf(firstMenus.isEmpty());
            //判断是否包含当前机构id，如果包含，该节点树展开
            rootOrg.setOpen(idList.contains(String.valueOf(rootOrg.getOrgid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if (!rootOrg.getIsLeaf()) {
                final List<OrganizationEntity> firstMenusVO = findChildNode(firstMenus, organizationEntityList, idList);
                rootOrg.setChildren(firstMenusVO);
            }

            orgList.add(rootOrg);
        }
        return orgList;
    }

    /**
     * 查找子节点
     */
    private List<OrganizationEntity> findChildNode(List<OrganizationEntity> children, List<OrganizationEntity> organizationEntityList, final List<String> idList) {
        final List<OrganizationEntity> secondMenuVOList = newArrayList();
        for (final OrganizationEntity second : children) {
            List<OrganizationEntity> chird = getChildren(organizationEntityList, second.getOrgid());

            second.setIsLeaf(chird.isEmpty());
            //判断是否包含当前机构id，如果包含，该节点树展开
            second.setOpen(idList.contains(String.valueOf(second.getOrgid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if (!second.getIsLeaf()) {
                final List<OrganizationEntity> threeMenuVOList = findChildNode(chird, organizationEntityList, idList);
                second.setChildren(threeMenuVOList);
            }
            secondMenuVOList.add(second);
        }
        return secondMenuVOList;
    }

    /**
     * 获取子节点
     */
    private List<OrganizationEntity> getChildren(final List<OrganizationEntity> sourceList, final Long parentId) {
        final Collection<OrganizationEntity> transform = Collections2.filter(
                sourceList, organizationEntity -> parentId.equals(organizationEntity.getParentid().longValue()));

        return newArrayList(transform);
    }


    /**
     * 保存菜单
     */
    @SysLog("机构菜单保存")
    @RequestMapping(URI_ORG_MENU_SAVE)
    public R saveOrgMenu(@RequestParam String namestr, @RequestParam Integer orgid) {
        R r = R.ok("保存成功");
        if(namestr==null || namestr.isEmpty()){
            return r;
        }
        //用户所在分库名
        final String schemaLabel = getCurrentUserSchemaLabel();

        organizationService.saveOrgMenu(schemaLabel, namestr, orgid);

        return r;
    }

    @SysLog("导入公司代码")
    @RequestMapping("/base/organization/jvAndStoreImport")
    public Map jvAndStoreImport(@RequestParam("cfile") MultipartFile multipartFile) {
        LOGGER.info("导入公司代码,params {}", multipartFile);
        Map<String,Object> map = organizationService.parseExcel(multipartFile,getUser().getLoginname());
        return map;
    }

    @SysLog("导入JV、门店")
    @RequestMapping("/base/organization/JVImport")
    public Map jvImport(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("导入JV、门店,params {}", multipartFile);
        Map<String,Object> map = organizationService.parseJvAndStoreExcel(multipartFile,getUser().getLoginname());
        return map;
    }

    @SysLog("导入公司代码模板")
    @RequestMapping("/export/companyCodeImportExport")
    public void companyCodeImportExport(HttpServletResponse response) {
        //生成excel
        final CertificationTemplateExport excelView = new CertificationTemplateExport("export/base/companyCodeImport.xlsx");
        excelView.write(response, "导入公司代码模板");
    }
}
