
//获取浏览器页面可见高度和宽度
var _PageHeight = document.documentElement.clientHeight,
    _PageWidth = document.documentElement.clientWidth;
//计算loading框距离顶部和左部的距离（loading框的宽度为2.15rem，高度为0.61rem）
var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
    _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;
//在页面未加载完毕之前显示的loading Html自定义内容
var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight +
    'px;top:0;background:white;opacity:98;filter:alpha(opacity=98);z-index:10000;">' +
    '<div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width: auto; height: 0.57rem; line-height: 0.57rem; padding-left: 0.5rem; padding-right: 0.05rem; background: #fff ' +
    'url("img/mainrolling.gif") no-repeat scroll 0.05rem 0.1rem; border: 0.02rem solid #95B8E7; color: #red; font-family:\'Microsoft YaHei\';">' +
    '页面加载中，请等待...</div></div>';
//呈现loading效果
document.write(_LoadingHtml);
//监听加载状态改变
document.onreadystatechange = completeLoading;

//加载状态为complete时移除loading效果
function completeLoading() {
    if (document.readyState == "complete") {
        var loadingMask = document.getElementById('loadingDiv');
        loadingMask.parentNode.removeChild(loadingMask);
    }
}