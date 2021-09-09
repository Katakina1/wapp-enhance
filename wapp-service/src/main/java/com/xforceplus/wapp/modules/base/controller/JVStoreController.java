package com.xforceplus.wapp.modules.base.controller;


import com.aisinopdf.text.pdf.parser.L;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.JVStoreEntity;
import com.xforceplus.wapp.modules.base.entity.ScanPathEntity;
import com.xforceplus.wapp.modules.base.export.JVStoreTemplateExport;
import com.xforceplus.wapp.modules.base.service.JVStoreService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

/**
 * @author toffler
 * jvstore
 * */
@RestController
public class JVStoreController extends AbstractController {
    private final static Logger LOGGER = getLogger(JVStoreController.class);

    private static final String STATUS = "status";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    @Autowired
    private JVStoreService service;
    /**
     * 查询
     * */
    @SysLog("JVStore查询")
    @RequestMapping("/base/jvstore/list")
    public R list(JVStoreEntity entity){
        //获取当前页面
        final Integer page = entity.getPage();
        if(page!=null){
            //分页查询起始值
            entity.setOffset((page - 1) * entity.getLimit());
        }
        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<JVStoreEntity> entityList = service.queryList(getCurrentUserSchemaLabel(), entity);
        int total = service.queryTotal(getCurrentUserSchemaLabel(), entity);
        if(page!=null) {
            PageUtils pageUtil = new PageUtils(entityList, total, entity.getLimit(), page);
            return R.ok().put("page", pageUtil);
        }else{
            return  R.ok().put("optionList", entityList);
        }
    }

    /**
     * 修改
     * */
    @SysLog("JVStore修改")
    @RequestMapping("/base/jvstore/update")
    public R update(@RequestBody JVStoreEntity entity){
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        service.update(getCurrentUserSchemaLabel(),entity);
        return R.ok();
    }

    /**
     * 删除
     * */
    @SysLog("JVStore删除")
    @RequestMapping("/base/jvstore/delete")
    public R delete(@RequestBody JVStoreEntity entity){
        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        service.delete(getCurrentUserSchemaLabel(), entity);
        return R.ok();
    }

    /**
     * 添加
     * */
    @SysLog("JVStore添加")
    @RequestMapping("/base/jvstore/save")
    public R save(@RequestBody JVStoreEntity entity){
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        service.save(getCurrentUserSchemaLabel(),entity);
        return R.ok();
    }

    /**
     * 查询jv门店名称
     * */
    @SysLog("查询jv门店名称")
    @RequestMapping("/base/jvstore/queryjv")
    public R queryjv(){
        List<String> jvcodes = service.queryjv();
        return R.ok().put("jvcodes",jvcodes);
    }

    @SysLog("模板下载")
    @AuthIgnore
    @RequestMapping("export/jvStoreTemplate")
    public void jvStoreTemplate(HttpServletResponse response) {
        //生成excel
        final JVStoreTemplateExport excelView = new JVStoreTemplateExport("export/base/jvStoreTemplate.xlsx");
        excelView.write(response, "jvStoreTemplate");
    }

