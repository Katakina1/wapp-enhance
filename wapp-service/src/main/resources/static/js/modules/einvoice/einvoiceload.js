
/**
 * 电票上传
 * @author marvin
 */

var electron = new Vue({
    el: '#rrapp',
    data: {
        listData: [],
        modifyRow: "",
        isNeedFileExtension: false,
        fileSizeIsFit: false,
        file: "",
        token: token,
        invoice: {
            invoiceDate: new Date(),
            invoiceCode: "",
            invoiceNo: "",
            checkCode: ""
        },
        updateWinShow: false,
        updateInvoice: {
            id: "",
            invoiceDate: "",
            invoiceCode: "",
            invoiceNo: "",
            invoiceAmount: "",
            checkCode: ""
        },
        imageShow: false,
        invoiceId: "",
        invoiceEditId: "",
        haveImageShow: false,
        noImageShow: false,
        loadOption: {
            lock: true,
            fullscreen: true,
            spinner: 'el-icon-loading'
        },
        saveInvoiceDate: {}
    },
    mounted: function () {
        this.saveInvoiceDate = {
            disabledDate: function (time) {
                var currentTime = new Date();
                var beforeAyear = new Date().setYear(currentTime.getFullYear() - 1);
                return time.getTime() >= currentTime;
            }
        };
    },
    watch: {
        'invoice.invoiceCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoice.invoiceCode = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoice.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoice.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'invoice.checkCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.invoice.checkCode = oldValue;
                    })
                }
            },
            deep: true
        },
        'updateInvoice.invoiceCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.updateInvoice.invoiceCode = oldValue;
                    })
                }
            },
            deep: true
        },
        'updateInvoice.invoiceNo': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,8}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.updateInvoice.invoiceNo = oldValue;
                    })
                }
            },
            deep: true
        },
        'updateInvoice.checkCode': {
            handler: function (val, oldValue) {
                var _this = this;
                var reg = /^[0-9]{0,}$/;
                if (!(reg.test(val) || val == null)) {
                    Vue.nextTick(function () {
                        _this.updateInvoice.checkCode = oldValue;
                    })
                }
            },
            deep: true
        }
    },
    methods: {
        /**
         * 手动录入保存
         */
        saveInvoice: function () {
            $(".checkMsg").remove();
            var flag = true;
            if (this.invoice.invoiceCode == null || this.invoice.invoiceCode == '') {
                $("#requireMsg1 .el-form-item__content").append('<div class="checkMsg">请输入发票代码</div>');
                flag = false;
            }else if (this.invoice.invoiceCode != '144031539110' && this.invoice.invoiceCode != '131001570151' &&
                this.invoice.invoiceCode != '133011501118' && this.invoice.invoiceCode != '111001571071') {
                if (this.invoice.invoiceCode.length != 12 || !(this.invoice.invoiceCode.substring(0, 1) == "0") ||
                    (!(this.invoice.invoiceCode.substring(10, 12) == "11") && !(this.invoice.invoiceCode.substring(10, 12) == "12"))) {
                    $("#requireMsg1 .el-form-item__content").append('<div class="checkMsg">您输入的发票代码不是电票代码，请核实！</div>');
                    flag = false;
                }
            }
            if (this.invoice.invoiceNo == null || this.invoice.invoiceNo == '') {
                $("#requireMsg2 .el-form-item__content").append('<div class="checkMsg">请输入发票号码</div>');
                flag = false;
            }
            if (this.invoice.invoiceDate == null || this.invoice.invoiceDate == '') {
                $("#requireMsg3 .el-form-item__content").append('<div class="checkMsg">请输入开票日期</div>');
                flag = false;
            }
            if (this.invoice.checkCode == null || this.invoice.checkCode == '') {
                $("#requireMsg4 .el-form-item__content").append('<div class="checkMsg">请输入校验码</div>');
                flag = false;
            }
            if (flag) {
                var loading = electron.getLoading("正在保存，请稍后...");
                var data = {
                    invoiceCode: this.invoice.invoiceCode,
                    invoiceNo: this.invoice.invoiceNo,
                    invoiceDate: new Date(this.invoice.invoiceDate).getTime(),
                    checkCode: this.invoice.checkCode
                };
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/save',
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (r) {
                        loading.close();
                        if (r != null && r.code == 0) {
                            electron.listData.push(r.invoice);
                            //alert(res.resultTip)
                        } else {
                            alert(r.msg);
                        }
                    },
                    error: function (res) {
                        loading.close();
                        alert(res.msg);
                    }
                });
            }
        },
        /**
         * 选择文件事件
         * @param event 事件触发点
         * @return {boolean}
         */
        onChangeFile: function (event) {
            electron.file = "";
            $("#showFileName").html("未选择文件");
            $("#showFileName").removeAttr("title");
            var str = $("#file").val();
            var index = str.lastIndexOf('.');
            var photoExt = str.substr(index, 4).toLowerCase();
            if (photoExt != '' && !(photoExt == '.zip' || photoExt == '.rar' || photoExt == '.pdf')) {
                alert("请上传zip/rar电票压缩包或者电票pdf文件!");
                $("#file").val("");
                electron.isNeedFileExtension = false;
                return false;
            } else {
                var maxsize = 2 * 1024 * 1024;//2M
                var file = event.target.files[0];
                var fileSize = file.size;

                if (fileSize > maxsize) {
                    alert("上传的文件不能大于2M");
                    $("#file").val("");
                    electron.fileSizeIsFit = false;
                    return false;
                } else {
                    electron.file = file;
                    $("#showFileName").attr("title", electron.file.name);
                    $("#showFileName").html(electron.file.name);
                    electron.isNeedFileExtension = true;
                    electron.fileSizeIsFit = true;
                }
            }
        },
        /**
         * 上传选择的文件
         * @param event
         */
        uploadFile: function (event) {
            if ($("#showFileName").html() == "未选择文件") {
                alert("请选择文件");
            } else if (!electron.isNeedFileExtension) {
                alert("请上传zip/rar电票压缩包或者电票pdf文件!");
            } else if (!electron.fileSizeIsFit) {
                alert("上传的文件不能大于2M");
            } else {
                event.preventDefault();
                var formData = new FormData();
                formData.append('file', this.file);
                formData.append('token', this.token);

                var url = baseURL + 'electron/upload';
                var loading = electron.getLoading("上传中...");
                $.ajax({
                    type: "POST",
                    url: url,
                    data: formData,
                    dataType: "json",
                    cache: false,//上传文件无需缓存
                    processData: false,//用于对data参数进行序列化处理 这里必须false
                    contentType: false, //必须
                    success: function (response) {
                        loading.close();
                        $("#showFileName").html('未选择文件');
                        if (response.code == 0) {
                            var pdfName = "";
                            for (var i = 0; i < response.list.length; i++) {
                                var data = response.list[i];
                                if (data.readPdfSuccess) {
                                    pdfName += '<p>文件：' + data.pdfName + '&nbsp;成功</p>';
                                    electron.listData.push(data);
                                } else {
                                    pdfName += '<p style="color: red">文件：' + data.pdfName + '&nbsp;失败</p>';
                                }
                            }
                            $("#pdfName").append(pdfName);
                        } else {
                            alert("系统错误！请稍后再试！");
                        }
                    },
                    error: function (response) {
                        loading.close();
                        alert("系统错误！请稍后再试！");
                    }

                });
            }
        },
        /**
         * 删除电子发票
         * @param row
         * @param id 删除的发票的id
         */
        deleteSelectInvoice: function (row, id) {
            if (id != null && id != "") {
                layer.confirm("确定删除选中的数据？", function (index) {
                    layer.close(index);
                    var loading = electron.getLoading("正在删除，请稍后...");
                    $.ajax({
                        type: "POST",
                        url: baseURL + 'electron/delete?id=' + id + "&token=" + token,
                        contentType: "application/json",
                        success: function (r) {
                            loading.close();
                            if (r.code == 1) {
                                electron.deleteRowData(row);
                                alert(r.msg);
                            } else {
                                alert(r.msg);
                            }
                        },
                        error: function () {
                            loading.close();
                            alert("系统繁忙！请稍后重试");
                        }
                    });
                });
            } else {
                electron.deleteRowData(row);
                alert("删除成功！");
            }
        },
        /**
         * 删除数据
         * @param row
         */
        deleteRowData: function (row) {
            for (var i = 0; i < electron.listData.length; i++) {
                if (electron.listData[i] == row) {
                    electron.listData.splice(i, 1);
                    break;
                }
            }
        },
        /**
         * 修改发票信息窗口显示
         * @param id 发票的id
         * @param scanId 发票图片的唯一标识，若为空，则表示没有图片属于手工录入
         */
        updateWindowShow: function (id, scanId) {
            if (id != null && id != "") {
                //填充图片
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/selectToUpdate?id=' + id + "&token=" + token,
                    contentType: "application/json",
                    success: function (r) {
                        if (r != null && r.code == 0 && r.invoice != null) {
                            electron.updateWinShow = true;
                            if (scanId != null) {
                                if (electron.invoiceEditId != id) {
                                    electron.invoiceEditId = id;
                                    electron.haveImageShow = true;
                                    electron.noImageShow = false;
                                    $('#edit_image_area').attr('src', "");
                                    $.ajax({
                                        type: "POST",
                                        url: baseURL + 'electron/checkImageToken',
                                        contentType: "application/json",
                                        success: function (r) {
                                            document.getElementById("edit_image_area").src = '/dxhy-gylpt/electron/getImageForAll?id=' + id + "&token=" + token;
                                        },
                                        error: function () {

                                        }
                                    });
                                }
                            } else {
                                electron.haveImageShow = false;
                                electron.noImageShow = true;
                            }

                            electron.updateInvoice.id = r.invoice.id;
                            electron.updateInvoice.invoiceCode = r.invoice.invoiceCode;
                            electron.updateInvoice.invoiceNo = r.invoice.invoiceNo;
                            electron.updateInvoice.invoiceDate = r.invoice.invoiceDate;
                            electron.updateInvoice.invoiceAmount = r.invoice.invoiceAmount;
                            electron.updateInvoice.checkCode = r.invoice.checkCode;
                        } else {
                            alert("系统错误！")
                        }
                    },
                    error: function () {
                        alert("系统错误！")
                    }
                });
            } else {
                alert("修改数据有误！请刷新页面");
            }
        },
        /**
         * 显示图片窗口
         * @param id 发票的id
         */
        imageWindowShow: function (id) {
            electron.imageShow = true;
            if (electron.invoiceId != id) {
                electron.invoiceId = id;
                $('#get_image_area').attr('src', "");
                $.ajax({
                    type: "POST",
                    url: baseURL + 'electron/checkImageToken',
                    contentType: "application/json",
                    success: function (r) {
                        document.getElementById("get_image_area").src = '/dxhy-gylpt/electron/getImageForAll?id=' + id + "&token=" + token;
                    },
                    error: function () {

                    }
                });
            }
        },
        /**
         * 校验表单信息，并保存
         */
        checkUpdateMsg: function () {
            this.$refs['updateInvoice'].validate(function (valid) {
                if (valid) {
                    electron.updateInvoiceToSave();
                } else {
                    return false
                }
            });
        },
        /**
         * 保存修改的电票信息
         */
        updateInvoiceToSave: function () {

            var data = {
                id: electron.updateInvoice.id,
                invoiceDate: new Date(electron.updateInvoice.invoiceDate).getTime(),
                invoiceCode: electron.updateInvoice.invoiceCode,
                invoiceNo: electron.updateInvoice.invoiceNo,
                checkCode: electron.updateInvoice.checkCode
            };
            var loading = electron.getLoading("正在保存，请稍后...");
            $.ajax({
                type: "POST",
                url: baseURL + 'electron/update/save',
                contentType: "application/json",
                data: JSON.stringify(data),
                success: function (r) {
                    loading.close();
                    if (r != null && r.code == '0') {
                        if (r.invoice.checkSuccess) {
                            electron.replaceItem(electron.modifyRow, r.invoice);
                            alert("保存成功！");
                            electron.updateWinShow = false;
                        } else {
                            alert(r.invoice.resultTip);
                        }
                    } else {
                        alert(r.msg);
                    }
                },
                error: function () {
                    loading.close();
                    alert("系统错误！请稍后再试");
                }
            });
        },
        /**
         * 修改行信息
         * @param row
         * @param targetData
         */
        replaceItem: function (row, targetData) {
            for (var i = 0; i < electron.listData.length; i++) {
                if (electron.listData[i] == row) {
                    electron.listData.splice(i, 1, targetData);
                    break;
                }
            }
        },
        /**
         * 关闭弹执行的放发
         */
        closeUpdateWin: function () {
            electron.clearUpdateParam();
        },
        /**
         * 点击弹出窗右上角的关闭时
         */
        beforeCloseUpdateWin: function () {
            electron.updateWinShow = false;
        },
        /**
         * 关闭图片窗口
         */
        beforeCloseImgWin: function () {
            electron.imageShow = false;
        },
        /**
         * 改变发票税额时改变价税合计金额
         */
        checkTaxAmount: function () {
            electron.updateInvoice.taxAmount = this.formatAmount(electron.updateInvoice.taxAmount);
            electron.updateInvoice.totalAmount = Number(
                (parseFloat(electron.updateInvoice.invoiceAmount) * 100 + parseFloat(electron.updateInvoice.taxAmount) * 100) / 100)
                .toFixed(2);
        },
        /**
         * 改变金额时改变价税合计金额
         */
        checkInvoiceAmount: function () {
            electron.updateInvoice.invoiceAmount = this.formatAmount(electron.updateInvoice.invoiceAmount);
        },
        /**
         * 格式化用户输入的金额
         * @param amount 金额
         */
        formatAmount: function (amount) {
            amount = amount.replace(/[^\d.-]/g, ""); //清除“数字”和“.”以外的字符
            amount = amount.replace(/^\./g, ""); //验证第一个字符是数字而不是.
            amount = amount.replace(/\.{2,}/g, "."); //只保留第一个. 清除多余的
            amount = amount.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
            amount = amount.replace(/^(\-)*(\d+)\.(\d\d).*$/, '$1$2.$3');//只能输入两个小数

            return amount == '' ? 0 : amount;
        },
        /**
         * 关闭弹出窗之后，清空字段
         */
        clearUpdateParam: function () {
            electron.updateInvoice.invoiceCode = null;
            electron.updateInvoice.invoiceNo = null;
            electron.updateInvoice.invoiceDate = null;
            electron.updateInvoice.invoiceAmount = null;
            electron.updateInvoice.checkCode = null;
        },
        /**
         * 显示图片窗口
         * @param index
         * @param row
         */
        showImageWin: function (index, row) {
            electron.imageWindowShow(row.id);
        },
        /**
         * 删除发票
         * @param index
         * @param row
         */
        deleteInvoice: function (index, row) {
            electron.deleteSelectInvoice(row, row.id);
        },
        /**
         * 显示修改页面
         * @param index
         * @param row
         */
        showUpdateWin: function (index, row) {
            electron.modifyRow = row;
            electron.updateWindowShow(row.id, row.scanId)
        },
        /**
         * 格式化签收状态
         * @param row
         * @param column
         * @param cellValue
         * @return {*}
         */
        formatterQsStatus: function (row, column, cellValue) {
            if (cellValue == '0') {
                return '签收失败'
            }
            if (cellValue == '1') {
                return '签收成功'
            }
        },
        /**
         * 格式化签收结果
         * @param row
         * @param column
         * @param cellValue
         * @return {*}
         */
        formatterQsResult: function (row, column, cellValue) {
            if (row.checkSuccess) {
                return cellValue;
            } else {
                return row.resultTip;
            }

        },
        /**
         * 格式化日期
         * @param row
         * @param column
         * @param cellValue
         */
        formatterKprq: function (row, column, cellValue) {
            return cellValue.substring(0, 11);

        },
        /**
         * 获取需要展示的loading
         * @param text loading时页面展示的提示文字
         * @return {*} 返回一个loading实例
         */
        getLoading: function (text) {
            electron.loadOption.text = text;
            return electron.$loading(electron.loadOption);
        },
        /**
         * 格式化日期
         * @param time
         * @return {string}
         */
        formatDate: function (time) {
            var date = new Date(time);
            var seperator1 = "-";
            var month = date.getMonth() + 1;
            var strDate = date.getDate();
            if (month >= 1 && month <= 9) {
                month = "0" + month;
            }
            if (strDate >= 0 && strDate <= 9) {
                strDate = "0" + strDate;
            }
            return date.getFullYear() + seperator1 + month + seperator1 + strDate;
        },
        /**
         * 选择日期时添加样式
         * @param val
         */
        addDateStyle: function (val) {
            if (val == 1) {
                $("#saveKPDate").parent().find(".el-icon-date").addClass("click_date");
                $("#saveKPDate").parent().find(".el-input__suffix").addClass("focus_date_bg");
            } else if (val == 2) {
                $("#editKPDate").parent().find(".el-icon-date").addClass("click_date");
                $("#editKPDate").parent().find(".el-input__suffix").addClass("focus_date_bg");
            }
        },
        /**
         * 移除选择日期时的样式
         * @param val
         */
        removeDateStyle: function (val) {
            if (val == 1) {
                $("#saveKPDate").parent().find(".el-icon-date").removeClass("click_date");
                $("#saveKPDate").parent().find(".el-input__suffix").removeClass("focus_date_bg");
            } else if (val == 2) {
                $("#editKPDate").parent().find(".el-icon-date").removeClass("click_date");
                $("#editKPDate").parent().find(".el-input__suffix").removeClass("focus_date_bg");
            }
        }
    }
});

