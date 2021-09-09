package com.xforceplus.wapp.modules.report.export;

import com.xforceplus.wapp.common.safesoft.AbstractExportExcel;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.transferOut.entity.DetailEntity;
import com.google.common.io.Closer;
import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ComprehensiveInvoiceQueryExcelMX extends AbstractExportExcel{
    private final Map<String, Object> map;

    /**
     * excel模板地址
     */
    private final String excelTempPath;

    /**
     * excel模板名
     */
    private final String excelName;
    private static final Logger LOGGER = LoggerFactory.getLogger(ComprehensiveInvoiceQueryExcelMX.class);
    private static final String ERROR_MESSAGE = "the java IO error:";

    public ComprehensiveInvoiceQueryExcelMX(Map<String, Object> map, String excelTempPath, String excelName) {
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

    }

    /**
     * 文件导出，可自定义文件名
     * @param response 响应
     * @param excelName 文件名
     */

    public void write2(HttpServletResponse response, String excelName,String excelName2,HttpServletRequest request,String excelNameSuffix) throws IOException, InvalidFormatException {


        List<File> fileList = new ArrayList<File>();

        //获取要导出的数据
        Map<Long, List<DetailEntity>> mapList=(Map<Long, List<DetailEntity>> )this.map.get(excelName);
        final List<ComprehensiveInvoiceQueryEntity> list = (List<ComprehensiveInvoiceQueryEntity>) this.map.get(excelName2);
        // 生成excel
        String path = request.getServletContext().getRealPath("/");
        //list下标计数器
        int indexNo=0;
        //总信息条数计数器,不清空，为了新excel能够继续读取中断前的数据
        int totalNo=0;
        //是否为新建的Excel
        boolean newExcel = false;
        for (int j = 0; list.size()>indexNo;j++) {
            final Closer closer = Closer.create();
            //获取文件流
            final InputStream fs = closer.register(Resources.asByteSource(Resources.getResource(getExcelUri())).openStream());
            //创建工作簿
            final XSSFWorkbook book = new XSSFWorkbook(OPCPackage.open(fs));

            //获取工作表
            final XSSFSheet sheet = book.getSheetAt(0);
            //设置开始行
            int beginLine = 2;
            //获取单元格样式
            final XSSFCellStyle style = getCellStyle(sheet, 0, 5);
            final Font font = book.createFont();
            font.setFontHeightInPoints((short) 12);
            font.setFontName("宋体");
            style.setFont(font);


            //当前excle条数计数器
            int no=0;
            //当前主信息条数计数器
            int no2=0;


            for (int i = 1; i < 400-1&&list.size()>indexNo; i++) {
                //循环明细数据

                ComprehensiveInvoiceQueryEntity entity = list.get(i-1);

                if(newExcel) {
                    entity = list.get(totalNo);
                }

                List<DetailEntity> jsonArrStr=mapList.get(entity.getId());
                List<DetailEntity> jsonArr=null;
                int jsonarrlength=0;
                if(jsonArrStr==null){
                    jsonArr=null;
                }else if(jsonArrStr!=null){
                    jsonArr =jsonArrStr;
                    jsonarrlength=jsonArr.size();
                }

                //生成前计算当前excel是否超出最大值,如超出则不加入并清空当前条数计数器
                //加入条件 主信息不超过200条，
                if(no2<200&&no+jsonarrlength+1<=400){
                    indexNo++;
                    no2++;
                    totalNo++;
                    if (jsonArr!=null&&jsonArr.size()>0) {
                        for (DetailEntity detailEntity : jsonArr) {
                            //序号
                            setSheetValue(sheet, no+2, 0, no+1, style);
                            //发票代码
                            setSheetValue(sheet, no+2, 1, entity.getInvoiceCode(), style);
                            //发票号码
                            setSheetValue(sheet, no+2, 2, entity.getInvoiceNo(), style);
                            //发票类型
                            setSheetValue(sheet, no+2, 3, formatInvoiceType(entity.getInvoiceType()), style);
                            //开票日期
                            setSheetValue(sheet, no+2, 4, formatDateString(entity.getInvoiceDate()), style);
                            //购方税号
                            setSheetValue(sheet, no+2, 5, entity.getGfTaxNo(), style);
                            //购方名称
                            setSheetValue(sheet, no+2, 6, entity.getGfName(), style);
                            //销方税号
                            setSheetValue(sheet, no+2, 7, entity.getXfTaxNo(), style);
                            //销方名称
                            setSheetValue(sheet, no+2, 8, entity.getXfName(), style);
                            //金额
                            setSheetValue(sheet, no+2, 9, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
                            //税额
                            setSheetValue(sheet, no+2, 10, CommonUtil.formatMoney(entity.getTaxAmount()), style);
                            //价税合计
                            setSheetValue(sheet, no+2, 11, CommonUtil.formatMoney(entity.getTotalAmount()), style);
                            //货物或应税劳务名称
                            setSheetValue(sheet, no+2, 12, detailEntity.getGoodsName(), style);
                            //规格型号
                            setSheetValue(sheet, no+2, 13, detailEntity.getModel(), style);
                            //单位
                            setSheetValue(sheet, no+2, 14, detailEntity.getUnit(), style);
                            //数量
                            setSheetValue(sheet, no+2, 15, detailEntity.getNum(), style);
                            //单价
                            setSheetValue(sheet, no+2, 16, detailEntity.getUnitPrice(), style);
                            //金额
                            setSheetValue(sheet, no+2, 17, detailEntity.getDetailAmount(), style);
                            //税额
                            setSheetValue(sheet, no+2, 18, detailEntity.getTaxAmount(), style);
                            //税率
                            setSheetValue(sheet, no+2, 19, detailEntity.getTaxRate(), style);
                            no++;
                        }
                    }else{
                        //序号
                        setSheetValue(sheet, no+2, 0, no+1, style);
                        //发票代码
                        setSheetValue(sheet, no+2, 1, entity.getInvoiceCode(), style);
                        //发票号码
                        setSheetValue(sheet, no+2, 2, entity.getInvoiceNo(), style);
                        //发票类型
                        setSheetValue(sheet, no+2, 3, formatInvoiceType(entity.getInvoiceType()), style);
                        //开票日期
                        setSheetValue(sheet, no+2, 4, formatDateString(entity.getInvoiceDate()), style);
                        //购方税号
                        setSheetValue(sheet, no+2, 5, entity.getGfTaxNo(), style);
                        //购方名称
                        setSheetValue(sheet, no+2, 6, entity.getGfName(), style);
                        //销方税号
                        setSheetValue(sheet, no+2, 7, entity.getXfTaxNo(), style);
                        //销方名称
                        setSheetValue(sheet, no+2, 8, entity.getXfName(), style);
                        //金额
                        setSheetValue(sheet, no+2, 9, CommonUtil.formatMoney(entity.getInvoiceAmount()), style);
                        //税额
                        setSheetValue(sheet, no+2, 10, CommonUtil.formatMoney(entity.getTaxAmount()), style);
                        no++;
                    }
                }else{
                    no =0 ;
                    no2 = 0;
                    newExcel = true;
                    break;
                }
            }
            FileOutputStream out = null;
            File file = File.createTempFile("发票明细"+excelNameSuffix, ".xlsx",new File(path));
            out = FileUtils.openOutputStream(file);
            book.write(out);//将数据写到指定文件
            fileList.add(file);
            out.close();

        }
//        OutputStream out = response.getOutputStream();
//        FileInputStream inStream = new FileInputStream(zip);
//        byte[] buf = new byte[4096];
//        int readLength;
//        while (((readLength = inStream.read(buf)) != -1)) {
//            out.write(buf, 0, readLength);
//        }
//        inStream.close();
        if(null != fileList && !fileList.isEmpty()){
            byte[] buf = new byte[1024];
            FileOutputStream os = null;
            File fileZip = File.createTempFile("发票明细"+excelNameSuffix, ".zip",new File(path));
            os = FileUtils.openOutputStream(fileZip);


            ZipOutputStream zipOut = new ZipOutputStream(os);
            for(File file : fileList){
                FileInputStream in = new FileInputStream(file);
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while((len = in.read(buf)) > 0){
                    zipOut.write(buf, 0, len);
                }
                zipOut.closeEntry();
                in.close();
                file.delete();//清除临时文档
            }
            zipOut.close();
            String name = fileZip.getName();//获取要下载的文件名
            //第一步：设置响应类型
            response.setContentType("application/force-download");//应用程序强制下载
            //第二读取文件

            InputStream in = new FileInputStream(path+name);
            //设置响应头，对文件进行url编码
            name = URLEncoder.encode(name, "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="+name);
            response.setContentLength(in.available());

            //第三步：老套路，开始copy
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[1024];
            int len = 0;
            while((len = in.read(b))!=-1){
                out.write(b, 0, len);
            }
            out.flush();
            out.close();
            in.close();





        }


    }

    /**
     *
     * @param srcfile 文件名数组
     * @param zipfile 压缩后文件
     */
    public static void zipFiles(java.io.File[] srcfile, java.io.File zipfile) {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipfile));
            for (int i = 0; i < srcfile.length; i++) {
                FileInputStream in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private String formatDateString(String date){
        return date == null ? "" : date.substring(0, 10);
    }



    private String formatInvoiceType(String status){
        return null==status ? "" :
                "01".equals(status) ? "增值税专用发票" :
                        "03".equals(status) ? "机动车销售统一发票" :
                                "04".equals(status) ? "增值税普通发票" :
                                        "10".equals(status) ? "增值税电子普通发票" :
                                                "11".equals(status) ? "增值税普通发票（卷票）" :
                                                    "14".equals(status) ? "增值税电子普通发票（通行费）" : "";
    }
}
