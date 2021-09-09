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
    <div align="center"><span class="bold bigSize" style="font-weight:bold;font-size:28px"> 商 品 结 款 清 单</span> </div>
<br/>
    <table  style="text-align: center" width="90%" >
        <tr>
            <td class="bold"  ><div  >供应商号:</div></td>
            <td  ><div >${(user.usercode)!}</div></td>
            <td class="bold" ><div > 名称:</div></td>
            <td  ><div>
            ${(user.username)!}
            </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div  >打印人:</div></td>
            <td><div >${(user.loginname)!}</div></td>
            <td class="bold" ><div > 打印时间:</div></td>
            <td  ><div>${currentDate}</div></td>
        </tr>
    </table>
    <hr/>

    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
           <#-- <td class="bold" ><div >JVCode</div></td>-->
            <td class="bold" ><div > 组号</div></td>
            <td class="bold" ><div>发票号码</div></td>
            <td class="bold" ><div>开票日期</div></td>
            <td class="bold" ><div>税率(%)</div></td>
            <td class="bold" ><div>税额</div></td>
            <td class="bold" ><div>价税合计</div></td>
            <td class="bold"  ><div  >订单/索赔号</div></td>
            <td class="bold"  ><div >订单金额</div></td>
            <td class="bold"  ><div >差异金额</div></td>

            <td class="bold" ><div>提交日期</div></td>
        </tr>

        <#--po集合-->
         <#list list as item>
            <#--多个发票一个po-->
             <#if (item.invoiceEntityList?size>1) >
                 <#list item.invoiceEntityList as invoice>
                    <tr>
                       <#-- <td><div >${(invoice.jvcode)!}</div></td>-->
                        <td ><div >${(invoice.matchno)!}</div></td>
                        <td ><div >${(invoice.invoiceNo)!}</div></td>
                        <td>
                            <div >${invoice.invoiceDate?substring(0,10)}</div>
                        </td>
                        <td style="text-align: right"><div >${(invoice.taxRate)!}</div></td>
                        <td style="text-align: right"><div >${invoice.taxAmount?string(",##0.00")}</div></td>
                        <td style="text-align: right"><div > ${invoice.totalAmount?string(",##0.00")}</div></td>
                         <#list item.poEntityList as po>
                             <#if po_index == 0>
                            <td><div >${(po.pocode)!}</div></td>
                             </#if>
                         </#list>
                        <td style="text-align: right"> <div>${invoice.invoiceAmount?string(",##0.00")}</div></td>
                        <td style="text-align: right"><div >${item.matchCover?string(",##0.00")}</div></td>
                        <td>
                            <div >
                             <#if item.matchDate??>
                                 ${item.matchDate?string("yyyy-MM-dd")}
                             </#if>
                            </div>
                        </td>
                    </tr>
                </#list>
             <#--索赔-->
                 <#if (item.claimEntityList?size>0) >
                     <#list item.claimEntityList as claim>
                        <tr>
                           <#-- <#list item.poEntityList as po>
                                <#if po_index == 0>
                                 <td ><div >${(po.jvcode)!}</div></td>
                                </#if>
                            </#list>-->
                            <td ><div >${(item.matchno)!}</div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: left"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td><div >${(claim.claimno)!}</div></td>
                            <td style="text-align: right"><div>${claim.claimAmount?string(",##0.00")}</div></td>
                            <td><div></div></td>
                            <td style="text-align: right"><div ></div></td>
                        </tr>
                     </#list>
                </#if>
             </#if>


         <#--1个发票多个po-->
             <#if (item.invoiceEntityList?size<2) >
                 <#list item.poEntityList as po>
                <tr>
                     <#list item.invoiceEntityList as invoice>
                         <#if invoice_index == 0>
                        <td ><div >${(invoice.matchno)!}</div></td>
                        <td ><div >${(invoice.invoiceNo)!}</div></td>
                             <td>
                                 <div >${invoice.invoiceDate?substring(0,10)}</div>
                           </td>
                        <td style="text-align: right"><div >${(invoice.taxRate)!}</div></td>
                        <td style="text-align: right"><div >${invoice.taxAmount?string(",##0.00")}</div></td>
                        <td style="text-align: right"><div >${invoice.totalAmount?string(",##0.00")}</div></td>
                         <td><div >${(po.pocode)!}</div></td>
                         <td style="text-align: right"><div>${po.amountpaid?string(",##0.00")}</div></td>
                          <td style="text-align: right"><div >${item.matchCover?string(",##0.00")}</div></td>
                            <td>
                                <#if item.matchDate??>
                                    ${item.matchDate?string("yyyy-MM-dd")}
                                </#if>
                            </td>
                         </#if>
                    </#list>
                </tr>
                 </#list>
             <#--索赔-->
                 <#if (item.claimEntityList?size>0) >
                     <#list item.claimEntityList as claim>
                        <tr>
                           <#-- <#list item.invoiceEntityList as invoice>
                                <#if invoice_index == 0>
                                 <td ><div >${(invoice.jvcode)!}</div></td>
                                </#if>
                            </#list>-->
                            <td ><div >${(item.matchno)!}</div></td>
                            <td style="text-align: left"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td style="text-align: right"><div ></div></td>
                            <td><div >${(claim.claimno)!}</div></td>
                            <td style="text-align: right"><div>${claim.claimAmount?string(",##0.00")}</div></td>
                            <td><div></div></td>
                            <td style="text-align: right"><div ></div></td>
                        </tr>
                     </#list>
                 </#if>
             </#if>
        </#list>
    </table>
</div>
</body>
</html>