<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title></title>
    <style type="text/css">
        body {
            margin-left: 15px;
            margin-right: 15px;
            font-family: Arial Unicode MS;
            font-size: 14px;
        }

        .table1 {
            margin: auto;
            width: 100%;
            border-collapse: collapse;
            border: 1px solid #444444;
        }
        .imgs {

            text-align:right
        }
        .esp {
            margin-left: 250px;/*离左边距离*/
        }


        .bigSize{font-size: large}
        .bold{font-weight: bold}
        .noRightBorder{border-right: none}
        .noLeftBorder{border-left: none}

    </style>
</head>
<body>
<div id="div-export" width="1300px">
    <div align="center"><span class=" bigSize">费 用 供 应 商 付 款 &nbsp;&nbsp;${(payOne)!}</span> </div>
    <div> <div><p  align="center">[${(settlementEntity.epsNo)!}]</p></div> <p height="110px" class="imgs"><img  src="${(settlementEntity.costNo)!}QRCode.png" height="100px" /></p></div>
   <table style="margin: auto;width: 100%;border-collapse: collapse; " >
        <tr>
            <td  ><div  >业务类型:</div></td>
            <td class="bold"  ><div >${(settlementEntity.serviceType)!}</div></td>
            <td class="bold" ><div ></div></td>
            <td class="bold" ><div></div></td>
        </tr>
        <tr>
            <td  ><div  >供应商号码：</div></td>
            <td class="bold"  ><div >${(settlementEntity.venderId)!}</div></td>
            <td  ><div >供应商名称：</div></td>
            <td class="bold" ><div>${(settlementEntity.venderName)!}</div></td>
        </tr>
        <tr>
            <td  ><div  >开户行名称:</div></td>
            <td class="bold"  ><div >${(settlementEntity.bankName)!}</div></td>
            <td ><div >银行账号:</div></td>
            <td class="bold" ><div>${(settlementEntity.bankAccount)!}</div></td>
        </tr>

        <tr>
            <td  ><div  >合同:</div></td>
            <td class="bold"  ><div >${(settlementEntity.contract)!}</div></td>
            <td ><div >总金额（人民币）:</div></td>
            <td class="bold" ><div>${settlementEntity.settlementAmount?string(",##0.00")}</div></td>
        </tr>

        <tr>
            <td  ><div >发票总额:</div></td>
            <td class="bold" ><div>${settlementEntity.settlementAmount?string(",##0.00")}</div></td>
            <td class="bold" ><div ></div></td>
            <td class="bold" ><div></div></td>

        </tr>

    </table>
       <hr/>

        <br/>
     <#list settlementEntity.invoiceRateList as item>
        <p>发票录入</p>

    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold">发票号码</td>
            <td class="bold">发票类型</td>
            <td class="bold">发票总额</td>
            <td class="bold">税率</td>
            <td class="bold">税额</td>
            <td class="bold">不含税额</td>
            <td class="bold">发票日期</td>
            <td class="bold">总金额（人民币）</td>
        </tr>
        <tr>
            <td>${(item.invoiceNo)!}</td>
            <td>
                 <#if item.invoiceType??>
                     <#if item.invoiceType == '01'>
                           增值税专用发票
                         <#else >
                            普通发票
                     </#if>
                 </#if>
            </td>
            <#assign sum =item.invoiceAmount - item.taxAmount>
            <td>  ${item.invoiceAmount?string(",##0.00")}</td>
            <td>${(item.taxRate)!}</td>
            <td>${item.taxAmount?string(",##0.00")}</td>
            <td>${sum?string(",##0.00")}</td>
            <td>
                <#if item.invoiceDate??>
                    ${item.invoiceDate?substring(0,10)}
                </#if>
            </td>
            <td>${item.invoiceAmount?string(",##0.00")}</td>
        </tr>
    </table>
         <#list item.costTableData as item2>
    <p>款项用途(用途栏不能超过100字)</p>

    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold">发票号码</td>
            <td class="bold">费用类型</td>
            <td class="bold">费用承担部门/店/JV</td>
            <td class="bold">费用发生时间</td>
            <td class="bold">用途</td>
            <td class="bold">金额</td>
            <td class="bold">项目代码</td>
        </tr>
        <tr>
            <td>${(item.invoiceNo)!}</td>
            <td>${(item2.costTypeName)!}</td>
            <td>${(item2.costDept)!}</td>
            <td> ${(item2.costTime)!}</td>
            <td>${(item2.costUse?html)}</td>
            <td>${item2.costAmount?string(",##0.00")}</td>
            <td>${(item2.projectCode)!}</td>
        </tr>
    </table >
    <br/>
         </#list>
     </#list>
    <br/>
    <div style="height: 60px">
        <div style="float:left; width: 20%; text-align: center; margin-top: 12px">
            <span style="font-size: 14px; font-weight: 700">备注:(200字以内)</span>
        </div>
        <div style="float:left; width: 70%;border: 1px solid #444444;">
            <td  style="width: 100%;height: 60px ">${(settlementEntity.remark?html)}</td>
        </div>
    </div>
    <br/>
</div>
</body>
</html>