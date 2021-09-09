
var isInitial = true;

function decimal(cellvalue, options, rowObject) {
    if (cellvalue != null) {
        var val = Math.round(cellvalue * 100) / 100;
        return val.formatMoney();
    }
    return "—— ——";
}

Number.prototype.formatMoney = function (places, symbol, thousand, decimal) {
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var number = this,
        negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};

function toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return '';
    }
    var f = Math.round(x*100)/100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}

function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
}

function formaterDate2(cellvalue) {
    var d = new Date(cellvalue.toString().replace(/-/g, "/")),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [year, month, day].join('');
}


function invoiceTypeImg(cellvalue, options, rowObject) {
    var invoiceType = getFplx(rowObject.invoiceCode);
    if (invoiceType == "01") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="增值税专用发票" src="../../img/special-invoice.png">';
    } else if (invoiceType == "03") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="机动车销售统一发票" src="../../img/motor-vehicles.png">';
    } else if (invoiceType == "04") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="增值税普通发票" src="../../img/plain-invoice.png">';
    } else if (invoiceType == "10") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="增值税电子普通发票" src="../../img/einvoice.png">';
    } else if (invoiceType == "11") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="增值税普通发票（卷票）" src="../../img/roll-invoice.png">';
    } else if (invoiceType == "14") {
        return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" title="增值税电子普通发票（通行费）" src="../../img/einvoice.png">';
    }
}

