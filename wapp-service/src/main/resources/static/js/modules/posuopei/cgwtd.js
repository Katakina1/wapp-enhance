
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var vm = new Vue({
    el: '#rrapp',
    data: {
        causeRules1:false,
        invoiceDateOptions1:{},
        invoiceDateOptions2:{},
        uploadLoading:false,
        listLoading: false,
        listLoading1: false,
        listLoading2: false,
        listLoading3: false,
        listLoading4: false,
        listLoading5: false,
        listLoading6:false,

        pageCount: 0,
        options: [],
        currentPage: 1,
        pagerCount: PAGE_PARENT.PAGER_COUNT,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        totalPage:1,
        total: 0,
        forms:{
            usercode:null,
            username:null,
            problemStream:null,
            questionDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            questionDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
        },
        cgwtdforms:{
            id:null,
            partition:null,
            purchaser:null,
            jvcode:null,
            city:null,
            usercode:null,
            username:null,
            telephone:null,
            department:null,
            invoiceNo:null,
            invoiceDate:null,
            questionType:null,
            totalAmount:null,
            problemCause:null,
            description:null,
            storeNbr:null,
            reason:null
        },
        part:[],
        questionTypes:[],
        citys:[],
        Cause:[],
        readonly:true,
        questionPaperData:[],
        questionPapers:false,
        questionPaper:false,
        questionPaperFile:false,
        questionPaper1:false,
        trackInvoiceStatusDialog:false,
        questionPaperFileList:[],
        claimChangeData:[],
        poChangeData:[],
        otherData:[],
        poDiscountData:[],
        countChangeData:[],
        tableData5: [],
        startDateOptions:{},
        endDateOptions:{},
        addClaimChangeDataShow:false,
        addPoDiscountShow:false,
        addOtherShow:false,
        addPoChangeDataShow:false,
        addCountChangeDataShow:false,
        addCountChangeDataShow2:false,
        addPoChangeForm:{
            poCode:null,
            goodsNo:null,
            systemAmount:null,
            vendorAmount:null,
            number:null,
            difference:null
        },
        addCountChangeForm:{
            poCode:null,
            claimno:null,
            receiptid:null,
            goodsNo:null,
            systemAmount:null,
            vendorNumber:null,
            systemNumber:null,
            numberDifference:null,
            amountDifference:null

        },
        addClaimChangeForm:{
            poCode:null,
            claimno:null,
            goodsNo:null,
            systemAmount:null,
            vendorAmount:null,
            number:null,
            difference:null
        },
        addPoDiscountForm:{
            poCode:null,
            goodsNo:null,
            difference:null
        },
        addOtherForm:{
            poCode:null,
            claimno:null,
            goodsNo:null,
            difference:null
        }
    },
    invoicequery:{
        checkNo:null,
        invoiceCode:null,
        invoiceNo:null,
        invoiceAmount:null,
        totalAmount:null,
        taxAmount:null,
        taxRate:null,
        // invoiceDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
        invoiceDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01" ,
    },
    mounted: function () {
        this.queryTaxRate();
        this.queryPart();
        this.queryCity();
        this.queryDetails();
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
        this.invoiceDateOptions1 = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };

        this.invoiceDateOptions2 = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
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
        questionPaperFileCancel:function () {
            vm.questionPaperFile=false;
        },
        findAll: function (currentPage) {

            validateDateTypeA(vm.forms.questionDateStart,vm.forms.questionDateEnd);

            var params = {
                page: currentPage,
                limit: this.pageSize,
                venderid:vm.forms.usercode,
                problemStream:vm.forms.problemStream,
                questionDateStart:vm.forms.questionDateStart,
                questionDateEnd:vm.forms.questionDateEnd

 
            };
            vm.listLoading = true;
            this.$http.post(baseURL + sysUrl.questionPaperQuery,
                params, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                this.total = response.data.page.totalCount;
                this.pageCount = response.data.page.totalPage;
                this.totalPage = response.data.page.totalPage;
                this.questionPaperData = [];
                flag = true;
                for (var key in response.data.page.list) {
                    this.$set(this.questionPaperData, key, response.data.page.list[key]);
                }
                this.listLoading = false;
            }).catch(function (response) {
                alert(response.data.msg);
                this.listLoading = false;
            });
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);


        },
        questionPaperCancel:function () {
            vm.questionPaper1=false;
        },
        questionPaperCancels:function () {
            vm.questionPapers=false;
        },
        blurAmount:function(){
            var a=0;
            var b=0;
            var c=0;
            a=vm.addPoChangeForm.number;
            b=vm.addPoChangeForm.vendorAmount;
            c=vm.addPoChangeForm.systemAmount;
            var d=(b-c)*a;
            vm.addPoChangeForm.difference=parseFloat(d).toFixed(2);
        },
        blurCaAmount:function(){
            var a=0;
            var b=0;
            var c=0;
            a=vm.addClaimChangeForm.number;
            b=vm.addClaimChangeForm.systemAmount;
            c=vm.addClaimChangeForm.vendorAmount;
            var d=(b-c)*a;
            vm.addClaimChangeForm.difference=parseFloat(d).toFixed(2);
        },
        blurNum:function(){
            var a=0;
            var b=0;
            var c=0;
            a=vm.addCountChangeForm.vendorNumber;
            b=vm.addCountChangeForm.systemNumber;
            c=vm.addCountChangeForm.systemAmount;
            var d=a-b;
            vm.addCountChangeForm.numberDifference=d;
            var e=(a-b)*c;
            vm.addCountChangeForm.amountDifference=parseFloat(e).toFixed(2);

        },
        viewQuestionPaper:function (row) {
            vm.questionPaper1=true;
            vm.cgwtdforms.id=row.id;
            vm.cgwtdforms.partition=row.partition;
            vm.cgwtdforms.purchaser=row.purchaser;
            vm.cgwtdforms.jvcode=row.jvcode;
            vm.cgwtdforms.city=row.cityCode;
            vm.cgwtdforms.usercode=row.usercode;
            vm.cgwtdforms.username=row.username;
            vm.cgwtdforms.telephone=row.telephone;
            vm.cgwtdforms.department=row.department;
            vm.cgwtdforms.invoiceNo=row.invoiceNo;
            vm.cgwtdforms.invoiceDate=row.invoiceDate;
            vm.cgwtdforms.questionType=row.questionType;
            vm.cgwtdforms.totalAmount=parseFloat(row.totalAmount).toFixed(2);
            vm.cgwtdforms.problemCause=row.problemCause;
            vm.cgwtdforms.description=row.description;
            vm.cgwtdforms.toreNbr=row.toreNbr;
            vm.cgwtdforms.reason=row.unPassReason;
            if(row.problemCause=="100401"){
                vm.causeRules1=true;
            }else if(row.problemCause=="100402"){
                vm.causeRules1=false;
            }
            vm.queryProblemCause(vm.cgwtdforms.questionType);
            vm.queryQuestionType(vm.cgwtdforms.partition);
            this.getFile(row);
            this.cgwtdDetail(row);


        },
        deleteQueryPart: function (row) {
            var id={
                id:row.id
            };
            $.ajax({
                url: baseURL + 'modules/posuopei/questionPaper/deleteQuestionPaper',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:JSON.stringify(id),
                success: function (r) {
                if(r.code==0){
                    alert("撤销成功！");
                    vm.query();
                }
                }
            });
        },
        trackInvoiceStatusClose:function(){
            vm.trackInvoiceStatusDialog=false;
        },
        //跟踪发票状态
        trackInvoiceStatus:function(row){
            vm.trackInvoiceStatusDialog = true;
            vm.tableData5= [];
            if(row.createdDate!=null ) {
                vm.tableData5.push({
                    invoiceDate: row.createdDate,
                    invoiceStatus: '提交申请'
                });
            }

            if(row.rejectDate!=null) {
                vm.tableData5.push({
                    invoiceDate: row.rejectDate,
                    invoiceStatus: '驳回申请'
                });
            }

            if(row.checkDate!=null) {
                vm.tableData5.push({
                    invoiceDate: row.checkDate,
                    invoiceStatus: checkStatus(row.checkstatus)
                });
            }

            if(row.replyDate!=null) {
                vm.tableData5.push({
                    invoiceDate: row.replyDate,
                    invoiceStatus:replyStatus(row.checkstatus)
                });
            }
        },
        deleteFile: function(tableData, row){
            tableData.splice(tableData.indexOf(row), 1);
        },
        queryPart: function () {

            var theKey='分区';

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
                            gf.value = r.List[i].dictcode;
                            gf.label = r.List[i].dictname;
                            gfs.push(gf);
                        }
                        vm.part = gfs;
                        if(vm.part.length>0){
                            vm.cgwtdforms.partition=vm.part[0].value;
                            vm.queryQuestionType(vm.cgwtdforms.partition);
                        }
                        $(".po").addClass("hideItem");
                        $(".count").addClass("hideItem");
                        $(".claim").addClass("hideItem");

                    }
                }
            });
        },
        queryQuestionType: function (key) {
            var theKey=key;

            $.ajax({
                url: baseURL + 'modules/posuopei/dicdeta',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:theKey,
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.value = r.List[i].dictcode;
                            gf.label = r.List[i].dictname;
                            gfs.push(gf);
                        }
                        vm.questionTypes = gfs;
                        if(vm.questionTypes.length>0){
                          // vm.cgwtdforms.questionType=vm.questionTypes[0].value;
                            vm.queryProblemCause(vm.cgwtdforms.questionType);
                            if("2001"===vm.cgwtdforms.questionType){
                                $(".po").addClass("hideItem");
                                $(".count").addClass("hideItem");
                                $(".claim").removeClass("hideItem");
                                $(".other").addClass("hideItem");
                            }else if("2002"===vm.cgwtdforms.questionType){
                                $(".po").removeClass("hideItem");
                                $(".count").addClass("hideItem");
                                $(".claim").addClass("hideItem")
                                $(".other").addClass("hideItem");
                            }else if("2004"===vm.cgwtdforms.questionType){
                                $(".po").addClass("hideItem");
                                $(".count").removeClass("hideItem");
                                $(".claim").addClass("hideItem");
                                $(".other").addClass("hideItem");
                            }else{
                                $(".po").addClass("hideItem");
                                $(".count").addClass("hideItem");
                                $(".claim").addClass("hideItem");
                                $(".other").removeClass("hideItem");

                            }
                        }else{
                            vm.forms.questionType="";
                            vm.forms.problemCause="";
                            vm.forms.description="";
                            vm.Cause=[];
                        }

                    }
                }
            });
        },
        queryCity: function () {
            $.ajax({
                url: baseURL + 'modules/posuopei/queryCity',
                type: "POST",
                contentType: "application/json",
                dataType: "json",

                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.value = r.List[i].cityCode;
                            gf.value = r.List[i].cityCode;
                            gf.label = r.List[i].city;
                            gfs.push(gf);
                        }
                        vm.citys = gfs;
                        if(vm.citys.length>0){
                            vm.cgwtdforms.city=vm.citys[0].value;

                        }

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
        },
        queryProblemCause: function (value) {

            var theKey=value;

            $.ajax({
                url: baseURL + 'modules/posuopei/dicdeta',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:theKey,
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.value = r.List[i].dictcode;
                            gf.label = r.List[i].dictname;
                            gfs.push(gf);
                        }
                        vm.Cause = gfs;
                        if(vm.Cause.length>0){
                            vm.forms.problemCause=vm.Cause[0].value;
                            vm.changeDescription(vm.forms.problemCause)
                        }else{
                            m.forms.problemCause="";
                            vm.changeDescription(vm.forms.problemCause)
                        }

                    }
                }
            });
        },
        uploadQuestionFile: function () {
            var fileValue = document.getElementById("fileId").value;
            if(fileValue==null || fileValue==''){
                alert("请选择文件");
                return;
            }
            var files=fileValue.split(".");
            if(files[1]=="zip"||files[1]=="exe"||files[1]=="rar"){
                alert("不能上传压缩文件或可执行文件！");
                return;
            }
            var file = document.getElementById("fileId").files[0];
            var maxsize = 10485760;//10M
            if(file!==undefined) {
                var fileSize = file.size;
                if (fileSize > maxsize) {
                    alert("附件大小不能超过10MB!");
                    return false;
                }
            }

            vm.listLoading6 = true;
            document.getElementById("uploadBtn").getElementsByTagName("span")[0].innerHTML = "上传中...";
            var file = document.getElementById("fileId").files[0];
            var formData = new FormData();
            formData.append("file", file);
            // formData.append("fileType", vm.fileForm.fileType);
            $.ajax({
                url: baseURL + 'modules/posuopei/question/uploadFile',
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
                        vm.questionPaperFileList.push(data)
                        alert("上传成功！")
                    }else{
                        alert(r.msg);
                    }
                    vm.listLoading6 = false;
                    document.getElementById("uploadBtn").getElementsByTagName("span")[0].innerHTML = "上传附件";

                },
            });
            document.getElementById("fileId").value = '';
        },
        changeDescription:function (key) {
            if(key==='100101' || key==='100102'){
                vm.forms.description="供应商认为索赔所退上述商品是订单低价进货商品，造成索赔成本差异合计金额为 0.0000 元。请采购确认供应商所述是否属实，是否返还上述差异金额。问题单说明表、索赔单详见附件。*因使用与采购沟通的判断方式无法确认,故发给采购协助确认.*";

            }else if(key==='100201'){
                vm.forms.description= '供应商自述订单中上述商品单价存在差异，成本差异合计金额为 0.0000 元。请采购回复供应商所述是否属实，是否返还上述订单差异。谢谢！问题单说明表、收货报告详见附件。';

            }else if(key==='100301'){
                vm.forms.description="供应商自述订单不应存在***折扣，而我司订单和收货系统均显示存在***折扣，成本差异金额:0.0000 元(扣除手续费折扣前)。请采购确认是否取消**折扣，谢谢！ 问题单说明表、收货报告详见附件。"
            }else if(key=='100401'){
                vm.causeRules1=true;
                vm.forms.description="供应商自述订单下商品实际收货数量为 0.0000 件，但我司收货系统为 0.0000 件,成本差异金额: 0.0000 元(扣除送货手续费后). 请收货办同事确认以下问题：     1.供应商所述是否属实。   2.如属实，请补录并告知订单号及收货号. 问题单说明表、送货单、收货报告详见附件.   3.请联系采购对差异部分补丁下订单，收货定案后告知我处订单号，我处会跟进通知供应商寄票结款"
            }else if(key=='100402'){
                vm.causeRules1=false;
                vm.forms.description="供应商自述索赔下商品实际退货数量为 0.0000 件，但我司系统中显示退货数量为 0.0000 件,成本差异金额: 0.0000 元(扣除送货手续费后). 请收货办同事确认以下问题：     1.供应商所述是否属实。   2.如属实，请补录并告知订单号及收货号. 问题单说明表、送货单、收货报告详见附件."
            }else if(key=='100701'){
                vm.forms.description="供应商自述是无索赔供应商，而我司定案索赔 ，合计金额 0.0000，索赔明细详见附件。通过VM提供的*******查询得知，该供应商确是无索赔供应商。请采购确认是否返还上述索赔金额，如需返还请及时联系门店冲帐。谢谢。问题单说明表、索赔明细表、超市索赔条款清单、索赔财务金额明细（TRAC）详见附件。*因供应商未收到索赔单，故无法提供*"
            }else if(key=='100702'){
                vm.forms.description="供应商自述由于换货时商品单价调整，导致索赔  =与冲账金额相差 0.0000 元，冲账差异明细详见附件。请采购确认供应商所述是否属实，是否返还冲账差异金额。谢谢。问题单说明表、索赔单、索赔冲账差异明细（TRAC）详见附件。"
            }else if(key=='100703'){
                vm.forms.description="供应商自述索赔  不应存在退货手续费，因是直接到门店自行退货的，退货手续费合计金额： 0.0000 元。请采购确认供应商所述是否属实，是否取消上述索赔退货手续费，谢谢。问题单说明表、退货手续费明细表详见附件。"
            }else if(key=='100704'){
                vm.forms.description="供应商自述索赔  不应定案，因其并未与该门店合作，索赔金额： 0.0000 元。 请采购确认以下问题： 1.供应商所述是否属实。 2.如属实，请及时联系门店冲帐，并告之冲帐号。 问题单说明表、TRAC明细、索赔明细见附件。 *因供应商没有收到索赔单故无法提供，提供财务系统查询明细供参考。*"
            }else if(key=='100705'){
                vm.forms.description="供应商自述税务登记注册类型为“个体户”，是小规模纳税人，只能在税务局代开4%的增值税发票，但我司订单却显示为增值税税率13%和17%，供应商要求更改订单税率，并愿意承担变更税率引起的损失，请采购确认以下问题：1.供应商所述是否属实? 2.如果属实,请更改订单税率,并告知更改时间.3.是否更改已送货订单成本,如更改,更改后的成本是多少?*"
            }else if(key=='100706'){
                vm.forms.description="供应商自述在与我司签署合同时为增值税一般纳税人，现被税局确认为增值税小规模纳税人，只能申请税局代开税率为3%的增值税发票，供应商要求更改订单税率并愿意承担我司由此产生的税金损失。请采购确认以下问题： 1.是否更改已送货订单成本？如更改,更改后的成本是多少? 2.由于税率变动可能会对我司造成税金损失,为避免以后订单税率下错，请回复订单税率更改时间?谢谢!*"
            }else if(key=='100707'){
                vm.forms.description="供应商自述订单  下给V#******(********),由此供应商送货和结款; 但我司系统将该送货定案在供应商V#******下。请采购确认供应商是否属实?是否需要财务部调帐?谢谢!问题单说明表、订单、送货单、收货报告详见附件。"
            }else if(key=='100708'){
                vm.forms.description="收到财务部涉税问题单，该供应商与我司签署合同时是小规模纳税人，现已于2008年9月被税局认定为一般纳税人。请采购及时更改订单税率，并请回复更改时间。谢谢。涉税问题单详见附件。"
            }else if(key=='100709'){
                vm.forms.description="供应商自述索赔  是订单  收货***的错误收货所做冲账。索赔合计金额： 0.0000 元。 经查，在财务系统内订单***与索赔***成本金额一致. 请门店确认供应商所述是否属实，如属实，请扼要说明冲账原因。 问题单说明表、财务系统明细（trac）、索赔明细见附件。"
            }else if(key=='100801'){
                vm.forms.description="供应商认为索赔  所退上述商品是订单 1234567890 低价进货商品，造成索赔成本差异合计金额为 100.0000 元。WM财务系统内，上述商品前期确有低价格送货。请确认是否返还上述差异金额？问题单说明表、索赔单、MISINFO详见附件."
            }else{
                vm.forms.description=""
            }

        },
        cgwtdDetail:function (row) {
            vm.questionPaper=true;
            if("2001"===row.questionType){

                vm.listLoading1 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.claimChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.claimChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").removeClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading1 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading1 = false;
                });
            }else if("2002"===row.questionType){


                vm.listLoading2=true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.poChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").removeClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading2 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading2 = false;
                });
            }else if("2004"===row.questionType){

                vm.listLoading3 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.countChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.countChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").removeClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading3 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading3 = false;
                });
            }else if("2003"===row.questionType){

                vm.listLoading4 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.poDiscountData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poDiscountData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").removeClass("hideItem");
                    $(".other").addClass("hideItem")
                    this.listLoading4 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading4 = false;
                });
            }else{


                vm.listLoading5 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.otherData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.otherData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").removeClass("hideItem");
                    this.listLoading5 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading5 = false;
                });

            }

        },

        addClaimChangeData:function () {
            vm.addClaimChangeDataShow=true;
        },
        addPoDiscountData:function () {
            vm.addPoDiscountShow=true;
        },
        addCountChangeData:function () {
            if(vm.causeRules1){
                vm.addCountChangeDataShow=true;
            }else{
                vm.addCountChangeDataShow2=true;
            }
        },
        addPoChangeData:function () {
            vm.addPoChangeDataShow=true;
        },
        addOtherData:function () {
            vm.addOtherShow=true;
        },
        chexiao:function (id,reason) {
            result={
                id:id,
                result:0,
                reason:reason
            }
            $.ajax({
                        url: baseURL + 'modules/posuopei/question/chexiao',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(result),
                        success: function (r) {
                            if (r.code == 0) {
                            }
                        }
                    });
        },
        countChangeSave:function (formName) {
            var flagwe=true;
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        if(vm.cgwtdforms.partition==='1005'){
                            if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                                flagwe=false;
                            }
                        }
                        if(vm.countChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }
                        if(flagwe){
                            if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var countSave={
                                    id:vm.cgwtdforms.id,
                                    partition:vm.cgwtdforms.partition,
                                    purchaser:vm.cgwtdforms.purchaser,
                                    jvcode:vm.cgwtdforms.jvcode,
                                    city:vm.cgwtdforms.city,
                                    usercode:vm.cgwtdforms.usercode,
                                    username:vm.cgwtdforms.username,
                                    telephone:vm.cgwtdforms.telephone,
                                    department:vm.cgwtdforms.department,
                                    invoiceNo:vm.cgwtdforms.invoiceNo,
                                    invoiceDate:vm.cgwtdforms.invoiceDate,
                                    questionType:vm.cgwtdforms.questionType,
                                    totalAmount:vm.cgwtdforms.totalAmount,
                                    problemCause:vm.cgwtdforms.problemCause,
                                    description:vm.cgwtdforms.description,
                                    countList:vm.countChangeData,
                                    storeNbr:vm.cgwtdforms.storeNbr,
                                    fileList:vm.questionPaperFileList
                                };
                                $.ajax({
                                    url:baseURL + 'modules/posuopei/question/save',
                                    type:"POST",
                                    contentType: "application/json",
                                    dataType: "json",
                                    data:JSON.stringify(countSave),
                                    success:function (r) {
                                        // vm.$refs[formName].resetFields();
                                        // if(r.orgEntity != null){
                                        //     vm.forms.usercode=r.orgEntity.usercode;
                                        // }
                                        // if(r.orgEntity != null){
                                        //     vm.forms.username=r.orgEntity.orgname;
                                        // }
                                        if(r.code==0){
                                            alert(r.msg)
                                            vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                            vm.questionPaper1=false;
                                            vm.query();

                                        }else{
                                            alert(r.msg);
                                        }
                                    }
                                });
                            }
                        }else{
                            alert("店号不能为空！");
                        }

                    }else{
                        return false;
                    }


                }
            );

        },
        claimChangeSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {

                        var flag=true;
                        if(vm.claimChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }
                        if(vm.cgwtdforms.partition==='1005'){
                            if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                                flag=false;
                            }
                        }
                        if(flag){
                            if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var claimSave={
                                    id:vm.cgwtdforms.id,
                                    partition:vm.cgwtdforms.partition,
                                    purchaser:vm.cgwtdforms.purchaser,
                                    jvcode:vm.cgwtdforms.jvcode,
                                    city:vm.cgwtdforms.city,
                                    usercode:vm.cgwtdforms.usercode,
                                    username:vm.cgwtdforms.username,
                                    telephone:vm.cgwtdforms.telephone,
                                    department:vm.cgwtdforms.department,
                                    invoiceNo:vm.cgwtdforms.invoiceNo,
                                    invoiceDate:vm.cgwtdforms.invoiceDate,
                                    questionType:vm.cgwtdforms.questionType,
                                    totalAmount:vm.cgwtdforms.totalAmount,
                                    problemCause:vm.cgwtdforms.problemCause,
                                    description:vm.cgwtdforms.description,
                                    claimList:vm.claimChangeData,
                                    storeNbr:vm.cgwtdforms.storeNbr,
                                    fileList:vm.questionPaperFileList
                                };
                                $.ajax({
                                    url:baseURL + 'modules/posuopei/question/save',
                                    type:"POST",
                                    contentType: "application/json",
                                    dataType: "json",
                                    data:JSON.stringify(claimSave),
                                    success:function (r) {
                                        // vm.$refs[formName].resetFields();
                                        // if(r.orgEntity != null){
                                        //     vm.forms.usercode=r.orgEntity.usercode;
                                        // }
                                        // if(r.orgEntity != null){
                                        //     vm.forms.username=r.orgEntity.orgname;
                                        // }
                                        if(r.code==0){
                                            alert(r.msg)
                                            vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                            vm.questionPaper1=false;
                                            vm.query();

                                        }else{
                                            alert(r.msg);
                                        }
                                    }
                                });
                            }
                        }else{
                            alert("店号不能为空！");
                        }

                    }else{
                        return false;
                    }
                }
            );


        },
        otherChangeSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        var flag=true;
                        if(vm.cgwtdforms.partition==='1005'){
                            if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                                flag=false;
                            }
                        }
                        if(flag){
                            if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var otherSave={
                                    id:vm.cgwtdforms.id,
                                    partition:vm.cgwtdforms.partition,
                                    purchaser:vm.cgwtdforms.purchaser,
                                    jvcode:vm.cgwtdforms.jvcode,
                                    city:vm.cgwtdforms.city,
                                    usercode:vm.cgwtdforms.usercode,
                                    username:vm.cgwtdforms.username,
                                    telephone:vm.cgwtdforms.telephone,
                                    department:vm.cgwtdforms.department,
                                    invoiceNo:vm.cgwtdforms.invoiceNo,
                                    invoiceDate:vm.cgwtdforms.invoiceDate,
                                    questionType:vm.cgwtdforms.questionType,
                                    totalAmount:vm.cgwtdforms.totalAmount,
                                    problemCause:vm.cgwtdforms.problemCause,
                                    description:vm.cgwtdforms.description,
                                    storeNbr:vm.cgwtdforms.storeNbr,
                                    fileList:vm.questionPaperFileList
                                };
                                $.ajax({
                                    url:baseURL + 'modules/posuopei/question/save',
                                    type:"POST",
                                    contentType: "application/json",
                                    dataType: "json",
                                    data:JSON.stringify(otherSave),
                                    success:function (r) {
                                        // vm.$refs[formName].resetFields();
                                        // if(r.orgEntity != null){
                                        //     vm.forms.usercode=r.orgEntity.usercode;
                                        // }
                                        // if(r.orgEntity != null){
                                        //     vm.forms.username=r.orgEntity.orgname;
                                        // }
                                        if(r.code==0){
                                            vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                            alert(r.msg)
                                            vm.questionPaper1=false;
                                            vm.query();
                                        }else{
                                            alert(r.msg);
                                        }
                                    }
                                });
                            }
                        }else{
                            alert("店号不能为空！");
                        }


                    }else{
                        return false;
                    }
                }
            );
        },
        poChangeSave:function (formName) {
            var flagwe=true;
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        if(vm.poChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }
                        if(vm.cgwtdforms.partition==='1005'){
                            if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                                flagwe=false;
                            }
                        }
                        if(flagwe){
                            if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var poSave={
                                    id:vm.cgwtdforms.id,
                                    partition:vm.cgwtdforms.partition,
                                    purchaser:vm.cgwtdforms.purchaser,
                                    jvcode:vm.cgwtdforms.jvcode,
                                    city:vm.cgwtdforms.city,
                                    usercode:vm.cgwtdforms.usercode,
                                    username:vm.cgwtdforms.username,
                                    telephone:vm.cgwtdforms.telephone,
                                    department:vm.cgwtdforms.department,
                                    invoiceNo:vm.cgwtdforms.invoiceNo,
                                    invoiceDate:vm.cgwtdforms.invoiceDate,
                                    questionType:vm.cgwtdforms.questionType,
                                    totalAmount:vm.cgwtdforms.totalAmount,
                                    problemCause:vm.cgwtdforms.problemCause,
                                    description:vm.cgwtdforms.description,
                                    poList:vm.poChangeData,
                                    storeNbr:vm.cgwtdforms.storeNbr,
                                    fileList:vm.questionPaperFileList
                                };
                                $.ajax({
                                    url:baseURL + 'modules/posuopei/question/save',
                                    type:"POST",
                                    contentType: "application/json",
                                    dataType: "json",
                                    data:JSON.stringify(poSave),
                                    success:function (r) {
                                        if(r.code==0){
                                            vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                            alert(r.msg);
                                            vm.questionPaper1=false;
                                            vm.query();

                                        }else{
                                            alert(r.msg);
                                        }
                                    }
                                });
                            }
                        }else{
                            alert("店号不能为空！");
                        }

                    }else{
                        return false;
                    }
                }
            );



        },
        otherSave:function(formName){
            var flagwe=true;
            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    if(vm.cgwtdforms.partition==='1005'){
                        if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                            flagwe=false;
                        }
                    }
                    if(flagwe){
                        if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                            alert("部门不能为空！");

                        }else{
                            var poSave={
                                id:vm.cgwtdforms.id,
                                partition:vm.cgwtdforms.partition,
                                purchaser:vm.cgwtdforms.purchaser,
                                jvcode:vm.cgwtdforms.jvcode,
                                city:vm.cgwtdforms.city,
                                usercode:vm.cgwtdforms.usercode,
                                username:vm.cgwtdforms.username,
                                telephone:vm.cgwtdforms.telephone,
                                department:vm.cgwtdforms.department,
                                invoiceNo:vm.cgwtdforms.invoiceNo,
                                invoiceDate:vm.cgwtdforms.invoiceDate,
                                questionType:vm.cgwtdforms.questionType,
                                totalAmount:vm.cgwtdforms.totalAmount,
                                problemCause:vm.cgwtdforms.problemCause,
                                description:vm.cgwtdforms.description,
                                otherList:vm.otherData,
                                storeNbr:vm.cgwtdforms.storeNbr,
                                fileList:vm.questionPaperFileList
                            };
                            $.ajax({
                                url:baseURL + 'modules/posuopei/question/save',
                                type:"POST",
                                contentType: "application/json",
                                dataType: "json",
                                data:JSON.stringify(poSave),
                                success:function (r) {
                                    if(r.code==0){
                                        vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                        alert(r.msg);
                                        vm.questionPaper1=false;
                                        vm.query();

                                    }else{
                                        alert(r.msg);
                                    }
                                }
                            });
                        }
                    }else{
                        alert("店号不能为空！");
                    }

                }else{
                    return false;
                }
            })
        },
        poDiscountSave:function(formName){
            var flagwe=true;
            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    if(vm.poDiscountData.length<1){
                        alert("请填写最底端的问题单明细表格！");
                        return;
                    }
                    if(vm.cgwtdforms.partition==='1005'){
                        if(vm.cgwtdforms.storeNbr=='' || vm.cgwtdforms.storeNbr==null ){
                            flagwe=false;
                        }
                    }
                    if(flagwe){
                        if((vm.cgwtdforms.department=='' || vm.cgwtdforms.department==null)&& vm.cgwtdforms.partition==='1005' ){
                            alert("部门不能为空！");

                        }else{
                            var poSave={
                                id:vm.cgwtdforms.id,
                                partition:vm.cgwtdforms.partition,
                                purchaser:vm.cgwtdforms.purchaser,
                                jvcode:vm.cgwtdforms.jvcode,
                                city:vm.cgwtdforms.city,
                                usercode:vm.cgwtdforms.usercode,
                                username:vm.cgwtdforms.username,
                                telephone:vm.cgwtdforms.telephone,
                                department:vm.cgwtdforms.department,
                                invoiceNo:vm.cgwtdforms.invoiceNo,
                                invoiceDate:vm.cgwtdforms.invoiceDate,
                                questionType:vm.cgwtdforms.questionType,
                                totalAmount:vm.cgwtdforms.totalAmount,
                                problemCause:vm.cgwtdforms.problemCause,
                                description:vm.cgwtdforms.description,
                                poDiscountList:vm.poDiscountData,
                                storeNbr:vm.cgwtdforms.storeNbr,
                                fileList:vm.questionPaperFileList
                            };
                            $.ajax({
                                url:baseURL + 'modules/posuopei/question/save',
                                type:"POST",
                                contentType: "application/json",
                                dataType: "json",
                                data:JSON.stringify(poSave),
                                success:function (r) {
                                    if(r.code==0){
                                        vm.chexiao(vm.cgwtdforms.id,vm.cgwtdforms.reason);
                                        alert(r.msg);
                                        vm.questionPaper1=false;
                                        vm.query();

                                    }else{
                                        alert(r.msg);
                                    }
                                }
                            });
                        }
                    }else{
                        alert("店号不能为空！");
                    }

                }else{
                    return false;
                }
            })
        },
        addClaimChangeSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {

                    if (valid ) {

                        if( ( vm.addClaimChangeForm.systemAmount - vm.addClaimChangeForm.vendorAmount ) * vm.addClaimChangeForm.number != vm.addClaimChangeForm.difference ){
                            alert("所填写内容不符合（系统单价-供应商单价）*数量=金额差额  关系，请重新填写!");
                            return false;
                        }

                        vm.addClaimChangeDataShow=false;

                        vm.claimChangeData= vm.claimChangeData.concat(JSON.parse(JSON.stringify(vm.addClaimChangeForm)));
                        vm.cgwtdforms.totalAmount=0.00;
                        vm.claimChangeData.forEach(function(object,indx){
                            vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);

                        vm.$refs[formName].resetFields();

                    }else{
                        return false;
                    }
                }
            );

        },
        addPoDiscountSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {

                    if (valid ) {



                        vm.addPoDiscountShow=false;

                        vm.poDiscountData= vm.poDiscountData.concat(JSON.parse(JSON.stringify(vm.addPoDiscountForm)));
                        vm.cgwtdforms.totalAmount=0.00;
                        vm.poDiscountData.forEach(function(object,indx){
                            vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
                        vm.$refs[formName].resetFields();

                    }else{
                        return false;
                    }
                }
            );

        },
        addOtherSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {

                    if (valid ) {



                        vm.addOtherShow=false;

                        vm.otherData= vm.otherData.concat(JSON.parse(JSON.stringify(vm.addOtherForm)));
                        vm.cgwtdforms.totalAmount=0.00;
                        vm.otherData.forEach(function(object,indx){
                            vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
                        vm.$refs[formName].resetFields();

                    }else{
                        return false;
                    }
                }
            );

        },
        addCountChangeSave:function (formName) {

            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        if(vm.causeRules1){
                            vm.addCountChangeDataShow=false;
                        }else{
                            vm.addCountChangeDataShow2=false;
                        }
                        vm.countChangeData= vm.countChangeData.concat(JSON.parse(JSON.stringify(vm.addCountChangeForm)))
                        vm.cgwtdforms.totalAmount=0.00;
                        vm.countChangeData.forEach(function(object,indx){
                            vm.cgwtdforms.totalAmount+=parseFloat(object.amountDifference);
                        });
                        vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
                        vm.$refs[formName].resetFields();
                    }else{
                        return false;
                    }
                }
            );
        },
        addPoChangeSave:function (formName) {


            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        vm.addPoChangeDataShow=false;
                        vm.poChangeData=vm.poChangeData.concat(JSON.parse(JSON.stringify(vm.addPoChangeForm)))
                        vm.cgwtdforms.totalAmount=0.00;
                        vm.poChangeData.forEach(function(object,indx){
                            vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
                        vm.$refs[formName].resetFields();
                    }else{
                        return false;
                    }
                }
            );
        },
        deleteClaimChangeData:function (scope) {
            this.claimChangeData.splice(scope.$index,1);
            vm.cgwtdforms.totalAmount=0.00;
            vm.claimChangeData.forEach(function(object,indx){
                vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
            });
            vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
        },
        deletePoDiscountData:function (scope) {
            this.poDiscountData.splice(scope.$index,1);
            vm.cgwtdforms.totalAmount=0.00;
            vm.poDiscountData.forEach(function(object,indx){
                vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
            });
            vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
        },

        deleteOtherData:function (scope) {
            this.otherData.splice(scope.$index,1);
            vm.cgwtdforms.totalAmount=0.00;
            vm.otherData.forEach(function(object,indx){
                vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
            });
            vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
        },
        deletePoChangeData:function (scope) {
            this.poChangeData.splice(scope.$index,1);
            vm.cgwtdforms.totalAmount=0.00;
            vm.poChangeData.forEach(function(object,indx){
                vm.cgwtdforms.totalAmount+=parseFloat(object.difference);
            });
            vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
        },
        deleteCountChangeData:function (scope) {
            this.countChangeData.splice(scope.$index,1);
            vm.cgwtdforms.totalAmount=0.00;
            vm.countChangeData.forEach(function(object,indx){
                vm.cgwtdforms.totalAmount+=parseFloat(object.amountDifference);
            });
            vm.cgwtdforms.totalAmount=parseFloat(vm.cgwtdforms.totalAmount).toFixed(2);
        },

        addPoDiscountCancel:function () {
            vm.addPoDiscountShow=false;
        },

        addOtherCancel:function () {
            vm.addOtherShow=false;
        },
        addClaimChangeCancel:function () {
            vm.addClaimChangeDataShow=false;
        },
        addCountChangeCancel:function () {
            if(vm.causeRules1){
                vm.addCountChangeDataShow=false;
            }else{
                vm.addCountChangeDataShow2=false;
            }
        },
        addPoChangeCancel:function () {
            vm.addPoChangeDataShow=false;
        },

        downloadFile : function(row){
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("downloadFileId").src = encodeURI(baseURL + 'modules/posuopei/question/downloadFile?id='+row.id + "&token=" + token);
                },
                error: function () {

                }
            });
        },
        formatCheckTime: function (row, column) {
            if (row.checkDate != null) {
                return dateFormats(row.checkDate);
            } else {
                return '—— ——';
            }
        },
        formatRejectTime: function (row, column) {
            if (row.rejectDate != null) {
                return dateFormats(row.rejectDate);
            } else {
                return '—— ——';
            }
        },
        formatReplyTime: function (row, column) {
            if (row.replyDate != null) {
                return dateFormats(row.replyDate);
            } else {
                return '—— ——';
            }
        },
        formatCreatedTime: function (row, column) {
            if (row.createdDate != null) {
                return dateFormats(row.createdDate);
            } else {
                return '—— ——';
            }
        },
        resultDetail:function (row) {
            vm.questionPapers=true;
            if("2001"===row.questionType){

                vm.listLoading1 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.claimChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.claimChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").removeClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading1 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading1 = false;
                });
            }else if("2002"===row.questionType){


                vm.listLoading2=true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.poChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").removeClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading2 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading2 = false;
                });
            }else if("2004"===row.questionType){

                vm.listLoading3 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.countChangeData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.countChangeData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").removeClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").addClass("hideItem");
                    this.listLoading3 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading3 = false;
                });
            }else if("2003"===row.questionType){

                vm.listLoading4 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.poDiscountData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.poDiscountData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").removeClass("hideItem");
                    $(".other").addClass("hideItem")
                    this.listLoading4 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading4 = false;
                });
            }else{


                vm.listLoading5 = true;
                var params={
                    id:row.id,
                    questionType:row.questionType
                }
                this.$http.post(baseURL + sysUrl.questionPaperDetailQuery,
                    params, {
                        'headers': {
                            "token": token
                        }
                    }).then(function (response) {
                    this.total = response.data.page.totalCount;
                    this.totalPage = response.data.page.totalPage;
                    this.otherData = [];

                    for (var key in response.data.page.list) {
                        this.$set(this.otherData, key, response.data.page.list[key]);
                    }
                    $(".po").addClass("hideItem");
                    $(".count").addClass("hideItem");
                    $(".claim").addClass("hideItem");
                    $(".poDiscount").addClass("hideItem");
                    $(".other").removeClass("hideItem");
                    this.listLoading5 = false;
                }).catch(function (response) {
                    alert(response.data.msg);
                    this.listLoading5 = false;
                });

            }

        },




        viewFile : function(row){
            var id={
                id:row.id
            };

            $.ajax({
                url: baseURL + 'modules/posuopei/question/getFileList',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(id),
                success: function (r) {
                    if (r.code == 0) {
                        vm.questionPaperFileList=r.fileEntityList;
                        vm.questionPaperFile=true;
                    }
                }
            });
        },
        getFile : function(row){
            var id={
                id:row.id
            };

            $.ajax({
                url: baseURL + 'modules/posuopei/question/getFileList',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data: JSON.stringify(id),
                success: function (r) {
                    if (r.code == 0) {
                        vm.questionPaperFileList=r.fileEntityList;
                    }
                }
            });
        },
        focuspickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }else{
                $('#datevalue2').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#333333');

            }
        },
        queryDetails:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.forms.usercode=r.orgEntity.usercode
                        vm.forms.username=r.orgEntity.orgname

                    }
                }
            });
        },
        blurpickerchange: function (val) {
            if (val == 1) {
                $('#datevalue1').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }else {
                $('#datevalue2').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue2').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        questionDateStartChanges: function (val) {
            vm.forms.questionDateStart = val;
        },
        questionDateEndChanges: function (val) {
            vm.forms.questionDateEnd = val;
        },
        invoiceDateChanges: function (val) {
            vm.forms.invoiceDate = val;
        },
        focuspickerchanges: function (val) {
            if (val == 1) {
                $('#datevalue1s').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1s').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        blurpickerchanges: function (val) {
            if (val == 1) {
                $('#datevalue1s').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1s').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
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
        /**
         * 格式化开票时间
         */
        formatCreateTime: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        formatInvoiceDate: function (row) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return "";
            }
        },
        /**
         * 格式化金额
         */
        totalFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.totalAmount);
        },
    }
});
function dateFormats(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}

function dateFormat(value) {
    if (value == null) {
        return '';
    }
    return value.substring(0, 10);
}
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
function checkStatus(val){
    if(val==0){
        return "未审核";
    }
    if(val==1){
        return "审核中";
    }
    if(val==2){
        return "审核不通过";
    }
    if(val==3){
        return "审核通过";
    }
    if(val==4){
        return "审核完成";
    }
    if(val==5){
        return "审核完成";
    }
    if(val==6){
        return "审核完成";
    }
}
function replyStatus(val){
    if(val==5){
        return "采购已同意";
    }
    if(val==6){
        return "采购不同意";
    }
}