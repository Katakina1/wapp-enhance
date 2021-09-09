
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
    venderid: null,
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
    venderid:'',
    serviceNo: null,
    userCode: null,
    id: '',
    page: 1,
    limit: 1
};


var vm = new Vue({
    el:'#rrapp',
    data:{
        importLoading : false,
        filePath:'',
        isNeedFileExtension: false,
        fileSizeIsFit: false,
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
        venderid:'',
        gfTaxNo:'',
        redTotalAmount:'',
        companyCode:'',
        jvcode:'',
        taxRate1:'',

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
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
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
        this.invoiceDateOptions3 = {
            disabledDate: function(time){
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions4 = {
            disabledDate: function(time){
                var currentTime = new Date();
                return time.getTime() >= currentTime;
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
                'serviceNo': vm.form.serviceNo
            };
            this.listLoading = true;
            vm.findAll(1);


        },


        enterRedTicket:function(row){
            vm.redTicketMatchId = row.id;
            vm.redNoticeNumber = row.redNoticeNumber;
            vm.redTotalAmount = row.redTotalAmount;
            vm.redTicketDataSerialNumber = row.redTicketDataSerialNumber;
            vm.businessType = row.businessType;
            vm.taxRate1 = row.taxRate;
            vm.venderid = row.venderid;
            vm.gfTaxNo = row.gfTaxNo;
            vm.jvcode = row.jvcode;
            vm.companyCode = row.companyCode;
            vm.enterRedTicketDialog = true;


        },
        invoiceDate3Change: function (val) {
            vm.invoicequery.invoiceDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.invoiceUpdate.invoiceDateStart = val;
        },
        codeIn:function(){
            if(this.invoicequery.invoiceCode!=null &&this.invoicequery.invoiceCode!=''&&this.invoicequery.invoiceNo!=null && this.invoicequery.invoiceNo!=''){
                var currentInvoiceParam1 = {
                    invoiceNo: vm.invoicequery.invoiceNo,
                    invoiceCode:vm.invoicequery.invoiceCode,
                    redTicketDataSerialNumber:vm.redTicketDataSerialNumber,
                    businessType:vm.businessType,
                    gfTaxNo:vm.gfTaxNo,
                    redNoticeNumber:vm.redNoticeNumber,
                    redTotalAmount:vm.redTotalAmount,
                    jvcode:vm.jvcode,
                    taxRate1:vm.taxRate1,
                    companyCode:vm.companyCode

                     //invoiceDate: vm.invoicequery.invoiceDateStart,
                    // invoiceDate: vm.invoicequery.invoiceDateStart,
                    // invoiceAmount:vm.invoicequery.invoiceAmount,
                    // totalAmount:vm.invoicequery.totalAmount,
                     //taxAmount:vm.invoicequery.taxAmount,
                     //taxRate:vm.invoicequery.taxRate
                    //venderid:vm.queryData1.usercode,
                    //jvcode:vm.queryData1.orgcode,
                    //gfName:vm.queryData1.gfName

                };
                $.ajax({
                    url:baseURL + 'modules/redTicket/invoice/query',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    /*async:false,*/
                    data:JSON.stringify(currentInvoiceParam1),
                    success:function (data) {
                        if(data.msg!=null && data.msg!=''){
                            alert(data.msg);
                            if(data.msg!='该发票已经代出'){
                                vm.invoicequery.invoiceCode=null;
                                vm.invoicequery.invoiceNo=null;
                            }

                        }
                        if(data.msg=='该发票已经代出'){
                            vm.findAll(vm.currentPage);
                            vm.enterRedTicketDialog = false;
                            vm.invoicequery.invoiceCode=null;
                            vm.invoicequery.invoiceNo=null;
                            vm.invoicequery.invoiceAmount=null;
                            vm.invoicequery.totalAmount=null;
                            vm.invoicequery.taxAmount=null;
                            vm.invoicequery.taxRate=null;
                            vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+format2(new Date().getDate());
                            vm.listLoading = false;
                        }


                    }
                });
            }
        },
        codeIn2:function(){
            if(this.invoiceUpdate.invoiceCode!=null &&this.invoiceUpdate.invoiceCode!=''&&this.invoiceUpdate.invoiceNo!=null && this.invoiceUpdate.invoiceNo!=''){
                var currentInvoiceParam1 = {
                    invoiceNo: vm.invoiceUpdate.invoiceNo,
                    invoiceCode:vm.invoiceUpdate.invoiceCode,
                    businessType:vm.businessType,
                    redTicketDataSerialNumber:vm.redTicketDataSerialNumber,
                    gfTaxNo:vm.gfTaxNo,
                    redNoticeNumber:vm.redNoticeNumber,
                    venderid:vm.venderid,
                    taxRate1:vm.taxRate1,
                    redTotalAmount:vm.redTotalAmount
                    // invoiceDate: vm.invoicequery.invoiceDateStart,
                    // invoiceAmount:vm.invoicequery.invoiceAmount,
                    // totalAmount:vm.invoicequery.totalAmount,
                    //taxAmount:vm.invoiceUpdate.taxAmount,
                    //taxRate:vm.invoiceUpdate.taxRate
                    //venderid:vm.queryData1.usercode,
                    //jvcode:vm.queryData1.orgcode,
                    //gfName:vm.queryData1.gfName

                };
                $.ajax({
                    url:baseURL + 'modules/redTicket/invoice/query',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    /*async:false,*/
                    data:JSON.stringify(currentInvoiceParam1),
                    success:function (data) {

                        if(data.msg!=null && data.msg!=''){
                            alert(data.msg);
                        }
                        if(data.msg=='该发票已经代出'){
                            vm.findAll(vm.currentPage);
                            vm.updateRedTicketDialog = false;
                            vm.invoiceUpdate.invoiceCode=null;
                            vm.invoiceUpdate.invoiceNo=null;
                            vm.invoiceUpdate.invoiceAmount=null;
                            vm.invoiceUpdate.totalAmount=null;
                            vm.invoiceUpdate.taxAmount=null;
                            vm.invoiceUpdate.taxRate=null;
                            vm.invoiceUpdate.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1)+"-" +format2(new Date().getDate());
                            vm.listLoading = false;
                        }


                    }
                });
            }
        },
        importFormCancel: function () {
            vm.batchRedTicketDialog = false;
            vm.findAll(vm.currentPage);
        },
        batchExport:function(){
            vm.batchRedTicketDialog = true;
        },
        updateRedTicketDialogCancel:function(){
            vm.$refs['invoiceUpdate'].clearValidate();
            vm.updateRedTicketDialog = false;
        },
        updateRedTicket:function(row){



            vm.redNoticeNumber= row.redNoticeNumber;
            vm.redTicketDataSerialNumber= row.redTicketDataSerialNumber;
            vm.businessType = row.businessType;
            vm.gfTaxNo = row.gfTaxNo;
            vm.venderid = row.venderid;
            vm.taxRate1 = row.taxRate;
            vm.redTotalAmount = row.redTotalAmount;
            vm.redTicketMatchId= row.id;
            vm.jvcode = row.jvcode;
            vm.companyCode = row.companyCode;
            var params = {
                'id': row.id
            };
            this.$http.post(baseURL + 'modules/redTicket/selectRedTicketById',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                vm.invoiceUpdate.invoiceNo =  xhr.invoiceEntity.invoiceNo;
                vm.invoiceUpdate.invoiceAmount =  xhr.invoiceEntity.invoiceAmount;
                vm.invoiceUpdate.taxRate =  xhr.invoiceEntity.taxRate.toString();
                vm.invoiceUpdate.taxAmount =  xhr.invoiceEntity.taxAmount;
                vm.invoiceUpdate.invoiceCode =  xhr.invoiceEntity.invoiceCode;
                vm.invoiceUpdate.invoiceDateStart =  xhr.invoiceEntity.invoiceDate;
                vm.invoiceUpdate.totalAmount =  xhr.invoiceEntity.totalAmount;

            });
            vm.updateRedTicketDialog = true;
        },
        saveRedTicket:function(){
            vm.$refs['invoicequery'].validate(function (valid) {
                if (valid) {
                    var params = {
                        'businessType':vm.businessType,
                        'redTicketDataSerialNumber': vm.redTicketDataSerialNumber,
                        'id': vm.redTicketMatchId,
                        'redNoticeNumber': vm.redNoticeNumber,
                        'invoiceNo': vm.invoicequery.invoiceNo,
                        'invoiceCode': vm.invoicequery.invoiceCode,
                        'gfTaxNo': vm.gfTaxNo,
                       // 'checkNo': vm.invoicequery.checkNo,
                        'invoiceAmount': vm.invoicequery.invoiceAmount,
                        'venderid': vm.venderid,
                        'flag1': "b",
                        'jvcode': vm.jvcode,
                        'redTotalAmount': vm.redTotalAmount,
                        'companyCode': vm.companyCode,
                        'taxRate1':vm.taxRate1,
                        'taxRate': vm.invoicequery.taxRate,
                        'taxAmount': vm.invoicequery.taxAmount,
                        'totalAmount': vm.invoicequery.totalAmount,
                        'invoiceDate': vm.invoicequery.invoiceDateStart
                    };


                    vm.$http.post(baseURL + 'modules/redTicket/saveRedTicket',
                        params,
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (data) {

                        if (data.body.msg=='SUCCESS') {
                            alert("保存成功");
                            vm.enterRedTicketDialog = false;
                            vm.findAll(vm.currentPage);
                            vm.invoicequery.invoiceCode=null;
                            vm.invoicequery.invoiceNo=null;
                            vm.invoicequery.invoiceAmount=null;
                            vm.invoicequery.totalAmount=null;
                            vm.invoicequery.taxAmount=null;
                            vm.invoicequery.taxRate=null;
                            vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1)+"-" +format2(new Date().getDate());
                            vm.listLoading = false;

                        } else {
                            alert(data.body.msg);
                            vm.invoicequery.invoiceCode=null;
                            vm.invoicequery.invoiceNo=null;
                            vm.invoicequery.invoiceAmount=null;
                            vm.invoicequery.totalAmount=null;
                            vm.invoicequery.taxAmount=null;
                            vm.invoicequery.taxRate=null;
                            vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+format2(new Date().getDate());
                            vm.$refs['invoicequery'].clearValidate();
                        }

                    });
                }else {
                    return false;
                }
            })
        },
        updateRedTicketSave:function(){
            vm.$refs['invoiceUpdate'].validate(function (valid) {
                if (valid) {
                    var params = {
                        'businessType':vm.businessType,
                        'redTicketDataSerialNumber': vm.redTicketDataSerialNumber,
                        'id': vm.redTicketMatchId,
                        'redNoticeNumber': vm.redNoticeNumber,
                        'invoiceNo': vm.invoiceUpdate.invoiceNo,
                        'invoiceCode': vm.invoiceUpdate.invoiceCode,
                        'invoiceAmount': vm.invoiceUpdate.invoiceAmount,
                        'taxRate1':vm.taxRate1,
                        'taxRate': vm.invoiceUpdate.taxRate,
                        'totalAmount': vm.invoiceUpdate.totalAmount,
                        'taxAmount': vm.invoiceUpdate.taxAmount,
                        'invoiceDate': vm.invoiceUpdate.invoiceDateStart,
                        'venderid': vm.venderid,
                        'gfTaxNo': vm.gfTaxNo,
                        'jvcode': vm.jvcode,
                        'redTotalAmount': vm.redTotalAmount,
                        'companyCode': vm.companyCode,
                        'flag1': 'a'
                    };


                    vm.$http.post(baseURL + 'modules/redTicket/saveRedTicket',
                        params,
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (data) {

                        if (data.body.msg=='SUCCESS') {
                            alert("修改成功！");
                            vm.updateRedTicketDialog = false;
                            vm.findAll(vm.currentPage);
                            vm.invoiceUpdate.invoiceCode=null;
                            vm.invoiceUpdate.invoiceNo=null;
                            vm.invoiceUpdate.invoiceAmount=null;
                            vm.invoiceUpdate.totalAmount=null;
                            vm.invoiceUpdate.taxAmount=null;
                            vm.invoiceUpdate.taxRate=null;
                            vm.invoiceUpdate.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1)+ "-" +format2(new Date().getDate());
                            vm.listLoading = false;
                        } else {
                            alert(data.body.msg);
                            vm.invoiceUpdate.invoiceCode=null;
                            vm.invoiceUpdate.invoiceNo=null;
                            vm.invoiceUpdate.invoiceAmount=null;
                            vm.invoiceUpdate.totalAmount=null;
                            vm.invoiceUpdate.taxAmount=null;
                            vm.invoiceUpdate.taxRate=null;
                            vm.invoiceUpdate.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+format2(new Date().getDate());
                            vm.$refs['invoiceUpdate'].clearValidate();
                        }

                    });
                }else {
                    return false;
                }
            })
        },
        resetRedTicket:function(){
            vm.$refs['invoicequery'].clearValidate();
            vm.invoicequery.invoiceNo = '';
            vm.invoicequery.invoiceCode = '';
            vm.invoicequery.invoiceAmount = '';
            vm.invoicequery.taxRate = '';
            vm.invoicequery.totalAmount = '';
            vm.invoicequery.taxAmount = '';
            vm.invoicequery.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+ format2(new Date().getDate());


        },
        resetRedTicketUpdate:function(){
            vm.$refs['invoiceUpdate'].clearValidate();
            vm.invoiceUpdate.invoiceNo = '';
            vm.invoiceUpdate.invoiceCode = '';
            vm.invoiceUpdate.invoiceAmount = '';
            vm.invoiceUpdate.taxRate = '';
            vm.invoiceUpdate.taxAmount = '';
            vm.invoiceUpdate.totalAmount = '';
            vm.invoiceUpdate.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+ format2(new Date().getDate());
        },
        enterRedTicketDialogCancel:function(){
            vm.enterRedTicketDialog = false;
            vm.findAll(vm.currentPage);
            vm.$refs['invoicequery'].clearValidate();
            vm.invoicequery.invoiceNo = '';
            vm.invoicequery.invoiceCode = '';
            vm.invoicequery.taxAmount = '';
            vm.invoicequery.invoiceAmount = '';
            vm.invoicequery.taxRate = '';
            vm.invoicequery.totalAmount = '';
            vm.invoicequery.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) +"-"+ format2(new Date().getDate());
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
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;

            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'modules/redTicket/selectRedTicketList',
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
                        document.getElementById("get_image_area").src = '/dxhy-gylpt/electron/getImageForAll?id=' + id + "&token=" + token;
                    },
                    error: function () {

                    }
                });
            }
        },
        showImg:function(row){
            vm.selectDetailDialogPicture = true;
            var  id = row.id;
            vm.imgWin = true;
            $('#invoiceImg').attr('src', "");
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("invoiceImg").src = baseURL + 'modules/openRedTicket/getImageForAll?id=' + id + "&token=" + token;
                },
                error: function () {

                }
            });
        },
        downLoadFileToken:function(row){
            var  id = row.id;
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src== baseURL + 'modules/downLoadFile?id=' + id + "&token=" + token;
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
        //查看上传资料
        checkData:function(row){
            var  dataAssociation = row.dataAssociation;
            vm.detailDialogPicture = true;
            var para = {
                'dataAssociation': dataAssociation
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
        /**
         * 选择文件事件
         * @param event 事件触发点
         * @return {boolean}
         */
        exportData:function(){
            document.getElementById("ifile").src = baseURL + "export/redTicket/invoiceImportExport";
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                $("#file").html("");
                this.isNeedFileExtension = false;
                return false;
            } else {
                this.selectFileFlag = '';
                this.file = '';
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    this.file = event.target.files[0];
                    this.isNeedFileExtension = true;
                    //截取名称最后18位
                    this.selectFileFlag = event.target.files[0].name;
                }
            }
        },

        uploadFile: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    $("#file").html("");
                    return;
                }
                vm.importLoading = true;
                allDataList=[];
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                //this.tempTableData = this.tableData;
                //this.tableData = [];
                this.listLoading = true;
                var flag = false;
                var hh;
                var url = baseURL + "modules/redTicket/invoiceImport";
                this.$http.post(url, formData, config).then(function (response) {
                    $("#file").html("");
                    this.listLoading = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    this.selectFileFlag = '';
                    this.file = '';
                    vm.importLoading = false;
                    if(response.data.errorCount>0){
                        alert("请检查数据是否完整！");
                        return;
                    }
                    if(response.data.errorCount5>0){
                        alert("导入数据超过500条，请修改模板！");
                        return;
                    }
                    if (response.data.success) {
                        vm.batchRedTicketDialog = false;
                        var error1='';
                        for (var i = 0; i < response.data.errorEntityList1.length; i++) {
                           error1 += response.data.errorEntityList1[i].redNoticeNumber+","
                        }
                        var error2='';
                        for (var i = 0; i < response.data.errorEntityList2.length; i++) {
                           error2 += response.data.errorEntityList2[i].redNoticeNumber+","
                        }
                        var error3='';
                        for (var i = 0; i < response.data.errorEntityList3.length; i++) {
                           error3 += response.data.errorEntityList3[i].redNoticeNumber+","
                        }
                        var error4='';
                        for (var i = 0; i < response.data.errorEntityList4.length; i++) {
                           error4 += response.data.errorEntityList4[i].redNoticeNumber+","
                        }
                        for (var i = 0; i < response.data.reason.length; i++) {
                            // if (dataList.length > 0 && this.contains(dataList, response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo)) {
                            //     response.data.reason[i].noAuthTip = "0";
                            //     allDataList.push(response.data.reason[i])
                            // } else {
                            //     dataList.push(response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo);
                            //     allDataList.push(response.data.reason[i])
                            // }
                            //allDataList.push(response.data.reason[i])
                        }
                        //this.tableData = allDataList;
                        // alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条,失败" + response.data.errorCount + "条");
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount+ response.data.errorCount1+ response.data.errorCount2+ response.data.errorCount3+response.data.errorCount4) + "条，成功" + response.data.reason.length + "条,税号不对应"+response.data.errorCount1+"条，红字通知单号"+error1+"excel重复条数"+
                            response.data.errorCount2+"条，红字通知单号"+error2+"金额错误的条数"+response.data.errorCount3+"条，红字通知单号"+error3+"红字通单错误的条数"+response.data.errorCount4+"条，红通知单号为："+error4);
                    } else {
                       //this.tableData = this.tempTableData;
                        //alert(response.data.reason);
                    }
                }/*(err) => {
                    this.tableData = this.tempTableData;
                this.listLoading = false;
                if (err.status == 408) {
                    alert(err.statusText);
                }
            }*/)
                /*var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);*/
            }
        },
       /* onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                this.isNeedFileExtension = false;
                var str ='';
                return false;
            } else {
                this.selectFileFlag = '';
                var meFile = event.target.files[0];
                this.file = '';
                if (event != undefined && meFile != null && meFile != '') {
                    this.file = event.target.files[0];
                    this.isNeedFileExtension = true;
                    //截取名称最后18位
                    this.selectFileFlag = event.target.files[0].name;
                }
            }
        },*/
        /**
         * 上传选择的文件
         * @param event
         */
       /* uploadFile: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                vm.listLoading4=true;
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                //formData.append('gfName',this.queryData1.gfName)
                //formData.append('jvcode',this.queryData1.orgcode)
                //formData.append('venderid',this.queryData1.usercode)
                //formData.append('venderName',this.queryData1.username)

                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };


                var flag = false;
                var hh;
                var url = baseURL + "modules/redTicket/invoiceImport";
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    // vm.file = '';
                    if (response.data.success) {
                        alert("批量导入成功！")
                        /!*if(vm.invoiceData.length>0) {
                            response.data.invoiceQueryList.forEach(function (object, index) {
                                if (response.data.invoiceQueryList.length > 0) {
                                    var type=true;
                                    vm.invoiceData.forEach(function (object, index1) {
                                        if (response.data.invoiceQueryList[index].uuid == object.uuid) {
                                            type=false;
                                            return;
                                        }
                                    })
                                    if(type){                                    vm.invoiceData = vm.invoiceData.concat(response.data.invoiceQueryList[index]);
                                    }
                                }
                            })

                        }/!*else {
                            vm.invoiceData = vm.invoiceData.concat(response.data.invoiceQueryList);

                        }*!/


                        vm.invoiceAmount=0.00;
                        vm.invoiceNum=vm.invoiceData.length;
                        vm.invoiceData.forEach(function (object,index) {
                            vm.invoiceAmount+=object.invoiceAmount;
                        })
                        vm.invoiceAmount=parseFloat(vm.invoiceAmount).toFixed(2);
                        vm.importDialogFormVisible = false;
                        $("#file").html("");*!/
                    } else {
                        vm.importDialogFormVisible = false;
                        $("#file").html("");
                        alert(response.data.reason);
                    }
                }, function(err) {

                    if (err.status == 408) {
                        vm.importDialogFormVisible = false;
                        alert(response.data.reason);
                    }
                })
                /!*var intervelId = setInterval(function () {
                    if (flag) {
                        hh = $(document).height();
                        $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                        clearInterval(intervelId);
                        return;
                    }
                }, 50);*!/
            }
        },*/
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
        },
        showSelectFileWin:function() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
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
// function showSelectFileWin() {
//     $("#file").val("");
//     $("#file").click();
// }

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