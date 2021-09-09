Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var isInitial = true;
var selectedRow = {};
var currentQueryParam = {
    jvcode: "-1",
    invoiceNo: null,
    isReturnTicket: "0",
    invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
    invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
    flowType: "-1",
    page: 1,
    limit: 1
};

var vm = new Vue({
    el:'#scanConfirmApp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        listLoading: false,
        invoiceDateOptions: {},
        jvs: [{
            value: "-1",
            label: "全部"
        }],
        confirmJvs:[],
        confirmVenders:[],
        flowTypes: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            jvcode: "-1",
            invoiceNo: null,
            isReturnTicket: "0",
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            flowType: "-1"
        },
        confirmForm:{
            reason: '',
            jvcode: '',
            venderid: '',
            deductibleTaxRate: '',
            deductibleTax: ''
        },
        formVisible: false,
        importDialogFormVisible:false,
        listLoadingImport: false,
        selectFileFlag:''
    },
    mounted:function(){
        this.invoiceDateOptions = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.getJV();
        this.getVender();
        this.querySearchFlowType();
    },
    watch: {
        'confirmForm.venderid': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.confirmForm.venderid = oldValue;
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
        }
    },
    methods: {
        querySearchFlowType: function () {
            $.get(baseURL + 'pack/GenerateBindNumber/searchFlowType',function(r){
                var flowTypes = [];
                flowTypes.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var flowType = {};
                    flowType.value = r.optionList[i].value;
                    flowType.label = r.optionList[i].label;
                    if(flowType.label!="费用"){
                        flowTypes.push(flowType);
                    }
                }
                vm.flowTypes = flowTypes;
            });
        },
        query: function () {
            isInitial = false;
            currentQueryParam = {
                'jvcode': vm.form.jvcode,
                'invoiceNo': vm.form.invoiceNo,
                'isReturnTicket': vm.form.isReturnTicket,
                'invoiceDate1': vm.form.invoiceDate1,
                'invoiceDate2': vm.form.invoiceDate2,
                'flowType': vm.form.flowType
            };
            $(".checkMsg").remove();
            var checkDate = true;

            var invoiceStartDate = new Date(vm.form.invoiceDate1);
            var invoiceEndDate = new Date(vm.form.invoiceDate2);

            invoiceStartDate.setMonth(invoiceStartDate.getMonth() + 12);

            if ( invoiceEndDate.getTime()+1000*60*60*24 > invoiceStartDate.getTime()) {
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkDate=false;
            }else if(invoiceEndDate.getTime() < new Date(vm.form.invoiceDate1)){
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate=false;
            }

            if(!checkDate){
                return;
            }

            this.$refs['form'].validate(function (valid) {
                if (valid) {
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },
        getJV: function () {
            $.get(baseURL + 'cost/application/getGfInfo',function(r){
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].optionKey;
                    gf.label = r.optionList[i].optionName;
                    gfs.push(gf);
                }
                vm.jvs = gfs;
            });
        },
        getJVByTaxNo: function(taxNo){
            $.get(baseURL + 'info/scanConfirm/getJV',{taxNo: taxNo},function(r){
                var gfs = [];
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].optionKey;
                    gf.label = r.optionList[i].optionName;
                    gfs.push(gf);
                }
                vm.confirmJvs = gfs;
            });
        },
        getVender: function(){
            $.get(baseURL + 'info/scanConfirm/getVender',function(r){
                var gfs = [];
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].optionKey;
                    gf.label = r.optionList[i].optionName;
                    gfs.push(gf);
                }
                vm.confirmVenders = gfs;
            });
        },
        invoiceDate1Change: function(val) {
            vm.form.invoiceDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form.invoiceDate2 = val;
        },
        formClose: function(){
            this.formVisible = false;
        },
        confirm: function(row){
            selectedRow = row;
            this.$set(this.confirmForm,'reason','');
            this.$set(this.confirmForm,'jvcode','');
            this.$set(this.confirmForm,'venderid','');
            this.$set(this.confirmForm,'deductibleTaxRate','');
            this.$set(this.confirmForm,'deductibleTax','');
            this.getJVByTaxNo(row.gfTaxNo);
            this.formVisible = true;
        },
        submitConfirm: function(){
            if(vm.confirmForm.reason==null || vm.confirmForm.reason.trim().length==0){
                alert("请填写旧发票号");
                return;
            }
            if(vm.confirmForm.jvcode==null || vm.confirmForm.jvcode==''){
                alert("请选择JV");
                return;
            }
            if(vm.confirmForm.venderid==null || vm.confirmForm.venderid==''){
                alert("请选择供应商");
                return;
            }
            var entity = {};
            entity.invoiceCode = selectedRow.invoiceCode;
            entity.invoiceNo = selectedRow.invoiceNo;
            entity.gfTaxNo = selectedRow.gfTaxNo;
            entity.jvcode = vm.confirmForm.jvcode;
            entity.venderid = vm.confirmForm.venderid;
            entity.confirmReason = vm.confirmForm.reason;
            entity.deductibleTaxRate = vm.confirmForm.deductibleTaxRate;
            entity.deductibleTax = vm.confirmForm.deductibleTax;
            $.ajax({
                type:"POST",
                url:baseURL + 'info/scanConfirm/submit',
                data: JSON.stringify(entity),
                dataType:"json",
                contentType:"application/json",
                async: false,
                cache:false,
                success:function(r){
                    if(r.code==0) {
                        alert("确认成功");
                        vm.query();
                    }else{
                        alert("确认失败,请稍后重试");
                    }
                    vm.formVisible = false;
                }
            });
        },
        exportExcel: function(){
            currentQueryParam = {
                'jvcode': vm.form.jvcode,
                'invoiceNo': vm.form.invoiceNo,
                'isReturnTicket': vm.form.isReturnTicket,
                'invoiceDate1': vm.form.invoiceDate1,
                'invoiceDate2': vm.form.invoiceDate2,
                'flowType': vm.form.flowType
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':41,'condition':JSON.stringify(currentQueryParam)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
            // document.getElementById("ifile").src = encodeURI(baseURL + 'export/info/scanConfirm'
            //     +'?jvcode='+currentQueryParam.jvcode
            //     +'&invoiceNo='+(currentQueryParam.invoiceNo==null?'':currentQueryParam.invoiceNo)
            //     +'&invoiceDate1='+currentQueryParam.invoiceDate1
            //     +'&invoiceDate2='+currentQueryParam.invoiceDate2);
            $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
        },
        importFormCancel: function () {
            vm.importDialogFormVisible = false;
        },
        importExcel:function(){
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
        importSubmit: function(){
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
                var url = baseURL + "import/info/scanConfirm";
                this.$http.post(url, formData, config).then(function (response) {
                    this.listLoadingImport = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    this.selectFileFlag = '';
                    this.file = '';
                    $("#upload_form")[0].reset();
                    alert(response.data.msg);
                    vm.query();
                    vm.importDialogFormVisible = false;
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
            this.$http.post(baseURL + 'info/scanConfirm/list',
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

                this.tableData = xhr.page.list;
                this.listLoading = false;

                if(this.tableData == null || this.tableData.length == 0){
                    $("#export_btn").attr("disabled","disabled").addClass("is-disabled");
                }else{
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
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
        flowTypeFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        flowTypeBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
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

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}