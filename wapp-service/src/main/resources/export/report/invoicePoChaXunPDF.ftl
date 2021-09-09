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
    <div align="center"><span class="bold bigSize" style="font-weight:bold;font-size:28px"> 金额差异信息表</span> </div>
<br/>
    <table  style="text-align: center" width="100%">
        <tr>
            <td style="text-align: left">合资公司代码:WI</td>
            <td style="text-align: left">打印日期:${(currentDate)!}</td>
        </tr>
        <tr>
            <td style="text-align: left">合资公司名称:沃尔玛(中国)投资有限公司</td>
            <td style="text-align: left">打印时间:${currentTime}</td>
        </tr>
        <tr>
            <td style="text-align: left">供应商号:${(venderid)!}</td>
            <td style="text-align: left">打印人:${(user.loginname)!}</td>
        </tr>
        <tr>
            <td style="text-align: left">供应商名称:${name}</td>
        </tr>
    </table>
    <hr/>
    <div align="center"><span style="font-size: 15px">发票信息</span></div>
    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold" ><div >发票组号</div></td>
            <td class="bold" ><div>发票号码</div></td>
            <td class="bold" ><div>发票日期</div></td>
            <td class="bold" ><div>发票类型</div></td>
            <td class="bold" ><div>税率(%)</div></td>
            <td class="bold" ><div>税额</div></td>
            <td class="bold" ><div>价税合计</div></td>
        </tr>
<#--po集合-->
         <#list list as item>
         <#--多个发票一个po-->
             <#if (item.invoiceEntityList?size>0) >
                 <#list item.invoiceEntityList as invoice>
                    <tr>
                        <td><div >${(item.matchno)!}</div></td>
                        <td><div >${(invoice.invoiceNo)!}</div></td>
                        <td>
                            <#if invoice.invoiceDate??>
                                ${invoice.invoiceDate?substring(0,10)}
                            </#if>
                        </td>
                        <#if invoice.invoiceType="01">
                        <td><div >增值税专用发票</div></td>
                        <#elseif invoice.invoiceType="03">
                        <td><div >机动车销售统一发票</div></td>
                        <#elseif invoice.invoiceType="04">
                        <td><div >增值税普通发票</div></td>
                        <#elseif invoice.invoiceType="10">
                        <td><div >电子发票</div></td>
                        <#elseif invoice.invoiceType="11">
                        <td><div >卷票</div></td>
                        <#elseif invoice.invoiceType="14">
                        <td><div >通行费发票</div></td>
                        </#if>
                        <td><div >${(invoice.taxRate)!}</div></td>
                        <td><div >${invoice.taxAmount?string[",##0.00"]}</div></td>
                        <td><div >${invoice.totalAmount?string[",##0.00"]}</div></td>
                    </tr>
                 </#list>
             <#else>
                <tr>
                    <td><div >没有记录</div></td>
                    <td><div ></div></td>
                    <td><div ></div></td>
                    <td><div ></div></td>
                    <td><div ></div></td>
                    <td><div ></div></td>
                </tr>
             </#if>
    </table>
    <div align="right"><span style="font-weight: lighter">已开发票不含税金额合计：${sttAmount?string[",##0.00"]}</span></div>
    <p></p>

    <div align="center"><span style="font-size: 15px">结款明细</span></div>
    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold" ><div >订单号</div></td>
            <td class="bold" ><div>不含税金额</div></td>
        </tr>
    <#if (item.poEntityList?size>0) >
         <#list item.poEntityList as po>
         <tr>
             <td><div >${(po.pocode)!}</div></td>
             <td><div >${po.amountpaid?string[",##0.00"]}</div></td>
         </tr>
         </#list>
    <#else>
        <tr>
         <td><div >没有记录</div></td>
         <td><div ></div></td>
        </tr>
    </#if>

    </table>

    <div align="right"><span style="font-weight: lighter">订单金额合计：${poAmount?string[",##0.00"]}</span></div>
    <p></p>

    <div align="center"><span style="font-size: 15px">结款索赔明细</span></div>
    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold" ><div >索赔单号</div></td>
            <td class="bold" ><div>不含税金额</div></td>
        </tr>
      <#if (item.claimEntityList?size>0) >
         <#list item.claimEntityList as claim>
                    <tr>
                        <td><div >${(claim.claimno)!}</div></td>
                        <td><div>${claim.claimAmount?string[",##0.00"]}</div></td>
                    </tr>
         </#list>
      <#else>
        <tr>
            <td><div >没有记录</div></td>
            <td><div ></div></td>
        </tr>
      </#if>
    </table>

    <div align="right"><span style="font-weight: lighter">索赔金额合计：${claimAmount?string[",##0.00"]}</span></div>
    <p></p>
         </#list>
    <p>发票差异金额合计：${cover?string[",##0.00"]}元，我司在沃尔玛结算平台的匹配数据，及关系确认具有法律效力，不再重复出具打印文件。</p>

</div>
</body>
</html>