package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;
import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;
import com.xforceplus.wapp.modules.base.service.RoleMenuService;
import com.xforceplus.wapp.modules.base.service.UserRoleService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_PERMISSION_ASSIGN_ROLE_MENU;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_PERMISSION_ASSIGN_ROLE_USER_SAVE;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_PERMISSION_ASSIGN_QUERY_MENUID_LIST;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_PERMISSION_ASSIGN_ROLE_USER_DELETE;

/**
 * Created by Sunny.xu on 4/21/2018.
 * 权限管理控制层
 */
@RestController
public class PermissionAssignController extends AbstractController {

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserRoleService userRoleService;

    /**
     * 保存角色菜单关联信息
     */
    @SysLog("角色菜单关联保存")
    @RequestMapping(URI_PERMISSION_ASSIGN_ROLE_MENU)
    public R saveRoleMenu(@RequestBody RoleMenuEntity roleMenuEntity) {

        roleMenuService.saveOrUpdate(getCurrentUserSchemaLabel(), roleMenuEntity);

        return R.ok();
    }

    /**
     * 保存角色用户关联信息
     */
    @SysLog("角色用户关联保存")
    @RequestMapping(URI_PERMISSION_ASSIGN_ROLE_USER_SAVE)
    public R saveRoleUser(@RequestBody UserRoleEntity userRoleEntity) {

        userRoleService.saveRoleUser(getCurrentUserSchemaLabel(), userRoleEntity);

        return R.ok();
    }

    /**
     * 根据角色ID，获取菜单ID列表
     */
    @SysLog("获取菜单ID列表")
    @RequestMapping(URI_PERMISSION_ASSIGN_QUERY_MENUID_LIST)
    public R queryMenuIdList(@PathVariable Long roleId) {

        return R.ok().put("menuIdList", roleMenuService.queryMenuIdList(getCurrentUserSchemaLabel(), roleId));
    }

    /**
     * 角色用户关联解绑
     */
    @SysLog("删除角色用户关联")
    @RequestMapping(URI_PERMISSION_ASSIGN_ROLE_USER_DELETE)
    public R deleteRoleUser(@RequestBody UserRoleEntity userRoleEntity) {

        userRoleService.delete(getCurrentUserSchemaLabel(), userRoleEntity);

        return R.ok();
    }

}
