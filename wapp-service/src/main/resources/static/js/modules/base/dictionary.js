
/**
 * 机构管理
 */
var isInitial = true;
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
            vm.columnShow = true;
        }
    }
};


var vm = new Vue({
    el: '#rrapp',
    data: {
        orgData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        orgDetailData : [],
        currentPageDetail: 1,
        pageSizeDetail: PAGE_PARENT.PAGE_SIZE,
        pageListDetail: PAGE_PARENT.PAGE_LIST,
        totalDetail: 0,
        totalPageDetail: 1,
        orgModel: false,
        readonly: true,
        disabled: true,
        fieldShow: false,
        columnShow: false,
        fieldOrglayerShow: false,
        inputShow: false,
        isInsert: true,
        isInsertDetail: true,
        queryForm: {
            dicttypename: null,
            dicttypecode: null
        },
        queryUserForm: {
            dictname:null,
            dictcode:null
        },
        multipleSelection: [],
        multipleSelectionDetail: [],
        addOrUpdateForm: {},
        addOrUpdateFormDetail: {},
        orgid: null,
        isLeaf: false,
        orgNode: null,
        orgRecord: [],
        orgRecordDetail: [],
        addOrUpdateWin: false,
        addOrUpdateWinDetail: false,
        items: [],
        itemsDB: [],
        showDicDetail: false,
        dicttypeExist: null,
        dicttype: null
    },
    methods: {
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
        submitFormDetail: function () {
            //表单校验
            this.$refs['addOrUpdateFormDetail'].validate(function (valid) {
                if (valid) {
                    vm.saveOrgDataDetail();
                } else {
                    return false
                }
            })
        },
        saveOrgData: function () {
            var data = vm.addOrUpdateForm;

            var url = data.dicttypeid == null ? "base/dictype/save" : "base/dictype/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWin = false;
                        vm.reloadOrg();
                        vm.addOrUpdateForm = {};
                        if (data.dicttypeid == null) {
                            alert('新增成功');
                        } else {
                            alert('修改成功');
                        }

                    }else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        saveOrgDataDetail: function () {
            var data = vm.addOrUpdateFormDetail;

            var url = data.dictid == null ? "base/dictype/detailSave" : "base/dictype/detailUpdate";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWinDetail = false;
                        vm.reloadUser();
                        vm.addOrUpdateFormDetail = {};
                        if (data.dictid == null) {
                            alert('新增成功');
                        } else {
                            alert('修改成功');
                        }

                    }else if (r.code == 401) {
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
            var orgIds = getOrgIds();
            if (orgIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/dictype/delete",
                    contentType: "application/json",
                    data: JSON.stringify(orgIds),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadOrg();
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
                        }else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        deleteOrgDetail: function () {
            var orgIds = getOrgIdsDetail();
            if (orgIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/dictype/detailDelete",
                    contentType: "application/json",
                    data: JSON.stringify(orgIds),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadUser();
                            vm.multipleSelectionDetail = [];
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
            //机构树点击事件
            //var children = treeNode.children;
            //
            //this.isLeaf = !!(children == null || children == '' || children == 'undefined');

            this.orgModel = true;
            this.orgNode = treeNode;
            this.orgid = treeNode.orgid;

            this.reloadOrg();
        },
        query: function () {
            isInitial = false;
            vm.reloadOrg();
        },
        queryDetail: function () {

            vm.reloadUser();
        },
        addOrUpdata: function (isInsert) {

            this.isInsert = isInsert;

            this.addOrUpdateForm.parentid = this.dicttypeid;

            if (isInsert) {
                this.addOrUpdateForm.title = '新增字典类型';
                this.readonly = false;
            } else {
                this.addOrUpdateForm = this.orgRecord;
                this.addOrUpdateForm.title = '修改字典类型';
                this.readonly = true;
            }

            vm.inputShow = false;

            this.addOrUpdateForm.isInsert = '0';

            this.addOrUpdateWin = true;

        },
        addOrUpdataDetail: function (isInsert) {

            this.isInsert = isInsert;
            this.addOrUpdateFormDetail.dicttype = vm.dicttype;
            if (isInsert) {
                this.addOrUpdateFormDetail.title = '新增业务字典';
                this.readonly = false;
            } else {
                this.addOrUpdateFormDetail = this.orgRecordDetail;
                this.addOrUpdateFormDetail.title = '修改业务字典';
                this.readonly = true;
            }


            vm.inputShow = false;


            this.addOrUpdateForm.isInsert = '0';

            this.addOrUpdateWinDetail = true;

        },
        addOrUpdataWinClose: function (val) {
            this.addOrUpdateWin = false;
        },
        addOrUpdataWinDetailClose: function (val) {
            this.addOrUpdateWinDetail = false;
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
        clearValidateDetail: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].clearValidate();
            this.addOrUpdateResetDetail();
        },
        /**
         * 选择框改变事件
         */
        selectChange: function (val) {
            vm.inputShow = '0' != val;
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFun: function (selection) {
            this.multipleSelection = selection;
        },
        changeFunDetail: function (selection) {
            this.multipleSelectionDetail = selection;
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.currentChange(1);
            }
        },
        handleSizeChangeDetail: function (val) {
            this.pageSizeDetail = val;
            this.currentChangeDetail(1);
        },
        currentChange1: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.currentChange(currentPage);
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.orgDetailData = null;
            this.totalDetail = 0;
            this.totalPageDetail = null;
            this.reloadOrg();
        },
        addOrUpdateReset: function () {
            //重置机构信息
            this.addOrUpdateForm = {};
            this.addOrUpdateForm.parentid = this.orgid;

            if (this.isInsert) {
                this.addOrUpdateForm.title = '新增字典类型';
                this.readonly = false;
            } else {
                this.addOrUpdateForm.title = '修改字典类型';
                this.readonly = true;
            }

            this.addOrUpdateForm.isInsert = '0';
        },
        addOrUpdateResetDetail: function () {
            //重置机构信息
            this.addOrUpdateFormDetail = {};

            if (this.isInsert) {
                this.addOrUpdateFormDetail.title = '新增业务字典';
                this.readonly = false;
            } else {
                this.addOrUpdateFormDetail.title = '修改业务字典';
                this.readonly = true;
            }

            this.addOrUpdateFormDetail.isInsert = '0';
        },
        /**
         * 行号 - 业务字典
         */
        dicttyIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 业务字典明细
         */
        dicttyDetailIndex: function (index) {
            return index + (this.currentPageDetail - 1) * this.pageSizeDetail + 1;
        },
        /**
         * 字典明细查询
         */
        queryUserAdded: function (dicttypeid) {
            vm.dicttype = dicttypeid;
            vm.showDicDetail = true;
            vm.dicttypeExist = dicttypeid;
            this.reloadUser(dicttypeid);
        },
        reloadOrg: function () {
            //刷新列表数据
            var data = this.queryForm;

            // data.dicttypeid = this.dicttypeid;
            data.page = this.currentPage;
            data.limit = this.pageSize;

            $.post(baseURL + 'base/dictype/list', data, function (r) {

                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
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
        /**
         * 刷新字典明细列表数据
         */
        reloadUser: function (dicttype) {
            var data = this.queryUserForm;

            if (dicttype) {
                data.dicttype = dicttype;
            } else if (!dicttype && vm.dicttypeExist) {
                data.dicttype = vm.dicttypeExist;
            }

            data.page = this.currentPageDetail;
            data.limit = this.pageSizeDetail;

            $.post(baseURL + 'base/dictype/detailList', data, function (r) {
                if (r.code === 0) {
                    vm.currentPageDetail = r.page.currPage;
                    vm.totalDetail = r.page.totalCount;
                    vm.totalPageDetail = r.page.totalPage;
                    vm.orgDetailData = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        openAddOrUpdataWin: function (data) {
            vm.orgRecord =
                {
                    dicttypeid: data.dicttypeid,
                    dicttypename: data.dicttypename,
                    dicttypecode: data.dicttypecode,
                    dicttypedesc: data.dicttypedesc,
                };
            vm.addOrUpdata(false)
        },
        openAddOrUpdataWinDetail: function (data) {
            vm.orgRecordDetail =
                {
                    dictid: data.dictid,
                    dicttype: vm.dicttype,
                    dictname: data.dictname,
                    dictcode: data.dictcode,
                    sortno: data.sortno,
                    status: data.status
                };
            vm.addOrUpdataDetail(false)
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChangeDetail: function (currentPageDetail) {
            this.currentPageDetail = currentPageDetail;
            this.reloadUser();
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

/***************获取选中的组织的id******************/
function getOrgIds() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var userIds = [];
    for (var i = 0; i < selection.length; i++) {
        userIds.push(selection[i].dicttypeid);
    }

    return userIds;
}

function getOrgIdsDetail() {
    var selection = vm.multipleSelectionDetail;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var userIds = [];
    for (var i = 0; i < selection.length; i++) {
        userIds.push(selection[i].dictid);
    }

    return userIds;
}