package com.xforceplus.wapp.modules.claim.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfBillDeductStatusEnum;
import com.xforceplus.wapp.enums.XfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.XfSettlementStatusEnum;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

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


    /**
     * 申请索赔单不定案
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    @Transactional
    public boolean applyClaimVerdict(Long settlementId, List<Long> billDeductIdList) {
        if (settlementId == null || CollectionUtils.isEmpty(billDeductIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
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
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(XfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(XfSettlementStatusEnum.WAIT_CHECK.getCode());

        //修改索赔单状态
        billDeductList.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfBillDeductStatusEnum.WAIT_CHECK.getCode());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });
        return true;
    }

    /**
     * 驳回申请索赔单不定案
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean rejectClaimVerdict(Long settlementId, List<Long> billDeductIdList) {
        return true;
    }

    /**
     * 通过申请索赔单不定案
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean agreeClaimVerdict(Long settlementId, List<Long> billDeductIdList) {
        return true;
    }

    /**
     * 作废结算单 结算单不能再次匹配 明细释放额度 蓝票释放额度
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean repealClaimSettlement(Long settlementId, List<Long> billDeductIdList) {
        return true;
    }

    /**
     * 撤销结算单 结算单能再次匹配 明细释放额度 蓝票释放额度
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    public boolean backOutClaimSettlement(Long settlementId, List<Long> billDeductIdList) {
        return true;
    }


}
