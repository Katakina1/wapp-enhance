package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 结算单公共逻辑
 * 1、作废预制发票
 * 2、重新申请预制发票 拆票
 */
@Service
public class CommSettlementService {
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private RedNotificationOuterService redNotificationOuterService;

    /**
     * 申请-作废结算单预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为待审核
     * 供应商调用
     *
     * @param settlementId
     * @return
     */
    @Transactional
    public void applyDestroySettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //预制发票
        //1、当预制发票有红字信息编码时，申请作废红字信息编码，需要沃尔玛审核
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        if (!CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
                //调用沃尔玛 需要沃尔玛审核
                commRedNotificationService.applyDestroyRedNotification(tXfPreInvoiceEntity.getId());
            });
            return;
        }
        //预制发票
        //2、当预制发票没有红字信息编码时，直接作废发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper2 = new QueryWrapper<>();
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList2 = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper2);
        if (!CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            tXfPreInvoiceEntityList2.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
            });
        }
        if (CollectionUtils.isEmpty(tXfPreInvoiceEntityList) || CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            throw new EnhanceRuntimeException("结算单没有可撤销的预制发票(红字信息)");
        }
    }

    /**
     * 驳回-作废结算单预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为待上传
     * 沃尔玛调用
     *
     * @param settlementId
     */
    @Transactional
    public void rejectDestroySettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
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
     * 通过-作废结算单待审核的预制发票
     * 1、结算单状态不变
     * 2、预制发票状态改为已作废，清空红字信息字段
     * 沃尔玛调用
     *
     * @param settlementId
     */
    @Transactional
    public void agreeDestroySettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            destroyPreInvoice(tXfPreInvoiceEntity.getId());
        });
    }

    /**
     * 直接作废预制发票，但是不能作废红字信息（这个时候主要给蓝冲使用的）
     *
     * @param preInvoiceId
     */
    @Transactional
    public void destroyPreInvoice(Long preInvoiceId) {
        if (preInvoiceId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setId(preInvoiceId);
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
    }

    /**
     * 申请作废预制发票,同时申请废红字信息
     * （这个时候主要给删除红票后 再删除红字信息使用）
     *
     * @param preInvoiceId
     */
    @Transactional
    public void applyDestroyPreInvoiceAndRedNotification(Long preInvoiceId) {
        if (preInvoiceId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setId(preInvoiceId);
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
        tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        commRedNotificationService.applyDestroyRedNotification(preInvoiceId);
    }

    /**
     * 检查结算单是否能重新拆票 申请红字
     * 如果不能则会抛出异常提示 调用方捕获异常处理相关后续逻辑
     *
     * @param settlementId
     * @return
     */
    public boolean checkAgainSplitSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> destroyPreInvoiceWrapper = new QueryWrapper<>();
        destroyPreInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        destroyPreInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.DESTROY.getCode());
        int destroyPreInvoiceCount = tXfPreInvoiceDao.selectCount(destroyPreInvoiceWrapper);
        //是否有作废的数据
        if (destroyPreInvoiceCount == 0) {
            //是否存在没有红字信息的预制发票
            QueryWrapper<TXfPreInvoiceEntity> noRedPreInvoiceWrapper = new QueryWrapper<>();
            noRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
            noRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
            int noRedPreInvoiceCount = tXfPreInvoiceDao.selectCount(noRedPreInvoiceWrapper);
            //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件神奇失败了，这个时候可以重新申请预制发票的红字信息)
            //是否有申请中的红字信息 或者 是否有审核通过
            boolean hasApplyWappRed = redNotificationOuterService.isWaitingApplyBySettlementNo(tXfSettlementEntity.getSettlementNo());
            if (noRedPreInvoiceCount == 0 && !hasApplyWappRed) {
                throw new EnhanceRuntimeException("不能重新申请预制发票(红字信息)");
            }
        }
        return true;
    }

    /**
     * 通过预制发票id查询结算单 然后同意作废结算单下面的预制发票
     * 多选预制发票去作废
     * 沃尔玛调用 同意作废红字的时候
     *
     * @param preInvoiceIdList 预制发票id
     */
    @Transactional
    public void agreeDestroySettlementPreInvoiceByPreInvoiceId(List<Long> preInvoiceIdList) {
        if (CollectionUtils.isEmpty(preInvoiceIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectBatchIds(preInvoiceIdList);
        if (CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            throw new EnhanceRuntimeException("预制发票不存在");
        }
        if (tXfPreInvoiceEntityList.size() != preInvoiceIdList.size()) {
            throw new EnhanceRuntimeException("预制发票缺失");
        }
        List<Long> settlementIdList = tXfPreInvoiceEntityList.stream()
                .map(TXfPreInvoiceEntity::getId).distinct().collect(Collectors.toList());
        List<TXfSettlementEntity> tXfSettlementEntityList = tXfSettlementDao.selectBatchIds(settlementIdList);
        if (CollectionUtils.isEmpty(tXfSettlementEntityList)) {
            throw new EnhanceRuntimeException("预制发票没有对应的结算单数据");
        }
        //作废待审核的预制发票
        tXfSettlementEntityList.forEach(tXfSettlementEntity -> {
            agreeDestroySettlementPreInvoice(tXfSettlementEntity.getId());
        });
    }

    /**
     * 通过预制发票id查询结算单 然后驳回作废结算单下面的预制发票
     * 多选预制发票 拒绝作废
     * 沃尔玛调用 驳回作废红字的时候
     *
     * @param preInvoiceIdList 预制发票id
     */
    @Transactional
    public void rejectDestroySettlementPreInvoiceByPreInvoiceId(List<Long> preInvoiceIdList) {
        if (CollectionUtils.isEmpty(preInvoiceIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectBatchIds(preInvoiceIdList);
        if (CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            throw new EnhanceRuntimeException("预制发票不存在");
        }
        if (tXfPreInvoiceEntityList.size() != preInvoiceIdList.size()) {
            throw new EnhanceRuntimeException("预制发票缺失");
        }
        List<Long> settlementIdList = tXfPreInvoiceEntityList.stream()
                .map(TXfPreInvoiceEntity::getSettlementId).distinct().collect(Collectors.toList());
        List<TXfSettlementEntity> tXfSettlementEntityList = tXfSettlementDao.selectBatchIds(settlementIdList);
        if (CollectionUtils.isEmpty(tXfSettlementEntityList)) {
            throw new EnhanceRuntimeException("预制发票没有对应的结算单数据");
        }
        //作废结算单
        tXfSettlementEntityList.forEach(tXfSettlementEntity -> {
            rejectDestroySettlementPreInvoice(tXfSettlementEntity.getId());
        });
    }

    /**
     * 这里主要是索赔单使用
     * 结算单重新申请预制发票（针对结算单下所有已作废的预制发票发起重新申请）
     * 针对结算单下所有已作废的预制发票发起重新申请，
     * 如果结算单下没有已作废的预制发票，
     * 进一步判断正常状态（非待审核、非已作废）的预制发票是否存在红字信息编码，
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
        //检查结算单是否能重新拆票
        checkAgainSplitSettlementPreInvoice(settlementId);
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //查询作废的预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.DESTROY.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceWrapper);
        List<Long> preInvoiceIdList = tXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        //查询作废的预制发票明细
        QueryWrapper<TXfPreInvoiceItemEntity> preInvoiceItemWrapper = new QueryWrapper<>();
        preInvoiceItemWrapper.in(TXfPreInvoiceItemEntity.PRE_INVOICE_ID, preInvoiceIdList);
        List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntityList = tXfPreInvoiceItemDao.selectList(preInvoiceItemWrapper);
        //拆票（针对已作废的预制发票明细重新拆票）
        preinvoiceService.reSplitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo(), tXfPreInvoiceItemEntityList);
        //删除结算单之前已作废的预制发票（作废了）避免申请逻辑状态判断问题
        tXfPreInvoiceDao.deleteBatchIds(preInvoiceIdList);
        tXfPreInvoiceItemDao.delete(preInvoiceItemWrapper);
    }
}
