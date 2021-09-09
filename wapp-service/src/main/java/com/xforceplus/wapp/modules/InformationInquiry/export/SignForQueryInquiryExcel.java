package com.xforceplus.wapp.modules.InformationInquiry.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryEntity;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 订单信息导出
 */
public final class SignForQueryInquiryExcel extends AbstractExportExcel {

    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public SignForQueryInquiryExcel(Map<String,Object> map, String excelTempPath, String excelName) {
        this.map = map;
        this.excelTempPath = excelTempPath;
        this.excelName = excelName;
    }

    @Override
    protected String getExcelUri() {
        return excelTempPath;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表
        final XSSFSheet sheet = workBook.getSheetAt(0);
        //获取要导出的数据
        final List<SignForQueryEntity> list = (List<SignForQueryEntity>)this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 1);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel

        for (SignForQueryEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //签收状态
            setSheetValue(sheet, beginLine, 1, formateVenderType(entity.getQsStatus()), style);
            //签收描述
            setSheetValue(sheet, beginLine, 2, entity.getNotes(), style);
            //签收日期
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getQsDate()), style);
            //扫描流水号
            setSheetValue(sheet, beginLine, 4, entity.getScanId(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 5, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 6, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 7, formatDate(entity.getInvoiceDate()), style);
            //业务类型
            setSheetValue(sheet, beginLine, 8, formatFlowType(entity.getFlowType()), style);
            //供应商号
            setSheetValue(sheet, beginLine, 9, entity.getVenderid(), style);

            //购方名
            setSheetValue(sheet, beginLine, 10, entity.getGfName(), style);
            //销方名
            setSheetValue(sheet, beginLine, 11, entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 12, entity.getInvoiceAmount().toString(), style);
            //税额
            setSheetValue(sheet, beginLine, 13, entity.getTaxAmount().toString(), style);
            //JvCode
            setSheetValue(sheet, beginLine, 14, entity.getJvCode(), style);

            setSheetValue(sheet, beginLine, 15, entity.getCompanyCode(), style);

            //扫描匹配状态
            setSheetValue(sheet, beginLine, 16, scanMatchStatus(entity.getScanMatchStatus()), style);
            //扫描匹配原因
            setSheetValue(sheet, beginLine, 17, entity.getScanFailReason(), style);

            setSheetValue(sheet, beginLine, 18, entity.getEpsNo(), style);

            beginLine++;
        }
    }


    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }

    private String formateVenderType(String qsStatus){
        String value="";
        if("0".equals(qsStatus)){
            value="签收失败";
        }else if("1".equals(qsStatus)){
            value="签收成功";
        }
        return value;
    }

    private String scanMatchStatus(String qsStatus) {
        String value = "";
        if ("0".equals(qsStatus)) {
            value = "未匹配";
        } else if ("1".equals(qsStatus)) {
            value = "匹配成功";
        } else if ("2".equals(qsStatus)) {
            value = "匹配失败";

        }else{
			 value = "未匹配";
		}
        return value;
    }

    private String formatFlowType(String type){
        return null==type ? "" :
                "1".equals(type) ? "商品" :
                        "2".equals(type) ? "费用" :
                                "3".equals(type) ? "外部红票" :
                                        "4".equals(type) ? "内部红票" :
                                                "5".equals(type) ? " 供应商红票" :
                                                        "6".equals(type) ? " 租赁" :
                                                                "7".equals(type) ? "直接认证":
                                                                        "8".equals(type) ? "Ariba":"";
    }
}
