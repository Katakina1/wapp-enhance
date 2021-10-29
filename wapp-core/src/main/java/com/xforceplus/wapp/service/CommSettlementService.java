package com.xforceplus.wapp.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 申请-作废结算单预制发票（红字信息）
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
            tXfPreInvoiceEntityList.parallelStream().forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
                //调用沃尔玛 需要沃尔玛审核
                commRedNotificationService.applyDestroyRedNotification(tXfPreInvoiceEntity.getId());
            });
        }
        //预制发票
        //2、当预制发票申请ing红字信息编码时，直接作废沃尔玛申请单，预制发票改为没有红字信息状态
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper2 = new QueryWrapper<>();
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList2 = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper2);
        if (!CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            tXfPreInvoiceEntityList2.parallelStream().forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
                commRedNotificationService.deleteRedNotification(tXfPreInvoiceEntity.getId());
            });
        }

        if (CollectionUtils.isEmpty(tXfPreInvoiceEntityList) && CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            throw new EnhanceRuntimeException("结算单没有可撤销的预制发票(红字信息)");
        }

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlementId, OperateLogEnum.CANCEL_RED_NOTIFICATION_APPLY,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),
                UserUtil.getUserId(),UserUtil.getUserName());
    }

    /**
     * 驳回-作废结算单预制发票（红字信息）
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
        tXfPreInvoiceEntityList.parallelStream().forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        });

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlementId, OperateLogEnum.REJECT_CANCEL_RED_NOTIFICATION_APPLY,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),
                UserUtil.getUserId(),UserUtil.getUserName());
    }

    /**
     * 通过-作废结算单待审核的预制发票（红字信息）
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
        tXfPreInvoiceEntityList.parallelStream().forEach(tXfPreInvoiceEntity -> {
            destroyPreInvoice(tXfPreInvoiceEntity.getId());
        });

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlementId, OperateLogEnum.AGREE_CANCEL_RED_NOTIFICATION_APPLY,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),
                UserUtil.getUserId(),UserUtil.getUserName());
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
        TXfPreInvoiceEntity preInvoiceEntity = tXfPreInvoiceDao.selectById(preInvoiceId);
        if(preInvoiceEntity == null){
            throw new EnhanceRuntimeException("关联的预制发票不存在");
        }
        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setId(preInvoiceId);
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
        tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
        //调用沃尔玛撤销红字信息审批
        commRedNotificationService.applyDestroyRedNotification(preInvoiceId);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(preInvoiceEntity.getSettlementId());
        operateLogService.add(settlement.getId(), OperateLogEnum.CANCEL_RED_NOTIFICATION_APPLY,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),
                UserUtil.getUserId(),UserUtil.getUserName());
    }

    /**
     * 检查结算单是否能重新拆票（红字信息）
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
        if (destroyPreInvoiceCount > 0) {
            return true;
        }
        QueryWrapper<TXfPreInvoiceEntity> noRedPreInvoiceWrapper = new QueryWrapper<>();
        noRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        noRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        int noRedPreInvoiceCount = tXfPreInvoiceDao.selectCount(noRedPreInvoiceWrapper);
        //是否存在没有红字信息的预制发票
        if (noRedPreInvoiceCount > 0) {
            return true;
        }
        QueryWrapper<TXfPreInvoiceEntity> applyingRedPreInvoiceWrapper = new QueryWrapper<>();
        applyingRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        applyingRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
        int applyingRedPreInvoiceCount = tXfPreInvoiceDao.selectCount(applyingRedPreInvoiceWrapper);
        //是否正在申请红字
        //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件神申请败了，这个时候可以重新申请预制发票的红字信息)
        //是否有申请中的红字信息 或者 是否有审核通过
        boolean hasApplyWappRed = redNotificationOuterService.isWaitingApplyBySettlementNo(tXfSettlementEntity.getSettlementNo());
        if (applyingRedPreInvoiceCount > 0 && hasApplyWappRed) {
            throw new EnhanceRuntimeException("不能重新申请预制发票(红字信息)，【有正在申请或者已申请红字信息的预制发票】");
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
        tXfSettlementEntityList.parallelStream().forEach(tXfSettlementEntity -> {
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
        tXfSettlementEntityList.parallelStream().forEach(tXfSettlementEntity -> {
            rejectDestroySettlementPreInvoice(tXfSettlementEntity.getId());
        });
    }

    /**
     * 供应商调用
     * 结算单的预制发票（没有红字信息、作废状态）重新拆分预制发票（红字信息）
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
        //没有红字信息、作废
        List<Integer> againSplitStatusList = new ArrayList<>();
        againSplitStatusList.add(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        againSplitStatusList.add(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        // 查询（没有红字信息、作废）的预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceWrapper.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, againSplitStatusList);

        List<TXfPreInvoiceEntity> tXfPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceWrapper);
        List<Long> preInvoiceIdList = tXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(preInvoiceIdList)) {
            throw new EnhanceRuntimeException("结算单没有可申请的预制发票(红字信息)");
        }
        //查询（没有红字信息、作废）的预制发票明细
        QueryWrapper<TXfPreInvoiceItemEntity> preInvoiceItemWrapper = new QueryWrapper<>();
        preInvoiceItemWrapper.in(TXfPreInvoiceItemEntity.PRE_INVOICE_ID, preInvoiceIdList);
        List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntityList = tXfPreInvoiceItemDao.selectList(preInvoiceItemWrapper);
        //拆票-针对（没有红字信息、作废）的预制发票明细重新拆票
        preinvoiceService.reSplitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo(), tXfPreInvoiceItemEntityList);
        //删除结算单之前已作废的预制发票（没有红字信息、作废）避免申请逻辑状态判断问题
        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        tXfPreInvoiceDao.update(updateTXfPreInvoiceEntity, preInvoiceWrapper);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlement.getId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),
                UserUtil.getUserId(),UserUtil.getUserName());
    }
}
