
/**
 * 电票查询
 */
var vm = new Vue({
    el: '#rrapp',
    data: {
        options: [],
        listData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pageCount: 0,
        total: 0,
        modifyRow: "",
        invoice: {
            gfTaxNo: "",
            invoiceNo: "",
            qsStartDate: new Date().getFullYear() + "-" + format2(new Date().getMonth() ) + "-01",
            qsEndDate: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        exportQuery: {
            gfTaxNo: "",
            invoiceNo: "",
            qsStartDate: "",
            qsEndDate: ""
        },
        updateWinShow: false,
        updateInvoice: {
            id: "",
            invoiceDate: "",
            invoiceCode: "",
            invoiceNo: "",
            invoiceAmount: "",
            checkCode: ""
        },
        imageShow: false,
        invoiceId: "",
        invoiceEditId: "",
        exportButtonFlag:true,
        haveImageShow: false,
        noImageShow: false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        qsStartDateOptions: {},
        qsEndDateOptions: {},
        listLoading: false
    },
    mounted: function () {
        this.getGfName();
        this.qsStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.qsEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength", "50");
    },
    watch: {
        'invoice.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoice.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        handleSizeChange: function (val) {
            if(vm.total > 0){
                vm.pageSize = val;
                vm.reloadInvoice();
            }
        },
        /**
         * 获取购方税号和名称
         */
        getGfName: function () {
            $.ajax({
                type: "post",
                url: baseURL + "modules/collect/getGfName?token=" + token,
                success: function (response) {
                    response.splice(0, 0, {orgName: "全部", orgTaxNo: ''});
                    for (var i = 0; i < response.length; i++) {
                        vm.$set(vm.options, i, response[i]);
                    }
                },
                error: function (response) {
                    alert("加载购方信息出错");
                }
            });
        },
        /**
         * 查询
         */
        reloadInvoice: function () {
            $(".checkMsg").remove();
            var checkKPDate = true;
            var qsStartDate = new Date(vm.invoice.qsStartDate);
            var qsEndDate = new Date(vm.invoice.qsEndDate);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            if ( (qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.invoice.qsStartDate)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if(!checkKPDate){
                return;
            }
            if (checkKPDate) {
                $("#exportBtn").addClass("is_disabled");
                $("#exportBtn").attr("disabled", "disabled");

                //刷新查询的发票列表数据
                var data = {
                    gfTaxNo: this.invoice.gfTaxNo,
                    invoiceNo: this.invoice.invoiceNo,
                    qsStartDate: this.invoice.qsStartDate != null || this.invoice.qsStartDate != "" ? new Date(this.invoice.qsStartDate).getTime() : "",
                    qsEndDate: this.invoice.qsEndDate != null || this.invoice.qsEndDate != "" ? new Date(this.invoice.qsEndDate).getTime() : "",
                    page: this.currentPage,
                    limit: this.pageSize
                };
                vm.listLoading = true;
                $.post(baseURL + 'electron/select/list', data, function (r) {
                    if (r.code == 0) {
                        $('#totalStatistics').html("合计数量: "+r.page.totalCount+"条, 合计金额: "+formatMoney(r.totalAmount)+"元, 合计税额: "+formatMoney(r.totalTax)+"元");
                        vm.total = r.page.totalCount;
                        vm.currentPage = r.page.currPage;
                        vm.pageCount = r.page.totalPage;
                        vm.listData = r.page.list;
                        vm.listLoading = false;
                        if (r.page.list.length > 0) {
                            /*$("#exportBtn").removeClass("is_disabled");
                            $("#exportBtn").removeAttr("disabled");*/
                            vm.exportButtonFlag = false;
                            vm.exportQuery.gfTaxNo = data.gfTaxNo;
                            vm.exportQuery.invoiceNo = data.invoiceNo;
                            vm.exportQuery.qsStartDate = data.qsStartDate;
                            vm.exportQuery.qsEndDate = data.qsEndDate;
                        }else{
                            vm.exportButtonFlag = true;
                        }
                    }

                });
            }
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.reloadInvoice();
            }

        },
        /**
         * 删除电子发票
         * @param t
         * @param id 删除的发票的id
         */
       /* deleteSelectInvoice: function (id) {
            if (id != null && id != "") {
                layer.confirm("确定删除选中的数据？", function (index) {
                    layer.close(index);
                    var loading = vm.getLoading("正在删除，请稍后...");
                    $.ajax({
                        type: "POST",
                        url: baseURL + 'electron/delete?id=' + id + "&token=" + token,
                        contentType: "application/json",
                        success: function (r) {
                            loading.close();
                            if (r.code == 1) {
                                vm.reloadInvoice();
                                alert(r.msg);
                            } else {
                                alert(r.msg);
                            }
                        },
                        error: function () {
                            loading.close();
                            alert("系统繁忙！请稍后重试");
                        }
                    });
                });
            } else {
                alert("删除成功！");
            }
        },*/
        /**
         * 修改发票信息窗口显示
         * @param id 发票的id
         * @param scanId 发票图片的唯一标识，若为空，则表示没有图片属于手工录入
         */
        updateWindowShow: function (id, scanId) {
            if (id != null && id != "") {
                //填充图片
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/selectToUpdate?id=' + id + "&token=" + token,
                    contentType: "application/json",
                    success: function (r) {
                        if (r != null && r.code == 0 && r.invoice != null) {
                            vm.updateWinShow = true;
                            if (scanId != null) {
                                if (vm.invoiceEditId != id) {
                                    vm.invoiceEditId = id;
                                    vm.haveImageShow = true;
                                    vm.noImageShow = false;
                                    $('#edit_image_area').attr('src', "");

                                    $.ajax({
                                        type: "POST",
                                        url: baseURL + 'electron/checkImageToken',
                                        contentType: "application/json",
                                        success: function (r) {
                                            document.getElementById("edit_image_area").src = '/dxhy-gylpt/electron/getImageForAll?id=' + id + "&token=" + token;
                                        },
                                        error: function () {

                                        }
                                    });
                                }
                            } else {
                                vm.haveImageShow = false;
                                vm.noImageShow = true;
                            }

                            vm.updateInvoice.id = r.invoice.id;
                            vm.updateInvoice.invoiceCode = r.invoice.invoiceCode;
                            vm.updateInvoice.invoiceNo = r.invoice.invoiceNo;
                            vm.updateInvoice.invoiceDate = r.invoice.invoiceDate;
                            vm.updateInvoice.invoiceAmount = r.invoice.invoiceAmount;
                            vm.updateInvoice.checkCode = r.invoice.checkCode;
                        } else {
                            alert("系统错误！")
                        }

                    }
                });
            } else {
                alert("修改数据有误！请刷新页面");
            }

        },
        /**
         * 校验表单信息，并保存
         */
        checkUpdateMsg: function () {
            this.$refs['updateInvoice'].validate(function (valid) {
                if (valid) {
                    vm.updateInvoiceToSave();
                } else {
                    return false
                }
            });
        },
        /**
         * 保存修改的电票信息
         */
        updateInvoiceToSave: function () {

            var data = {
                id: vm.updateInvoice.id,
                invoiceDate: new Date(vm.updateInvoice.invoiceDate).getTime(),
                invoiceCode: vm.updateInvoice.invoiceCode,
                invoiceNo: vm.updateInvoice.invoiceNo,
                checkCode: vm.updateInvoice.checkCode
            };
            var loading = vm.getLoading("正在保存，请稍后...");
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/update/save',
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    loading.close();
                    if (r != null && r.code == '0') {
                        if (r.invoice.checkSuccess) {
                            vm.replaceItem(vm.modifyRow, r.invoice);
                            alert("保存成功！");
                            vm.updateWinShow = false;
                        } else {
                            alert(r.invoice.resultTip);
                        }
                    } else {
                        alert(r.msg);
                    }
                },
                error: function () {
                    loading.close();
                    alert("系统错误！请稍后再试");
                }
            });
        },
        /**
         * 修改行信息
         * @param row
         * @param targetData
         */
        replaceItem: function (row, targetData) {
            for (var i = 0; i < this.listData.length; i++) {
                if (this.listData[i] == row) {
                    this.listData.splice(i, 1, targetData);
                    break;
                }
            }
        },
        /**
         * 查看图片
         * @param id
         */
        imageWindowShow: function (id) {
            vm.imageShow = true;
            if (vm.invoiceId != id) {
                vm.invoiceId = id;
                $('#get_image_area').attr('src', "");
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/checkImageToken',
                    contentType: "application/json",
                    success: function (r) {
                        document.getElementById("get_image_area").src = '/dxhy-gylpt/electron/getImageForAll?id=' + id + "&token=" + token;
                    },
                    error: function () {

                    }
                });

            }
        },
        /**
         * 导出电票
         */
        exportElectronInvoice: function () {
            //$("exportElectronInvoice").unbind("click");
            //$("#exportBtn").attr("disabled","true");
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src = baseURL + 'electron/export?gfTaxNo=' + vm.exportQuery.gfTaxNo +
                        '&invoiceNo=' + vm.exportQuery.invoiceNo + '&qsStartDate=' + vm.exportQuery.qsStartDate + '&qsEndDate=' + vm.exportQuery.qsEndDate +
                        '&token=' + token;
                },
                error: function () {

                }
            });

        },
        /**
         * 点击弹出窗右上角的关闭时
         */
        beforeCloseUpdateWin: function () {
            vm.updateWinShow = false;
        },
        /**
         * 关闭图片窗口
         */
        beforeCloseImgWin: function () {
            vm.imageShow = false;
        },
        /**
         * 改变金额时改变价税合计金额
         */
        checkInvoiceAmount: function () {
            vm.updateInvoice.invoiceAmount = this.formatAmount(vm.updateInvoice.invoiceAmount);
        },
        /**
         * 格式化用户输入的金额
         * @param amount 金额
         */
        formatAmount: function (amount) {
            amount = amount.replace(/[^\d.-]/g, ""); //清除“数字”和“.”以外的字符
            amount = amount.replace(/^\./g, ""); //验证第一个字符是数字而不是.
            amount = amount.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的
            amount = amount.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            amount = amount.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数

            return amount == '' ? 0 : amount;
        },
        /**
         * 显示图片窗口
         * @param index
         * @param row
         */
        showImageWin: function (index, row) {
            vm.imageWindowShow(row.id);
        },
        /**
         * 删除发票
         * @param index
         * @param row
         */
        deleteInvoice: function (index, row) {
            var id=row.id;
           // vm.deleteSelectInvoice(row.id);
            //alert("uduuioioioi");
                if (id != null && id != "") {
                    parent.layer.confirm("确定删除选中的数据？", function (index) {
                        parent.layer.close(index);
                        var loading = vm.getLoading("正在删除，请稍后...");
                        $.ajax({
                            type: "POST",
                            url: baseURL + 'electron/delete?id=' + id + "&token=" + token,
                            contentType: "application/json",
                            success: function (r) {
                                loading.close();
                                if (r.code == 1) {
                                    vm.reloadInvoice();
                                    alert(r.msg);
                                } else {
                                    alert(r.msg);
                                }
                            },
                            error: function () {
                                loading.close();
                                alert("系统繁忙！请稍后重试");
                            }
                        });
                    });
                } else {
                    alert("删除成功！");
                }

        },
        /**
         * 显示修改页面
         * @param index
         * @param row
         */
        showUpdateWin: function (index, row) {
            vm.modifyRow = row;
            vm.updateWindowShow(row.id, row.scanId);
        },
        /**
         * 格式化签收状态
         * @param row
         * @param column
         * @param cellValue
         * @return {*}
         */
        formatterQsStatus: function (row, column, cellValue) {
            if (cellValue == '0') {
                return '签收失败'
            }
            if (cellValue == '1') {
                return '签收成功'
            }
        },
        /**
         * 格式化签收方式
         * @param row
         * @param column
         * @param cellValue
         * @return {*}
         */
        formatterQsType: function (row, column, cellValue) {
            if (cellValue == '0') {
                return '扫码签收';
            }
            if (cellValue == '1') {
                return '扫描仪签收';
            }
            if (cellValue == '2') {
                return 'app签收';
            }
            if (cellValue == '3') {
                return '导入签收';
            }
            if (cellValue == '5') {
                return 'pdf上传';
            }
        },
        /**
         * 格式化日期
         * @param row
         * @param column
         * @param cellValue
         */
        formatterKprq: function (row, column, cellValue) {
            return cellValue.substring(0, 11);

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
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time);
            var seperator1 = "-";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            return date.getFullYear() + seperator1 + month + seperator1 + strDate;
        },
        /**
         * 参数说明：
         * number：要格式化的数字
         * decimals：保留几位小数
         * dec_point：小数点符号
         * thousands_sep：千分位符号
         */
        amountFormat: function (row, column, cellValue) {
            var number = cellValue;
            var decimals = 2;
            var dec_point = ".";
            var thousands_sep = ",";
            number = (number + '').replace(/[^0-9+-Ee.]/g, '');
            var n = !isFinite(+number) ? 0 : +number,
                prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
                sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
                dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
                s = '',
                toFixedFix = function (n, prec) {
                    var k = Math.pow(10, prec);
                    return '' + Math.ceil(n * k) / k;
                };

            s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
            var re = /(-?\d+)(\d{3})/;
            while (re.test(s[0])) {
                s[0] = s[0].replace(re, "$1" + sep + "$2");
            }

            if ((s[1] || '').length < prec) {
                s[1] = s[1] || '';
                s[1] += new Array(prec - s[1].length + 1).join('0');
            }
            return s.join(dec);
        },
        /**
         * 选择日期时添加样式
         * @param val
         */
        addDateStyle: function (val) {
            if (val == 1) {
                $("#qsStartDate").parent().find(".el-icon-date").addClass("click_date");
                $("#qsStartDate").parent().find(".el-input__suffix").addClass("focus_date_bg");
            } else if (val == 2) {
                $("#qsEndDate").parent().find(".el-icon-date").addClass("click_date");
                $("#qsEndDate").parent().find(".el-input__suffix").addClass("focus_date_bg");
            } else {
                $("#editKPDate").parent().find(".el-icon-date").addClass("click_date");
                $("#editKPDate").parent().find(".el-input__suffix").addClass("focus_date_bg");
            }
        },
        /**
         * 移除选择日期时的样式
         * @param val
         */
        removeDateStyle: function (val) {
            if (val == 1) {
                $("#qsStartDate").parent().find(".el-icon-date").removeClass("click_date");
                $("#qsStartDate").parent().find(".el-input__suffix").removeClass("focus_date_bg");
            } else if (val == 2) {
                $("#qsEndDate").parent().find(".el-icon-date").removeClass("click_date");
                $("#qsEndDate").parent().find(".el-input__suffix").removeClass("focus_date_bg");
            } else {
                $("#editKPDate").parent().find(".el-icon-date").removeClass("click_date");
                $("#editKPDate").parent().find(".el-input__suffix").removeClass("focus_date_bg");
            }
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});


/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInputValue(t) {
    var reg = /[^\d]/g;
    vm.updateInvoice.checkCode = t.value.replace(reg, '');
}

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}

function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
}