
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    gfName: "***",
    xfName: null,
    invoiceNo: null,
    createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth())+"-01",
    createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
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
    page: 1,
    limit: 1,
    multipleSelection: [],
    store:null,
    redLetterNotice:null,
    serialNumber:null,
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        aId:'',
        id:'',

        filePath:'',
        isNeedFileExtension: false,
        fileSizeIsFit: false,
        file: "",
        fileNumber:'',
        files:[],
        fileList3:[],

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

        serialNumber:'',
        listLoading: false,
        totalAmount: 0,
        totalTax: 0,
        createDateOptions1: {},
        createDateOptions2: {},
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rzhDate2: {},
        xfMaxlength: 30,
        rebateNo:null,
        images: [],
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            gfName: "-1",
            xfName: null,
            invoiceNo: null,
            createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
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
            createDate: null,
            store:null,
            redLetterNotice:null

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
        detailDialogRedNoticePicture :false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        scarletLetterDialog: false,
        uploadLetterDialog:false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
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
        this.fundOrgType = this.funduserid();

        this.createDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.createDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.createDateOptions2 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.createDate1));
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
        }
    },
    methods: {
        exportExcel: function(){
            currentQueryParam = {
                'gfName': vm.form.gfName,
                'xfName': vm.form.xfName,
                'invoiceNo': vm.form.invoiceNo,
                'createDate1': vm.form.createDate1,
                'createDate2': vm.form.createDate2,
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
                'createDate': vm.form.createDate,
                'store':vm.form.store,
                'redLetterNotice':vm.form.redLetterNotice
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':37,'condition':JSON.stringify(currentQueryParam)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
            // document.getElementById("ifile").src = encodeURI(baseURL + 'export/uploadInvoiceExport'
            //     +'?&store='+(currentQueryParam.store==null?'':currentQueryParam.store)
            //     +'&redLetterNotice='+(currentQueryParam.redLetterNotice==null?'':currentQueryParam.redLetterNotice)
            //     +'&createDate1='+currentQueryParam.createDate1
            //     +'&createDate2='+currentQueryParam.createDate2
            //     +'&page='+currentQueryParam.page
            //     +'&limit='+currentQueryParam.limit
            // );
        },

        downLoadFileToken:function(row) {
            var id = row.id;
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src = baseURL + 'modules/downLoadFile?id=' + id + "&token=" + token;
                },
                error: function () {

                }
            });
        },
        showImg:function(row){
            vm.selectDetailDialogPicture = true;
            var  id = row.id;
            vm.imgWin = true;
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
        detailFormCancelA: function () {
            vm.selectDetailDialogPicture = false;

        },
        funduserid:function () {
            this.$http.post(baseURL + 'redInvoiceManager/invoiceList/Orgid',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                console.log(res)
                var xhr = res.body;
                if(xhr.orgType == '5'&&xhr.taxNo =='0' ){

                };
                if(xhr.orgType == '5'||xhr.orgType == '2'){

                    $(".diss").removeClass("hideItem");
                };

            });

        },
        check: function (row) {
            // currentQueryParam.serialNumber = vm.form.serialNumber;
            currentQueryParam.serialNumber = row.serialNumber;
            currentQueryParam.page = vm.currentPage1;
            currentQueryParam.limit = vm.pageSize1;

            this.$http.post(baseURL + 'redInvoice/uploadScarletLetter/filelist',
                // serialnumber,
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;

                vm.scarletLetterDialog = true;
                this.total1 = xhr.page.totalCount;
                this.currentPage1 = xhr.page.currPage;
                this.totalPage1 = xhr.page.totalPage;
                this.tableData1 = xhr.page.list;

            });
        },
        upload:function(row){
            // vm.files = [];
            // $("#showFileNames").show()
            // $("#ol").hide()
            // vm.id = row.id;
            // vm.serialNumber = row.serialNumber;
            // vm.detailDialogRedNoticePicture = true;

            $("#showFileName").html('未选择文件');
            vm.files = [];
            $("#showFileNames").show()
            $("#ol").hide()
            vm.id = row.id;
            vm.serialNumber = row.serialNumber;
            vm.detailDialogRedNoticePicture = true;
            //vm.fileNumber = row.dataAssociation;
            // var para = {
            //     'fileNumber': vm.aId
            // };
            // vm. findFileList(para);
        },
        /**
         * 选择文件事件
         * @param event 事件触发点
         * @return {boolean}
         */
        onChangeFile: function (event) {

            $("#showFileNames").html("未选择文件");
            $("#showFileNames").removeAttr("title");
            var str = $("#files").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4).toLowerCase();
            var photoExt1 = str.substr(index, 5).toLowerCase();
            if (photoExt != '' && !( photoExt == '.pdf'||photoExt == '.jpg'||  photoExt == '.png'|| photoExt == '.gif' || photoExt1 == '.jpeg')) {
                alert("请上传pdf/png/gif/jpg/jpeg文件!");
                $("#files").val("");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var maxsize = 20 * 1024 * 1024;//20M
                var file = event.target.files[0];
                var fileSize = file.size;
                if (fileSize > maxsize) {
                    alert("上传的文件不能大于20M");
                    $("#files").val("");
                    vm.fileSizeIsFit = false;
                    return false;
                } else {
                    vm.files.push(file);
                    if(vm.files.length>0){
                        $("#showFileNames").hide();
                    }
                    $("#ol").html("");
                    $.each(vm.files,function(index,value){
                        $("#ol").append("<li>"+value.name+"</li>")
                        $("#ol").show();
                    });
                    $("#ol").append("<button class='aaaa' type='button'> "+"删除"+"</button>")
                    $("#ol").on("click",".aaaa",function () {
                        vm.files = [];
                        $("#ol").hide();
                        $("#showFileNames").show();
                    });
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
            if (vm.files.length<0) {
                alert("请选择文件");
            } else if (!vm.isNeedFileExtension) {
                alert("请上传pdf/png/gif/jpg/jpeg文件!");
            } else if (!vm.fileSizeIsFit) {
                alert("上传的文件不能大于2M");
            } else {
                event.preventDefault();
                for(var i =0;i< vm.files.length;i++) {
                    var formData = new FormData();
                    formData.append('file', vm.files[i]);
                    // formData.append('file', this.file);
                    formData.append('token', this.token);
                    formData.append('serialNumber', vm.serialNumber);
                    formData.append('id', vm.id);
                    var url = baseURL + 'redInvoice/uploadScarletLetter/uploadRed';
                    var loading = vm.getLoading("上传中...");
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: formData,
                        dataType: "json",
                        async: false,
                        cache: false,//上传文件无需缓存
                        processData: false,//用于对data参数进行序列化处理 这里必须false
                        contentType: false, //必须
                        success: function (response) {
                            var b = vm.files.length-1;
                            if(i==b){
                                loading.close();
                            }
                            $("#ol").html("");
                            $("#showFileNames").show();
                            if (response.code == 0) {
                                var j = vm.files.length-1;
                                if(i==j){
                                    alert(response.msg);
                                }
                                vm.detailDialogRedNoticePicture = false;
                                vm.findAll(1);
                            } else {
                                alert("系统错误！请稍后再试！");
                            }
                        },
                    });
                }

            }
        },
        //删除文件
        delete1:function(row){
            // alert(row.filePath);
            var para = {
                // 'id': row.id,
                'localFileName': row.localFileName,
                'filePath': row.filePath
            };
            this.$http.post(baseURL + 'redInvoice/uploadScarletLetter/delete',
                para,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                if(xhr.code==0){
                    alert('删除成功' );
                    vm.scarletLetterDialog = false;
                    // var para = {
                    //     'fileName': row.fileName
                    // };
                    // vm.findFileList(para);
                    vm.findAll(1);

                }


            });
        },

        findFileList(para){


            this.$http.post(baseURL + 'modules/openRedTicket/queryOpenRedDataList',
                para,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                vm.tableDataFileList = xhr.fileList;
            });
        },

        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
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

            var qsStartDate = new Date(vm.form.createDate1);
            var qsEndDate = new Date(vm.form.createDate2);
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
            }else if(qsEndDate.getTime() < new Date(vm.form.createDate1)){
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
                        'createDate1': vm.form.createDate1,
                        'createDate2': vm.form.createDate2,
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
                        'createDate': vm.form.createDate,
                        'store':vm.form.store,
                        'redLetterNotice':vm.form.redLetterNotice
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
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
        detailFormCance: function () {
            vm.uploadLetterDialog = false;
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
        detailFormCancel: function () {
            vm.file='';
            $("#showFileName").html('未选择文件');
            vm.scarletLetterDialog = false;
            vm.detailDialogFormVisible = false;
            vm.detailDialogRedNoticePicture = false;
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
            if(vm.total1==0){
                return;
            }
            this.findAll1(currentPage1);
        },
        currentChange2: function(currentPage2){
            if(vm.total2==0){
                return;
            }
            this.findAll2(currentPage2);
        },
        findAll: function (currentPage) {
            currentQueryParam.page = currentPage;
            currentQueryParam.limit = vm.pageSize;
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'redInvoice/uploadScarletLetter/list',
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
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return dateFormatStrToYM(cellValue);
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
        }
    }
});
/**
 * 显示选择文件的窗口
 */
function showSelectFileWin() {
    // $("#file").val("");
    // $("#files").click();
    $("#files").val("");
    $("#files").click();
}


function getContextPath(){

    var pathName = document.location.pathname;

    var index = pathName.substr(1).indexOf("/");

    var result = pathName.substr(0,index+1);

    return result;

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
    var tempCreateDate = new Date(value);
    var tempYear = tempCreateDate.getFullYear() + "年";
    var tempMonth = tempCreateDate.getMonth() + 1;
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