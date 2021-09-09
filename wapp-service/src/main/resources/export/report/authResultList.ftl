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

        table {
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
<div id="div-export">
    <div align="center"><span class="bold bigSize">发票认证结果清单（认证相符）</span></div>
    <div align="center"><span>所属期（${rzhBelongDate?substring(0,4)}年${rzhBelongDate?substring(4,6)}月）</span></div>
    <div>
        <div id="lab-tax-name" align="left" style="float:left"><span id="taxpayer_name">${taxName}</span></div>
        <div id="lab-tax-no" align="right"><span id="taxpayer_no">${gfName}</span></div>
    </div>
    <div>
        <table border="1" style="text-align: center">
            <tr>
                <td class="bold"><div style="width: 20px">序号</div></td>
                <td class="bold"><div style="width: 70px">发票代码</div></td>
                <td class="bold"><div style="width: 55px">发票号码</div></td>
                <td class="bold"><div style="width: 55px">开票日期</div></td>
                <td class="bold"><div style="width: 120px">销方税号</div></td>
                <td class="bold"><div style="width: 155px">销方名称</div></td>
                <td class="bold"><div style="width: 60px">金额</div></td>
                <td class="bold"><div style="width: 60px">税额</div></td>
                <td class="bold"><div style="width: 55px">认证时间</div></td>
            </tr>
            <#list resultList as item>
                <tr>
                    <td><div style="width: 20px">${item_index+1}</div></td>
                    <td style="text-align: left"><div style="width: 70px">${item.invoiceCode}</div></td>
                    <td><div style="width: 55px">${item.invoiceNo}</div></td>
                    <td><div style="width: 55px">${item.invoiceDate?substring(0,10)}</div></td>
                    <td style="text-align: left"><div style="width: 120px">${item.xfTaxNo}</div></td>
                    <td style="text-align: left"><div style="width: 155px;word-wrap: break-word">${item.xfName}</div></td>
                    <td style="text-align: right"><div style="width: 60px">${item.invoiceAmount}</div></td>
                    <td style="text-align: right"><div style="width: 55px">${item.taxAmount}</div></td>
                <#if (item.rzhDate)??>
                    <td><div style="width: 60px">${item.rzhDate?string("yyyy-MM-dd")}</div></td>
                <#else>
                    <td><div style="width: 60px">—— ——</div></td>
                </#if>
                </tr>
            </#list>
            <tr>
                <td class="noRightBorder bold" colspan="2">合计:</td>
                <td class="noRightBorder noLeftBorder bold" colspan="2">份数: ${totalData.totalCount}</td>
                <td class="noRightBorder noLeftBorder" colspan="2"></td>
                <td style="text-align: right" class="noRightBorder noLeftBorder bold">${totalData.totalAmount}</td>
                <td style="text-align: right" class="noRightBorder noLeftBorder bold">${totalData.totalTax}</td>
                <td class="noLeftBorder"></td>
            </tr>
            <tr>
                <td class="noRightBorder bold" colspan="4">制表时间: ${currentDate}</td>
                <td class="noRightBorder noLeftBorder" colspan="2"></td>
                <td class="noRightBorder noLeftBorder bold" colspan="2">金额: 元</td>
                <td class="noLeftBorder"></td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>