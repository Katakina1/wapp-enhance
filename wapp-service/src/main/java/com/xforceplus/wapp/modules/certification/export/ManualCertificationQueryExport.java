package com.xforceplus.wapp.modules.certification.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.certification.entity.InvoiceCertificationEntity;
import com.xforceplus.wapp.modules.collect.entity.InvoiceCollectionInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
public class ManualCertificationQueryExport extends AbstractExportExcel {

    private final Map<String, List<InvoiceCertificationEntity>> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;

    public ManualCertificationQueryExport(Map<String, List<InvoiceCertificationEntity>> map, String excelTempPath, String excelName) {
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
        final List<InvoiceCertificationEntity> list = this.map.get(excelName);
        //设置开始行
        int beginLine = 2;
        //获取单元格样式
        final XSSFCellStyle style = getCellStyle(sheet, 2, 0);
        final Font font = workBook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(FONT);
        style.setFont(font);
        int index=1;
        //数据填入excel
        for (InvoiceCertificationEntity entity : list) {
            //序号
            setSheetValue(sheet, beginLine, 0, index++, style);
            //发票代码
            setSheetValue(sheet, beginLine, 1, entity.getInvoiceCode(), style);
            //发票号码
            setSheetValue(sheet, beginLine, 2, entity.getInvoiceNo(), style);
            //开票日期
            setSheetValue(sheet, beginLine, 3,formatDate(entity.getInvoiceDate()), style);
            //金额
            try {
            	setSheetValue(sheet, beginLine, 4,entity.getInvoiceAmount().setScale(2,BigDecimal.ROUND_HALF_EVEN).toString(), style);
            }catch(Exception e) {
            	setSheetValue(sheet, beginLine, 4,"0.00", style);
            	
            }
            //税额
            try {
            	setSheetValue(sheet, beginLine, 5,entity.getTaxAmount().setScale(2,BigDecimal.ROUND_HALF_EVEN).toString(), style);
            	
            }catch(Exception e) {
            	setSheetValue(sheet, beginLine, 5,"0.00", style);
            	
            }
            //价税合计
            try {
            	setSheetValue(sheet, beginLine, 6,entity.getTotalAmount().setScale(2,BigDecimal.ROUND_HALF_EVEN).toString(), style);
            	
            }catch(Exception e) {
            	
            	setSheetValue(sheet, beginLine, 6,"0.00", style);
            }
            //供应商号
            setSheetValue(sheet, beginLine, 7, entity.getVenderid(), style);
            //凭证号
            setSheetValue(sheet, beginLine, 8,entity.getCertificateNo(), style);
            //JV
            setSheetValue(sheet, beginLine, 9,entity.getJvcode(), style);
            //公司代码
            setSheetValue(sheet, beginLine, 10,entity.getCompanyCode(), style);
            //业务类型
            setSheetValue(sheet, beginLine, 11,formatFlowType(entity.getFlowType()), style);
            beginLine++;
        }
    }



    private String formatDate(String source) {
        return source == null ? "" : source.substring(0,10);
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
                                                    "6".equals(type) ? " 租赁" :"7".equals(type) ? "直接认证": "8".equals(type) ? "Ariba":"";
    }
}
