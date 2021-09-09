
/**
 * 功能菜单管理
 */

var settingOrg = {
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
            vm.menuShow = false;
            vm.orgTreeOnClick(event, treeId, treeNode);
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
    callback: {
        onCollapse: treeOnCollapse,
        onExpand: treeOnExpand,
        onClick: function (event, treeId, treeNode) {
            vm.menuShow = true;
            vm.menuTreeOnClick(event, treeId, treeNode);
        }
    }
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        menuShow: false,
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        menuData: [],
        queryForm: {
            menuname: null,
            menucode: null
        },
        addOrUpdateForm: {
            sortno: null,
            menuaction: null,
            menudesc: null
        },
        orgid: null,
        menuid: null,
        isLeaf: false,
        orgNode: null,
        menuNode: null,
        menuRecord: [],
        multipleSelection: [],
        expandMenuId: [],
        addOrUpdateWin: false,
        menuLoading: false
    },
    mounted: function () {
        this.getOrgTree();
    },
    methods: {
        getOrgTree: function () {
            //加载组织树
            $.post(baseURL + 'base/organization/getOrgTree', {orgtypeStr: '0,1'}, function (r) {
                if (r.code === 0) {
                    $.fn.zTree.init($("#orgTree"), settingOrg, r.orgList);
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        getMenuTree: function (expandMenuId) {
            //加载菜单树
            $.get(baseURL + 'base/menu/menuTree', {orgid: this.orgid, menuldStr: expandMenuId}, function (r) {
                if (r.code === 0) {
                    $.fn.zTree.init($("#menuTree"), settingMenu, r.menuTree);

                    if (vm.menuid != null) {
                        vm.selectTree();
                    }
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
         * 选中某个节点
         */
        selectTree: function () {
            var zTree = $.fn.zTree.getZTreeObj("menuTree");
            var node = zTree.getNodeByParam("menuid", vm.menuid);
            //将指定ID的节点选中
            zTree.selectNode(node);
        },
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveOrgData();
                } else {
                    return false
                }
            })
        },
        saveOrgData: function () {

            var data = vm.addOrUpdateForm;
            data.orgid = this.orgid;

            var url = data.menuid == null ? "base/menu/saveMenu" : "base/menu/updateMenu";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.getMenuTree(vm.expandMenuId.join(','));
                        vm.reloadMenu();
                        vm.addOrUpdateWin = false;
                        if (data.menuid == null) {
                            alert('新增成功');
                        } else {
                            alert('修改成功');
                        }

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
        deleteOrg: function () {
            var menuIds = getSelectedRows();
            if (menuIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/menu/deleteMenu",
                    contentType: "application/json",
                    data: JSON.stringify({menuIds: menuIds}),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.getMenuTree(vm.expandMenuId.join(','));
                            vm.reloadMenu();
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
            });
        },
        orgTreeOnClick: function (event, treeId, treeNode) {

            this.orgNode = treeNode;
            this.orgid = treeNode.orgid;
            this.getMenuTree();
        },
        menuTreeOnClick: function (event, treeId, treeNode) {

            this.menuNode = treeNode;
            this.menuid = treeNode.menuid;
            this.menulevel = treeNode.menulevel;

            //页码初始化
            vm.currentPage = 1;

            this.reloadMenu(null);
        },
        query: function () {

            vm.reloadMenu();
        },
        reloadMenu: function () {
            //刷新列表数据
            vm.menuLoading = true;

            var data = this.queryForm;

            data.parentid = this.menuid;
            data.page = vm.currentPage;
            data.limit = vm.pageSize;

            $.post(baseURL + 'base/menu/list', data, function (r) {
                vm.menuLoading = false;

                if (r.code == 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.menuData = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        addOrUpdata: function (isInsert) {
            var treeObj = $.fn.zTree.getZTreeObj("menuTree");
            var selectRecord = treeObj.getSelectedNodes();
            if (!selectRecord) {
                alert("请选择功能菜单!");
                return;
            }

            if (isInsert) {
                this.addOrUpdateForm.title = '新增功能菜单';
                this.addOrUpdateForm.menuid = null;
                this.addOrUpdateForm.parentid = this.menuid;
                this.addOrUpdateForm.menulevel = this.menulevel + 1;
                this.addOrUpdateForm.isbottom = 0;
            } else {
                this.addOrUpdateForm = this.menuRecord;
                this.addOrUpdateForm.title = '修改功能菜单';
            }

            this.addOrUpdateWin = true;
        },
        addOrUpdataWinClose: function (val) {
            //隐藏窗口
            this.addOrUpdateWin = false;
        },
        resetForm: function (formName) {
            //对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
            this.$refs[formName].resetFields();
        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].clearValidate();
            this.addOrUpdateReset();
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
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.reloadMenu();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadMenu();
        },
        openAddOrUpdateWin: function (data) {
            vm.menuRecord = {
                menuid: data.menuid,
                menuname: data.menuname,
                menucode: data.menucode,
                menulabel: data.menulabel,
                sortno: data.sortno,
                image: data.image,
                menuaction: data.menuaction,
                menudesc: data.menudesc
            };
            vm.addOrUpdata(false)
        },
        addOrUpdateReset: function () {
            //重置菜单信息
            this.addOrUpdateForm.menuname = null;
            this.addOrUpdateForm.menucode = null;
            this.addOrUpdateForm.menulabel = null;
            this.addOrUpdateForm.sortno = null;
            this.addOrUpdateForm.image = null;
            this.addOrUpdateForm.menuaction = null;
            this.addOrUpdateForm.menudesc = null;
        },
        /**
         * 行号 - 菜单
         */
        menuIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});

/*******************组织树展开事件**********************/
function treeOnExpand(event, treeId, treeNode) {
    vm.expandMenuId.push(treeNode.menuid);
}

/*******************组织树收起事件**********************/
function treeOnCollapse(event, treeId, treeNode) {
    var result = [];
    var expandOrgId = vm.expandMenuId;
    for (var i = 0; i < expandOrgId.length; i++) {
        if (expandOrgId[i] != treeNode.menuid) {
            result.push(expandOrgId[i])
        }
    }
    vm.expandMenuId = result;
}

/***************选择多条记录******************/
function getSelectedRows() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var menuIds = [];
    for (var i = 0; i < selection.length; i++) {
        menuIds.push(selection[i].menuid);
    }

    return menuIds;
}