
$(function () {
    vm.load();
});

var defaultRzhBelongDate = null;

var vm = new Vue({
    el:'#rrapp',
    data:{
        rzhBelongDate: "",
        taxName: "",
        totalAmount: null,
        totalTax: null,
        totalOutTax: null,
        outTax1: '',
        outTax2: '',
        outTax3: '',
        outTax4: '',
        outTax5: '',
        outTax6: '',
        outTax7: '',
        outTax8: '',
        outTax0: '',
        rzhBelongDateOption: {},
        gfs: [],
        form:{
            gfName: null,
            rzhBelongDate: null
        },
        rules:{
            rzhBelongDate:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        if (value>defaultRzhBelongDate) {
                            callback(new Error('不能超过当前税款所属期'))
                        } else {
                            callback();
                        }
                    }else{
                        callback(new Error('税款所属期不能为空'));
                    }
                }, trigger: 'change'
            }]
        }
    },
    mounted: function(){
        this.querySearchGf();
        $("#gf-select").attr("maxlength","50");
        this.rzhBelongDateOption = {
            disabledDate: function(time) {
                var currentTime = new Date(defaultRzhBelongDate.substring(0,4),parseInt(defaultRzhBelongDate.substring(4))-1,1,0,0,0);
                return time > currentTime;
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
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        query: function (formName) {
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.load();
                } else {
                    return false;
                }
            });
        },
        querySearchGf: function () {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchGf',function(r){
                var gfs = [];
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                    if(i==0){
                        //获取默认税号的当前税款所属期
                        $.get(baseURL + 'report/invoiceAuthDailyReport/currentTaxPeriod',
                            {taxNo: gf.value},
                            function(r){
                                vm.form.rzhBelongDate = ''+r;
                                defaultRzhBelongDate = ''+r;
                            });
                        vm.taxName = gf.label;
                        vm.form.gfName = gf.value;
                    }
                }
                vm.gfs = gfs;
            });
        },
        dqskssqDateChange: function(val) {
            vm.form.rzhBelongDate = val;
        },
        gfNameChange: function(value) {
            $.get(baseURL + 'report/invoiceAuthDailyReport/currentTaxPeriod',
                {taxNo: value},
                function(r){
                    vm.form.rzhBelongDate = ''+r;
                    defaultRzhBelongDate = ''+r;
                })
        },
        exportExcel: function(){
            var uri = baseURL + 'export/inputTaxReportExport'
                +'?totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax
                +'&totalOutTax='+vm.totalOutTax
                +'&outTax1='+vm.outTax1
                +'&outTax2='+vm.outTax2
                +'&outTax3='+vm.outTax3
                +'&outTax4='+vm.outTax4
                +'&outTax5='+vm.outTax5
                +'&outTax6='+vm.outTax6
                +'&outTax7='+vm.outTax7
                +'&outTax8='+vm.outTax8
                +'&outTax0='+vm.outTax0
                +'&taxName='+vm.taxName
                +'&rzhBelongDate='+vm.rzhBelongDate;
            document.getElementById("ifile").src = encodeURI(uri);
        },
        load: function () {
            $.get(baseURL + 'report/inputTaxReport/index',
                {gfName:vm.form.gfName, rzhBelongDate:vm.form.rzhBelongDate},
                function(r){

                    if(r.code==401){
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }

                    vm.totalAmount = formatMoney(r.totalAmount);
                    vm.totalTax = formatMoney(r.totalTax);
                    vm.totalOutTax = formatMoney(r.totalOutTax);
                    for(var i=0;i<r.outTaxDetailList.length;i++){
                        if(r.outTaxDetailList[i].rzhDate=="1"){
                            vm.outTax1 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="2"){
                            vm.outTax2 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="3"){
                            vm.outTax3 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="4"){
                            vm.outTax4 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="5"){
                            vm.outTax5 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="6"){
                            vm.outTax6 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="7"){
                            vm.outTax7 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate=="8"){
                            vm.outTax8 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                        if(r.outTaxDetailList[i].rzhDate==null){
                            vm.outTax0 = formatMoney(r.outTaxDetailList[i].tax);
                        }
                    }

                    vm.form.gfName = r.gfName;
                    vm.form.rzhBelongDate = r.rzhBelongDate;
                    vm.rzhBelongDate = r.rzhBelongDate;

                    for(var i=0;i<vm.gfs.length;i++){
                        if(vm.form.gfName==vm.gfs[i].value){
                            vm.taxName = vm.gfs[i].label;
                        }
                    }
                    //不为空说明是第一次进页面,后台将此值带入
                    if(r.defaultRzhBelongDate != null) {
                        defaultRzhBelongDate = r.defaultRzhBelongDate;
                    }

                    if(r.totalAmount==0 && r.totalTax==0 && r.totalOutTax==0){
                        $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
                    }else {
                        $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                    }
            });
        }
    }
});

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}