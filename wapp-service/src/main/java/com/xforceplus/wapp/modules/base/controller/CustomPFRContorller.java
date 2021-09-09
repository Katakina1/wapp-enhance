package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.ConfigConstant;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.CustomPFREntity;
import com.xforceplus.wapp.modules.base.export.PFRListExcel;
import com.xforceplus.wapp.modules.base.export.PFRTemplateExport;
import com.xforceplus.wapp.modules.base.service.CustomPFRService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
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
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.springframework.util.StringUtils.endsWithIgnoreCase;
import static org.springframework.util.StringUtils.trimAllWhitespace;
@RestController
public class CustomPFRContorller extends AbstractController {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomPFRContorller.class);
    @Autowired
    private CustomPFRService customPFRService;

    @SysLog("发布自定义公告")
    @RequestMapping("customPFR/release")
    public R releaseCustom() {
        customPFRService.releaseCustom();
        return R.ok();
    }

    @SysLog("删除自定义公告")
    @RequestMapping("announcementPFR/delete")
    public R delete() {
        customPFRService.deleteDebt();
        return R.ok();
    }

    @SysLog("发布页面数据导出")
    @AuthIgnore
    @RequestMapping("export/PFRSuccessful")
    public void exportDebt(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        final Map<String, List<CustomPFREntity>> map = newHashMapWithExpectedSize(1);
        PagedQueryResult<CustomPFREntity> infoPagedQueryResult=customPFRService.customAnnouncementList(params);
        map.put("PFRSuccessful",infoPagedQueryResult.getResults());
        //生成excel
        final PFRListExcel excelView = new PFRListExcel(map, "export/base/PFRList.xlsx", "PFRSuccessful");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "PFRSuccessful" + excelName);
    }

    @SysLog("失败数据导出")
    @RequestMapping("export/PFRFailureList")
    public void exportInvoiceDetailFailure(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final Map<String, List<CustomPFREntity>> map = newHashMapWithExpectedSize(1);
        params.put("userCode",getUser().getUsercode());
        map.put("PFRFailureList", customPFRService.queryDebtFailureList(params));
        //生成excel
        final PFRListExcel excelView = new PFRListExcel(map, "export/base/PFRFailureList.xlsx", "PFRFailureList");
        final String excelName =String.valueOf(new Date().getTime());
        excelView.write(response, "PFRFailureList" + excelName);
    }

    @SysLog("PFR模板下载")
    @AuthIgnore
    @RequestMapping("/export/PFRTemplate")
    public void protocolTemplate(HttpServletResponse response) {
        //生成excel
        final PFRTemplateExport excelView = new PFRTemplateExport("export/base/PFRTemplate.xlsx");
        excelView.write(response, "PFRTemplate");
    }

    @SysLog("查询PFR")
    @RequestMapping("customPFR/list")
    public R list(@RequestParam Map<String, Object> params){
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<CustomPFREntity> infoPagedQueryResult = customPFRService.customAnnouncementList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("导入PFR")
    @PostMapping(value = "/export/customPFR", produces = "text/html; charset=utf-8")
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
            if("protocol".equals(type)) {
                //导入协议
                result = insertCustomPFR(sheet,rowCount);
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
//        String empty = result.get("empty").toString();
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
    private Map insertCustomPFR(Sheet sheet, int rowCount) {
        final List<CustomPFREntity> list = newArrayList();
        final Map<String, Object> result = newHashMap();
        //判断是否为正确模板
        String[] topics = {"供应商号码","供应商名称","部门号","订单号","商品号","商品描述","订单取消日期",
                "未送齐货金额（含税）A","合同违约金比率（%）B","合同生效时间","订单折扣（%）C","应收违约金（含税）D=A*B/100*(1-C/100)"};
        for(int k = 0;k<topics.length;k++){
            if(!topics[k].equals( getCellData(sheet.getRow(0), k))){
                String name = getCellData(sheet.getRow(0), k);
                result.put("fail","数据模板错误!");
                return result;
            }
        }
        Row row;
        int empty = 0;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final CustomPFREntity wrapEntity = getEntity(row);//得到行中基本信息相关数据
            if(wrapEntity != null){
                list.add(wrapEntity);
            }else {
                empty++;
            }
        }
        String userCode=getUser().getUsercode();
        Map<String,Integer> map = customPFRService.saveBatchDebt(list,userCode);
//        result.put("total",rowCount);
        result.put("success",map.get("suc"));
        result.put("failure",map.get("fail"));
        result.put("total",map.get("suc") + map.get("fail"));
//        result.put("empty" ,empty);
        return result;
    }

    /**
     * 将Excel协议数据包装进实体类
     *
     * @param row Excel 行数据
     * @return 费用类型数据
     */
    private CustomPFREntity getEntity(Row row) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        final CustomPFREntity entity = new CustomPFREntity();
        //供应商号码
        entity.setVenderId(getCellData(row, 0));
        //供应商名称
        entity.setVenderName(getCellData(row, 1));
        //部门号
        entity.setDeptNo(getCellData(row, 2));
        //订单号
        entity.setOrderNo(getCellData(row, 3));
        //商品号
        entity.setGoodsNo(getCellData(row, 4));
        //商品描述
        entity.setGoodsName(getCellData(row, 5));
        try {
            //订单取消日期
            if(StringUtils.isNotBlank(getCellData(row, 6))){
                entity.setOrderCancelDate(sdf.parse(getCellData(row, 6)));
            }else {
                return null;
            }
            //合同生效时间
            if(StringUtils.isNotBlank(getCellData(row, 9))){
                entity.setContarctEffectDate(sdf.parse(getCellData(row, 9)));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        //未送齐货金额（含税）
        entity.setNotFullGoodsAmount(getCellData(row, 7));
        //合同违约金比率
        entity.setContractBreakRate(getCellData(row, 8));
        //订单折扣
        if(StringUtils.isNotBlank(getCellData(row, 10))){
            entity.setOrderDiscount(new BigDecimal(getCellData(row, 10)));
        }
        //应收违约金（含税）
        if(StringUtils.isNotBlank(getCellData(row, 11))) {
            entity.setBreakAmount(new BigDecimal(getCellData(row, 11)));
        }
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
                if(HSSFDateUtil.isCellDateFormatted(cell)){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                    Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                    returnValue =  sdf.format(date).toString();
                }else {
                    returnValue = NumberFormat.getInstance().format(cell.getNumericCellValue()).replace(",","");
                }
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
