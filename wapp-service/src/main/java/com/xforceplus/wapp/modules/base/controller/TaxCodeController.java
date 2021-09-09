package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.TaxCodeEntity;
import com.xforceplus.wapp.modules.base.service.TaxCodeService;
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
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@RestController
public class TaxCodeController extends AbstractController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaxCodeController.class);
    @Autowired
    private TaxCodeService taxCodeService;

    @SysLog("查询税码表信息")
    @RequestMapping("base/taxcode/list")
    public R list(@RequestBody TaxCodeEntity entity){
        //获取当前页面
        final Integer page = entity.getPage();
        if(page!=null){
            //分页查询起始值
            entity.setOffset((page - 1) * entity.getLimit());
        }

        List<TaxCodeEntity> entityList = taxCodeService.queryList(entity);
        int total = taxCodeService.queryTotal();
        if(page!=null) {
            PageUtils pageUtil = new PageUtils(entityList, total, entity.getLimit(), page);
            return R.ok().put("page", pageUtil);
        }else{
            return  R.ok().put("optionList", entityList);
        }
    }

    @SysLog("导入税码表")
    @PostMapping(value = "/export/upload_taxcode")
    @SuppressWarnings("unchecked")
    public String importTaxCode(@RequestParam("file") MultipartFile file, @RequestParam("type")String type, HttpServletRequest request, HttpServletResponse response){
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }
        //session保存12个小时，防止导入中途session失效
        HttpSession session =request.getSession();
        session.setMaxInactiveInterval(720*60);

        Workbook wb = null;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException e) {
            e.printStackTrace();
            LOGGER.error("ExcelException:", e);
            return new Gson().toJson(R.error("请导入合适的Excel文件模板！"));
        }
        final Sheet sheet = wb.getSheetAt(0);//得到Excel工作表对象,第1张工作表
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数
        //将excel通过验证的数据添加到数据库
        Map<String, Object> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 20000) {
            if("taxcode".equals(type)) {
                //导入协议
                result = insertTaxCode(sheet,rowCount);
                if(result.get("fail")!=null){
                    return new Gson().toJson(R.error("导入失败,请选择合适的Excel文件！"));
                }
            }
        } else if (rowCount > 20000) {
            return new Gson().toJson(R.error("导入数据超过20000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }
        String total = result.get("total").toString();
        String success = result.get("success").toString();
        String failure = result.get("failure").toString();
        String msg = "共计导入：" + total + "条，" + "成功：" + success + "条, " + "失败:" + failure + "条";
        return new Gson().toJson(R.ok(msg));
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
    private Map insertTaxCode(Sheet sheet, int rowCount) {
        final List<TaxCodeEntity> list = newArrayList();
        final Map<String, Object> result = newHashMap();
        //判断是否为正确模板
        String[] topics = {"税收分类编码","名称","说明"};
        for(int k = 0;k<topics.length;k++){
            if(!topics[k].equals( getCellData(sheet.getRow(0), k))){
                result.put("fail","数据模板错误!");
                return result;
            }
        }
        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final TaxCodeEntity wrapEntity = wrap(row);//得到行中基本信息相关数据
            list.add(wrapEntity);
        }
        //String userCode=getUser().getUsercode();
        //批量保存协议
        //Map<String,Integer> resultMap = service.saveBatchJVStore(list,userCode,response);
        int sum = taxCodeService.insert(list);
        result.put("total",rowCount);
        result.put("success",sum);
        result.put("failure",rowCount - sum);

        return result;
    }

    /**
     * 将Excel协议数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private TaxCodeEntity wrap(Row row) {
        final TaxCodeEntity entity = new TaxCodeEntity();

        //taxSortcode
        entity.setTaxSortcode(getCellData(row, 0));
        //name
        entity.setTaxName(getCellData(row, 1));
        //note
        entity.setNote(getCellData(row, 2));

        return entity;
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
