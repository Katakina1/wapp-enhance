Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;
var personalTaxNumber=123456789012345;



var vm=new Vue({
    el: '#rrapp',
    data:{

        questionPaper:true,
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
        claimChangeData:[],
        poChangeData:[],
        countChangeData:[],


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


        addClaimChangeDataShow:false,
        addPoChangeDataShow:false,
        addCountChangeDataShow:false,
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
        }

    },mounted: function () {
        //use

        this.queryPart();
        this.queryCity();

        this.queryDetails();
        $('.defaultItem').removeClass('hideItem');
        this.queryGf();
        this.queryDetail();


    },
    methods: {



        questionTypeChange:function () {

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
        },
        partionChange:function () {

            this.queryQuestionType(vm.forms.partition);
            $(".po").addClass("hideItem");
            $(".count").addClass("hideItem");
            $(".claim").addClass("hideItem");

        },
        addClaimChangeData:function () {
            vm.addClaimChangeDataShow=true;
        },
        addCountChangeData:function () {
            vm.addCountChangeDataShow=true;
        },
        addPoChangeData:function () {
            vm.addPoChangeDataShow=true;
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
                        if(vm.forms.partition==='1005'){
                            if(vm.forms.storeNbr=='' || vm.forms.storeNbr==null ){
                                flag=false;
                            }
                        }
                        if(flag){
                            if((vm.forms.department=='' || vm.forms.department==null)&& vm.forms.partition==='1005' ){
                                alert("部门不能为空！");

                            }else{
                                var otherSave={
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
                                    storeNbr:vm.forms.storeNbr,
                                    fileList:vm.questionPaperFile
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
                                            vm.forms.purchaser='';
                                            vm.forms.jvcode='';
                                            vm.forms.telephone='';
                                            vm.forms.department='';
                                            vm.forms.invoiceNo='';
                                            vm.forms.invoiceDate='';
                                            vm.forms.totalAmount='';
                                            vm.forms.storeNbr='';
                                            vm.questionPaperFile=[];
                                            alert(r.msg)
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

        addClaimChangeSave:function (formName) {
            vm.$refs[formName].validate(function (valid) {

                    if (valid ) {

                        if( ( vm.addClaimChangeForm.systemAmount - vm.addClaimChangeForm.vendorAmount ) * vm.addClaimChangeForm.number != vm.addClaimChangeForm.difference ){
                            alert("所填写内容不符合（系统单价-供应商单价）*数量=金额差额  关系，请重新填写!");
                            return false;
                        }

                        vm.addClaimChangeDataShow=false;

                        vm.claimChangeData= vm.claimChangeData.concat(JSON.parse(JSON.stringify(vm.addClaimChangeForm)));

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
                        vm.addCountChangeDataShow=false;
                        vm.countChangeData= vm.countChangeData.concat(JSON.parse(JSON.stringify(vm.addCountChangeForm)))
                        // vm.addCountChangeForm.resetField()
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
                        // vm.addPoChangeForm.resetField()
                    }else{
                        return false;
                    }
                }
            );
        },
        deleteClaimChangeData:function (scope) {
            this.claimChangeData.splice(scope.$index,1);
        },
        deletePoChangeData:function (scope) {
            this.poChangeData.splice(scope.$index,1);
        },
        deleteCountChangeData:function (scope) {
            this.countChangeData.splice(scope.$index,1);
        },



        addClaimChangeCancel:function () {
            vm.addClaimChangeDataShow=false;
        },
        addCountChangeCancel:function () {
            vm.addCountChangeDataShow=false;
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
                        if(vm.citys.length>0){
                            vm.forms.city=vm.citys[0].value;

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
                vm.forms.description="供应商自述由于换货时商品单价调整，导致索赔与冲账金额相差 0.0000 元，冲账差异明细详见附件。请采购确认供应商所述是否属实，是否返还冲账差异金额。谢谢。问题单说明表、索赔单、索赔冲账差异明细（TRAC）详见附件。"
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
        },
        matchFailureCancel:function () {
            vm.matchFailureShow=false;
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
        /**
         *end
         */
        invoiceDateFormat:function (row) {
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


        //use
        uploadQuestionFile: function () {
            var fileValue = document.getElementById("fileId").value;
            if(fileValue==null || fileValue==''){
                alert("请选择文件");
                return;
            }
            vm.uploadLoading = true;
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
                    vm.uploadLoading = false;
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
                        for (var i = 0; i < r.List.length; i++) {
                            var gf = {};
                            gf.value = r.List[i].orgcode;
                            gf.label = r.List[i].orgcode;
                            gf.gfName=r.List[i].orgname;
                            gfs.push(gf);
                        }
                        vm.gfsh = gfs;
                        if(vm.gfsh.length>0){
                            vm.queryData1.gfName=vm.gfsh[0].gfName;
                            vm.queryData1.orgcode=vm.gfsh[0].value;
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
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                vm.listLoading4=true;
                var formData = new FormData();
                formData.append('file', this.file);
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
                                            type=false;
                                            return;
                                        }
                                    })
                                    if(type){                                    vm.invoiceData = vm.invoiceData.concat(response.data.invoiceQueryList[index]);
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
        //end







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