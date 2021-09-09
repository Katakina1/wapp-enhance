
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    gfName: "***",
    xfName: null,
    invoiceNo: null,
    rebateDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    rebateDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    invoiceStatus: "-1",
    invoiceType: "-1",
    qsStatus: "-1",
    rzhYesorno: "-1",
    qsType: "-1",
    qsDate1: null,
    qsDate2: null,
    rzhBelongDate: null,
    rzhDate1: null,
    rzhDate2: null,
    flowType: -1,
    page: 1,
    limit: 1,
    ids:[],
    venderId:null,
    rebateNo:null,
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        venderIds:[],
        ids:[],
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        listLoading: false,
        totalAmount: 0,
        totalTax: 0,
        rebateDateOptions1: {},
        rebateDateOptions2: {},
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rzhDate2: {},
        upform: {},
        xfMaxlength: 30,
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        options:[
            {value:'全部',label:'全部'},
            {value:'商品发票退票',label:'商品发票退票'},
            {value:'红票',label:'红票'}
        ],
        options1:[
            // {value:'-1',label:'全部'},
            {value:'101',label:'101'},
            {value:'102',label:'102'},
            {value:'103',label:'103'},
            {value:'104',label:'104'},
            {value:'105',label:'105'},
            {value:'106',label:'106'},
            {value:'107',label:'107'},
            {value:'108',label:'108'},
            {value:'109',label:'109'},
            {value:'110',label:'110'},
            {value:'111',label:'111'},
            {value:'112',label:'112'},
            {value:'113',label:'113'},
            {value:'114',label:'114'},
            {value:'115',label:'115'},
            {value:'201',label:'201'},
            {value:'202',label:'202'},
            {value:'203',label:'203'},
            {value:'204',label:'204'},
            {value:'205',label:'205'},
            {value:'206',label:'206'},
            {value:'207',label:'207'},
            {value:'208',label:'208'},
            {value:'209',label:'209'},
            {value:'210',label:'210'},
            {value:'211',label:'211'},
            {value:'212',label:'212'},
            {value:'220',label:'220'},
            {value:'301',label:'301'},
            {value:'302',label:'302'},
            {value:'304',label:'304'},
            {value:'305',label:'305'}
        ],
        options2:[
            // {value:'-1',label:'全部'},
            {value:'306',label:'306'},
        ],
        options3:[
            // {value:'-1',label:'全部'},
            {value:'101',label:'101'},
            {value:'102',label:'102'},
            {value:'103',label:'103'},
            {value:'104',label:'104'},
            {value:'105',label:'105'},
            {value:'106',label:'106'},
            {value:'107',label:'107'},
            {value:'108',label:'108'},
            {value:'109',label:'109'},
            {value:'110',label:'110'},
            {value:'111',label:'111'},
            {value:'112',label:'112'},
            {value:'113',label:'113'},
            {value:'114',label:'114'},
            {value:'115',label:'115'},
            {value:'201',label:'201'},
            {value:'202',label:'202'},
            {value:'203',label:'203'},
            {value:'204',label:'204'},
            {value:'205',label:'205'},
            {value:'206',label:'206'},
            {value:'207',label:'207'},
            {value:'208',label:'208'},
            {value:'209',label:'209'},
            {value:'210',label:'210'},
            {value:'211',label:'211'},
            {value:'212',label:'212'},
            {value:'220',label:'220'},
            {value:'301',label:'301'},
            {value:'302',label:'302'},
            {value:'304',label:'304'},
            {value:'305',label:'305'},
            {value:'306',label:'306'}
        ],
        flowTypes: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            gfName: "-1",
            xfName: null,
            invoiceNo: null,
            rebateDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            rebateDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceStatus: "-1",
            invoiceType: "-1",
            qsStatus: "-1",
            rzhYesorno: "-1",
            qsType: "-1",
            qsDate1: null,
            qsDate2: null,
            rzhBelongDate: null,
            rzhDate1: null,
            rzhDate2: null,
            rebateNo:null,
            rebateExpressno:null,
            venderId:null,
            flowType:"-1",
        },
        detailEntityList: [],
        tempDetailEntityList: [],
        multipleSelection: [],
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
            rebateDate:null,
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
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        updateWin:false,
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
            rebateNo:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{0,18}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            venderId:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{0,6}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            rebateDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            rebateDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.rebateDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.rebateDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.rebateDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.rebateDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.qsDateOptions1 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.qsDateOptions2 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.rzhDateOptions1 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.rzhDateOptions2 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.rzhDate1 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.rzhDate2 = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.querySearchFlowType();
        // this.querySearchGf();
        // $("#gf-select").attr("maxlength","50");
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
        'form.venderId': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.venderId = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        querySearchFlowType: function () {
            $.get(baseURL + 'pack/GenerateBindNumber/searchFlowType',function(r){
                var flowTypes = [];
                flowTypes.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var flowType = {};
                    flowType.value = r.optionList[i].value;
                    flowType.label = r.optionList[i].label;
                    if(flowType.label!="费用"){
                        flowTypes.push(flowType);
                    }
                }
                vm.flowTypes = flowTypes;
            });
        },
        changeFun: function(selection){

            if(selection.length>0) {
                vm.multipleSelection = selection;
                $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
            }
            if(selection.length==0){
                $("#export_btn").attr("disabled","disabled").addClass("is-disabled");

            }
        },
        // print:function (row) {
        //
        //     vm.upform.refundType ='';
        //     vm.upform.refundReason ='';
        //     vm.upform.refundRemark ='';
        //     var roleIds = getSelectedRows();
        //     if (roleIds == null) {
        //        return;
        //    }
        //     vm.multipleSelection = roleIds;
        //
        //     vm.updateWin = true;
        //
        // },
        // submitCancel: function () {
        //     vm.updateWin = false;
        // },
        // submitForm: function () {
        //     this.$refs['upform'].validate(function (valid) {
        //         if (valid) {
        //             vm.printrefund();
        //             vm.findAll(1);
        //         } else {
        //             return false;
        //         }
        //     })
        // },
        print:function() {
            var roleIds = getSelectedRows();
            if (roleIds == null) {
                return;
            }
            vm.multipleSelection = roleIds;
            vm.upform.refundNo ='';
            vm.upform.refundType ='';
            vm.upform.refundReason ='';
            vm.upform.refundRemark ='';
                    var url = baseURL + "export/printQueryPayList"+'?matchnoList='+JSON.stringify(vm.multipleSelection )
                        +'&refundType='+vm.upform.refundType+'&refundNo='+vm.upform.refundNo
                        +'&refundReason='+vm.upform.refundReason+'&refundRemark='+vm.upform.refundRemark;
                    $.ajax({
                        type: "POST",
                        url: baseURL + 'modules/checkImageToken',
                        contentType: "application/json",
                        success: function (r) {
                            document.getElementById("ifile").src = encodeURI(url)
                            // vm.updateWin=false;
                            vm.findAll(1);
                        },
                        error: function () {

                        }
                    });

        },
        exportExcel: function(){
            // document.getElementById("ifile").src = encodeURI(baseURL + 'export/refundExport'
            //     +'?&venderId='+(vm.form.venderId==null?'':vm.form.venderId)
            //     +'&rebateNo='+(vm.form.rebateNo==null?'':vm.form.rebateNo)
            //     +'&rebateDate1='+currentQueryParam.rebateDate1
            //     +'&rebateDate2='+currentQueryParam.rebateDate2
            //     +'&invoiceNo='+(vm.form.invoiceNo==null?'':vm.form.invoiceNo)
            // );
            $("#export_btn1").attr("disabled","true").addClass("is-disabled");


            var params ={
                'venderId':(vm.form.venderId==null?'':vm.form.venderId),
                'rebateNo':(vm.form.rebateNo==null?'':vm.form.rebateNo),
                'rebateDate1':currentQueryParam.rebateDate1,
                'rebateDate2':currentQueryParam.rebateDate2,
                'invoiceNo':(vm.form.invoiceNo==null?'':vm.form.invoiceNo),
                'flowType':vm.form.flowType

            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':30,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
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
        query: function (formName) {
            isInitial = false;
            $(".checkMsg").remove();
            var checkKPDate = true;
            var checkQSDate = true;
            var checkRZDate = true;

            var qsStartDate = new Date(vm.form.rebateDate1);
            var qsEndDate = new Date(vm.form.rebateDate2);
            var qsqsStartDate = new Date(vm.form.qsDate1);
            var qsqsEndDate = new Date(vm.form.qsDate2);
            var qsrzStartDate = new Date(vm.form.rzhDate1);
            var qsrzEndDate = new Date(vm.form.rzhDate2);


            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            qsqsStartDate.setMonth(qsqsStartDate.getMonth() + 12);
            qsrzStartDate.setMonth(qsrzStartDate.getMonth() + 12);

            if ( qsEndDate.getTime()+1000*60*60*24 > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.rebateDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if ( qsqsEndDate.getTime()+1000*60*60*24 > qsqsStartDate.getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkQSDate=false;
            }else if(qsqsEndDate.getTime() < new Date(vm.form.qsDate1)){
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkQSDate=false;
            }
            if ( qsrzEndDate.getTime()+1000*60*60*24 > qsrzStartDate.getTime()) {
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkRZDate=false;
            }else if(qsrzEndDate.getTime() < new Date(vm.form.rzhDate1)){
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkRZDate=false;
            }
            if ( qsrzEndDate.getTime()+1000*60*60*24 > qsrzStartDate.getTime()) {
                $("#requireMsg7 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkRZDate=false;
            }else if(qsrzEndDate.getTime() < new Date(vm.form.rzhDate1)){
                $("#requireMsg7 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkRZDate=false;
            }

            if(!(checkKPDate && checkQSDate && checkRZDate)){
                return;
            }
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'gfName': vm.form.gfName,
                        'xfName': vm.form.xfName,
                        'invoiceNo': vm.form.invoiceNo,
                        'rebateDate1': vm.form.rebateDate1,
                        'rebateDate2': vm.form.rebateDate2,
                        'invoiceStatus': vm.form.invoiceStatus,
                        'invoiceType': vm.form.invoiceType,
                        'qsStatus': vm.form.qsStatus,
                        'rzhYesorno': vm.form.rzhYesorno,
                        'qsType': vm.form.qsType,
                        'qsDate1': vm.form.qsDate1,
                        'qsDate2': vm.form.qsDate2,
                        'rzhBelongDate': vm.form.rzhBelongDate,
                        'rzhDate1': vm.form.rzhDate1,
                        'rzhDate2': vm.form.rzhDate2,
                        'rebateNo': vm.form.rebateNo,
                        'venderId': vm.form.venderId,
                        'flowType': vm.form.flowType
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
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
        // invoiceDate1Change: function(val) {
        //     vm.form.invoiceDate1 = val;
        // },
        // invoiceDate2Change: function(val) {
        //     vm.form.invoiceDate2 = val;
        // },
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
            vm.detailForm.rebateDate = null;
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
            vm.detailForm.rebateDate = null;
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
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'scanRefund/printRefundInformation/list',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                // $('#totalStatistics').html("合计数量: "+xhr.page.totalCount+"条, 合计金额: "+formatMoney(xhr.totalAmount)+"元, 合计税额: "+formatMoney(xhr.totalTax)+"元");

                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;

                this.totalAmount = formatMoney(xhr.totalAmount);
                this.totalTax = formatMoney(xhr.totalTax);

                this.tableData = xhr.page.list;
                if(this.tableData.length>0){
                    $("#export_btn1").removeAttr("disabled").removeClass("is-disabled");
                }
                this.listLoading = false;
                flag = true;
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
        },
        flowTypeFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        flowTypeBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        func:function(val){
            var obj = [];
            for(var i=0;i<this.options.length;i++){
                var gf = {};
                gf.value = this.options[i].value;
                gf.label = this.options[i].label;
                obj.push(gf);
            }
            vm.options = obj;
            // alert(vm.upform.refundType);
            if(vm.upform.refundType == '商品发票退票'){
                var obj1 = [];
                for(var i=0;i<this.options1.length;i++){
                    var gf1 = {};
                    gf1.value = this.options1[i].value;
                    gf1.label = this.options1[i].label;
                    obj1.push(gf1);
                }
                vm.options1 = obj1;
            }else if (vm.upform.refundType == '红票'){
                var obj2 = [];
                for(var i=0;i<this.options2.length;i++){
                    var gf2 = {};
                    gf2.value = this.options2[i].value;
                    gf2.label = this.options2[i].label;
                    obj2.push(gf2);
                }
                vm.options2 = obj2;
            }
            else if (vm.upform.refundType == '全部'){
                var obj3 = [];
                for(var i=0;i<this.options3.length;i++){
                    var gf3 = {};
                    gf3.value = this.options3[i].value;
                    gf3.label = this.options3[i].label;
                    obj3.push(gf3);
                }
                vm.options3 = obj3;
            }
        },
        func1 : function () {
            if(vm.upform.refundNo == '101'){
                vm.upform.refundReason = '购货单位信息有误';
            }else if (vm.upform.refundNo == '102'){
                vm.upform.refundReason = '发票过期';
            } else if (vm.upform.refundNo == '103'){
                vm.upform.refundReason = '密码区发票号与发票不符';
            } else if (vm.upform.refundNo == '104'){
                vm.upform.refundReason = '非沃尔玛公司发票';
            } else if (vm.upform.refundNo == '105'){
                vm.upform.refundReason = '销货单位信息有误';
            } else if (vm.upform.refundNo == '106'){
                vm.upform.refundReason = '无有效减免税证明';
            } else if (vm.upform.refundNo == '107'){
                vm.upform.refundReason = '发票未盖发票专用章';
            } else if (vm.upform.refundNo == '108'){
                vm.upform.refundReason = '发票无开票人签名';
            } else if (vm.upform.refundNo == '109'){
                vm.upform.refundReason = '欠发票联或抵扣联';
            } else if (vm.upform.refundNo == '110'){
                vm.upform.refundReason = '普票未提供《发票领购簿》中有单位基本信息的首页/该次结款发票的相应领购记录页的复印件（需盖有效章）';
            } else if (vm.upform.refundNo == '111'){
                vm.upform.refundReason = '普票所有联次未一次性复写';
            } else if (vm.upform.refundNo == '112'){
                vm.upform.refundReason = '普票票面信息不合格';
            } else if (vm.upform.refundNo == '113'){
                vm.upform.refundReason = '欠结款清单（需盖章）/结款清单内容有误';
            } else if (vm.upform.refundNo == '114'){
                vm.upform.refundReason = '欠销货清单（需盖章）/销货清单内容有误';
            } else if (vm.upform.refundNo == '115'){
                vm.upform.refundReason = '发票上货物名称、单位、数量、单价、税率等错误';
            } else if (vm.upform.refundNo == '201'){
                vm.upform.refundReason = '金额多开';
            } else if (vm.upform.refundNo == '202'){
                vm.upform.refundReason = '交易或收货已结';
            } else if (vm.upform.refundNo == '203'){
                vm.upform.refundReason = '索赔已扣';
            } else if (vm.upform.refundNo == '204'){
                vm.upform.refundReason = '货款冻结';
            } else if (vm.upform.refundNo == '205'){
                vm.upform.refundReason = '订单已结';
            } else if (vm.upform.refundNo == '206'){
                vm.upform.refundReason = '其他（请说明）';
            } else if (vm.upform.refundNo == '207'){
                vm.upform.refundReason = '相同税率下，不允许多份发票对应多份订单';
            } else if (vm.upform.refundNo == '208'){
                vm.upform.refundReason = '订单或索赔不存在';
            } else if (vm.upform.refundNo == '209'){
                vm.upform.refundReason = '供应商在PIE平台提交的发票信息有误';
            } else if (vm.upform.refundNo == '210'){
                vm.upform.refundReason = '同一发票组号的部分发票未提交实物';
            } else if (vm.upform.refundNo == '211'){
                vm.upform.refundReason = '欠交票清单（需盖章）';
            } else if (vm.upform.refundNo == '212'){
                vm.upform.refundReason = '山姆鲜食订单问题';
            } else if (vm.upform.refundNo == '220'){
                vm.upform.refundReason = '退换票问题';
            } else if (vm.upform.refundNo == '301'){
                vm.upform.refundReason = '采购问题单（不合格资料、采购批复原件退供应商）';
            } else if (vm.upform.refundNo == '302'){
                vm.upform.refundReason = '涉税问题单（不合格资料、多余资料退供应商）';
            } else if (vm.upform.refundNo == '304'){
                vm.upform.refundReason = '自用品请交99部门采购';
            } else if (vm.upform.refundNo == '305'){
                vm.upform.refundReason = 'DDS退回重寄';
            } else if (vm.upform.refundNo == '306'){
                vm.upform.refundReason = '其他（详见附件）';
            }

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
function getSelectedRows() {
    var selection = vm.multipleSelection;
    var ids=[];
    for (var i = 0; i < selection.length; i++) {
        ids.push(selection[i].id);
    };
    return ids;
}



