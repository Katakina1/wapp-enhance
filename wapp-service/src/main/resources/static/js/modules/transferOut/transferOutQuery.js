
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var isInitial = true;

var vm = new Vue({
    el: '#transferOut',
    //directives:{
    //    number: {
    //        bind: function(el,binding) {
    //            el.handler = function(el,binding) {
    //                var temp=binding.value;
    //                var reg=/D+/;
    //                if(!reg.test(el.value)){
    //                    if(el.value!=null){
    //                        el.value=el.value.substring(0,el.value.length-1);
    //                        vm.invoiceNo='';
    //                    }
    //                }
    //            }
    //            el.addEventListener('input', el.handler);
    //        },
    //        unbind: function (el) {
    //            el.removeEventListener('input', el.handler);
    //        }
    //    }
    //},
    data: {
        tableData: [],
        multipleSelection: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 0,
        total: 0,
        summationTotalAmount: 0,
        summationTaxAmount: 0,
        listLoading: false,
        dialogTableVisible: false,
        form: {
            gfNames: [],
            gfName: null,
            gfTaxNo: "-1",
            xfName: null,
            invoiceNo: null,
            invoiceStatus: "-1",
            dqskssq: null,
            rzhDate1: new Date().getFullYear() + "-" + formatToday(new Date().getMonth()) + "-01",
            rzhDate2: new Date().getFullYear() + "-" + formatToday(new Date().getMonth() + 1) + "-" + formatToday(new Date().getDate()),
            rzhDate: null
        },
        showList: true,
        detailEntityList: [],//存放明细页面数据
        tempDetailEntityList: [],//暂存明细页面详情清单数据
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
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            rzhBelongDate: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            outList: [],
            qsStatus: null,
            outStatus: null,
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
        formLabelWidth: '1.2rem',
        outForm: {
            outStatus: null,
            outInvoiceAmout: null,
            outTaxAmount: null,
            outReason: "1",
            outRemark: null,
            radioFlag: false,
            inputFlag: false,
            tempOutInvoiceAmout: null,
            tempOutTaxAmount: null
        },
        //几个dialog的显示控制标志
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        radio: '1',//转出类型单选按钮标志
        rzhStartDateOptions: {},//用于限制日期的范围
        rzhEndDateOptions: {},//用于限制日期的范围
        rules: {
            invoiceNo: [{
                validator: function (rule, value, callback) {
                    if (value != null && value != "") {
                        var regex = /^[0-9]{1,8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为不超过8位的数字'))
                        } else {
                            callback();
                        }
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }],
            /*rzhDate1: [
             {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'},
             {
             validator: function (rule, value, callback) {
             var rzhStartDate = new Date(vm.form.rzhDate1);
             var rzhEndDate = new Date(vm.form.rzhDate2);
             if(rzhStartDate.getTime()>rzhEndDate.getTime()){
             callback(new Error('认证开始日期不得晚于认证结束日期'))
             }
             rzhStartDate.setMonth(rzhStartDate.getMonth() + 6);
             if(rzhEndDate.getTime()>rzhStartDate.getTime()){
             callback(new Error('认证日期相差不得超过六个月'))
             } else {
             callback();
             }
             },trigger:'change'
             }
             ],
             rzhDate2: [
             {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'},
             {
             validator: function (rule, value, callback) {
             var rzhStartDate = new Date(vm.form.rzhDate1);
             var rzhEndDate = new Date(vm.form.rzhDate2);
             if(rzhStartDate.getTime()>rzhEndDate.getTime()){
             callback(new Error('认证结束日期不得早于认证开始日期'))
             }
             rzhStartDate.setMonth(rzhStartDate.getMonth() + 3);
             if(rzhEndDate.getTime()>rzhStartDate.getTime()){
             callback(new Error('认证日期相差不得超过三个月'))
             } else {
             callback();
             }
             },trigger:'change'
             }
             ]*/
        },
        outFormRules: {
            outTaxAmount: [{
                validator: function (rule, value, callback) {
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2})))$/;
                    if(vm.radio==2){
                        if (!regex.test(value)) {
                            callback(new Error("只能输入正数,小数位保留两位"))
                        }
                        else if (Subtr(value, vm.outForm.tempOutTaxAmount) >= 0) {
                            callback(new Error('转出税额应小于税额'));
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }
            ],
            outInvoiceAmout: [{
                validator: function (rule, value, callback) {
                    var regex = /^(([1-9][0-9]*)|(([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2})))$/;
                    if(vm.radio==2) {
                        if (!regex.test(value)) {
                            callback(new Error("只能输入正数,小数位保留两位"))
                        }
                        else if (Subtr(value, vm.outForm.tempOutInvoiceAmout) >= 0) {
                            callback(new Error('转出金额应小于金额'));
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }
            ]
        }
    },
    mounted: function () {
        this.queryGf();
        $("#gfSelect").attr("maxlength", "50");
        this.rzhStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.rzhEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },

    watch: {
        'form.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'outForm.outInvoiceAmout': {
            handler: function (val, oldValue) {
                var _this = this;
                var regex = /^(([1-9][0-9]*)|(([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2})))$/;
                if (!regex.test(val)) {
                    if(val==null || val==''){
                        Vue.nextTick(function () {
                            _this.outForm.outInvoiceAmout = '';
                        });
                    }else {
                        Vue.nextTick(function () {
                            _this.outForm.outInvoiceAmout = oldValue;
                        });
                    }
                }
            }
        },
        'outForm.outTaxAmount': {
            handler: function (val, oldValue) {
                var _this = this;
                var regex = /^(([1-9][0-9]*)|(([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2})))$/;
                if (!regex.test(val)) {
                    if(val==null || val==''){
                        Vue.nextTick(function () {
                            _this.outForm.outTaxAmount = '';
                        });
                    }else {
                        Vue.nextTick(function () {
                            _this.outForm.outTaxAmount = oldValue;
                        });
                    }
                }
            }
        }
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
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
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
            }
        },
        query: function (formName) {
            $(".checkMsg").remove();
            var checkKPDate = true;
            var qsStartDate = new Date(vm.form.rzhDate1);
            var qsEndDate = new Date(vm.form.rzhDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            if (qsEndDate.getTime()+24*60*60*1000  > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate = false;
            } else if (qsEndDate.getTime() < new Date(vm.form.rzhDate1)) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate = false;
            }
            if (!checkKPDate) {
                return;
            }
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.findAll(1);
                }
            });

        },
        queryGf: function () {
            $.get(baseURL + 'transferOut/detailQuery/gfNameAndTaxNo', function (r) {
                for (var i = 0; i < r.gfNameList.length; i++) {
                    vm.form.gfNames.push({name: r.gfNameList[i], taxNo: r.gfTaxNoList[i]});
                }

            })
        },
        gfNameChange: function (val) {
            if (val == "-1") {
                vm.form.dqskssq = null;
                return;
            }

            $.get(baseURL + 'transferOut/transferOutQuery/getDqskssq', {gfTaxNo: val}, function (r) {
                vm.form.dqskssq = r.dqskssq;

            })
        },
        queryXf: function (queryString, callback) {
            $.get(baseURL + 'transferOut/transferOutQuery/xfName', {queryString: queryString}, function (r) {
                var resultList = [];
                for (var i = 0; i < r.list.length; i++) {
                    var res = {};
                    res.value = r.list[i];
                    resultList.push(res);
                }
                callback(resultList);
            });
        },
        formatRzhDate: function (row, column) {
            if (row.rzhDate != null) {
                return dateFormat(row.rzhDate);
            } else {
                return '';
            }
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '';
            }
        },
        invoiceNoChange: function (value) {

        },
        dqskssqDateChange: function (val) {
            vm.form.dqskssq = val;
        },
        rzhDate1Change: function (val) {
            vm.form.rzhDate1 = val;
        },
        rzhDate2Change: function (val) {
            vm.form.rzhDate2 = val;
        },
        handleSelectionChange: function (val) {
            vm.multipleSelection = val;
        },
        out: function () {
            var ids = [];
            if (!this.multipleSelection.length > 0) {
                alert("请勾选要转出的发票！");
                return;
            }
            if (this.multipleSelection.length > 1) {
                vm.outForm.radioFlag = true;
                vm.outForm.inputFlag = true;
            } else if (vm.radio == "1") {
                vm.outForm.radioFlag = false;
                vm.outForm.inputFlag = true;
            } else {
                vm.outForm.radioFlag = false;
                vm.outForm.inputFlag = false;
            }
            for (var i = 0; i < this.multipleSelection.length; i++) {
                ids.push(this.multipleSelection[i].id);
            }
            var idss = ids.join(",");
            $.get(baseURL + "transferOut/transferOutQuery/getToOutInformation", {ids: idss}, function (r) {
                vm.outForm.tempOutInvoiceAmout = r.outInvoiceAmout;
                vm.outForm.tempOutTaxAmount = r.outTaxAmount;
                vm.outForm.outInvoiceAmout = formatMoney(vm.outForm.tempOutInvoiceAmout);
                vm.outForm.outTaxAmount = formatMoney(vm.outForm.tempOutTaxAmount);
            });
            vm.outForm.outReason = "1";
            vm.outForm.outRemark = null;
            vm.dialogFormVisible = true;
        },
        radioJudge: function () {
            var radioFlag = vm.radio;
            if (radioFlag == 1) {
                vm.outForm.outInvoiceAmout = Subtr(vm.multipleSelection[0].invoiceAmount, vm.multipleSelection[0].outInvoiceAmout);
                vm.outForm.outTaxAmount = Subtr(vm.multipleSelection[0].taxAmount, vm.multipleSelection[0].outTaxAmount);
                vm.$refs["outForm"].clearValidate();
                vm.outForm.inputFlag = true;
            } else {
                vm.outForm.inputFlag = false;
            }
        },
        setOut: function (formName) {

            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    var outRemark = vm.outForm.outRemark;
                    var outReason = vm.outForm.outReason;
                    var outInvoiceAmout = vm.outForm.outInvoiceAmout;
                    var outTaxAmount = vm.outForm.outTaxAmount;
                    if (Subtr(outInvoiceAmout, vm.outForm.tempOutInvoiceAmout) > 0 || Subtr(outTaxAmount, vm.outForm.tempOutTaxAmount) > 0) {
                        return;
                    }
                    var ids = [];
                    for (var i = 0; i < vm.multipleSelection.length; i++) {
                        ids.push(vm.multipleSelection[i].id);
                    }
                    var idss = ids.join(",");
                    var outStatus = vm.radio;
                    if (outStatus == 2) {
                        var outInvoiceAmout = vm.outForm.outInvoiceAmout;
                        var outTaxAmount = vm.outForm.outTaxAmount;
                    }
                    $.get(baseURL + "transferOut/transferOutQuery/setTransferOut", {
                        ids: idss, outRemark: outRemark, outReason: outReason, outInvoiceAmout: outInvoiceAmout,
                        outTaxAmount: outTaxAmount, outStatus: outStatus
                    }, function (r) {
                        if (r.code == "0") {
                            alert("转出成功！");
                            vm.reload();
                        } else {
                            alert("转出失败！");
                        }
                    });
                    vm.dialogFormVisible = false;
                    vm.outForm.outInvoiceAmout = null;
                    vm.outForm.outTaxAmount = null;
                    vm.outForm.outReason = "1";
                    vm.outForm.outRemark = null;
                    vm.radio = "1";
                }
            });
        },
        outFormCancel: function () {
            vm.dialogFormVisible = false;
            vm.outForm.outInvoiceAmout = null;
            vm.outForm.outTaxAmount = null;
            vm.outForm.outReason = "1";
            vm.outForm.outRemark = null;
            vm.radio = "1";
        },
        outDialogClose: function (formName) {
            this.$refs[formName].resetFields();
        },
        printform: function (oper) {
            /*var bdhtml,sprnstr,eprnstr,prnhtml;
             if (oper < 10){
             bdhtml=window.document.body.innerHTML;//获取当前页的html代码
             console.log(bdhtml);
             sprnstr="<!--startprint"+oper+"-->";//设置打印开始区域
             eprnstr="<!--endprint"+oper+"-->";//设置打印结束区域
             var startn=bdhtml.indexOf(sprnstr);
             /!*alert(startn);*!/
             prnhtml=bdhtml.substring(bdhtml.indexOf(sprnstr)+18); //从开始代码向后取html
             console.log('<br>');
             console.log(prnhtml);
             prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html
             window.document.body.innerHTML=prnhtml;
             /!*var newWin = window.open("",'newwindow','height=700,width=750,top=100,left=200,toolbar=no,menubar=no,resizable=no,location=no, status=no');
             newWin.document.write(prnhtml);
             newWin.print();*!/
             window.print();
             window.document.body.innerHTML=bdhtml;
             } else {
             window.print();
             }*/
            if (oper < 10) {
                var w = screen.availWidth - 10;
                var h = screen.availHeight - 30;
                if (oper <= 3) {
                    var head = '<script type="text/javascript" charset="utf-8" src="../../js/customer/resource.js"></script>';
                    var prnhtml = $('#printdiv' + oper).find('.el-dialog__body .col-xs-9').html();
                } else {
                    var prnhtml = $('#printdiv' + oper).html();
                }

                var newWin = parent.window.open('', "win", "fullscreen=0,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=1,width=" + w + ",height=" + h + ",top=0,left=0", true);
                newWin.document.write(prnhtml);
                newWin.document.close();
                newWin.focus();
                newWin.print();
                newWin.close();
            } else {
                window.print();
            }

        },
        detailFormCancel: function () {
            vm.detailDialogFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];

        },
        detailVehicleFormCancel: function () {
            vm.detailDialogVehicleFormVisible = false;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.buyerIdNum = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.vehicleType = null;
            vm.detailForm.factoryModel = null;
            vm.detailForm.productPlace = null;
            vm.detailForm.certificate = null;
            vm.detailForm.certificateImport = null;
            vm.detailForm.inspectionNum = null;
            vm.detailForm.engineNo = null;
            vm.detailForm.vehicleNo = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.phone = null;
            vm.detailForm.address = null;
            vm.detailForm.account = null;
            vm.detailForm.bank = null;
            vm.detailForm.taxRate = null;
            vm.detailForm.taxAmount = null;
            vm.detailForm.taxBureauName = null;
            vm.detailForm.taxBureauCode = null;
            vm.detailForm.invoiceAmount = null;
            vm.detailForm.taxRecords = null;
            vm.detailForm.tonnage = null;
            vm.detailForm.limitPeople = null;
        },
        detailCheckFormCancel: function () {
            vm.detailDialogCheckFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];
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
        formatInvoiceAmount: function (row, column) {
            if (row.outInvoiceAmout != null) {
                return vm.numberFormat(null, null, Subtr(row.invoiceAmount, row.outInvoiceAmout));
            } else {
                return vm.numberFormat(null, null, row.invoiceAmount);
            }
        },
        formatTaxAmount: function (row, column) {
            if (row.outTaxAmount != null) {
                return vm.numberFormat(null, null, Subtr(row.taxAmount, row.outTaxAmount));
            } else {
                return vm.numberFormat(null, null, row.taxAmount);
            }
        },
        indexMethod: function (index) {
            return this.pageSize * (this.currentPage - 1) + index + 1;
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
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var flag = false;
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                gfTaxNo: this.form.gfTaxNo == -1 ? null : this.form.gfTaxNo,
                xfName: this.form.xfName,
                invoiceNo: this.form.invoiceNo,
                invoiceStatus: this.form.invoiceStatus == -1 ? null : this.form.invoiceStatus,
                dqskssq: this.form.dqskssq,
                rzhDate1: this.form.rzhDate1,
                rzhDate2: this.form.rzhDate2,
                sidx: '',
                order: 'desc',
            };
            this.$http.post(baseURL + 'transferOut/transferOutQuery/transferOutQuery',
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                flag = true;
                if (response.data.code != 0 && response.data.code != 401) {
                    alert(response.data.msg);
                    this.listLoading = false;
                    return;
                }
                this.total = response.data.page.totalCount;
                if (this.total > 0) {
                    isInitial = false;
                } else {
                    isInitial = true;
                }
                this.totalPage = response.data.page.totalPage;
                this.tableData = response.data.page.list;
                //    for (const key in response.data.page.list) {
                //        this.$set(this.tableData, key, response.data.page.list[key]);
                //}
                this.listLoading = false;
                $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                    formatMoney(response.data.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(response.data.page.summationTaxAmount)+"元");

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
        reload: function () {
            this.findAll(1);
        },
        detail: function (row) {
            var value = row.id;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
            $.ajax({
                type: "POST",
                url: baseURL + "transferOut/detailQuery/invoiceDetail",
                contentType: "application/json",
                data: JSON.stringify(value),
                success: function (r) {
                    if (r.code == 0) {
                        vm.detailForm.gxDate = r.invoiceEntity.gxDate;
                        vm.detailForm.gxUserName = r.invoiceEntity.gxUserName;
                        vm.detailForm.confirmDate = r.invoiceEntity.confirmDate;
                        vm.detailForm.confirmUser = r.invoiceEntity.confirmUser;
                        vm.detailForm.sendDate = r.invoiceEntity.sendDate;
                        vm.detailForm.authStatus = r.invoiceEntity.authStatus;
                        vm.detailForm.machinecode = r.invoiceEntity.machinecode;
                        vm.detailForm.invoiceType = r.invoiceEntity.invoiceType;
                        vm.detailForm.outList = r.outList;
                        if (r.invoiceEntity.gfTaxNo == personalTaxNumber) {
                            vm.detailForm.gfTaxNo = "";
                        } else {
                            vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                        }
                        vm.detailForm.invoiceStatus = formatInvoiceStatus(r.invoiceEntity.invoiceStatus);
                        vm.detailForm.sourceSystem = formatSourceSystem(r.invoiceEntity.sourceSystem);
                        vm.detailForm.createDate = r.invoiceEntity.createDate;
                        vm.detailForm.statusUpdateDate = r.invoiceEntity.statusUpdateDate;
                        vm.detailForm.qsType = formatQsType(r.invoiceEntity.qsType);
                        vm.detailForm.qsBy = r.invoiceEntity.qsBy;
                        vm.detailForm.qsDate = r.invoiceEntity.qsDate;
                        vm.detailForm.rzhYesorno = r.invoiceEntity.rzhYesorno;
                        vm.detailForm.rzhBelongDate = r.invoiceEntity.rzhBelongDate;
                        vm.detailForm.rzhDate = r.invoiceEntity.rzhDate;
                        vm.detailForm.outDate = r.invoiceEntity.outDate;
                        vm.detailForm.outBy = r.invoiceEntity.outBy;
                        vm.detailForm.outReason = formatOutReason(r.invoiceEntity.outReason);
                        vm.detailForm.qsStatus = r.invoiceEntity.qsStatus;
                        vm.detailForm.outStatus = r.invoiceEntity.outStatus;
                        vm.detailForm.checkCode = r.invoiceEntity.checkCode;
                        vm.detailForm.gfName = r.invoiceEntity.gfName;
                        vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                        vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                        vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                        vm.detailForm.xfName = r.invoiceEntity.xfName;
                        vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                        vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                        vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                        vm.detailForm.remark = r.invoiceEntity.remark;
                        vm.detailForm.totalAmount = vm.numberFormat(null, null, r.invoiceEntity.totalAmount);
                        vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                        vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                        vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                        //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
                        if (r.invoiceEntity.invoiceType == "03") {
                            vm.detailForm.taxAmount = vm.numberFormat(null, null, r.invoiceEntity.taxAmount);
                            vm.detailForm.phone = r.phone;
                            vm.detailForm.address = r.address;
                            vm.detailForm.account = r.account;
                            vm.detailForm.bank = r.bank;
                            if (r.detailVehicleEntity != null) {
                                vm.detailForm.vehicleType = r.detailVehicleEntity.vehicleType;
                                vm.detailForm.factoryModel = r.detailVehicleEntity.factoryModel;
                                vm.detailForm.productPlace = r.detailVehicleEntity.productPlace;
                                vm.detailForm.certificate = r.detailVehicleEntity.certificate;
                                vm.detailForm.certificateImport = r.detailVehicleEntity.certificateImport;
                                vm.detailForm.inspectionNum = r.detailVehicleEntity.inspectionNum;
                                vm.detailForm.engineNo = r.detailVehicleEntity.engineNo;
                                vm.detailForm.vehicleNo = r.detailVehicleEntity.vehicleNo;
                                vm.detailForm.taxRate = r.detailVehicleEntity.taxRate;
                                vm.detailForm.taxBureauName = r.detailVehicleEntity.taxBureauName;
                                vm.detailForm.taxBureauCode = r.detailVehicleEntity.taxBureauCode;
                                vm.detailForm.invoiceAmount = vm.numberFormat(null, null, r.invoiceEntity.invoiceAmount);
                                vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                                vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                                vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                                vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                            }
                            vm.detailDialogVehicleFormVisible = true;
                        } else if (r.invoiceEntity.invoiceType == "14") {
                            vm.detailDialogCheckFormVisible = true;
                            vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                            vm.detailEntityList = r.detailEntityList;
                            for (var i = 0; i < vm.detailEntityList.length; i++) {
                                vm.detailEntityList[i].unitPrice =  vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList = null;
                            }
                        } else {
                            vm.detailDialogFormVisible = true;
                            vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                            vm.detailEntityList = r.detailEntityList;
                            for (var i = 0; i < vm.detailEntityList.length; i++) {
                                vm.detailEntityList[i].unitPrice =  vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList = null;
                            }
                        }
                    } else {
                        alert(r.msg);
                    }
                    /*if(vm.detailForm.invoiceStatus=='正常'){
                     $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
                     alert('normol');
                     }else{
                     alert('error')
                     }*/
                }
            });
        },
        numberFormat: function (row, column, cellValue) {
            /*
             * 参数说明：
             * number：要格式化的数字
             * decimals：保留几位小数
             * dec_point：小数点符号
             * thousands_sep：千分位符号
             * */
            if (cellValue == 0) {
                return "0.00";
            }
            if (cellValue == null || cellValue == '' || cellValue == undefined) {
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
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});


function formatToday(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
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

//js 减法计算
//调用：Subtr(arg1,arg2)
//返回值：arg1减arg2的精确结果
function Subtr(arg1, arg2) {
    var r1, r2, m, n;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    //last modify by deeka
    //动态控制精度长度
    n = (r1 >= r2) ? r1 : r2;
    return ((arg1 * m - arg2 * m) / m).toFixed(2);
}


//js 加法计算
//调用：accAdd(arg1,arg2)
//返回值：arg1加arg2的精确结果
function accAdd(arg1, arg2) {
    var r1, r2, m;
    try {
        r1 = arg1.toString().split(".")[1].length
    } catch (e) {
        r1 = 0
    }
    try {
        r2 = arg2.toString().split(".")[1].length
    } catch (e) {
        r2 = 0
    }
    m = Math.pow(10, Math.max(r1, r2));
    return ((arg1 * m + arg2 * m) / m).toFixed(2);
}

function formatMoney(value) {
    var sv = value.toString();
    var p = sv.indexOf('.');
    if (p < 0) {
        //整数
        sv += '.00';
    }
    if (sv.length == p + 2) {
        sv += '0';
    }
    return sv;
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
    if(val==0){
        return "采集";
    }else if(val==1){
        return "查验";
    }else{
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