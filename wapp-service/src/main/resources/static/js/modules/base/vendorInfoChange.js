
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el:'#rrapp',
    data:{
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        form:{
            usercode:'',
            username:'',
            bankName:'',
            bankAccount:'',
            newUserName:'',
            newBankName:'',
            newBankAccount:''
        }
    },
    mounted:function () {
        this.getVenderInfo();
    },
    methods: {
        submit:function(formName){
            $(".checkMsg").remove();
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.submitSave();
                } else {
                    return false;
                }
            });
        },
        submitSave:function () {
            var loading =  vm.getLoading("提交中...");
            var url = "vendorInfoChange/submit";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.form),
                success: function (r) {
                    loading.close();
                    if (r.code === 0) {
                        alert("提交成功", function () {
                            location.reload();
                        });
                    } else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    }else {
                        alert(r.msg);
                    }
                }
            });
        },
        getVenderInfo: function(){
            this.$http.post(baseURL + 'vendorInfoChange/getUserInfo',
                null,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                if (res.body.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                }
                var info = res.body.userInfo;
                vm.form.usercode = info.usercode;
                vm.form.username = info.username;
                vm.form.bankName = info.bankName;
                vm.form.bankAccount = info.bankAccount;
            });
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
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){
                this.findAll();
            }
        },
        dateFormat: function(row, column, cellValue, index){
            if(cellValue==null){
                return '—— ——';
            }
            return cellValue.substring(0, 10);
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




