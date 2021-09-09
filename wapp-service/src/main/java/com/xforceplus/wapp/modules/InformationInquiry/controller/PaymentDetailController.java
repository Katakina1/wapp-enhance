package com.xforceplus.wapp.modules.InformationInquiry.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.InformationInquiry.constant.InformationInquiry;
import com.xforceplus.wapp.modules.InformationInquiry.entity.PaymentDetailEntity;
import com.xforceplus.wapp.modules.InformationInquiry.service.PaymentDetailService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;


/**
 * 订单查询
 */
@RestController
public class PaymentDetailController extends AbstractController {
    private static final Logger LOGGER = getLogger(PaymentDetailController.class);
    @Autowired
    private PaymentDetailService paymentDetailService;


    /**
     * 供应商付款明细查询
     */
    @SysLog("供应商付款明细查询")
    @PostMapping(value = InformationInquiry.INFORMATIONINQUIRY_PAYMENTLIST_QUERY)
    public R getReturnGoodsList(@RequestParam Map<String, Object> params) {
        LOGGER.info("供应商付款明细查询,param{}", params);
        Query query = new Query(params);
        Integer result = paymentDetailService.paylistCount(query);
        List<PaymentDetailEntity> list = paymentDetailService.findPayList(query);
        PageUtils pageUtil = new PageUtils(list, result, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @PostMapping("modules/InformationInquiry/paymentlist/upload")
    public R upload(@RequestParam("file") MultipartFile multipartFile) {
        LOGGER.info("SAP供应商数据上传：" +multipartFile);
        try {
            paymentDetailService.upload(multipartFile);
            return R.ok();
        } catch (Exception e) {
            LOGGER.error("SAP供应商数据上传:{}", e);
            return R.error(500, e.getMessage());
        }
    }
}
