var flag = "min";
var changeval = 400;
function zdh(){
    if(flag == "max"){return}
    console.log("----zdh----");
    $(".el-dialog").width($(".el-dialog").width()+changeval);
    $("#shell").width($("#shell").width()+changeval);
    $("#shell").children("div").children("img").css("left",parseInt($("#shell").children("div").children("img").css("left"))+changeval);
    $("#shell").children("div").children("div").css("left",parseInt($("#shell").children("div").children("div").css("left"))+changeval);
    $("#shell").children("div").children("a").css("left",parseInt($("#shell").children("div").children("a").css("left"))+changeval);
    $(".el-dialog").css("margin-top","1vh")
    $("#shell").children("div").children("a").text("还原")
    $("#shell").children("div").children("a").attr("onclick","hyzdh()")
    $("#shell").children("div").eq(1).css("min-height","517px");
    $("#shell").children("div").eq(1).css("height","auto");
    flag = "max";
}

function hyzdh(){
     if(flag == "min"){return}
     console.log("----unzdh----");
     $(".el-dialog").width($(".el-dialog").width()-changeval);
     $("#shell").width($("#shell").width()-changeval);
     $("#shell").children("div").children("img").css("left",parseInt($("#shell").children("div").children("img").css("left"))-changeval);
     $("#shell").children("div").children("div").css("left",parseInt($("#shell").children("div").children("div").css("left"))-changeval);
     $("#shell").children("div").children("a").css("left",parseInt($("#shell").children("div").children("a").css("left"))-changeval);
     $(".el-dialog").css("margin-top","15vh")
     $("#shell").children("div").children("a").text("最大化")
     $("#shell").children("div").children("a").attr("onclick","zdh()")
      $("#shell").children("div").eq(1).css("height","517px");
     flag = "min";
 }



Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;
var isInitial = true;

var editor2;
$(function(){
    setTimeout(function(){
        var E = window.wangEditor
        editor2 = new E( document.getElementById('div3') );
        editor2.customConfig.menus = [
            'head',  // 标题
            'bold',  // 粗体
            'fontSize',  // 字号
            'fontName',  // 字体
            'italic',  // 斜体
            'underline',  // 下划线
            'strikeThrough',  // 删除线
            'foreColor',  // 文字颜色
            'backColor',  // 背景颜色
            'link',  // 插入链接
            'list',  // 列表
            'justify',  // 对齐方式
            'quote',  // 引用
            'image',  // 插入图片
            'table',  // 表格
            'undo',  // 撤销
            'redo'  // 重复
        ];
        editor2.customConfig.uploadImgServer = '/upload'
        var E = window.wangEditor;
         editor2 = new E( document.getElementById('div3') );
        editor2.customConfig.uploadImgShowBase64 = true;
        editor2.create();
    },500)
});

