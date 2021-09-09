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
    <div align="center"><span class=" bigSize" style="font-weight:bold;font-size:28px"> 开具红字增值税专用发票信息表</span> </div>
<br/>

    <table border="1" style="text-align: center" width="90%" class="table1">
        <tr>
            <td class="bold"  ><div  >填开日期：</div></td>
            <td class="bold" colspan="2" ><div >${(tkDate)!}</div></td>
            <td class="bold" ><div > </div></td>
            <td class="bold" ><div></div></td>
            <td class="bold" ><div></div></td>
            <td class="bold" ><div></div></td>
            <td class="bold" ><div></div></td>
        </tr>
        <tr>
            <td class="bold"  rowspan="2" ><div  >销售方：</div></td>
            <td class="bold" ><div >名               称</div></td>
            <td class="bold" colspan="2" ><div > ${(xfName)!}</div></td>
            <td class="bold" rowspan="2"><div>购买方</div></td>
            <td class="bold" ><div>名               称</div></td>
            <td class="bold"  colspan="2"><div> ${(gfName)!}</div></td>
        </tr>

        <tr>
            <td class="bold" ><div >纳税人识别号</div></td>
            <td class="bold" colspan="2" ><div > ${(xfTaxno)!}</div></td>
            <td class="bold" ><div>纳税人识别号</div></td>
            <td class="bold"  colspan="2"><div> ${(gfTaxno)!}</div></td>
        </tr>
        <tr>
            <td class="bold" rowspan="3" ><div >开具
                红字
                专用
                发票
                内容</div></td>
            <td class="bold" colspan="3" ><div >货物（劳务服务）名称 </div></td>
            <td class="bold" colspan="2"><div>金额</div></td>
            <td class="bold"  ><div>税率 </div></td>
            <td class="bold"  ><div>税额 </div></td>
        </tr>
        <tr>
            <td class="bold" colspan="3" ><div >商品一批（详见清单）</div></td>
            <td class="bold" colspan="2"><div>${(amount)!}</div></td>
            <td class="bold"  ><div> ${(taxRate)!}</div></td>
            <td class="bold"  ><div> ${(taxAmount)!}</div></td>
        </tr>

        <tr>
            <td class="bold" colspan="3" ><div >合计 </div></td>
            <td class="bold" colspan="2"><div>${(amount)!}</div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> ${(taxAmount)!}</div></td>
        </tr>

        <tr>
            <td class="bold" rowspan="15" ><div >说明 </div></td>
            <td class="bold" ><div>一、购买方<input type="checkbox" checked="checked" readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>

        <tr>
            <td class="bold"  colspan="3"><div>     对应蓝字专用发票抵扣增值税销项税额情况：</div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>

        </tr>
        <tr>
            <td class="bold"  colspan="3"><div> 1. 已抵扣<input type="checkbox" checked="checked" readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>

        </tr>
        <tr>
            <td class="bold"  colspan="3"><div> 2. 未抵扣<input type="checkbox" readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>

        </tr>
        <tr>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  colspan="2"><div> （1）无法认证<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  colspan="3"><div> （2）纳税人识别号认证不符<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  colspan="3"><div> （3）增值税专用发票代码、号码认证不符<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  colspan="4"><div> （4）所购货物或劳务、服务不属于增值税扣税项目范围<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>

        <tr>
            <td class="bold"  colspan="4"><div>  对应蓝字专用发票的代码：____________________<span></span></div></td>
            <td class="bold"  colspan="2"><div>号码：_______________________<span></span> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  ><div> 二、销售方<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>

        <tr>
            <td class="bold"  colspan="4"><div>   1. 因开票有误购买方拒收的<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  colspan="4"><div>      2. 因开票有误等原因尚未交付的<input type="checkbox"  readonly="true"/></div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr>
            <td class="bold"  colspan="4"><div>       对应蓝字专用发票的代码：_______________<span></span></div></td>
            <td class="bold"  colspan="2"><div> 号码：_______________<span></span></div></td>
            <td class="bold"  ><div> </div></td>
        </tr>
        <tr style="height: 14px">
            <td   ><div></div></td>
            <td   ><div> </div></td>
            <td  ><div> </div></td>
            <td  ><div> </div></td>
            <td  ><div> </div></td>
            <td   ><div> </div></td>
            <td  ><div> </div></td>
        </tr>
        <tr>

            <td class="bold"  ><div>红字发
                票信息
                表编号</div></td>
            <td class="bold"  colspan="7"><div> ${(redNoticeNumber)!}</div></td>

        </tr>
    </table>
</div>
</body>
</html>