//验证两个时间相差一年以内
function validateDateTypeA(startDate,endDate){
    debugger;
    if ( new Date(startDate).getTime()+1000*60*60*24*365 < new Date(endDate).getTime()) {
        $(".el-col-14 .el-form-item__content div.el-date-editor:first-child").append('<div class="validateDateTypeA checkMsg">日期选择跨度不可超过1年</div>');
        stop();
    }else if(new Date(endDate).getTime() < new Date(startDate).getTime()){
        $(".el-col-14 .el-form-item__content div.el-date-editor:first-child").append('<div class="validateDateTypeA checkMsg">结束时间不可小于开始时间</div>');
       stop();
    }
    $(".validateDateTypeA").remove();
}
//数字类型验证,自动补0
//tipElClass是el-col一层的class标识
function validateOnlyNumber(value,length,tipElClass,tipInfo,len){
    debugger;
    if(!isNaN(value) && value.length<=len){
        var xx = length - (value+"").length;
        if(xx>0){
            var zero="";
            for(var i = 0;i<xx;i++){
                zero+="0";
            }
            value=zero+value;
        }
        $(".validateOnlyNumber").remove();
        return value;
    }
    $("."+tipElClass+" .el-form-item__content>div").append('<div class="validateOnlyNumber checkMsg">'+tipInfo+'</div>');
    stop();
}

/* 格式化时间 String to yyyy-MM */
function dateFormatStrToYM(cellValue){
    if (cellValue != null && cellValue != "") {
        return cellValue.substring(0, 4) + '-' + cellValue.substring(4, 6) ;
    } else {
        return "";
    }
}
/* 格式化时间 String to yyyy-MM-dd */
function dateFormatStrToYMD(cellValue){
    if (cellValue != null && cellValue != "") {
        return cellValue.substring(0, 4) + '-' + cellValue.substring(4, 6) + '-' + cellValue.substring(6, 8);
    } else {
        return "";
    }
}
//金钱格式格式化
function moneyFormat(cellValue) {
    if(cellValue==null || cellValue==='' || cellValue == undefined){
        return "—— ——";
    }
    var number = cellValue;
    var decimals = 2;
    var dec_point = ".";
    var thousands_sep = ",";
    number = (number + '').replace(/[^0-9+-Ee.]/g, '');
    var n = !isFinite(+number) ? 0 : +number,
        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
        sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
        dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
        s = '',
        toFixedFix = function (n, prec) {
            var k = Math.pow(10, prec);
            return '' + Math.round(n * k) / k;
        };

    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
    var re = /(-?\d+)(\d{3})/;
    while (re.test(s[0])) {
        s[0] = s[0].replace(re, "$1" + sep + "$2");
    }

    if ((s[1] || '').length < prec) {
        s[1] = s[1] || '';
        s[1] += new Array(prec - s[1].length + 1).join('0');
    }
    return s.join(dec);
}



//停止当前线程
function stop(){
    throw SyntaxError();
}