var vm = new Vue({
    el:'#rrapp',
    data:{
        total: 0,
        currentPage: 1,
        totalPage: 0,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        pagerCount: 5,
        tableData: [],

        userinfo:[],
        questionPaperFile:[],

        listLoading: false,
        totalAmount: 0,
        totalTax: 0,
        invoiceDateOptions1: {},
        invoiceDateOptions2: {},
        qsDateOptions1: {},
        qsDateOptions2: {},
        rzhDateOptions1: {},
        rzhDateOptions2: {},
        rzhDate1: {},
        rzhDate2: {},
        poCodeMaxlength: 16,
        fileList: [],
        venderFileList: [],
        selectFileFlag: '未选择文件',
        venderSelectFileFlag: '未选择文件',
        claimCodeMaxlength:16,
        announceForm:{
            id:null,
            announcementType:'',
            header:'',
            footer:'',
            announcementInfo:'',
            announcementAnnex:'',
            announcementAnnexName:''
        },
        gfs: [],
        form:{
            userType:"0",
            venderId: "",
            orgLevel:"",
            title:null,
            content:null,
            header:"Global Business Services",
            footer:"©"+new Date().getFullYear()+"沃尔玛中国 Walmart China",
            releaseDate: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        venderTypeList: [],
        detailEntityList: [],
        tempDetailEntityList: [],
        tempValue: null,
        isNeedFileExtension:false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        file: "",
        venderFile: "",
        announceWin:false,
        token: token,
        rules:{
            invoiceNo:[{
                validator: function (rule, value, callback) {
                    if(value !=null && value != ""){
                        var regex = /^[0-9]{1,8}$/;
                        if (!regex.test(value)) {
                            callback(new Error('必须为不超过8位的数字'))
                        } else {
                            callback();
                        }
                    }else{
                        callback();
                    }
                }, trigger: 'blur'
            }],
            invoiceDate1: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ],
            invoiceDate2: [
                { type: 'string', required: true, message: '开票日期范围不能为空', trigger: 'change' }
            ]
        },
        showList: true
    },
    mounted:function(){
        this.invoiceDateOptions1 = {
            disabledDate: function(time){
                var currentTime = new Date(vm.formatDate(vm.form.releaseDate));
                return time.getTime() >= currentTime;
            }

        };
        this.invoiceDateOptions2 = {
            disabledDate: function(time){
                return time.getTime() <= Date.now();
            }
        };
        this.querySearchOrglevel();
        $("#gf-select").attr("maxlength","50");
    },
    watch: {
        'form.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.form.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        closeAnnounceWin: function () {
            hyzdh();
            vm.announceForm.id = null;
            vm.announceForm.announcementType = '';
            vm.announceForm.header = '';
            vm.announceForm.footer = '';
            vm.announceForm.announcementInfo = '';
            vm.announceForm.announcementAnnex = '';
            vm.announceForm.announcementAnnexName = '';
            vm.announceWin = false;
        },
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly','readonly');
        },
        focuspickerchange: function (val) {
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
        query: function (formName) {
            isInitial = false;
            $(".checkMsg").remove();
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    currentQueryParam = {
                        'usercode': vm.form.venderId,
                        'orgLevel': vm.form.orgLevel
                    };
                    vm.findAll(1);
                } else {
                    return false;
                }
            });
        },
        view:function(){
            vm.announceForm.announcementType = vm.form.announcementType;
            vm.announceForm.header = vm.form.header;
            vm.announceForm.footer = vm.form.footer;
            vm.announceForm.announcementInfo = editor2.txt.html();

            vm.announceWin = true;
        },
        release:function(formName){
            isInitial = false;
            $("#contentTip").remove();
            $(".checkMsg").remove();
            var Self = this;
            this.$refs[formName].validate(function (valid) {
                if (valid) {
                    if (editor2.txt.html() == '<p><br></p>' ) {
                        $("#requireMsg1 .el-form-item__content").append('<div class="el-form-item__error" id="contentTip">请输入内容!</div>');
                        return false;
                    }
                    vm.releaseAnnouncement();
                } else {
                    return false;
                }
            });
            vm.detailDialogFormInnerVisible = true;
        },
        announcement:function(){
            if(vm.userinfo.length>0){
                vm.detailDialogFormInnerVisible = true;
            }else{
                parent.layer.confirm("未选择供应商,确认向供应商列表中所有供应商发布公告吗?",{btn: ['确定', '取消']},function (index) {
                    parent.layer.close(index);
                    vm.detailDialogFormInnerVisible = true;

                })
            }
        },
        fileStatusChange: function (file, fileList) {
            this.fileList = fileList.slice(-1);
            if (file.status == 'ready') {
                this.selectFileFlag = file.name;
                this.file = file.raw;
                const index = file.name.lastIndexOf('.');
                const photoExt = file.name.substr(index, 4);
                this.fileExt = photoExt.toLowerCase();
            }
        },
        numberOnKeyBingding(event) {
            this.numberChangeBinding(event.target._value);
        },
        numberChangeBinding(val) {
            var reg = /[^\d,]/g;
            this.form.venderId = val.replace(reg, '');
        },
        venderFileStatusChange: function (file, fileList) {
            this.venderFileList = fileList.slice(-1);
            if (file.status == 'ready') {
                this.venderSelectFileFlag = file.name;
                this.file = file.raw;
                const index = file.name.lastIndexOf('.');
                const photoExt = file.name.substr(index, 4);
                this.fileExt = photoExt.toLowerCase();
            }
        },
        querySearchOrglevel: function () {
            this.$http.post(baseURL + sysUrl.getParamMap,
                {type: 'VENDOR_LEVEL'}, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                for (var i in response.data) {
                    this.$set(this.venderTypeList, i, response.data[i]);
                }
            }).catch(function (response) {
            });
        },
        querySearchXf: function (queryString, callback) {
            $.get(baseURL + 'report/comprehensiveInvoiceQuery/searchXf',{queryString:queryString},function(r){
                var resultList = [];
                for(var i=0;i<r.list.length;i++){
                    var res = {};
                    res.value = r.list[i];
                    resultList.push(res);
                }
                callback(resultList);
            });
        },
        invoiceDate1Change: function(val) {
            vm.form.invoiceDate1 = val;
        },
        invoiceDate2Change: function(val) {
            vm.form.releaseDate = val;
        },
        dqskssqDateChange: function(val) {
            vm.form.rzhBelongDate = val;
        },
        qsDate1Change: function(val) {
            vm.form.qsDate1 = val;
        },
        qsDate2Change: function(val) {
            vm.form.qsDate2 = val;
        },
        rzhDate1Change: function(val) {
            vm.form.rzhDate1 = val;
        },
        rzhDate2Change: function(val) {
            vm.form.rzhDate2 = val;
        },
        qsChange: function (value) {
            if(value=="1"){
                $('.qsItem').removeClass("hideItem");
                $('.btn-row2').addClass("hideItem");

                if(vm.form.rzhYesorno=="1"){
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row4').removeClass("hideItem");
                    $('.rzh-row3').addClass("hideItem");
                    $('.rzh-row4').removeClass("hideItem");
                }else{
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row4').addClass("hideItem");
                }
            }else{
                $('.qsItem').addClass("hideItem");
                vm.form.qsType = "-1";
                vm.form.qsDate1 = null;
                vm.form.qsDate2 = null;
                $('.btn-row4').addClass("hideItem");
                if(vm.form.rzhYesorno=="1"){
                    $('.btn-row3').removeClass("hideItem");
                    $('.btn-row2').addClass("hideItem");
                    $('.rzh-row4').addClass("hideItem");
                    $('.rzh-row3').removeClass("hideItem");
                }else{
                    $('.btn-row3').addClass("hideItem");
                    $('.btn-row2').removeClass("hideItem");
                }
            }
        },
        clearValidate: function (formName) {
            vm.form.venderId="";
            vm.venderFileList=[];
            vm.form.orgLevel="";
            this.venderSelectFileFlag = '未选择文件';
        },
        currentChange: function (currentPage) {
            if(vm.total > 0){
                vm.currentPage = currentPage;
                vm.findAll();
            }

        },
        clickView: function () {
            this.selectFileFlag = '未选择文件';
            this.file = '';
        },
        venderClickView: function () {
            this.venderSelectFileFlag = '未选择文件';
            this.venderFile = '';
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
        releaseAnnouncement: function () {
            var maxsize = 20 * 1024 * 1024;//5M
            if(vm.fileList[0]!==undefined) {
                var fileSize = vm.fileList[0].size;
                if (fileSize > maxsize) {
                    alert("附件大小不能超过20MB!");
                    return false;
                }
            }

            var formData = new FormData();
            formData.append('token', this.token);
            formData.append('attachment', vm.fileList[0]===undefined?null:vm.fileList[0].raw);
            formData.append('venderFile', vm.venderFileList[0]===undefined?null:vm.venderFileList[0].raw);
            formData.append('announcementInfo',editor2.txt.html() );
            formData.append('announcementTitle',vm.form.title );
            formData.append('userType',vm.form.userType );
            formData.append('header',vm.form.header );
            formData.append('footer',vm.form.footer );
            formData.append('orgLevel',vm.form.orgLevel==null?"":vm.form.orgLevel);
            formData.append('venderId',vm.form.venderId);
            formData.append('releaseDate',vm.form.releaseDate);
            formData.append('announcementType','0');
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
            var url = baseURL + 'base/releaseAnnouncement/announcement/insert';
            var me = this;
            var loading =  vm.getLoading("发布中...");
            me.$http.post(url, formData, config).then(function (response) {
                loading.close();
                this.clickView();
                this.venderClickView();
                if (response.data.code == 0) {

                    alert(response.data.message, function () {
                        location.reload();
                    });

                } else {
                    alert(response.data.msg);
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
        },
        numberFormat: function (row, column, cellValue) {
            if(cellValue==null || cellValue==='' || cellValue == undefined){
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
        exportExcel: function(){
            document.getElementById("ifile").src = encodeURI(baseURL + 'export/InformationInquiryClaimExport'
                +'?venderid='+currentQueryParam.venderid
                +'&claimno='+currentQueryParam.claimno
                +'&invoiceDate1='+currentQueryParam.invoiceDate1
                +'&invoiceDate2='+currentQueryParam.invoiceDate2);
        },
        exportData:function(){
            document.getElementById("iifile").src = baseURL + sysUrl.userImportExport;
        },
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time.toString().replace(/-/g, "/"));
            var seperator1 = "-";
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
         * 行号
         */
        mainIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
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




