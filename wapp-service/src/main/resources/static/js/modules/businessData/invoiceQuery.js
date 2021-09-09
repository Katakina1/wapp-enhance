
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    gfName: "***",
    xfName: null,
    invoiceNo: null,
    invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    invoiceStatus: "-1",
    invoiceType: "-1",
    qsStatus: "-1",
    rzhYesorno: "-1",
    qsType: "-1",
    hoststatus:"-1",
    qsDate1: null,
    qsDate2: null,
    rzhBelongDate: null,
    rzhDate1: null,
    rzhDate2: null,
    bbindingno:'',
    packingno:'',
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
        tableData5: [],
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
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            gfName: "-1",
            xfName: null,
            venderid:null,
            invoiceNo: null,
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceStatus: "-1",
            hoststatus:"-1",
            invoiceType: "-2",
            qsStatus: "-1",
            rzhYesorno: "-1",
            qsType: "-1",
            qsDate1: null,
            qsDate2: null,
            rzhBelongDate: null,
            rzhDate1: null,
            rzhDate2: null
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
        trackInvoiceStatusDialog:false,

        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
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
            invoiceDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.queryDetail();
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
            var checkKPDate = true;
            var checkQSDate = true;
            var checkRZDate = true;

            var qsStartDate = new Date(vm.form.invoiceDate1);
            var qsEndDate = new Date(vm.form.invoiceDate2);
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
            }else if(qsEndDate.getTime() < new Date(vm.form.invoiceDate1)){
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
                        'invoiceDate1': vm.form.invoiceDate1,
                        'invoiceDate2': vm.form.invoiceDate2,
                        'invoiceStatus': vm.form.invoiceStatus,
                        'invoiceType': vm.form.invoiceType,
                        'dxhyMatchStatus': vm.form.qsStatus,
                        'rzhYesorno': vm.form.rzhYesorno,
                        'hoststatus':vm.form.hoststatus,
                        // 'qsType': vm.form.qsType,
                        'matchDateStart': vm.form.qsDate1,
                        'matchDateEnd': vm.form.qsDate2,
                        'rzhBelongDate': vm.form.rzhBelongDate,
                        'rzhDate1': vm.form.rzhDate1,
                        'rzhDate2': vm.form.rzhDate2,
                        'venderno':vm.form.venderid,
                        'bbindingno':'',
                        'packingno':''
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
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
            if(value=="2" || value=="3" || value=="4" || value=="5" || value=="6" ){
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
        queryDetail:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.form.venderid=r.orgEntity.usercode;
                        // vm.queryData1.username=r.orgEntity.username

                    }
                }
            });
        },
        exportExcel: function(){
            currentQueryParam = {
                'gfName': vm.form.gfName,
                'xfName': vm.form.xfName,
                'invoiceNo': vm.form.invoiceNo,
                'invoiceDate1': vm.form.invoiceDate1,
                'invoiceDate2': vm.form.invoiceDate2,
                'invoiceStatus': vm.form.invoiceStatus,
                'invoiceType': vm.form.invoiceType,
                'dxhyMatchStatus': vm.form.qsStatus,
                'rzhYesorno': vm.form.rzhYesorno,
                'hoststatus':vm.form.hoststatus,
                // 'qsType': vm.form.qsType,
                'matchDateStart': vm.form.qsDate1,
                'matchDateEnd': vm.form.qsDate2,
                'rzhBelongDate': vm.form.rzhBelongDate,
                'rzhDate1': vm.form.rzhDate1,
                'rzhDate2': vm.form.rzhDate2,
                'venderno':vm.form.venderid,
                'bbindingno':'',
                'packingno':''
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':11,'condition':JSON.stringify(currentQueryParam)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");
                    }

                }
            });
            $("#export_btn").attr("disabled","true").addClass("is-disabled");
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
            if(currentQueryParam.venderno==""||currentQueryParam.venderno==null){
                alert("参数错误,请重新刷新页面！");
                this.listLoading = false;
                return;
            }
            this.$http.post(baseURL + 'report/comprehensiveInvoiceQuery/list',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                    var xhr = res.body;
                    $('#totalStatistics').html("合计数量: "+xhr.page.totalCount+"条, 合计金额: "+formatMoney(xhr.totalAmount)+"元, 合计税额: "+formatMoney(xhr.totalTax)+"元");

                    this.total = xhr.page.totalCount;
                    this.currentPage = xhr.page.currPage;
                    this.totalPage = xhr.page.totalPage;

                    this.totalAmount = formatMoney(xhr.totalAmount);
                    this.totalTax = formatMoney(xhr.totalTax);

                    this.tableData = xhr.page.list;
                    this.listLoading = false;

                if(this.tableData == null || this.tableData.length == 0){
                    $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
                    $("#export_btnMX").attr("disabled","disabled").addClass("is-disabled");
                    $("#export_btnSL").attr("disabled","disabled").addClass("is-disabled");

                    if(currentQueryParam.qsStatus=="1"){
                        $(".btn-row3 > #export_btnSL").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row3 > #export_btnSL").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1" && currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row4 > #export_btnSL").attr("disabled","disabled").addClass("is-disabled");
                    }

                    if(currentQueryParam.qsStatus=="1"){
                        $(".btn-row3 > #export_btnMX").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row3 > #export_btnMX").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1" && currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row4 > #export_btnMX").attr("disabled","disabled").addClass("is-disabled");
                    }

                    if(currentQueryParam.qsStatus=="1"){
                        // $(".btn-row3 > #export_btn").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.rzhYesorno=="1"){
                        // $(".btn-row3 > #export_btn").attr("disabled","disabled").addClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1" && currentQueryParam.rzhYesorno=="1"){
                        // $(".btn-row4 > #export_btn").attr("disabled","disabled").addClass("is-disabled");
                    }

                }else{
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                    $("#export_btnMX").removeAttr("disabled").removeClass("is-disabled");
                    $("#export_btnSL").removeAttr("disabled").removeClass("is-disabled");


                    if(currentQueryParam.qsStatus=="1"){
                        $(".btn-row3 > #export_btnSL").removeAttr("disabled").removeClass("is-disabled");
                    }
                    if(currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row3 > #export_btnSL").removeAttr("disabled").removeClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1" && currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row4 > #export_btnSL").removeAttr("disabled").removeClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1"){
                         $(".btn-row3 > #export_btnMX").removeAttr("disabled").removeClass("is-disabled");
                    }
                    if(currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row3 > #export_btnMX").removeAttr("disabled").removeClass("is-disabled");
                    }
                    if(currentQueryParam.qsStatus=="1" && currentQueryParam.rzhYesorno=="1"){
                        $(".btn-row4 > #export_btnMX").removeAttr("disabled").removeClass("is-disabled");
                    }

                        $(".btn-row3 > #export_btn").removeAttr("disabled").removeClass("is-disabled");


                        $(".btn-row3 > #export_btn").removeAttr("disabled").removeClass("is-disabled");


                        $(".btn-row4 > #export_btn").removeAttr("disabled").removeClass("is-disabled");

                }
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
        numberFormat1: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
                return "—— ——";
            }
            var number = cellValue;
            var decimals = 0;
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
            return s.join(dec)+"%";
        },
        
      
        detail:function(row){
        	//alert(row)
            var value = row.id;
        	//alert(value)
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            $.ajax({
                type: "POST",
                url: baseURL + "transferOut/detailQuery/invoiceDetail",
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
                    /*if(vm.detailForm.invoiceStatus=='正常'){
                     $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
                     alert('normol');
                     }else{
                     alert('error')
                     }*/
                }
            });
        },
        trackInvoiceStatusClose:function(){
            vm.trackInvoiceStatusDialog=false;
        },
        //跟踪发票状态
        trackInvoiceStatus:function(row){
            vm.trackInvoiceStatusDialog = true;
            vm.tableData5= [];
            if(row.invoiceDate!=null ) {
                vm.tableData5.push({
                    invoiceDate: row.invoiceDate,
                    invoiceStatus: '开票'
                });
            }

            if(row.matchDate!=null && row.dxhyMatchStatus !='') {
                vm.tableData5.push({
                    invoiceDate: row.matchDate,
                    invoiceStatus: dxhyMatchStatus(row.dxhyMatchStatus)
                });
            }

            if(row.qsDate!=null && row.qsStatus !='') {
                vm.tableData5.push({
                    invoiceDate: row.qsDate,
                    invoiceStatus:qsStatus(row.qsStatus)
                });
            }

            if(row.scanMatchDate!=null && row.scanMatchStatus !='') {
                vm.tableData5.push({
                    invoiceDate: row.scanMatchDate,
                    invoiceStatus: scanMatchStatus(row.scanMatchStatus)
                });
            }
            if(row.hostDate!=null && row.hostStatus !=''){
                vm.tableData5.push({
                    invoiceDate: row.hostDate,
                    invoiceStatus: formatHostStatus(row.hostStatus)
                });
            }
            // 2 已退票
            if(row.tpStatus==2 ){
                vm.tableData5.push({
                    invoiceDate: row.tpDate,
                    invoiceStatus: "已退票"
                });
            }


            if(row.hostStatus!=99 && row.hostStatus !=999){
                if(row.paymentDate!=null && row.paymentDate!=''){
                    vm.tableData5.push({
                        invoiceDate: row.paymentDate,
                        invoiceStatus: '付款'
                    });
                }
            }



            /*vm.trackInvoiceStatusDialog = true;
            vm.form1.invoiceDate = row.invoiceDate;
            vm.form1.dxhyMatchStatus = row.dxhyMatchStatus;//匹配状态（）
            vm.form1.matchDate = row.matchDate;//匹配日期
            vm.form1.qsDate = row.qsDate;//沃爱马收票（签收时间）
            vm.form1.qsStatus = row.qsStatus;//沃爱马收票（0-未签收 1-已签收）
            vm.form1.scanMatchStatus = row.scanMatchStatus;//扫描匹配（0-未匹配，1-匹配成功，2匹配失败）
            vm.form1.scanMatchDate = row.scanMatchDate;//扫描匹配
            vm.form1.hostStatus = row.hostStatus;//host匹配（）
            vm.form1.hostDate = row.hostDate;//时间*/


        },
        formatInvoiceDate: function (row) {
            if (row.invoiceDate != null) {
                return this.formatDate(row.invoiceDate);
            } else {
                return "";
            }
        },
        
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            if(time==null){
                return "";
            }
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
        return "未扫描匹配";
    }
    if(val==1){
        return "扫描匹配成功";
    }
    if(val==2){
        return "扫描匹配失败";
    }
}

function formatHostStatus(val){
    if(val==1){
        return "--";
    } if(val==0){
        return "--";
    }
    if(val==9){
        return "等待付款";
    }
    if(val==10){
        return "--";
    }
    if(val==11){
        return "等待付款";
    }
    if(val==12){
        return "等待付款";
    }
    if(val==13){
        return "--";
    }
    if(val==14){
        return "等待付款";
    }if(val==15){
        return "--";
    }if(val==19){
        return "--";
    }if(val==99){
        return "已处理";
    }
    if(val==999){
        return "已处理";
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