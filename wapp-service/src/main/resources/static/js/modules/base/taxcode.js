

var vm = new Vue({
    el:'#rrapp',
    data:{
        taxCodeForm:[],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        loading:false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
    },
    methods:{

        reloadTaxCode:function () {
            vm.loading = true;
            var params = {
                page : this.currentPage,
                limit : this.pageSize
            }
            $.ajax({
                type:'POST',
                url: baseURL + 'base/taxcode/list',
                contentType: "application/json",
                data:JSON.stringify(params),
                success:function (r) {
                    vm.loading = false;
                    if (r.code === 0) {
                        vm.taxCodeForm = r.page.list;
                        vm.currentPage = r.page.currPage;
                        vm.total = r.page.totalCount;
                        vm.totalPage = r.page.totalPage;
                    }else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }

                }
            });

        },
        /**
         * 行号
         */
        costIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.reloadTaxCode();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadTaxCode();
        },

        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            vm.loadOption.text = text;
            return vm.$loading(vm.loadOption);
        },
    }
})
/***************导入数据模板******************/
$(function () {
    var loading;
    new AjaxUpload('#import', {
        action: baseURL + 'export/upload_taxcode' ,
        name: 'file',
        autoSubmit: true,
        data:{type:"taxcode"},
        onSubmit: function(file, extension){
            loading = vm.getLoading("导入中...");
        },
        onComplete: function (file, r) {
            loading.close();
            if(r==null){
                alert("系统异常,请联系管理员！");
            }
            var r = JSON.parse(r);
            if (r.code == 0) {
                parent.layer.confirm(r.msg,{btn: ['确定']},function (index) {
                    parent.layer.close(index);
                    vm.reloadTaxCode();
                });
            } else if (r.code == 401) {
                alert("登录超时，请重新登录", function () {
                    parent.location.href = baseURL + 'login.html';
                });
            } else {
                alert(r.msg);
            }
        }
    });
});