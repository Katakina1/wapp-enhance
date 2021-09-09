
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var defaultRzhBelongDate = null;
var isInitial = true;

var currentQueryParam = {
    gfName: "-1",
    rzhBelongDate: null,
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
        rzhBelongDateOption: {},
        gfs: [],
        form:{
            gfName: null,
            rzhBelongDate: null
        },
        rzhBelongDate: "",
        taxName: "",
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
        },
        showList: true
    },
    mounted:function(){
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
                $('#rzhBelongDate').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#rzhBelongDate').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#rzhBelongDate').siblings('span.el-input__suffix').css('background', 'white');
                $('#rzhBelongDate').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        query: function (formName) {
            isInitial = false;
            $(".norecords").hide();
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'gfName': vm.form.gfName,
                        'rzhBelongDate': vm.form.rzhBelongDate
                    };
                    vm.findAll(1);
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
                                currentQueryParam.rzhBelongDate = ''+r;
                                defaultRzhBelongDate = ''+r;
                            });
                        vm.taxName = gf.label;
                        vm.form.gfName = gf.value;
                        currentQueryParam.gfName = gf.value;
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
        exportPdf: function(){
            var uri = baseURL + 'export/authResultListExport'
                +'?gfName='+currentQueryParam.gfName
                +'&rzhBelongDate='+(currentQueryParam.rzhBelongDate==null?'':currentQueryParam.rzhBelongDate)
                +'&taxName='+vm.taxName
                +'&init='+(isInitial?0:1);
            document.getElementById("ifile").src = encodeURI(uri);
        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'report/authResultList/list',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                $('#totalStatistics').html("合计数量: "+xhr.page.totalCount+"条, 合计金额: "+formatMoney(xhr.totalAmount)+"元, 合计税额: "+formatMoney(xhr.totalTax)+"元");

                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;

                this.tableData = xhr.page.list;
                this.listLoading = false;

                vm.rzhBelongDate = vm.form.rzhBelongDate;

                for(var i=0;i<vm.gfs.length;i++){
                    if(vm.form.gfName==vm.gfs[i].value){
                        vm.taxName = vm.gfs[i].label;
                    }
                }

                if(this.tableData == null || this.tableData.length == 0){
                    $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
                }else{
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                }

            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.findAll(1);
            }
        },
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
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
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}