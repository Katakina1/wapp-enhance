
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
            venderType: "-1"
            /*createStartDate: new Date().getFullYear() + "-" + format2(new Date().getMonth()+1) + "-01",
            createEndDate: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())*/
        },
        addOrUpdateForm: {
        },
        uploadWin: false,
        costRecord: [],
        // goodsForm:[],
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate:function (time)  {
                return time.getTime() >= Date.now();
            }
        };
        this.createEndDateOptions = {
            disabledDate:function (time)  {
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
        uploadWinClose: function (val) {
            this.uploadWin = false;
        },
        download: function (row) {
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/downLoadFileToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src=encodeURI(baseURL + 'knowCenter/downLoadFile?path=' + row.filePath + '&fileName='+row.fileName+ '&token=' + token);
                },
                error: function () {

                }
            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if(!isInitial) {
                this.currentChange(1);
            }
        },
        handlePost:function (content) {
            console.log(content);
            var formData = new FormData();
            formData.append('file', content.file);
            formData.append('venderType', vm.uploadForm.venderType);
            formData.append('token', token);
            var url = baseURL + 'knowCenter/upload';
            $.ajax({
                type: "POST",
                url: url,
                data: formData,
                async: false,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (response) {
                    if (response.code == 0) {
                        content.onSuccess('文件上传成功');
                    } else {
                        alert('文件上传失败!');
                        content.onError('文件上传失败,已自动删除失败的文件!');
                    }
                },
                error: function (response) {
                    alert('文件上传失败!');
                    content.onError('文件上传失败,已自动删除失败的文件!');
                }

            });
        },
        exportData:function() {
            var me = this;
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/knowCenter'
                +'?venderType='+me.queryForm.venderType
                +'&fileName='+me.queryForm.fileName
                +'&fileExtension='+me.queryForm.fileExtension);
               /* +'&createStartDate='+me.queryForm.createStartDate
                +'&createEndDate='+me.queryForm.createEndDate);*/
        },
        beforeAvatarUpload:function (file) {
            var maxsize = 5 * 1024 * 1024;//5M
            var fileSize = file.size;
            if(fileSize > maxsize){
                alert("文件大小超过5MB的已自动删除!");
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
//            var scStartDate = new Date(this.queryForm.createStartDate);
//            var scEndDate = new Date(this.queryForm.createEndDate);
//            scStartDate.setMonth(scStartDate.getMonth() + 12);
//            if ( (scEndDate.getTime() + 1000*60*60*24) > scStartDate.getTime()) {
//                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
//                return;
//            } else if(scEndDate.getTime()<new Date(this.queryForm.createStartDate)){
//                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
//                return;
//            }
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
