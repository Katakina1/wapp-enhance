/**
 * 数据权限修改
 */
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber = 123456789012345;
var isInitial = true;

var currentQueryParam = {
    dictdetaNo: null,
    onOff: null,
    userName: null,
    amendDate: null

};
var vm = new Vue({
    el: '#rrapp',
    data: {
        pageList: PAGE_PARENT.PAGE_LIST,
        userForm: {
            username: null,
            loginname: null
        },
        userPage: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        userPageChild: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        userPageChildNot: {
            currentPage: 1,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            totalPage: 1,
            total: 0
        },
        orgid: null,
        userid: null,
        company: null,
        userData: [],
        userDataChild: [],
        showList: true,
        rrappChild: false,
        addOrUpdateForms: {
            id:'',
            onOff:'0'
        },
        queryTaxCodeForm: {
            orgname: null,
            taxno: null
        },
        total: 0,
        currentPage: 1,
        pageCount: 0,
        tableData: [],
        totalPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,

        Updatedate: false,
        addOrUpdateWin: false,
        addOrUpdateUserForm: [],
        multipleSelection: [],
        addOrUpdateForm: {},
        userLoading: false,
        orgLoading: false,
        listLoading: false,
        scanpathRecord: [],
        multipleSelectionBind: []
    },
    methods: {
        query: function () {
            vm.findAll(1);
        },
        findAll: function (currentPage) {
            // if (!isNaN(currentPage)) {
            //     this.currentPage = currentPage;
            // }
            var params = {
                page: currentPage,
                limit: this.pageSize
            };
            vm.listLoading = true;
            var flag = false;
            //modules/base/dataAmend/List
            this.$http.post(baseURL + 'base/dataAmend/dataQuery/list',
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

        },
        resultDetail: function (row) {

            vm.addOrUpdateForms.id = row.id;
            vm.Updatedate = true;
        },
        resultDetailFormCancel: function () {

            vm.Updatedate = false;
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        submitForm: function () {
            //表单校验
            vm.saveScanPathData();
        },
        saveScanPathData: function () {
            var data = this.addOrUpdateForms;

            var url = 'base/dataAmend/update/updateAmend';

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    vm.Updatedate = false;
                    alert('修改成功');
                    vm.findAll(1);
                }
            });
        }


    }

});