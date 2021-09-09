
var isInitial = true;
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
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
    invoiceCode:null,
    invoiceNo: null,
    invoiceDate1: null,
    invoiceDate2: null,
    createDate1: null,
    createDate2: null,
    kpDate1:null,
    kpDate2: null,
    gfName: "-1",
    shName: "-1",
    invoiceType: "-1",
    qsStatus:"-1",
    userAccount:null

};
var edit= new Vue({
    el:'#editScan',
    data:{
        recordInvoice:{},
        edit:{
            invoiceNo:null,
            invoiceCode:null,
            invoiceDate:null,
            gfTaxNo:null,
            xfTaxNo:null,
            invoiceAmount:null,
            taxAmount:null,
            totalAmount:null,
            checkCode:null
        }
    }
})
var vm = new Vue({
    el: '#rrapp',
    data: {
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
        tempValue: null,
        gfsh: [{
            value: "-1",
            label: "全部"
        }],
        q: {
            key: '',
            userId: null,
            invoiceCode:null,
            invoiceNo: null,
            invoiceDate1: null,
            invoiceDate2: null,
            createDate1: null,
            createDate2: null,
            kpDate1:null,
            kpDate2: null,
            gfName: "-1",
            shName: "-1",
            invoiceType: "-1",
            qsStatus:"-1",
            userAccount:parent.vm.user.username
        },
        qsStartDateOptions:{},
        qsEndDateOptions:{},
        createStartDateOptions:{},
        createEndDateOptions:{},
        kpStartDateOptions:{},
        kpEndDateOptions:{}
    },
    mounted: function () {
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
        this.createStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.createEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.kpStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.kpEndDateOptions = {
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
        },'q.invoiceCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,12}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.q.invoiceCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        oneKey:function(){
            if (this.multipleSelection.length == 0) {
                alert("请勾选需要一键同步的发票!");
                return;
            }
            var batchDelValueArray = [];
            $.each(this.multipleSelection, function (index, curDom) {
                batchDelValueArray.push(curDom.id);
            })
            layer.confirm("您选择了" + batchDelValueArray.length + "条数据，确定要同步吗?", {
                btn: ['确定','取消'] //按钮
            }, function(index){
                $.ajax({
                    url: "inqueryMRXY/oneKey",   // 提交的页面
                    data: JSON.stringify(batchDelValueArray),
                    type: "POST",
                    contentType: "application/json;charset=UTF-8",
                    success: function (data) {

                        layer.confirm(data.result, {
                            btn: ['确定','取消'] //按钮
                        }, function(index){
                            if (data.flg) {
                                vm.findAll(1);
                            }
                            layer.close(index);
                        }, function(index){
                            layer.close(index);
                        });



                        if(confirm(data.result)){
                            if (data.flg) {
                                vm.findAll(1);
                                // window.location.reload();
                            }
                        }
                    }
                });
                layer.close(index);
            }, function(index){
                layer.close(index);
            });


        },
         modifiedRow:function(rowData,curObj,scanId,type,no){
            // $(t).parent().click();
            // var gr= getSelectedRow();
            var rowId = curObj;
            var btn=["保存"];
            var row=rowData;
            edit.edit.invoiceNo=no;
            edit.edit.invoiceCode=rowData.invoiceCode;


             var str1=rowData.invoiceDate;
             var invoiceDate=new Date(Date.parse(str1.replace(/-/g, "/")));
             edit.edit.invoiceDate=invoiceDate.getFullYear().toString()+ ("0" + (invoiceDate.getMonth() + 1)).slice(-2) + ("0" + invoiceDate.getDate()).slice(-2);
            edit.edit.gfTaxNo=rowData.gfTaxNo;
            edit.edit.xfTaxNo=rowData.xfTaxNo;
            edit.edit.invoiceAmount=rowData.invoiceAmount;
            edit.edit.taxAmount=rowData.taxAmount;
            edit.edit.checkCode=rowData.checkCode;
            edit.edit.totalAmount=parseFloat(rowData.invoiceAmount)+parseFloat(rowData.taxAmount);
            if(type=='01'){


                //获取底账数据
                $.ajax({
                    type: 'post',
                    url: baseURL+'rest/invoice/sign/getRecordInvoice',
                    data: {invoiceNo:edit.edit.invoiceNo,invoiceCode:edit.edit.invoiceCode},
                    dataType: 'json',
                    contentType: 'application/x-www-form-urlencoded; charset=utf-8',
                    success: function (data) {
                        edit.recordInvoice=data.msg;
                        if(edit.recordInvoice.invoiceDate!=null){
                            var str1=edit.recordInvoice.invoiceDate;
                            var invoiceDate=new Date(Date.parse(str1.replace(/-/g, "/")));
                            edit.recordInvoice.invoiceDate=invoiceDate.getFullYear().toString()+ ("0" + (invoiceDate.getMonth() + 1)).slice(-2) + ("0" + invoiceDate.getDate()).slice(-2);

                        }
                        edit.recordInvoice.totalAmount=edit.recordInvoice.totalAmount;

                    },error:function(XMLHttpRequest, textStatus, errorThrown){
                        edit.recordInvoice={};
                    }
                });

                btn.push("一键同步");
                $('#editCheckCodeDiv').hide();
            }else{
                $('#editCheckCodeDiv').show();
                edit.recordInvoice={};
            }
            $('#editImg').attr("src",baseURL+'rest/invoice/sign/getImg?scanId='+scanId)
            layer.open({
                type: 1,
                // skin: 'layui-layer-molv',
                title: "修改",
                area: ['12.0rem', '5.7rem'],
                shadeClose: true,
                skin: 'demo-class2',
                btn: btn,
                content: $("#editScan"),
                btn1: function (index) {



                                var invoiceType = getFplx(edit.edit.invoiceCode);
                                var params = {
                                    "id": edit.edit.id,
                                    "invoiceCode": edit.edit.invoiceCode,
                                    "invoiceNo": edit.edit.invoiceNo,
                                    "gfTaxNo": edit.edit.gfTaxNo,
                                    "checkCode": edit.edit.checkCode,
                                    "invoiceAmount": edit.edit.invoiceAmount,
                                    "invoiceDate": edit.edit.invoiceDate,
                                    "invoiceType": invoiceType,
                                    "xfTaxNo": edit.edit.xfTaxNo,
                                    "taxAmount": edit.edit.taxAmount,
                                    "totalAmount": edit.edit.totalAmount,
                                    "uuid": edit.edit.invoiceCode + edit.edit.invoiceNo
                                };
                                if (invoiceType == "01" || invoiceType == "14" || invoiceType == "03") {
                                    $.ajax({
                                        type: "POST",
                                        url: baseURL + "inqueryMRXY/checkInvoice",
                                        contentType: "application/json",
                                        data: JSON.stringify(params),
                                        success: function (r) {
                                            alert(r.msg);

                                            layer.close(index)
                                            vm.findAll(1);
                                        }
                                    });
                                } else if (invoiceType == "04" || invoiceType == "10" || invoiceType == "11") {
                                    $.ajax({
                                        type: "POST",
                                        url: baseURL + "SignatureProcessing/checkPlainInvoice",
                                        contentType: "application/json",
                                        data: JSON.stringify(params),
                                        success: function (r) {
                                            if (r.code == 1) {
                                                alert(r.msg);
                                            }
                                            layer.close(index)
                                            vm.findAll(1);
                                        }
                                    });
                                } else {
                                    alert("更改的数据有误！");
                                }



                },
                btn2:function(){
                    edit.edit.invoiceDate=edit.recordInvoice.invoiceDate;
                    edit.edit.gfTaxNo=edit.recordInvoice.gfTaxNo;
                    edit.edit.xfTaxNo=edit.recordInvoice.xfTaxNo;
                    edit.edit.invoiceAmount=edit.recordInvoice.invoiceAmount;
                    edit.edit.taxAmount=edit.recordInvoice.taxAmount;
                    edit.edit.totalAmount=edit.recordInvoice.totalAmount;
                    return false;
                }

            });
        },

         imgRow:function(curObj){
            var scanId = curObj;
            var url=baseURL+'rest/invoice/sign/getImg?scanId='+scanId;
            var img = "<img src='" + url + "' style='width:100%;height:100%;'/>";
            parent.layer.open({
                type: 1,
                shade: false,
                title: false, //不显示标题
                area: ['9.22rem', '5.4rem'],
                content: img, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
                cancel: function () {
                    //layer.msg('图片查看结束！', { time: 5000, icon: 6 });
                }
                ///content:[baseURL+'rest/invoice/sign/getImg?scanId='+scanId]
            })

        },
        deleteData: function (row) {
            vm.openConfirm(vm, "\n" + "确定要删除选中的记录？", function () {
                var params = {
                    "invoiceCode": row.invoiceCode,
                    "invoiceNo": row.invoiceNo
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
        },
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
        }, focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==2){
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
            if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
            if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
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
            if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==4){
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
            if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if(val==6){
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        findAll: function (currentPage) {
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.q.invoiceDate1);
            var qsEndDate = new Date(vm.q.invoiceDate2);
            var createStartDate = new Date(vm.q.createDate1);
            var createEndDate = new Date(vm.q.createDate2);
            var kpStartDate = new Date(vm.q.kpDate1);
            var kpEndDate = new Date(vm.q.kpDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);
            createStartDate.setMonth(createStartDate.getMonth() + 12);
            kpStartDate.setMonth(kpStartDate.getMonth() + 12);
            var flag = false;
            if ( (qsEndDate.getTime() + 1000*60*60*24) > qsStartDate.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return ;
            } else if(qsEndDate.getTime() < new Date(vm.q.invoiceDate1)){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }
            if ( (createEndDate.getTime() + 1000*60*60*24) > createStartDate.getTime()) {
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return ;
            } else if(createEndDate.getTime() < new Date(vm.q.createDate1)){
                $("#requireMsg5 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }
            if ( (kpEndDate.getTime() + 1000*60*60*24) > kpStartDate.getTime()) {
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return ;
            } else if(kpEndDate.getTime() < new Date(vm.q.kpDate1)){
                $("#requireMsg6 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }
            currentQueryParam = {
                page: currentPage,
                limit: this.pageSize,
                invoiceNo: this.q.invoiceNo,
                invoiceCode: this.q.invoiceCode,
                invoiceDate1: this.q.invoiceDate1,
                invoiceDate2: this.q.invoiceDate2,
                createDate1: this.q.createDate1,
                createDate2: this.q.createDate2,
                kpDate1: this.q.kpDate1,
                kpDate2: this.q.kpDate2,
                shName: vm.q.gfName,
                invoiceType: this.q.invoiceType,
                qsStatus:this.q.qsStatus,
                userAccount:this.q.userAccount
            };
            vm.listLoading=true;
            $.ajax({
                url:baseURL + 'inqueryMRXY/PageList',
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
                        $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                            formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

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
           /* $.post(baseURL + 'inqueryMRXY/PageList', currentQueryParam, function (r) {
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
            $.ajax({
                url:baseURL + 'inqueryMRXY/queryGf',
                type:"POST",
                contentType: "application/json",
                dataType: "json",
                success:function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        gfs.push({
                            value: "-1",
                            label: "全部"
                        });
                        for (var i = 0; i < r.optionList.length; i++) {
                            var gf = {};
                            gf.value = r.optionList[i].value;
                            gf.label = r.optionList[i].label;
                            gfs.push(gf);
                        }
                        vm.gfsh = gfs;
                    }
                }
            });
           /* $.get(baseURL + 'inqueryMRXY/queryGf', function (r) {
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for (var i = 0; i < r.optionList.length; i++) {
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.gfsh = gfs;
            });*/
        }, changeFun: function (row) {
            this.multipleSelection = row;
        },checkSelectable:function(row) {
            return row.qsStatus != "1";
        },
        invoiceDate1Change: function (val) {
            vm.q.invoiceDate1 = val;
        },
        invoiceDate2Change: function (val) {
            vm.q.invoiceDate2 = val;
        },
        createDate1Change: function (val) {
            vm.q.createDate1 = val;
        },
        createDate2Change: function (val) {
            vm.q.createDate2 = val;
        },kpDate1Change: function (val) {
            vm.q.kpDate1 = val;
        },
        kpDate2Change: function (val) {
            vm.q.kpDate2 = val;
        }, toExceil: function () {
            window.open(baseURL + 'export/inqueryDataExportMRXY'
                + '?shName=' + (currentQueryParam.gfName == null ? '' : currentQueryParam.gfName)
                + '&invoiceCode=' + (currentQueryParam.invoiceCode == null ? '' : currentQueryParam.invoiceCode)
                + '&invoiceNo=' + (currentQueryParam.invoiceNo == null ? '' : currentQueryParam.invoiceNo)
                + '&invoiceDate1=' + (currentQueryParam.invoiceDate1 == null ? '' : currentQueryParam.invoiceDate1)
                + '&invoiceDate2=' + (currentQueryParam.invoiceDate2 == null ? '' : currentQueryParam.invoiceDate2)
                + '&createDate1=' + (currentQueryParam.createDate1 == null ? '' : currentQueryParam.createDate1)
                + '&createDate2=' + (currentQueryParam.createDate2 == null ? '' : currentQueryParam.createDate2)
                + '&kpDate1=' + (currentQueryParam.kpDate1 == null ? '' : currentQueryParam.kpDate1)
                + '&kpDate2=' + (currentQueryParam.kpDate2 == null ? '' : currentQueryParam.kpDate2)
                + '&invoiceType=' + (currentQueryParam.invoiceType == null ? '' : currentQueryParam.invoiceType)
                + '&qsStatus=' + (currentQueryParam.qsStatus == null ? '' : currentQueryParam.qsStatus)
                + '&userAccount=' + (currentQueryParam.userAccount == null ? '' : currentQueryParam.userAccount));
        },
        invoiceAmountFormatDecimal: function (row, column, index) {
            return decimal(row.invoiceAmount);
        }, taxAmountFormatDecimal: function (row, column, index) {
            return decimal(row.taxAmount);
        }, totalAmountFormatDecimal: function (row, column, index) {
            return decimal(row.totalAmount);
        }, qsStatusFormatter: function (row) {
            var val = row.qsStatus;
            if (val == "0") {
                return "签收失败";
            } else if (val == "1") {
                return "签收成功";
            }
        }, qsTypeFormatter: function (row) {
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
        formatCreateDate: function (row) {
            if (row.createDate != null) {
                return formaterDate(row.createDate);
            } else {
                return '';
            }
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

function formatMoney(value){
    return Vue.prototype.numberFormat2(null,null,value);
}

