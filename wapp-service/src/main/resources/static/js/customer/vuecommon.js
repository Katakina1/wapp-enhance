
//请求超时处理
Vue.http.options._timeout = 60000;
Vue.http.interceptors.push((request, next) => {
    var timeout;
    // 這裡改用 _timeout ，就不會觸發原本的
    if (request._timeout) {
        // 一樣綁定一個定時器，但是這裡是只要觸發了，就立即返回 response ， 並且這邊自定義了 status 和 statusText
        timeout = setTimeout(() => {
            next(request.respondWith(request.body, {
                status: 408,
                statusText: '请求超时'
            }));
        }, request._timeout);
    }
    next((response) => {
        clearTimeout(timeout);
    });
});
Vue.http.interceptors.push(function (request, next) {
    request.headers.set('token', token); //setting request.headers
    next(function (response) {
        if (response.body.code === 401) { //与后台约定登录失效的返回码
            alert("登录超时，请重新登录", function () {
                parent.location.href = baseURL + 'login.html';
            });
        }
        return response
    })
});
Vue.prototype.numberFormat = function (row, column, cellValue) {
    /*
               * 参数说明：
               * number：要格式化的数字
               * decimals：保留几位小数
               * dec_point：小数点符号
               * thousands_sep：千分位符号
               * */
    if(cellValue==null || cellValue==='' || cellValue == undefined){
        return "一 一";
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
};

Vue.prototype.numberFormat2 = function (row, column, cellValue) {
    /*
               * 参数说明：
               * number：要格式化的数字
               * decimals：保留几位小数
               * dec_point：小数点符号
               * thousands_sep：千分位符号
               * */
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
};

Vue.prototype.insertItemToArray = function (array, index, item) {
    array.splice(index, 0, item);
};

Vue.prototype.openConfirm = function (vm, msg, successBack, errorBack) {
    parent.layer.confirm(msg, {btn: ['确定', '取消']},
        function () {//确定事件
            if (typeof(successBack) === "function") {
                parent.layer.closeAll('dialog');
                successBack();
            }
        });
};

Vue.prototype.formatterNull = function (row, column, cellValue) {
    if (cellValue == null || cellValue == "" || cellValue == undefined) {
        return "一 一";
    } else {
        return cellValue;
    }
};

Vue.prototype.showCellTooltip = function (row, column, cellValue) {
    return "<template><el-tooltip class=\"item\" effect=\"dark\" content=\"Bottom Center 提示文字\" placement=\"bottom\">" +
        "      <span>" + cellValue + "</span>" +
        "    </el-tooltip></template>"
};

Vue.prototype.trimStr = function (str) {
    if (str != undefined && str != null && str != '') {
        var result = str.replace(/(^\s+)|(\s+$)/g,"");
        result = result.replace(/\s/g,"");
        return result;
    } else {
        return str;
    }
};