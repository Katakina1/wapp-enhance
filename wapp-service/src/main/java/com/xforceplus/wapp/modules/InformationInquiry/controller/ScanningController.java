package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.entity.ScanningEntity;
import com.xforceplus.wapp.modules.InformationInquiry.entity.poEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.PoInquiryService;
import com.xforceplus.wapp.modules.InformationInquiry.service.ScanningService;
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
 * 问题单查询(商品)
 */
@RestController
@RequestMapping("/InformationInquiry/scanning")
public class ScanningController extends AbstractController {
    private static final Logger LOGGER = getLogger(ScanningController.class);
    @Autowired
    private ScanningService scanningService;

    @SysLog("问题单查询(商品)")
    @RequestMapping("/list")
    public R scanningList(@RequestParam Map<String,Object> params ){
        LOGGER.info("问题单查询(商品),param{}",params);
        Query query =new Query(params);
        if(query.get("orgType").equals("8")){
            query.put("venderid",getUser().getUsercode());
        }
        Integer result = scanningService.scanningCount(query);
        List<ScanningEntity> list=scanningService.scanningList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("查询当前登录人是销方还是购方")
    @RequestMapping("/searchUserOrgType")
    public R searchUserOrgType(@RequestParam Map<String,Object> params ){
        LOGGER.info("查询当前登录人是销方还是购方",params);
        params.put("userid",getUserId());
        String list=scanningService.serachUserOrgType(params);
        return R.ok().put("page", list);
    }
}
