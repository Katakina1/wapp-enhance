
var dataList=[];
var currentIds =[];
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm =new Vue({
        el: '#rrapp',
        data: {
            show:false,
            tableData:dataList,
            currentPage: 1,
            total: 500,
            listLoading: false,
            multipleSelection: [],
            form:{
                scanCode:null,
                invoiceNo: null,
                invoiceCode: null,
                invoiceDate: null,
                invoiceAmount: null,
                notCheckMsg:null,
                checkCode:null
            },
            checkInvoiceDate: {}
        },
        mounted: function () {
            $("#autofocusinput").focus();
            this.form.scanCode;
            this.form.invoiceNo;
            this.form.invoiceCode;
            this.form.invoiceDate;
            this.form.invoiceAmount;
            this.form.notCheckMsg;
            this.form.checkCode;
            this.checkInvoiceDate = {
                disabledDate: function (time) {
                    return time.getTime() >= Date.now();
                }
            };
        },
        watch:{
            'form.invoiceNo':{
                handler: function (val,oldValue) {
                    $('#requireMsg2 .checkMsg').remove();
                    var reg=/^[0-9]{0,}$/;

                    if(!(reg.test(val)||val==null)){
                        Vue.nextTick(function() {
                            vm.form.invoiceNo = oldValue;
                        })
                    }
                },
                deep:true
            },
            'form.invoiceAmount':{
                handler: function (val,oldValue) {
                    $('#requireMsg3 .checkMsg').remove();
                    $('#requireMsg5 .checkMsg').remove();
                    var reg;
                    if(getInvoiceType()=="14"){
                        reg=/^[0-9]{0,}$/;
                    }else{
                        reg=/(^[0-9]{0,}\.{0,1}[0-9]{0,2}$)|(^-[0-9]{0,}\.{0,1}[0-9]{0,2}$)/;
                    }

                    if(!(reg.test(val)||val==null)){
                        Vue.nextTick(function() {
                            vm.form.invoiceAmount = oldValue;
                        })
                    }
                },
                deep:true
            },
            'form.invoiceCode':{
                handler: function (val,oldValue) {

                    $('#requireMsg1 .checkMsg').remove();
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

                    if(getInvoiceType()=="14"){
                        $("#requireMsg5").removeClass("hideItem");
                        $("#requireMsg3").addClass("hideItem");
                    }else{
                        $("#requireMsg3").removeClass("hideItem");
                        $("#requireMsg5").addClass("hideItem");
                    }

                },
                deep:true
            }
        },
        methods: {
            focuspickerchange: function (val) {
                if (val == 1) {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
                } else {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
                }
            },
            blurpickerchange: function (val) {
                if (val == 1) {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
                } else {
                    $('div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                    $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
                }
            },
            invoiceAmountFormatDecimal:function(row) {
                return decimal(row.invoiceAmount);
            } ,

            changeFun: function (row) {

                this.multipleSelection = row;
            },
            checkboxInit: function (row, index) {

                if(row.noAuthTip!='' && row.noAuthTip!=null) {
                    return 0;
                } else {
                    return 1;
                }
            },
            formatInvoiceDate: function (row, column) {
                if (row.invoiceDate != null) {
                    return dateFormat(row.invoiceDate);
                } else {
                    return '';
                }
            }
            ,add:function() {
                var scanCode=this.form.scanCode;
                if(scanCode==null&&scanCode==""||scanCode==undefined){

                } else if(scanCode.split(",").length<=0){

                }else{
                    var code=scanCode.split(",");
                    var invoiceType = getFplx(code[2]);
                    if (invoiceType != '01' && invoiceType != '03' && invoiceType != '14') {
                        alert("发票类型不符！",function(){
                            vm.reset();
                        });
                    }else{
                        var invoiceNo=code[3];
                        var invoiceCode=code[2];
                        var invoiceAmount=code[4];
                        var invoiceDate=code[5];
                        var checkCode=code[6].substring(code[6].length-6);
                        if(invoiceDate.length==8){
                            var year=invoiceDate.substring(0,4);
                            var month=invoiceDate.substring(4,6);
                            var day=invoiceDate.substring(6);
                            invoiceDate=year+"-"+month+"-"+day;
                        }
                        var params = {
                            invoiceNo:invoiceNo,
                            invoiceCode:invoiceCode,
                            invoiceDate:invoiceDate,
                            invoiceAmount:invoiceAmount
                        };
                        $.get(baseURL + "certification/scanCertification/selectCheck",params, function (r) {
                            if(r.code==0&&r.entity!=null){
                                var data={'uuid':true,
                                    'noAuthTip':checkValue(r.entity),
                                    'invoiceCode':r.entity.invoiceCode,
                                    'id':r.entity.id,
                                    'invoiceNo':r.entity.invoiceNo,
                                    'invoiceType':r.entity.invoiceType,
                                    'invoiceDate':r.entity.invoiceDate,
                                    'invoiceAmount':r.entity.invoiceAmount,
                                    'checkCode':r.entity.checkCode
                                };
                                if(currentIds.length>0&&contains(currentIds,r.entity.id)){
                                    return alert("此发票已扫码",function(){
                                        vm.reset();
                                    });
                                }
                                currentIds.push(r.entity.id);
                                dataList.push(data);
                                this.tableData=dataList;
                                vm.reset();
                            }else if(r.code==1) {
                                var data={'uuid':true,
                                    'noAuthTip':"无底账信息",
                                    'invoiceCode':invoiceCode,
                                    'id':invoiceCode+invoiceNo,
                                    'invoiceNo':invoiceNo,
                                    'invoiceType':getFplx(invoiceCode),
                                    'invoiceDate':invoiceDate,
                                    'invoiceAmount':invoiceAmount,
                                    'checkCode':checkCode
                                };
                                if(currentIds.length>0&&contains(currentIds,invoiceCode+invoiceNo)){
                                    return alert("请不要重复操作同一发票",function(){
                                        vm.reset();
                                    });
                                }
                                currentIds.push(invoiceCode+invoiceNo);
                                dataList.push(data);
                                this.tableData=dataList;
                                vm.reset();

                            }

                        });

                    }


                }
            },

            check:function() {

                if (this.multipleSelection.length == 0) {
                    alert("您尚未选择需要提交勾选的发票!");
                    return;
                }
                var ids=[];
                var select= this.multipleSelection;
                for (var i=0;i<select.length;i++){
                    ids.push(select[i].id);
                }
                var idss=ids.join(",");

                this.openConfirm(vm,'确定要认证选中的记录？', function(){
                    $.ajax({
                        type: "GET",
                        url:baseURL + "certification/scanCertification/submit",
                        contentType: "application/json",
                        data: {ids:idss},
                        success: function(r){
                            if(r){
                                vm.tableData=dataList.filter(function (item) {
                                    return !(contains(ids, item.id));
                                });
                                alert('提交认证成功');
                            }else{
                                alert('提交认证失败');
                            }
                        }
                    });
                },function() {});


            },
            reset: function () {
                $("#requireMsg3").removeClass("hideItem");
                $("#requireMsg5").addClass("hideItem");
                $(".checkMsg").remove();
                this.$refs.scanCode.focus();
                this.form.invoiceNo=null;
                this.form.invoiceCode=null;
                this.form.invoiceDate=null;
                this.form.invoiceAmount=null;
                this.form.notCheckMsg=null;
                this.form.checkCode=null;
                this.form.scanCode=null;
            },
            invoiceDate: function(val) {
                $('#requireMsg4 .checkMsg').remove();
                this.form.invoiceDate = val;
            },

            save: function () {
                $(".checkMsg").remove();
                var checkCode="";
                if(getInvoiceType()=="14"){
                    checkCode=vm.form.invoiceAmount;
                }
                var params = {
                    invoiceNo:this.form.invoiceNo,
                    invoiceCode:this.form.invoiceCode,
                    invoiceDate:this.form.invoiceDate,
                    invoiceAmount:this.form.invoiceAmount,
                    checkCode:checkCode
                };

                var checkInvoiceCode=true;
                var checkInvoiceNo=true;
                var checkInvoiceAmount=true;
                var checkInvoiceDate=true;

                if(this.form.invoiceCode==null||this.form.invoiceCode==""||this.form.invoiceCode==undefined){

                    $("#requireMsg1 .el-form-item__content").append('<div class="checkMsg">发票代码不可为空</div>');
                    checkInvoiceCode=false;
                }
                if(this.form.invoiceNo==null||this.form.invoiceNo==""||this.form.invoiceNo==undefined){

                    $("#requireMsg2 .el-form-item__content").append('<div class="checkMsg">发票号码不可为空</div>');
                    checkInvoiceNo=false;
                }
                if(this.form.invoiceAmount==null||this.form.invoiceAmount==""||this.form.invoiceAmount==undefined){

                    $("#requireMsg3 .el-form-item__content").append('<div class="checkMsg">发票金额不可为空</div>');
                    checkInvoiceAmount=false;
                }
                if(this.form.invoiceAmount==null||this.form.invoiceAmount==""||this.form.invoiceAmount==undefined){

                    $("#requireMsg5 .el-form-item__content").append('<div class="checkMsg">验证码不可为空</div>');
                    checkInvoiceAmount=false;
                }
                if(this.form.invoiceDate==null||this.form.invoiceDate==""||this.form.invoiceDate==undefined){

                    $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">开票日期不可为空</div>');
                    checkInvoiceDate=false;
                }

                if(!(checkInvoiceCode && checkInvoiceNo && checkInvoiceAmount && checkInvoiceDate)){
                    return ;
                }

                var invoiceType = getFplx(this.form.invoiceCode);
                if (invoiceType != '01' && invoiceType != '03' && invoiceType != '14') {
                    return alert("发票类型不符！",function(){
                        vm.reset();
                    });

                }
                $.get(baseURL + "certification/scanCertification/selectCheck",params, function (r) {
                    if(r.code==0&&r.entity!=null){
                        var data={'uuid':true,
                            'noAuthTip':checkValue(r.entity),
                            'invoiceCode':r.entity.invoiceCode,
                            'id':r.entity.id,
                            'invoiceNo':r.entity.invoiceNo,
                            'invoiceType':r.entity.invoiceType,
                            'invoiceDate':r.entity.invoiceDate,
                            'invoiceAmount':r.entity.invoiceAmount,
                            'checkCode':r.entity.checkCode
                        };
                        if(currentIds.length>0&&contains(currentIds,r.entity.id)){
                            return alert("请不要重复操作同一发票",function(){
                                vm.reset();
                            });
                        }
                        currentIds.push(r.entity.id);
                        dataList.push(data);
                        this.tableData=dataList;
                        vm.reset();
                    }else if(r.code==1) {
                        var currentInvoiceCode=vm.form.invoiceCode;
                        var currentInvoiceNo=vm.form.invoiceNo;
                        var currentInvoiceAmount=vm.form.invoiceAmount;
                        var currentCheckCode=vm.form.invoiceAmount;
                        if(checkCode!="" && checkCode!=null){
                            currentInvoiceAmount="";
                        }else{
                            currentCheckCode="—— ——";
                        }

                        var data={'uuid':true,
                            'noAuthTip':"无底账信息",
                            'invoiceCode':currentInvoiceCode,
                            'id':currentInvoiceCode+currentInvoiceNo,
                            'invoiceNo':currentInvoiceNo,
                            'invoiceType':getInvoiceType(),
                            'invoiceDate':vm.form.invoiceDate,
                            'invoiceAmount':currentInvoiceAmount,
                            'checkCode':currentCheckCode
                        };
                        if(currentIds.length>0&&contains(currentIds,currentInvoiceCode+currentInvoiceNo)){
                            return alert("请不要重复操作同一发票",function(){
                                vm.reset();
                            });
                        }
                        currentIds.push(currentInvoiceCode+currentInvoiceNo);
                        dataList.push(data);
                        this.tableData=dataList;
                        vm.reset();
                    }

                });

            }

        }
    })
;
function getInvoiceType(){
    var invoice=vm.form.invoiceCode;
    if(invoice!=null && !undefined){
        return getFplx(invoice)
    }else{
        return "";
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
function contains(arr, obj) {
    var i = arr.length;
    while (i--) {
        if (arr[i] === obj) {
            return true;
        }
    }
    return false;
}
function checkValue(entity){
    var date =new Date(entity.invoiceDate);
    var dqskssq=entity.currentTaxPeriod;

    if(dqskssq!=null){
        var year=dqskssq.substring(0,4);
        var month= dqskssq.substring(4);
        dqskssq=new Date(year,month,"00");
    }
    var msg=null;
    if(entity.authStatus!="0" && entity.authStatus!="5"){
        return msg="已在认证处理状态";
    }else if(!entity.taxAccess) {
        return msg="没有税号权限";
    } else if(entity.invoiceStatus=="1"){
        return msg="失控发票";
    }else if (entity.invoiceStatus=="2"){
        return msg="作废发票";
    }else if (entity.invoiceStatus=="3"){
        return msg="红冲发票";
    }else if (entity.invoiceStatus=="4"){
        return msg="异常发票";
    }else if(entity.valid=="0"){
        return msg="无效发票";
    }else if(entity.invoiceAmount<=0){
        return msg="金额小于等于0";
    }else if(entity.taxAmount<0){
        return msg="税额小于0";
    }else if(entity.rzhYesOrNo=="1"){
        return msg="已认证";
    }else if(date<new Date('2017-01-01 00:00:00')){
        return msg="发票已逾期";
    }else if(date>dqskssq && dqskssq!=null){
        return msg="开票日期不大于当前税款所属期";
    }
    return msg;

}
function addDay(dayNumber, date) {
    date = date ? date : new Date();
    var ms = dayNumber * (1000 * 60 * 60 * 24);
    var newDate = new Date(date.getTime() + ms);
    return newDate;
}
function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}

/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceNo = t.value.replace(reg, '');

}
/**
 * 格式化货币 千分号和四舍五入保留两位小数
 * @param cellvalue
 * @param options
 * @param rowObject
 * @returns {*}
 */
function decimal(cellvalue) {
    if(cellvalue!=null){
        var val=Math.round(cellvalue * 100) / 100;
        return val.formatMoney();
    }
    return "";
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