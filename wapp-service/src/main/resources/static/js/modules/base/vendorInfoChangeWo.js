
var isInitial = true;

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
        queryForm: {
            venderId: null,
            costType: null,
            costTypeName: null
        }
    },
    methods: {
        auditAgree: function () {
            var ids = getIds();
            if (ids == null) {
                return;
            }

            confirm('确定要通过选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "vendorInfoChangeWo/auditAgree",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.findAll();
                            vm.multipleSelection = [];
                            alert('审核成功');
                        } else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                parent.location.href = baseURL + 'login.html';
                            });
                        }else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        findAll: function () {
            //刷新列表数据
            var data = this.queryForm;

            data.page = this.currentPage;
            data.limit = this.pageSize;
            vm.listLoading=true;
            $.post(baseURL + 'vendorInfoChangeWo/list', data, function (r) {
                vm.listLoading=false;
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
        query: function () {
            isInitial = false;
            vm.findAll();
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
            this.findAll();
        },
        /**
         * 行号
         */
        costIndex: function (index) {
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

/***************获取选中id******************/
function getIds() {
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

