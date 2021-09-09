
$(function () {
    vm.load();
});

var defaultRzhBelongDate = null;

var vm = new Vue({
    el:'#rrapp',
    data:{
        rzhBelongDate: "",
        taxName: "",
        rzhBelongDateOption: {},
        rate: {
            totalAmount: 0.0,
            totalTax: 0.0,
            amount17: null,
            tax17: null,
            amount16: null,
            tax16: null,
            amount13: null,
            tax13: null,
            amount11: null,
            tax11: null,
            amount10: null,
            tax10: null,
            amount6: null,
            tax6: null,
            amount5: null,
            tax5: null,
            amount3: null,
            tax3: null
        },
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
            var uri = baseURL + 'export/inputTaxDetailReportExport'
                +'?amount17='+(vm.rate.amount17==null?'':vm.rate.amount17)
                +'&tax17='+(vm.rate.tax17==null?'':vm.rate.tax17)
                +'&amount13='+(vm.rate.amount13==null?'':vm.rate.amount13)
                +'&tax13='+(vm.rate.tax13==null?'':vm.rate.tax13)
                +'&amount11='+(vm.rate.amount11==null?'':vm.rate.amount11)
                +'&tax11='+(vm.rate.tax11==null?'':vm.rate.tax11)
                +'&amount6='+(vm.rate.amount6==null?'':vm.rate.amount6)
                +'&tax6='+(vm.rate.tax6==null?'':vm.rate.tax6)
                +'&amount5='+(vm.rate.amount5==null?'':vm.rate.amount5)
                +'&tax5='+(vm.rate.tax5==null?'':vm.rate.tax5)
                +'&amount3='+(vm.rate.amount3==null?'':vm.rate.amount3)
                +'&tax3='+(vm.rate.tax3==null?'':vm.rate.tax3)
                +'&totalAmount='+(vm.rate.totalAmount==null?'':vm.rate.totalAmount)
                +'&totalTax='+(vm.rate.totalTax==null?'':vm.rate.totalTax)
                +'&taxName='+vm.taxName
                +'&rzhBelongDate='+vm.rzhBelongDate;
            document.getElementById("ifile").src = encodeURI(uri);
        },
        load: function () {
            vm.rate.amount17=null;
            vm.rate.tax17=null;
            vm.rate.amount16=null;
            vm.rate.tax16=null;
            vm.rate.amount13=null;
            vm.rate.tax13=null;
            vm.rate.amount11=null;
            vm.rate.tax11=null;
            vm.rate.amount10=null;
            vm.rate.tax10=null;
            vm.rate.amount6=null;
            vm.rate.tax6=null;
            vm.rate.amount5=null;
            vm.rate.tax5=null;
            vm.rate.amount3=null;
            vm.rate.tax3=null;

            var totalAmount = 0;
            var totalTax = 0;

            $.get(baseURL + 'report/inputTaxDetailReport/index',
                {gfName:vm.form.gfName, rzhBelongDate:vm.form.rzhBelongDate},
                function(r){

                    if(r.code==401){
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
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

                    if(r.noneDetailCount>0){
                        $('#noDetailTip').html("发现有"+r.noneDetailCount+"张未获取到相关明细的发票将会影响数据准确性,请先再\"未获取明细的发票\"里面完善数据!")
                    }else{
                        $('#noDetailTip').html("");
                    }

                    for(var i=0;i<r.rateList.length;i++){
                        var rateData = r.rateList[i];
                        if(rateData.rate == 17.0){
                            vm.rate.amount17 = rateData.amount;
                            vm.rate.tax17 = rateData.tax;
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 16.0){
                            vm.rate.amount16 = rateData.amount;
                            vm.rate.tax16 = rateData.tax;
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 13.0){
                            vm.rate.amount13 = formatMoney(rateData.amount);
                            vm.rate.tax13 = formatMoney(rateData.tax);
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 11.0){
                            vm.rate.amount11 = rateData.amount;
                            vm.rate.tax11 = rateData.tax;
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 10.0){
                            vm.rate.amount10 = rateData.amount;
                            vm.rate.tax10 = rateData.tax;
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 6.0){
                            vm.rate.amount6 = formatMoney(rateData.amount);
                            vm.rate.tax6 = formatMoney(rateData.tax);
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 5.0){
                            vm.rate.amount5 = formatMoney(rateData.amount);
                            vm.rate.tax5 = formatMoney(rateData.tax);
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                        if(rateData.rate == 3.0){
                            vm.rate.amount3 = formatMoney(rateData.amount);
                            vm.rate.tax3 = formatMoney(rateData.tax);
                            if(rateData.amount!=null){
                                totalAmount += rateData.amount;
                            }if(rateData.tax!=null){
                                totalTax += rateData.tax;
                            }
                        }
                    }
                    vm.rate.amount17 = formatMoney((vm.rate.amount17==null?0:vm.rate.amount17)+(vm.rate.amount16==null?0:vm.rate.amount16));
                    vm.rate.tax17 = formatMoney((vm.rate.tax17==null?0:vm.rate.tax17)+(vm.rate.tax16==null?0:vm.rate.tax16));
                    vm.rate.amount11 = formatMoney((vm.rate.amount11==null?0:vm.rate.amount11)+(vm.rate.amount10==null?0:vm.rate.amount10));
                    vm.rate.tax11 = formatMoney((vm.rate.tax11==null?0:vm.rate.tax11)+(vm.rate.tax10==null?0:vm.rate.tax10));
                    if(vm.rate.amount17==0){
                        vm.rate.amount17=null;
                    }
                    if(vm.rate.tax17==0){
                        vm.rate.tax17=null;
                    }
                    if(vm.rate.amount11==0){
                        vm.rate.amount11=null;
                    }
                    if(vm.rate.tax11==0){
                        vm.rate.tax11=null;
                    }
                    if(vm.rate.amount17==null && vm.rate.tax17==null
                        && vm.rate.amount13==null && vm.rate.tax13==null
                        && vm.rate.amount11==null && vm.rate.tax11==null
                        && vm.rate.amount6==null && vm.rate.tax6==null
                        && vm.rate.amount5==null && vm.rate.tax5==null
                        && vm.rate.amount3==null && vm.rate.tax3==null){
                        $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
                    }else {
                        $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                    }

                    vm.rate.totalAmount = totalAmount==0?null:formatMoney(totalAmount);
                    vm.rate.totalTax = totalTax==0?null:formatMoney(totalTax);
                });
        }
    }
});

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}