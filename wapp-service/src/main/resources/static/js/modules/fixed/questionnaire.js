Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var isInitial = true;
var personalTaxNumber = 123456789012345;
var matchId;

var vm = new Vue({
    el: '#fixedQuestionnaireApp',
    data: {
        form: {
            invoiceNo: '',
            orderNo: '',
            matchDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            matchDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        fileForm: {},
        matchDateOptions:{},
        listLoading: false,
        uploadLoading: false,
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        invoiceData:[],
        orderData:[],
        fileData:[],
        showDetailWin: false,
        showFileDetailWin: false,
        showImgWin: false,

        tableData5: [],
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
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        questionTypeArray:[],
        //下面是对应模态框隐藏的属性
        trackInvoiceStatusDialog:false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogFormInnerVisible: false,
    },
    mounted: function () {
        this.queryQuestionType();
        this.matchDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
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
        },
        'form.orderNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,20}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.orderNo = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        queryQuestionType: function () {

            var theKey='供应商红票问题单类型';

            $.ajax({
                url: baseURL + 'modules/posuopei/queryPart',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:theKey,
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.label = r.List[i].dictname;
                            gf.value = r.List[i].dictcode;
                            gf.status = r.List[i].dictcode;
                            gfs.push(gf);
                        }
                        vm.questionTypeArray = gfs;
                    }
                }
            });
        },
        query: function(formName){
            isInitial = false;
            this.findAll(1);
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        },
        findAll: function(currentPage){
            $(".checkMsg").remove();
            var checkDate = true;
            var poDateStart = new Date(vm.form.matchDate1);
            var poDateEnd = new Date(vm.form.matchDate2);
            if (poDateStart.getTime() + 1000 * 60 * 60 * 24 * 360 < poDateEnd.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate = false;
            } else if (poDateEnd.getTime() < poDateStart.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate = false;
            }
            if (!checkDate) {
                return;
            }


            var params = {
                invoiceNo: vm.form.invoiceNo,
                orderNo: vm.form.orderNo,
                matchDate1: vm.form.matchDate1,
                matchDate2: vm.form.matchDate2,
                page: currentPage,
                limit: this.pageSize
            };
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'fixed/questionnaire/list',
                params,
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
        },
        matchDetail: function(row){
            var params = {matchId: row.id};
            this.$http.post(baseURL + 'fixed/questionnaire/detail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.invoiceData = xhr.invoiceList;
                this.orderData = xhr.orderList;
                vm.showDetailWin = true;
            });
        },
        beforeCloseDetailWin: function(){
            this.invoiceData = [];
            this.orderData = [];
            vm.showDetailWin = false;
        },
        fileDetail: function(row){
            var params = {matchId: row.id};
            matchId = row.id;
            this.$http.post(baseURL + 'fixed/questionnaire/fileDetail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.fileData = xhr.fileList;
                vm.showFileDetailWin = true;
            });
        },
        beforeCloseFileDetailWin: function(){
            this.fileData = [];
            vm.showFileDetailWin = false;
        },
        viewFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("viewDetailImgId").src = baseURL + 'fixed/questionnaire/viewFile?id='+row.id + "&token=" + token;
                },
                error: function () {

                }
            });
            vm.showImgWin = true;
        },
        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'fixed/questionnaire/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        deleteFile: function(tableData, row){
            tableData.splice(tableData.indexOf(row), 1);
        },
        beforeCloseImgWin: function () {
            document.getElementById("viewDetailImgId").src = '';
            vm.showImgWin = false;
        },
        fileConfirm: function(){
            var match = {};
            match.id = matchId;
            match.fileList = vm.fileData;
            $.ajax({
                type:"POST",
                url:baseURL + 'fixed/questionnaire/fileConfirm',
                data: JSON.stringify(match),
                dataType:"json",
                contentType:"application/json",
                async: false,
                cache:false,
                success:function(r){
                    vm.showFileDetailWin = false;
                    if(r.code==0) {
                        alert("已提交问题单");
                        vm.fileData = [];
                    }else{
                        alert("提交问题单失败,请稍后重试");
                        vm.fileData = [];
                    }
                }
            });
        },
        uploadFile: function () {
            var fileValue = document.getElementById("fileId").value;
            if(fileValue==null || fileValue==''){
                alert("请选择文件");
                return;
            }
            var filename=fileValue;
            var index1=filename.lastIndexOf(".");
            var index2=filename.length;
            var type=filename.substring(index1,index2);
            if(type!='.bpm'&&type!='.jpg'&&type!='.png'&&type!='.pdf'&&type!='.PDF'){
                alert("请选择后缀名为bmp、jpg、png、pdf的文件");
                return;
            }
            var f = document.getElementById("fileId");
            var fileSize = 0;
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            if (isIE && !f.files) {
                var filePath = f.value;
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                var file = fileSystem.GetFile (filePath);
                fileSize = file.Size;
            }else {
                fileSize = f.files[0].size;
            }
            fileSize = Math.round(fileSize/1024/1024*100)/100; //单位为M
            if(fileSize>=5){
                $(f).val('');
                alert("您上传的图片大小超过5M，请重新上传！");
                return false;
            }
            vm.uploadLoading = true;
            var file = document.getElementById("fileId").files[0];
            var formData = new FormData();
            formData.append("file", file);
            if(type!='.bpm'&&type!='.jpg'&&type!='.png'){
                formData.append("fileType", "2");
            }else{
                formData.append("fileType", "1");
            }
            $.ajax({
                url: baseURL + 'fixed/match/uploadFile',
                data: formData,
                type: "POST",
                async: false,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (r) {
                    if(r.code=='0') {
                        var data = r.fileEntity;
                        vm.fileData.push(data);
                    }else{
                        alert(r.msg);
                    }
                    vm.uploadLoading = false;
                },
            });
            document.getElementById("fileId").value = '';
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
            if (cellValue == null || cellValue === '' || cellValue == undefined) {
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
        rateFormat: function(row, column, cellValue){
            if(cellValue==null){
                return "";
            }
            return String(cellValue)+"%";
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
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
        detailInnerFormCancel: function () {
            vm.detailDialogFormInnerVisible = false;
        },
        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        detail:function(row){
            var value = row.id;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            $.ajax({
                type: "POST",
                url: baseURL + "modules/fixed/matchQuery/invoice",
                contentType: "application/json",
                data: JSON.stringify(value),
                success: function (r) {
                    if (r.code == 0) {
                        vm.detailForm.gxDate = r.invoiceEntity.gxDate;
                        vm.detailForm.gxUserName = r.invoiceEntity.gxUserName;
                        vm.detailForm.confirmDate = r.invoiceEntity.confirmDate;
                        vm.detailForm.confirmUser = r.invoiceEntity.confirmUser;
                        vm.detailForm.sendDate = r.invoiceEntity.sendDate;
                        vm.detailForm.authStatus = r.invoiceEntity.authStatus;
                        vm.detailForm.machinecode = r.invoiceEntity.machinecode;
                        vm.detailForm.invoiceType = r.invoiceEntity.invoiceType;
                        vm.detailForm.outList = r.outList;
                        vm.detailForm.matchno = r.invoiceEntity.matchno;
                        vm.detailForm.matchDate = r.invoiceEntity.fixedMatchDate;
                        vm.detailForm.matchStatus = r.invoiceEntity.matchStatus;
                        if (r.invoiceEntity.gfTaxNo == personalTaxNumber) {
                            vm.detailForm.gfTaxNo = "";
                        } else {
                            vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                        }
                        vm.detailForm.invoiceStatus=formatInvoiceStatus(r.invoiceEntity.invoiceStatus);
                        vm.detailForm.sourceSystem=formatSourceSystem(r.invoiceEntity.sourceSystem);
                        vm.detailForm.createDate=r.invoiceEntity.createDate;
                        vm.detailForm.statusUpdateDate=r.invoiceEntity.statusUpdateDate;
                        vm.detailForm.qsType=formatQsType(r.invoiceEntity.qsType);
                        vm.detailForm.qsBy=r.invoiceEntity.qsBy;
                        vm.detailForm.qsDate=r.invoiceEntity.qsDate;
                        vm.detailForm.rzhYesorno=r.invoiceEntity.rzhYesorno;
                        vm.detailForm.rzhBelongDate=r.invoiceEntity.rzhBelongDate;
                        vm.detailForm.rzhDate=r.invoiceEntity.rzhDate;
                        vm.detailForm.outDate=r.invoiceEntity.outDate;
                        vm.detailForm.outBy=r.invoiceEntity.outBy;
                        vm.detailForm.outReason=formatOutReason(r.invoiceEntity.outReason);
                        vm.detailForm.qsStatus=r.invoiceEntity.qsStatus;
                        vm.detailForm.outStatus=r.invoiceEntity.outStatus;
                        vm.detailForm.checkCode=r.invoiceEntity.checkCode;
                        vm.detailForm.gfName = r.invoiceEntity.gfName;
                        vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                        vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                        vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                        vm.detailForm.xfName = r.invoiceEntity.xfName;
                        vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                        vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                        vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                        vm.detailForm.remark = r.invoiceEntity.remark;
                        vm.detailForm.totalAmount =vm.numberFormat(null,null,r.invoiceEntity.totalAmount);
                        vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                        vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                        vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                        //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
                        if (r.invoiceEntity.invoiceType == "03") {
                            vm.detailForm.taxAmount =vm.numberFormat(null,null,r.invoiceEntity.taxAmount);
                            vm.detailForm.phone = r.phone;
                            vm.detailForm.address = r.address;
                            vm.detailForm.account = r.account;
                            vm.detailForm.bank = r.bank;
                            if (r.detailVehicleEntity != null) {
                                vm.detailForm.vehicleType = r.detailVehicleEntity.vehicleType;
                                vm.detailForm.factoryModel = r.detailVehicleEntity.factoryModel;
                                vm.detailForm.productPlace = r.detailVehicleEntity.productPlace;
                                vm.detailForm.certificate = r.detailVehicleEntity.certificate;
                                vm.detailForm.certificateImport = r.detailVehicleEntity.certificateImport;
                                vm.detailForm.inspectionNum = r.detailVehicleEntity.inspectionNum;
                                vm.detailForm.engineNo = r.detailVehicleEntity.engineNo;
                                vm.detailForm.vehicleNo = r.detailVehicleEntity.vehicleNo;
                                vm.detailForm.taxRate = r.detailVehicleEntity.taxRate;
                                vm.detailForm.taxBureauName = r.detailVehicleEntity.taxBureauName;
                                vm.detailForm.taxBureauCode = r.detailVehicleEntity.taxBureauCode;
                                vm.detailForm.invoiceAmount =vm.numberFormat(null,null,r.invoiceEntity.invoiceAmount);
                                vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                                vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                                vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                                vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                            }
                            vm.detailDialogVehicleFormVisible = true;
                        } else if (r.invoiceEntity.invoiceType == "14") {
                            vm.detailDialogCheckFormVisible = true;
                            vm.detailForm.detailAmountTotal =vm.numberFormat(null,null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal =vm.numberFormat(null,null, r.taxAmountTotal);
                            vm.detailEntityList=r.detailEntityList;
                            for(var i=0;i<vm.detailEntityList.length;i++){
                                vm.detailEntityList[i].unitPrice=vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount=vm.numberFormat(null,null,vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount=vm.numberFormat(null,null,vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList=null;
                            }
                        } else {
                            vm.detailDialogFormVisible = true;
                            vm.detailForm.detailAmountTotal =vm.numberFormat(null,null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal =vm.numberFormat(null,null, r.taxAmountTotal);
                            vm.detailEntityList=r.detailEntityList;
                            for(var i=0;i<vm.detailEntityList.length;i++){
                                vm.detailEntityList[i].unitPrice=vm.detailEntityList[i].unitPrice;
                                vm.detailEntityList[i].detailAmount=vm.numberFormat(null,null,vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount=vm.numberFormat(null,null,vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList=null;
                            }
                        }
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        trackInvoiceStatusClose:function(){
            vm.trackInvoiceStatusDialog=false;
        },
        //跟踪发票状态
        trackInvoiceStatus:function(row){
            vm.trackInvoiceStatusDialog = true;
            tableData5: [
                {
                    invoiceDate: row.invoiceDate,
                    invoiceStatus: '开票'
                },{
                    invoiceDate: row.matchDate,
                    invoiceStatus: dxhyMatchStatus(row.dxhyMatchStatus)
                },{
                    invoiceDate: row.qsDate,
                    invoiceStatus:qsStatus(row.qsStatus)
                },{
                    invoiceDate: row.scanMatchDate,
                    invoiceStatus: scanMatchStatus(row.scanMatchStatus)
                },{
                    invoiceDate: row.hostDate,
                    invoiceStatus: formatHostStatus(row.hostStatus)
                },{
                    invoiceDate: row.invoiceDate,
                    invoiceStatus: '付款'
                }
            ]
        },
        printform:function(oper) {
            if(oper<10){
                var w=screen.availWidth-10;
                var h=screen.availHeight-30;
                if(oper<=3){
                    var head='<script type="text/javascript" charset="utf-8" src="../../js/customer/resource.js"></script>';
                    var prnhtml = $('#printdiv' + oper).find('.el-dialog__body .col-xs-9').html();
                }else{
                    var prnhtml=$('#printdiv'+oper).html();
                }

                var newWin = parent.window.open('',"win","fullscreen=0,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=1,width=" + w + ",height=" + h + ",top=0,left=0",true);
                newWin.document.write(prnhtml);
                newWin.document.close();
                newWin.focus();
                newWin.print();
                newWin.close();
            }else{
                window.print();
            }
        },
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
//扫描匹配（0-未匹配，1-匹配成功，2匹配失败）
function scanMatchStatus(val){
    if(val==0){
        return "未匹配";
    }
    if(val==1){
        return "匹配成功";
    }
    if(val==2){
        return "匹配失败";
    }
}
//（0-未签收 1-已签收）
function qsStatus(val){
    if(val==0){
        return "未签收";
    }
    if(val==1){
        return "已签收";
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
//0-未匹配 2 部分-匹配3-完全匹配4-差异匹配5-匹配失败6-取消匹配
function dxhyMatchStatus(val){
    if(val==0){
        return "未匹配";
    }
    if(val==2){
        return "部分-匹配";
    }
    if(val==3){
        return "完全匹配";
    }
    if(val==4){
        return "差异匹配";
    }
    if(val==5){
        return "匹配失败";
    }
    if(val==6){
        return "取消匹配";
    }
}
function formatHostStatus(val){
    if(val==1){
        return "未匹配";
    }
    if(val==9){
        return "已过账，等待付款";
    }
    if(val==10){
        return "未匹配";
    }
    if(val==11){
        return "简易计税方法征税项目用";
    }
    if(val==12){
        return "已匹配,未过账（无Balance）";
    }
    if(val==13){
        return "删除";
    }
    if(val==14){
        return "当天过账，等待付款";
    }if(val==15){
        return "发票放行";
    }if(val==19){
        return "发票放行";
    }if(val==99){
        return "当月付款";
    }
    if(val==999){
        return "上月付款";
    }else {
        return "host匹配失败";
    }
}