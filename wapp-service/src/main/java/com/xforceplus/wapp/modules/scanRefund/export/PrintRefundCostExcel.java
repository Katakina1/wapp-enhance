package com.xforceplus.wapp.modules.scanRefund.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.joda.time.DateTime.now;

public final class PrintRefundCostExcel extends AbstractExportExcel{
    private final List<Map<String, Object>> mapList;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    private String vendor;

    /**
     * excel模板名
     */
    private final String excelName;

    public PrintRefundCostExcel(List<Map<String, Object>> mapList, String excelTempPath, String excelName) {
        this.mapList = mapList;
        this.excelTempPath = excelTempPath;
        this.excelName = excelName;
//        this.vendor = vendor;
    }

    @Override
    protected String getExcelUri() {
        return excelTempPath;
    }

    @Override
    protected void buildExcel(XSSFWorkbook workBook) {
        //获取工作表



        for(int i=0;i<mapList.size();i++) {

            XSSFSheet sheet = workBook.getSheetAt(i);
            workBook.setSheetName(i,this.mapList.get(i).get("vendor").toString());


            //获取要导出的数据
            final List<EnterPackageNumberEntity> list = (List<EnterPackageNumberEntity>) this.mapList.get(i).get("printRefundList");

            //设置开始行
            int beginLine = 6;
            //获取单元格样式
//            final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
            XSSFCellStyle style = workBook.createCellStyle();
            final Font font = workBook.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("宋体");
//            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            style.setFont(font);

            XSSFCellStyle style1 = workBook.createCellStyle();
            final Font font1 = workBook.createFont();
            font1.setFontHeightInPoints((short) 10);
            font1.setFontName("宋体");
//            font1.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style1.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            style1.setWrapText(true);
            style1.setFont(font1);

            XSSFCellStyle style2 = workBook.createCellStyle();
            final Font font2 = workBook.createFont();
            font2.setFontHeightInPoints((short) 10);
            font2.setFontName("宋体");
//            font2.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style2.setBorderTop(XSSFCellStyle.BORDER_THIN); //上
            style2.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
            style2.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
            style2.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
            style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
//            style2.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直居中
            style2.setFont(font2);

            XSSFCellStyle style3 = workBook.createCellStyle();
            final Font font3 = workBook.createFont();
            font3.setFontHeightInPoints((short) 10);
            style3.setBorderTop(XSSFCellStyle.BORDER_THIN); //上
            style3.setBorderBottom(XSSFCellStyle.BORDER_THIN); //下边框
            style3.setBorderLeft(XSSFCellStyle.BORDER_THIN);//左边框
            style3.setBorderRight(XSSFCellStyle.BORDER_THIN);//右边框
            style3.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            style3.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);//垂直居中
            style3.setWrapText(true);
            style3.setFont(font3);

            XSSFCellStyle style4 = workBook.createCellStyle();
            final Font font4 = workBook.createFont();
            font4.setFontHeightInPoints((short) 10);
            style4.setFont(font4);

            XSSFCellStyle style5 = workBook.createCellStyle();
            final Font font5 = workBook.createFont();
            font5.setFontHeightInPoints((short) 10);
            font5.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style5.setFont(font5);


            if(i>0) {
                sheet.setColumnWidth(0,256*10);
                sheet.setColumnWidth(1,256*8);
                sheet.setColumnWidth(2,256*8);
                sheet.setColumnWidth(3,256*10);
                sheet.setColumnWidth(4,256*12);
                sheet.setColumnWidth(5,256*9);
                sheet.setColumnWidth(6,256*9);
                sheet.setColumnWidth(7,256*9);
                sheet.setColumnWidth(8,256*12);
                sheet.createRow(0).setHeight((short) 450);
                sheet.createRow(1).setHeight((short) 400);
                sheet.createRow(2).setHeight((short) 400);
                sheet.createRow(3).setHeight((short) 400);
                sheet.createRow(4).setHeight((short) 400);
                sheet.createRow(5).setHeight((short) 400);
                CellRangeAddress callRangeAddress =new CellRangeAddress(0,0,0,8);
                sheet.addMergedRegion(callRangeAddress);
                setSheetValue(sheet, 0, 0, "费用退票信息表",style);
                if(list.get(0).getApplicantName() != null){
                    setSheetValue(sheet, 1, 0, "总部/门店：",style1);
                    setSheetValue(sheet, 1, 1, list.get(0).getShopNo(),style1);
                    setSheetValue(sheet, 1, 3, "收件人：",style1);
                    setSheetValue(sheet, 1, 4, list.get(0).getApplicantNo(),style1);
                    setSheetValue(sheet, 1, 6, "收件人电话：",style1);
                    setSheetValue(sheet, 1, 7, list.get(0).getApplicantCall(),style1);
                }
                setSheetValue(sheet, 2, 0, "发票共",style1);
                setSheetValue(sheet, 2, 2, "份",style1);
                setSheetValue(sheet, 2, 3, "供应商号：",style1);
                setSheetValue(sheet, 3, 0, "邮寄方式：",style1);
                setSheetValue(sheet, 3, 3, "销货单位：",style1);
                CellRangeAddress callRangeAddress1 =new CellRangeAddress(4,4,0,1);
                sheet.addMergedRegion(callRangeAddress1);
                setSheetValue(sheet, 4, 0, "一、退货内容：",style1);

                setSheetValue(sheet, 5, 0, "序号",style2);
                setSheetValue(sheet, 5, 1, "EPS单号",style2);
                setSheetValue(sheet, 5, 2, "发票号码",style2);
                setSheetValue(sheet, 5, 3, "发票金额",style2);
                setSheetValue(sheet, 5, 4, "签收日期",style2);
                setSheetValue(sheet, 5, 5, "退票日期",style2);
                setSheetValue(sheet, 5, 6, "退票类型",style2);
                setSheetValue(sheet, 5, 7, "退票编码",style2);
                setSheetValue(sheet, 5, 8, "退票原因",style2);
            }
            if(list.get(0).getApplicantName() != null){
                setSheetValue(sheet, 1, 0, "总部/门店：",style1);
                setSheetValue(sheet, 1, 1, list.get(0).getShopNo(),style1);
                setSheetValue(sheet, 1, 3, "收件人：",style1);
                setSheetValue(sheet, 1, 4, list.get(0).getApplicantNo(),style1);
                setSheetValue(sheet, 1, 6, "收件人电话：",style1);
                setSheetValue(sheet, 1, 7, list.get(0).getApplicantCall(),style1);
            }
            setSheetValue(sheet, 3, 1, list.get(0).getRebateDate(),style1);
            setSheetValue(sheet, 2, 1, this.mapList.get(i).get("total").toString(),style1);
            setSheetValue(sheet, 2, 4, list.get(0).getVenderId(),style1);
            //setSheetValue(sheet, 3, 1, list.get(0).getMailCompany(),style1);
            //setSheetValue(sheet, 3, 1, this.mapList.get(i).get("postType").toString(),style1);
            setSheetValue(sheet, 3, 4, list.get(0).getXfName(),style1);
            int index = 1;
            //数据填入excel
            for (EnterPackageNumberEntity entity : list) {
                //序号
                setSheetValue(sheet, beginLine, 0, index++,style3);
                //EPS单号
                setSheetValue(sheet, beginLine, 1, entity.getEpsNo(),style3);
                //发票号码
                setSheetValue(sheet, beginLine, 2, entity.getInvoiceNo(),style3);
                if(entity.getInvoiceAmount()!=null) {
                	setSheetValue(sheet, beginLine, 3, entity.getInvoiceAmount().substring(0, entity.getInvoiceAmount().length()-2),style3);
                }else {
                //发票金额
                setSheetValue(sheet, beginLine, 3, "",style3);
                }
                //签收日期
                setSheetValue(sheet, beginLine, 4, entity.getQsDate(),style3);
                //退票日期
                setSheetValue(sheet, beginLine, 5, entity.getRebateDate(),style3);
                //退票类型
                setSheetValue(sheet, beginLine, 6, entity.getFlowType(),style3);
                //退票编码
                setSheetValue(sheet, beginLine, 7, entity.getRefundCode(),style3);
                //退票原因
                setSheetValue(sheet, beginLine, 8, entity.getRefundReason(),style3);

                beginLine++;
            }

            beginLine = beginLine + 3;
            setSheetValue(sheet, beginLine++, 0, "二、重要提示:",style4);
            setSheetValue(sheet, beginLine++, 0, "1、请供应商收到退单以后，在结算平台上根据退票原因修改，并按原流程重新申请付款，待审批完成邮寄发票至沃尔玛财务部；",style4);
            setSheetValue(sheet, beginLine++, 0, "2、若对结算金额有疑问，请咨询对应沃尔玛业务合作伙伴；",style4);
            setSheetValue(sheet, beginLine++, 0, "3、为防止贵司发票过期，请先与我司联系后再申请费用付款与邮寄。",style4);


            beginLine = beginLine + 3;
            setSheetValue(sheet, beginLine++, 0, "三、联系方法：",style4);
            setSheetValue(sheet, beginLine++, 0, "如有疑问请联系我司：电邮：CNWSIPC@walmart.com",style4);
            setSheetValue(sheet, beginLine++, 0, "邮寄地址：深圳市福田区农林路69号深国投广场二号楼4楼",style4);
            setSheetValue(sheet, beginLine++, 0, "收件人：沃尔玛（中国）投资有限公司  财务部发票处理中心-费用付款； 邮编:518040 ",style4);


            workBook.createSheet();
        }

    }

    private String formatDate(Date source) {
        return source == null ? "" : (new SimpleDateFormat("yyyy-MM-dd")).format(source);
    }



}
