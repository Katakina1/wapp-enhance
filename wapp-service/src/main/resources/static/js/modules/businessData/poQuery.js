
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var currentQueryParam = {
    pocode: '',
    invoiceNo:'',
    receiptId:'',
    poDateStart:'',
    poDateEnd:'',
    hoststatus:'-1',
    dxhyMatchStatus:'-1',
    page: 1,
    limit: 1
};
var vm = new Vue({
    el:'#rrapp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        listLoading: false,
        poEndDateOptions:{},
        poStartDateOptions:{},
        poquery:{
            hoststatus:'-1',
            pocode:null,
            invoiceNo:'',
            receiptId:'',
            dxhyMatchStatus:'-1',
            poDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            poDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
        },
        rules:{
            invoiceNo:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{1,8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为不超过8位的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            invoiceDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.poStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.poEndDateOptions = {
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
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },

        handleSizeChange: function (val) {
            this.pageSize = val;

                this.findAll(1);

        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        query: function (formName) {
        	// console.log(vm.poquery.hoststatus)
        	// if(vm.poquery.pocode!=null&&vm.poquery.pocode==""&&vm.poquery.pocode.length>15){
        	// 	alert("po号不能超过15个字符")
        	// 	return
        	// }
        	  $(".checkMsg").remove();
        	var checkDate=true
            this.$refs[formName].validate(function (valid) {
            var dateStart=new Date(vm.poquery.poDateStart)
            var dateEnd=new Date(vm.poquery.poDateEnd)
              if (dateStart.getTime() + 1000 * 60 * 60 * 24 * 31*6 < dateEnd.getTime()) {
                            $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过半年</div>');
                            checkDate = false;
               } else if (dateEnd.getTime() < dateStart.getTime()) {
                            $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                            checkDate = false;
              }
              if (!(checkDate)) {
                            return false;
              }
                if (valid) {
                    currentQueryParam = {
                        hoststatus:vm.poquery.hoststatus,
                        pocode: vm.poquery.pocode,
                        invoiceNo:vm.poquery.invoiceNo,
                        receiptId:vm.poquery.receiptId,
                        poDateStart:vm.poquery.poDateStart,
                        poDateEnd:vm.poquery.poDateEnd,
                        dxhyMatchStatus:vm.poquery.dxhyMatchStatus,
                    };
                    vm.currentPage=1;
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },
        exportData: function(){

            // document.getElementById("ifile").src = baseURL + 'export/poQueryExport'
            //     +'?'+$.param(currentQueryParam);

            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':9,'condition':JSON.stringify(currentQueryParam)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
        },
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'modules/businessData/po/query',
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
                this.listLoading = false;


            });
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
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        dueDateFormat: function (row) {
            if (row.receiptdate != null) {
                return formaterDate(row.receiptdate);
            } else {
                return '—— ——';
            }
        },
        formaterDueDate: function(cellvalue) {
            if (cellvalue == null) {
                return '—— ——';
            }
            return cellvalue.substring(0, 10);
        },
        claimAmountFormat: function (row) {
            return decimal(row.claimAmount);
        },
        poDate1Change: function (val) {
            vm.poquery.poDateStart = val;
        },
        poDate2Change: function (val) {
            vm.poquery.poDateEnd = val;
        },
    }



});

function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
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
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
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
function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}
function verificationPoCodeValue(t) {
    var reg = /[^\d]/g;
    vm.poquery.pocode = t.value.replace(reg, '');
};
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