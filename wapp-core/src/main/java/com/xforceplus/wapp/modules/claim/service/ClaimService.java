package com.xforceplus.wapp.modules.claim.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.service.CommClaimService;
import com.xforceplus.wapp.service.CommRedNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
public class ClaimService {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private TXfBillDeductItemRefDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private CommClaimService commClaimService;

    /**
     * 申请索赔单不定案
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    @Transactional
    public void applyClaimVerdict(Long settlementId, List<Long> billDeductIdList) {
        if (settlementId == null || CollectionUtils.isEmpty(billDeductIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if (tXfSettlementEntity.getSettlementStatus() == TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode()) {
            throw new EnhanceRuntimeException("已经开具红票");
        }
        //索赔单
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectBatchIds(billDeductIdList);
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(wrapper);

        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CHECK.getCode());

        //修改索赔单状态
        billDeductList.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //TODO 需要将数据放入到问题列表清单(关联一期)

    }

    /**
     * 驳回申请索赔单不定案
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional
    public void rejectClaimVerdict(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //索赔单 查询待审核状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper.eq(TXfBillDeductEntity.STATUS, TXfBillDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());

        //修改索赔单状态
        billDeductList.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

    }

    /**
     * 通过申请索赔单不定案
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional
    public void agreeClaimVerdict(Long settlementId) {
        commClaimService.cancelClaimSettlement(settlementId);
    }

    /**
     * 通过索赔单id申请不定案 需要将所理赔单分组（根据结算单分组）
     * 供应商调用
     *
     * @param billDeductIdList 预制发票id
     */
    public void applyClaimVerdictByBillDeductId(List<Long> billDeductIdList) {
        if (CollectionUtils.isEmpty(billDeductIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        List<TXfBillDeductEntity> tXfBillDeductEntityList = tXfBillDeductDao.selectBatchIds(billDeductIdList);
        if (CollectionUtils.isEmpty(tXfBillDeductEntityList)) {
            throw new EnhanceRuntimeException("索赔单不存在");
        }
        List<String> settlementNoList = tXfBillDeductEntityList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TXfSettlementEntity.SETTLEMENT_NO, settlementNoList);
        List<TXfSettlementEntity> tXfSettlementEntityList = tXfSettlementDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tXfSettlementEntityList)) {
            throw new EnhanceRuntimeException("索赔单没有对应的结算单数据");
        }
        //分组处理不定案
        Map<String, List<TXfBillDeductEntity>> settlementIdToBillDeductMap = tXfBillDeductEntityList.stream().collect(Collectors.groupingBy(TXfBillDeductEntity::getRefSettlementNo));
        //<结算单编号,结算单id>
        Map<String, Long> settlementIdToNoMap = tXfSettlementEntityList.stream().collect(Collectors.toMap(TXfSettlementEntity::getSettlementNo, TXfSettlementEntity::getId));
        //<结算单id,Array<索赔单id列表>>
        Map<Long, List<Long>> settlementIdToBillDeductIdMap = Maps.newHashMap();
        settlementIdToBillDeductMap.entrySet().forEach(entry -> {
            Long settlementId = settlementIdToNoMap.get(entry.getKey());
            settlementIdToBillDeductIdMap.put(settlementId, entry.getValue().stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList()));
        });
        settlementIdToBillDeductIdMap.entrySet().forEach(entry -> {
            applyClaimVerdict(entry.getKey(), entry.getValue());
        });


    }


}
