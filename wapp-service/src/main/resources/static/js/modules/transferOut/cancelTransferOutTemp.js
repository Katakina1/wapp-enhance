
$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + 'transferOut/transferOutQuery/TransferOutedQuery',
        datatype: "json",
        postData:{
            'outDate1':vm.form.outDate1,
            'outDate2':vm.form.outDate2
        },
        colModel: [
            { label: '操作', name: 'id',  width: 40,key:true,
                formatter: function(value, options, row){
                    return '<a onclick="detail('+value+')">明细</a>';
                } },
            { label: '转出日期', name: 'outDate', width: 100,formatter: function (value, options, row){ return dateFormat(value)} },
            { label: '发票代码', name: 'invoiceCode', width: 100},
            { label: '发票号码', name: 'invoiceNo', width: 140, formatter: function (value, options, row) {
                if (row['invoiceType'] == "01") {
                    return value + '<image title="增值税专用发票" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/special-invoice.png">';
                } else if (row['invoiceType'] == "03") {
                    return value + '<image title="机动车销售统一发票" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/motor-vehicles.png">';
                } else if (row['invoiceType'] == "04") {
                    return value + '<image title="增值税普通发票" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/plain-invoice.png">';
                } else if (row['invoiceType'] == "10") {
                    return value + '<image title="增值税普通发票（电子普通发票）" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/einvoice.png">';
                } else if (row['invoiceType'] == "11") {
                    return value + '<image title="增值税普通发票（卷式发票）" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/roll-invoice.png">';
                } else if (row['invoiceType'] == "14") {
                    return value + '<image title="增值税普通发票（通行费发票）" style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/general-invoice.png">';
                }
            } },
            { label: '开票日期', name: 'invoiceDate', width: 100,formatter: function (value, options, row){ return dateFormat(value)} },
            { label: '发票状态', name: 'invoiceStatus', width: 120, align: 'center', formatter: function (value, options, row){
                if(value=='0'){
                    return '正常';
                }else if(value=='1'){
                    return '失控';
                }else if(value=='2'){
                    return '作废';
                }else if(value=='3'){
                    return '红冲';
                }else if(value=='4'){
                    return '异常';
                }else{
                    return '';
                }
            } },
            { label: '购方税号', name: 'gfTaxNo', width: 120,align:"left"},
            { label: '购方名称', name: 'gfName', width: 120,align:"left" },
            { label: '销方名称', name: 'xfName', width: 120,align:"left" },
            { label: '金额', name: 'invoiceAmount', width: 80,align:"right" },
            { label: '税额', name: 'taxAmount', width: 80,align:"right" },
            { label: '转出金额', name: 'outInvoiceAmout', width: 80,align:"right" },
            { label: '转出税额', name: 'outTaxAmount', width: 80,align:"right" },
            { label: '转出原因', name: 'outReason', width: 80 ,formatter: function (value, options, row){
                if(value=='1'){
                    return '免税项目用';
                }else if(value=='2'){
                    return '集体福利,个人消费';
                }else if(value=='3'){
                    return '非正常损失';
                }else if(value=='4'){
                    return '简易计税方法征税项目用';
                }else if(value=='5'){
                    return '免抵退税办法不得抵扣的进项税额';
                }else if(value=='6'){
                    return '纳税检查调减进项税额';
                }else if(value=='7'){
                    return '红字专用发票通知单注明的进项税额';
                }else if(value=='8'){
                    return '上期留抵税额抵减欠税';
                }else{
                    return '';
                }
            }}
        ],
        viewrecords: true,
        height: 385,
        rowNum: 10,
        rowList : [10,30,50],
        rownumbers: true,
        rownumWidth: 25,
        autowidth:true,
        autoScroll: true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        loadComplete: function(){
            console.log($("#jqGrid").getGridParam('records'));
            // $('#totalStatistics').html($("#jqGrid").jqGrid('getGridParam','total'));
        }
    });
    vm.queryGf();
});

