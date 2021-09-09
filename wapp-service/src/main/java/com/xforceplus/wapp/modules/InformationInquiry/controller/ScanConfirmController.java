package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.ScanConfirmExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanConfirmService;
import com.xforceplus.wapp.modules.cost.entity.SelectionOptionEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.FpghExcelEntity;
import com.xforceplus.wapp.modules.report.entity.SpthpExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
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
public class ScanConfirmController extends AbstractController {

    @Autowired
    private ScanConfirmService scanConfirmService;

    @SysLog("发票查询列表")
    @RequestMapping("/info/scanConfirm/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> list = scanConfirmService.queryList(query);
        int count = scanConfirmService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("获取JV信息")
    @RequestMapping("/info/scanConfirm/getJV")
    public R getJV(@RequestParam String taxNo) {
        List<SelectionOptionEntity> optionList = scanConfirmService.getJV(taxNo);
        return R.ok().put("optionList", optionList);
    }

    @SysLog("获取供应商信息")
    @RequestMapping("/info/scanConfirm/getVender")
    public R getVender() {
        List<SelectionOptionEntity> optionList = scanConfirmService.getVender();
        return R.ok().put("optionList", optionList);
    }

    @SysLog("扫描人工确认")
    @RequestMapping("/info/scanConfirm/submit")
    public R submit(@RequestBody ConfirmInvoiceEntity entity) {
        entity.setConfirmUserId(getUserId());
        boolean r = scanConfirmService.submit(entity);
        if(r){
            return R.ok();
        }else{
            return R.error();
        }
    }

    @SysLog("导出待确认信息")
    @RequestMapping("/export/info/scanConfirm")
    public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ComprehensiveInvoiceQueryEntity> list = scanConfirmService.queryList(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("scanConfirmList", list);


        try {
            List<SpthpExcelEntity> list2=scanConfirmService.transformExcle(list);
            ExcelUtil.writeExcel(response,list2,"商品退换票导出","sheet1", ExcelTypeEnum.XLSX,SpthpExcelEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        //生成excel
//        final ScanConfirmExcel excelView = new ScanConfirmExcel(map, "export/InformationInquiry/scanConfirmList.xlsx", "scanConfirmList");
//        final String excelNameSuffix = String.valueOf(new Date().getTime());
//        excelView.write(response, "scanConfirmList" + excelNameSuffix);
    }

    @SysLog("导入已确认信息")
    @RequestMapping("/import/info/scanConfirm")
    public R importExcel(@RequestParam("file") MultipartFile multipartFile) {
        return scanConfirmService.submitBatch(multipartFile, getUserId());
    }
}
