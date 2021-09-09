package com.xforceplus.wapp.modules.fixed.controller;

import com.xforceplus.wapp.common.annotation.SysLog;
import com.xforceplus.wapp.common.utils.PageUtils;
import com.xforceplus.wapp.common.utils.Query;
import com.xforceplus.wapp.common.utils.R;
import com.xforceplus.wapp.modules.fixed.entity.IndexGroupRefundEntity;
import com.xforceplus.wapp.modules.fixed.service.IndexGroupRefundService;
import com.xforceplus.wapp.modules.report.entity.ReportStatisticsEntity;
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
@RequestMapping("/index/indexGroupRefund")
public class IndexGroupRefundController extends AbstractController {



    @Autowired
    private IndexGroupRefundService indexGroupRefundService;
    private static final Logger LOGGER = getLogger(IndexGroupRefundController.class);

    /**
     * 查询列表
     * @param params
     * @return
     */
    @SysLog("扫描查询列表")
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        //查询列表数据
        Query query = new Query(params);
        //当前登录人ID,用于查询对应税号
        query.put("userID", getUserId());
        List<IndexGroupRefundEntity> list = indexGroupRefundService.queryList(query);
        ReportStatisticsEntity result = indexGroupRefundService.queryTotalResult(query);


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
        Integer resultReturn1 = indexGroupRefundService.getRecordInvoiceListCount(query);
        List<IndexGroupRefundEntity> groupRefundEntity1 = indexGroupRefundService.getRecordInvoiceList(query);

        //PO查列表数据
        Integer resultReturn2 = indexGroupRefundService.getPOListCount(query);
        List<IndexGroupRefundEntity> groupRefundEntity2 = indexGroupRefundService.getPOList(query);

        //索赔查列表数据
//        Integer resultReturn3 = indexGroupRefundService.getClaimListCount(query);
//        List<IndexGroupRefundEntity> groupRefundEntity3 = indexGroupRefundService.getClaimList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity1, resultReturn1, query.getLimit(),query.getPage());
        PageUtils pageUtil2 = new PageUtils(groupRefundEntity2, resultReturn2, query.getLimit(),query.getPage());
//        PageUtils pageUtil3 = new PageUtils(groupRefundEntity3, resultReturn3, query.getLimit(),query.getPage());


        return R.ok().put("page1", pageUtil1).put("page2", pageUtil2);

    }
    @RequestMapping("/recordinvoicelist")
    @SysLog("查询抵账信息列表")
    public R returnRecordInvoice(@RequestParam Map<String, Object> params) {

        //params.put("userId",getUserId());
        LOGGER.info("查询条件为:{}", params);
        Query query = new Query(params);
        //抵账查列表数据
        Integer resultReturn = indexGroupRefundService.getRecordInvoiceListCount(query);
        List<IndexGroupRefundEntity> groupRefundEntity = indexGroupRefundService.getRecordInvoiceList(query);

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
        Integer resultReturn = indexGroupRefundService.getPOListCount(query);
        List<IndexGroupRefundEntity> groupRefundEntity = indexGroupRefundService.getPOList(query);

        PageUtils pageUtil1 = new PageUtils(groupRefundEntity, resultReturn, query.getLimit(), query.getPage());


        return R.ok().put("page2", pageUtil1);

    }


    @SysLog("确定是否退票")
    @RequestMapping("/refundyesno")
    public R refundyesnobyId(@RequestBody IndexGroupRefundEntity indexGroupRefundEntity) {
        int a = indexGroupRefundEntity.getIds().length;
        for (int i = 0; i < a; i++) {
            List<IndexGroupRefundEntity> queryuuid = indexGroupRefundService.queryuuid(indexGroupRefundEntity.getIds()[i]);
            IndexGroupRefundEntity queryreason = indexGroupRefundService.queryReason(indexGroupRefundEntity.getIds()[i]);
            if(queryreason==null){
                queryreason = new IndexGroupRefundEntity();
                queryreason.setRefundReason("");
            }
            int count = queryuuid.size();
            for(int j = 0; j<count;j++){
                indexGroupRefundService.inputrefundyesno(queryuuid.get(j).getUuid(),queryreason.getRefundReason());
            }

        }
        return R.ok();
    }


}
