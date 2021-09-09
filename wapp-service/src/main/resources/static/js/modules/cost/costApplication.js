
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: '#costApplicationApp',
    data: {
        invoiceDateOptions:{},
        gfInfoOptions:[],
        costTypeOptions:[],
        rateOptions:[],
        deptOptions: [],
        isAble:false,
        kindOptions:[{
            optionKey: '1',
            optionName: '增值税专用发票'
        },{
            optionKey: '2',
            optionName: '增值税普通发票'
        },{
            optionKey: '3',
            optionName: '普通发票'
        }],
        businessTypeOptions:[{
            optionKey: '0',
            optionName: '非合同'
        },{
            optionKey: '1',
            optionName: '合同'
        }],
        uploadLoading: false,
        submitLoading: false,
        showImgWin: true,
        importDialogFormVisible:false,
        listLoadingImport: false,
        selectFileFlag:'',
        form:{
            venderId:'',
            venderName:'',
            bankName:'',
            bankAccount:'',
            approverEmail:'',
            businessType:'1',
            settlementAmount:'',
            costTotalAmount:'',
            invoiceTotalAmount:'',
            staffEmail:'',
            winId:''
        },
        formRules:{
            venderId: [{required: true, message: '_', trigger: 'blur'}],
            staffEmail: [{required: true, message: '_', trigger: 'blur'}],
            settlementAmount: [{ required: true, message: ' ', trigger: 'blur'}],
            businessType: [{required: true, message: '_', trigger: 'blur'}]
        },
        invoiceForm:{
            invoiceCode:'',
            invoiceNo:'',
            invoiceType:'',
            invoiceKind:'1',
            invoiceDate:'',
            invoiceAmount:'',
            checkCode:'',
            totalAmount:'',
            taxAmount:'',
            taxRate:'',
            gfTaxNo:''
        },
        fileForm:{
            fileType:'1'
        },
        tableData:[],
        invoiceImgData:[],
        costFileData:[]
    },
    mounted: function () {
        this.invoiceDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
        this.getGfInfo();
        this.getVenderInfo();
        this.getRateSelection();
        this.showImgWin = false;
        document.getElementById("item-checkCode").style.display = "none";
        $("#rate_tip_id .el-form-item__content").append('<div style="color: red">多税率不用选择，请录入税率明细</div>');
        // $("#requireMsgs2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">发票开票日期已超270天</div>');
       /* var startDate = new Date(vm.invoiceForm.invoiceDate);
        var endDate = new Date(new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()));
        startDate.setMonth(startDate.getMonth()+9);
        if ( endDate.getTime()+1000*60*60*24 > startDate.getTime()) {
            $("#requireMsgs2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">发票开票日期已超270天</div>');
        }*/
    },
    methods: {
        blurStaffEmail:function () {
            $.get(baseURL + 'cost/application/getStaffInfo',{staffEmail: vm.form.staffEmail},function(r){
                if(r.staffInfo===null){
                    vm.form.staffEmail='';
                    return;
                }else if(r.staffInfo.staffNo === null ||　r.staffInfo.staffNo === ''){
                    alert("请刷新页面重新输入!");
                    vm.form.staffEmail='';
                    return;
                }else if(r.staffInfo.winID===null ||　r.staffInfo.winID === ''){
                    vm.form.staffEmail='';
                    return;
                }
                vm.form.winId = r.staffInfo.winID;
                vm.form.approverEmail = r.staffInfo.staffNo;
            });
        },
        changeAmount:function(value){
            vm.form.settlementAmount = formatNo2(vm.form.settlementAmount);
        },
        changeBusinessType: function(value){
            if(vm.form.venderId==null || vm.form.venderId==''){
                return;
            }
            if(value==''){
                return;
            }
            this.$http.post(baseURL + 'cost/application/getCostTypeOption',
                {venderid: vm.form.venderId, businessType: value},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                vm.costTypeOptions = res.body.costTypeOptionList;
            });
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        changeFile: function(value){
            if(value==1){
                $("#tic").text("支持图片格式:*.jpg、*.png");
            }else{
                $("#tic").text("");
            }
        },
        changeKind: function(value){
            vm.invoiceForm.invoiceCode='';
            vm.invoiceForm.invoiceNo='';
            vm.invoiceForm.invoiceType='';
            vm.invoiceForm.invoiceDate='';
            vm.invoiceForm.checkCode='';
            vm.invoiceForm.totalAmount='';
            vm.invoiceForm.taxAmount='';
            vm.invoiceForm.taxRate='';
            vm.invoiceForm.gfTaxNo='';
        },
        getGys: function(){
            var val = $('#form_venderid').val();
            if(val==null || val.trim().length==0){
                alert('供应商号不能为空');
                vm.form.venderName = '';
                vm.form.bankName = '';
                vm.form.bankAccount = '';
                vm.costTypeOptions = [];
                return;
            }
            this.$http.post(baseURL + 'cost/application/getGys',
                {venderid: val},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.userInfo;
                // var costTypeOptionList = res.body.costTypeOptionList;
                if(info){
                    vm.form.venderId = info.venderId;
                    vm.form.venderName = info.venderName;
                    vm.form.bankName = info.bankName;
                    vm.form.bankAccount = info.bankAccount;
                    // vm.costTypeOptions = costTypeOptionList;
                    this.$http.post(baseURL + 'cost/application/getCostTypeOption',
                        {venderid: info.venderId, businessType: vm.form.businessType},
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (res) {
                        vm.costTypeOptions = res.body.costTypeOptionList;
                    });
                }else{
                    alert('供应商不存在,请重新填写');
                    vm.form.venderId = '';
                    vm.form.venderName = '';
                    vm.form.bankName = '';
                    vm.form.bankAccount = '';
                    vm.costTypeOptions = [];
                }
            });
        },
        getVenderInfo: function(){
            this.$http.post(baseURL + 'cost/application/getUserInfo',
                null,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.userInfo;
                // var costTypeOptionList = res.body.costTypeOptionList;
                if(info.venderId==null || info.venderId==''){
                    $("#form_venderid").removeAttr('readonly');
                }else {
                    vm.form.venderId = info.venderId;
                    vm.form.venderName = info.venderName;
                    vm.form.bankName = info.bankName;
                    vm.form.bankAccount = info.bankAccount;
                    // vm.costTypeOptions = costTypeOptionList;

                    this.$http.post(baseURL + 'cost/application/getCostTypeOption',
                        {venderid: info.venderId, businessType: '1'},
                        {
                            'headers': {
                                "token": token
                            }
                        }).then(function (res) {
                        vm.costTypeOptions = res.body.costTypeOptionList;
                    });
                }
            });
        },
        searchInvoice: function () {
            if(vm.invoiceForm.invoiceKind=='3') {
                return;
            }
            var params = {
                invoiceCode: vm.invoiceForm.invoiceCode,
                invoiceNo: vm.invoiceForm.invoiceNo,
                orgcode: vm.invoiceForm.gfTaxNo
            };
            this.$http.post(baseURL + 'cost/application/searchInvoice',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.invoiceInfo;
                if(info!=null) {
                    //发票可用性校验
                    if((info.dxhyMatchStatus==null || info.dxhyMatchStatus=='0' || info.dxhyMatchStatus=='5' || info.dxhyMatchStatus=='6')
                        // && (info.costNo==null || info.costNo=='')
                    ) {
                        //来源是录入的,不带出
                        if(info.sourceSystem=='2'){
                            return;
                        }
                        if(info.gfTaxNo!=info.jvcode){
                            alert("所选JV与发票抬头不一致!");
                            this.invoiceForm.invoiceCode = '';
                            this.invoiceForm.invoiceNo = '';
                        }else {
                            this.invoiceForm.invoiceDate = info.invoiceDate;
                            this.invoiceForm.invoiceAmount = info.invoiceAmount;
                            this.invoiceForm.checkCode = info.checkCode;
                            this.invoiceForm.totalAmount = info.totalAmount;
                            this.invoiceForm.taxAmount = info.taxAmount;
                            // this.invoiceForm.taxRate = info.taxRate;
                            // this.invoiceForm.gfTaxNo = info.gfTaxNo;
                            this.addInvoice(true);
                        }
                    }else{
                        alert("此发票已有匹配!");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                    }
                }
            });
        },
        getGfInfo: function(value){
            $.get(baseURL + 'cost/application/getGfInfo',function(r){
                vm.gfInfoOptions = r.optionList;
            });
        },
        getRateSelection: function(){
            $.get(baseURL + 'cost/application/getRateOptions',function(r){
                vm.rateOptions = r.optionList;
            });
        },
        addInvoice: function (isExist) {
            var invoice = {};
            invoice.isExist = isExist;
            invoice.invoiceCode = vm.invoiceForm.invoiceCode;
            invoice.invoiceNo = vm.invoiceForm.invoiceNo;
            invoice.invoiceType = vm.invoiceForm.invoiceType;
            invoice.invoiceKind = vm.invoiceForm.invoiceKind;
            invoice.invoiceDate = vm.invoiceForm.invoiceDate;
            invoice.totalAmount = vm.invoiceForm.totalAmount;
            invoice.coverAmount = vm.invoiceForm.totalAmount;
            invoice.taxAmount = vm.invoiceForm.taxAmount;
            invoice.taxRate = vm.invoiceForm.taxRate;
            invoice.taxRateName = formatRate(vm.invoiceForm.taxRate);
            invoice.gfTaxNo = vm.invoiceForm.gfTaxNo;
            invoice.gfName = formatGf(vm.invoiceForm.gfTaxNo);
            invoice.invoiceAmount = vm.invoiceForm.invoiceAmount;
            invoice.checkCode = vm.invoiceForm.checkCode;
            if(vm.invoiceForm.invoiceType=='04'){
                //普票0税率
                invoice.taxRate = '0';
                invoice.taxRateName = formatRate('0');
            }
            invoice.isEdit = false;

            invoice.invoiceRateForm = {};
            invoice.rateTableData = [];
            var startDate = new Date(vm.invoiceForm.invoiceDate);
            var endDate = new Date(new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()));
            startDate.setMonth(startDate.getMonth()+9);
            if ( endDate.getTime()+1000*60*60*24 > startDate.getTime()) {
               alert("该发票开票日期已超270天");
                //$("#requireMsgs2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">发票开票日期已超270天</div>');
            }
            //数据非空校验
            if(invoiceValidate(invoice)) {
                //普票,自动0税率
                if(invoice.invoiceType=='04'){
                    var rate = {};
                    rate.invoiceAmount = invoice.totalAmount;
                    rate.taxRate = '0';
                    rate.taxRateName = formatRate('0');
                    rate.taxAmount = invoice.taxAmount;
                    rate.costForm = {};
                    rate.costTableData = [];
                    rate.costDeptOptions = [];
                    $.post(baseURL + 'cost/application/getDeptInfo',{jv: invoice.gfTaxNo},function(r){
                        rate.costDeptOptions = r.optionList;
                        vm.deptOptions = vm.deptOptions.concat(r.optionList);
                    });
                    rate.isEdit = false;
                    invoice.rateTableData.push(rate);
                }else if(invoice.taxRate!=null && invoice.taxRate!=''){
                    var rate = {};
                    rate.invoiceAmount = invoice.totalAmount;
                    rate.taxRate = invoice.taxRate;
                    rate.taxRateName = formatRate(invoice.taxRate);
                    rate.taxAmount = invoice.taxAmount;
                    rate.costForm = {};
                    rate.costTableData = [];
                    rate.costDeptOptions = [];
                    $.post(baseURL + 'cost/application/getDeptInfo',{jv: invoice.gfTaxNo},function(r){
                        rate.costDeptOptions = r.optionList;
                        vm.deptOptions = vm.deptOptions.concat(r.optionList);
                    });
                    rate.isEdit = false;
                    invoice.rateTableData.push(rate);
                }
                vm.tableData.push(invoice);
                this.$set(this.invoiceForm,'invoiceCode','');
                this.$set(this.invoiceForm,'invoiceNo','');
                this.$set(this.invoiceForm,'invoiceType','');
                this.$set(this.invoiceForm,'invoiceKind','1');
                this.$set(this.invoiceForm,'invoiceDate','');
                this.$set(this.invoiceForm,'checkCode','');
                this.$set(this.invoiceForm,'invoiceAmount','');
                this.$set(this.invoiceForm,'totalAmount','');
                this.$set(this.invoiceForm,'taxAmount','');
                this.$set(this.invoiceForm,'taxRate','');
                this.$set(this.invoiceForm,'gfTaxNo','');
                getTotalInvoiceAmount();
            }
        },
        addRate: function (row) {
            var form = row.invoiceRateForm;
            var rate = {};
            rate.invoiceAmount = form.invoiceAmount;
            rate.taxRate = form.taxRate;
            rate.taxRateName = formatRate(form.taxRate);
            rate.taxAmount = form.taxAmount;
            rate.costForm = {};
            rate.costTableData = [];
            rate.isEdit = false;
            rate.costDeptOptions = [];
            $.ajaxSettings.async = false;
            $.post(baseURL + 'cost/application/getDeptInfo',{jv: row.gfTaxNo},function(r){
                rate.costDeptOptions = r.optionList;
                vm.deptOptions = vm.deptOptions.concat(r.optionList);
            });
            $.ajaxSettings.async = false;
            if(rateValidate(rate)) {
                row.rateTableData.push(rate);
                row.invoiceRateForm = {};
            }
        },
        addCost: function (row) {
            vm.isAble = true;
            var form = row.costForm;
            var cost = {};
            cost.costTime1 = form.costTime1;
            cost.costTime2 = form.costTime2;
            cost.costTime = form.costTime1+"至"+form.costTime2;
            cost.costUse = form.costUse;
            cost.costAmount = form.costAmount;
            cost.costType = form.costType;
            cost.costTypeName = formatCostType(form.costType);
            if(row.costDeptOptions==null || row.costDeptOptions.length==0){
                cost.costDeptId = '';
                cost.costDept = '';
            }else{
                cost.costDeptId = row.costDeptOptions[0].optionKey;
                cost.costDept = row.costDeptOptions[0].optionName;
            }
            cost.isEdit = false;
            if(costValidate(cost)) {
                row.costTableData.push(cost);
                row.costForm = {};
                getTotalCostAmount();
            }

        },
        uploadFile: function () {
            var fileValue = document.getElementById("fileId").value;
            if(fileValue==null || fileValue==''){
                alert("请选择文件");
                return;
            }
            var file = document.getElementById("fileId").files[0];
            if(vm.fileForm.fileType=='1' && (file.type).indexOf('image/')!=0){
                alert("必须上传图片文件");
                return;
            }
            var maxsize = 10485760;//10M
            if(file!==undefined) {
                var fileSize = file.size;
                if (fileSize > maxsize) {
                    alert("附件大小不能超过10MB!");
                    return false;
                }
            }

            vm.uploadLoading = true;
            document.getElementById("uploadBtn").getElementsByTagName("span")[0].innerHTML = "上传中...";
            var formData = new FormData();
            formData.append("file", file);
            formData.append("fileType", vm.fileForm.fileType);
            $.ajax({
                url: baseURL + 'cost/application/uploadFile',
                data: formData,
                type: "POST",
                async: false,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (r) {
                    if(r.code=='0') {
                        var data = r.fileEntity;
                        if (data.fileType == '1') {
                            vm.invoiceImgData.push(data);
                        } else if (data.fileType == '2') {
                            vm.costFileData.push(data);
                        }
                    }else{
                        alert(r.msg);
                    }
                    vm.uploadLoading = false;
                    document.getElementById("uploadBtn").getElementsByTagName("span")[0].innerHTML = "上传";
                },
            });
            document.getElementById("fileId").value = '';
        },
        viewFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("viewImgId").src = baseURL + 'cost/application/viewFile?id='+row.id + "&token=" + token;
                },
                error: function () {

                }
            });
            vm.showImgWin = true;
        },
        deleteFile: function(tableData, row){
            tableData.splice(tableData.indexOf(row), 1);
        },
        beforeCloseImgWin: function () {
            document.getElementById("viewImgId").src = '';
            vm.showImgWin = false;
        },
        submitAll: function () {
            vm.submitLoading = true;
            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存中...";
            vm.$refs['form'].validate(function (valid) {
                if(valid){
                    //提交前校验
                    if(vm.invoiceImgData==null || vm.invoiceImgData.length==0){
                        alert("发票图片必须上传!");
                        vm.submitLoading = false;
                        document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                        return;
                    }
                    if(vm.form.settlementAmount!=vm.form.invoiceTotalAmount){
                        alert("发票总金额与结算金额不一致!");
                        vm.submitLoading = false;
                        document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                        return;
                    }
                    if(vm.form.settlementAmount!=vm.form.costTotalAmount){
                        alert("费用行总金额与结算金额不一致!");
                        vm.submitLoading = false;
                        document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                        return;
                    }
                    for(var a=0;a<vm.tableData.length;a++){
                        if(vm.tableData[a].isEdit){
                            alert("有数据还未修改完成,请完成后重试!");
                            vm.submitLoading = false;
                            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                            return;
                        }
                        var sh1 = getGftaxNo(vm.tableData[a].gfTaxNo);//jv对应的税号
                        var invoiceTotal = 0.00;
                        var taxTotal = 0.00;
                        var rateData = vm.tableData[a].rateTableData;
                        for(var b=0;b<rateData.length;b++){
                            if(rateData[b].isEdit){
                                alert("有数据还未修改完成,请完成后重试!");
                                vm.submitLoading = false;
                                document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                return;
                            }
                            invoiceTotal += Number(rateData[b].invoiceAmount);
                            taxTotal += Number(rateData[b].taxAmount);
                            var total = 0.00;
                            var costData = rateData[b].costTableData;
                            var checkCostAmount=0.00;
                            var checkRate=0.00;
                            var checkCost=0.00;
                            for(var c=0;c<costData.length;c++){
                                if(costData[c].isEdit){
                                    alert("有数据还未修改完成,请完成后重试!");
                                    vm.submitLoading = false;
                                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                    return;
                                }
                                var sh2 = getGftaxNoByDept(costData[c].costDeptId);
                                if(sh1!=sh2){
                                    alert("费用承担部门与发票抬头不一致!");
                                    vm.submitLoading = false;
                                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                    return;
                                }
                                checkRate=Number(rateData[b].taxRateName.split("%")[0])/100;
                                checkCostAmount=Number(rateData[b].invoiceAmount)/(1+checkRate)*checkRate;
                                checkCost=Number(rateData[b].taxAmount);
                                if(checkCostAmount.toFixed(2)!=checkCost){
                                    var checkAbs=Math.abs(checkCostAmount.toFixed(2)-checkCost);
                                    if(checkAbs>0.03){
                                        if(rateData[b].taxRateName != "5%房屋、停车场租赁"){
                                            alert("费用行税率或税额有误!行:"+(a+1)+",发票号:"+vm.tableData[a].invoiceNo);
                                            vm.submitLoading = false;
                                            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                            return;
                                        }
                                    }
                                }
                                total += Number(costData[c].costAmount);
                            }
                            var totalns = Number(rateData[b].invoiceAmount);
                            if(total.toFixed(2) != totalns.toFixed(2)){
                                alert("税率明细金额与对应费用行总金额不一致!");
                                vm.submitLoading = false;
                                document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                return;
                            }
                        }
                        var invoiceTotalns = Number(vm.tableData[a].totalAmount);
                        if(invoiceTotal.toFixed(2) != invoiceTotalns.toFixed(2) ){
                            alert("发票金额与税率明细总金额不一致!");
                            vm.submitLoading = false;
                            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                            return;
                        }
                        var taxTotalns = Number(vm.tableData[a].taxAmount);
                        if(taxTotal.toFixed(2) != taxTotalns.toFixed(2)){
                            alert("发票税额与税率明细总税额不一致!");
                            vm.submitLoading = false;
                            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                            return;
                        }
                    }

                    //构造数据
                    var settlement = {};
                    settlement.venderId = vm.form.venderId;
                    settlement.venderName = vm.form.venderName;
                    settlement.bankName = vm.form.bankName;
                    settlement.bankAccount = vm.form.bankAccount;
                    settlement.approverEmail = vm.form.approverEmail;
                    settlement.settlementAmount = vm.form.settlementAmount;
                    settlement.remark = encodeURIComponent($("#remarkId").val());
                    settlement.walmartStatus = '0';//申请,状态为审批中
                    settlement.payModel = '0';//非预付款
                    settlement.businessType = vm.form.businessType;//合同
                    settlement.winId = vm.form.winId;
                    settlement.staffEmail = vm.form.staffEmail;

                    settlement.invoiceList = vm.tableData;
                    vm.invoiceImgData.push.apply(vm.invoiceImgData, vm.costFileData);
                    settlement.fileList = vm.invoiceImgData;

                    $.ajax({
                        type:"POST",
                        url:baseURL + 'cost/application/submitAll.ignoreHtmlFilter',
                        data: JSON.stringify(settlement),
                        dataType:"json",
                        contentType:"application/json",
                        async: false,
                        cache:false,
                        success:function(r){
                            if(r.code==0) {
                                alert("保存成功");
                                vm.isAble= false;
                                vm.resetAll();
                            }else{
                                alert("保存失败")
                            }
                            vm.submitLoading = false;
                            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                        }
                    });
                }else{
                    if(vm.form.approverEmail==null || vm.form.approverEmail=='') {
                        alert("审批人邮箱不能为空!");
                    }else if(vm.form.settlementAmount==null || vm.form.settlementAmount=='') {
                        alert("结算金额不能为空!");
                    }
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                    return;
                }
            });
        }
        ,
        resetAll: function () {
            vm.form.staffEmail='';
            vm.form.settlementAmount='';
            vm.form.costTotalAmount='';
            vm.form.invoiceTotalAmount='';

            this.$set(this.invoiceForm,'invoiceCode','');
            this.$set(this.invoiceForm,'invoiceNo','');
            this.$set(this.invoiceForm,'invoiceType','');
            this.$set(this.invoiceForm,'invoiceKind','1');
            this.$set(this.invoiceForm,'invoiceDate','');
            this.$set(this.invoiceForm,'checkCode','');
            this.$set(this.invoiceForm,'invoiceAmount','');
            this.$set(this.invoiceForm,'totalAmount','');
            this.$set(this.invoiceForm,'taxAmount','');
            this.$set(this.invoiceForm,'taxRate','');
            this.$set(this.invoiceForm,'gfTaxNo','');

            document.getElementById("fileId").value = '';
            vm.fileForm.fileType = '1';

            $("#remarkId").val('');
			$("#tic").text("支持图片格式:*.jpg、*.png");
            vm.tableData = [];
            vm.invoiceImgData = [];
            vm.costFileData = [];
            vm.$refs['form'].clearValidate();

        },
        formatFileType: function (row, column, cellValue) {
            if(cellValue==null){
                return;
            }
            if(cellValue=='1'){
                return "发票图片";
            }else if(cellValue=='2'){
                return "附件";
            }
            return "";
        },
        numberFormat: function (row, column, cellValue) {
            if(cellValue==null || cellValue == undefined){
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
        changeItem:function (val) {
            if(vm.form.staffEmail===''){
                vm.invoiceForm.invoiceCode = '';
                alert('请先输入邮箱!');
                return;
            }
            if(vm.invoiceForm.invoiceKind=='1' || vm.invoiceForm.invoiceKind=='2') {
                var invoice = vm.invoiceForm.invoiceCode;
                var invoiceType = getFplx(invoice);
                if (invoiceType == null || invoiceType == '') {
                    alert('请输入正确的发票代码!');
                    vm.invoiceForm.invoiceCode = '';
                    return;
                }
                if(vm.invoiceForm.invoiceKind=='1' && invoiceType != "01"){
                    alert('请输入正确的发票代码!');
                    vm.invoiceForm.invoiceCode = '';
                    return;
                }
                if(vm.invoiceForm.invoiceKind=='2' && invoiceType != "04"){
                    alert('请输入正确的发票代码!');
                    vm.invoiceForm.invoiceCode = '';
                    return;
                }
                //根据输入的代码解析出的发票类型对金额或校验码进行操作
                if (invoiceType == "01" || invoiceType == "03") {
                    document.getElementById("item-checkCode").style.display = "none";
                } else {
                    document.getElementById("item-checkCode").style.display = "block";
                   // $("#item-checkCode .el-form-item__content").append('<div style="color: red">请录入后六位数字</div>');
                }
                this.searchInvoice();
                vm.invoiceForm.invoiceType = invoiceType;
            }else{
                vm.invoiceForm.invoiceType = '';
            }
        },
        modifyInvoiceOn: function (row) {
            if(row.isExist){
                alert('底账表已存在的发票无法修改');
            }else {
                row.isEdit = true;
            }
        },
        modifyInvoiceOff: function (row) {
            if(invoiceValidate(row)) {
                row.gfName = formatGf(row.gfTaxNo);
                row.taxRateName = formatRate(row.taxRate);
                row.coverAmount = row.totalAmount;
                row.isEdit = false;
                getTotalInvoiceAmount();
            }
        },
        deleteInvoice: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            getTotalInvoiceAmount();
            getTotalCostAmount();
        },
        modifyRateOn: function (row) {
            row.isEdit = true;
        },
        modifyRateOff: function (row) {
            if(rateValidate(row))
            {
                row.taxRateName = formatRate(row.taxRate);
                row.isEdit = false;
            }
        },
        deleteRate: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            getTotalCostAmount();
        },
        modifyCostOn: function (row) {
            row.isEdit = true;
        },
        modifyCostOff: function (row) {
            if(costValidate(row)) {
                row.costTime = row.costTime1 + "至" + row.costTime2;
                row.costTypeName = formatCostType(row.costType);
                row.costDept = formatDept(row.costDeptId);
                row.isEdit = false;
                getTotalCostAmount();
            }
        },
        deleteCost: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            getTotalCostAmount();
        },
        importFormCancel: function () {
            vm.importDialogFormVisible = false;
        },
        importIn:function(){
            if(vm.form.approverEmail==''){
                alert("请先输入审批人邮箱");
                return;
            }
            vm.importDialogFormVisible = true;
        },
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
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
        batchImport: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.listLoadingImport = true;
                var flag = false
                var hh;
                var url = baseURL + "cost/application/excelImport";
                this.$http.post(url, formData, config).then(function (response) {
                    this.listLoadingImport = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    this.selectFileFlag = '';
                    this.file = '';
                    $("#upload_form")[0].reset();
                    if (response.data.success) {
                        var dataList = response.data.dataList;
                        for(var i=0;i<dataList.length;i++){
                            dataList[i].isEdit = false;
                            dataList[i].invoiceKind = "1";
                            dataList[i].invoiceType = getFplx(dataList[i].invoiceCode);
                            dataList[i].gfName = formatGf(dataList[i].gfTaxNo);
                            dataList[i].isEdit = false;
                            dataList[i].invoiceRateForm = {};
                            //查询底账表,若已有发票,则取底账的发票信息
                            var params = {
                                invoiceCode: dataList[i].invoiceCode,
                                invoiceNo: dataList[i].invoiceNo,
                                orgcode: dataList[i].gfTaxNo
                            };
                            $.ajax({
                                url: baseURL + 'cost/application/searchInvoice',
                                async: false,
                                type: "POST",
                                data: params,
                                success: function (res) {
                                    var info = res.invoiceInfo;
                                    if(info!=null) {
                                        dataList[i].invoiceDate = info.invoiceDate;
                                        dataList[i].invoiceAmount = info.invoiceAmount;
                                        dataList[i].checkCode = info.checkCode;
                                        dataList[i].totalAmount = info.totalAmount;
                                        dataList[i].taxAmount = info.taxAmount;
                                        dataList[i].isExist = true;
                                    }else{
                                        dataList[i].isExist = false;
                                    }
                                }
                            });
                            dataList[i].coverAmount = dataList[i].totalAmount;
                            //设置税率中一些默认值
                            var rateList = dataList[i].rateTableData;
                            for(var a=0;a<rateList.length;a++){
                                rateList[a].isEdit = false;
                                rateList[a].taxRateName = formatRate(rateList[a].taxRate);
                                rateList[a].costForm = {};
                                rateList[a].costDeptOptions = [];
                                $.ajaxSettings.async = false;
                                $.post(baseURL + 'cost/application/getDeptInfo',{jv: dataList[i].gfTaxNo},function(r){
                                    rateList[a].costDeptOptions = r.optionList;
                                    vm.deptOptions = vm.deptOptions.concat(r.optionList);
                                });
                                $.ajaxSettings.async = true;
                                //设置费用中一些默认值
                                var costList = rateList[a].costTableData;
                                for(var b=0;b<costList.length;b++) {
                                    costList[b].isEdit = false;
                                    costList[b].costTime1 = costList[b].costTime.split("至")[0];
                                    costList[b].costTime2 = costList[b].costTime.split("至")[1];
                                    costList[b].costDept = formatDept(costList[b].costDeptId);
                                }
                            }
                        }
                        vm.tableData = dataList;
                        //计算费用总金额和发票总金额
                        getTotalInvoiceAmount();
                        getTotalCostAmount();
                        vm.importDialogFormVisible = false;
                    } else {
                        vm.importDialogFormVisible = false;
                        alert(response.data.reason);
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);
            }
        },
        exportData() {
            document.getElementById("ifile").src = baseURL + "export/cost/application/template";
        }
    }
})

