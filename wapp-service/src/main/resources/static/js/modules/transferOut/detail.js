

function detail(row) {
    var value=row.id;
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
                    vm.detailForm.invoiceDate =detailDateFormat(r.invoiceEntity.invoiceDate);
                    vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                    vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                    vm.detailForm.taxAmount = r.invoiceEntity.taxAmount;
                    vm.detailForm.totalAmount = r.invoiceEntity.totalAmount;
                    vm.detailForm.xfName = r.invoiceEntity.xfName;
                    vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                    vm.detailForm.phone = r.phone;
                    vm.detailForm.address = r.address;
                    vm.detailForm.account = r.account;
                    vm.detailForm.bank = r.bank;
                    if(r.detailVehicleEntity!=null){
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
                        vm.detailForm.invoiceAmount = r.invoiceEntity.invoiceAmount;
                        vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                        vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                        vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                        vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                    }
                    vm.detailDialogVehicleFormVisible = true;
                } else if (r.invoiceEntity.invoiceType == "14") {
                    vm.detailDialogCheckFormVisible = true;
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
                    if (r.detailEntityList.length > 2) {
                        vm.detailEntityList.push(r.detailEntityList[0]);
                    } else {
                        vm.detailEntityList = r.detailEntityList;
                    }
                    vm.tempDetailEntityList= r.detailEntityList;
                } else {
                    vm.detailDialogFormVisible = true;
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
                    if (r.detailEntityList.length > 2) {
                        vm.detailEntityList.push(r.detailEntityList[0]);
                    } else {
                        vm.detailEntityList = r.detailEntityList;
                    }
                    vm.tempDetailEntityList= r.detailEntityList;
                }
            } else {
                alert(r.msg);
            }
        }
    });
}



var vm = new Vue({
    el: '#transferOut',//对应模块的名称
    data: {
        detailEntityList: [],
        tempDetailEntityList: [],
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
        //下面是对应模态框隐藏的属性
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false
    },
    methods: {
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
            $("#CommonDetailCheck").css('display', 'none');
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
function dateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}