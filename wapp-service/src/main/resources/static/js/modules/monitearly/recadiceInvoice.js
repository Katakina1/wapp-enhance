
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm=new Vue({
    el: '#rrapp',
    data: {
        tableData: [],
        currentPage: 1,
        total: 0,
        totalPage:0,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        listLoading: false,
        options: [],
        dialogVisible : false,
        exportButtonFlag:true,
        multipleSelection:[],
        invoiceTypeList: [],
        formInline: {
            numDate: '329',
            gfName: ''
        }

    },
    mounted: function () {
        this.getGfName();
        this.getInvoiceType();
        //下拉框输入长度限制
        $("#gfSelect").attr("maxlength","50");
    },
    methods: {
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            this.listLoading = true;
             if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var flag = false;
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                sidx: '',
                order: 'desc',
                gfTaxNo: this.formInline.gfName,
                numDate:this.formInline.numDate
            };
            this.$http.post(baseURL + 'monit/record/search',
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                flag = true;
                if (response.data.code != 0 && response.data.code !=401) {
                    alert(response.data.msg);
                    this.listLoading = false;
                    return;
                }
                if(response.data.page.totalCount>0){
                    vm.exportButtonFlag = false; //查询结果大于0
                }else{
                    vm.exportButtonFlag = true;
                }
                var xhr = response.data;
                $('#totalStatistics').html("合计数量: "+xhr.page.totalCount+"条, 合计金额: "+formatMoney(xhr.totalAmount)+"元, 合计税额: "+formatMoney(xhr.totalTax)+"元");
                this.total = response.data.page.totalCount;
                this.totalPage=response.data.page.totalPage;
                this.tableData = [];
                for (var key in response.data.page.list) {
                    this.$set(this.tableData, key, response.data.page.list[key]);
                }
                this.listLoading = false;
            }).catch(function (response) {
                alert(response.data.msg);
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
        }, handleSizeChange: function(val) {
            this.pageSize = val;
            if(vm.tableData.length>0){
                this.findAll(1);
            }
        },
        getGfName: function () {
            this.$http.post(baseURL + sysUrl.collectionTaxName,
                {}, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                response.data.splice(0, 0, {orgName: "全部", orgTaxNo: ''});
                for (var i in response.data) {
                    this.$set(this.options, i, response.data[i]);
                }
            }).catch(function (response) {
            });
        },changeFun: function (selection) {
            this.multipleSelection = selection;
        },
        getInvoiceType: function () {
            //param/getParamMap
            this.$http.post(baseURL + sysUrl.getParamMap,
                {type: 'INV_TYPE'}, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                response.data.splice(0, 0, {typeName: "全部", typeCode: ''});
                for (var i in response.data) {
                    this.$set(this.invoiceTypeList, i, response.data[i]);
                }
            }).catch(function (response) {
            });
        },Formatnum: function(strTime){
            if(strTime<10){return "0"+strTime}else{return strTime}
        },
        formatDate: function (strTime, flag) {
            //  IE11里面不能直接转换带"-",必须先替换成"/"
            strTime = strTime.replace("-","/");
            strTime = strTime.replace("-","/");
            var date = new Date(strTime); //这里也可以写成 Date.parse(strTime);
            return date.getFullYear()+"-"+this.Formatnum(date.getMonth()+1)+"-"+this.Formatnum(date.getDate());
        }, formatcutAppDate: function (row, column) {
            if (row.cutApproveDate != null) {
                return this.formatDate(row.cutApproveDate, true);
            } else {
                return '';
            }
        },assignNullValue: function (row, column, cellValue, index) {
            if (cellValue == null || cellValue == '') {
                return "一 一";
            } else {
                return cellValue;
            }
        },
        formatInvoiceDate: function (row) {
            if (row.invoiceDate != null) {
                return this.formatDate(row.invoiceDate, true);
            } else {
                return '';
            }
        },

        certification:function() {
            var selection = this.multipleSelection;
            var lists=[];
            var gfNo=[];
            if(!selection.length>0){
                alert("请选择数据！");
                return;
            }

            for (var i = 0; i < selection.length; i++) {
                lists.push(selection[i].id);
                gfNo.push(selection[i].gfTaxNo);
            }
            parent.layer.confirm('确定要认证所选记录吗?',function(index){
                parent.layer.close(index);
                $.ajax({
                    type:"GET",
                    dataType:"json",
                    url: baseURL + "monit/record/update",
                    data:{"lists":lists,"gfNo":gfNo},
                    async: false,
                    success: function(r) {
                        if(r.code==1){
                            vm.onSubmit();
                            alert("提交认证成功!");
                        }else{
                            alert("提交认证失败!");
                        }
                    },
                    error:function(){
                        alert("系统繁忙!请稍后重试");
                    }
                });
            },function(){
               // alert("认证失败！");
            });
            },

        onSubmit:function() {
            this.findAll(1);
        },
        exportExcel : function () {
            document.getElementById("ifile").src = baseURL + 'monit/exportExcel'
                +'?gfTaxNo='+(this.formInline.gfName==null?'':this.formInline.gfName)
                +'&numDate='+(this.formInline.numDate==null?'':this.formInline.numDate)
                +'&token='+token;
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