/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fpdmlist=new Array("144031539110","131001570151","133011501118","111001571071");
    var  fplx="";
    if (fpdm.length==12){
        var fplxflag=fpdm.substring(7,8);

        for(var i =0; i<fpdmlist.length;i++){
            if(fpdm==fpdmlist[i]){
                fplx="10";
                break;
            }
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="11") {
            fplx="10";
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="12") {
            fplx="14";
        }
        if (fpdm.substring(0,1)=="0" && (fpdm.substring(10,12)=="06"|| fpdm.substring(10,12)=="07")) {
            //判断是否为卷式发票  第1位为0且第11-12位为06或07
            fplx="11";
        }
        if(fpdm.substring(0,1)=="0"&&(fpdm.substring(10,12)=="04"|| fpdm.substring(10,12)=="05")){
            fplx="04"
        }
        if (fplxflag=="2" && fpdm.substring(0,1)!="0") {
            fplx="03";
        }

    }else if(fpdm.length==10){
        var fplxflag=fpdm.substring(7,8);
        if(fplxflag=="1"||fplxflag=="5"){
            fplx="01";
        }else if(fplxflag=="6"||fplxflag=="3"){
            fplx="04";
        }else if(fplxflag=="7"||fplxflag=="2"){
            fplx="02";
        }

    }
    return fplx;
}

