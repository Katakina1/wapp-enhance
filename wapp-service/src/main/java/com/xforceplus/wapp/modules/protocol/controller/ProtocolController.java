package com.xforceplus.wapp.modules.protocol.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poExcelEntity;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolDetailEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolExcelEntity;
import com.xforceplus.wapp.modules.protocol.entity.ProtocolInvoiceDetailEntity;
import com.xforceplus.wapp.modules.protocol.export.InvoiceDetailFailureExcel;
import com.xforceplus.wapp.modules.protocol.export.InvoiceDetailTemplateExport;
import com.xforceplus.wapp.modules.protocol.export.ProtocolExcel;
import com.xforceplus.wapp.modules.protocol.export.ProtocolFailureExcel;
import com.xforceplus.wapp.modules.protocol.export.ProtocolTemplateExport;
import com.xforceplus.wapp.modules.protocol.service.ProtocolService;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class ProtocolController extends AbstractImportExcelController {
    private final static Logger LOGGER = getLogger(ProtocolController.class);

    @Autowired
    private ProtocolService protocolService;

    private static final String STATUS = "status";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    @SysLog("协议列表查询")
    @RequestMapping("protocol/list")
    public R list(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        query.put("userCode",getUser().getUsercode());
        List<ProtocolEntity> list = protocolService.queryList(query);
        Integer count = protocolService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
    @SysLog("失败协议列表查询")
    @RequestMapping("protocolFailure/list")
    public R protocolFailureList(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        query.put("userCode",getUser().getUsercode());
        List<ProtocolEntity> list = protocolService.queryFailureList(query);
        Integer count = protocolService.queryFailureCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("协议明细查询")
    @RequestMapping("protocol/detail")
    public R getDetail(@RequestBody Map<String,Object> params){
        String venderId=(String)params.get("venderId");
        String protocolNo=(String)params.get("protocolNo");
        Object id=params.get("id");
        ProtocolEntity po= protocolService.queryProtocolById(id.toString());
        List<ProtocolDetailEntity> list = protocolService.queryDetailList(venderId,protocolNo,po.getAmount(),po.getCaseDate());
        return R.ok().put("detailList",list);
    }

    @SysLog("发票明细查询")
    @RequestMapping("protocol/invoiceDetail")
    public R getInvoiceDetail(@RequestBody Map<String,Object> params){
        String caseDate=(String)params.get("caseDate");
        String protocolNo=(String)params.get("protocolNo");
        String venderName=(String)params.get("venderName");//增加供应商名称查询条件
        caseDate=caseDate.substring(0,10);
        List<ProtocolInvoiceDetailEntity> list = protocolService.queryInvoiceDetailList(caseDate,protocolNo,venderName);
        return R.ok().put("detailList",list);
    }

    @SysLog("协议导出")
    @RequestMapping("export/protocol")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);
        params.put("userCode",getUser().getUsercode());
        //查询列表数据
        List<ProtocolExcelEntity> list = protocolService.selectExcelpolist(params);
        try {

            ExcelUtil.writeExcel(response,list,"协议导出","sheet1", ExcelTypeEnum.XLSX,ProtocolExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }




//        final String schemaLabel = getCurrentUserSchemaLabel();
//        params.put("schemaLabel", schemaLabel);
//        params.put("userCode",getUser().getUsercode());
//        final Map<String, List<ProtocolEntity>> map = newHashMapWithExpectedSize(1);
//        map.put("protocolList", protocolService.queryList(params));
//        //生成excel
//        final ProtocolExcel excelView = new ProtocolExcel(map, "export/protocol/protocolList.xlsx", "protocolList");
//        final String excelName =String.valueOf(new Date().getTime());
//        excelView.write(response, "protocolList" + excelName);
    }

    @SysLog("失败协议导出")
    @RequestMapping("export/protocolFailure")
    public void exportProtocolFailure(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);
        params.put("userCode",getUser().getUsercode());
        final Map<String, List<ProtocolEntity>> map = newHashMapWithExpectedSize(1);
        map.put("failureProtocolList", protocolService.queryFailureList(params));
        //生成excel
        final ProtocolFailureExcel excelView = new ProtocolFailureExcel(map, "export/protocol/failureProtocolList.xlsx", "failureProtocolList");
        final String excelName =String.valueOf(new Date().getTime());
        excelView.write(response, "failureProtocolList" + excelName);
    }

    @SysLog("失败发票明细导出")
    @RequestMapping("export/invoiceDetailFailure")
    public void exportInvoiceDetailFailure(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final Map<String, List<ProtocolInvoiceDetailEntity>> map = newHashMapWithExpectedSize(1);
        map.put("failureInvoiceDetailList", protocolService.queryInvoiceDetailFailureList(params));
        //生成excel
        final InvoiceDetailFailureExcel excelView = new InvoiceDetailFailureExcel(map, "export/protocol/failureInvoiceDetailList.xlsx", "failureInvoiceDetailList");
        final String excelName =String.valueOf(new Date().getTime());
        excelView.write(response, "failureInvoiceDetailList" + excelName);
    }

    @SysLog("协议模板下载")
    @AuthIgnore
    @RequestMapping("/export/protocolTemplate")
    public void protocolTemplate(HttpServletResponse response) {
        //生成excel
        final ProtocolTemplateExport excelView = new ProtocolTemplateExport("export/protocol/protocolTemplate.xlsx");
        excelView.write(response, "protocolTemplate");
    }

    @SysLog("协议删除")
    @RequestMapping("protocol/delete")
    public R delete(@RequestParam Map<String, Object> params) {
        protocolService.deletePorotocol(params);
        return R.ok();
    }

    @SysLog("发票明细模板下载")
    @AuthIgnore
    @RequestMapping("/export/invoiceDetailTemplate")
    public void invoiceDetailTemplate(HttpServletResponse response) {
        //生成excel
        final InvoiceDetailTemplateExport excelView = new InvoiceDetailTemplateExport("export/protocol/invoiceDetailTemplate.xlsx");
        excelView.write(response, "invoiceDetailTemplate");
    }
    @PostMapping(value = "/export/uploadProtocol")
    @SuppressWarnings("unchecked")
    public String uploadProtocol(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpServletResponse response) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }
        //session保存6个小时，防止导入中途session失效
        HttpSession session =request.getSession();
        session.setMaxInactiveInterval(360*60);
        //创建工作簿对象
        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }catch (Exception e){
            return new Gson().toJson(R.error("请导入合适的Excel文件！"));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Object> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 10000) {
            //导入协议
            result = insertProtocol(sheet, rowCount,response);
            if(result.get("fail")!=null){
                return new Gson().toJson(R.error("导入失败,请选择合适的Excel文件！"));
            }

        } else if (rowCount > 10000) {
            return new Gson().toJson(R.error("导入数据超过10000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        //获取导入成功和失败的数量
        String total = result.get("total").toString();
        String success = result.get("success").toString();
        String failure = result.get("failure").toString();
        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条<br>";
        return new Gson().toJson(R.ok(message));
    }

    @AuthIgnore
    @PostMapping(value = "/export/uploadInvoiceDetail", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request,HttpServletResponse response) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }
        //session保存6个小时，防止导入中途session失效
        HttpSession session =request.getSession();
        session.setMaxInactiveInterval(360*60);
        //创建工作簿对象
        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }catch (Exception e){
            return new Gson().toJson(R.error("导入失败,请选择合适的Excel文件！"));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Object> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 10000) {
            //导入发票明细
                result = insertInvoiceDetail(sheet, rowCount);
            if(result.get("fail")!=null){
                return new Gson().toJson(R.error("请选择正确的模板！"));
            }

        } else if (rowCount > 10000) {
            return new Gson().toJson(R.error("导入数据超过10000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        //获取导入成功和失败的数量
        String total = result.get("total").toString();
        String success = result.get("success").toString();
        String failure = result.get("failure").toString();
        if(failure.equals("-1")){
            failure="0";
        }

        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条<br>";
        return new Gson().toJson(R.ok(message));
    }


    /**
     * 读取excel文件获取协议数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertProtocol(Sheet sheet, int rowCount,HttpServletResponse response) {
        final List<ProtocolEntity> protocolList = newArrayList();

        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapProtocol(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）

            String status = (String) wrapResult.get(STATUS);

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加协议主信息到list
                ProtocolEntity protocol = (ProtocolEntity) wrapResult.get("protocol");
                protocol.setRow(i+1);
                protocolList.add(protocol);
            }
        }
        String userCode=getUser().getUsercode();
        //批量保存协议
        Map resultMap = protocolService.saveBatchProtocol(protocolList,userCode,response);
        return resultMap;
    }

    /**
     * 将Excel协议数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private Map<String, Object> wrapProtocol(Row row) {
        final Map<String, Object> result = newHashMap();
        final ProtocolEntity protocolEntity = new ProtocolEntity();

        //如果协议号为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row,0))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //为协议主信息赋值
        if(!Strings.isNullOrEmpty(getCellData(row, 0))) {
            protocolEntity.setProtocolNo(getCellData(row, 0));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 1))) {
            protocolEntity.setVenderId(getCellData(row, 1));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 2))) {
            protocolEntity.setDeptNo(getCellData(row, 2));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 3))) {
            protocolEntity.setSeq(getCellData(row, 3));
        }
        protocolEntity.setPayItem(getCellData(row, 4));
        protocolEntity.setPayCompanyCode(getCellData(row, 5));
        if(!Strings.isNullOrEmpty(getCellData(row, 6))) {
            protocolEntity.setAmount(new BigDecimal(getCellData(row, 6)).setScale(2, BigDecimal.ROUND_DOWN));
        }
        protocolEntity.setProtocolStatus(getCellData(row, 7));
        if("协议审批完成".equals(protocolEntity.getProtocolStatus())){
            protocolEntity.setProtocolStatus("1");
        } else{
            protocolEntity.setProtocolStatus("0");
        }
        //获取excel日期单元格的数据(定案日期)
        Cell cell = row.getCell(8);
         if(1==cell.getCellType()){
          String caseDateString =   getCellData(row, 8);
          try {
              protocolEntity.setCaseDate(new SimpleDateFormat("yyyy/MM/dd").parse(caseDateString));
          } catch (Exception e){
              LOGGER.error("定案日期格式化异常!",e);
          }
         }else{
             Date date = cell.getDateCellValue();
             protocolEntity.setCaseDate(date);
         }


        //如果扣款原因不为空，代表有协议明细，为协议明细赋值
        if(!Strings.isNullOrEmpty(getCellData(row,9))){
            protocolEntity.setReason(getCellData(row,9));
            protocolEntity.setNumber(getCellData(row,10));
            if(isScientific(getCellData(row,11))){
                protocolEntity.setNumberDesc(new BigDecimal(getCellData(row,11)).stripTrailingZeros().toPlainString());
            }else {
                protocolEntity.setNumberDesc(getCellData(row,11));
            }
            if(!Strings.isNullOrEmpty(getCellData(row, 12))) {
                protocolEntity.setDetailAmount(new BigDecimal(getCellData(row, 12)).setScale(2, BigDecimal.ROUND_DOWN));
            }
            protocolEntity.setStore(getCellData(row,13));
        }

        result.put(STATUS, STATUS_NORMAL);
        result.put("protocol", protocolEntity);

        return result;
    }

    /**
     * 读取excel文件获取发票明细数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertInvoiceDetail(Sheet sheet, int rowCount){
        final List<ProtocolInvoiceDetailEntity> invoiceDetailList = newArrayList();
        final Map<String, Integer> result = newHashMap();


        Row row;
        Integer errorCount=0;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapInvoiceDetail(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                errorCount=errorCount+1;
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加协议主信息到list
                invoiceDetailList.add((ProtocolInvoiceDetailEntity) wrapResult.get("invoiceDetail"));
            }
        }

        //批量保存协议
        Integer success = protocolService.saveBatchInvoiceDetail(invoiceDetailList);
        result.put("failure", invoiceDetailList.size() - success+errorCount);
        result.put("success", success);
        result.put("total", invoiceDetailList.size()+errorCount);
        return result;
    }

    /**
     * 将Excel发票明细数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private Map<String, Object> wrapInvoiceDetail(Row row){
        final Map<String, Object> result = newHashMap();
        final ProtocolInvoiceDetailEntity detailEntity = new ProtocolInvoiceDetailEntity();

        //如果供应商号为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row,2))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //获取excel日期单元格的数据(日期)
        Cell cell = row.getCell(0);
        if(cell!=null && 1==cell.getCellType()){
            String dateString =   getCellData(row, 0);
            try {
                if(dateString!=null){
                    detailEntity.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
                }else{
                    detailEntity.setDate(null);
                }
            } catch (Exception e){
                LOGGER.error("日期格式化异常!",e);
            }
        }else{
            if( cell !=null && cell.getDateCellValue()!=null){
                Date date = cell.getDateCellValue();
                if(date!=null){
                    detailEntity.setDate(date);
                }else{
                    detailEntity.setDate(null);
                }
            }else{
                detailEntity.setDate(null);
            }
        }

        //为协议主信息赋值
        if(!Strings.isNullOrEmpty(getCellData(row, 1))) {
            detailEntity.setSeq(getCellData(row, 1));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 2))) {
            try{
                detailEntity.setVenderId(new BigDecimal(getCellData(row, 2)).stripTrailingZeros().toPlainString());
            }catch (Exception e){
                result.put("fail", "notsuccess");
                return result;
            }

            //供应商号如果不足6位，前面补0
            DecimalFormat g1=new DecimalFormat("000000");
            String venderId = g1.format(Integer.valueOf(detailEntity.getVenderId()));
            detailEntity.setVenderId(venderId);
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 3))) {
            try {
                detailEntity.setCompanyName(getCellData(row, 3));
            }catch (Exception e){
                result.put("fail", "notsuccess");
                return result;
            }

        }
        if(!Strings.isNullOrEmpty(getCellData(row, 4))) {
            try {
                detailEntity.setInvoiceNo(new BigDecimal(getCellData(row, 4)).stripTrailingZeros().toPlainString());
            }catch (Exception e){
                result.put("fail", "notsuccess");
                return result;
            }

            //发票号如果不足8位，前面补0
            DecimalFormat g1=new DecimalFormat("00000000");
            String invoiceNo = g1.format(Integer.valueOf(detailEntity.getInvoiceNo()));
            detailEntity.setInvoiceNo(invoiceNo);
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 5))) {
            try {
                detailEntity.setInvoiceAmount(new BigDecimal(getCellData(row, 5)).setScale(2, BigDecimal.ROUND_DOWN));
            }catch (Exception e){
                result.put("fail", "notsuccess");
                return result;
            }

        }
        try {
            detailEntity.setFapiao(getCellData(row, 6));
        }catch (Exception e){
            result.put("fail", "notsuccess");
            return result;
        }

        //如果Fapi发票不为空，截取协议号
        if(StringUtils.isNotBlank(detailEntity.getFapiao())){
            char[] chars = detailEntity.getFapiao().toCharArray();
            //获取协议号长度，用来截取协议号
            int n =0;
            for(int i=chars.length-1;i<chars.length;i--){
                //从发票信息倒着判断，判断到不是数字就终止判断，获取协议号长度
                if(Character.isDigit(chars[i])) {
                    ++n;
                } else {
                    break;
                }

            }
            String fapiao = detailEntity.getFapiao();
            String protocol = fapiao.substring(fapiao.length()-n,fapiao.length());
            detailEntity.setProtocolNo(protocol);
        }
        detailEntity.setCompanyCode(getCellData(row, 7));
        detailEntity.setContent(getCellData(row, 8));
        detailEntity.setNotes(getCellData(row, 9));
        //如果备注不为空，从备注里获取定案日期
        if(StringUtils.isNotBlank(detailEntity.getNotes())){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String notes = detailEntity.getNotes().trim();
            String caseDateString = notes.trim().substring(notes.length()-10,notes.length());
            try {
                Date caseDate = dateFormat.parse(caseDateString);
                detailEntity.setCaseDate(caseDate);
            } catch (ParseException e){
                LOGGER.error("定案日期解析异常!",e);
                //定案日期解析异常，跳过此行数据，继续读取下一条
                result.put(STATUS, STATUS_NULL);
                return result;
            }
        }
        //获取excel日期单元格的邮寄时间(日期)
        Cell postDateCell = row.getCell(10);
        if(postDateCell!=null){
            String dateString =  getCellData(row, 10);
            try {
                if(!dateString.equals("")){
                    detailEntity.setPostDate(new SimpleDateFormat("yyyy-MM-dd").parse(dateString));
                }else{
                    detailEntity.setPostDate(null);
                }

            } catch (Exception e){
                LOGGER.error("邮寄日期格式化异常!",e);
                result.put(STATUS, STATUS_NULL);
                return result;
            }
        }/*else{
           // Date date = cell.getDateCellValue();
          //  detailEntity.setPostDate(date);
            if( cell !=null && cell.getDateCellValue()!=null){
                Date date = cell.getDateCellValue();
                if(date!=null){
                    detailEntity.setPostDate(date);
                }else{
                    detailEntity.setPostDate(date);
                }
            }else{
                detailEntity.setPostDate(null);
            }
        }*/
        detailEntity.setPostNo(getCellData(row, 11));
        detailEntity.setPostCompany(getCellData(row, 12));

        result.put(STATUS, STATUS_NORMAL);
        result.put("invoiceDetail", detailEntity);

        return result;
    }




    private static boolean isScientific(String str){
        Pattern pattern = Pattern.compile("^[+-]?[\\d]+([.][\\d]*)?([Ee][+-]?[\\d]+)?$");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }
}
