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

Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: "#index_main_vendor_panel",
    data: {
        pageList: PAGE_PARENT.PAGE_LIST,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        noticeAllCount:0,
        noticeReadCount:0,
        matchSuccessCount:0,
        matchFailedCount:0,
        refundCount:0,
        costCount:0,
        venderTable:[],
        venderWin: false,
        redCount:0,
        currentRow:{},
        agreeRedCount:0,
        disagreeRedCount:0,
        abnormalCount:0,
        debtListLoading:false,
        showInvoice:false,
        abnormalData:[],
        abnormalDataLoading:false,
        announceListLoading:false,
        releasetimeDebt:null,
        announceCurrentPageData:{
            announceCurrentPage: 1,
            announceTotal: 0,
            announceTotalPage: 0
        },
        debtCurrentPageData:{
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
        this.announceShow();
        this.getAllInfo();
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
        closeAnnounceWin: function () {
                hyzdh();
                vm.announceForm.id = null;
                vm.announceForm.announcementType = '';
                vm.announceForm.header = '';
                vm.announceForm.footer = '';
                vm.announceForm.announcementInfo = '';
                vm.announceForm.announcementAnnex = '';
                vm.announceForm.announcementAnnexName = '';
                vm.announceWin = false;
            },
        getAllInfo: function(){
            $.ajax({
                url: baseURL + "index/vendor/main",
                type: "POST",
                success: function (result) {
                    vm.noticeAllCount = result.noticeAllCount;
                    vm.noticeReadCount = result.noticeReadCount;
                    vm.matchSuccessCount = result.matchSuccessCount;
                    vm.matchFailedCount = result.matchFailedCount;
                    vm.refundCount = result.refundCount;
                    vm.costCount = result.costCount;
                    vm.redCount = result.redCount;
                    vm.agreeRedCount = result.agreeRedCount;
                    vm.disagreeRedCount = result.disagreeRedCount;
                    vm.abnormalCount = result.abnormalCount;
                    vm.abnormalData = result.abnormalList;
                }
            });
        },
        closeAnnounceUnreadWin: function () {
            vm.announceUnreadWin = false;
        },
        isRead:function(){
            $.ajax({
                url: baseURL + "announcementInquiry/readPlus",
                type: "POST",
                dataType: "json",
                data: {announceId: vm.currentRow.id,announcementType:vm.currentRow.announcementType,releasetime:vm.releasetimeDebt},
                success: function (results) {
                    vm.announceTable = [];
                    vm.announceListLoading = true;
                    vm.closeAnnounceWin();

                }
            })

        },
        closeAnnounceWin: function () {
            vm.announceForm.id = null;
            vm.announceForm.announcementType = '';
            vm.announceForm.header = '';
            vm.announceForm.footer = '';
            vm.announceForm.announcementInfo = '';
            vm.announceForm.announcementAnnex = '';
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
          /*  $.ajax({
                url: baseURL + "announcementInquiry/readPlus",
                type: "POST",
                dataType: "json",
                data: {announceId: vm.currentRow.id,announcementType:vm.currentRow.announcementType},
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
            });*/
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
        venderShow:function () {
            vm.venderWin=true;
            vm.getDebt();
        },
        closeVenderWin: function () {
            vm.venderWin = false;
        },
        currentDebtChange: function (currentPage) {
            if (!isNaN(currentPage)) {
                vm.debtCurrentPageData.announceCurrentPage = currentPage;
            }
            this.getDebt();
        },
        handleDebtSizeChange: function (val) {
            this.pageSize = val;
            this.getDebt();
        },
        getDebt: function () {;
            this.debtListLoading = true;

            var params = {};
            params.page = this.debtCurrentPageData.announceCurrentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            params.announcementType=vm.currentRow.announcementType;
            params.releasetime=vm.releasetimeDebt;
            $.ajax({
                url: baseURL + "announcement/debtList", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    if(results.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    $('#totalStatistics').html("合计数量: "+results.page.totalCount+"条, PC金额: "+formatMoney(results.pcTotalAmount)+"元, MD金额: "+formatMoney(results.mdTotalAmount)+"元"
                        +", 合计金额: "+formatMoney(results.totalAmount)+"元");
                    vm.debtCurrentPageData.announceTotal = results.page.totalCount;
                    vm.debtCurrentPageData.announceTotalPage = results.page.totalPage;
                    vm.venderTable = [];
                    $.each(results.page.list, function (index, element) {
                        vm.venderTable.push(element);
                    });
                    vm.debtListLoading = false;
                }
            });
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
            vm.releasetimeDebt=row.customReleasetime;
            vm.currentRow=row;
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
                            parent.location.href = baseURL + 'login.html';
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
        showAbnormalInvoice: function(){
            this.showInvoice = true;
        },
        closeInvoice: function(){
            this.showInvoice = false;
        },
        exportDebt:function () {
            document.getElementById("ifile").src = baseURL + 'export/venderDebt?releasetime='+vm.releasetimeDebt;
        },
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        /**
         * 行号 - 债务数据列表
         */
         debtIndex: function (index) {
            return index + (this.debtCurrentPageData.announceCurrentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 未读公告列表
         */
        announceIndex: function (index) {
            return index + (this.announceCurrentPageData.announceCurrentPage - 1) * this.pageSize + 1;
        },
        checkNullFormat:function (row, column, index) {
            if(index==null||index==''){
                return "—— ——" ;
            }
            return index;
        },
        formatDate: function (row, column, cellValue, index) {
            if (cellValue != null && cellValue != "") {
                return cellValue.substring(0, 10);
            } else {
                return "";
            }
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
        }
    }
});
Vue.prototype.numberFormat2 = function (row, column, cellValue) {
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
};

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}