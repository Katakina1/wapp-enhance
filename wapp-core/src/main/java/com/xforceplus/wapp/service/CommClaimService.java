package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 索赔单相关公共逻辑操作
 * @author Xforce
 */
@Service
public class CommClaimService {

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
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 作废整个索赔单流程
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void destroyClaimSettlement(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //索赔单 查询待审核状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper1 = new QueryWrapper<>();
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
        List<TXfBillDeductEntity> billDeductList1 = tXfBillDeductDao.selectList(billDeductEntityWrapper1);

        //索赔单 查询已生成结算单状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper2 = new QueryWrapper<>();
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
        List<TXfBillDeductEntity> billDeductList2 = tXfBillDeductDao.selectList(billDeductEntityWrapper2);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改预制发票状态
        tXfPreInvoiceEntityList.parallelStream().forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改索赔单状态
        //申请中的索赔单修改为：作废
        billDeductList1.parallelStream().forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_DESTROY.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
            //日志
            operateLogService.add(tXfBillDeduct.getId(), OperateLogEnum.CANCEL_DEDUCT,
                    TXfDeductStatusEnum.CLAIM_DESTROY.getDesc(),
                    0L,"系统");
        });
        //已生成结算单的索赔单修改为：待生成结算单 清空结算单编号
        billDeductList2.parallelStream().forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
            updateTXfBillDeductEntity.setRefSettlementNo("");
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //释放索赔单明细额度（作废的索赔单）
        List<Long> billDeductIdList = billDeductList1.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
        QueryWrapper<TXfBillDeductItemRefEntity> billDeductItemRefEntityWrapper = new QueryWrapper<>();
        billDeductItemRefEntityWrapper.in(TXfBillDeductItemRefEntity.DEDUCT_ID, billDeductIdList);
        List<TXfBillDeductItemRefEntity> tXfBillDeductItemRefEntityList = tXfBillDeductItemRefDao.selectList(billDeductItemRefEntityWrapper);
        tXfBillDeductItemRefEntityList.forEach(tXfBillDeductItemRefEntity -> {
            TXfBillDeductItemEntity tXfBillDeductItemEntity = tXfBillDeductItemDao.selectById(tXfBillDeductItemRefEntity.getDeductItemId());
            //还原额度
            TXfBillDeductItemEntity updateTXfBillDeductItemEntity = new TXfBillDeductItemEntity();
            updateTXfBillDeductItemEntity.setId(tXfBillDeductItemEntity.getId());
            updateTXfBillDeductItemEntity.setRemainingAmount(tXfBillDeductItemRefEntity.getUseAmount().add(tXfBillDeductItemEntity.getRemainingAmount()));
            tXfBillDeductItemDao.updateById(updateTXfBillDeductItemEntity);
            //删除匹配关系
            TXfBillDeductItemRefEntity updateTXfBillDeductItemRefEntity = new TXfBillDeductItemRefEntity();
            updateTXfBillDeductItemRefEntity.setId(tXfBillDeductItemRefEntity.getId());
            updateTXfBillDeductItemRefEntity.setStatus(1);
            tXfBillDeductItemRefDao.updateById(updateTXfBillDeductItemRefEntity);
        });

        //释放索赔单蓝票（作废的索赔单）
        List<String> billDeductBusinessNoList = billDeductList1.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper();
        tXfBillDeductInvoiceWrapper.in(TXfBillDeductInvoiceEntity.BUSINESS_NO, billDeductBusinessNoList);
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfDeductInvoiceBusinessTypeEnum.CLAIM_BILL.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TDxRecordInvoiceEntity> tDxInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_CODE, tXfBillDeductInvoiceEntity.getInvoiceCode());
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_NO, tXfBillDeductInvoiceEntity.getInvoiceNo());
            TDxRecordInvoiceEntity tDxInvoiceEntity = tDxRecordInvoiceDao.selectOne(tDxInvoiceEntityQueryWrapper);
            if (tDxInvoiceEntity != null) {
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoiceEntity.getId());
                updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
                tDxRecordInvoiceDao.updateById(updateTDxInvoiceEntity);
            }
            //删除蓝票关系
            TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoiceEntity.getId());
            updateTXfBillDeductInvoiceEntity.setStatus(1);
            tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
        });
    }

    /**
     * 索赔单[确认]按钮相关逻辑，这个主要是针对结算单明细拆票
     * 结算单明细拆成预制发票（红字信息）
     * 底层逻辑调用产品服务(拆票、申请红字信息)
     *
     * @param settlementId
     */
    @Transactional(rollbackFor = Exception.class)
    public void splitPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
    }
}
