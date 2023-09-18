package com.xforceplus.wapp.modules.log.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.deduct.service.BillQueryTabStatusConvert;
import com.xforceplus.wapp.modules.deduct.service.DeductPreInvoiceService;
import com.xforceplus.wapp.modules.log.model.QueryOperationLogResponse;
import com.xforceplus.wapp.modules.rednotification.mapstruct.IdGenerator;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfOperationLogDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.daoExt.TXfDeductPreInvoiceExtDao;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfDeductPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfOperationLogEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by SunShiyong on 2021/10/25.
 */

@Service
@Slf4j
public class OperateLogService {

    @Autowired
    private TXfOperationLogDao tXfOperationLogDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfBillDeductExtDao tXfBillDeductExtDao;
    @Autowired
    private TXfDeductPreInvoiceExtDao tXfDeductPreInvoiceExtDao;
    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private LockClient lockClient;

    public List<QueryOperationLogResponse> query(Long businessId, Long userId){
        QueryWrapper<TXfOperationLogEntity> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc(TXfOperationLogEntity.ID);
        if(businessId !=null){
            wrapper.eq(TXfOperationLogEntity.BUSINESS_ID,businessId);
        }
        if(userId != null){
            wrapper.eq(TXfOperationLogEntity.USER_ID,userId);
        }
        List<TXfOperationLogEntity> tXfOperationLogEntities = tXfOperationLogDao.selectList(wrapper);
        List<QueryOperationLogResponse> response = new ArrayList<>();
        BeanUtil.copyList(tXfOperationLogEntities,response,QueryOperationLogResponse.class);
        return response;
    }

