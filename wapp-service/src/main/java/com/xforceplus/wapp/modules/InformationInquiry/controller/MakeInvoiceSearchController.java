package com.xforceplus.wapp.modules.InformationInquiry.controller;

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcel2Entity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcelEntity;
import com.xforceplus.wapp.utils.excel.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchEntity;
import com.xforceplus.wapp.modules.InformationInquiry.export.MakeInvoiceSearchExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.MakeInvoiceSearchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;



/**
 * 开票信息查询
 * @author user
 *
 */
@RestController
public class MakeInvoiceSearchController extends AbstractController{

	@Autowired
	private MakeInvoiceSearchService makeInvoiceSearchService;
	
	/**
	 * 开票信息查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/information/makeInvoiceSearch")
	public R getSupplier(@RequestParam Map<String, Object> params) {
		//查询列表数据
        Query query = new Query(params);
        query.put("userID", getUserId());
        PagedQueryResult<SupplierInformationSearchEntity> queryCount = makeInvoiceSearchService.queryResult(query);
      
        PageUtils pageUtil = new PageUtils(queryCount.getResults(), queryCount.getTotalCount(), query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	@SysLog("供应商信息导出")
    @RequestMapping("/export/makeInvoiceSearch")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        PagedQueryResult<SupplierInformationSearchEntity> queryCount = makeInvoiceSearchService.queryResult(params);


        //转换Excel数据
        List<SupplierInformationSearchExcel2Entity> list2=makeInvoiceSearchService.transformExcle(queryCount.getResults());
        try {

            ExcelUtil.writeExcel(response,list2,"开票信息导出","sheet1", ExcelTypeEnum.XLSX,SupplierInformationSearchExcel2Entity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }






//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("makeInvoiceSearch",queryCount.getResults());
//
//        //生成excel
//        final MakeInvoiceSearchExcel excelView = new MakeInvoiceSearchExcel(map, "export/InformationInquiry/makeInvoiceSearch.xlsx", "makeInvoiceSearch");
//        final String excelName = now().toString(SHORT_DATE_FORMAT);
//        excelView.write(response, "开票信息查询" + excelName);
    }
}
