package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.EnterPackageNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.service.EnterPackageNumberService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/enterPackageNumber")
public class EnterPackageNumberController extends AbstractController {



    @Autowired
    private EnterPackageNumberService enterPackageNumberService;

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描退票查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<EnterPackageNumberEntity> list = enterPackageNumberService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = enterPackageNumberService.queryTotalResult(schemaLabel,query);

        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }
    @SysLog("录入邮包号")
    @RequestMapping("/uplist")
    public R inputrebateExpressno(@RequestBody EnterPackageNumberEntity enterPackageNumberEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //去掉邮包号不能重复的限制
//        final int count = enterPackageNumberService.queryrebateexpressno(enterPackageNumberEntity.getRebateExpressno());
//        if(count>0){
//            return R.error(1,"邮包号已经存在！");
//        }
//        查询列表数据
        enterPackageNumberEntity.setSchemaLabel(schemaLabel);
        String[] str = enterPackageNumberEntity.getRebateNos();
        //录入邮包号
        enterPackageNumberService.inputrebateExpressno(schemaLabel, enterPackageNumberEntity.getRebateNos(),enterPackageNumberEntity.getRebateExpressno(),enterPackageNumberEntity.getMailDate(),enterPackageNumberEntity.getMailCompany());

        return R.ok();

    }
}
