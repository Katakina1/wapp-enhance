package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.MenuEntity;
import com.xforceplus.wapp.modules.base.entity.RoleMenuEntity;
import com.xforceplus.wapp.modules.base.service.MenuService;
import com.xforceplus.wapp.modules.base.service.RoleMenuService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.collect.Collections2;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.xforceplus.wapp.modules.base.WebUriMappingConstant.*;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Sunny.xu on 4/20/2018.
 * 功能菜单管理控制层
 */
@RestController
public class MenuController extends AbstractController {

    private final MenuService menuService;

    private final RoleMenuService roleMenuService;

    @Autowired
    public MenuController(MenuService menuService, RoleMenuService roleMenuService) {
        this.menuService = menuService;
        this.roleMenuService = roleMenuService;
    }

    /**
     * 导航菜单
     */
    @SysLog("查询导航菜单")
    @RequestMapping("/sys/menu/nav")
    public R nav() {
        final String schemaLabel = getCurrentUserSchemaLabel();
        List<MenuEntity> menuList = menuService.getUserMenuList(schemaLabel, getUserId(), 1);
        return R.ok().put("menuList", menuList);
    }

    /**
     * 查询菜单列表
     */
    @SysLog("菜单列表查询")
    @RequestMapping(URI_MENU_LIST)
    public R list(MenuEntity menuEntity) {
        //获取当前页面
        final Integer page = menuEntity.getPage();

        //分页查询起始值
        menuEntity.setOffset((page - 1) * menuEntity.getLimit());
        //分库
        menuEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<MenuEntity> menuEntityList = menuService.queryList(getCurrentUserSchemaLabel(), menuEntity);

        int total = menuService.queryTotal(getCurrentUserSchemaLabel(), menuEntity);

        PageUtils pageUtil = new PageUtils(menuEntityList, total, menuEntity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 获取菜单树
     */
    @SysLog("菜单树获取")
    @RequestMapping(URI_MENU_TREE)
    public R selectMenuTree(MenuEntity entity) {
        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

        //用来存放菜单id
        List<String> idList = newArrayList();

        if (StringUtils.isNotBlank(entity.getMenuldStr())) {
            //需要展开的菜单id数组
            final String[] expandOrgIdArr = entity.getMenuldStr().split(",");

            //将数组转成集合
            idList = Arrays.asList(expandOrgIdArr);
        }

        List<MenuEntity> menuEntityList = menuService.queryList(getCurrentUserSchemaLabel(), entity);

        return R.ok().put("menuTree", menuTreeGenerator(menuEntityList, idList));
    }


    /**
     * 获取菜单树(机构模块用)
     */
    @SysLog("菜单树获取")
    @RequestMapping(URI_MENU_ORG_TREE)
    public R selectOrgMenuTree(MenuEntity entity) {
        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

        //用来存放菜单id
        List<String> idList = newArrayList();

        if (StringUtils.isNotBlank(entity.getMenuldStr())) {
            //需要展开的菜单id数组
            final String[] expandOrgIdArr = entity.getMenuldStr().split(",");

            //将数组转成集合
            idList = Arrays.asList(expandOrgIdArr);
        }

        List<MenuEntity> menuEntityList = menuService.queryOrgMenuList(getCurrentUserSchemaLabel(), entity);

        return R.ok().put("menuTree", orgMenuTreeGenerator(menuEntityList, idList));
    }

    /**
     * 根据菜单id获取菜单信息
     */
    @SysLog("菜单信息获取")
    @RequestMapping(URI_MENU_INFO_GET_BY_ID)
    public R selectSingle(@PathVariable Long menuId) {

        return R.ok().put("menuInfo", menuService.queryObject(getCurrentUserSchemaLabel(), menuId));
    }

    /**
     * 保存菜单信息
     */
    @SysLog("菜单信息保存")
    @RequestMapping(URI_MENU_SAVE)
    public R save(@RequestBody MenuEntity menuEntity) {

        //分库
        menuEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        menuService.save(getCurrentUserSchemaLabel(), menuEntity);

        return R.ok();
    }

    /**
     * 更新菜单信息
     */
    @SysLog("菜单信息更新")
    @RequestMapping(URI_MENU_UPDATE)
    public R update(@RequestBody MenuEntity menuEntity) {

        //分库
        menuEntity.setSchemaLabel(getCurrentUserSchemaLabel());

        menuService.update(getCurrentUserSchemaLabel(), menuEntity);

        return R.ok();
    }

    /**
     * 删除菜单信息(批量)
     * 删除对应的菜单信息，已有下级菜单的菜单信不不能删除，已关联角色的菜单不能删除
     */
    @SysLog("菜单信息删除")
    @RequestMapping(URI_MENU_DELETE)
    public R delete(@RequestBody MenuEntity menuEntity) {

        //统计一共有多少个下级菜单
        final int total = menuService.queryTotal(getCurrentUserSchemaLabel(), menuEntity);

        //如果统计结果大于0，表示这些菜单中，有的菜单存在下级菜单
        if (total > 0) {
            return R.error("删除失败,请先删除下级菜单 !");
        }

        //创建一个角色菜单实体，用来查询
        final RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
        roleMenuEntity.setMenuIdList(Arrays.asList(menuEntity.getMenuIds()));

        //统计一共关联了多少个角色
        final int totalRole = roleMenuService.queryTotal(getCurrentUserSchemaLabel(), roleMenuEntity);

        //如果统计结果大于0，表示这些菜单中，有的菜单关联了角色
        if (totalRole > 0) {
            return R.error("删除失败,请先解除角色关联 !");
        }

        menuService.deleteBatch(getCurrentUserSchemaLabel(), menuEntity.getMenuIds());

        return R.ok();
    }

    /**
     * 菜单树数据封装
     */
    private List<MenuEntity> menuTreeGenerator(final List<MenuEntity> menuEntityList, final List<String> idList) {
        //菜单根节点
        MenuEntity rootMenu = null;

        //菜单集合（有层次）
        List<MenuEntity> menuList = newArrayList();

        //获取根节点
        if (!menuEntityList.isEmpty()) {
            rootMenu = menuEntityList.get(0);
        }

        if (null != rootMenu) {
            //获取子级菜单
            final List<MenuEntity> firstMenus = getChildren(menuEntityList, rootMenu.getMenuid());
            //本级菜单
            rootMenu.setIsLeaf(firstMenus.isEmpty());
            //判断是否包含当前菜单id，如果包含，该节点树展开
            rootMenu.setOpen(idList.contains(String.valueOf(rootMenu.getMenuid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if (!rootMenu.getIsLeaf()) {
                final List<MenuEntity> firstMenusVO = findChildNode(firstMenus, menuEntityList, idList);
                rootMenu.setChildren(firstMenusVO);
            }

            menuList.add(rootMenu);
        }
        return menuList;
    }

    /**
     * 查找子节点
     */
    private List<MenuEntity> findChildNode(List<MenuEntity> children, List<MenuEntity> menuEntityList, final List<String> idList) {
        final List<MenuEntity> secondMenuVOList = newArrayList();
        for (final MenuEntity second : children) {
            List<MenuEntity> chird = getChildren(menuEntityList, second.getMenuid());

            second.setIsLeaf(chird.isEmpty());
            //判断是否包含当前菜单id，如果包含，该节点树展开
            second.setOpen(idList.contains(String.valueOf(second.getMenuid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if (!second.getIsLeaf()) {
                final List<MenuEntity> threeMenuVOList = findChildNode(chird, menuEntityList, idList);
                second.setChildren(threeMenuVOList);
            }
            secondMenuVOList.add(second);
        }
        return secondMenuVOList;
    }

    /**
     * 菜单树数据封装
     */
    private List<MenuEntity> orgMenuTreeGenerator(final List<MenuEntity> menuEntityList, final List<String> idList) {
        //菜单根节点
        MenuEntity rootMenu = null;

        //菜单集合（有层次）
        List<MenuEntity> menuList = newArrayList();

        //获取根节点
        if (!menuEntityList.isEmpty()) {
            rootMenu = menuEntityList.get(0);
        }

        if (null != rootMenu) {
            //获取子级菜单
            final List<MenuEntity> firstMenus = getChildren(menuEntityList, rootMenu.getMenuid());
            //本级菜单
            rootMenu.setIsLeaf(firstMenus.isEmpty());
            //判断是否包含当前菜单id，如果包含，该节点树展开
            rootMenu.setOpen(idList.contains(String.valueOf(rootMenu.getMenuid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if (!rootMenu.getIsLeaf()) {
                final List<MenuEntity> firstMenusVO = findChildNodeForOrg(firstMenus, menuEntityList, idList);
                rootMenu.setChildren(firstMenusVO);
            }

            menuList.add(rootMenu);
        }
        return menuList;
    }

    /**
     * 查找子节点（组织机构模块用）
     */
    private List<MenuEntity> findChildNodeForOrg(List<MenuEntity> children, List<MenuEntity> menuEntityList, final List<String> idList) {
        final List<MenuEntity> secondMenuVOList = newArrayList();
        for (final MenuEntity second : children) {
            List<MenuEntity> chird = getChildren(menuEntityList, second.getMenuid());

            second.setIsLeaf(chird.isEmpty());
            //判断是否包含当前菜单id，如果包含，该节点树展开
            second.setOpen(idList.contains(String.valueOf(second.getMenuid())));

            //如果不是叶子节点，说明存在子节点，继续循环，获取子节点数据
            if(second.getIsLeaf() && second.getMenulevel()==1){

            }
            else{
                final List<MenuEntity> threeMenuVOList = findChildNodeForOrg(chird, menuEntityList, idList);
                second.setChildren(threeMenuVOList);
                secondMenuVOList.add(second);
            }
        }
        return secondMenuVOList;
    }

    /**
     * 获取子节点
     */
    private List<MenuEntity> getChildren(final List<MenuEntity> sourceList, final Integer parentId) {
        final Collection<MenuEntity> transform = Collections2.filter(
                sourceList, organizationEntity -> parentId.equals(organizationEntity.getParentid()));

        return newArrayList(transform);
    }
}
