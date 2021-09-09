/**
 *
 */

Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    /*i18n,*/
    data: {
        supplierTypeList:[],
        supplierTypeLists:[],
        selection:[],
        bathBtnUpdateDialog:false,
        tableShow: true,
        tableData: [],
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 1,
        total:0,
        options: [],
        form: {
            orgCode: '',
            userName:'',
            usertype:'-1',
            supplierType:'-1'
        },
        form1: {
            supplierType: '0',
            usertype:'-1'
        },
        exportParam: {},
        rules:{
            orgCode:[{
                validator: function (rule, value, callback) {
                    var regex = /^[0-9]{0,6}$/;
                    if(!regex.test(value)){
                        callback(new Error('必须为不超过6位的数字'))
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
        }
    },
    mounted:function(){
        this.getSupplierTypeList();
        this.getSupplierTypeLists();


    },
    watch: {
        'form.orgCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.orgCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        getSupplierTypeList:function(){
            $.get(baseURL + 'modules/supplier/getSupplierTypeList',function(r){
                var gfs = [];
                 // gfs.push({
                 //     value: "-1",
                 //     label: "全部"
                 // });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.supplierTypeList = gfs;
            });
        },
        getSupplierTypeLists:function(){
            $.get(baseURL + 'modules/supplier/getSupplierTypeList',function(r){
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for(var i=0;i<r.optionList.length;i++){
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].label;
                    gfs.push(gf);
                }
                vm.supplierTypeLists = gfs;
            });
        },
        currentChange: function(val) {
            if(this.total > 0) {
                this.findAll(val);
            }
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(this.total > 0) {
                this.findAll(1);
            }
        },
        findAll: function (currentPage) {
            var loadingInstance = this.$loading({
                text: '正在拼命加载中',
                target: document.querySelector('.tableData')
            });
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            var orgcodeSize=this.form.orgCode.toString().length;
            while(orgcodeSize<6&&orgcodeSize!=0){
                this.form.orgCode="0"+this.form.orgCode;
                orgcodeSize++;
            }
            const params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                order: 'desc',
                orgCode: this.form.orgCode,
                userName: this.form.userName,
                usertype:this.form.usertype,
                supplierType:this.form.supplierType
            };
            this.exportParam = params_;
            this.$http.post(baseURL + 'information/supplierInformationSearch',
                params_, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                loadingInstance.close();
                this.total = response.data.page.totalCount;
                this.totalPage = response.data.page.totalPage;

                this.tableData = [];
                for (let key in response.data.page.list) {
                    this.$set(this.tableData, key, response.data.page.list[key]);
                }
            },function(err){
                loadingInstance.close();
            if(err.status == 408) {
                alert(err.statusText);
            }
        }).catch(function (response) {
                loadingInstance.close();
                alert(response.data.msg);
            });
        },
        //批量修改供应商类型
        updateSupplierTypeBath:function(){
            vm.bathBtnUpdateDialog = true;
        },
        changeFunPO:function(row) {
            var details=[];
            for(var i=0;i<row.length;i++){
                details.push(row[i].userid);
            }
            vm.selection=details;
            if(vm.selection.length>0){
                //增加样式
                $("#bath_btn_update").removeAttr("disabled").removeClass("is-disabled");
            }else {
                $("#bath_btn_update").attr("disabled","disabled").addClass("is-disabled");
            }
        },
        bathBtnUpdateCancel:function() {
            vm.bathBtnUpdateDialog = false;
            vm.form1.supplierType='';
        },
        //
        submitForm:function(){
            var param = {
                "idList": JSON.stringify(vm.selection),
                "supplierType": vm.form1.supplierType
            };
            this.$http.post(baseURL + 'modules/informationInquiry/updateSupplierTypeBath',
                param,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                if(xhr.code==0){
                    alert("供应商级别修改成功!");
                    vm.bathBtnUpdateCancel();
                    vm.findAll(vm.currentPage);
                }else {
                    alert("操作失败，系统异常！");
                }

            });

        },
        exportData:function() {
            const me = this;
            const params_ = {
                orgCode: me.exportParam.orgCode,
                userName: me.exportParam.userName,
                usertype:me.exportParam.usertype,
                supplierType:me.exportParam.supplierType

            };
            $("#daochu").attr("disabled","true").addClass("is-disabled");
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':42,'condition':JSON.stringify(params_)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
            // var url=baseURL+ 'export/supplierInformationSearch?orgCode='+params_.orgCode+'&userName='+params_.userName+'&supplierType='+params_.supplierType+'&usertype='+params_.usertype;
            // document.getElementById("ifile").src=url;


        },

        onSubmit:function(formName) {
            this.tableShow = true;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    $("#daochu").removeAttr("disabled").removeClass("is-disabled");
                    vm.findAll(1);
                } else {
                    return false;
                }
            });

        },

        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        formatterUserType: function (a,b,usertype){
            if(usertype=="P"||usertype=="PP"){
                return "商品";
            }else if(usertype=="E"||usertype=="EJ"){
                return "费用";
            }else{
                return "";
            }

        },
        formatterOrgLevel: function (a,b,orgLevel){
            if(orgLevel=="0"){
                return "商品-KEY Vendor";
            }else if(orgLevel=="1"){
                return "商品-VIP Vendor";
            }else if(orgLevel=="2"){
                return "商品-其他";
            }else if(orgLevel=="3"){
                return "费用-其他";
            }else{
                return "--";
            }

        },
        fromatextf0:function (a,b,usertype) {
    if(usertype=="0"||usertype=="FALSE"){
        return "正常";
    }else if(usertype=="1"||usertype=="TRUE"){
        return "冻结";
    }else{
        return "正常";
    }
},
 fromatextf1:function (a,b,usertype) {
    if(usertype=="0"||usertype=="FALSE"){
        return "正常";
    }else if(usertype=="1"||usertype=="TRUE"){
        return "删除";
    }else{
        return "正常";
    }
}
    }

});