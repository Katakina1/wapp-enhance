package com.xforceplus.wapp.modules.base;

/**
 * Created by sunny.xu on 4/16/2018.
 * 请求地址
 */
public final class WebUriMappingConstant {

    private WebUriMappingConstant() {
    }

    private static final String BASE_ROOT = "/base/";

    /**
     * 机构管理
     */
    public static final String URI_ORG_LIST = BASE_ROOT + "organization/list";
    public static final String URI_ORG_GET_ORG_TREE = BASE_ROOT + "organization/getOrgTree";
    public static final String URI_ORG_INFO_GET_BY_ID = BASE_ROOT + "organization/getOrgInfoById/{orgId}";
    public static final String URI_ORG_SAVE = BASE_ROOT + "organization/saveOrg";
    public static final String URI_ORG_UPDATE = BASE_ROOT + "organization/updateOrg";
    public static final String URI_ORG_DELETE = BASE_ROOT + "organization/deleteOrg";
    public static final String URI_ORG_MENU_SAVE = BASE_ROOT + "organization/saveOrgMenu";

    /**
     * 用户管理
     */
    public static final String URI_USER_LIST = BASE_ROOT + "user/list";
    public static final String URI_USER_LIST_ROLE = BASE_ROOT + "user/listRoleUser";
    public static final String URI_USER_INFO_GET_BY_ID = BASE_ROOT + "user/getUserInfoById/{userId}";
    public static final String URI_USER_SAVE = BASE_ROOT + "user/saveUser";
    public static final String URI_USER_UPDATE = BASE_ROOT + "user/updateUser";
    public static final String URI_USER_DELETE = BASE_ROOT + "user/deleteUser";

    /**
     * 角色管理
     */
    public static final String URI_ROLE_LIST = BASE_ROOT + "role/list";
    public static final String URI_ROLE_INFO_GET_BY_ID = BASE_ROOT + "role/getRoleInfoById/{roleId}";
    public static final String URI_ROLE_SAVE = BASE_ROOT + "role/saveRole";
    public static final String URI_ROLE_UPDATE = BASE_ROOT + "role/updateRole";
    public static final String URI_ROLE_DELETE = BASE_ROOT + "role/deleteRole";

    /**
     * 功能菜单管理
     */
    public static final String URI_MENU_LIST = BASE_ROOT + "menu/list";
    public static final String URI_MENU_TREE = BASE_ROOT + "menu/menuTree";
    public static final String URI_MENU_ORG_TREE = BASE_ROOT + "menu/orgMenuTree";
    public static final String URI_MENU_INFO_GET_BY_ID = BASE_ROOT + "menu/getMenuInfoById/{menuId}";
    public static final String URI_MENU_SAVE = BASE_ROOT + "menu/saveMenu";
    public static final String URI_MENU_UPDATE = BASE_ROOT + "menu/updateMenu";
    public static final String URI_MENU_DELETE= BASE_ROOT + "menu/deleteMenu";

    /**
     * 权限管理
     */
    public static final String URI_PERMISSION_ASSIGN_ROLE_MENU = BASE_ROOT + "permissionassign/save";
    public static final String URI_PERMISSION_ASSIGN_ROLE_USER_SAVE = BASE_ROOT + "permissionassign/saveRoleUser";
    public static final String URI_PERMISSION_ASSIGN_QUERY_MENUID_LIST = BASE_ROOT + "permissionassign/queryMenuIdList/{roleId}";
    public static final String URI_PERMISSION_ASSIGN_ROLE_USER_DELETE = BASE_ROOT + "permissionassign/deleteRoleUser";

}
