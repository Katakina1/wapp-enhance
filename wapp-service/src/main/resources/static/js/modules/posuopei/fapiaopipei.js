
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;



var vm=new Vue({
    el: '#rrapp',
    data:{
        causeRules1:false,
        invoiceDateOptions1:{},
        invoiceDateOptions2:{},
        //use
        jvcodeList:[],
        startDateOptions:{},
        endDateOptions:{},
        poEndDateOptions:{},
        poStartDateOptions:{},
        startDateOptions1:{},
        endDateOptions1:{},
        readonly:true,
        taxRateArray:[],
        cover:20,
        citys:[],
        //end

       //use
       llloading: false,
        listLoading: false,
        listLoading1: false,
        claimNum:0,
        claimAmount:0.00,
        matchcover:0.00,
        listLoading11: false,
        matchFailureShow:false,
        multipleSelection2:[],
        multipleSelection3: [],
        poNum:0,
        poAmount:0.00,
        invoiceNum:0,
        invoiceAmount:0.00,
        importDialogFormVisible:false,
        //end

        listLoading4: false,
           listLoading99: false,
        listLoading5: false,
        questionPaperFile:[],
        gfsh: [],
        form:{
            transactionType:null
        },

        /**
         * new
         */
        matchFailForm:{
            choose:"1"
        },
        poquery:{
            pocode:null
        },
        claimquery:{
            claimno:null,
            claimDateStart: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01",
            claimDateEnd: new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-" + format2(new Date().getDate()),
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
        questionPaper:false,
        forms:{
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
            storeNbr:null
        },
        part:[],
        questionTypes:[],
        Cause:[],
        poDiscountData:[],
        claimChangeData:[],
        poChangeData:[],
        countChangeData:[],
        otherData:[],

        queryData1:{
            // gfTaxNo:null,
            gfName:"沃尔玛(中国)投资有限公司",
            orgcode:"WI",
            usercode:null,
            username:null
        },
        poData:[],
        claimData:[],
        invoiceData:[],
        selectFileFlag: '',
        fileList: [],
        tempTableData: [],
        importFileUrl: '',
        isNeedFileExtension: false,
        uploadLoading:false,
        file: "",
        token: token,
        /**
         * end
         */

        /**发票明细******/
        matchNo:"",
        tempValue: null,
        detailEntityList: [],//存放明细页面数据
        tempDetailEntityList: [],//暂存明细页面详情清单数据
        detailForm: {
            matchNo: null,
            matchStatus:  null,
            matchDate : null,
            matchUser : null,
            matchErrInfo : null,
            invoiceType: null,
            invoiceStatus: null,
            createDate: null,
            statusUpdateDate: null,
            qsBy: null,
            qsType: null,
            sourceSystem: null,
            qsDate: null,
            rzhYesorno: null,
            gxDate:null,
            gxUserName:null,
            confirmDate:null,
            confirmUser:null,
            sendDate:null,
            authStatus:null,
            machinecode:null,
            dqskssq: null,
            rzhDate: null,
            outDate: null,
            outBy: null,
            outReason: null,
            outList: [],
            qsStatus: null,
            outStatus: null,
            checkCode: null,
            gfName: null,
            gfTaxNo: null,
            gfAddressAndPhone: null,
            gfBankAndNo: null,
            xfName: null,
            xfTaxNo: null,
            xfAddressAndPhone: null,
            xfBankAndNo: null,
            remark: null,
            detailEntityList: [],
            totalAmount: null,
            invoiceNo: null,
            invoiceCode: null,
            invoiceDate: null,
            buyerIdNum: null,
            vehicleType: null,//机动车明细里的车辆类型
            factoryModel: null,
            productPlace: null,
            certificate: null,
            certificateImport: null,
            inspectionNum: null,
            engineNo: null,
            vehicleNo: null,
            taxBureauName: null,
            taxBureauCode: null,
            phone: null,
            address: null,
            bank: null,
            account: null,
            taxRecords: null,
            limitPeople: null,
            tonnage: null,
            invoiceAmount: null,
            detailAmountTotal: null,
            taxAmountTotal: null,
            taxRate: null,
            taxAmount: null,
            stringTotalAmount: null
        },
        formLabelWidth: '1.2rem',
        dialogFormVisible: false,
        detailDialogFormVisible: false,
        detailDialogVehicleFormVisible: false,
        detailDialogCheckFormVisible: false,
        detailDialogFormInnerVisible: false,
        detailDialogCheckFormInnerVisible: false,
        /************************************/

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

    },mounted: function () {
        //use
        this.queryrc();
        this.queryTaxRate();
        this.queryPart();
        this.queryCity();

        this.queryDetails();
        $('.defaultItem').removeClass('hideItem');
        this.queryGf();
        this.queryDetail();

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
        this.poStartDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.poEndDateOptions = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };

        this.startDateOptions1 = {
            disabledDate: function (time) {
                return time.getTime() >= Date.now();
            }
        };
        this.endDateOptions1 = {
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

        //end
    },
    methods: {

//        invoiceDateOptions1 :function(time) {
//            disabledDate: function (time) {
//                return time.getTime() >= Date.now();
//            }
//        },
//        invoiceDateOptions2:function(time) {
//           disabledDate: function (time) {
//               return time.getTime() >= Date.now();
//           }
//       },
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
        questionTypeChange:function () {
            vm.forms.totalAmount=null;
            this.queryProblemCause(vm.forms.questionType);
            // if("2001"===vm.forms.questionType){
            //     $(".po").addClass("hideItem");
            //     $(".count").addClass("hideItem");
            //     $(".claim").removeClass("hideItem");
            // }else if("2002"===vm.forms.questionType){
            //     $(".po").removeClass("hideItem");
            //     $(".count").addClass("hideItem");
            //     $(".claim").addClass("hideItem");
            // }else if("2004"===vm.forms.questionType){
            //     $(".po").addClass("hideItem");
            //     $(".count").removeClass("hideItem");
            //     $(".claim").addClass("hideItem");
            // }else{
            //     $(".po").addClass("hideItem");
            //     $(".count").addClass("hideItem");
            //     $(".claim").addClass("hideItem");
            // }


            if("2001"===vm.forms.questionType){
                $(".po").addClass("hideItem");
                $(".count").addClass("hideItem");
                $(".claim").removeClass("hideItem");
                $(".other").addClass("hideItem");
                $(".poDiscount").addClass("hideItem");
            }else if("2002"===vm.forms.questionType){
                $(".po").removeClass("hideItem");
                $(".count").addClass("hideItem");
                $(".claim").addClass("hideItem")
                $(".other").addClass("hideItem");
                $(".poDiscount").addClass("hideItem");
            }else if("2004"===vm.forms.questionType){
                $(".po").addClass("hideItem");
                $(".count").removeClass("hideItem");
                $(".claim").addClass("hideItem");
                $(".other").addClass("hideItem");
                $(".poDiscount").addClass("hideItem");
            }else if("2003"===vm.forms.questionType){
                $(".po").addClass("hideItem");
                $(".count").addClass("hideItem");
                $(".claim").addClass("hideItem");
                $(".other").addClass("hideItem");
                $(".poDiscount").removeClass("hideItem");
            }else{
                $(".po").addClass("hideItem");
                $(".count").addClass("hideItem");
                $(".claim").addClass("hideItem");
                $(".other").removeClass("hideItem");
                $(".poDiscount").addClass("hideItem");
            }
        },
        partionChange:function () {
            vm.forms.totalAmount=null;
            this.queryQuestionType(vm.forms.partition);
            $(".po").addClass("hideItem");
            $(".count").addClass("hideItem");
            $(".claim").addClass("hideItem");

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
        countChangeSave:function (formName) {
            var flagwe=true;
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {
                        if(vm.forms.partition==='1005'){
                            if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                                flagwe=false;
                            }
                        }
                        if(vm.questionPaperFile.length<1){
                            alert("请先上传附件！");
                            return;
                        }
                        if(vm.countChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }
                        if(flagwe){
                            if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var countSave={
                                    partition:vm.forms.partition,
                                    purchaser:vm.forms.purchaser,
                                    jvcode:vm.forms.jvcode,
                                    city:vm.forms.city,
                                    usercode:vm.forms.usercode,
                                    username:vm.forms.username,
                                    telephone:vm.forms.telephone,
                                    department:vm.forms.department,
                                    invoiceNo:vm.forms.invoiceNo,
                                    invoiceDate:vm.forms.invoiceDate,
                                    questionType:vm.forms.questionType,
                                    totalAmount:vm.forms.totalAmount,
                                    problemCause:vm.forms.problemCause,
                                    description:vm.forms.description,
                                    countList:vm.countChangeData,
                                    storeNbr:vm.forms.storeNbr,
                                    fileList:vm.questionPaperFile
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
                                            vm.countChangeData=[];
                                            vm.forms.purchaser='';
                                            vm.forms.jvcode='';
                                            vm.forms.telephone='';
                                            vm.forms.department='';
                                            vm.forms.invoiceNo='';
                                            vm.forms.invoiceDate='';
                                            vm.forms.totalAmount='';
                                            vm.forms.storeNbr='';
                                            vm.questionPaperFile=[];
                                            vm.description='';
                                            vm.questionPaper=false;
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
                        if(vm.questionPaperFile.length<1){
                            alert("请先上传附件！");
                            return;
                        }
                        if(vm.claimChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }

                        if(vm.forms.partition==='1005'){
                            if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                                flag=false;
                            }
                        }
                        if(flag){
                            if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var claimSave={
                                    partition:vm.forms.partition,
                                    purchaser:vm.forms.purchaser,
                                    jvcode:vm.forms.jvcode,
                                    city:vm.forms.city,
                                    usercode:vm.forms.usercode,
                                    username:vm.forms.username,
                                    telephone:vm.forms.telephone,
                                    department:vm.forms.department,
                                    invoiceNo:vm.forms.invoiceNo,
                                    invoiceDate:vm.forms.invoiceDate,
                                    questionType:vm.forms.questionType,
                                    totalAmount:vm.forms.totalAmount,
                                    problemCause:vm.forms.problemCause,
                                    description:vm.forms.description,
                                    claimList:vm.claimChangeData,
                                    storeNbr:vm.forms.storeNbr,
                                    fileList:vm.questionPaperFile
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
                                            vm.claimChangeData=[];
                                            vm.forms.purchaser='';
                                            vm.forms.jvcode='';
                                            vm.forms.telephone='';
                                            vm.forms.department='';
                                            vm.forms.invoiceNo='';
                                            vm.forms.invoiceDate='';
                                            vm.forms.totalAmount='';
                                            vm.forms.storeNbr='';
                                            vm.questionPaperFile=[];
                                            vm.description='';
                                            vm.questionPaper=false;

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

                        if(vm.questionPaperFile.length<1){
                            alert("请先上传附件！");
                            return;
                        }
                        if(vm.poChangeData.length<1){
                            alert("请填写最底端的问题单明细表格！");
                            return;
                        }

                        if(vm.forms.partition==='1005'){
                            if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                                flagwe=false;
                            }
                        }
                        if(flagwe){
                            if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var poSave={
                                    partition:vm.forms.partition,
                                    purchaser:vm.forms.purchaser,
                                    jvcode:vm.forms.jvcode,
                                    city:vm.forms.city,
                                    usercode:vm.forms.usercode,
                                    username:vm.forms.username,
                                    telephone:vm.forms.telephone,
                                    department:vm.forms.department,
                                    invoiceNo:vm.forms.invoiceNo,
                                    invoiceDate:vm.forms.invoiceDate,
                                    questionType:vm.forms.questionType,
                                    totalAmount:vm.forms.totalAmount,
                                    problemCause:vm.forms.problemCause,
                                    description:vm.forms.description,
                                    poList:vm.poChangeData,
                                    storeNbr:vm.forms.storeNbr,
                                    fileList:vm.questionPaperFile
                                };
                                $.ajax({
                                    url:baseURL + 'modules/posuopei/question/save',
                                    type:"POST",
                                    contentType: "application/json",
                                    dataType: "json",
                                    data:JSON.stringify(poSave),
                                    success:function (r) {
                                        if(r.code==0){

                                            alert(r.msg)
                                            vm.poChangeData=[];
                                            vm.forms.purchaser='';
                                            vm.forms.jvcode='';
                                            vm.forms.telephone='';
                                            vm.forms.department='';
                                            vm.forms.invoiceNo='';
                                            vm.forms.invoiceDate='';
                                            vm.forms.totalAmount='';
                                            vm.forms.storeNbr='';
                                            vm.questionPaperFile=[];
                                            vm.description='';
                                            vm.questionPaper=false;

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
                    if(vm.questionPaperFile.length<1){
                        alert("请先上传附件！");
                        return;
                    }
                    if(vm.otherData.length<1){
                        alert("请填写最底端的问题单明细表格！");
                        return;
                    }

                    if(vm.forms.partition==='1005'){
                        if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                            flagwe=false;
                        }
                    }
                    if(flagwe){
                        if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                            alert("部门不能为空！");

                        }else{
                            var poSave={
                                partition:vm.forms.partition,
                                purchaser:vm.forms.purchaser,
                                jvcode:vm.forms.jvcode,
                                city:vm.forms.city,
                                usercode:vm.forms.usercode,
                                username:vm.forms.username,
                                telephone:vm.forms.telephone,
                                department:vm.forms.department,
                                invoiceNo:vm.forms.invoiceNo,
                                invoiceDate:vm.forms.invoiceDate,
                                questionType:vm.forms.questionType,
                                totalAmount:vm.forms.totalAmount,
                                problemCause:vm.forms.problemCause,
                                description:vm.forms.description,
                                otherList:vm.otherData,
                                storeNbr:vm.forms.storeNbr,
                                fileList:vm.questionPaperFile
                            };
                            $.ajax({
                                url:baseURL + 'modules/posuopei/question/save',
                                type:"POST",
                                contentType: "application/json",
                                dataType: "json",
                                data:JSON.stringify(poSave),
                                success:function (r) {
                                    if(r.code==0){

                                        alert(r.msg)
                                        vm.otherData=[];
                                        vm.forms.purchaser='';
                                        vm.forms.jvcode='';
                                        vm.forms.telephone='';
                                        vm.forms.department='';
                                        vm.forms.invoiceNo='';
                                        vm.forms.invoiceDate='';
                                        vm.forms.totalAmount='';
                                        vm.forms.storeNbr='';
                                        vm.questionPaperFile=[];
                                        vm.description='';
                                        vm.questionPaper=false;

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
                    if(vm.questionPaperFile.length<1){
                        alert("请先上传附件！");
                        return;
                    }
                    if(vm.poDiscountData.length<1){
                        alert("请填写最底端的问题单明细表格！");
                        return;
                    }
                    if(vm.forms.partition==='1005'){
                        if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                            flagwe=false;
                        }
                    }
                    if(flagwe){
                        if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                            alert("部门不能为空！");

                        }else{
                            var poSave={
                                partition:vm.forms.partition,
                                purchaser:vm.forms.purchaser,
                                jvcode:vm.forms.jvcode,
                                city:vm.forms.city,
                                usercode:vm.forms.usercode,
                                username:vm.forms.username,
                                telephone:vm.forms.telephone,
                                department:vm.forms.department,
                                invoiceNo:vm.forms.invoiceNo,
                                invoiceDate:vm.forms.invoiceDate,
                                questionType:vm.forms.questionType,
                                totalAmount:vm.forms.totalAmount,
                                problemCause:vm.forms.problemCause,
                                description:vm.forms.description,
                                poDiscountList:vm.poDiscountData,
                                storeNbr:vm.forms.storeNbr,
                                fileList:vm.questionPaperFile
                            };
                            $.ajax({
                                url:baseURL + 'modules/posuopei/question/save',
                                type:"POST",
                                contentType: "application/json",
                                dataType: "json",
                                data:JSON.stringify(poSave),
                                success:function (r) {
                                    if(r.code==0){

                                        alert(r.msg)
                                        vm.poDiscountData=[];
                                        vm.forms.purchaser='';
                                        vm.forms.jvcode='';
                                        vm.forms.telephone='';
                                        vm.forms.department='';
                                        vm.forms.invoiceNo='';
                                        vm.forms.invoiceDate='';
                                        vm.forms.totalAmount='';
                                        vm.forms.storeNbr='';
                                        vm.questionPaperFile=[];
                                        vm.description='';
                                        vm.questionPaper=false;

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
                         if( (( vm.addClaimChangeForm.systemAmount - vm.addClaimChangeForm.vendorAmount ).toFixed(2) * vm.addClaimChangeForm.number).toFixed(2) != vm.addClaimChangeForm.difference ){
                            alert("所填写内容不符合（系统单价-供应商单价）*数量=金额差额  关系，请重新填写!");
                            return false;
                        }

                        vm.addClaimChangeDataShow=false;

                        vm.claimChangeData= vm.claimChangeData.concat(JSON.parse(JSON.stringify(vm.addClaimChangeForm)));
                        vm.forms.totalAmount=0.00;
                        vm.claimChangeData.forEach(function(object,indx){
                            vm.forms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);

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

                        vm.forms.totalAmount=0.00;
                        vm.poDiscountData.forEach(function(object,indx){
                            vm.forms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
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

                        vm.forms.totalAmount=0.00;
                        vm.otherData.forEach(function(object,indx){
                            vm.forms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
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
                        }
                        else{
                            vm.addCountChangeDataShow2=false;
                        }
                        vm.countChangeData= vm.countChangeData.concat(JSON.parse(JSON.stringify(vm.addCountChangeForm)))
                        vm.forms.totalAmount=0.00;
                        vm.countChangeData.forEach(function(object,indx){
                            vm.forms.totalAmount+=parseFloat(object.amountDifference);
                        });
                        vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
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
                        vm.forms.totalAmount=0.00;
                        vm.poChangeData.forEach(function(object,indx){
                            vm.forms.totalAmount+=parseFloat(object.difference);
                        });
                        vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                        vm.$refs[formName].resetFields();
                    }else{
                        return false;
                    }
                }
            );
        },
        deleteClaimChangeData:function (scope) {
            this.claimChangeData.splice(scope.$index,1);
            vm.forms.totalAmount=0.00;
            vm.claimChangeData.forEach(function(object,indx){
                vm.forms.totalAmount+=parseFloat(object.difference);
            });
            vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
        },
        deletePoDiscountData:function (scope) {
            this.poDiscountData.splice(scope.$index,1);
            vm.forms.totalAmount=0.00;
            vm.poDiscountData.forEach(function(object,indx){
                vm.forms.totalAmount+=parseFloat(object.difference);
            });
            vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
        },

        deleteOtherData:function (scope) {
            this.otherData.splice(scope.$index,1);
            vm.forms.totalAmount=0.00;
            vm.otherData.forEach(function(object,indx){
                vm.forms.totalAmount+=parseFloat(object.difference);
            });
            vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
        },
        deletePoChangeData:function (scope) {
            this.poChangeData.splice(scope.$index,1);
            vm.forms.totalAmount=0.00;
            vm.poChangeData.forEach(function(object,indx){
                vm.forms.totalAmount+=parseFloat(object.difference);
            });
            vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
        },
        deleteCountChangeData:function (scope) {
            this.countChangeData.splice(scope.$index,1);
            vm.forms.totalAmount=0.00;
            vm.countChangeData.forEach(function(object,indx){
                vm.forms.totalAmount+=parseFloat(object.amountDifference);
            });
            vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
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
            }
            else{
                vm.addCountChangeDataShow2=false;
            }
            vm.addCountChangeForm.poCode=null;
            vm.addCountChangeForm.claimno=null;
            vm.addCountChangeForm.receiptid=null;
            vm.addCountChangeForm.goodsNo=null;
            vm.addCountChangeForm.systemAmount=null;
            vm.addCountChangeForm.vendorNumber=null;
            vm.addCountChangeForm.systemNumber=null;
            vm.addCountChangeForm.numberDifference=null;
            vm.addCountChangeForm.amountDifference=null;
        },
        addPoChangeCancel:function () {
            vm.addPoChangeDataShow=false;
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
                            vm.forms.partition=vm.part[0].value;
                            vm.queryQuestionType(vm.forms.partition);
                        }
                        $(".po").addClass("hideItem");
                        $(".count").addClass("hideItem");
                        $(".claim").addClass("hideItem");

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
                        debugger
                        if(vm.citys.length>0){
                            vm.forms.city=vm.citys[0].value;

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
        queryrc: function () {

            var theKey='匹配容差';

            $.ajax({
                url: baseURL + 'modules/posuopei/queryPart',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                data:theKey,
                success: function (r) {
                    if (r.code == 0) {
                       vm.cover=parseFloat(r.List[0].dictcode);

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
                            vm.forms.problemCause="";
                            vm.changeDescription(vm.forms.problemCause)
                        }

                    }
                }
            });
        },
        problemCauseChange:function () {
            vm.changeDescription(vm.forms.problemCause)
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
                            vm.forms.questionType=vm.questionTypes[0].value;
                            vm.queryProblemCause(vm.forms.questionType);
                            if("2001"===vm.forms.questionType){
                                $(".po").addClass("hideItem");
                                $(".count").addClass("hideItem");
                                $(".claim").removeClass("hideItem");
                                $(".other").addClass("hideItem");
                            }else if("2002"===vm.forms.questionType){
                                $(".po").removeClass("hideItem");
                                $(".count").addClass("hideItem");
                                $(".claim").addClass("hideItem")
                                $(".other").addClass("hideItem");
                            }else if("2004"===vm.forms.questionType){
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

        focuspickerchanges: function (val) {
            if (val == 1) {
                $('#datevalue1s').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue1s').siblings('span.el-input__prefix').children('i').css('color', '#333333');
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
                        if(r.orgEntity != null){
                            vm.forms.usercode=r.orgEntity.usercode;
                        }
                        if(r.orgEntity != null){
                            vm.forms.username=r.orgEntity.orgname;
                        }


                    }
                }
            });
        },
        blurpickerchanges: function (val) {
            if (val == 1) {
                $('#datevalue1s').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue1s').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        invoiceDateChanges: function (val) {
            vm.forms.invoiceDate = val;
        },
        questionPaperCancel:function () {
            vm.questionPaper=false;
            vm.countChangeData=[];
            vm.forms.purchaser='';
            vm.forms.jvcode='';
            vm.forms.telephone='';
            vm.forms.department='';
            vm.forms.invoiceNo='';
            vm.forms.invoiceDate='';
            vm.forms.totalAmount='';
            vm.forms.storeNbr='';
            vm.questionPaperFile=[];
            vm.description='';
        },
        matchFailureCancel:function () {
            vm.matchFailureShow=false;
            vm.matchFailForm.choose="1";
        },
        detailFormCancel: function () {
            vm.detailDialogFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];

        },
        detailVehicleFormCancel: function () {
            vm.detailDialogVehicleFormVisible = false;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.buyerIdNum = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.vehicleType = null;
            vm.detailForm.factoryModel = null;
            vm.detailForm.productPlace = null;
            vm.detailForm.certificate = null;
            vm.detailForm.certificateImport = null;
            vm.detailForm.inspectionNum = null;
            vm.detailForm.engineNo = null;
            vm.detailForm.vehicleNo = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.phone = null;
            vm.detailForm.address = null;
            vm.detailForm.account = null;
            vm.detailForm.bank = null;
            vm.detailForm.taxRate = null;
            vm.detailForm.taxAmount = null;
            vm.detailForm.taxBureauName = null;
            vm.detailForm.taxBureauCode = null;
            vm.detailForm.invoiceAmount = null;
            vm.detailForm.taxRecords = null;
            vm.detailForm.tonnage = null;
            vm.detailForm.limitPeople = null;
        },
        detailCheckFormCancel: function () {
            vm.detailDialogCheckFormVisible = false;
            vm.detailForm.gfName = null;
            vm.detailForm.gfTaxNo = null;
            vm.detailForm.gfAddressAndPhone = null;
            vm.detailForm.gfBankAndNo = null;
            vm.detailForm.xfName = null;
            vm.detailForm.xfTaxNo = null;
            vm.detailForm.xfAddressAndPhone = null;
            vm.detailForm.xfBankAndNo = null;
            vm.detailForm.remark = null;
            vm.detailForm.totalAmount = null;
            vm.detailForm.detailAmountTotal = null;
            vm.detailForm.taxAmountTotal = null;
            vm.detailForm.invoiceCode = null;
            vm.detailForm.invoiceNo = null;
            vm.detailForm.invoiceDate = null;
            vm.detailForm.stringTotalAmount = null;
            vm.detailEntityList = [];
            vm.tempDetailEntityList = [];
        },
        detailInnerFormCancel: function () {
            vm.detailDialogFormInnerVisible = false;
        },
        detailCheckInnerFormCancel: function () {
            vm.detailDialogCheckFormInnerVisible = false;
        },
        showInner: function () {
            vm.detailDialogFormInnerVisible = true;
        },
        showCheckInner: function () {
            vm.detailDialogCheckFormInnerVisible = true;
        },
        //use
        gfNameFocus: function (event) {
            $(".el-select input").removeAttr('readonly');
        },
        gfNameBlur: function (event) {
            $(".el-select input").attr('readonly', 'readonly');
        },
        gfNameChange:function(event){
            vm.gfsh.forEach(function(object){
                if(object.value===event){
                    vm.queryData1.gfName=object.gfName;
                    return;
                }
            });
            vm.poData=[];
            vm.claimData=[];
            vm.invoiceData=[];
            vm.claimNum=0;
            vm.claimAmount=0.00;
            vm.multipleSelection2=[];
            vm.multipleSelection3=[];
            vm.poNum=0;
            vm.poAmount=0.00;
            vm.invoiceNum=0;
            vm.invoiceAmount=0.00;



        },
        //end
        blueTaxAmountFormat: function (row) {
            return decimal(row.blueTaxAmount);
        },
        redTaxAmountFormat: function (row) {
            return decimal(row.redTaxAmount);
        },
        //use
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
            }else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', '#ffaa00');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#333333');
            }
        },
        deleteFile: function(tableData, row){
            tableData.splice(tableData.indexOf(row), 1);
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
            }else if (val == 5) {
                $('#datevalue5').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue5').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            } else if (val == 6) {
                $('#datevalue6').siblings('span.el-input__suffix').css('background', 'white');
                $('#datevalue6').siblings('span.el-input__prefix').children('i').css('color', '#ffaa00');
            }
        },
        //end
        blueAmountFormat: function (row) {
            return decimal(row.blueInvoiceAmount);
        },
        //new
        importFormCancel: function () {
            vm.importDialogFormVisible = false;
        },
        showSelectFileWin:function() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        importIn:function(){
            vm.importDialogFormVisible = true;
            $("#file").val("");
        },
        codeIn:function(){
            if(this.invoicequery.invoiceCode!=null &&this.invoicequery.invoiceCode!=''&&this.invoicequery.invoiceNo!=null && this.invoicequery.invoiceNo!=''){
                currentInvoiceParam1 = {
                    invoiceNo: vm.invoicequery.invoiceNo,
                    invoiceCode:vm.invoicequery.invoiceCode,
                    // invoiceDate: vm.invoicequery.invoiceDateStart,
                    // invoiceAmount:vm.invoicequery.invoiceAmount,
                    // totalAmount:vm.invoicequery.totalAmount,
                    // taxAmount:vm.invoicequery.taxAmount,
                    // taxRate:vm.invoicequery.taxRate,
                    venderid:vm.queryData1.usercode,
                    jvcode:vm.queryData1.orgcode,
                    gfName:vm.queryData1.gfName

                };
                $.ajax({
                    url:baseURL + 'modules/posuopei/invoice/query',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    /*async:false,*/
                    data:JSON.stringify(currentInvoiceParam1),
                    success:function (r) {
                        if(r.code==0){

                            // vm.invoiceData = r.page;
                            var flag=true;
                            if(r.page.list.length>0) {
                                vm.invoiceData.forEach(function (object, index) {


                                    if (vm.invoiceData[index].uuid == r.page.list[0].uuid) {
                                        flag = false;

                                        alert("该发票已在页面上！")
                                        vm.invoicequery.invoiceCode=null
                                        vm.invoicequery.invoiceNo=null
                                        vm.invoicequery.invoiceAmount=null
                                        vm.invoicequery.totalAmount=null
                                        vm.invoicequery.taxAmount=null
                                        vm.invoicequery.taxRate=null
                                        vm.invoicequery.checkNo=null
                                        vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01"
                                        vm.invoiceData.splice(index,1);
                                        vm.invoiceData = vm.invoiceData.concat(r.page.list[0]);

                                        // vm.invoiceData[index]=r.page.list[0];
                                        vm.invoiceAmount=0.00;
                                        vm.invoiceNum=vm.invoiceData.length;
                                        vm.invoiceData.forEach(function (object,index) {
                                            vm.invoiceAmount+=object.invoiceAmount;
                                        })
                                        vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);

                                        vm.invoiceAmount=parseFloat(vm.invoiceAmount).toFixed(2);

                                        vm.listLoading = false;
                                        return;
                                    }
                                })
                            }else {
                                if(getFplx(vm.invoicequery.invoiceCode)==="04"){
                                    $(".checkNoItem").removeClass("hideItem");
                                    // $(".invoiceAmountItem").addClass("hideItem");
                                }else{
                                    $(".checkNoItem").addClass("hideItem");
                                }
                                flag=false;
                            }

                            if(flag){

                                  vm.invoicequery.invoiceCode=null
                                  vm.invoicequery.invoiceNo=null
                                  vm.invoicequery.invoiceAmount=null
                                  vm.invoicequery.totalAmount=null
                                  vm.invoicequery.taxAmount=null
                                  vm.invoicequery.taxRate=null
                                  vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01"

                                vm.invoiceData = vm.invoiceData.concat(r.page.list);
                                vm.invoiceAmount=0.00;
                                vm.invoiceNum=vm.invoiceData.length;
                                vm.invoiceData.forEach(function (object,index) {
                                    vm.invoiceAmount+=object.invoiceAmount;
                                })
            vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);

                                vm.invoiceAmount=parseFloat(vm.invoiceAmount).toFixed(2);

                            }
                            setTimeout(function () {
                                vm.listLoading = false;
                            }, 500);
                        }else if(r.code==488){
                            alert(r.msg);
                            vm.invoicequery.invoiceCode=null
                            vm.invoicequery.invoiceNo=null
                            vm.invoicequery.invoiceAmount=null
                            vm.invoicequery.totalAmount=null
                            vm.invoicequery.taxAmount=null
                            vm.invoicequery.taxRate=null
                            vm.invoicequery.checkNo=null
                            vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01"
                            vm.listLoading = false;
                        }


                    }
                });
            }
        },
        noIn:function () {
            if(this.invoicequery.invoiceCode!=null &&this.invoicequery.invoiceCode!=''&&this.invoicequery.invoiceNo!=null && this.invoicequery.invoiceNo!=''){

            }

        },
        amountpaidFormat: function (row) {
            return decimal(row.amountpaid);
        },
        amountunpaidFormat: function (row) {
            return decimal(row.amountunpaid);
        },
        receiptAmountFormat: function (row) {
            return decimal(row.receiptAmount);
        },
        receiptdateFormat: function (row) {
            if (row.receiptdate != null) {
                return formaterDate(row.receiptdate);
            } else {
                return '—— ——';
            }
        },
        claimAmountFormat: function (row) {
            return decimal(row.claimAmount);
        },
        postdateFormat: function (row) {
            if (row.postdate != null) {
                return formaterDate(row.postdate);
            } else {
                return '—— ——';
            }
        },
        deleteInvoice:function (scope) {
            this.invoiceData.splice(scope.$index,1);
            vm.invoiceAmount=0.00;
            vm.invoiceNum=vm.invoiceData.length;
            vm.invoiceData.forEach(function (object,index) {
                vm.invoiceAmount+=object.invoiceAmount;
            })
            vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);
            vm.invoiceAmount=parseFloat(vm.invoiceAmount).toFixed(2);


        },
        //end
        redAmountFormat:function (row) {
            return decimal(row.redInvoiceAmount);
        },

        //use
        query1:function (formName) {
            vm.findAll1(formName)
        },
        //end
        query2:function () {
            vm.findAll2()
        },
        /**
         * new
         */
        querypo:function (formName) {
            vm.findAllpo(formName)
        },
        queryclaim:function (formName) {
            vm.findAllclaim(formName)
        },

        findAll1:function (formName) {



            vm.$refs[formName].validate(function (valid) {
                if (valid) {
                    vm.listLoading=true;
                    currentInvoiceParam = {
                        invoiceNo: vm.invoicequery.invoiceNo,
                        invoiceCode:vm.invoicequery.invoiceCode,
                        invoiceDate: vm.invoicequery.invoiceDateStart,
                        invoiceAmount:vm.invoicequery.invoiceAmount,
                        totalAmount:vm.invoicequery.totalAmount,
                        taxAmount:vm.invoicequery.taxAmount,
                        taxRate:vm.invoicequery.taxRate,
                        venderid:vm.queryData1.usercode,
                        jvcode:vm.queryData1.orgcode,
                        gfName:vm.queryData1.gfName,
                        checkNo:vm.invoicequery.checkNo

                    };
                     vm.llloading = true;

                    $.ajax({
                        url:baseURL + 'modules/posuopei/invoice/save',
                        type:"POST",
                        contentType: "application/json",
                        dataType: "json",
                        /*async:false,*/
                        data:JSON.stringify(currentInvoiceParam),
                        success:function (r) {
                            vm.llloading = false;
                            if(r.code==0){

                                // vm.invoiceData = r.page;
                                var flag=true;
                                vm.invoiceData.forEach(function (object,index) {
                                    if(r.page.list.length>0){
                                    if(vm.invoiceData[index].uuid==r.page.list[0].uuid){
                                        flag=false;

                                        alert("该发票已在页面上！")
                                        vm.invoicequery.invoiceCode=null
                                        vm.invoicequery.invoiceNo=null
                                        vm.invoicequery.invoiceAmount=null
                                        vm.invoicequery.totalAmount=null
                                        vm.invoicequery.taxAmount=null
                                        vm.invoicequery.taxRate=null
                                        vm.invoicequery.checkNo=null
                                        vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01"
                                        vm.invoiceData.splice(index,1);
                                        vm.invoiceData = vm.invoiceData.concat(r.page.list[0]);

                                        // vm.invoiceData[index]=r.page.list[0];
                                        vm.invoiceAmount=0.00;
                                        vm.invoiceNum=vm.invoiceData.length;
                                        vm.invoiceData.forEach(function (object,index) {
                                            vm.invoiceAmount+=object.invoiceAmount;
                                        })
                                        vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);

                                        vm.listLoading = false;
                                        return;
                                    }
                                }else{
                                        flag=false;
                                    }})

                                if(flag){
                                    vm.invoicequery.invoiceCode=null
                                    vm.invoicequery.invoiceNo=null
                                    vm.invoicequery.invoiceAmount=null
                                    vm.invoicequery.totalAmount=null
                                    vm.invoicequery.taxAmount=null
                                    vm.invoicequery.taxRate=null
                                    vm.invoicequery.checkNo=null
                                    vm.invoicequery.invoiceDateStart=new Date().getFullYear() + "-" + format2(new Date().getMonth() + 1) + "-01"
                                    vm.invoiceData = vm.invoiceData.concat(r.page.list);
                                    vm.invoiceAmount=0.00;
                                    vm.invoiceNum=vm.invoiceData.length;
                                    vm.invoiceData.forEach(function (object,index) {
                                        vm.invoiceAmount+=object.invoiceAmount;
                                    })
            vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);

                                }
                                setTimeout(function () {
                                    vm.listLoading = false;
                                }, 500);
                            }else if(r.code==488){

                                alert(r.msg);
                                vm.listLoading = false;
                            }


                        }
                    });
                }else{
                    return false;
                }})
            var intervelId = setInterval(function () {

                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;

            }, 50);

        },
        /**
         * end
         */

        /**
         * new
         */

        findAllpo:function (formName) {
            $(".checkMsg").remove();
        this.findAllclaims()
            var checkDate=true;
            var poDateStart = new Date(vm.poquery.poDateStart);
            var poDateEnd = new Date(vm.poquery.poDateEnd);
            if ( poDateStart.getTime()+1000*60*60*24*364*2 < poDateEnd.getTime()) {
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过2年</div>');
                checkDate=false;
            }else if(poDateEnd.getTime() < poDateStart.getTime()){
                $("#requireMsg4 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate=false;
            }
            if(!(checkDate )){
                return;
            }
            vm.$refs[formName].validate(function (valid) {
                    if (valid) {

                        var poCurrentParam = {
                            pocode: vm.poquery.pocode,
                            poDateStart:vm.poquery.poDateStart,
                            poDateEnd:vm.poquery.poDateEnd,
                            gfname: vm.queryData1.gfName,
                            venderid: vm.queryData1.usercode,
                            jvcode:vm.queryData1.orgcode
                        };
                        vm.listLoading11 = true;
                        $.ajax({
                            url: baseURL + 'modules/posuopei/po/query',
                            type: "POST",
                            contentType: "application/json",
                            dataType: "json",
                            data: JSON.stringify(poCurrentParam),
                            success: function (r) {
                                if (r.code == 0) {

                                    var flag=true;

                                        // vm.poData.forEach(function (object, index) {
                                        //     if( r.page.list.length>0) {
                                        //     if (vm.poData[index].pocode == r.page.list[0].pocode) {
                                        //
                                        //            flag = false;
                                        //            return;
                                        //        }
                                        //     }
                                        //
                                        // })
                                    vm.poData =(r.page.list);
                                    if(false){


                                        vm.poAmount=0.00;
                                        vm.poNum=vm.poData.length;
                                        vm.poData.forEach(function (object,index) {
                                            vm.poAmount+=object.amountunpaid;
                                        })
                                        vm.poAmount=parseFloat(vm.poAmount).toFixed(2);
                                    }
                                    setTimeout(function () {
                                        vm.listLoading11 = false;
                                    }, 500);
                                }

                            }
                        });
                    }else{
                        return false;
                    }
                })
        },
        findAllclaim:function (formName) {
            $(".checkMsg").remove();
            var checkDate1=true;
            var claimDateStart = new Date(vm.claimquery.claimDateStart);
            var claimDateEnd = new Date(vm.claimquery.claimDateEnd);
            if ( claimDateStart.getTime()+1000*60*60*24*360*2< claimDateEnd.getTime()) {
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">日期选择跨度不可超过2年</div>');
                checkDate1=false;
            }else if(claimDateEnd.getTime() <claimDateStart.getTime() ){
                $("#requireMsg2 .el-form-item__content div.el-date-editor:first-child").append('<div class="checkMsg">结束时间不可小于开始时间</div>');
                checkDate1=false;
            }
            if(!(checkDate1 )){
                return;
            }
            vm.$refs[formName].validate(function (valid) {
                if (valid) {

                    var claimCurrentParam = {
                        claimno: vm.claimquery.claimno,
                        gfname: vm.queryData1.gfName,
                        jvcode:vm.queryData1.orgcode,
                        venderid: vm.queryData1.usercode,
                        claimDateStart:vm.claimquery.claimDateStart,
                        claimDateEnd:vm.claimquery.claimDateEnd
                    };
                    vm.listLoading1 = true;
                    $.ajax({
                        url: baseURL + 'modules/posuopei/claim/query',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(claimCurrentParam),
                        success: function (r) {
                            if (r.code == 0) {

                                vm.claimData=r.page.list;

                                setTimeout(function () {
                                    vm.listLoading1 = false;
                                }, 500);
                                if(r.page.list.length>0){
                                    r.page.list.forEach(function (value,index) {
                                        if(value.ifYq=="1"){
                                            vm.$nextTick(function(){
                                                vm.$refs.multipleTable.toggleRowSelection(vm.claimData[index],true);
                                            })
                                        }
                                    })
                                }
                            }

                        }
                    });
                }else{
                    return false;
                }
            })
        },
        findAllclaims:function () {

                    var claimCurrentParam = {

                        gfname: vm.queryData1.gfName,
                        jvcode:vm.queryData1.orgcode,
                        venderid: vm.queryData1.usercode,
                        claimDateStart:vm.claimquery.claimDateStart,
                        claimDateEnd:vm.claimquery.claimDateEnd
                    };
                    vm.listLoading1 = true;
                    $.ajax({
                        url: baseURL + 'modules/posuopei/claim/query',
                        type: "POST",
                        contentType: "application/json",
                        dataType: "json",
                        data: JSON.stringify(claimCurrentParam),
                        success: function (r) {
                            if (r.code == 0) {

                                vm.claimData=r.page.list;

                                setTimeout(function () {
                                    vm.listLoading1 = false;
                                }, 500);
                                if(r.page.list.length>0){
                                    r.page.list.forEach(function (value,index) {
                                        if(value.ifYq=="1"){
                                            vm.$nextTick(function(){
                                                vm.$refs.multipleTable.toggleRowSelection(vm.claimData[index],true);
                                            })
                                        }
                                    })
                                }
                            }

                        }
                    });


        },
        //复选框
        checkboxT:function(row,index){
            if(row.ifYq=='1'){
                return 0;
            }else{
                return 1;
            }
        },
        exportPoPDF:function(matchno){
            var selection=[];
            selection.push(matchno)
            var venderName=[];
            venderName.push(vm.queryData1.username);
            var uri = baseURL + 'export/invoicePoRepertoire/invoiceChaXunExport' +'?matchnoList='+JSON.stringify(selection)+'&venderName='+JSON.stringify(venderName);
            $.ajax({
                type: "POST",
                url: baseURL + 'modules/checkImageToken',
                contentType: "application/json",
                success: function (r) {
                    document.getElementById("ifile").src = encodeURI(uri)
                },
                error: function () {
                }
            });
        },

        saveMatch:function(){

            if(vm.poNum>0 &&vm.invoiceNum>0){
                if(vm.poNum>1&&vm.invoiceNum>1){
                    alert("多张PO单和多张发票无法匹配，请选择1张PO单N张发票，或者N张PO单1张发票!")
                }else if(parseFloat(vm.poAmount)+parseFloat(vm.claimAmount)<0){
                    alert("索赔金额大于PO金额，无法匹配!")
                }else if((parseFloat(vm.poAmount)+parseFloat(vm.claimAmount)-parseFloat(vm.invoiceAmount))<-5000){
                    alert("差异金额大于5000，无法匹配!")
                }else{
                    var po=[];
                    var claim=[];
                    var invoice=[];
                    if((parseFloat(vm.poAmount)+parseFloat(vm.claimAmount)-parseFloat(vm.invoiceAmount))>vm.cover ){
                        //部分匹配 抵账表、po、索赔表匹配状态更新，插入matchno、匹配表插入匹配记录，把发票金额插入结算金额（部分匹配,未结金额-发票金额 插入PO未结金额）
                        po=vm.multipleSelection3;

                        claim=vm.multipleSelection2;
                        invoice=vm.invoiceData;
                        var InvoiceValueObject={
                            matchingType:"2",
                            poEntityList:vm.multipleSelection3,
                            claimEntityList:vm.multipleSelection2,
                            invoiceEntityList:vm.invoiceData,
                            invoiceAmount:vm.invoiceAmount,
                            poAmount:vm.poAmount,
                            venderid:vm.queryData1.usercode,
                            gfName:vm.queryData1.gfName,
                            claimAmount:vm.claimAmount,
                            poNum:vm.poNum,
                            claimNum:vm.claimNum,
                            invoiceNum:vm.invoiceNum,
                            settlementamount:vm.invoiceAmount,
                            jvcode:vm.queryData1.orgcode



                        };
                        vm.listLoading=true;
                        $.ajax({
                            url:baseURL + 'modules/posuopei/invoice/match',
                            type:"POST",
                            contentType: "application/json",
                            dataType: "json",
                            data:JSON.stringify(InvoiceValueObject),
                            success:function (r) {
                                if(r.code==0){

                                    alert(r.msg)

                                    vm.poData=[];
                                    vm.claimData=[];
                                    vm.invoiceData=[];
                                    vm.claimNum=0;
                                    vm.claimAmount=0.00;
                                    vm.multipleSelection2=[];
                                    vm.multipleSelection3=[];
                                    vm.poNum=0;
                                    vm.poAmount=0.00;
                                    vm.invoiceNum=0;
                                    vm.invoiceAmount=0.00;
                                    vm.listLoading=false;

                                }else if(r.code==1){
                                    vm.listLoading=false;

                                    alert(r.msg);
                                }
                            }
                        });
                    }else if((parseFloat(vm.poAmount)+parseFloat(vm.claimAmount)-parseFloat(vm.invoiceAmount))<-vm.cover){
                        //匹配失败 抵账表、po、索赔表匹配状态不变、匹配表插入匹配记录，
                        vm.matchFailureShow=true;
                    }else{

                            po=vm.multipleSelection3;

                           claim=vm.multipleSelection2;
                           invoice=vm.invoiceData;
                        //匹配成功 抵账表、po、索赔表匹配状态更新，插入matchno、匹配表插入匹配记录，把发票金额插入结算金额

                        var InvoiceValueObject={
                            matchingType:"3",
                            poEntityList:vm.multipleSelection3,
                            claimEntityList:vm.multipleSelection2,
                            invoiceEntityList:vm.invoiceData,
                            invoiceAmount:vm.invoiceAmount,
                            poAmount:vm.poAmount,
                            venderid:vm.queryData1.usercode,
                            gfName:vm.queryData1.gfName,
                            claimAmount:vm.claimAmount,
                            poNum:vm.poNum,
                            claimNum:vm.claimNum,
                            invoiceNum:vm.invoiceNum,
                            settlementamount:parseFloat(vm.poAmount)+parseFloat(vm.claimAmount),
                            jvcode:vm.queryData1.orgcode
                        };
                        vm.listLoading=true;
                        debugger
                        $("#export_btn_match").attr("disabled","disabled").addClass("is-disabled")
                        $.ajax({
                            url:baseURL + 'modules/posuopei/invoice/match',
                            type:"POST",
                            contentType: "application/json",
                            dataType: "json",
                            data:JSON.stringify(InvoiceValueObject),
                            success:function (r) {
                                if(r.code==0){

                                    alert(r.msg)

                                    vm.poData=[];
                                    vm.claimData=[];
                                    vm.invoiceData=[];
                                    vm.claimNum=0;
                                    vm.claimAmount=0.00;
                                    vm.multipleSelection2=[];
                                    vm.multipleSelection3=[];
                                    vm.poNum=0;
                                    vm.poAmount=0.00;
                                    vm.invoiceNum=0;
                                    vm.invoiceAmount=0.00;
                                    vm.listLoading=false;
                                }else if(r.code==1){
                                    vm.listLoading=false;
                                    alert(r.msg);
                                }
                            }
                        });

                                        $("#export_btn_match").removeAttr("disabled").removeClass("is-disabled");
                    }
                }

            }else{
                alert("未选择PO单/发票！")
            }
        },
        //匹配失败
        CYMatch:function () {
            // 差异匹配

                po=vm.multipleSelection3;

                claim=vm.multipleSelection2;
                invoice=vm.invoiceData;
            var settlement;
            var matchsts;
            if(vm.matchFailForm.choose==="3"){
                matchsts="4";
                settlement=parseFloat(vm.poAmount)+parseFloat(vm.claimAmount)
            }else{
                matchsts="5";
                settlement=0.00
                if(vm.matchFailForm.choose==="2"){
                    vm.questionPaper=true;
                }
            }
                var InvoiceValueObject={
                    matchingType:matchsts,
                    poEntityList:vm.multipleSelection3,
                    claimEntityList:vm.multipleSelection2,
                    invoiceEntityList:vm.invoiceData,
                    invoiceAmount:vm.invoiceAmount,
                    poAmount:vm.poAmount,
                    venderid:vm.queryData1.usercode,
                    gfName:vm.queryData1.gfName,
                    claimAmount:vm.claimAmount,
                    poNum:vm.poNum,
                    claimNum:vm.claimNum,
                    invoiceNum:vm.invoiceNum,
                    settlementamount:settlement,
                    jvcode:vm.queryData1.orgcode

                };
            vm.listLoading=true;
                 $("#export_btn_match").attr("disabled","disabled").addClass("is-disabled")
                $.ajax({
                    url:baseURL + 'modules/posuopei/invoice/match',
                    type:"POST",
                    contentType: "application/json",
                    dataType: "json",
                    data:JSON.stringify(InvoiceValueObject),
                    success:function (r) {
                        if(r.code==0){
                            if(matchsts==="4" && r.msg!="匹配失败"){

                                vm.exportPoPDF(r.msg);
                            }else{
                              //  alert(r.msg);
                            }


                            vm.poData=[];
                            vm.claimData=[];
                            vm.invoiceData=[];
                            vm.claimNum=0;
                            vm.claimAmount=0.00;
                            vm.multipleSelection2=[];
                            vm.multipleSelection3=[];
                            vm.poNum=0;
                            vm.poAmount=0.00;
                            vm.invoiceNum=0;
                            vm.invoiceAmount=0.00;
                            vm.matchFailureShow=false;
                            vm.listLoading=false;
                        }else if(r.code==1){
                            vm.listLoading=false;
                            vm.matchFailureShow=false;
                            alert(r.msg);
                        }
                    },error:function () {
                        vm.listLoading=false;
                        alert("数据问题!请尝试其他单据");
                    }
                });

                   $("#export_btn_match").removeAttr("disabled").removeClass("is-disabled");

        }
        /**
         *end
         */
        ,invoiceDateFormat:function (row) {
            if (row.invoiceDate != null) {
                return formaterDate(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        createrDateFormat:function (row) {
            if (row.createDate != null) {
                return formaterDate(row.createDate);
            } else {
                return '—— ——';
            }
        },
        invoiceTypeFormat:function (row) {
            if(row.invoiceType=="01"){
                return "增值税专用发票";
            }else if(row.invoiceType=="03"){
                return "机动车销售统一发票";
            }else if(row.invoiceType=="04"){
                return "增值税普通发票";
            }else if(row.invoiceType=="10"){
                return "增值税普通发票（电子普通发票）";
            }else if(row.invoiceType=="11"){
                return "增值税普通发票（卷式发票）";
            }else if(row.invoiceType=="14"){
                return "增值税普通发票（通行费发票）";
            }else{
                return "—— ——";
            }

        },
        //use
        invoiceAmountFormatDecimal: function (row, column, index) {
            return decimal(row.invoiceAmount);
        }, taxAmountFormatDecimal: function (row, column, index) {
            return decimal(row.taxAmount);
        },totalValoremTaxFormat:function (row, column, index) {
            return decimal(row.totalAmount);
        },
        //end

        //use
        changeFun2: function (row) {

            vm.multipleSelection2 = row;
            vm.claimNum=row.length;
            vm.claimAmount=0;
            for(var i=0;i<row.length;i++){
                vm.claimAmount+=row[i].claimAmount;
            }
            debugger
            vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);
            vm.claimAmount=parseFloat(vm.claimAmount).toFixed(2);
        },
        changeFun3: function (row) {

            vm.multipleSelection3 = row;
            vm.poNum=row.length;
            vm.poAmount=0.00;
            for(var i=0;i<row.length;i++){
                vm.poAmount+=row[i].amountunpaid;
            }
            vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);
            vm.poAmount=parseFloat(vm.poAmount).toFixed(2);
        },

        claimDate1Change: function (val) {
            vm.claimquery.claimDateStart = val;
        },
        claimDate2Change: function (val) {
            vm.claimquery.claimDateEnd = val;
        },
        poDate1Change: function (val) {
            vm.poquery.poDateStart = val;
        },
        poDate2Change: function (val) {
            vm.poquery.poDateEnd = val;
        },

        invoiceDate3Change: function (val) {
            vm.invoicequery.invoiceDateStart = val;
        },
        invoiceDate4Change: function (val) {
            vm.invoicequery.invoiceDateEnd = val;
        },

        //use
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

            vm.listLoading4 = true;
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
                        vm.questionPaperFile.push(data)
                        alert("上传成功！")
                    }else{
                        alert(r.msg);
                    }
                    vm.listLoading4 = false;
                    document.getElementById("uploadBtn").getElementsByTagName("span")[0].innerHTML = "上传附件";

                },
            });
            document.getElementById("fileId").value = '';
        },
        numberFormat:function(a,b,c){
            return moneyFormat(c);
        },
        queryGf: function () {
            $.ajax({
                url: baseURL + 'modules/posuopei/gfNameAndTaxNo',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        var gfs = [];
                        var index=0;
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.value = r.List[i].orgcode;
                            gf.label = r.List[i].orgcode;
                            gf.gfName=r.List[i].orgname;
                            gfs.push(gf);
                            if(r.List[i].orgcode=="WI"){
                                index=i;
                            }
                        }
                        vm.gfsh = gfs;
                       if(vm.gfsh.length>0){
                            vm.queryData1.gfName=vm.gfsh[index].gfName;
                            vm.queryData1.orgcode=vm.gfsh[index].value;
                        }

                    }
                }
            });
        },
        queryDetail:function(){
            $.ajax({
                url: baseURL + 'modules/posuopei/getDefaultMessage',
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                success: function (r) {
                    if (r.code == 0) {
                        // vm.queryData1.orgcode=r.orgEntity.orgcode
                        vm.queryData1.usercode=r.orgEntity.usercode
                        vm.queryData1.username=r.orgEntity.orgname

                    }
                }
            });
        },
        exportData:function(){
            document.getElementById("ifile").src = baseURL + sysUrl.invoiceImportExport;
        },
        exportDataAdd:function(name){
            document.getElementById("ifile").src = baseURL + sysUrl.addImportExport+"?name="+name;
        },

        onChangeFile: function (event) {
            var str = event.target.val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                this.isNeedFileExtension = false;
                return false;
            } else {
                this.selectFileFlag = '';
                var meFile = event.target.files[0];
                this.file = '';
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
                var fileValue = document.getElementById("file").value;
                if (fileValue == '' || fileValue == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                vm.listLoading4=true;
                var formData = new FormData();
                formData.append('file', document.getElementById("file").files[0]);
                formData.append('token', this.token);
                formData.append('gfName',this.queryData1.gfName)
                formData.append('jvcode',this.queryData1.orgcode)
                formData.append('venderid',this.queryData1.usercode)
                formData.append('venderName',this.queryData1.username)

                var config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };


                var flag = false;
                var hh;
                var url = baseURL + sysUrl.invoiceImport;
                this.$http.post(url, formData, config).then(function (response) {
                    vm.listLoading4=false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    // vm.file = '';
                    if (response.data.success) {

                        if(vm.invoiceData.length>0) {
                            response.data.invoiceQueryList.forEach(function (object, index) {
                                if (response.data.invoiceQueryList.length > 0) {
                                    var type=true;
                                    vm.invoiceData.forEach(function (object, index1) {
                                        if (response.data.invoiceQueryList[index].uuid == object.uuid) {
                                            vm.invoiceData[index1]=response.data.invoiceQueryList[index];

                                            type=false;
                                            return;
                                        }
                                    })
                                    if(type){
                                        vm.invoiceData = vm.invoiceData.concat(response.data.invoiceQueryList[index]);
                                    }
                                }
                            })

                        }else {
                            vm.invoiceData = vm.invoiceData.concat(response.data.invoiceQueryList);

                        }


                        vm.invoiceAmount=0.00;
                        vm.invoiceNum=vm.invoiceData.length;
                        vm.invoiceData.forEach(function (object,index) {
                            vm.invoiceAmount+=object.invoiceAmount;
                        })
                        vm.invoiceAmount=parseFloat(vm.invoiceAmount).toFixed(2);
                        vm.matchcover=(parseFloat(vm.claimAmount)+parseFloat(vm.poAmount)-parseFloat(vm.invoiceAmount)).toFixed(2);

                        vm.importDialogFormVisible = false;
                        $("#file").html("");
                    } else {
                        vm.importDialogFormVisible = false;
                        $("#file").html("");

                        alert(response.data.reason);
                    }
                }, function(err) {

                    if (err.status == 408) {
                        vm.importDialogFormVisible = false;
                        alert(response.data.reason);
                    }

                    $("#file").val("");
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh = $(document).height();
                        $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                        clearInterval(intervelId);
                        return;
                    }
                }, 50);
            }
        },
        //导入其他差异明细
        uploadFileAddOther: function () {
            var fileValue = document.getElementById("otherFile").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("otherFile").files[0]);
            formData.append('token', this.token);
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addOtherImport;
            vm.addOtherShow=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#otherFile").val("");
                if(response.data.list.length>0){
                    vm.otherData=vm.otherData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.otherData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.difference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //导入索赔差异明细
        uploadFileAddClaim: function () {
            var fileValue = document.getElementById("claimFile").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("claimFile").files[0]);
            formData.append('token', this.token);
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addClaimImport;
            vm.addClaimChangeDataShow=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#claimFile").val("");
                if(response.data.list.length>0){
                    vm.claimChangeData=vm.claimChangeData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.claimChangeData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.difference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //导入订单单价差异明细
        uploadFileAddPo: function () {
            var fileValue = document.getElementById("poFile").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("poFile").files[0]);
            formData.append('token', this.token);
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addPoImport;
            vm.addPoChangeDataShow=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#poFile").val("");
                if(response.data.list.length>0){
                    vm.poChangeData=vm.poChangeData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.poChangeData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.difference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //导入订单折扣差异明细
        uploadFileAddPoDiscount: function () {
            var fileValue = document.getElementById("poDiscountFile").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("poDiscountFile").files[0]);
            formData.append('token', this.token);
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addPoDiscountImport;
            vm.addPoDiscountShow=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#poDiscountFile").val("");
                if(response.data.list.length>0){
                    vm.poDiscountData=vm.poDiscountData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.poDiscountData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.difference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //导入收货数量明细
        uploadFileAddCount1: function () {
            var fileValue = document.getElementById("count1File").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("count1File").files[0]);
            formData.append('token', this.token);
            formData.append('type', "1");
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addCountImport;
            vm.addCountChangeDataShow=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#count1File").val("");
                if(response.data.list.length>0){
                    vm.countChangeData=vm.countChangeData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.countChangeData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.amountDifference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //导入退货数量明细
        uploadFileAddCount2: function () {
            var fileValue = document.getElementById("count2File").value;
            if (fileValue == '' || fileValue == undefined) {
                alert("请选择excel文件!");
                return;
            }
            vm.listLoading=true;
            var formData = new FormData();
            formData.append('file', document.getElementById("count2File").files[0]);
            formData.append('token', this.token);
            formData.append('type', "2");
            var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };


            var flag = false;
            var hh;
            var url = baseURL + sysUrl.addCountImport;
            vm.addCountChangeDataShow2=false;
            this.$http.post(url, formData, config).then(function (response) {
                vm.listLoading=false;
                flag = true;
                $("#count2File").val("");
                if(response.data.list.length>0){
                    vm.countChangeData=vm.countChangeData.concat(response.data.list);
                    vm.forms.totalAmount=0.00;
                    vm.countChangeData.forEach(function(object,indx){
                        vm.forms.totalAmount+=parseFloat(object.amountDifference);
                    });
                    vm.forms.totalAmount=parseFloat(vm.forms.totalAmount).toFixed(2);
                }
                if(response.data.result.length>0){
                    alert(response.data.result);
                }

            }, function(err) {

                if (err.status == 408) {
                    vm.importDialogFormVisible = false;
                    alert(response.data.reason);
                }
            })
            var intervelId = setInterval(function () {
                if (flag) {
                    hh = $(document).height();
                    $("body", parent.document).find("#myiframe").css('height', hh + 'px');
                    clearInterval(intervelId);
                    return;
                }
            }, 50);

        },
        //end
        invoiceDetail : function (row) {
            if(row.invoiceType=="01"||row.invoiceType=="04"){
                $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display', 'none');
                $.ajax({
                    type: "POST",
                    url: baseURL + "modules/posuopei/invoice/detail",
                    contentType: "application/json",
                    data: JSON.stringify(row.id),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.detailForm.gxDate = r.invoiceEntity.gxDate;
                            vm.detailForm.gxUserName = r.invoiceEntity.gxUserName;
                            vm.detailForm.confirmDate = r.invoiceEntity.confirmDate;
                            vm.detailForm.confirmUser = r.invoiceEntity.confirmUser;
                            vm.detailForm.sendDate = r.invoiceEntity.sendDate;
                            vm.detailForm.authStatus = r.invoiceEntity.authStatus;
                            vm.detailForm.machinecode = r.invoiceEntity.machinecode;
                            vm.detailForm.invoiceType = r.invoiceEntity.invoiceType;
                            vm.detailForm.outList = r.outList;
                            if (r.invoiceEntity.gfTaxNo == personalTaxNumber) {
                                vm.detailForm.gfTaxNo = "";
                            } else {
                                vm.detailForm.gfTaxNo = r.invoiceEntity.gfTaxNo;
                            }
                            vm.detailForm.invoiceStatus = formatInvoiceStatus(r.invoiceEntity.invoiceStatus);
                            vm.detailForm.sourceSystem = formatSourceSystem(r.invoiceEntity.sourceSystem);
                            vm.detailForm.createDate = r.invoiceEntity.createDate;
                            vm.detailForm.statusUpdateDate = r.invoiceEntity.statusUpdateDate;
                            vm.detailForm.qsType = formatQsType(r.invoiceEntity.qsType);
                            vm.detailForm.qsBy = r.invoiceEntity.qsBy;
                            vm.detailForm.qsDate = r.invoiceEntity.qsDate;
                            vm.detailForm.rzhYesorno = r.invoiceEntity.rzhYesorno;
                            vm.detailForm.dqskssq = r.invoiceEntity.dqskssq;
                            vm.detailForm.rzhDate = r.invoiceEntity.rzhDate;
                            vm.detailForm.outDate = r.invoiceEntity.outDate;
                            vm.detailForm.outBy = r.invoiceEntity.outBy;
                            vm.detailForm.outReason = formatOutReason(r.invoiceEntity.outReason);
                            vm.detailForm.qsStatus = r.invoiceEntity.qsStatus;
                            vm.detailForm.outStatus = r.invoiceEntity.outStatus;
                            vm.detailForm.checkCode = r.invoiceEntity.checkCode;
                            vm.detailForm.gfName = r.invoiceEntity.gfName;
                            vm.detailForm.invoiceDate = detailDateFormat(r.invoiceEntity.invoiceDate);
                            vm.detailForm.gfAddressAndPhone = r.invoiceEntity.gfAddressAndPhone;
                            vm.detailForm.gfBankAndNo = r.invoiceEntity.gfBankAndNo;
                            vm.detailForm.xfName = r.invoiceEntity.xfName;
                            vm.detailForm.xfTaxNo = r.invoiceEntity.xfTaxNo;
                            vm.detailForm.xfAddressAndPhone = r.invoiceEntity.xfAddressAndPhone;
                            vm.detailForm.xfBankAndNo = r.invoiceEntity.xfBankAndNo;
                            vm.detailForm.remark = r.invoiceEntity.remark;
                            vm.detailForm.totalAmount = vm.numberFormat(null, null, r.invoiceEntity.totalAmount);
                            vm.detailForm.invoiceCode = r.invoiceEntity.invoiceCode;
                            vm.detailForm.invoiceNo = r.invoiceEntity.invoiceNo;
                            vm.detailForm.stringTotalAmount = r.invoiceEntity.stringTotalAmount;
                            //var invoiceFlag=getFpLx(r.invoiceEntity.invoiceCode);//用于判断发票类型
                            if (r.invoiceEntity.invoiceType == "03") {
                                vm.detailForm.taxAmount = vm.numberFormat(null, null, r.invoiceEntity.taxAmount);
                                vm.detailForm.phone = r.phone;
                                vm.detailForm.address = r.address;
                                vm.detailForm.account = r.account;
                                vm.detailForm.bank = r.bank;
                                if (r.detailVehicleEntity != null) {
                                    vm.detailForm.vehicleType = r.detailVehicleEntity.vehicleType;
                                    vm.detailForm.factoryModel = r.detailVehicleEntity.factoryModel;
                                    vm.detailForm.productPlace = r.detailVehicleEntity.productPlace;
                                    vm.detailForm.certificate = r.detailVehicleEntity.certificate;
                                    vm.detailForm.certificateImport = r.detailVehicleEntity.certificateImport;
                                    vm.detailForm.inspectionNum = r.detailVehicleEntity.inspectionNum;
                                    vm.detailForm.engineNo = r.detailVehicleEntity.engineNo;
                                    vm.detailForm.vehicleNo = r.detailVehicleEntity.vehicleNo;
                                    vm.detailForm.taxRate = r.detailVehicleEntity.taxRate;
                                    vm.detailForm.taxBureauName = r.detailVehicleEntity.taxBureauName;
                                    vm.detailForm.taxBureauCode = r.detailVehicleEntity.taxBureauCode;
                                    vm.detailForm.invoiceAmount = vm.numberFormat(null, null, r.invoiceEntity.invoiceAmount);
                                    vm.detailForm.taxRecords = r.detailVehicleEntity.taxRecords;
                                    vm.detailForm.tonnage = r.detailVehicleEntity.tonnage;
                                    vm.detailForm.limitPeople = r.detailVehicleEntity.limitPeople;
                                    vm.detailForm.buyerIdNum = r.detailVehicleEntity.buyerIdNum;
                                }
                                vm.detailDialogVehicleFormVisible = true;
                            } else if (r.invoiceEntity.invoiceType == "14") {
                                vm.detailDialogCheckFormVisible = true;
                                vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                                vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                                vm.detailEntityList = r.detailEntityList;
                                for (var i = 0; i < vm.detailEntityList.length; i++) {
                                    vm.detailEntityList[i].unitPrice = vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice) ;
                                    vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                    vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                                }
                                vm.tempDetailEntityList = vm.detailEntityList
                                if (r.detailEntityList.length > 8) {
                                    vm.detailEntityList = null;
                                }
                            } else {
                                vm.detailDialogFormVisible = true;
                                vm.detailForm.detailAmountTotal = vm.numberFormat(null, null, r.detailAmountTotal);
                                vm.detailForm.taxAmountTotal = vm.numberFormat(null, null, r.taxAmountTotal);
                                vm.detailEntityList = r.detailEntityList;
                                for (var i = 0; i < vm.detailEntityList.length; i++) {
                                    vm.detailEntityList[i].unitPrice =  vm.numberFormat(null, null, vm.detailEntityList[i].unitPrice);
                                    vm.detailEntityList[i].detailAmount = vm.numberFormat(null, null, vm.detailEntityList[i].detailAmount);
                                    vm.detailEntityList[i].taxAmount = vm.numberFormat(null, null, vm.detailEntityList[i].taxAmount);
                                }
                                vm.tempDetailEntityList = vm.detailEntityList
                                if (r.detailEntityList.length > 8) {
                                    vm.detailEntityList = null;
                                }
                            }
                        } else {
                            alert(r.msg);
                        }
                        /*if(vm.detailForm.invoiceStatus=='正常'){
                         $('.col-xs-3 tr td:contains("异常")').parent('tr').css('display','none');
                         alert('normol');
                         }else{
                         alert('error')
                         }*/
                    }
                });
            }
            else{
                alert("该发票没有明细！")
            }


        },






      /**
         * 格式化发票金额
         */
        invoiceAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.invoiceAmount);
        },
        /**
         * 格式化开票时间
         */
        formatInvoiceDate: function (row, column) {
            if (row.invoiceDate != null) {
                return dateFormat(row.invoiceDate);
            } else {
                return '—— ——';
            }
        },
        /**
         * 格式化创建时间
         */
        formatCreateTime: function (row, column) {
            if (row.createTime != null) {
                return dateFormat(row.createTime);
            } else {
                return '—— ——';
            }
        },
        formatterTransactionDate: function (row, column) {
            if (row.transactionDate != null) {
                return dateFormat(row.transactionDate);
            } else {
                return '—— ——';
            }
        },
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        onChangeInFile: function (event) {
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                this.isNeedFileExtension = false;
                return false;
            }
            this.isNeedFileExtension = true;
        },
        /**
         * 格式化发票税额
         */
        taxAmountFormatDecimal: function (row) {

            return vm.numberFormat(null, null, row.taxAmount);
        },

        transactionNetCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionNetCost);
        },
        purchasingVatDecimal: function (row) {

            return vm.numberFormat(null, null, row.purchasingVat);
        },
        transactionEstLandedCostDecimal: function (row) {

            return vm.numberFormat(null, null, row.transactionEstLandedCost);
        }
    }
});
//use
function format2(value) {
    if (value < 10) {
        return "0" + value;
    } else {
        return value;
    }
}
//end

