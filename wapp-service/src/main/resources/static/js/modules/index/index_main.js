var flag = "min";
var changeval = 400;
function zdh(){
    if(flag == "max"){return}
    console.log("----zdh----");
    $("#announceWin>.el-dialog").width($("#announceWin>.el-dialog").width()+changeval);
    $("#shell").width($("#shell").width()+changeval);
    $("#shell").children("div").children("img").css("left",parseInt($("#shell").children("div").children("img").css("left"))+changeval);
    $("#shell").children("div").children("div").css("left",parseInt($("#shell").children("div").children("div").css("left"))+changeval);
    $("#shell").children("div").children("a").css("left",parseInt($("#shell").children("div").children("a").css("left"))+changeval);
    $(".el-dialog").css("margin-top","1vh")
    $("#shell").children("div").children("a").text("还原")
    $("#shell").children("div").children("a").attr("onclick","hyzdh()")
    $("#shell").children("div").eq(1).css("min-height","517px");
    $("#shell").children("div").eq(1).css("height","auto");
    flag = "max";
}

function hyzdh(){
     if(flag == "min"){return}
     console.log("----unzdh----");
     $("#announceWin>.el-dialog").width($("#announceWin>.el-dialog").width()-changeval);
     $("#shell").width($("#shell").width()-changeval);
     $("#shell").children("div").children("img").css("left",parseInt($("#shell").children("div").children("img").css("left"))-changeval);
     $("#shell").children("div").children("div").css("left",parseInt($("#shell").children("div").children("div").css("left"))-changeval);
     $("#shell").children("div").children("a").css("left",parseInt($("#shell").children("div").children("a").css("left"))-changeval);
     $(".el-dialog").css("margin-top","15vh")
     $("#shell").children("div").children("a").text("最大化")
     $("#shell").children("div").children("a").attr("onclick","zdh()")
      $("#shell").children("div").eq(1).css("height","517px");
     flag = "min";
 }