var currentQueryParam = {
    userId: null,
    invoiceNo: null,
    invoiceDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth()) + "-01",
    invoiceDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
    gfName: "-1",
    shName: "-1",
    invoiceType: "-1"
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        dialogLoading: false,
        exportCondition: true,
        haveImageShow: false,
        noImageShow: false,
        show: false,
        tableData: [],
        multipleSelection: [],
        options: [],
        pageCount: 0,
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 1,
        total: 0,
        listLoading: false,
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        gfsh: [{
            value: "-1",
            label: "全部"
        }],
        q: {
            key: '',
            userId: null,
            invoiceNo: null,
            invoiceDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth()) + "-01",
            invoiceDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            gfName: "-1",
            shName: "-1",
            invoiceType: "-1"
        },
        dataform: {},
        editform: [],
        dataEditForm: {},
        dialogVisibleEdit: false,
        editDialogLoading: false,
        dialogVisible: false,
        qsStartDateOptions: {},
        qsEndDateOptions: {},
        invoiceDateOptions: {},
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
    },
    mounted: function () {
        this.queryGf();
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
        this.invoiceDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch: {
        'q.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.invoiceNo = oldValue;
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
        checkNullFormat: function (row, column, index) {
            if (index == null || index == '') {
                return "—— ——";
            }
            return index;
        },
        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        /*getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },*/
        query: function () {
            isInitial = false;
            this.findAll(1)
        },

        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        findAll: function (currentPage) {
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.q.invoiceDate1);
            var qsEndDate = new Date(vm.q.invoiceDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            var flag = false;
            if ((qsEndDate.getTime() + 1000 * 60 * 60 * 24) > qsStartDate.getTime()) {
                $("#requireMsg3 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return;
            } else if (qsEndDate.getTime() < new Date(vm.q.invoiceDate1)) {
                $("#requireMsg3 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }
            currentQueryParam = {
                page: currentPage,
                limit: this.pageSize,
                invoiceNo: this.q.invoiceNo,
                invoiceDate1: this.q.invoiceDate1,
                invoiceDate2: this.q.invoiceDate2,
                shName: vm.q.gfName,
                invoiceType: this.q.invoiceType
            };
            vm.listLoading = true;
            $.ajax({
                url: baseURL + 'SignatureProcessing/PageList',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(currentQueryParam),
                success: function (r) {
                    flag = true;
                    if (r.code == 0) {
                        vm.total = r.page.totalCount;
                        vm.currentPage = r.page.currPage;
                        vm.pageCount = r.page.totalPage;
                        vm.tableData = r.page.list;
                        vm.listLoading = false;
                        if (r.page.list.length > 0) {
                            vm.exportCondition = false;
                        } else {
                            vm.exportCondition = true;
                        }
                    }
                    var intervelId = setInterval(function () {
                        if (flag) {
                            hh = $(document).height();
                            $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                            clearInterval(intervelId);
                            return;
                        }
                    }, 50);
                }
            });
        }, currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            vm.findAll(currentPage);
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        }, queryGf: function () {
            $.ajax({
                url: baseURL + 'SignatureProcessing/queryGf',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        gfs.push({
                            value: "-1",
                            label: "全部"
                        });
                        for (var i = 0; i < r.optionList.length; i++) {
                            var gf = {};
                            gf.value = r.optionList[i].value;
                            gf.label = r.optionList[i].label;
                            gfs.push(gf);
                        }
                        vm.gfsh = gfs;
                    }
                }
            });
        }, changeFun: function (row) {
            this.multipleSelection = row;
        },
        invoiceDate1Change: function (val) {
            vm.q.invoiceDate1 = val;
        },
        invoiceDate2Change: function (val) {
            vm.q.invoiceDate2 = val;
        }, toExceil: function () {
            document.getElementById("ifile").src = baseURL + 'export/SignatureProcessingDateExport'
                + '?shName=' + (currentQueryParam.gfName == null ? '' : currentQueryParam.gfName)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + currentQueryParam.invoiceDate1
                + '&invoiceDate2=' + currentQueryParam.invoiceDate2
                + '&invoiceType=' + currentQueryParam.invoiceType;
        }, saveData: function () {
            var invoiceType = getFplx(vm.dataform.invoiceCode);
            if (invoiceType == "01" || invoiceType == "14" || invoiceType == "03") {
                var params = {
                    "invoiceCode": vm.dataform.invoiceCode,
                    "invoiceNo": vm.dataform.invoiceNo,
                    "uuid": vm.dataform.invoiceCode + vm.dataform.invoiceNo
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/checkInvoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        alert(r.msg);
                        vm.dialogVisible = false;
                        vm.findAll(1);
                    }
                });
            } else if (invoiceType == "04" || invoiceType == "10" || invoiceType == "11") {
                var params = {
                    "id": vm.dataform.id,
                    "invoiceCode": vm.dataform.invoiceCode,
                    "invoiceNo": vm.dataform.invoiceNo,
                    "gfTaxNo": vm.dataform.gfTaxNo,
                    "checkCode": vm.dataform.checkCode,
                    "invoiceAmount": vm.dataform.invoiceAmount,
                    "invoiceDate": formaterDate2(vm.dataform.invoiceDate),
                    "invoiceType": invoiceType
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/checkPlainInvoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 1) {
                            alert(r.msg);
                        }
                        vm.dialogVisible = false;
                        vm.findAll(1);
                    }
                });
            } else {
                alert("更改的数据有误！");
                vm.dialogVisible = false;
            }
        },
        editSaveData: function () {
            this.$refs["dataEditForm"].validate(function (valid) {
                if (valid) {
                    var invoiceType = getFplx(vm.dataEditForm.invoiceCode);
                    var params = {
                        "id": vm.dataEditForm.id,
                        "invoiceCode": vm.dataEditForm.invoiceCode,
                        "invoiceNo": vm.dataEditForm.invoiceNo,
                        "gfTaxNo": vm.dataEditForm.gfTaxNo,
                        "checkCode": vm.dataEditForm.checkCode,
                        "invoiceAmount": vm.dataEditForm.invoiceAmount,
                        "invoiceDate": vm.dataEditForm.invoiceDate,
                        "invoiceType": invoiceType,
                        "xfTaxNo": vm.dataEditForm.xfTaxNo,
                        "taxAmount": vm.dataEditForm.taxAmount,
                        "totalAmount": vm.dataEditForm.totalAmount,
                        "uuid": vm.dataEditForm.invoiceCode + vm.dataEditForm.invoiceNo
                    };
                    if (invoiceType == "01" || invoiceType == "14" || invoiceType == "03") {
                        $.ajax({
                            type: "POST",
                            url: baseURL + "SignatureProcessing/checkInvoice",
                            contentType: "application/json",
                            data: JSON.stringify(params),
                            success: function (r) {
                                alert(r.msg);
                                vm.dialogVisibleEdit = false;
                                vm.$refs["dataEditForm"].resetFields();
                                vm.findAll(1);
                            }
                        });
                    } else if (invoiceType == "04" || invoiceType == "10" || invoiceType == "11") {
                        $.ajax({
                            type: "POST",
                            url: baseURL + "SignatureProcessing/checkPlainInvoice",
                            contentType: "application/json",
                            data: JSON.stringify(params),
                            success: function (r) {
                                if (r.code == 1) {
                                    alert(r.msg);
                                }
                                vm.dialogVisibleEdit = false;
                                vm.$refs["dataEditForm"].resetFields();
                                vm.findAll(1);
                            }
                        });
                    } else {
                        alert("更改的数据有误！");
                        vm.dialogVisibleEdit = false;
                        vm.$refs["dataEditForm"].resetFields();
                    }
                } else {
                    return false;
                }
            });
        },
        updateWinShow: function () {
            vm.dialogVisible = false;
        },
        beforeCloseUpdateWin: function () {
            vm.dialogVisible = false;
        },
        beforeCloseEditWin: function () {
            vm.dialogVisibleEdit = false;
            vm.$refs["dataEditForm"].resetFields();
        },
        invoiceAmountFormatDecimal: function (row, column, index) {
            return decimal(row.invoiceAmount);
        }, taxAmountFormatDecimal: function (row, column, index) {
            return decimal(row.taxAmount);
        }, qsStatusFormatter: function (row) {
            var val = row.qsStatus;
            if (val == "0") {
                return "签收失败";
            } else if (val == "1") {
                return "签收成功";
            }
        }, qsTypeFormatter: function (row) {
            var qsType = row.qsType;
            if (qsType == null || qsType == undefined || qsType == "") {
                return "— —"
            } else if (qsType == "0") {
                return "扫码签收";
            } else if (qsType == "1") {
                return "扫描仪签收";
            } else if (qsType == "2") {
                return "app签收";
            } else if (qsType == "3") {
                return "导入签收";
            } else if (qsType == "4") {
                return "手工签收";
            } else if (qsType == "5") {
                return "pdf上传签收";
            }
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '';
            }
        }, deleteData: function (row) {
            vm.openConfirm(vm, "\n" + "确定要删除选中的记录？", function () {
                var params = {
                    "invoiceCode": row.invoiceCode,
                    "invoiceNo": row.invoiceNo
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/deleteRevoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }
                        vm.findAll(1);
                    }
                });
            }, function () {
            });
        },
        invoiceDate3Change: function (val) {
            vm.dataform.invoiceDate = new Date(val);
        },
        checkInvoice: function (row) {
            var invoiceType = getFplx(row.invoiceCode);
            var params = {
                "id": row.id,
                "invoiceCode": row.invoiceCode,
                "invoiceNo": row.invoiceNo,
                "gfTaxNo": row.gfTaxNo,
                "checkCode": row.checkCode,
                "invoiceDate": formaterDate2(row.invoiceDate),
                "invoiceAmount": row.invoiceAmount,
                "invoiceType": invoiceType
            };
            vm.openConfirm(vm, "确定要签收选中的记录？", function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/checkPlainInvoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 1) {
                            alert(r.msg);
                        }
                        vm.findAll(1);
                    }
                });
            }, function () {
            });
        }, editData: function (row) {
            if (!(row.qsType == "1" || row.qsType == "5")) {
                //不是扫描仪签收
                vm.dialogVisible = true;
                vm.dataform = {
                    id: row.id,
                    gfTaxNo: row.gfTaxNo,
                    invoiceNo: row.invoiceNo,
                    invoiceCode: row.invoiceCode,
                    invoiceDate: new Date(row.invoiceDate),
                    xfTaxNo: row.xfTaxNo,
                    invoiceAmount: row.invoiceAmount,
                    taxAmount: row.taxAmount,
                    totalAmount: row.totalAmount,
                    checkCode: row.checkCode
                };
                return;
            }

            $("#imgEdit").attr("src", "");
            vm.listLoading = true;
            var uuid = row.invoiceCode + row.invoiceNo;
            $.ajax({
                url: baseURL + "phoneApp/getPicture",
                type: "POST",
                dataType: "json",
                data: {uuid: uuid},
                success: function (r) {
                    vm.listLoading = false;
                    vm.dialogVisibleEdit = true;
                    vm.dataEditForm = {
                        id: row.id,
                        gfTaxNo: row.gfTaxNo,
                        invoiceNo: row.invoiceNo,
                        invoiceCode: row.invoiceCode,
                        invoiceDate: formaterDate2(row.invoiceDate),
                        xfTaxNo: row.xfTaxNo,
                        invoiceAmount: toDecimal2(row.invoiceAmount),
                        taxAmount: toDecimal2(row.taxAmount),
                        totalAmount: toDecimal2(row.totalAmount),
                        checkCode: row.checkCode
                    };
                    if(r.code == 0) {
                        setTimeout(function () {
                            $("#imgEdit").attr("src", "data:image/png;base64," + r.url);
                        }, 100);
                    }
                }
            });
        }, /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time.toString().replace(/-/g, "/"));
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
        taxAmountChange: function (val) {
            var taxAmount = val;
            var invoiceAmount = vm.dataEditForm.invoiceAmount;
            vm.dataEditForm.taxAmount = taxAmount;
            if (taxAmount != null && taxAmount != '' && invoiceAmount != null && invoiceAmount != '') {
                vm.dataEditForm.totalAmount = (Number(taxAmount) + Number(invoiceAmount)).toFixed(2);
            }
        },
        invoiceAmountChange: function (val) {
            var taxAmount = vm.dataEditForm.taxAmount;
            var invoiceAmount = val;
            vm.dataEditForm.invoiceAmount = invoiceAmount;
            if (taxAmount != null && taxAmount != '' && invoiceAmount != null && invoiceAmount != '') {
                vm.dataEditForm.totalAmount = (Number(taxAmount) + Number(invoiceAmount)).toFixed(2);
            }
        },
        changeAmount: function(val) {
            var positiveReg = /(^[0]$)|(^[1-9]\d*$)|(^[1-9]\d*\.\d{1,2}$)|(^0\.\d{1,2}$)/;
            var negativeReg = /(^-[1-9]\d*$)|(^-[1-9]\d*\.\d{1,2}$)|(^-0\.\d{1,2}$)/;
            if (!(new RegExp(positiveReg).test(val) || new RegExp(negativeReg).test(val))) {
                vm.dataEditForm.invoiceAmount = "";
            }
        },
        changeTaxAmount: function(val) {
            var positiveReg = /(^[0]$)|(^[1-9]\d*$)|(^[1-9]\d*\.\d{1,2}$)|(^0\.\d{1,2}$)/;
            var negativeReg = /(^-[1-9]\d*$)|(^-[1-9]\d*\.\d{1,2}$)|(^-0\.\d{1,2}$)/;
            if (!(new RegExp(positiveReg).test(val) || new RegExp(negativeReg).test(val))) {
                vm.dataEditForm.taxAmount = "";
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

//校验金额

function num(obj) {
    obj.value = obj.value.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
    obj.value = obj.value.replace(/^\./g, ""); //验证第一个字符是数字
    obj.value = obj.value.replace(/\.{2,}/g, ""); //只保留第一个, 清除多余的
    obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
    if(obj.value !='' && obj.value != null) {
        vm.invoiceAmountChange(obj.value);
    }
}

function numTax(obj) {
    obj.value = obj.value.replace(/[^\d.]/g, ""); //清除"数字"和"."以外的字符
    obj.value = obj.value.replace(/^\./g, ""); //验证第一个字符是数字
    obj.value = obj.value.replace(/\.{2,}/g, ""); //只保留第一个, 清除多余的
    obj.value = obj.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
    obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3'); //只能输入两个小数
    if(obj.value !='' && obj.value != null) {
        vm.taxAmountChange(obj.value);
    }
}

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}


/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fpdmlist = ["144031539110", "131001570151", "133011501118", "111001571071"];
    var fplx = "";
    if (fpdm.length == 12) {
        var fplxflag = fpdm.substring(7, 8);

        for (var i = 0; i < fpdmlist.length; i++) {
            if (fpdm == fpdmlist[i]) {
                fplx = "10";
                break;
            }
        }
        if (fpdm.substring(0, 1) == "0" && fpdm.substring(10, 12) == "11") {
            fplx = "10";
        }
        if (fpdm.substring(0, 1) == "0" && fpdm.substring(10, 12) == "12") {
            fplx = "14";
        }
        if (fpdm.substring(0, 1) == "0" && (fpdm.substring(10, 12) == "06" || fpdm.substring(10, 12) == "07")) {
            //判断是否为卷式发票  第1位为0且第11-12位为06或07
            fplx = "11";
        }
        if (fpdm.substring(0, 1) == "0" && (fpdm.substring(10, 12) == "04" || fpdm.substring(10, 12) == "05")) {
            fplx = "04"
        }
        if (fplxflag == "2" && !fpdm.substring(0, 1) == "0") {
            fplx = "03";
        }

    } else if (fpdm.length == 10) {
        var fplxflag = fpdm.substring(7, 8);
        if (fplxflag == "1" || fplxflag == "5") {
            fplx = "01";
        } else if (fplxflag == "6" || fplxflag == "3") {
            fplx = "04";
        } else if (fplxflag == "7" || fplxflag == "2") {
            fplx = "02";
        }

    }
    return fplx;
}

/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.q.invoiceNo = t.value.replace(reg, '');

}

