package com.xforceplus.wapp.modules.InformationInquiry.controller;

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentInvoiceUploadExcelEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.SupplierInformationSearchExcelEntity;
import com.xforceplus.wapp.modules.report.entity.OptionEntity;
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
import com.xforceplus.wapp.modules.InformationInquiry.export.SupplierInformationSearchExcel;
import com.xforceplus.wapp.modules.InformationInquiry.service.SupplierInformationSearchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;


/**
 * 供应商信息查询
 *
 */
@RestController
public class SupplierInformationSearchController extends AbstractController{

	@Autowired
	private SupplierInformationSearchService supplierInformationSearchService;
	
	/**
	 * 供应商信息查询
	 * @param params
	 * @return
	 */
	@RequestMapping("/information/supplierInformationSearch")
	public R getSupplier(@RequestParam Map<String, Object> params) {
		//查询列表数据
        Query query = new Query(params);
        query.put("userID", getUserId());
        PagedQueryResult<SupplierInformationSearchEntity> queryCount = supplierInformationSearchService.queryResult(query);
      
        PageUtils pageUtil = new PageUtils(queryCount.getResults(), queryCount.getTotalCount(), query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	
	@SysLog("供应商信息导出")
    @RequestMapping("/export/supplierInformationSearch")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        PagedQueryResult<SupplierInformationSearchEntity> queryCount = supplierInformationSearchService.queryResult(params);
        //转换Excel数据
        List<SupplierInformationSearchExcelEntity> list2=supplierInformationSearchService.transformExcle(queryCount.getResults());
        try {

            ExcelUtil.writeExcel(response,list2,"供应商信息导出","sheet1", ExcelTypeEnum.XLSX,SupplierInformationSearchExcelEntity.class);
        } catch (com.xforceplus.wapp.utils.excel.ExcelException e) {
            e.printStackTrace();
        }


//
//        final Map<String, Object> map = newHashMapWithExpectedSize(1);
//        map.put("supplierInformation",queryCount.getResults());
//
//        //生成excel
//        final SupplierInformationSearchExcel excelView = new SupplierInformationSearchExcel(map, "export/InformationInquiry/supplierInformationSearch.xlsx", "supplierInformation");
//        final String excelName = now().toString(SHORT_DATE_FORMAT);
//        excelView.write(response, "供应商信息查询" + excelName);
    }

    @SysLog("查找供应商类型")
    @RequestMapping("/modules/supplier/getSupplierTypeList")
    public R getSupplierTypeList() {
        List<OptionEntity> optionEntities  = supplierInformationSearchService.getSupplierTypeList();
        return R.ok().put("optionList",optionEntities);
    }
 @SysLog("查找供应商类型")
    @RequestMapping("/modules/informationInquiry/updateSupplierTypeBath")
    public R updateSupplierTypeBath(@RequestParam Map<String, Object> param) {
        String message = supplierInformationSearchService.updateSupplierTypeBath(param);
        return R.ok().put("message",message);
    }

}