//传入字号,字体,文字,返回文字宽度
function textSize(fontSize,fontFamily,text){
    var span = document.createElement("span");
    var result = {};
    result.width = span.offsetWidth;
    result.height = span.offsetHeight;
    span.style.visibility = "hidden";
    span.style.fontSize = fontSize;
    span.style.fontFamily = fontFamily;
    span.style.display = "inline-block";
    document.body.appendChild(span);
    if(typeof span.textContent != "undefined"){
      span.textContent = text;
    }else{
      span.innerText = text;
    }
    result.width = parseFloat(window.getComputedStyle(span).width) - result.width;
    result.height = parseFloat(window.getComputedStyle(span).height) - result.height;
    span.style.display = "none";
    return result.width;
}
//修改表格宽度,
//offset是在算好的宽度上增加的值
//magnification是倍率
//maxWidth最大宽度
//minWidth最小宽度
var flag = 0;
function superChange(offset,magnification,maxWidth,minWidth){

    if(maxWidth == null){maxWidth = 500;}
    if(minWidth == null){minWidth = 40;}
    if(offset == null){offset = 20;}
    if(magnification == null){magnification = 1;}

    var ifnull = $(".el-table .el-table__body-wrapper tr").length;
    if(ifnull == 0 && flag < 20){
        console.log("数据未加载完成,正在重试");
        flag++;
        window.setTimeout(function(){
            superChange(offset,magnification,maxWidth,minWidth);
        },100);
        return false;
    }

    //计算部分
    var tablewidths = [];
    //$(".el-table__body-wrapper").css("overflow-x","scroll");el-table--scrollable-x
    $(".el-table").addClass("el-table--scrollable-x");
    var fontsize = parseFloat( $(".el-table .el-table__header-wrapper th").eq(0).children("div").css("font-size") );
    var fontsize2 = parseFloat( $(".el-table .el-table__body-wrapper td").eq(0).children("div").css("font-size") );
    //th部分计算
    $.each($(".el-table .el-table__header-wrapper th"),function(index,th){
    	var str = $(th).children("div").text();
    	var fontFamily = $(th).children("div").css("fontFamily");
    	var textLength = textSize(fontsize,fontFamily,str) * parseFloat(magnification) + offset;
    	textLength = textLength > maxWidth ? maxWidth : textLength;
    	textLength = textLength < minWidth ? minWidth : textLength;
    	tablewidths.push(textLength);
    });
    console.log("th 计 算 宽 度 --> ",tablewidths);
    //td部分计算
    $.each($(".el-table .el-table__body-wrapper tr"),function(index,tr){
    	$.each($(tr).children("td"),function(index,td){
    		var str = $(td).children("div").text();
    		var fontFamily = $(td).children("div").css("fontFamily");
    		if(str == "" || str == null){
    			str = $(td).children("div>:nth-child(n)").text();
    			fontFamily = $(td).children("div").css("fontFamily");
    		}
    		var textLength = textSize(fontsize,fontFamily,str) * parseFloat(magnification) + offset;

            //加上图片的宽度
    		var imgLength = $(td).children("div").children("img").width();
    		if(imgLength != null){ textLength += imgLength; }

    		textLength = textLength > maxWidth ? maxWidth : textLength;
            textLength = textLength < minWidth ? minWidth : textLength;
    		if(parseInt(tablewidths[index]) < parseInt(textLength)){
    			tablewidths[index] = textLength;
    		}
    	});
    });
    console.log("td计算最终宽度 --> ",tablewidths);

    //赋值部分
    var allWidth = 0;
    for(var i = 0;i < tablewidths.length;i++){
        allWidth += tablewidths[i];
    }
    console.log("总宽度 ----->",allWidth);
    console.log("--------------表格宽度自动修正完成--------------");
    if($(".el-table .el-table__header-wrapper table").width() < allWidth){
        $(".el-table .el-table__header-wrapper table").width(allWidth);
        $(".el-table .el-table__body-wrapper table").width(allWidth);
    }

    $.each($(".el-table .el-table__header-wrapper col"),function(index,col){
    	$(col).attr("width",tablewidths[index]);
    })
    $.each($(".el-table .el-table__body-wrapper col"),function(index,col){
    	$(col).attr("width",tablewidths[index]);
    })
    $.each($(".el-table .el-table__body-wrapper tr"),function(index,tr){
    	$.each($(tr).children("td"),function(index,td){
    		$(td).children("div").css("width","100%");
    	});
    });

    if($(".el-table .el-table__header-wrapper table").width() <= allWidth){
        $(".el-table .el-table__header-wrapper table").width(allWidth);
        $(".el-table .el-table__body-wrapper table").width(allWidth);
    }else if($(".el-table .el-table__header-wrapper table").width() - allWidth >= 16){
        $(".el-table .el-table__header-wrapper table").width($(".el-table .el-table__header-wrapper table").width()-17);
        $(".el-table .el-table__body-wrapper table").width($(".el-table .el-table__body-wrapper table").width()-17);
    }
}


var bigFlag = false;
function bands(){
    if(bigFlag){
        $("#layui-layer1").css("left","411px").css("width","9.22rem").css("top","160px").css("height",'5.4rem');
    }else{
        $("#layui-layer1").css("left","2%").css("width","96%").css("top","0rem").css("height",$(window).height()-10);
    }
}


//$(function(){
//    window.setTimeout(function(){
//        superChange();
//    },10000)
//})