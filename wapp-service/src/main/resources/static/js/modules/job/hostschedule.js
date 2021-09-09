
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: '#rrapp',
    data: {
        listLoading: false,


        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        addHostDataShow:false,
        total: 0,
        form:{

        },

        readonly:false,
        hostTaskData:[],
        addHostDataForm:{
            taskDate:new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
            thiskey:'',
            vendorId:'',
            type:''
        }

    },
    mounted: function () {
        this.startDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.endDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        // vm.query();
    },
    watch:{

    },
    methods: {
        query: function () {
            this.findAll(1);
        },


        /**
         * 分页查询
         */
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll(currentPage);
            }

        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(vm.total > 0){

                this.findAll(1);
            }
        },
        checkUnPassCancel:function () {
            vm.checkUnPass=false;
        },
        findAll: function (currentPage) {

            var params = {
                page: currentPage,
                limit: this.pageSize


            };
            vm.listLoading = true;
            this.$http.post(baseURL +'modules/job/host/error',
                params, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;
                this.hostTaskData = [];
                flag = true;
                for (var key in response.data.page.list) {
                    this.$set(this.hostTaskData, key, response.data.page.list[key]);
                }
                vm.listLoading = false;
            }).catch(function (response) {
                alert(response.data.msg);
                vm.listLoading = false;
            });


        },
        addHostDataSave:function(form){
            vm.$refs[form].validate(function (valid) {
                if(valid){
                    if(vm.addHostDataForm.thiskey==='1Qzzzyjzdzj'){
                        var recordIn={
                            taskDate:vm.addHostDataForm.taskDate,
                            vendorId: vm.addHostDataForm.vendorId,
                            type:vm.addHostDataForm.type
                        };
                        $.ajax({
                            url:baseURL + 'modules/job/host/recordIn',
                            type:"POST",
                            contentType: "application/json",
                            dataType: "json",
                            data:JSON.stringify(recordIn),
                            success:function (r) {
                                if(r.code==0){
                                   alert("补录开始")
                                }else{
                                    alert(r.msg);
                                }
                            }
                        });

                    }else{
                        alert("请输入密钥！");
                    }
                }

            })
        },

        openRecordIn:function(){
            vm.addHostDataShow=true;
        },
        focuspickerchange: function(val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        addHostDataCancel:function () {
            vm.addHostDataShow=false;
        },
        taskDateChange: function (val) {
             vm.addHostDataForm.taskDate = new Date(val);
         },


    }
});

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}