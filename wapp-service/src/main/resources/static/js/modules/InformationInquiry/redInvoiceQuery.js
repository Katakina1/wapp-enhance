
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var allDataList = [];
var currentQueryParam = {
    returnGoodsDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    returnGoodsDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    invoiceOrAgreementNo:'',
    redInvoiceNo:'',
    redType:'-1',
    page: 1,
    limit: 1,
    multipleSelection: []
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        selectFileFlag: '',
        id:'',
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        tempTableData: [],

        total1: 0,
        currentPage1: 1,
        totalPage1: 0,
        pageSize1: PAGE_PARENT.PAGE_SIZE,
        pageList1: PAGE_PARENT.PAGE_LIST,
        pagerCount1: 5,
        tableData1: [],

        returnGoodsDateOptions1: {},
        returnGoodsDateOptions2: {},
        xfMaxlength: 30,

        form:{
            returnGoodsDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            returnGoodsDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceOrAgreementNo:'',
            redInvoiceNo:'',
            redType:'-1'
        },
        detailEntityList: [],
        tempDetailEntityList: [],
        multipleSelection: [],
        tempValue: null,
        //下面是对应模态框隐藏的属性
        detailDialogRedNotice :false,
        enterRedTicketDialog:false,
        detailDialogVisible:false,
        enterPaymentDialog:false,
        scarletLetterDialog: false,
        uploadLetterDialog:false,
        listLoading: false,
        rules:{
            venderId:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{0,6}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为6位内的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],

            returnGoodsDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            returnGoodsDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.returnGoodsDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.returnGoodsDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.returnGoodsDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.returnGoodsDate1));
                return time.getTime() >= Date.now();
            }
        };
    },
    methods: {

        exportExcel: function(){
            $("#export_btn").attr("disabled","true").addClass("is-disabled");
            var params ={
                'invoiceOrAgreementNo':(currentQueryParam.invoiceOrAgreementNo==null?'':currentQueryParam.invoiceOrAgreementNo),
                'redInvoiceNo':currentQueryParam.redInvoiceNo,
                'redType':currentQueryParam.redType,
                'returnGoodsDate1':currentQueryParam.returnGoodsDate1,
                'returnGoodsDate2':currentQueryParam.returnGoodsDate2
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':59,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.pge.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });




        },
        exportExcel1: function(){
            $("#export_btn2").attr("disabaled","true").addClass("is-disabled");


            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceExport1'
                // +'?&supplierAssociation='+(currentQueryParam.supplierAssociation==null?'':currentQueryParam.supplierAssociation)
                // +'&returnGoodsCode='+(currentQueryParam.returnGoodsCode==null?'':currentQueryParam.returnGoodsCode)
                // +'&returnGoodsDate1='+currentQueryParam.returnGoodsDate1
                // +'&returnGoodsDate2='+currentQueryParam.returnGoodsDate2
                // +'&page='+currentQueryParam.page
                // +'&limit='+currentQueryParam.limit
            );
            setTimeout(function(){$("#export_btn2").removeAttr("disabled").removeClass("is-disabled")},5000);
        },

        exportData:function() {
            $("#export_btn3").attr("disabled","true").addClass("is-disabled");
            document.getElementById("ifile").src = baseURL + "export/redInvoiceUploadExport";
            setTimeout(function(){$("#export_btn3").removeAttr("disabled").removeClass("is-disabled")},5000);
        },



        showSelectFileWin:function() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==5){
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==7){
                $('#datevalue7').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==5){
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==7){
                $('#datevalue7').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        query: function (formName) {
            isInitial = false;
            $(".checkMsg").remove();
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'returnGoodsDate1': vm.form.returnGoodsDate1,
                        'returnGoodsDate2': vm.form.returnGoodsDate2,
                        'invoiceOrAgreementNo':vm.form.invoiceOrAgreementNo,
                        'redInvoiceNo':vm.form.redInvoiceNo,
                        'redType':vm.form.redType
                    };
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },
        detailFormCance: function () {
            vm.uploadLetterDialog = false;
            vm.detailDialogFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.remark = null;
        },
        detailFormCancel: function () {
            vm.scarletLetterDialog = false;
            vm.detailDialogVisible= false;
            vm.detailDialogFormVisible = false;
            vm.detailDialogRedNotice = false;
            vm.detailForm.gfName = null;
            vm.detailForm.remark = null;

        },

        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        showCheckInner: function () {
            vm.detailDialogCheckFormInnerVisible = true;
        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        currentChange1: function(currentPage1){
            if(vm.total1==0){
                return;
            }
            this.findAll1(currentPage1);
        },
        currentChange2: function(currentPage2){
            if(vm.total2==0){
                return;
            }
            this.findAll2(currentPage2);
        },
        findAll: function (currentPage) {
            $('.toolbar').show();
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'export/queryList',
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

                this.totalAmount = formatMoney(xhr.totalAmount);
                this.totalTax = formatMoney(xhr.totalTax);

                this.tableData = xhr.page.list;
                this.listLoading = false;

                flag = true;
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
            this.pageSize = val;
            if(!isInitial) {
                this.findAll(1);
            }
        },
        handleSizeChange1: function (val) {
            this.pageSize1 = val;
            if(!isInitial) {
                this.findAll1(1);
            }
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
            if(!isInitial) {
                this.findAll2(1);
            }
        },
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        taxRateFormat:function(row, column, cellValue, index) {
            if (cellValue.indexOf("%") != -1) {
                var percent = cellValue;
                var str = percent.replace("%", "");
                str = str / 100;
                return str;
            }else{
                return cellValue;
            }
        },
        numberFormat: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
                return "";
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
    }
});

function loadNumber(event) {
    var el = event.currentTarget;
    var elValue = el.value;
    var reg = /^((?!0)\d{1,2}|100)$/;
    if (!elValue.match(reg)) {
        elValue = "";
        console.log("b")
        alert('税率请输入1-100的整数');
        rules.msg()
        return false;
    } else {
        return true;
    }
}

function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
}

function detailDateFormat(value) {
    value = value.replace("-", "/");
    value = value.replace("-", "/");
    var tempCreateDate = new Date(value);
    var tempYear = tempCreateDate.getFullYear() + "年";
    var tempMonth = tempCreateDate.getMonth() + 1;
    var tempDay = tempCreateDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}

function venderIdFormat(value) {
    var v=value;
    while(v.length<6){v='0'+v;}
    value=v;
    return value;
}

function paymentAndpurchaseInvoiceNoFormat(value) {
    var v=value;
    while(v.length<8){v='0'+v;}
    value=v;
    return value;
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