package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.common.validator.Assert;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;
import com.xforceplus.wapp.modules.base.service.BaseUserService;
import com.xforceplus.wapp.modules.base.service.OrganizationService;
import com.xforceplus.wapp.modules.base.service.UserRoleService;
import com.xforceplus.wapp.modules.base.service.UserTaxnoService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.*;

/**
 * Created by sunny.xu on 4/18/2018.
 * 用户管理控制层
 */
@RestController
public class UserController extends AbstractController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private BaseUserService userService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserTaxnoService userTaxnoService;

    /**
     * 查询用户列表
     */
    @SysLog("用户列表查询")
    @RequestMapping(URI_USER_LIST)
    public R list(UserEntity userEntity) {
        //用户所在分库名
        final String schemaLabel = getCurrentUserSchemaLabel();

        //获取当前页面
        final Integer page = userEntity.getPage();

        //分页查询起始值
        userEntity.setOffset((page - 1) * userEntity.getLimit());

        //获取分库
        userEntity.setSchemaLabel(schemaLabel);

        //所属中心企业
     /*   final String company = userEntity.getCompany();
        if (StringUtils.isNotBlank(company)) {
            //获取子级组织id
            final List<Long> subOrgIds = organizationService.querySubOrgIdList(schemaLabel, company);

            userEntity.setIds(subOrgIds);
        }*/

        List<UserEntity> userEntityList = userService.queryList(schemaLabel, userEntity);

        int total = userService.queryTotal(schemaLabel, userEntity);

        PageUtils pageUtil = new PageUtils(userEntityList, total, userEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询角色用户列表
     */
    @SysLog("角色用户列表查询")
    @RequestMapping(URI_USER_LIST_ROLE)
    public R listRoleUser(@RequestBody UserEntity userEntity) {

        //获取当前页面
        final Integer page = userEntity.getPage();

        //分页查询起始值
        userEntity.setOffset((page - 1) * userEntity.getLimit());

        //分库
        userEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<UserEntity> userEntityList = userService.queryList(getCurrentUserSchemaLabel(), userEntity);

        int total = userService.queryTotal(getCurrentUserSchemaLabel(), userEntity);

        PageUtils pageUtil = new PageUtils(userEntityList, total, userEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据用户id获取用户信息
     */
    @SysLog("用户信息查询")
    @RequestMapping(URI_USER_INFO_GET_BY_ID)
    public R selectSingle(@PathVariable Long userId) {

        return R.ok().put("userInfo", userService.queryObject(getCurrentUserSchemaLabel(), userId));
    }

    /**
     * 保存用户信息
     */
    @SysLog("用户信息保存")
    @RequestMapping(URI_USER_SAVE)
    public R save(@RequestBody UserEntity userEntity) {

        //分库
        userEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        return R.ok(userService.save(getCurrentUserSchemaLabel(), userEntity));
    }

    /**
     * 更新用户信息
     */
    @SysLog("用户信息更新")
    @RequestMapping(URI_USER_UPDATE)
    public R update(@RequestBody UserEntity userEntity) {

        //分库
        userEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        return R.ok(userService.update(getCurrentUserSchemaLabel(), userEntity));
    }

    /**
     * 删除用户信息(批量)
     */
    @SysLog("用户信息删除")
    @RequestMapping(URI_USER_DELETE)
    public R delete(@RequestBody Long[] ids) {

        for (Long id : ids) {

            //删除用户绑定的角色
            final UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserid(Integer.valueOf(String.valueOf(id)));

            userRoleService.delete(getCurrentUserSchemaLabel(), userRoleEntity);

            //解除用户与机构税号的绑定
            userTaxnoService.deleteByUserId(getCurrentUserSchemaLabel(), id);

            //删除用户
            userService.delete(getCurrentUserSchemaLabel(), id);
        }

        return R.ok();
    }

    /**
     * 获取登录的用户信息
     */
    @RequestMapping("/sys/user/info")
    public R info() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("登录的用户信息:{}", getUser().toString());
        }
        //获取登陆人的orgtype
        String orgtype = userService.getOrgtype(getUserId());
        return R.ok().put("user", getUser()).put("orgtype", orgtype);
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @RequestMapping("/sys/user/password")
    public R password(String password, String newPassword) {
        Assert.isBlank(newPassword, "新密码不为能空");

        //更新密码
        int count = userService.modifyPassword(getCurrentUserSchemaLabel(), getUserId(), password, newPassword);
        if (count == 0) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }
}