function formatGf(cellValue){
    if(cellValue==null){
        return '';
    }
    for(var i=0;i<vm.gfInfoOptions.length;i++){
        if(cellValue==vm.gfInfoOptions[i].optionKey){
            return vm.gfInfoOptions[i].optionName;
        }
    }
    return cellValue;
}

function formatRate(cellValue){
    if(cellValue==null){
        return '';
    }
    for(var i=0;i<vm.rateOptions.length;i++){
        if(cellValue==vm.rateOptions[i].optionKey){
            return vm.rateOptions[i].optionName;
        }
    }
    return cellValue;
}

function formatCostType(cellValue){
    if(cellValue==null){
        return '';
    }
    for(var i=0;i<vm.costTypeOptions.length;i++){
        if(cellValue==vm.costTypeOptions[i].optionKey){
            return vm.costTypeOptions[i].optionName;
        }
    }
    return cellValue;
}

function formatDept(cellValue){
    if(cellValue==null){
        return '';
    }
    for(var i=0;i<vm.deptOptions.length;i++){
        if(cellValue==vm.deptOptions[i].optionKey){
            return vm.deptOptions[i].optionName;
        }
    }
    return cellValue;
}

function getTotalInvoiceAmount(){
    var total = 0.00;
    for(var i=0;i<vm.tableData.length;i++){
        total += Number(vm.tableData[i].totalAmount);
    }
    vm.form.invoiceTotalAmount = formatNo2(total.toFixed(2));
}

