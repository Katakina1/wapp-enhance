package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductInvoiceBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * epd 通用逻辑操作
 */
@Service
public class CommEpdService {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TDxInvoiceDao tDxInvoiceDao;
    @Autowired
    private PreinvoiceService preinvoiceService;

    /**
     * 作废EPD单 作废结算单 蓝票释放额度 如果有预制发票 作废预制发票
     * EPD单还可以再次使用
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional
    public void destroyEpdSettlement(Long settlementId) {
        if (settlementId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }

        //EPD单
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> pPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改作废状态====
        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());

        //修改EPD单状态
        billDeductList.forEach(billDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(billDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //作废预制发票
        pPreInvoiceList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
            // 作废红字信息
            commRedNotificationService.confirmDestroyRedNotification(tXfPreInvoiceEntity.getId());
        });

        //释放结算单蓝票
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper();
        tXfBillDeductInvoiceWrapper.in(TXfBillDeductInvoiceEntity.BUSINESS_NO, tXfSettlementEntity.getSettlementNo());
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TDxInvoiceEntity> tDxInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tDxInvoiceEntityQueryWrapper.eq(TDxInvoiceEntity.INVOICE_CODE, tXfBillDeductInvoiceEntity.getInvoiceCode());
            tDxInvoiceEntityQueryWrapper.eq(TDxInvoiceEntity.INVOICE_NO, tXfBillDeductInvoiceEntity.getInvoiceNo());
            TDxInvoiceEntity tDxInvoiceEntity = tDxInvoiceDao.selectOne(tDxInvoiceEntityQueryWrapper);

            TDxInvoiceEntity updateTDxInvoiceEntity = new TDxInvoiceEntity();
            updateTDxInvoiceEntity.setId(tDxInvoiceEntity.getId());
            updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
            tDxInvoiceDao.updateById(updateTDxInvoiceEntity);
        });
        //删除结算单蓝票关系
        tXfBillDeductInvoiceDao.delete(tXfBillDeductInvoiceWrapper);
    }

    /**
     * 修改后的结算单的中的部门预制发票明细重新去拆票
     * 删除之前的预制发票
     *
     * @param settlementId
     * @param preInvoiceItemList
     */
    public void againSplitPreInvoice(Long settlementId, List<TXfPreInvoiceItemEntity> preInvoiceItemList) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        preinvoiceService.reSplitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo(), preInvoiceItemList);
        //删除之前的预制发票，避免申请逻辑状态判断问题
        List<Long> preInvoiceIdList = preInvoiceItemList.stream().map(TXfPreInvoiceItemEntity::getPreInvoiceId).collect(Collectors.toList());
        tXfPreInvoiceDao.deleteBatchIds(preInvoiceIdList);
        QueryWrapper<TXfPreInvoiceItemEntity> preInvoiceItemWrapper = new QueryWrapper<>();
        preInvoiceItemWrapper.in(TXfPreInvoiceItemEntity.PRE_INVOICE_ID, preInvoiceIdList);
        tXfPreInvoiceItemDao.delete(preInvoiceItemWrapper);
    }
}
