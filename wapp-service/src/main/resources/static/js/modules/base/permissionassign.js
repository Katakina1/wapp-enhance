
/**
 * 权限分配
 */

var menu_ztree;

var setting = {
    showLine: true,
    checkable: true,
    view: {
        showIcon: false,
        expandSpeed: 'fast'
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "orgid",
            pIdKey: "parentid",
            rootPId: -1
        },
        key: {
            name: "orgname"
        }
    },
    callback: {
        onClick: function (event, treeId, treeNode) {
            vm.roleModel = true;
            vm.showRoleMenu = false;
            vm.showRoleUser = false;
            vm.treeOnClick(event, treeId, treeNode);
        }
    }
};

var settingMenu = {
    showLine: true,
    checkable: true,
    view: {
        showIcon: false,
        expandSpeed: 'fast'
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "menuid",
            pIdKey: "parentid",
            rootPId: -1
        },
        key: {
            name: "menuname"
        }
    },
    check: {
        enable: true,
        nocheckInherit: true,
        chkboxType: {"Y": "s", "N": "s"}
    },
    callback: {
        onCheck: checkEvent
    }
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        pageList: PAGE_PARENT.PAGE_LIST,
        roleData: [],
        userData: [],
        userBindData: [],
        multipleSelection: [],
        multipleSelectionBind: [],
        currentPageUserUnBind: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        total: 0,
        totalPage: 1,
        rolePage: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            total: 0,
            totalPage: 1
        },
        userPageBind: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            total: 0,
            totalPage: 1
        },
        queryRoleForm: {
            rolename: null,
            rolecode: null
        },
        queryUserForm: {
            username: null,
            usercode: null,
            orgid: null
        },
        queryUserUnBindForm: {
            username: null,
            usercode: null
        },
        company: null,
        orgid: null,
        orgRecord: [],
        userRecord: [],
        roleModel: false,
        showRoleMenu: false,
        showRoleUser: false,
        addRoleUserWin: false,
        roleLoading: false,
        userBindLoading: false,
        userNotBindLoading: false
    },
    mounted: function () {
        this.getOrgTree();
    },
    methods: {
        /**
         * 加载组织树
         */
        getOrgTree: function () {
            $.post(baseURL + 'base/organization/getOrgTree', {orgtypeStr: '0,1'}, function (r) {

                if (r.code === 0) {
                    $.fn.zTree.init($("#orgTree"), setting, r.orgList);
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        /**
         * 加载菜单树
         */
        getMenuTree: function (roleid) {
            $.get(baseURL + 'base/menu/menuTree', {orgid: this.orgid}, function (r) {
                menu_ztree = $.fn.zTree.init($("#menuTree"), settingMenu, r.menuTree);

                //勾选角色所拥有的部门数据权限
                $.get(baseURL + 'base/permissionassign/queryMenuIdList/' + roleid, function (r) {
                    if (r.code === 0) {
                        var data = r.menuIdList;

                        for (var i = 0; i < data.length; i++) {
                            var node = menu_ztree.getNodeByParam("menuid", data[i]);
                            if (node) {
                                menu_ztree.checkNode(node, true, false);
                            }
                        }
                    } else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                })
            });
        },
        /**
         * 菜单勾选事件 - 勾选父级菜单
         */
        checkEvent: function (event, treeId, treeNode) {
            if (treeNode.checked && treeNode.menulevel != 0) {
                var parentNode = treeNode.getParentNode();
                menu_ztree.checkNode(parentNode, true, false);

                //递归勾选
                vm.checkEvent(event, treeId, parentNode);
            }
        },
        /**
         * 权限
         */
        permissionAssign: function permissionAssign(roleid) {
            vm.getMenuTree(roleid);
            vm.roleid = roleid;
            vm.showRoleMenu = true;
            vm.showRoleUser = false;
        },
        /**
         * 查询按钮 - 角色查询
         */
        queryRole: function () {
            vm.rolePage.currentPage = 1;
            vm.reloadRole();
        },
        /**
         * 查询按钮 - 用户查询（已绑定）
         */
        queryUser: function () {
            vm.userPageBind.currentPage = 1;
            vm.reloadUser(this.roleid);
        },
        /**
         * 查询按钮 - 用户查询（未绑定）
         */
        queryUserUnBind: function () {
            vm.currentPageUserUnBind = 1;
            vm.reloadRoleUserUnBind();
        },
        /**
         * 用户列表查询
         */
        queryUserAdded: function (roleid) {
            vm.roleid = roleid;
            vm.showRoleMenu = false;
            vm.showRoleUser = true;
            this.reloadUser(roleid);
        },
        /**
         * 刷新角色列表数据
         */
        reloadRole: function () {
            vm.roleLoading = true;

            var data = this.queryRoleForm;
            var rolePage = this.rolePage;

            data.orgid = this.orgid;
            data.page = rolePage.currentPage;
            data.limit = rolePage.pageSize;

            $.post(baseURL + 'base/role/list', data, function (r) {
                vm.roleLoading = false;

                if (r.code === 0) {
                    rolePage.currentPage = r.page.currPage;
                    rolePage.total = r.page.totalCount;
                    rolePage.totalPage = r.page.totalPage;
                    vm.roleData = r.page.list;
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }

            })
        },
        /**
         * 刷新用户列表数据
         */
        reloadUser: function (roleid) {
            vm.userBindLoading = true;

            var data = this.queryUserForm;
            var userPageBind = this.userPageBind;

            data.company = this.company;
            data.roleId = roleid;
            data.bind = '1';
            data.page = userPageBind.currentPage;
            data.limit = userPageBind.pageSize;

            $.post(baseURL + 'base/user/list', data, function (r) {
                vm.userBindLoading = false;

                if (r.code === 0) {
                    userPageBind.currentPage = r.page.currPage;
                    userPageBind.total = r.page.totalCount;
                    userPageBind.totalPage = r.page.totalPage;
                    vm.userBindData = r.page.list;
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        /**
         * 刷新用户列表数据(未绑定角色)
         */
        reloadRoleUserUnBind: function () {
            vm.userNotBindLoading = true
            ;
            var data = this.queryUserUnBindForm;

            data.company = this.company;
            data.roleId = this.roleid;
            data.bind = '0';
            data.page = this.currentPageUserUnBind;
            data.limit = this.pageSize;

            $.post(baseURL + 'base/user/list', data, function (r) {
                vm.userNotBindLoading = false;

                if (r.code === 0) {
                    vm.currentPageUserUnBind = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.userData = r.page.list;
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        /**
         * 权限设置 - 角色与菜单关联
         */
        saveRoleMenu: function () {
            //获取选择的菜单
            var nodes = menu_ztree.getCheckedNodes(true);
            var menuIdList = [];
            for (var i = 0; i < nodes.length; i++) {
                menuIdList.push(nodes[i].menuid);
            }

            var data = {
                roleid: this.roleid,
                menuIdList: menuIdList
            };

            $.ajax({
                type: "POST",
                url: baseURL + "base/permissionassign/save",
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        alert('保存成功')
                    } else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        /**
         * 添加用户 - 角色与用户关联
         */
        addRoleUser: function () {
            //页码初始化
            this.currentPageUserUnBind = 1;

            this.reloadRoleUserUnBind();
            this.addRoleUserWin = true;
        },
        /**
         * 保存用户 - 角色与用户关联
         */
        saveRoleUser: function () {
            var userIds = getUserIds();

            if (typeof(userIds) == "undefined") {
                return;
            }

            var data = {
                roleid: this.roleid,
                ids: userIds
            };

            $.ajax({
                type: "POST",
                url: baseURL + "base/permissionassign/saveRoleUser",
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addRoleUserWin = false;
                        alert('保存成功', function () {
                            vm.reloadUser(vm.roleid);
                        });
                    } else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        /**
         * 删除用户，解除用户角色关联
         */
        deleteUser: function () {
            var userIds = getUserBindIds();
            if (userIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                var data = {
                    roleid: vm.roleid,
                    ids: userIds
                };

                $.ajax({
                    type: "POST",
                    url: baseURL + "base/permissionassign/deleteRoleUser",
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (r) {
                        if (r.code === 0) {
                            vm.reloadUser(vm.roleid);
                            vm.multipleSelectionBind = [];
                            alert('删除成功');
                        } else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                var hostHref = parent.location.href;
                                if(hostHref.indexOf("int")!=-1){
                                    parent.location.href ="http://rl.wal-mart.com";
                                }else if(hostHref.indexOf("ext")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else{
                                    parent.location.href = baseURL + 'login.html';
                                }
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            })
        },
        addOrUpdataWinClose: function () {
            vm.resetForm('queryUserUnBindForm');

            this.addRoleUserWin = false;
        },
        /**
         * 事件 - 机构树点击事件
         */
        treeOnClick: function (event, treeId, treeNode) {
            this.orgid = treeNode.orgid;
            this.company = treeNode.company;

            //页码初始化
            vm.rolePage.currentPage = 1;

            this.reloadRole();
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFunBind: function (selection) {
            this.multipleSelectionBind = selection;
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFun: function (selection) {
            this.multipleSelection = selection;
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentRoleChange: function (currentPage) {
            this.rolePage.currentPage = currentPage;
            this.reloadRole();
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChangeUserBind: function (currentPage) {
            this.userPageBind.currentPage = currentPage;
            this.reloadUser(this.roleid);
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChangeUserUnBind: function (currentPage) {
            this.currentPageUserUnBind = currentPage;
            this.reloadRoleUserUnBind();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChangeRole: function (val) {
            this.rolePage.pageSize = val;
            this.reloadRole();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChangeUserBind: function (val) {
            this.userPageBind.pageSize = val;
            this.reloadUser(this.roleid);
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChangeUserUnBind: function (val) {
            this.pageSize = val;
            this.reloadRoleUserUnBind();
        },
        /**
         * 重置表单 - 对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
         */
        resetForm: function (formName) {
            this.$refs[formName].resetFields();
        },
        /**
         * 行号 - 角色
         */
        roleIndex: function (index) {
            return index + (this.rolePage.currentPage - 1) * this.rolePage.pageSize + 1;
        },
        /**
         * 行号 - 用户（绑定）
         */
        userBindIndex: function (index) {
            return index + (this.userPageBind.currentPage - 1) * this.userPageBind.pageSize + 1;
        },
        /**
         * 行号 - 用户（未绑定）
         */
        userUnBindIndex: function (index) {
            return index + (this.currentPageUserUnBind - 1) * this.pageSize + 1;
        },
        /**
         * 格式化 - 数据为空时显示 --
         */
        formatterField: function (row, column, cellValue, index) {
            if (null == cellValue || '' == cellValue) {
                return '—— ——';
            }
            return cellValue;
        }
    }
});

function checkEvent(event, treeId, treeNode) {
    vm.checkEvent(event, treeId, treeNode);
}


function permissionAssign(roleid) {
    //  $("#qqqq").html(roleMenuHtml);
    vm.getMenuTree(roleid);
    vm.roleid = roleid;
    vm.showRoleMenu = true;
    vm.showRoleUser = false;
}

/***************角色用户（已绑定）查询******************/
function queryUserAdded(roleid) {
    //页码初始化
    vm.userPageBind.currentPage = 1;

    vm.queryUserAdded(roleid);
    vm.roleid = roleid;
    vm.showRoleMenu = false;
    vm.showRoleUser = true;
}

/***************选择多条记录******************/
function getSelectedRows() {
    var grid = $("#userGrid");
    var rowKey = grid.getGridParam("selrow");
    if (!rowKey) {
        alert("请选择一条记录");
        return;
    }

    return grid.getGridParam("selarrrow");
}

/***************获取选中的用户的id******************/
function getUserBindIds() {
    var selection = vm.multipleSelectionBind;
    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var userIds = [];
    for (var i = 0; i < selection.length; i++) {
        userIds.push(selection[i].userid);
    }

    return userIds;
}

/***************获取选中的用户的id******************/
function getUserIds() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var userIds = [];
    for (var i = 0; i < selection.length; i++) {
        userIds.push(selection[i].userid);
    }

    return userIds;
}