function getTotalCostAmount(){
    var total = 0.00;
    for(var a=0;a<vm.tableData.length;a++){
        var rateData = vm.tableData[a].rateTableData;
        for(var b=0;b<rateData.length;b++){
            var costData = rateData[b].costTableData;
            for(var c=0;c<costData.length;c++){
                total += Number(costData[c].costAmount);
            }
        }
    }
    vm.form.costTotalAmount = formatNo2(total.toFixed(2));
}

//发票验证
function invoiceValidate(invoice){
    if(invoice.invoiceKind=='1' || invoice.invoiceKind=='2') {
        if (invoice.gfTaxNo == null || invoice.gfTaxNo == '') {
            alert("JV不能为空!");
            return false;
        }
        if (invoice.invoiceCode == null || invoice.invoiceCode == '') {
            alert("发票代码不能为空!");
            return false;
        }
        if (invoice.invoiceNo == null || invoice.invoiceNo == '') {
            alert("发票号码不能为空!");
            return false;
        }
        if (invoice.invoiceDate == null || invoice.invoiceDate == '') {
            alert("开票日期不能为空!");
            return false;
        }
        if (invoice.invoiceAmount == null || invoice.invoiceAmount == '') {
            alert("不含税金额不能为空!");
            return false;
        }
        if (invoice.taxAmount == null || invoice.taxAmount == '') {
            alert("税额不能为空!");
            return false;
        }
        if (invoice.totalAmount == null || invoice.totalAmount == '') {
            alert("含税金额不能为空!");
            return false;
        }
        if (invoice.invoiceType == '01' || invoice.invoiceType == '03') {

        } else {
            if (invoice.checkCode == null || invoice.checkCode == '') {
                alert("校验码不能为空!");
                return false;
            }
        }
    }else{
        if (invoice.gfTaxNo == null || invoice.gfTaxNo == '') {
            alert("JV不能为空!");
            return false;
        }
        if (invoice.invoiceNo == null || invoice.invoiceNo == '') {
            alert("发票号码不能为空!");
            return false;
        }
        if (invoice.invoiceDate == null || invoice.invoiceDate == '') {
            alert("开票日期不能为空!");
            return false;
        }
        if (invoice.totalAmount == null || invoice.totalAmount == '') {
            alert("含税金额不能为空!");
            return false;
        }
        invoice.venderid = vm.form.venderId;
        var isExists = false;
        $.ajax({
            url: baseURL + 'cost/match/checkInvoice',
            async: false,
            type: "POST",
            contentType: 'application/json',
            dataType: 'json',
            data: JSON.stringify(invoice),
            success: function (res) {
                isExists = res;
            }
        });
        if(isExists){
            alert("发票重复!");
            return false;
        }
    }
    return true;
}

