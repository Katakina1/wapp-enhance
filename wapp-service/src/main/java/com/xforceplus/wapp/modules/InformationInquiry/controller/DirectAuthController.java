package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ConfirmInvoiceEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.ScanConfirmExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.DirectAuthService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
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
public class DirectAuthController extends AbstractController {

    @Autowired
    private DirectAuthService scanConfirmService;

    @SysLog("发票查询列表")
    @RequestMapping("/info/scanConfirm2/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<ComprehensiveInvoiceQueryEntity> list = scanConfirmService.queryList(query);
        int count = scanConfirmService.queryCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("扫描人工确认")
    @RequestMapping("/info/scanConfirm2/submit")
    public R submit(@RequestBody List<ConfirmInvoiceEntity> list) {
        for(ConfirmInvoiceEntity entity : list) {
            entity.setConfirmUserId(getUserId());
        }
        return scanConfirmService.submit(list);
    }

    @SysLog("导出待确认信息")
    @RequestMapping("/export/info/scanConfirm2")
    public void exportExcel(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        List<ComprehensiveInvoiceQueryEntity> list = scanConfirmService.queryList(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("scanConfirmList", list);
        //生成excel
        final ScanConfirmExcel excelView = new ScanConfirmExcel(map, "export/InformationInquiry/directAuthList.xlsx", "scanConfirmList");
        final String excelNameSuffix = String.valueOf(new Date().getTime());
        excelView.write(response, "scanConfirmList" + excelNameSuffix);
    }

    @SysLog("导入已确认信息")
    @RequestMapping("/import/info/scanConfirm2")
    public R importExcel(@RequestParam("file") MultipartFile multipartFile) {
        return scanConfirmService.submitBatch(multipartFile, getUserId());
    }
}

