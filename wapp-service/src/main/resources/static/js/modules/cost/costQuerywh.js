Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var isInitial = true;

var vm = new Vue({
    el:'#costQueryApp',
    data:{
        form:{
            rzUserId: '',
            lzUserId:''
        },

    },
    mounted: function () {

    },
    methods: {
        update: function () {
            var params = {
                rzUserId: vm.form.rzUserId,
                lzUserId:vm.form.lzUserId
            };
            this.$http.post(baseURL + 'cost/updateUser',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                    alert("修改成功！");
            });
        }
    }
});

