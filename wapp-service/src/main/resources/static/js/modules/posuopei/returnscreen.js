
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var vm = new Vue({
    el: '#rrapp',
    data: {
        bnId:"",
        startDateOptions:{},
        matchno:'',
        imgList:[],
        tableData: [],
        pageCount: 0,
        options: [],
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        listLoading: false,
        scanWin:false,
        form:{
            code:'',
            screenName: '',
            createDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            createDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),

        },

        /***********************发票明细 开始*******************************/
        matchNo:"",
        tempValue: null,
        detailEntityList: [],//存放明细页面数据
        tempDetailEntityList: [],//暂存明细页面详情清单数据
        detailForm: {
            matchno: null,
            matchStatus:  null,
            createDate : null,
            matchUser : null,
            matchErrInfo : null,
            invoiceType: null,
            invoiceStatus: null,
            createDate: null,
            statusUpdateDate: null,
            qsBy: null,
            qsType: null,
            sourceSystem: null,
            qsDate: null,
            rzhYesorno: null,
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            dqskssq: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            outList: [],
            qsStatus: null,
            outStatus: null,
            checkCode: null,
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
        formLabelWidth: '1.2rem',
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        /*******************************结束***********************************************/
    },
    mounted: function () {
        this.startDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch:{
    },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            }
        },

        invoiceDate3Change: function (val) {
            vm.form.createDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.form.createDateEnd = val;
        },
        /**
         * 格式化
         */
        bnSumDecimal:function(row) {

            return vm.numberFormat(null,null,row.bnSum);
        } ,
        query: function () {
            this.$refs['xiaob'].validate(function (valid) {
                if (valid) {
                    vm.findAll(1);
                } else {
                    alert("4444");
                    return false
                }
            })

        },

        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll(currentPage);
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){

                this.findAll(1);
            }
        },
        findAll: function (currentPage) {



            $(".checkMsg").remove();
            var checkDate=true;
            var poDateStart = new Date(vm.form.createDateStart);
            var poDateEnd = new Date(vm.form.createDateEnd);
            if ( poDateStart.getTime()+1000*60*60*24*364 < poDateEnd.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate=false;
            }else if(poDateEnd.getTime() < poDateStart.getTime()){
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate=false;
            }
            if(!(checkDate )){
                return;
            }
            var params = {
                page: currentPage,
                limit: this.pageSize,
                code:vm.form.code,
                screenName:vm.form.screenName,
                createDateStart: vm.form.createDateStart,
                createDateEnd: vm.form.createDateEnd
            };
            vm.listLoading = true;
            var flag = false;
            this.$http.post(baseURL  + '/modules/posuopei/returnScreen/getReturnScreenPage',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                flag = true;
                vm.total = xhr.page.totalCount;
                vm.currentPage = xhr.page.currPage;
                vm.pageCount = xhr.page.totalPage;
                vm.tableData = xhr.page.list;
                vm.listLoading = false;
            });
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);


        },


        /**
         * 格式化匹配时间
         */
        formatMatchDate: function (row, column) {
            if (row.createDate != null) {
                return dateFormat(row.createDate);
            } else {
                return '';
            }
        },


        detailFormCancel: function () {
            vm.detailDialogFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];

        },
        detailVehicleFormCancel: function () {
            vm.detailDialogVehicleFormVisible = false;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.buyerIdNum = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.vehicleType = null;
            vm.detailForm.factoryModel = null;
            vm.detailForm.productPlace = null;
            vm.detailForm.certificate = null;
            vm.detailForm.certificateImport = null;
            vm.detailForm.inspectionNum = null;
            vm.detailForm.engineNo = null;
            vm.detailForm.vehicleNo = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.phone = null;
            vm.detailForm.address = null;
            vm.detailForm.account = null;
            vm.detailForm.bank = null;
            vm.detailForm.taxRate = null;
            vm.detailForm.taxAmount = null;
            vm.detailForm.taxBureauName = null;
            vm.detailForm.taxBureauCode = null;
            vm.detailForm.invoiceAmount = null;
            vm.detailForm.taxRecords = null;
            vm.detailForm.tonnage = null;
            vm.detailForm.limitPeople = null;
        },
        detailCheckFormCancel: function () {
            vm.detailDialogCheckFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];
        },
        detailInnerFormCancel: function () {
            vm.detailDialogFormInnerVisible = false;
        },
        detailCheckInnerFormCancel: function () {
            vm.detailDialogCheckFormInnerVisible = false;
        },
        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        showCheckInner: function () {
            vm.detailDialogCheckFormInnerVisible = true;
        },

        cancelMatchButton:function (row) {
            var params={
                matchno:row.matchno
            }
        },
        receiptdateFormat: function (row) {
            if (row.receiptdate != null) {
                return formaterDate(row.receiptdate);
            } else {
                return '—— ——';
            }
        },

        postdateFormat: function (row) {
            if (row.postdate != null) {
                return formaterDate(row.postdate);
            } else {
                return '—— ——';
            }
        },




        /**
         * 格式化创建时间
         */
        formatCreateTime: function (row, column) {
            if (row.createDate != null) {
                return dateFormat(row.createDate);
            } else {
                return '—— ——';
            }
        },








    }
});
function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}



Number.prototype.formatMoney1 = function (places, symbol, thousand, decimal) {
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var number = this,
        negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};



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
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
function detailDateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}
