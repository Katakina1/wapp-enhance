Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var bigFlag = false;
function bands(){
    if(bigFlag){
        $(".layui-layer").css("left","218px").css("width","9.22rem").css("top","56px").css("height",'5.4rem').children(".layui-layer-content").height("416px");
        bigFlag = false;
    }else{
        $(".layui-layer").css("left","2%").css("width","96%").css("top","0rem").css("height",$(window).height()-10).children(".layui-layer-content").height($(window).height()-123);
        bigFlag = true;
    }
}

bigimgpath = "";
function bigimg(){
    layer.open({
        type: 1,
        title: false,
        closeBtn: 0,
        area: '70%',
        skin: 'layui-layer-nobg', //没有背景色
        shadeClose: true,
        content: $('#imgEdit'),
        shade: [0.01, '#393D49']
    });
}

var isInitial = true;
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
}

function formaterDate2(cellvalue) {
    if(cellvalue != null){
        var d = new Date(cellvalue.toString().replace(/-/g, "/")),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2) month = '0' + month;
        if (day.length < 2) day = '0' + day;

        return [year, month, day].join('');
    }else{
        return '';
    }

}
function decimal(cellvalue, options, rowObject) {
    if (cellvalue != null) {
        var val = Math.round(cellvalue * 100) / 100;
        return val.formatMoney();
    }
    return "—— ——";
}

Number.prototype.formatMoney = function (places, symbol, thousand, decimal) {
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
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fpdmlist = ["144031539110", "131001570151", "133011501118", "111001571071"];
    console.log(fpdm);
    var fplx = "";
    if (fpdm.length == 12) {
        var fplxflag = fpdm.substring(7, 8);

        for (var i = 0; i < fpdmlist.length; i++) {
            if (fpdm == fpdmlist[i]) {
                fplx = "10";
                break;
            }
        }
        if (fpdm.substring(0, 1) == "0" && fpdm.substring(10, 12) == "11") {
            fplx = "10";
        }
        if (fpdm.substring(0, 1) == "0" && fpdm.substring(10, 12) == "12") {
            fplx = "14";
        }
        if (fpdm.substring(0, 1) == "0" && (fpdm.substring(10, 12) == "06" || fpdm.substring(10, 12) == "07")) {
            //判断是否为卷式发票  第1位为0且第11-12位为06或07
            fplx = "11";
        }
        if (fpdm.substring(0, 1) == "0" && (fpdm.substring(10, 12) == "04" || fpdm.substring(10, 12) == "05")) {
            fplx = "04"
        }
        if (fplxflag == "2" && !fpdm.substring(0, 1) == "0") {
            fplx = "03";
        }

    } else if (fpdm.length == 10) {
        var fplxflag = fpdm.substring(7, 8);
        if (fplxflag == "1" || fplxflag == "5") {
            fplx = "01";
        } else if (fplxflag == "6" || fplxflag == "3") {
            fplx = "04";
        } else if (fplxflag == "7" || fplxflag == "2") {
            fplx = "02";
        }

    }
    return fplx;
}