    @PostMapping(value = "/export/upload_jvStore", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam("type")String type, HttpServletRequest request, HttpServletResponse response) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }
        //session保存12个小时，防止导入中途session失效
        HttpSession session =request.getSession();
        session.setMaxInactiveInterval(720*60);
        //创建工作簿对象
        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }catch (Exception e){
            return new Gson().toJson(R.error("请导入合适的Excel文件模板！"));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Object> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 50000) {
            if("jvStore".equals(type)) {
                //导入协议
                result = insertJVStore(sheet, rowCount,response);
                if(result.get("fail")!=null){
                    return new Gson().toJson(R.error("导入失败,请选择合适的Excel文件！"));
                }
            }
        } else if (rowCount > 50000) {
            return new Gson().toJson(R.error("导入数据超过50000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }
        //获取导入成功和失败的数量
        String total = result.get("total").toString();
        String success = result.get("success").toString();
        String failure = result.get("failure").toString();
        String nullFail = result.get("nullFail").toString();
        String outFail = result.get("outFail").toString();

        String message = "共计导入：" + total + "条，" + "成功：" + success + "条<br>" + "失败:" + failure + "条<br>" + "其中：数据含有空值" + nullFail + "条<br>数据过长" + outFail + "条<br>";
        return new Gson().toJson(R.ok(message));
    }

    /**
     * 获取工作簿对象
     * @param file 导入的文件
     * @return 工作簿对象
     * @throws ExcelException 异常
     */
    protected static Workbook getWorkBook(MultipartFile file) throws ExcelException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误，请导入.xls或.xlsx文件!");
            }
            return workbook;
        } catch (IOException e) {
            LOGGER.error("Excel读取错误:{}", e);
            throw new ExcelException(ExcelException.READ_ERROR, "Excel读取错误!");
        }
    }

    /**
     * 读取excel文件获取协议数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertJVStore(Sheet sheet, int rowCount,HttpServletResponse response) {
        final List<JVStoreEntity> list = newArrayList();
        final Map<String, Object> result = newHashMap();
        int nullCount = 0;
        //判断是否为正确模板
        String[] topics = {"JV代码","商场代码","商场中文名","商场税务","JV对应门店名称","纳税人识别号"};
        for(int k = 0;k<topics.length;k++){
            if(!topics[k].equals( getCellData(sheet.getRow(0), k))){
                result.put("fail","数据模板错误!");
                return result;
            }
        }
        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapJV(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
//            if(wrapResult.get("fail")!=null){
//                return wrapResult;
//            }
            String status = (String) wrapResult.get(STATUS);
            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加信息到list
                JVStoreEntity entity = (JVStoreEntity) wrapResult.get("jvStore");
                entity.setRow(i+1);
                entity.setStoreTax(entity.getStoreTax().replace(".0",""));
                entity.setStoreCode(entity.getStoreCode().replace(".0",""));
                list.add(entity);
            }
            if(STATUS_NULL.equals(status)){
                nullCount++;
            }
        }
        String userCode=getUser().getUsercode();
        //批量保存协议
        Map<String,Integer> resultMap = service.saveBatchJVStore(list,userCode,response);

        result.put("total", list.size() + nullCount);
        result.put("success", resultMap.get("successIn"));
        result.put("nullFail",nullCount);
        result.put("outFail", resultMap.get("failureIn"));
        result.put("failure", nullCount + resultMap.get("failureIn"));
        return result;
    }

    /**
     * 将Excel协议数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private Map<String, Object> wrapJV(Row row) {
        final Map<String, Object> result = newHashMap();
        final JVStoreEntity entity = new JVStoreEntity();
        final List<String> list = new ArrayList<>();
        String s = "";
        for (int i = 0;i < 6; i++){
            s = getCellData(row, i);
            if(!Strings.isNullOrEmpty(s)){
                list.add(s);
            }else {
                result.put(STATUS, STATUS_NULL);
                return result;
            }
        }

        //jvcode
        entity.setJvcode(list.get(0));
        //storecode
        entity.setStoreCode(list.get(1));
        //storechinese
        entity.setStoreChinese(list.get(2));
        //storetax
        entity.setStoreTax(list.get(3));
        //jvcode
        entity.setJvcodeName(list.get(4));
        //taxpayercode
        entity.setTaxpayerCode(list.get(5));

        result.put(STATUS, STATUS_NORMAL);
        result.put("jvStore", entity);
        return result;
    }

    /**
     * 获取excel单元格数据
     * @param row 行
     * @param cellNum 列号
     * @return 单元格数据
     */
    protected static String getCellData(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        if (cell == null) {
            return EMPTY;
        }
        int type = cell.getCellType();
        String returnValue = null;
        switch (type) {
            case Cell.CELL_TYPE_STRING:
                returnValue = trimAllWhitespace(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                returnValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                returnValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                returnValue = EMPTY;
                break;
            default:
                LOGGER.error("Excel读取错误!");
                break;
        }
        return returnValue;
    }

}