//自定义加法运算
function addNum (num1, num2) {
    var sq1,sq2,m;
    try {
        sq1 = num1.toString().split(".")[1].length;
    }
    catch (e) {
        sq1 = 0;
    }
    try {
        sq2 = num2.toString().split(".")[1].length;
    }
    catch (e) {
        sq2 = 0;
    }
    m = Math.pow(100,Math.max(sq1, sq2));
    return (num1 * m + num2 * m) / m;
}

//减法取绝对值
function Subtr(arg1,arg2){
    var r1,r2,m,n;
    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}
    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}
    m=Math.pow(100,Math.max(r1,r2));
    n=(r1>=r2)?r1:r2;
    return Math.abs(((arg1*m-arg2*m)/m).toFixed(n));
}





/**
 * 格式化时间
 * @param cellvalue
 * @returns {string}
 */
function formaterDate(cellvalue) {
    if (cellvalue == null) {
        return '';
    }
    return cellvalue.substring(0, 10);
}

function decimal(cellvalue) {
    if (cellvalue != null) {
        var val = Math.round(cellvalue * 100) / 100;
        return val.formatMoney1();
    }
    return "0";
}

Number.prototype.formatMoney1 = function (places, symbol, thousand, decimal) {
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var number = this,
        negative = number < 0 ? "-" : "",
        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
        j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};



