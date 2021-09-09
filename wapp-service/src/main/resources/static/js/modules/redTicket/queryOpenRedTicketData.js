
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
        filePath:'',
        isNeedFileExtension: false,
        fileSizeIsFit: false,
        trackInvoiceStatusDialog:false,
        file: "",
        fileNumber:'',
        xl:[],//税率
        OpenRedTicketType:[],//开红票类型  退货 协议 折让


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
        redNoticeAssociation:'',
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
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rzhDate2: {},
        xfMaxlength: 30,
        form:{
            businessType:'-1',
           //businessType:'',
            userCode: null,
            serviceNo: null,
            createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
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
        //下面是对应模态框隐藏的属性
        selectDetailDialogPicture:false,
        selectDetailDialogPicturePDF:false,
        detailDialogPicture:false,
        detailDialogRedNoticePicture :false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        uploadDialog: false,
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
                var currentTime = new Date(vm.formatDate(vm.form.createDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.createDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.querySearchGf();
        this.queryOpenRedTicketType();
        $("#gf-select").attr("maxlength","50");
        this.queryXL();
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
        'form.userCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]*$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.userCode = oldValue;
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
        query: function () {
            isInitial = false;
            $(".checkMsg").remove();
            var checkKPDate = true;
            var checkQSDate = true;
            var checkRZDate = true;

            var qsStartDate = new Date(vm.form.createDate1);
            var qsEndDate = new Date(vm.form.createDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);


            if ( qsEndDate.getTime()+1000*60*60*24 > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.createDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }


            if(!(checkKPDate )){
                return;
            }
            var Self = this;

                    currentQueryParam = {
                        'invoiceDate1': vm.form.createDate1,
                        'invoiceDate2': vm.form.createDate2,
                        'businessType': vm.form.businessType,
                        'userCode': vm.form.userCode,
                        'serviceNo': vm.form.serviceNo
                    };
                    vm.findAll(1);


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
        queryOpenRedTicketType: function () {
            $.get(baseURL + 'modules/openRedInvoiceQuery/queryOpenRedTicketType',function(r){
                var gfs = [];
                gfs.push({
                     value: '-1',
                     label: "全部"
                 });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.OpenRedTicketType = gfs;
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
            vm.form.createDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form.createDate2 = val;
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
        qsChange: function (value) {
            if(value=="1"){
                $('.qsItem').removeClass("hideItem");
                $('.btn-row2').addClass("hideItem");

                if(vm.form.rzhYesorno=="1"){
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row4').removeClass("hideItem");
                    $('.rzh-row3').addClass("hideItem");
                    $('.rzh-row4').removeClass("hideItem");
                }else{
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row4').addClass("hideItem");
                }
            }else{
                $('.qsItem').addClass("hideItem");
                vm.form.qsType = "-1";
                vm.form.qsDate1 = null;
                vm.form.qsDate2 = null;
                $('.btn-row4').addClass("hideItem");
                if(vm.form.rzhYesorno=="1"){
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row2').addClass("hideItem");
                    $('.rzh-row4').addClass("hideItem");
                    $('.rzh-row3').removeClass("hideItem");
                }else{
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row2').removeClass("hideItem");
                }
            }
        },
        rzhChange: function (value) {
            if(value=="1"){
                $('.rzhItem').removeClass("hideItem");
                $('.btn-row2').addClass("hideItem");
                if(vm.form.qsStatus=="1"){
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row4').removeClass("hideItem");
                    $('.rzh-row3').addClass("hideItem");
                    $('.rzh-row4').removeClass("hideItem");
                }else{
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row4').addClass("hideItem");
                    $('.rzh-row4').addClass("hideItem");
                    $('.rzh-row3').removeClass("hideItem");
                }
            }else{
                $('.rzhItem').addClass("hideItem");
                vm.form.rzhBelongDate = null;
                vm.form.rzhDate1 = null;
                vm.form.rzhDate2 = null;
                $('.btn-row4').addClass("hideItem");
                if(vm.form.qsStatus=="1"){
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row2').addClass("hideItem");
                }else{
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row2').removeClass("hideItem");
                }
            }
        },
        exportExcel: function(){
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExport'
                +'?gfName='+currentQueryParam.gfName
                +'&xfName='+(currentQueryParam.xfName==null?'':currentQueryParam.xfName)
                +'&invoiceNo='+(currentQueryParam.invoiceNo==null?'':currentQueryParam.invoiceNo)
                +'&invoiceDate1='+currentQueryParam.invoiceDate1
                +'&invoiceDate2='+currentQueryParam.invoiceDate2
                +'&invoiceStatus='+currentQueryParam.invoiceStatus
                +'&invoiceType='+currentQueryParam.invoiceType
                +'&qsStatus='+currentQueryParam.qsStatus
                +'&rzhYesorno='+currentQueryParam.rzhYesorno
                +'&qsType='+currentQueryParam.qsType
                +'&qsDate1='+(currentQueryParam.qsDate1==null?'':currentQueryParam.qsDate1)
                +'&qsDate2='+(currentQueryParam.qsDate2==null?'':currentQueryParam.qsDate2)
                +'&rzhBelongDate='+(currentQueryParam.rzhBelongDate==null?'':currentQueryParam.rzhBelongDate)
                +'&rzhDate1='+(currentQueryParam.rzhDate1==null?'':currentQueryParam.rzhDate1)
                +'&rzhDate2='+(currentQueryParam.rzhDate2==null?'':currentQueryParam.rzhDate2)
                +'&totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax);
        },
        exportExcelMX: function(){
            $("#export_btnMX").attr("disabled","disabled").addClass("is-disabled");
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExportMX'
                +'?gfName='+currentQueryParam.gfName
                +'&xfName='+(currentQueryParam.xfName==null?'':currentQueryParam.xfName)
                +'&invoiceNo='+(currentQueryParam.invoiceNo==null?'':currentQueryParam.invoiceNo)
                +'&invoiceDate1='+currentQueryParam.invoiceDate1
                +'&invoiceDate2='+currentQueryParam.invoiceDate2
                +'&invoiceStatus='+currentQueryParam.invoiceStatus
                +'&invoiceType='+currentQueryParam.invoiceType
                +'&qsStatus='+currentQueryParam.qsStatus
                +'&rzhYesorno='+currentQueryParam.rzhYesorno
                +'&qsType='+currentQueryParam.qsType
                +'&qsDate1='+(currentQueryParam.qsDate1==null?'':currentQueryParam.qsDate1)
                +'&qsDate2='+(currentQueryParam.qsDate2==null?'':currentQueryParam.qsDate2)
                +'&rzhBelongDate='+(currentQueryParam.rzhBelongDate==null?'':currentQueryParam.rzhBelongDate)
                +'&rzhDate1='+(currentQueryParam.rzhDate1==null?'':currentQueryParam.rzhDate1)
                +'&rzhDate2='+(currentQueryParam.rzhDate2==null?'':currentQueryParam.rzhDate2)
                +'&totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax);
        },
        exportExcelSL: function(){
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExportSL'
                +'?gfName='+currentQueryParam.gfName
                +'&xfName='+(currentQueryParam.xfName==null?'':currentQueryParam.xfName)
                +'&invoiceNo='+(currentQueryParam.invoiceNo==null?'':currentQueryParam.invoiceNo)
                +'&invoiceDate1='+currentQueryParam.invoiceDate1
                +'&invoiceDate2='+currentQueryParam.invoiceDate2
                +'&invoiceStatus='+currentQueryParam.invoiceStatus
                +'&invoiceType='+currentQueryParam.invoiceType
                +'&qsStatus='+currentQueryParam.qsStatus
                +'&rzhYesorno='+currentQueryParam.rzhYesorno
                +'&qsType='+currentQueryParam.qsType
                +'&qsDate1='+(currentQueryParam.qsDate1==null?'':currentQueryParam.qsDate1)
                +'&qsDate2='+(currentQueryParam.qsDate2==null?'':currentQueryParam.qsDate2)
                +'&rzhBelongDate='+(currentQueryParam.rzhBelongDate==null?'':currentQueryParam.rzhBelongDate)
                +'&rzhDate1='+(currentQueryParam.rzhDate1==null?'':currentQueryParam.rzhDate1)
                +'&rzhDate2='+(currentQueryParam.rzhDate2==null?'':currentQueryParam.rzhDate2)
                +'&totalAmount='+vm.totalAmount
                +'&totalTax='+vm.totalTax);
        },
        printform:function(oper) {
            /*var bdhtml,sprnstr,eprnstr,prnhtml;
          if (oper < 10){
              bdhtml=window.document.body.innerHTML;//获取当前页的html代码
              console.log(bdhtml);
              sprnstr="<!--startprint"+oper+"-->";//设置打印开始区域
              eprnstr="<!--endprint"+oper+"-->";//设置打印结束区域
              var startn=bdhtml.indexOf(sprnstr);
              /!*alert(startn);*!/
              prnhtml=bdhtml.substring(bdhtml.indexOf(sprnstr)+18); //从开始代码向后取html
              console.log('<br>');
              console.log(prnhtml);
              prnhtml=prnhtml.substring(0,prnhtml.indexOf(eprnstr));//从结束代码向前取html
              window.document.body.innerHTML=prnhtml;
              /!*var newWin = window.open("",'newwindow','height=700,width=750,top=100,left=200,toolbar=no,menubar=no,resizable=no,location=no, status=no');
              newWin.document.write(prnhtml);
              newWin.print();*!/
              window.print();
              window.document.body.innerHTML=bdhtml;
          } else {
              window.print();
          }*/
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
        detailFormCancel: function () {
            vm.detailDialogVehicleFormVisible = false;
            vm.detailDialogCheckFormVisible = false;
            vm.detailDialogCheckFormInnerVisible = false;
            vm.detailDialogPicture = false;
            vm.detailDialogRedNoticePicture = false;
            vm.uploadDialog = false;
            //location. reload();


           // vm.tableData1 = null;
            vm.tableData2 = null;
            vm.tableData3 = null;
            vm.tableData4 = null;
            vm.tableData5 = null;
            vm.tableData6 = null;
            vm.tableData7 = null;
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
        detailFormCancelA: function () {
            vm.selectDetailDialogPicture = false;

        },
        detailFormCancelPDF: function () {
            vm.selectDetailDialogPicturePDF = false;

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
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        currentChange1: function(currentPage1){

                this.findAll1(currentPage1);
        },
        currentChange2: function(currentPage2){
            if(vm.total2==0){
                return;
            }
            this.findAll2(currentPage2);
        },
        currentChange3: function(currentPage3){
            if(vm.total3 ==0){
                return;
            }
           this.findAll3(currentPage3);
        },
        currentChange4: function(currentPage4){
            if(vm.total4==0){
                return;
            }
            this.findAll4(currentPage4);
        },
        currentChange5: function(currentPage5){
            if(vm.total5==0){
                return;
            }
            this.findAll5(currentPage5);
        },
        findAll5: function (currentPage5) {
            currentQueryParam.page = currentPage5;
            currentQueryParam.limit = vm.pageSize5;
            currentQueryParam.invoiceCode = vm.invoiceCode;
            currentQueryParam.invoiceNo = vm.invoiceNo;
            currentQueryParam.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam.id = vm.id;

            var flag = false;
            if (!isNaN(currentPage5)) {
                this.currentPage5 = currentPage5;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryByInvoiceCodeAndNo',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total5 = xhr.page5.totalCount;
                this.currentPage5 = xhr.page5.currPage;
                this.totalPage5 = xhr.page5.totalPage;
                this.tableData5 = xhr.page5.list;
                this.listLoading = false;
            });
        },
        findAll4: function (currentPage4) {
            currentQueryParam.page = currentPage4;
            currentQueryParam.limit = vm.pageSize4;
            currentQueryParam.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam.id = vm.id;
            currentQueryParam.invoiceCode = vm.invoiceCode;
            currentQueryParam.invoiceNo = vm.invoiceNo;


            var flag = false;
            if (!isNaN(currentPage4)) {
                this.currentPage4 = currentPage4;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryMergeDetailByNumber',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total4 = xhr.page4.totalCount;
                this.currentPage4 = xhr.page4.currPage;
                this.totalPage4 = xhr.page4.totalPage;
                this.tableData4 = xhr.page4.list;
                this.listLoading = false;
            });
        },
        findAll3: function (currentPage3) {
            currentQueryParam.page = currentPage3;
            currentQueryParam.limit = vm.pageSize3;
            currentQueryParam.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam.id = vm.id;
            currentQueryParam.invoiceCode = vm.invoiceCode;
            currentQueryParam.invoiceNo = vm.invoiceNo;


            var flag = false;
            if (!isNaN(currentPage3)) {
                this.currentPage3 = currentPage3;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryInvoiceDetailByNumber',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total3 = xhr.page3.totalCount;
                this.currentPage3 = xhr.page3.currPage;
                this.totalPage3 = xhr.page3.totalPage;
                this.tableData3 = xhr.page3.list;
                this.listLoading = false;
            });
        },
        findAll2: function (currentPage2) {
            currentQueryParam.page = currentPage2;
            currentQueryParam.limit = vm.pageSize2;
            currentQueryParam.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam.id = vm.id;
            currentQueryParam.invoiceCode = vm.invoiceCode;
            currentQueryParam.invoiceNo = vm.invoiceNo;


            var flag = false;
            if (!isNaN(currentPage2)) {
                this.currentPage2 = currentPage2;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryByInvoiceCodeAndNo',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total2 = xhr.page2.totalCount;
                this.currentPage2 = xhr.page2.currPage;
                this.totalPage2 = xhr.page2.totalPage;
                this.tableData2 = xhr.page2.list;
                this.listLoading = false;
            });
        },
        findAll1: function (currentPage1) {
            currentQueryParam1.page = currentPage1;
            currentQueryParam1.limit = vm.pageSize1;
            currentQueryParam1.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam1.id = vm.id;
            currentQueryParam1.invoiceCode = vm.invoiceCode;
            currentQueryParam1.invoiceNo = vm.invoiceNo;

            var flag = false;
           /* if (!isNaN(currentPage1)) {
                this.currentPage1 = currentPage1;
            }*/
            if(vm.businessType == 1){
                this.$http.post(baseURL + 'modules/openRedTicket/list/queryByRedTicketDataSerialNumber',
                    currentQueryParam1,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.total1 = xhr.page1.totalCount;
                    this.currentPage1 = xhr.page1.currPage;
                    this.totalPage1 = xhr.page1.totalPage;
                    this.tableData1 = xhr.page1.list;
                    this.listLoading = false;
                });
            }
            if(vm.businessType == 2){
                this.$http.post(baseURL + 'modules/openRedTicket/list/queryAgreementByRedTicketDataSerialNumber',
                    currentQueryParam1,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.total1 = xhr.page1.totalCount;
                    this.currentPage1 = xhr.page1.currPage;
                    this.totalPage1 = xhr.page1.totalPage;
                    this.tableData1 = xhr.page1.list;
                    this.listLoading = false;
                });
            }

        },
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryPaged',
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
        exportOpenRedTicket() {
            var params_ = {
                businessType:this.form.businessType,
                invoiceDate1: this.form.createDate1===null?'':this.formatDateTime(this.form.createDate1),
                invoiceDate2: this.form.createDate2===null?'':this.formatDateTime(this.form.createDate2),
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':50,'condition':JSON.stringify(params_)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
           // document.getElementById("ifile").src = baseURL + 'export/RedTicket'+ '?' + $.param(params_);
        },
        //跟踪红票记录
        openRedTicketStatus:function(row){
            vm.trackInvoiceStatusDialog = true;
            vm.tableData5= [];
            if(row.redTicketCreationTime!=null ) {
                vm.tableData5.push({
                    invoiceDate: row.redTicketCreationTime,
                    invoiceStatus: '生成红票资料'
                });
            }
            if(row.twoExamineTime!=null) {
                if(row.examineResult=="2"){
                    vm.tableData5.push({
                        invoiceDate: row.twoExamineTime,
                        invoiceStatus: '上次审核不通过'
                    });
                }
            }
            if(row.examineDate!=null) {
                if(row.examineResult=="2"){
                    vm.tableData5.push({
                        invoiceDate: row.examineDate,
                        invoiceStatus: '审核通过'
                    });
                }
            }
            if(row.examineDate!=null) {
                if(row.examineResult=="3"){
                    vm.tableData5.push({
                        invoiceDate: row.examineDate,
                        invoiceStatus: '审核不通过'
                    });
                }
            }

            if(row.uploadRednoticeTime!=null) {
                vm.tableData5.push({
                    invoiceDate: row.uploadRednoticeTime,
                    invoiceStatus: '上传红字通知单'
                });
            }

            if(row.openRedticketTime!=null) {
                vm.tableData5.push({
                    invoiceDate: row.openRedticketTime,
                    invoiceStatus: '录入红票'
                });
            }
        },
        trackInvoiceStatusClose:function(){
            vm.trackInvoiceStatusDialog=false;
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {

                this.findAll(1);
            }
        },
        handleSizeChange1: function (val) {
            this.pageSize1 = val;
            if(!isInitial) {

                this.findAll1(1);
            }
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
            if(!isInitial) {
                this.findAll2(1);
            }
        },
        handleSizeChange3: function (val) {
            this.pageSize3 = val;
            if(!isInitial) {
                this.findAll3(1);
            }
        },
        handleSizeChange4: function (val) {
            this.pageSize4 = val;
            if(!isInitial) {
                this.findAll4(1);
            }
        },
        handleSizeChange5: function (val) {
            this.pageSize5 = val;
            if(!isInitial) {
                this.findAll5(1);
            }
        },
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        formatRate: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return Number(cellValue)+'%';
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

        detail:function(row){

           var  value = row.id;
           vm.id = row.id;
            vm.id =value;
            var  buType = row.businessType;
           vm.businessType = row.businessType;
           var redNumber =row.redTicketDataSerialNumber;
            vm.redTicketDataSerialNumber = row.redTicketDataSerialNumber;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            var params = {
                'id': value,
                'redTicketDataSerialNumber': redNumber,
                'page': 1,
                'limit':  PAGE_PARENT.PAGE_SIZE
            };
            //退货类型
            if(buType == 1){

                this.$http.post(baseURL + 'modules/openRedTicket/return/redTicketDetail',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    vm.total1 = xhr.page1.totalCount;
                    vm.currentPage1 = 1;
                    vm.totalPage1 = xhr.page1.totalPage;
                    vm.tableData1 = xhr.page1.list;

                    this.total2 = xhr.page2.totalCount;
                    this.currentPage2 = 1;
                    this.totalPage2 = xhr.page2.totalPage;
                    this.tableData2 = xhr.page2.list;

                    this.total3 = xhr.page3.totalCount;
                    this.currentPage3 = 1;
                    this.totalPage3 = xhr.page3.totalPage;
                    this.tableData3 = xhr.page3.list;

                    this.total4 = xhr.page4.totalCount;
                    //this.currentPage4 = xhr.page4.currPage;
                    this.currentPage4 = 1;
                    this.totalPage4 = xhr.page4.totalPage;
                    this.tableData4 = xhr.page4.list;



                    vm.detailDialogVehicleFormVisible = true;

                });

            }
            //协议类型
            if(buType == 2){
                vm.detailDialogCheckFormVisible = true;
                this.$http.post(baseURL + 'redTicket/checkRedTicketInformation/agreementlist',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.total1 = xhr.page1.totalCount;
                    this.currentPage1 = xhr.page1.currPage;
                    this.totalPage1 = xhr.page1.totalPage;
                    this.tableData1 = xhr.page1.list;

                    this.total2 = xhr.page2.totalCount;
                    this.currentPage2 = xhr.page2.currPage;
                    this.totalPage2 = xhr.page2.totalPage;
                    this.tableData2 = xhr.page2.list;

                    this.total3 = xhr.page3.totalCount;
                    this.currentPage3 = xhr.page3.currPage;
                    this.totalPage3 = xhr.page3.totalPage;
                    this.tableData3 = xhr.page3.list;

                    this.total4 = xhr.page4.totalCount;
                    this.currentPage4 = xhr.page4.currPage;
                    this.totalPage4 = xhr.page4.totalPage;
                    this.tableData4 = xhr.page4.list;


                });


            }
            //折让类型
            if(buType == 3){
                vm.detailDialogCheckFormInnerVisible = true;
                this.$http.post(baseURL + 'modules/openRedTicket/list/queryByInvoiceCodeAndNo',
                    params,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.total5 = xhr.page2.totalCount;
                    this.currentPage5 = xhr.page2.currPage;
                    this.totalPage5 = xhr.page2.totalPage;
                    this.tableData5 = xhr.page2.list;

                    this.total3 = xhr.page3.totalCount;
                    this.currentPage3 = xhr.page3.currPage;
                    this.totalPage3 = xhr.page3.totalPage;
                    this.tableData3 = xhr.page3.list;

                    this.total4 = xhr.page4.totalCount;
                    this.currentPage4 = xhr.page4.currPage;
                    this.totalPage4 = xhr.page4.totalPage;
                    this.tableData4 = xhr.page4.list;

                });

            }
        }
        ,
        /**
         * 显示图片窗口
         * @param id 发票的id
         */
        imageWindowShow: function (id) {
            electron.imageShow = true;
            if (electron.invoiceId != id) {
                electron.invoiceId = id;
                $('#get_image_area').attr('src', "");
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/checkImageToken',
                    contentType: "application/json",
                    success: function (r) {
                        document.getElementById("get_image_area").src = baseURL + 'electron/getImageForAll?id=' + id + "&token=" + token;
                    },
                    error: function () {

                    }
                });
            }
        },
        showImg:function(row){
            var  id = row.id;
            var type=row.fileType;
            vm.imgWin = true;
            vm.selectDetailDialogPicture = true;
            $('#viewInvoicesImg').attr('src', "");
                $.ajax({
                    type: "POST",
                    url: baseURL + 'modules/checkImageToken',
                    contentType: "application/json",
                    success: function (r) {
                        document.getElementById("viewInvoicesImg").src = baseURL + 'modules/openRedTicket/getImageForAll?id=' + id + "&token=" + token;
                    },
                    error: function () {

                    }
                });


        },
        /*showPDF:function(row){






            vm.selectDetailDialogPicturePDF = true;
            var  redNoticeAssociation = row.redNoticeAssociation;
            vm.redNoticeAssociation = row.redNoticeAssociation;
            vm.imgWin = true;
            $('#invoiceImgPDF').attr('src', "");
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src = baseURL + 'modules/openRedTicket/getImageForNotice?redNoticeAssociation=' + redNoticeAssociation + "&token=" + token;


                },
                error: function () {

                }
            });
        },*/
        downLoadFileTokenPDF:function(row){
            var  id = row.redNoticeAssociation;
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src= baseURL + 'modules/downLoadFile?id=' + id + "&token=" + token;
                },
                error: function () {

                }
            });

            /* $.ajax({
                 type: "POST",
                 url: baseURL + 'modules/downLoadFile',
                 data:para,{
                 'headers': {
                     "token": token
                 }
             },
                 contentType: "application/json",
                 success: function (r) {

                 },
                 error: function () {

                 }
             });*/
        },
        downLoadFileToken:function(row){
            var  id = row.id;
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src= baseURL + 'modules/downLoadFile?id=' + id + "&token=" + token;
                },
                error: function () {

                }
            });

           /* $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFile',
                data:para,{
                'headers': {
                    "token": token
                }
            },
                contentType: "application/json",
                success: function (r) {

                },
                error: function () {

                }
            });*/
        },
       /* selectImg:function(row){downLoadFile
            vm.selectDetailDialogPicture = true;
            //vm.filePath = row.getFilePath;
            console.log(row.id)
            console.log(row.filePath)
            alert(row.filePath)
        },*/
        //查看上传资料列表
        checkData:function(row){
            var  dataAssociation = row.dataAssociation;
            vm.detailDialogPicture = true;
            var para = {
                'dataAssociation': dataAssociation,
                'id': row.id
            };
            this.$http.post(baseURL + 'modules/openRedTicket/queryImg',
                para,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                   var xhr = res.body;
                    vm.tableData6 = xhr.fileList;
                    vm.detailDialogPicture = true;
            });

        },
        //查看上传红字通知单
        check:function(row){
            var  redNoticeAssociation = row.redNoticeAssociation;
            var para = {
                'redNoticeAssociation': redNoticeAssociation
            };
            this.$http.post(baseURL + 'modules/openRedTicket/queryRedNoticeImg',
                para,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                   var xhr = res.body;
                    vm.tableData7 = xhr.fileList;
                    vm.detailDialogRedNoticePicture = true;
            });

        },
        //上传文件
        uploadImg:function(row){
                vm.aId = row.id;
                vm.fileNumber = row.dataAssociation;
                vm.uploadDialog = true;
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
        /**
         * 选择文件事件
         * @param event 事件触发点
         * @return {boolean}
         */
        onChangeFile: function (event) {
            vm.file = "";
            $("#showFileName").html("未选择文件");
            $("#showFileName").removeAttr("title");
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4).toLowerCase();
            var photoExt1 = str.substr(index, 5).toLowerCase();
            if (photoExt != '' && !(photoExt == '.zip' || photoExt == '.rar' || photoExt == '.pdf' || photoExt == '.png' || photoExt == '.gif' || photoExt1 == '.jpeg')) {
                alert("请上传zip/rar压缩包或者pdf/png/gif/jpeg文件!");
                $("#file").val("");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var maxsize = 2 * 1024 * 1024;//2M
                var file = event.target.files[0];
                var fileSize = file.size;

                if (fileSize > maxsize) {
                    alert("上传的文件不能大于2M");
                    $("#file").val("");
                    vm.fileSizeIsFit = false;
                    return false;
                } else {
                    vm.file = file;
                    $("#showFileName").attr("title", vm.file.name);
                    $("#showFileName").html(vm.file.name);
                    vm.isNeedFileExtension = true;
                    vm.fileSizeIsFit = true;
                }
            }
        },
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
            if ($("#showFileName").html() == "未选择文件") {
                alert("请选择文件");
            } else if (!vm.isNeedFileExtension) {
                alert("请上传zip/rar压缩包或者pdf/png/gif/jpeg文件!");
            } else if (!vm.fileSizeIsFit) {
                alert("上传的文件不能大于2M");
            } else {
                event.preventDefault();
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                formData.append('fileNumber', vm.fileNumber);
                formData.append('id', vm.aId);
               // formData.append('scanPath', electron.form.scanPathId);

                var url = baseURL + 'modules/openRedTicket/data/upload';
               // var loading = vm.getLoading("上传中...");
                $.ajax({
                    type: "POST",
                    url: url,
                    data: formData,
                    dataType: "json",
                    cache: false,//上传文件无需缓存
                    processData: false,//用于对data参数进行序列化处理 这里必须false
                    contentType: false, //必须
                    success: function (response) {
                        //loading.close();
                        $("#showFileName").html('未选择文件');
                        console.log(response)
                        if (response.code == 0) {

                            alert(response.msg);
                            vm.query();
                            vm.uploadDialog = false;

                            /* var pdfName = "";
                           for (var i = 0; i < response.list.length; i++) {
                                var data = response.list[i];
                                if (data.readPdfSuccess) {
                                    pdfName += '<p>文件：' + data.pdfName + '&nbsp;成功</p>';
                                    electron.listData.push(data);
                                } else {
                                    pdfName += '<p style="color: red">文件：' + data.pdfName + '&nbsp;失败</p>';
                                }
                            }
                            $("#pdfName").append(pdfName);*/
                        } else {
                            alert("系统错误！请稍后再试！");
                        }
                    },
                    error: function (response) {
                        //loading.close();
                        alert("系统错误！请稍后再试！");
                    }

                });
            }
        },
        /**
         * 行号
         */
        mainIndex: function (index) {

            return index + (this.currentPage - 1) * this.pageSize + 1;
        },

        // /!**
        //  * 行号
        //  *!/
        mainIndexA: function (index) {
            return index + (this.currentPage1 - 1) * this.pageSize1 + 1;
        },

        mainIndex2: function (index) {
            return index + (this.currentPage2 - 1) * this.pageSize2 + 1;
        }
        ,

        mainIndex3: function (index) {
            return index + (this.currentPage3 - 1) * this.pageSize3 + 1;
        }
        ,

        mainIndex4: function (index) {
            return index + (this.currentPage4 - 1) * this.pageSize4 + 1;
        }
        ,

        mainIndex5: function (index) {
            return index + (this.currentPage5 - 1) * this.pageSize5 + 1;
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