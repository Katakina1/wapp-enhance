package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.pack.entity.GenerateBindNumberEntity;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.HostRefundService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/hostRefund")
public class HostRefundController extends AbstractController {



    @Autowired
    private HostRefundService hostRefundService;
    private static final Logger LOGGER = getLogger(HostRefundController.class);

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描查询列表")
    @RequestMapping("/listhost")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GroupRefundEntity> list = hostRefundService.queryList(query);
        ReportStatisticsEntity result = hostRefundService.queryTotalResult(query);


        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描查询列表")
    @RequestMapping("/listrzh")
    public R listrzh(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GroupRefundEntity> list = hostRefundService.queryRzhList(query);
        ReportStatisticsEntity result = hostRefundService.queryRzhTotalResult(query);


        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }


    @SysLog("确定是否退票")
    @RequestMapping("/refundyesnohost")
    public R refundyesnobyId(@RequestBody GroupRefundEntity groupRefundEntity) {
        int a = groupRefundEntity.getIds().length;

        for (int i = 0; i < a; i++) {

            List<GroupRefundEntity> queryuuid = hostRefundService.queryuuid(groupRefundEntity.getIds()[i]);
            int count = hostRefundService.getuuidCount(queryuuid.get(0).getUuid());
            if(count == 0){
                    GenerateBindNumberEntity entity = hostRefundService.queryListUuid(queryuuid.get(0).getUuid());
                    String str = entity.getInvoiceStatus();
                    if(str.equals("1")){
                        entity.setRefundReason("认证后失控");
                    }else if(str.equals("2")){
                        entity.setRefundReason("认证后作废");
                    }else if(str.equals("4")){
                        entity.setRefundReason("认证后异常");
                    }else {
                        entity.setRefundReason("");
                    }
                hostRefundService.saveInvoice(entity);
            }else {
                GenerateBindNumberEntity entity1 = hostRefundService.queryListUuid(queryuuid.get(0).getUuid());
                String str = entity1.getInvoiceStatus();
                if(str.equals("1")){
                    entity1.setRefundReason("认证后失控");
                }else if(str.equals("2")){
                    entity1.setRefundReason("认证后作废");
                }else if(str.equals("4")){
                    entity1.setRefundReason("认证后异常");
                }else {
                    entity1.setRefundReason("");
                }
                hostRefundService.inputrefundyesno(queryuuid.get(0).getUuid(),entity1.getRefundReason());
            }

        }
        return R.ok();
    }

    @SysLog("确定退票")
    @RequestMapping("/refundsearchhost")
    public R refundsearch(@RequestBody GroupRefundEntity groupRefundEntity) {


        List<GroupRefundEntity> queryuuid = hostRefundService.queryuuid(groupRefundEntity.getId());
        GroupRefundEntity entity = hostRefundService.queryisdel(queryuuid.get(0).getUuid());

        return R.ok().put("isdel",entity.getIsdel());
    }

}
