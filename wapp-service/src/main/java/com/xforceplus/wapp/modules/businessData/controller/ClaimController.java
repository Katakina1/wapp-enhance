package com.xforceplus.wapp.modules.businessData.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.businessData.export.ClaimQueryExcel;
import com.xforceplus.wapp.modules.businessData.service.ClaimService;
import com.xforceplus.wapp.modules.businessData.service.PoService;
import com.xforceplus.wapp.modules.posuopei.entity.ClaimEntity;
import com.xforceplus.wapp.modules.posuopei.entity.PoEntity;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMapWithExpectedSize;
import static org.joda.time.DateTime.now;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class ClaimController extends AbstractController {
    private final static Logger LOGGER = getLogger(ClaimController.class);
    private ClaimService claimService;
    @Autowired
    public ClaimController(ClaimService claimService){
        this.claimService=claimService;
    }

    /**
     * claim查询
     * @param params
     * @return
     */
    @SysLog("claim查询")
    @PostMapping(value = "/modules/businessData/claim/query")
    public R claimQuery(@RequestParam Map<String,Object> params){
        LOGGER.info("po查询,param{}",params);
        params.put("venderid",getUser().getUsercode());
        Query query=new Query(params);
        PagedQueryResult<ClaimEntity> poEntityPagedQueryResult=claimService.claimQueryList(query);
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),poEntityPagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }

    @SysLog("索賠查询导出")
    @GetMapping(value = "/export/claimQueryExport")
    public void poQueryExport(@RequestParam Map<String,Object> params, HttpServletResponse response){
        LOGGER.info("po查询,param{}",params);
        params.put("venderid",getUser().getUsercode());
        PagedQueryResult<ClaimEntity> poEntityPagedQueryResult=claimService.claimQueryList(params);
        final Map<String, Object> map = newHashMapWithExpectedSize(1);
        map.put("claimAmount",params.get("claimAmount"));
        //生成excel
        final ClaimQueryExcel excelView = new ClaimQueryExcel(poEntityPagedQueryResult,map, "export/InformationInquiry/claimqueryList.xlsx", "ClaimQueryExcel");
        final String excelName = now().toString("yyyyMMdd");
        excelView.write(response, "ClaimQueryExcel" + excelName);

    }
}
