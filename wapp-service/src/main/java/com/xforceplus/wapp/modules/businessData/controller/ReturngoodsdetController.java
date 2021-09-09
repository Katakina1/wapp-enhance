package com.xforceplus.wapp.modules.businessData.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.safesoft.PagedQueryResult;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsdetEntity;
import com.xforceplus.wapp.modules.businessData.service.ReturngoodsdetService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.businessData.constant.BusinessDataConstant.BUSINESSDATA_RETURNGOODSDET_QUERY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Adil.Xu
 */
@RestController
public class ReturngoodsdetController extends AbstractController {
    private static final Logger LOGGER = getLogger(ReturngoodsController.class);
    private ReturngoodsdetService returngoodsdetService;
    @Autowired
    public ReturngoodsdetController(ReturngoodsdetService returngoodsdetService){ this.returngoodsdetService=returngoodsdetService; }

    /**
     * 退货明细查询
     */
    @SysLog("退货明细信息查询")
    @PostMapping(value = BUSINESSDATA_RETURNGOODSDET_QUERY)
    public R getReturnGoodsDetList(@RequestParam Map<String,Object> params){
        LOGGER.info("退货明细信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result =returngoodsdetService.returnGoodsDetQueryCount(query);
        List<ReturngoodsdetEntity> list =returngoodsdetService.getReturnGoodsdetList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page1", pageUtil);
    }
}
