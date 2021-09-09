
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var currentTaxNo= "-1";
var currentCreditRating= "-1";
var currentDeclarePeriod= "-1";

var vm = new Vue({
    el: '#rrapp',
    data: {
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        tableData: [],
        gridData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        pageCount: 0,
        summationTotalAmount:0,
        summationTaxAmount:0,
        listLoading: false,
        dialogTableVisible: false,
        form: {
            gfName: null,
            gfNames: [],
            gfTaxNo: "-1",
            creditRating:"-1",
            companyCode:"",
            declarePeriod:"-1"
        },
    },
    mounted: function () {
        this.querySearchGf();
        this.queryGf();
        //页面初始化--购方名称下拉选输入长度限制
        $("#gfSelect").attr("maxlength","50");
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
        query: function () {

            this.findAll();
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

            var  params = {
                page: this.currentPage,
                limit: this.pageSize,
                taxNo: this.form.gfTaxNo,
                creditRating: this.form.creditRating,
                companyCode: this.form.companyCode,
                declarePeriod: this.form.declarePeriod
            };
            vm.listLoading = true;

            this.$http.post(baseURL  + 'certification/enterpriseTaxInformation/list',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;

               /* this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;

                this.tableData = xhr.page.list;
                this.listLoading = false;*/
                
                vm.total = xhr.page.totalCount;
                vm.currentPage = xhr.page.currPage;
                vm.pageCount = xhr.page.totalPage;
                vm.tableData = xhr.page.list;
                vm.listLoading = false;

                if(this.tableData == null || this.tableData.length == 0){
                    $('.exportExcel').addClass("is-disabled");
                    $('.exportExcel').attr("disabled",true);
                }else{
                    $('.exportExcel').removeAttr("disabled");
                    $('.exportExcel').removeClass("is-disabled");
                    currentTaxNo= params.taxNo;
                    currentCreditRating= params.creditRating;
                    currentDeclarePeriod= params.declarePeriod;
                }
            });




            /*$.post(baseURL  + 'certification/enterpriseTaxInformation/list', params, function (r) {
                if(r.page.totalCount>0){
                    $('.exportExcel').removeAttr("disabled");
                    $('.exportExcel').removeClass("is-disabled");
                    currentTaxNo= params.taxNo;
                    currentCreditRating= params.creditRating;
                    currentDeclarePeriod= params.declarePeriod;
                }else{
                    $('.exportExcel').addClass("is-disabled");
                    $('.exportExcel').attr("disabled",true);
                }


                vm.total = r.page.totalCount;
                vm.currentPage = r.page.currPage;
                vm.pageCount = r.page.totalPage;
                vm.tableData = r.page.list;
                vm.listLoading = false;
            });*/

        },
        exportExcel: function(){
            /*var currentQueryParam = {
                taxNo: vm.form.gfTaxNo,
                creditRating: vm.form.creditRating,
                declarePeriod: vm.form.declarePeriod
            };
            var taxNo=currentQueryParam.taxNo;
            if(taxNo==undefined||taxNo==null||taxNo==""){
                taxNo="-1"
            }*/

            document.getElementById("ifile").src = baseURL + 'export/enterpriseTaxInformationExport'
                +'?taxNo='+currentTaxNo
                +'&creditRating='+currentCreditRating
                +'&declarePeriod='+currentDeclarePeriod;
        },
        queryGf: function () {
            $.get(baseURL + 'transferOut/detailQuery/gfNameAndTaxNo', function (r) {
                for (var i = 0; i < r.gfNameList.length; i++) {
                    vm.form.gfNames.push({name: r.gfNameList[i], taxNo: r.gfTaxNoList[i]});
                }

            })
        },
        formatCurrentTaxPeriodDate: function (row, column) {
            var value=row.currentTaxPeriod;
            if (value != null  && value != "") {
                return value.substring(0, 4)+"年"+value.substring(4)+"月";
            } else {
                return '—— ——';
            }
        },
        formatSelectStartDateDate: function (row, column) {
            var value=row.selectStartDate;
            if (value != null  && value != "") {
                return value.substring(0, 4)+"-"+value.substring(4,6)+"-"+value.substring(6);
            } else {
                return '—— ——';
            }
        },
        formatSelectEndDateDate: function (row, column) {
            var value=row.selectEndDate;
            if (value != null  && value != "") {
                return value.substring(0, 4)+"-"+value.substring(4,6)+"-"+value.substring(6);
            } else {
                return '—— ——';
            }
        },
        formatOperationEndDateDate: function (row, column) {
            var value=row.operationEndDate;
            if (value != null  && value != "") {
                return value.substring(0, 4)+"-"+value.substring(4,6)+"-"+value.substring(6);
            } else {
                return '—— ——';
            }
        } ,
        formatOldTaxNo: function (row, column) {
            if (row.oldTaxNo != null && row.oldTaxNo != "") {
                return row.oldTaxNo;
            } else {
                return '—— ——';
            }
        },
        formatCreditRating: function (row, column) {
            if (row.creditRating != null && row.creditRating != "") {
                return row.creditRating;
            } else {
                return '—— ——';
            }
        },
        /**
         * 行号 - 企业税务信息
         */
        enterpriseTaxIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});