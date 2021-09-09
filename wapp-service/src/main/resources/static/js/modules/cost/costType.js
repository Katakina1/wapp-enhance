
var isInitial = true;
$(function () {
    new AjaxUpload('#import', {
        action: baseURL + 'costType/cost/upload?token=' + token,
        name: 'file',
        autoSubmit: true,
        onComplete: function (file, r) {
            var r = JSON.parse(r);
            if (r.code == 0) {
                vm.reloadCost();
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
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    data: {
        costData: [],
        total: 0,
        totalPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        currentPage: 1,
        listLoading: false,
        isInsert: true,
        title:null,
        multipleSelection: [],
        isDisable: false,
        costRecord: [],
        queryForm: {
            venderId: null,
            costType: null,
            costTypeName: null
        },
        addOrUpdateForm: {
        },
        addOrUpdateWin: false,
        costRecord: [],
        // goodsForm:[],
    },
    methods: {
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveCostData();
                } else {
                    return false
                }
            })
        },
        saveCostData: function () {
            var data = vm.addOrUpdateForm;

            var url = data.id == null ? "costType/save" : "costType/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWin = false;
                        vm.reloadCost();
                        vm.addOrUpdateForm = {};
                        if (data.id == null) {
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
        deleteCost: function () {
            var costIds = getCostIds();
            if (costIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "costType/delete",
                    contentType: "application/json",
                    data: JSON.stringify(costIds),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadCost();
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
        addOrUpdate: function (isInsert) {

            vm.isInsert = isInsert;

            if (isInsert) {
                vm.title = '新增费用类型';
            } else {
                this.addOrUpdateForm = this.costRecord;
                vm.title = '修改费用类型';
            }

            vm.addOrUpdateWin = true;

        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].resetFields();
        },
        query: function () {
            isInitial = false;
            vm.reloadCost();
        },
        addOrUpdataWinClose: function (val) {
            vm.addOrUpdateForm={};
            this.addOrUpdateWin = false;
        },
        download: function () {
            document.getElementById("ifile").src = baseURL + "costType/downloadTemplate?token=" + token;
        },
        resetForm: function (formName) {
            //对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
            this.$refs[formName].resetFields();
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
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.currentChange(1);
            }
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.reloadCost();
        },
        /**
         * 行号
         */
        costIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        reloadCost: function () {
            //刷新列表数据
            var data = this.queryForm;

            // data.dicttypeid = this.dicttypeid;
            data.page = this.currentPage;
            data.limit = this.pageSize;

            $.post(baseURL + 'costType/list', data, function (r) {

                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.costData = r.page.list;

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
            vm.costRecord = {
                id: data.id,
                venderId: data.venderId,
                costType: data.costType,
                costTypeName: data.costTypeName,
                isContract: data.isContract
            };
            vm.addOrUpdate(false)
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

/***************获取选中的费用类型的id******************/
function getCostIds() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var costIds = [];
    for (var i = 0; i < selection.length; i++) {
        costIds.push(selection[i].id);
    }

    return costIds;
}

function verificationQueryVender(t) {
    var reg = /[^\d]/g;
    vm.queryForm.venderId = t.value.replace(reg,'');
};

function verificationAddVender(t) {
    var reg = /[^\d]/g;
    vm.addOrUpdateForm.venderId = t.value.replace(reg,'');
};

function verificationQueryCostType(t) {
    var reg = /[^\d]/g;
    vm.queryForm.costType = t.value.replace(reg,'');
};

function verificationAddCostType(t) {
    var reg = /[^\d]/g;
    vm.addOrUpdateForm.costType = t.value.replace(reg,'');
};
