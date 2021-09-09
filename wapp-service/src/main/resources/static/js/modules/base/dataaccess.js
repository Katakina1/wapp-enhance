
/**
 * 数据权限管理
 */

Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var isInitial = true;

var vm = new Vue({
    el: '#rrapp',
    data: {
        pageList: PAGE_PARENT.PAGE_LIST,
        userForm: {
            username: null,
            loginname: null
        },
        userPage: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        userPageChild: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        userPageChildNot: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        orgid: null,
        userid: null,
        company: null,
        userData: [],
        userDataChild: [],
        tableData: [],
        showList: true,
        rrappChild: false,
        form: {
            orgname: null,
            taxno: null
        },
        queryTaxCodeForm: {
            orgname: null,
            taxno: null
        },
        currentPage: 1,
        addOrUpdateWin: false,
        addOrUpdateUserForm: [],
        userLoading: false,
        orgLoading: false,
        listLoading: false,
        multipleSelection: [],
        multipleSelectionBind: []
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.reload();
        },
        reload: function () {
            //刷新机构列表数据
            vm.userLoading = true;

            var data = this.userForm;
            var userPage = this.userPage;

            data.page = userPage.currentPage;
            data.limit = userPage.pageSize;

            $.post(baseURL + 'base/dataaccess/list', data, function (r) {
                vm.userLoading = false;

                if (r.code === 0) {
                    userPage.currentPage = r.page.currPage;
                    userPage.total = r.page.totalCount;
                    userPage.totalPage = r.page.totalPage;
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
         * 授权
         */
        authorization: function (row) {
            this.rrappChild = true;

            this.userid = row.userid;
            this.orgid = row.orgid;
            this.company = row.company;
            vm.userPageChild.currentPage = 1;
            this.seacrchDetail();
        },
        seacrchDetail: function () {
            vm.orgLoading = true;
            //刷新用户列表数据
            var data = vm.form;
            var userPageChild = this.userPageChild;

            data.userid = this.userid;
            data.orgid = this.orgid;

            data.page = userPageChild.currentPage;
            data.limit = userPageChild.pageSize;

            $.post(baseURL + 'base/dataaccess/detailList', data, function (r) {
                vm.orgLoading = false;

                if (r.code === 0) {
                    userPageChild.currentPage = r.page.currPage;
                    userPageChild.total = r.page.totalCount;
                    userPageChild.totalPage = r.page.totalPage;
                    vm.userDataChild = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        queryChild: function () {
            vm.userPageChild.currentPage = 1;
            this.seacrchDetail();
        },
        addChild: function () {

            this.findAll(1);
            this.addOrUpdateWin = true;

        },
        handleSizeChangeUser: function (val) {
            this.userPage.pageSize = val;
            if(!isInitial) {
                this.currentChangeUser(1);
            }
        },
        handleSizeChangeUserChild: function (val) {
            this.userPageChild.pageSize = val;
            this.currentChangeUserChild(1);
        },
        handleSizeChangeChildNot: function (val) {
            this.userPageChildNot.pageSize = val;
            this.findAll(1);
        },
        findAll: function (currentPage) {
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.userPageChildNot.currentPage = currentPage;
            }

            var data = vm.queryTaxCodeForm;
            var userPageChildNot = this.userPageChildNot;

            data.parentId = this.orgid;
            data.userid = this.userid;
            data.company = this.company;
            data.page = userPageChildNot.currentPage;
            data.limit = userPageChildNot.pageSize;

            $.post(baseURL + 'base/dataaccess/getNotAddList', data, function (r) {
                vm.listLoading = false;

                if (r.code === 0) {
                    userPageChildNot.currentPage = r.page.currPage;
                    userPageChildNot.total = r.page.totalCount;
                    userPageChildNot.totalPage = r.page.totalPage;
                    vm.tableData = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })

        },
        delChild: function () {

            var id = getSelectedRows();
            if (id == null) {
                return;
            }
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/dataaccess/delete",
                    contentType: "application/json",
                    data: JSON.stringify(id),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('删除成功', function () {
                                vm.queryChild();
                                vm.multipleSelectionBind = [];
                            });
                        }else if (r.code == 401) {
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
        queryOrg: function () {
            this.findAll(1);
        },
        addAndSave: function () {
            var data = this.multipleSelection;

            if (data.length == 0) {
                alert('请选择要操作的数据');
                return;
            }

            var url = "base/dataaccess/taxnoAdd/" + this.userid;
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWin = false;
                        vm.queryChild();
                        vm.multipleSelection = [];
                        alert('新增成功');
                    } else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }else {
                        alert(r.msg);
                    }
                }
            });
        },
        addOrUpdataWinClose: function (val) {
            this.addOrUpdateWin = false;
            this.resetForm('queryTaxCodeForm');
        },
        changeFun: function (val) {
            this.multipleSelection = val;
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFunBind: function (selection) {
            this.multipleSelectionBind = selection;
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function(currentPage){
            if(vm.userPage.total==0){
                return;
            }
            this.currentChangeUser(currentPage);
        },
        currentChangeUser: function (currentPage) {
            this.userPage.currentPage = currentPage;
            this.userDataChild = null;
            this.userPageChild.total = 0;
            this.userPageChild.totalPage = null;
            this.reload();
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChangeUserChild: function (currentPage) {
            this.userPageChild.currentPage = currentPage;
            this.seacrchDetail();
        },
        resetForm: function (formName) {
            //对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
            this.$refs[formName].resetFields();
        },
        /**
         * 行号 - 用户
         */
        userIndex: function (index) {
            return index + (this.userPage.currentPage - 1) * this.userPage.pageSize + 1;
        },
        /**
         * 行号 - 子用户绑定
         */
        userChildIndex: function (index) {
            return index + (this.userPageChild.currentPage - 1) * this.userPage.pageSize + 1;
        },
        /**
         * 行号 - 子用户未绑定
         */
        userPageChildNotIndex: function (index) {
            return index + (this.userPageChildNot.currentPage - 1) * this.userPageChildNot.pageSize + 1;
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
    var selection = vm.multipleSelectionBind;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var ids = [];
    for (var i = 0; i < selection.length; i++) {
        ids.push(selection[i].userTaxnoId);
    }

    return ids;
}