package com.xforceplus.wapp.modules.scanRefund.controller;


import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.CostGroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.CostGroupRefundService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/costGroupRefund")
public class CostGroupRefundController extends AbstractController {
    @Autowired
    private CostGroupRefundService costgroupRefundService;
    private static final Logger LOGGER = getLogger(GroupRefundController.class);

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        final String schemaLabel = getCurrentUserSchemaLabel();
        //查询列表数据
        Query query = new Query(params);
        List<CostGroupRefundEntity> list = costgroupRefundService.queryList(schemaLabel,query);
        ReportStatisticsEntity result = costgroupRefundService.queryTotalResult(schemaLabel,query);


        PageUtils pageUtil = new PageUtils(list, result.getTotalCount(), query.getLimit(), query.getPage());

        return R.ok().put("page", pageUtil).put("totalAmount", result.getTotalAmount()).put("totalTax", result.getTotalTax());
    }

    @RequestMapping("/alllist")
    @SysLog("查询明细列表")
    public R allList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn1 = costgroupRefundService.getRecordInvoiceListCount(query);
        List<CostGroupRefundEntity> groupRefundEntity1 = costgroupRefundService.getRecordInvoiceList(query);

        //索赔查列表数据
        Integer resultReturn2 = costgroupRefundService.getRateListCount(query);
        CostGroupRefundEntity gostGroupRefundEntity = costgroupRefundService.getRateListTotal(query);
        List<CostGroupRefundEntity> groupRefundEntity2 = costgroupRefundService.getRateList(query);

        //费用查列表数据
        Integer resultReturn3 = costgroupRefundService.getCostListCount(query);
        List<CostGroupRefundEntity> groupRefundEntity3 = costgroupRefundService.getCostList(query);



        PageUtils pageUtil1 = new PageUtils(groupRefundEntity1, resultReturn1, query.getLimit(),query.getPage());
        PageUtils pageUtil2 = new PageUtils(groupRefundEntity2, resultReturn2, query.getLimit(),query.getPage(),gostGroupRefundEntity.getSummationTotalAmount(),gostGroupRefundEntity.getSummationTaxAmount());
        PageUtils pageUtil3 = new PageUtils(groupRefundEntity3, resultReturn3, query.getLimit(),query.getPage());


        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2).put("page3", pageUtil3);

    }
    @RequestMapping("/recordinvoicelist")
    @SysLog("查询抵账信息列表")
    public R returnRecordInvoice(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = costgroupRefundService.getRecordInvoiceListCount(query);
        List<CostGroupRefundEntity> groupRefundEntity = costgroupRefundService.getRecordInvoiceList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }
    @RequestMapping("/Ratelist")
    @SysLog("查询税率信息列表")
    public R returnRateList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //查列表数据
        CostGroupRefundEntity gostGroupRefundEntity = costgroupRefundService.getRateListTotal(query);
        Integer resultReturn = costgroupRefundService.getRateListCount(query);
        List<CostGroupRefundEntity> groupRefundEntity = costgroupRefundService.getRateList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage(),gostGroupRefundEntity.getSummationTotalAmount(),gostGroupRefundEntity.getSummationTaxAmount());


        return R.ok().put("page2", pageUtil1);

    }

    @RequestMapping("/Costlist")
    @SysLog("查询Cost信息列表")
    public R returnPOList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //PO查列表数据
        Integer resultReturn = costgroupRefundService.getCostListCount(query);
        List<CostGroupRefundEntity> groupRefundEntity = costgroupRefundService.getCostList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page3", pageUtil1);

    }

    @SysLog("整组退")
    @RequestMapping("/refund")
    public R bbindingnobyId(@RequestBody CostGroupRefundEntity costGroupRefundEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        costGroupRefundEntity.setSchemaLabel(schemaLabel);

        Date de = new Date();
        //查询最大退单号
        List<CostGroupRefundEntity> queryuuid = costgroupRefundService.queryuuid(costGroupRefundEntity.getId());
        CostGroupRefundEntity querymaxrebateno = costgroupRefundService.querymaxrebateno();
        if (querymaxrebateno != null) {
            String str2 = querymaxrebateno.getRebateNo();
            if (!str2.equals(null) && !str2.equals("")) {
                str2 = str2.substring(0, 6);
                SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                String str = df.format(de);
                //判断是否是今天日期
                if (str.equals(str2)) {
                    String str3 = querymaxrebateno.getRebateNo();
                    Long a = Long.valueOf(str3);
                    a = a + 1;
                    str3 = String.valueOf(a);
                    for (int i = 0; i < queryuuid.size(); i++) {
//              生成整组退单号

                        costgroupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), costGroupRefundEntity.getRefundNotes(), str3);

                    }


//            //查询整组退单号
//            GenerateBindNumberEntity querybbindingno = generateBindNumberService.querybbindingno(generateBindNumberEntity.getIds()[0]);
//            return R.ok().put("bbindingNo",querybbindingno.getBbindingNo());
                    return R.ok();
                } else {
                    AtomicInteger atomicNum = new AtomicInteger();
                    int newNum = atomicNum.incrementAndGet();
                    String newStrNum = String.format("%06d", newNum);
                    String str4 = str + newStrNum;
                    for (int i = 0; i < queryuuid.size(); i++) {
//              生成整组退单号
                        costgroupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), costGroupRefundEntity.getRefundNotes(), str4);
                    }
//            //查询整组退单号
//            GenerateBindNumberEntity querybbindingno = generateBindNumberService.querybbindingno(generateBindNumberEntity.getIds()[0]);
//            return R.ok().put("bbindingNo",querybbindingno.getBbindingNo());
                }
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
                String str = df.format(de);
                AtomicInteger atomicNum = new AtomicInteger();
                int newNum = atomicNum.incrementAndGet();
                String newStrNum = String.format("%06d", newNum);
                String str4 = str + newStrNum;
                for (int i = 0; i < queryuuid.size(); i++) {
//              生成整组退单号
                    costgroupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), costGroupRefundEntity.getRefundNotes(), str4);
                }
//            //查询整组退单号
//            GenerateBindNumberEntity querybbindingno = generateBindNumberServic.querybbindingno(generateBindNumberEntity.getIds()[0]);
//            return R.ok().put("bbindingNo",querybbindingno.getBbindingNo());

            }

        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
            String str = df.format(de);
            AtomicInteger atomicNum = new AtomicInteger();
            int newNum = atomicNum.incrementAndGet();
            String newStrNum = String.format("%06d", newNum);
            String str4 = str + newStrNum;
            for (int i = 0; i < queryuuid.size(); i++) {
//              生成整组退单号
                costgroupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), costGroupRefundEntity.getRefundNotes(), str4);
            }
        }
        return R.ok();
    }

    @SysLog("确定是否退票")
    @RequestMapping("/refundyesno1")
    public R refundyesnobyId(@RequestBody CostGroupRefundEntity costGroupRefundEntity) {
        int a = costGroupRefundEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            List<CostGroupRefundEntity> queryuuid = costgroupRefundService.queryuuid(costGroupRefundEntity.getIds()[i]);
            CostGroupRefundEntity queryreason = costgroupRefundService.queryReason(costGroupRefundEntity.getIds()[i]);
            if(queryreason==null){
                queryreason = new CostGroupRefundEntity();
                queryreason.setRefundReason("");
            }
            int count = queryuuid.size();
            for(int j = 0; j<count;j++) {
                costgroupRefundService.inputrefundyesno(queryuuid.get(j).getUuid(),queryreason.getRefundReason());
            }
        }
        return R.ok();
    }
}
