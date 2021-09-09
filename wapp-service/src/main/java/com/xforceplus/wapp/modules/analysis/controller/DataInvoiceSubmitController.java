package com.xforceplus.wapp.modules.analysis.controller;


import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.modules.analysis.entity.InvoiceDataExcelEntity;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryExcelEntity;
import com.xforceplus.wapp.utils.excel.ExcelException;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.analysis.export.DataInvoiceSubmitExcel;
import com.xforceplus.wapp.modules.analysis.service.DataInvoiceSubmitService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

/**
 *数据发票提交统计
 */
@RestController
@RequestMapping("/analysis")
public class DataInvoiceSubmitController extends AbstractController{
	
	@Autowired
	private DataInvoiceSubmitService dataInvoiceSubmitService;

	/**
	 * 统计供应商发票提交数量
	 * @param params
	 * @return
	 */
	@RequestMapping("/dataInvoicesSubmitStatistics/count/{type}")
	public R count(@RequestParam Map<String, Object>params,@PathVariable("type") String type) {
		
        //查询列表数据
        Query query = new Query(params);
        
        query.put("userID", getUserId());
        query.put("type", type);
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryCount = dataInvoiceSubmitService.queryInvoiceSubmit(query);
      
        PageUtils pageUtil = new PageUtils(queryCount.getResults(), queryCount.getTotalCount(), query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}

    @RequestMapping("/materialInvoicesSubmitStatistics/count/{type}")
    public R matercount(@RequestParam Map<String, Object>params,@PathVariable("type") String type) {

        //查询列表数据
        Query query = new Query(params);

        query.put("userID", getUserId());
        query.put("type", type);
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryCount = dataInvoiceSubmitService.queryRealInvoiceSubmit(query);

        PageUtils pageUtil = new PageUtils(queryCount.getResults(), queryCount.getTotalCount(), query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
	
	@SysLog("导出数据发票提交统计")
    @RequestMapping("/dataInvoicesSubmitStatistics/export")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit = dataInvoiceSubmitService.queryInvoiceSubmit(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("invoiceSubmit",queryInvoiceSubmit.getResults());
       
        String path;
        String prefix;
        if("1".equals(params.get("type"))) {
        	path = "export/analysis/dataInvoiceSubmit.xlsx";
        	prefix = "数据发票提交统计";
        }else {
        	path = "export/analysis/materialInvoiceSubmit.xlsx";
        	prefix = "实物发票提交统计";
        }
        //生成excel
        final DataInvoiceSubmitExcel excelView = new DataInvoiceSubmitExcel(map, path, "invoiceSubmit");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, prefix + excelName);
    }

    @SysLog("导出数据发票提交统计")
    @RequestMapping("/dataInvoicesSubmitStatistics/exportByEasy")
    public void exportByEasy(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        List<InvoiceDataExcelEntity> list = dataInvoiceSubmitService.queryInvoiceSubmitForExcel(params);
        try {
            ExcelUtil.writeExcel(response,list,"数据发票提交统计","sheet1", ExcelTypeEnum.XLSX,InvoiceDataExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }

    }

    @SysLog("导出数据发票提交统计")
    @RequestMapping("/materialInvoicesSubmitStatistics/export")
    public void materialExport(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit = dataInvoiceSubmitService.queryRealInvoiceSubmit(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("invoiceSubmit",queryInvoiceSubmit.getResults());

        String path;
        String prefix;
        if("1".equals(params.get("type"))) {
            path = "export/analysis/dataInvoiceSubmit.xlsx";
            prefix = "数据发票提交统计";
        }else {
            path = "export/analysis/materialInvoiceSubmit.xlsx";
            prefix = "实物发票提交统计";
        }
        //生成excel
        final DataInvoiceSubmitExcel excelView = new DataInvoiceSubmitExcel(map, path, "invoiceSubmit");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, prefix + excelName);
    }

    @SysLog("导出数据发票提交统计")
    @RequestMapping("/materialInvoicesSubmitStatistics/exportByEasy")
    public void materialexportByEasy(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        List<InvoiceDataExcelEntity> list = dataInvoiceSubmitService.queryRealInvoiceSubmitForExcel(params);
        try {
            ExcelUtil.writeExcel(response,list,"实物发票提交统计","sheet1", ExcelTypeEnum.XLSX,InvoiceDataExcelEntity.class);
        } catch (ExcelException e) {
            e.printStackTrace();
        }

    }
}
