Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var taxNo;
var settlementMethod;

var vm = new Vue({
    el: '#fixedMatchApp',
    data: {
        invoiceTypeValue:"",
        invoiceTypeValueOptions:[{
            value: '',
            label: '请选择'
        },{
            value: '01',
            label: '增值税专用发票'
        }, {
            value: '02',
            label: '增值税普通发票'
        }],
        topForm: {
            jvcode: '',
            venderid: '',
            venderName: ''
        },
        jvOptions: [],
        invoiceQueryForm:{
            invoiceNo:'',
            invoiceQueryDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            invoiceQueryDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        orderForm: {
            orderNo: '',
            orderDate1: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01",
            orderDate2: new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate())
        },
        orderDateOptions: {},
        orderData: [],
        invoiceQueryData:[],
        taxRateArray:[],
        questionTypeArray:[],
        listLoading: false,
        listLoading2:false,
        invoiceForm: {
            invoiceCode: '',
            invoiceNo: '',
            invoiceDate: '',
            invoiceType: '',
            invoiceAmount: '',
            checkCode: '',
            totalAmount: '',
            taxAmount: '',
            taxRate: '',
            id: '',
            sourceSystem:''
        },
        invoiceDateOptions: {},
        invoiceQueryDateOptions: {},
        rateOptions: [],
        invoiceData: [],
        totalOrderCount: 0,
        totalOrderAmount: 0.00,
        totalInvoiceCount: 0,
        totalInvoiceAmount: 0.00,
        submitLoading: false,
        showConfirm: false,
        showMethod: false,
        showImgWin: false,
        fileForm: {},
        fileData: [],
        uploadLoading: false,
        zyInvoiceDialog:false,
        ptInvoiceDialog:false,
        zpxzOptionList:[],
        matchFalidType:0,
        choose:'',
        methodCheck:''
    },
    mounted: function () {
        this.getJV();
        this.getGys();
        this.getRate();
        this.queryTaxRate();
        this.queryQuestionType();
        this.displayHeaderCheckbox();
        this.orderDateOptions1 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.orderForm.orderDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.orderDateOptions2 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.orderForm.orderDate2));
                return time.getTime() >= Date.now();
            }
        };
        this.orderDateOptions3 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.invoiceForm.invoiceDate));
                return time.getTime() >= Date.now();
            }
        };
        this.orderDateOptions4 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.invoiceQueryForm.invoiceQueryDate1));
                return time.getTime() >= Date.now();
            }
        };
        this.orderDateOptions5 = {
            disabledDate: function (time) {
                var currentTime = new Date(vm.formatDate(vm.invoiceQueryForm.invoiceQueryDate2));
                return time.getTime() >= Date.now();
            }
        };
        document.getElementById("item-checkCode").style.display = "none";
    },
    watch: {
        'orderForm.orderNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,20}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.orderForm.orderNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoiceForm.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoiceForm.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoiceQueryForm.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoiceQueryForm.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoiceForm.checkCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,6}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoiceForm.checkCode = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoiceForm.invoiceCode':{
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,20}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoiceForm.invoiceCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {

        displayHeaderCheckbox:function(){
            $('.el-table__header .el-checkbox__input').css('display','none');
        },
        isDisabled:function(row,index){
            var cars=vm.invoiceData;

            for (var i=0;i<cars.length;i++){
                if(cars[i].invoiceCode==row.invoiceCode&&cars[i].invoiceNo==row.invoiceNo){
                    return 0;
                }
            }
            return 1;
        },
        noJVAlert:function () {
            alert('请先选择JV');
        },
        invoiceQuery:function(){
            var checkKPDate = true;
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.invoiceQueryForm.invoiceQueryDate1);
            var qsEndDate = new Date(vm.invoiceQueryForm.invoiceQueryDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);

            if (qsEndDate.getTime() + 1000 * 60 * 60 * 24 > qsStartDate.getTime()) {
                $("#requireMsg1 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate = false;
            } else if (qsEndDate.getTime() < new Date(vm.invoiceQueryForm.invoiceQueryDate1)) {
                $("#requireMsg1 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate = false;
            }
            if (!(checkKPDate)) {
                return;
            }
            var params = {
                invoiceQueryDate1: vm.invoiceQueryForm.invoiceQueryDate1,
                invoiceQueryDate2: vm.invoiceQueryForm.invoiceQueryDate2,
                invoiceNo: vm.invoiceQueryForm.invoiceNo,
                gfTaxNo:taxNo,
                orgid:parent.vm.user.orgid
            };
            listLoading2=true;
            vm.invoiceQueryData=[];
            this.$http.post(baseURL + 'fixed/match/searchInvoiceQuery',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                listLoading2=false;
                    if(res.body.code==0&&res.body.invoiceInfo.length>0){
                        var invoiceList=res.body.invoiceInfo
                        for(var j = 0,len = invoiceList.length; j < len; j++){
                            var invoiceObject=invoiceList[j];
                            var invoice = {};
                            invoice.invoiceCode = invoiceObject.invoiceCode;
                            invoice.invoiceNo = invoiceObject.invoiceNo;
                            //根据代码号码判断是否已经选入表格


                            invoice.id = invoiceObject.id;
                            invoice.invoiceType = invoiceObject.invoiceType;
                            invoice.invoiceDate = invoiceObject.invoiceDate;
                            invoice.invoiceAmount = invoiceObject.invoiceAmount;
                            invoice.totalAmount = invoiceObject.totalAmount;
                            invoice.taxAmount = invoiceObject.taxAmount;
                            invoice.taxRate = invoiceObject.taxRate;
                            invoice.gfTaxNo = invoiceObject.gfTaxNo;
                            invoice.xfName = invoiceObject.xfName;
                            invoice.checkCode = invoiceObject.checkCode;
                            invoice.sourceSystem = invoiceObject.sourceSystem;
                            vm.invoiceQueryData.push(invoice);
                        }
                    }


                })

            } ,
        getRate: function(){
            $.get(baseURL + 'fixed/match/getRate',function(r){
                vm.rateOptions = r.optionList;
            });
        },
        getJV: function(){
            $.get(baseURL + 'cost/application/getGfInfo',function(r){
                vm.jvOptions = r.optionList;
            });
        },
        changeJV: function(value){
            $.get(baseURL + 'cost/application/getGfTaxNo',{jvcode:value},function(r){
                taxNo = r.gfTaxNo;
            });
            this.query();
        },
        getGys: function(){
            this.$http.post(baseURL + 'cost/application/getUserInfo',
                null,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.userInfo;
                vm.topForm.venderid = info.venderId;
                vm.topForm.venderName = info.venderName;
            });
        },
        invoiceTypeChange:function(){

        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 2) {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 3) {
                $('#datevalue3').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue3').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 4) {
                $('#datevalue4').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue4').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 7) {
                $('#datevalue7').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue7').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        orderDate1Change: function (val) {
            vm.orderForm.orderDate1 = val;
        },
        orderDate2Change: function (val) {
            vm.orderForm.orderDate2 = val;
        },
        orderDate3Change: function (val) {
            vm.invoiceQueryForm.invoiceQueryDate1 = val;
        },
        orderDate4Change: function (val) {
            vm.invoiceQueryForm.invoiceQueryDate2 = val;
        },
        query: function(){
            vm.displayHeaderCheckbox();
            if(vm.topForm.jvcode==''){
                alert("请先选择JV");
                return;
            }
            var checkKPDate = true;
            $(".checkMsg").remove();
            var qsStartDate = new Date(vm.orderForm.orderDate1);
            var qsEndDate = new Date(vm.orderForm.orderDate2);
            qsStartDate.setMonth(qsStartDate.getMonth() + 12);

            if (qsEndDate.getTime() + 1000 * 60 * 60 * 24 > qsStartDate.getTime()) {
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过1年</div>');
                checkKPDate = false;
            } else if (qsEndDate.getTime() < new Date(vm.orderForm.orderDate1)) {
                $("#requireMsg .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkKPDate = false;
            }
            if (!(checkKPDate)) {
                return;
            }
            var params = {
                orderNo: vm.orderForm.orderNo,
                jvcode: vm.topForm.jvcode,
                orderDate1: vm.orderForm.orderDate1,
                orderDate2: vm.orderForm.orderDate2
            };
            this.listLoading = true;
            this.$http.post(baseURL + 'fixed/match/orderList',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function(res){
                    vm.orderData = res.body.orderList;
                    this.listLoading = false;

            });
            vm.displayHeaderCheckbox();
        },
        handleSelection:function(selection, row){
            //获取选中的订单号
            var orderNo=row.orderNo;
            for (var i=0;i<vm.orderData.length;i++){
                if(vm.orderData[i].orderNo==orderNo){
                    this.$refs.orderDataMultipleTable.toggleRowSelection(vm.orderData[i],true );
                }else{
                    this.$refs.orderDataMultipleTable.toggleRowSelection(vm.orderData[i],false );
                }
            }
        },
        handleSelectionChange: function(val) {
            this.multipleSelection = val;
            this.totalOrderCount = val.length;
            var sum = 0.00;
            for(var i=0;i<val.length;i++){
                sum += val[i].amount;
            }
            this.totalOrderAmount = returnFloat(sum);
        },
        handleSelectionChange2: function(val) {
            vm.zpxzOptionList=val;
        },
        formatDate: function(row, column, cellValue, index){
            if(cellValue==null || cellValue==''){
                return '';
            }
            return cellValue.substring(0, 10);
        },
        getInvoiceTotal: function(){
            this.totalInvoiceCount = this.invoiceData.length;
            var sum = 0.00;
            for(var i=0;i<this.invoiceData.length;i++){
                sum += Number(this.invoiceData[i].totalAmount);
            }
            this.totalInvoiceAmount = returnFloat(sum);
        },
        closezyInvoiceDialog:function() {
            vm.invoiceQueryForm.invoiceNo='';
            vm.invoiceQueryData= [];
            vm.zyInvoiceDialog = false;
            vm.invoiceQueryForm.invoiceQueryDate1=new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-01";
            vm.invoiceQueryForm.invoiceQueryDate2=new Date().getFullYear()+"-"+format2(new Date().getMonth()+1)+"-"+format2(new Date().getDate());
        },
        closeptInvoiceDialog:function() {
            vm.invoiceForm.invoiceCode = '';
            vm.invoiceForm.invoiceNo = '';
            vm.invoiceForm.invoiceDate = '';
            vm.invoiceForm.invoiceType = '';
            vm.invoiceForm.invoiceAmount = '';
            vm.invoiceForm.checkCode = '';
            vm.invoiceForm.totalAmount = '';
            vm.invoiceForm.taxAmount = '';
            vm.invoiceForm.taxRate = '';
            vm.invoiceForm.id = '';
            vm.invoiceForm.sourceSystem = '';


            vm.ptInvoiceDialog = false
        },
        addInvoiceQuery:function() {
            vm.invoiceQueryData= [];
            vm.zyInvoiceDialog = false
            vm.zpxzOptionList

            if(vm.zpxzOptionList.length>0){
                var invoiceList=vm.zpxzOptionList
                for(var j = 0,len = invoiceList.length; j < len; j++){
                    var invoiceObject=invoiceList[j];
                    var invoice = {};
                    invoice.id = invoiceObject.id;
                    invoice.invoiceCode = invoiceObject.invoiceCode;
                    invoice.invoiceNo = invoiceObject.invoiceNo;
                    invoice.invoiceType = invoiceObject.invoiceType;
                    invoice.invoiceDate = invoiceObject.invoiceDate;
                    invoice.invoiceAmount = invoiceObject.invoiceAmount;
                    invoice.totalAmount = invoiceObject.totalAmount;
                    invoice.taxAmount = invoiceObject.taxAmount;
                    invoice.taxRate = invoiceObject.taxRate;
                    invoice.gfTaxNo = invoiceObject.gfTaxNo;
                    invoice.xfName= invoiceObject.xfName;
                    invoice.checkCode = invoiceObject.checkCode;
                    invoice.sourceSystem = invoiceObject.sourceSystem;
                    vm.invoiceData.push(invoice);

                }
            }
            vm.getInvoiceTotal()

        },
        addInvoice: function(isExist){
            if(vm.invoiceForm.invoiceCode==null || vm.invoiceForm.invoiceCode==''){
                alert("发票代码不能为空！");
                return;
            }else{
                if(getFplx(vm.invoiceForm.invoiceCode)!='04'){
                    alert("请录入增值税普通发票！");
                    return;
                }
            }
            if(vm.invoiceForm.invoiceNo==null || vm.invoiceForm.invoiceNo==''){
                alert("发票号码不能为空！");
                return;
            }
            if(vm.invoiceForm.invoiceDate==null || vm.invoiceForm.invoiceDate==''){
                alert("开票日期不能为空！");
                return;
            }else{
                var qsStartDate = new Date(vm.invoiceForm.invoiceDate);
                qsStartDate.setMonth(qsStartDate.getMonth() + 12);
                if (new Date().getTime() + 1000 * 60 * 60 * 24 > qsStartDate.getTime()) {
                    alert("日期选择不可超过1年");
                   return;
                }
            }
            if (vm.invoiceForm.invoiceType != '' && vm.invoiceForm.invoiceType != '01' && vm.invoiceForm.invoiceType != '03') {
                if (vm.invoiceForm.checkCode == null || vm.invoiceForm.checkCode == '') {
                    alert("校验码不能为空！");
                    return;
                }
            }
            if(vm.invoiceForm.invoiceAmount==null || vm.invoiceForm.invoiceAmount==''){
                alert("金额不能为空！");
                return;
            }else{
                var re = /^\d+(?=\.{0,1}\d+$|$)/;
                var amount=vm.invoiceForm.invoiceAmount;
                    if (!re.test(amount)) {
                        vm.invoiceForm.invoiceAmount = "";
                        alert("请输入整数或小数");
                        return;
                    }
            }
            if(vm.invoiceForm.totalAmount==null || vm.invoiceForm.totalAmount==''){
                alert("价税合计不能为空！");
                return;
            }else{
                var re = /^\d+(?=\.{0,1}\d+$|$)/;
                var amount=vm.invoiceForm.totalAmount;
                if (!re.test(amount)) {
                    vm.invoiceForm.totalAmount = "";
                    alert("请输入整数或小数");
                    return;
                }
            }
            if(vm.invoiceForm.taxAmount==null || vm.invoiceForm.taxAmount===''){
                alert("税额不能为空！");
                return;
            }else{
                var re = /^\d+(?=\.{0,1}\d+$|$)/;
                var amount=vm.invoiceForm.taxAmount;
                if (!re.test(amount)) {
                    vm.invoiceForm.taxAmount = "";
                    alert("请输入整数或小数");
                    return;
                }
            }
            if(!isExist) {
                if (vm.invoiceForm.taxRate==null || vm.invoiceForm.taxRate==='') {
                    alert("税率不能为空！");
                    return;
                }

               var taxRateArry=['0','3','6','13','10','11','16','17'];
                if(taxRateArry.indexOf(vm.invoiceForm.taxRate+'')==-1){
                    alert("税率不可输入不符合国税局给出的税率");
                    return;
                }
            }
            // var d1 = new Date(vm.invoiceForm.invoiceDate);
            // var t = (new Date()-d1)/(1000*60*60*24);
            // if(t>330){
            //     alert("发票逾期,无法匹配!");
            //     return;
            // }
             if(vm.invoiceForm.invoiceType!='04'){
                alert("该发票增值税普通发票，请选择增值税专用发票");
                return;
            }

            var invoice = {};
            invoice.isExist = isExist;
            invoice.id = vm.invoiceForm.id;
            invoice.invoiceCode = vm.invoiceForm.invoiceCode;
            invoice.invoiceNo = vm.invoiceForm.invoiceNo;
            invoice.invoiceType = vm.invoiceForm.invoiceType;
            invoice.invoiceDate = vm.invoiceForm.invoiceDate;
            invoice.invoiceAmount = vm.invoiceForm.invoiceAmount;
            invoice.totalAmount = vm.invoiceForm.totalAmount;
            invoice.taxAmount = vm.invoiceForm.taxAmount;
            invoice.taxRate = vm.invoiceForm.taxRate;
            invoice.gfTaxNo = taxNo;
            invoice.xfName = vm.invoiceForm.xfName;;
            invoice.checkCode = vm.invoiceForm.checkCode;
            invoice.sourceSystem=vm.invoiceForm.sourceSystem;





            vm.invoiceData.push(invoice);
            this.$set(this.invoiceForm,'invoiceCode','');
            this.$set(this.invoiceForm,'invoiceNo','');
            this.$set(this.invoiceForm,'invoiceType','');
            this.$set(this.invoiceForm,'invoiceDate','');
            this.$set(this.invoiceForm,'checkCode','');
            this.$set(this.invoiceForm,'invoiceAmount','');
            this.$set(this.invoiceForm,'totalAmount','');
            this.$set(this.invoiceForm,'taxAmount','');
            this.$set(this.invoiceForm,'taxRate','');
            this.$set(this.invoiceForm,'id','');
            this.$set(this.invoiceForm,'sourceSystem','');
            this.getInvoiceTotal();
            vm.ptInvoiceDialog = false
        },
        changeItem:function (val) {
            var invoice = vm.invoiceForm.invoiceCode;
            var invoiceType = getFplx(invoice);
            if (invoiceType == null || invoiceType == ''||invoiceType!='04') {
                alert('请输入正确的发票代码！');
                if (vm.invoiceForm.id != '') {
                    vm.invoiceForm.invoiceCode = '';
                    vm.invoiceForm.invoiceNo = '';
                    vm.invoiceForm.invoiceDate = '';
                    vm.invoiceForm.invoiceType = '';
                    vm.invoiceForm.invoiceAmount = '';
                    vm.invoiceForm.checkCode = '';
                    vm.invoiceForm.totalAmount = '';
                    vm.invoiceForm.taxAmount = '';
                    vm.invoiceForm.taxRate = '';
                    vm.invoiceForm.id = '';
                    vm.invoiceForm.sourceSystem = '';
                } else {
                    vm.invoiceForm.invoiceCode = '';
                }
                return;
            }
            //根据输入的代码解析出的发票类型对金额或校验码进行操作
            if (invoiceType == "01" || invoiceType == "03") {
                document.getElementById("item-checkCode").style.display = "none";
            } else {
                document.getElementById("item-checkCode").style.display = "block";
            }
            vm.invoiceForm.invoiceType = invoiceType;
            if(vm.invoiceForm.invoiceNo!=''){
                this.searchInvoice();
            }
        },
        validAmount:function(){
            var re = /^\d+(?=\.{0,1}\d+$|$)/;
            var amount=vm.invoiceForm.invoiceAmount;
            if (!re.test(amount)) {
                vm.invoiceForm.invoiceAmount = "";
                alert("请输入整数或小数");
                return;
            }
        },
        validTaxAmount:function(){
            var re = /^\d+(?=\.{0,1}\d+$|$)/;
            var amount=vm.invoiceForm.taxAmount;
            if (!re.test(amount)) {
                vm.invoiceForm.taxAmount = "";
                alert("请输入整数或小数");
                return;
            }
           var invoiceAmount = vm.invoiceForm.invoiceAmount;
            var tax = vm.invoiceForm.taxRate;
            if(tax==''){
                vm.invoiceForm.taxAmount = "";
                alert("请先选择税率");
                return;
            }
            if(eval(amount)!=eval((invoiceAmount*(tax/100)).toFixed(2))){
                alert("请核对金额，税额，税率是否正确");
                vm.invoiceForm.taxAmount='';
                return;
            }
        },
        validTotalAmount:function(){
            var re = /^\d+(?=\.{0,1}\d+$|$)/;
            var amount=vm.invoiceForm.totalAmount;
            if (!re.test(amount)) {
                vm.invoiceForm.totalAmount = "";
                alert("请输入整数或小数");
                return;
            }
            var invoiceAmount = vm.invoiceForm.invoiceAmount;
            var taxAmount = vm.invoiceForm.taxAmount;
            if(invoiceAmount==''||taxAmount==''){
                vm.invoiceForm.totalAmount = "";
                alert("请核对金额，税额是否输入");
                return;
            }
            if(eval(amount)!= eval(eval(invoiceAmount)+eval(taxAmount))){
                vm.invoiceForm.totalAmount = "";
                alert("请核对价税合计是否输入正确");
                return;
            }
        },
        searchInvoice: function () {
            //检查下方列表内，有没有该代码号码的内容
            var cars=vm.invoiceData;
            for (var i=0;i<cars.length;i++){
                if(cars[i].invoiceCode==vm.invoiceForm.invoiceCode&&cars[i].invoiceNo==vm.invoiceForm.invoiceNo){
                    alert("该发票已录入列表，请删除后重新录入");
                    this.closeptInvoiceDialog();
                }
            }
            var params = {
                invoiceCode: vm.invoiceForm.invoiceCode,
                invoiceNo: vm.invoiceForm.invoiceNo
            };
            this.$http.post(baseURL + 'fixed/match/searchInvoice',
                params,
                {
                    'headers': {
                        "token": token
                    }
                }).then(function (res) {
                var info = res.body.invoiceInfo;
                if(info!=null) {

                    if (info.invoiceType != '04' ) {
                        alert("请录入增值税普通发票！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if (info.invoiceStatus != '0' ) {
                        alert("发票状态异常，无法匹配！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if (info.sap!=''&&info.sap!=null&&info.sap!='0'&&info.sap!='3') {
                        alert("SAP待确认或已成功，无法匹配！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if (info.flowType != null && info.flowType != '') {
                        alert("此发票已有匹配！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if(info.gfTaxNo!=taxNo){
                        alert("所选JV与发票抬头不一致！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if ((info.taxRate == null || info.taxRate === '') && info.detailYesorno == '1') {
                        alert("多税率发票无法匹配！");
                        this.invoiceForm.invoiceCode = '';
                        this.invoiceForm.invoiceNo = '';
                        return;
                    }
                    if (info.taxRate==null || info.taxRate==='') {
                        alert("税率不能为空！");
                        return;
                    }

                    var taxRateArry=['0','3','6','13','10','11','16','17'];
                    if(taxRateArry.indexOf(info.taxRate+'')==-1){
                        alert("税率不可输入不符合国税局给出的税率");
                        return;
                    }



                    this.invoiceForm.id = info.id;
                    this.invoiceForm.sourceSystem = info.sourceSystem;
                    this.invoiceForm.xfName = info.xfName;
                    if(info.sourceSystem==1){
                        this.invoiceForm.invoiceDate = info.invoiceDate;
                        this.invoiceForm.invoiceAmount = info.invoiceAmount;
                        this.invoiceForm.checkCode = info.checkCode;
                        this.invoiceForm.totalAmount = info.totalAmount;
                        this.invoiceForm.taxAmount = info.taxAmount;
                        this.invoiceForm.taxRate = info.taxRate;
                         this.addInvoice(true);
                    }



                }else{
                    this.invoiceForm.xfName = vm.topForm.venderName;
                }
            });
        },
        deleteInvoice: function (tableData, row) {
            tableData.splice(tableData.indexOf(row), 1);
            this.getInvoiceTotal();
        },
        deleteFile: function(tableData, row){
            tableData.splice(tableData.indexOf(row), 1);
        },
        submitAll: function (){
            // vm.submitLoading = true;

            if(vm.totalOrderCount==0){
                alert("请选择订单！");

                return;
            }
            if(vm.totalInvoiceCount==0){
                alert("请填写发票！");

                return;
            }
            if(vm.totalOrderCount>1 && vm.totalInvoiceCount>1){
                alert("多张订单不能匹配多张发票！");

                return;
            }

            var invoiceDataBoolean=0;
            var invoiceXfNameBooleam=0;
            for(var i =0; i<vm.invoiceData.length;i++){
                var d1=vm.invoiceData[i].invoiceDate;
                d1=d1.split(" ")[0]+" 23:59:59"
                var t = (new Date()-new Date(d1))/(1000*60*60*24);
                if(t>300){
                    invoiceDataBoolean++;
                }
                if(vm.topForm.venderName!=vm.invoiceData[i].xfName){
                    invoiceXfNameBooleam++;
                }
            }



            if((Math.abs(vm.totalInvoiceAmount*100-vm.totalOrderAmount*100)>100)||invoiceDataBoolean>0){
                vm.matchFalidType=0;
                //匹配失败
                vm.choose='';
                vm.showConfirm = true;

            }
            else if(invoiceXfNameBooleam>0){
                vm.matchFalidType=1;
                //匹配失败
                vm.choose='';
                vm.showConfirm = true;
            }
            else{
                //匹配成功
                var match = {};
                match.jvcode = vm.topForm.jvcode;
                match.venderid = vm.topForm.venderid;
                match.venderName = vm.topForm.venderName;
                match.invoiceAmount = vm.totalInvoiceAmount;
                match.invoiceCount = vm.totalInvoiceCount;
                match.orderAmount = vm.totalOrderAmount;
                match.orderCount = vm.totalOrderCount;
                match.settlementMethod = '0';
                match.orderList = vm.multipleSelection;
                match.invoiceList = vm.invoiceData;
                $.ajax({
                    type:"POST",
                    url:baseURL + 'fixed/match/submitAll',
                    data: JSON.stringify(match),
                    dataType:"json",
                    contentType:"application/json",
                    async: false,
                    cache:false,
                    success:function(r){
                        if(r.code==0) {
                            alert("保存匹配成功");
                            vm.orderData = [];
                            vm.invoiceData = [];
                            vm.totalInvoiceCount=0;
                            vm.totalInvoiceAmount=0;
                        }else{
                            alert("保存匹配失败,请稍后重试")
                        }
                        vm.submitLoading = false;
                    }
                });
            }
        },
        closeConfirm: function(){
            this.showConfirm = false;
        },
        failedConfirm: function(){
            var val =vm.choose;
            if(val=='1'){
                vm.showConfirm = false;
                vm.submitLoading=false;
            }else if(val=='2'){
                vm.showConfirm = false;
                vm.submitLoading=false;
                vm.methodCheck='';
                vm.showMethod = true;
            }else{
                alert("请选择一个后续操作");
            }
        },
        closeMethod: function(){
            this.showMethod = false;
        },
        methodConfirm: function(){
            var val = vm.methodCheck;
            if(!val){
                alert("请选择一种结算方式");
            }else{
                settlementMethod = val;
                vm.showMethod = false;
                vm.showImgWin = true;
            }
        },
        closeImgWin: function(){
            this.showImgWin = false;
        },
        fileConfirm: function(){
            // if(vm.fileData.length==0&&vm.matchFalidType==0){
            //     alert("请上传问题单文件");
            //     return;
            // }
            this.showImgWin = false;
            var match = {};
            match.jvcode = vm.topForm.jvcode;
            match.venderid = vm.topForm.venderid;
            match.venderName = vm.topForm.venderName;
            match.invoiceAmount = vm.totalInvoiceAmount;
            match.invoiceCount = vm.totalInvoiceCount;
            match.orderAmount = vm.totalOrderAmount;
            match.orderCount = vm.totalOrderCount;
            match.settlementMethod = settlementMethod;
            match.orderList = vm.multipleSelection;
            match.invoiceList = vm.invoiceData;
            match.fileList = vm.fileData;
            $.ajax({
                type:"POST",
                url:baseURL + 'fixed/match/submitAll',
                data: JSON.stringify(match),
                dataType:"json",
                contentType:"application/json",
                async: false,
                cache:false,
                success:function(r){
                    if(r.code==0) {
                        alert("已提交问题单");
                        vm.orderData = [];
                        vm.invoiceData = [];
                        vm.fileData = [];
                        vm.totalInvoiceCount=0;
                        vm.totalInvoiceAmount=0;
                    }else{
                        alert("保存匹配失败,请稍后重试");
                        vm.fileData = [];
                    }
                }
            });
            vm.submitLoading = false;
        },
        uploadFile: function () {
            var fileValue = document.getElementById("fileId").value;
            if(fileValue==null || fileValue==''){
                alert("请选择文件");
                return;
            }
            var filename=fileValue;
            var index1=filename.lastIndexOf(".");
            var index2=filename.length;
            var type=filename.substring(index1,index2);
            if(type!='.bpm'&&type!='.jpg'&&type!='.png'&&type!='.pdf'&&type!='.PDF'){
                alert("请选择后缀名为bmp、jpg、png、pdf的文件");
                return;
            }
            var f = document.getElementById("fileId");
            var fileSize = 0;
            var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
            if (isIE && !f.files) {
                var filePath = f.value;
                var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
                var file = fileSystem.GetFile (filePath);
                fileSize = file.Size;
            }else {
                fileSize = f.files[0].size;
            }
            fileSize = Math.round(fileSize/1024/1024*100)/100; //单位为M
            if(fileSize>=5){
                $(f).val('');
                alert("您上传的图片大小超过5M，请重新上传！");
                return false;
            }


            vm.uploadLoading = true;
            var file = document.getElementById("fileId").files[0];
            var formData = new FormData();
            formData.append("file", file);
            if(type!='.bpm'&&type!='.jpg'&&type!='.png'){
                formData.append("fileType", "2");
            }else{
                formData.append("fileType", "1");
            }
            $.ajax({
                url: baseURL + 'fixed/match/uploadFile',
                data: formData,
                type: "POST",
                async: false,
                dataType: "json",
                cache: false,//上传文件无需缓存
                processData: false,//用于对data参数进行序列化处理 这里必须false
                contentType: false, //必须
                success: function (r) {
                    if(r.code=='0') {
                        var data = r.fileEntity;
                        vm.fileData.push(data);
                    }else{
                        alert(r.msg);
                    }
                    vm.uploadLoading = false;
                },
            });
            document.getElementById("fileId").value = '';
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
        rateFormat: function(row, column, cellValue){
            if(cellValue==null){
                return "";
            }
            return String(cellValue)+"%";
        },

       queryQuestionType: function () {

           var theKey='供应商红票问题单类型';

           $.ajax({
               url: baseURL + 'modules/posuopei/queryPart',
               type: "POST",
               contentType: "application/json",
               dataType: "json",
               data:theKey,
               success: function (r) {
                   if (r.code == 0) {
                       var gfs = [];
                       for (var i = 0; i < r.List.length; i++) {
                           var gf = {};
                           gf.label = r.List[i].dictname;
                           gf.value = r.List[i].dictcode;
                           gf.status = r.List[i].dictcode;
                           gfs.push(gf);
                       }
                       vm.questionTypeArray = gfs;
                   }
               }
           });
       },
        queryTaxRate: function () {

            var theKey='税率';

            $.ajax({
                url: baseURL + 'modules/posuopei/queryPart',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:theKey,
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.label = r.List[i].dictname;
                            gf.value = r.List[i].dictcode;
                            gfs.push(gf);
                        }
                        vm.taxRateArray = gfs;
                        if(vm.taxRateArray.length>0){
                            vm.invoicequery.taxRate=vm.taxRateArray[0].value;

                        }

                    }
                }
            });
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
function returnFloat(value){
    var xsd=value.toString().split(".");
    if(xsd.length==1){
        value=value.toString()+".00";
        return value;
    }
    if(xsd.length>1){
        if(xsd[1].length<2){
            value=value.toString()+"0";
        }
        return value;
    }
}
function getFplx(fpdm) {
    var fpdmlist=new Array("144031539110","131001570151","133011501118","111001571071");
    var  fplx="";
    if (fpdm.length==12){
        var fplxflag=fpdm.substring(7,8);

        for(var i =0; i<fpdmlist.length;i++){
            if(fpdm==fpdmlist[i]){
                fplx="10";
                break;
            }
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="11") {
            fplx="10";
        }
        if (fpdm.substring(0,1)=="0" && fpdm.substring(10,12)=="12") {
            fplx="14";
        }
        if (fpdm.substring(0,1)=="0" && (fpdm.substring(10,12)=="06"|| fpdm.substring(10,12)=="07")) {
            //判断是否为卷式发票  第1位为0且第11-12位为06或07
            fplx="11";
        }
        if(fpdm.substring(0,1)=="0"&&(fpdm.substring(10,12)=="04"|| fpdm.substring(10,12)=="05")){
            fplx="04"
        }
        if (fplxflag=="2" && fpdm.substring(0,1)!="0") {
            fplx="03";
        }

    }else if(fpdm.length==10){
        var fplxflag=fpdm.substring(7,8);
        if(fplxflag=="1"||fplxflag=="5"){
            fplx="01";
        }else if(fplxflag=="6"||fplxflag=="3"){
            fplx="04";
        }else if(fplxflag=="7"||fplxflag=="2"){
            fplx="02";
        }

    }
    return fplx;
}