package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
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
@RequestMapping("base/billaccess")
public class BillAccessController extends AbstractController {

    private static final Logger LOGGER = getLogger(BillAccessController.class);

    private final BaseUserService baseUserService;
    private final UserBilltypeService userBilltypeService;
    private final OrganizationService organizationService;

    @Autowired
    public BillAccessController(BaseUserService baseUserService,
                                UserBilltypeService userBilltypeService,
                                OrganizationService organizationService) {
        this.baseUserService = baseUserService;
        this.userBilltypeService = userBilltypeService;
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
    @SysLog("业务类型列表查询")
    @RequestMapping("/detailList")
    public R detailList(UserBilltypeEntity userBilltypeEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //获取当前页面
        final Integer page = userBilltypeEntity.getPage();

        //分页查询起始值
        userBilltypeEntity.setOffset((page - 1) * userBilltypeEntity.getLimit());

        //子级组织id
        final String orgChildStr = organizationService.getSubOrgIdList(schemaLabel, userBilltypeEntity.getOrgid().longValue());
        userBilltypeEntity.setSubOrgIdStr(orgChildStr);

        final List<UserBilltypeEntity> Entities = userBilltypeService.getOrgDetail(schemaLabel, userBilltypeEntity);

        final int total = userBilltypeService.getOrgDetailCount(schemaLabel, userBilltypeEntity);

        PageUtils pageUtil = new PageUtils(Entities, total, userBilltypeEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);

    }

    /**
     * 业务类型关联表 删除
     */
    @SysLog("业务类型删除")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        for (Long id :
                ids) {
            userBilltypeService.delete(schemaLabel, id);
        }
        return R.ok();
    }

    
    /**
     * 设置用户默认的业务类型
     */
    @SysLog("设置用户默认的业务类型")
    @RequestMapping("/setdefault")
    public R setdefault(@RequestBody Long[] ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        
        UserBilltypeEntity entity = new UserBilltypeEntity();
        for (Long id :
            ids) {
        	 entity.setId(id.intValue());
             entity.setIsdefault("是");
             userBilltypeService.updateSome(schemaLabel, entity);
             userBilltypeService.update(schemaLabel, entity);
       }
        
        return R.ok();
    }
    
    /**
     * 设置用户默认的业务类型
     */
    @SysLog("取消设置用户默认的业务类型")
    @RequestMapping("/cancleSetdefault")
    public R cancleSetdefault(@RequestBody Long[] ids) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        
        UserBilltypeEntity entity = new UserBilltypeEntity();
        for (Long id :
            ids) {
        	 entity.setId(id.intValue());
             entity.setIsdefault("");
             userBilltypeService.update(schemaLabel, entity);
       }
       return R.ok();
    }

    /**
     * 根据company(所属中心企业),获取还未添加的机构信息
     */
    @SysLog("未关联的业务类型查询")
    @RequestMapping("/getNotAddList")
    public R getNotAddList(UserBilltypeEntity userBilltypeEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        //已绑定的购方企业
        final List<UserBilltypeEntity> userBilltypeEntities = userBilltypeService.queryList(schemaLabel, userBilltypeEntity);

        String billTypeid = "";
       
        for(UserBilltypeEntity entity:userBilltypeEntities)
        {  
        	billTypeid +=(entity.getBilltypeid()+",");
	       
        }
        String[] billtypeidArr = billTypeid.split(",") ;
        
        //获取当前页面
        final Integer page = userBilltypeEntity.getPage();

        //分页查询起始值
        userBilltypeEntity.setOffset((page - 1) * userBilltypeEntity.getLimit());

        //子级组织id
        final String orgChildStr = organizationService.getSubOrgIdList(schemaLabel, userBilltypeEntity.getParentId().longValue());
        //转换成数组
        final String[] orgChildArr = orgChildStr.split(",");

        List<UserBilltypeEntity> organizationEntityList = userBilltypeService.getNotAddList(schemaLabel, billtypeidArr,
                userBilltypeEntity, ORG_TPYE_FIVE, orgChildArr);

        int total = userBilltypeService.getNotAddListTotal(schemaLabel, billtypeidArr,
                userBilltypeEntity, ORG_TPYE_FIVE, orgChildArr);
       
        PageUtils pageUtil = new PageUtils(organizationEntityList, total, userBilltypeEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 添加业务类型
     */
    @SysLog("业务类型添加")
    @RequestMapping("/billtypeAdd/{userid}")
    public R getNotAddList(@RequestBody List<UserBilltypeEntity> userBilltypeEntities, @PathVariable("userid") Integer userid) {

        final String schemaLabel = getCurrentUserSchemaLabel();
        for (UserBilltypeEntity userBilltypeEntity :
                userBilltypeEntities) {
            userBilltypeEntity.setUserid(userid);
            userBilltypeService.save(schemaLabel, userBilltypeEntity);
        }
        return R.ok();

    }
}
