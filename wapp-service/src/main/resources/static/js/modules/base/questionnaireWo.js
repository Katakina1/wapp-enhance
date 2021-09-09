

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
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        options: [],
        venderTable:[],
        questionTable:[],
        venderWin: false,
        questionWin: false,
        rowQuestionnaireId:null,
        invoiceTypeList: [],
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        createDateOptions:{},
        venderListLoading:false,
        questionListLoading:false,
        createEndDateOptions: {},
        venderCurrentPageData:{
            venderCurrentPage: 1,
            venderTotal: 0,
            venderTotalPage: 0
        },
        venderForm:{
            venderId:'',
            venderName:''
        },
        questionnaire:{
            id:'',
            questionnaireTitle:'',
            topics:[{
                //topicid:'',
                topicTitle:'',
                tipicOp:'',
                options:[{
                    id:'',
                    optionName:''
                }]
            }]
        },
        formInline: {
            questionnaireTitle:'',
            createStartDate: new Date().getFullYear() + "-" + format2(new Date().getMonth()+1) + "-01",
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
        this.createEndDateOptions = {
            disabledDate: function(time){
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
        queryVender:function () {
            this.getVender();
        },
        venderShow:function (row) {
            vm.venderWin=true;
            vm.getVender(row);
        },
        questionShow:function (row) {
            var params = {
                id:vm.rowQuestionnaireId,
                userId:row.userid
            };
            $.ajax({
                type: "POST",
                url: baseURL + 'base/questionnaireWo/queryQuestionnaire',
                data: params,
                success: function (r) {
                    if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    vm.questionnaire = r.entity;
                }
            });

            vm.questionWin = true;
        },
        closeVenderWin: function () {
            vm.venderForm.venderId='';
            vm.venderForm.venderName='';
            vm.venderWin = false;
        },
        closeQuestionWin: function () {
            vm.questionWin = false;
        },
        currentVenderChange: function (currentPage) {
            if (!isNaN(currentPage)) {
                vm.venderCurrentPageData.venderCurrentPage = currentPage;
            }
            this.getVender();
        },
        handleVenderSizeChange: function (val) {
            this.pageSize = val;
            this.getVender();
        },
        getVender: function (row) {
            this.venderListLoading = true;
            if(row!==undefined) {
                vm.rowQuestionnaireId = row.id;
            }

            var params = {};
            params.page = this.venderCurrentPageData.venderCurrentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            params.questionnaireId=vm.rowQuestionnaireId;
            params.venderId = vm.venderForm.venderId;
            params.venderName = vm.venderForm.venderName;

            $.ajax({
                url: baseURL + "questionnaireWo/venderList", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    if(results.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }
                    vm.venderCurrentPageData.venderTotal = results.page.totalCount;
                    vm.venderCurrentPageData.venderTotalPage = results.page.totalPage;
                    vm.venderTable = [];
                    $.each(results.page.list, function (index, element) {
                        vm.venderTable.push(element);
                    });
                    vm.venderListLoading = false;
                }
            });
        },
        questionWinClose: function () {
            vm.questionWin = false;
            vm.questionnaire = {};
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
                questionnaireTitle:this.formInline.questionnaireTitle,
                createStartDate: this.formInline.createStartDate===null?'':this.formatDateTime(this.formInline.createStartDate),
                createEndDate: this.formInline.createEndDate===null?'':this.formatDateTime(this.formInline.createEndDate),
            };

            this.exportParam = params_;
            var flag = false;
            var hh;
            this.$http.post(baseURL + "questionnaireWo/list",
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
            if(cellValue==null || cellValue=='' || cellValue == undefined){
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
         * 行号 - 供应商数据列表
         */
        venderIndex: function (index) {
            return index + (this.venderCurrentPageData.venderCurrentPage - 1) * this.pageSize + 1;
        },
    }
});
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
