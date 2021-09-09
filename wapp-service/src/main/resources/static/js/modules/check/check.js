
var personalTaxNumber = 123456789012345;

var currentUuIds = [];
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
        el: "#invoice_check",
        data: {
            loading: false,
            invoiceCheckHistoryTableData: [],
            autoScan: '',
            invoiceCheckQuery: {
                invoiceNo: '',
                invoiceCode: '',
                invoiceDate: '',
                invoiceAmount: ''

            },
            pageData: {
                currentPage: 1,
                total: 10,
            },
            pagerCount: PAGE_PARENT.PAGER_COUNT,
            total: 0,
            totalPage: 1,
            pageList: PAGE_PARENT.PAGE_LIST,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            amountCode: true,
            checkCode: false,

            detailDialogFormVisible: false, /*明细弹框默认隐藏*/
            tempValue: null, /*鬼知道什么鬼*/
            detailEntityList: [], /*鬼知道什么鬼*/
            tempDetailEntityList: [],
            detailDialogFormInnerVisible: false,

            detailDialogCheckFormVisible: false, /*14*/
            dialogVisible: false,
            detailDialogCheckFormInnerVisible: false,
            detailDialogVehicleFormVisible: false,
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
                dqskssq: null,
                rzhDate: null,
                outDate: null,
                outBy: null,
                outReason: null,
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
                invoiceDate: null,//日期。。。。。。。。。。。。。。。。。。。。。
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
                stringTotalAmount: null,
                machineNo: ""
            },
            reg: /^[0-9]*$/,
            reg1: /^\d+(\.\d+)?$/
        },
        created: function () {
            /* this.getInvoiceCheckHistory();*/
        },
        mounted: function () {
            $("#autofocusinput").focus();
        },
        methods: {
            doInvoiceNoCheckLimit: function (values) {
                var e = new RegExp(vm.reg);
                if (!e.test(values)) {
                    vm.invoiceCheckQuery.invoiceNo = null;
                }
            },

            doInvoiceAmountCheckLimit: function (values) {
                // var e = new RegExp(vm.reg1);
                var positiveReg = /(^[0]$)|(^[1-9]\d*$)|(^[1-9]\d*\.\d{1,2}$)|(^0\.\d{1,2}$)/;
                var negativeReg = /(^-[1-9]\d*$)|(^-[1-9]\d*\.\d{1,2}$)|(^-0\.\d{1,2}$)/;
                if (!(new RegExp(positiveReg).test(values) || new RegExp(negativeReg).test(values))) {
                    vm.invoiceCheckQuery.invoiceAmount = null;
                }
            },
            focuspickerchange: function (val) {
                if (val == 1) {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
                } else {
                    $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                    $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
                }
            },
            blurpickerchange: function (val) {
                if (val == 1) {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
                } else {
                    $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                    $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
                }
            },
            getInvoiceCheckHistory: function (currentPage) {
                if (!isNaN(currentPage)) {
                    this.pageData.currentPage = currentPage;
                }
                var params = {};
                params.page = this.pageData.currentPage;
                params.limit = this.pageSize;
                params.sidx = "wtf";
                params.order = "desc";
                $.ajax({
                    url: baseURL + "modules/invoice/check/history", async: true, type: "POST", dataType: "json",
                    data: params,
                    success: function (results) {
                        var resultString = results.result;
                        vm.pageData.total = resultString.totalCount;
                        vm.total = resultString.totalCount;
                        vm.invoiceCheckHistoryTableData = [];
                        $.each(resultString.list, function (index, element) {
                            vm.invoiceCheckHistoryTableData.push(element);
                        });
                    }
                });
            },
            doInvoiceCheckByHand: function () {
                var params = {};
                params.invoiceNo = this.invoiceCheckQuery.invoiceNo;
                params.invoiceCode = this.invoiceCheckQuery.invoiceCode;
                params.invoiceDate = this.invoiceCheckQuery.invoiceDate;
                params.invoiceAmount = this.invoiceCheckQuery.invoiceAmount;

                if (currentUuIds.length > 0 && contains(currentUuIds, params.invoiceCode + params.invoiceNo)) {
                    alert('此发票已在查验列表中');
                    return;
                }
                vm.loading = true;
                $.ajax({
                    url: baseURL + "modules/invoice/check/handCheck", async: true, type: "POST", dataType: "json",
                    data: params,
                    success: function (results) {
                        vm.loading = false;
                        if (results.code == 0) {
                            if (results.result.RCode === "003") {
                                vm.$alert('无效的发票信息，请核对后重新发起查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn'
                                });
                                vm.resetForm('invoiceCheckQuery');
                            } else if (results.result.RCode === "000") {
                                vm.resetForm('invoiceCheckQuery');
                                var uuid = results.result.data.invoiceCode + results.result.data.invoiceNo;
                                //if (vm.invoiceCheckHistoryTableData.length > 0) {
                                //    $.each(vm.invoiceCheckHistoryTableData, function (index, element) {
                                //        var tempUuid = element.invoiceCode + element.invoiceNo;
                                //        if (tempUuid === uuid) {
                                //            vm.invoiceCheckHistoryTableData.splice(index, 1);
                                //        }
                                //        vm.invoiceCheckHistoryTableData.push(results.result.data);
                                //    });
                                //} else {
                                //    vm.invoiceCheckHistoryTableData.push(results.result.data);
                                //}
                                vm.invoiceCheckHistoryTableData.push(results.result.data);
                                currentUuIds.push(uuid);
                            } else if (results.result.RCode === "001") {
                                vm.$alert('该发票已查验成功，请勿重复查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn'
                                });
                                vm.resetForm('invoiceCheckQuery');
                            } else if (results.result.RCode === "002") {
                                vm.$alert('服务器内部错误，请稍后重试', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn'
                                });
                            } else if (results.result.RCode === "004") {
                                vm.$alert('该发票已被他人查验，请勿重复查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn',
                                    callback: function () {
                                        vm.autoScan = '';
                                    }
                                });
                            }
                        } else {
                            if (results.code != 401) {
                                alert(results.msg);
                            }
                        }
                    }
                });
            },
            onSubmit: function () {
                //表单校验
                this.$refs['invoiceCheckQuery'].validate(function (valid) {
                    if (valid) {
                        vm.doInvoiceCheckByHand();
                    } else {
                        return false
                    }
                })
            },
            autoCheckInvoice: function (values) {
                var paramsString = values.split(",");
                var INVOICE_TYPE = "04,10,11,14";

                var params = {};
                params.invoiceCode = Trim(paramsString[2]);
                if (INVOICE_TYPE.indexOf(getFplx(Trim(paramsString[2]))) != -1) {
                    params.invoiceAmount = Trim(paramsString[6]).substring(Trim(paramsString[6]).length - 6, Trim(paramsString[6]));
                } else {
                    params.invoiceAmount = Trim(paramsString[4]);
                }
                /*    params.invoiceAmount = Trim(paramsString[4]);*/
                params.invoiceNo = Trim(paramsString[3]);
                params.invoiceDate = Trim(paramsString[5]);

                if (currentUuIds.length > 0 && contains(currentUuIds, params.invoiceCode + params.invoiceNo)) {
                    alert('此发票已在查验列表中');
                    return;
                }
                vm.loading = true;
                $.ajax({
                    url: baseURL + "modules/invoice/check/handCheck", async: true, type: "POST", dataType: "json",
                    data: params,
                    success: function (results) {
                        vm.loading = false;
                        if (results.code == 0) {
                            if (results.result.RCode === "003") {
                                vm.$alert('无效的发票信息，请核对后重新发起查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn',
                                    callback: function () {
                                        vm.autoScan = '';

                                    }
                                });
                            } else if (results.result.RCode === "000") {
                                vm.autoScan = '';
                                var uuid = results.result.data.invoiceCode + results.result.data.invoiceNo;
                                // if (vm.invoiceCheckHistoryTableData.length > 0) {
                                //     $.each(vm.invoiceCheckHistoryTableData, function (index, element) {
                                //         var tempUuid = element.invoiceCode + element.invoiceNo;
                                //         if (tempUuid === uuid) {
                                //             vm.invoiceCheckHistoryTableData.splice(index, 1);
                                //         }
                                //         vm.invoiceCheckHistoryTableData.push(results.result.data);
                                //     });
                                // } else {
                                //     vm.invoiceCheckHistoryTableData.push(results.result.data);
                                // }
                                vm.invoiceCheckHistoryTableData.push(results.result.data);
                                currentUuIds.push(uuid);

                            } else if (results.result.RCode === "001") {
                                vm.$alert('该发票已查验成功，请勿重复查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn',
                                    callback: function () {
                                        vm.autoScan = '';
                                    }
                                });
                                /*后台执行报错*/
                            } else if (results.result.RCode === "002") {
                                vm.$alert('服务器内部错误，请稍后重试', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn',
                                    callback: function () {
                                        vm.autoScan = '';
                                    }
                                });
                            } else if (results.result.RCode === "004") {
                                vm.$alert('该发票已被他人查验，请勿重复查验', '消息提示', {
                                    confirmButtonText: '确定',
                                    confirmButtonClass: 'queryBtn',
                                    callback: function () {
                                        vm.autoScan = '';
                                    }
                                });
                            }
                        } else {
                            if (results.code != 401) {
                                alert(results.msg);
                            }
                        }
                    }
                });


            }
            , numberFormat: function (row, column, cellValue) {
                /*
                 * 参数说明：
                 * number：要格式化的数字
                 * decimals：保留几位小数
                 * dec_point：小数点符号
                 * thousands_sep：千分位符号
                 * */
                if (cellValue == null || cellValue == '' || cellValue == undefined) {
                    return "一 一";
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
            }
            ,
            analyseInvoiceCode: function (values) {
                var e = new RegExp(vm.reg);
                if (!e.test(values)) {
                    vm.invoiceCheckQuery.invoiceCode = null;
                } else {
                    var INVOICE_TYPE = "04,10,11,14";
                    if (INVOICE_TYPE.indexOf(getFplx(values)) != -1) {
                        this.amountCode = false;
                        this.checkCode = true;
                    } else {
                        this.amountCode = true;
                        this.checkCode = false;
                    }
                }

            }
            ,
            formatDate: function (row, column, cellValue, index) {
                if (cellValue != null && cellValue != "") {

                    return cellValue.substring(0, 4) + '-' + cellValue.substring(4, 6) + '-' + cellValue.substring(6, 8);
                } else {
                    return "";
                }
            }
            ,
            detail: function (row) {
                $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
                var r = row.responseInvoice;
                vm.detailForm.checkCode = r.checkCode;
                vm.detailForm.gfTaxNo = r.buyerTaxNo;
                vm.detailForm.gfName = r.buyerName;
                vm.detailForm.invoiceDate = detailDateFormat(r.invoiceDate.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3"));
                vm.detailForm.gfAddressAndPhone = r.buyerAddressPhone;
                vm.detailForm.gfBankAndNo = r.buyerAccount;
                vm.detailForm.xfName = r.salerName;
                vm.detailForm.xfTaxNo = r.salerTaxNo;
                vm.detailForm.xfAddressAndPhone = r.salerAddressPhone;
                vm.detailForm.xfBankAndNo = r.salerAccount;
                vm.detailForm.remark = r.remark;
                vm.detailForm.totalAmount = vm.numberFormat(null, null, r.totalAmount);
                vm.detailForm.invoiceCode = r.invoiceCode;
                vm.detailForm.invoiceNo = r.invoiceNo;
                vm.detailForm.stringTotalAmount = row.stringTotalAmount;
                vm.detailForm.machineNo = r.machineNo;
                //发票类型
                var invoiceType = row.invoiceType;
                vm.detailForm.invoiceType = invoiceType;
                if (invoiceType == "03") {
                    vm.detailForm.taxAmount = vm.numberFormat(null, null, r.taxAmount);
                    var salerAddressPhone = getPhoneOrAddress(r.salerAddressPhone);
                    var bankOrName = getBankAccountOrName(r.salerAccount);
                    vm.detailForm.phone = salerAddressPhone.phone;
                    vm.detailForm.address = salerAddressPhone.address;
                    vm.detailForm.account = bankOrName.account;
                    vm.detailForm.bank = bankOrName.bank;

                    vm.detailForm.vehicleType = r.vehicleType;
                    vm.detailForm.factoryModel = r.factoryModel;
                    vm.detailForm.productPlace = r.productPlace;
                    vm.detailForm.certificate = r.certificate;
                    vm.detailForm.certificateImport = r.certificateImport;
                    vm.detailForm.inspectionNum = r.inspectionNum;
                    vm.detailForm.engineNo = r.engineNo;
                    vm.detailForm.vehicleNo = r.vehicleNo;
                    vm.detailForm.taxRate = r.taxRate;
                    vm.detailForm.taxBureauName = r.taxBureauName;
                    vm.detailForm.taxBureauCode = r.taxBureauCode;
                    vm.detailForm.invoiceAmount = vm.numberFormat(null, null, r.invoiceAmount);
                    vm.detailForm.taxRecords = r.taxRecords;
                    vm.detailForm.tonnage = r.tonnage;
                    vm.detailForm.limitPeople = r.limitPeople;
                    vm.detailForm.buyerIdNum = r.buyerIdNum;

                    vm.detailDialogVehicleFormVisible = true;
                } else {
                    if (invoiceType == "14") {
                        vm.detailDialogCheckFormVisible = true;
                    } else {
                        vm.detailDialogFormVisible = true;
                    }
                    vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.totalAmount);
                    vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmount);
                    vm.detailEntityList = r.detailList;
                    for (var i = 0; i < vm.detailEntityList.length; i++) {
                        vm.detailEntityList[i].unitPrice =  vm.detailEntityList[i].unitPrice;
                        vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                        vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                    }
                    vm.tempDetailEntityList = vm.detailEntityList;
                    if (r.detailList.length > 8) {
                        vm.detailEntityList = null;
                    }
                }
            },
            deleteCheck: function (row) {
                vm.openConfirm(vm, "是否确认删除？", function () {
                    //重复发票删除不操作表 类型错误发票不操作数据库
                    if (row.handleCode == '0001') {
                        for (var i = 0; i < vm.invoiceCheckHistoryTableData.length; i++) {
                            if (vm.invoiceCheckHistoryTableData[i] == row) {
                                vm.invoiceCheckHistoryTableData.splice(i, 1);
                                var index = currentUuIds.indexOf(row.invoiceCode + row.invoiceNo);
                                if (index > -1) {
                                    currentUuIds.splice(index, 1);
                                }
                            }
                        }
                        alert("删除成功！");
                    } else {
                        var formData = {
                            invoiceCode: row.invoiceCode,
                            invoiceNo: row.invoiceNo
                        };
                        vm.$http.post(baseURL + 'modules/invoice/checkDelete', formData, {
                            'headers': {
                                "token": token
                            }
                        }).then(function (response) {
                            if (response.data.code == 0 && response.data.result) {
                                for (var i = 0; i < vm.invoiceCheckHistoryTableData.length; i++) {
                                    if (vm.invoiceCheckHistoryTableData[i] == row) {
                                        vm.invoiceCheckHistoryTableData.splice(i, 1);
                                        var index = currentUuIds.indexOf(row.invoiceCode + row.invoiceNo);
                                        if (index > -1) {
                                            currentUuIds.splice(index, 1);
                                        }
                                    }
                                }
                                alert("删除成功");
                            } else {
                                if (response.data.code != 401) {
                                    alert("删除失败");
                                }
                            }
                        }, (err) => {
                            if (err.status == 408) {
                                alert(err.statusText);
                            }
                        });
                    }
                }, function () {
                });
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
                        var prnhtml = $('#printdiv' + oper).find('.el-dialog__body .col-xs-12').html();
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
            }
            ,
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

            }
            ,
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
            }
            ,
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
            }
            ,
            showInner: function () {
                vm.detailDialogFormInnerVisible = true;
            }
            ,
            detailInnerFormCancel: function () {
                vm.detailDialogFormInnerVisible = false;
            }
            ,
            detailCheckInnerFormCancel: function () {
                vm.detailDialogCheckFormInnerVisible = false;
            }
            ,
            handleSizeChange: function (val) {
                this.pageSize = val;
                this.getInvoiceCheckHistory(1);
            }
            ,

            assignNullValue: function (row, column, cellValue, index) {
                if (cellValue == null || cellValue == '') {
                    return "一 一";
                } else {
                    return cellValue;
                }
            }
            ,
            lastSix: function (row, column, cellValue, index) {
                if (cellValue == null || cellValue == '') {
                    return "一 一";
                } else {
                    return cellValue.substring(cellValue.length - 6, cellValue.leng);
                }
            }
            ,
            resetForm: function (formName) {
                //对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
                this.$refs[formName].resetFields();
            }
        }

    })
