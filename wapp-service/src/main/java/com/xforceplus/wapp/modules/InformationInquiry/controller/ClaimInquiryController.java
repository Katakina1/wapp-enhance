package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ClaimEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.ClaimInquiryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_CLAIMLIST_QUERY;
import static com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry.INFORMATIONINQUIRY_PODETAILS_QUERY;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * 索赔查询
 */
@RestController
public class ClaimInquiryController extends AbstractController {
    private static final Logger LOGGER = getLogger(ClaimInquiryController.class);
    @Autowired
    private ClaimInquiryService claimInquiryService;


    /**
     * 索赔查询
     */
    @SysLog("索赔信息查询")
    @PostMapping(value = INFORMATIONINQUIRY_CLAIMLIST_QUERY)
    public R getReturnGoodsList(@RequestParam Map<String,Object> params ){
        LOGGER.info("索赔信息查询,param{}",params);
        Query query =new Query(params);
        if(query.get("hostStatus").equals("1")){
            query.put("hostStatus",'1');
        }
        if(query.get("hostStatus").equals("0")){
            query.put("hostStatus",'0');
        }
        Integer result = claimInquiryService.claimlistCount(query);
        List<ClaimEntity> list=claimInquiryService.claimlist(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }
}
