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

var currentQueryParam = {
    userId: null,
    invoiceNo: null,
    invoiceDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
    invoiceDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
    gfName: "-1",
    shName: "-1",
    invoiceType: "-1"
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        img_src:'',
        exportCondition:true,
        tableData: [],
        multipleSelection: [],
        options: [],
        pageCount: 0,
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 1,
        total: 0,
        summationTotalAmount: '0.00',
        summationTaxAmount: '0.00',
        listLoading: false,
        detailEntityList: [],
        tempDetailEntityList: [],
        dialogVisibleEdit: false,
        editDialogLoading: false,
        invoiceDateOptions: {},
        dataEditForm: {},
        tempValue: null,
        flowType:[],
        gfsh: [{
            value: "-1",
            label: "全部"
        }],
        q: {
            key: '',
            userId: null,
            invoiceNo: null,
            invoiceDate1: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            invoiceDate2: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            gfName: "-1",
            shName: "-1",
            invoiceType: "-1",
            jvCode:null,
            companyCode:null,
            venderid:null,
            scanId:null,
            qsStatus:"-1",
            flowType:"-1"
        },
        qsStartDateOptions:{},
        qsEndDateOptions:{}
    },
    mounted: function () {
        this.queryGf();
        this.queryYWLX();
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
        this.invoiceDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };

    },
    watch: {
        'q.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'q.scanId': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9a-zA-Z_]{0,55}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.scanId = oldValue;
                    })
                }
            },
            deep: true
        },
        'q.venderid': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9a-zA-Z]{0,55}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.venderid = oldValue;
                    })
                }
            },
            deep: true
        },
        'q.companyCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9a-zA-Z]{0,55}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.companyCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        checkNullFormat:function (row, column, index) {
            if(index==null||index==''){
                return "—— ——" ;
            }
            return index;
        },
        query: function () {
            isInitial = false;
            this.findAll(1)


            // vm.dialogVisibleEdit = true;
        }, focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },invoiceDate3Change: function (val) {
            vm.dataform.invoiceDate = new Date(val);
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
        beforeCloseEditWin: function () {
            vm.dialogVisibleEdit = false;
            vm.$refs["dataEditForm"].resetFields();
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
                        "flowType":vm.dataEditForm.flowType,
                        "venderid":vm.dataEditForm.venderid
                        ,
                        "isExistStamper":vm.dataEditForm.isExistStamper,
                        "noExistStamperNotes":vm.dataEditForm.noExistStamperNotes
                    };

                        $.ajax({
                            type: "POST",
                            url: baseURL + "inquery/checkInvoice",
                            contentType: "application/json",
                            data: JSON.stringify(params),
                            success: function (r) {
                                if(r=="0"){
                                    alert("修改成功");
                                }else if(r=="5001"){
                                    alert("修改失败,该发票已存在");
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
        findAll: function (currentPage) {
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.q.invoiceDate1);
            var qsEndDate = new Date(vm.q.invoiceDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            var flag = false;
            if ( (qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return ;
            } else if(qsEndDate.getTime() < new Date(vm.q.invoiceDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }
            currentQueryParam = {
                page: currentPage,
                limit: this.pageSize,
                invoiceNo: this.q.invoiceNo,
                invoiceDate1: this.q.invoiceDate1,
                invoiceDate2: this.q.invoiceDate2,
                shName: vm.q.gfName,
                invoiceType: this.q.invoiceType,
                jvCode:this.q.jvCode,
                companyCode:this.q.companyCode,
                venderid:this.q.venderid,
                scanId:this.q.scanId,
                qsStatus:this.q.qsStatus,
                flowType:this.q.flowType
            };
            vm.listLoading=true;
            $.ajax({
                url:baseURL + 'inquery/PageList',
                type:"POST",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(currentQueryParam),
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        vm.total = r.page.totalCount;
                        vm.currentPage = r.page.currPage;
                        vm.pageCount = r.page.totalPage;
                        vm.tableData = r.page.list;
                        vm.listLoading = false;
                        if(r.page.list.length>0){
                            vm.exportCondition=false;
                        }else{
                            vm.exportCondition=true;
                        }
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }
                    var intervelId = setInterval(function () {
                        if (flag) {
                            hh=$(document).height();
                            $("body",parent.document).find("#myiframe").css('height',hh+'px');
                            clearInterval(intervelId);
                            return;
                        }
                    },50);
                }
            });
           /* $.post(baseURL + 'inquery/PageList', currentQueryParam, function (r) {
                vm.total = r.page.totalCount;
                vm.currentPage = r.page.currPage;
                vm.pageCount = r.page.totalPage;
                vm.tableData = r.page.list;
                vm.listLoading = false;
                if(r.page.list.length>0){
                    vm.exportCondition=false;
                }else{
                    vm.exportCondition=true;
                }
            });*/
        }, currentChange: function (currentPage) {
            if(vm.total==0){
                return;
            }
            vm.findAll(currentPage);
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.findAll(1);
            }
        }, queryGf: function () {

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
                vm.gfsh = gfs;
            });

        }, queryYWLX: function () {
            var data={
                billtypename:'',
                billtypecode:''
            }

        $.post(baseURL + 'base/billtype/listNoPage',data ,function (r) {
            var gfs = [];
            if(r.code==0){
            for (var i = 0; i < r.page.length; i++) {

                var gf = {};
                gf.value = r.page[i].billtypecode + "";
                gf.label = r.page[i].billtypename;
                if (gf.value != '2') {

                gfs.push(gf);
                }
            }
            vm.flowType = gfs;
            }else{
                console.log("获取业务类型失败");
            }
        });
    },
        changeFun: function (row) {
            this.multipleSelection = row;
        },
        invoiceDate1Change: function (val) {
            vm.q.invoiceDate1 = val;
        },

        invoiceDate2Change: function (val) {
            vm.q.invoiceDate2 = val;
        }, toExceil: function () {
            currentQueryParam = {
                page: this.currentPage,
                limit: this.pageSize,
                invoiceNo: this.q.invoiceNo,
                invoiceDate1: this.q.invoiceDate1,
                invoiceDate2: this.q.invoiceDate2,
                shName: vm.q.gfName,
                invoiceType: this.q.invoiceType,
                jvCode:this.q.jvCode,
                companyCode:this.q.companyCode==null?'':this.q.companyCode,
                venderid:this.q.venderid==null?'':this.q.venderid,
                scanId:this.q.scanId==null?'':this.q.scanId,
                qsStatus:this.q.qsStatus==null?'':this.q.qsStatus,
                flowType:this.q.flowType
            };


            document.getElementById("ifile").src =encodeURI( baseURL + 'export/inqueryDataExport'
                + '?shName=' + (currentQueryParam.gfName == null ? '' : currentQueryParam.gfName)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + currentQueryParam.invoiceDate1
                + '&invoiceDate2=' + currentQueryParam.invoiceDate2
                + '&invoiceType=' + currentQueryParam.invoiceType
                + '&jvCode=' + currentQueryParam.jvCode
                + '&companyCode=' + currentQueryParam.companyCode
                + '&venderid=' + currentQueryParam.venderid
                + '&scanId=' + currentQueryParam.scanId
                + '&qsStatus=' + currentQueryParam.qsStatus
                +'&flowType=' + currentQueryParam.flowType
            );
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

        qsTypeFormatter: function (row) {
            var qsType = row.qsType;
            if (qsType == null || qsType == undefined || qsType == "") {
                return "— —"
            } else if (qsType == "0") {
                return "扫码签收";
            } else if (qsType == "1") {
                return "扫描仪签收";
            } else if (qsType == "2") {
                return "app签收";
            } else if (qsType == "3") {
                return "导入签收";
            } else if (qsType == "4") {
                return "手工签收";
            } else if (qsType == "5") {
                return "pdf上传签收";
            }
        },
        formatCreateDate: function (row, column) {
            if (row.createDate != null) {
                return formaterDate(row.createDate);
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
        formatFlowType: function (row, column){
            var laber="";
            vm.flowType.forEach(function(items, j){
                if(items.value+""==row.flowType+""){
                    laber=items.label;
                    return ;
                }
            });
            return laber;
            // if(row.flowType==1){
            //     return "商品"
            // }else if(row.flowType==2){
            //     return "费用"
            // }else if(row.flowType==3){
            //     return "外红"
            // }else if(row.flowType==4){
            //     return "内红"
            // }else if(row.flowType==5){
            //     return "供应商红票"
            // }else if(row.flowType==6){
            //     return "租赁"
            // }
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

        imgRow:function(curObj){
            console.log(curObj);
            var scanId = curObj;
            var url=baseURL+'rest/invoice/sign/getImg?scanId='+scanId;
//            var img = "<a href='" + url + "' target='_blank'><img src='" + url + "' style='width:100%;height:auto;'/></a>";
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

        deleteData: function (row) {
            vm.openConfirm(vm, "\n" + "确定要退票选中的记录？", function () {
                var params = {
                    "invoiceCode": row.invoiceCode,
                    "invoiceNo": row.invoiceNo,
                    "id":row.id,
                    "qsStatus":row.qsStatus,
                    "notes":row.notes,
                    "scanMatchStatus":row.scanMatchStatus,
                    "scanFailReason":row.scanFailReason
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + "SignatureProcessing/deleteRevoice",
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
        },returnData: function (row){
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
                        flowType:row.flowType+"",
                        venderid:row.venderid,
                        venderidEdit:row.venderidEdit
                        //,
                        //isExistStamper:row.isExistStamper==1?1+"":0+"",
               // noExistStamperNotes:row.noExistStamperNotes
                    };


            // $("#imgEdit").attr("src", baseURL+'rest/invoice/sign/getImg?scanId='+row.scanId);
            // $("#imgEdit2").attr("src", baseURL+'rest/invoice/sign/getImg?scanId='+row.scanId);
            vm.img_src=baseURL+'rest/invoice/sign/getImg?scanId='+row.scanId;
        },
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time);
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

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
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