var currentDate = new Date();
var currentYear = currentDate.getFullYear();
var currentMonth = currentDate.getMonth() + 1;
var currentDay = currentDate.getDate();
var currentHour = currentDate.getHours();
var currentMinutes = currentDate.getMinutes();
var currentSeconds = currentDate.getSeconds();
var fillZero = "00:00:00";
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
        el: "#index_main_panel",
        data: {
            upform: {refundType:'发票认证'},
            options:[
                {value:'发票认证',label:'发票认证'},
                {value:'发票签收',label:'发票签收'}
            ],

            announceListLoading:false,
            announceCurrentPageData:{
                announceCurrentPage: 1,
                announceTotal: 0,
                announceTotalPage: 0
            },
            announceUnreadWin:false,
            announceTable:[],
            announceWin:false,
            announceForm:{
                id:null,
                announcementType:'',
                header:'',
                footer:'',
                announcementInfo:'',
                announcementAnnex:''
            },
            pageSize: PAGE_PARENT.PAGE_SIZE,
            pageList: PAGE_PARENT.PAGE_LIST,




            dialogVisible1: false,
            dataList1: [],
            listLoading1: false,
            currentPage1: 1,
            dataCount1: 0,
            totalPage1: 0,
            pageSize1: PAGE_PARENT.PAGE_SIZE,
            pageList1: PAGE_PARENT.PAGE_LIST,
            isFirstTime1: true,

            dialogVisible2: false,
            dataList2: [],
            listLoading2: false,
            currentPage2: 1,
            dataCount2: 0,
            totalPage2: 0,
            pageSize2: PAGE_PARENT.PAGE_SIZE,
            pageList2: PAGE_PARENT.PAGE_LIST,
            isFirstTime2: true,

            dialogVisible3: false,
            dataList3: [],
            listLoading3: false,
            currentPage3: 1,
            dataCount3: 0,
            totalPage3: 0,
            pageSize3: PAGE_PARENT.PAGE_SIZE,
            pageList3: PAGE_PARENT.PAGE_LIST,
            isFirstTime3: true,

            dialogVisible4: false,
            dataList4: [],
            listLoading4: false,
            currentPage4: 1,
            dataCount4: 0,
            totalPage4: 0,
            pageSize4: PAGE_PARENT.PAGE_SIZE,
            pageList4: PAGE_PARENT.PAGE_LIST,
            isFirstTime4: true,

            dialogVisible5: false,
            dataList5: [],
            listLoading5: false,
            currentPage5: 1,
            dataCount5: 0,
            totalPage5: 0,
            pageSize5: PAGE_PARENT.PAGE_SIZE,
            pageList5: PAGE_PARENT.PAGE_LIST,
            isFirstTime5: true,

            dialogVisible6: false,
            dataList6: [],
            listLoading6: false,
            currentPage6: 1,
            dataCount6: 0,
            totalPage6: 0,
            pageSize6: PAGE_PARENT.PAGE_SIZE,
            pageList6: PAGE_PARENT.PAGE_LIST,
            isFirstTime6: true,

            dialogVisible7: false,
            dataList7: [],
            listLoading7: false,
            currentPage7: 1,
            dataCount7: 0,
            totalPage7: 0,
            pageSize7: PAGE_PARENT.PAGE_SIZE,
            pageList7: PAGE_PARENT.PAGE_LIST,
            isFirstTime7: true,

            dialogVisible8: false,
            dataList8: [],
            listLoading8: false,
            currentPage8: 1,
            dataCount8: 0,
            totalPage8: 0,
            pageSize8: PAGE_PARENT.PAGE_SIZE,
            pageList8: PAGE_PARENT.PAGE_LIST,
            isFirstTime8: true,

            dialogVisible9: false,
            dataList9: [],
            listLoading9: false,
            currentPage9: 1,
            dataCount9: 0,
            totalPage9: 0,
            pageSize9: PAGE_PARENT.PAGE_SIZE,
            pageList9: PAGE_PARENT.PAGE_LIST,
            isFirstTime9: true,

            dialogVisible10: false,
            dataList10: [],
            listLoading10: false,
            currentPage10: 1,
            dataCount10: 0,
            totalPage10: 0,
            pageSize10: PAGE_PARENT.PAGE_SIZE,
            pageList10: PAGE_PARENT.PAGE_LIST,
            isFirstTime10: true,

            dialogVisible11: false,
            dataList11: [],
            listLoading11: false,
            currentPage11: 1,
            dataCount11: 0,
            totalPage11: 0,
            pageSize11: PAGE_PARENT.PAGE_SIZE,
            pageList11: PAGE_PARENT.PAGE_LIST,
            isFirstTime11: true,

            dialogVisible12: false,
            dataList12: [],
            listLoading12: false,
            currentPage12: 1,
            dataCount12: 0,
            totalPage12: 0,
            pageSize12: PAGE_PARENT.PAGE_SIZE,
            pageList12: PAGE_PARENT.PAGE_LIST,
            isFirstTime12: true,

            dialogVisible13: false,
            dataList13: [],
            listLoading13: false,
            currentPage13: 1,
            dataCount13: 0,
            totalPage13: 0,
            pageSize13: PAGE_PARENT.PAGE_SIZE,
            pageList13: PAGE_PARENT.PAGE_LIST,
            isFirstTime13: true,

            address1: '',
            code1: '',
            tel1: '',
            address2: '',
            code2: '',
            tel2: '',
            recipients1:'',
            recipients2:''
            
        },
        mounted:function () {
            this.mainGf();
            this.doStatisticsSwitchDay(1);
            this.getReceiptInfo();
        },
        methods: {
            getReceiptInfo: function(){
                $.ajax({
                    url: baseURL + "index/vendor/receipt",
                    type: "POST",
                    success: function (result) {
                        vm.address1 = result.SPXX.address;
                        vm.code1 = result.SPXX.zipCode;
                        vm.tel1 = result.SPXX.tel;
                        vm.address2 = result.FYXX.address;
                        vm.code2 = result.FYXX.zipCode;
                        vm.tel2 = result.FYXX.tel;
                        vm.recipients1=result.SPXX.recipients;
                        vm.recipients2=result.FYXX.recipients;
                    }
                });
            },
            closeAnnounceUnreadWin: function () {
                vm.announceUnreadWin = false;
            },
            closeAnnounceWin: function () {
                hyzdh();
                vm.announceForm.id = null;
                vm.announceForm.announcementType = '';
                vm.announceForm.header = '';
                vm.announceForm.footer = '';
                vm.announceForm.announcementInfo = '';
                vm.announceForm.announcementAnnex = '';
                vm.announceWin = false;
            },
            announceShow:function () {
                if(localStorage.getItem("announceShowTimes")==1) {
                    this.getAnnouncement();
                    localStorage.setItem("announceShowTimes",2);
                }
            },
            currentAnnounceChange: function (currentPage) {
                if (!isNaN(currentPage)) {
                    this.announceCurrentPage = currentPage;
                }
                this.getAnnouncement();
            },
            handleAnnounceSizeChange: function (val) {
                this.pageSize = val;
                this.getAnnouncement();
            },
            agree:function () {
                confirm('确定同意吗？', function() {
                    $.ajax({
                        url: baseURL + "announcementInquiry/agreePlus",
                        type: "POST",
                        dataType: "json",
                        data: {announceId: vm.announceForm.id},
                        success: function (results) {
                            alert("成功!已同意。");
                            vm.closeAnnounceWin();
                        }
                    });
                })
            },
            disagree:function () {
                confirm('确定不同意吗？', function() {
                    $.ajax({
                        url: baseURL + "announcementInquiry/disagreePlus",
                        type: "POST",
                        dataType: "json",
                        data: {announceId: vm.announceForm.id},
                        success: function (results) {
                            alert("成功!已不同意。");
                            vm.closeAnnounceWin();
                        }
                    });
                })
            },
            detail:function (row) {
                vm.announceForm.id = row.id;
                vm.announceForm.announcementType = row.announcementType;
                vm.announceForm.header = row.header;
                vm.announceForm.footer = row.footer;
                vm.announceForm.announcementInfo = row.announcementInfo;
                vm.announceForm.announcementAnnex = row.announcementAnnex==''?null:row.announcementAnnex;

                if(row.announcementAnnex!=null){
                    var i = row.announcementAnnex.lastIndexOf("/");
                    var attachmentName = row.announcementAnnex.slice(i+1);
                    vm.announceForm.announcementInfo=vm.announceForm.announcementInfo
                        +'</br><span style="color: red">附件下载：</span><a href="'+baseURL+'export/downLoadFile?path='+vm.announceForm.announcementAnnex+'" download="">'+attachmentName+'</a>';
                }
                vm.announceWin = true;
                $.ajax({
                    url: baseURL + "announcementInquiry/readPlus",
                    type: "POST",
                    dataType: "json",
                    data: {announceId: row.id},
                    success: function (results) {
                        vm.announceTable = [];
                        vm.announceListLoading = true;

                        var params = {};
                        params.page = vm.announceCurrentPageData.announceCurrentPage;
                        params.limit = 10;
                        params.sidx = "wtf";
                        params.order = "desc";
                        $.ajax({
                            url: baseURL + "announcementInquiry/unreadList", async: true, type: "POST", dataType: "json",
                            data: params,
                            success: function (results) {
                                if(results.code == 401) {
                                    alert("登录超时，请重新登录", function () {
                                        parent.location.href = baseURL + 'login.html';
                                    });
                                }
                                vm.announceCurrentPageData.announceTotal = results.page.totalCount;
                                vm.announceCurrentPageData.announceTotalPage = results.page.totalPage;
                                vm.announceTable = [];
                                $.each(results.page.list, function (index, element) {
                                    vm.announceTable.push(element);
                                });
                                vm.announceListLoading = false;
                            }
                        });
                    }
                });
            },
            getAnnouncement: function () {
                this.announceTable = [];
                this.announceListLoading = true;

                var params = {};
                params.page = this.announceCurrentPageData.announceCurrentPage;
                params.limit = this.pageSize;
                params.sidx = "wtf";
                params.order = "desc";

                $.ajax({
                    url: baseURL + "announcementInquiry/unreadList", async: true, type: "POST", dataType: "json",
                    data: params,
                    success: function (results) {
                        if(results.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                var hostHref = parent.location.href;
                                if(hostHref.indexOf("int")!=-1){
                                    parent.location.href ="http://rl.wal-mart.com";
                                }else if(hostHref.indexOf("ext")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else{
                                    parent.location.href = baseURL + 'login.html';
                                }
                            });
                        }
                        vm.announceCurrentPageData.announceTotal = results.page.totalCount;
                        vm.announceCurrentPageData.announceTotalPage = results.page.totalPage;
                        vm.announceTable = [];
                        $.each(results.page.list, function (index, element) {
                            vm.announceTable.push(element);
                        });
                         if(vm.announceTable.length === 1){
                            vm.detail(vm.announceTable[0]);
                        } else if(vm.announceTable.length>0) {
                            vm.announceUnreadWin = true;
                        }
                        vm.announceListLoading = false;
                    }
                });
            },
            announceIndex: function (index) {
                return index + (this.announceCurrentPageData.announceCurrentPage - 1) * this.pageSize + 1;
            },
            
            mainGf: function(){
                this.$http.post(baseURL + 'index/walmart/mainGf',
                    null,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount1 = xhr.count1;
                    this.dataCount2 = xhr.count2;
                    this.dataCount3 = xhr.count3;
                    this.dataCount4 = xhr.count4;
                    this.dataCount5 = xhr.count5;
                    this.dataCount6 = xhr.count6;
                    this.dataCount7 = xhr.count7;
                    this.dataCount8 = xhr.count8;
                    this.dataCount9 = xhr.count9;
                    this.dataCount10 = xhr.count10;
                    this.dataCount11 = xhr.count11;
                    this.dataCount12 = xhr.count12;
                    this.dataCount13 = xhr.count13;
                    
                });
            },


            func:function(val) {
                var obj = [];
                for (var i = 0; i < this.options.length; i++) {
                    var gf = {};
                    gf.value = this.options[i].value;
                    gf.label = this.options[i].label;
                    obj.push(gf);
                }
                vm.options = obj;
                if(vm.upform.refundType == '发票认证'){
                    vm.doStatisticsSwitchDay(1);
                }else
                    if(vm.upform.refundType == '发票签收'){
                    vm.doStatisticsSwitchDayQS(1);
                }
                // else if(vm.upform.refundType == '发票扫描匹配'){
                //     vm.doStatisticsSwitchDaySM(1);
                // }
            },
            doStatisticsSwitchDay: function (type) {
                $('#rz_span').show();
                $('#qs_span').hide();
                /**
                 * 图表
                 */
                var rzcgArray, rzsbArray, xzArray, dateArray;

                //认证成功
                rzcgArray = [];
                //认证失败
                rzsbArray = [];
                //新增
                xzArray = [];
                //日期
                dateArray = [];
                var dateUnit;
                if (type === 1) {
                    $("#showDay").addClass('bg-color');
                    $("#showMonth").removeClass('bg-color');
                    dateUnit = "单位：天";
                    $.ajax({
                        url: "modules/index/invoice/chartStatistics-zhushi", async: false, type: "POST",
                        data: {
                            endDate: getCurrentDateTime(),
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });

                } else {

                    $("#showDay").removeClass('bg-color');
                    $("#showMonth").addClass('bg-color');
                    dateUnit = "单位：月";
                    $.ajax({
                        url: "modules/index/invoice/chartMonthStatistics-zhushi", async: false, type: "POST",
                        data: {
                            endDate: currentYear,
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });
                }


                Highcharts.chart('container', {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: ''
                    },
                    xAxis: {
                        categories: dateArray,
                        crosshair: true,
                        title: {
                            text: dateUnit,
                            align: 'high'
                        }
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: '单位：份',
                            align: 'high'
                        }
                    },
                    tooltip: {
                        headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
                        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                        '<td style="padding:0"><b>{point.y} 份</b></td></tr>',
                        footerFormat: '</table>',
                        shared: true,
                        useHTML: true
                    },
                    plotOptions: {
                        column: {
                            pointPadding: 0.2,
                            borderWidth: 0
                        }
                    },
                    credits: {
                        enabled: false // 禁用版权信息
                    },
                    legend: {
                        layout: 'horizontal',
                        align: 'right',
                        x: 0,
                        verticalAlign: 'top',
                        y: 0,
                        floating: false,
                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
                    },
                    series: [{
                        name: '新增',
                        color: "#FF8282",
                        data: xzArray

                    }, {
                        // type: 'spline',
                        name: '认证失败',
                        color: "#7958FF",
                        data: rzsbArray,
                        // marker: {
                        //     lineWidth: 2,
                        //     lineColor: Highcharts.getOptions().colors[3],
                        //     fillColor: 'white'
                        // }
                    }, {
                        name: '认证成功',
                        color: "#7DBCFF",
                        data: rzcgArray

                    }]
                });
                /*    var chart = $('#container').highcharts();
                    chart.reflow();*/


            },
            doStatisticsSwitchDayQS: function (type) {
                $('#qs_span').show();
                $('#rz_span').hide();
                /**
                 * 图表
                 */
                var rzcgArray, rzsbArray, xzArray, dateArray;

                //认证成功
                rzcgArray = [];
                //认证失败
                rzsbArray = [];
                //新增
                xzArray = [];
                //日期
                dateArray = [];
                var dateUnit;
                if (type === 1) {
                    $("#showDay1").addClass('bg-color');
                    $("#showMonth1").removeClass('bg-color');
                    dateUnit = "单位：天";
                    $.ajax({
                        url: "modules/index/invoice/chartStatisticsQS", async: false, type: "POST",
                        data: {
                            endDate: getCurrentDateTime(),
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });

                } else {

                    $("#showDay1").removeClass('bg-color');
                    $("#showMonth1").addClass('bg-color');
                    dateUnit = "单位：月";
                    $.ajax({
                        url: "modules/index/invoice/chartMonthStatisticsQS", async: false, type: "POST",
                        data: {
                            endDate: currentYear,
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });
                }


                Highcharts.chart('container', {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: ''
                    },
                    xAxis: {
                        categories: dateArray,
                        crosshair: true,
                        title: {
                            text: dateUnit,
                            align: 'high'
                        }
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: '单位：份',
                            align: 'high'
                        }
                    },
                    tooltip: {
                        headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
                        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                        '<td style="padding:0"><b>{point.y} 份</b></td></tr>',
                        footerFormat: '</table>',
                        shared: true,
                        useHTML: true
                    },
                    plotOptions: {
                        column: {
                            pointPadding: 0.2,
                            borderWidth: 0
                        }
                    },
                    credits: {
                        enabled: false // 禁用版权信息
                    },
                    legend: {
                        layout: 'horizontal',
                        align: 'right',
                        x: 0,
                        verticalAlign: 'top',
                        y: 0,
                        floating: false,
                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
                    },
                    series: [{
                        name: '未签收',
                        color: "#FF8282",
                        data: xzArray

                    }, {
                        // type: 'spline',
                        name: '签收失败',
                        color: "#7958FF",
                        data: rzsbArray,
                        // marker: {
                        //     lineWidth: 2,
                        //     lineColor: Highcharts.getOptions().colors[3],
                        //     fillColor: 'white'
                        // }
                    }, {
                        name: '签收成功',
                        color: "#7DBCFF",
                        data: rzcgArray

                    }]
                });
                /*    var chart = $('#container').highcharts();
                    chart.reflow();*/


            },
            doStatisticsSwitchDaySM: function (type) {
                /**
                 * 图表
                 */
                var rzcgArray, rzsbArray, xzArray, dateArray;

                //认证成功
                rzcgArray = [];
                //认证失败
                rzsbArray = [];
                //新增
                xzArray = [];
                //日期
                dateArray = [];
                var dateUnit;
                if (type === 1) {
                    $("#showDay2").addClass('bg-color');
                    $("#showMonth2").removeClass('bg-color');
                    dateUnit = "单位：天";
                    $.ajax({
                        url: "modules/index/invoice/chartStatisticsSM", async: false, type: "POST",
                        data: {
                            endDate: getCurrentDateTime(),
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });

                } else {

                    $("#showDay2").removeClass('bg-color');
                    $("#showMonth2").addClass('bg-color');
                    dateUnit = "单位：月";
                    $.ajax({
                        url: "modules/index/invoice/chartMonthStatisticsSM", async: false, type: "POST",
                        data: {
                            endDate: currentYear,
                        },
                        success: function (results) {
                            var resultString = results.result;
                            $.each(resultString, function (index, element) {
                                if (element.rzcgInvoiceCount != null) {
                                    rzcgArray.push(element.rzcgInvoiceCount);
                                } else {
                                    rzcgArray.push(0);
                                }
                                if (element.rzsbInvoiceCount != null) {
                                    rzsbArray.push(element.rzsbInvoiceCount);
                                } else {
                                    rzsbArray.push(0);
                                }
                                if (element.xzInvoiceCount != null) {
                                    xzArray.push(element.xzInvoiceCount);
                                } else {
                                    xzArray.push(0)
                                }
                                if (element.dayOfMonthInYear != null) {
                                    dateArray.push(element.dayOfMonthInYear);
                                }
                            });
                        }
                    });
                }


                Highcharts.chart('container', {
                    chart: {
                        type: 'column'
                    },
                    title: {
                        text: ''
                    },
                    xAxis: {
                        categories: dateArray,
                        crosshair: true,
                        title: {
                            text: dateUnit,
                            align: 'high'
                        }
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: '单位：份',
                            align: 'high'
                        }
                    },
                    tooltip: {
                        headerFormat: '<span style="font-size:0.1rem">{point.key}</span><table>',
                        pointFormat: '<tr><td style="color:{series.color};padding:0">{series.name}: </td>' +
                        '<td style="padding:0"><b>{point.y} 份</b></td></tr>',
                        footerFormat: '</table>',
                        shared: true,
                        useHTML: true
                    },
                    plotOptions: {
                        column: {
                            pointPadding: 0.2,
                            borderWidth: 0
                        }
                    },
                    credits: {
                        enabled: false // 禁用版权信息
                    },
                    legend: {
                        layout: 'horizontal',
                        align: 'right',
                        x: 0,
                        verticalAlign: 'top',
                        y: 0,
                        floating: false,
                        backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF'
                    },
                    series: [{
                        name: '未扫描匹配',
                        color: "#FF8282",
                        data: xzArray

                    }, {
                        // type: 'spline',
                        name: '扫描匹配失败',
                        color: "#7958FF",
                        data: rzsbArray,
                        // marker: {
                        //     lineWidth: 2,
                        //     lineColor: Highcharts.getOptions().colors[3],
                        //     fillColor: 'white'
                        // }
                    }, {
                        name: '扫描匹配成功',
                        color: "#7DBCFF",
                        data: rzcgArray

                    }]
                });
                /*    var chart = $('#container').highcharts();
                    chart.reflow();*/


            },
            formatDate: function (row, column, cellValue, index) {
                if (cellValue != null && cellValue != "") {
                    return Trim(cellValue).substring(0, 10);
                } else {
                    return "";
                }
            },
            formatDate1: function (strTime) {
                //  IE11里面不能直接转换带"-",必须先替换成"/"
                strTime = strTime.replace("-", "/");
                strTime = strTime.replace("-", "/");
                var date = new Date(strTime); //这里也可以写成 Date.parse(strTime);
                return date.getFullYear() + "-" + this.Formatnum(date.getMonth() + 1) + "-" + this.Formatnum(date.getDate());
            },

            numberFormat: function (row, column, cellValue) {
                if(cellValue==null || cellValue==='' || cellValue == undefined){
                    return "一 一";
                }
                /*
                           * 参数说明：
                           * number：要格式化的数字
                           * decimals：保留几位小数
                           * dec_point：小数点符号
                           * thousands_sep：千分位符号
                           * */
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
            assignNullValue: function (row, column, cellValue, index) {
                if (cellValue == null || cellValue == '') {
                    return "一 一";
                } else {
                    return cellValue;
                }
            },
            formatInvoiceDate: function (row) {
                if (row.invoiceDate != null) {
                    return this.formatDate1(row.invoiceDate, true);
                } else {
                    return '';
                }
            },
            formatstatusUpdateDate: function (row, column) {
                if (row.statusUpdateDate != null) {
                    return this.formatDate1(row.statusUpdateDate);
                } else {
                    return "一 一";
                }
            },
            Formatnum: function (strTime) {
                if (strTime < 10) {
                    return "0" + strTime
                } else {
                    return strTime
                }
            },
            formatcutAppDate: function (row, column) {
                if (row.cutApproveDate != null) {
                    return this.formatDate1(row.cutApproveDate);
                } else {
                    return "一 一";
                }
            },
            formatQsDate: function (row, column) {
                if (row.qsDate != null) {
                    return this.formatDate1(row.qsDate, true);
                } else {
                    return '';
                }
            },
            formatterNull: function (row, column, cellValue) {
                if (cellValue == null || cellValue == "" || cellValue == undefined) {
                    return "一 一";
                } else {
                    return cellValue;
                }
            },
            openLogin:function(code) {
                if(code!=undefined && code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                }
            },
            openAjaxLogin:function(results) {
                if(results.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                }
            },






























            getDataList1: function(){
                this.dialogVisible1 = true;
                if(vm.isFirstTime1){
                    vm.isFirstTime1 = false;
                    vm.queryData1(1);
                }
            },
            closeDialog1: function () {
                this.dialogVisible1 = false;
            },
            mainIndex1: function (index) {
                return index + (this.currentPage1 - 1) * this.pageSize1 + 1;
            },
            currentChange1: function (currentPage) {
                this.queryData1(currentPage)
            },
            handleSizeChange1: function (val) {
                this.pageSize1 = val;
                this.queryData1(1);
            },
            queryData1: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize1
                };
                this.listLoading1 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage1 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list1',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount1 = xhr.page.totalCount;
                    this.currentPage1 = xhr.page.currPage;
                    this.totalPage1 = xhr.page.totalPage;

                    this.dataList1 = xhr.page.list;
                    this.listLoading1 = false;
                });
            },
            
            getDataList2: function(){
                this.dialogVisible2 = true;
                if(vm.isFirstTime2){
                    vm.isFirstTime2 = false;
                    vm.queryData2(1);
                }
            },
            closeDialog2: function () {
                this.dialogVisible2 = false;
            },
            mainIndex2: function (index) {
                return index + (this.currentPage2 - 1) * this.pageSize2 + 1;
            },
            currentChange2: function (currentPage) {
                this.queryData2(currentPage)
            },
            handleSizeChange2: function (val) {
                this.pageSize2 = val;
                this.queryData2(1);
            },
            queryData2: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize2
                };
                this.listLoading2 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage2 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list2',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount2 = xhr.page.totalCount;
                    this.currentPage2 = xhr.page.currPage;
                    this.totalPage2 = xhr.page.totalPage;

                    this.dataList2 = xhr.page.list;
                    this.listLoading2 = false;
                });
            },

            getDataList3: function(){
                this.dialogVisible3 = true;
                if(vm.isFirstTime3){
                    vm.isFirstTime3 = false;
                    vm.queryData3(1);
                }
            },
            closeDialog3: function () {
                this.dialogVisible3 = false;
            },
            mainIndex3: function (index) {
                return index + (this.currentPage3 - 1) * this.pageSize3 + 1;
            },
            currentChange3: function (currentPage) {
                this.queryData3(currentPage)
            },
            handleSizeChange3: function (val) {
                this.pageSize3 = val;
                this.queryData3(1);
            },
            queryData3: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize3
                };
                this.listLoading3 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage3 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list3',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount3 = xhr.page.totalCount;
                    this.currentPage3 = xhr.page.currPage;
                    this.totalPage3 = xhr.page.totalPage;

                    this.dataList3 = xhr.page.list;
                    this.listLoading3 = false;
                });
            },

            getDataList4: function(){
                this.dialogVisible4 = true;
                if(vm.isFirstTime4){
                    vm.isFirstTime4 = false;
                    vm.queryData4(1);
                }
            },
            closeDialog4: function () {
                this.dialogVisible4 = false;
            },
            mainIndex4: function (index) {
                return index + (this.currentPage4 - 1) * this.pageSize4 + 1;
            },
            currentChange4: function (currentPage) {
                this.queryData4(currentPage)
            },
            handleSizeChange4: function (val) {
                this.pageSize4 = val;
                this.queryData4(1);
            },
            queryData4: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize4
                };
                this.listLoading4 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage4 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list4',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount4 = xhr.page.totalCount;
                    this.currentPage4 = xhr.page.currPage;
                    this.totalPage4 = xhr.page.totalPage;

                    this.dataList4 = xhr.page.list;
                    this.listLoading4 = false;
                });
            },

            getDataList5: function(){
                this.dialogVisible5 = true;
                if(vm.isFirstTime5){
                    vm.isFirstTime5 = false;
                    vm.queryData5(1);
                }
            },
            closeDialog5: function () {
                this.dialogVisible5 = false;
            },
            mainIndex5: function (index) {
                return index + (this.currentPage5 - 1) * this.pageSize5 + 1;
            },
            currentChange5: function (currentPage) {
                this.queryData5(currentPage)
            },
            handleSizeChange5: function (val) {
                this.pageSize5 = val;
                this.queryData5(1);
            },
            queryData5: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize5
                };
                this.listLoading5 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage5 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list5',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount5 = xhr.page.totalCount;
                    this.currentPage5 = xhr.page.currPage;
                    this.totalPage5 = xhr.page.totalPage;

                    this.dataList5 = xhr.page.list;
                    this.listLoading5 = false;
                });
            },

            getDataList6: function(){
                this.dialogVisible6 = true;
                if(vm.isFirstTime6){
                    vm.isFirstTime6 = false;
                    vm.queryData6(1);
                }
            },
            closeDialog6: function () {
                this.dialogVisible6 = false;
            },
            mainIndex6: function (index) {
                return index + (this.currentPage6 - 1) * this.pageSize6 + 1;
            },
            currentChange6: function (currentPage) {
                this.queryData6(currentPage)
            },
            handleSizeChange6: function (val) {
                this.pageSize6 = val;
                this.queryData6(1);
            },
            queryData6: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize6
                };
                this.listLoading6 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage6 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list6',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount6 = xhr.page.totalCount;
                    this.currentPage6 = xhr.page.currPage;
                    this.totalPage6 = xhr.page.totalPage;

                    this.dataList6 = xhr.page.list;
                    this.listLoading6 = false;
                });
            },

            getDataList7: function(){
                this.dialogVisible7 = true;
                if(vm.isFirstTime7){
                    vm.isFirstTime7 = false;
                    vm.queryData7(1);
                }
            },
            closeDialog7: function () {
                this.dialogVisible7 = false;
            },
            mainIndex7: function (index) {
                return index + (this.currentPage7 - 1) * this.pageSize7 + 1;
            },
            currentChange7: function (currentPage) {
                this.queryData7(currentPage)
            },
            handleSizeChange7: function (val) {
                this.pageSize7 = val;
                this.queryData7(1);
            },
            queryData7: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize7
                };
                this.listLoading7 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage7 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list7',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount7 = xhr.page.totalCount;
                    this.currentPage7 = xhr.page.currPage;
                    this.totalPage7 = xhr.page.totalPage;

                    this.dataList7 = xhr.page.list;
                    this.listLoading7 = false;
                });
            },

            getDataList8: function(){
                this.dialogVisible8 = true;
                if(vm.isFirstTime8){
                    vm.isFirstTime8 = false;
                    vm.queryData8(1);
                }
            },
            closeDialog8: function () {
                this.dialogVisible8 = false;
            },
            mainIndex8: function (index) {
                return index + (this.currentPage8 - 1) * this.pageSize8 + 1;
            },
            currentChange8: function (currentPage) {
                this.queryData8(currentPage)
            },
            handleSizeChange8: function (val) {
                this.pageSize8 = val;
                this.queryData8(1);
            },
            queryData8: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize8
                };
                this.listLoading8 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage8 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list8',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount8 = xhr.page.totalCount;
                    this.currentPage8 = xhr.page.currPage;
                    this.totalPage8 = xhr.page.totalPage;

                    this.dataList8 = xhr.page.list;
                    this.listLoading8 = false;
                });
            },

            getDataList9: function(){
                this.dialogVisible9 = true;
                if(vm.isFirstTime9){
                    vm.isFirstTime9 = false;
                    vm.queryData9(1);
                }
            },
            closeDialog9: function () {
                this.dialogVisible9 = false;
            },
            mainIndex9: function (index) {
                return index + (this.currentPage9 - 1) * this.pageSize9 + 1;
            },
            currentChange9: function (currentPage) {
                this.queryData9(currentPage)
            },
            handleSizeChange9: function (val) {
                this.pageSize9 = val;
                this.queryData9(1);
            },
            queryData9: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize9
                };
                this.listLoading9 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage9 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list9',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount9 = xhr.page.totalCount;
                    this.currentPage9 = xhr.page.currPage;
                    this.totalPage9 = xhr.page.totalPage;

                    this.dataList9 = xhr.page.list;
                    this.listLoading9 = false;
                });
            },

            getDataList10: function(){
                this.dialogVisible10 = true;
                if(vm.isFirstTime10){
                    vm.isFirstTime10 = false;
                    vm.queryData10(1);
                }
            },
            closeDialog10: function () {
                this.dialogVisible10 = false;
            },
            mainIndex10: function (index) {
                return index + (this.currentPage10 - 1) * this.pageSize10 + 1;
            },
            currentChange10: function (currentPage) {
                this.queryData10(currentPage)
            },
            handleSizeChange10: function (val) {
                this.pageSize10 = val;
                this.queryData10(1);
            },
            queryData10: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize10
                };
                this.listLoading10 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage10 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list10',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount10 = xhr.page.totalCount;
                    this.currentPage10 = xhr.page.currPage;
                    this.totalPage10 = xhr.page.totalPage;

                    this.dataList10 = xhr.page.list;
                    this.listLoading10 = false;
                });
            },

            getDataList11: function(){
                this.dialogVisible11 = true;
                if(vm.isFirstTime11){
                    vm.isFirstTime11 = false;
                    vm.queryData11(1);
                }
            },
            closeDialog11: function () {
                this.dialogVisible11 = false;
            },
            mainIndex11: function (index) {
                return index + (this.currentPage11 - 1) * this.pageSize11 + 1;
            },
            currentChange11: function (currentPage) {
                this.queryData11(currentPage)
            },
            handleSizeChange11: function (val) {
                this.pageSize11 = val;
                this.queryData11(1);
            },
            queryData11: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize11
                };
                this.listLoading11 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage11 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list11',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount11 = xhr.page.totalCount;
                    this.currentPage11 = xhr.page.currPage;
                    this.totalPage11 = xhr.page.totalPage;

                    this.dataList11 = xhr.page.list;
                    this.listLoading11 = false;
                });
            },

            getDataList12: function(){
                this.dialogVisible12 = true;
                if(vm.isFirstTime12){
                    vm.isFirstTime12 = false;
                    vm.queryData12(1);
                }
            },
            closeDialog12: function () {
                this.dialogVisible12 = false;
            },
            mainIndex12: function (index) {
                return index + (this.currentPage12 - 1) * this.pageSize12 + 1;
            },
            currentChange12: function (currentPage) {
                this.queryData12(currentPage)
            },
            handleSizeChange12: function (val) {
                this.pageSize12 = val;
                this.queryData12(1);
            },
            queryData12: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize12
                };
                this.listLoading12 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage12 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list12',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount12 = xhr.page.totalCount;
                    this.currentPage12 = xhr.page.currPage;
                    this.totalPage12 = xhr.page.totalPage;

                    this.dataList12 = xhr.page.list;
                    this.listLoading12 = false;
                });
            },

            getDataList13: function(){
                this.dialogVisible13 = true;
                if(vm.isFirstTime13){
                    vm.isFirstTime13 = false;
                    vm.queryData13(1);
                }
            },
            closeDialog13: function () {
                this.dialogVisible13 = false;
            },
            mainIndex13: function (index) {
                return index + (this.currentPage13 - 1) * this.pageSize13 + 1;
            },
            currentChange13: function (currentPage) {
                this.queryData13(currentPage)
            },
            handleSizeChange13: function (val) {
                this.pageSize13 = val;
                this.queryData13(1);
            },
            queryData13: function (currentPage) {
                var params = {
                    page: currentPage,
                    limit: this.pageSize13
                };
                this.listLoading13 = true;
                if (!isNaN(currentPage)) {
                    this.currentPage13 = currentPage;
                }
                this.$http.post(baseURL + 'index/walmart/list13',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.dataCount13 = xhr.page.totalCount;
                    this.currentPage13 = xhr.page.currPage;
                    this.totalPage13 = xhr.page.totalPage;

                    this.dataList13 = xhr.page.list;
                    this.listLoading13 = false;
                });
            }
        }

    })
