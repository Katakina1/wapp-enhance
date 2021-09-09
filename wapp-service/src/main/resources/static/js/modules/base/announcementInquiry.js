
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


var editor2;
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#vm',
    i18n,
    data: {
        tableData: [],
        currentPage: 1,
        total: 0,
        detailWin: false,
        multipleSelection: [],
        totalPage: 1,
        venderWin: false,
        debtWin:false,
        detailTableData:[],
        editTemplateWin:false,
        announceWin:false,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        options: [],
        venderTable:[],
        debtTable:[],
        debtListLoading:false,
        templateSelectRow:{},
        announceCurrentRow:{},
        debtCurrentPageData:{
            announceCurrentPage: 1,
            announceTotal: 0,
            announceTotalPage: 0
        },
        announceForm:{
            id:null,
            announcementType:'',
            header:'',
            footer:'',
            announcementInfo:'',
            announcementAnnex:'',
            announcementAnnexName:''
        },
        announceCurrentPageData:{
            announceCurrentPage: 1,
            announceTotal: 0,
            announceTotalPage: 0
        },
        templateForm: {
            announcementInfo:''
        },
        announceListLoading:false,
        invoiceTypeList: [],
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        createDateOptions:{},
        createEndDateOptions: {},
        formInline: {
            announcementTitle:'',
            announcementType:'-1',
            createStartDate:  getSixMonthAgo(),
            createEndDate:new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        exportParam: {}
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };

    },
    methods: {
        closeAnnounceWin: function () {
              hyzdh();
//              vm.announceForm.header = '';
//              vm.announceForm.footer = '';
//              vm.announceForm.announcementInfo = '';
//              vm.announceForm.announcementAnnex = '';
//              vm.announceForm.announcementAnnexName = '';
//              vm.announceWin = false;
          },
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
        closeDetailWin: function () {
            vm.detailTableData = [];
            this.detailWin = false;
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
        download: function (row) {
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src= baseURL + 'export/downLoadFile?path=' + row.announcementAnnex + "&token=" + token;
                },
                error: function () {

                }
            });
        },
        deleteBatch: function () {
            var ids = getIds();
            parent.layer.confirm("确定要删除查询出的所有数据吗?",{btn: ['确定', '取消']},function (index) {
                    parent.layer.close(index);
                var loading = vm.getLoading("删除中...");
                $.ajax({
                    type: "POST",
                    url: baseURL + "announcement/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        loading.close();
                        if (r.code == 0) {
                            vm.findAll(1);
                            vm.multipleSelection = [];
                            alert('删除成功');
                        } else if (r.code == 401) {
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
                        }else {
                            alert(r.msg);
                        }
                    }
                });

            });
        },
        venderShow:function (row) {
            this.announceCurrentPageData.announceCurrentPage = 1;
            vm.venderWin=true;
            vm.getVender(row);
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
        getDebt: function () {
            this.debtListLoading = true;

            var params = {};
            params.page = this.debtCurrentPageData.announceCurrentPage;
            params.limit = this.pageSize;
            params.all="1";
            params.sidx = "wtf";
            params.order = "desc";
            params.supplierAnnoucement='1';
            params.announcementType='4';
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
                    vm.debtTable = [];
                    $.each(results.page.list, function (index, element) {
                        vm.debtTable.push(element);
                    });
                    vm.debtListLoading = false;
                }
            });
        },
        getVender: function (row) {
            vm.announceCurrentRow=row;
            this.announceTable = [];
            this.announceListLoading = true;

            var params = {};
            params.page = this.announceCurrentPageData.announceCurrentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            params.announcementId=row.id;

            $.ajax({
                url: baseURL + "announcementInquiry/venderList", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    if(results.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    if(results.page.totalCount===undefined){
                        vm.announceListLoading = false;
                        return;
                    }
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
        exportData:function() {
            var param = this.exportParam;
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':53,'condition':JSON.stringify(param)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
           // document.getElementById("ifile").src = baseURL + 'export/annoucement' + '?' + $.param(param);
        },
        exportTrainVender:function(){
            var params = {};
            params.announcementId = this.announceCurrentRow.id;
            params.announcementTitle = this.announceCurrentRow.announcementTitle;
            document.getElementById("ifile").src = baseURL + 'export/trainVender' + '?' + $.param(params);
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
        checkNullFormat:function (row, column, index) {
            if(index==null||index==''){
                return "—— ——" ;
            }
            return index;
        },
        editAnnounce:function (row) {
            $("#contentTip").remove();
            vm.templateSelectRow = row;
            vm.editTemplateWin=true;
            if(editor2===null||editor2===undefined) {
                $(function () {
                    setTimeout(function () {
                        var E = window.wangEditor;
                        editor2 = new E(document.getElementById('div3'));
                        editor2.customConfig.uploadImgShowBase64 = true;
                        editor2.create();
                    }, 100)
                });
            }
            var data = {
                id: vm.templateSelectRow.id,
            };
            $(function () {
                setTimeout(function () {
                    editor2.txt.html('');
                    $.ajax({
                        type: "POST",
                        url: baseURL + "announcementInquiry/queryAnnounce",
                        contentType: "application/json",
                        async: false,
                        data: JSON.stringify(data),
                        success: function (r) {
                            if (r.code === 0) {
                                editor2.txt.html(r.announcementInfo);
                            } else if (r.code == 401) {
                                alert("登录超时，请重新登录", function () {
                                    parent.location.href = baseURL + 'login.html';
                                });
                            }
                        }
                    });
                }, 100)
            });

        },
        saveTemplate:function () {
            $("#contentTip").remove();
            if (editor2.txt.html() == '<p><br></p>' ) {
                $("#requireMsg1 .el-form-item__content").append('<div class="el-form-item__error" id="contentTip">请输入模板内容!</div>');
                return false;
            }
            var data = {
                id: vm.templateSelectRow.id,
                announcementInfo: editor2.txt.html()
            };
            $.ajax({
                type: "POST",
                url: baseURL + "announcementInquiry/updateAnnounce.ignoreHtmlFilter",
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.findAll(1);
                        vm.editTemplateWin = false;
                        alert('保存成功');

                    }else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        closeEditTemplateWin: function () {
            this.editTemplateWin = false;
        },
        closeVenderWin: function () {
            vm.venderWin = false;
        },
        detailCustom:function (row) {
            vm.debtWin=true;
            vm.getDebt();
        },
        
        detail:function (row) {
            vm.announceForm.id = row.id;
            vm.announceForm.announcementType = row.announcementType;
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
        closeDebtWin: function () {
            vm.debtWin = false;
        },
        closeAnnounceWin: function () {
            vm.announceForm.id = null;
            vm.announceForm.announcementType = '';
            vm.announceForm.header = '';
            vm.announceForm.footer = '';
            vm.announceForm.announcementInfo = '';
            vm.announceForm.announcementAnnex = '';
            vm.announceForm.announcementAnnexName = '';
            vm.announceWin = false;
        },
        currentAnnounceChange: function (currentPage) {
            if (!isNaN(currentPage)) {
                this.announceCurrentPageData.announceCurrentPage = currentPage;
            }
            this.getVender(vm.announceCurrentRow);
        },
        handleAnnounceSizeChange: function (val) {
            this.pageSize = val;
            this.getVender(vm.announceCurrentRow);
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
            var strDate = date.getDate();
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
            var strDate = date.getDate();
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
        debtIndex: function (index) {
            return index + (this.debtCurrentPageData.announceCurrentPage - 1) * this.pageSize + 1;
        },
        /**
         * 行号 - 公告关联供应商列表
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
/***************获取选中的id******************/
function getIds() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var ids = [];
    for (var i = 0; i < selection.length; i++) {
        ids.push(selection[i].id);
    }

    return ids;
}
function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}