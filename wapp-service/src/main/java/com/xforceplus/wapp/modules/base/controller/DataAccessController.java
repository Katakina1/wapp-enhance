package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.OrganizationEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserTaxnoEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.base.service.UserTaxnoService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by Mark.chen on 2018/04/18.
 */
@RestController
@RequestMapping("base/dataaccess")
public class DataAccessController extends AbstractController {

    private static final Logger LOGGER = getLogger(DataAccessController.class);

    private final BaseUserService baseUserService;
    private final UserTaxnoService userTaxnoService;
    private final OrganizationService organizationService;

    @Autowired
    public DataAccessController(BaseUserService baseUserService,
                                UserTaxnoService userTaxnoService,
                                OrganizationService organizationService) {
        this.baseUserService = baseUserService;
        this.userTaxnoService = userTaxnoService;
        this.organizationService = organizationService;
    }


    private static final String ORG_TPYE_FIVE = "5";
    private static final String ORG_TPYE_EIGHT = "8";

    /**
     * 用户表 信息
     */
    @SysLog("用户列表查询")
    @RequestMapping("/list")
    public R list(UserEntity userEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前页面
        final Integer page = userEntity.getPage();

        //分页查询起始值
        userEntity.setOffset((page - 1) * userEntity.getLimit());

        userEntity.setOrgtype(ORG_TPYE_EIGHT);

        //判断是否为总部人员（总部id为1），不必限制
        if(getUser().getOrgid()!= 1L){
            //获取当前用户所属中心企业下所有组织id
            final List<Long> subOrgIds = organizationService.querySubOrgIdList(schemaLabel, getUser().getCompany());

            userEntity.setOrgIdStr(StringUtils.join(subOrgIds.iterator(), ","));
        }

        List<UserEntity> list = baseUserService.queryDataAccessList(schemaLabel, userEntity);
        int total = baseUserService.queryDataAccessTotal(schemaLabel, userEntity);

        PageUtils pageUtil = new PageUtils(list, total, userEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据用户表关联获取对应的机构表信息
     */
    @SysLog("购方税号列表查询")
    @RequestMapping("/detailList")
    public R detailList(UserTaxnoEntity userTaxnoEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前页面
        final Integer page = userTaxnoEntity.getPage();

        //分页查询起始值
        userTaxnoEntity.setOffset((page - 1) * userTaxnoEntity.getLimit());

        //子级组织id
        final String orgChildStr = organizationService.getSubOrgIdList(schemaLabel, userTaxnoEntity.getOrgid().longValue());
        userTaxnoEntity.setSubOrgIdStr(orgChildStr);

        final List<OrganizationEntity> organizationEntities = organizationService.getOrgDetail(schemaLabel, userTaxnoEntity);

        final int total = organizationService.getOrgDetailCount(schemaLabel, userTaxnoEntity);

        PageUtils pageUtil = new PageUtils(organizationEntities, total, userTaxnoEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);

    }

    /**
     * 用户税号关联表 删除
     */
    @SysLog("购方税号删除")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        for (Long id :
                ids) {
            userTaxnoService.delete(schemaLabel, id);
        }
        return R.ok();
    }

    /**
     * 根据company(所属中心企业),获取还未添加的机构信息
     */
    @SysLog("未关联的购方税号查询")
    @RequestMapping("/getNotAddList")
    public R getNotAddList(UserTaxnoEntity userTaxnoEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        //已绑定的购方企业
        final List<UserTaxnoEntity> userTaxnoEntities = userTaxnoService.queryList(schemaLabel, userTaxnoEntity);

        //获取当前页面
        final Integer page = userTaxnoEntity.getPage();

        //分页查询起始值
        userTaxnoEntity.setOffset((page - 1) * userTaxnoEntity.getLimit());

        //子级组织id
        final String orgChildStr = organizationService.getSubOrgIdList(schemaLabel, userTaxnoEntity.getParentId().longValue());
        //转换成数组
        final String[] orgChildArr = orgChildStr.split(",");

        List<OrganizationEntity> organizationEntityList = organizationService.getNotAddList(schemaLabel, userTaxnoEntities,
                userTaxnoEntity, ORG_TPYE_FIVE, orgChildArr);

        int total = organizationService.getNotAddListTotal(schemaLabel, userTaxnoEntities, userTaxnoEntity, ORG_TPYE_FIVE, orgChildArr);
        PageUtils pageUtil = new PageUtils(organizationEntityList, total, userTaxnoEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 添加机构信息
     */
    @SysLog("购方税号添加")
    @RequestMapping("/taxnoAdd/{userid}")
    public R getNotAddList(@RequestBody List<UserTaxnoEntity> userTaxnoEntities, @PathVariable("userid") Integer userid) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        for (UserTaxnoEntity userTaxnoEntity :
                userTaxnoEntities) {
            userTaxnoEntity.setUserid(userid);
            userTaxnoService.save(schemaLabel, userTaxnoEntity);
        }
        return R.ok();

    }
}
