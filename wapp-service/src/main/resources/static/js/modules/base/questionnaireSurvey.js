Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var vm = new Vue({
    el: '#vm',
    data: {radio2:'',
        bnId: "",
        startDateOptions: {},
        questionnaireData:[],

        tableData: [],
        pageCount: 0,
        options: [],
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 1,
        total: 0,
        listLoading : false,
        questionnaire_loading: false,

        questionnaireForm: false,
        questionnaire:{
                id:'',
                questionnaireTitle:'',
                topics:[{
                    //topicid:'',
                    topicTitle:'',
                    topicOp:'',
                    options:[{
                        id:'',
                        optionName:''
                    }]
                }]
        },

        scanWin: false,
        billingDetailsForm: false,
        costDataDetailsForm: false,
        billDataDetailsForm: false,
        costDetailsForm: false,
        filePage: false,
        selectDetailDialogPicture : false,
        form: {
            // matchDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            // matchDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            questionnaireTitle:''
        },


        tempValue: null,
        formLabelWidth: '1.2rem',
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        /*******************************结束***********************************************/
    },
    mounted: function () {
        // this.startDateOptions = {
        //     disabledDate: function (time) {
        //         return time.getTime() >= Date.now();
        //     }
        // };
        // // this.queryDetail();
    },
    watch: {

    },
    methods: {

        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly', 'readonly');
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            }
        },
        /**
         * 格式化发票金额
         */
        invoiceDecimal: function (row) {

            return vm.numberFormat(null, null, row.invoiceAmount);
        },
        settlementamountDecimal: function (row) {

            return vm.numberFormat(null, null, row.settlementamount);
        },
        poDecimal: function (row) {

            return vm.numberFormat(null, null, row.poAmount);
        },
        claimDecimal: function (row) {

            return vm.numberFormat(null, null, row.claimAmount);
        },
        invoiceDate3Change: function (val) {
            vm.form.matchDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.form.matchDateEnd = val;
        },
        /**
         * 格式化
         */
        bnSumDecimal: function (row) {

            return vm.numberFormat(null, null, row.bnSum);
        },
        query: function () {
            this.$refs['chaxun'].validate(function (valid) {
                if (valid) {
                    vm.findAll(1);
                } else {
                    return false;
                }
            })

        },

        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if (vm.total > 0) {
                vm.currentPage = currentPage;
                vm.findAll(currentPage);
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (vm.total > 0) {

                this.findAll(1);
            }
        },
        findAll: function (currentPage) {
             vm.listLoading = true;
            var params = {
                page: currentPage,
                limit: this.pageSize,
                questionnaireTitle: vm.form.questionnaireTitle
            };
            //var flag = false;
            $.ajax({
                type: "POST",
                url: baseURL + 'base/questionnaireSurvey/query',
                data: params,
                success: function (r) {
                    if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    vm.total = r.page.totalCount;
                    vm.currentPage = r.page.currPage;
                    vm.pageCount = r.page.totalPage;
                    vm.tableData = r.page.list;
                    vm.listLoading = false;
                }
            });

        },
        fillIn:function(row){
             vm.questionnaire_loading = true;
            var params = {
               id:row.id
            };
            $.ajax({
                type: "POST",
                url: baseURL + 'base/questionnaireSurvey/queryQuestionnaire',
                data: params,
                success: function (r) {
                    if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    vm.questionnaire = r.entity;
                    vm.questionnaire_loading = false;
                }
            });
            vm.questionnaireForm = true;
        },
        /**
         * 提交调查问卷
         */
        submitQuestionnaire:function(){
            var param = vm.questionnaire;
            // console.log(JSON.stringify(param));
            $.ajax({
                type: "POST",
                url: baseURL + 'base/questionnaireSurvey/submitQuestionnaire',
                contentType: "application/json",
                data: JSON.stringify(param),
                success: function (r) {
                    if (r.code === 0) {
                        alert("提交成功", function () {
                            //location.reload();
                            vm.questionnaireForm = false;
                            vm.questionnaire = {};
                            vm.findAll(1);
                        });
                    }
                    if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                }
            });
        },
        agree:function(){
            for(var index = 0;index < vm.questionnaire.topics.length;index++){
                if (vm.questionnaire.topics[index].topicOp == null) {
                    alert("第" + (index + 1) + "题未选择！");
                    return;
                }
            }
            confirm('确定提交吗？', function() {
                vm.submitQuestionnaire();
            })
        },
        /**
         * 关闭调查问卷
         */
        questionnaireFormCancel: function () {

            vm.questionnaireForm = false;
            vm.questionnaire = {};
        },
        /**
         * 格式化时间
         */
        formatMatchDate: function (row, column) {
            if (row.matchDate != null) {
                return dateFormat(row.matchDate);
            } else {
                return '';
            }
        },

        write: function () {
            $.ajax({
                type: "POST",
                url: baseURL + "modules/posuopei/matchQuery/write",
                contentType: "application/json",
                success: function (r) {
                    alert(r.msg);


                }
            });
        },

        scanWinCancel: function () {
            vm.scanWin = false;
        },
        filePageCancel:function () {
            vm.filePage=false;
        },
        amountpaidFormat: function (row) {
            return decimal(row.amountpaid);
        },
        amountunpaidFormat: function (row) {
            return decimal(row.amountunpaid);
        },
        receiptAmountFormat: function (row) {
            return decimal(row.receiptAmount);
        },
        receiptdateFormat: function (row) {
            if (row.receiptdate != null) {
                return formaterDate(row.receiptdate);
            } else {
                return '—— ——';
            }
        },
        claimAmountFormat: function (row) {
            return decimal(row.claimAmount);
        },
        postdateFormat: function (row) {
            if (row.postdate != null) {
                return formaterDate(row.postdate);
            } else {
                return '—— ——';
            }
        },

        blueTaxAmountFormat: function (row) {
            return decimal(row.blueTaxAmt);
        },

        /**
         * 格式化发票金额
         */
        invoiceAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.invoiceAmount);
        },
        /**
         * 格式化开票时间
         */
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        /**
         * 格式化创建时间
         */
        formatCreateTime: function (row, column) {
            if (row.createTime != null) {
                return dateFormat(row.createTime);
            } else {
                return '—— ——';
            }
        },
        formatterTransactionDate: function (row, column) {
            if (row.transactionDate != null) {
                return dateFormat(row.transactionDate);
            } else {
                return '—— ——';
            }
        },
        invoiceDateFormat: function (row) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        /**
         * 格式化发票税额
         */
        taxAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.taxAmount);
        },

        transactionNetCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionNetCost);
        },
        purchasingVatDecimal: function (row) {

            return vm.numberFormat(null, null, row.purchasingVat);
        },
        transactionEstLandedCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionEstLandedCost);
        },
        /**
         * 费用明细列表税额、金额格式化处理
         **/
        formatterSpecialDiscountVat: function (row) {

            return vm.numberFormat(null, null, row.specialDiscountVat);
        },

        formatterSpecialDiscountAmt: function (row) {

            return vm.numberFormat(null, null, row.specialDiscountAmt);
        },
        numberFormat: function (row, column, cellValue) {
            if(cellValue==null || cellValue=='' || cellValue == undefined){
                return "—— ——";
            }
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
                    return '' + Math.round(n * k) / k;
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
    }
});

