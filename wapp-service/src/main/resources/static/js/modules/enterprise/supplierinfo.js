
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var isInitial = true;
var vm = new Vue({
    el:'#rrapp',
    data:{
        tableData: [],
        total: 0,
        totalPage:1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        currentPage: 1,
        listLoading: false,
        readonly: true,
        form:{
            orgName: null,
            taxNo: null
        },
        enterpriseForm:{
            orgName: null,
            taxNo: null,
            linkman: null,
            phone: null,
            address: null,
            email: null,
            bank: null,
            account: null,
            createTime: null,
            title: null
        },
        isShowList:true,
        detailWin:false,
        enterpriseRecord: []
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.isShowList = true;
            this.findAll(1);
        },
        handleSizeChange: function(val) {
            this.pageSize = val;
            if(!isInitial) {
                this.findAll(1);
            }
        },
        currentChange: function(currentPage){
            if(vm.total==0){
                return;
            }
            this.findAll(currentPage);
        },
        findAll: function (currentPage) {
            this.listLoading = true;
            var flag = false;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                taxNo: this.form.taxNo,
                orgName: this.form.orgName,
                orgType: ETERPRISE_TYPE.SUPPLIER_ENTERPRISE,
                isBlack: COMMON.NOT_BLACK
            };

            this.$http.post(baseURL + sysUrl.enterpriseInfoQueryPaged,
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.tableData = [];
                flag = true;
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
        },
        closeWin: function() {
            this.detailWin = false;
        },
        detail: function(row) {
            $.post(baseURL + 'modules/enterprise/getEnterpriseById/' + row.id, function (r) {
                 if (r.code == 0) {
                     var data = r.enterpriseInfo;
                     vm.enterpriseRecord = {
                         orgName: data.orgName == null?'一 一':data.orgName,
                         taxNo: data.taxNo == null || data.taxNo == ''?'一 一': data.taxNo,
                         linkman: data.linkman == null || data.linkman == ''?'一 一': data.linkman,
                         phone: data.phone == null || data.phone == ''?'一 一': data.phone,
                         address: data.address == null || data.address == ''?'一 一': data.address,
                         email: data.email == null || data.email == ''?'一 一': data.email,
                         bank: data.bank == null || data.bank == ''?'一 一': data.bank,
                         account: data.account == null || data.account == ''?'一 一': data.account,
                         createTime: data.createTime == null || data.createTime == ''?'一 一': data.createTime
                     };
                     vm.enterpriseForm = vm.enterpriseRecord;
                     vm.enterpriseForm.title = '供应商信息明细';
                     vm.detailWin = true;
                 }

            });
        },
        assignNullValue: function (row, column, cellValue, index) {
            var  trimCellvalue = $.trim(cellValue);
            if (trimCellvalue == null || trimCellvalue == '') {
                return "—— ——";
            } else {
                return cellValue;
            }
        },
        /**
         * 行号 - 供应商信息
         */
        supplyInfoIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        }
    }
});