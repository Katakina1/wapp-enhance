
var isInitial = true;
$(function () {
    new AjaxUpload('#import', {
        action: baseURL + 'modules/enterpriseblack/import?token=' + token,
        name: 'file',
        autoSubmit: true,
        onComplete: function (file, r) {
            var r = JSON.parse(r);
            if (r.code == 0) {
                vm.isShowList = true;
                vm.findAll(1);
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
        tableData: [],
        currentPage: 1,
        total: 0,
        totalPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        listLoading: false,
        multipleSelection: [],
        form: {
            orgName: null,
            taxNo: null,
            comType: ''
        },
        /*enterpriseForm: {
         id: null,
         orgName: null,
         taxNo: null,
         comType: null,
         title: null
         },*/
        enterpriseForm: {},
        oldEnterpriseForm: {
            id: null,
            orgName: null,
            taxNo: null,
            comType: null
        },
        isShowList: true,
        addOrUpdateWin: false,
        enterpriseRecord: []
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.isShowList = true;
            this.findAll(1);
        },
        download: function () {
            document.getElementById("ifile").src = baseURL + sysUrl.exportEnterpriseBlack;
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {

            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                taxNo: this.form.taxNo,
                orgName: this.form.orgName,
                comType: this.form.comType
            };
            this.$http.post(baseURL + sysUrl.enterpriseBlackQueryPaged,
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.tableData = [];
                flag = true;
                for (var key in response.data.page.list) {
                    this.$set(this.tableData, key, response.data.page.list[key]);
                }
                this.listLoading = false;
            }).catch(function (response) {
                alert(response.data.msg);
                this.listLoading = false;
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
        deleteBlackEnterprise: function () {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要删除的数据!");
                return;
            }
            var orgIds = [];
            for (var i = 0; i < this.multipleSelection.length; i++) {
                orgIds.push(this.multipleSelection[i].id)
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "modules/enterpriseblack/delete",
                    contentType: "application/json",
                    data: JSON.stringify(orgIds),
                    success: function (r) {
                        if (r.code == 0) {
                            alert('删除成功', function () {
                                vm.findAll(1);
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        addOrUpdate: function (isInsert) {
            if (isInsert) {
                this.enterpriseForm.title = '新增企业黑名单';
            } else {
                this.enterpriseForm = this.enterpriseRecord;
                this.enterpriseForm.title = '修改企业黑名单';
            }
            this.addOrUpdateWin = true;

        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].clearValidate();
            this.enterpriseForm = {};
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        submitForm: function () {
            this.$refs['enterpriseForm'].validate(function (valid) {
                if (valid) {
                    vm.saveEnterpriseData();
                } else {
                    return false
                }
            })
        },
        saveEnterpriseData: function () {
            /* var data = {
             id: null,
             orgName: null,
             taxNo: null,
             comType: null
             };

             data.id = vm.enterpriseForm.id;
             data.orgName = vm.enterpriseForm.orgName;
             data.taxNo = vm.enterpriseForm.taxNo;
             data.comType = vm.enterpriseForm.comType;

             if (this.oldEnterpriseForm.orgName == data.orgName) {
             data.orgName = null;
             }

             if (this.oldEnterpriseForm.taxNo == data.taxNo) {
             data.taxNo = null;
             }*/
            var data = vm.enterpriseForm;

            var url = data.id == null ? "modules/enterpriseblack/save" : "modules/enterpriseblack/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWin = false;
                        vm.findAll(1);
                        vm.enterpriseForm = {};
                        if (data.id == null) {
                            alert('新增成功')
                        } else {
                            alert('修改成功')
                        }
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        closeWin: function () {
            this.addOrUpdateWin = false;
            //location.reload();
        },
        openUpdateWin: function (row) {
            $.post(baseURL + 'modules/enterpriseblack/getBlackEnterpriseById/' + row.id, function (r) {
                if (r.code == 0) {
                    var data = r.enterpriseInfo;
                    vm.enterpriseRecord = {
                        id: data.id,
                        orgName: data.orgName,
                        taxNo: data.taxNo,
                        comType: data.comType
                    };
                    vm.oldEnterpriseForm = {
                        id: data.id,
                        orgName: data.orgName,
                        taxNo: data.taxNo,
                        comType: data.comType
                    };
                    vm.addOrUpdate(false);
                }

            });
        },
        assignNullValue: function (row, column, cellValue, index) {
            var trimCellvalue = $.trim(cellValue);
            if (trimCellvalue == null || trimCellvalue == '') {
                return "—— ——";
            } else {
                return cellValue;
            }
        },

        orgName: function (val) {
            vm.enterpriseForm.orgName = val;
        },

        taxNo: function (val) {
            vm.enterpriseForm.taxNo = val;
        },

        comType: function (val) {
            vm.enterpriseForm.comType = val;
        },
        /**
         * 行号 - 企业黑名单
         */
        enterpriseblackIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});
