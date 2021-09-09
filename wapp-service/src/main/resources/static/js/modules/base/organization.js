
/**
 * 机构管理
 */
Vue.http.options.emulateJSON = true;
Vue.http.options.emulateHTTP = true;

var menu_ztree;
var orgid=0;


var setting = {
    showLine: true,
    checkable: true,
    view: {
        showIcon: false,
        expandSpeed: 'fast'
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "orgid",
            pIdKey: "parentid",
            rootPId: -1
        },
        key: {
            name: "orgname"
        }
    },
    callback: {
        onCollapse: treeOnCollapse,
        onExpand: treeOnExpand,
        onClick: function (event, treeId, treeNode) {

            vm.treeOnClick(event, treeId, treeNode);

            vm.company = treeNode.company;
            vm.columnShow = true;
            vm.items = [];
            vm.gfType=treeNode.orgtype;
            var orgType = treeNode.orgtype;

            //中心企业/集团：每个企业集团的一级目录。
            if ('0' == orgType) {
                vm.items = [{text: '中心企业', value: '1'}];

                //总部节点，不显示机构名称和机构编码列
                vm.columnShow = false;
            }

            //购方、销方：作为企业、集团的二级目录。
            if ('1' == orgType) {
                vm.items = [
                    {text: '购方虚机构', value: '2'},
                    {text: '销方虚机构', value: '3'}
                ];

                vm.company = treeNode.orglayer;
            }
            //购方下级可添加管理机构（区域）、购方企业
            if ('2' == orgType) {
                vm.items = [
                   // {text: '管理机构', value: '4'},
                    {text: '购方企业', value: '5'}
                    //暂不考虑
                    //{text: '购销双方', value: '6'},
                ]
            }

            if ('3' == orgType) {
                vm.items = [
                    {text: '销方企业', value: '8'}
                ]
            }

            if ('4' == orgType) {
                vm.items = [
                    {text: '购方企业', value: '5'}
                ]
            }

            //购方企业下面可添加门店。
            if ('5' == orgType) {
                vm.items = [
                    {text: '门店', value: '7'}
                ]
            }
        }
    }
};

var settingMenu = {
    showLine: true,
    checkable: true,
    view: {
        showIcon: false,
        expandSpeed: 'fast'
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "menuid",
            pIdKey: "parentid",
            rootPId: -1
        },
        key: {
            name: "menuname"
        }
    },
    check: {
        enable: true,
        nocheckInherit: true,
        chkboxType: {"Y": "ps", "N": "ps"}
    },
    callback: {
        onCheck: checkEvent
    }
};

