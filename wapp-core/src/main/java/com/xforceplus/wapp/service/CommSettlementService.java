package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 结算单公共逻辑
 * 1、撤销预制发票
 * 2、重新申请预制发票 拆票
 */
@Service
public class CommSettlementService {
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;

    /**
     * 申请-撤销结算单预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为待审核
     * 供应商调用
     *
     * @param settlementId
     * @return
     */
    @Transactional
    public void applyCancelSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if (tXfSettlementEntity.getSettlementStatus() == TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getCode()) {
            throw new EnhanceRuntimeException("已经开具红票");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        if (!CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            //1、当预制发票有红字信息编码时，申请撤销红字信息编码
            tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
            });
        } else {
            //2、当预制发票没有红字信息编码时，直接撤销发票
            tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.CANCEL.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
            });
            //TODO 撤销红字信息
        }

    }

    /**
     * 驳回-撤销结算单预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为待上传
     * 沃尔玛调用
     *
     * @param settlementId
     */
    @Transactional
    public void rejectCancelSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
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
    }

    /**
     * 通过-撤销结算单预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为已撤销，清空红字信息字段
     * 沃尔玛调用
     *
     * @param settlementId
     */
    @Transactional
    public void agreeCancelSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.CANCEL.getCode());
            updateTXfPreInvoiceEntity.setRedNotificationNo("");
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });
    }


    /**
     * 重新拆分结算订单下面的预制发票
     * 针对结算单下所有已撤销的预制发票发起重新申请，
     * 如果结算单下没有已撤销的预制发票，
     * 进一步判断正常状态（非待审核、非已撤销）的预制发票是否存在红字信息编码，
     * 如果不存在并且沃尔玛侧也没有待处理的红字信息表待审请记录，则表明本结算单在第一次拆票后自动申请红字信息表失败了，
     * 或没有通过中心侧审核，此时允许供应商重新发起红字信息表申请，前提是必须先确认限额信息；
     * 1、获取结算单明细
     * 2、重新拆票
     * 3、新的预制发票去申请红字信息
     * 供应商调用
     *
     * @param settlementId
     */
    @Transactional
    public void againSplitSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if (TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode() != tXfSettlementEntity.getSettlementStatus()) {
            throw new EnhanceRuntimeException("结算单目前不是待开票");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        boolean hasCancelSettlementPreInvoice = tXfPreInvoiceEntityList.stream()
                .anyMatch(tXfPreInvoice -> tXfPreInvoice.getPreInvoiceStatus() == TXfPreInvoiceStatusEnum.CANCEL.getCode());
        if (!hasCancelSettlementPreInvoice) {
            //如果正常发票没有红字信息
            boolean hasNoApplyRedSettlementPreInvoice = tXfPreInvoiceEntityList.stream()
                    .anyMatch(tXfPreInvoice -> tXfPreInvoice.getPreInvoiceStatus() == TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
            //TODO 需要判断结算单是否在沃尔玛有待审核状态
            boolean hasApplyWappRed = false;
            if (!hasNoApplyRedSettlementPreInvoice && !hasApplyWappRed) {
                throw new EnhanceRuntimeException("不能拆票");
            }
        }
        //结算单明细
        QueryWrapper<TXfSettlementItemEntity> settlementItemEntityWrapper = new QueryWrapper<>();
        settlementItemEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfSettlementItemEntity> tXfSettlementItemEntityList = tXfSettlementItemDao.selectList(settlementItemEntityWrapper);
        //TODO 拆票

        //TODO 申请红字信息
    }

    /**
     * 通过预制发票id查询结算单 然后撤销结算单下面的预制发票
     * 沃尔玛调用
     * @param preInvoiceIdList 预制发票id
     */
    public void cancelSettlementPreInvoiceByPreInvoiceId(List<Long> preInvoiceIdList) {
        if(CollectionUtils.isEmpty(preInvoiceIdList)){
            throw new EnhanceRuntimeException("参数异常");
        }
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectBatchIds(preInvoiceIdList);
        if(CollectionUtils.isEmpty(tXfPreInvoiceEntityList)){
            throw new EnhanceRuntimeException("预制发票不存在");
        }
        List<String> settlementNoList = tXfPreInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getSettlementNo).distinct().collect(Collectors.toList());
        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TXfSettlementEntity.SETTLEMENT_NO,settlementNoList);
        List<TXfSettlementEntity>  tXfSettlementEntityList = tXfSettlementDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(tXfSettlementEntityList)){
            throw new EnhanceRuntimeException("预制发票没有对应的结算单数据");
        }
        //撤销结算单
        tXfSettlementEntityList.forEach(tXfSettlementEntity -> {
            agreeCancelSettlementPreInvoice(tXfSettlementEntity.getId());
        });
    }


}
