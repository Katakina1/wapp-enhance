
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: '#rrapp',
    data: {
        listLoading: false,
        listLoading1: false,
        listLoading2: false,
        listLoading3: false,
        listLoading4: false,
        listLoading5: false,
        checkUnPass:false,
        pageCount: 0,
        options: [],
         result:{},
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        forms:{
            usercode:"",
            problemStream:"",
            username:null,
            invoiceNo:"",
            checkStatus:"-1",
            questionDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            questionDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            reason:null
},

        readonly:false,
        questionPaperData:[],
        otherData:[],
        poDiscountData:[],
        questionPaper:false,
        questionPaperFile:false,
        questionPaperFileList:[],
        claimChangeData:[],
        poChangeData:[],
        countChangeData:[],
        startDateOptions:{},
        endDateOptions:{}

    },
    mounted: function () {
        this.startDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.endDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
    },
    watch:{

    },
    methods: {
        query: function () {
            this.findAll(1);
        },
        /**
         * 格式化开票时间
         */
        formatCreateTime: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormats(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        formatCheckTime: function (row, column) {
            if (row.checkDate != null) {
                return dateFormats(row.checkDate);
            } else {
                return '—— ——';
            }
        },
        formatReplyTime: function (row, column) {
            if (row.replyDate != null) {
                return dateFormats(row.replyDate);
            } else {
                return '—— ——';
            }
        },
        formatRejectTime: function (row, column) {
            if (row.rejectDate != null) {
                return dateFormats(row.rejectDate);
            } else {
                return '—— ——';
            }
        },
        dateFormat:function(a,b,c){
            return dateFormatStrToYMD(c);
        },
        moneyFormat:function(a,b,c){
            return moneyFormat(c);
        },

        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll(currentPage);
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){

                this.findAll(1);
            }
        },
        checkUnPassCancel:function () {
            vm.checkUnPass=false;
            vm.listLoading = false;
        },
        findAll: function (currentPage) {

            validateDateTypeA(vm.forms.questionDateStart,vm.forms.questionDateEnd);
            vm.forms.usercode = validateOnlyNumber(vm.forms.usercode,0,'num',"请输入数字格式供应商号",6);
            //vm.forms.invoicdNo = validateOnlyNumber(vm.forms.invoicdNo,0,'num',"请输入数字格式发票号",8);

            var params = {
                page: currentPage,
                limit: this.pageSize,
                venderid:vm.forms.usercode,
                invoiceNo:vm.forms.invoiceNo,
                checkStatus:vm.forms.checkStatus,
                problemStream:vm.forms.problemStream,
                questionDateStart:vm.forms.questionDateStart,
                questionDateEnd:vm.forms.questionDateEnd


            };
            vm.listLoading = true;
            this.$http.post(baseURL + sysUrl.questionPaperQuery,
                params, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.questionPaperData = [];
                flag = true;
                for (var key in response.data.page.list) {
                    this.$set(this.questionPaperData, key, response.data.page.list[key]);
                }
                this.listLoading = false;
            }).catch(function (response) {
                alert(response.data.msg);
                this.listLoading = false;
            });
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);


        },
        chexiao:function (row) {

            result={
                id:row.id,
                result:0,
                reason:''
            }
            parent.layer.open({
                title:'撤销',
                content:'是否撤销审核？',
                shadeClose: true,
                btn: ['是', '否'],
                yes:function (index) {
                    vm.listLoading = true;
                    //通过
                    parent.layer.close(index);
                    $.ajax({
                        url: baseURL + 'modules/posuopei/question/chexiao',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(result),
                        success: function (r) {
                            if (r.code == 0) {
                                alert(r.msg);
                                vm.findAll(1);
                                vm.listLoading = false;
                            }
                        }
                    });
        },btn2:function(index){
                //vm.listLoading = true;
                //不通过
                parent.layer.close(index);
                //vm.checkUnPass=true;

            },cancel:function (index,layero) {
                parent.layer.close(index);
                vm.listLoading = false;
            }
        });
        },
        check:function (row) {

             result={
                id:row.id,
                result:1,
                reason:row.unPassReason
            }
            parent.layer.open({
                title:'审核',
                content:'请选择通过或者不通过？',
                shadeClose: true,
                btn: ['通过', '不通过'],
                yes:function (index) {
                    vm.listLoading = true;
                        //通过
                        parent.layer.close(index);
                        $.ajax({
                            url: baseURL + 'modules/posuopei/question/check',
                            type: "POST",
                            contentType: "application/json",
                            dataType: "json",
                            data: JSON.stringify(result),
                            success: function (r) {
                                if (r.code == 0) {
                                    alert(r.msg);
                                    vm.findAll(1);
                                    vm.listLoading = false;
                                }
                            }
                        });
                },btn2:function(index){
                    vm.listLoading = true;
                    //不通过
                    parent.layer.close(index);
                    vm.checkUnPass=true;

                },cancel:function (index,layero) {
                        parent.layer.close(index);
                        vm.listLoading = false;
                }
            });
        },
        checkQr:function (row) {
            result={
                id:row.id,
                result:4,
                reason:row.unPassReason
            }
            parent.layer.confirm("是否上传BPMS？",{btn: ['是', '否']}, function (index) {
                //通过
                parent.layer.close(index);
                vm.listLoading = true;
                $.ajax({
                    url: baseURL + 'modules/posuopei/question/check',
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(result),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                            vm.findAll(1)
                            vm.listLoading = false;
                        }
                    }
                });


            },function(index){
                //不通过
                parent.layer.close(index);

            });
        },
        updateStatus:function (row) {
            result={
                id:row.id,
            }
            parent.layer.confirm("采购是否同意？",{btn: ['采购已同意', '采购不同意']}, function (index) {
                //通过
                parent.layer.close(index);
                vm.listLoading = true;
                $.ajax({
                    url: baseURL + 'modules/posuopei/question/updateY',
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(result),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                            vm.findAll(1)
                            vm.listLoading = false;
                        }
                    }
                });


            },function(index){
                //不通过
                parent.layer.close(index);
                $.ajax({
                    url: baseURL + 'modules/posuopei/question/updateN',
                    type: "POST",
                    contentType: "application/json",
                    dataType: "json",
                    data: JSON.stringify(result),
                    success: function (r) {
                        if (r.code == 0) {
                            alert(r.msg);
                            vm.findAll(1)
                            vm.listLoading = false;
                        }
                    }
                });
            });
        },
        unPass:function () {
            result.result=2;
            result.reason=vm.forms.reason

            $.ajax({
                url: baseURL + 'modules/posuopei/question/check',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(result),
                success: function (r) {
                    if (r.code == 0) {
                        alert(r.msg);
                        vm.checkUnPass=false;
                        vm.findAll(1)
                    }
                }
            });

        },
        questionPaperCancel:function () {
            vm.questionPaper=false;
        },
        questionPaperFileCancel:function () {
            vm.questionPaperFile=false;
        },
        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'modules/posuopei/question/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        viewFile : function(row){
            var id={
                id:row.id
            };

            $.ajax({
                url: baseURL + 'modules/posuopei/question/getFileList',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(id),
                success: function (r) {
                    if (r.code == 0) {
                       vm.questionPaperFileList=r.fileEntityList;
                       vm.questionPaperFile=true;
                    }
                }
            });
        },
        resultDetail:function (row) {
            vm.questionPaper=true;
            if("2001"===row.questionType){

                vm.listLoading1 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                   /* this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;*/
                    this.claimChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.claimChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").removeClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading1 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading1 = false;
                });
            }else if("2002"===row.questionType){


                vm.listLoading2=true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    /*this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;*/
                    this.poChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").removeClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading2 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading2 = false;
                });
            }else if("2004"===row.questionType){

                vm.listLoading3 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    /*this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;*/
                    this.countChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.countChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").removeClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading3 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading3 = false;
                });
            }else if("2003"===row.questionType){

                vm.listLoading4 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    /*this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;*/
                    this.poDiscountData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poDiscountData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").removeClass("hideItem");
                    $(".other").addClass("hideItem")
                    this.listLoading4 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading4 = false;
                });
            }else{


                vm.listLoading5 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    /*this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;*/
                    this.otherData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.otherData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").removeClass("hideItem");
                    this.listLoading5 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading5 = false;
                });

            }

        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }else{
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }
        },
        queryDetails:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.forms.usercode=r.orgEntity.usercode
                        vm.forms.username=r.orgEntity.orgname

                    }
                }
            });
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        questionDateStartChanges: function (val) {
            vm.forms.questionDateStart = val;
        },
        questionDateEndChanges: function (val) {
            vm.forms.questionDateEnd = val;
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
    }
});
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
function dateFormats(value) {
    if (value == null) {
        return '';
}
    return value.substring(0, 10);
}
