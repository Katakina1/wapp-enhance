package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductInvoiceBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 索赔单相关公共逻辑操作
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
    private TXfInvoiceDao tXfInvoiceDao;;

    /**
     * 作废整个索赔单流程
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional
    public void destroyClaimSettlement(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if(!Objects.equals(tXfSettlementEntity.getSettlementStatus(),TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode())){
            throw new EnhanceRuntimeException("结算单已上传红票不能操作");
        }
        //索赔单 查询待审核状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper1 = new QueryWrapper<>();
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.STATUS, TXfBillDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
        List<TXfBillDeductEntity> billDeductList1 = tXfBillDeductDao.selectList(billDeductEntityWrapper1);

        //索赔单 查询已生成结算单状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper2 = new QueryWrapper<>();
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.STATUS, TXfBillDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
        List<TXfBillDeductEntity> billDeductList2 = tXfBillDeductDao.selectList(billDeductEntityWrapper2);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());

        //修改索赔单状态
        //申请中的索赔单修改为：作废
        billDeductList1.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_DESTROY.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });
        //已生成结算单的索赔单修改为：待生成结算单 清空结算单编号
        billDeductList2.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
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
            tXfBillDeductItemRefDao.deleteById(tXfBillDeductItemRefEntity.getId());
        });

        //释放索赔单蓝票（作废的索赔单）
        List<String> billDeductBusinessNoList = billDeductList1.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper();
        tXfBillDeductInvoiceWrapper.in(TXfBillDeductInvoiceEntity.BUSINESS_NO, billDeductBusinessNoList);
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfBillDeductInvoiceBusinessTypeEnum.CLAIM_BILL.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TXfInvoiceEntity> tXfInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tXfInvoiceEntityQueryWrapper.eq(TDxInvoiceEntity.INVOICE_CODE,tXfBillDeductInvoiceEntity.getInvoiceCode());
            tXfInvoiceEntityQueryWrapper.eq(TDxInvoiceEntity.INVOICE_NO,tXfBillDeductInvoiceEntity.getInvoiceNo());
            TXfInvoiceEntity tXfInvoiceEntity = tXfInvoiceDao.selectOne(tXfInvoiceEntityQueryWrapper);

            TXfInvoiceEntity updateTXfInvoiceEntity = new TXfInvoiceEntity();
            updateTXfInvoiceEntity.setId(tXfInvoiceEntity.getId());
            updateTXfInvoiceEntity.setRemainingAmount(tXfInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
            tXfInvoiceDao.updateById(updateTXfInvoiceEntity);
        });

        //删除蓝票关系
        //释放索赔单蓝票额度（作废的索赔单）
        tXfBillDeductInvoiceDao.delete(tXfBillDeductInvoiceWrapper);
    }
}
