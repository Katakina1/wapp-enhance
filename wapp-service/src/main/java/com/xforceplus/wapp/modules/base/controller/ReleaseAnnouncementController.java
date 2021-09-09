package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.exception.ExcelException;
import com.xforceplus.wapp.common.exception.RRException;
import com.xforceplus.wapp.common.safesoft.AbstractImportExcelController;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.base.entity.AnnouncementEntity;
import com.xforceplus.wapp.modules.base.entity.DebtEntity;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.export.DebtExcel;
import com.xforceplus.wapp.modules.base.export.DebtFailureExcel;
import com.xforceplus.wapp.modules.base.export.DebtTemplateExport;
import com.xforceplus.wapp.modules.base.service.ReleaseAnnouncementService;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class ReleaseAnnouncementController extends AbstractImportExcelController {

    private static final Logger LOGGER= getLogger(ReleaseAnnouncementController.class);
    @Autowired
    private ReleaseAnnouncementService releaseAnnouncementService;

    private static final String STATUS = "status";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    @RequestMapping("/base/releaseAnnouncement/searchOrglevel")
    @SysLog("供应商类型下拉列表")
    public R searchOrglevelList() {
        List<UserEntity> userList = releaseAnnouncementService.levelList();
        return R.ok().put("userList", userList);
    }

    @PostMapping("/base/releaseAnnouncement/announcement/insert")
    @SysLog("发布公告")
    public R releaseAnnouncement(
            @RequestParam(value = "attachment",required = false) MultipartFile attachment,  @RequestParam(value = "venderFile",required = false) MultipartFile venderFile,
            @RequestParam("orgLevel") String orgLevel,    @RequestParam("announcementInfo") String announcementInfo,
            @RequestParam("announcementTitle") String announcementTitle,@RequestParam("userType") String userType,
            @RequestParam("venderId") String venderId,@RequestParam("releaseDate") String releaseDate,
            @RequestParam("header") String header,@RequestParam("footer") String footer,
            @RequestParam(value="trainDate",required = false)String trainDate,@RequestParam("announcementType") String announcementType) {

        AnnouncementEntity entity = new AnnouncementEntity();
        entity.setAnnouncementType(announcementType);
        entity.setAttchment(attachment);
        entity.setVenderFile(venderFile);
        entity.setOrgLevel(orgLevel);
        entity.setAnnouncementInfo(announcementInfo);
        entity.setAnnouncementTitle(announcementTitle);
        entity.setUserType(userType);
        entity.setVenderId(venderId);
        entity.setHeader(header);
        entity.setFooter(footer);
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if(StringUtils.isNotBlank(trainDate)){
                entity.setTrainDate(dateFormat.parse(trainDate));
            }
            entity.setReleasetime(dateFormat.parse(releaseDate));
        } catch (Exception e){
            LOGGER.error("日期格式异常!",e);
            return R.error("日期格式化异常!");
        }
        String message=releaseAnnouncementService.releaseAnnouncement(entity);
        if("上传公告附件异常".equals(message)){
            return R.error("上传公告附件异常");
        }
        return R.ok().put("message", message);
    }

    @SysLog("模板下载")
    @AuthIgnore
    @RequestMapping("/export/debtTemplate")
    public void protocolTemplate(HttpServletResponse response) {
        //生成excel
        final DebtTemplateExport excelView = new DebtTemplateExport("export/base/debtTemplate.xlsx");
        excelView.write(response, "debtTemplate");
    }

    @SysLog("自定义公告列表查询")
    @RequestMapping("customAnnouncement/list")
    public R announcementInquiryList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<DebtEntity> infoPagedQueryResult = releaseAnnouncementService.customAnnouncementList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("债务数据查询")
    @RequestMapping("announcement/debtList")
    public R announcementdebtList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("venderId", getUser().getUsercode());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<DebtEntity> infoPagedQueryResult = releaseAnnouncementService.debtList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil).put("totalAmount", infoPagedQueryResult.getTotalAmount())
                .put("mdTotalAmount", infoPagedQueryResult.getMdTotalAmount()).put("pcTotalAmount",infoPagedQueryResult.getPcTotalAmount());
    }

    @SysLog("自定义模板保存")
    @RequestMapping("customAnnoucement/saveTemplate.ignoreHtmlFilter")
    public R saveTemplate(@RequestBody AnnouncementEntity entity) {
        releaseAnnouncementService.saveTemplate(entity);
        return R.ok();
    }

    @SysLog("查询自定义模板内容")
    @RequestMapping("customAnnoucement/queryTemplate")
    public R queryTemplate(@RequestBody AnnouncementEntity entity) {
        AnnouncementEntity announcementInfo = releaseAnnouncementService.queryTemplate(entity);
        return R.ok().put("announcementInfo",announcementInfo.getAnnouncementInfo()==null?"":announcementInfo.getAnnouncementInfo());
    }

    @SysLog("发布自定义公告")
    @RequestMapping("customAnnouncement/release")
    public R releaseCustom() {
        releaseAnnouncementService.releaseCustom();
        return R.ok();
    }

    @SysLog("删除自定义公告")
    @RequestMapping("announcementDebt/delete")
    public R delete() {
        releaseAnnouncementService.deleteDebt();
        return R.ok();
    }

    @SysLog("失败债务数据导出")
    @RequestMapping("export/debtFailure")
    public void exportInvoiceDetailFailure(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final Map<String, List<DebtEntity>> map = newHashMapWithExpectedSize(1);
        params.put("userCode",getUser().getUsercode());
        map.put("failureDebtList", releaseAnnouncementService.queryDebtFailureList(params));
        //生成excel
        final DebtFailureExcel excelView = new DebtFailureExcel(map, "export/base/failureDebtList.xlsx", "failureDebtList");
        final String excelName =String.valueOf(new Date().getTime());
        excelView.write(response, "failureDebtList" + excelName);
    }

    @SysLog("供应商债务数据导出")
    @AuthIgnore
    @RequestMapping("export/venderDebt")
    public void exportVenderDebt(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userCode",getUser().getUsercode());

        final Map<String, List<DebtEntity>> map = newHashMapWithExpectedSize(1);
        map.put("debtList", releaseAnnouncementService.getVenderDebtList(params));
        //生成excel
        final DebtExcel excelView = new DebtExcel(map, "export/base/debtList.xlsx", "debtList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "debtList" + excelName);
    }

    @SysLog("发布页面债务数据导出")
    @AuthIgnore
    @RequestMapping("export/debt")
    public void exportDebt(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        final Map<String, List<DebtEntity>> map = newHashMapWithExpectedSize(1);
        PagedQueryResult<DebtEntity> infoPagedQueryResult=releaseAnnouncementService.customAnnouncementList(params);
        map.put("debtList",infoPagedQueryResult.getResults());
        //生成excel
        final DebtExcel excelView = new DebtExcel(map, "export/base/debtList.xlsx", "debtList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "debtList" + excelName);
    }

    @SysLog("查询模板是否存在")
    @RequestMapping("customAnnouncement/templateIsExist")
    public R templateIsExist() {
        return R.ok().put("count",  releaseAnnouncementService.templateIsExist());
    }

    @PostMapping(value = "/export/customAnnouncementUpload", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
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
        }

        final Sheet sheetMD = wb.getSheetAt(0);//得到MD Excel工作表对象
        final int rowCountMD = sheetMD.getLastRowNum();//得到Excel工作表最大行数

        final Sheet sheetPC = wb.getSheetAt(1);//得到PC Excel工作表对象
        final int rowCountPC = sheetPC.getLastRowNum();//得到Excel工作表最大行数
        //将excel通过验证的数据添加到数据库
        Map<String, Integer> result = new HashMap<>();
        if (1 <= rowCountMD && rowCountMD <= 50000) {
            result = insertDebt(sheetMD, rowCountMD,sheetPC,rowCountPC);
        } else if (rowCountMD > 50000) {
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

    /**
     *  读取excel文件债务数据，将数据保存到数据库中
     * @param sheetMD MD数据
     * @param rowCountMD MD行数
     * @param sheetPC PC数据
     * @param rowCountPC PC行数
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map insertDebt(Sheet sheetMD, int rowCountMD,Sheet sheetPC,int rowCountPC) {
        final List<DebtEntity> debtList = newArrayList();
        final Map<String, Integer> result = newHashMap();


        Row row;
        //获取MD表格数据
        for (int i = 1; i <= rowCountMD; i++) {
            row = sheetMD.getRow(i);
            final Map<String, Object> wrapResult = wrapMD(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加数据到list
                debtList.add((DebtEntity) wrapResult.get("debtMD"));
            }
        }
        //获取PC数据表格
        for (int i = 1; i <= rowCountPC; i++) {
            row = sheetPC.getRow(i);
            final Map<String, Object> wrapResult = wrapPC(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加数据到list
                debtList.add((DebtEntity) wrapResult.get("debtPC"));
            }
        }

        //批量保存债务数据
        Integer success = releaseAnnouncementService.saveBatchDebt(debtList,getUser().getUsercode());
        result.put("failure", debtList.size() - success);
        result.put("success", success);
        result.put("total", debtList.size());
        return result;
    }

    /**
     * 将Excel MD数据包装进实体类
     *
     * @param row Excel 行数据
     * @return MD数据
     */
    private Map<String, Object> wrapMD(Row row) {
        final Map<String, Object> result = newHashMap();
        final DebtEntity debtEntity = new DebtEntity();

        //如果供应商号或商品号为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row,0))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        if(!Strings.isNullOrEmpty(getCellData(row, 0))) {
            debtEntity.setVenderId(new BigDecimal(getCellData(row, 0)).stripTrailingZeros().toPlainString());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 1))) {
            debtEntity.setDeptNo(new BigDecimal(getCellData(row, 1)).stripTrailingZeros().toPlainString());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 2))) {
            debtEntity.setGoodsNo(new BigDecimal(getCellData(row, 2)).stripTrailingZeros().toPlainString());
        }
        debtEntity.setGoodsName(getCellData(row, 3));
        //获取excel日期单元格的数据(商品下调日期)
        Cell cell = row.getCell(4);
        if(cell!=null && 1==cell.getCellType()){
            String dateString =   getCellData(row, 4);
            try {
                if(dateString!=null){
                    debtEntity.setGoodsReduceDate(new SimpleDateFormat("yyyy/MM/dd").parse(dateString));
                }else{
                    debtEntity.setGoodsReduceDate(null);
                }
            } catch (Exception e){
                LOGGER.error("日期格式化异常!",e);
            }
        }else{
            if( cell !=null && cell.getDateCellValue()!=null){
                Date date = cell.getDateCellValue();
                if(date!=null){
                    debtEntity.setGoodsReduceDate(date);
                }else{
                    debtEntity.setGoodsReduceDate(null);
                }
            }else{
                debtEntity.setGoodsReduceDate(null);
            }
        }

        if(!Strings.isNullOrEmpty(getCellData(row, 5))) {
            debtEntity.setPriceReduceBefore(new BigDecimal(getCellData(row, 5)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 6))) {
            debtEntity.setPriceReduceAfter(new BigDecimal(getCellData(row, 6)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 7))) {
            debtEntity.setReduceStockNum(new BigDecimal(getCellData(row, 7)).intValue());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 8))) {
            debtEntity.setTaxRate(new BigDecimal(getCellData(row, 8)));
        }
        debtEntity.setProtocolNo(getCellData(row,9));
        if(!Strings.isNullOrEmpty(getCellData(row, 10))) {
            debtEntity.setProtocolAmount(new BigDecimal(getCellData(row, 10)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 11))) {
            debtEntity.setCompensationAmount(new BigDecimal(getCellData(row, 11)));
        }
        debtEntity.setDebtType("3");

        result.put(STATUS, STATUS_NORMAL);
        result.put("debtMD", debtEntity);

        return result;
    }

    /**
     * 将Excel PC数据包装进实体类
     *
     * @param row Excel 行数据
     * @return MD数据
     */
    private Map<String, Object> wrapPC(Row row) {
        final Map<String, Object> result = newHashMap();
        final DebtEntity debtEntity = new DebtEntity();

        //如果供应商号或订单号为空，标记此行状态为空,不读取
        if (Strings.isNullOrEmpty(getCellData(row,0))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        if(!Strings.isNullOrEmpty(getCellData(row, 0))) {
            debtEntity.setVenderId(new BigDecimal(getCellData(row, 0)).stripTrailingZeros().toPlainString());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 1))) {
            debtEntity.setDeptNo(new BigDecimal(getCellData(row, 1)).stripTrailingZeros().toPlainString());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 2))) {
            debtEntity.setOrderNo(new BigDecimal(getCellData(row, 2)).stripTrailingZeros().toPlainString());
        }
        debtEntity.setStore(getCellData(row, 3));
        //获取excel日期单元格的数据(收货日期)
        Cell cell = row.getCell(4);
        if(cell!=null && 1==cell.getCellType()){
            String dateString =   getCellData(row, 4);
            try {
                if(dateString!=null){
                    debtEntity.setReceiveDate(new SimpleDateFormat("yyyy/MM/dd").parse(dateString));
                }else{
                    debtEntity.setReceiveDate(null);
                }
            } catch (Exception e){
                LOGGER.error("日期格式化异常!",e);
            }
        }else{
            if( cell !=null && cell.getDateCellValue()!=null){
                Date date = cell.getDateCellValue();
                if(date!=null){
                    debtEntity.setReceiveDate(date);
                }else{
                    debtEntity.setReceiveDate(null);
                }
            }else{
                debtEntity.setReceiveDate(null);
            }
        }

        //获取excel日期单元格的数据(商品价格下调日期)
        Cell cell2 = row.getCell(5);
        if(cell2!=null && 1==cell2.getCellType()){
            String dateString =   getCellData(row, 5);
            try {
                if(dateString!=null){
                    debtEntity.setGoodsReduceDate(new SimpleDateFormat("yyyy/MM/dd").parse(dateString));
                }else{
                    debtEntity.setGoodsReduceDate(null);
                }
            } catch (Exception e){
                LOGGER.error("日期格式化异常!",e);
            }
        }else{
            if( cell2 !=null && cell2.getDateCellValue()!=null){
                Date date = cell2.getDateCellValue();
                if(date!=null){
                    debtEntity.setGoodsReduceDate(date);
                }else{
                    debtEntity.setGoodsReduceDate(null);
                }
            }else{
                debtEntity.setGoodsReduceDate(null);
            }
        }

        if(!Strings.isNullOrEmpty(getCellData(row, 6))) {
            debtEntity.setGoodsNo(new BigDecimal(getCellData(row, 6)).stripTrailingZeros().toPlainString());
        }
        debtEntity.setGoodsName(getCellData(row, 7));
        if(!Strings.isNullOrEmpty(getCellData(row, 8))) {
            debtEntity.setReceiveNum(new BigDecimal(getCellData(row, 8)).intValue());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 9))) {
            debtEntity.setPackageNum(new BigDecimal(getCellData(row, 9)).intValue());
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 10))) {
            debtEntity.setGoodsActualPrice(new BigDecimal(getCellData(row, 10)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 11))) {
            debtEntity.setPriceReduceAfter(new BigDecimal(getCellData(row, 11)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 12))) {
            debtEntity.setOrderDiscount(new BigDecimal(getCellData(row, 12)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 13))) {
            debtEntity.setTaxRate(new BigDecimal(getCellData(row, 13)));
        }
        if(!Strings.isNullOrEmpty(getCellData(row, 14))) {
            debtEntity.setPriceDifference(new BigDecimal(getCellData(row, 14)));
        }
        debtEntity.setDebtType("2");
        result.put(STATUS, STATUS_NORMAL);
        result.put("debtPC", debtEntity);

        return result;
    }
}
