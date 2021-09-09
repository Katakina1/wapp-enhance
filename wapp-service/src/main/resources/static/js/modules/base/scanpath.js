
/**
 * 扫描点
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
        scanpathData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        items: [
            {text: '是', value: '1'},
            {text: '否', value: '0'}
        ],
        queryForm: {
            scanPath: null,
            invoiceRemark: null
        },
        addOrUpdateForm: {},
        isLeaf: false,
        orgNode: null,
        scanpathRecord: [],
        multipleSelection: [],
        addOrUpdateWin: false,
        menuGridShow: true,
        scanpathLoading: false
    },
    mounted: function () {
    	 // this.menuGridShow = true;
    	 //页码初始化
        this.currentPage=1;

        // this.reloadScanPath();
    },
    methods: {
       
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveScanPathData();
                } else {
                    return false
                }
            })
        },
        saveScanPathData: function () {

            var data = vm.addOrUpdateForm;

            var url = data.id == null ? "base/scanpath/saveScanPath" : "base/scanpath/updateScanPath";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.reloadScanPath();
                        vm.addOrUpdateWin = false;
                        if (data.id == null) {
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
            var scanpathIds = getSelectedRows();
            if (scanpathIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/scanpath/deleteScanPath",
                    contentType: "application/json",
                    data: JSON.stringify({ids: scanpathIds}),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadScanPath();
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
            this.orgNode = treeNode;

            //页码初始化
            this.currentPage=1;

            this.reloadScanPath();
        },
        query: function () {

            vm.reloadScanPath();
        },
        reloadScanPath: function () {
            //刷新列表数据
            vm.scanpathLoading = true;

            var data = this.queryForm;

            data.page = this.currentPage;
            data.limit = this.pageSize;

            $.post(baseURL + 'base/scanpath/list', data, function (r) {
                vm.scanpathLoading = false;
                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.scanpathData = r.page.list;
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

            if (isInsert) {
                this.addOrUpdateForm.title = '新增扫描点';
                this.addOrUpdateForm.id = null;
                this.addOrUpdateForm.ssshit = false;
            } else {
                this.addOrUpdateForm = this.scanpathRecord;
                this.addOrUpdateForm.title = '修改扫描点';
                this.addOrUpdateForm.ssshit = true;
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
            this.reloadScanPath();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadScanPath();
        },
        openAddOrUpdateWin: function (data) {
            vm.scanpathRecord = {
                id: data.id,
                scanPath: data.scanPath,
                profit: data.profit,
                pushErp: data.pushErp,
                invoiceRemark: data.invoiceRemark
            };
            vm.addOrUpdata(false)
        },
        addOrUpdateReset: function () {
            //重置扫描点信息
            this.addOrUpdateForm.scanPath = null;
            this.addOrUpdateForm.id = null;
            this.addOrUpdateForm.invoiceRemark = null;
        },
        /**
         * 行号 - 扫描点
         */
        scanpathIndex: function (index) {
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
        },
        formatProfit: function (row, column) {
            if (row.profit == '1') {
                return '是';
            } else {
                return '否';
            }
        },
        formatPushErp: function (row, column) {
            if (row.pushErp == '1') {
                return '是';
            } else {
                return '否';
            }
        }
    }
});

/***************打开窗口 新增/修改******************/
function openAddOrUpdateWin(id) {
    $.post(baseURL + 'base/scanpath/getScanPathInfoById/' + id, function (r) {
        var data = r.scanpathInfo;
        vm.scanpathRecord = {
            id: data.id,
            scanPath: data.scanPath,
            id: data.id,
            invoiceRemark: data.invoiceRemark
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
        ids.push(selection[i].id);
    }

    return ids;
}