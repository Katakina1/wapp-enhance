
var defaultRzhBelongDate = ''+new Date().getFullYear();
var currentQueryParam = {
    gfName: null,
    rzhBelongDate: defaultRzhBelongDate
};

$(function () {

    var w = $('#jqGraph').width();

    $("#jqGrid").jqGrid({
        url: baseURL + 'report/invoiceAuthMonthlyReport/list',
        datatype: "json",
        postData: {
            'gfName': vm.form.gfName,
            'rzhBelongDate': vm.form.rzhBelongDate,
        },
        colModel: [
            { label: '税款所属期', name: 'rzhDate',sortable:false, align: 'center'},
            { label: '发票数量', name: 'count',sortable:false},
            { label: '合计金额', name: 'amount',sortable:false, align: 'right', formatter : formatMoney},
            { label: '合计税额', name: 'tax',sortable:false, align: 'right', formatter : formatMoney}
        ],
        viewrecords: true,
        height: 'auto',
        width: w,
        autoScroll: true,
        multiselect: false,
        jsonReader : {
            root: "reportList"
        },
        loadComplete: function(xhr){
            if(xhr.code==401){
                alert("登录超时，请重新登录", function () {
                    parent.location.href = baseURL + 'login.html';
                });
            }
            var totalCount = 0;
            var totalAmount = 0;
            var totalTax = 0;
            for(var i=0;i<xhr.reportList.length;i++){
                totalCount += xhr.reportList[i].count;
                totalAmount += xhr.reportList[i].amount*10000;
                totalTax += xhr.reportList[i].tax*10000;
            }
            totalAmount = totalAmount/10000;
            totalTax = totalTax/10000;
            $('#totalStatistics').html("合计数量: "+totalCount+"条, 合计金额: "+formatMoney(totalAmount)+"元, 合计税额: "+formatMoney(totalTax)+"元");

            vm.totalAmount = formatMoney(totalAmount);
            vm.totalTax = formatMoney(totalTax);
            vm.totalCount = totalCount;

            //纳税人名称
            var taxName = null;

            for(var i=0;i<vm.gfs.length;i++){
                if(vm.form.gfName==vm.gfs[i].value){
                    taxName = vm.gfs[i].label;
                }
            }

            $('#lab-tax-name').html("纳税人名称: "+taxName);
            $('#lab-tax-no').html("纳税人识别号: "+xhr.gfName);

            vm.form.gfName = xhr.gfName;
            currentQueryParam.gfName = xhr.gfName;

            createAmountGraph(xhr.reportList);
            createCountGraph(xhr.reportList);

            $("#jqGrid").find("td").removeAttr("title");
        }
    }).closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
});


var vm = new Vue({
    el:'#rrapp',
    data:{
        rzhBelongDateOption: {},
        totalAmount: 0,
        totalTax: 0,
        totalCount: 0,
        gfs: [],
        form:{
            gfName: null,
            rzhBelongDate: defaultRzhBelongDate
        },
        rules:{
            rzhBelongDate:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        if (value>defaultRzhBelongDate) {
                            callback(new Error('认证年份不能超过当年'))
                        } else {
                            callback();
                        }
                    }else{
                        callback(new Error('认证年份不能为空'));
                    }
                }, trigger: 'change'
            }]
        },
        showList: true
    },
    mounted: function(){
        this.querySearchGf();
        $("#gf-select").attr("maxlength","50");
        this.rzhBelongDateOption = {
            disabledDate: function(time) {
                var currentTime = new Date(defaultRzhBelongDate,1,1,0,0,0);
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
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.reload();
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
            vm.form.rzhBelongDate = defaultRzhBelongDate;
        },
        showTable: function() {
            $('.defaultItem').removeClass('hideItem');
            $('#jqGraph').addClass('hideItem');
            $('#showTableId').addClass('bg-color');
            $('#showGraphId').removeClass('bg-color');
        },
        showGraph: function() {
            $('.defaultItem').addClass('hideItem');
            $('#jqGraph').removeClass('hideItem');
            $('#showTableId').removeClass('bg-color');
            $('#showGraphId').addClass('bg-color');
        },
        showAmountGraph: function() {
            $('#amountGraph').removeClass('hideItem');
            $('#countGraph').addClass('hideItem');
            $('#showAmountGraphId').addClass('bg-color');
            $('#showCountGraphId').removeClass('bg-color');
        },
        showCountGraph: function() {
            $('#countGraph').removeClass('hideItem');
            $('#amountGraph').addClass('hideItem');
            $('#showCountGraphId').addClass('bg-color');
            $('#showAmountGraphId').removeClass('bg-color');
        },
        exportExcel: function(){
            document.getElementById("ifile").src = baseURL + 'export/invoiceAuthMonthlyReportExport'
                +'?gfName='+currentQueryParam.gfName
                +'&rzhBelongDate='+currentQueryParam.rzhBelongDate
                +'&taxName='+$('#lab-tax-name').html()
                +'&taxNo='+$('#lab-tax-no').html()
                +'&totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax
                +'&totalCount='+vm.totalCount;
        },
        reload: function () {
            currentQueryParam = {
                'gfName': vm.form.gfName,
                'rzhBelongDate': vm.form.rzhBelongDate
            };
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:currentQueryParam,
                page:page
            }).trigger("reloadGrid");
        }
    }
});


function createAmountGraph(reportList){

    var labels = [];
    var series = [];
    var data2 = [];//合计金额
    var data3 = [];//合计税额
    for(var i=0;i<reportList.length;i++){
        labels[i] = reportList[i].rzhDate;
        data2[i] = reportList[i].amount;
        data3[i] = reportList[i].tax;
    }
    series[0] = {name:'合计金额', data: data2};
    series[1] = {name:'合计税额', data: data3};

    $('#amountGraph').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: labels,
            title:{
                text:'税款所属期',
                align:'high'
            },
            crosshair: true
        },
        yAxis: {
            title: {
                text: '金额、税额（元）',
                align:'high'
            }
        },
        credits: {
            enabled: false//右下角的版权信息不显示
        },
        // exporting: {
        //     enabled:false
        // },
        tooltip: {
            headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.2f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                borderWidth: 0
            }
        },
        series: series
    });
}

function createCountGraph(reportList){

    var labels = [];
    var series = [];
    var data1 = [];//发票数量
    for(var i=0;i<reportList.length;i++){
        labels[i] = reportList[i].rzhDate;
        data1[i] = reportList[i].count;
    }
    series[0] = {name:'发票数量', data: data1};

    $('#countGraph').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: labels,
            title:{
                text:'税款所属期',
                align:'high'
            },
            crosshair: true
        },
        yAxis: {
            title: {
                text: '发票数量（份）',
                align:'high'
            }
        },
        credits: {
            enabled: false//右下角的版权信息不显示
        },
        // exporting: {
        //     enabled:false
        // },
        tooltip: {
            headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            column: {
                borderWidth: 0
            }
        },
        series: series
    });
}

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}