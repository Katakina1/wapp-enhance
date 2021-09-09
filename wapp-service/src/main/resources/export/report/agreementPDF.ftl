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
    <div align="center"><span class="bold bigSize" style="font-weight:bold;font-size:28px"> 协 议 说 明 书</span> </div>


    <div ><p style="float:right;padding-right: 200px">申请编号：${(redTicketDataSerialNumber)!}</p></div>
<br/>

    <div ><p  style="float:left">甲方 ：沃尔玛（中国）投资有限公司</p></div>
    <div  style="float:left">
        <p>乙方 :${user.username}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span >（供应商号：${(user.usercode)!} ）</span></p>
        <p>就折让事宜，甲、乙双方兹达成协议如下：</p>
        <p>1. 双方确认，甲方于<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(0,4)}</span>年<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(5,7)}</span>月<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(8,10)}</span>日至<span style="text-decoration:underline">${invoiceEntity.pdfDateEnd?substring(0,4)}</span>年<span style="text-decoration:underline">${invoiceEntity.pdfDateEnd?substring(5,7)}</span>月<span style="text-decoration:underline">${invoiceEntity.pdfDateEnd?substring(8,10)}</span>日自乙方购入如下表商品，由于商品质量等原因，</p>
        <p>甲方于<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(0,4)}</span>年<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(5,7)}</span>月<span style="text-decoration:underline">${invoiceEntity.pdfDateStart?substring(8,10)}</span>日至<span style="text-decoration:underline">${invoiceEntity.pdfDateEnd?substring(0,4)}</span>年<span style="text-decoration:underline">${invoiceEntity.pdfDateEnd?substring(5,7)}月${invoiceEntity.pdfDateEnd?substring(8,10)}</span>日将上述已购入的部分商品作如下折让：</p>


        <table border="1" style="text-align: center" width="90%" class="table1">
            <tr>
                <td class="bold"  rowspan="3"><div  >产品名称</div></td>
                <td class="bold"  rowspan="3"><div >单位</div></td>
                <td class="bold" colspan="5"><div > 购进商品</div></td>
                <td class="bold" colspan="2"><div>折让商品</div></td>

            </tr>
            <tr>
                <td class="bold"  rowspan="2"><div  >数量</div></td>
                <td class="bold"  rowspan="2"><div >单价(不含税)</div></td>
                <td class="bold"  rowspan="2"><div >货款 (不含税)</div></td>
                <td class="bold"  rowspan="2"><div >税额</div></td>
                <td class="bold" rowspan="2"><div > 购进发票号</div></td>

                <td class="bold"  rowspan="2"><div >折让金额 (不含税)</div></td>
                <td class="bold"  rowspan="2"><div>折让税额</div></td>

            </tr>
            <tr></tr>
         <#list resultList as item>
            <tr>
                <td><div >${item.goodsName}</div></td>
                <td><div>${item.unit}</div></td>


                <td style="text-align: left"><div >${(item.num)!}</div></td>
                <td style="text-align: left"><div >${(item.unitPrice)!}</div></td>
                <td style="text-align: right"><div >${(item.detailAmount)!}</div></td>
                <td style="text-align: right"><div >${(item.taxAmount)!}</div></td>
                <td style="text-align: right"><div >${(item.invoiceNo)!}</div></td>

                <td style="text-align: right"><div >${(item.redRushAmount)!}</div></td>
                <td style="text-align: right"><div >${(item.redRushTaxAmount)!}</div></td>
            </tr>
         </#list>
            <tr>
                <td >合计:</td>
                <td><div ></div></td>
                <td><div ></div></td>
                <td><div></div></td>
                <td><div ></div></td>
                <td><div ></div></td>
                <td><div></div></td>
                <td style="text-align: right"><div >${(totalData.redPushTotalAmount)!}</div></td>
                <td style="text-align: right"><div >${(totalData.redPushTotalTaxAmount)!}</div></td>
            </tr>
        </table>
        <br/>
        <p>2. 本协议一式两份，自双方盖章之日起生效，甲、乙双方税务机关各执一份,具有同等法律效力。</p>

        <table>
            <tr>
                <td>甲方：</td>
                <td>沃尔玛（中国）投资有限公司</td>
                <td>乙方：</td>
                <td>${(user.username)!}</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>地址：</td>
                <td>深圳市福田区农林路69号深国投广场二号楼 2-5层及三号楼1-12层</td>
                <td>地址：</td>
                <td>${(user.address)!}</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>电话：</td>
                <td>0755-21511388</td>
                <td>电话：</td>
                <td>${(user.cellphone)!}</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>传真：</td>
                <td>0755-21510888</td>
                <td>传真：</td>
                <td>${(user.phone)!}</td>
            </tr>
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>日期：</td>
                <td>${(currentDate)!}</td>
                <td>日期：</td>
                <td>${(currentDate)!}</td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>