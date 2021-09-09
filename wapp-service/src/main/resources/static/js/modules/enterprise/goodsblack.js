
var isInitial = true;
$(function () {
    new AjaxUpload('#import', {
        action: baseURL + 'modules/goodsblack/import?token=' + token,
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
        total: 0,
        totalPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        currentPage: 1,
        listLoading: false,
        multipleSelection: [],
        isDisable: false,
        form: {
            goodsName: null
        },
        goodsForm: {
            id: null,
            goodsCode: null,
            goodsName: null,
            goodsRemark: null,
            title: null
        },
        oldGoodsForm: {
            id: null,
            goodsCode: null,
            goodsName: null,
            goodsRemark: null,
        },
        isShowList: true,
        addOrUpdateWin: false,
        goodsRecord: [],
        // goodsForm:[],
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.isShowList = true;
            vm.findAll(1);
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
                goodsName: this.form.goodsName,
                isBlack: COMMON.IS_BLACK
            };

            this.$http.post(baseURL + sysUrl.goodsBlackQueryPaged,
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
        download: function () {
            document.getElementById("ifile").src = baseURL + sysUrl.exportGoodsBlackTemp;
        },
        deleteBlackGoods: function () {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要删除的数据!");
                return;
            }
            var goodsIds = [];
            for (var i = 0; i < this.multipleSelection.length; i++) {
                goodsIds.push(this.multipleSelection[i].id)
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "modules/goods/delete",
                    contentType: "application/json",
                    data: JSON.stringify(goodsIds),
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
        addOrUpdata: function (isInsert) {
            this.goodsForm.goodsCode = '';
            this.goodsForm.goodsName = '';
            this.goodsForm.goodsRemark = '';
            if (isInsert) {
                this.goodsForm.title = '新增商品黑名单';
            } else {
                this.goodsForm = this.goodsRecord;
                this.goodsForm.title = '修改商品黑名单';
            }
            this.addOrUpdateWin = true;

        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].resetFields();
        },
        submitForm: function () {
            vm.isDisable = true;
            this.$refs['goodsForm'].validate(function (valid) {
                if (valid) {
                    vm.saveGoodsData();
                } else {
                    vm.isDisable = false;
                    return false
                }
            })
        },
        saveGoodsData: function () {

            var data = {
                id: null,
                goodsCode: null,
                goodsName: null,
                goodsRemark: null
            };

            data.id = vm.goodsForm.id;
            data.goodsCode = vm.goodsForm.goodsCode;
            data.goodsName = vm.goodsForm.goodsName;
            data.goodsRemark = vm.goodsForm.goodsRemark;

            if (this.oldGoodsForm.goodsCode == data.goodsCode) {
                data.goodsCode = null;
            }

            if (this.oldGoodsForm.goodsName == data.goodsName) {
                data.goodsName = null;
            }

            var url = data.id == null ? "modules/goodsblack/save" : "modules/goods/update";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        if (data.id == null) {
                            alert('新增成功')
                        } else {
                            alert('修改成功')
                        }
                        vm.findAll(1);
                        vm.goodsForm.id = null;
                        vm.goodsForm.goodsCode = null;
                        vm.goodsForm.goodsName = null;
                        vm.goodsForm.goodsRemark = null;
                        vm.addOrUpdateWin = false;
                        vm.isDisable = false;

                    } else {
                        alert(r.msg);
                        vm.isDisable = false;
                    }
                }
            });
        },
        closeWin: function () {
            this.goodsForm.id = null;
            this.goodsForm.goodsCode = null;
            this.goodsForm.goodsName = null;
            this.goodsForm.goodsRemark = null;
            this.addOrUpdateWin = false;
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        openUpdateWin: function (row) {
            $.post(baseURL + 'modules/goods/getGoodsById/' + row.id, function (r) {
                if (r.code == 0) {
                    var data = r.goodsInfo;
                    vm.goodsRecord = {
                        id: data.id,
                        goodsCode: data.goodsCode,
                        goodsName: data.goodsName,
                        goodsRemark: data.goodsRemark
                    };
                    vm.oldGoodsForm = {
                        id: data.id,
                        goodsCode: data.goodsCode,
                        goodsName: data.goodsName,
                        goodsRemark: data.goodsRemark
                    };
                    vm.addOrUpdata(false)
                }

            });
        },
        goodsCode: function (val) {
            vm.goodsForm.goodsCode = val;
        },
        goodsName: function (val) {
            vm.goodsForm.goodsName = val;
        },
        goodsRemark: function (val) {
            vm.goodsForm.goodsRemark = val;
        },
        /**
         * 行号 - 商品黑名单
         */
        goodsblackIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});