//税率数据验证
function rateValidate(rate){
    if(rate.invoiceAmount==null || rate.invoiceAmount==''){
        alert("含税金额不能为空!");
        return false;
    }
    if(rate.taxRate==null || rate.taxRate==''){
        alert("税率不能为空!");
        return false;
    }
    if(rate.taxAmount==null || rate.taxAmount==''){
        alert("税额不能为空!");
        return false;
    }
    return true;
}

//费用数据验证
function costValidate(cost){
    if(cost.costTime1==null || cost.costTime1=='' || cost.costTime2==null || cost.costTime2==''){
        alert("费用发生起止时间不能为空!");
        return false;
    }
    if(cost.costTime1>cost.costTime2){
        alert("费用发生开始时间不能大于结束时间!");
        return false;
    }
    if(cost.costUse==null || cost.costUse==''){
        alert("用途不能为空!");
        return false;
    }
    if(cost.costAmount==null || cost.costAmount==''){
        alert("含税金额不能为空!");
        return false;
    }
    if(cost.costType==null || cost.costType==''){
        alert("费用类型不能为空!");
        return false;
    }
    return true;
}

//根据jvcode获取购方税号
function getGftaxNo(jvcode){
    var taxNo='';
    if(jvcode==null || jvcode==''){
        return taxNo;
    }
    $.ajax({
        type:"POST",
        url:baseURL + 'cost/application/getGfTaxNo',
        data: {jvcode : jvcode},
        async: false,
        success:function(r){
            taxNo = r.gfTaxNo;
        }
    });
    return taxNo;
}