var vm = new Vue({
    el:'#costQueryApp',
    data:{
        img_src:'',
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        rateOptions:[],
        statusOptions:[],
        listLoading: false,
        editDialogLoading: false,
        invoiceDateOptions: {},
        createDateOptions: {},
        dataEditForm: {},
        refundReasonForm:{},
        refundReason:{},
        refundReasonEdit:false,
        form:{
            costNo: '',
            epsNo:'',
            venderId:'',
            scanStatus: '-1',
            payModel: '1',
            createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        dialogVisibleEdit: false,
        invoiceTableData:[],
        invoiceContrastData:[],
        fileData:[],
        showDetailWin: true,
        showContrastWin: true,
        showFileDetailWin: true,
        showImgWin: true
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
        this.getRateSelection();
        this.getStatusSelection();
        this.showDetailWin = false;
        this.showContrastWin = false;
        this.showFileDetailWin = false;
        this.showImgWin = false;
        this.invoiceDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    methods: {
        getStatusSelection: function(){
            $.get(baseURL + 'cost/query/getStatusOptions',function(r){
                var option = {optionKey: '', optionName: '全部'};
                vm.statusOptions.push(option);
                for(var i=0;i<r.optionList.length;i++){
                    vm.statusOptions.push(r.optionList[i]);
                }
            });
        },
        getRateSelection: function(){
            $.get(baseURL + 'cost/application/getRateOptions',function(r){
                vm.rateOptions = r.optionList;
            });
        },
        query: function(formName){
            isInitial = false;
            this.findAll(1);
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        exportExcel: function(){
            // document.getElementById("downloadFileId").src = encodeURI(baseURL + 'export/cost/listAllExport/lists'
            //     +'?costNo='+vm.form.costNo
            //     +'&epsNo='+vm.form.epsNo
            //     +'&payModel='+vm.form.payModel
            //     +'&venderId='+vm.form.venderId
            //     +'&scanStatus='+vm.form.scanStatus
            //     +'&invoiceDate1='+vm.form.createDate1
            //     +'&invoiceDate2='+vm.form.createDate2
            // );

            var params ={
                'costNo':vm.form.costNo,
                'epsNo':vm.form.epsNo,
                'payModel':vm.form.payModel,
                'venderId':vm.form.venderId,
                'scanStatus':vm.form.scanStatus,
                'createDate1':vm.form.createDate1,
                'createDate2':vm.form.createDate2
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':45,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
        },
        findAll: function (currentPage) {
            var params = {
                costNo: vm.form.costNo,
                epsNo:vm.form.epsNo,
                payModel:vm.form.payModel,
                venderId:vm.form.venderId,
                scanStatus: vm.form.scanStatus,
                createDate1: vm.form.createDate1,
                createDate2: vm.form.createDate2,
                page: currentPage,
                limit: this.pageSize
            };
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'cost/query/costQueryQuits',
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
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                }
            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        },
        changeAmount: function(val) {
            var positiveReg = /(^[0]$)|(^[1-9]\d*$)|(^[1-9]\d*\.\d{1,2}$)|(^0\.\d{1,2}$)/;
            var negativeReg = /(^-[1-9]\d*$)|(^-[1-9]\d*\.\d{1,2}$)|(^-0\.\d{1,2}$)/;
            if (!(new RegExp(positiveReg).test(val) || new RegExp(negativeReg).test(val))) {
                vm.dataEditForm.invoiceAmount = "";
            }
        },
        changeTaxAmount: function(val) {
            var positiveReg = /(^[0]$)|(^[1-9]\d*$)|(^[1-9]\d*\.\d{1,2}$)|(^0\.\d{1,2}$)/;
            var negativeReg = /(^-[1-9]\d*$)|(^-[1-9]\d*\.\d{1,2}$)|(^-0\.\d{1,2}$)/;
            if (!(new RegExp(positiveReg).test(val) || new RegExp(negativeReg).test(val))) {
                vm.dataEditForm.taxAmount = "";
            }
        },
        invoiceDate3Change: function (val) {
            vm.dataform.invoiceDate = new Date(val);
        },
        imgRow:function(curObj){
            console.log(curObj);
            var scanId = curObj;
            var url=baseURL+'rest/invoice/sign/getImg?scanId='+scanId;
            var img = "<a href='javaScript:void(0)' onclick='bands()' ><img src='" + url + "' style='width:100%;height:auto;'/></a>";
            layer.open({
                type: 1,
                shade: false,
                title: "图片", //不显示标题
                area: ['9.22rem', '5.4rem'],
                content: img, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
                btn: ['关闭']
            })

        },
        beforeCloseEditWin: function () {
            vm.dialogVisibleEdit = false;
            vm.$refs["dataEditForm"].resetFields();
        },
        beforeCloseEditWins: function () {
            vm.refundReasonEdit = false;
            vm.$refs["refundReasonForm"].resetFields();
        },
        editSaveData: function () {
            this.$refs["dataEditForm"].validate(function (valid) {
                if (valid) {
                    var invoiceType = getFplx(vm.dataEditForm.invoiceCode);
                    var params = {
                        "id": vm.dataEditForm.id,
                        "invoiceCode": vm.dataEditForm.invoiceCode,
                        "invoiceNo": vm.dataEditForm.invoiceNo,
                        "dyInvoiceCode": vm.dataEditForm.dyInvoiceCode,
                        "dyInvoiceNo": vm.dataEditForm.dyInvoiceNo,
                        "gfTaxNo": vm.dataEditForm.gfTaxNo,
                        "checkCode": vm.dataEditForm.checkCode,
                        "invoiceAmount": vm.dataEditForm.invoiceAmount,
                        "invoiceDate": vm.dataEditForm.invoiceDate,
                        "invoiceType": invoiceType,
                        "xfTaxNo": vm.dataEditForm.xfTaxNo,
                        "taxAmount": vm.dataEditForm.taxAmount,
                        "totalAmount": parseFloat(vm.dataEditForm.taxAmount)+parseFloat(vm.dataEditForm.invoiceAmount)+"",
                        "uuid": vm.dataEditForm.invoiceCode + vm.dataEditForm.invoiceNo,
                        "venderid":vm.dataEditForm.venderid,
                        "costNo":vm.dataEditForm.costNo,
                        "flowType":vm.dataEditForm.flowType

                        //,
                        //"isExistStamper":vm.dataEditForm.isExistStamper,
                        //"noExistStamperNotes":vm.dataEditForm.noExistStamperNotes
                    };

                    $.ajax({
                        type: "POST",
                        url: baseURL + "scanQueryCost/checkInvoice",
                        contentType: "application/json",
                        data: JSON.stringify(params),
                        success: function (r) {
                            if(r=="0"){
                                alert("修改成功");
                            }else if(r=="5001"){
                                vm.openConfirm(vm, "\n" + "发票已存在，确定要删除该记录？", function () {
                                    var params2 = {
                                        "id":params.id,
                                        "qsStatus":params.qsStatus,
                                        "scanMatchStatus":params.scanMatchStatus
                                    };
                                    $.ajax({
                                        type: "POST",
                                        url: baseURL + "SignatureProcessing/scanDeleteIevoice",
                                        contentType: "application/json",
                                        data: JSON.stringify(params2),
                                        success: function (r) {
                                            if (r.code == 0) {
                                                alert(r.msg);
                                            }else{
                                                alert("刪除失败");
                                            }
                                        }
                                    });
                                }, function () {
                                });
                            }else{
                                alert("修改失败");
                            }

                            vm.dialogVisibleEdit = false;
                            vm.$refs["dataEditForm"].resetFields();
                            vm.findAll(1);
                        }
                    });

                } else {
                    return false;
                }
            });
        },
        editData: function (row) {

            // $("#imgEdit").attr("src", "");
            vm.listLoading = true;
            var uuid = row.invoiceCode + row.invoiceNo;

            vm.listLoading = false;
            vm.dialogVisibleEdit = true;
            $("#imgEdit").attr("src","../../img/fpload.png");

            vm.dataEditForm = {
                id: row.id,
                gfTaxNo: row.gfTaxNo,
                invoiceNo: row.invoiceNo,
                invoiceCode: row.invoiceCode,
                invoiceDate: formaterDate2(row.invoiceDate),
                xfTaxNo: row.xfTaxNo,
                invoiceAmount: toDecimal2(row.invoiceAmount),
                taxAmount: toDecimal2(row.taxAmount),
                checkCode: row.checkCode,
                dyInvoiceCode:row.dyInvoiceCode,
                dyInvoiceNo:row.dyInvoiceNo,
                venderid:row.venderid,
                venderidEdit:row.venderidEdit,
                costNo:row.costNo,
                flowType:row.flowType
                //,
                //isExistStamper:row.isExistStamper==1?1+"":0+"",
                //noExistStamperNotes:row.noExistStamperNotes
            };

            vm.img_src=baseURL+'rest/invoice/sign/getImg?scanId='+row.scanId;
        },
        refundReasons:function (row) {
            vm.refundReasonEdit=true;
            vm.refundReasonForm={
                costNo:row.costNo,
                instanceId:row.instanceId,
                epsNo:row.epsNo,
                payModel:row.payModel
            }
        },
        refundYFReasons:function (row) {
            vm.refundReasonEdit=true;
            vm.refundReasonForm={
                costNo:row.costNo,
                instanceId:row.instanceId,
                epsNo:row.epsNo,
                payModel:row.payModel
            }
        },

        deleteData: function (row) {
            vm.openConfirm(vm, "\n" + "确定要退票选中的记录？", function () {
                var params = {
                    "costNo":vm.refundReasonForm.costNo,
                    "instanceId":vm.refundReasonForm.instanceId,
                    "epsNo":vm.refundReasonForm.epsNo,
                    "refundCode":vm.refundReasonForm.refundCode,
                    "refundReason":vm.refundReasonForm.refundReason,
                    "belongsTo":vm.refundReasonForm.belongsTo,
                    "payModel":vm.refundReasonForm.payModel
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "cost/query/deleteRevoicessb",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }
                        vm.beforeCloseEditWins();
                        vm.findAll(1);
                    }

                });
            }, function () {
            });
        },

        returnData: function (row){
            vm.openConfirm(vm, "\n" + "确定要撤回选中的记录？", function () {
                var params = {
                    "invoiceCode": row.invoiceCode,
                    "invoiceNo": row.invoiceNo,
                    "id":row.id
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/returnRevoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }
                        vm.findAll(1);
                    }
                });
            }, function () {
            });



        },
        deleteDataCost: function(row){
            vm.openConfirm(vm, "\n" + "确定要删除选中的记录？", function () {
                var params = {
                    "costNo":row.costNo
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "scanQueryCost/deleteDateCost",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }else{
                            alert(r.msg);
                        }
                        vm.findAll(1);
                    }
                });
            }, function () {
            });
        },
        deleteInvoice:function (id) {
            vm.openConfirm(vm, "\n" + "确认选中删除的扫描发票？", function () {
                var params = {
                    "scanId": id
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "scanQueryCost/deleteScanInvoice",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }else{
                            alert(r.msg);
                        }
                        vm.beforeCloseDetailWin();
                        vm.findAll(1);
                    }
                });


            });
        },
        confirmDateCost: function(row){
            vm.openConfirm(vm, "\n" + "确认选中的记录？", function () {
                var params = {
                    "costNo":row.costNo
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "scanQueryCost/confirmDateCost",
                    contentType: "application/json",
                    data: JSON.stringify(params),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                        }else{
                            alert(r.msg);
                        }
                        vm.findAll(1);
                    }
                });
            }, function () {
            });
        },
        confirmDateContrast:function(row){
            var params = {
                "costNo":row.costNo
            };
            this.$http.post(baseURL + 'cost/query/confirmDateContrast',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.invoiceContrastData = xhr.invoiceContrastList;
                vm.showContrastWin = true;
            });
        },
        detail: function(row){
            var params = {costNo: row.costNo};
            this.$http.post(baseURL + 'cost/query/details',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.invoiceTableData = xhr.invoiceList;
                $("#detailRemarkId").val(row.remark);
                vm.showDetailWin = true;
            });
        },
        beforeContrastWin: function(){
            this.invoiceContrastData = [];
            vm.showContrastWin = false;
        },
        beforeCloseDetailWin: function(){
            this.invoiceTableData = [];
            vm.showDetailWin = false;
        },
        fileDetail: function(row){
            var params = {costNo: row.costNo};
            this.$http.post(baseURL + 'cost/query/fileDetail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.fileData = xhr.fileList;
                vm.showFileDetailWin = true;
            });
        },
        beforeCloseFileDetailWin: function(){
            this.fileData = [];
            vm.showFileDetailWin = false;
        },
        viewFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("viewDetailImgId").src = baseURL + 'cost/application/viewFile?id='+row.id + "&token=" + token;
                },
                error: function () {

                }
            });
            vm.showImgWin = true;
        },
        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'cost/application/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        beforeCloseImgWin: function () {
            document.getElementById("viewDetailImgId").src = '';
            vm.showImgWin = false;
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




        invoiceAmountFormatDecimal: function (row, column, index) {
            return decimal(row.invoiceAmount);
        }, taxAmountFormatDecimal: function (row, column, index) {
            return decimal(row.taxAmount);
        }, qsStatusFormatter: function (row) {
            var val = row.qsStatus;
            if (val == "1") {
                return "签收成功";
            } else  {
                return "签收失败";
            }
        },
        fileTypeFormatter:function (row) {
            var val = row.fileType;
            if (val == "1") {
                return "发票";
            } else if (val == "3") {
                return "封面";
            }else{
                return "附件";
            }
        },
        rzhYesornoFormatter: function (row) {
            var val = row.rzhYesorno;
            if (val == "1") {
                return "已认证";
            } else  {
                return "未认证";
            }
        },
        formatCreateDate: function (row, column) {
            if (row.createDate != null) {
                return formaterDate(row.createDate);
            } else {
                return '';
            }
        },
        formatScanDate: function (row, column) {
            if (row.scanDate != null) {
                return formaterDate(row.scanDate);
            } else {
                return '';
            }
        },
        formatRzhDate: function (row, column) {
            if (row.rzhDate != null) {
                return formaterDate(row.rzhDate);
            } else {
                return '';
            }
        },
        formatScanMatchStatus: function (row, column){
            if(row.fileType==1){
                if(row.scanMatchStatus==1){
                    return "匹配成功"
                }else if(row.scanMatchStatus==2){
                    return "匹配失败"
                }else  {
                    return "未匹配"
                }
            }else{
                return "—— ——"
            }
        },
        formatScanStatus: function (row, column){
                if(row.scanStatus==1){
                    return "匹配成功"
                }else if(row.scanStatus==2){
                    return "匹配失败"
                }else  {
                    return "未匹配"
                }
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '';
            }
        }, formatQsDate: function (row) {
            if (row.signInDate != null) {
                return formaterDate(row.signInDate);
            } else {
                return '';
            }
        },
        formatContrast1Date: function (row) {
            if (row.invoiceType == row.invoiceTypeX) {
                return 'OK';
            } else {
                return '失败('+row.invoiceType+':'+row.invoiceTypeX+')';
            }
        },
        formatContrast2Date: function (row) {
            if (row.venderid == row.venderidX) {
                return 'OK';
            } else {
                return '失败('+row.venderid+':'+row.venderidX+')';
            }
        },
        formatContrast3Date: function (row) {
            if (row.invoiceCode == row.invoiceCodeX) {
                return 'OK';
            } else {
                return '失败('+row.invoiceCode+':'+row.invoiceCodeX+')';
            }
        },
        formatContrast4Date: function (row) {
            if (row.invoiceNo == row.invoiceNoX) {
                return 'OK';
            } else {
                return '失败('+row.invoiceNo+':'+row.invoiceNoX+')';
            }
        },
        formatContrast5Date: function (row) {
            if (row.invoiceDate == row.invoiceDateX) {
                return 'OK';
            } else {
                return '失败('+formaterDate(row.invoiceDate)+':'+formaterDate(row.invoiceDateX)+')';
            }
        },
        formatContrast6Date: function (row) {
            if (row.invoiceAmount == row.invoiceAmountX) {
                return 'OK';
            } else {
                return '失败('+row.invoiceAmount+':'+row.invoiceAmountX+')';
            }
        },
        formatContrast7Date: function (row) {
            if (row.taxAmount == row.taxAmountX) {
                return 'OK';
            } else {
                return '失败('+row.taxAmount+':'+row.taxAmountX+')';
            }
        },
        formatContrast8Date: function (row) {
            if (row.totalAmount == row.totalAmountX) {
                return 'OK';
            } else {
                return '失败('+row.totalAmount+':'+row.totalAmountX+')';
            }
        },




        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        rateFormat: function (row, column, cellValue, index) {
            return formatRate(cellValue);
        },
        statusFormat: function (row, column, cellValue, index) {
            if(cellValue==null){
                return '';
            }
            for(var i=0;i<vm.statusOptions.length;i++){
                if(cellValue==vm.statusOptions[i].optionKey){
                    return vm.statusOptions[i].optionName;
                }
            }
            return cellValue;
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

/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.q.invoiceNo = t.value.replace(reg, '');

}
function toDecimal2(x) {
    var f = parseFloat(x);
    if (isNaN(f)) {
        return '';
    }
    var f = Math.round(x*100)/100;
    var s = f.toString();
    var rs = s.indexOf('.');
    if (rs < 0) {
        rs = s.length;
        s += '.';
    }
    while (s.length <= rs + 2) {
        s += '0';
    }
    return s;
}
function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}