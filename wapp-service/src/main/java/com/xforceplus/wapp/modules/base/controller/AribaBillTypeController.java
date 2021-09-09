package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.AribaBillTypeEntity;
import com.xforceplus.wapp.modules.base.entity.BillTypeEntity;
import com.xforceplus.wapp.modules.base.entity.UserBilltypeEntity;
import com.xforceplus.wapp.modules.base.export.JVStoreTemplateExport;
import com.xforceplus.wapp.modules.base.service.AribaBillTypeService;
import com.xforceplus.wapp.modules.base.service.BillTypeService;
import com.xforceplus.wapp.modules.base.service.UserBilltypeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

/**
 * Created by jingsong.mao on 2018/08/10.
 * 业务类型管理控制层
 */
@RestController
public class AribaBillTypeController extends AbstractController {

    @Autowired
    private AribaBillTypeService Service;
    @Autowired
    private UserBilltypeService userBilltypeService;


    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/aribabilltype/list")
    public R list(AribaBillTypeEntity Entity) {

        //获取当前页面
        final Integer page = Entity.getPage();

        //分页查询起始值
        Entity.setOffset((page - 1) * Entity.getLimit());

        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<AribaBillTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);

        int total = Service.queryTotal(getCurrentUserSchemaLabel(), Entity);

        PageUtils pageUtil = new PageUtils(EntityList, total, Entity.getLimit(), page);

