
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
        invoiceDetailWin: false,
        multipleSelection: [],
        totalPage: 1,
        detailTableData:[],
        invoiceDetailTableData:[],
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        options: [],
        invoiceTypeList: [],
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        createDateOptions:{},
        createEndDateOptions: {},
        formInline: {
            venderId:'',
            agreementCode:'',
            createStartDate: null,
            createEndDate:null
        },
        exportParam: {}
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.createEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    methods: {
        detailShow:function (row) {
            var params={
                venderId:row.venderId,
                protocolNo:row.protocolNo
            };
            $.ajax({
                type: "POST",
                url: baseURL + "protocol/detail",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (r) {
                    console.log(r);
                    if (r.code == 0) {
                     vm.detailTableData = r.detailList;
                    } else{
                        alert(r.msg);
                    }
                }
            });
            vm.detailWin=true;
        },
        invoiceDetailShow:function (row) {
            var params={
                caseDate:row.caseDate,
                protocolNo:row.protocolNo
            };
            $.ajax({
                type: "POST",
                url: baseURL + "protocol/invoiceDetail",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (r) {
                    if (r.code == 0) {
                        vm.invoiceDetailTableData = r.detailList;
                    } else{
                        alert(r.msg);
                    }
                }
            });
            vm.invoiceDetailWin=true;
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
        closeInvoiceDetailWin: function () {
            vm.invoiceDetailTableData = [];
            this.invoiceDetailWin = false;
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
            if ( (qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return;
            } else if(qsEndDate.getTime()<new Date(this.formInline.createStartDate)){
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
                venderId:this.formInline.venderId,
                agreementCode: this.trimStr(this.formInline.agreementCode),
                //createStartDate: this.formInline.createStartDate===null?'':this.formatDateTime(this.formInline.createStartDate),
                //createEndDate: this.formInline.createEndDate===null?'':this.formatDateTime(this.formInline.createEndDate),
            };

            this.exportParam = params_;
            var flag = false;
            var hh;
            this.$http.post(baseURL + "protocolFailure/list",
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
        deleteBatch: function () {
            var ids = getIds();
            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "protocol/delete",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
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
        exportData() {
            var param = this.exportParam;
            document.getElementById("ifile").src = baseURL + 'export/protocolFailure' + '?' + $.param(param);
        },
        protocolTemplate:function(){
            document.getElementById("ifile").src = baseURL +"export/protocolTemplate";
        },
        invoiceDetailTemplate:function(){
            document.getElementById("ifile").src = baseURL + "export/invoiceDetailTemplate";
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
        },formatCreateDate: function (row, column) {
            if (row.createDate != null) {
                return this.formatDate(row.createDate, true);
            } else {
                return '';
            }
        },
        formatCaseDate: function (row) {
            if (row.caseDate != null) {
                return this.formatDate(row.caseDate, true);
            } else {
                return '';
            }
        },
        formatPostDate: function (row) {
            if (row.postDate != null) {
                return this.formatDate(row.postDate, true);
            } else {
                return '';
            }
        },
        formatPayDate: function (row) {
            if (row.payDate != null) {
                return this.formatDate(row.payDate, true);
            } else {
                return '';
            }
        },
        formatUploadDate: function (row) {
            if (row.uploadDate != null) {
                return this.formatDate(row.uploadDate, true);
            } else {
                return '';
            }
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
        onSubmit() {
            this.findAll(1);
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        handleClick(row) {
            var loadingInstance = this.$loading({
                text: '正在拼命加载中',
                target: document.querySelector('.tableData')
            });
            var reqParam = {
                buyerTaxNo: row.gfTaxNo,
                invoiceType: row.invoiceType,
                invoiceCode: row.invoiceCode,
                agreementCode: row.agreementCode,
                invoiceDate: row.invoiceDate == null ? "" : this.formatDate(row.invoiceDate, false),
                checkCode: row.checkCode,
                invoiceAmount: row.invoiceAmount
            };
            this.$http.post(baseURL + sysUrl.detailedInvoiceHandle,
                reqParam, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                loadingInstance.close();
                alert(response.data.resultTip);
                this.findAll(1);
            }).catch(function (response) {
                loadingInstance.close();
                alert(response.data.msg);
            });
        },
        numberChange(val) {
            var reg = /[^\d]/g;
            this.formInline.agreementCode = val.replace(reg, '');
        },
        numberChangeBinding(val) {
            var reg = /[^\d]/g;
            this.formInline.bindingNo = val.replace(reg, '');
        },
        numberOnKey(event) {
            this.numberChange(event.target._value);
        },
        numberOnKeyBingding(event) {
            this.numberChangeBinding(event.target._value);
        },
        /**
         * 行号 - 发票采集注列表
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});
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