var vm = new Vue({
    el:'#cancelTransferOut',
    data:{
        form:{
            gfNames:[],
            gfTaxNo:"-1",
            xfName: null,
            invoiceNo: null,
            invoiceStatus: "-1",
            outReason: "-1",
            outDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            outDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        rules:{
            outDate1: [
                { type: 'string', required: true, message: '转出日期范围不能为空', trigger: 'change' }
            ],
            outDate2: [
                { type: 'string', required: true, message: '转出日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true,
        detailEntityList: [],
        tempDetailEntityList:[],
        tempValue: null,
        detailForm: {
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
        detailDialogFormVisible:false,
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false
    },
    methods: {
        query: function () {
            vm.reload();
        },
        queryGf:function(){
            $.get(baseURL + 'transferOut/detailQuery/gfNameAndTaxNo',function(r){
                for(var i=0;i<r.gfNameList.length;i++){
                    vm.form.gfNames.push({name:r.gfNameList[i],taxNo:r.gfTaxNoList[i]});
                }

            })
        },
        queryXf: function (queryString, callback) {
            $.get(baseURL + 'transferOut/transferOutQuery/xfName',{queryString:queryString},function(r){
                var resultList = new Array();
                for(var i=0;i<r.list.length;i++){
                    var res = {};
                    res.value = r.list[i];
                    resultList.push(res);
                }
                callback(resultList);
            });
        },
        outDate1Change: function(val) {
            vm.form.outDate1 = val;
        },
        outDate2Change: function(val) {
            vm.form.outDate2 = val;
        },
        cancelOut:function(){
            var ids=[];
            var rows=$('#jqGrid').jqGrid('getGridParam','selarrrow');
            if(!rows.length>0){
                alert("请勾选要取消转出的发票！");
                return;
            }
            for (var i=0;i<rows.length;i++){
                ids.push(rows[i]);
            }
            var idss=ids.join(",");
            confirm('确定要取消转出选中的发票？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "transferOut/transferOutQuery/cancelTransferOut",
                    data:{"idss":idss},
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        detailFormCancel: function () {
            vm.detailDialogFormVisible=false;
            vm.detailForm.gfName=null;
            vm.detailForm.gfTaxNo=null;
            vm.detailForm.gfAddressAndPhone=null;
            vm.detailForm.gfBankAndNo=null;
            vm.detailForm.xfName=null;
            vm.detailForm.xfTaxNo=null;
            vm.detailForm.xfAddressAndPhone=null;
            vm.detailForm.xfBankAndNo=null;
            vm.detailForm.model=null;
            vm.detailForm.unit=null;
            vm.detailForm.num=null;
            vm.detailForm.unitPrice=null;
            vm.detailForm.detailAmount=null;
            vm.detailForm.taxAmount=null;
            vm.detailForm.taxRate=null;
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{
                    'gfTaxNo': vm.form.gfTaxNo == -1 ? null:vm.form.gfTaxNo,
                    'xfName': vm.form.xfName,
                    'invoiceNo': vm.form.invoiceNo,
                    'invoiceStatus': vm.form.invoiceStatus == -1 ? null : vm.form.invoiceStatus,
                    'outReason':vm.form.outReason==-1?null:vm.form.outReason,
                    'outDate1':vm.form.outDate1,
                    'outDate2':vm.form.outDate2
                },
                page:page
            }).trigger("reloadGrid");
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
            vm.detailEntityList = [];
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.stringTotalAmount = null;
            vm.tempValue = null;
            vm.tempDetailEntityList=[];
            $("#CommonDetail").css('display', 'none');

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
            vm.detailEntityList = [];
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
            vm.detailEntityList = [];
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.stringTotalAmount = null;
            vm.tempValue = null;
            $("#CommonDetail").css('display', 'none');
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
        }
    }
});

function detail(value) {
    vm.tempValue = value;
    $.ajax({
        type: "POST",
        url: baseURL + "transferOut/detailQuery/invoiceDetail",
        contentType: "application/json",
        data: JSON.stringify(value),
        success: function (r) {
            //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
            if (r.code == 0) {
                if (r.invoiceEntity.invoiceType == "03") {
                    vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                    vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                    vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                    vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                    vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                    vm.detailForm.vehicleType = r.detailVehicleEntity.vehicleType;
                    vm.detailForm.factoryModel = r.detailVehicleEntity.factoryModel;
                    vm.detailForm.productPlace = r.detailVehicleEntity.productPlace;
                    vm.detailForm.certificate = r.detailVehicleEntity.certificate;
                    vm.detailForm.certificateImport = r.detailVehicleEntity.certificateImport;
                    vm.detailForm.inspectionNum = r.detailVehicleEntity.inspectionNum;
                    vm.detailForm.engineNo = r.detailVehicleEntity.engineNo;
                    vm.detailForm.vehicleNo = r.detailVehicleEntity.vehicleNo;
                    vm.detailForm.totalAmount = r.invoiceEntity.totalAmount;
                    vm.detailForm.xfName = r.invoiceEntity.xfName;
                    vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                    vm.detailForm.phone = r.phone;
                    vm.detailForm.address = r.address;
                    vm.detailForm.account = r.account;
                    vm.detailForm.bank = r.bank;
                    vm.detailForm.taxRate = r.detailVehicleEntity.taxRate;
                    vm.detailForm.taxAmount = r.invoiceEntity.taxAmount;
                    vm.detailForm.taxBureauName = r.detailVehicleEntity.taxBureauName;
                    vm.detailForm.taxBureauCode = r.detailVehicleEntity.taxBureauCode;
                    vm.detailForm.invoiceAmount = r.invoiceEntity.invoiceAmount;
                    vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                    vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                    vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                    vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                    vm.detailDialogVehicleFormVisible = true;
                } else if (r.invoiceEntity.invoiceType == "14") {
                    vm.detailForm.gfName = r.invoiceEntity.gfName;
                    vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                    vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                    vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                    vm.detailForm.xfName = r.invoiceEntity.xfName;
                    vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                    vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                    vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                    vm.detailForm.remark = r.invoiceEntity.remark;
                    vm.detailForm.totalAmount = r.invoiceEntity.totalAmount;
                    vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                    vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                    vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                    vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                    vm.detailEntityList = r.detailEntityList;
                    vm.detailForm.detailAmountTotal = r.detailAmountTotal;
                    vm.detailForm.taxAmountTotal = r.taxAmountTotal;
                    vm.detailDialogCheckFormVisible = true;
                } else {
                    vm.detailForm.gfName = r.invoiceEntity.gfName;
                    vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                    vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                    vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                    vm.detailForm.xfName = r.invoiceEntity.xfName;
                    vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                    vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                    vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                    vm.detailForm.remark = r.invoiceEntity.remark;
                    vm.detailForm.totalAmount = r.invoiceEntity.totalAmount;
                    vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                    vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                    vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                    vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                    vm.detailForm.detailAmountTotal = r.detailAmountTotal;
                    vm.detailForm.taxAmountTotal = r.taxAmountTotal;
                    if (r.detailEntityList.length > 8) {
                        $("#CommonDetail").css('display', 'block');
                        //document.getElementById("CommonDetail").style.display="block";
                        vm.detailEntityList.push(r.detailEntityList[0]);
                    } else {
                        $("#CommonDetail").css('display', 'none');
                        vm.detailEntityList = r.detailEntityList;
                    }
                    vm.detailDialogFormVisible = true;
                    vm.tempDetailEntityList= r.detailEntityList;
                }
            } else {
                alert(r.msg);
            }
        }
    });
}

function dateFormat(value){
    if(value==null){
        return '';
    }
    return value.substring(0, 10);
}
function detailDateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}

function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
}