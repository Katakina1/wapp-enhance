<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <style type="text/css">
        body {
            margin-left: 15px;
            margin-right: 15px;
            font-family: Arial Unicode MS;
            font-size: 10px;
        }

        .table1 {
            margin: auto;
            width: 100%;
            border-collapse: collapse;
            border: 1px solid #444444;
        }

        .bigSize{font-size: large}
        .bold{font-weight: bold}
        .noRightBorder{border-right: none}
        .noLeftBorder{border-left: none}

    </style>
</head>
<body>
<div id="div-export" width="1300px">
    <div align="center"><span class="bold bigSize" style="font-weight:bold;font-size:28px">退 票 信 息 表</span> </div>



<br/>
        <table >
            <tr>
                <td></td>
                <td></td>
                <td> 退票日期：${(rebateDate)!}</td>
                <td></td>
            </tr>

            <tr>
                <td>发票共  ${(total)!}</td>
                <td>份</td>
                <td>供应商号:  ${(venderid)!}</td>
                <td></td>
            </tr>
            <tr>
                <td>邮寄方式：</td>
                <td></td>
                <td>  销货单位：${(vendername)!}</td>
                <td></td>
            </tr>
        </table>
    <br/>
    <p>一、退货内容：</p>
    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td>退票编号</td>
            <td>发票号码</td>
            <td>发票成本</td>
            <td>沃尔玛成本</td>
            <td>退票类型</td>
            <td>补充说明</td>
            <td>发票组号</td>
            <td>购货单位</td>
            <td>解决方法</td>
        </tr>
    <#list list as item>
            <tr>
                <td>${(item.refundNo)!}</td>
                <td>${(item.invoiceNo)!}</td>
                <td>${(item.invoiceAmount)!}</td>
                <td></td>
                <td>${(item.refundType)!}</td>
                <td>${(item.refundRemark)!}</td>
                <td></td>
                <td>${(item.gfName)!}</td>
                <td></td>
            </tr>
    </#list>
    </table>
    <br/>
        <p>二、金额差异解决方法（供应商选择解决方法时,只能选择一种方法,并要加盖公章）:</p>
        <p>   1.同意沃尔玛金额，重新开票</p>
        <p> 2.请及时扣减索赔,如果索赔从定案日期算起超过60日的，我司将在供应商已交来到期发票中直接扣除索赔款</p>
        <p>3.不同意沃尔玛金额，请先通过以下联系方法与我司联系后，再行结款。</p>
        <p> 供应商反馈解决方法，请在以上表格中解决方法栏内填写解决方法代码并盖公章。</p>


        <p> 三、重要提示：</p>
        <p>    1.请以订单为最小结算单位开具发票，并将同一订单项下的货款一次性结清.</p>
        <p>    2.请及时扣减索赔,如果索赔从定案日期算起超过60日的，我司将在供应商已交来到期发票中直接扣除索赔款</p>
        <p>     3.为防止贵司发票过期，在选择第3种解决方法时，请先与我司联系后再交发票结算。</p>
        <p>   4.供应商结款时，不再需要提供送货单，请自行妥善保存。</p>


        <p>四、联系方法：</p>
        <p>如有疑问请联系我司：电邮：cnhotln@wal-mart.com</p>
        <p>邮寄地址：深圳市福田区农林路69号深国投广场二号楼2-5层及三号楼1-12层</p>
        <p>收件人：沃尔玛（中国）投资有限公司  财务部顾客服务组收； 邮编:518040 </p>


    </div>

</body>
</html>