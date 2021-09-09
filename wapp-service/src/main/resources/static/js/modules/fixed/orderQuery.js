Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var isInitial = true;
var currentQueryParam = {
    orderNo: '',
    matchStatus: '',
    orderDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
    orderDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
    page: 1,
    limit: 1
};
var vm = new Vue({
    el: '#rrapp',
    data: {
        user: {},
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],

        listLoading: false,
        listLoading1: false,
        listLoading2: false,
        totalAmount: 0,
        totalTax: 0,
        readonly:false,
        createDateOptions1: {},
        createDateOptions2: {},
        xfMaxlength: 30,
        status: [
            {value: '', label: "全部"},
            {value: '0', label: "未匹配"},
            {value: '1', label: "已匹配"}
        ],
        status1: '',
        orgtype: '',
        form: {
            orderNo: '',
            matchStatus: '',
            venderid: '',
            orderDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            orderDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate())
        },
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        detailForm: {
            invoiceType: null,
            invoiceStatus: null,
            createDate: null,
            statusUpdateDate: null,
            qsBy: null,
            qsType: null,
            sourceSystem: null,
            qsDate: null,
            rzhYesorno: null,
            gxDate: null,
            gxUserName: null,
            confirmDate: null,
            confirmUser: null,
            sendDate: null,
            authStatus: null,
            machinecode: null,
            rzhBelongDate: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            qsStatus: null,
            outStatus: null,
            outList: [],
            checkCode: null,
            gfName: null,
            gfTaxNo: null,
            gfAddressAndPhone: null,
            gfBankAndNo: null,
            xfName: null,
            xfTaxNo: null,
            xfAddressAndPhone: null,
            xfBankAndNo: null,
            remark: null,
            detailEntityList: [],
            totalAmount: null,
            invoiceNo: null,
            invoiceCode: null,
            invoiceDate: null,
            buyerIdNum: null,
            vehicleType: null,//机动车明细里的车辆类型
            factoryModel: null,
            productPlace: null,
            certificate: null,
            certificateImport: null,
            inspectionNum: null,
            engineNo: null,
            vehicleNo: null,
            taxBureauName: null,
            taxBureauCode: null,
            phone: null,
            address: null,
            bank: null,
            account: null,
            taxRecords: null,
            limitPeople: null,
            tonnage: null,
            invoiceAmount: null,
            detailAmountTotal: null,
            taxAmountTotal: null,
            taxRate: null,
            taxAmount: null,
            stringTotalAmount: null
        },
        //下面是对应模态框隐藏的属性
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogVehicleFormVisible1: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        rules: {
            /*orderNo: [{
                validator: function (rule, value, callback) {
                    var regex = /^[0-9]{10}$/;
                    var regex2 = /^[0-9]{12}$/;
                    if (value != null && value != "") {
                        callback(new Error('订单号不为空'))
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }],*/
            venderid: [{
                validator: function (rule, value, callback) {
                    if (value != null && value != "") {
                        var regex = /^[0-9]{0,6}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为6位的数字'))
                        } else {
                            callback();
                        }
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }],
            orderDate1: [
                {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'}
            ],
            orderDate2: [
                {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'}
            ]
        },
        showList: true
    },
    mounted: function () {
        this.queryDetails();
        this.orderDateOptions1 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.orderDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.orderDateOptions2 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.orderDate2));
                return time.getTime() >= Date.now();
            }
        };

        // 获取orgtype对其赋值判断购方或销方
        this.orgType();

        // this.querySearchGf();
        $("#gf-select").attr("maxlength", "50");
    },
    watch: {
        'form.venderid': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.venderid = oldValue;
                    })
                }
            },
            deep: true
        },
        'form.orderNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,20}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.orderNo = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        queryDetails:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        if(r.isOk=="yes"){
                            vm.form.venderid=r.orgEntity.usercode
                            vm.readonly=true;
                        }
                    }
                }
            });
        },
        /**
         * 获取orgtype
         */
        orgType: function () {
            this.$http.post(baseURL + 'modules/fixed/orgtypeQuery',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                this.orgtype = res.data.orgtype;
            });
        },

        number: function (row) {
            row.redRushNumber = row.redRushNumber.replace(/[^\.\d]/g, '');
            row.redRushNumber = row.redRushNumber.replace('.', '');
            if (parseInt(row.redRushNumber) > parseInt(row.num)) {
                row.redRushNumber = row.num;
            }
        },
        isSelected: function (row, index) {
            if (row.redRushAmount != null) {
                return 1;
            } else {
                return 0;
            }
        },
        generateRedRushData: function () {
            if (vm.tableData5 != null && vm.sumReturnAmount > 0) {
                return 1;
            } else {
                return 0;
            }
        },
        changeFunR: function (row) {
            var returnSumAmount = 0.00;
            vm.returnGoods = row;
            vm.returnNum = vm.returnGoods.length;
            for (var i = 0; i < vm.returnGoods.length; i++) {
                returnSumAmount = returnSumAmount + vm.returnGoods[i].returncostAmount;
            }

            vm.sumReturnAmount = returnSumAmount;
        },
        changeFunFP: function (row) {
            var details = [];
            for (var i = row.length - 1; i >= 0; i--) {
                if (row[i].redRushAmount != null) {
                    row[i].redRushPrice = row[i].unitPrice;
                    details = details.concat(row[i]);
                }
            }
            vm.invoiceDetails = details;
        },
        redRushNumberInput: function (row) {

            row.redRushAmount = row.unitPrice * 100 * row.redRushNumber / 100;

        },
        msNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        msNameBlur: function (event) {
            $(".el-select input").attr('readonly', 'readonly');
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        query: function (formName) {
            isInitial = false;
            var checkKPDate = true;
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.form.orderDate1);
            var qsEndDate = new Date(vm.form.orderDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);

            if (qsEndDate.getTime() + 1000 * 60 * 60 * 24 > qsStartDate.getTime()) {
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate = false;
            } else if (qsEndDate.getTime() < new Date(vm.form.orderDate1)) {
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate = false;
            }
            if (!(checkKPDate)) {
                return;
            }
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'orderNo': vm.form.orderNo,
                        'matchStatus': vm.form.matchStatus,
                        'orderDate1': vm.form.orderDate1,
                        'orderDate2': vm.form.orderDate2,
                        'venderid': vm.form.venderid
                    };
                    vm.findAll1(1);
                } else {
                    return false;
                }
            });
        },
        clear: function (formName) {
            if(vm.readonly!==true) {
                vm.form.venderid = "";
            }
            vm.form.orderNo = null;
        },


        querySearchGf: function () {
            $.get(baseURL + 'report/invoiceProcessingStatusReport/searchGf', function (r) {
                var gfs = [];
                for (var i = 0; i < r.optionList.length; i++) {
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].orgCode + "(" + r.optionList[i].label + ")";
                    gfs.push(gf);
                }
                vm.gfs = gfs;
            });
        },
        orderDate1Change: function (val) {
            vm.form.orderDate1 = val;
        },
        orderDate2Change: function (val) {
            vm.form.orderDate2 = val;
        },

        detailInnerFormCancel: function () {
            vm.detailDialogFormInnerVisible = false;
        },
        detailCheckInnerFormCancel: function () {
            vm.detailDialogCheckFormInnerVisible = false;
        },
        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        showCheckInner: function () {
            vm.detailDialogCheckFormInnerVisible = true;
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll1(currentPage);
        },
        findAll1: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            this.$http.post(baseURL + 'modules/fixed/orderList/query',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;
                this.tableData = xhr.page.list;
                $('.redrush').removeClass("hideItem");
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

        formatMatchStatus: function (row) {
            var matchStatus = row.matchStatus;
            if (matchStatus == null || matchStatus === undefined || matchStatus === "") {
                return "—— ——"
            } else if (matchStatus === "0") {
                return "未匹配";
            } else if (matchStatus === "1") {
                return "已匹配";
            }
        },

        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findRAll(1);
            }
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
            if (!isInitial) {
                this.findAll2(1);
            }
        },
        handleSizeChange3: function (val) {
            this.pageSize3 = val;
            if (!isInitial) {
                this.findAll1(1);
            }
        },
        handleSizeChange4: function (val) {
            this.pageSize4 = val;
            if (!isInitial) {
                this.findAll2(1);
            }
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
            if (cellValue == null || cellValue === '' || cellValue === undefined) {
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
        /**
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
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    },
    created: function () {
        this.getUser();
    },
});


function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

function detailDateFormat(value) {
    value = value.replace("-", "/");
    value = value.replace("-", "/");
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}

function formatMoney(value) {
    return Vue.prototype.numberFormat2(null, null, value);
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

function deepClone(obj) {
    var _obj = JSON.stringify(obj),
        objClone = JSON.parse(_obj);
    return objClone
}