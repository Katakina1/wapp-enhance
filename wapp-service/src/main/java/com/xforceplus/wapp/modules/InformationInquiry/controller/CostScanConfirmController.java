package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.ScanConfirmExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.CostScanConfirmService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanConfirmService;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceCostQueryExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.InvoiceQueryExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

@RestController
public class CostScanConfirmController extends AbstractController {

    @Autowired
    private CostScanConfirmService costScanConfirmService;

    @SysLog("发票查询列表")
    @RequestMapping("/info/costScanConfirm/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> list = costScanConfirmService.queryList(query);
        int count = costScanConfirmService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("获取JV信息")
    @RequestMapping("/info/costScanConfirm/getJV")
    public R getJV(@RequestParam String taxNo) {
        List<SelectionOptionEntity> optionList = costScanConfirmService.getJV(taxNo);
        return R.ok().put("optionList", optionList);
    }

    @SysLog("获取供应商信息")
    @RequestMapping("/info/costScanConfirm/getVender")
    public R getVender() {
        List<SelectionOptionEntity> optionList = costScanConfirmService.getVender();
        return R.ok().put("optionList", optionList);
    }

    @SysLog("扫描人工确认")
    @RequestMapping("/info/costScanConfirm/submit")
    public R submit(@RequestBody ConfirmInvoiceEntity entity) {
        entity.setConfirmUserId(getUserId());
        boolean r = costScanConfirmService.submit(entity);
        if(r){
            return R.ok();
        }else{
            return R.error();
        }
    }

    @SysLog("导出待确认信息")
    @RequestMapping("/export/info/costScanConfirm")
    public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ComprehensiveInvoiceCostQueryExcelEntity> list = costScanConfirmService.queryExcelList(params);
        try {
            ExcelUtil.writeExcel(response,list,"费用退换票","sheet1", ExcelTypeEnum.XLSX,ComprehensiveInvoiceCostQueryExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("scanConfirmList", list);
//        //生成excel
//        final ScanConfirmExcel excelView = new ScanConfirmExcel(map, "export/InformationInquiry/scanConfirmList.xlsx", "scanConfirmList");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "scanConfirmList" + excelNameSuffix);
    }

    @SysLog("导入已确认信息")
    @RequestMapping("/import/info/costScanConfirm")
    public R importExcel(@RequestParam("file") MultipartFile multipartFile) {
        return costScanConfirmService.submitBatch(multipartFile, getUserId());
    }
}
