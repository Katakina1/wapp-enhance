
var isInitial = true;
$(function () {
    new AjaxUpload('#import', {
        action: baseURL + 'modules/goods/import?token=' + token,
        name: 'file',
        autoSubmit: true,
        onComplete: function (file, r) {
            var r = JSON.parse(r);
            if (r.code == 0) {
                vm.isShowList = true;
                vm.findTaxCode(1);
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
        readonly: true,
        pageTableData: [],
        total: 0,
        total2: 0,
        totalPage: 1,
        totalPage2: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pagerCount2: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageSize2: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pageList2: PAGE_PARENT.PAGE_LIST,
        currentPage: 1,
        currentPage2: 1,
        multipleSelection: [],
        tableData: [],
        isDisable: false,
        radio: null,
        listLoading: false,
        form: {
            goodsName: null,
            goodsCode: null,
            ssbmName: null,
            ssbmCode: null
        },
        isShowList: true,
        goodsForm: {
            id: null,
            goodsCode: null,
            goodsName: null,
            goodsRemark: null,
            ssbmId: null,
            ssbmCode: null,
            ssbmName: null,
            title: null
        },
        oldGoodsForm: {
            id: null,
            goodsCode: null,
            goodsName: null
        },
        queryTaxCodeForm: {
            ssbmCode: null,
            ssbmName: null,
            title: null,
        },
        addOrUpdateWin: false,
        selectTaxCodeWin: false,
        goodsRecord: []
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.isShowList = true;
            vm.findTaxCode(1);
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findTaxCode(1);
            }
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
            this.findAll(1);
        },
        queryTaxCode: function () {
            this.findAll(1);
        },
        queryReset: function () {
            vm.queryTaxCodeForm.ssbmName = null;
            vm.queryTaxCodeForm.ssbmCode = null;
        },
        findTaxCode: function (currentPage) {
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                goodsName: this.form.goodsName,
                goodsCode: this.form.goodsCode,
                ssbmCode: this.form.ssbmCode,
                ssbmName: this.form.ssbmName,
                isBlack: COMMON.NOT_BLACK
            };

            this.$http.post(baseURL + sysUrl.taxCodeQueryPaged,
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.pageTableData = [];
                flag = true;
                for (var key in response.data.page.list) {
                    this.$set(this.pageTableData, key, response.data.page.list[key]);
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
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findTaxCode(currentPage);
        },
        currentChange2: function (currentPage) {
            if (vm.total2 == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage2 = currentPage;
            }
            var params_ = {
                page: this.currentPage2,
                limit: this.pageSize2,
                ssbmName: this.queryTaxCodeForm.ssbmName,
                ssbmCode: this.queryTaxCodeForm.ssbmCode,
                isBlack: COMMON.NOT_BLACK
            };

            this.$http.post(baseURL + sysUrl.taxCodeList,
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total2 = response.data.page.totalCount;
                this.totalPage2 = response.data.page.totalPage;
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
            document.getElementById("ifile").src = baseURL + sysUrl.exportTaxCodeTemp;
        },
        deleteTaxCode: function () {
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
                                vm.findTaxCode(1);
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        submitForm: function () {
            this.isDisable = true;
            this.$refs['goodsForm'].validate(function (valid) {
                if (valid) {
                    vm.saveGoodsData();
                } else {
                    vm.isDisable = false;
                    return false
                }
            })
        },
        addOrUpdata: function (isInsert) {
            this.goodsForm.goodsCode = '';
            this.goodsForm.goodsName = '';
            this.goodsForm.goodsRemark = '';
            this.goodsForm.ssbmCode = '';
            this.goodsForm.ssbmName = '';
            if (isInsert) {
                this.goodsForm.title = '新增税收分类编码';
            } else {
                this.goodsForm = this.goodsRecord;
                this.goodsForm.title = '修改商品税收分类编码';
            }
            this.addOrUpdateWin = true;

        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].resetFields();
        },
        saveGoodsData: function () {

            var data = {
                id: null,
                goodsCode: null,
                goodsName: null,
                goodsRemark: null,
                ssbmCode: null,
                ssbmName: null,
                ssbmId: null
            };

            data.id = vm.goodsForm.id;
            data.ssbmId = vm.goodsForm.ssbmId;
            data.goodsCode = vm.goodsForm.goodsCode;
            data.goodsName = vm.goodsForm.goodsName;
            data.goodsRemark = vm.goodsForm.goodsRemark;
            data.ssbmName = vm.goodsForm.ssbmName;
            data.ssbmCode = vm.goodsForm.ssbmCode;

            if (this.oldGoodsForm.goodsCode == data.goodsCode) {
                data.goodsCode = null;
            }

            if (this.oldGoodsForm.goodsName == data.goodsName) {
                data.goodsName = null;
            }

            var url = data.id == null ? "modules/goods/save" : "modules/goods/update";

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
                        vm.findTaxCode(1);
                        vm.goodsForm.id = null;
                        vm.goodsForm.goodsCode = null;
                        vm.goodsForm.goodsName = null;
                        vm.goodsForm.goodsRemark = null;
                        vm.goodsForm.ssbmName = null;
                        vm.goodsForm.ssbmCode = null;
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
            this.goodsForm.ssbmCode = null;
            this.goodsForm.ssbmName = null;
            this.addOrUpdateWin = false;
        },
        closeTaxCodeWin: function () {
            this.queryTaxCodeForm.ssbmName = null;
            this.queryTaxCodeForm.ssbmCode = null;
            this.selectTaxCodeWin = false;
        },
        selectSsbmCode: function () {
            this.findAll(1);
            vm.queryTaxCodeForm.title = "查询税收分类编码";
            vm.selectTaxCodeWin = true;
        },
        comfirmSelect: function () {
            if (this.currentRow == null) {
                alert("请先选择税收分类编码!");
                return;
            }
            this.closeTaxCodeWin();
            this.goodsForm.ssbmCode = this.currentRow.ssbmCode;
            this.goodsForm.ssbmName = this.currentRow.ssbmName;
            this.goodsForm.ssbmId = this.currentRow.id;
        },
        showRow: function (row) {
            this.radio = this.tableData.indexOf(row);
        },
        handleCurrentChange: function (val, index) {
            this.currentRow = val;
            if (val == null) {
                this.radio = null;
            }
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
                        goodsRemark: data.goodsRemark,
                        ssbmCode: data.ssbmCode,
                        ssbmName: data.ssbmName
                    };
                    vm.oldGoodsForm = {
                        id: data.id,
                        goodsCode: data.goodsCode,
                        goodsName: data.goodsName
                    };
                    vm.addOrUpdata(false)
                }

            });
        },
        goodsGode: function (val) {
            vm.goodsForm.goodsCode = val;
        },
        goodsName: function (val) {
            vm.goodsForm.goodsName = val;
        },
        goodsRemark: function (val) {
            vm.goodsForm.goodsRemark = val;
        },
        ssbmCode: function (val) {
            vm.goodsForm.ssbmCode = val;
        },
        ssbmName: function (val) {
            vm.goodsForm.ssbmName = val;
        },
        /**
         * 行号 - 税收分类编码
         */
        taxcategoryIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 税收分类编码2
         */
        taxcategoryIndex2: function (index) {
            return index + (this.currentPage2 - 1) * this.pageSize2 + 1;
        }
    }
});