var vm = new Vue({
    el: '#rrapp',
    data: {
        orgData: [],
        currentPage: 1,
        pageSize: PAGE_PARENT.PAGE_SIZE,
        pageList: PAGE_PARENT.PAGE_LIST,
        total: 0,
        totalPage: 1,
        orgModel: false,
        orgType:'',
        gfType:'',
        token: token,
        readonly: true,
        disabled: true,
        fieldShow: false,
        columnShow: false,
        fieldOrglayerShow: false,
        inputShow: false,
        isInsert: true,
        disabledOrgType: true,
        taxShow: false,
        isGf:false,
        tempTableData: [],
        queryForm: {
            orgname: null,
            orgcode: null,
            taxname: null,
            taxno: null,
            companyCode:null
        },
        multipleSelection: [],
        addOrUpdateForm: {
            orgtype: null,
            linkName: null,
            isUpdate: ""
        },
        orgid: null,
        isLeaf: false,
        orgLoading: false,
        orgNode: null,
        company: null,
        file:'',
        cfile:'',
        selectFileFlag: '未选择文件',
        selectcompanyFileFlag:'未选择文件',
        isNeedFileExtension:false,
        isNeedCFileExtension:false,
        orgRecord: [],
        addOrUpdateWin: false,
        addMenuWin: true,
        items: [],
        itemsDB: [],
        expandOrgId: []
    },
    mounted: function () {
        this.getOrgTree(null);
        this.addMenuWin = false;
    },
    methods: {
        getOrgTree: function (expandOrgIdSet) {
            //加载组织树
            $.post(baseURL + 'base/organization/getOrgTree', {orgldStr: expandOrgIdSet}, function (r) {
                if (r.code === 0) {
                    $.fn.zTree.init($("#orgTree"), setting, r.orgList);

                    if (vm.orgid != null) {
                        vm.selectTree();
                    }
                }else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        /**
         * 菜单勾选事件 - 勾选父级菜单
         */
        checkEvent: function (event, treeId, treeNode) {
            if (treeNode.checked && treeNode.menulevel != 0) {
                var parentNode = treeNode.getParentNode();
                menu_ztree.checkNode(parentNode, true, false);

                //递归勾选
                vm.checkEvent(event, treeId, parentNode);
            }
        },
        uploadFile: function (event) {
            if (!vm.isNeedFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (this.file == '' || this.file == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                const formData = new FormData();
                formData.append('file', vm.file);
                formData.append('token', vm.token);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                vm.tempTableData = vm.orgData;
                vm.orgData = [];
                vm.orgLoading = true;
                var flag = false
                var hh;
                const url = baseURL + "base/organization/JVImport";
                vm.$http.post(url, formData, config).then(function (response) {
                    vm.orgLoading = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    vm.selectFileFlag = '';
                    vm.file = '';
                    if(response.data.errorCount>0){
                        alert("请检查数据是否完整！");
                        return;
                    }
                    if (response.data.success) {
                        if (vm.tempTableData.length + response.data.reason.length + response.data.errorCount > 500) {
                            vm.tableData = this.tempTableData;
                            alert('导入数据超过500条，请修改模板！');
                            return;
                        }
                        for (var i = 0; i < response.data.reason.length; i++) {
                            allDataList.push(response.data.reason[i])
                        }
                        vm.orgData = allDataList;
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条");
                        vm.selectFileFlag = '';
                        vm.file = '';
                        $("#upload_form")[0].reset();
                    } else {
                        vm.orgData = vm.tempTableData;
                        alert(response.data.reason);
                        vm.selectFileFlag = '';
                        vm.file = '';
                        $("#upload_form")[0].reset();
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);

            }
        },
        onChangeFile: function (event) {

            const str = $("#file").val();
            const index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                vm.isNeedFileExtension = false;
                return false;
            } else {
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    vm.file = event.target.files[0];
                    vm.isNeedFileExtension = true;
                    //截取名称最后18位
                    vm.selectFileFlag = event.target.files[0].name;
                }
            }
        },
        showSelectFileWin() {
            this.selectFileFlag = '';
            this.file = '';
            $("#upload_form")[0].reset();
            $("#file").click();
        },
        uploadCFile: function (event) {
            if (!vm.isNeedCFileExtension) {
                alert("请上传excel文件格式!");
            } else {
                event.preventDefault();
                if (vm.cfile == '' || vm.cfile == undefined) {
                    alert("请选择excel文件!");
                    return;
                }
                allDataList=[];
                var errorJV=[];
                const formData = new FormData();
                formData.append('cfile', vm.cfile);
                formData.append('token', vm.ctoken);
                const config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                };
                vm.orgLoading = true;
                var flag = false
                var hh;
                const url = baseURL + "base/organization/jvAndStoreImport";
                vm.$http.post(url, formData, config).then(function (response) {
                    vm.orgLoading = false;
                    flag = true;
                    if(response.data.code !=undefined && response.data.code == 401) {
                        return;
                    }
                    if (response.data.success) {
                        for (var i = 0; i < response.data.reason.length; i++) {
                            allDataList.push(response.data.reason[i])
                        }
                        vm.orgData = allDataList;
                        if(response.data.errorCount>0){
                            for (var i = 0; i < response.data.errorlist.length; i++) {
                                errorJV.push(response.data.errorlist[i].orgcode)
                            }
                            alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条，以下JV不存在:["+
                                errorJV+"]");
                        } else {
                        alert("共计导入" + (response.data.reason.length + response.data.errorCount) + "条，成功" + response.data.reason.length + "条");
                        }
                        vm.selectcompanyFileFlag = '';
                        vm.cfile = '';
                        $("#upload_form")[0].reset();
                    } else {
                        alert(response.data.reason);
                        vm.selectcompanyFileFlag = '';
                        vm.cfile = '';
                        $("#upload_form")[0].reset();
                    }
                })
                var intervelId = setInterval(function () {
                    if (flag) {
                        hh=$(document).height();
                        $("body",parent.document).find("#myiframe").css('height',hh+'px');
                        clearInterval(intervelId);
                        return;
                    }
                },50);

            }
        },
        onChangeCFile: function (event) {

            const str = $("#cfile").val();
            const index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4);
            photoExt = photoExt.toLowerCase();
            if (photoExt != '' && !(photoExt == '.xls' || photoExt == '.xlsx')) {
                alert("请上传excel文件格式!");
                vm.isNeedCFileExtension = false;
                return false;
            } else {
                var meFile = event.target.files[0];
                if (event != undefined && meFile != null && meFile != '') {
                    vm.cfile = event.target.files[0];
                    vm.isNeedCFileExtension = true;
                    //截取名称最后18位
                    vm.selectcompanyFileFlag = event.target.files[0].name;
                }
            }
        },
        showSelectFileCWin() {
            this.selectcompanyFileFlag = '';
            this.cfile = '';
            $("#upload_form")[0].reset();
            $("#cfile").click();
        },
        getDBLinkName: function () {
            //加载组织树
            $.get(baseURL + 'base/dictype/detailListQuery', {dicttype: 1583}, function (r) {
                if (r.code === 0) {
                    vm.itemsDB = r.list;
                }else if (r.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                } else {
                    alert(r.msg);
                }
            })
        },
        /**
         * 选中某个节点
         */
        selectTree: function () {
            var zTree = $.fn.zTree.getZTreeObj("orgTree");
            var node = zTree.getNodeByParam("orgid", vm.orgid);
            //将指定ID的节点选中
            zTree.selectNode(node);
        },
        getOrgTypeName: function (orgType) {
            var parentName;

            if ('0' == orgType) {
                parentName = '总部';
            } else if ('1' == orgType) {
                parentName = '中心企业';
            } else if ('2' == orgType) {
                parentName = '购方机构';
            } else if ('3' == orgType) {
                parentName = '销方机构';
            } else if ('4' == orgType) {
                parentName = '管理机构';
            } else if ('5' == orgType) {
                parentName = '购方企业';
            }
            return parentName;
        },
        /***************打开窗口 新增/修改******************/
        openAddOrUpdataWin: function (data) {
            vm.orgid = data.parentid;

            vm.orgRecord =
            {
                orgid: data.orgid,
                orgname: data.orgname,
                orgcode: data.orgcode,
                storeNumber:data.storeNumber,
                companyCode:data.companyCode,
                taxname: data.taxname,
                taxno: data.taxno,
                parentid: data.parentid,
                orgtype: data.orgtype,
                linkman: data.linkman,
                phone: data.phone,
                address: data.address,
                email: data.email,
                postcode: data.postcode,
                bank: data.bank,
                account: data.account,
                isbottom: data.isbottom,
                orglevel: data.orglevel,
                orglayer: data.orglayer,
                company: data.company,
                remark: data.remark,
                linkName: data.linkName,
                extf1: data.extf1,
                extf2: data.extf2,
                extf3: data.extf3,
                extf4: data.extf4,
                extf5: data.extf5,
                extf6: data.extf6,
                extf7: data.extf7,
                extf8: data.extf8,
                extf9: data.extf9,
                comType: data.comType,
                isUpdate: data.isUpdate,
            };

            vm.addOrUpdata(false);
        },
        saveOrgMenu: function () {

            var treeObj=$.fn.zTree.getZTreeObj("orgMenuTree"),
                nodes=treeObj.getCheckedNodes(true),
                v="";
            for(var i=0;i<nodes.length;i++) {
                v += nodes[i].menulevel+nodes[i].menuname + ",";
            }
            $.ajax({
                url: baseURL + 'base/organization/saveOrgMenu',
                data: {namestr: v, orgid: orgid},
                async: false,
                success: function (r) {
                    alert(r.msg);
                }
            });
            this.addMenuWinClose();
        },
        getOrgMenuTree: function () {
            $.ajax({
                url: baseURL + 'base/menu/orgMenuTree',
                data: {orgid: orgid},
                async: false,
                success: function (r) {
                    $.fn.zTree.init($("#orgMenuTree"), settingMenu, r.menuTree);
                }
            });
        },
        openAddMenuWin: function(data){

            orgid = data.orgid;
            this.addMenuWin = true;
            this.getOrgMenuTree();
        },
        submitForm: function () {
            //表单校验
            this.$refs['addOrUpdateForm'].validate(function (valid) {
                if (valid) {
                    vm.saveOrgData();
                } else {
                    return false
                }
            })
        },
        saveOrgData: function () {
            var data = vm.addOrUpdateForm;
            data.company = data.orglayer;

            //假如新增分库，需要先判断是否已经存在
            /*if (data.isInsert == 1) {
                $.get(baseURL + 'base/dictype/detailListQuery', {
                    dicttype: 1583,
                    dictname: data.linkName
                }, function (r) {
                    if (r.code === 0) {
                        if (r.list.length > 0) {
                            alert("数据库连接名已存在！")
                        } else {
                            vm.saveOrgToDB(data);
                        }
                    }else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                })
            } else {*/
                vm.saveOrgToDB(data);
            // }
        },
        /**
         * 保存机构信息到数据库
         * @param data 组织信息
         */
        saveOrgToDB: function (data) {
            var url = data.orgid == null ? "base/organization/saveOrg" : "base/organization/updateOrg";

            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    if (r.code === 0) {
                        vm.addOrUpdateWin = false;
                        vm.reloadOrg();
                        vm.getOrgTree(vm.expandOrgId.join(','));
                        if (data.orgid == null) {
                            if(data.orgtype==1){
                                vm.openAddMenuWin(r.data);
                            }else {
                                alert('新增成功');
                            }

                        } else {
                            alert('修改成功');
                        }
                    }else if (r.code == 401) {
                        alert("登录超时，请重新登录", function () {
                            parent.location.href = baseURL + 'login.html';
                        });
                    } else {
                        alert(r.msg);
                    }
                }
            });
        },
        deleteOrg: function () {
            var orgIds = getOrgIds();
            if (orgIds == null) {
                return;
            }

            confirm('确定要删除选中的记录？', function () {
                $.ajax({
                    type: "POST",
                    url: baseURL + "base/organization/deleteOrg",
                    contentType: "application/json",
                    data: JSON.stringify({orgIds: orgIds}),
                    success: function (r) {
                        if (r.code == 0) {
                            vm.reloadOrg();
                            vm.getOrgTree(vm.expandOrgId.join(','));
                            vm.multipleSelection = [];
                            alert('删除成功');
                        } else if (r.code == 401) {
                            alert("登录超时，请重新登录", function () {
                                var hostHref = parent.location.href;
                                if(hostHref.indexOf("int")!=-1){
                                    parent.location.href ="http://rl.wal-mart.com";
                                }else if(hostHref.indexOf("ext")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else if(hostHref.indexOf("https://cnwapp.wal-mart.com")!=-1){
                                    parent.location.href ="https://retaillink.wal-mart.com";
                                }else{
                                    parent.location.href = baseURL + 'login.html';
                                }
                            });
                        } else {
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        treeOnClick: function (event, treeId, treeNode) {
            //机构树点击事件

            this.orgModel = true;
            this.orgNode = treeNode;
            this.orgid = treeNode.orgid;
            if(treeNode!=2){
                vm.queryForm.companyCode="";
            }
            //页码初始化
            this.currentPage = 1;

            this.reloadOrg();
        },
        query: function () {

            vm.reloadOrg();
        },
        reloadOrg: function () {
            //刷新列表数据
            vm.orgLoading = true;
            var data = this.queryForm;

            data.parentid = this.orgid;
            data.page = this.currentPage;
            data.limit = this.pageSize;

            this.$http.post(baseURL + 'base/organization/list',
                data, {
                    'headers': {
                        "token": token
                    }
                }).then(function (response) {
                vm.orgLoading = false;
                if (response.data.code == 401) {
                    alert("登录超时，请重新登录", function () {
                        parent.location.href = baseURL + 'login.html';
                    });
                }
                var data = response.data.page;

                vm.currentPage = data.currPage;
                vm.total = data.totalCount;
                vm.totalPage = data.totalPage;
                vm.orgData = data.list;
                vm.orgType=data.list[0].orgtype;
            }).catch(function (response) {
                vm.orgLoading = false;
                alert(response.data.page.msg);
            });
        },
        addOrUpdata: function (isInsert) {
            if (isInsert && null == this.orgid) {
                alert("请选择组织机构!");
                return;
            }
            var orglevel = this.orgNode.orglevel;
            var orgtype = this.orgNode.orgtype;

           /* if (isInsert && ('7' == orgtype || '8' == orgtype)) {
                alert("门店/销方企业下不能新增组织!");
                return;
            }*/

            this.getDBLinkName();

            this.isInsert = isInsert;

            this.addOrUpdateForm.parentid = this.orgid;
            //纳税人识别号、纳税人名称隐藏
            vm.taxShow = false;

            if (isInsert) {
                this.addOrUpdateForm.orgid = null;
                this.addOrUpdateForm.title = '新增机构';
                //机构类型初始化
                this.addOrUpdateForm.orgtype = null;
                //机构类型只有一个选项时，设置默认选项
                if ('0' == orgtype) {
                    vm.addOrUpdateForm.orgtype = '1';
                }
                if ('3' == orgtype) {
                    vm.addOrUpdateForm.orgtype = '8';
                    vm.taxShow = true;
                }
                if ('2' == orgtype) {
                    vm.addOrUpdateForm.orgtype = '5';
                    vm.taxShow = true;
                }
                if ('5' == orgtype) {
                    vm.addOrUpdateForm.orgtype = '7';
                }

                this.addOrUpdateForm.orglayer = null;
                this.addOrUpdateForm.orglevel = orglevel + 1;
                this.addOrUpdateForm.linkName = this.orgNode.linkName;
                //只有根节点可以新增，其他
                if ('0' != orgtype) {
                    vm.addOrUpdateForm.orglayer = vm.orgNode.orglayer;
                }

                this.readonly = false;
                //如果是中心企业，显示数据库字段
                vm.disabled = '0' != this.orgNode.orgtype;
                //启用机构类型
                vm.disabledOrgType = false;
            } else {
                this.addOrUpdateForm = this.orgRecord;
                this.addOrUpdateForm.title = '修改机构';
                this.readonly = true;
                vm.disabled = true;
                //禁止修改机构类型
                vm.disabledOrgType = true;

                orgtype = this.addOrUpdateForm.orgtype;

                //销方企业和购方企业可以修改税号及纳税人名称
                if ('5' == orgtype) {
                    vm.taxShow = true;
                }
                if ('8' == orgtype) {
                    vm.taxShow = true;
                }
            }

            vm.inputShow = false;
            //如果上级机构是总部，总部代码显示，且必填
            vm.fieldOrglayerShow = '0' == this.orgNode.orgtype;

            this.addOrUpdateForm.parentName = this.getOrgTypeName(this.orgNode.orgtype);

            this.addOrUpdateForm.isInsert = '0';

            this.addOrUpdateWin = true;

        },
        templateDownload:function(){
            document.getElementById("ifile").src = baseURL + sysUrl.companyCodeImportExport;
        },
        addOrUpdataWinClose: function (val) {
            this.addOrUpdateWin = false;
        },
        addMenuWinClose: function (val) {
            this.addMenuWin = false;
        },
        resetForm: function (formName) {
            //对整个表单进行重置，将所有字段值重置为初始值并移除校验结果
            this.$refs[formName].resetFields();
        },
        clearValidate: function (formName) {
            //移除整个表单的校验结果
            this.$refs[formName].clearValidate();
            this.addOrUpdateReset();
        },
        /**
         * 选择框改变事件
         */
        selectChange: function (val) {
            vm.inputShow = '0' != val;
            vm.addOrUpdateForm.linkName = null;
        },
        /**
         * 选择框改变事件 - 机构类型
         */
        selectChangeOrgType: function (val) {
            vm.taxShow = !!(val == '5' || val == '8');
        },
        /**
         * 事件 - 表格选中改变事件
         */
        changeFun: function (selection) {
            this.multipleSelection = selection;
        },
        /**
         * 事件 - currentPage 改变时会触发
         */
        currentChange: function (currentPage) {
            this.currentPage = currentPage;
            this.reloadOrg();
        },
        /**
         * 事件 - pageSize 改变时会触发
         */
        sizeChange: function (val) {
            this.pageSize = val;
            this.reloadOrg();
        },
        addOrUpdateReset: function () {
            //重置机构信息

            this.addOrUpdateForm.orgname = null;
            this.addOrUpdateForm.orgcode = null;
            this.addOrUpdateForm.storeNumber=null;
            this.addOrUpdateForm.companyCode=null;
            this.addOrUpdateForm.taxname = null;
            this.addOrUpdateForm.taxno = null;
            this.addOrUpdateForm.linkman = null;
            this.addOrUpdateForm.phone = null;
            this.addOrUpdateForm.address = null;
            this.addOrUpdateForm.email = null;
            this.addOrUpdateForm.postcode = null;
            this.addOrUpdateForm.bank = null;
            this.addOrUpdateForm.account = null;
            this.addOrUpdateForm.remark = null;
            this.addOrUpdateForm.extf1 = null;
            this.addOrUpdateForm.extf2 = null;
            this.addOrUpdateForm.extf3 = null;
            this.addOrUpdateForm.extf4 = null;
            this.addOrUpdateForm.extf5 = null;
            this.addOrUpdateForm.extf6 = null;
            this.addOrUpdateForm.extf7 = null;
            this.addOrUpdateForm.extf8 = null;
            this.addOrUpdateForm.extf9 = null;
            this.addOrUpdateForm.comType = null;
        },
        /**
         * 行号 - 菜单
         */
        orgIndex: function (index) {
            return index + (this.currentPage - 1) * this.pageSize + 1;
        },
        /**
         * 格式化 - 数据为空时显示 --
         */
        formatterField: function (row, column, cellValue, index) {
            if (null == cellValue || '' == cellValue) {
                return '—— ——';
            }
            return cellValue;
        }
    }
});

/*******************组织树展开事件**********************/
function treeOnExpand(event, treeId, treeNode) {
    vm.expandOrgId.push(treeNode.orgid);
}

/*******************组织树收起事件**********************/
function treeOnCollapse(event, treeId, treeNode) {
    var result = [];
    var expandOrgId = vm.expandOrgId;
    for (var i = 0; i < expandOrgId.length; i++) {
        if (expandOrgId[i] != treeNode.orgid) {
            result.push(expandOrgId[i])
        }
    }
    vm.expandOrgId = result;
}

/***************获取选中的组织的id******************/
function getOrgIds() {
    var selection = vm.multipleSelection;

    if (selection.length == 0) {
        alert("请选择要操作的记录");
        return;
    }

    var userIds = [];
    for (var i = 0; i < selection.length; i++) {
        userIds.push(selection[i].orgid);
    }

    return userIds;
}

function checkEvent(event, treeId, treeNode) {
    vm.checkEvent(event, treeId, treeNode);
}