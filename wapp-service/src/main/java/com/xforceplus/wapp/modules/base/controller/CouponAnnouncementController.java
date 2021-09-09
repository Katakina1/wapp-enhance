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
import com.xforceplus.wapp.modules.base.entity.CouponEntity;
import com.xforceplus.wapp.modules.base.export.CouponExcel;
import com.xforceplus.wapp.modules.base.export.CouponFailureExcel;
import com.xforceplus.wapp.modules.base.export.DebtTemplateExport;
import com.xforceplus.wapp.modules.base.service.CouponAnnouncementService;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class CouponAnnouncementController extends AbstractImportExcelController {

    private static final Logger LOGGER= getLogger(CouponAnnouncementController.class);
    @Autowired
    private CouponAnnouncementService couponAnnouncementService;

    private static final String STATUS = "status";
    //status 为1 cell为空，0为正常
    private static final String STATUS_NORMAL = "0";
    private static final String STATUS_NULL = "1";

    @SysLog("模板下载")
    @AuthIgnore
    @RequestMapping("/export/couponTemplate")
    public void protocolTemplate(HttpServletResponse response) {
        //生成excel
        final DebtTemplateExport excelView = new DebtTemplateExport("export/base/couponTemplate.xlsx");
        excelView.write(response, "couponTemplate");
    }

    @SysLog("Coupon自定义公告列表查询")
    @RequestMapping("couponAnnouncement/list")
    public R announcementInquiryList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<CouponEntity> infoPagedQueryResult = couponAnnouncementService.couponAnnouncementList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("发布自定义公告")
    @RequestMapping("couponAnnouncement/release")
    public R releaseCustom() {
        couponAnnouncementService.releaseCustom();
        return R.ok();
    }

    @SysLog("删除自定义公告")
    @RequestMapping("announcementCoupon/delete")
    public R delete() {
        couponAnnouncementService.deleteDebt();
        return R.ok();
    }

    @SysLog("失败债务数据导出")
    @RequestMapping("export/couponFailure")
    public void exportInvoiceDetailFailure(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        final Map<String, List<CouponEntity>> map = newHashMapWithExpectedSize(1);
        params.put("userCode",getUser().getUsercode());
        map.put("failureCoupontList", couponAnnouncementService.queryDebtFailureList(params));
        //生成excel
        final CouponFailureExcel excelView = new CouponFailureExcel(map, "export/base/failureCoupontList.xlsx", "failureCoupontList");
        final String excelName =String.valueOf(new Date().getTime());
        excelView.write(response, "failureCoupontList" + excelName);
    }

    @SysLog("发布页面债务数据导出")
    @AuthIgnore
    @RequestMapping("export/coupon")
    public void exportDebt(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        final Map<String, List<CouponEntity>> map = newHashMapWithExpectedSize(1);
        PagedQueryResult<CouponEntity> infoPagedQueryResult= couponAnnouncementService.couponAnnouncementList(params);
        map.put("couponList",infoPagedQueryResult.getResults());
        //生成excel
        final CouponExcel excelView = new CouponExcel(map, "export/base/couponList.xlsx", "couponList");
        final String excelName = String.valueOf(new Date().getTime());
        excelView.write(response, "couponList" + excelName);
    }

    @PostMapping(value = "/export/couponAnnouncementUpload", produces = "text/html; charset=utf-8")
    @SuppressWarnings("unchecked")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
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

        final Sheet sheet = wb.getSheetAt(0);//得到MD Excel工作表对象
        final int rowCount = sheet.getLastRowNum();//得到Excel工作表最大行数

        //将excel通过验证的数据添加到数据库
        Map<String, Integer> result = new HashMap<>();
        if (1 <= rowCount && rowCount <= 50000) {
            result = insertCoupon(sheet, rowCount);
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

    /**
     *  读取excel文件债务数据，将数据保存到数据库中
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map insertCoupon(Sheet sheet, int rowCount) {
        final List<CouponEntity> debtList = newArrayList();
        final Map<String, Integer> result = newHashMap();


        Row row;
        for (int i = 1; i <= rowCount; i++) {
            row = sheet.getRow(i);
            final Map<String, Object> wrapResult = wrapCoupon(row);//得到行中基本信息相关数据，及获取状态（是否获取成功）
            String status = (String) wrapResult.get(STATUS);

            //excel行状态如果为空，继续下一个循环
            if (STATUS_NULL.equals(status)) {
                continue;
            }

            //excel行状态正常，获取数据
            if (STATUS_NORMAL.equals(status)) {
                //添加数据到list
                debtList.add((CouponEntity) wrapResult.get("data"));
            }
        }

        //批量保存债务数据
        Integer success = couponAnnouncementService.saveBatchCoupon(debtList,getUser().getUsercode());
        result.put("failure", debtList.size() - success);
        result.put("success", success);
        result.put("total", debtList.size());
        return result;
    }

    /**
     * 将Excel PC数据包装进实体类
     *
     * @param row Excel 行数据
     * @return MD数据
     */
    private Map<String, Object> wrapCoupon(Row row) {
        final Map<String, Object> result = newHashMap();
        final CouponEntity couponEntity = new CouponEntity();

        //如果供应商号或定案日期为空，标记此行状态为空,不读取
        if (StringUtils.isBlank(getCellData(row,0))||StringUtils.isBlank(getCellData(row,3))) {
            result.put(STATUS, STATUS_NULL);
            return result;
        }

        if(StringUtils.isNotBlank(getCellData(row, 0))) {
            couponEntity.setSixD(new BigDecimal(getCellData(row, 0)).toPlainString());
        }
        if(StringUtils.isNotBlank(getCellData(row, 1))) {
            couponEntity.setEightD(new BigDecimal(getCellData(row, 1)).toPlainString());
        }
        if(StringUtils.isNotBlank(getCellData(row, 2))) {
            couponEntity.setVenderName(getCellData(row, 2));
        }
        //获取excel日期单元格的数据(现金房定案日期)
        Cell cell = row.getCell(3);
        if(1==cell.getCellType()){
            String caseDateString =   getCellData(row, 3);
            try {
                couponEntity.setCaseDate(new SimpleDateFormat("yyyy/MM/dd").parse(caseDateString));
            } catch (Exception e){
                LOGGER.error("定案日期格式化异常!",e);
            }
        }else{
            Date date = cell.getDateCellValue();
            couponEntity.setCaseDate(date);
        }

        couponEntity.setStore(getCellData(row, 4));
        couponEntity.setCouponNo(getCellData(row, 5));
        couponEntity.setNineD(getCellData(row, 6));
        couponEntity.setCouponDesc(getCellData(row, 7));
        couponEntity.setTicketDesc(getCellData(row, 8));

        //获取excel日期单元格的数据(开始日期)
        Cell cell9 = row.getCell(9);
        if(1==cell9.getCellType()){
            String caseDateString =   getCellData(row, 9);
            try {
                couponEntity.setStartDate(new SimpleDateFormat("yyyy/MM/dd").parse(caseDateString));
            } catch (Exception e){
                LOGGER.error("定案日期格式化异常!",e);
            }
        }else{
            Date date = cell9.getDateCellValue();
            couponEntity.setStartDate(date);
        }

        //获取excel日期单元格的数据(结束日期)
        Cell cel1End = row.getCell(10);
        if(1==cel1End.getCellType()){
            String caseDateString =   getCellData(row, 10);
            try {
                couponEntity.setEndDate(new SimpleDateFormat("yyyy/MM/dd").parse(caseDateString));
            } catch (Exception e){
                LOGGER.error("定案日期格式化异常!",e);
            }
        }else{
            Date date = cel1End.getDateCellValue();
            couponEntity.setEndDate(date);
        }

        if(StringUtils.isNotBlank(getCellData(row, 11))) {
            couponEntity.setCouponCount(new BigDecimal(getCellData(row, 11)).intValue());
        }
        if(StringUtils.isNotBlank(getCellData(row, 12))) {
            couponEntity.setCaseAmount(new BigDecimal(getCellData(row, 12)));
        }
        couponEntity.setAssumeScale(getCellData(row, 13));
        if(StringUtils.isNotBlank(getCellData(row, 14))) {
            couponEntity.setReceivableAmount(new BigDecimal(getCellData(row, 14)));
        }

        result.put(STATUS, STATUS_NORMAL);
        result.put("data", couponEntity);

        return result;
    }
}