        return R.ok().put("page", pageUtil);
    }

    /**
     * 查询用户列表
     */
    @SysLog("业务类型列表查询")
    @RequestMapping("/base/aribabilltype/listNoPage")
    public R listNoPage(AribaBillTypeEntity Entity) {



        //分库
        Entity.setSchemaLabel(getCurrentUserSchemaLabel());

        List<AribaBillTypeEntity> EntityList = Service.queryList(getCurrentUserSchemaLabel(), Entity);



        return R.ok().put("page", EntityList);
    }

    /**
     * 根据业务类型id获取业务类型信息
     */
    @SysLog("业务类型信息查询")
    @RequestMapping("/base/aribabilltype/getBillTypeInfoById/{id}")
    public R selectSingle(@PathVariable Long id) {

        return R.ok().put("Info", Service.queryObject(getCurrentUserSchemaLabel(), id));
    }

    /**
     * 更新保存信息
     */
    @SysLog("业务类型信息保存")
    @RequestMapping("/base/aribabilltype/saveBillType")
    public R save(@RequestBody AribaBillTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getServiceName(), entity.getMccCode(),entity.getGlAccount(),null);
        if(num > 0)
        {
        	return R.error("保存失败：MCC编码和GLAccount唯一，不可重复");
        }

        Service.save(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }

    /**
     * 更新业务类型信息
     */
    @SysLog("业务类型信息更新")
    @RequestMapping("/base/aribabilltype/updateBillType")
    public R update(@RequestBody AribaBillTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());
        
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getServiceName(), entity.getMccCode(),entity.getGlAccount(),entity.getId());
        if(num > 0)
        {
        	return R.error("保存失败：MCC编码和GLAccount唯一，不可重复");
        }

        Service.update(getCurrentUserSchemaLabel(), entity);

        return R.ok();
    }
    
    /**
     * 删除业务类型信息(批量)
     */
    @SysLog("业务类型信息删除")
    @RequestMapping("/base/aribabilltype/deleteBillType")
    public R delete(@RequestBody AribaBillTypeEntity entity) {

        //分库
        entity.setSchemaLabel(getCurrentUserSchemaLabel());

       Service.deleteBatch(getCurrentUserSchemaLabel(), entity.getIds());

        return R.ok();
    }

    @SysLog("模板下载")
    @AuthIgnore
    @RequestMapping("/export/aribaBillTypeTemplate")
    public void aribaBillTypeTemplate(HttpServletResponse response) {
        //生成excel
        final JVStoreTemplateExport excelView = new JVStoreTemplateExport("export/base/aribaBillTypeTemplate.xlsx");
        excelView.write(response, "aribaBillTypeTemplate");
    }

    @PostMapping(value = "/export/upload_aribaBillType", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
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
        } catch (com.xforceplus.wapp.common.exception.ExcelException excelE) {
            return new Gson().toJson(R.error(excelE.getMessage()));
        }catch (Exception e){
            return new Gson().toJson(R.error("请导入合适的Excel文件模板！"));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Object> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 1000) {
            //导入协议
            result = insertAribaBillType(sheet, rowCount,response);
            if(result.get("fail")!=null){
                return new Gson().toJson(R.error("导入失败,请选择合适的Excel文件！"));
            }

        } else if (rowCount > 1000) {
            return new Gson().toJson(R.error("导入数据超过1000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }
        //获取导入成功和失败的数量
        String total = result.get("total").toString();
        String success = result.get("success").toString();
        String nullFail = result.get("nullFail").toString();
        String outFail = result.get("outFail").toString();
        String errorFail = result.get("errorFail").toString();
        String message = "共计导入：" + total + "条，" + "新增：" + success + "条<br>" + "更新:" + outFail + "条<br>" + "其中：数据含有空值" + nullFail + "条<br>所属大类填写错误" + errorFail + "条<br>";
        return new Gson().toJson(R.ok(message));
    }
    /**
     * 获取工作簿对象
     * @param file 导入的文件
     * @return 工作簿对象
     * @throws com.xforceplus.wapp.common.exception.ExcelException 异常
     */
    protected static Workbook getWorkBook(MultipartFile file) throws com.xforceplus.wapp.common.exception.ExcelException {
        final Workbook workbook;
        try {
            if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLS)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (endsWithIgnoreCase(file.getOriginalFilename(), ConfigConstant.EXCEL_XLSX)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                throw new com.xforceplus.wapp.common.exception.ExcelException(com.xforceplus.wapp.common.exception.ExcelException.READ_ERROR, "Excel读取错误，请导入.xls或.xlsx文件!");
            }
            return workbook;
        } catch (IOException e) {

            throw new com.xforceplus.wapp.common.exception.ExcelException(com.xforceplus.wapp.common.exception.ExcelException.READ_ERROR, "Excel读取错误!");
        }
    }
    /**
     * 读取excel文件获取协议数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertAribaBillType(Sheet sheet, int rowCount, HttpServletResponse response) {
        final List<AribaBillTypeEntity> list = newArrayList();
        final List<AribaBillTypeEntity> updateList = newArrayList();
        final Map<String, Object> result = newHashMap();
        int nullCount = 0;
        int errorCount = 0;
        //判断是否为正确模板
        String[] topics = {"MCC编码","GLAccount","业务名称","所属大类(资产类、费用类)"};
        for(int k = 0;k<topics.length;k++){
            if(!topics[k].equals( getCellData(sheet.getRow(0), k))){
                result.put("fail","数据模板错误!");
                return result;
            }
        }
        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapBillType(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
//            if(wrapResult.get("fail")!=null){
//                return wrapResult;
//            }
            String status = (String) wrapResult.get("status");
            //excel行状态正常，获取数据
            if ("0".equals(status)) {
                //添加信息到list
                AribaBillTypeEntity entity = (AribaBillTypeEntity) wrapResult.get("aribaBillType");
                entity.setRow(i+1);
                list.add(entity);
            }
            if("2".equals(status)){
                AribaBillTypeEntity entity = (AribaBillTypeEntity) wrapResult.get("aribaBillType");
                updateList.add(entity);
            }
            if("3".equals(status)){
                errorCount++;
            }
            if("1".equals(status)){
                nullCount++;
            }
        }

        for (AribaBillTypeEntity en:list) {
            Service.save(getCurrentUserSchemaLabel(),en);
        }
        for (AribaBillTypeEntity en:updateList) {
            Service.updateImport(getCurrentUserSchemaLabel(),en);
        }
        result.put("total", list.size() +updateList.size()+ nullCount+errorCount);
        result.put("success", list.size());
        result.put("nullFail",nullCount);
        result.put("outFail", updateList.size());
        result.put("errorFail", errorCount);
        return result;
    }
    /**
     * 将Excel协议数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private Map<String, Object> wrapBillType(Row row) {
        final Map<String, Object> result = newHashMap();
        final AribaBillTypeEntity entity = new AribaBillTypeEntity();
        final List<String> list = new ArrayList<>();
        String s = "";
        for (int i = 0;i < 4; i++){
            s = getCellData(row, i);
            if(!Strings.isNullOrEmpty(s)){
                list.add(s);
            }else {
                result.put("status", "1");
                return result;
            }
        }

        //jvcode
        entity.setMccCode(list.get(0));
        //storecode
        entity.setGlAccount(list.get(1));
        //storechinese
        entity.setServiceName(list.get(2));
        //storetax
        if(list.get(3).equals("资产类")){
            entity.setServiceType("0");
        }else if(list.get(3).equals("费用类")){
            entity.setServiceType("1");
        }else {
            result.put("status", "3");
            return result;
        }
        int num = Service.queryByNameAndCode(getCurrentUserSchemaLabel(), entity.getServiceName(), entity.getMccCode(),entity.getGlAccount(),entity.getId());
        if(num > 0)
        {
            result.put("status", "2");
        }else{
            result.put("status", "0");
        }
        result.put("aribaBillType", entity);
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

                break;
        }
        return returnValue;
    }
}
