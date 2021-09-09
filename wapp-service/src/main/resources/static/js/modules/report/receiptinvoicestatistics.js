
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var postData1 = {};
var paginate = false;
var vm = new Vue({
        el: '#rrapp',
        data: {
            currentPage: 0,
            total: 0,
            totalPage: 0,
            pagerCount: 5,
            pageSize: 12,
            pageList: [12, 50],
            tableData: [],
            listLoading: false,
            qsStartDate: {},
            qsEndDate: {},
            totalAmount: 0,
            totalTax: 0,
            gfs: [{
                value: "",
                label: "全部"
            }],
            form: {
                gfName: "",
                xfName: "",
                invoiceNo: "",
                qsStartDate: new Date().getFullYear() + "-" + formatDate(new Date().getMonth()) + "-01",
                qsEndDate: new Date().getFullYear() + "-" + formatDate(new Date().getMonth() + 1) + "-" + formatDate(new Date().getDate()),
                qsStatus: "",
                qsType: ""
            },
            rules: {
                xfName: [{
                    validator: function (rule, value, callback) {
                        if (value != null && value != "") {
                            //var regex = /^[0-9]{0,50}$/;
                            if (value.length > 50) {
                                callback(new Error('请您输入不超过50位的字符'))
                            } else {
                                callback();
                            }
                        } else {
                            callback();
                        }
                    }, trigger: 'change'
                }],
                invoiceNo: [{
                    validator: function (rule, value, callback) {
                        if (value != null && value != "") {
                            var regex = /^[0-9]{1,8}$/;
                            if (!regex.test(value)) {
                                callback(new Error('请您输入不超过8位的数字'))
                            } else {
                                callback();
                            }
                        } else {
                            callback();
                        }
                    }, trigger: 'change'
                }],

                qsStartDate: [
                    {type: 'string', required: true, message: '签收开始日期不能为空', trigger: 'change'}
                ],
                qsEndDate: [
                    {type: 'string', required: true, message: '签收结束日期不能为空', trigger: 'change'}
                ]
            },
            showList: true,
            btnExportFlag:true

        },
        mounted: function () {
            this.querySearchGf();
            this.qsStartDate = {
                disabledDate: function (time) {
                    return time.getTime() >= Date.now();
                }
            }
            ;
            this.qsEndDate = {
                disabledDate: function (time) {
                    return time.getTime() >= Date.now();
                }
            }


            $("#gfSelect").attr("maxlength", "50");

        },
        methods: {
            gfNameFocus: function (event) {
                $(".el-select input").removeAttr('readonly');
            },
            gfNameBlur: function (event) {
                $(".el-select input").attr('readonly','readonly');
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
            query: function (formName) {

                $(".checkMsg").remove();
                var checkQSDate = true;
                var qsStartDate = new Date(vm.form.qsStartDate);
                var qsEndDate = new Date(vm.form.qsEndDate);
                qsStartDate.setMonth(qsStartDate.getMonth() + 12);

                if (qsEndDate.getTime()+24*60*60*1000  > qsStartDate.getTime()) {
                    $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                    checkQSDate = false;
                } else if (qsEndDate.getTime() < new Date(vm.form.qsStartDate).getTime()) {
                    $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                    checkQSDate = false;
                }
                if (!checkQSDate) {
                    return;
                }


                var Self = this;
                this.$refs[formName].validate(function (valid) {
                    if (valid) {
                        paginate = true;
                        vm.findAll(1);
                    } else {
                        return false;
                    }
                });
            },
            numberFormat: function (row, column, cellValue) {
                if(cellValue==null || cellValue==='' || cellValue == undefined){
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
            findAll: function (currentPage) {
                var postData = {
                    'gfName': vm.form.gfName,
                    'xfName': vm.form.xfName,
                    'invoiceNo': vm.form.invoiceNo,
                    'qsStartDate': vm.form.qsStartDate,
                    'qsEndDate': vm.form.qsEndDate,
                    'qsStatus': vm.form.qsStatus,
                    'qsType': vm.form.qsType,
                    'page': currentPage,
                    'limit': vm.pageSize
                };
                var flag = false;
                this.listLoading = true;
                if (!isNaN(currentPage)) {
                    this.currentPage = currentPage;
                }
                this.$http.post(baseURL + 'report/receiptInvoiceStatistics/query',
                    postData,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    flag =true;
                    $('#totalStatistics').html("合计数量: " + xhr.page.totalCount + "条, 合计金额: " + formateAmount(xhr.totalAmount) + "元, 合计税额: " + formateAmount(xhr.totalTax) + "元");
                    //分页条
                    this.total = xhr.page.totalCount;
                    this.currentPage = xhr.page.currPage;
                    this.totalPage = xhr.page.totalPage;

                    //合计
                    this.totalAmount = formateAmount(xhr.totalAmount);
                    this.totalTax = formateAmount(xhr.totalTax);

                    this.tableData = xhr.page.list;
                    this.listLoading = false;

                    if (xhr.page.totalCount != 0 && xhr.page.totalCount != null) {
                        postData1 = {
                            'gfName': vm.form.gfName,
                            'xfName': vm.form.xfName,
                            'invoiceNo': vm.form.invoiceNo,
                            'qsStartDate': vm.form.qsStartDate,
                            'qsEndDate': vm.form.qsEndDate,
                            'qsStatus': vm.form.qsStatus,
                            'qsType': vm.form.qsType,
                        };
                        vm.btnExportFlag=false;
                    } else {
                        vm.btnExportFlag=true;

                    }
                });
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);
            },
            handleSizeChange: function (val) {
                if (paginate) {
                this.pageSize = val;
                this.findAll(1);
                }
            },

            querySearchGf: function (queryString, callback) {
                $.get(baseURL + 'report/receiptInvoiceStatistics/searchGf', {queryString: queryString}, function (r) {
                    var gfs = [];
                    gfs.push({
                        value: "",
                        label: "全部"
                    });
                    for (var i = 0; i < r.optionList.length; i++) {
                        var gf = {};
                        gf.value = r.optionList[i].value;
                        gf.label = r.optionList[i].label;
                        gfs.push(gf);
                    }
                    vm.gfs = gfs;
                });
            }
            ,
            querySearchXf: function (queryString, callback) {
                $.get(baseURL + 'report/receiptInvoiceStatistics/searchXf', {queryString: queryString}, function (r) {
                    var resultList = new Array();
                    for (var i = 0; i < r.list.length; i++) {
                        var res = {};
                        res.value = r.list[i];
                        resultList.push(res);
                    }
                    callback(resultList);
                });
            }
            ,
            qsStartDateChange: function (val) {
                vm.form.qsStartDate = val
            }
            ,
            qsEndDateChange: function (val) {
                vm.form.qsEndDate = val
            }
            ,
            advanceQuery: function () {
                $('.defaultItem').removeClass("hideItem");
                $('#advanceSearchBtn').addClass("hideItem");
                $('#backSearchBtn').removeClass("hideItem");
            }
            ,
            exportExcel: function () {
//                        const me = this;
//                        me.openConfirm(me, "是否确认导出？", function () {
                document.getElementById("ifile").src = baseURL + 'export/receiptInvoiceStatistics'
                    + '?gfName=' + postData1.gfName
                    + '&xfName=' + postData1.xfName
                    + '&invoiceNo=' + postData1.invoiceNo
                    + '&qsStartDate=' + postData1.qsStartDate
                    + '&qsEndDate=' + postData1.qsEndDate
                    + '&qsStatus=' + postData1.qsStatus
                    + '&qsType=' + postData1.qsType
                    +'&totalAmount='+vm.totalAmount
                    +'&totalTax='+vm.totalTax;
//                        }, function () {
//                        });
            }
            ,
            dateFormat: function (row, column, cellValue, index) {
                if (cellValue == null) {
                    return '--';
                }
                return cellValue.substring(0, 10);
            },
            formatDateTime: function (time) {
                var date = new Date(time.toString().replace(/-/g, "/"));
                var seperator1 = "-";
                var seperator2 = ":";
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
            index: function (index) {
                return index + (this.currentPage - 1) * this.pageSize + 1;
            }
        }
    })
    ;
function formatDate(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

//格式化金额 使其保留2位小数
function formateAmount(val) {
    /*
     * 参数说明：
     * number：要格式化的数字
     * decimals：保留几位小数
     * dec_point：小数点符号
     * thousands_sep：千分位符号
     * */
    var number = val;
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
}

function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceNo = t.value.replace(reg, '');

}