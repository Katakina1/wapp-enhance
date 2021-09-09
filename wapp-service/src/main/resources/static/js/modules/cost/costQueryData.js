Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var isInitial = true;

var vm = new Vue({
    el:'#costQueryApp',
    data:{
        form:{
            epsNos: ''
        },

    },
    mounted: function () {

    },
    methods: {
        update: function () {
            var params = {
                epsNos: vm.form.epsNos
            };
            this.$http.post(baseURL + 'cost/dataFromBPMS',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function(res) {
                if (res.body.code == 0) {
                    alert(res.body.msgs);
                }
            });
            vm.deletes();
        },
        deletes:function () {
            vm.form.epsNos = '';
        }
    }
});