    /**
     * 操作日志添加
     * @param businessId 业务id 结算单id  业务单id 或发票id
     * @param operateLogEnum 自定义操作枚举
     * @param  businessStatus  业务单状态
     * @param  desc  备注 如果有放在desc之后
     * @param  userId 用户id
     * @param  userName 用户名称
     */
    @Async
    public void add(Long businessId, OperateLogEnum operateLogEnum,String businessStatus, String desc ,Long userId, String userName){
        try {
            if(businessId == null ||  operateLogEnum ==null ){
                log.info("操作日志添加失败 businessId:{},operateLogEnum:{},businessStatus:{}",businessId,operateLogEnum,businessStatus);
                return;
            }
            TXfOperationLogEntity operationLogEntity = new TXfOperationLogEntity();
            operationLogEntity.setBusinessId(businessId);
            operationLogEntity.setBusinessStatus(businessStatus);
            operationLogEntity.setOperateDesc(StringUtils.isNotEmpty(desc) ? operateLogEnum.getOperateDesc()+desc : operateLogEnum.getOperateDesc());
            operationLogEntity.setOperateType(operateLogEnum.getOperateType());
            operationLogEntity.setOperateCode(operateLogEnum.getOperateCode());
            operationLogEntity.setCreateTime(new Date());
            operationLogEntity.setId(IdGenerator.generate());
            operationLogEntity.setUserId(userId);
            operationLogEntity.setUserName(userName);
            log.info("操作日志保存1:{}", JSON.toJSON(operationLogEntity));
            tXfOperationLogDao.insert(operationLogEntity);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    @Async
    public void addDeductLog(Long businessId, Integer businessType, TXfDeductStatusEnum deductStatusEnum, String settlementNo, DeductRedNotificationEventEnum eventEnum, Long userId, String userName) {
        String lockKey = "deduct_red_notification_log1:" + businessId + ":" + settlementNo + ":" + eventEnum;
        log.info("设置key1:{}", lockKey);
        if (lockClient.tryLock(lockKey)) {
            // 统计业务单下有多少预制发票 -- 剔除无需申请
            Integer count = tXfDeductPreInvoiceExtDao.selectCount(businessId, settlementNo);
            String cacheKey1 = lockKey + ":001";
            if (cacheClient.get(cacheKey1) == null && count > 1) {
                // 部分日志
                cacheClient.set(cacheKey1, businessId, 24 * 60 * 60);
                OperateLogEnum operateLogEnum = DeductRedNotificationEventEnum.APPLY_SUCCEED == eventEnum ? OperateLogEnum.SETTLEMENT_RED_NOTIFICATION_APPLY_PART_SUCCESS : OperateLogEnum.SETTLEMENT_RED_NOTIFICATION_CANCEL_PART_SUCCESS;
                addDeductLog(businessId, businessType, deductStatusEnum, settlementNo, operateLogEnum, "", userId, userName);
            }

            String cacheKey2 = lockKey + ":002";
            if (cacheClient.get(cacheKey2) == null) {
                // 查询业务单下红字信息表申请/撤销情况
                List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = tXfDeductPreInvoiceExtDao.selectList(businessId, settlementNo);

                // 申请成功数量， 撤销成功数量
                int applyNum = 0, cancelNum = 0;
                for (TXfDeductPreInvoiceEntity entity : tXfDeductPreInvoiceEntities) {
                    if (AgreementRedNotificationStatus.APPLIED.getValue() == entity.getApplyStatus()) {
                        applyNum++;
                    } else if (AgreementRedNotificationStatus.REVOKED.getValue() == entity.getApplyStatus()) {
                        cancelNum++;
                    }
                }
                OperateLogEnum operateLogEnum;
                int size = tXfDeductPreInvoiceEntities.size();
                // 全部已申请（包含蓝冲）
                boolean allApply = size == (applyNum + cancelNum) && applyNum > 0 && DeductRedNotificationEventEnum.APPLY_SUCCEED == eventEnum;
                // 全部已撤销
                boolean allCancel = size == cancelNum;
                log.info("businessId:{},settlementNo:{},allApply:{},allCancel:{}",businessId,settlementNo,allApply,allCancel);
                if (allApply || allCancel) {
                    // 全部申请/全部撤销
                    operateLogEnum = DeductPreInvoiceService.EVENT_LOG_RELATION.get(eventEnum);

                    cacheClient.set(cacheKey2, businessId, 24 * 60 * 60);
                    //生成日志
                    addDeductLog(businessId, businessType, deductStatusEnum, settlementNo, operateLogEnum, "", userId, userName);
                }
            }
        }
    }

    /**
     * 操作日志添加
     * @param businessId 业务单id
     * @param businessType 业务单类型
     * @param  deductStatusEnum  业务单状态
     * @param settlementNo 关联结算单号
     * @param operateLogEnum 自定义操作枚举
     * @param  desc  备注 如果有放在desc之后
     * @param  userId 用户id
     * @param  userName 用户名称
     */
    @Async
    public void addDeductLog(Long businessId, Integer businessType, TXfDeductStatusEnum deductStatusEnum, String settlementNo,
                             OperateLogEnum operateLogEnum, String desc, Long userId, String userName) {
        try {
            if(businessId == null || operateLogEnum == null || deductStatusEnum == null){
                log.info("操作日志添加失败 businessId:{},operateLogEnum:{},businessStatus:{}", businessId, operateLogEnum, deductStatusEnum);
                return;
            }
            // 查询关联结算单
            Integer settlementStatus = null;
            if (StringUtils.isNotBlank(settlementNo)) {
                QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, settlementNo);
                TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectOne(queryWrapper);
                settlementStatus = Optional.ofNullable(tXfSettlementEntity).map(TXfSettlementEntity::getSettlementStatus).orElse(null);
            }

            addDeductLog(businessType, operateLogEnum, desc, userId, userName, businessId, deductStatusEnum.getCode(), settlementStatus);
        } catch (Exception e) {
            log.error("addDeductLog error:{}-{},", settlementNo, e.getMessage(), e);
        }
    }

    /**
     * 根据结算单操作添加关联业务单日志
     * @param settlementNo 结算单id
     * @param businessType 业务单类型
     * @param settlementStatus 结算单状态
     * @param operateLogEnum 操作
     * @param desc 备注
     * @param userId 操作id
     * @param userName 操作名称
     */
    @Async
    public void addDeductLog(String settlementNo, Integer businessType, TXfSettlementStatusEnum settlementStatus, OperateLogEnum operateLogEnum, String desc, Long userId, String userName) {
        if (StringUtils.isBlank(settlementNo) || operateLogEnum == null) {
            log.info("操作日志添加失败 settlementNo:{},operateLogEnum:{}", settlementNo, operateLogEnum);
            return;
        }
        //查询批次下的业务单信息
        List<TXfBillDeductEntity> deductList = tXfBillDeductExtDao.queryBillBySettlementNoAndBusinessType(settlementNo, businessType);
        if (CollectionUtil.isNotEmpty(deductList)) {
            deductList.forEach(deduct -> {
                try {
                    addDeductLog(businessType, operateLogEnum, desc, userId, userName, deduct.getId(), deduct.getStatus(), settlementStatus.getCode());
                } catch (Exception e) {
                    log.error("addDeductLog error bySettlementNo:{}-{},", settlementNo, e.getMessage(), e);
                }
            });
        }
    }

    /**
     * @Description 操作写入
     * @Author pengtao
     * @return
    **/
    private void addDeductLog(Integer businessType, OperateLogEnum operateLogEnum, String desc, Long userId, String userName, Long id, Integer status, Integer code) {
        TXfOperationLogEntity operationLogEntity = new TXfOperationLogEntity();
        operationLogEntity.setBusinessId(id);
        operationLogEntity.setBusinessStatus(Optional.ofNullable(BillQueryTabStatusConvert.getQueryTab(businessType, status, code)).map(IQueryTab::message).orElse(""));
        operationLogEntity.setOperateDesc(StringUtils.isNotEmpty(desc) ? operateLogEnum.getOperateDesc() + desc : operateLogEnum.getOperateDesc());
        operationLogEntity.setOperateType(operateLogEnum.getOperateType());
        operationLogEntity.setOperateCode(operateLogEnum.getOperateCode());
        operationLogEntity.setCreateTime(new Date());
        operationLogEntity.setId(IdGenerator.generate());
        operationLogEntity.setUserId(userId);
        operationLogEntity.setUserName(userName);
        log.info("操作日志保存2:{}", JSON.toJSON(operationLogEntity));
        tXfOperationLogDao.insert(operationLogEntity);
    }

}
