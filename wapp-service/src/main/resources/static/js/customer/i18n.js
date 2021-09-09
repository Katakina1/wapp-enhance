
var messages = {
    cn: {
        message: {
            queryBtn: '查询',
            exportBtn: '导出',
            label_select_placeholder: '请选择',
            index_no: '序号',
            page_toolbar_text: '',
            invoice_collect: {
                gfName: '购方名称',
                gfTaxNo: '购方税号',
                createDate: '采集日期',
                collectCount: '采集数量合计',
                sumTotalAmount: '金额合计',
                sumTaxAmount: '税额合计',
                invoiceCode: '发票代码',
                invoiceNo: '发票号码',
                openInvoiceDate: '开票日期',
                xfName: '销方名称',
                invoiceAmount: '金额',
                taxAmount: '税额',
                invoiceStatusName: '异常状态',
                statusUpdateDate: '异常日期'
            },
            invoice_type_name: {
                specialInvoice:'增值税专用发票',
                ordinaryInvoice:'增值税普通发票',
                electricInvoice:'增值税电子普通发票',
                volumeInvoice:'增值税普通发票（卷票）',
                tollsInvoice:'增值税电子普通发票（通行费）',
                motorInvoice:'机动车销售统一发票'
            }
        }
    }
};
var i18n = new VueI18n({
    locale: 'cn', // set locale
    messages: messages
});