
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    username:null,
    Code:null,
    gfOrgCode:null,
    invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    invoiceDate3: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    invoiceDate4: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    redTicketDataSerialNumber:'',
    invoiceCode:null,
    invoiceNo:null,
    serviceNo: null,
    goods_name:null,
    invoiceDetails:[],
    redRushDetails:[],
    returnGoods:[],
    page: 1,
    limit: 1
};
var invoiceCode=null;
var invoiceNo=null;
var uuid=null;
var goodsNumber=null;
var vm = new Vue({
    el:'#rrapp',
    data:{
        gf:{
            orgcode:"",
            orgname:"",
            companyCode:'',
            taxno:''
        },
        user:{},
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: 300,
        pageList: [300,400,500],
        pagerCount: 5,
        tableData: [],

        total1: 0,
        currentPage1:1,
        totalPage1: 0,
        pageSize1: 300,
        pageList1: [300,400,500],
        pagerCount1: 5,
        tableData1: [],

        total2: 0,
        currentPage2:1,
        totalPage2: 0,
        pageSize2: 300,
        pageList2: [300,400,500],
        pagerCount2: 5,
        tableData2: [],

        total3: 0,
        currentPage3:1,
        totalPage3: 0,
        pageSize3: 300,
        pageList3: [300,400,500],
        pagerCount3: 5,
        tableData3: [],

        total4: 0,
        currentPage4:1,
        totalPage4: 0,
        pageSize4: 300,
        pageList4: [300,400,500],
        pagerCount4: 5,
        tableData4: [],

        tableData5:[],
        returnGoods:[],


        invoiceDetails:[],
        invoiceDetails1:[],
        invoiceDetailsStorage:[],

        returnNum:0,
        sumReturnAmount:0.00,
        invoiceDetailNum:0,
        sumRedRushAmount:0.00,

        redTicketDataSerialNumber:'',
        invoiceNo:'',
        invoiceCode: '',
        listLoading: false,
        listLoading1: false,
        listLoading2: false,
        totalAmount: 0,
        totalTax: 0,
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        invoiceDateOptions3: {},
        invoiceDateOptions4: {},
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        xfMaxlength: 30,
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            gfName: "-1",
            username:null,
            usercode:null,
            businessType:"1",
            Code:null,
            goods_name:null,
            invoiceDate3: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate4: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
        },
        form1:{
            invoiceNo: null,
            invoiceCode:null,
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
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
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogVehicleFormVisible1: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        rules:{
            invoiceCode:[{
                validator: function (rule, value, callback) {
                    var regex = /^[0-9]{10}$/;
                    var regex2 = /^[0-9]{12}$/;
                    if(value !=null && value != ""){
                        if (!(regex.test(value)||regex2.test(value))) {
                            callback(new Error('必须为10位或12位的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            invoiceNo:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为8位的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            invoiceDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate3: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate4: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.invoiceDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form1.invoiceDate1));
                return time.getTime() >= Date.now();
            }

        };
        this.invoiceDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form1.invoiceDate2));
                return time.getTime() >= Date.now();
            }
        };
        this.invoiceDateOptions3= {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate3));
                return time.getTime() >= Date.now();
            }

        };
        this.invoiceDateOptions4 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate4));
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
        number(row){
            row.redRushNumber=row.redRushNumber.replace('.','');
            row.redRushNumber=row.redRushNumber.replace('。','');
            row.redRushNumber=row.redRushNumber.replace(/^(0|[1-9]\\d)/g,'');
            if(parseInt(row.redRushNumber)>parseInt(row.num)){
                row.redRushNumber=row.num;
            }
        },
        isSelected:function(row,index){
            if (row.redRushAmount!=null) {
                return 1;
            }else {
                return 0;
            }
        },
        generateRedRushData:function() {
            if (vm.tableData5 != null &&vm.sumReturnAmount>0){
                return 1;
            }else{
                return 0;
            }
        },
        changeFunR:function(row){
            var returnSumAmount=0.00;
            vm.returnGoods=row;
            vm.returnNum=vm.returnGoods.length;
            for(var i=0;i<vm.returnGoods.length;i++){
                returnSumAmount=returnSumAmount+vm.returnGoods[i].amount;
            }

            vm.sumReturnAmount=returnSumAmount;
        },
        changeFunFP:function(row) {
            var details=[];
            for(var i=row.length-1;i>=0;i--){
                if (row[i].redRushAmount!=null) {
                    row[i].redRushPrice=row[i].unitPrice;
                    details=details.concat(row[i]);
                }
            }

            vm.invoiceDetails=details;
        },
        redRushNumberInput:function(row) {

            row.redRushAmount=(row.unitPrice*100*row.redRushNumber/100).toFixed(2);

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
            if(vm.form.gfName=="-1"){
                alert("请选择一个购方单位");
                return;
            }
            isInitial = false;
            var checkKPDate = true;
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.form.invoiceDate3);
            var qsEndDate = new Date(vm.form.invoiceDate4);
            var nowtime=format2(new Date().getFullYear()-1)+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());

            if ( new Date(nowtime)>new Date(vm.form.invoiceDate3)) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">协议日期必须在一年以内</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.invoiceDate3)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if(!(checkKPDate )){
                return;
            }
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    if (vm.form.Code==null) {
                        currentQueryParam = {
                            'Code': null
                        };
                    }else{
                        currentQueryParam = {
                            'Code': vm.form.Code

                        };
                    }
                    vm.findAAll(1);
                } else {
                    return false;
                }
            });
        },
        query1: function (formName) {
            if(vm.form.gfName=="-1"){
                alert("请选择一个购方单位");
                return;
            }
            isInitial = false;
            var checkKPDate = true;
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.form1.invoiceDate1);
            var qsEndDate = new Date(vm.form1.invoiceDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);

            if ( qsEndDate.getTime()+1000*60*60*24 > qsStartDate.getTime()) {
                $("#requireMsg3 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form1.invoiceDate1)){
                $("#requireMsg3 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate=false;
            }
            if(!(checkKPDate )){
                return;
            }
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'invoiceNo': vm.form1.invoiceNo,
                        'orgcode':vm.gf.orgcode,
                        'invoiceDate1': vm.form1.invoiceDate1,
                        'invoiceDate2': vm.form1.invoiceDate2,
                        'invoiceCode': vm.form1.invoiceCode
                    };
                    vm.findAll1(1);
                } else {
                    return false;
                }
            });
        },
        query2: function (formName) {
            isInitial = false;
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'goodsName': vm.form.goods_name,
                    };
                    vm.findAll2(1);
                } else {
                    return false;
                }
            });
        },
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },
        query3: function () {
            var nowtime=format2(new Date().getFullYear()-1)+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());
            for(var t=0;t<vm.returnGoods.length;t++){
                if(vm.returnGoods[t].caseDate<nowtime){
                    alert("所选索赔单号含有超过1年的，请重新选择");
                    return false;
                }
            }
            for (var h=0;h<vm.invoiceDetails1.length-1;h++){
                for (var y=vm.invoiceDetails1.length-1;y>h;y--){
                    if(vm.invoiceDetails1[h].taxRate!=vm.invoiceDetails1[y].taxRate){
                        alert("红冲需要选择税率相同的发票明细信息，请重新勾选");
                        return false;
                    }
                }
            }
            if(Math.abs(vm.sumReturnAmount)<=0){
                alert("没有选择协议单号，请重新选择");
                return false;
            }else {
                if (Math.abs(vm.sumRedRushAmount) <= 0) {
                    alert("没有选择红冲明细，请重新选择");
                    return false;
                } else {
                    if (Math.abs(vm.sumReturnAmount) < vm.sumRedRushAmount) {
                        alert("所选红冲的总金额大于所选协议的金额，请重新选择");
                        return false;
                    }
                    else {
                        var RedRushAmount=0;
                        /*for (var i=0;i<vm.invoiceDetails1.length;i++){
                            vm.invoiceDetails1[i].redRushAmount=parseInt(vm.invoiceDetails1[i].redRushNumber)*vm.invoiceDetails1[i].redRushPrice;
                        }
                        for (var k=0;k<vm.tableData5.length;k++){
                            vm.tableData5[k].redRushAmount=parseInt(vm.tableData5[k].redRushNumber)*vm.tableData5[k].redRushPrice;
                        }*/
                        for (var k=0;k<vm.tableData5.length;k++) {
                            RedRushAmount=parseFloat(RedRushAmount)+parseFloat(vm.tableData5[k].redRushAmount);
                        }
                        /*vm.sumRedRushAmount=(vm.sumRedRushAmount/(parseFloat(vm.invoiceDetails1[0].taxRate)/100+1)).toFixed(2);*/
                        currentQueryParam = {
                            'orgcode':vm.gf.orgcode,
                            'invoiceDetails': vm.invoiceDetails1,
                            'redRushDetails': vm.tableData5,
                            'agreementEntities': vm.returnGoods,
                            'sumRedRushAmount': RedRushAmount,
                            "businessType": '2'
                        };
                        var loading = vm.getLoading("生成中...");
                        $.ajax({
                            url: baseURL + 'generateRedTicketData/list/insert',
                            type: "POST",
                            contentType: "application/json",
                            data: JSON.stringify(currentQueryParam),
                            success: function (r) {
                                loading.close();
                                if (r.code == 0) {
                                    if (r.message == "红冲成功") {
                                        alert(r.message, function () {
                                            location.reload();
                                        });
                                    } else {
                                        alert(r.message);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        },
        updateInvoiceDetails: function () {
            var redRushDetailsSumAmount=0;
            vm.form.goods_name="";
            var yesandNo=true;
            var number="";
            vm.invoiceDetails1=vm.invoiceDetails1.concat(vm.invoiceDetails);
            if(vm.invoiceDetails1.length>0){
                for (var i=0;i<vm.invoiceDetails1.length;i++){
                    number=vm.invoiceDetails1[i].redRushNumber;
                    if(number==""){
                        alert("请先输入红冲数量！");
                        yesandNo=false;
                        vm.invoiceDetails=[];
                        vm.invoiceDetails1.splice(i,1);
                        //vm.invoiceDetails1=[];
                    }
               }
                if(!yesandNo){
                    return;
                }
                    vm.detailDialogVehicleFormVisible1 = false;
                    for (var i=0;i<vm.invoiceDetails1.length-1;i++){
                        for (var k=vm.invoiceDetails1.length-1;k>i;k--){
                            if(vm.invoiceDetails1[i].goodsName==vm.invoiceDetails1[k].goodsName&&
                                vm.invoiceDetails1[i].model==vm.invoiceDetails1[k].model&&
                                vm.invoiceDetails1[i].taxRate==vm.invoiceDetails1[k].taxRate&&
                                vm.invoiceDetails1[i].id!=vm.invoiceDetails1[k].id){
                                if(parseFloat(vm.invoiceDetails1[i].unitPrice)>parseFloat(vm.invoiceDetails1[k].unitPrice)){
                                    vm.invoiceDetails1[i].redRushPrice=vm.invoiceDetails1[k].unitPrice;
                                    vm.invoiceDetails1[i].redRushAmount=vm.invoiceDetails1[i].redRushPrice*100*vm.invoiceDetails1[i].redRushNumber/100;
                                }
                                if(parseFloat(vm.invoiceDetails1[i].unitPrice)<parseFloat(vm.invoiceDetails1[k].unitPrice)){
                                    vm.invoiceDetails1[k].redRushPrice=vm.invoiceDetails1[i].unitPrice;
                                    vm.invoiceDetails1[k].redRushAmount=vm.invoiceDetails1[k].redRushPrice*100*vm.invoiceDetails1[k].redRushNumber/100;

                                }
                            }
                        }
                    }
                    vm.invoiceDetailNum=vm.invoiceDetails1.length;
                    var invoiceRedRuahDetails;
                    invoiceRedRuahDetails=deepClone(vm.invoiceDetails1);
                    var tableData5 = vm.invoiceDetails1;
                    for (var z=0;z<tableData5.length-1;z++){
                        for (var j=tableData5.length-1;j>z;j--){
                            if(tableData5[z].goodsName==tableData5[j].goodsName&&
                                tableData5[z].model==tableData5[j].model&&
                                tableData5[z].taxRate==tableData5[j].taxRate&&
                                tableData5[z].id!=tableData5[j].id){
                                tableData5[z].detailAmount=parseFloat(tableData5[z].detailAmount)+parseFloat(tableData5[j].detailAmount);
                                tableData5[z].redRushNumber=parseInt(tableData5[z].redRushNumber)+parseInt(tableData5[j].redRushNumber);
                                tableData5[z].redRushAmount=tableData5[z].redRushPrice*100*tableData5[z].redRushNumber/100;
                                tableData5.splice(j,1);
                            }
                        }
                    }
                    for(var s=0;s<tableData5.length;s++){
                        var redRushAmount=((tableData5[s].redRushPrice*tableData5[s].redRushNumber)*
                            ((parseFloat(tableData5[s].taxRate)/100+1)));
                        var redRushAmount=redRushAmount.toFixed(2);
                        redRushDetailsSumAmount=parseFloat(redRushDetailsSumAmount)+parseFloat(redRushAmount);
                    }
                    vm.sumRedRushAmount=redRushDetailsSumAmount;
                    vm.tableData5=tableData5;
                    vm.invoiceDetails1=invoiceRedRuahDetails;


            }else{
                alert("请先勾选发票明细！");
                return;
            }
        },
        getUser: function () {
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        vm.form.usercode=r.orgEntity.usercode
                        vm.form.username=r.orgEntity.orgname

                    }
                }
            });
        },

        querySearchGf: function () {
            $.get(baseURL + 'report/invoiceProcessingStatusReport/searchGf',function(r){
                var gfs = [];
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
            vm.form1.invoiceDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form1.invoiceDate2 = val;
        },
        invoiceDate3Change: function(val) {
            vm.form.invoiceDate3 = val;
        },
        invoiceDate4Change: function(val) {
            vm.form.invoiceDate4 = val;
        },
        dqskssqDateChange: function(val) {
            vm.form.rzhBelongDate = val;
        },
        gfOrgCode:function(val){
            if(val=='-1'){
                vm.gf.orgcode="";
                vm.gf.orgname="";
                return;
            }
            currentQueryParam = {
                'taxno': val
            };
            $.ajax({
                url: baseURL + 'gfOrg/list/query',
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(currentQueryParam),
                success: function (r) {
                    if (r.code==0){
                        vm.gf = r.list;
                    }
                }
            });
        },
        /*qsChange: function (value) {
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
        },*/
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
        detailFormCancel1: function () {
            vm.form.goods_name="";
            vm.detailDialogVehicleFormVisible1 = false;
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
            this.findAAll(currentPage);
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
            this.findAll1(currentPage3);
        },
        currentChange4: function(currentPage4){
            if(vm.total4==0){
                return;
            }
            this.findAll2(currentPage4);
        },
        findAll4: function (currentPage4) {
            currentQueryParam.page = currentPage4;
            currentQueryParam.limit = vm.pageSize4;
            currentQueryParam.redTicketDataSerialNumber = vm.redTicketDataSerialNumber;
            currentQueryParam.invoiceCode = vm.invoiceCode;
            currentQueryParam.invoiceNo = vm.invoiceNo;


            var flag = false;
            if (!isNaN(currentPage4)) {
                this.currentPage4 = currentPage4;
            }
            this.$http.post(baseURL + 'modules/openRedTicket/list/queryByInvoiceCodeAndNo',
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
        updateDetails:function(currentPage5){
            currentQueryParam.page = currentPage5;
            currentQueryParam.limit = vm.pageSize5;
            $.ajax({
                url: baseURL + 'invoiceOut/details/redRushInvoiceDetails',
                type: "POST",
                contentType: "application/json",
                data: JSON.stringify(currentQueryParam),
                success: function (r) {
                    if (r.code==0){

                        vm.detailDialogVehicleFormVisible1 = false;
                        console.log(r);
                        vm.total5= r.page6.totalCount;
                        vm.currentPage5 = r.page6.currPage;
                        vm.totalPage5 = r.page6.totalPage;
                        vm.tableData5 = r.page6.list;
                        vm.invoiceDetails1.concat(r.page6.list);
                        console.log(vm.invoiceDetails1);
                    }
                }
            });
        },
        findAll2: function (currentPage4) {
            currentQueryParam.invoiceCode=vm.invoiceCode;
            currentQueryParam.invoiceNo=vm.invoiceNo;
            currentQueryParam.page = currentPage4;
            currentQueryParam.limit = vm.pageSize4;
            this.listLoading2 = true;
            var flag = false;
            this.$http.post(baseURL + 'modules/canRedRushInvoiceDetails/list/query',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.tableData4 = xhr.page4.list;
                this.listLoading2 = false;
            });
        },
        findAll1: function (currentPage3) {
            currentQueryParam.page = currentPage3;
            currentQueryParam.limit = vm.pageSize3;
            this.listLoading1 = true;
            var flag = false;
            this.$http.post(baseURL + 'redTicket/agreementRedTicketInformation/invoicelist',
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
                /*for(var i=0;i<xhr.page3.list.length;i++){
                    this.tableData3[i].actualredMoneyAmount=(xhr.page3.list[i].redMoneyAmount*100/100)-(xhr.page3.list[i].settlementAmount*100/100*0.6);
                }*/
                this.listLoading1 = false;
                $('.redrush').removeClass("hideItem");
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
        findAAll: function (currentPage) {
            var params = {
                page: currentPage,
                limit: this.pageSize,
                agreementCode:currentQueryParam.Code,
                payCompanyCode:vm.gf.orgcode,
                invoiceDate3: vm.form.invoiceDate3,
                invoiceDate4: vm.form.invoiceDate4,
            };
            this.listLoading = true;
            var flag = false;
            this.$http.post(baseURL + 'redTicket/agreementRedTicketInformation/protocollist',
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
                this.tableData2 = xhr.page.list;
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
                this.findAAll(1);
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
                this.findAll1(1);
            }
        },
        handleSizeChange4: function (val) {
            this.pageSize4 = val;
            if(!isInitial) {
                this.findAll2(1);
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
        numberFormat1: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
                return "—— ——";
            }
            return cellValue;

        },
        detail:function(row){
            var  value = row.protocolNo;

            vm.detailDialogVehicleFormVisible = true;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            var params = {
                'protocolNo': value,
                'page': vm.currentPage1,
                'limit': vm.pageSize1
            };
                this.$http.post(baseURL + 'redTicket/agreementRedTicketInformation/protocoldetaillist',
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

                });


        },
        detail1:function(row){
            vm.invoiceCode = row.invoiceCode;
            vm.invoiceNo = row.invoiceNo;
            vm.invoice=row;
            vm.detailDialogVehicleFormVisible1 = true;
            var params = {
                'invoiceCode': vm.invoiceCode,
                'invoiceNo':vm.invoiceNo,
                'page': vm.currentPage4,
                'limit': vm.pageSize4
            };
            this.$http.post(baseURL + 'modules/canRedRushInvoiceDetails/list/query',
                params,
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
                for (var i=this.tableData4.length-1;i>=0;i--){
                    for (var k=0;k<vm.invoiceDetails1.length;k++){
                        if(this.tableData4[i].id==vm.invoiceDetails1[k].id){
                            this.tableData4.splice(i,1)
                        }

                    }
                }
            });

        },
        detail2:function(row){
            for (var k=0;k<vm.invoiceDetails1.length;k++){
                if(vm.invoiceDetails1[k].goodsName==row.goodsName&&
                    vm.invoiceDetails1[k].model==row.model&&
                    vm.invoiceDetails1[k].taxRate==row.taxRate){
                    var redRushAmount=((vm.invoiceDetails1[k].redRushPrice*vm.invoiceDetails1[k].redRushNumber)*
                        ((parseFloat(vm.invoiceDetails1[k].taxRate)/100+1)));
                    redRushAmount=Math.floor(redRushAmount*100)/100;
                    vm.sumRedRushAmount=parseFloat(vm.sumRedRushAmount)-parseFloat(redRushAmount);
                    vm.invoiceDetails1.splice(k,1);
                }
            }
            vm.invoiceDetailNum=vm.invoiceDetails1.length;
            var invoiceRedRuahDetails;
            invoiceRedRuahDetails=deepClone(vm.invoiceDetails1);
            var tableData5 = vm.invoiceDetails1;
            for (var z=0;z<tableData5.length-1;z++){
                for (var j=tableData5.length-1;j>z;j--){
                    if(tableData5[z].goodsName==tableData5[j].goodsName&&
                        tableData5[z].model==tableData5[j].model&&
                        tableData5[z].taxRate==tableData5[j].taxRate&&
                        tableData5[z].id!=tableData5[j].id){
                        tableData5[z].detailAmount=parseFloat(tableData5[z].detailAmount)+parseFloat(tableData5[j].detailAmount);
                        tableData5[z].redRushNumber=parseInt(tableData5[z].redRushNumber)+parseInt(tableData5[j].redRushNumber);
                        tableData5[z].redRushAmount=tableData5[z].redRushPrice*100*tableData5[z].redRushNumber/100;
                        tableData5.splice(j,1);
                    }
                }
            }
            vm.tableData5=tableData5;
            vm.invoiceDetails1=invoiceRedRuahDetails;
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
    created: function () {
        this.getUser();
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
function deepClone(obj){
    var _obj = JSON.stringify(obj),
        objClone = JSON.parse(_obj);
    return objClone
}