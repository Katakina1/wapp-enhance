package com.xforceplus.wapp.modules.claim.service;

import com.aisinopdf.text.pdf.S;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.util.StringUtils;

import java.util.List;
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
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SALES_BILL_CODE, tXfSettlementEntity.getSettlementNo());
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


}
