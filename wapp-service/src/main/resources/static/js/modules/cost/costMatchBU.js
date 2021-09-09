Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var isInitial = true;
var orgtype = '';
var venderid = '';
var venderInfo = {};
var currentRateRow = {};
var costCount=0;

var vm = new Vue({
    el:'#costMatchApp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        detailTableData: [],
        listLoading: false,
        invoiceDateOptions:{},
        gfInfoOptions:[],
        rateOptions:[],
        costTypeOptions:[],
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
        uploadLoading: false,
        submitLoading: false,
        form:{
            venderid: '',
            costNo: '',
            epsNo:''
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
        invoiceImgData:[],
        costFileData:[],
        invoiceTableData:[],
        selectDetailTableData:[],
        showDetailWin: true,
        showSelectDetailWin: true,
        showImgWin: true,
        importDialogFormVisible:false,
        listLoadingImport: false,
        selectFileFlag:''
    },
    mounted: function () {
        this.invoiceDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
        $.ajax({
            url: baseURL + 'cost/matchBU/getOrgtype',
            type: "POST",
            async: false,
            success: function (r) {
                orgtype = r.orgtype;
                if(orgtype=='3'){
                    document.getElementById("form-item-venderid").style.display = "block";
                }else{
                    document.getElementById("form-item-venderid").style.display = "none";
                }
            }
        });
        this.getGfInfo();
        this.getRateSelection();
        this.showDetailWin = false;
        this.showSelectDetailWin = false;
        this.showImgWin = false;
        document.getElementById("item-checkCode").style.display = "none";
        $("#rate_tip_id .el-form-item__content").append('<div style="color: red">多税率不用选择</div>');
    },
    watch:{
        'invoiceForm.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg =  /^[0-9]{0,8}$/;
                if(this.invoiceForm.invoiceKind == '1' || this.invoiceForm.invoiceKind == '2') {
                    if (!(reg.test(val) || val == null)) {
                        Vue.nextTick(function () {
                            _this.invoiceForm.invoiceNo = oldValue;
                        })
                    }
                }
            },
            deep: true
        }
    },
    methods: {
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
        getVenderInfo: function(){
            this.$http.post(baseURL + 'cost/application/getUserInfo',
                null,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.userInfo;
                var costTypeOptionList = res.body.costTypeOptionList;
                venderInfo.venderId = info.venderId;
                venderInfo.venderName = info.venderName;
                venderInfo.bankName = info.bankName;
                venderInfo.bankAccount = info.bankAccount;
                vm.costTypeOptions = costTypeOptionList;
            });
        },
        getGys: function(venderid){
            this.$http.post(baseURL + 'cost/application/getGys',
                {venderid: venderid},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.userInfo;
                var costTypeOptionList = res.body.costTypeOptionList;
                venderInfo.venderId = info.venderId;
                venderInfo.venderName = info.venderName;
                venderInfo.bankName = info.bankName;
                venderInfo.bankAccount = info.bankAccount;
                vm.costTypeOptions = costTypeOptionList;
            });
        },
        getGfInfo: function(){
            $.get(baseURL + 'cost/application/getGfInfo',function(r){
                vm.gfInfoOptions = r.optionList;
            });
        },
        getRateSelection: function(){
            $.get(baseURL + 'cost/application/getRateOptions',function(r){
                vm.rateOptions = r.optionList;
            });
        },
        query: function(formName){
            isInitial = false;
            venderid = vm.form.venderid;

                // if(venderid==null || venderid.trim().length==0){
                //     alert("请先填写供应商号");
                //     return;
                // }
            this.findAll(1);
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            var params = {
                venderId: venderid,
                costNo: vm.form.costNo,
                epsNo:'EPS-'+vm.form.epsNo,
                page: currentPage,
                limit: this.pageSize
            };
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'cost/matchBU/list',
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

                this.tableData = xhr.page.list;
                this.listLoading = false;

                if(this.tableData.length>0){
                    $("#match_field_id").removeClass("notclick");
                }else{
                    $("#match_field_id").addClass("notclick");
                }
            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        },
        detail: function(row){
            var params = {costNo: row.costNo};
            this.$http.post(baseURL + 'cost/matchBU/detail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.detailTableData = xhr.detailList;
                $("#detailRemarkId").val(row.remark);
                vm.showDetailWin = true;
            });
        },
        beforeCloseDetailWin: function(){
            this.detailTableData = [];
            vm.showDetailWin = false;
        },
        beforeCloseImgWin: function () {
            document.getElementById("viewImgId").src = '';
            vm.showImgWin = false;
        },
        changeFile: function(value){
            if(value==1){
                $("#tic").text("支持图片格式:*.jpg、*.png");
            }else{
                $("#tic").text("");
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
        selectDetail: function(row){
            if(this.multipleSelection==null || this.multipleSelection.length==0){
                alert('请选择一条费用单!');
                return;
            }
            if(this.multipleSelection.length>1){
                alert('只能选择一条费用单!');
                return;
            }
            var costNos = '';
            for(var i=0;i<this.multipleSelection.length;i++){
                if(i>0){
                    costNos += ',';
                }
                costNos += this.multipleSelection[i].costNo;
            }
            var params = {costNos: costNos};
            this.$http.post(baseURL + 'cost/matchBU/selectDetail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.selectDetailTableData = xhr.detailList;
                for(var a=this.selectDetailTableData.length-1;a>=0;a--){
                    var data = this.selectDetailTableData[a];
                    for(var b=0;b<this.invoiceTableData.length;b++){
                        var rateList = this.invoiceTableData[b].rateTableData;
                        for(var c=0;c<rateList.length;c++){
                            var costList = rateList[c].costTableData;
                            for(var d=0;d<costList.length;d++){
                                if(costList[d].id==data.id){
                                    data.coveredAmount = Number(data.coveredAmount) + Number(costList[d].costAmount);
                                    data.uncoveredAmount -= costList[d].costAmount;
                                }
                            }
                        }
                    }
                    data.coverAmount = data.uncoveredAmount.toFixed(2);
                    if(data.uncoveredAmount<=0){
                        this.selectDetailTableData.splice(a, 1);
                    }
                    data.uncoveredAmount = data.uncoveredAmount.toFixed(2)
                }
                vm.showSelectDetailWin = true;
                currentRateRow = row;
            });
        },
        beforeCloseSelectDetailWin: function(){
            this.selectDetailTableData = [];
            vm.showSelectDetailWin = false;
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        rateFormat: function (row, column, cellValue, index) {
            return formatRate(cellValue);
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
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        handleSelectionChange: function(val) {
            this.multipleSelection = val;
        },
        handleCostSelectionChange: function(val) {
            this.multipleCostSelection = val;
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
                        && (info.totalAmount>info.coveredAmount || info.totalAmount == null || info.totalAmount == '')&&(info.costNo==null || info.costNo=='')) {

                        //来源是录入的,不带出
                        if(info.sourceSystem=='2' || info.totalAmount == null || info.totalAmount == ''){
                            return;
                        }
                        if(info.gfTaxNo!=info.jvcode){
                            alert("所选JV与发票抬头不一致!");
                            this.invoiceForm.invoiceCode = '';
                            this.invoiceForm.invoiceNo = '';
                        }else {
                            if(vm.invoiceForm.invoiceKind=='2') {
                                return;
                            }
                            this.invoiceForm.invoiceDate = info.invoiceDate;
                            this.invoiceForm.invoiceAmount = info.invoiceAmount;
                            this.invoiceForm.checkCode = info.checkCode;
                            this.invoiceForm.totalAmount = info.totalAmount;
                            this.invoiceForm.taxAmount = info.taxAmount;
                            this.invoiceForm.uncoveredAmount = info.uncoveredAmount;
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
        addInvoice: function (isExist) {
            this.getGys(this.multipleSelection[0].venderId);
            var invoice = {};
            invoice.isExist = isExist;
            invoice.invoiceCode = vm.invoiceForm.invoiceCode;
            invoice.invoiceNo = vm.invoiceForm.invoiceNo;
            invoice.invoiceType = vm.invoiceForm.invoiceType;
            invoice.invoiceKind = vm.invoiceForm.invoiceKind;
            invoice.invoiceDate = vm.invoiceForm.invoiceDate;
            invoice.totalAmount = vm.invoiceForm.totalAmount;
            if (isExist) {
                invoice.uncoveredAmount = vm.invoiceForm.uncoveredAmount;
            }else{
                invoice.uncoveredAmount = invoice.totalAmount;
            }
            invoice.taxAmount = taxAmountss(vm.invoiceForm.taxAmount);
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
            if(vm.invoiceForm.totalAmount<0){
                alert("含税金额不能小于0");
                return;
            }
            if(vm.invoiceForm.taxAmount<0){
                alert("税额不能小于0");
                return;
            }
            if(vm.invoiceForm.invoiceAmount<0){
                alert("不含税金额不能小于0");
                return;
            }
            invoice.isEdit = false;

            invoice.invoiceRateForm = {};
            invoice.rateTableData = [];
            //数据非空校验
            if(invoiceValidate(invoice)) {
                if(invoice.invoiceType=='04'){
                    var rate = {};
                    rate.invoiceAmount = invoice.uncoveredAmount;
                    rate.taxRate = '0';
                    rate.taxRateName = formatRate('0');
                    rate.taxAmount = invoice.taxAmount;
                    rate.costForm = {};
                    rate.costTableData = [];
                    rate.isEdit = true;
                    invoice.rateTableData.push(rate);
                }else if(invoice.taxRate!=null && invoice.taxRate!=''){
                    var rate = {};
                    rate.invoiceAmount = invoice.uncoveredAmount;
                    rate.taxRate = invoice.taxRate;
                    rate.taxRateName = formatRate(invoice.taxRate);
                    rate.taxAmount = invoice.taxAmount;
                    rate.costForm = {};
                    rate.costTableData = [];
                    rate.isEdit = true;
                    invoice.rateTableData.push(rate);
                }
                vm.invoiceTableData.push(invoice);
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
            if(form.invoiceAmount<0){
                alert("含税金额不能小于0");
                return;
            }
            if(form.taxAmount<0){
                alert("税额不能小于0");
                return;
            }
            if(rateValidate(rate)) {
                row.rateTableData.push(rate);
                row.invoiceRateForm = {};
            }
        },
        addCost: function () {
            if(this.multipleCostSelection==null || this.multipleCostSelection.length==0){
                alert('请选择费用信息!');
                return;
            }
            for(var i=0;i<this.multipleCostSelection.length;i++){
                if(this.multipleCostSelection[i].coverAmount==null || this.multipleCostSelection[i].coverAmount=='' || this.multipleCostSelection[i].coverAmount==0){
                    alert('请输入冲销金额!');
                    return;
                }
                if(Number(this.multipleCostSelection[i].coverAmount) > Number(this.multipleCostSelection[i].uncoveredAmount)){
                    alert('冲销金额不能大于未冲销金额!');
                    return;
                }
            }
            for(var i=0;i<this.multipleCostSelection.length;i++) {
                var selectedCost =  this.multipleCostSelection[i];
                var cost = {};
                cost.id = selectedCost.id;
                cost.costTime = selectedCost.costTime;
                cost.costUse = selectedCost.costUse;
                cost.costAmount = selectedCost.coverAmount;
                cost.costTypeName = selectedCost.costTypeName;
                cost.costType = selectedCost.costType;
                cost.costDept = selectedCost.costDept;
                cost.costDeptId = selectedCost.costDeptId;
                cost.instanceId = selectedCost.instanceId;
                cost.bpmsId = selectedCost.bpmsId;
                cost.projectCode = selectedCost.projectCode;
                currentRateRow.costTableData.push(cost);
            }
            this.showSelectDetailWin = false;
            costCount++;
            $("#costMatch_main_id").addClass("notclick");
        },
        changeItem:function (val) {
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
                }
                this.searchInvoice();
                vm.invoiceForm.invoiceType = invoiceType;
            }else{
                vm.invoiceForm.invoiceType = '';
            }
        },
        deleteInvoice: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            for(var i=0;i<row.rateTableData.length;i++){
                costCount -= row.rateTableData[i].costTableData.length;
            }
            if(costCount==0) {
                $("#costMatch_main_id").removeClass("notclick");
            }
        },
        deleteRate: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            costCount -= row.costTableData.length;
            if(costCount==0) {
                $("#costMatch_main_id").removeClass("notclick");
            }
        },
        deleteCost: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            costCount--;
            if(costCount==0) {
                $("#costMatch_main_id").removeClass("notclick");
            }
        },
        submitAll: function () {
            //this.getGys(this.multipleSelection[0].venderId);
            vm.submitLoading = true;
            document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存中...";
            if(vm.invoiceImgData==null || vm.invoiceImgData.length==0){
                alert("发票图片必须上传!");
                vm.submitLoading = false;
                document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                return;
            }
            //提交前校验
            if(venderInfo.venderId ==null || venderInfo.venderId==''){
                alert("请重新选择费用行!");
                vm.submitLoading = false;
                document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                return;
            }
            var totalAmount = 0.00;
            for(var a=0;a<vm.invoiceTableData.length;a++){
                // for(var o=0;o<vm.invoiceTableData.length;o++){
                //     if(vm.invoiceTableData[a].invoiceNo==vm.invoiceTableData[o].invoiceNo && vm.invoiceTableData[a].invoiceCode ==vm.invoiceTableData[o].invoiceCode ){
                //         alert("同一发票不可录入多次!");
                //         vm.submitLoading = false;
                //         document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                //         return;
                //     }
                // }
                var sh1 = getGftaxNo(vm.invoiceTableData[a].gfTaxNo);
                var invoiceTotal = 0.00;
                var rateData = vm.invoiceTableData[a].rateTableData;
                var checkCostAmount=0.00;
                var checkRate=0.00;
                var checkCost=0.00;
                for(var b=0;b<rateData.length;b++){
                    invoiceTotal += Number(rateData[b].invoiceAmount);
                    var total = 0.00;
                    var costData = rateData[b].costTableData;
                    for(var c=0;c<costData.length;c++){
                        var sh2 = getGftaxNoByDept(costData[c].costDeptId);
                        if(sh1!=sh2){
                            alert("费用承担部门与发票抬头不一致!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
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
                                    alert("费用行税率或税额有误!行:"+(a+1)+",发票号:"+vm.invoiceTableData[a].invoiceNo);
                                    vm.submitLoading = false;
                                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存";
                                    return;
                                }
                            }
                        }
                        total += Number(costData[c].costAmount);
                    }
                    var totaln2 = Number(rateData[b].invoiceAmount);
                    if(Number(total.toFixed(2)) != Number(totaln2.toFixed(2))){
                        alert("税率明细金额与对应费用行总金额不一致!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                        vm.submitLoading = false;
                        document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                        return;
                    }
                    if(rateData[b].taxRate == '0' && Number(rateData[b].taxAmount) != 0 ){
                        alert("税率为0,税额必须为0!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                        vm.submitLoading = false;
                        document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                        return;
                    }
                }
                var totaln3 = Number(vm.invoiceTableData[a].uncoveredAmount);
                if(Number(invoiceTotal.toFixed(2)) > Number(totaln3.toFixed(2))){
                    alert("税率明细总金额不能大于发票未冲销金额!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                    return;
                }
                //本次冲销金额
                vm.invoiceTableData[a].coverAmount = invoiceTotal;
                totalAmount += invoiceTotal;
                if(Number(vm.invoiceTableData[a].invoiceAmount) < Number(vm.invoiceTableData[a].taxAmount)){
                    alert("不含税金额必须大于税额!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                    return;
                }
                if(Number(vm.invoiceTableData[a].invoiceAmount) == 0){
                    alert("不含税金额不可为0!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                    return;
                }
                var totaln5 = Number(vm.invoiceTableData[a].invoiceAmount)+Number(vm.invoiceTableData[a].taxAmount);
                var totaln6 = Number(vm.invoiceTableData[a].totalAmount);
                if(Number(totaln5.toFixed(2)) != Number(totaln6.toFixed(2))){
                    alert("价税合计不相等!行:"+(a+1)+"发票号:"+vm.invoiceTableData[a].invoiceNo);
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                    return;
                }

            }

            if(totalAmount==0){
                alert("未填写匹配数据!");
                vm.submitLoading = false;
                document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                return;
            }

            //构造数据
            var match = vm.multipleSelection[0];
            var settlement = {};
            settlement.venderId = this.multipleSelection[0].venderId;
            settlement.venderName = venderInfo.venderName;
            settlement.bankName = venderInfo.bankName;
            settlement.bankAccount = venderInfo.bankAccount;
            settlement.approverEmail = match.staffNo;
            settlement.settlementAmount = totalAmount.toFixed(2);
            settlement.remark = $("#remarkId").val();
            settlement.walmartStatus = '1';//匹配,状态为审批通过
            settlement.payModel = '1';//预付款
            settlement.epsId = match.epsId;
            settlement.serviceType = match.serviceType;
            settlement.belongsTo = match.belongsTo;
            settlement.paymentMode = match.paymentMode;
            settlement.payDay = match.payDay;
            settlement.contract = match.contract;
            settlement.hasInvoice = match.hasInvoice;
            settlement.urgency = match.urgency;
            settlement.epsNo = match.epsNo;
            settlement.oldBindId = match.instanceId;
            settlement.oldTotalAmount = match.costAmount.toFixed(2);
            var uncoveredAmounts = Number(match.uncoveredAmount);
            var totalAmounts = Number(totalAmount);

            settlement.surplusAmount = uncoveredAmounts.toFixed(2)-totalAmounts.toFixed(2);

            settlement.invoiceList = vm.invoiceTableData;
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
                        vm.resetAll();
                    }else{
                        alert("保存失败");
                    }
                    vm.submitLoading = false;
                    document.getElementById("submitId").getElementsByTagName("span")[0].innerHTML = "保存匹配";
                }

                });
        },
        resetAll: function () {
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

            vm.invoiceTableData = [];
            vm.invoiceImgData = [];
            vm.costFileData = [];

            vm.query('form');

            costCount=0;
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
        importFormCancel: function () {
            vm.importDialogFormVisible = false;
        },
        importIn:function(){
            if(this.multipleSelection==null || this.multipleSelection.length==0){
                alert('请选择一条费用单!');
                return;
            }
            if(this.multipleSelection.length>1){
                alert('只能选择一条费用单!');
                return;
            }
            this.getGys(this.multipleSelection[0].venderId);
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
                vm.listLoadingImport = true;
                var flag = false
                var hh;
                var url = baseURL + "cost/matchBU/excelImport";
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoadingImport = false;
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
                            if(dataList[i].invoiceType=='01'){
                                dataList[i].invoiceKind = "1";
                            }else if(dataList[i].invoiceType=='04'){
                                dataList[i].invoiceKind = "2";
                            }else{
                                dataList[i].invoiceKind = "3";
                            }
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
                                    if(info!=null && dataList[i].invoiceType!='04') {
                                        dataList[i].invoiceDate = info.invoiceDate;
                                        dataList[i].invoiceAmount = info.invoiceAmount;
                                        dataList[i].checkCode = info.checkCode;
                                        dataList[i].totalAmount = info.totalAmount;
                                        dataList[i].taxAmount = info.taxAmount;
                                        dataList[i].isExist = true;
                                        dataList[i].uncoveredAmount = info.uncoveredAmount;
                                    }else{
                                        dataList[i].isExist = false;
                                        dataList[i].uncoveredAmount = dataList[i].totalAmount;
                                    }
                                }
                            });
                            dataList[i].coverAmount = dataList[i].uncoveredAmount;
                            //设置税率中一些默认值
                            var rateList = dataList[i].rateTableData;
                            for(var a=0;a<rateList.length;a++){
                                rateList[a].isEdit = false;
                                rateList[a].taxRateName = formatRate(rateList[a].taxRate);
                                rateList[a].costForm = {};
                                var costList = rateList[a].costTableData;
                                for(var b=0;b<costList.length;b++) {
                                    if(costList[b].costAmount>rateList[a].invoiceAmount){
                                        costList[b].costAmount = rateList[a].invoiceAmount;
                                    }
                                }
                            }
                        }
                        vm.invoiceTableData = dataList;
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
        exportIn() {
            if(this.multipleSelection==null || this.multipleSelection.length==0){
                alert('请选择一条费用单!');
                return;
            }
            if(this.multipleSelection.length>1){
                alert('只能选择一条费用单!');
                return;
            }
            var costNo = this.multipleSelection[0].costNo;
            document.getElementById("ifile").src = baseURL + "export/cost/match/template?costNo="+costNo;
        }
    }
});

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

//发票验证
function invoiceValidate(invoice){
    for(var o=0;o<vm.invoiceTableData.length;o++){
        if(invoice.invoiceNo ==vm.invoiceTableData[o].invoiceNo &&  invoice.invoiceCode==vm.invoiceTableData[o].invoiceCode ){
            alert("同一发票不可录入多次!");
            return false;
        }
    }
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
        invoice.venderid = venderInfo.venderId;
        var isExists = false;
        $.ajax({
            url: baseURL + 'cost/matchBU/checkInvoice',
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
function taxAmountss(taxAmount){
    if(taxAmount==null || taxAmount ==''){
        return '0';
    }
    return taxAmount;
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