
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;


var actionlistType = [
  {
    key: 1,
    name: "扫描处理"
  },
  {
    key: 2,
    name: "扫描处理报告"
  },
  {
    key: 3,
    name: "HOST匹配成功报告"
  },
  {
    key: 4,
    name: "HOST匹配失败报告"
  }
];


var vm = new Vue({
    el: '#rrapp',
    data: {
//    	datalist: {
//            total: 10,
//            list: []
//        },
//        formlist: {
//            curr: 1, // 当前页数
//            size: 10, // 每页条数
//            operationStatus: ""
//        },
//        bodyheight: "",
//        loading: true
    	pagereload:false,
    	dialogdetail: false,
        detaildata: "",
        datastateshowhide: true,
        bodyheight: "",
        tiplist: true,
        loading: true,
        selectvalue: "99",
        selectdata: [
          {
            value: "99",
            title: "全部"
          },
          {
            value: "1",
            title: "已读"
          },
          {
            value: "2",
            title: "未读"
          }
        ],
        getnews: {
          id: null,
          type: null
        },
        pagingConfigure: 20,
        formlist: {
          curr: 1, // 当前页数
          size: 20, // 每页条数
          operationStatus: ""
        },
        datalist: {
          total: 10,
          list: []
        }
    },
    created() {
        this.bodyheight = window.innerHeight - 260 + "px";
        this.getList();
        $('.display_bar').css('display', 'block')
      },
    methods: {
    	updateclick() {
	      console.log("刷新");
	      this.getList();
	    },
    	getList: function () {
    	      const that = this;
    	      this.$http.post(baseURL + 'messageControl/list',
    	                this.formlist,
    	                {
    	                    'headers': {
    	                        "token": token
    	                    }
    	                }).then(function (res) {
    	                var r = res.body;

    	                if (r.code == 0) {
                      	  var dataList = r.dataList;
                      	  if(dataList.length>0){
                      		  that.tiplist = false;
                                that.loading = false;
                      		  that.datalist.list = dataList;
                      	  } else {
                      		  that.tiplist = true;
                      	  }
                        }
    	            });
//    	      $.ajax({
//                  url: baseURL + 'messageControl/list',
//                  type: "POST",
//                  contentType: "application/json",
//                  data: {
//                	  operationStatus: this.formlist.operationStatus
//                  },
//                  success: function (r) {
//                      if (r.code == 0) {
//                    	  var dataList = r.dataList;
//                    	  if(dataList.length>0){
//                    		  that.tiplist = false;
//                              that.loading = false;
//                    		  that.datalist.list = dataList;
//                    	  } else {
//                    		  that.tiplist = true;
//                    	  }
//                      }
//
//                  }
//              });
	    },
	     
	 // 下拉选择框
	    selectchange(val) {
	      // console.log(val)
	      switch (val) {
	        case "99":
	          this.formlist.operationStatus = "";
	          break;
	        case "1":
	          this.formlist.operationStatus = 1;
	          break;
	        case "2":
	          this.formlist.operationStatus = 0;
	          break;
	      }
	      this.getList();
	    },
	    
	 // 进入详情
	    licilck(val, i) {
	      const that = this;
	      that.getnews.type = null;
	      // 判断当前消息是否为未读
	      if (val.operationStatus === "0") {
	    	$.ajax({
	          url: baseURL + 'message/clickcommit',
	          type: "POST",
	          data: JSON.stringify(val.id),
	          contentType: "application/json",
	          success: function (response) {
	        	  if (response.code === 0) {
	        		  that.getList();
	  	            //这里是弹出dialog
	        		  that.detaildata = val;
	        		  that.dialogdetail = true;
	        		  that.getMessageCount();
	        	  }
	
	          }
	        });
	    	  
//	    	console.log(that.getnews);
//	        NewsState(that.getnews).then(response => {
//	          console.log(response);
//	          if (response.code === 0) {
//	            this.getList();
//	            //这里是弹出dialog
//	            this.detaildata = val;
//	            this.dialogdetail = true;
//	          } else {
//	            this.$message.error(response.data.message);
//	          }
//	        });
	      } else {
	        //这里是弹出dialog
	        this.detaildata = val;
	        this.dialogdetail = true;
	      }
	    },
	    
	 // 全部标记已读
	    newsallclick() {
	      const that = this;
	      that.getnews.type = "0";
	      
	      $.ajax({
            url: baseURL + 'allclickcommit',
            type: "POST",
            contentType: "application/json",
            success: function (r) {
                if (r.code == 0) {
                	that.$message({
      	              message: "已将" + r.count + "消息标记为已读",
      	              type: "success"
      	            });
                }

            }
        });
	      this.getMessageCount();
	      this.getList();
      },
      getMessageCount: function () {
          $.getJSON(baseURL + "message/getMessageCount", {operationStatus : 0}, function (r) {
          	localStorage.setItem("newsnumberonlyone", (r.messageCount == null) ? 0 : r.messageCount);
          });
      },
      // 下载文件
      uploadfileclick(data, name) {
          console.log(data, name)
          // console.log(axiosRequest)
          // let url = 'core/ftp/download?serviceType=2&downloadId=1133732526987096066'
          let url = data.substring(4);
          let nametitle = name.replace("导出成功，可以下载","");
          window.location.href=baseURL + url;
        }
    }
});
