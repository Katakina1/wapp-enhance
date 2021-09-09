
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    gfName: "***",
    xfName: null,
    invoiceCode:'',
    invoiceNo:'',
    createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth())+"-01",
    createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    invoiceType: "-1",
    page: 1,
    limit: 1,
    multipleSelection: [],
    store:null,
    redLetterNotice:'',
    redNoticeNumber:'',
    serialNumber:null,
};

var vm = new Vue({
    el:'#rrapp',
    data:{
        selectFileFlag: '',
        id:'',
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
        tempTableData: [],

        xl:[],

        total2: 0,
        currentPage2:1,
        totalPage2: 0,
        pageSize2: PAGE_PARENT.PAGE_SIZE,
        pageList2: PAGE_PARENT.PAGE_LIST,
        pagerCount2: 5,
        tableData2: [],

        serialNumber:'',
        redLetterNotice:'',
        redNoticeNumber:'',
        listLoading: false,
        totalInvoiceAmount:0,
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
        invoicequery:{
            invoiceCode:'',
            invoiceNo: null,
            invoiceAmount: null,
            taxRate: null,
            taxAmount:null,
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
            redLetterNotice:null,
            redNoticeNumber:null

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
        listLoading4:false,
        batchRedTicketDialog:false,
        detailDialogRedNotice :false,
        enterRedTicketDialog:false,
        updateRedTicketDialog:false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        scarletLetterDialog: false,
        uploadLetterDialog:false,
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
        this.createDateOptions3 = {
            disabledDate: function(time){
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }

        };

        this.createDateOptions4 = {
            disabledDate: function(time){
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }

        };
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
        importFormCancel: function () {
            vm.batchRedTicketDialog = false;
        },
        batchExport:function(){
            this.selectFileFlag = '';
            this.file = '';
            vm.batchRedTicketDialog = true;
        },
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
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

                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.tempTableData = this.tableData;
                this.tableData = [];
                this.listLoading = true;

                var flag = false;
                var hh;
                var url = baseURL + "modules/redInvoiceManager/InputRedTicketInformationImport";
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    // if(response.data.errorCount>0){
                    //     alert("请检查数据格式是否正确！");
                    //     return;
                    // }
                    if (response.data.success) {
                        if (this.tempTableData.length + response.data.reason.length + response.data.errorCount > 50000) {
                            this.tableData = this.tempTableData;
                            alert('导入数据超过50000条，请修改模板！');
                            return;
                        }

                        if (response.data.errorCount>0 && response.data.errorCount1>0 && response.data.errorCount2>0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，系统中没有对应的红票通知单号" + response.data.errorCount1 +
                                "条导入失败；excle数据格式有误的有"+ response.data.errorCount + "条导入失败；excle数据红票通知单重复的有" + response.data.errorCount2 + "条导入失败。");
                        }

                        if (response.data.errorCount==0 && response.data.errorCount1>0 && response.data.errorCount2>0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，系统中没有对应的红票通知单号" + response.data.errorCount1 +
                                "条导入失败；excle数据红票通知单重复的有" + response.data.errorCount2 + "条导入失败。");
                        }

                        if (response.data.errorCount>0 && response.data.errorCount1==0 && response.data.errorCount2>0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，excle数据格式有误的有"+ response.data.errorCount + "条导入失败；excle数据红票通知单重复的有" + response.data.errorCount2 + "条导入失败。");
                        }

                        if (response.data.errorCount>0 && response.data.errorCount1>0 && response.data.errorCount2==0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，系统中没有对应的红票通知单号" + response.data.errorCount1 +
                                "条导入失败；excle数据格式有误的有"+ response.data.errorCount + "条导入失败。");
                        }

                        if (response.data.errorCount==0 && response.data.errorCount1==0 && response.data.errorCount2>0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，excle数据红票通知单重复的有" + response.data.errorCount2 + "条导入失败。");
                        }

                        if (response.data.errorCount==0 && response.data.errorCount1>0 && response.data.errorCount2==0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，系统中没有对应的红票通知单号" + response.data.errorCount1 +
                                "条导入失败。");
                        }
                        if (response.data.errorCount>0 && response.data.errorCount1==0 && response.data.errorCount2==0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，excle数据格式有误的有"+ response.data.errorCount + "条导入失败。");
                        }
                       /* if (response.data.errorCount3>0 && response.data.errorCount1==0 && response.data.errorCount2==0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount3 + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条，发票购方税号与当前登录用户税号不一致的有"+ response.data.errorCount3 + "条导入失败。");
                        }*/
                        if (response.data.errorCount==0 && response.data.errorCount1==0 && response.data.errorCount2==0) {
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount + response.data.errorCount1+ response.data.errorCount2) + "条，成功"
                                + response.data.reason.length + "条。");
                        }

                        this.selectFileFlag = '';
                        this.file = '';
                        $("#upload_form")[0].reset();

                        vm.batchRedTicketDialog = false;
                        this.listLoading = false;

                    }else {
                        vm.batchRedTicketDialog = false;
                        $("#file").html("");
                        alert(response.data.reason);
                    }
                }, function(err) {

                    if (err.status == 408) {
                        vm.batchRedTicketDialog = false;
                        alert(response.data.reason);
                    }
                })
            }
        },
        /**
         * 选择文件事件
         * @param event 事件触发点
         * @return {boolean}
         */
        exportData:function(){
            document.getElementById("ifile").src = baseURL + "export/redInvoiceManager/InputRedTicketInformation";
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
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
        showSelectFileWin:function() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        resetRedTicket:function(){
            vm.invoicequery.invoiceNo = '';
            vm.invoicequery.invoiceCode = '';
            vm.invoicequery.invoiceAmount = '';
            vm.invoicequery.taxRate = '';
            vm.invoicequery.taxAmount = '';
            vm.invoicequery.totalAmount = '';
            vm.invoicequery.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate());
            vm.invoiceUpdate.invoiceNo = '';
            vm.invoiceUpdate.invoiceCode = '';
            vm.invoiceUpdate.invoiceAmount = '';
            vm.invoiceUpdate.taxRate = '';
            vm.invoiceUpdate.totalAmount = '';
            vm.invoiceUpdate.taxAmount='';
            vm.invoiceUpdate.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate());
        },
        enterRedTicket:function(row){
            vm.serialNumber= row.serialNumber;
            vm.redTicketMatchId = row.id;
            vm.redNoticeNumber = row.redNoticeNumber;
            vm.enterRedTicketDialog = true;
        },
        emptyRedTicket:function (row) {
            // emptyparams = {
            //     'id' : row.id,
            //     'redNoticeNumber':row.redNoticeNumber
            // };
            var id = row.id;
            confirm('确定清空数据吗？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "redInvoiceManager/inputRedTicketInformation/emptyRedTicket",
                    contentType: "application/json",
                    data: JSON.stringify({id:id}),
                    success: function (r) {

                        if (r.code == 0) {
                            alert('清空成功');
                            vm.detailDialogRedNotice = false;
                            // vm.queryinformation(row);

                        }else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                var hostHref = parent.location.href;
                                if(hostHref.indexOf("int")!=-1){
                                    parent.location.href ="http://rl.wal-mart.com";
                                }else if(hostHref.indexOf("ext")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else{
                                    parent.location.href = baseURL + 'login.html';
                                }
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        enterRedTicketDialogCancel:function(){
            vm.enterRedTicketDialog = false;
            vm.invoicequery.invoiceNo = null;
            vm.invoicequery.invoiceCode = null;
            vm.invoicequery.invoiceAmount = null;
            vm.invoicequery.taxRate = null;
            vm.invoicequery.totalAmount = null;
            //vm.invoicequery.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + format2(new Date().getDate());
        },
        updateRedTicketDialogCancel:function(){
            vm.updateRedTicketDialog = false;
        },
        updateRedTicket:function(row){
            vm.redNoticeNumber= row.redNoticeNumber;
            vm.serialNumber= row.serialNumber;
            vm.redTicketMatchId= row.id;
            var params = {
                'id': row.id
            };
            this.$http.post(baseURL + 'redInvoiceManager/inputRedTicketInformation/selectRedTicketById',
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
                        'serialNumber': vm.serialNumber,
                        'id': vm.redTicketMatchId,
                        'redNoticeNumber': vm.redNoticeNumber,
                        'totalInvoiceAmount':vm.totalInvoiceAmount,
                        'invoiceNo': vm.invoicequery.invoiceNo,
                        'invoiceCode': vm.invoicequery.invoiceCode,
                        'invoiceAmount': vm.invoicequery.invoiceAmount,
                        'taxRate': vm.invoicequery.taxRate,
                        'taxAmount': vm.invoicequery.taxAmount,
                        'totalAmount': vm.invoicequery.totalAmount,
                        'invoiceDate': vm.invoicequery.invoiceDateStart
                    };


                    vm.$http.post(baseURL + 'redInvoiceManager/inputRedTicketInformation/saveRedTicket',
                        params,
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (r) {
                        var xhr = r.body;
                        console.log(r)
                        if (xhr.code == 0) {

                            // vm.invoiceData = r.page;
                            alert('保存成功');
                            vm.enterRedTicketDialog = false;
                            vm.detailDialogRedNotice = false;
                            vm.invoicequery.invoiceNo = null;
                            vm.invoicequery.invoiceCode = null;
                            vm.invoicequery.invoiceAmount = null;
                            vm.invoicequery.taxRate = null;
                            vm.invoicequery.totalAmount = null;
                            vm.invoicequery.taxAmount = null;
                            // vm.queryinformation(vm.redTicketMatchId);

                        } else if (xhr.code == 488) {
                            alert(xhr.msg);
                            vm.listLoading = false;
                        }else {
                            alert(xhr.msg);
                            vm.listLoading = false;
                        }

                    });
                }else {
                    return false;
                }
            })
        },
        invoiceDate3Change: function (val) {
            vm.invoicequery.invoiceDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.invoiceUpdate.invoiceDateStart = val;
        },
        updateRedTicketSave:function(){
            vm.$refs['invoiceUpdate'].validate(function (valid) {
                if (valid) {
                    var params = {
                        'serialNumber': vm.serialNumber,
                        'id': vm.redTicketMatchId,
                        'redNoticeNumber': vm.redNoticeNumber,
                        'invoiceNo': vm.invoiceUpdate.invoiceNo,
                        'invoiceCode': vm.invoiceUpdate.invoiceCode,
                        'invoiceAmount': vm.invoiceUpdate.invoiceAmount,
                        'taxRate': vm.invoiceUpdate.taxRate.toString(),
                        'taxAmount': vm.invoiceUpdate.taxAmount,
                        'totalAmount': vm.invoiceUpdate.totalAmount,
                        'invoiceDate': vm.invoiceUpdate.invoiceDateStart,
                        // 'checkNo': vm.invoiceUpdate.checkNo ,
                        'flag1': 'a'
                    };

                    vm.$http.post(baseURL + 'redInvoiceManager/inputRedTicketInformation/saveRedTicket',
                        params,
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (r) {
                        //var xhr = res.body;
                        console.log(r)
                        if (r.body.code == 0) {
                            alert('保存成功');
                            vm.updateRedTicketDialog = false;
                            vm.detailDialogRedNotice = false;
                            // vm.queryinformation(vm.redTicketMatchId);
                        } else if (r.body.code == 488) {
                            alert(r.body.msg);
                            vm.listLoading = false;
                        }else   {
                            alert(r.body.msg);
                            vm.listLoading = false;
                        }

                    });
                }else {
                    return false;
                }
            })
        },
        codeIn:function(){
            if(this.invoicequery.invoiceCode!=null &&this.invoicequery.invoiceCode!=''&&this.invoicequery.invoiceNo!=null && this.invoicequery.invoiceNo!=''){
                currentInvoiceParam1 = {
                    invoiceNo: vm.invoicequery.invoiceNo,
                    invoiceCode:vm.invoicequery.invoiceCode,
                    redNoticeNumber: vm.redNoticeNumber,
                };
                $.ajax({
                    url:baseURL + 'redInvoiceManager/inputRedTicketInformation/query',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    /*async:false,*/
                    data:JSON.stringify(currentInvoiceParam1),
                    success:function (r) {
                        if(r.code==0){
                            var flag=true;
                            if(r.page.list.length>0) {

                            }
                        }else if(r.code==488){

                            alert(r.msg);
                            vm.invoicequery.invoiceNo = null;
                            vm.invoicequery.invoiceCode = null;
                            vm.invoicequery.invoiceAmount = null;
                            vm.invoicequery.taxRate = null;
                            vm.invoicequery.totalAmount = null;
                            vm.invoicequery.invoiceDateStart= new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + format2(new Date().getDate());
                            // vm.enterRedTicketDialog = false;
                            //vm.detailDialogRedNotice = false;
                        }


                    }
                });
            }
        },
        codeIn2:function(){
            if(this.invoiceUpdate.invoiceCode!=null &&this.invoiceUpdate.invoiceCode!=''&&this.invoiceUpdate.invoiceNo!=null && this.invoiceUpdate.invoiceNo!=''){
                currentInvoiceParam1 = {
                    invoiceNo: vm.invoiceUpdate.invoiceNo,
                    invoiceCode:vm.invoiceUpdate.invoiceCode,
                    invoiceDate: vm.invoiceUpdate.invoiceDateStart,

                };
                $.ajax({
                    url:baseURL + 'redInvoiceManager/inputRedTicketInformation/query',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    /*async:false,*/
                    data:JSON.stringify(currentInvoiceParam1),
                    success:function (r) {
                        if(r.code==0){

                            // vm.invoiceData = r.page;
                            var flag=true;
                            if(r.page.list.length>0) {

                            }
                        }else if(r.code==488){
                            alert(r.msg);

                        }
                    }
                });
            }
        },
        queryinformation: function (row) {
            vm.detailDialogRedNotice = true;
            var  value = row.id;
            // var value2 = row.serialNumber;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
            var params = {
                'id': value,
                // 'serialNumber':value2,
                'page': 1,
                'limit': 12
            };
            this.$http.post(baseURL + 'redInvoiceManager/inputRedTicketInformation/invoicelist',
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
        batchRedTicketExport:function(){
            var params = {
                'store':vm.form.store ==null?'':vm.form.store,
                'redNoticeNumber':vm.form.redNoticeNumber==null?'':vm.form.redNoticeNumber,
                'createDate1':vm.form.createDate1,
                'createDate2':vm.form.createDate2
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':36,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");
                    }
                }
            });

            var uri = baseURL + 'export/redInvoiceManager/batchRedTicketExport' +'?params='+JSON.stringify(params);
            document.getElementById("ifile").src = encodeURI(uri);

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
                        'redNoticeNumber':vm.form.redNoticeNumber
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },

        createDate1Change: function(val) {
            vm.form.createDate1 = val;
        },
        createDate2Change: function(val) {
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
            vm.scarletLetterDialog = false;
            vm.detailDialogFormVisible = false;
            vm.detailDialogRedNotice = false;
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
            this.$http.post(baseURL + 'redInvoiceManager/inputRedTicketInformation/list',
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
                return "";
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

function getContextPath(){

    var pathName = document.location.pathname;

    var index = pathName.substr(1).indexOf("/");

    var result = pathName.substr(0,index+1);

    return result;

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