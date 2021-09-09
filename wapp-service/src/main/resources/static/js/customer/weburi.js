
//请求前缀
var pre = 'modules/';
//导出前缀
var exportPre = 'export/';
var sysUrl = {
    //发票采集列表-页面数据
    collectListQueryPaged: pre + 'collect/list/queryPaged',
    //根据税号获取发票信息
    collectListByTaxNo: pre + 'collect/getInvoiceInfo',
    //获取购方税号和购方名称
    collectionTaxName:pre + 'collect/getGfName',
    //发票采集列表数据导出
    collectListExport: exportPre + 'invoiceCollectionList',
    //异常发票采集-页面数据
    abnormalList: pre + 'collect/abnormal/queryPaged',
    //异常发票采集-导出
    abnormalExport: exportPre + 'abnormalInvoiceCollectionList',
    //未补明细发票-页面数据
    detailedInvoiceList: pre + 'collect/detailed/queryPaged',
    //未补明细发票-导出
    detailedInvoiceExport: exportPre + 'detailedInvoiceCollectionList',
    //未补明细发票-手动查验
    detailedInvoiceHandle: pre + 'collect/detailed/manualInspection',
    //查询认证-页面数据
    invoiceCertificationList: pre + 'certification/query/queryPaged',
    //查询认证-导出
    invoiceCertificationExport: exportPre + 'certification',
    //根据类型获取类型明细
    getParamMap: pre + 'param/getParamMap',
    //导入认证-模板导出
    exportTemp: exportPre + 'certificationTemplate',
    //导入认证-导入
    importCertification: pre + 'certification/import',
    //导入认证-提交认证
    authSumit: pre + "certification/authSumit",
    //导入勾选-模板导出
    exportCheckTemp: exportPre + 'certificationCheckTemplate',
    //导入勾选-导入
    importCheckCertification: pre + 'certification/checkImport',
    //导入认证-提交勾选
    submit: pre + "certification/submit",
    //发票签收-导入签收-模板下载
    exportSignTemp: exportPre + 'exportSignTemp',
    //发票匹配-模板下载
    invoiceImportExport:exportPre + 'invoiceImportExport',
    //导入匹配关系-模板下载
    matchImportExport:exportPre + 'matchImportExport',
    //导入供应商-模板下载
    userImportExport:exportPre+'userImportExport',
    //导入公司代码-模板下载
    companyCodeImportExport:exportPre+'companyCodeImportExport',
    //导入匹配关系(沃)-模板下载
    matchImportExportWo:exportPre + 'matchImportExportWo',
    //发票匹配-excel导入
    invoiceImport:pre + 'invoiceImport',
    //供应商-excel导入
    userImport:pre+'userImport',

    //匹配关系-excel导入
    matchImport:pre + 'matchImport',
    //发票认证-导入认证-excel导入
    excelSign: pre + 'signIn/excelSign',
    //发票认证-导入认证-图片导入
    imgSign: pre + 'signIn/imgSign',
    getSignInImg: pre + 'signIn/getSignInImg',
    saveModifyInvoice: pre + 'signIn/modify',
    deleteInvoice: 'SignatureProcessing/deleteInvoice',
    questionPaperQuery:pre+'posuopei/questionPaper/query',
    questionPaperDetailQuery:pre+'posuopei/questionPaper/detailQuery',

    //税收分类编码-表格数据
    taxCodeList: pre + 'taxcode/list/queryPaged',
    //企业信息-页面数据
    enterpriseInfoQueryPaged: pre + 'enterpriseinfo/list/queryPaged',
    //企业黑名单-页面数据
    enterpriseBlackQueryPaged: pre + 'enterpriseblack/list/queryPaged',
    //商品黑名单-页面数据
    goodsBlackQueryPaged: pre + 'goods/list/queryPaged',
    //税收分类编码管理-页面数据
    taxCodeQueryPaged: pre + 'goods/list/queryPaged',
    //商品黑名单-模板导出
    exportGoodsBlackTemp: exportPre + 'goodsblack/downloadTemplate',
    //税收分类编码管理-模板导出
    exportTaxCodeTemp: exportPre + 'goodstaxcode/downloadTemplate',
    //企业黑名单-模板导出
    exportEnterpriseBlack: exportPre + 'enterpriseblack/downloadTemplate',
    //明细-模板导出
    addImportExport:exportPre + 'exportTemplateAdd',
    //发票匹配-excel导入
    addClaimImport:pre + 'importAddClaim',
    //发票匹配-excel导入
    addOtherImport:pre + 'importAddOther',
    //发票匹配-excel导入
    addPoImport:pre + 'importAddPo',
    //发票匹配-excel导入
    addPoDiscountImport:pre + 'importAddPoDiscount',
    //发票匹配-excel导入
    addCountImport:pre + 'importAddCount',

};