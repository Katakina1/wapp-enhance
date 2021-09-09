/**
 *
 */

Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    /*i18n,*/
    data: {
        tableShow: true,
        tableData: [],
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage: 1,
        total: 0,
        options: [],
        gfs: [{
            value: "-1",
            label: "全部"
        }],
        form: {
            orgName: '',
            extf1: '',
            orgcode: '-1'
        },
        exportParam: {},
        rules: {
            extf1: [{
                validator: function (rule, value, callback) {
                    var regex = /^[0-9]*$/;
                    if (!regex.test(value)) {
                        callback(new Error('店号只能输入数字'))
                    } else {
                        callback();
                    }
                }, trigger: 'blur'
            }],
            /*orgcode:[{
                validator: function (rule, value, callback) {
                    var regex=/[\W]/;
                    if(regex.test(value)){
                        callback(new Error('JV号不能输入中文'))
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }]*/
        }
    },
    mounted: function () {
        this.querySearchGf();
        $("#gf-select").attr("maxlength", "50");
    },
    watch: {

        'form.extf1': {
            handler: function (val, oldValue) {
                var _this = this;
                var regex = /^[0-9]*$/;
                if (!regex.test(val)) {
                    Vue.nextTick(function () {
                        _this.form.extf1 = oldValue;
                    })
                }
            },
            deep: true
        },
        /*'form.orgcode': {
            handler: function (val, oldValue) {
                var _this = this;
                var regex=/[\W]/;
                if (regex.test(val)) {
                    Vue.nextTick(function () {
                        _this.form.orgcode = oldValue;
                    })
                }
            },
            deep: true
        }*/

    },

    methods: {

        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly', 'readonly');
        },
        currentChange: function (val) {
            if (this.total > 0) {
                this.findAll(val);
            }
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (this.total > 0) {
                this.findAll(1);
            }
        },
        querySearchGf: function () {
            $.get(baseURL + 'report/invoiceProcessingStatusReport/searchGf', function (r) {
                var gfs = [];
                gfs.push({
                    value: "-1",
                    label: "全部"
                });
                for (var i = 0; i < r.optionList.length; i++) {
                    var gf = {};
                    gf.value = r.optionList[i].value;
                    gf.label = r.optionList[i].value + "(" + r.optionList[i].label + ")";
                    gfs.push(gf);
                }
                vm.gfs = gfs;
            });
        },
        findAll: function (currentPage) {
            var loadingInstance = this.$loading({
                text: '正在拼命加载中',
                target: document.querySelector('.tableData')
            });
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            const params_ = {
                page: this.currentPage,
                limit: this.pageSize,
                order: 'desc',
                orgName: vm.form.orgName,
                orgcode: vm.form.orgcode,
                extf1: vm.form.extf1

            };
            this.exportParam = params_;
            this.$http.post(baseURL + 'information/makeInvoiceSearch',
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
            }, function (err) {
                loadingInstance.close();
                if (err.status == 408) {
                    alert(err.statusText);
                }
            }).catch(function (response) {
                loadingInstance.close();
                alert(response.data.msg);
            });
        },

        exportData: function () {
            const me = this;

            const params_ = {
                orgName: me.exportParam.orgName,
                orgcode: me.exportParam.orgcode,
                extf1: me.exportParam.extf1,

            };


            var url = baseURL + 'export/makeInvoiceSearch?orgName=' + params_.orgName + '&orgcode='
                + params_.orgcode + '&extf1=' + params_.extf1;
            document.getElementById("ifile").src = url;


        }
        ,

        onSubmit: function (formName) {
            $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
            this.tableShow = true;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
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
        }
    }

});