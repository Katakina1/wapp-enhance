
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    data: {
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        tableData: [],
        pageCount: 0,
        multipleSelection: [],
        options: [],
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
            gxDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            gxDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceNo: null,
        },
        gxStartDateOptions: {},
        gxEndDateOptions: {},
    },
    mounted: function () {
        this.querySearchGf();
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength","50");
        this.queryGf();
        this.gxStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.gxEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };

    },
    methods: {
        querySearchGf: function () {
            $.get(baseURL + 'report/invoiceProcessingStatusReport/searchGf',function(r){
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].value+"("+r.optionList[i].label+")";
                    gfs.push(gf);
                }
                vm.gfs = gfs;
            });
        },
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
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==4){
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
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        invoiceAmountFormatDecimal:function(row) {

            return decimal(row.invoiceAmount);
        } ,
        taxAmountFormatDecimal:function(row) {

            return decimal(row.taxAmount);
        } ,
        query: function () {
            $(".checkMsg").remove();
            var checkKPDate = true;
            var checkSwitchStatus = true;
            var qsStartDate = new Date(vm.form.gxDate1);
            var qsEndDate = new Date(vm.form.gxDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            if ( (qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.gxDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if(!checkKPDate){
                return;
            }
            //查询开关是否开启。
            $.get(baseURL + 'certification/intelligentizeCheck/selectSwitchStatus',function(r){
                if(r.code=="0" && r.switchStatus=='0'){
                    checkSwitchStatus =true;
                    vm.findAll();
                }else{
                    checkSwitchStatus=false;
                    alert("控制开关处于关闭状态！");
                    return;
                }

            });

        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        cancel: function() {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要撤销勾选的数据!");
                return;
            }
            var allIds = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                allIds.push(this.multipleSelection[i].id)
            }
            var ids=allIds.join(",");


            confirm('确定要撤销选中的记录？', function(){
                $.ajax({
                    type: "GET",
                    url: baseURL + "certification/submitCheck/cancel",
                    contentType: "application/json",
                    data: {ids:ids},
                    success: function(r){
                        if(r){
                            alert('撤销成功', function(){
                                vm.findAll(1);
                            });
                        }else{
                            alert('撤销失败');
                        }
                    }
                });
            });
        },
        submit: function() {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要提交认证的数据!");
                return;
            }
            var allIds = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                allIds.push(this.multipleSelection[i].id)
            }
            var ids=allIds.join(",");


            confirm('确定要提交认证选中的记录？', function(){
                $.ajax({
                    type: "GET",
                    url: baseURL + "certification/submitCheck/submit",
                    contentType: "application/json",
                    data: {ids:ids},
                    success: function(r){
                        if(r.code!=undefined && r.code!=null && r.code == 500) {
                            alert('提交认证失败');
                            return;
                        }
                        if(r){
                            alert('提交认证成功', function(){
                                vm.findAll(1);
                            });
                        }else{
                            alert('提交认证失败');
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
            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                gfName: this.form.gfTaxNo,
                xfName: this.form.xfName,
                invoiceNo: this.form.invoiceNo,
                gxDate1: this.form.gxDate1,
                gxDate2: this.form.gxDate2,
                invoiceType: this.form.invoiceType
            };
            vm.listLoading = true;
            var flag = false;
            this.$http.post(baseURL  + 'certification/submitCheck/list',
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
            /*$.post(baseURL  + 'certification/submitCheck/list', params, function (r) {
                vm.total = r.page.totalCount;
                vm.currentPage = r.page.currPage;
                vm.pageCount = r.page.totalPage;
                vm.tableData = r.page.list;
                vm.listLoading = false;
            });*/

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
        gxDate1Change: function(val) {
            vm.form.gxDate1 = val;
        },
        gxDate2Change: function(val) {
            vm.form.gxDate2 = val;
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '';
            }
        },
        formatGxDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.gxDate);
            } else {
                return '';
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

function verificationXfName(t) {
    var reg=vm.form.xfName;
    if(reg!=null &&reg.length>30){
        reg=reg.substring(0,30);
    }
    vm.form.xfName=reg;

}

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}