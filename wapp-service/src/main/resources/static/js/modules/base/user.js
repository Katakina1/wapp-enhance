
/**
 * 用户管理
 */

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
            vm.treeOnClick(event, treeId, treeNode);
        }
    }
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        readonly: true,
        pageList: PAGE_PARENT.PAGE_LIST,
        orgData: [],
        orgPage: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        queryForm: {
            orgname: null,
            orgcode: null,
            taxname: null,
            taxno: null
        },
        userData: [],
        userPage: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        queryUserForm: {
            username: null,
            usercode: null,
            orgid: null
        },
        addOrUpdateUserForm: {
            sex: null,
            status: null
        },
        orgid: null,
        orgidSale: null,
        isLeaf: false,
        orgNode: null,
        orgRecord: [],
        userRecord: [],
        multipleSelection: [],
        orgModel: false,
        orgState: false,
        userModel: false,
        addOrUpdateWin: false,
        orgLoading: false,
        userLoading: false,
        readonlyPassword: false,
        spanValue: 18,
        items: [
            {text: '男', value: '0'},
            {text: '女', value: '1'}
        ],
        scanPathList: [],
        itemsStatus: [
            {text: '初始', value: '0'},
            {text: '正常', value: '1'},
            {text: '挂起', value: '2'},
            {text: '锁定', value: '3'},
            {text: '停用', value: '4'}
        ],
        scanPoint:true
    },
    mounted: function () {
        this.getOrgTree();

        this.getScanPathList();
    },
    methods: {
        getScanPathList: function (){
            $.post(baseURL + 'base/scanpath/list', function (r) {
                if (r.code === 0) {
                    var editgfs = [];

                    for (var i = 0; i < r.optionList.length; i++) {
                        var gf = {};
                        gf.value = r.optionList[i].id+"";
                        gf.text = r.optionList[i].scanPath;
                        editgfs.push(gf);
                    }
                    vm.scanPathList = editgfs;
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })

        },
        getOrgTree: function () {
            //加载组织树
            $.post(baseURL + 'base/organization/getOrgTree', function (r) {
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
        searchUser: function (row) {
            this.orgidSale = row.orgid;
            this.orgNode = row;

            //页码初始化
            this.userPage.currentPage = 1;

            //用户列表查询
            this.reloadUser(row.orgid);
        },
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateUserForm'].validate(function (valid) {
                if (valid) {
                    vm.saveUserData();
                } else {
                    return false
                }
            })
        },
        saveUserData: function () {

            var data = vm.addOrUpdateUserForm;
            var birthday = data.birthday;
            if (null != birthday) {
                data.birthday = new Date(data.birthday).getTime();
            }
            data.plainpassword = data.password;

            var url = data.userid == null ? "base/user/saveUser" : "base/user/updateUser";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        if (vm.orgModel) {
                            if(vm.orgState){
                                vm.reloadUser(vm.orgidSale);
                            }else {
                                vm.reloadUser(vm.orgid);
                            }
                        } else {
                            vm.reloadUser(vm.orgid);
                        }
                        vm.addOrUpdateWin = false;
                        if (data.userid == null) {
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
        deleteUser: function () {
            var userIds = getSelectedRows();
            if (userIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/user/deleteUser",
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadUser(vm.orgid);
                            vm.multipleSelection = [];
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
        treeOnClick: function (event, treeId, treeNode) {
            this.orgNode = treeNode;
            this.orgid = treeNode.orgid;
            this.userModel = true;

            //页码初始化
            vm.userPage.currentPage = 1;

            //点击除“销方”节点的其他节点,只显示用户列表
            if ('3' != treeNode.orgtype) {
                this.orgModel = false;
                this.orgState = false;
                vm.orgidSale = null;
                vm.spanValue = 18;

                this.reloadUser(this.orgid);
            } else {
                this.orgModel = true;
                vm.spanValue = 9;

                //页码初始化
                vm.orgPage.currentPage = 1;

                //子级组织列表数据加载
                this.reloadOrg();

                //用户列表初始化
                vm.userData = [];
                vm.userPage.totalPage = 0;
                vm.userPage.total = 0;
                this.orgid = treeNode.orgid;
            }
        },
        query: function () {

            vm.reloadOrg();
        },
        queryUser: function () {

            vm.reloadUser(this.orgid);
        },
        reloadOrg: function () {
            //刷新机构列表数据
            vm.orgLoading = true;

            var data = this.queryForm;

            data.parentid = this.orgid;
            data.page = this.orgPage.currentPage;
            data.limit = this.orgPage.pageSize;

            $.post(baseURL + 'base/organization/list', data, function (r) {
                vm.orgLoading = false;
                if (r.code === 0) {
                    vm.orgPage.currentPage = r.page.currPage;
                    vm.orgPage.total = r.page.totalCount;
                    vm.orgPage.totalPage = r.page.totalPage;
                    vm.orgData = r.page.list;
                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }

            })
        },
        reloadUser: function (orgid) {
            //刷新用户列表数据
            vm.userLoading = true;
            var data = this.queryUserForm;

            data.orgid = orgid;

            //如果组织节点是销方，则用销方下的具体机构id进行查询
            if (vm.orgModel) {
               if(vm.orgidSale!=null){
                   data.orgid = vm.orgidSale;
               }

            }

            data.page = this.userPage.currentPage;
            data.limit = this.userPage.pageSize;
            var flag = false;
            $.post(baseURL + 'base/user/list', data, function (r) {
                vm.userLoading = false;
                if (r.code === 0) {
                    vm.userPage.currentPage = r.page.currPage;
                    vm.userPage.total = r.page.totalCount;
                    vm.userPage.totalPage = r.page.totalPage;
                    vm.userData = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }

                flag = true;
            });
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);
        },
        addOrUpdata: function (isInsert) {
            var orgid = vm.orgid;
            if (vm.orgModel) {
                if (vm.orgidSale == null) {
                   /* alert("请选择组织机构");
                    return;*/
                } else {
                    orgid = vm.orgidSale;
                }
            }

            //门店下不能新增用户
            if (this.orgNode.orgtype == '1') {
                alert("门店下不能新增用户");
                return;
            }


          if (this.orgNode.orgtype == '3'||this.orgNode.orgtype == '8') {
                this.scanPoint=false;
                this.orgState=true;
            }

            if (isInsert) {
                this.addOrUpdateUserForm.title = '新增用户';
                this.addOrUpdateUserForm.orgid = orgid;
                this.addOrUpdateUserForm.userid = null;
                vm.readonlyPassword = false;
            } else {
                this.addOrUpdateUserForm = this.userRecord;
                this.addOrUpdateUserForm.title = '修改用户';
                vm.readonlyPassword = true;
            }

            this.addOrUpdateUserForm.orgName = this.orgNode.orgname;
            this.addOrUpdateWin = true;

        },
        addOrUpdataWinClose: function (val) {
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
         * 事件 - currentPage 改变时会触发
         */
        currentChangeOrg: function (currentPage) {
            this.orgPage.currentPage = currentPage;
            this.reloadOrg();
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChangeUser: function (currentPage) {

            this.userPage.currentPage = currentPage;
            this.reloadUser(this.orgid);
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChangeOrg: function (val) {
            this.orgPage.pageSize = val;
            this.reloadOrg();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChangeUser: function (val) {
            this.userPage.pageSize = val;
            this.reloadUser(this.orgid);
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFunUser: function (selection) {
            this.multipleSelection = selection;
        },
        openAddOrUpdataWin: function (data) {
            vm.userRecord = {
                userid: data.userid,
                username: data.username,
                usercode: data.usercode,
                loginname: data.loginname,
                password: data.plainpassword,
                sex: data.sex,
                birthday: data.birthday,
                email: data.email,
                orgid: data.orgid,
                phone: data.phone,
                cellphone: data.cellphone,
                address: data.address,
                status: data.status,
                extf0: data.extf0
            };
            vm.addOrUpdata(false)
        },
        addOrUpdateReset: function () {
            //重置用户信息
            this.addOrUpdateUserForm.username = null;
            this.addOrUpdateUserForm.usercode = null;
            this.addOrUpdateUserForm.password = null;
            this.addOrUpdateUserForm.loginname = null;
            this.addOrUpdateUserForm.sex = null;
            this.addOrUpdateUserForm.birthday = null;
            this.addOrUpdateUserForm.email = null;
            this.addOrUpdateUserForm.orgid = null;
            this.addOrUpdateUserForm.phone = null;
            this.addOrUpdateUserForm.cellphone = null;
            this.addOrUpdateUserForm.address = null;
            this.addOrUpdateUserForm.status = null;
        },
        /**
         * 行号 - 组织
         */
        orgIndex: function (index) {
            return index + (this.orgPage.currentPage - 1) * this.orgPage.pageSize + 1;
        },
        /**
         * 行号 - 用户
         */
        userIndex: function (index) {
            return index + (this.userPage.currentPage - 1) * this.userPage.pageSize + 1;
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

/***************选择多条记录******************/
function getSelectedRows() {
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