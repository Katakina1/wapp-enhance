
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
        jvs :[],
        jvStoreData: [],
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
            jvcode: null,
            storeTax: null
        },
        addOrUpdateForm: {},
        isLeaf: false,
        orgNode: null,
        jvStoreRecord: [],
        multipleSelection: [],
        addOrUpdateWin: false,
        jvcodeLoading: false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
    },
    mounted: function () {

        //页码初始化
        this.currentPage=1;
        this.jvList();

    },
    methods: {
        jvList:function(){
            this.queryForm = {};
            $.post(baseURL + 'report/invoiceProcessingStatusReport/searchGf',function(r){
                var jvs = [];
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].value+"("+r.optionList[i].label+")";
                    jvs.push(gf);
                }
                vm.jvs = jvs;
            });
        },
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    confirm("确定保存？",function () {
                        vm.savejvStoreData();
                    });

                } else {
                    return false
                }
            })
        },
        savejvStoreData: function () {

            var data = vm.addOrUpdateForm;

            var url = data.title == "新增" ? "base/jvstore/save" : "base/jvstore/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.reloadjvcode();
                        vm.addOrUpdateWin = false;
                        if ("新增" == data.title) {
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
                        } else if ("修改" == data.title){
                            alert('修改成功');
                        }

                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        deleteOrg: function () {
            var selection = vm.multipleSelection;

            if (selection.length == 0) {
                alert("请选择要操作的记录");
                return;
            }
            var jvcodes = [];
            var storeCodes = [];

            for (var i = 0; i < selection.length; i++) {
                jvcodes.push(selection[i].jvcode);
                storeCodes.push(selection[i].storeCode);
            }
            if (jvcodes == null || storeCodes == null) {
                return;
            }
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/jvstore/delete",
                    contentType: "application/json",
                    data: JSON.stringify({jvcodes:jvcodes,storeCodes:storeCodes}),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadjvcode();
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
            this.orgNode = treeNode;

            //页码初始化
            this.currentPage=1;

            this.reloadjvcode();
        },
        query: function () {
            //表单校验
            this.$refs['queryForm'].validate(function (valid) {
                if (valid) {
                    vm.reloadjvcode();
                }
            })

        },
        reloadjvcode: function () {
            //刷新列表数据
            vm.jvcodeLoading = true;

            var data = this.queryForm;
            data.page = this.currentPage;
            data.limit = this.pageSize;
            this.jvList();
            $.post(baseURL + 'base/jvstore/list', data, function (r) {
                vm.jvcodeLoading = false;
                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.jvStoreData = r.page.list;
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
                this.addOrUpdateForm= {};
                this.addOrUpdateForm.title = '新增';
                this.addOrUpdateForm.ssshit = false;
            } else {
                this.addOrUpdateForm = this.jvStoreRecord;
                this.addOrUpdateForm.title = '修改';
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
            this.reloadjvcode();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadjvcode();
        },
        openAddOrUpdateWin: function (row) {
            vm.jvStoreRecord = {
                id:row.id,
                jvcode: row.jvcode,
                storeCode: row.storeCode,
                storeChinese: row.storeChinese,
                storeTax: row.storeTax,
                jvcodeName: row.jvcodeName,
                taxpayerCode: row.taxpayerCode
            };
            vm.addOrUpdata(false);

        },
        addOrUpdateReset: function () {
            //重置扫描点信息
            // this.addOrUpdateForm.jvcode = null;
            // this.addOrUpdateForm.id = null;
            // this.addOrUpdateForm.invoiceRemark = null;
        },
        /**
         * 行号 - 扫描点
         */
        jvcodeIndex: function (index) {
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
        },
        jvStoreTemplate:function(){
            console.log("ifile");
            document.getElementById("ifile").src = baseURL +"export/jvStoreTemplate";
        },
        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },
    }
});
/***************导入数据模板******************/
$(function () {
    var loading;
    new AjaxUpload('#import', {
        action: baseURL + 'export/upload_jvStore' ,
        name: 'file',
        autoSubmit: true,
        data:{type:"jvStore"},
        onSubmit: function(file, extension){
            loading = vm.getLoading("导入中...");
        },
        onComplete: function (file, r) {
            loading.close();
            if(r==null){
                alert("系统异常,请联系管理员！");
            }
            var r = JSON.parse(r);
            if (r.code == 0) {
                vm.query();
                alert(r.msg);
            } else if (r.code == 401) {
                alert("登录超时，请重新登录", function () {
                    parent.location.href = baseURL + 'login.html';
                });
            } else {
                alert(r.msg);
            }
        }
    });
});

/***************打开窗口 新增/修改******************/
function openAddOrUpdateWin(id) {
    $.post(baseURL + 'base/jvcode/getjvcodeInfoById/' + id, function (r) {
        var data = r.jvcodeInfo;
        vm.jvStoreRecord = {
            id: data.id,
            jvcode: data.jvcode,
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
    var map = [];

    for (var i = 0; i < selection.length; i++) {

    }


    return map;
}