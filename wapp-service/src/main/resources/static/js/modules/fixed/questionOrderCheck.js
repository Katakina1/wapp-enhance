
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var vm = new Vue({
    el: '#rrapp',
    data: {
        listLoading: false,
        listLoading1: false,
        listLoading2: false,
        listLoading3: false,
        checkUnPass:false,
        pageCount: 0,
        multipleSelection: [],
        options: [],
         result:{},
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        rejectWin:false,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        forms:{

        	invoiceNo:"",
        	venderid:"",
        	orderNo:'',
            username:null,
            matchDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
           matchDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            reason:null
        },

        readonly:false,
        questionPaperData:[],
        questionPaper:false,
        filePage:false,
        claimChangeData:[],
        poChangeData:[],
        fileData:[],
        rejectForm:{
            reason:'',
        },
        countChangeData:[],
        startDateOptions:{},
        endDateOptions:{},
        /***********************发票明细 开始*******************************/
        matchNo: "",
        tempValue: null,
        detailEntityList: [],//存放明细页面数据
        tempDetailEntityList: [],//暂存明细页面详情清单数据
        detailForm: {
            matchno: null,
            matchStatus: null,
            matchDate: null,
            matchUser: null,
            matchErrInfo: null,
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
        formLabelWidth: '1.2rem',
        questionTypeArray:[],
        dialogFormVisible: false,
        selectDetailDialogPicture:false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        /******************************结束*********************************/

    },
    mounted: function () {
        this.queryQuestionType();
        this.startDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.endDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch:{
        'forms.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,12}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.orderNo = oldValue;
                    })
                }
            }
        },
        'forms.orderNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,20}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.forms.orderNo = oldValue;
                    })
                }
            }
        },
        'forms.venderid': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.forms.venderid= oldValue;
                    })
                }
            }
        },
        'forms.invoiceNo':{
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.forms.invoiceNo = oldValue;
                    })
                }
            }
        }
    },
    methods: {


    queryQuestionType: function () {

        var theKey='供应商红票问题单类型';

        $.ajax({
            url: baseURL + 'modules/posuopei/queryPart',
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            data:theKey,
            success: function (r) {
                if (r.code == 0) {
                    var gfs = [];
                    for (var i = 0; i < r.List.length; i++) {
                        var gf = {};
                        gf.label = r.List[i].dictname;
                        gf.value = r.List[i].dictcode;
                        gf.status = r.List[i].dictcode;
                        gfs.push(gf);
                    }
                    vm.questionTypeArray = gfs;
                }
            }
        });
    },
        rejectWinClose:function () {
            vm.rejectWin=false;
            vm.rejectForm.reason='';
        },
        submitReason:function () {
            this.$refs['rejectForm'].validate(function (valid) {
                if (valid) {
                    var ids = getIds();
                    var  result={
                        ids:ids,
                        result:"2",
                        reason:vm.rejectForm.reason
                    };

                    $.ajax({
                        url: baseURL + 'modules/fixed/QuestionOrderCheck/check',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(result),
                        success: function (r) {
                            /*if (r.code == 0) {
                            }*/
                            if(r.msg=="0"){
                                vm.findAll(1);
                                alert("保存审核结果成功！")
                                vm.rejectWinClose();
                            }else{
                                alert("保存审核结果失败！")
                            }
                        }
                    });
                } else {
                    return false
                }
            })



        },
        query: function () {
            this.findAll(1);
        },

        dateFormat:function(a,b,c){
            return dateFormatStrToYMD(c);
        },
        moneyFormat:function(a,b,c){
            return moneyFormat(c);
        },
        changeFun: function (row) {
            this.multipleSelection = row;
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
        detailFormCancelA: function () {
            vm.selectDetailDialogPicture = false;

        },
        checkUnPassCancel:function () {
            vm.checkUnPass=false;
        },
        findAll: function (currentPage) {

        	if(currentPage==10){
        		validateDateTypeA(vm.forms.matchDate1,vm.forms.matchDate2);
        		var params = {
        				page: 1,
        				limit: this.pageSize,
        				invoiceNo:vm.forms.invoiceNo,
        				orderNo:vm.forms.orderNo,
        				venderid:vm.forms.venderid,
        				matchDate1:vm.forms.matchDate1,
        				matchDate2:vm.forms.matchDate2
        				
        				
        		};
        	}else{
        		
        		validateDateTypeA(vm.forms.matchDate1,vm.forms.matchDate2);
        		vm.forms.invoiceNo = validateOnlyNumber(vm.forms.invoiceNo,0,'invoice',"请输入数字格式发票号码",8);
        		vm.forms.venderid = validateOnlyNumber(vm.forms.venderid,0,'vender',"请输入数字格式供应商号",8);
        		vm.forms.orderNo = validateOnlyNumber(vm.forms.orderNo,0,'num',"请输入数字格式订单号",15);
        		
        		var params = {
        				page: currentPage,
        				limit: this.pageSize,
        				invoiceNo:vm.forms.invoiceNo,
        				orderNo:vm.forms.orderNo,
        				venderid:vm.forms.venderid,
        				matchDate1:vm.forms.matchDate1,
        				matchDate2:vm.forms.matchDate2
        				
        				
        		};
        	}
        		vm.listLoading = true;
        		this.$http.post(baseURL +"modules/fixed/QuestionOrderCheck/list",
        				params, {
        			'headers': {
        				"token": token
        			}
        		}).then(function (response) {
        			this.total = response.data.page.totalCount;
                    this.pageCount=response.data.page.totalPage;
        			this.questionPaperData = [];
        			flag = true;
        			for (var key in response.data.page.list) {
        				this.$set(this.questionPaperData, key, response.data.page.list[key]);
        				//alert(response.data.page.list[key])
        			}
        			this.listLoading = false;
        		}).catch(function (response) {
        			alert(response.data.msg);
        			this.listLoading = false;
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
        uncheck:function(){
            vm.rejectWin=true;
        },
        check:function (par) {
            var ids = getIds();

            if(par==1){
            	
            	result={
            			ids:ids,
            			result:par,
            			reason:''
            	}
            }else{
            	result={
            			ids:ids,
            			result:par,
            			reason:''
            	}
            }
         
                $.ajax({
                    url: baseURL + 'modules/fixed/QuestionOrderCheck/check',
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(result),
                    success: function (r) {
                        /*if (r.code == 0) {
                        }*/
                    	if(r.msg=="0"){
                    		vm.findAll(10);
                    		alert("保存审核结果成功！")
                    	}else{
                    		alert("保存审核结果失败！")
                    	}
                    }
                });


            
        },
        
        
        //*********************************打印功能*******************************
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
        unPass:function () {
            result.result=2;
            result.reason=vm.forms.reason

            $.ajax({
                url: baseURL + 'modules/posuopei/question/check',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(result),
                success: function (r) {
                    if (r.code == 0) {
                        vm.checkUnPass=false;
                        vm.findAll(1)
                    }
                }
            });

        },
        questionPaperCancel:function () {
            vm.questionPaper=false;
        },
        filePageCancel:function () {
            vm.filePage=false;
        },

      //*************************文件查看***********************************
        scanWinShow:function (row) {
            vm.filePage=true;
        	vm.listLoading1 = true;
        	var params={
        			 
        			id:row.id
        			//questionType:row.questionType
        	}
        	this.$http.post(baseURL + "modules/fixed/QuestionOrderCheck/fileInfo",
        			params, {
        		'headers': {
        			"token": token
        		}
        	}).then(function (response) {
        		//this.total = response.data.page.totalCount;
        		//this.totalPage = response.data.page.totalPage;
        		
        		this.fileData=[];
        		for (var key in response.data.file) {
        			this.$set(this.fileData, key, response.data.file[key]);
        			
        		}
        		//$(".count").addClass("hideItem");
        		$(".filepath").removeClass("hideItem");
        		//$(".po").removeClass("hideItem");
        		this.listLoading1 = false;
        	}).catch(function (response) {
        		alert(response.data.msg);
        		this.listLoading1 = false;
        	});
        },
        //*************************结束***********************************
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
                    document.getElementById("viewInvoicesImg").src = baseURL + 'modules/fixed/QuestionOrderCheck/getImageForAll?id=' + id + "&token=" + token;
                },
                error: function () {

                }
            });
        },
        
        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'modules/fixed/QuestionOrderCheck/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        resultDetail:function (row) {
            vm.questionPaper=true;
        	vm.listLoading1 = true;
        	var params={
        			 
        			id:row.id
        			//questionType:row.questionType
        	}
        	this.$http.post(baseURL + "modules/fixed/QuestionOrderCheck/orderInfo",
        			params, {
        		'headers': {
        			"token": token
        		}
        	}).then(function (response) {
        		//this.total = response.data.page.totalCount;
        		//this.totalPage = response.data.page.totalPage;
        		
        		this.poChangeData=[];
        		for (var key in response.data.order) {
        			this.$set(this.poChangeData, key, response.data.order[key]);
        			
        		}
        		$(".count").addClass("hideItem");
        		$(".claim").removeClass("hideItem");
        		$(".po").removeClass("hideItem");
        		this.listLoading1 = false;
        	}).catch(function (response) {
        		alert(response.data.msg);
        		this.listLoading1 = false;
        	});
        	this.$http.post(baseURL + "modules/fixed/QuestionOrderCheck/invoiceInfo",
        			params, {
        		'headers': {
        			"token": token
        		}
        	}).then(function (response) {
        		//this.total = response.data.page.totalCount;
        		//this.totalPage = response.data.page.totalPage;
        		this.claimChangeData = [];
        		
        		for (var key in response.data.page) {
        			this.$set(this.claimChangeData, key, response.data.page[key]);
        			
        		}
        		
        		$(".count").addClass("hideItem");
        		$(".claim").removeClass("hideItem");
        		$(".po").removeClass("hideItem");
        		this.listLoading1 = false;
        	}).catch(function (response) {
        		alert(response.data.msg);
        		this.listLoading1 = false;
        	});
        	

        },
  
        
        /*****************************发票明细  开始*************************************/

        invoiceDetail: function (value) {
        	//vm.detailDialogFormVisible=true;
            $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
            $.ajax({
                type: "POST",
                url: baseURL + "modules/fixed/matchQuery/invoice",
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
                        vm.detailForm.matchDate = r.invoiceEntity.fixedMatchDate;
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
                                vm.detailEntityList[i].unitPrice = vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice);
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
                                vm.detailEntityList[i].unitPrice = vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice);
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

        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }else{
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }
        },
        queryDetails:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.forms.invoiceNo=r.orgEntity.invoiceNo
                        vm.forms.username=r.orgEntity.orgname

                    }
                }
            });
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        questionDateStartChanges: function (val) {
            vm.forms.matchDate1 = val;
        },
        questionDateEndChanges: function (val) {
            vm.forms.matchDate2 = val;
        },
       
        /**
         * 格式化匹配时间
         */
        formatMatchDate: function (row, column) {
            if (row.matchDate != null) {
                return dateFormat(row.matchDate);
            } else if(row.invoiceDate !=null){
            	
                return dateFormat(row.invoiceDate);
     	
            }else if(row.orderDate !=null){
            	
                return dateFormat(row.orderDate);
     	
            }else {
                return '';
            }
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
        
       
    }
    
});
function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
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


function detailDateFormat(value) {
    var tempInvoiceDate = new Date(value);
    var tempYear = tempInvoiceDate.getFullYear() + "年";
    var tempMonth = tempInvoiceDate.getMonth() + 1;
    var tempDay = tempInvoiceDate.getDate() + "日";
    var temp = tempYear + tempMonth + "月" + tempDay;
    return temp;
}
/***************获取选中的id******************/
function getIds() {
    var selection = vm.multipleSelection;
    
    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var ids = [];
    for (var i = 0; i < selection.length; i++) {
        ids.push(selection[i].id);
    }

    return ids;
}

