
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    jvCode: "-1",
    invoiceNo:'',
    venderId:'',
    companyCode:'',
    invoiceDate1:'',
    invoiceDate2:'',
    //invoiceStatus: "-1",
    //invoiceType: "-1",
    //qsStatus: "-1",

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
        fileList: [],
        selectFileFlag: '',
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
        sumReturnAmount:0.00,
        poCodeMaxlength: 16,
        claimCodeMaxlength:16,
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            jvCode: "-1",
            invoiceNo:'',
            venderId:'',
            companyCode:'',
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        listLoading4: false,
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
        batchRedTicketDialog:false,
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
        },
        showList: true
    },
    mounted:function(){
        this.querySearchGf();
        $("#gf-select").attr("maxlength","50");
        this.invoiceDateOptions1 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.invoiceDateOptions2 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.invoiceDate2));
                return time.getTime() >= Date.now();
            }
        };
    },
    watch: {
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
        },
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
        orderDate1Change: function (val) {
            vm.orderForm.orderDate1 = val;
        },
        orderDate2Change: function (val) {
            vm.orderForm.orderDate2 = val;
        },
        query: function (formName) {

            $(".checkMsg").remove();
            var checkDate = true;
            var invoiceDate1 = new Date(vm.form.invoiceDate1);
            var invoiceDate2 = new Date(vm.form.invoiceDate2);
            if (invoiceDate1.getTime() + 1000 * 60 * 60 * 24 * 360 < invoiceDate2.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate = false;
            } else if (invoiceDate2.getTime() < invoiceDate1.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate = false;
            }
            if (!checkDate) {
                return;
            }
            isInitial = false;
            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'jvCode':vm.form.jvCode,
                        'invoiceNo': vm.form.invoiceNo,
                        'venderId':vm.form.venderId,
                        'companyCode': vm.form.companyCode,
                        'invoiceDate1': vm.form.invoiceDate1,
                        'invoiceDate2': vm.form.invoiceDate2
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
            $.get(baseURL + 'modules/InformationInquiry/matchQuerys/searchGf',function(r){
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].orgCode;
                    gf.label = r.optionList[i].orgCode+"("+r.optionList[i].label+")";
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
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }

        },
        findAll: function () {
            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                jvCode:currentQueryParam.jvCode,
                invoiceNo: currentQueryParam.invoiceNo,
                venderId:currentQueryParam.venderId,
                companyCode: currentQueryParam.companyCode,
                invoiceDate1:currentQueryParam.invoiceDate1,
                invoiceDate2:currentQueryParam.invoiceDate2

            };
            this.listLoading = true;
            var flag = false;
            this.$http.post(baseURL + 'modules/fixed/invoiceImportExport/list',
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
                if(this.tableData.length>0){
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                }

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
        exportExcel: function(){
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/fixed/invoiceImportExport'
                +'?jvCode='+currentQueryParam.jvCode
                +'&invoiceNo='+currentQueryParam.invoiceNo
                +'&venderId='+currentQueryParam.venderId
                +'&companyCode='+currentQueryParam.companyCode
                +'&invoiceDate1='+currentQueryParam.invoiceDate1
                +'&invoiceDate2='+currentQueryParam.invoiceDate2

            );
        },
        importFormCancel: function () {
            vm.batchRedTicketDialog = false;
        },
        batchExport:function(){
            vm.batchRedTicketDialog = true;
        },
        numberFormat1: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
                return "—— ——";
            }
            return cellValue+'%';

        },


        /**
         * 文件批量导入
         *
         *
         * */
        exportData:function(){
            document.getElementById("ifile").src = baseURL + "modules/fixed/invoiceImportExportExportImport";
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                vm.selectFileFlag = '';
                vm.file = '';
                $("#upload_form")[0].reset();
                this.isNeedFileExtension = false;
                return false;
            } else {
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    this.file = event.target.files[0];
                    this.isNeedFileExtension = true;
                    //截取名称最后18位
                    this.selectFileFlag = event.target.files[0].name;
                }
            }
        },
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else{
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
                //formData.append('venderId',this.queryData1.usercode)
                //formData.append('venderName',this.queryData1.username)

                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };


                var flag = false;
                var hh;
                var url = baseURL + "modules/fixed/invoiceImportExportExportImport";
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    if (response.data.success) {
                        vm.batchRedTicketDialog = false;
                        vm.findAll(vm.currentPage);
                        vm.selectFileFlag = '';
                        vm.file = '';
                        $("#upload_form")[0].reset();
                        alert("批量导入成功！")
                        /*if(vm.invoiceData.length>0) {
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
                        $("#file").html("");*/
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
                /*var intervelId = setInterval(function () {
                    if (flag) {
                        hh = $(document).height();
                        $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                        clearInterval(intervelId);
                        return;
                    }
                }, 50);*/
            }
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
    }else{
        return "查验";
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