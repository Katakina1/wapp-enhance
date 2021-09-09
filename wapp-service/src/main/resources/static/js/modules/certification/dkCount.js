
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    data: {
        tableData: [],
        detailTable: [],
        multipleSelection: [],
        options: [],
        pageCount: 1,
        currentPage: 1,
        deptList:{},
        deptShow:false,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        gfTaxNos:[],
        listLoading: false,
        taxPayerName:null,
        taxPayerNo:null,
        belongMonth:null,
        tjDate: null,
        updateWin:false,
        form:{
            deptId:'-1',
            gfNames: [],
            gfName: null,
            gfTaxNo: "-1",
            skssq: ''+new Date().getFullYear()+format2(new Date().getMonth()+1),
            tjStatus: "-1",
            qsStatus: "-1",
            dkWhetherPassword:"0"
        },
        limitDate: {},
        passwordWin:false,
        applyCountParam:{},
        passwordForm: {
            dkPassword: ""
        },
    },
    mounted: function () {
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength","50");
        this.queryGf();
        this.limitDate = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();

            }
        };
    },
    methods: {
     dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        tjDateFormat: function(row){
            var tjDate = row.tjDate;
            if (tjDate != null && tjDate != "") {
                return tjDate.substring(0,4)+"-"+tjDate.substring(4,6)+"-"+tjDate.substring(6,8);
            }else{
                return '—— ——';
            }
        },
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        dksehjFormatDecimal:function(row) {
            var dksehj = row.dksehj;
            if (dksehj != null && dksehj != "") {
                return decimal(row.dksehj);
            }else {
                return "—— ——";
            }
        } ,
        dkjeFormatDecimal:function(row) {
            var dkje = row.dkAmountCount;
            if (dkje != null && dkje != "") {
                return decimal(dkje);
            }else {
                return "—— ——";
            }
        } ,
        dkseFormatDecimal:function(row) {
            var dkse = row.dkTaxAmountCount;
            if (dkse != null && dkse != "") {
                return decimal(dkse);
            }else {
                return "—— ——";
            }
        } ,
        bdkseFormatDecimal:function(row) {
            var bdkse = row.bdkTaxAmountCount;
            if (bdkse != null && bdkse != "") {
                return decimal(bdkse);
            }else {
                return "—— ——";
            }
        } ,
        bdkjeFormatDecimal:function(row) {
            var bdkje = row.bdkAmountCount;
            if (bdkje != null && bdkje != "") {
                return decimal(bdkje);
            }else {
                return "—— ——";
            }
        } ,
        dkNumFormat:function(row){
            return parseInt(row.dkInvoiceCount);
        },
        bdkNumFormat:function(row){
            return parseInt(row.bdkInvoiceCount);
        },
        query: function () {
         vm.findAll();
        },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        applyCount: function() {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要申请统计的数据!");
                return;
            }
            var gfshs = [];
            var skssqs = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                var sfdqskssq = this.multipleSelection[i].sfdqskssq;
                var tjStatus = this.multipleSelection[i].tjStatus;
                if (sfdqskssq!='1' || (tjStatus!='0' && tjStatus!='3')){
                    alert("所选数据中存在不符合条件的数据，请重新选择");
                    return;
                }
                if (tjStatus=='1'){
                    alert("所选数据中存在统计中数据，请耐心等待");
                    return;
                }
                gfshs.push(this.multipleSelection[i].taxno);
                skssqs.push(this.multipleSelection[i].skssq);
            }
            var gfshArr=gfshs.join(",");
            var skssqArr=skssqs.join(",");
            $("#export_btn1").attr("disabled","true").addClass("is-disabled");
            confirm('确定要申请统计选中的记录？', function(){
                $.ajax({
                    type: "GET",
                    url: baseURL + "certification/deductStatistics/applyCount",
                    contentType: "application/json",
                    data: {gfshs:gfshArr,skssqs:skssqArr},
                    success: function(r){
                        console.log(r);
                        if(r.code!=undefined && r.code!=null && r.code == 500) {
                            alert('撤销统计失败');
                            return;
                        }
                        if(r.code==0){
                            alert(r.msg, function(){
                                vm.findAll();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        confirmCount: function() {
            if (this.multipleSelection.length == 0) {
                alert("请先选择要申请统计的数据!");
                return;
            }
            var dkWhetherPassword =vm.form.dkWhetherPassword;
            if(dkWhetherPassword==1){
                if (this.multipleSelection.length > 1) {
                    alert("需要密码只能选择单条数据!");
                    return;
                }
            }
            var dkPassword="";
            var gfshs = [];
            var skssqs = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                var sfdqskssq = this.multipleSelection[i].sfdqskssq;
                var tjStatus = this.multipleSelection[i].tjStatus;
                var qsStatus = this.multipleSelection[i].qsStatus;
                if (sfdqskssq!='1' || tjStatus!='2' || (qsStatus!='0' && qsStatus!='3')){
                    alert("所选数据中存在不符合条件的数据，请重新选择");
                    return;
                }
                gfshs.push(this.multipleSelection[i].taxno);
                skssqs.push(this.multipleSelection[i].skssq);
                dkWhetherPassword = this.multipleSelection[i].dkWhetherPassword;
                if(dkWhetherPassword==1){
                    dkPassword=this.multipleSelection[i].dkPassword;
                }
            }
            var gfshArr=gfshs.join(",");
            var skssqArr=skssqs.join(",");
            vm.applyCountParam = {
                gfTaxNo: gfshArr,
                skssq: skssqArr
            };

            if(dkWhetherPassword==1){
                if (this.multipleSelection.length > 1) {
                    alert("需要密码只能选择单条数据!");
                    return;
                }
                confirm('确定要对所选税号进行确认统计吗?', function(){
                    $("#export_btn2").attr("disabled","true").addClass("is-disabled");
                    parent.layer.close(parent.layer.index);
                    vm.passwordWin = true;
                    vm.passwordForm.dkPassword=dkPassword;
                });
            }else {

                confirm('确定要确认统计选中的记录？', function () {
                    $("#export_btn2").attr("disabled","true").addClass("is-disabled");
                    vm.listLoading = true;
                    $.ajax({
                        type: "GET",
                        url: baseURL + "certification/deductStatistics/confirmCount",
                        contentType: "application/json",
                        data: {gfshs: gfshArr, skssqs: skssqArr,dkPassword:dkPassword},
                        success: function (r) {
                            console.log(r);
                            if (r.code != undefined && r.code != null && r.code == 500) {
                                alert('确认统计失败');
                                return;
                            }
                            if (r.code == 0) {
                                alert(r.msg, function () {
                                    vm.findAll();
                                });
                            } else {
                                alert(r.msg);
                            }
                        }
                    });
                    vm.listLoading = false;
                });
            }
        },
        confirmApplyCountPassword: function() {
            var dkPassword =vm.passwordForm.dkPassword;
            if(!dkPassword){
                alert('请输入确认密码');
                return;
            }
            var gfshs1 = [];
            var skssqs1 = [];
            for(var i=0; i<this.multipleSelection.length; i++) {
                var sfdqskssq = this.multipleSelection[i].sfdqskssq;
                var tjStatus = this.multipleSelection[i].tjStatus;
                var qsStatus = this.multipleSelection[i].qsStatus;
                if (sfdqskssq!='1' || tjStatus!='2' || (qsStatus!='0' && qsStatus!='3')){
                    alert("所选数据中存在不符合条件的数据，请重新选择");
                    return;
                }
                gfshs1.push(this.multipleSelection[i].taxno);
                skssqs1.push(this.multipleSelection[i].skssq);
            }
            var gfshArr=gfshs1.join(",");
            var skssqArr=skssqs1.join(",");
            $.ajax({
                type: "GET",
                url: baseURL + "certification/deductStatistics/confirmCount",
                contentType: "application/json",
                data:{gfshs: gfshArr, skssqs: skssqArr,dkPassword:dkPassword},
                success: function(r){
                    if(r.code==0){
                        vm.passwordWin = false;
                        alert(r.msg, function(){
                            vm.findAll();
                        });
                    }else{
                        vm.passwordWin = false;
                        alert('确认统计失败!');
                    }
                }
            });
        },
        closePasswordWin: function () {
            vm.passwordWin = false;
        },
        openUpdateWin: function (row) {
         var gfsh = row.taxno;
         var skssq = row.skssq;
         var taxName = row.taxname;
            var that = this;
            $.ajax({
                type: "POST",
                url: baseURL + "certification/deductStatistics/dkCountDetail",
                contentType: "application/x-www-form-urlencoded; charset=utf-8",
                dataType: "json",
                data: {"taxno":gfsh,"skssq":skssq},
                success: function(r){
                    if(r.code==0){
                        that.taxPayerName=taxName.length>12?taxName.substring(0,10)+"...":taxName;
                        that.taxPayerNo = row.taxno;
                        that.belongMonth = r.skssq;
                        vm.tjDate = r.detailList[0].tjDate;
                        vm.detailTable=r.detailList;
                        vm.updateWin = true;
                    }else{
                        alert('获取明细失败');
                    }
                }
            });


        },
        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){
                this.findAll();
            }
        },
        findAll: function () {
            var flag = false;
            var params = {
                page: this.currentPage,
                limit: this.pageSize,
                gfName: this.form.gfTaxNo,
                skssq: this.form.skssq,
                tjStatus: this.form.tjStatus,
                qsStatus: this.form.qsStatus,
                dkWhetherPassword:this.form.dkWhetherPassword
            };
            vm.listLoading = true;


            this.$http.post(baseURL  + 'certification/deductStatistics/list',
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
            $("#export_btn1").removeAttr("disabled").removeClass("is-disabled");
            $("#export_btn2").removeAttr("disabled").removeClass("is-disabled");
            var intervelId = setInterval(function () {
                if (flag) {
                    hh=$(document).height();
                    $("body",parent.document).find("#myiframe").css('height',hh+'px');
                    clearInterval(intervelId);
                    return;
                }
            },50);
        },
        queryGf: function () {
            $.get(baseURL + 'transferOut/detailQuery/gfNameAndTaxNo', function (r) {
                for (var i = 0; i < r.gfNameList.length; i++) {
                    vm.form.gfNames.push({name: r.gfNameList[i], taxNo: r.gfTaxNoList[i]});
                }
                vm.gfTaxNos = r.gfTaxNoList;
            })
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
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
function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}
/**
 * 验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;

    vm.form.invoiceNo = t.value.replace(reg, '');

}

function verificationXfName(t) {
    var reg=vm.form.xfName;
    if(reg!=null &&reg.length>30){
        reg=reg.substring(0,30);
    }
    vm.form.xfName=reg;

}
function verificationGfName(t) {
    var reg=vm.form.gfName;
    console.log(reg);
    if(reg.length>5){
        reg=reg.substring(0,5);
    }
    vm.form.gfName=reg;

}
/**
 * 格式化货币 千分号和四舍五入保留两位小数
 * @param cellvalue
 * @param options
 * @param rowObject
 * @returns {*}
 */
function decimal(cellvalue) {
    if(cellvalue!=null){
        var val=Math.round(cellvalue * 100) / 100;
        return val.formatMoney();
    }
    return "";
}
Number.prototype.formatMoney = function(places, symbol, thousand, decimal) {
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

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}