/**
 *格式化金额
 */
function formatMoney(value){
    return Vue.prototype.numberFormat(null,null,value);
}


/**
 * 发票号码验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceNoValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.invoiceNo = t.value.replace(reg,'');
};

/**
 * 发票验证码输入格式
 * @param t 当前的input
 */
function verificationCheckNoValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.checkNo = t.value.replace(reg,'');
};

/**
 * 发票代码验证数据输入格式
 * @param t 当前的input
 */
function verificationInvoiceCodeValue(t) {
    var reg = /[^\d]/g;
    vm.invoicequery.invoiceCode = t.value.replace(reg, '');
};
function verificationPoCodeValue(t) {
    var reg = /[^\d]/g;
    vm.poquery.pocode = t.value.replace(reg, '');
};
function verificationClaimNoValue(t) {
    var reg = /[^\d]/g;
    vm.claimquery.claimno = t.value.replace(reg, '');
};
/**
 * 根据发票代码获取发票类型
 */
function getFplx(fpdm) {
    var fplx = "";
    if (fpdm.length == 12) {
        var zero=fpdm.substring(0,1);
        var lastTwo=fpdm.substring(10,12)
        if(zero=="0" && (lastTwo=="04" || lastTwo=="05")){
            fplx="04";
        }

    } else if (fpdm.length == 10) {
        var fplxflag = fpdm.substring(7, 8);
       if (fplxflag == "6" || fplxflag == "3") {
            fplx = "04";
        }
    }
    return fplx;
}


