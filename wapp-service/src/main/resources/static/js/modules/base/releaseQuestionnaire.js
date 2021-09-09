
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;


var vm = new Vue({
    el:'#rrapp',
    data:{
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        form:{
            title:'',
            questions: [{
                label:'问卷题目',
                value: '',
                type:'topic'
            },{
                label:'选项',
                value:'',
                type:'option'
            },
            {
                label:'选项',
                value:'',
                type:'option'
            },
            {
                label:'选项',
                value:'',
                type:'option'
            },
            {
                label:'选项',
                value:'',
                type:'option'
            }],
        },
        token: token
    },
    methods: {
        addTopic:function() {
            if(this.form.questions.length===50){
                alert("问卷调查已新增10题，不能继续新增!");
                return;
            }
            this.form.questions.push( {
                    label:'问卷题目',
                    value: '',
                    type:'topic'
                },{
                    label:'选项',
                    value:'',
                    type:'option'
                },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                });
        },
        reset:function () {
            this.form.questions= [{
                label:'问卷题目',
                value: '',
                type:'topic'
            },{
                label:'选项',
                value:'',
                type:'option'
            },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                },
                {
                    label:'选项',
                    value:'',
                    type:'option'
                }];
        },
        release:function(formName){
            isInitial = false;
            $(".checkMsg").remove();
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.releaseQuestionnaire();
                } else {
                    return false;
                }
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
        releaseQuestionnaire: function () {


            var loading =  vm.getLoading("发布中...");
            var url = "base/releaseQuestionnaire/" + this.form.title;
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(this.form.questions),
                success: function (r) {
                    loading.close();
                    if (r.code === 0) {
                        alert("发布成功", function () {
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
    },
    created: function () {
        //this.announcement();
    },
});


function format2(value){
    if(value<10){
        return "0"+value;
    }else{
        return value;
    }
}




