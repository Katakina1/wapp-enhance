
var num=0;

var personalTaxNumber=123456789012345;
/**
 * 格式化货币 千分号和四舍五入保留两位小数
 * @param cellvalue
 * @param options
 * @param rowObjectdataList
 * @returns {*}
 */
function decimal(cellvalue) {
    if(cellvalue!=null){
        var val=Math.round(cellvalue * 100) / 100;
        return val.formatMoney();
    }
    return "—— ——";
}
Number.prototype.formatMoney = function(places, symbol, thousand, decimal) {
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


function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
}


var dataList=[];
var vm = new Vue({
    el: '#rrapp',
    data: {
        listLoading: false,
        qsStartDateOptions:{},
        show:false,
        tableData:dataList,
        currentPage: 1,
        total: 500,
        multipleSelection: [],
        form:{
            key:'',
            scanCode:null,
            invoiceNo: null,
            invoiceCode: null,
            invoiceDate: null,
            invoiceAmount: null,
            checkCode: null
        },
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
            rzhBelongDate:null,
            rzhDate:null,
            outDate:null,
            outBy:null,
            outReason:null,
            outList: [],
            qsStatus:null,
            outStatus:null,
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
        deleteFlag:false,
        //下面是对应模态框隐藏的属性
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false
        //showList: true
    },watch:{
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
        'form.invoiceCode':{
            handler: function (val,oldValue) {

                var reg=/^[0-9]{0,}$/;
                if(!(reg.test(val)||val==null)){
                    Vue.nextTick(function() {
                        vm.form.invoiceCode = oldValue;
                    })
                }
                //判断发票类型是否改变
                if(val!="" && val!=null && oldValue != null){

                    if(getFplx(oldValue)!=getInvoiceType()){
                        vm.form.invoiceAmount="";
                    }
                }
            },
            deep:true
        },
        'form.invoiceAmount':{
            handler: function (val,oldValue) {
                var _this = this;
                var invoiceType=getInvoiceType();
                var reg=null;
                if(invoiceType=="01"||invoiceType=="03") {
                    reg=/(^[0-9]{0,}\.{0,1}[0-9]{0,2}$)|(^-[0-9]{0,}\.{0,1}[0-9]{0,2}$)/;
                }else{
                    reg =/^[0-9]{0,}$/;
                }
                if(!(reg.test(val)||val==null)){
                    Vue.nextTick(function() {
                        _this.form.invoiceAmount = oldValue;
                    })
                }
            },
            deep:true
        }
    },
    mounted: function () {
        $("#autofocusinput").focus();
        this.qsStartDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
     },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        checkNullFormat:function (row, column, index) {
            if(index==null||index==''){
                return "—— ——" ;
            }
            return index;
        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs['form'].resetFields();
        },focuspickerchange:function(val){
            if(val==1){
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background','#ffaa00');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color','#333333');
            }else{
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background','#ffaa00');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color','#333333');
            }
        },
        blurpickerchange:function(val){
            if(val==1){
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background','white');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color','#ffaa00');
            }else{
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background','white');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color','#ffaa00');
            }
        },
        submitForm: function () {
            //表单校验
            this.$refs['form'].validate(function (valid) {
                if (valid) {
                    vm.saveCheckData();
                } else {
                    return false
                }
            })
        },
        add: function () {
            var scanCode = vm.form.scanCode;
            if (scanCode == null && scanCode == "" || scanCode == undefined) {
                return;
            } else if (scanCode.split(",").length <= 0) {
                return;
            } else {
                //code正确格式：01,10,011001700112发票代码,10113872发票号码,32金额,20180224开票日期,17602940807063740560校验码,0819,
                var code = scanCode.split(",");
                var invoiceNo = code[3];
                var invoiceCode = code[2];
                var invoiceAmount = code[4];
                var invoiceDate = code[5];
                var invoiceType = getFplx(invoiceCode);
                var checkCode = code[6].substr(14);

                var uuid = invoiceCode + invoiceNo;
                vm.listLoading=true;
                //判断扫描表是否存在数据
                $.ajax({
                    type: "POST",
                    url: baseURL + "sweepcode/getInvoiceData",
                    data: {uuid: uuid},
                    success: function (r) {
                        var id = r.invoiceId;
                        if (id != null) {
                            vm.listLoading=false;
                            alert("该发票已进行签收操作，不可重复操作！");
                            vm.reset();
                            return;
                        } else {
                            var params = {
                                invoiceNo: invoiceNo,
                                invoiceType: invoiceType,
                                invoiceCode: invoiceCode,
                                invoiceDate: invoiceDate,
                                invoiceAmount: invoiceAmount,
                                checkCode: checkCode
                            };
                            if (invoiceType == "01" || invoiceType == "03" || invoiceType == "14") {
                                $.ajax({
                                    type: "POST",
                                    url: baseURL + "sweepcode/receiptInvoiceTwo",
                                    contentType: "application/json",
                                    data: JSON.stringify(params),
                                    success: function (r) {
                                        var jqdata  = {
                                            "qsStatus": r.entity.qsStatus,
                                            "notes": r.entity.notes,
                                            "invoiceCode": r.entity.invoiceCode,
                                            "invoiceNo": r.entity.invoiceNo,
                                            "invoiceType":r.entity.invoiceType,
                                            "invoiceDate": r.entity.invoiceDate,
                                            "invoiceAmount": r.entity.invoiceAmount,
                                            "taxAmount": r.entity.taxAmount,
                                            "checkCode": r.entity.checkCode
                                        };
                                        dataList.unshift(jqdata);
                                        vm.listLoading=false;
                                        vm.reset();
                                        //vm.clearValidate();
                                    }
                                });
                            } else if(invoiceType=="04" || invoiceType == "10"||invoiceType =="11"){
                                $.ajax({
                                    type: "POST",
                                    url: baseURL + "sweepcode/ReceiptInvoice",
                                    contentType: "application/json",
                                    data: JSON.stringify(params),
                                    success: function (r) {
                                        var jqdata = {
                                            "qsStatus": r.entity.qsStatus,
                                            "notes": r.entity.notes,
                                            "invoiceCode": r.entity.invoiceCode,
                                            "invoiceNo": r.entity.invoiceNo,
                                            "invoiceType":r.entity.invoiceType,
                                            "invoiceDate": r.entity.invoiceDate,
                                            "invoiceAmount": r.entity.invoiceAmount,
                                            "taxAmount": r.entity.taxAmount,
                                            "checkCode": r.entity.checkCode
                                        };
                                        dataList.unshift(jqdata);
                                        vm.listLoading=false;
                                        vm.reset();
                                        //vm.clearValidate();
                                    }
                                });
                            }else{
                                alert("扫码数据有误！");
                                vm.reset();
                            }
                        }
                    }
                });

            }
        },qsStatusFormatter:function (row) {
            var val=row.qsStatus;
            if(val=="0"){
                return "签收失败";
            }else if(val=="1"){
                return "签收成功";
            }
        },formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '';
            }
        },invoiceAmountFormatDecimal:function (row,column,index) {
            return decimal(row.invoiceAmount);
        },taxAmountFormatDecimal:function (row,column,index) {
            return decimal(row.taxAmount);
        },
        reset: function () {
            /*vm.clearValidate();*/
            vm.form.invoiceNo = null;
            vm.form.invoiceCode = null;
            vm.form.invoiceDate = null;
            vm.form.invoiceAmount = null;
            vm.form.scanCode = null;
            $("#labelCode label").text('金额:');
            $("#lableMsg").attr("placeholder", "请输入金额");
        },
        saveCheckData: function () {
            var invoiceType=getFplx(vm.form.invoiceCode);
            var uuid = vm.form.invoiceCode + vm.form.invoiceNo;
            vm.listLoading=true;
            //判断扫描表是否存在数据
            $.ajax({
                type: "POST",
                url: baseURL + "sweepcode/getInvoiceData",
                data: {uuid: uuid},
                success: function (r) {
                    var id = r.invoiceId;
                    if (id != null) {
                        alert("该发票已进行签收操作，不可重复操作！")
                        vm.form.scancode = null;
                        vm.form.invoiceNo = null;
                        vm.form.invoiceCode = null;
                        vm.form.invoiceDate = null;
                        vm.form.invoiceAmount = null;
                        vm.listLoading=false;
                        vm.reset();
                        return;
                    } else {
                        if (invoiceType=="01" ||invoiceType =="03" || invoiceType == "14") {
                            var params = {
                                invoiceNo: vm.form.invoiceNo,
                                invoiceCode: vm.form.invoiceCode,
                                invoiceDate: vm.form.invoiceDate,
                                invoiceType:invoiceType
                            };
                            if(invoiceType == "14") {
                                params.checkCode = vm.form.invoiceAmount;
                            } else {
                                params.invoiceAmount = vm.form.invoiceAmount;
                            }
                            $.ajax({
                                type: "POST",
                                url: baseURL + "sweepcode/receiptInvoiceTwo",
                                contentType: "application/json",
                                data: JSON.stringify(params),
                                success: function (r) {
                                    if(r.entity!=null){
                                        var jqdata  = {
                                            "qsStatus": r.entity.qsStatus,
                                            "notes": r.entity.notes,
                                            "invoiceCode": r.entity.invoiceCode,
                                            "invoiceNo": r.entity.invoiceNo,
                                            "invoiceType":r.entity.invoiceType,
                                            "invoiceDate": r.entity.invoiceDate,
                                            "invoiceAmount": r.entity.invoiceAmount,
                                            "taxAmount": r.entity.taxAmount,
                                            "checkCode": r.entity.checkCode
                                        };
                                        dataList.unshift(jqdata);
                                        vm.listLoading=false;
                                        vm.reset();
                                        // vm.clearValidate();
                                    }else{
                                        vm.listLoading=false;
                                        alert("输入数据错误,请重新输入！");
                                        // vm.clearValidate();
                                        vm.reset();
                                    }

                                }
                            });
                        } else if(invoiceType=="04" || invoiceType == "10"||invoiceType =="11"){
                            var params = {
                                invoiceNo: vm.form.invoiceNo,
                                invoiceCode: vm.form.invoiceCode,
                                invoiceDate: vm.form.invoiceDate,
                                checkCode:vm.form.invoiceAmount,
                                invoiceType:invoiceType
                            };
                            $.ajax({
                                type: "POST",
                                url: baseURL + "sweepcode/ReceiptInvoice",
                                contentType: "application/json",
                                data: JSON.stringify(params),
                                success: function (r) {
                                    if(r.entity!=null){
                                        var jqdata = {
                                            "qsStatus": r.entity.qsStatus,
                                            "notes": r.entity.notes,
                                            "invoiceCode": r.entity.invoiceCode,
                                            "invoiceNo": r.entity.invoiceNo,
                                            "invoiceType":r.entity.invoiceType,
                                            "invoiceDate": r.entity.invoiceDate,
                                            "invoiceAmount": r.entity.invoiceAmount,
                                            "taxAmount": r.entity.taxAmount,
                                            "checkCode": r.entity.checkCode
                                        };
                                        dataList.unshift(jqdata);
                                        vm.listLoading=false;
                                        vm.reset();
                                        // vm.clearValidate();
                                    }else{
                                        vm.listLoading=false;
                                        alert("输入数据错误,请重新输入")
                                         //vm.clearValidate();
                                        vm.reset();
                                    }

                                }
                            });
                        }else{
                            vm.listLoading=false;
                            alert("输入数据有误！");
                            vm.reset();
                        }

                    }
                }
            });
    },
        printform:function(oper) {
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
            if(oper<10){
                var w=screen.availWidth-10;
                var h=screen.availHeight-30;
                if(oper<=3){
                    var head='<script type="text/javascript" charset="utf-8" src="../../js/customer/resource.js"></script>';
                    var prnhtml = $('#printdiv' + oper).find('.el-dialog__body .col-xs-9').html();
                }else{
                    var prnhtml=$('#printdiv'+oper).html();
                }

                var newWin = parent.window.open('',"win","fullscreen=0,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=1,width=" + w + ",height=" + h + ",top=0,left=0",true);
                newWin.document.write(prnhtml);
                newWin.document.close();
                newWin.focus();
                newWin.print();
                newWin.close();
            }else{
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
        },focusChange:function () {
            // vm.clearValidate();
            this.reset();
        },changeMsg:function (val) {
            var invoice=vm.form.invoiceCode;
           var invoiceType=getFplx(invoice);
           //根据输入的代码解析出的发票类型对金额或校验码进行操作
           if(invoiceType=="01"||invoiceType=="03"){
               $("#lableMsg").attr("maxlength", "");
               $("#lableMsg").attr("placeholder", "请输入金额");
                $("#labelCode label").text('金额:');
           }else{
               $("#lableMsg").attr("maxlength", "6");
               $("#lableMsg").attr("placeholder", "请输入校验码");
               $("#labelCode label").text('校验码:');
               /*$("#lableMsg").keyup(verificationInvoiceAmountValue(this));*/
           }
        } ,
        changeFun: function (row) {
            this.multipleSelection = row;
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
        detail:function(row){
            /*var value = row.id;*/
            var uuid=row.invoiceCode+row.invoiceNo;
            var id;
            $.ajax({
                type: "POST",
                url: baseURL + "sweepcode/getInvoiceId",
                data: {uuid: uuid},
                success: function (r) {
                    id = r.id;
                    var value = id;
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
                }
            });
        },deleteData:function (row,index) {
            if (row.qsStatus == "1") {
                alert("签收成功不可删除");
                return;
            } else {
                var uuid = row.invoiceCode + row.invoiceNo;
                vm.openConfirm(vm, "\n" + "确定要删除选中的记录？", function () {
                    $.ajax({
                        type: "POST",
                        url: baseURL + "sweepcode/deleteInvoiceData",
                        data: {uuid:uuid},
                        success: function (r) {
                            alert(r.msg);
                            dataList.splice(index,1);
                            this.tableData=dataList;
                        }
                    });
                }, function () {
                });
            }
        }
}
});


