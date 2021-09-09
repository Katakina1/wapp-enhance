/**
 * 业务类型
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
        onClick: treeOnClick
    }
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        billtypeData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        queryForm: {
            billtypename: null,
            billtypecode: null
        },
        addOrUpdateForm: {},
        orgid: null,
        isLeaf: false,
        orgNode: null,
        billtypeRecord: [],
        multipleSelection: [],
        addOrUpdateWin: false,
        menuGridShow: false,
        billtypeLoading: false
    },
    watch: {
        'queryForm.billtypecode':{
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9a-zA-Z]*$/g;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.queryForm.billtypecode = oldValue;
                    })
                }
            },
            deep: true
        },
        'addOrUpdateForm.billtypecode':{
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9a-zA-Z]*$/g;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.addOrUpdateForm.billtypecode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    mounted: function () {
        this.getOrgTree();
        this.treeOnClick();
    },
    methods: {
        getOrgTree: function () {
            //加载组织树
            $.post(baseURL + 'base/organization/getOrgTree', {orgtypeStr: '0,1'}, function (r) {
                if (r.code === 0) {
                    $.fn.zTree.init($("#orgTree"), setting, r.orgList);
                }else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveBillTypeData();
                } else {
                    return false
                }
            })
        },
        saveBillTypeData: function () {

            var data = vm.addOrUpdateForm;

            var url = data.billtypeid == null ? "base/billtype/saveBillType" : "base/billtype/updateBillType";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.reloadBillType();
                        vm.addOrUpdateWin = false;
                        if (data.billtypeid == null) {
                            alert('新增成功');
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
                            alert('修改成功');
                        }

                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        deleteOrg: function () {
            var billtypeIds = getSelectedRows();
            if (billtypeIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/billtype/deleteBillType",
                    contentType: "application/json",
                    data: JSON.stringify({ids: billtypeIds}),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadBillType();
                            vm.multipleSelection = [];
                            alert('删除成功');
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
        treeOnClick: function (event, treeId, treeNode) {
            this.menuGridShow = true;
            // this.orgNode = treeNode;
            // this.orgid = treeNode.orgid;

            //页码初始化
            this.currentPage=1;

            this.reloadBillType();
        },
        query: function () {

            vm.reloadBillType();
        },
        reloadBillType: function () {
            //刷新列表数据
            vm.billtypeLoading = true;

            var data = this.queryForm;

            data.orgid = this.orgid;
            data.page = this.currentPage;
            data.limit = this.pageSize;

            $.post(baseURL + 'base/billtype/list', data, function (r) {
                vm.billtypeLoading = false;
                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.billtypeData = r.page.list;
                }else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        addOrUpdata: function (isInsert) {
            // if (null == this.orgid) {
            //     alert("请选择组织机构!");
            //     return;
            // }

            if (isInsert) {
                this.addOrUpdateForm.title = '新增业务类型';
                this.addOrUpdateForm.billtypeid = null;
                this.addOrUpdateForm.orgid = this.orgid;
            } else {
                this.addOrUpdateForm = this.billtypeRecord;
                this.addOrUpdateForm.title = '修改业务类型';
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
            this.reloadBillType();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadBillType();
        },
        openAddOrUpdateWin: function (data) {
            vm.billtypeRecord = {
                billtypeid: data.billtypeid,
                billtypename: data.billtypename,
                billtypecode: data.billtypecode,
                billtypedesc: data.billtypedesc
            };
            vm.addOrUpdata(false)
        },
        addOrUpdateReset: function () {
            //重置业务类型信息
            this.addOrUpdateForm.billtypename = null;
            this.addOrUpdateForm.billtypecode = null;
            this.addOrUpdateForm.billtypedesc = null;
        },
        /**
         * 行号 - 业务类型
         */
        billtypeIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
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

/***************打开窗口 新增/修改******************/
function openAddOrUpdateWin(billtypeid) {
    $.post(baseURL + 'base/billtype/getBillTypeInfoById/' + billtypeid, function (r) {
        var data = r.billtypeInfo;
        vm.billtypeRecord = {
            billtypeid: data.billtypeid,
            billtypename: data.billtypename,
            billtypecode: data.billtypecode,
            billtypedesc: data.billtypedesc
        };
        vm.addOrUpdata(false)
    });
}

/***************组织树 - 点击事件******************/
function treeOnClick(event, treeId, treeNode) {
    vm.treeOnClick(event, treeId, treeNode);
}

/***************选择多条记录******************/
function getSelectedRows() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var ids = [];
    for (var i = 0; i < selection.length; i++) {
        ids.push(selection[i].billtypeid);
    }

    return ids;
}