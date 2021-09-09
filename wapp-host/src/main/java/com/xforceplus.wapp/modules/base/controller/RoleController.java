package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.RoleEntity;
import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;
import com.xforceplus.wapp.modules.base.entity.UserRoleEntity;
import com.xforceplus.wapp.modules.base.service.RoleMenuService;
import com.xforceplus.wapp.modules.base.service.RoleService;
import com.xforceplus.wapp.modules.base.service.UserRoleService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_ROLE_LIST;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_ROLE_INFO_GET_BY_ID;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_ROLE_SAVE;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_ROLE_UPDATE;
import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.URI_ROLE_DELETE;

/**
 * Created by Sunny.xu on 4/19/2018.
 * 角色管理控制层
 */
@RestController
public class RoleController extends AbstractController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleMenuService roleMenuService;

    /**
     * 查询用户列表
     */
    @SysLog("角色列表查询")
    @RequestMapping(URI_ROLE_LIST)
    public R list(RoleEntity roleEntity) {

        //获取当前页面
        final Integer page = roleEntity.getPage();

        //分页查询起始值
        roleEntity.setOffset((page - 1) * roleEntity.getLimit());

        //分库
        roleEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<RoleEntity> roleEntityList = roleService.queryList(getCurrentUserSchemaLabel(), roleEntity);

        int total = roleService.queryTotal(getCurrentUserSchemaLabel(), roleEntity);

        PageUtils pageUtil = new PageUtils(roleEntityList, total, roleEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 根据角色id获取角色信息
     */
    @SysLog("角色信息查询")
    @RequestMapping(URI_ROLE_INFO_GET_BY_ID)
    public R selectSingle(@PathVariable Long roleId) {

        return R.ok().put("roleInfo", roleService.queryObject(getCurrentUserSchemaLabel(), roleId));
    }

    /**
     * 更新保存信息
     */
    @SysLog("角色信息保存")
    @RequestMapping(URI_ROLE_SAVE)
    public R save(@RequestBody RoleEntity roleEntity) {

        //分库
        roleEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        roleService.save(getCurrentUserSchemaLabel(), roleEntity);

        return R.ok();
    }

    /**
     * 更新角色信息
     */
    @SysLog("角色信息更新")
    @RequestMapping(URI_ROLE_UPDATE)
    public R update(@RequestBody RoleEntity roleEntity) {

        //分库
        roleEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        roleService.update(getCurrentUserSchemaLabel(), roleEntity);

        return R.ok();
    }

    /**
     * 删除角色信息(批量)
     */
    @SysLog("角色信息删除")
    @RequestMapping(URI_ROLE_DELETE)
    public R delete(@RequestBody UserRoleEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

        //统计待删除的角色下有多少用户
        final int total = userRoleService.queryTotal(getCurrentUserSchemaLabel(), entity);

        //如果统计结果大于0，表示这些角色中存在关联账号的角色。已关联账号的角色不可删除
        if (total > 0) {
            return R.error("删除失败,请先删除角色下的账户 !");
        }

        //创建一个角色菜单实体，用来查询
        final RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
        roleMenuEntity.setRoleIdList(Arrays.asList(entity.getIds()));

        //统计一共关联了多少个菜单
        final int totalMenu = roleMenuService.queryTotal(getCurrentUserSchemaLabel(), roleMenuEntity);

        //如果统计结果大于0，表示这些角色中，有的角色关联了菜单
        if (totalMenu > 0) {
            return R.error("删除失败,请先解除菜单关联 !");
        }

        roleService.deleteBatch(getCurrentUserSchemaLabel(), entity.getIds());

        return R.ok();
    }
}
