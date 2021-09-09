
var chart = 0;
var postData1 = {};
$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'report/invoiceauthenticationsummarystatistics/query',
        datatype: "json",
        mtype: 'GET',
        postData: {
            'dqskssq': new Date().getFullYear()
        },
        colModel: [
            {label: '当前税款所属期', name: 'dqskssq', align: 'center', sortable: false,},
            {label: '发票数量', name: 'invoiceCount', align: 'center', sortable: false,},
            {
                label: '合计金额', name: 'totalAmount', align: 'right', sortable: false,
                formatter: function (value, options, row) {
                    if (value != null) {
                        return formateAmount(value);
                    } else {
                        return "—— ——"
                    }

                }
            },
            {
                label: '合计税额', name: 'taxAmount', align: 'right', sortable: false,
                formatter: function (value, options, row) {
                    if (value != null) {
                        return formateAmount(value);
                    } else {
                        return "—— ——"
                    }

                }
            },
            {
                label: ' ', name: 'dqskssq', align: 'center', sortable: false,
                formatter: function (value, options, row) {
                    if (value < new Date().getFullYear() + "" + formatDate(new Date().getMonth() + 1)) {
                        return "已过税款所属期"

                    } else {
                        return "当前所属期"
                    }
                }
            }
        ],
        height: 'auto',
        //shrinkToFit:false,
        viewrecords: true,
        rowNum: 12,
        width: $('#container').width(),
        autoScroll: true,
        multiselect: false,
        jsonReader: {
            root: "page.list",
            //page: "page.currPage",
            //total: "page.totalPage",
            //records: "page.totalCount"
        },
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order"
        },
        beforeRequest: function (v) {
            $('.ui-jqgrid-hbox').addClass("tableColor");
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        },

        loadComplete: function (xhr) {
            if(xhr.code==401){
                alert("登录超时，请重新登录", function () {
                    parent.location.href = baseURL + 'login.html';
                });
            }
            //计算总的发票数
            var totalCount = 0;
            var dataList = xhr.page.list;
            for(var i=0;i<dataList.length;i++){
                totalCount += dataList[i].invoiceCount;
            }

            $('#totalStatistics').html("合计数量: " + totalCount + "条, 合计金额: "
                + (xhr.totalAmount == null ? 0 : formateAmount(xhr.totalAmount)) + "元, 合计税额: "
                + ( xhr.totalTax == null ? 0 : formateAmount(xhr.totalTax)) + "元");

            vm.totalCount = totalCount;
            vm.totalAmount = formateAmount(xhr.totalAmount);
            vm.totalTax = formateAmount(xhr.totalTax);

            markAmountCharts(xhr.page.list);
            markCountCharts(xhr.page.list);
            if (xhr.page.totalCount != 0 && xhr.page.totalCount != null) {
                postData1 = {
                    'dqskssq': vm.form.dqskssq.substring(0, 4)
                };
                $('#btn').hide();
                $('#btnExport').show();
            } else {
                $('#btn').show();
                $('#btnExport').hide();

            }
        }


    });


});

