package com.xforceplus.wapp.modules.scanRefund.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.pack.controller.GenerateBindNumberController;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GenerateRefundNumberEntity;
import com.xforceplus.wapp.modules.scanRefund.entity.GroupRefundEntity;
import com.xforceplus.wapp.modules.scanRefund.service.GenerateRefundNumberService;
import com.xforceplus.wapp.modules.scanRefund.service.GroupRefundService;
import com.xforceplus.wapp.modules.sys.controller.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 发票综合查询
 */
@RestController
@RequestMapping("/scanRefund/groupRefund")
public class GroupRefundController extends AbstractController {



    @Autowired
    private GroupRefundService groupRefundService;
    private GenerateRefundNumberService generateRefundNumberService;
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
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<GroupRefundEntity> list = groupRefundService.queryList(query);
        ReportStatisticsEntity result = groupRefundService.queryTotalResult(query);


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
        Integer resultReturn1 = groupRefundService.getRecordInvoiceListCount(query);
        List<GroupRefundEntity> groupRefundEntity1 = groupRefundService.getRecordInvoiceList(query);

        //PO查列表数据
        Integer resultReturn2 = groupRefundService.getPOListCount(query);
        List<GroupRefundEntity> groupRefundEntity2 = groupRefundService.getPOList(query);

        //索赔查列表数据
        Integer resultReturn3 = groupRefundService.getClaimListCount(query);
        List<GroupRefundEntity> groupRefundEntity3 = groupRefundService.getClaimList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity1, resultReturn1, query.getLimit(),query.getPage());
        PageUtils pageUtil2 = new PageUtils(groupRefundEntity2, resultReturn2, query.getLimit(),query.getPage());
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
        Integer resultReturn = groupRefundService.getRecordInvoiceListCount(query);
        List<GroupRefundEntity> groupRefundEntity = groupRefundService.getRecordInvoiceList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page1", pageUtil1);

    }

    @RequestMapping("/POlist")
    @SysLog("查询PO信息列表")
    public R returnPOList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //PO查列表数据
        Integer resultReturn = groupRefundService.getPOListCount(query);
        List<GroupRefundEntity> groupRefundEntity = groupRefundService.getPOList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page2", pageUtil1);

    }


    @RequestMapping("/claimlist")
    @SysLog("查询索赔信息列表")
    public R returnClaimList(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //索赔查列表数据
        Integer resultReturn = groupRefundService.getClaimListCount(query);
        List<GroupRefundEntity> groupRefundEntity = groupRefundService.getClaimList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page3", pageUtil1);

    }


    @SysLog("确定是否退票")
    @RequestMapping("/refundyesno")
    public R refundyesnobyId(@RequestBody GroupRefundEntity groupRefundEntity) {
        int a = groupRefundEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            List<GroupRefundEntity> queryuuid = groupRefundService.queryuuid(groupRefundEntity.getIds()[i]);
            GroupRefundEntity queryreason = groupRefundService.queryReason(groupRefundEntity.getIds()[i]);
            if(queryreason==null){
                queryreason = new GroupRefundEntity();
                queryreason.setRefundReason("");
            }
            int count = queryuuid.size();
            for(int j = 0; j<count;j++){
                groupRefundService.inputrefundyesno(queryuuid.get(j).getUuid(),queryreason.getRefundReason());
            }

        }
        return R.ok();
    }

    @SysLog("确定退票")
    @RequestMapping("/refundsearch")
    public R refundsearch(@RequestBody GroupRefundEntity groupRefundEntity) {


        List<GroupRefundEntity> queryuuid = groupRefundService.queryuuid(groupRefundEntity.getId());
        GroupRefundEntity entity = groupRefundService.queryisdel(queryuuid.get(0).getUuid());

        return R.ok().put("isdel",entity.getIsdel());
    }
    @SysLog("整组退")
    @RequestMapping("/refund")
    public R bbindingnobyId(@RequestBody GroupRefundEntity groupRefundEntity) {
        final String schemaLabel = getCurrentUserSchemaLabel();

        groupRefundEntity.setSchemaLabel(schemaLabel);

        Date de = new Date();

        List<GroupRefundEntity> queryuuid = groupRefundService.queryuuid(groupRefundEntity.getId());
//        GroupRefundEntity queryuuid = groupRefundService.queryuuid(schemaLabel,groupRefundEntity.getId());
        //查询最大装订册号
        GroupRefundEntity querymaxrebateno = groupRefundService.querymaxrebateno();
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

                        groupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), groupRefundEntity.getRefundNotes(), str3);

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
                        groupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), groupRefundEntity.getRefundNotes(), str4);
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
                    groupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), groupRefundEntity.getRefundNotes(), str4);
                }
//            //查询整组退单号
//            GenerateBindNumberEntity querybbindingno = generateBindNumberServic.querybbindingno(generateBindNumberEntity.getIds()[0]);
//            return R.ok().put("bbindingNo",querybbindingno.getBbindingNo());

            }
        }else {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
            String str = df.format(de);
            AtomicInteger atomicNum = new AtomicInteger();
            int newNum = atomicNum.incrementAndGet();
            String newStrNum = String.format("%06d", newNum);
            String str4 = str + newStrNum;
            for (int i = 0; i < queryuuid.size(); i++) {
//              生成整组退单号
                groupRefundService.inputrefundnotes(schemaLabel, queryuuid.get(i).getUuid(), groupRefundEntity.getRefundNotes(), str4);
            }
        }
        return R.ok();
    }
}