/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fpdmlist=new Array("144031539110","131001570151","133011501118","111001571071");
    var  fplx="";
    if (fpdm.length==12){
        var fplxflag=fpdm.substring(7,8);

        for(var i =0; i<fpdmlist.length;i++){
            if(fpdm==fpdmlist[i]){
                fplx="10";
                break;
            }
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="11") {
            fplx="10";
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="12") {
            fplx="14";
        }
        if (fpdm.substring(0,1)=="0" && (fpdm.substring(10,12)=="06"|| fpdm.substring(10,12)=="07")) {
            //判断是否为卷式发票  第1位为0且第11-12位为06或07
            fplx="11";
        }
        if(fpdm.substring(0,1)=="0"&&(fpdm.substring(10,12)=="04"|| fpdm.substring(10,12)=="05")){
            fplx="04"
        }
        if (fplxflag=="2" && fpdm.substring(0,1)!="0") {
            fplx="03";
        }

    }else if(fpdm.length==10){
        var fplxflag=fpdm.substring(7,8);
        if(fplxflag=="1"||fplxflag=="5"){
            fplx="01";
        }else if(fplxflag=="6"||fplxflag=="3"){
            fplx="04";
        }else if(fplxflag=="7"||fplxflag=="2"){
            fplx="02";
        }

    }
    return fplx;
}

/*
function deleteData(val){
    $.ajax({
        type: "POST",
        url: baseURL + "sweepcode/delete",
        contentType: "application/json",
        data: {invoiceNo:val},
        success: function (r) {
            vm.reload();
        }

    });
}*/
function createJson(prop, val) {
    // 如果 val 被忽略
    if(typeof val === "undefined") {
        // 删除属性
        delete str1[prop];
    }
    else {
        // 添加 或 修改
        str1[prop] = val;
    }
}

function IsInArray(arr,val){

    var testStr=','+arr.join(",")+",";

    return testStr.indexOf(","+val+",")!=-1;

}

function dateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}

/**
 * 验证数据输入格式
 * @param t 当前的input
 */
/*function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceNo = t.value.replace(reg, '');

}*/
/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceCodeValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceCode = t.value.replace(reg, '');

}

/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceAmountValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceAmount = t.value.replace(reg, '');

}



function detailDateFormat(value) {
    value = value.replace("-","/");
    value = value.replace("-","/");
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}

function getInvoiceType(){
    var invoice=vm.form.invoiceCode;
    if(invoice!=null&&!undefined){
        return getFplx(invoice)
    }else{
        return "";
    }
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