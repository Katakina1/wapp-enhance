
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;
var currentQueryParam = {
    jvCode: "-1",
    invoiceNo:'',
    venderId:'',
    companyCode:''

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
        listLoading: false,
        totalAmount: 0,
        totalTax: 0,
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rejectWin:false,
        rzhDate2: {},
        sumReturnAmount:0.00,
        poCodeMaxlength: 16,
        claimCodeMaxlength:16,
        currentRow:{},
        rejectForm:{
            reason:'',
        },
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form:{
            jvCode: "-1",
            invoiceNo:'',
            venderId:'',
            companyCode:''

        },
        listLoading4: false,
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
        rejectWinClose:function () {
            vm.rejectWin=false;
            vm.rejectForm.reason='';
        },
        submitReason:function () {
            this.$refs['rejectForm'].validate(function (valid) {
                if (valid) {
                    var  result={
                        id:vm.currentRow.id,
                        reason:vm.rejectForm.reason,
                        uuid:vm.currentRow.uuid
                    };

                    $.ajax({
                        url: baseURL + 'modules/fixed/sapUnconfirm/refund',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(result),
                        success: function (r) {
                            /*if (r.code == 0) {
                            }*/
                            if(r.msg=="0"){
                                vm.findAll(1);
                                alert("退票成功！")
                                vm.rejectWinClose();
                            }else{
                                alert("退票失败！")
                            }
                        }
                    });
                } else {
                    return false
                }
            })



        },
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        query: function (formName) {
            isInitial = false;
            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'jvCode':vm.form.jvCode,
                        'invoiceNo': vm.form.invoiceNo,
                        'venderId':vm.form.venderId,
                        'companyCode': vm.form.companyCode
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
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }

        },
        matchSuccess:function(row){
                $.ajax({
                    type: "POST",
                    url: baseURL + "modules/fixed/sapUnconfirm/sapSuccess",
                    dataType: "json",
                    data: {invoiceId: row.id},
                    success: function (r) {
                        if (r.code == 0) {
                            vm.findAll(1);
                            alert('已修改为匹配成功');
                        } else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                parent.location.href = baseURL + 'login.html';
                            });
                        }else {
                            alert(r.msg);
                        }
                    }
                });

        },
        refund:function (row) {
            vm.currentRow=row;
            vm.rejectWin=true;
        },
        findAll: function () {
            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                jvCode:currentQueryParam.jvCode,
                invoiceNo: currentQueryParam.invoiceNo,
                venderId:currentQueryParam.venderId,
                companyCode: currentQueryParam.companyCode
            };
            this.listLoading = true;
            var flag = false;
            this.$http.post(baseURL + 'modules/fixed/sapUnconfirm/list',
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
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/fixed/invoiceImportAndExport/invoiceImportAndExportExports'
                +'?jvCode='+currentQueryParam.jvCode
                +'&invoiceNo='+currentQueryParam.invoiceNo
                +'&venderId='+currentQueryParam.venderId
                +'&companyCode='+currentQueryParam.companyCode
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
            document.getElementById("ifile").src = baseURL + "modules/fixed/invoiceImportAndExportExportImport";
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式！");
                this.isNeedFileExtension = false;
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
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式！");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件！");
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
                var url = baseURL + "modules/fixed/invoiceImportAndExportExportImport";
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    // vm.file = '';
                    if (response.data.success) {
                        vm.batchRedTicketDialog = false;
                        vm.findAll(vm.currentPage);
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