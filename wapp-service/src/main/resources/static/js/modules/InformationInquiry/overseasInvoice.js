
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    venderId: "",
    invoiceNo: "",
    orgcode:'-1',
    flowType:'-1',
    receiptId:null,
    receiptAmount:null,
    receiptDate:null,
    invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    orgName:null,
    hostStatus:null,
    page: 1,
    limit: 1
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        listLoading: false,
        sumReturnAmount:0.00,
        amountpaid:0.00,
        file:'',
        selectFileFlag: '未选择文件',
        isNeedFileExtension:false,
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            venderId: '',
            invoiceNo: "",
            orgcode:'-1',
            flowType:'-1',
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        //下面是对应模态框隐藏的属性
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        rules:{},
        showList: true
    },
    mounted:function(){
        this.invoiceDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.querySearchGf();
        $("#gf-select").attr("maxlength","50");
    },
    watch: {
        'form.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        }
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
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==5){
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==7){
                $('#datevalue7').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==5){
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==7){
                $('#datevalue7').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        query: function (formName) {
            isInitial = false;
            $(".checkMsg").remove();
            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'orgcode': vm.form.orgcode,
                        'venderId': vm.form.venderId,
                        'invoiceNo':vm.form.invoiceNo,
                        'flowType':vm.form.flowType,
                        'invoiceDate1': vm.form.invoiceDate1,
                        'invoiceDate2': vm.form.invoiceDate2,
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },

        clear:function(formName){
            vm.form.venderId=null;
            vm.form.poCode=null;
        },
        querySearchGf: function () {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchGf',function(r){
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
        querySearchXf: function (queryString, callback) {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchXf',{queryString:queryString},function(r){
                var resultList = [];
                for(var i=0;i<r.list.length;i++){
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
        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        showCheckInner: function () {
            vm.detailDialogCheckFormInnerVisible = true;
        },
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }
        },
        findAll: function () {
            $(".checkMsg").remove();
            var checkDate=true;
            var poDateStart = new Date(vm.form.invoiceDate1);
            var poDateEnd = new Date(vm.form.invoiceDate2);
            if ( poDateStart.getTime()+1000*60*60*24*360 < poDateEnd.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate=false;
            }else if(poDateEnd.getTime() < poDateStart.getTime()){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate=false;
            }
            if(!(checkDate )){
                return;
            }

            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                jvcode:currentQueryParam.orgcode,
                venderid:currentQueryParam.venderId,
                invoiceNo:currentQueryParam.invoiceNo,
                invoiceDate1: currentQueryParam.invoiceDate1,
                invoiceDate2: currentQueryParam.invoiceDate2,
                flowType:currentQueryParam.flowType
            };
            this.listLoading = true;
            var flag = false;
            this.$http.post(baseURL + 'InformationInquiry/OverseasInvoice/list',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                flag = true;
                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;
                this.tableData = xhr.page.list;
                for(var i=0;i<this.tableData.length;i++){
                    vm.sumReturnAmount=vm.sumReturnAmount+this.tableData[i].receiptAmount;
                }
                /*if(this.tableData.length>0){
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                }*/

                this.listLoading = false;

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
        uploadFile: function (event) {
            if (!vm.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                const formData = new FormData();
                formData.append('file', vm.file);
                formData.append('token', vm.token);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                vm.tempTableData = vm.tableData;
                vm.tableData = [];
                vm.orgLoading = true;
                var flag = false
                var hh;
                const url = baseURL + "InformationInquiry/OverseasInvoice/overseasInvoiceImport";
                vm.$http.post(url, formData, config).then(function (response) {
                    vm.orgLoading = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    vm.file = '';
                    if(response.data.errorCount>0){
                        alert("请检查数据是否完整！");
                        return;
                    }
                    if (response.data.success) {
                        if (vm.tempTableData.length + response.data.reason.length > 1000) {
                            vm.tableData = this.tempTableData;
                            alert('导入数据超过1000条，请修改模板！');
                            return;
                        }
                        for (var i = 0; i < response.data.reason.length; i++) {
                            allDataList.push(response.data.reason[i])
                        }
                        vm.tableData = allDataList;
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount1 + response.data.errorCount2+ response.data.errorCount3+response.data.errorCount4) + "条，导入成功" + response.data.reason.length + "条，导入失败"+(response.data.errorCount1+ response.data.errorCount2+response.data.errorCount3)+"条,其中有"+response.data.errorCount4+"条被修改。请点击 海外问题发票导出 查看海外问题发票数据");
                        vm.selectFileFlag = '';
                        vm.file = '';
                        vm.total=response.data.reason.length;
                        $("#upload_form")[0].reset();
                    } else {
                        vm.tableData = vm.tempTableData;
                        alert(response.data.reason);
                        vm.selectFileFlag = '';
                        vm.file = '';
                        $("#upload_form")[0].reset();
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);

            }
        },
        onChangeFile: function (event) {
            const str = $("#file").val();
            const index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    vm.file = event.target.files[0];
                    vm.isNeedFileExtension = true;
                    //截取名称最后18位
                    vm.selectFileFlag = event.target.files[0].name;
                }
            }
        },
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){
                this.findAll();
            }
        },
        dateFormat: function(row, column, cellValue, index){
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
        templateDownload:function(){
            document.getElementById("ifile").src = baseURL + sysUrl.overseasInvoiceImportExport;
        },
        exportExcel: function(){
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/overseasErrorInvoiceExport');
        },

        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time.toString().replace(/-/g, "/"));
            var seperator1 = "-";
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
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    },
});


function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
}

function detailDateFormat(value) {
    value = value.replace("-", "/");
    value = value.replace("-", "/");
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}

function formatInvoiceStatus(val){

    if(val==0){
        return "正常";
    }
    if(val==1){
        return "失控";
    }
    if(val==2){
        return "作废";
    }
    if(val==3){
        return "红冲";
    }else{
        return "异常";
    }

}
function formatSourceSystem(val){
    if(val==0){
        return "采集";
    }else if(val==1){
        return "查验";
    }else{
        return "录入";
    }
}
function formatQsType(val){
    if(val==0){
        return "扫码签收";
    }
    if(val==1){
        return "扫描仪签收";
    }
    if(val==2){
        return "app签收";
    }
    if(val==3){
        return "导入签收";
    }
    if(val==4){
        return "手工签收";
    }else{
        return "pdf上传签收";
    }
}
function formatOutReason(val){
    if(val==1){
        return "免税项目用";
    }
    if(val==2){
        return "集体福利,个人消费";
    }
    if(val==3){
        return "非正常损失";
    }
    if(val==4){
        return "简易计税方法征税项目用";
    }
    if(val==5){
        return "免抵退税办法不得抵扣的进项税额";
    }
    if(val==6){
        return "纳税检查调减进项税额";
    }
    if(val==7){
        return "红字专用发票通知单注明的进项税额";
    }else{
        return "上期留抵税额抵减欠税";
    }
}
function venderIdFormat(t) {
    var v=t.value;
    while(v.length<6){
        if(v.length == 0){
            break;
        }
        v='0'+v;
    }
    t.value = v;
    // return t;
    vm.form.venderId = t.value;

}