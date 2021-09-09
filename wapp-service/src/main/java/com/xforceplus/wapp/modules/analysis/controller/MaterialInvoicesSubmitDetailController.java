package com.xforceplus.wapp.modules.analysis.controller;

import static com.xforceplus.wapp.modules.Constant.SHORT_DATE_FORMAT;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.xforceplus.wapp.common.exception.RRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.analysis.export.MaterialInvoiceSubmitDetailExcel;
import com.xforceplus.wapp.modules.analysis.service.MaterialInvoiceSubmitDetailService;
import com.xforceplus.wapp.modules.report.entity.ComprehensiveInvoiceQueryEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;

/**
 *实物发票提交明细
 */
@RestController
@RequestMapping("/analysis")
public class MaterialInvoicesSubmitDetailController extends AbstractController{
	
	@Autowired
	private MaterialInvoiceSubmitDetailService materialInvoiceSubmitDetailService;

	/**
	 * 查询实物发票提交明细
	 * @param params
	 * @return
	 */
	@RequestMapping("/materialInvoicesSubmitDetail/queryList")
	public R queryList(@RequestParam Map<String, Object>params) {
		
        //查询列表数据
        Query query = new Query(params);
        if(getUserId()==null){
            throw new RRException("当前会话已过期,请按F5或重新登录");
        }
       
        query.put("userID", getUserId());
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryCount = materialInvoiceSubmitDetailService.queryMaterial(query);
      
        PageUtils pageUtil = new PageUtils(queryCount.getResults(), queryCount.getTotalCount(), query.getLimit(), query.getPage());
		return R.ok().put("page", pageUtil);
	}
	
	@SysLog("实物发票明细导出")
    @RequestMapping("/materialInvoicesSubmitDetail/export")
    public void export(@RequestParam Map<String, Object> params,HttpServletResponse response) {
        //获取当前用户的userId
        params.put("userId", getUserId());
        PagedQueryResult<ComprehensiveInvoiceQueryEntity> queryInvoiceSubmit = materialInvoiceSubmitDetailService.queryMaterial(params);

        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("materialDetail",queryInvoiceSubmit.getResults());
       
        //生成excel
        final MaterialInvoiceSubmitDetailExcel excelView = new MaterialInvoiceSubmitDetailExcel(map, "export/analysis/materialInvoicesSubmitDetail.xlsx", "materialDetail");
        final String excelName = now().toString(SHORT_DATE_FORMAT);
        excelView.write(response, "实物发票提交明细" + excelName);
    }
}