function ClearSubmit(e) {
    if (e.keyCode == 13) {
        return false;
    }
}

function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}

/**
 *格式化金额
 */
function formatMoney(value) {
    return Vue.prototype.numberFormat(null, null, value);
}

/**
 * 格式化金额
 * @param cellvalue
 * @returns {*}
 */
function decimal(cellvalue) {
    if (cellvalue != null) {
        var val = Math.round(cellvalue * 100) / 100;
        return val.formatMoney1();
    }
    return "0.00";
}

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

Number.prototype.formatMoney1 = function (places, symbol, thousand, decimal) {
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

/**
 * 格式化时间
 * @param cellvalue
 * @returns {string}
 */
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
}

function formatInvoiceStatus(val) {

    if (val == 0) {
        return "正常";
    }
    if (val == 1) {
        return "失控";
    }
    if (val == 2) {
        return "作废";
    }
    if (val == 3) {
        return "红冲";
    } else {
        return "异常";
    }

}

function formatSourceSystem(val) {
    if (val == 0) {
        return "采集";
    } else if (val == 1) {
        return "查验";
    } else {
        return "录入";
    }
}


function formatQsType(val) {
    if (val == 0) {
        return "扫码签收";
    }
    if (val == 1) {
        return "扫描仪签收";
    }
    if (val == 2) {
        return "app签收";
    }
    if (val == 3) {
        return "导入签收";
    }
    if (val == 4) {
        return "手工签收";
    } else {
        return "pdf上传签收";
    }
}

function formatOutReason(val) {
    if (val == 1) {
        return "免税项目用";
    }
    if (val == 2) {
        return "集体福利,个人消费";
    }
    if (val == 3) {
        return "非正常损失";
    }
    if (val == 4) {
        return "简易计税方法征税项目用";
    }
    if (val == 5) {
        return "免抵退税办法不得抵扣的进项税额";
    }
    if (val == 6) {
        return "纳税检查调减进项税额";
    }
    if (val == 7) {
        return "红字专用发票通知单注明的进项税额";
    } else {
        return "上期留抵税额抵减欠税";
    }
}

function detailDateFormat(value) {
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}
