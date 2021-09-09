package com.xforceplus.wapp.modules.businessData.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.businessData.export.PoQueryExcel;
import com.xforceplus.wapp.modules.businessData.service.PoService;
import com.xforceplus.wapp.modules.posuopei.entity.*;
import com.xforceplus.wapp.modules.posuopei.service.MatchService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static org.joda.time.DateTime.now;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.posuopei.constant.Constants.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author raymond.yan
 */
@RestController
public class PoController extends AbstractController {
    private final static Logger LOGGER = getLogger(PoController.class);
    private PoService poService;
    @Autowired
    public PoController(PoService poService){
        this.poService=poService;
    }

    /**
     * po查询
     * @param params
     * @return
     */
    @SysLog("po查询")
    @PostMapping(value = "/modules/businessData/po/query")
    public R poQuery(@RequestParam Map<String,Object> params){
        LOGGER.info("po查询,param{}",params);
        params.put("venderid",getUser().getUsercode());
        Query query=new Query(params);
        PagedQueryResult<PoEntity> poEntityPagedQueryResult=poService.poQueryList(query);
        PageUtils pageUtils=new PageUtils(poEntityPagedQueryResult.getResults(),poEntityPagedQueryResult.getTotalCount(),query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }

    @SysLog("订单查询导出")
    @GetMapping(value = "/export/poQueryExport")
    public void poQueryExport(@RequestParam Map<String,Object> params, HttpServletResponse response){
        LOGGER.info("po查询,param{}",params);
        params.put("venderid",getUser().getUsercode());
        PagedQueryResult<PoEntity> poEntityPagedQueryResult=poService.poQueryList(params);


        // Map<String, Map<String,Object>> map = newHashMapWithExpectedSize(1);
        //map.put("poEntityPagedQueryResult",poEntityPagedQueryResult);
        //生成excel
        final PoQueryExcel excelView = new PoQueryExcel(poEntityPagedQueryResult, "export/posuopei/PoQueryExcel.xlsx", "PoQueryExcel");
        final String excelName = now().toString("yyyyMMdd");
        excelView.write(response, "PoQueryExcel" + excelName);

    }

}
