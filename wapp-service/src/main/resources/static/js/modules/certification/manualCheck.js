
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    data: {
        tableData: [],
        multipleSelection: [],
        options: [],
        pageCount: 0,
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        listLoading: false,
        form:{
            gfNames: [],
            gfName: null,
            gfTaxNo: "-1",
            xfName: null,
            invoiceType: "-1",
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth())+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceNo: null,
            qsStatus: "-1",
            qsType: "-1",
            qsDate1: null,
            qsDate2: null
        },
        limitDate: {},
    },
    mounted: function () {
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength","50");
        this.queryGf();
        this.limitDate = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();

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
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            } else if(val == 2){
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            }else if(val == 3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val == 4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            } else if(val == 2){
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('#appForm>form>.el-row:first-child .el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            }else if(val == 3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val == 4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        qsChange: function (value) {
            if(value=="1"){
                $('.qsItem').removeClass("hideItem");
                $('.defaultShow').addClass("hideItem");

                vm.form.qsDate1 = new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01";
                vm.form.qsDate2 = new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());
            }else{
                $('.qsItem').addClass("hideItem");
                $('.defaultShow').removeClass("hideItem");
                vm.form.qsType = "-1";
                vm.form.qsDate1 = null;
                vm.form.qsDate2 = null;
            }
        },
        invoiceAmountFormatDecimal:function(row) {

            return decimal(row.invoiceAmount);
        } ,
        taxAmountFormatDecimal:function(row) {

            return decimal(row.taxAmount);
        } ,

        query: function () {
            var checkInvoiceDate=true;
            var checkqsDate=true;
            $(".checkMsg").remove();
            var invoiceStartDate = new Date(vm.form.invoiceDate1);
            var invoiceEndDate = new Date(vm.form.invoiceDate2);
            invoiceStartDate.setMonth(invoiceStartDate.getMonth() + 12);
            var qsStartDate = new Date(vm.form.qsDate1);
            var qsEndDate = new Date(vm.form.qsDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            if ( (invoiceEndDate.getTime() + + 1000*60*60*24) > invoiceStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkInvoiceDate=false;
            } else if(invoiceEndDate.getTime() < new Date(vm.form.invoiceDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkInvoiceDate=false;
            }
            if ((qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkqsDate=false;
            } else if(qsEndDate.getTime() < new Date(vm.form.qsDate1)){
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkqsDate=false;
            }

            if(!(checkInvoiceDate && checkqsDate)){
                return;
            }
            this.findAll();
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        check: function() {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要勾选的数据!");
                return;
            }
            var allIds = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                allIds.push(this.multipleSelection[i].id)
            }
            var ids=allIds.join(",");

            confirm('确定要勾选选中的记录？', function(){
                $.ajax({
                    type: "GET",
                    url: baseURL + "certification/manualCheck/submit",
                    contentType: "application/json",
                    data: {ids:ids},
                    success: function(r){
                        if(r.code!=undefined && r.code!=null && r.code == 500) {
                            alert('提交勾选失败');
                            return;
                        }
                        if(r){
                            alert('提交勾选成功', function(){
                                vm.findAll();
                            });
                        }else{
                            alert('提交勾选失败');
                        }
                    }
                });
            });
        },
        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){
                this.findAll();
            }
        },
        findAll: function () {
            var flag = false;
            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                gfName: this.form.gfTaxNo,
                xfName: this.form.xfName,
                invoiceNo: this.form.invoiceNo,
                invoiceDate1: this.form.invoiceDate1,
                invoiceDate2: this.form.invoiceDate2,
                invoiceType: this.form.invoiceType,
                qsStatus: this.form.qsStatus,
                qsType: this.form.qsType,
                qsDate1: this.form.qsDate1,
                qsDate2: this.form.qsDate2
            };
            vm.listLoading = true;


            this.$http.post(baseURL  + 'certification/manualCheck/list',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                $('#totalStatistics').html("合计数量: "+xhr.page.totalCount+"条, 合计金额: "+formatMoney(xhr.totalAmount)+"元, 合计税额: "+formatMoney(xhr.totalTax)+"元");
                flag = true;
                vm.total = xhr.page.totalCount;
                vm.currentPage = xhr.page.currPage;
                vm.pageCount = xhr.page.totalPage;
                vm.tableData = xhr.page.list;
                vm.listLoading = false;
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
        queryGf: function () {
            $.get(baseURL + 'transferOut/detailQuery/gfNameAndTaxNo', function (r) {
                for (var i = 0; i < r.gfNameList.length; i++) {
                    vm.form.gfNames.push({name: r.gfNameList[i], taxNo: r.gfTaxNoList[i]});
                }

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
        invoiceDate1Change: function(val) {
            vm.form.invoiceDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form.invoiceDate2 = val;
        },
        qsDate1Change: function(val) {
            vm.form.qsDate1 = val;
        },
        qsDate2Change: function(val) {
            vm.form.qsDate2 = val;
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        formatQsDate: function (row, column) {
            if (row.qsDate != null) {
                return dateFormat(row.qsDate);
            } else {
                return '—— ——';
            }
        },
        formatQsType: function (row, column) {
            if (row.qsType != null) {
                return row.qsType;
            } else {
                return '—— ——';
            }
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});
function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
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

function verificationXfName(t) {
    var reg=vm.form.xfName;
    if(reg!=null &&reg.length>30){
        reg=reg.substring(0,30);
    }
    vm.form.xfName=reg;

}
function verificationGfName(t) {
    var reg=vm.form.gfName;
    console.log(reg);
    if(reg.length>5){
        reg=reg.substring(0,5);
    }
    vm.form.gfName=reg;

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

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}