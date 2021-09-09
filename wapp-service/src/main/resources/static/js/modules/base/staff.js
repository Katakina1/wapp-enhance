var allDataList = [];
var dataList = [];
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: '#rrapp',
    data: {
        listLoading: false,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total:0,
        form : {
            staffNo:'',
            email:'',
            currentPage: 1,
            totalPage: 0,
            pageSize: PAGE_PARENT.PAGE_SIZE,
            pageList: PAGE_PARENT.PAGE_LIST,
            total:0
        },
        updateForm:{
            staffNo:'',
            email:'',
            staffName:'',
            winID:'',
            costCenter:'',
            costCenterName:'',
            gfTaxNo:''
        },
        listLoadingImport: false,
        updateStaffShow:false,
        tableData: [],
        importDialogFormVisible:false,
        importCbzxFormVisible:false,
        fileList: [],
        selectFileFlag:'',

        listLoading1: false,
        showVendorWin1: true,
        form1:{
            staff: '',
            usercode: '',
            username: ''
        },
        total1: 0,
        currentPage1: 1,
        totalPage1: 0,
        pageSize1: PAGE_PARENT.PAGE_SIZE,
        pageList1: PAGE_PARENT.PAGE_LIST,
        pagerCount1: 5,
        tableData1: [],

        listLoading2: false,
        showVendorWin2: true,
        form2:{
            usercode: '',
            username: ''
        },
        total2: 0,
        currentPage2: 1,
        totalPage2: 0,
        pageSize2: PAGE_PARENT.PAGE_SIZE,
        pageList2: PAGE_PARENT.PAGE_LIST,
        pagerCount2: 5,
        tableData2: [],
        form3:{
            staff: '',
            jv: '',
            orgname: ''
        },
        showJvWin3: true,
        total3: 0,
        currentPage3: 1,
        totalPage3: 0,
        pageSize3: PAGE_PARENT.PAGE_SIZE,
        pageList3: PAGE_PARENT.PAGE_LIST,
        pagerCount3: 5,
        tableData3: [],
        listLoading3:false,
        form4:{
            staff: '',
            jv: '',
            orgname: ''
        },
        showJvWin4: true,
        total4: 0,
        currentPage4: 1,
        totalPage4: 0,
        pageSize4: PAGE_PARENT.PAGE_SIZE,
        pageList4: PAGE_PARENT.PAGE_LIST,
        pagerCount4: 5,
        tableData4: [],
        listLoading4:false,
        multipleSelection3: [],
        multipleSelection4: [],
        multipleSelection5: [],
        form5:{
            orgid: '',
            costcode: '',
            costname: ''
        },
        showCostWin1: true,
        showCostWin2: true,
        total5: 0,
        currentPage5: 1,
        totalPage5: 0,
        pageSize5: PAGE_PARENT.PAGE_SIZE,
        pageList5: PAGE_PARENT.PAGE_LIST,
        pagerCount5: 5,
        tableData5: [],
        listLoading5:false,
        addCostForm:{
            orgid:'',
            costcode:'',
            costname:'',
        },


    },
    mounted:function(){
        this.showVendorWin1 = false;
        this.showVendorWin2 = false;
        this.showJvWin3 = false;
        this.showJvWin4 = false;
        this.showCostWin1 = false;
        this.showCostWin2 = false;
    },
    methods: {
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        showSelectFileWin1() {
            this.selectFileFlag1 = '';
            this.file1 = '';
            $("#upload_form1")[0].reset();
            $("#file1").click();
        },

        query:function () {
            var data = this.form;
            data.page = this.currentPage;
            data.limit = this.pageSize;
            $.ajax({
                type: "POST",
                url: baseURL + 'base/staff/query',
                data:{
                  page:   this.currentPage,
                  limit:  this.pageSize,
                  staffNo: this.form.staffNo,
                  email: this.form.email
                },
                success: function (r) {
                    vm.currentPage = r.page.currPage;
                    vm.total = r.page.totalCount;
                    vm.totalPage = r.page.totalPage;
                    vm.tableData = r.page.list;
                }
            });
        },
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.query();
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
        },
        bindVendor: function (row) {
            this.showVendorWin1 = true;
            this.form1.staff = row.staffNo;
            this.query1();
        },
        bindJv: function (row) {
            this.showJvWin3 = true;
            this.form3.staff = row.staffNo;
            this.query3();
        },
        bindCost: function (row) {
            this.showCostWin1 = true;
            this.form5.orgid = row.orgid;
            this.queryCost();
        },
        beforeCloseVendorWin1: function(){
            this.showVendorWin1 = false;
            this.form1.staff = '';
            this.form1.usercode = '';
            this.form1.username = '';
        },
        beforeCloseJvWin3: function(){
            this.showJvWin3 = false;
            this.form3.staff = '';
            this.form3.jv = '';
            this.form3.orgname = '';
        },
        beforeCloseCostWin1: function(){
            this.showCostWin1 = false;
            this.form5.orgid = '';
            this.form5.costcode = '';
            this.form5.costname = '';
        },
        query1: function(){
            this.findAll1(1);
        },
        query3: function(){
            this.findAll3(1);
        },
        query4: function(){
            this.findAll4(1);
        },
        queryCost: function(){
            this.findAllCost(1);
        },
        currentChange1: function (currentPage) {
            if (vm.total1 == 0) {
                return;
            }
            this.findAll1(currentPage);
        },
        currentChange3: function (currentPage) {
            if (vm.total3 == 0) {
                return;
            }
            this.findAll3(currentPage);
        },
        currentChange4: function (currentPage) {
            if (vm.total4 == 0) {
                return;
            }
            this.findAll4(currentPage);
        },
        currentChange5: function (currentPage) {
            if (vm.total5 == 0) {
                return;
            }
            this.findAllCost(currentPage);
        },
        findAll1: function (currentPage) {
            var params = {
                staff: vm.form1.staff,
                usercode: vm.form1.usercode,
                username: vm.form1.username,
                page: currentPage,
                limit: this.pageSize1
            };
            this.listLoading1 = true;
            if (!isNaN(currentPage)) {
                this.currentPage1 = currentPage;
            }
            this.$http.post(baseURL + 'base/staff/query1',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total1 = xhr.page.totalCount;
                this.currentPage1 = xhr.page.currPage;
                this.totalPage1 = xhr.page.totalPage;

                this.tableData1 = xhr.page.list;
                this.listLoading1 = false;
            });
        },
        findAll3: function (currentPage) {

            this.listLoading3 = true;
            if (!isNaN(currentPage)) {
                this.currentPage3 = currentPage;
            }
            var params = {
                staff: vm.form3.staff,
                jv: vm.form3.jv,
                orgname: vm.form3.orgname,
                page: this.currentPage3,
                limit: this.pageSize3
            };
            this.$http.post(baseURL + 'base/staff/queryJv',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total3 = xhr.page.totalCount;
                this.currentPage3 = xhr.page.currPage;
                this.totalPage3 = xhr.page.totalPage;

                this.tableData3 = xhr.page.list;
                this.listLoading3 = false;
            });
        },
        findAll4: function (currentPage) {
            var params = {
                staff: vm.form4.staff,
                jv: vm.form4.jv,
                orgname: vm.form4.orgname,
                page: currentPage,
                limit: this.pageSize4
            };
            this.listLoading4 = true;
            if (!isNaN(currentPage)) {
                this.currentPage4 = currentPage;
            }
            this.$http.post(baseURL + 'base/staff/queryJvNotAdd',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total4 = xhr.page.totalCount;
                this.currentPage4 = xhr.page.currPage;
                this.totalPage4 = xhr.page.totalPage;

                this.tableData4 = xhr.page.list;
                this.listLoading4 = false;
            });
        },
        findAllCost: function (currentPage) {
            var params = {
                orgid: vm.form5.orgid,
                costcode: vm.form5.costcode,
                costname: vm.form5.costname,
                page: currentPage,
                limit: this.pageSize5
            };
            this.listLoading5 = true;
            if (!isNaN(currentPage)) {
                this.currentPage5 = currentPage;
            }
            this.$http.post(baseURL + 'base/staff/queryCost',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total5 = xhr.page.totalCount;
                this.currentPage5 = xhr.page.currPage;
                this.totalPage5 = xhr.page.totalPage;

                this.tableData5 = xhr.page.list;
                this.listLoading5 = false;
            });
        },
        handleSizeChange1: function (val) {
            this.pageSize1 = val;
        },
        handleSizeChange3: function (val) {
            this.pageSize3 = val;
        },
        handleSizeChange4: function (val) {
            this.pageSize4 = val;
        },
        handleSizeChange5: function (val) {
            this.pageSize5 = val;
        },
        handleSelectionChange1(val) {
            this.multipleSelection1 = val;
        },
        handleSelectionChange3(val) {
            this.multipleSelection3 = val;
        },
        mainIndex1: function (index) {
            return index + (this.currentPage1 - 1) * this.pageSize1 + 1;
        },
        addVendor: function () {
            this.showVendorWin2 = true;
            this.query2();
        },
        deleteVendor: function(){
            if(this.multipleSelection1.length==0){
                alert('请选择需要删除的供应商');
                return;
            }
            var c = this;
            confirm("确定删除所选的供应商?", function () {
                var idArray = [];
                for(var i=0;i<vm.multipleSelection1.length;i++){
                    idArray[i] = vm.multipleSelection1[i].userid;
                }
                var data = idArray.join(',');
                c.$http.post(baseURL + 'base/staff/deleteVendor',
                    {ids: data},
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                        vm.findAll1(1);
                        alert("删除成功");
                });
            })
        },
        addVendorSubmit: function(){
            if(this.multipleSelection2.length==0){
                alert('请选择需要添加的供应商');
                return;
            }
            var vendorArray = [];
            for(var i=0;i<vm.multipleSelection2.length;i++){
                vendorArray[i] = vm.multipleSelection2[i].usercode;
            }
            var data = vendorArray.join(',');
            this.$http.post(baseURL + 'base/staff/addVendor',
                {staff: vm.form1.staff, vendors: data},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                    vm.beforeCloseVendorWin2();
                    vm.findAll1(1);
            });
        },
        beforeCloseVendorWin2: function(){
            this.showVendorWin2 = false;
        },
        query2: function(){
            this.findAll2(1);
        },
        currentChange2: function (currentPage) {
            if (vm.total2 == 0) {
                return;
            }
            this.findAll2(currentPage);
        },
        findAll2: function (currentPage) {
            var params = {
                staff: vm.form1.staff,
                usercode: vm.form2.usercode,
                username: vm.form2.username,
                page: currentPage,
                limit: this.pageSize2
            };
            this.listLoading2 = true;
            if (!isNaN(currentPage)) {
                this.currentPage1 = currentPage;
            }
            this.$http.post(baseURL + 'base/staff/query2',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total2 = xhr.page.totalCount;
                this.currentPage2 = xhr.page.currPage;
                this.totalPage2 = xhr.page.totalPage;

                this.tableData2 = xhr.page.list;
                this.listLoading2 = false;
            });
        },
        handleSizeChange2: function (val) {
            this.pageSize2 = val;
        },
        handleSelectionChange2(val) {
            this.multipleSelection2 = val;
        },
        handleSelectionChange4(val) {
            this.multipleSelection4 = val;
        },
        handleSelectionChange5(val) {
            this.multipleSelection5 = val;
        },
        mainIndex2: function (index) {
            return index + (this.currentPage2 - 1) * this.pageSize2 + 1;
        },

        importFormCancel: function () {
            vm.importDialogFormVisible = false;
        },
        importIn:function(){
            vm.importDialogFormVisible = true;
        },
        importCbzx:function(){
            vm.importCbzxFormVisible = true;
        },
        importCbzxCancel: function () {
            vm.importCbzxFormVisible = false;
        },
        updateStaffShowCancel: function () {
            vm.updateStaffShow = false;
        },
        addCostShowCancel:function () {
            vm.showCostWin2 = false;
        },
        onChangeFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                this.isNeedFileExtension = false;
                return false;
            } else {
                this.selectFileFlag = '';
                this.file = '';
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    this.file = event.target.files[0];
                    this.isNeedFileExtension = true;
                    //截取名称最后18位
                    this.selectFileFlag = event.target.files[0].name;
                }
            }
        },
        uploadFile: function (event) {
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.listLoadingImport = true;
                var flag = false
                var hh;
                var url = baseURL + "base/staff/excelImport";
                this.$http.post(url, formData, config).then(function (response) {
                    this.listLoadingImport = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    this.selectFileFlag = '';
                    this.file = '';
                    if(response.data.errorCount>0){
                        alert("请检查数据是否完整！");
                        return;
                    }
                    if (response.data.success) {
                        for (var i = 0; i < response.data.reason.length; i++) {
                            // if (dataList.length > 0 && this.contains(dataList, response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo)) {
                            //     response.data.reason[i].noAuthTip = "0";
                            //     allDataList.push(response.data.reason[i])
                            // } else {
                            //     dataList.push(response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo);
                            //     allDataList.push(response.data.reason[i])
                            // }
                            allDataList.push(response.data.reason[i])
                        }
                        vm.importDialogFormVisible = false;
                        // alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条,失败" + response.data.errorCount + "条");
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条");
                        //清空上一次导入内容
                        this.selectFileFlag = '';
                        this.file = '';
                        $("#upload_form")[0].reset();
                        this.query();
                    } else {
                        vm.importDialogFormVisible = false;
                        alert(response.data.reason);
                        this.selectFileFlag = '';
                        this.file = '';
                        $("#upload_form")[0].reset();
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);
            }
        },
        uploadFile1: function (event) {  //导入成本中心信息
            if (!this.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);
                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                this.listLoadingImport = true;
                var flag = false
                var hh;
                var url = baseURL + "base/staff/excelImportCbzx";
                this.$http.post(url, formData, config).then(function (response) {
                    this.listLoadingImport = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    this.selectFileFlag = '';
                    this.file = '';
                    if(response.data.errorCount>0){
                        alert("请检查数据是否完整!");
                        return;
                    }
                    if (response.data.success) {
                        for (var i = 0; i < response.data.reason.length; i++) {
                            // if (dataList.length > 0 && this.contains(dataList, response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo)) {
                            //     response.data.reason[i].noAuthTip = "0";
                            //     allDataList.push(response.data.reason[i])
                            // } else {
                            //     dataList.push(response.data.reason[i].invoiceCode + response.data.reason[i].invoiceNo);
                            //     allDataList.push(response.data.reason[i])
                            // }
                            allDataList.push(response.data.reason[i])
                        }
                        vm.importDialogFormVisible = false;
                        // alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条,失败" + response.data.errorCount + "条");
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条");
                        //清空上一次导入内容
                        this.selectFileFlag = '';
                        this.file = '';
                        $("#upload_form1")[0].reset();
                        this.query();
                    } else {
                        vm.importDialogFormVisible = false;
                        alert(response.data.reason);
                        this.selectFileFlag = '';
                        this.file = '';
                        $("#upload_form1")[0].reset();
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);
            }
        },
        deleteStaff: function (row) {
            confirm('确定要删除该记录吗？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/staff/delete",
                    data: {staffNo:row.staffNo},
                    success: function (r) {
                        if (r.code == 0) {
                            vm.query();
                            alert('删除成功');
                        } else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                var hostHref = parent.location.href;
                                if(hostHref.indexOf("int")!=-1){
                                    parent.location.href ="http://rl.wal-mart.com";
                                }else if(hostHref.indexOf("ext")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else{
                                    parent.location.href = baseURL + 'login.html';
                                }
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        addJv: function () {
            this.showJvWin4 = true;
            this.form4.staff = this.form3.staff;
            this.query4();
        },
        addCost: function () {
            this.showCostWin2 = true;
            this.addCostForm.orgid = this.form5.orgid;

        },
        beforeCloseJvWin4:function (){
            this.showJvWin4 = false;
            this.form4.staff = '';
            this.form4.jv = '';
            this.form4.orgname = '';
        },
        addJvSubmit:function(){
            if(this.multipleSelection4.length==0){
                alert('请选择需要添加的JV信息');
                return;
            }
            var vendorArray = [];
            for(var i=0;i<vm.multipleSelection4.length;i++){
                vendorArray[i] = vm.multipleSelection4[i].orgid;
            }
            var data = vendorArray.join(',');
            this.$http.post(baseURL + 'base/staff/addJvInfo',
                {staff: vm.form4.staff, jvs: data},
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                vm.beforeCloseJvWin4();
                vm.findAll3(1);
            });
        },

        deleteJv: function(){
            if(this.multipleSelection3.length==0){
                alert('请选择需要删除的JV');
                return;
            }
            var c = this;
            confirm("确定删除所选的JV?", function () {
                var idArray = [];
                for(var i=0;i<vm.multipleSelection3.length;i++){
                    idArray[i] = vm.multipleSelection3[i].orgid;
                }
                var data = idArray.join(',');
                c.$http.post(baseURL + 'base/staff/deleteJv',
                    {ids: data},
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    vm.findAll3(1);
                    alert("删除成功");
                });
            })
        },
        updateStaff:function(row){
            vm.updateStaffShow = true;
            vm.updateForm.staffNo = row.staffNo;
            vm.updateForm.staffName = row.staffName;
            vm.updateForm.email = row.email;
            vm.updateForm.winID = row.winID;
            vm.updateForm.costCenter = row.costCenter;
            vm.updateForm.costCenterName = row.costCenterName;
            vm.updateForm.gfTaxNo = row.gfTaxNo;

        },
        submitForm: function () {
            //表单校验
            this.$refs['updateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveStaff();
                } else {
                    return false
                }
            })
        },
        saveStaff:function () {
            var data = this.updateForm;
            $.ajax({
                type: "POST",
                url: baseURL + 'base/staff/save',
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    alert("保存成功");
                    vm.updateStaffShow = false;
                    vm.query();
                }
            });
        },
        exportData() {
            document.getElementById("ifile").src = baseURL + "export/staff/template";
        },
        exportCostData() {
            document.getElementById("ifileCost").src = baseURL + "export/costCenter/template";
        },
        submitCostForm: function () {
            //表单校验
            this.$refs['addCostForm'].validate(function (valid) {
                if (valid) {
                    vm.saveCost();
                } else {
                    return false
                }
            })
        },
        saveCost:function () {
            var data = this.addCostForm;
            $.ajax({
                type: "POST",
                url: baseURL + 'base/staff/saveCost',
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if(r.error!=null){
                    alert(r.error);
                    return;
                    }
                    alert("保存成功");
                    vm.showCostWin2 = false;
                    vm.addCostForm.costcode='';
                    vm.addCostForm.costname='';
                    vm.findAllCost(1);
                }
            });
        },
        deleteCost: function(){
            if(this.multipleSelection5.length==0){
                alert('请选择需要删除的成本中心');
                return;
            }
            var c = this;
            confirm("确定删除所选的成本中心?", function () {
                var idArray = [];
                for(var i=0;i<vm.multipleSelection5.length;i++){
                    idArray[i] = vm.multipleSelection5[i].id;
                }
                var data = idArray.join(',');
                c.$http.post(baseURL + 'base/staff/deleteCost',
                    {ids: data},
                    {
                        'headers': {
                            "token": token
                        }
                    }).then(function (res) {
                    vm.findAllCost(1);
                    alert("删除成功");
                });
            })
        },

}



    })




