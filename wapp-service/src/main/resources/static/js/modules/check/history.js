
var currentDate = new Date();
var currentYear = currentDate.getFullYear();
var currentMonth = currentDate.getMonth() + 1;
var currentDay = currentDate.getDate();
var personalTaxNumber=123456789012345;
var vm = new Vue({
    el: "#invoice_check_history",
    data: {
        invoiceCheckHistoryTableData: [],
        invoiceCheckQuery: {
            invoiceNo: '',
            invoiceCode: '',
            invoiceStartDate: getCurrentMonthDateTime(),
            invoiceEndDate: getCurrentDateTime1(),
            invoiceCheckStartDate: getCurrentMonthDateTime(),
            invoiceCheckEndDate: getCurrentDateTime1()

        },
        pageData: {
            currentPage: 1,
            total: 0,
        },
        dialogTableVisible: false,
        invoiceCheckHistoryDetailTableData: [],
        detailRow: {},
        detailPageData: {
            currentPage: 1,
            total: 0,
            totalPage: 0
        },
        doCheckButton: true,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        total: 0,
        totalPage: 0,
        pageList: PAGE_PARENT.PAGE_LIST,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        exportButtonFlag: true, //导出按钮默认禁用
        exportParams: {},
        loading: false,
        reg: /^[0-9]*$/,
        kpStartDateOptions: [],
        kpEndDateOptions: [],
        cyStartDateOptions: [],
        cyEndDateOptions: [],
        exportParam:{},
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        detailForm: {
            invoiceType: null,
            invoiceStatus:null,
            createDate:null,
            statusUpdateDate:null,
            qsBy:null,
            qsType:null,
            sourceSystem:null,
            qsDate:null,
            rzhYesorno:null,
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            dqskssq:null,
            rzhDate:null,
            outDate:null,
            outBy:null,
            outReason:null,
            qsStatus:null,
            outStatus:null,
            outList: [],
            checkCode:null,
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
    dialogFormVisible: false,
    detailDialogFormVisible: false,
    detailDialogVehicleFormVisible: false,
    detailDialogCheckFormVisible: false,
    detailDialogFormInnerVisible: false,
    detailDialogCheckFormInnerVisible: false
    },
    created: function () {
        /*  this.getInvoiceCheckHistory();*/
        this.kpStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.kpEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.cyStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.cyEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    methods: {
        invoiceDate1Change: function (val) {
            vm.invoiceCheckQuery.invoiceStartDate = val;
        },
        invoiceDate2Change: function (val) {
            vm.invoiceCheckQuery.invoiceEndDate = val;
        },
        invoiceDate3Change: function (val) {
            vm.invoiceCheckQuery.invoiceCheckStartDate = val;
        },
        invoiceDate4Change: function (val) {
            vm.invoiceCheckQuery.invoiceCheckEndDate = val;
        },
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate1: function (time) {
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
        formatDateToMin: function(row, column, cellValue, index){
            if (cellValue != null && cellValue != "") {
                return Trim(cellValue).substring(0, 16);
            } else {
                return "";
            }
        },detail:function(row){
            var value = row.id;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            $.ajax({
                type: "POST",
                url: baseURL + "transferOut/detailQuery/invoiceCheckDetail",
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
                        vm.detailForm.invoiceStatus=formatInvoiceStatus(r.invoiceEntity.invoiceStatus);
                        vm.detailForm.sourceSystem=formatSourceSystem(r.invoiceEntity.sourceSystem);
                        vm.detailForm.createDate=r.invoiceEntity.createDate;
                        vm.detailForm.statusUpdateDate=r.invoiceEntity.statusUpdateDate;
                        vm.detailForm.qsType=formatQsType(r.invoiceEntity.qsType);
                        vm.detailForm.qsBy=r.invoiceEntity.qsBy;
                        vm.detailForm.qsDate=r.invoiceEntity.qsDate;
                        vm.detailForm.rzhYesorno=r.invoiceEntity.rzhYesorno;
                        vm.detailForm.dqskssq=r.invoiceEntity.dqskssq;
                        vm.detailForm.rzhDate=r.invoiceEntity.rzhDate;
                        vm.detailForm.outDate=r.invoiceEntity.outDate;
                        vm.detailForm.outBy=r.invoiceEntity.outBy;
                        vm.detailForm.outReason=formatOutReason(r.invoiceEntity.outReason);
                        vm.detailForm.qsStatus=r.invoiceEntity.qsStatus;
                        vm.detailForm.outStatus=r.invoiceEntity.outStatus;
                        vm.detailForm.checkCode=r.invoiceEntity.checkCode;
                        vm.detailForm.gfName = r.invoiceEntity.gfName;
                        vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                        vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                        vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                        vm.detailForm.xfName = r.invoiceEntity.xfName;
                        vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                        vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                        vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                        vm.detailForm.remark = r.invoiceEntity.remark;
                        vm.detailForm.totalAmount =vm.numberFormat(null,null,r.invoiceEntity.totalAmount);
                        vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                        vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                        vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                        //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
                        if (r.invoiceEntity.invoiceType == "03") {
                            vm.detailForm.taxAmount =vm.numberFormat(null,null,r.invoiceEntity.taxAmount);
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
                                vm.detailForm.invoiceAmount =vm.numberFormat(null,null,r.invoiceEntity.invoiceAmount);
                                vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                                vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                                vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                                vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                            }
                            vm.detailDialogVehicleFormVisible = true;
                        } else if (r.invoiceEntity.invoiceType == "14") {
                            vm.detailDialogCheckFormVisible = true;
                            vm.detailForm.detailAmountTotal =vm.numberFormat(null,null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal =vm.numberFormat(null,null, r.taxAmountTotal);
                            vm.detailEntityList=r.detailEntityList;
                            for(var i=0;i<vm.detailEntityList.length;i++){
                                vm.detailEntityList[i].unitPrice=vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount=vm.numberFormat(null,null,vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount=vm.numberFormat(null,null,vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList=null;
                            }
                        } else {
                            vm.detailDialogFormVisible = true;
                            vm.detailForm.detailAmountTotal =vm.numberFormat(null,null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal =vm.numberFormat(null,null, r.taxAmountTotal);
                            vm.detailEntityList=r.detailEntityList;
                            for(var i=0;i<vm.detailEntityList.length;i++){
                                vm.detailEntityList[i].unitPrice=vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount=vm.numberFormat(null,null,vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount=vm.numberFormat(null,null,vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList=null;
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
        doInvoiceCodeCheckLimit: function (values) {
            var e = new RegExp(vm.reg)
            if (!e.test(values)) {
                vm.invoiceCheckQuery.invoiceCode = null;
            }
        },
        doInvoiceNoCheckLimit: function (values) {
            var e = new RegExp(vm.reg)
            if (!e.test(values)) {
                vm.invoiceCheckQuery.invoiceNo = null;
            }
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
            }
        },
        getInvoiceCheckHistory: function (currentPage) {

            $(".checkMsg").remove();
            var checkCYDate = true;
            var invoiceStartDate2 = new Date(vm.invoiceCheckQuery.invoiceCheckStartDate);
            var invoiceEndDate2 = new Date(vm.invoiceCheckQuery.invoiceCheckEndDate);
            invoiceStartDate2.setMonth(invoiceStartDate2.getMonth() + 12);
            if ((invoiceEndDate2.getTime() + 1000 * 60 * 60 * 24) > invoiceStartDate2.getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkCYDate = false;
            } else if (invoiceEndDate2.getTime() < new Date(vm.invoiceCheckQuery.invoiceCheckStartDate).getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkCYDate = false;
            }

            if (!(checkCYDate)) {
                return;
            }

            if (!isNaN(currentPage)) {
                this.pageData.currentPage = currentPage;
            }
            var params = {};
            params.invoiceNo = Trim(this.invoiceCheckQuery.invoiceNo);
            params.invoiceCode = Trim(this.invoiceCheckQuery.invoiceCode);
            /*     params.invoiceStartDate = Trim(this.invoiceCheckQuery.invoiceStartDate);
                 params.invoiceEndDate = Trim(this.invoiceCheckQuery.invoiceEndDate);*/
            params.checkStartDate = Trim(this.invoiceCheckQuery.invoiceCheckStartDate);
            params.checkEndDate = Trim(this.invoiceCheckQuery.invoiceCheckEndDate);
            params.page = this.pageData.currentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            this.exportParam = params;
            vm.loading = true;
            $(".checkMsg").remove();
            var flag = false;
            var hh;
            $.ajax({
                url: baseURL + "modules/invoice/check/history", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    flag = true;
                    if (results.code == 0) {
                        var resultString = results.result;
                        $('#totalStatistics').html("合计数量: "+resultString.totalCount+"条, 合计金额: "+formatMoney(results.totalAmount)+"元, 合计税额: "+formatMoney(results.totalTax)+"元");
                        if (resultString.totalCount > 0) {
                            vm.exportButtonFlag = false; //查询结果大于0
                        } else {
                            vm.exportButtonFlag = true;
                        }
                        vm.pageData.total = resultString.totalCount;
                        vm.total = resultString.totalCount;
                        vm.totalPage = resultString.totalPage;
                        vm.invoiceCheckHistoryTableData = [];
                        $.each(resultString.list, function (index, element) {
                            vm.invoiceCheckHistoryTableData.push(element);
                        });

                        vm.loading = false;
                    } else {
                        if (results.code != 401) {
                            parent.layer.alert(results.msg);
                        }
                        vm.loading = false;
                    }
                }

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
        doInvoiceCheckHistoryExport: function () {
            var param = this.exportParam;
            document.getElementById("ifile").src = baseURL + "export/invoice/check/export" + "?" + $.param(param);
        },
        formatDate:function(row, column, cellValue, index) {
            if (cellValue != null && cellValue != "") {
                return Trim(cellValue).substring(0, 10);
            } else {
                return "";
            }
        },
        doInvoiceCheckHistoryAdd: function (row) {
            var params = {};

            params.invoiceType = row.invoiceType;
            params.invoiceCode = row.invoiceCode;
            params.invoiceNo = row.invoiceNo;
            params.invoiceAmount = row.invoiceAmount;
            if (row.invoiceDate == null || row.invoiceDate == "") {
                parent.layer.alert('开票日期不能为空');
            }
            var formatDate = row.invoiceDate.substring(0, 10).split("-");

            params.invoiceDate = formatDate[0] + formatDate[1] + formatDate[2];
            $.ajax({
                url: baseURL + "modules/invoice/check/doCheck", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {


                    if (results.result.RCode === "003") {
                        parent.layer.alert('无效的发票信息，请核对后重新发起查验', {
                            callback: function () {
                                document.getElementById("ifile").src = baseURL + "modules/check/history.html";
                            }
                        });

                    } else if (results.result.RCode === "000") {
                        parent.layer.alert(results.result.msg, {
                            callback: function () {
                                document.getElementById("ifile").src = baseURL + "modules/check/history.html";
                            }
                        });
                    } else if (results.result.RCode === "001") {
                        parent.layer.alert('该发票已查验成功，请勿重复查验', {
                            callback: function () {
                                document.getElementById("ifile").src = baseURL + "modules/check/history.html";
                            }
                        });
                        /*后台执行报错*/
                    } else if (results.result.RCode === "002") {
                        parent.layer.alert('服务器内部错误，请稍后重试', {
                            callback: function () {
                                document.getElementById("ifile").src = baseURL + "modules/check/history.html";
                            }
                        });
                    }


                    /* window.location.href = baseURL + "modules/check/history.html";*/
                }
            });
        },
        doInvoiceCheckDetail: function (row) {
            vm.dialogTableVisible = true;
            if (row != null) {
                vm.detailRow.invoiceCode = row.invoiceCode;
                vm.detailRow.invoiceNo = row.invoiceNo;
            }
            var params = {};
            params.page = this.detailPageData.currentPage;
            params.limit = this.pageSize;
            params.invoiceNo = vm.detailRow.invoiceNo;
            params.invoiceCode = vm.detailRow.invoiceCode;
            params.sidx = "wtf";
            params.order = "desc";
            $.ajax({
                url: baseURL + "modules/invoice/check/detail", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    var resultString = results.result;
                    vm.detailPageData.total = resultString.totalCount;
                    //vm.total = resultString.totalCount;
                    vm.detailPageData.totalPage = resultString.totalPage;
                    vm.invoiceCheckHistoryDetailTableData = [];
                    $.each(resultString.list, function (index, element) {
                        vm.invoiceCheckHistoryDetailTableData.push(element);
                    });

                }
            });
        },

        doInvoiceCheckDetailInPage: function (currentPage) {
            if (!isNaN(currentPage)) {
                this.detailPageData.currentPage = currentPage;
            }
            var params = {};
            params.page = this.detailPageData.currentPage;
            params.limit =this.pageSize;
            params.invoiceNo = vm.detailRow.invoiceNo;
            params.invoiceCode = vm.detailRow.invoiceCode;
            params.sidx = "wtf";
            params.order = "desc";
            $.ajax({
                url: baseURL + "modules/invoice/check/detail", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    var resultString = results.result;
                    vm.detailPageData.total = resultString.totalCount;
                    vm.detailPageData.totalPage = resultString.totalPage;
                    vm.invoiceCheckHistoryDetailTableData = [];
                    $.each(resultString.list, function (index, element) {
                        vm.invoiceCheckHistoryDetailTableData.push(element);
                    });

                }
            });
        },
        handleDetailSizeChange: function (val) {
            this.pageSize = val;
            this.doInvoiceCheckDetailInPage(1);
        },
        onSubmit: function () {
            this.getInvoiceCheckHistory();
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (vm.pageData.total > 0) {
                this.getInvoiceCheckHistory(1);
            }
        },
        currentChange: function (val) {
            if (vm.pageData.total > 0) {
                this.getInvoiceCheckHistory(val);
            }
        },
        assignNullValue: function (row, column, cellValue, index) {
            if (cellValue == null || cellValue == '') {
                return "一 一";
            } else {
                return cellValue;
            }

        },
        /**
         * 行号 - 主列表
         */
        mainIndex: function (index) {
            return index + (this.pageData.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 明细
         */
        detailIndex: function (index) {
            return index + (this.detailPageData.currentPage - 1) * this.pageSize + 1;
        }
    }

});

function Trim(str) {
    if (str != null) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }
}

/**
 * 过滤空格后判空
 */
function TrimLength(o) {
    if (typeof (o) != "undefined" && o != null) {
        if (!isNaN(o)) {
            return o.replace(/(^\s*)|(\s*$)/g, "").length == 0;
        }
    } else {
        return true;
    }

}

/**当前月份第一天yyyy-mm-dd*/
function getCurrentMonthDateTime() {
    var monthDate = new Date();
    monthDate.setDate(1);

    return currentYear + '-' + fmtNumber(currentMonth-1) + "-" + fmtNumber(monthDate.getDate());

}

/**当前日期时间yyyy-mm-dd */
function getCurrentDateTime1() {

    return currentYear + '-' + fmtNumber(currentMonth) + "-" + fmtNumber(currentDay);
}

/*补0*/
function fmtNumber(s) {
    return s < 10 ? '0' + s : s;
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

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}

function formatInvoiceStatus(val){

    if(val==0){
        return "正常";
    }
    if(val==1){
        return "失控";
    }
    if(val==2){
        return "作废";
    }
    if(val==3){
        return "红冲";
    }else{
        return "异常";
    }

}
function formatSourceSystem(val){
    if(val==0){
        return "采集";
    }else if(val==1){
        return "查验";
    }else{
        return "录入";
    }
}
function formatQsType(val){
    if(val==0){
        return "扫码签收";
    }
    if(val==1){
        return "扫描仪签收";
    }
    if(val==2){
        return "app签收";
    }
    if(val==3){
        return "导入签收";
    }
    if(val==4){
        return "手工签收";
    }else{
        return "pdf上传签收";
    }
}
function formatOutReason(val){
    if(val==1){
        return "免税项目用";
    }
    if(val==2){
        return "集体福利,个人消费";
    }
    if(val==3){
        return "非正常损失";
    }
    if(val==4){
        return "简易计税方法征税项目用";
    }
    if(val==5){
        return "免抵退税办法不得抵扣的进项税额";
    }
    if(val==6){
        return "纳税检查调减进项税额";
    }
    if(val==7){
        return "红字专用发票通知单注明的进项税额";
    }else{
        return "上期留抵税额抵减欠税";
    }
}

