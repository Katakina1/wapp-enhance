


var currentQueryParam ={
    //税金浮动
    floatTax:null,
    //最大税金
    maxTax:null,
    //优选筛选
    priorityFiltrate:"-1",
    gfName: null,
    gfNames:[],
    xfName: null,
    invoiceType: "-1",
    invoiceDate1: new Date(new Date().getFullYear()+"-"+(new Date().getMonth())+"-1"),
    invoiceDate2: new Date(),
    invoiceNo: null,
    qsStatus: "-1",
    qsType: "-1",
    venderid:'',
    qsDate1: null,
    qsDate2: null ,
    companyCode:"",
    gfTaxNo:"-1"
};
var ids;
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
	el:'#rrapp',
    data:{
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        tableData: [],
        options: [],
        pageCount: 0,
        currentPage: 1,
        pagerCount: 1,
        pageSize: 500, //每次查询500条
        totalPage:1,
        total: 0,
        listLoading: false,
        form:{


            floatTax:"",
            invoiceDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate()),
            invoiceType: "-1",
            xfName: null,
            venderid:'',
            priorityFiltrate:"-1",
            companyCode:"",
            gfTaxNo:"-1",
            maxTax:""
            /*gfName: null,
            gfNames:'',
            invoiceNo: null,
            qsStatus: "-1",
            qsType: "-1",
            qsDate1: null,
            qsDate2: null ,
            maxTax:""*/

        },
        qsStartDateOptions: {},
        qsEndDateOptions: {},
        invoiceStartDateOptions: {},
        invoiceEndDateOptions: {},
        showList: true ,
        submitWin:false,
        invoiceCount:"",
        invoiceAmount:"",
        taxAmount:""
    },
    mounted: function () {
        this.querySearchGf();
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength","50");
        this.queryGf();
        this.qsStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.qsEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.invoiceStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.invoiceEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch:{
        'form.floatTax':{
            handler: function (val,oldValue) {
                var _this = this;
                var reg=/^[0-9]{0,}\.{0,1}[0-9]{0,2}$/;
                if(!(reg.test(val)||val==null)){
                    Vue.nextTick(function() {
                        _this.form.floatTax = oldValue;
                    })
                }
            },
            deep:true
        },
        'form.maxTax':{
            handler: function (val,oldValue) {
                var _this = this;
                var reg=/^[0-9]{0,}\.{0,1}[0-9]{0,2}$/;
                if(!(reg.test(val)||val==null)){
                    Vue.nextTick(function() {
                        _this.form.maxTax = oldValue;
                    })
                }
            },
            deep:true
        },
        'form.venderid': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]*$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.venderid = oldValue;
                    })
                }
            },
            deep: true
        },
        'form.companyCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[A-Za-z0-9]*$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.companyCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
	methods: {
        querySearchGf: function () {
            $.get(baseURL + 'report/invoiceProcessingStatusReport/searchGf',function(r){
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].value+"("+r.optionList[i].label+")";
                    gfs.push(gf);
                }
                vm.gfs = gfs;
            });
        },
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==3){
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '';
            }
        },
        qsChange: function (value) {
            if(value=="1"){
                $('.qsItem').removeClass("hideItem");
                $('.default').addClass("hideItem");
                vm.form.qsDate1 = new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01";
                vm.form.qsDate2 = new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());
            }else{
                $('.qsItem').addClass("hideItem");
                $('.default').removeClass("hideItem");

                vm.form.qsType = "-1";
                vm.form.qsDate1 = null;
                vm.form.qsDate2 = null;
            }
        },
        submitForm: function () {
            vm.reload();
            this.submitWin = false;
        },
        closeWin: function() {
            this.submitWin = false;
        },
        resetQuery: function () {
            $('.default').removeClass("hideItem");
            $('.qsItem').addClass("hideItem");
          vm.form.gfTaxNo='-1';
          vm.form.xfName=null;
          vm.form.invoiceNo='-1';
          vm.form.invoiceDate1=new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01";
          vm.form.invoiceDate2=new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());
          vm.form.invoiceType="-1";
          vm.form.qsStatus="-1";
          vm.form.qsType="-1";
          vm.form.qsDate1=null;
          vm.form.qsDate2=null;
          vm.form.companyCode=null;
          vm.form.floatTax=null;
          vm.form.maxTax=null;
          vm.form.venderid=null;
          vm.form.priorityFiltrate="-1";

        },
        
        check: function () {

            this.$refs['form'].validate(function (valid) {
                if (!valid) {
                   return false
                }
            })
            var checkGfName=true;
            var checkMaxTax=true;
            var checkFloatTax=true;
            var checkInvoiceDate=true;
            var checkSwitchStatus=true;
		    //var maxTax=vm.form.maxTax;
		   // var floatTax=vm.form.floatTax;7
		    var gfName=vm.form.gfTaxNo;
            $(".checkMsg").remove();
		    /*if(gfName==null||gfName==""||gfName==undefined||
                maxTax==null||maxTax==""||maxTax==undefined||
                floatTax==null||floatTax==""||floatTax==undefined){
                $("#requireMsg1 .el-form-item__content").append('<div class="checkMsg">购方名称不可为空</div>');
                $("#requireMsg2 .el-form-item__content").append('<div class="checkMsg">税额上限不可为空</div>');
                $("#requireMsg3 .el-form-item__content").append('<div class="checkMsg">税额容差不可为空</div>');
                return alert("必填项不可为空！");
            }*/
            if(gfName==null||gfName==""||gfName==undefined){
                $("#requireMsg1 .el-form-item__content").append('<div class="checkMsg">购方名称不可为空</div>')
                checkGfName=false;
            }
           /*if(maxTax==null||maxTax==""||maxTax==undefined){

                $("#requireMsg2 .el-form-item__content").append('<div class="checkMsg">税额上限不可为空</div>')
                checkMaxTax=false;
            }

            if(floatTax==null||floatTax==""||floatTax==undefined){
                $("#requireMsg3 .el-form-item__content").append('<div class="checkMsg">税额容差不可为空</div>')
                checkFloatTax=false;
            }*/
           if(vm.form.floatTax!=null&&vm.form.floatTax!=''&&vm.form.floatTax!=undefined){

               if(vm.form.maxTax==null||vm.form.maxTax==""||vm.form.maxTax==undefined){
                 alert("容差值有值，税额上限不可为空!");
                 return;
               }
           }

            var invoiceStartDate = new Date(vm.form.invoiceDate1);
            var invoiceEndDate = new Date(vm.form.invoiceDate2);
            invoiceStartDate.setMonth(invoiceStartDate.getMonth() + 12);
          /*  var qsStartDate = new Date(vm.form.qsDate1);
            var qsEndDate = new Date(vm.form.qsDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);*/
            if ( (invoiceEndDate.getTime() + 1000*60*60*24) > invoiceStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkInvoiceDate=false;
            }else if(invoiceEndDate.getTime() < new Date(vm.form.invoiceDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkInvoiceDate=false;
            }
           /* if ((qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkInvoiceDate=false;
            }else if(qsEndDate.getTime() < new Date(vm.form.qsDate1)){
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkInvoiceDate=false;
            }*/
            if(!(checkGfName && checkMaxTax && checkFloatTax && checkInvoiceDate)){
                return;
            }
            currentQueryParam={
                page: this.currentPage,
                limit: this.pageSize,
                gfName: vm.form.gfTaxNo,
                xfName: vm.form.xfName,
                // invoiceNo: vm.form.invoiceNo,
                companyCode: vm.form.companyCode,
                invoiceDate1: vm.form.invoiceDate1,
                invoiceDate2: vm.form.invoiceDate2,
                invoiceType: vm.form.invoiceType,
                venderid: vm.form.venderid,
                /* qsStatus: vm.form.qsStatus,
                 qsType: vm.form.qsType,
                 qsDate1: vm.form.qsDate1,
                 qsDate2: vm.form.qsDate2,*/
                floatTax: vm.form.floatTax,
                maxTax: vm.form.maxTax,
                priorityFiltrate: vm.form.priorityFiltrate
            };
            //查询开关是否开启。
            $.get(baseURL + 'certification/intelligentizeCheck/selectSwitchStatus',function(r){
                if(r.code=="0" && r.switchStatus!='0'){
                    checkSwitchStatus =false;
                    alert("控制开关处于关闭状态！");
                }else{
                    checkSwitchStatus=true;
                    $.get(baseURL + 'certification/intelligentizeCheck/select',currentQueryParam,function(r){
                        if(r.code=="0"&&r.entity!=null&&r.entity.count!=null){
                            /*confirm('勾选发票数量：'+r.entity.count+'张，不含税金额合计：'+r.entity.invoiceAmount+'元，发票税额合计：('+r.entity.taxAmount+')元，是否确定勾选？', function(){
                                ids=r.entity.ids.join(",");
                                vm.reload();
                                alert("操作成功");
                            });*/
                            ids=r.entity.ids.join(",");
                            vm.invoiceCount=r.entity.count;
                            vm.invoiceAmount=r.entity.invoiceAmount;
                            vm.taxAmount=r.entity.taxAmount;
                            vm.submitWin = true;
                        }else{
                            alert("未查到符合条件的发票");
                        }
                    })
                }
            });
        },
        queryGf:function(){
            $.ajax({
                type: "GET",
                url: baseURL + 'transferOut/detailQuery/gfNameAndTaxNo',
                contentType: "application/json",
                data: {},
                success: function(r){
                    if(r.code==0){
                        for(var i=0;i<r.gfNameList.length;i++){
                        vm.form.gfNames.push({name:r.gfNameList[i],taxNo:r.gfTaxNoList[i]});
                    }
                    }
                }
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
		reload: function () {
            currentQueryParam={
                'gfName': vm.form.gfTaxNo,
                'xfName': vm.form.xfName,
                'companyCode': vm.form.companyCode,
                //'invoiceNo': vm.form.invoiceNo,
                'invoiceDate1': vm.form.invoiceDate1,
                'invoiceDate2': vm.form.invoiceDate2,
                'invoiceType': vm.form.invoiceType,
                //'qsStatus': vm.form.qsStatus,
                //'qsType': vm.form.qsType,
                //'qsDate1': vm.form.qsDate1,
                'floatTax': vm.form.floatTax,
                'venderid': vm.form.venderid,
                'maxTax': vm.form.maxTax,
                'priorityFiltrate': vm.form.priorityFiltrate,
                'ids':ids
            };

            $.post(baseURL + 'certification/intelligentizeCheck/list', currentQueryParam, function (r) {
                vm.tableData = r.list;
                vm.listLoading = false;
            });
		},
        invoiceDate1Change: function(val) {
            vm.form.invoiceDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form.invoiceDate2 = val;
        },
        qsDate1Change: function(val) {
            vm.form.qsDate1 = val;
        },
        qsDate2Change: function(val) {
            vm.form.qsDate2 = val;
        },
        invoiceAmountFormatDecimal:function(row) {

            return decimal(row.invoiceAmount);
        } ,
        taxAmountFormatDecimal:function(row) {

            return decimal(row.taxAmount);
        } ,
        
        
        
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
function verificationXfName(t) {
    var reg=vm.form.xfName;
    if(reg!=null &&reg.length>30){
        reg=reg.substring(0,30);
    }
    vm.form.xfName=reg;

}