;

/**当前日期时间 yyyy-mm-dd hh:mm:ss*/
function getCurrentDateTime() {
    return currentYear + '-' + fmtNumber(currentMonth) + "-" + fmtNumber(currentDay) + " " + fmtNumber(currentHour) + ':' + fmtNumber(currentMinutes) + ":" + fmtNumber(currentSeconds);
}

/**当前年份第一天yyyy-mm-dd 00:00:00*/
function getCurrentYearDateTime() {
    return currentYear + '-' + '01' + "-" + '01' + " " + fillZero;

}

/**当前日期时间yyyy-mm-dd 00:00:00*/
function getCurrentDateTime1() {

    return currentYear + '-' + fmtNumber(currentMonth) + "-" + fmtNumber(currentDay) + " " + fillZero;
}

/**当前月份第一天yyyy-mm-dd 00:00:00*/
function getCurrentMonthDateTime() {
    var monthDate = new Date();
    monthDate.setDate(1);

    return currentYear + '-' + fmtNumber(currentMonth) + "-" + fmtNumber(monthDate.getDate()) + " " + fillZero;

}

function getBeforeCurrentYear() {
    return currentYear - 1;
}

function fmtNumber(s) {
    return s < 10 ? '0' + s : s;
}

function Trim(str) {
    if (str != null) {
        return str.replace(/(^\s*)|(\s*$)/g, "");
    }
}


