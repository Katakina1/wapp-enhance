
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    businessType: null,
    invoiceDate1: '',
    invoiceDate2: '',
    redTicketDataSerialNumber:'',
    invoiceCode:'',
    invoiceNo:'',
    id: '',
    serviceNo: null,
    userCode: null,
    page: 1,
    limit: 1
};
var currentQueryParam1 = {
    businessType: null,
    invoiceDate1: '',
    invoiceDate2: '',
    redTicketDataSerialNumber:'',
    invoiceCode:'',
    invoiceNo:'',
    serviceNo: null,
    userCode: null,
    id: '',
    page: 1,
    limit: 1
};


var vm = new Vue({
    el:'#rrapp',
    data:{
        selection:[],
        filePath:'',
        isNeedFileExtension: false,
        fileSizeIsFit: false,
        file: "",
        fileNumber:'',
        xl:[],
        OpenRedTicketType:[],


        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],

        total1: 0,
        currentPage1:1,
        totalPage1: 0,
        pageSize1: PAGE_PARENT.PAGE_SIZE,
        pageList1: PAGE_PARENT.PAGE_LIST,
        pagerCount1: 5,
        tableData1: [],

        total2: 0,
        currentPage2:1,
        totalPage2: 0,
        pageSize2: PAGE_PARENT.PAGE_SIZE,
        pageList2: PAGE_PARENT.PAGE_LIST,
        pagerCount2: 5,
        tableData2: [],

        total3: 0,
        currentPage3:1,
        totalPage3: 0,
        pageSize3: PAGE_PARENT.PAGE_SIZE,
        pageList3: PAGE_PARENT.PAGE_LIST,
        pagerCount3: 5,
        tableData3: [],

        total4: 0,
        currentPage4:1,
        totalPage4: 0,
        pageSize4: PAGE_PARENT.PAGE_SIZE,
        pageList4: PAGE_PARENT.PAGE_LIST,
        pagerCount4: 5,
        tableData4: [],


        total5: 0,
        currentPage5:1,
        totalPage5: 0,
        pageSize5: PAGE_PARENT.PAGE_SIZE,
        pageList5: PAGE_PARENT.PAGE_LIST,
        pagerCount5: 5,
        tableData5: [],

        redTicketDataSerialNumber:'',
        id:'',
        aId:'',
        invoiceNo:'',
        invoiceCode: '',
        businessType:'',

        tableData6 :'',
        tableData7:'',
        listLoading: false,
        totalAmount: 0,
        totalTax: 0,

        listLoading4: false,
        selectFileFlag: '',
        redTicketMatchId:'',
        redNoticeNumber:'',
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rzhDate2: {},
        xfMaxlength: 30,
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            businessType:"1",
            userCode: null,
            serviceNo: null,
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
        },
        invoicequery:{
            invoiceCode:'',
            invoiceNo: null,
            invoiceAmount: null,
            taxRate: null,
            totalAmount: null,
            invoiceDateStart: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        invoiceUpdate:{
            invoiceCode:'',
            invoiceNo: null,
            invoiceAmount: null,
            taxRate: null,
            totalAmount: null,
            taxAmount: null,
            checkNo: null,
            invoiceDateStart: null
        },
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        detailForm: {
            invoiceType: null,
            invoiceStatus:null,
            createDate:null,
            statusUpdateDate:null,
            qsBy:null,
            qsType:null,
            sourceSystem:null,
            qsDate:null,
            rzhYesorno:null,
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            rzhBelongDate:null,
            rzhDate:null,
            outDate:null,
            outBy:null,
            outReason:null,
            qsStatus:null,
            outStatus:null,
            outList: [],
            checkCode:null,
            gfName: null,
            gfTaxNo: null,
            gfAddressAndPhone: null,
            gfBankAndNo: null,
            xfName: null,
            xfTaxNo: null,
            xfAddressAndPhone: null,
            xfBankAndNo: null,
            remark: null,
            detailEntityList: [],
            totalAmount: null,
            invoiceNo: null,
            invoiceCode: null,
            invoiceDate: null,
            buyerIdNum: null,
            vehicleType: null,//机动车明细里的车辆类型
            factoryModel: null,
            productPlace: null,
            certificate: null,
            certificateImport: null,
            inspectionNum: null,
            engineNo: null,
            vehicleNo: null,
            taxBureauName: null,
            taxBureauCode: null,
            phone: null,
            address: null,
            bank: null,
            account: null,
            taxRecords: null,
            limitPeople: null,
            tonnage: null,
            invoiceAmount: null,
            detailAmountTotal: null,
            taxAmountTotal: null,
            taxRate: null,
            taxAmount: null,
            stringTotalAmount: null
        },
        queryData1:{
            // gfTaxNo:null,
            gfName:null,
            orgcode:null,
            usercode:null,
            username:null
        },
        //下面是对应模态框隐藏的属性
        selectDetailDialogPicture:false,
        batchRedTicketDialog:false,
        updateRedTicketDialog:false,
        enterRedTicketDialog:false,
        detailDialogPicture:false,
        detailDialogRedNoticePicture :false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        uploadDialog: false,
        file:'',
        token: token,
        rules:{
            invoiceNo:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{1,8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为不超过8位的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            createDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            createDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.invoiceDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.queryOpenRedTicketType();
        this.queryXL();
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
        queryXL: function () {
            $.get(baseURL + 'modules/openRedInvoiceQuery/queryXL',function(r){
                var gfs = [];
                /* gfs.push({
                     value: "-1",
                     label: "全部"
                 });*/
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.xl = gfs;
            });
        },
        formatInvoiceDate: function (row) {
            if (row.invoiceDate != null) {
                return this.formatDate(row.invoiceDate);
            } else {
                return "一 一";
            }
        },formatDate: function (time) {
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
        queryOpenRedTicketType: function () {
            $.get(baseURL + 'modules/openRedInvoiceQuery/queryOpenRedTicketType',function(r){
                var gfs = [];
                /* gfs.push({
                     value: "-1",
                     label: "全部"
                 });*/
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.OpenRedTicketType = gfs;
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
        query: function () {
            isInitial = false;
            $(".checkMsg").remove();
            var checkKPDate = true;
            var checkQSDate = true;
            var checkRZDate = true;
            var qsStartDate = new Date(vm.form.invoiceDate1);
            var qsEndDate = new Date(vm.form.invoiceDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            if ( qsEndDate.getTime()+1000*60*60*24 > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.invoiceDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if(!(checkKPDate )){
                return;
            }
            var Self = this;

            currentQueryParam = {
                'invoiceDate1': vm.form.invoiceDate1,
                'invoiceDate2': vm.form.invoiceDate2,
                'businessType': vm.form.businessType,
                'userCode': vm.form.userCode,
                'serviceNo': vm.form.serviceNo
            };
            vm.findAll(1);


        },

        invoiceDate3Change: function (val) {
            vm.invoicequery.invoiceDateStart = val;
        },
        importFormCancel: function () {
            vm.batchRedTicketDialog = false;
        },
        batchExport:function(){
            vm.batchRedTicketDialog = true;
        },
        updateRedTicketDialogCancel:function(){
            vm.$refs['invoiceUpdate'].clearValidate();
            vm.updateRedTicketDialog = false;
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
                    gf.label = r.optionList[i].label;
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
        dqskssqDateChange: function(val) {
            vm.form.rzhBelongDate = val;
        },
        qsDate1Change: function(val) {
            vm.form.qsDate1 = val;
        },
        qsDate2Change: function(val) {
            vm.form.qsDate2 = val;
        },
        rzhDate1Change: function(val) {
            vm.form.rzhDate1 = val;
        },
        rzhDate2Change: function(val) {
            vm.form.rzhDate2 = val;
        },


        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'modules/redTicket/selectRedTicketPrintCoverList',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;
                this.tableData = xhr.page.list;
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
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {

                this.findAll(1);
            }
        },
        formatRate: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return Number(cellValue)+'%';
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

        /**
         * 打印封面
         */
        changeFunPO:function(row) {
            var details=[];
            for(var i=0;i<row.length;i++){
                details.push(row[i].id);
            }
            vm.selection=details;
            if(vm.selection.length>0){
                //增加样式 export_btn_pdf
                $("#export_btn_red_pdf").removeAttr("disabled").removeClass("is-disabled");
            }else {
                $("#export_btn_red_pdf").attr("disabled","disabled").addClass("is-disabled");
            }
        },
        printCover:function(){
            var uri = baseURL + 'export/redTicket/printCover' +'?ids='+JSON.stringify(vm.selection)+"&userCode="+vm.form.userCode;
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src = encodeURI(uri)
                },
                error: function () {
                }
            });
        },



        UploadUrl:function(){
            return "返回需要上传的地址";
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

    }
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
/**
 * 显示选择文件的窗口
 */
function showSelectFileWin() {
    $("#file").val("");
    $("#file").click();
}

/**
 * 文件表单提交
 */
function submitFile() {
    if ($("#showFileName").html() == "未选择文件") {
        alert("请选择文件");
    } else if (!vm.isNeedFileExtension) {
        alert("请上传zip/rar电票压缩包或者电票pdf文件!");
    } else if (!vm.fileSizeIsFit) {
        alert("上传的文件不能大于2M");
    } else {
        fileUploadIng = vm.getLoading("上传中...");
        var url = baseURL + 'electron/upload';
        $('#submitFileForm').attr('action', url);
        $('#submitFileForm').submit();
    }
}

/**
 * 处理文件上传的submit，及其返回的结果
 * @type {*|jQuery|HTMLElement}
 */
var frm = $("#upLoadFileCallBack");
frm.load(function () {
    var wnd = this.contentWindow;
    var jsonDataStr = $(wnd.document.body).find("pre").html();
    uploadFileCallBack(jsonDataStr);
});

/**
 * 处理返回数据
 * @param data
 */
function uploadFileCallBack(data) {
    fileUploadIng.close();
    if (data == undefined) {
        return;
    }
    var response = $.parseJSON(data);
    if (response == undefined) {
        return;
    }
    if (response.code == 0) {
        var pdfName = "";
        for (var i = 0; i < response.list.length; i++) {
            var data = response.list[i];
            if (data.readPdfSuccess) {
                pdfName += '<p>文件：' + data.pdfName + '&nbsp;成功</p>';
                electron.listData.push(data);
            } else {
                pdfName += '<p style="color: red">文件：' + data.pdfName + '&nbsp;失败</p>';
            }
        }
        $("#pdfName").append(pdfName);
    } else if (response.code == 401) {
        parent.location.href = baseURL + 'login.html';
    } else {
        alert("系统错误！请稍后再试！");
    }
}
/**
 * 发票验证码输入格式
 * @param t 当前的input
 */
function verificationCheckNoValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.checkNo = t.value.replace(reg,'');
};
/**
 * 发票代码验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceCodeValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.invoiceCode = t.value.replace(reg, '');
};
/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fplx = "";
    if (fpdm.length == 12) {
        var zero=fpdm.substring(0,1);
        var lastTwo=fpdm.substring(10,12)
        if(zero=="0" && (lastTwo=="04" || lastTwo=="05")){
            fplx="04";
        }

    } else if (fpdm.length == 10) {
        var fplxflag = fpdm.substring(7, 8);
        if (fplxflag == "6" || fplxflag == "3") {
            fplx = "04";
        }
    }
    return fplx;
}
function verificationClaimNoValue(t) {
    var reg = /[^\d]/g;
    vm.claimquery.claimno = t.value.replace(reg, '');
};
/**
 * 发票号码验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.invoiceNo = t.value.replace(reg,'');
};