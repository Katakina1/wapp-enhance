package com.xforceplus.wapp.modules.index.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.index.service.IndexMainVendorService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index/vendor")
public class IndexMainVendorController extends AbstractController {

    @Autowired
    IndexMainVendorService indexMainVendorService;

    @SysLog("供应商首页信息")
    @PostMapping("/main")
    public R getInvoiceCollectionSituation() {
        R result = R.ok();
        result.putAll(indexMainVendorService.getIndexMainInfo(getUserId(), getUser().getUsercode()));
        return result;
    }

    @SysLog("首页收货信息")
    @PostMapping("/receipt")
    public R getReceiptInfo() {
        R result = R.ok();
        result.putAll(indexMainVendorService.getReceiptInfo());
        return result;
    }
}
