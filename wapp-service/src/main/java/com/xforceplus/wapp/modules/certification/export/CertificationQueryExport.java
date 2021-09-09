package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.Constant.DEFAULT_SHORT_DATE_FORMAT;
import static com.xforceplus.wapp.modules.Constant.FONT;

/**
 * 认证查询导出
 * @author Colin.hu
 * @date 4/14/2018
 */
public class CertificationQueryExport extends AbstractExportExcel {

    private final Map<String, List<InvoiceCollectionInfo>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public CertificationQueryExport(Map<String, List<InvoiceCollectionInfo>> map, String excelTempPath, String excelName) {
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
        final List<InvoiceCollectionInfo> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 0, 16);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index = 1;
        //数据填入excel
        for (InvoiceCollectionInfo entity : list) {
            //认证结果
            final String authStatus = formatAuthStatus(entity.getAuthStatus());
            //认证方式
            final String rzhType = formatRzhType(entity.getRzhType());
           /* setSheetValue(sheet, beginLine, 0, authMap().get(entity.getAuthStatus()), style);*/
            setSheetValue(sheet, beginLine, 0, index++, style);
            setSheetValue(sheet, beginLine, 1, authStatus, style);
            //认证状态（是否认证）
            setSheetValue(sheet, beginLine, 2, formatRzhYesOrNo(entity.getRzhYesorno()), style);
            //认证时间
            setSheetValue(sheet, beginLine, 3, formatDate(entity.getRzhDate()), style);
            //认证人
            setSheetValue(sheet, beginLine, 4,entity.getConfirmUser(), style);
            //认证归属期
            setSheetValue(sheet, beginLine, 5,entity.getRzhBelongDate(), style);
            //发票代码
            setSheetValue(sheet, beginLine, 6,entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 7,entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 8, formatDate(entity.getInvoiceDate()), style);
            //购方税号
            setSheetValue(sheet, beginLine, 9,entity.getGfTaxNo(), style);
            //购方名称
            setSheetValue(sheet, beginLine, 10,entity.getGfName(), style);
            //销方税号
            setSheetValue(sheet, beginLine, 11,entity.getXfTaxNo(), style);
            //供应商号
            setSheetValue(sheet, beginLine, 12,entity.getVenderid(), style);
            //销方名称
            setSheetValue(sheet, beginLine, 13,entity.getXfName(), style);
            //金额
            setSheetValue(sheet, beginLine, 14,entity.getInvoiceAmount(), style);
            //税额
            setSheetValue(sheet, beginLine, 15,entity.getTaxAmount(), style);
            //发票状态
            setSheetValue(sheet, beginLine, 16,formatInvoiceStatus(entity.getInvoiceStatus()), style);
            //签收状态
            setSheetValue(sheet, beginLine, 17,formatQs(entity.getQsStatus()), style);
            //签收日期
            setSheetValue(sheet, beginLine, 18, formatDate(entity.getQsDate()), style);
           /* //签收方式
            setSheetValue(sheet, beginLine, 17,formatQsType(entity.getQsType()), style);*/
            //jvcode
            setSheetValue(sheet, beginLine, 19,entity.getJvCode(), style);
            //companyCode
            setSheetValue(sheet, beginLine, 20,entity.getCompanyCode(), style);
            //业务类型
            setSheetValue(sheet, beginLine, 21,formatFlowType(entity.getFlowType()), style);
            beginLine++;
        }
    }

    private String formatRzhYesOrNo(String rzhYesorno) {
        if (StringUtils.isEmpty(rzhYesorno)){
            return "一 一";
        }
        if(rzhYesorno.equals("1")){
            return "已认证";
        }else if(rzhYesorno.equals("0")){
            return "未认证";
        }
        return "一 一";
    }
    private String formatQs(String rzhYesorno) {
        if (StringUtils.isEmpty(rzhYesorno)){
            return "一 一";
        }
        if(rzhYesorno.equals("1")){
            return "已签收";
        }else if(rzhYesorno.equals("0")){
            return "未签收";
        }
        return "一 一";
    }

    private String formatDate(Date source) {
        return source == null ? "" : (new DateTime(source.getTime())).toString(DEFAULT_SHORT_DATE_FORMAT);
    }

    private String formatRzhType(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "线上认证";
        } else  if("2".equals(authStatus)) {
            authStatusName = "线下认证";
        }
        return authStatusName;
    }

    private String formatAuthStatus(String authStatus) {
        String authStatusName = "";
        if("1".equals(authStatus)) {
            authStatusName = "已勾选";
        } else  if("2".equals(authStatus)) {
            authStatusName = "已确认";
        } else  if("3".equals(authStatus)) {
            authStatusName = "已发送认证";
        } else  if("4".equals(authStatus)) {
            authStatusName = "认证成功";
        } else if("5".equals(authStatus)) {
            authStatusName = "认证失败";
        }
     return authStatusName;
    }
    private String formatInvoiceStatus(String authStatus) {
        String authStatusName = "";
        if (StringUtils.isEmpty(authStatus)){
            return "";
        }
        if("1".equals(authStatus)) {
            authStatusName = "失控";
        } else  if("2".equals(authStatus)) {
            authStatusName = "作废";
        } else  if("3".equals(authStatus)) {
            authStatusName = "红冲";
        } else  if("4".equals(authStatus)) {
            authStatusName = "异常";
        } else if("0".equals(authStatus)) {
            authStatusName = "正常";
        }
        return authStatusName;
    }
    private String formatQsType(String authStatus) {
        String authStatusName = "";
        if (StringUtils.isEmpty(authStatus)){
            return "一 一";
        }
        if("1".equals(authStatus)) {
            authStatusName = "扫描仪签收";
        } else  if("2".equals(authStatus)) {
            authStatusName = "app签收";
        } else  if("3".equals(authStatus)) {
            authStatusName = "导入签收";
        } else  if("4".equals(authStatus)) {
            authStatusName = "手工签收";
        } else if("0".equals(authStatus)) {
            authStatusName = "扫码签收";
        }else if("5".equals(authStatus)) {
            authStatusName = "pdf上传签收";
        }
        return authStatusName;
    }
    private String formatFlowType(String type) {
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
