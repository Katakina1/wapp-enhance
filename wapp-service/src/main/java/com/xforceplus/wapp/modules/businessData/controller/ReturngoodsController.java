package com.xforceplus.wapp.modules.businessData.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.businessData.entity.ReturngoodsEntity;
import com.xforceplus.wapp.modules.businessData.service.ReturngoodsService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.businessData.constant.BusinessDataConstant.BUSINESSDATA_RETURNGOODSBY_QUERY;
import static com.xforceplus.wapp.modules.businessData.constant.BusinessDataConstant.BUSINESSDATA_RETURNGOODS_QUERY;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Adil.Xu
 */
@RestController
public class ReturngoodsController extends AbstractController{

    private static final Logger LOGGER = getLogger(ReturngoodsController.class);
    private ReturngoodsService returngoodsService;
    @Autowired
    public ReturngoodsController(ReturngoodsService returngoodsService){
        this.returngoodsService=returngoodsService;
    }

    /**
     * 退货查询
     */
    @SysLog("退货信息查询")
    @PostMapping(value = BUSINESSDATA_RETURNGOODS_QUERY)
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("退货信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result = returngoodsService.returnGoodsQueryCount(query);
        List<ReturngoodsEntity> list = returngoodsService.getReturnGoodsList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 未红冲退货查询
     */
    @SysLog("未红冲退货信息查询")
    @PostMapping(value = BUSINESSDATA_RETURNGOODSBY_QUERY)
    public R getReturnGoods(@RequestParam Map<String,Object> params ){
        LOGGER.info("未红冲退货信息查询,param{}",params);
        Query query =new Query(params);
        query.put("userCode",getUser().getUsercode());
        Integer result = returngoodsService.returnGoodsQueryRedCount(query);
        List<ReturngoodsEntity> list = returngoodsService.getReturnGoodsListBy(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
