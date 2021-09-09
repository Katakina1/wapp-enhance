Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var isInitial = true;
var currentQueryParam = {
    examineResult: '',
    businessType: null,
    invoiceDate1: '',
    invoiceDate2: '',
    redTicketDataSerialNumber: '',
    invoiceCode: '',
    invoiceNo: '',
    serviceNo: null,
    userCode: null,
    id: '',
    page: 1,
    limit: 1
};
var currentQueryParam1 = {
    examineResult: '',
    businessType: null,
    invoiceDate1: '',
    invoiceDate2: '',
    redTicketDataSerialNumber: '',
    invoiceCode: '',
    invoiceNo: '',
    serviceNo: null,
    userCode: null,
    id: '',
    page: 1,
    limit: 1
};


var vm = new Vue({
    el: '#rrapp',
    data: {
        selection: [],
        isNeedFileExtension: false,
        fileSizeIsFit: false,
        file: "",
        fileNumber: '',
        files: [],
        fileList3: [],
        fileList: [],
        OpenRedTicketType: [],
        total: 0,
        uploadForm1: {},
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        sendMessage: {},

        total1: 0,
        currentPage1: 1,
        totalPage1: 0,
        pageSize1: PAGE_PARENT.PAGE_SIZE,
        pageList1: PAGE_PARENT.PAGE_LIST,
        pagerCount1: 5,
        tableData1: [],

        total2: 0,
        currentPage2: 1,
        totalPage2: 0,
        pageSize2: PAGE_PARENT.PAGE_SIZE,
        pageList2: PAGE_PARENT.PAGE_LIST,
        pagerCount2: 5,
        tableData2: [],

        total3: 0,
        currentPage3: 1,
        totalPage3: 0,
        pageSize3: PAGE_PARENT.PAGE_SIZE,
        pageList3: PAGE_PARENT.PAGE_LIST,
        pagerCount3: 5,
        tableData3: [],

        total4: 0,
        currentPage4: 1,
        totalPage4: 0,
        pageSize4: PAGE_PARENT.PAGE_SIZE,
        pageList4: PAGE_PARENT.PAGE_LIST,
        pagerCount4: 5,
        tableData4: [],


        total5: 0,
        currentPage5: 1,
        totalPage5: 0,
        pageSize5: PAGE_PARENT.PAGE_SIZE,
        pageList5: PAGE_PARENT.PAGE_LIST,
        pagerCount5: 5,
        tableData5: [],

        id: '',
        aId: '',
        redTicketDataSerialNumber: '',
        invoiceNo: '',
        invoiceCode: '',
        businessType: '',

        tableData6: '',
        tableData7: '',
        listLoading: false,
        totalAmount: 0,
        totalTax: 0,
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        invoiceDateOptions3: {},
        invoiceDateOptions4: {},
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
        form: {
            examineResult: "-1",
            businessType: "-1",
            userCode: null,
            serviceNo: null,
            examineStartDate: null,
            examineEndDate: null,
            createDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            createDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
        },
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        form1: {
            examineRemarks: ''
        },
        form2: {

        },
        disagreeId: '',
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        detailForm: {
            invoiceType: null,
            invoiceStatus: null,
            createDate: null,
            statusUpdateDate: null,
            qsBy: null,
            qsType: null,
            sourceSystem: null,
            qsDate: null,
            rzhYesorno: null,
            gxDate: null,
            gxUserName: null,
            confirmDate: null,
            confirmUser: null,
            sendDate: null,
            authStatus: null,
            machinecode: null,
            rzhBelongDate: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            qsStatus: null,
            outStatus: null,
            outList: [],
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
        //下面是对应模态框隐藏的属性
        detailDialogRedNoticePicture: false,
        batchUploadDialogRedNoticePicture: false,
        selectDetailDialogPicture: false,
        disagreeDialogRedNotice: false,
        detailDialogPicture: false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        uploadDialog: false,
        sendMessageDetailDialog: false,
        rules: {
            invoiceNo: [{
                validator: function (rule, value, callback) {
                    if (value != null && value != "") {
                        var regex = /^[0-9]{1,8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为不超过8位的数字'))
                        } else {
                            callback();
                        }
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }],
            createDate1: [
                {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'}
            ],
            createDate2: [
                {type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change'}
            ]
        },
        showList: true
    },
    mounted: function () {
        this.queryOpenRedTicketType();

        this.invoiceDateOptions1 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.createDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions2 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.createDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.invoiceDateOptions3 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.createDate2));
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions4 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.form.createDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.querySearchGf();
        $("#gf-select").attr("maxlength", "50");
    },

    methods: {
        queryOpenRedTicketType: function () {
            $.get(baseURL + 'modules/openRedInvoiceQuery/queryOpenRedTicketType', function (r) {
                var gfs = [];
                /* gfs.push({
                     value: "-1",
                     label: "全部"
                 });*/
                for (var i = 0; i < r.optionList.length; i++) {
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
            $(".el-select input").attr('readonly', 'readonly');
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        examineQuery: function () {
            isInitial = false;
            $(".checkMsg").remove();
            var checkExamineDate = true;
            var examineStartDate = new Date(vm.form.examineStartDate);
            var examineEndDate = new Date(vm.form.examineEndDate);
            if ((vm.form.examineStartDate != '' && vm.form.examineEndDate == '') || (vm.form.examineStartDate == null && vm.form.examineEndDate != null)) {
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">审核日期不能选择一个开始日期或者结束日期</div>');
                checkExamineDate = false;
            }
            if (examineStartDate.getTime() > examineEndDate.getTime()) {
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">审核始日期不能大于结束日期</div>');
                checkExamineDate = false;
            }
            examineStartDate.setMonth(examineStartDate.getMonth() + 12);
            if ( examineStartDate.getTime()+1000*60*60*24 < examineEndDate.getTime()) {
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkExamineDate=false;
            }

            var checkCreateDate = true;
            var createStartDate = new Date(vm.form.createDate1);
            var createEndDate = new Date(vm.form.createDate2);
            if ((vm.form.createDate1 != '' && vm.form.createDate2 == '') || (vm.form.createDate1 == null && vm.form.createDate2 != null)) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">创建日期不能选择一个开始日期或者结束日期</div>');
                checkCreateDate = false;
            }
            if (createStartDate.getTime() > createEndDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">始日期不能大于结束日期</div>');
                checkCreateDate = false;
            }
            createStartDate.setMonth(createStartDate.getMonth() + 12);
            if ( createStartDate.getTime()+1000*60*60*24 < createEndDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkCreateDate=false;
            }

            /*var checkKPDate = true;
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

*/
            if (!(checkExamineDate && checkCreateDate)) {
                return;
            }
            var Self = this;

            currentQueryParam = {
                'invoiceDate1': vm.form.createDate1,
                'invoiceDate2': vm.form.createDate2,
                'businessType': vm.form.businessType,
                'examineStartDate': vm.form.examineStartDate,
                'examineEndDate': vm.form.examineEndDate,
                'userCode': vm.form.userCode,
                'examineResult': vm.form.examineResult,
                'serviceNo': vm.form.serviceNo
            };
            vm.findAll(1);


        },
        querySearchGf: function () {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchGf', function (r) {
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for (var i = 0; i < r.optionList.length; i++) {
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.gfs = gfs;
            });
        },
        querySearchXf: function (queryString, callback) {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchXf', {queryString: queryString}, function (r) {
                var resultList = [];
                for (var i = 0; i < r.list.length; i++) {
                    var res = {};
                    res.value = r.list[i];
                    resultList.push(res);
                }
                callback(resultList);
            });
        },
        invoiceDate1Change: function (val) {
            vm.form.createDate1 = val;
        },
        invoiceDate2Change: function (val) {
            vm.form.createDate2 = val;
        },

        examineStartDateOptions: function (val) {
            vm.form.examineStartDate = val;
        },
        examineEndDateOptions: function (val) {
            vm.form.examineEndDate = val;
        },
        dqskssqDateChange: function (val) {
            vm.form.rzhBelongDate = val;
        },
        qsDate1Change: function (val) {
            vm.form.qsDate1 = val;
        },
        qsDate2Change: function (val) {
            vm.form.qsDate2 = val;
        },
        rzhDate1Change: function (val) {
            vm.form.rzhDate1 = val;
        },
        rzhDate2Change: function (val) {
            vm.form.rzhDate2 = val;
        },
        qsChange: function (value) {
            if (value == "1") {
                $('.qsItem').removeClass("hideItem");
                $('.btn-row2').addClass("hideItem");

                if (vm.form.rzhYesorno == "1") {
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row4').removeClass("hideItem");
                    $('.rzh-row3').addClass("hideItem");
                    $('.rzh-row4').removeClass("hideItem");
                } else {
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row4').addClass("hideItem");
                }
            } else {
                $('.qsItem').addClass("hideItem");
                vm.form.qsType = "-1";
                vm.form.qsDate1 = null;
                vm.form.qsDate2 = null;
                $('.btn-row4').addClass("hideItem");
                if (vm.form.rzhYesorno == "1") {
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row2').addClass("hideItem");
                    $('.rzh-row4').addClass("hideItem");
                    $('.rzh-row3').removeClass("hideItem");
                } else {
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row2').removeClass("hideItem");
                }
            }
        },
        rzhChange: function (value) {
            if (value == "1") {
                $('.rzhItem').removeClass("hideItem");
                $('.btn-row2').addClass("hideItem");
                if (vm.form.qsStatus == "1") {
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row4').removeClass("hideItem");
                    $('.rzh-row3').addClass("hideItem");
                    $('.rzh-row4').removeClass("hideItem");
                } else {
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row4').addClass("hideItem");
                    $('.rzh-row4').addClass("hideItem");
                    $('.rzh-row3').removeClass("hideItem");
                }
            } else {
                $('.rzhItem').addClass("hideItem");
                vm.form.rzhBelongDate = null;
                vm.form.rzhDate1 = null;
                vm.form.rzhDate2 = null;
                $('.btn-row4').addClass("hideItem");
                if (vm.form.qsStatus == "1") {
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row2').addClass("hideItem");
                } else {
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row2').removeClass("hideItem");
                }
            }
        },
        exportExcel: function () {
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExport'
                + '?gfName=' + currentQueryParam.gfName
                + '&xfName=' + (currentQueryParam.xfName == null ? '' : currentQueryParam.xfName)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + currentQueryParam.invoiceDate1
                + '&invoiceDate2=' + currentQueryParam.invoiceDate2
                + '&invoiceStatus=' + currentQueryParam.invoiceStatus
                + '&invoiceType=' + currentQueryParam.invoiceType
                + '&qsStatus=' + currentQueryParam.qsStatus
                + '&rzhYesorno=' + currentQueryParam.rzhYesorno
                + '&qsType=' + currentQueryParam.qsType
                + '&qsDate1=' + (currentQueryParam.qsDate1 == null ? '' : currentQueryParam.qsDate1)
                + '&qsDate2=' + (currentQueryParam.qsDate2 == null ? '' : currentQueryParam.qsDate2)
                + '&rzhBelongDate=' + (currentQueryParam.rzhBelongDate == null ? '' : currentQueryParam.rzhBelongDate)
                + '&rzhDate1=' + (currentQueryParam.rzhDate1 == null ? '' : currentQueryParam.rzhDate1)
                + '&rzhDate2=' + (currentQueryParam.rzhDate2 == null ? '' : currentQueryParam.rzhDate2)
                + '&totalAmount=' + vm.totalAmount
                + '&totalTax=' + vm.totalTax);
        },
        exportExcelMX: function () {
            $("#export_btnMX").attr("disabled", "disabled").addClass("is-disabled");
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExportMX'
                + '?gfName=' + currentQueryParam.gfName
                + '&xfName=' + (currentQueryParam.xfName == null ? '' : currentQueryParam.xfName)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + currentQueryParam.invoiceDate1
                + '&invoiceDate2=' + currentQueryParam.invoiceDate2
                + '&invoiceStatus=' + currentQueryParam.invoiceStatus
                + '&invoiceType=' + currentQueryParam.invoiceType
                + '&qsStatus=' + currentQueryParam.qsStatus
                + '&rzhYesorno=' + currentQueryParam.rzhYesorno
                + '&qsType=' + currentQueryParam.qsType
                + '&qsDate1=' + (currentQueryParam.qsDate1 == null ? '' : currentQueryParam.qsDate1)
                + '&qsDate2=' + (currentQueryParam.qsDate2 == null ? '' : currentQueryParam.qsDate2)
                + '&rzhBelongDate=' + (currentQueryParam.rzhBelongDate == null ? '' : currentQueryParam.rzhBelongDate)
                + '&rzhDate1=' + (currentQueryParam.rzhDate1 == null ? '' : currentQueryParam.rzhDate1)
                + '&rzhDate2=' + (currentQueryParam.rzhDate2 == null ? '' : currentQueryParam.rzhDate2)
                + '&totalAmount=' + vm.totalAmount
                + '&totalTax=' + vm.totalTax);
        },
        exportExcelSL: function () {
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/comprehensiveInvoiceQueryExportSL'
                + '?gfName=' + currentQueryParam.gfName
                + '&xfName=' + (currentQueryParam.xfName == null ? '' : currentQueryParam.xfName)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + currentQueryParam.invoiceDate1
                + '&invoiceDate2=' + currentQueryParam.invoiceDate2
                + '&invoiceStatus=' + currentQueryParam.invoiceStatus
                + '&invoiceType=' + currentQueryParam.invoiceType
                + '&qsStatus=' + currentQueryParam.qsStatus
                + '&rzhYesorno=' + currentQueryParam.rzhYesorno
                + '&qsType=' + currentQueryParam.qsType
                + '&qsDate1=' + (currentQueryParam.qsDate1 == null ? '' : currentQueryParam.qsDate1)
                + '&qsDate2=' + (currentQueryParam.qsDate2 == null ? '' : currentQueryParam.qsDate2)
                + '&rzhBelongDate=' + (currentQueryParam.rzhBelongDate == null ? '' : currentQueryParam.rzhBelongDate)
                + '&rzhDate1=' + (currentQueryParam.rzhDate1 == null ? '' : currentQueryParam.rzhDate1)
                + '&rzhDate2=' + (currentQueryParam.rzhDate2 == null ? '' : currentQueryParam.rzhDate2)
                + '&totalAmount=' + vm.totalAmount
                + '&totalTax=' + vm.totalTax);
        },
        printform: function (oper) {
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
            if (oper < 10) {
                var w = screen.availWidth - 10;
                var h = screen.availHeight - 30;
                if (oper <= 3) {
                    var head = '<script type="text/javascript" charset="utf-8" src="../../js/customer/resource.js"></script>';
                    var prnhtml = $('#printdiv' + oper).find('.el-dialog__body .col-xs-9').html();
                } else {
                    var prnhtml = $('#printdiv' + oper).html();
                }

                var newWin = parent.window.open('', "win", "fullscreen=0,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=0,resizable=1,width=" + w + ",height=" + h + ",top=0,left=0", true);
                newWin.document.write(prnhtml);
                newWin.document.close();
                newWin.focus();
                newWin.print();
                newWin.close();
            } else {
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
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        currentChange1: function (currentPage1) {
            if (vm.total1 == 0) {
                return;
            }
            this.findAll1(currentPage1);
        },
        currentChange2: function (currentPage2) {
            if (vm.total2 == 0) {
                return;
            }
            this.findAll2(currentPage2);
        },
        currentChange3: function (currentPage3) {
            if (vm.total3 == 0) {
                return;
            }
            this.findAll3(currentPage3);
        },
        currentChange4: function (currentPage4) {
            if (vm.total4 == 0) {
                return;
            }
            this.findAll4(currentPage4);
        },
        currentChange5: function (currentPage5) {
            if (vm.total5 == 0) {
                return;
            }
            this.findAll5(currentPage5);
        },
        findAll5: function (currentPage5) {
            currentQueryParam.page = currentPage5;
            currentQueryParam.limit = vm.pageSize5;
            currentQueryParam.invoiceCode = vm.invoiceCode;
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

            var flag = false;
            if (!isNaN(currentPage1)) {
                this.currentPage1 = currentPage1;
            }
            if (vm.businessType == 1) {
                this.$http.post(baseURL + 'modules/openRedTicket/list/queryByRedTicketDataSerialNumber',
                    currentQueryParam1,
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    var xhr = res.body;
                    this.total1 = xhr.page1.totalCount;
                    console.log(xhr.page1.currPage);
                    this.currentPage1 = xhr.page1.currPage;
                    this.totalPage1 = xhr.page1.totalPage;
                    this.tableData1 = xhr.page1.list;
                    this.listLoading = false;
                });
            }
            if (vm.businessType == 2) {
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
            this.$http.post(baseURL + 'modules/openRedTicket/list/examineQueryPaged',
                currentQueryParam,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                vm.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;
                this.tableData = xhr.page.list;
                this.listLoading = false;


            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {

                this.findAll(1);
            }
        },
        handleSizeChange1: function (val) {
            this.pageSize1 = val;
            if (!isInitial) {

                this.findAll1(1);
            }
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
            if (!isInitial) {
                this.findAll2(1);
            }
        },
        handleSizeChange3: function (val) {
            this.pageSize3 = val;
            if (!isInitial) {
                this.findAll3(1);
            }
        },
        handleSizeChange4: function (val) {
            this.pageSize4 = val;
            if (!isInitial) {
                this.findAll4(1);
            }
        },
        handleSizeChange5: function (val) {
            this.pageSize5 = val;
            if (!isInitial) {
                this.findAll5(1);
            }
        },
        formatRate: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return Number(cellValue)+'%';
            // if(cellValue==null){
            //     return '—— ——';
            // }
            // if(cellValue=='1.5000'){
            //     return cellValue.substring(0,3)+'%';
            // }
            // return  cellValue.substring(0,cellValue.indexOf('.'))+'%';
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
            if (cellValue == null || cellValue == '' || cellValue == undefined) {
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
        checkSelectable:function(row) {
            return row.examineResult !== '2' && row.examineResult !== '3';
        },
        /**
         * 撤销
         * */
        revoke:function (row) {
            parent.layer.confirm("确定撤销吗？",{btn: ['确定', '取消']},function (index) {
                parent.layer.close(index);
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/openRedTicket/revoke',
                data: {id: row.id},
                success: function (r) {
                    vm.examineQuery();
                    alert(r.msg);
                },
                error: function () {
                }
            });
        });
        },
        changeFunPO: function (row) {
            var details = [];
            for (var i = 0; i < row.length; i++) {
                details.push(row[i].id);
            }
            vm.selection = details;
            if (vm.selection.length > 0) {
                //增加样式 export_btn_pdf
                $(".agree_btn_red_data").removeAttr("disabled").removeClass("is-disabled");
            } else {
                $(".agree_btn_red_data").attr("disabled", "disabled").addClass("is-disabled");
            }
        },
        printCover: function () {
            var uri = baseURL + 'export/redTicket/printCover' + '?ids=' + JSON.stringify(vm.selection) + "&userCode=" + vm.form.userCode;
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
        detail: function (row) {

            var value = row.id;
            vm.id = row.id;
            var buType = row.businessType;
            vm.businessType = row.businessType;
            var redNumber = row.redTicketDataSerialNumber;
            vm.redTicketDataSerialNumber = row.redTicketDataSerialNumber;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
            var params = {
                'redTicketDataSerialNumber': redNumber,
                'id': value,
                'page': 1,
                'limit': PAGE_PARENT.PAGE_SIZE
            };
            //退货类型
            if (buType == 1) {
                vm.detailDialogVehicleFormVisible = true;
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


                });

            }
            //协议类型
            if (buType == 2) {
                vm.tableData1 = [];
                vm.tableData2 = [];
                vm.tableData3 = [];
                vm.tableData4 = [];
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
            if (buType == 3) {
                vm.tableData3 = [];
                vm.tableData5 = [];
                vm.tableData4 = [];
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

        },

        //查看上传资料
        checkData: function (row) {
            var dataAssociation = row.dataAssociation;
            var id = row.id;
            var para = {
                'dataAssociation': dataAssociation,
                'id': id
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
        showImg: function (row) {
            vm.selectDetailDialogPicture = true;
            var id = row.id;
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
        //查看上传红字通知单
        /* check:function(row){
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

         },*/

        batchUpload: function () {
            vm.batchUploadDialogRedNoticePicture = true;
        },
        submitUpload:function() {

            vm.$refs.uploadBatch.submit();
        },
        /* submitUpload() {
             /!*if(vm.fileList3.length == 0){
                 alert("请选择pdf文件！")
                 return;
             }*!/
             var fileList3 = vm.fileList3;
             console.log(fileList3 + 'asdasdas')
             for (var i = 0; i < fileList3.length; i++) {
                 var fileName = fileList3[i].name;
                 var index = fileName.lastIndexOf('.');
                 var photoExt = str.substr(index).toLowerCase();
                 alert(fileName)

             }
             this.$refs.upload.submit();
         },*/
        handleRemove:function(file, fileList3) {
            console.log(file, fileList3);
        },
        beforeAvatarUpload:function(file) {

            var Xls = file.name.split('.');

            if (Xls[1] === 'pdf' || Xls[1] === 'pdf') {
                return file
            } else {
                this.$message.error('上传文件只能是 pdf 格式!')
                return false
            }

        },
        handlePreview:function(file) {
            console.log(file);
        },

        batchUploadClose: function () {
            vm.batchUploadDialogRedNoticePicture = false;
        },
        check: function (row) {
            vm.aId = row.id;
            vm.redTicketDataSerialNumber = row.redTicketDataSerialNumber;
            vm.businessType = row.businessType;
            vm.detailDialogRedNoticePicture = true;

        },

        detailFormCancelA: function () {
            vm.selectDetailDialogPicture = false;

        },
        downLoadFileToken: function (row) {
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
        saveExamineRemarks: function () {
            if (vm.form1.examineRemarks == '') {
                alert("理由不能为空！！！")
                return;
            }
            var para = {
                'ids': JSON.stringify(vm.selection),
                'examineRemarks': vm.form1.examineRemarks,
                'businessType': vm.businessType
            };
            this.$http.post(baseURL + 'modules/openRedTicket/saveExamineRemarksById',
                para,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                if (res.body.code == 500) {
                    alert("操作失败！");
                } else {
                    alert(res.data.message);
                    vm.disagreeDialogRedNotice = false;
                    vm.form1.examineRemarks = '';
                    //vm.examineQuery();
                     vm.findAll(vm.currentPage);
                    //});
                }
            });

        },
        //不同意操作
        disagree: function () {
            /*vm.redTicketDataSerialNumber = '';
            vm.businessType = '';
            vm.disagreeId = row.id;
            vm.businessType = row.businessType;
            vm.redTicketDataSerialNumber = row.redTicketDataSerialNumber;*/
            vm.disagreeDialogRedNotice = true;
        },
        disagreeFormCancel: function (row) {
            vm.disagreeDialogRedNotice = false;
            vm.form1.examineRemarks = '';
        },
        sendMessageCancel: function (row) {
            vm.sendMessageDetailDialog = false;
        },
        //同意
        agree: function () {
            //修改状态
            var ids = '';
            vm.sendMessage =vm.selection.join(',')
            this.$http.post(baseURL + 'modules/openRedTicket/updateMatchStatus',
                {ids: JSON.stringify(vm.selection)},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                if (res.body.code == 500) {
                    alert(res.data.msg);
                } else {
                  // vm.examineQuery();
                    vm.findAll(vm.currentPage);
                    vm.sendMessageDetailDialog =true;
                }
            });

            //var uri = baseURL + 'export/redTicket/printCover' +'?ids='+JSON.stringify(vm.selection)+"&userCode="+vm.form.userCode;
        },
        //发送邮件通知税务组
        sendMessageToTax: function () {
            //修改状态
            var ids = '';
            this.$http.post(baseURL + 'modules/openRedTicket/sendMessage',
                {ids: vm.sendMessage},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                    alert(res.body.success);
                if (res.body.success=="no") {
                    vm.findAll(vm.currentPage);
                    vm.sendMessageDetailDialog =false;
                    alert("发送失败！");
                } else {
                    alert("已成功通知税务组！");
                    vm.findAll(vm.currentPage);
                    vm.sendMessageDetailDialog =false;
                }
            });

            //var uri = baseURL + 'export/redTicket/printCover' +'?ids='+JSON.stringify(vm.selection)+"&userCode="+vm.form.userCode;
        },
        //导出审核清单
        exportDataExamine: function () {
            var param = {
                'examineResult': vm.form.examineResult,
                'businessType': vm.form.businessType,
                'invoiceDate1': vm.form.createDate1,
                'examineStartDate': vm.form.examineStartDate,
                'examineEndDate': vm.form.examineEndDate,
                'page': vm.currentPage,
                'limit': vm.pageSize,
                'invoiceDate2': vm.form.createDate2
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':33,'condition':JSON.stringify(param)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
           // document.getElementById("ifile").src = baseURL + 'export/exportDataExamine' + '?' + $.param(param);
        },
        //上传文件
        uploadImg: function (row) {
            vm.uploadDialog = true;
        },

        UploadUrl: function () {
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
        onChangeFile: function (event) {
            vm.file = "";
            $("#showFileName").html("未选择文件");
            $("#showFileName").removeAttr("title");
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4).toLowerCase();
            if (photoExt != '' && !(photoExt == '.pdf')) {
                alert("请上传pdf文件!");
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
        handlePost: function (content) {
            console.log(content);
            var formData = new FormData();
            formData.append('file', content.file);
            formData.append('token', token);
            var url = baseURL + 'modules/openRedTicket/data/uploadRedBatch';
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                async: false,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (response) {
                    if (response.code == 0) {
                        content.onSuccess('文件上传成功');
                    } else {
                        alert('文件上传失败!');
                        content.onError('文件上传失败,已自动删除失败的文件!');
                    }
                },
                error: function (response) {
                    alert('文件上传失败!');
                    content.onError('文件上传失败,已自动删除失败的文件!');
                }

            });
        },
        beforeAvatarUploadBatch: function (file) {
            var maxsize = 5 * 1024 * 1024;//5M
            var fileSize = file.size;
            if (fileSize > maxsize) {
                alert("文件大小超过5MB的已自动删除!");
                return false;
            }
            var Xls = file.name.split('.');
            alert(Xls[1]);
            if (Xls[1] === 'pdf' || Xls[1] === 'pdf') {
                return file
            } else {
                alert('上传文件只能是 pdf 格式!');
                return false
            }
        },
        /*onChangeFileBatch: function (event) {
            //vm.files = [];
            $("#showFileNames").html("未选择文件");
            $("#showFileNames").removeAttr("title");
            var str = $("#files").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4).toLowerCase();
            if (photoExt != '' && !(photoExt == '.pdf')) {
                alert("请上传pdf文件!");
                $("#files").val("");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var maxsize = 2 * 1024 * 1024;//2M
                var file = event.target.files[0];
                var fileSize = file.size;
                if (fileSize > maxsize) {
                    alert("上传的文件不能大于2M");
                    $("#files").val("");
                    vm.fileSizeIsFit = false;
                    return false;
                } else {

                    vm.files.push(file);
                    if (vm.files.length > 0) {
                        $("#showFileNames").hide();
                    }
                    $("#ol").html("");
                    $.each(vm.files, function (index, value) {
                        $("#ol").append("<li>" + value.name + "</li>")
                    })
                    /!*for(var i =0;i<vm.files.length;i++){
                        $("#ol").appendChild("<li>"+vm.files[i].name+"</li>")
                       // $("#showFileNames").attr("title", vm.files[i].name);
                       // $("#showFileNames").html(vm.files[i].name);
                    }*!/
                    /!* $("#showFileNames").attr("title", vm.file.name);
                     $("#showFileNames").html(vm.file.name);*!/
                    vm.isNeedFileExtension = true;
                    vm.fileSizeIsFit = true;
                }
            }
        },*/
        /**
         * 上传选择的文件
         * @param event
         */
        /*uploadFileBatch: function (event) {

            if (vm.files.length < 0) {
                alert("请选择文件");
            } else if (!vm.isNeedFileExtension) {
                alert("请上传pdf文件!");
            } else if (!vm.fileSizeIsFit) {
                alert("上传的文件不能大于2M");
            } else {
                event.preventDefault();
                var messageA = '';
                // for(var i =0;i< vm.files.length;i++){
                var formData = new FormData();
                formData.append('file', vm.files[i]);
                formData.append('token', this.token);
                //formData.append('fileNumber', vm.fileNumber);
                //formData.append('id', vm.aId);
                // formData.append('scanPath', electron.form.scanPathId);
                /!* for(){

                 }*!/
                var url = baseURL + 'modules/openRedTicket/data/uploadRedBatch';
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
                        if (response.code == 0) {
                            //alert(response.msg);
                            if (response.msg != "success") {
                                messageA = messageA + " " + vm.files[i].name;
                            }
                            vm.detailDialogRedNoticePicture = false;
                            // vm.examineQuery();
                            /!* var pdfName = "";
                           for (var i = 0; i < response.list.length; i++) {
                                var data = response.list[i];
                                if (data.readPdfSuccess) {
                                    pdfName += '<p>文件：' + data.pdfName + '&nbsp;成功</p>';
                                    electron.listData.push(data);
                                } else {
                                    pdfName += '<p style="color: red">文件：' + data.pdfName + '&nbsp;失败</p>';
                                }
                            }
                            $("#pdfName").append(pdfName);*!/
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
            alert(messageA + "   " + "上传失败")
            $("#ol").html("");
            $("#showFileName").show();

        },*/
        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
            if ($("#showFileName").html() == "未选择文件") {
                alert("请选择文件");
            } else if (!vm.isNeedFileExtension) {
                alert("请上传pdf文件!");
            } else if (!vm.fileSizeIsFit) {
                alert("上传的文件不能大于2M");
            } else {
                //event.preventDefault();
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                formData.append('redTicketDataSerialNumber', vm.redTicketDataSerialNumber);
                formData.append('id', vm.aId);
                formData.append('businessType', vm.businessType);
                // formData.append('scanPath', electron.form.scanPathId);

                var url = baseURL + 'modules/openRedTicket/data/uploadRed';
                var loading = vm.getLoading("上传中...");
                $.ajax({
                    type: "POST",
                    url: url,
                    data: formData,
                    dataType: "json",
                    cache: false,//上传文件无需缓存
                    processData: false,//用于对data参数进行序列化处理 这里必须false
                    contentType: false, //必须
                    success: function (response) {
                        loading.close();
                        $("#showFileName").html('未选择文件');
                        if (response.code == 0) {

                            alert(response.msg);
                            vm.detailDialogRedNoticePicture = false;
                            //vm.examineQuery();
                             vm.findAll(vm.currentPage);
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


function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
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

function formatMoney(value) {
    return Vue.prototype.numberFormat2(null, null, value);
}

function formatInvoiceStatus(val) {

    if (val == 0) {
        return "正常";
    }
    if (val == 1) {
        return "失控";
    }
    if (val == 2) {
        return "作废";
    }
    if (val == 3) {
        return "红冲";
    } else {
        return "异常";
    }

}

function formatSourceSystem(val) {
    if (val == 0) {
        return "采集";
    } else if (val == 1) {
        return "查验";
    } else {
        return "录入";
    }
}

function formatQsType(val) {
    if (val == 0) {
        return "扫码签收";
    }
    if (val == 1) {
        return "扫描仪签收";
    }
    if (val == 2) {
        return "app签收";
    }
    if (val == 3) {
        return "导入签收";
    }
    if (val == 4) {
        return "手工签收";
    } else {
        return "pdf上传签收";
    }
}

function formatOutReason(val) {
    if (val == 1) {
        return "免税项目用";
    }
    if (val == 2) {
        return "集体福利,个人消费";
    }
    if (val == 3) {
        return "非正常损失";
    }
    if (val == 4) {
        return "简易计税方法征税项目用";
    }
    if (val == 5) {
        return "免抵退税办法不得抵扣的进项税额";
    }
    if (val == 6) {
        return "纳税检查调减进项税额";
    }
    if (val == 7) {
        return "红字专用发票通知单注明的进项税额";
    } else {
        return "上期留抵税额抵减欠税";
    }
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
 * 显示选择文件的窗口
 */
function showSelectFileWin() {
    $("#file").val("");
    $("#file").click();
}

function showSelectFileWin1() {
    // $("#files").val("");
    $("#files").click();
}

function getContextPath() {

    var pathName = document.location.pathname;

    var index = pathName.substr(1).indexOf("/");

    var result = pathName.substr(0, index + 1);

    return result;

}
