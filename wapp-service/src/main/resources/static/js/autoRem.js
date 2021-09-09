//
////用于自动计算rem的基准值
//(function(doc, win) {
//    var html = doc.getElementsByTagName("html")[0],
//    // orientationchange->手机屏幕转屏事件
//    // resize->页面大小改变事件(一边pc端有用)
//        reEvt = "orientationchange" in win ? "orientationchange" : "resize",
//
//        reFontSize = function() {
//            var clientW = doc.documentElement.clientWidth || doc.body.clientWidth;
//            if(!clientW) {
//                return;
//            }
//            //console.log(clientW);
//            doc.getElementsByTagName("body")[0].style.fontSize = html.style.fontSize = 27 * (clientW / 375) + "px";
//    }
//    win.addEventListener(reEvt, reFontSize);
//    // DOMContentLoaded->dom加载完就执行,onload要dom/css/js都加载完才执行
//    doc.addEventListener("DOMContentLoaded", reFontSize);
//})(document, window);


//用于自动计算rem的基准值
(function(doc, win) {
   win.addEventListener("resize", reFont(doc));
   doc.addEventListener("DOMContentLoaded", reFont(doc));
})(document, window);

function reFont(doc){
   var clientW = doc.documentElement.clientWidth || doc.body.clientWidth;
   if(!clientW) {
       return;
   }
   doc.getElementsByTagName("html")[0].style.fontSize = 27 * (clientW / 375) + "px";
}