function formatInvoiceStatus(val){

    if(val==0){
        return "正常";
    }
    if(val==1){
        return "失控";
    }
    if(val==2){
        return "作废";
    }
    if(val==3){
        return "红冲";
    }if(val==4){
        return "异常";
    }else{
        return "";
    }

}
function formatSourceSystem(val){
    if(val==0){
        return "采集";
    }else if(val==1){
        return "查验";
    }else{
        return "录入";
    }
}


function formatQsType(val){
    if(val==0){
        return "扫码签收";
    }
    if(val==1){
        return "扫描仪签收";
    }
    if(val==2){
        return "app签收";
    }
    if(val==3){
        return "导入签收";
    }
    if(val==4){
        return "手工签收";
    }else{
        return "pdf上传签收";
    }
}
function formatOutReason(val){
    if(val==1){
        return "免税项目用";
    }
    if(val==2){
        return "集体福利,个人消费";
    }
    if(val==3){
        return "非正常损失";
    }
    if(val==4){
        return "简易计税方法征税项目用";
    }
    if(val==5){
        return "免抵退税办法不得抵扣的进项税额";
    }
    if(val==6){
        return "纳税检查调减进项税额";
    }
    if(val==7){
        return "红字专用发票通知单注明的进项税额";
    }else{
        return "上期留抵税额抵减欠税";
    }
}
function detailDateFormat(value){
    var tempInvoiceDate=new Date(value);
    var tempYear=tempInvoiceDate.getFullYear()+"年";
    var tempMonth=tempInvoiceDate.getMonth()+1;
    var tempDay=tempInvoiceDate.getDate()+"日";
    var temp=tempYear+tempMonth+"月"+tempDay;
    return temp;
}