;

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

function dateFormat(value) {
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    return tempYear + tempMonth + "月" + tempDay;
}

/**
 *  过滤空格
 */
function Trim(o) {
    return o.replace(/(^\s*)|(\s*$)/g, "");
}

/**
 * 过滤空格后判空
 */
function TrimLength(o) {
    if (typeof (o) != "undefined" && o != null) {
        if (!isNaN(o)) {
            return o.replace(/(^\s*)|(\s*$)/g, "").length == 0;
        } else {
            return true;
        }
    } else {
        return true;
    }
}


/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fpdmlist = ['144031539110', "131001570151", "133011501118", "111001571071"];
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
        if (fplxflag == "2" && fpdm.substring(0, 1) != "0") {
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

function contains(arr, obj) {
    var i = arr.length;
    while (i--) {
        if (arr[i] === obj) {
            return true;
        }
    }
    return false;
}

Array.prototype.notEmpty = function () {
    return this.filter(t => t != undefined && t !== null && t != "");
}

function getPhoneOrAddress(val) {
    var obj = {phone: "", address: ""};
    if (val != undefined && val != null && val != '') {
        var valArray = val.match(/((((13[0-9])|(15[^4])|(18[0,1,2,3,5-9])|(17[0-8])|(147))\d{8})|((\d3,4\d3,4|\d{3,4}-|\s)?\d{7,14}))?/g);
        valArray.notEmpty();
        var phone = valArray[0];
        var address = val.replace(phone, "");
        obj.phone = phone;
        obj.address = address;
    }
    return obj;
}

function getBankAccountOrName(val) {
    var obj = {account: "", bank: ""};
    if (val != undefined && val != null && val != '') {
        var valArray = val.match(/(\d{16,19})?/g);
        valArray.notEmpty();
        var account = valArray[0];
        var bank = val.replace(account, "");
        obj.account = account;
        obj.bank = bank;
    }
    return obj;
}