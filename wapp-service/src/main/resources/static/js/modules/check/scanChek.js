
$(document).ready(function(){
    var scanner = document.getElementById("Scanner");
    var sc = new Object;

    /**
     *  封装扫描仪方法
     * */
    //初始化扫描核心
    sc.initialize = function () {
        var result = scanner.ScannerInitialize();
        return result;
    }

    //终止扫描核心
    sc.terminate = function () {
        var result = scanner.ScannerTerminate(0);
        return result;
    }

    /* //扫多张（异步）
    sc.getPages = function () {
        scanner.ScanPagesAsync(document.cookie);
    } */
    //扫多张

    sc.getPages = function () {
         var host = location.href.substring(0, location.href.indexOf(location.pathname))+baseURL;
         host=host.substring(0,host.length-1)
        // var host = location.href.substring(0, location.href.indexOf(location.pathname))+baseURL+'rest/invoice/sign/uploadImg';
        var gfId = "";

        var userAccount ="";
        scanner.ScanPagesAsync(document.cookie,host,gfId);
    }

    //sc.initialize();

    // 扫描
    function scanPages() {
        if(sc.initialize() != 0){
            alert("初始化扫描仪失败！请稍后再试");
            return ;
        }
        sc.getPages();
    }

    // 取消
    function cancel() {
        sc.terminate();
    }


    vm = new Vue({
        el:'#rrapp',
        data:{
            q:{
                invoiceType:'1',
                scanDetails: ''
            }
        },
        methods: {
            startScan: function () {
                if(vm.q.invoiceType==null){
                    alert("请选择发票类型");
                }else{
                    scanPages();

                    // var gfTaxNo = "";
                    // var invoiceType =vm.q.invoiceType;
                    // var data = {"invoices":"","gfTaxNo":"","invoiceType":""};
                    // if(vm.q.invoiceType==1){
                    // //data.invoices = JSON.stringify(rtnData.rtnData);
                    //     //data.invoices = '[{"scanId":"20180228161354757511787909805413","invoiceCode":"4403172130","invoiceNo":"23418447","invoiceDate":"171011","gfTaxNo":"91110108MA004CPN95","xfTaxNo":"91440300587000184","totalAmount":"1200.00","invoiceAmount":"1132.08","taxAmount":"67.92","checkCode":"","qrCode":"01,01,4300171130,06834828,-73576.07,20180224,,C807,","uploadURI":"http://103.237.3.25:8082/rest/invoice/sign/uploadImg"},{"scanId":"20180228161358308159898179567153","invoiceCode":"4300171130","invoiceNo":"06834820","invoiceDate":"180224","gfTaxNo":"91110108MA004CPN95","xfTaxNo":"91430100582765184L","totalAmount":"-8198.00","invoiceAmount":"-7006.84","taxAmount":"-1191.16","checkCode":"","qrCode":"01,01,4300171130,06834820,-7006.84,20180224,,EA84,","uploadURI":"http://103.237.3.25:8082/rest/invoice/sign/uploadImg"}]';
                    //     data.invoices = '[{"scanId":"20180228161354757511787909805413","invoiceCode":"4403172130","invoiceNo":"23418447","invoiceDate":"171011","gfTaxNo":"91110108MA004CPN95","xfTaxNo":"","totalAmount":"","invoiceAmount":"113.08","taxAmount":"67.92","checkCode":"","qrCode":"01,01,4300171130,06834828,-73576.07,20180224,,C807,","uploadURI":"http://103.237.3.25:8082/rest/invoice/sign/uploadImg"}]';
                    //
                    //     data.gfTaxNo = gfTaxNo;
                    //     data.invoiceType =invoiceType;
                    // }else if(vm.q.invoiceType==2){
                    //     data.invoices = '[{"scanId":"20180228161354757511787909805413","invoiceCode":"1100173320","invoiceNo":"4892830","invoiceDate":"171125","gfTaxNo":"91110108MA004CPN95","xfTaxNo":"911101146621615087","totalAmount":"283.00","invoiceAmount":"274.76","taxAmount":"8.24","checkCode":"123","qrCode":"01,01,4300171130,06834828,-73576.07,20180224,,C807,","uploadURI":"http://103.237.3.25:8082/rest/invoice/sign/uploadImg"}]';
                    //     data.gfTaxNo = gfTaxNo;
                    //     data.invoiceType =invoiceType;
                    // }
                    // sign(data);
                }



            },restScanner:function () {
                cancel();
                //重置页面
                history.go(0)
            }
        }
    })


    edit= new Vue({
        el:'#editScan',
        data:{
            recordInvoice:{},
            edit:{
                invoiceNo:null,
                invoiceCode:null,
                invoiceDate:null,
                gfTaxNo:null,
                xfTaxNo:null,
                invoiceAmount:null,
                taxAmount:null,
                totalAmount:null,
                checkCode:null
            }
        }
    })

    function addCellAttr(rowId, val, rawObject, cm, rdata) {
        if(rawObject.notes != "签收成功" ){
            return "style='color:red'";
        }
    }

    function invoiceTypeImg(cellvalue, options, rowObject) {
        var invoiceType=getFplx(rowObject.invoiceCode);
        if (invoiceType == "01") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/special-invoice.png">';
        } else if (invoiceType == "03") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/motor-vehicles.png">';
        } else if (invoiceType == "04") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/plain-invoice.png">';
        } else if (invoiceType == "10") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/einvoice.png">';
        } else if (invoiceType == "11") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/roll-invoice.png">';
        } else if (invoiceType == "14") {
            return cellvalue + '<image style="margin-left:0.18rem;height:0.2rem;width:0.2rem" src="../../img/einvoice.png">';
        }else {
            return cellvalue
        }
    };

    function getFplx(fpdm) {
        console.log(fpdm);
        var  fplx="";
        if (fpdm.length==12){
            var fplxflag=fpdm.substring(7,8);
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
            if (fplxflag=="2" && !fpdm.substring(0,1)=="0") {
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

    var rtnData={invoiceList:[]}
    $("#jqGrid").jqGrid({
        data: rtnData.invoiceList,
        datatype: "local",
        multiselect: true,
        multiboxonly:true,
        beforeSelectRow: function () {
            $("#jqGrid").jqGrid('resetSelection');
            return(true);
        },//js方法
        colModel: [
            { label: '扫描描述', name: 'notes', width: 120,align:"left",cellattr: addCellAttr },
            { label: '签收结果', name: 'invoiceStatus', width: 80 ,align:"center"},
            { label: '发票代码', name: 'invoiceCode', width: 80 ,align:"center"},
            { label: '发票号码', name: 'invoiceNo', width: 100 ,align:"center", formatter:invoiceTypeImg},
            { label: '开票日期', name: 'invoiceDate' ,align:"center"},
            { label: '购方税号', name: 'gfTaxNo', width: 120 ,align:"center"},
            { label: '销方税号', name: 'xfTaxNo', width: 120 ,align:"center"},
            { label: '金额', name: 'invoiceAmount', width: 60 , align:"right"},
            { label: '税额', name: 'taxAmount', width: 60 , align:"right"},
            { label: '校验码', name: 'checkCode', width: 40,hidden : true ,align:"center"},
            { label: '操作', name: 'scanId', width: 140 ,align:"center",title:false,formatter:function (value, options, row) {

                    //签收成功的发票，操作仅保留图片按钮,重复扫描什么都不显示
                    if(row.notes=="重复扫描"||row.notes=="选择类型与实际扫描发票类型不一致"){
                        return '';
                    }else {
                            return '<a  class="btn " title="图片"  onClick="imgRow(\'' + value + '\')">图片</a>' +
                                '<a  class="btn "  title="修改" onClick="modifiedRow(this,\'' + row.id + '\',\'' + row.scanId + '\',\'' + row.invoiceType + '\',\'' + row.invoiceNo + '\')">修改</a>'+
                                '<a  class="btn " title="删除" onClick="deleCteRow(this,\'' + row.uuid + '\')">删除</a>';

                    }
                }}

        ],
        height: 'auto',
        scrollOffset:0,
        rownumbers: true,
        rownumWidth: 55,
        autowidth:true,
        gridComplete:function(){
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" });

            //给序号添加标题
            $("#jqGrid").jqGrid('setLabel','rn', '序号', {'text-align':'left'},'');
        }, loadComplete: function(xhr) {
            //隐藏勾选框
            $("#jqGrid").setGridParam().hideCol("cb").trigger("reloadGrid");
        }
    });




});
var vm;
var edit;
function sign(data){
    var url='';
    var invoice_type = data.invoiceType;
    if(invoice_type==1){
        url=baseURL+'rest/invoice/sign/signWithRecord';
    }else{
        url=baseURL+'rest/invoice/sign/signWithoutRecord';
    }
    $.ajax({
        type: 'post',
        url: url,
        data: data,
        dataType:'text',
        contentType:'application/x-www-form-urlencoded; charset=utf-8',
        success: success
    });
}
var scanNum=0;
var successNum=0;
var qsFailNum=0;
var rkFailNum=0;


function success(result){
    var rtnData = $.parseJSON(result);
    // scanNum+=rtnData.scanNum;
    // successNum+=rtnData.successNum;
    // qsFailNum+=rtnData.qsFailNum;
    // rkFailNum+=rtnData.rkFailNum;
    scanNum=rtnData.scanNum;
    successNum=rtnData.successNum;
    qsFailNum=rtnData.qsFailNum;
    rkFailNum=rtnData.rkFailNum;
    //扫描详情
    if(rtnData.type == 0){
        var scanInfo = '扫描数量：' + scanNum
            + '\n签收成功数量：' + successNum + '\n签收失败数量：' + qsFailNum + '\n重复扫描数量：' + rkFailNum ;

        vm.q.scanDetails=scanInfo;
        console.log(vm.q.scanDetails);
        console.log(scanInfo);
    }
    //表格
    if(rtnData.invoiceList == undefined){
        return;
    }
    $.each(rtnData.invoiceList, function(i,val){
        $("#jqGrid").jqGrid("addRowData", Math.floor(Math.random()*60000), val, "first");
    });


}
function imgRow(curObj){
    var scanId = curObj;
    var url=baseURL+'rest/invoice/sign/getImg?scanId='+scanId;
    var img = "<img src='" + url + "' style='width:100%;height:100%;'/>";
    parent.layer.open({
        type: 1,
        shade: false,
        title: false, //不显示标题
        area: ['9.22rem', '5.4rem'],
        content: img, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响
        cancel: function () {
            //layer.msg('图片查看结束！', { time: 5000, icon: 6 });
        }
        ///content:[baseURL+'rest/invoice/sign/getImg?scanId='+scanId]
    })

}
function deleCteRow(t, curObj){
    $(t).parent().click();
    var gr= getSelectedRow();
    var rowId = curObj;

    // layer.open({
    //         title: ['删除确认','text-align:center;padding:0.0rem'],
    //         area: ['4.0rem','2.2rem'],
    //         skin: 'demo-class'
    //     ,btn: ['取消','确定']
    //     , btnAlign: 'c'
    //     ,shade: 0 //遮罩透明度
    //
    //     ,content: '<div style="text-align:center">删除后将无法恢复，确认删除？</div>'
    //         ,btn1: function (index) {
    //             layer.close(index)
    //         },btn2: function (index) {
    //                 $.ajax({
    //                     'type': 'POST',
    //                     'url': baseURL+'rest/invoice/sign/invoiceDelete.json?uuId=' + rowId,
    //                     'dataType':'text',
    //                     'contentType':'application/x-www-form-urlencoded; charset=utf-8',
    //                     'success': function(){
    //                         $("#jqGrid").jqGrid('delRowData',gr);
    //                     }
    //                 });
    //         }
    // });

    layer.open({
        type:1
        ,title: ['','text-align:center;padding:0.0rem;    background-color: #fff;border-bottom-width: 0']
        ,skin: 'demo-class'
        ,closeBtn: 0
        ,shade: 0
        ,content: '<div style="text-align:center;"><h4 style="font-weight:bold;">删除确认</h4></div><div style="text-align:center;padding-top: 0.4rem">删除后将无法恢复，确认删除？</div>'
        ,area:['4.0rem','2.2rem']
        ,btn: ['取消', '确认']
        , btnAlign: 'c'
        ,btn1: function (index) {
            layer.close(index)
        },btn2: function (index) {
                $.ajax({
                    'type': 'POST',
                    'url': baseURL+'rest/invoice/sign/invoiceDelete.json?uuId=' + rowId,
                    'dataType':'text',
                    'contentType':'application/x-www-form-urlencoded; charset=utf-8',
                    'success': function(){
                        $("#jqGrid").jqGrid('delRowData',gr);
                    }
                });
        }
    });

    // confirm("删除后将不予恢复，确认删除？",function(){

    // })


}

function modifiedRow(t,curObj,scanId,type,no){
    $(t).parent().click();
    var gr= getSelectedRow();
    var rowId = curObj;
    var rowData = $("#jqGrid").jqGrid("getRowData",gr);
    var btn=["保存"];

    edit.edit.invoiceNo=no;
    edit.edit.invoiceCode=rowData.invoiceCode;
    edit.edit.invoiceDate=rowData.invoiceDate;
    edit.edit.gfTaxNo=rowData.gfTaxNo;
    edit.edit.xfTaxNo=rowData.xfTaxNo;
    edit.edit.invoiceAmount=rowData.invoiceAmount;
    edit.edit.taxAmount=rowData.taxAmount;
    edit.edit.checkCode=rowData.checkCode;
    edit.edit.totalAmount=(parseFloat(rowData.invoiceAmount)+parseFloat(rowData.taxAmount)).toFixed(2);
    if(type=='01'){


        //获取底账数据
        $.ajax({
            type: 'post',
            url: baseURL+'rest/invoice/sign/getRecordInvoice',
            data: {invoiceNo:edit.edit.invoiceNo,invoiceCode:edit.edit.invoiceCode},
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded; charset=utf-8',
            success: function (data) {
                edit.recordInvoice=data.msg;
                if(edit.recordInvoice.invoiceDate!=null){
                    var str1=edit.recordInvoice.invoiceDate;
                    var invoiceDate=new Date(Date.parse(str1.replace(/-/g, "/")));
                    edit.recordInvoice.invoiceDate=invoiceDate.getFullYear().toString()+ ("0" + (invoiceDate.getMonth() + 1)).slice(-2) + ("0" + invoiceDate.getDate()).slice(-2);

                }
                edit.recordInvoice.totalAmount=edit.recordInvoice.totalAmount.toFixed(2);

            },error:function(XMLHttpRequest, textStatus, errorThrown){
                edit.recordInvoice={};
            }
        });

        btn.push("一键同步");
        $('#editCheckCodeDiv').hide();
    }else{
        $('#editCheckCodeDiv').show();
        edit.recordInvoice={};
    }
    $('#editImg').attr("src",baseURL+'rest/invoice/sign/getImg?scanId='+scanId)
    layer.open({
        type: 1,
        // skin: 'layui-layer-molv',
        title: "修改",
        area: ['12.0rem', '5.7rem'],
        shadeClose: true,
        skin: 'demo-class2',
        btn: btn,
        content: $("#editScan"),
        btn1: function (index) {
            var url='';
            var invoice_type = type;
            if(invoice_type=='01'){
                url=baseURL+'rest/invoice/sign/signWithRecord';
            }else{
                url=baseURL+'rest/invoice/sign/signWithoutRecord';
            }
            var gfTaxNo = "";
            var data = {"invoices":"","gfTaxNo":"","invoiceType":"","id":rowId };
            //data.invoices = JSON.stringify(rtnData.rtnData);

            data.invoices= '['+JSON.stringify(edit.edit)+']';

            data.invoiceType =vm.q.invoiceType;

            $.ajax({
                type: 'post',
                url: url,
                data: data,
                dataType:'json',
                contentType:'application/x-www-form-urlencoded; charset=utf-8',
                success: function(data){
                    // var rtnData = $.parseJSON(data);
                    var rtnData=data;
                    //表格
                    if(rtnData.invoiceList == undefined){
                        return;
                    }

                    $("#jqGrid").jqGrid('setRowData',gr,rtnData.invoiceList[0])
                    if(rtnData.invoiceList[0].notes=="签收成功"){
                        $("#jqGrid").jqGrid('setCell',gr,"notes","",{'color':'#333'});
                    }else{
                        $("#jqGrid").jqGrid('setCell',gr,"notes","",{'color':'red'});
                    }

                    // $("#jqGrid").jqGrid('setCell',gr,"notes",rtnData.invoiceList[0].notes);
                    // $("#jqGrid").jqGrid('setCell',gr,"invoiceStatus",rtnData.invoiceList[0].invoiceStatus);
                    // $("#jqGrid").jqGrid('setCell',gr,"invoiceCode",rtnData.invoiceList[0].invoiceCode);
                    // $("#jqGrid").jqGrid('setCell',gr,"invoiceDate",rtnData.invoiceList[0].invoiceDate);
                    // $("#jqGrid").jqGrid('setCell',gr,"gfName",rtnData.invoiceList[0].gfName);
                    // $("#jqGrid").jqGrid('setCell',gr,"xfName",rtnData.invoiceList[0].xfName);
                    // $("#jqGrid").jqGrid('setCell',gr,"invoiceAmount",rtnData.invoiceList[0].invoiceAmount);
                    // $("#jqGrid").jqGrid('setCell',gr,"taxAmount",rtnData.invoiceList[0].taxAmount);
                    // $("#jqGrid").jqGrid('setCell',gr,"gfTaxNo",rtnData.invoiceList[0].gfTaxNo);
                    // $("#jqGrid").jqGrid('setCell',gr,"xfTaxNo",rtnData.invoiceList[0].xfTaxNo);
                    // $("#jqGrid").jqGrid('setCell',gr,"scanId",rtnData.invoiceList[0].scanId);

                    alert("保存成功");
                    layer.close(index)
                }
            });
        },
        btn2:function(){
            edit.edit.invoiceDate=edit.recordInvoice.invoiceDate;
            edit.edit.gfTaxNo=edit.recordInvoice.gfTaxNo;
            edit.edit.xfTaxNo=edit.recordInvoice.xfTaxNo;
            edit.edit.invoiceAmount=edit.recordInvoice.invoiceAmount;
            edit.edit.taxAmount=edit.recordInvoice.taxAmount;
            edit.edit.totalAmount=edit.recordInvoice.totalAmount;
            return false;
        }

    });
}