//根据成本中心获取购方税号
function getGftaxNoByDept(dept){
    var taxNo='';
    if(dept==null || dept==''){
        return taxNo;
    }
    $.ajax({
        type:"POST",
        url:baseURL + 'cost/application/getGfTaxNoByDept',
        data: {dept : dept},
        async: false,
        success:function(r){
            taxNo = r.gfTaxNo;
        }
    });
    return taxNo;
}
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}

function formatNo2(value) {
    if(value==null || value==''){
        return '';
    }
    var val = String(value);
    if(val.indexOf('.')<0){
        return val+'.00';
    }else{
        var ab = val.split('.');
        if(ab[1].length==1){
            return val+'0';
        }else if(ab[1].length==2){
            return val;
        }else{
            return ab[0]+'.'+ab[1].substring(0,2);
        }
    }
}
function verificationTaxAmountValue(t) {
    var reg = /[^\d]/g;
    vm.invoiceForm.taxAmount = t.value.replace(reg, '');
};
function verificationTotalAmountValue(t) {
    var reg = /[^\d]/g;
    vm.invoiceForm.totalAmount = t.value.replace(reg, '');
};
function verificationTotalAmountValue(t) {
    var reg = /[^\d]/g;
    vm.invoiceForm.totalAmount = t.value.replace(reg, '');
};