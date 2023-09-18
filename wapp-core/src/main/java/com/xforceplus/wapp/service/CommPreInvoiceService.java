package com.xforceplus.wapp.service;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.enums.RedNoEventTypeEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;

import java.util.Date;
import java.util.Objects;

import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.ClaimSplitAgainCallable;
import com.xforceplus.wapp.threadpool.callable.SettlementDestroyCallable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 公共的预制发票逻辑操作
 */
@Service
@Slf4j
public class CommPreInvoiceService {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private RedNotificationMainService redNotificationMainService;
    @Autowired
    private LockClient lockClient;
    @Autowired
    private CommClaimService commClaimService;
    @Autowired
    private CommSettlementService commSettlementService;
    @Autowired
    private CommonMessageService commonMessageService;

    /**
     * 回填红字信息
     *
     * @param preInvoiceId
     * @param redNotification
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void fillPreInvoiceRedNotification(Long preInvoiceId, String redNotification) {
        if (preInvoiceId == null || StringUtils.isBlank(redNotification)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //修改预制发票表
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setId(preInvoiceId);
        tXfPreInvoiceEntity.setRedNotificationNo(redNotification);
        tXfPreInvoiceEntity.setUpdateTime(new Date());
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity);
    }

    /**
     * 沃尔玛申请红字信息失败
     * @param preInvoiceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyPreInvoiceRedNotificationFail(Long preInvoiceId) {
        if (preInvoiceId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //修改预制发票表
        TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        tXfPreInvoiceEntity.setId(preInvoiceId);
        tXfPreInvoiceEntity.setUpdateTime(new Date());
        tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        tXfPreInvoiceDao.updateById(tXfPreInvoiceEntity);
    }


    /**
     * 红字信息表撤销成功 需作废相关预制发票并发起结算单重新拆票/撤销
     * @param redNoId 红字信息表id
     */
    public void destroyPreInvoiceAndNext(Long redNoId, String eventType) {
        log.info("红字信息表撤销成功:[{}]", redNoId);
        if (!CommonUtil.isEdit(redNoId)) {
            return;
        }
        TXfRedNotificationEntity redNotificationEntity = redNotificationMainService.getById(redNoId);
        if (Objects.isNull(redNotificationEntity)) {
            return;
        }
        // 查询预制发票
        TXfPreInvoiceEntity tXfPreInvoiceEntity = tXfPreInvoiceDao.selectById(redNotificationEntity.getPid());
        // 更新预制发票为作废状态。同时更新红字信息表编号为空
        TXfPreInvoiceEntity preInvoiceEntityU = new TXfPreInvoiceEntity();
        preInvoiceEntityU.setId(tXfPreInvoiceEntity.getId());
        preInvoiceEntityU.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        preInvoiceEntityU.setRedNotificationNo("");
        int count = tXfPreInvoiceDao.updateById(preInvoiceEntityU);
        if (count == 1) {
            //TODO 事件消费代码已经注释了
            commonMessageService.sendPreInvoiceDiscardMessage(Lists.newArrayList(tXfPreInvoiceEntity.getId()));

            if (RedNoEventTypeEnum.SPLIT_AGAIN.code().equals(eventType)) {
                // 更新成功  异步处理索赔结算单重新拆票
                ClaimSplitAgainCallable claimSplitAgainCallable = new ClaimSplitAgainCallable(tXfPreInvoiceEntity, lockClient, commClaimService);
                ThreadPoolManager.submitCustomL1(claimSplitAgainCallable);

            } else if (RedNoEventTypeEnum.DESTROY_SETTLEMENT.code().equals(eventType)) {
                // 更新成功  异步处理结算单撤销
                SettlementDestroyCallable settlementDestroyCallable = new SettlementDestroyCallable(tXfPreInvoiceEntity, lockClient, commSettlementService);
                ThreadPoolManager.submitCustomL1(settlementDestroyCallable);
            }
        }
    }
}