/**
 * 验证数据输入格式
 * @param t 当前的input
 * @param mode save  or   update
 * @param type 1:invoiceCode 2:invoiceNo 3:checkCode
 */
function verificationInputValue(t, mode, type) {
    var reg = /[^\d]/g;
    if (mode == 'save') {
        if (type == 1) {
            electron.invoice.invoiceCode = t.value.replace(reg, '');
        } else if (type == 2) {
            electron.invoice.invoiceNo = t.value.replace(reg, '');
        } else {
            electron.invoice.checkCode = t.value.replace(reg, '');
        }
    } else {
        if (type == 1) {
            electron.updateInvoice.invoiceCode = t.value.replace(reg, '');
        } else if (type == 2) {
            electron.updateInvoice.invoiceNo = t.value.replace(reg, '');
        } else {
            electron.updateInvoice.checkCode = t.value.replace(reg, '');
        }
    }

}

/**
 * 显示选择文件的窗口
 */
function showSelectFileWin() {
    $("#file").click();
}


/****************************文件上传form表单提交方式*****************************************/

//上传时的loading
var fileUploadIng = "";

/**
 * 文件表单提交
 */
function submitFile() {
    if ($("#showFileName").html() == "未选择文件") {
        alert("请选择文件");
    } else if (!electron.isNeedFileExtension) {
        alert("请上传zip/rar电票压缩包或者电票pdf文件!");
    } else if (!electron.fileSizeIsFit) {
        alert("上传的文件不能大于2M");
    } else {
        fileUploadIng = electron.getLoading("上传中...");
        var url = baseURL + 'electron/upload';
        $('#submitFileForm').attr('action', url);
        $('#submitFileForm').submit();
    }
}

/**
 * 处理文件上传的submit，及其返回的结果
 * @type {*|jQuery|HTMLElement}
 */
var frm = $("#upLoadFileCallBack");
frm.load(function () {
    var wnd = this.contentWindow;
    var jsonDataStr = $(wnd.document.body).find("pre").html();
    uploadFileCallBack(jsonDataStr);
});

/**
 * 处理返回数据
 * @param data
 */
function uploadFileCallBack(data) {
    fileUploadIng.close();
    if (data == undefined) {
        return;
    }
    var response = $.parseJSON(data);
    if (response == undefined) {
        return;
    }
    if (response.code == 0) {
        var pdfName = "";
        for (var i = 0; i < response.list.length; i++) {
            var data = response.list[i];
            if (data.readPdfSuccess) {
                pdfName += '<p>文件：' + data.pdfName + '&nbsp;成功</p>';
                electron.listData.push(data);
            } else {
                pdfName += '<p style="color: red">文件：' + data.pdfName + '&nbsp;失败</p>';
            }
        }
        $("#pdfName").append(pdfName);
    } else if (response.code == 401) {
        parent.location.href = baseURL + 'login.html';
    } else {
        alert("系统错误！请稍后再试！");
    }
}