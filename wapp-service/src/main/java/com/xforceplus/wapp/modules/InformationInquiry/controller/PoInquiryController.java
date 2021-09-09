package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_PODETAILS_QUERY;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 订单查询
 */
@RestController
public class PoInquiryController extends AbstractController {
    private static final Logger LOGGER = getLogger(PoInquiryController.class);
    @Autowired
    private PoInquiryService poInquiryService;


    /**
     * 订单查询
     */
    @SysLog("订单信息查询")
    @PostMapping(value = INFORMATIONINQUIRY_PODETAILS_QUERY)
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("订单信息查询,param{}",params);
        Query query =new Query(params);
        Integer result = poInquiryService.polistCount(query);
        List<poEntity> list=poInquiryService.polist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
