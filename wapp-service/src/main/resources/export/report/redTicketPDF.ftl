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
    <div align="center"><span class="bold bigSize" style="font-weight:bold;font-size:28px"> 红 票 清 单</span> </div>
<br/>
    <table  style="text-align: center" width="90%" >
        <tr>

            <td class="bold"  ><div  >供应商号:</div></td>

             <#list list as item>
                 <#if item_index == 0>
                    <td  ><div >${(item.venderid)!}</div></td>
                 </#if>
             </#list>
            <td class="bold" ><div > 名称:</div></td>
            <td  ><div>
            ${(user.username)!}
            </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div>打印人:</div></td>
            <td><div >${(user.loginname)!}</div></td>
            <td class="bold" ><div > 打印时间:</div></td>
            <td><div>${currentDate}</div></td>
        </tr>
    </table>
    <hr/>

    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <#--<td class="bold"  ><div  >生成红票序列号</div></td>-->

          <#--  <td class="bold" ><div > 红冲总金额</div></td>-->
                <td class="bold" ><div>序号</div></td>
            <td class="bold" ><div>红字通知单号</div></td>
            <td class="bold" ><div>红票代码</div></td>
            <td class="bold" ><div>红票号码</div></td>
                <td class="bold" ><div>开票日期</div></td>
            <td class="bold" ><div>金额</div></td>
            <td class="bold" ><div>税额</div></td>
            <td class="bold" ><div>税率(%)</div></td>
            <td class="bold" ><div>价税合计</div></td>
        </tr>


         <#list list as item>

                    <tr>
                        <td style="text-align: left"><div >${(item.indexNo)!}</div></td>
                        <td ><div >${(item.redNoticeNumber)!}</div></td>
                        <td> <div >${(item.invoiceCode)!}</div></td>
                        <td ><div >${(item.invoiceNo)!}</div></td>
                                 <td>
                                     <div >${item.invoiceDate?substring(0,10)}</div>
                                 </td>
                        <td style="text-align: right"><div >${item.invoiceAmount?string(",##0.00")}</div></td>
                        <td style="text-align: right"><div >${item.taxAmount?string(",##0.00")}</div></td>
                        <td style="text-align: right"><div >${(item.taxRateOne)!}</div></td>
                        <td style="text-align: right"><div >${item.totalAmount?string(",##0.00")}</div></td>
                    </tr>
        </#list>
    </table>
</div>
</body>
</html>