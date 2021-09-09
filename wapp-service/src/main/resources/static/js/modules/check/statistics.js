
var vm = new Vue({
    el: "#invoice_check_history",
    data: {
        invoiceStatisticsTableData: [],
        invoiceStatisticsQuery: {
            invoiceYear: new Date().getFullYear().toString()
        },
        pageData: {
            currentPage: 1,
            total: 0,
        },
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        total: 0,
        totalPage: 1,
        pageList: PAGE_PARENT.PAGE_LIST,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        doLimit: {
            disabledDate(time) {
                var currentYear = vm.invoiceStatisticsQuery.invoiceYear;
                if (currentYear) {
                    return time.getTime() > Date.now();

                }
            }
        },
        loading: false

    },
    mounted: function () {
        /*  this.getInvoiceStatistics();*/
    },
    methods: {
        focuspickerchange: function (val) {
            if (val == 1) {
                $('div.el-date-editor:first-child span.el-input__suffix').css('background', '#ffaa00');
                $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#333333');
            } else {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('div.el-date-editor:first-child span.el-input__suffix').css('background', 'white');
                $('div.el-date-editor:first-child span.el-input__prefix i').css('color', '#ffaa00');
            } else {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.getInvoiceStatistics(currentPage);
        },
        getInvoiceStatistics: function (currentPage) {
            if (!isNaN(currentPage)) {
                this.pageData.currentPage = currentPage;
            }
            var params = {};
            params.invoiceYear = this.invoiceStatisticsQuery.invoiceYear;
            params.page = this.pageData.currentPage;
            params.limit = this.pageSize;
            params.sidx = "wtf";
            params.order = "desc";
            vm.loading = true;
            $.ajax({
                url: baseURL + "modules/invoice/check/statistics", async: true, type: "POST", dataType: "json",
                data: params,
                success: function (results) {
                    if (results.code == 0) {
                        var resultString = results.result;
                        vm.pageData.total = resultString.totalCount;
                        vm.total = resultString.totalCount;
                        vm.invoiceStatisticsTableData = [];
                        $.each(resultString.list, function (index, element) {
                            vm.invoiceStatisticsTableData.push(element);
                        });
                        vm.loading = false;
                    } else {
                        if (results.code != 401) {
                            alert(results.msg);
                        }
                        vm.loading = false;
                    }
                }
            });

        },
        onSubmit: function () {
            this.getInvoiceStatistics();
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (vm.pageData.total > 0) {
                this.getInvoiceStatistics(1);
            }
        },
        assignNullValue: function (row, column, cellValue, index) {

            if (cellValue == null || cellValue == '') {
                return "一 一";
            } else {
                return cellValue;
            }

        },
        /**
         * 行号 - 主列表
         */
        mainIndex: function (index) {
            return index + (this.pageData.currentPage - 1) * this.pageSize + 1;
        }
    }

});