
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var vm = new Vue({
    el: '#rrapp',
    data: {
        startDateOptions:{},
        bnId:"",
        invoiceData:[],
        claimData:[],
        poData:[],
        imgList:[],
        tableData: [],
        pageCount: 0,
        options: [],
        currentPage: 1,
        readonly:true,
        selectFileFlag: '',
        listLoading4: false,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        listLoading: false,
        resultDetailForm:false,
        matchDetailForm:false,
        scanWin:false,
        importDialogFormVisible:false,
        importFileUrl: '',
        isNeedFileExtension: false,
        file: "",
        token: token,
        form:{
            poCode:'',
            claimNo: '',
            invoiceNo: '',
            venderid:'',
            matchDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            matchDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),

        },
        resultForm:{
            unmatchReason:'',
            buyTaxNo:'',
            sellTaxNo:'',
            matchNo:'',
            invSum:'',
            invTaxSum:'',
            invCount:'',
            bnSum:'',
            bnTaxSum:'',
            bnCount:'',
            matchStatus:'',
            matchUser:'',
            matchDate:'',
            unmatchUser:'',
            unmatchDateTime:'',
            unmatchDesc:'',
            auditUser:'',
            auditDateTime:'',
            status:'',
            auditDesc:'',
            resultDetailForm:false,
            entity:''
        },
        billDetailForm:{
            bnNo:"",
            createBy:"",
            createTime:"",
            vendorId:"",
            vendorName:"",
            vendorTaxNo:"",
            vendorType:"",
            vendorAddr:"",
            vendorPhone:"",
            vendorBank:"",
            vendorAccount:"",
            buyCompCode:"",
            buyCompanyName:"",
            buyAddr:"",
            buyTaxNo:"",
            buyPhone:"",
            buyBank:"",
            buyAccount:"",

            bnId:"",
            billType:"",
            takeTotalTaxAbleAmt:"",
            takeTotalTaxAmt:"",
            takeAmt:"",
            discountAmt:"",
            discountTaxAmt:"",
            discountTaxAbleAmt:"",
            totalAmt:"",
            totalTaxAmt:"",
            totalTaxAbleAmt:"",
        },
        costDetailForm:{
            entity:"",
            siteId:"",
            siteName:"",
            vendorId:"",
            vendorName:"",
            transactionType:"",
            poId:"",
            receiptId:"",
            posReceiptId:"",
            transactionQty:"",
            transactionNetCost:"",
            purchasingVat:"",
            transactionDate:"",
            vendorTaxNo:"",
            buyTaxNo:"",
            buyCompanyName:"",
            buyAddressTel:"",
            buyBankInfo :""

        },

        /***********************发票明细 开始*******************************/
        matchNo:"",
        tempValue: null,
        detailEntityList: [],//存放明细页面数据
        tempDetailEntityList: [],//暂存明细页面详情清单数据
        detailForm: {
            matchNo: null,
            matchStatus:  null,
            matchDate : null,
            matchUser : null,
            matchErrInfo : null,
            invoiceType: null,
            invoiceStatus: null,
            createDate: null,
            statusUpdateDate: null,
            qsBy: null,
            qsType: null,
            sourceSystem: null,
            qsDate: null,
            rzhYesorno: null,
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            dqskssq: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            outList: [],
            qsStatus: null,
            outStatus: null,
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
        queryData1:{
            // gfTaxNo:null,
            gfName:null,
            orgcode:null,
            usercode:null,
            username:null
        },
        formLabelWidth: '1.2rem',
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        /*******************************结束***********************************************/
    },
    mounted: function () {
       this.queryDetail();
        this.startDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch:{
        // 'form.matchId':{
        //     handler: function (val,oldValue) {
        //         var _this = this;
        //         var reg=/^[0-9]{0,}$/;
        //         if(!(reg.test(val)||val==null)){
        //             Vue.nextTick(function() {
        //                 _this.form.matchId = oldValue;
        //             })
        //         }
        //     },
        //     deep:true
        // }
    },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:first-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            } else {
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('.el-form-item__content>div:last-child div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            }
        },
        /**
         * 格式化发票金额
         */
        invoiceDecimal:function(row) {

            return vm.numberFormat(null,null, row.invoiceAmount);
        } ,
        poDecimal:function(row) {

            return vm.numberFormat(null,null, row.poAmount);
        } ,
        claimDecimal:function(row) {

            return vm.numberFormat(null,null, row.claimAmount);
        } ,
        invoiceDate3Change: function (val) {
            vm.form.matchDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.form.matchDateEnd = val;
        },
        /**
         * 格式化
         */
        bnSumDecimal:function(row) {

            return vm.numberFormat(null,null,row.bnSum);
        } ,
        query: function () {
            this.findAll(1);
        },



        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll(currentPage);
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){

                this.findAll(1);
            }
        },
        findAll: function (currentPage) {
            $(".checkMsg").remove();

            validateOnlyNumber(vm.form.poCode,0,"pohao","PO号只能为15位以内数字",15);
            validateOnlyNumber(vm.form.claimNo,0,"suopeihao","索赔号只能为15位以内数字",15);

            var checkDate=true;
            var poDateStart = new Date(vm.form.matchDateStart);
            var poDateEnd = new Date(vm.form.matchDateEnd);
            if ( poDateStart.getTime()+1000*60*60*24*364 < poDateEnd.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate=false;
            }else if(poDateEnd.getTime() < poDateStart.getTime()){
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate=false;
            }
            if(!(checkDate )){
                return;
            }
            var params = {
                page: currentPage,
                limit: this.pageSize,
                venderid:vm.queryData1.usercode,
                poCode:vm.form.poCode,
                claimNo:vm.form.claimNo,
                invoiceNo:vm.form.invoiceNo,
                matchDateStart: vm.form.matchDateStart,
                matchDateEnd: vm.form.matchDateEnd
            };
            vm.listLoading = true;
            var flag = false;
            this.$http.post(baseURL  + 'modules/posuopei/matchQuery/list',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                flag = true;
                vm.total = xhr.page.totalCount;
                vm.currentPage = xhr.page.currPage;
                vm.pageCount = xhr.page.totalPage;
                vm.tableData = xhr.page.list;
                vm.listLoading = false;
            });
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);


        },

        // queryDetail:function(){
        //     $.ajax({
        //         url: baseURL + 'modules/posuopei/getDefaultMessage',
        //         type: "POST",
        //         contentType: "application/json",
        //         dataType: "json",
        //         success: function (r) {
        //             if (r.code == 0) {
        //                 // vm.queryData1.orgcode=r.orgEntity.orgcode
        //                 vm.form.venderid=r.orgEntity.usercode
        //                 // vm.queryData1.username=r.orgEntity.username
        //             //
        //             }
        //         }
        //     });
        // },
        /**
         * 格式化匹配时间
         */
        formatMatchDate: function (row, column) {
            if (row.matchDate != null) {
                return dateFormat(row.matchDate);
            } else {
                return '';
            }
        },
        /*****************************发票明细  开始*************************************/

        invoiceDetail : function (value) {

            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
            $.ajax({
                type: "POST",
                url: baseURL + "modules/posuopei/invoice/detail",
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
                        vm.detailForm.matchDate = r.invoiceEntity.matchDate.substring(0, 10);
                        vm.detailForm.matchStatus = r.invoiceEntity.matchStatus;
                        if (r.invoiceEntity.gfTaxNo == personalTaxNumber) {
                            vm.detailForm.gfTaxNo = "";
                        } else {
                            vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                        }
                        vm.detailForm.invoiceStatus = formatInvoiceStatus(r.invoiceEntity.invoiceStatus);
                        vm.detailForm.sourceSystem = formatSourceSystem(r.invoiceEntity.sourceSystem);
                        vm.detailForm.createDate = r.invoiceEntity.createDate;
                        vm.detailForm.statusUpdateDate = r.invoiceEntity.statusUpdateDate;
                        vm.detailForm.qsType = formatQsType(r.invoiceEntity.qsType);
                        vm.detailForm.qsBy = r.invoiceEntity.qsBy;
                        vm.detailForm.qsDate = r.invoiceEntity.qsDate;
                        vm.detailForm.rzhYesorno = r.invoiceEntity.rzhYesorno;
                        vm.detailForm.dqskssq = r.invoiceEntity.dqskssq;
                        vm.detailForm.rzhDate = r.invoiceEntity.rzhDate;
                        vm.detailForm.outDate = r.invoiceEntity.outDate;
                        vm.detailForm.outBy = r.invoiceEntity.outBy;
                        vm.detailForm.outReason = formatOutReason(r.invoiceEntity.outReason);
                        vm.detailForm.qsStatus = r.invoiceEntity.qsStatus;
                        vm.detailForm.outStatus = r.invoiceEntity.outStatus;
                        vm.detailForm.checkCode = r.invoiceEntity.checkCode;
                        vm.detailForm.gfName = r.invoiceEntity.gfName;
                        vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                        vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                        vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                        vm.detailForm.xfName = r.invoiceEntity.xfName;
                        vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                        vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                        vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                        vm.detailForm.remark = r.invoiceEntity.remark;
                        vm.detailForm.totalAmount = vm.numberFormat(null, null, r.invoiceEntity.totalAmount);
                        vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                        vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                        vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                        //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
                        if (r.invoiceEntity.invoiceType == "03") {
                            vm.detailForm.taxAmount = vm.numberFormat(null, null, r.invoiceEntity.taxAmount);
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
                                vm.detailForm.invoiceAmount = vm.numberFormat(null, null, r.invoiceEntity.invoiceAmount);
                                vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                                vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                                vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                                vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                            }
                            vm.detailDialogVehicleFormVisible = true;
                        } else if (r.invoiceEntity.invoiceType == "14") {
                            vm.detailDialogCheckFormVisible = true;
                            vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                            vm.detailEntityList = r.detailEntityList;
                            for (var i = 0; i < vm.detailEntityList.length; i++) {
                                vm.detailEntityList[i].unitPrice = vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice) ;
                                vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList = null;
                            }
                        } else {
                            vm.detailDialogFormVisible = true;
                            vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                            vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                            vm.detailEntityList = r.detailEntityList;
                            for (var i = 0; i < vm.detailEntityList.length; i++) {
                                vm.detailEntityList[i].unitPrice =  vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice);
                                vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                            }
                            vm.tempDetailEntityList = vm.detailEntityList
                            if (r.detailEntityList.length > 8) {
                                vm.detailEntityList = null;
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
        exportData:function(){
            document.getElementById("ifile").src = baseURL + sysUrl.matchImportExport;
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                this.isNeedFileExtension = false;
                $("#file").val('');
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
        },
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
                // formData.append('gfName',this.queryData1.gfName)
                // formData.append('jvcode',this.queryData1.orgcode)
                formData.append('venderid',this.queryData1.usercode)
                formData.append('venderName',this.queryData1.username)
                formData.append('orgtype','8')


                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };


                var flag = false;
                var hh;

                var url = baseURL + sysUrl.matchImport;
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    vm.file = '';
                    $("#file").html("");
                    if (response.data.success) {
                        alert(response.data.result);
                        vm.importDialogFormVisible = false;
                    } else {
                        vm.importDialogFormVisible = false;
                        alert(response.data.result);
                    }
                }, function(err) {
                    $("#file").html("");
                    if (err.status == 408) {
                        vm.importDialogFormVisible = false;
                        alert(response.data.result);
                    }
                })

                $("#file").val("");

                var intervelId = setInterval(function () {
                    if (flag) {
                        hh = $(document).height();
                        $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                        clearInterval(intervelId);
                        return;
                    }
                }, 50);
            }
        },
        /************************************结束***********************************/
        queryDetail:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.queryData1.usercode=r.orgEntity.usercode
                        vm.queryData1.username=r.orgEntity.orgname

                    }
                }
            });
        },
        resultDetail:function (row) {
            var params={
                matchno:row.matchno
            }
            $.ajax({
                type: "POST",
                url: baseURL + "modules/posuopei/matchQuery/detail",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (r) {
                        debugger
                        vm.invoiceData= r.invoiceList;
                        vm.poData=r.poList;
                        vm.claimData=r.claimList;
                        vm.matchDetailForm = true;



                }
            });



        },

        importFormCancel: function () {
            vm.importDialogFormVisible = false;
            $("#file").val("");
        },
        MatchCancel:function (row) {
            var params={
                matchno:row.matchno
            }
            $.ajax({
                type: "POST",
                url: baseURL + "modules/posuopei/matchQuery/cancel",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (r) {
                    if(r.code===0){
                        alert(r.msg);
                        vm.findAll(1);
                    }else {
                        alert(r.msg);
                    }

                }
            });



        },
        /**
         * 关闭结果明细
         */
        resultDetailFormCancel: function () {

            vm.matchDetailForm = false;
            vm.invoiceData= [];
            vm.poData=[];
            vm.claimData=[];
        },

        /**
         * 关闭匹配明细
         */
        matchDetailFormCancel: function () {
            vm.matchDetailTableData=[];
            vm.billDetailTableData=[];
            vm.matchDetailForm = false;
        },
        scanWinCancel:function(){
            vm.scanWin=false;
        },

        scanWinShow:function(row){
            var params={
                matchno:row.matchno
            }
            $.ajax({
                type: "POST",
                url: baseURL + "modules/posuopei/matchQuery/img",
                contentType: "application/json",
                data: JSON.stringify(params),
                success: function (r) {
                    if(r.imgList.length>0){

                       for(var i=0;i<r.imgList.length;i++){
                           r.imgList[i]=baseURL+'rest/invoice/sign/getImg?scanId='+r.imgList[i];
                       }
                        vm.imgList=r.imgList;
                        vm.scanWin=true;

                    }else {
                        alert("找不到相关图片！")
                    }





                }
            });


        },

        settlementamountDecimal:function(row) {
            return vm.numberFormat(null,null, row.settlementamount);
        } ,
        amountpaidFormat: function (row) {
            return decimal(row.amountpaid);
        },
        amountunpaidFormat: function (row) {
            return decimal(row.amountunpaid);
        },
        receiptAmountFormat: function (row) {
            return decimal(row.receiptAmount);
        },
        receiptdateFormat: function (row) {
            if (row.receiptdate != null) {
                return formaterDate(row.receiptdate);
            } else {
                return '—— ——';
            }
        },
        claimAmountFormat: function (row) {
            return decimal(row.claimAmount);
        },
        postdateFormat: function (row) {
            if (row.postdate != null) {
                return formaterDate(row.postdate);
            } else {
                return '—— ——';
            }
        },

        blueTaxAmountFormat:function (row) {
            return decimal(row.blueTaxAmt);
        },

        /**
         * 格式化发票金额
         */
        invoiceAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.invoiceAmount);
        },
        /**
         * 格式化开票时间
         */
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        /**
         * 格式化创建时间
         */
        formatCreateTime: function (row, column) {
            if (row.createTime != null) {
                return dateFormat(row.createTime);
            } else {
                return '—— ——';
            }
        },
        formatterTransactionDate: function (row, column) {
            if (row.transactionDate != null) {
                return dateFormat(row.transactionDate);
            } else {
                return '—— ——';
            }
        },
        invoiceDateFormat:function (row) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        /**
         * 格式化发票税额
         */
        taxAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.taxAmount);
        },
        totalAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.totalAmount);
        },
        transactionNetCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionNetCost);
        },
        purchasingVatDecimal: function (row) {

            return vm.numberFormat(null, null, row.purchasingVat);
        },
        transactionEstLandedCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionEstLandedCost);
        },
        /**
         * 费用明细列表税额、金额格式化处理
         **/
        formatterSpecialDiscountVat:function (row) {

            return vm.numberFormat(null, null, row.specialDiscountVat);
        },

        formatterSpecialDiscountAmt:function (row) {

            return vm.numberFormat(null, null, row.specialDiscountAmt);
        },
        importIn:function(){
            vm.importDialogFormVisible = true;

            $("#file").val("");
        }
    }
});
function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}

/**
 *格式化金额
 */
function formatMoney(value){
    return Vue.prototype.numberFormat(null,null,value);
}
/**
 * 格式化金额
 * @param cellvalue
 * @returns {*}
 */
function decimal(cellvalue) {
    if (cellvalue != null) {
        var val = Math.round(cellvalue * 100) / 100;
        return val.formatMoney1();
    }
    return "0.00";
}
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
Number.prototype.formatMoney1 = function (places, symbol, thousand, decimal) {
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var number = this,
        negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};
/**
 * 格式化时间
 * @param cellvalue
 * @returns {string}
 */
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
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
function detailDateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}