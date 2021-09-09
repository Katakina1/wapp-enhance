

//生成菜单
var menuItem = Vue.extend({
    name: 'menu-item',
    props: {item: {}, index: 0},
    template: [
        '<li>',
        '<a  v-if="item.menulevel == 0" :href="item.menuaction">',
        '<i v-if="item.image != null" :class="item.image"></i>',
        '<span>{{item.menulabel}}</span>',
        '</a>',

        '<a  v-if="item.menulevel == 1" href="javascript:;">',
        '<i v-if="item.image != null" :class="item.image"></i>',
        '<span>{{item.menulabel}}<i class="fa fa-angle-right pull-right"></i></span>',
        '</a>',

        '<ul v-if="item.menulevel == 1" class="treeview-menu">',
        '<menu-item :item="item" :index="index" v-for="(item, index) in item.subList"></menu-item>',
        '</ul>',
        '<a v-if="item.menulevel == 2" :href="\'#\'+item.menuaction">' +
        '<i v-if="item.image != null" :class="item.image"></i>' +
        '<i v-else ></i> {{item.menulabel}}' +
        '</a>',

        '</li>'
    ].join('')
});

//iframe自适应
/*$(window).on('resize', function () {
    var $content = $('.content');
    $content.height($(this).height() - 120);
    $content.find('iframe').each(function () {
        $(this).height($content.height());
    });
}).resize();*/

//注册菜单组件
Vue.component('menuItem', menuItem);

var vm = new Vue({
    el: '#rrapp',
    data: {
        user: {},
        menuList: {},
        main: "main.html",
        password: '',
        newPassword: '',
        navTitle: "首页",
        navTag: '沃尔玛结算平台',
        messageCount: 0
    },
    mounted() {
    	setInterval(function(){
    		vm.messageCount = localStorage.getItem("newsnumberonlyone")
    		if (vm.messageCount > 0) {
    			$("#messageCountSpan").css('display','block'); 
    		} else {
    			$("#messageCountSpan").css('display','none'); 
    		}
    	},1000)
    },
    updated(){
    	debugger
    	vm.messageCount = localStorage.getItem("newsnumberonlyone") 
    },
    methods: {
//    	websokitnewsnum(){
////	      console.log("用户信息", this.$store.getters.pagingConfigure)
//	      var that = this;
//	      if ("WebSocket" in window) {
//	        // 打开一个 web socket
//	    	var username = localStorage.getItem("username");
//	        let passurl  = "ws://localhost:8080/dxhy/websocket/" + username
//	        window.webSocket = new WebSocket(passurl);  
//	        webSocket.onopen = function() {
//	          // Web Socket 已连接上，使用send() 方法发送数据
//	          let parameter = {
//	            token:  localStorage.getItem("token")
//	          };
//	          webSocket.send(JSON.stringify(parameter));
//	          console.log("数据发送中...");
//
//	        };
//	        webSocket.onmessage = function(evt) {
//	          var received_msg = evt.data;
//	          let val = JSON.parse(received_msg)
//	          console.log("websokit返回参数", val)
//	        };
//	        webSocket.onclose = function() {
//	          // 关闭 websocket
//	          that.loadingbar = false;
//	          console.log("监听到连接已关闭...");
//	        };
//	      } else {
//	        // 浏览器不支持 WebSocket
//	        this.$message.error("您的浏览器不支持 WebSocket!");
//	      }
//	    },
        getMenuList: function () {
            $.getJSON(baseURL + "sys/menu/nav", function (r) {
                vm.menuList = r.menuList;
                window.permissions = r.permissions;
            });
        },
        getUser: function () {
            $.getJSON(baseURL + "sys/user/info", function (r) {
                vm.user = r.user;
                var user = localStorage.setItem("username", vm.user.loginname);
                if(r.orgtype=='8'){
                    vm.main = 'main_vendor.html';
                }else{
                    vm.main = 'main.html';
                }
            });
        },
        getMessageCount: function () {
            $.getJSON(baseURL + "message/getMessageCount", {operationStatus : 0}, function (r) {
            	localStorage.setItem("newsnumberonlyone", (r.messageCount == null) ? 0 : r.messageCount);
            });
        },
        updatePassword: function () {
            layer.open({
                type: 1,
                skin: 'layui-layer-molv',
                title: "修改密码",
                area: ['5.5rem', '2.7rem'],
                shadeClose: false,
                content: jQuery("#passwordLayer"),
                btn: ['修改', '取消'],
                btn1: function (index) {
                    var data = "password=" + vm.password + "&newPassword=" + vm.newPassword;
                    $.ajax({
                        type: "POST",
                        url: baseURL + "sys/user/password",
                        data: data,
                        dataType: "json",
                        success: function (r) {
                            if (r.code == 0) {
                                layer.close(index);
                                layer.alert('修改成功', function () {
                                    location.reload();
                                });
                            } else {
                                layer.alert(r.msg);
                            }
                        }
                    });
                }
            });
        },
        logout: function () {
            //删除本地token
            localStorage.removeItem("token");
            localStorage.removeItem("announceShowTimes");
            //跳转到登录页面
            var hostHref = location.href;
            if(hostHref.indexOf("int")!=-1){
                parent.location.href = 'http://rl.wal-mart.com';
            }else if(hostHref.indexOf("ext")!=-1){
                parent.location.href ="https://retaillink.wal-mart.com";
            }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                parent.location.href ="https://retaillink.wal-mart.com";
            }else{
                parent.location.href = baseURL + 'login.html';
            }
            //location.href ="http://rl.dev.wal-mart.com/new_home/";
        },
        donate: function () {
            layer.open({
                type: 2,
                title: false,
                area: ['8.06rem', '4.67rem'],
                closeBtn: 1,
                shadeClose: false,
                content: ['http://cdn.dxhy.io/donate.jpg', 'no']
            });
        },
        switchClothes: function () {
            $("#hf").slideToggle("slow");
        },
        hrefNewUrl:function () {
            var newUserCode=window.btoa(vm.user.usercode);
            var usertype=vm.user.usertype;
            if(usertype==null | usertype==""){
                return;
            }
         window.open("https://wmtcontactcenter.walmartmobile.cn/cctapi/walmartapi/online/wapp?vendor_id="+newUserCode+"&type=product")
        }
    },
    created: function () {
        this.getMenuList();
        this.getUser();
        this.getMessageCount();
//        this.websokitnewsnum();
    },
    updated: function () {
        //路由
        var router = new Router();
        routerList(router, vm.menuList);
        router.start();
        router.add('#modules/message/messageDetail.html', function () {
        	 var url = window.location.hash;
             //替换iframe的url
             vm.main = url.replace('#', '');

      
             vm.navTitle = "导出消息列表"
             vm.navTag= "导出管理";
        });
    }
});


function routerList(router, menuList) {
    for (var key in menuList) {
        var menu = menuList[key];
        if (menu.isbottom == 1) {
            routerList(router, menu.subList);
        } else if (menu.isbottom == 0) {
            router.add('#' + menu.menuaction, function () {
                var url = window.location.hash;
                //替换iframe的url
                vm.main = url.replace('#', '');

                //导航菜单展开
                $(".treeview-menu li").removeClass("active");
                $(".sidebar-menu li").removeClass("active");
                $("a[href='" + url + "']").parents("li").addClass("active");

                vm.navTitle = $("a[href='" + url + "']").text();
                vm.navTag= $('.sidebar-menu>li.active>a>span').text();
            });
        }
    }
}