Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var isInitial = true;

var vm = new Vue({
    el:'#costQueryApp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],
        rateOptions:[],
        statusOptions:[],
        listLoading: false,
        createDateOptions: {},
        form:{
            costNo: '',
            epsNo:'',
            walmartStatus: '',
            invoiceNo:'',
            createDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            createDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        invoiceTableData:[],
        fileData:[],
        showDetailWin: true,
        showFileDetailWin: true,
        showImgWin: true
    },
    mounted: function () {
        this.createDateOptions = {
            disabledDate: function (time) {
                var currentTime = new Date();
                return time.getTime() >= currentTime;
            }
        };
        this.getRateSelection();
        this.getStatusSelection();
        this.showDetailWin = false;
        this.showFileDetailWin = false;
        this.showImgWin = false;
    },
    methods: {
        getStatusSelection: function(){
            $.get(baseURL + 'cost/query/getStatusOptions',function(r){
                var option = {optionKey: '', optionName: '全部'};
                vm.statusOptions.push(option);
                for(var i=0;i<r.optionList.length;i++){
                    vm.statusOptions.push(r.optionList[i]);
                }
            });
        },
        getRateSelection: function(){
            $.get(baseURL + 'cost/application/getRateOptions',function(r){
                vm.rateOptions = r.optionList;
            });
        },
        query: function(formName){
            isInitial = false;
            this.findAll(1);
        },
        currentChange: function (currentPage) {
            if (vm.total == 0) {
                return;
            }
            this.findAll(currentPage);
        },
        exportExcel: function(){
            // document.getElementById("downloadFileId").src = encodeURI(baseURL + 'export/cost/costListAllExport/costLists'
            //     +'?costNo='+vm.form.costNo
            //     +'&epsNo='+vm.form.epsNo
            //     +'&invoiceNo='+vm.form.invoiceNo
            //     +'&walmartStatus='+vm.form.walmartStatus
            //     +'&createDate1='+vm.form.createDate1
            //     +'&createDate2='+vm.form.createDate2
            // );

            var params ={
                'costNo':vm.form.costNo,
                'epsNo':vm.form.epsNo,
                'invoiceNo':vm.form.invoiceNo,
                'walmartStatus':vm.form.walmartStatus,
                'createDate1':vm.form.createDate1,
                'createDate2':vm.form.createDate2
            };
            $.ajax({
                url:baseURL + 'excel/apply',
                type:"POST",
                data:{'serviceType':13,'condition':JSON.stringify(params)},
                success:function (r) {
                    flag = true;
                    if(r.code==0){
                        alert(r.data);
                        // $('#totalStatistics').html("合计数量: "+vm.total+"条, 合计金额: "+
                        //     formatMoney(r.page.summationTotalAmount)+"元, 合计税额: "+formatMoney(r.page.summationTaxAmount)+"元");

                    }

                }
            });
        },
        findAll: function (currentPage) {
            var params = {
                costNo: vm.form.costNo,
                epsNo:vm.form.epsNo,
                invoiceNo:vm.form.invoiceNo,
                walmartStatus: vm.form.walmartStatus,
                createDate1: vm.form.createDate1,
                createDate2: vm.form.createDate2,
                page: currentPage,
                limit: this.pageSize
            };
            this.listLoading = true;
            if (!isNaN(currentPage)) {
                this.currentPage = currentPage;
            }
            this.$http.post(baseURL + 'cost/query/list',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.total = xhr.page.totalCount;
                this.currentPage = xhr.page.currPage;
                this.totalPage = xhr.page.totalPage;

                this.tableData = xhr.page.list;
                this.listLoading = false;
                if(this.tableData.length>0){
                    $("#export_btn").removeAttr("disabled").removeClass("is-disabled");
                }
            });
        },
        handleSizeChange: function (val) {
            this.pageSize = val;
            if (!isInitial) {
                this.findAll(1);
            }
        },
        detail: function(row){
            var params = {costNo: row.costNo};
            this.$http.post(baseURL + 'cost/query/detail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.invoiceTableData = xhr.invoiceList;
                $("#detailRemarkId").val(row.remark);
                vm.showDetailWin = true;
            });
        },
        beforeCloseDetailWin: function(){
            this.invoiceTableData = [];
            vm.showDetailWin = false;
        },
        fileDetail: function(row){
            var params = {costNo: row.costNo};
            this.$http.post(baseURL + 'cost/query/fileDetail',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var xhr = res.body;
                this.fileData = xhr.fileList;
                vm.showFileDetailWin = true;
            });
        },
        beforeCloseFileDetailWin: function(){
            this.fileData = [];
            vm.showFileDetailWin = false;
        },
        viewFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("viewDetailImgId").src =encodeURI( baseURL + 'cost/application/viewFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
            vm.showImgWin = true;
        },
        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'cost/application/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        beforeCloseImgWin: function () {
            document.getElementById("viewDetailImgId").src = '';
            vm.showImgWin = false;
        },
        formatFileType: function (row, column, cellValue) {
            if(cellValue==null){
                return;
            }
            if(cellValue=='1'){
                return "发票图片";
            }else if(cellValue=='2'){
                return "附件";
            }
            return "";
        },
        dateFormat: function (row, column, cellValue, index) {
            if (cellValue == null) {
                return '—— ——';
            }
            return cellValue.substring(0, 10);
        },
        numberFormat: function (row, column, cellValue) {
            if (cellValue == null || cellValue == '' || cellValue == undefined) {
                return "—— ——";
            }
            var number = cellValue;
            var decimals = 2;
            var dec_point = ".";
            var thousands_sep = ",";
            number = (number + '').replace(/[^0-9+-Ee.]/g, '');
            var n = !isFinite(+number) ? 0 : +number,
                prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
                sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
                dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
                s = '',
                toFixedFix = function (n, prec) {
                    var k = Math.pow(10, prec);
                    return '' + Math.round(n * k) / k;
                };

            s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
            var re = /(-?\d+)(\d{3})/;
            while (re.test(s[0])) {
                s[0] = s[0].replace(re, "$1" + sep + "$2");
            }

            if ((s[1] || '').length < prec) {
                s[1] = s[1] || '';
                s[1] += new Array(prec - s[1].length + 1).join('0');
            }
            return s.join(dec);
        },
        /**
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        rateFormat: function (row, column, cellValue, index) {
            return formatRate(cellValue);
        },
        statusFormat: function (row, column, cellValue, index) {
            if(cellValue==null){
                return '';
            }
            for(var i=0;i<vm.statusOptions.length;i++){
                if(cellValue==vm.statusOptions[i].optionKey){
                    return vm.statusOptions[i].optionName;
                }
            }
            return cellValue;
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

function formatRate(cellValue){
    if(cellValue==null){
        return '';
    }
    for(var i=0;i<vm.rateOptions.length;i++){
        if(cellValue==vm.rateOptions[i].optionKey){
            return vm.rateOptions[i].optionName;
        }
    }
    return cellValue;
}