var vm = new Vue({
    el: '#rrapp',
    totalAmount: 0,
    totalTax: 0,
    totalCount: 0,
    data: {
        dqskssq: {},
        form: {
            dqskssq: new Date().getFullYear() + "-" + formatDate(new Date().getMonth() + 1) + "-" + formatDate(new Date().getDate())
        },
        rules: {
            dqskssq: [{type: 'string', required: true, message: '认证年份不能为空', trigger: 'change'}]
        },
        showList: true
    },

    mounted: function () {

        this.dqskssq = {
            disabledDate: function (time) {
                return time > Date.now();
            }
        };
    },
    methods: {
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').children('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').children('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').children('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').children('span.el-input__prefix').children('i').css('color', '#ffaa00');
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
        dqskssqYearChange: function (val) {
            vm.form.dqskssq = val;
        },
        exportExcel: function () {
            //const me = this;
            //me.openConfirm(me, "是否确认导出？", function () {
            document.getElementById("ifile").src = baseURL + 'export/invoiceeauthenticationsummarystatistics'
                + '?dqskssq=' + postData1.dqskssq
                +'&totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax
                +'&totalCount='+vm.totalCount;
            //}, function () {
            //});
        },
        showTable: function () {
            $('.defaultItem').removeClass('hideItem');
            $('#jqGraph').addClass("gridItem");
            $('#grid').removeClass("gridItem");
            $('#exportData').removeClass("gridItem");
            $('#showTableId').addClass('bg-color');
            $('#showGraphId').removeClass('bg-color');
        },
        showGraph: function () {
            $('.defaultItem').addClass('hideItem');
            $('#jqGraph').removeClass("gridItem");
            $('#grid').addClass("gridItem");
            $('#exportData').addClass("gridItem");
            $('#showTableId').removeClass('bg-color');
            $('#showGraphId').addClass('bg-color');

        },
        showAmountGraph: function () {
            $('#amountGraph').removeClass('gridItem');
            $('#countGraph').addClass('gridItem');
            $('#showAmountGraphId').addClass('bg-color');
            $('#showCountGraphId').removeClass('bg-color');
        },
        showCountGraph: function () {
            $('#countGraph').removeClass('gridItem');
            $('#amountGraph').addClass('gridItem');
            $('#showCountGraphId').addClass('bg-color');
            $('#showAmountGraphId').removeClass('bg-color');
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam', 'page');
            $("#jqGrid").jqGrid('setGridParam', {
                postData: {
                    'dqskssq': vm.form.dqskssq.substring(0, 4)
                },
                page: page
            }).trigger("reloadGrid");
        }
    }
});

function formatDate(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
//创建表格(金额，税额)
function markAmountCharts(data) {
    var dqskssq = [];
    var totalAmount = [];
    var taxAmount = [];
    for (var i = data.length - 1; i >= 0; i--) {
        dqskssq.push(data[i].dqskssq);
        totalAmount.push(data[i].totalAmount);
        taxAmount.push(data[i].taxAmount);
    }

    //创建highchats图标
    $('#amountGraph').highcharts({
        chart: {
            type: 'areaspline'
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: dqskssq,
            title: {
                text: '税款所属期',
                align: 'high'
            },
        },
        yAxis: {
            title: {
                text: '金额、税额（元）',
                align:'high'
            },
            labels: {
                formatter: function () {
                    return this.value;
                }
            }
        },
        credits: {
            enabled: false//右下角的版权信息不显示
        },
        tooltip: {
            headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.2f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            area: {
                //pointStart: 1940,
                marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        },
        series: [{
            name: '合计金额',
            data: totalAmount
        }, {
            name: '合计税额',
            data: taxAmount
        }]
    });

}//创建表格(数量)
function markCountCharts(data) {
    var dqskssq = [];
    var invoiceCount = [];
    for (var i = data.length - 1; i >= 0; i--) {
        dqskssq.push(data[i].dqskssq);
        invoiceCount.push(data[i].invoiceCount);
    }
    //创建highchats图标
    $('#countGraph').highcharts({
        chart: {
            type: 'areaspline'
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: dqskssq,
            title: {
                text: '税款所属期',
                align: 'high'
            },
        },
        yAxis: {
            title: {
                text: '发票数量（份）',
                align:'high'
            },
            labels: {
                formatter: function () {
                    return this.value;
                }
            }
        },
        credits: {
            enabled: false//右下角的版权信息不显示
        },
        tooltip: {
            headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
            pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
            '<td style="padding:0"><b>{point.y:.2f}</b></td></tr>',
            footerFormat: '</table>',
            shared: true,
            useHTML: true
        },
        plotOptions: {
            area: {
                //pointStart: 1940,
                marker: {
                    enabled: false,
                    symbol: 'circle',
                    radius: 2,
                    states: {
                        hover: {
                            enabled: true
                        }
                    }
                }
            }
        },
        series: [{
            name: '发票数量',
            data: invoiceCount
        }
        ]
    });

}


//格式化金额 使其保留2位小数
function formateAmount(val) {
    /*
     * 参数说明：
     * number：要格式化的数字
     * decimals：保留几位小数
     * dec_point：小数点符号
     * thousands_sep：千分位符号
     * */
    var number = val;
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
}

