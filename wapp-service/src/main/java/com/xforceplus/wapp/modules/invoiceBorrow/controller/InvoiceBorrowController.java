package com.xforceplus.wapp.modules.invoiceBorrow.controller;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SignForQueryExcelEntity;
import com.xforceplus.wapp.modules.api.annotation.AuthIgnore;
import com.xforceplus.wapp.modules.invoiceBorrow.entity.BorrowEntity;
import com.xforceplus.wapp.modules.invoiceBorrow.export.BorrowInvoiceExcel;
import com.xforceplus.wapp.modules.invoiceBorrow.export.BorrowRecordExcel;
import com.xforceplus.wapp.modules.invoiceBorrow.service.InvoiceBorrowService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.report.entity.FpghExcelEntity;
import com.xforceplus.wapp.modules.report.entity.FpjyExcelEntity;
import com.xforceplus.wapp.modules.report.entity.JyjlcxExcelEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class InvoiceBorrowController extends AbstractController {

    private final static Logger LOGGER = getLogger(InvoiceBorrowController.class);

    private final InvoiceBorrowService invoiceBorrowService;

    @Autowired
    public InvoiceBorrowController(InvoiceBorrowService invoiceBorrowService) {
        this.invoiceBorrowService = invoiceBorrowService;
    }

    @SysLog("发票借阅列表查询")
    @RequestMapping("/invoice/borrow/list")
    public R queryInvoiceBorrowList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<ComprehensiveInvoiceQueryEntity> infoPagedQueryResult = invoiceBorrowService.queryInvoiceBorrowList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

    @SysLog("借阅记录列表查询")
    @RequestMapping("/invoice/borrow/record/list")
    public R queryInvoiceBorrowRecordList(@RequestParam Map<String, Object> params) {
        LOGGER.info("查询条件为:{}", params);

        //获取当前用户的userId
        params.put("userId", getUserId());

        //查询列表数据
        final Query query = new Query(params);

        //执行业务层
        final PagedQueryResult<BorrowEntity> infoPagedQueryResult = invoiceBorrowService.queryBorrowRecordList(query);

        //分页
        final PageUtils pageUtil = new PageUtils(infoPagedQueryResult.getResults(), infoPagedQueryResult.getTotalCount(), query.getLimit(), query.getPage(), infoPagedQueryResult.getSummationTotalAmount(), infoPagedQueryResult.getSummationTaxAmount());
        //返回
        return R.ok().put("page", pageUtil);
    }

	@SysLog("借阅保存")
	@RequestMapping("/invoice/borrow/save")
	public R save(@RequestBody BorrowEntity borrowEntity) {
		invoiceBorrowService.save(borrowEntity);
		return R.ok();
	}

    @SysLog("发票借阅导出")
    @AuthIgnore
    @RequestMapping("export/invoiceBorrowExport")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        final Map<String, List<ComprehensiveInvoiceQueryEntity>> map = newHashMapWithExpectedSize(1);
        map.put("borrowInvoiceList", invoiceBorrowService.queryInvoiceBorrowList(params).getResults());





        try {
            List<FpjyExcelEntity> list2=invoiceBorrowService.transformExcle(map.get("borrowInvoiceList"));
            ExcelUtil.writeExcel(response,list2,"发票借阅导出","sheet1", ExcelTypeEnum.XLSX,FpjyExcelEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }




//        //生成excel
//        final BorrowInvoiceExcel excelView = new BorrowInvoiceExcel(map, "export/invoiceBorrow/borrowInvoiceList.xlsx", "borrowInvoiceList");
//        final String excelName = String.valueOf(new Date().getTime());
//        excelView.write(response, "borrowInvoiceList" + excelName);
    }

    @SysLog("发票归还导出")
    @AuthIgnore
    @RequestMapping("export/invoiceReturnExport")
    public void invoiceReturnExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final String schemaLabel = getCurrentUserSchemaLabel();
        params.put("schemaLabel", schemaLabel);

        //获取当前用户的userId
        params.put("userId", getUserId());

        final Map<String, List<ComprehensiveInvoiceQueryEntity>> map = newHashMapWithExpectedSize(1);
        map.put("returnInvoiceList", invoiceBorrowService.queryInvoiceBorrowList(params).getResults());


        try {
            List<FpghExcelEntity> list2=invoiceBorrowService.transformExcle2(map.get("returnInvoiceList"));
            ExcelUtil.writeExcel(response,list2,"发票归还导出","sheet1", ExcelTypeEnum.XLSX,FpghExcelEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        //生成excel
//        final BorrowInvoiceExcel excelView = new BorrowInvoiceExcel(map, "export/invoiceBorrow/returnInvoiceList.xlsx", "returnInvoiceList");
//        final String excelName = String.valueOf(new Date().getTime());
//        excelView.write(response, "returnInvoiceList" + excelName);
    }

    @SysLog("借阅记录导出")
    @AuthIgnore
    @RequestMapping("export/recordBorrowExport")
    public void recordBorrowExport(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        LOGGER.info("导出excel:{}", params);

        final Map<String, List<BorrowEntity>> map = newHashMapWithExpectedSize(1);
        List<BorrowEntity> recordList = invoiceBorrowService.queryBorrowRecordList(params).getResults();
        map.put("borrowRecordList", recordList);

        try {
            List<JyjlcxExcelEntity> list2=invoiceBorrowService.transformExcle3(recordList);
            ExcelUtil.writeExcel(response,list2,"借阅记录导出","sheet1", ExcelTypeEnum.XLSX,JyjlcxExcelEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


//        //生成excel
//        final BorrowRecordExcel excelView = new BorrowRecordExcel(map, "export/invoiceBorrow/borrowRecordList.xlsx", "borrowRecordList");
//        final String excelName = String.valueOf(new Date().getTime());
//        excelView.write(response, "borrowRecordList" + excelName);

    }

    @SysLog("借阅导入")
    @PostMapping(value = "/invoice/borrow/import")
    public Map<String, Object> getInvoiceCheck(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("借阅导入,params {}", multipartFile);
        Map<String,Object> map = invoiceBorrowService.parseExcel(multipartFile);
        return map;
    }
    @SysLog("归还导入")
    @PostMapping(value = "/invoice/borrow/importGh")
    public Map<String, Object> getInvoiceGh(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("归还导入,params {}", multipartFile);
        Map<String,Object> map = invoiceBorrowService.parseExcelGh(multipartFile);
        return map;
    }
}
