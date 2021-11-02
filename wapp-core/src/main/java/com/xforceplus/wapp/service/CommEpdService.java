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
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private PreinvoiceService preinvoiceService;
    private final List<Integer> canDestroyStatus;

    public CommEpdService() {
        canDestroyStatus= Arrays.asList(
                TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode()
                ,TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode()
        );
    }

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
        if(!canDestroyStatus.contains(tXfSettlementEntity.getSettlementStatus())){
            throw new EnhanceRuntimeException("结算单已上传红票不能操作");
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
        //作废结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改EPD单状态
        billDeductList.parallelStream().forEach(billDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(billDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            updateTXfBillDeductEntity.setRefSettlementNo("");
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //作废预制发票
        Optional.ofNullable(pPreInvoiceList).ifPresent(x->x.parallelStream().forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        }));

        //释放结算单蓝票
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper();
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_NO, tXfSettlementEntity.getSettlementNo());
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfBillDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TDxRecordInvoiceEntity> tDxInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_CODE, tXfBillDeductInvoiceEntity.getInvoiceCode());
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_NO, tXfBillDeductInvoiceEntity.getInvoiceNo());
            TDxRecordInvoiceEntity tDxInvoiceEntity = tDxRecordInvoiceDao.selectOne(tDxInvoiceEntityQueryWrapper);
            if(tDxInvoiceEntity != null) {
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoiceEntity.getId());
                updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
                tDxRecordInvoiceDao.updateById(updateTDxInvoiceEntity);
            }
            //删除蓝票关系
            //释放索赔单蓝票额度（作废的索赔单）
            TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoiceEntity.getId());
            updateTXfBillDeductInvoiceEntity.setStatus(1);
            tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
        });
    }

    /**
     * 这个主要是针对作废的预制发票明细处理
     * 修改后的结算单的中的部分预制发票明细重新去拆票（申请红字信息），删除之前的预制发票
     *
     * @param settlementId
     * @param preInvoiceItemList
     */
    @Transactional
    public void againSplitPreInvoice(Long settlementId, List<TXfPreInvoiceItemEntity> preInvoiceItemList) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if(CollectionUtils.isEmpty(preInvoiceItemList)){
            throw new EnhanceRuntimeException("结算单无数据可拆分预制发票");
        }
        preinvoiceService.reSplitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo(), preInvoiceItemList);
        //删除之前的预制发票，避免申请逻辑状态判断问题
        List<Long> preInvoiceIdList = preInvoiceItemList.stream().map(TXfPreInvoiceItemEntity::getPreInvoiceId).collect(Collectors.toList());

        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());

        QueryWrapper<TXfPreInvoiceEntity> deletePreInvoiceWrapper = new QueryWrapper<>();
        deletePreInvoiceWrapper.in(TXfPreInvoiceEntity.ID,preInvoiceIdList);
        tXfPreInvoiceDao.update(updateTXfPreInvoiceEntity,deletePreInvoiceWrapper);
    }

    /**
     * EPD[确认]按钮相关逻辑，这个主要是针对结算单明细拆票
     * 结算单明细拆成预制发票（红字信息）
     * 底层逻辑调用产品服务(拆票、申请红字信息)
     * @param settlementId
     */
    @Transactional
    public void splitPreInvoice(Long settlementId) throws IOException {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
    }
}
