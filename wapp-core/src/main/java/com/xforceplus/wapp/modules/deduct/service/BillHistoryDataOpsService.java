package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.core.collection.CollectionUtil;
import com.aisinopdf.text.E;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.enums.DeductBillMakeInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.deduct.dto.SyncHistoryBillMakeInvoiceStatusRequest;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfDeductPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Describe: 业务历史数据运维操作
 *
 * @Author xiezhongyong
 * @Date 2022-10-14
 */
@Slf4j
@Service
public class BillHistoryDataOpsService {

    @Autowired
    DeductPreInvoiceService deductPreInvoiceService;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfPreInvoiceDao preInvoiceDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private BillMakeInvoiceStatusService billMakeInvoiceStatusService;


    /**
     * 业务单历史数据开票状态同步
     *
     * @param request
     */
    @Async
    public void syncHistoryBillMakeInvoiceStatus(SyncHistoryBillMakeInvoiceStatusRequest request) {
        log.info("历史业务单开票状态刷新入参: {}", JSON.toJSON(request));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("历史业务单开票状态刷新");
        try {
            if (StringUtils.isNotBlank(request.getSettlementNo())) {
                List<TXfSettlementEntity> settlementEntityList = tXfSettlementDao.selectList(Wrappers.lambdaQuery(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, request.getSettlementNo()));
                syncBillMakeInvoiceStatus(settlementEntityList);
            } else {
                syncList(request);
            }
        } catch (Exception e) {
            log.error("历史业务单开票状态刷新异常: {}", e);
        } finally {
            stopWatch.stop();
        }

        log.info("opId: {};历史业务单开票状态刷新结束,耗时: {}", request.getOpId(), stopWatch.getLastTaskTimeMillis());
        log.info("opId: {};历史业务单开票状态刷新结束: {}", request.getOpId(), stopWatch.toString());


    }

    private void syncList(SyncHistoryBillMakeInvoiceStatusRequest request) {
        if (StringUtils.isBlank(request.getCreateTimeBegin()) || StringUtils.isBlank(request.getCreateTimeEnd())) {
            throw new RuntimeException("结算单编号为空时，时间参数不能为空");
        }
        LambdaQueryWrapper<TXfSettlementEntity> queryWrapper = Wrappers.lambdaQuery(TXfSettlementEntity.class).ge(TXfSettlementEntity::getCreateTime, request.getCreateTimeBegin())
                .le(TXfSettlementEntity::getCreateTime, request.getCreateTimeEnd())
                .in(TXfSettlementEntity::getSettlementStatus,
                        Arrays.asList(
                                TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
                                TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getCode(),
                                TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode(),
                                TXfSettlementStatusEnum.WAIT_CHECK.getCode(),
                                TXfSettlementStatusEnum.FINISH.getCode()
                        ));

        int pageSize = 1000;
        for (int i = 1; i <= pageSize; i++) {
            Page<TXfSettlementEntity> page = tXfSettlementDao.selectPage(new Page(i, pageSize), queryWrapper);
            List<TXfSettlementEntity> records = page.getRecords();
            if (CollectionUtils.isEmpty(records)) {
                log.info("opId: {};刷新历史业务单开票状态结束，本次总计查询结算单数量为: {}", request.getOpId(), page.getTotal());
                break;
            }
            log.info("opId: {};刷新历史业务单开票状态进行中，当前page: {}, size: {}", request.getOpId(), i, pageSize);
            syncBillMakeInvoiceStatus(records);
        }

    }


    /**
     * 同步结算单开票状态到业务单（只适用于历史数据处理 2022-10月之前的业务处理）
     *
     * @param settlementList
     */
    public void syncBillMakeInvoiceStatus(List<TXfSettlementEntity> settlementList) {
        for (TXfSettlementEntity settlement : settlementList) {
            try {
                log.warn("结算单号：{} 开始刷新开票状态", settlement.getSettlementNo());
                List<TXfPreInvoiceEntity> settlementPreInvoiceList = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getSettlementId, settlement.getId()));
                DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = deductPreInvoiceService.getMakeInvoiceStatus(settlementPreInvoiceList);
                // 结算单关联的业务单
                List<TXfBillDeductEntity> settlementRefBillList = tXfBillDeductDao.selectList(Wrappers.lambdaQuery(TXfBillDeductEntity.class).eq(TXfBillDeductEntity::getRefSettlementNo, settlement.getSettlementNo()));
                filterNewBill(settlement, settlementRefBillList);
                if (CollectionUtil.isEmpty(settlementRefBillList)) {
                    continue;
                }
                for (TXfBillDeductEntity bill : settlementRefBillList) {
                    billMakeInvoiceStatusService.syncMakeInvoiceStatus(bill.getId(), makeInvoiceStatus);
                    log.warn("历史数据-结算单号:{}, 业务单ID{},业务单号:{} 更新业务单开票状态为: {}", settlement.getSettlementNo(), bill.getId(), bill.getBusinessNo(), makeInvoiceStatus);
                }
            }catch (Exception e) {
                log.error("结算单号:{} 同步开票状态异常: {}", settlement.getSettlementNo(), e);
            }
        }


    }

    /**
     * 过滤新（10月迭代后的数据）业务单
     *
     * @param settlementRefBillList
     */
    private void filterNewBill(TXfSettlementEntity settlement, List<TXfBillDeductEntity> settlementRefBillList) {
        if (CollectionUtil.isEmpty(settlementRefBillList)) {
            return;
        }
        Iterator<TXfBillDeductEntity> iterator = settlementRefBillList.iterator();
        while (iterator.hasNext()) {
            TXfBillDeductEntity bill = iterator.next();
            LambdaQueryWrapper<TXfDeductPreInvoiceEntity> queryWrapper = Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class)
                    .eq(TXfDeductPreInvoiceEntity::getDeductId, bill.getId());
            List<TXfDeductPreInvoiceEntity> list = deductPreInvoiceService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                List<Long> preInvoiceIds = list.stream().map(TXfDeductPreInvoiceEntity::getPreInvoiceId).collect(Collectors.toList());
                // 查询业务单关联的预制发票数据
                List<TXfPreInvoiceEntity> preInvoiceList = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getId, preInvoiceIds).ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode()));
                DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = deductPreInvoiceService.getMakeInvoiceStatus(preInvoiceList);
                billMakeInvoiceStatusService.syncMakeInvoiceStatus(bill.getId(), makeInvoiceStatus);
                log.warn("新数据-业务单ID{},业务单号:{} ,结算单号: {}更新业务单开票状态为: {}", bill.getId(), bill.getBusinessNo(), settlement, makeInvoiceStatus);
                iterator.remove();
            }
        }

    }


}
