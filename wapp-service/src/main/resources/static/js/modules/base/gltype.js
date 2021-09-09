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
            remark: null,
            matchName1: null,
            matchName2: null,
            glName: null,
        },
        addOrUpdateForm: {
            glType:'0'
        },
        orgid: null,
        isLeaf: false,
        orgNode: null,
        billtypeRecord: [],
        multipleSelection: [],
        addOrUpdateWin: false,
        menuGridShow: false,
        billtypeLoading: false,
        orgLoading: false
    },
    watch: {

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

            var url = data.id == null ? "base/gltype/saveBillType" : "base/gltype/updateBillType";
            if(data.glType=='0'){
              if(data.remark==null||data.remark==''){
                  alert("请输入remark!");
                return;
              }
             }else if(data.glType=='1'){
                if(data.matchName1==null||data.matchName1==''){
                    alert("请输入matchName1!");
                    return;
                }
            }else if(data.glType=='2'){
                if(data.matchName2==null||data.matchName1==null||data.matchName1==''||data.matchName2==''){
                    alert("请输入matchName1和matchName2!");
                    return;
                }
            }
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.reloadBillType();
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
            var billtypeIds = getSelectedRows();
            if (billtypeIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/gltype/deleteBillType",
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


            data.page = this.currentPage;
            data.limit = this.pageSize;

            $.post(baseURL + 'base/gltype/list', data, function (r) {
                vm.billtypeLoading = false;
                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.billtypeData = r.page.list;
                    if(vm.total>0){
                        $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                    }
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
                this.addOrUpdateForm.id = null;
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
                id: data.id,
                remark: data.remark,
                matchName1: data.matchName1,
                matchName2: data.matchName2,
                glType: data.glType,
                glName: data.glName
            };
            vm.addOrUpdata(false)
        },
        addOrUpdateReset: function () {
            //重置业务类型信息
            this.addOrUpdateForm.remark = null;
            this.addOrUpdateForm.matchName1 = null;
            this.addOrUpdateForm.matchName2 = null;
            this.addOrUpdateForm.glType=null;
            this.addOrUpdateForm.glName=null;
        },
        jvStoreTemplate:function(){
            document.getElementById("ifile").src = baseURL +"export/aribaBillTypeTemplate";
        },
        uploadFile: function (event) {
            if (!vm.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                const formData = new FormData();
                formData.append('file', vm.file);
                formData.append('token', vm.token);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                vm.tempTableData = vm.tableData;
                vm.tableData = [];
                vm.orgLoading = true;
                var flag = false
                var hh;
                const url = baseURL + "export/upload_aribaBillType";
                vm.$http.post(url, formData, config).then(function (response) {
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    vm.file = '';
                        alert(response.data.msg);
                        vm.orgLoading = false;
                        vm.query();
                    $("#upload_form")[0].reset();
                        return;

                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);
            }
        },
        exportExcel: function(){
            $("#export_btn").attr("disabled","true").addClass("is-disabled");
            var data = this.queryForm;
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':61,'condition':JSON.stringify(data)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");
                    }

                }
            });
        },
        onChangeFile: function (event) {
            const str = $("#file").val();
            const index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    vm.file = event.target.files[0];
                    vm.isNeedFileExtension = true;
                    //截取名称最后18位
                    vm.selectFileFlag = event.target.files[0].name;
                }
            }
        },
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
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
    $.post(baseURL + 'base/gltype/getBillTypeInfoById/' + billtypeid, function (r) {
        var data = r.billtypeInfo;
        vm.billtypeRecord = {
            id: data.id,
            remark: data.remark,
            matchName1: data.matchName1,
            matchName2: data.matchName2,
            glType: data.glType,
            glName: data.glName,
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