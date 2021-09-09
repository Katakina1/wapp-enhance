var flag = "min";
var changeval = 400;
function zdh(){
    if(flag == "max"){return}
    console.log("----zdh----");
    $(".el-dialog").width($(".el-dialog").width()+changeval);
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
     $(".el-dialog").width($(".el-dialog").width()-changeval);
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
    el: '#vm',
    i18n,
    data: {
        tableData: [],
        currentPage: 1,
        total: 0,
        multipleSelection: [],
        totalPage: 1,
        announceWin:false,
        releasetimeDebt:null,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        options: [],
        venderTable:[],
        currentRow:{},
        venderWin: false,
        invoiceTypeList: [],
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        announceForm:{
            id:null,
            isAgree:null,
            announcementType:'',
            header:'',
            footer:'',
            announcementInfo:'',
            announcementAnnex:'',
            announcementAnnexName:''
        },
        createDateOptions:{},
        announceListLoading:false,
        createEndDateOptions: {},
        announceCurrentPageData:{
            announceCurrentPage: 1,
            announceTotal: 0,
            announceTotalPage: 0
        },
        formInline: {
            announcementTitle:'',
            supplierAnnoucement:'1',
            announcementType:'-1',
            createStartDate: getSixMonthAgo(),
            createEndDate:new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        exportParam: {}
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function(time)  {
                return time.getTime() >= Date.now();
            }
        };

    },
    methods: {
        currentChange: function(val) {
            if(this.total > 0) {
                this.findAll(val);
            }
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(this.total > 0) {
                this.findAll(1);
            }
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
                        vm.findAll(1);
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
                        vm.findAll(1);
                    }
                });
            })
        },
        detail:function (row) {
            vm.releasetimeDebt=row.customReleasetime;
            vm.currentRow=row;
            vm.announceForm.id = row.id;
            vm.announceForm.announcementType = row.announcementType;
            vm.announceForm.isAgree = row.isAgree;
            vm.announceForm.header = row.header;
            vm.announceForm.footer = row.footer;
            vm.announceForm.announcementInfo = row.announcementInfo;
            vm.announceForm.announcementAnnex = row.announcementAnnex==''?null:row.announcementAnnex;

            if(row.announcementAnnex!=null){
                let i = row.announcementAnnex.lastIndexOf("/");
                let attachmentName = row.announcementAnnex.slice(i+1);
                vm.announceForm.announcementInfo=vm.announceForm.announcementInfo
                    +'</br><span style="color: red">附件下载：</span><a href="'+baseURL+'export/downLoadFile?path='+vm.announceForm.announcementAnnex+'" download="">'+attachmentName+'</a>';
            }
            vm.announceWin = true;
        },
        closeAnnounceWin: function () {
            hyzdh();
            vm.announceForm.id = null;
            vm.announceForm.isAgree =null;
            vm.announceForm.announcementType = '';
            vm.announceForm.header = '';
            vm.announceForm.footer = '';
            vm.announceForm.announcementInfo = '';
            vm.announceForm.announcementAnnex = '';
            vm.announceForm.announcementAnnexName = '';
            vm.announceWin = false;
        },
        venderShow:function () {
            vm.venderWin=true;
            vm.getDebt();
        },
        closeVenderWin: function () {
            vm.venderWin = false;
        },
        currentAnnounceChange: function (currentPage) {
            if (!isNaN(currentPage)) {
                this.announceCurrentPage = currentPage;
            }
            this.getDebt();
        },
        handleAnnounceSizeChange: function (val) {
            this.pageSize = val;
            this.getDebt();
        },
        exportDebt:function () {
            document.getElementById("ifile").src = baseURL + 'export/venderDebt?releasetime='+vm.releasetimeDebt;
        },
        getDebt: function () {
            this.announceListLoading = true;

            var params = {};
            params.page = this.announceCurrentPageData.announceCurrentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            params.supplierAnnoucement='1';
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
                    vm.announceCurrentPageData.announceTotal = results.page.totalCount;
                    vm.announceCurrentPageData.announceTotalPage = results.page.totalPage;
                    vm.venderTable = [];
                    $.each(results.page.list, function (index, element) {
                        vm.venderTable.push(element);
                    });
                    vm.announceListLoading = false;
                }
            });
        },
        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },
        findAll: function (currentPage) {
            $(".checkMsg").remove();

            var qsStartDate = new Date(this.formInline.createStartDate);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);

            var qsEndDate = new Date(this.formInline.createEndDate);
            if(qsEndDate.getTime()<new Date(this.formInline.createStartDate)){
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }


            var loadingInstance = this.$loading({
                text: '正在拼命加载中',
                target: document.querySelector('.tableData')
            });
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                sidx: '',
                order: 'desc',
                supplierAnnoucement:this.formInline.supplierAnnoucement,
                announcementTitle:this.formInline.announcementTitle,
                announcementType: this.formInline.announcementType,
                createStartDate: this.formInline.createStartDate===null?'':this.formatDateTime(this.formInline.createStartDate),
                createEndDate: this.formInline.createEndDate===null?'':this.formatDateTime(this.formInline.createEndDate),
            };

            this.exportParam = params_;
            var flag = false;
            var hh;
            this.$http.post(baseURL + "announcementInquiry/list",
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                flag = true;
                loadingInstance.close();
                if (response.data.code != 0 && response.data.code != 401) {
                    alert(response.data.msg);
                    return;
                }
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.tableData = [];
                for (var key in response.data.page.list) {
                    this.$set(this.tableData, key, response.data.page.list[key]);
                }
            },(err)=>{
                loadingInstance.close();
                if(err.status == 408) {
                    alert(err.statusText);
                }
            }).catch(function (response) {
                loadingInstance.close();
                alert(response.data.msg);
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
        exportData() {
            var param = this.exportParam;
            document.getElementById("ifile").src = baseURL + 'export/protocol' + '?' + $.param(param);
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
        formatProtocolStatus:function (row) {
            var protocolStatus = row.protocolStatus;
            if (protocolStatus == null || protocolStatus == undefined || protocolStatus == "") {
                return "— —"
            } else if (protocolStatus == "0") {
                return "协议审批未完成";
            } else if (protocolStatus == "1") {
                return "协议审批完成";
            }
        },
        formatDateTime: function (time) {
            var date = new Date(time.toString().replace(/-/g, "/"));
            var seperator1 = "-";
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            let strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            return date.getFullYear() + seperator1 + month + seperator1 + strDate;
        },
        formatDate: function (time, flag) {
            var date = new Date(time.toString().replace(/-/g,"/"));
            var seperator1 = "";
            if (flag) {
                seperator1 = "-";
            }
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            let strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            return date.getFullYear() + seperator1 + month + seperator1 + strDate;
        },
        formatterNull: function (row, column, cellValue) {
            if (cellValue == null || cellValue == "" || cellValue == undefined) {
                return "一 一";
            } else {
                return cellValue;
            }
        },  dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
                return "—— ——";
            }
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
        checkNullFormat:function (row, column, index) {
            if(index==null||index==''){
                return "—— ——" ;
            }
            return index;
        },
        formatReleaseDate: function (row) {
            if (row.customReleasetime != null) {
                return this.formatDate(row.customReleasetime, true);
            } else if(row.releasetime != null){
                return this.formatDate(row.releasetime, true);
            } else {
                return '';
            }
        },
        onSubmit() {
            this.findAll(1);
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        /**
         * 行号 - 发票采集注列表
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 债务数据列表
         */
        announceIndex: function (index) {
            return index + (this.announceCurrentPageData.announceCurrentPage - 1) * this.pageSize + 1;
        },
    }
});
function getSixMonthAgo(){
    var dt = new Date();
    dt.setMonth( dt.getMonth()-6 );
    return dt.getFullYear()+"-"+format2(dt.getMonth()+1)+"-"+"01";
}
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}
