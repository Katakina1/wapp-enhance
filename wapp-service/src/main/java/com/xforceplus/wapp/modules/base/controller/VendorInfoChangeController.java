package com.xforceplus.wapp.modules.base.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.base.entity.UserEntity;
import com.xforceplus.wapp.modules.base.service.VendorInfoChangeService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class VendorInfoChangeController extends AbstractController {
    private final static Logger LOGGER = getLogger(VendorInfoChangeController.class);

    @Autowired
    private VendorInfoChangeService vendorInfoChangeService;

    @SysLog("获取供应商信息")
    @RequestMapping("vendorInfoChange/getUserInfo")
    public R getUserInfo() {
        return R.ok().put("userInfo", getUser());
    }

    @SysLog("供应商信息变更提交")
    @RequestMapping("vendorInfoChange/submit")
    public R save(@RequestBody UserEntity userEntity) {
        vendorInfoChangeService.submit(userEntity);
        return R.ok();
    }
    @SysLog("供应商信息变更列表查询")
    @RequestMapping("vendorInfoChangeWo/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        List<UserEntity> list = vendorInfoChangeService.queryVendorInfoChangeList(query);
        Integer count = vendorInfoChangeService.queryVendorInfoChangeCount(query);
        PageUtils pageUtil = new PageUtils(list, count, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    @SysLog("变更信息审核通过")
    @RequestMapping("vendorInfoChangeWo/auditAgree")
    public R auditAgree(@RequestBody Long[] ids) {
        vendorInfoChangeService.auditAgree(ids);
        return R.ok();
    }
}
