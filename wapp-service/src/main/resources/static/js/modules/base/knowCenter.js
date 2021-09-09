
var isInitial = true;
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var vm = new Vue({
    el: '#rrapp',
    data: {
        costData: [],
        total: 0,
        totalPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        currentPage: 1,
        createDateOptions:{},
        createEndDateOptions: {},
        listLoading: false,
        isInsert: true,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        title:null,
        multipleSelection: [],
        isDisable: false,
        fileList:[],
        uploadForm:{
          venderType:"-1"
        },
        queryForm: {
            fileName:'',
            fileExtension:'',
            venderType: "-1",
            createStartDate: new Date().getFullYear() + "-" + format2(new Date().getMonth()+1) + "-01",
            createEndDate: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        addOrUpdateForm: {
        },
        uploadWin: false,
        costRecord: [],
        // goodsForm:[],
        multipleSelection:[],
        fullscreenLoading:false
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function(time) {
                return time.getTime() >= Date.now();
            }
        };
        this.createEndDateOptions = {
            disabledDate: function (time)  {
                return time.getTime() >= Date.now();
            }
        };
    },
    methods: {
        query: function () {
            isInitial = false;
            vm.reloadCost();
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        uploadWinClose: function () {
            this.reloadCost();
            vm.uploadForm.venderType="-1";
            this.$refs.upload.clearFiles();
            this.uploadWin = false;
        },
        download: function (row) {
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src= encodeURI(baseURL + 'knowCenter/downLoadFile?path=' + row.filePath + '&fileName='+row.fileName+ '&token=' + token);
                },
                error: function () {

                }
            });
        },
        deleteFile:function () {
            var selection = this.multipleSelection;
            if(selection.length == 0){
                alert("请选择要操作的记录");
                return;
            };
            var entity = [];
            for(var i = 0;i<selection.length;i++){
                entity.push({fileId:selection[i].fileId,filePath:selection[i].filePath})
            }
            console.log(JSON.stringify(entity));
            if (entity == null) {
                return;
            }
            parent.layer.confirm("确定要删除所有选择的数据吗?",{btn: ['确定', '取消']},function (index) {
                parent.layer.close(index);
                vm.fullscreenLoading = true;
                $.ajax({
                    type: "POST",
                    url: baseURL + 'knowCenter/deleteFile',
                    data: JSON.stringify(entity),
                    contentType: "application/json",
                    success: function (data) {
                        vm.fullscreenLoading = false;
                        vm.reloadCost();
                        vm.multipleSelection = [];
                        alert(data.msg);
                    }
                });
            })

        },
        changeSelect:function (selection) {
            this.multipleSelection = selection;
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.currentChange(1);
            }
        },
        handlePost:function (content) {
            var formData = new FormData();
            formData.append('file', content.file);
            formData.append('venderType', vm.uploadForm.venderType);
            formData.append('token', token);
            var url = baseURL + 'knowCenter/upload';
            var loading =  vm.getLoading("上传中...");
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                async: true,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (response) {
                    loading.close();
                    if (response.code == 0) {
                        alert('文件上传成功!');
                        vm.fileList=[];
                        content.onSuccess('文件上传成功');
                    } else {
                        vm.fileList=[];
                        alert('文件上传失败!');
                        content.onError('文件上传失败,已自动删除失败的文件!');
                    }
                },
                error: function (response) {
                    vm.fileList=[];
                    loading.close();
                    alert('文件上传失败!');
                    content.onError('文件上传失败,已自动删除失败的文件!');
                }

            });
        },
        beforeAvatarUpload:function (file) {
            var maxsize = 150 * 1024 * 1024;//150M
            var fileSize = file.size;
            if(fileSize > maxsize){
                alert("文件大小不能超过150MB!");
                return false;
            }
        },
        formatUploadDate: function (row) {
            if (row.uploadDate != null) {
                return this.formatDate(row.uploadDate, true);
            } else {
                return '';
            }
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
        formatDate: function (time, flag) {
            var date = new Date(time.toString().replace(/-/g,"/"));
            var seperator1 = "";
            if (flag) {
                seperator1 = "-";
            }
            var seperator2 = ":";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            return date.getFullYear() + seperator1 + month + seperator1 + strDate;
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.reloadCost();
        },
        /**
         * 行号
         */
        costIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        uploadWinShow: function () {
            vm.uploadWin=true;
        },
        submitUpload: function() {
            this.$refs.upload.submit();
        },
        reloadCost: function () {
            $(".checkMsg").remove();
            var scStartDate = new Date(this.queryForm.createStartDate);
            var scEndDate = new Date(this.queryForm.createEndDate);
            scStartDate.setMonth(scStartDate.getMonth() + 12);
            if ( (scEndDate.getTime() + 1000*60*60*24) > scStartDate.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                return;
            } else if(scEndDate.getTime()<new Date(this.queryForm.createStartDate)){
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                return;
            }

            //刷新列表数据
            var data = this.queryForm;

            // data.dicttypeid = this.dicttypeid;
            data.page = this.currentPage;
            data.limit = this.pageSize;
            this.listLoading = true;
            $.post(baseURL + 'knowCenter/list', data, function (r) {
                vm.listLoading = false;
                if (r.code === 0) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.costData = r.page.list;

                } else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        exportData:function(){
            var me = this;
            var params ={
                venderType:me.queryForm.venderType,
                createStartDate:me.queryForm.createStartDate,
                fileName:me.queryForm.fileName,
                fileExtension:me.queryForm.fileExtension,
                createEndDate:me.queryForm.createEndDate
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':49,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
            // document.getElementById("ifile").src = encodeURI(baseURL + 'export/knowCenter'
            //     +'?venderType='+me.queryForm.venderType
            //     +'&createStartDate='+me.queryForm.createStartDate
            //     +'&fileName='+me.queryForm.fileName
            //     +'&fileExtension='+me.queryForm.fileExtension
            //     +'&createEndDate='+me.queryForm.createEndDate);
        },
        /**
         * 格式化 - 数据为空时显示 --
         */
        formatterField: function (row, column, cellValue, index) {
            if (null == cellValue || '' == cellValue) {
                return '—— ——';
            }
            return cellValue;
        }
    }
});

function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
