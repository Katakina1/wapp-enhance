package com.xforceplus.wapp.modules.cost.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.cost.entity.CostEntity;
import com.xforceplus.wapp.modules.cost.importTemplate.CostTypeTemplate;
import com.xforceplus.wapp.modules.cost.service.CostTypeService;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("costType")
public class CostTypeController extends AbstractImportExcelController {

    private static final Logger LOGGER = getLogger(CostTypeController.class);

    private static final String STATUS = "status";
    private static final String DATA = "data";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";
    @Autowired
    private  CostTypeService costTypeService;

    @SysLog("费用类型列表查询")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {

        //查询列表数据
        Query query = new Query(params);
        List<CostEntity> list = costTypeService.queryList(query);
        Integer count = costTypeService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("费用类型保存")
    @RequestMapping("/save")
    public R save(@RequestBody CostEntity costEntity) {

        int count = costTypeService.queryCostType(costEntity);
        if(count > 0) {
            return R.error("费用编号已存在，请重新输入!");
        }else {
            costTypeService.save(costEntity);
        }
        return R.ok();
    }

    @SysLog("费用类型更新")
    @RequestMapping("/update")
    public R update(@RequestBody CostEntity costEntity) {

        int count = costTypeService.queryCostType(costEntity);
        if(count > 0) {
            return R.error("费用编号已存在，请重新输入!");
        }else {
            costTypeService.update(costEntity);
        }

        return R.ok();
    }

    @SysLog("费用类型删除")
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {

        for (Long id :
                ids) {
            costTypeService.delete(id);
        }
        return R.ok();
    }

    @SysLog("导入费用类型")
    @PostMapping(value = "/cost/upload", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String importCostType(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RRException("上传文件不能为空!");
        }

        //创建工作簿对象
        Workbook wb;
        try {
            wb = getWorkBook(file);
        } catch (ExcelException excelE) {
            LOGGER.error("ExcelException:", excelE);
            return new Gson().toJson(R.error(excelE.getMessage()));
        }

        final int sheetStart = 0;
        final Sheet sheet = wb.getSheetAt(sheetStart);//得到Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Integer> result;
        if (1 < rowCount && rowCount <= 50000) {
            result = insertCostType(sheet, rowCount);
        } else if (rowCount > 50000) {
            return new Gson().toJson(R.error("导入数据超过50000条，请修改模板！"));
        } else {
            return new Gson().toJson(R.error("Excel数据为空!"));
        }

        //获取导入成功和失败的数量
        Integer total = result.get("total");
        Integer success = result.get("success");
        Integer failure = result.get("failure");

        String message = "共计导入:" + total + "条<br>" + "成功:" + success + "条," + "失败:" + failure + "条";

        return new Gson().toJson(R.ok(message));
    }

    @SysLog("下载费用类型模板")
    @AuthIgnore
    @GetMapping(value = "/downloadTemplate")
    public void downloadEnterpriseBlackTemplate(HttpServletResponse response) {
        LOGGER.info("导出费用类型模板");

        //生成excel
        final CostTypeTemplate excelView = new CostTypeTemplate();
        excelView.write(response, "costtype");
    }

    /**
     * 读取excel文件获取数据，将数据保存到数据库中
     *
     * @param sheet    Excel工作表对象
     * @param rowCount Excel工作表最大行数
     */
    @SuppressWarnings("unchecked")
    private Map insertCostType(Sheet sheet, int rowCount) {
        final List<CostEntity> costTypeList = newArrayList();
        final Map<String, Integer> result = newHashMap();


        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapCostType(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                CostEntity costEntity = (CostEntity) wrapResult.get(DATA);
                costTypeList.add(costEntity);
            }
        }

        //批量保存费用类型
        Integer success = costTypeService.saveBatch( costTypeList);
        result.put("failure", rowCount - success);
        result.put("success", success);
        result.put("total", costTypeList.size());

        return result;
    }

    /**
     * 将Excel数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private Map<String, Object> wrapCostType(Row row) {
        final Map<String, Object> result = newHashMap();
        final CostEntity costEntity = new CostEntity();

        //如果供应商号或费用编码或费用名称为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row,0)) ||
                Strings.isNullOrEmpty(getCellData(row, 1)) ||
                Strings.isNullOrEmpty(getCellData(row, 2))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        //将Excel供应商号数据包装进实体类
        costEntity.setVenderId(getCellData(row,0));
        //将Excel合同数据包装进实体类
        if("合同".equals(getCellData(row, 1))){
            costEntity.setIsContract("1");
        }else if("非合同".equals(getCellData(row, 1))){
            costEntity.setIsContract("0");
        }
        //将Excel费用编码数据包装进实体类
        costEntity.setCostType(getCellData(row, 2));
        //将Excel费用名称数据包装进实体类
        costEntity.setCostTypeName(getCellData(row, 3));

        result.put(STATUS, STATUS_NORMAL);
        result.put(DATA, costEntity);

        return result;
    }
}
