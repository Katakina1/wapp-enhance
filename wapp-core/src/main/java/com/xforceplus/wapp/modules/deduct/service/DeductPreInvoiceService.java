package com.xforceplus.wapp.modules.deduct.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreBillDetailService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.dto.DeductSettlementItemRefDto;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 协议单：预制发票明细找到结算单明细（t_xf_pre_bill_detail胡锦涛）--》结算单明细找发票明细关系（t_xf_settlement_item_invoice_detail 王先瑞）--》发票明细找协议单主信息（t_xf_bill_deduct_invoice_detail 王先瑞）
 * t_xf_pre_bill_detail N----1 t_xf_settlement_item_invoice_detail
 * t_xf_settlement_item_invoice_detail  N----N t_xf_bill_deduct_invoice_detail
 * <p>
 * <p>
 * <p>
 * 索赔单：预制发票明细找到结算单明细（t_xf_pre_bill_detail胡锦涛）--》结算单明细找索赔单明细（t_xf_settlement_item）--》索赔单明细找索赔单主信息（t_xf_bill_deduct_item_ref）
 * t_xf_pre_bill_detail N----1 t_xf_settlement_item
 * t_xf_settlement_item 1----1 t_xf_bill_deduct_item_ref N---1  t_xf_bill_deduct_item
 *
 * @author malong@xforceplus.com
 * @program wapp-enhance
 * @description
 * @create 2022-09-14 15:07
 **/
@SuppressWarnings("AlibabaTransactionMustHaveRollback")
@Service
@Slf4j
public class DeductPreInvoiceService extends ServiceImpl<TXfDeductPreInvoiceDao, TXfDeductPreInvoiceEntity> {

    @Autowired
    private PreBillDetailService preBillDetailService;

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;

    @Autowired
    private TXfSettlementExtDao tXfSettlementExtDao;

    @Autowired
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefExtDao;

    @Autowired
    private TXfBillDeductInvoiceDetailDao tXfBillDeductInvoiceDetailDao;

    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;

    @Autowired
    private TXfRedNotificationDao tXfRedNotificationDao;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    private IDSequence idSequence;

    @Autowired
    private TXfPreInvoiceDao preInvoiceDao;

    @Autowired
    private CommonMessageService commonMessageService;

    @Autowired
    private BillMakeInvoiceStatusService billMakeInvoiceStatusService;

    public static final Map<DeductRedNotificationEventEnum, OperateLogEnum> EVENT_LOG_RELATION = new HashMap<>();

    static {
        EVENT_LOG_RELATION.put(DeductRedNotificationEventEnum.APPLY_SUCCEED, OperateLogEnum.SETTLEMENT_RED_NOTIFICATION_APPLY_SUCCESS);
        EVENT_LOG_RELATION.put(DeductRedNotificationEventEnum.REVOCATION_SUCCEED, OperateLogEnum.SETTLEMENT_RED_NOTIFICATION_CANCEL_SUCCESS);
    }

    public void pushBySettlementNo(String settlementNo) {
        LambdaQueryWrapper<TXfSettlementEntity> queryWrapper = Wrappers.lambdaQuery(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, settlementNo);
        TXfSettlementEntity settlementEntity = tXfSettlementExtDao.selectOne(queryWrapper);
        Asserts.isNull(settlementEntity, "结算单信息不存在");

        // 查询预制发票
        List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).eq(TXfPreInvoiceEntity::getSettlementId, settlementEntity.getId()).ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode()));
        Asserts.isEmpty(preInvoiceEntityList, "结算单暂未生成预制发票");

        List<TXfDeductPreInvoiceEntity> deductPreInvoiceEntityList = this.list(Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class).in(TXfDeductPreInvoiceEntity::getPreInvoiceId, preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList())));
        List<Long> existsPreInvoiceIdList = Optional.ofNullable(deductPreInvoiceEntityList).orElse(Lists.newArrayList()).stream().map(TXfDeductPreInvoiceEntity::getPreInvoiceId).distinct().collect(Collectors.toList());

        List<DeductRedNotificationEvent.DeductRedNotificationModel> applySuccessList = Lists.newArrayList();
        List<DeductRedNotificationEvent.DeductRedNotificationModel> discardList = Lists.newArrayList();
        for (TXfPreInvoiceEntity preInvoiceEntity : preInvoiceEntityList) {
            if (existsPreInvoiceIdList.contains(preInvoiceEntity.getId())) {
                continue;
            }
            try {
                DeductRedNotificationEvent.DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationEvent.DeductRedNotificationModel();
                deductRedNotificationModel.setPreInvoiceId(preInvoiceEntity.getId());
                deductRedNotificationModel.setApplyRequired(preInvoiceEntity.getTaxRate().compareTo(BigDecimal.ZERO) != 0);
                // 创建事件
                commonMessageService.sendMessage(DeductRedNotificationEventEnum.PRE_INVOICE_CREATED, deductRedNotificationModel);

                if (StringUtils.isNotEmpty(preInvoiceEntity.getRedNotificationNo())) {
                    deductRedNotificationModel.setRedNotificationNo(preInvoiceEntity.getRedNotificationNo());
                    TXfRedNotificationEntity tXfRedNotificationEntity = tXfRedNotificationDao.selectOne(Wrappers.lambdaQuery(TXfRedNotificationEntity.class).eq(TXfRedNotificationEntity::getRedNotificationNo, preInvoiceEntity.getRedNotificationNo()));
                    deductRedNotificationModel.setRedNotificationId(tXfRedNotificationEntity.getId());
                    applySuccessList.add(JSON.parseObject(JSON.toJSONString(deductRedNotificationModel), DeductRedNotificationEvent.DeductRedNotificationModel.class));
                }
                if (TXfPreInvoiceStatusEnum.DESTROY.getCode().equals(preInvoiceEntity.getPreInvoiceStatus())
                        || TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode().equals(preInvoiceEntity.getPreInvoiceStatus())) {
                    discardList.add(JSON.parseObject(JSON.toJSONString(deductRedNotificationModel), DeductRedNotificationEvent.DeductRedNotificationModel.class));
                }
            } catch (Exception e) {
                log.error("push error:{}", preInvoiceEntity.getId(), e);
            }
        }

        if (CollectionUtil.isNotEmpty(applySuccessList)) {
            try {
                Thread.sleep(1000);
                applySuccessList.forEach(model -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.APPLY_SUCCEED, model));
            } catch (Exception e) {
                log.error("push apply succeed error:", e);
            }
        }

        if (CollectionUtil.isNotEmpty(discardList)) {
            try {
                Thread.sleep(1000);
                discardList.forEach(model -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.PRE_INVOICE_DISCARD, model));
            } catch (Exception e) {
                log.error("push discard error:", e);
            }
        }

    }

    @Transactional
    public void consume(DeductRedNotificationEvent event) {
        log.info("event:{}", JSON.toJSONString(event));
        DeductRedNotificationEvent.DeductRedNotificationModel body = event.getBody();

        switch (event.getEvent()) {
            case PRE_INVOICE_CREATED:
                this.add(body.getPreInvoiceId(), body.getApplyRequired(), event.getTimestamp());
                break;
            case APPLY_NOTIFICATION:
            case REVOCATION_APPLY:
            case APPLY_FAILED:
            case REVOCATION_FAILED:
            case REVOCATION_SUCCEED:
            case REVOCATION_REJECTED:
                log.info("更新业务单预制发票:{}关联表状态:{}", body.getPreInvoiceId(), event.getEvent());
                updateStatus(body, event.getEvent(), event.getTimestamp());
                break;
            case APPLY_SUCCEED:
                log.info("红字信息申请成功，回填红字信息");
                updateStatusAndRedNotificationInfo(body, event.getEvent(), event.getTimestamp());
                break;
            case PRE_INVOICE_DISCARD:
                log.info("预制发票作废:{}", body.getPreInvoiceId());
                discard(body.getPreInvoiceId(), event.getTimestamp());
                break;
            case PRE_INVOICE_DELETE:
                log.info("预制发票删除:{}", body.getPreInvoiceId());
                delete(body.getPreInvoiceId(), event.getTimestamp());
                break;
            case UPLOAD_RED_INVOICE:
                break;
            case DELETE_RED_INVOICE:
                break;
            default:
                log.info("事件类型不匹配");
        }
        // 刷新业务单开票状态
        uploadAndUpdateStatus(body.getPreInvoiceId(), event.getEvent());
        //生成日志
        invokeOperationLog(body, event.getEvent());
    }

    /**
     * 上传时同时更新业务单状态
     *
     * @param preInvoice 当前预制发票
     */
    private void uploadAndUpdateStatus(Long preInvoice, DeductRedNotificationEventEnum eventEnum) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        Map<Long, List<Long>> deductMappingByPreInvoiceId = getDeductMappingByPreInvoiceId(preInvoice);
        if (!org.springframework.util.CollectionUtils.isEmpty(deductMappingByPreInvoiceId)) {
            deductMappingByPreInvoiceId.forEach((k, v) -> {
                List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getId, v).ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode()));
                DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = getMakeInvoiceStatus(tXfPreInvoiceEntities);
                log.info("收到预制发票事件:{},预制发票ID:{}->业务单ID: {} 更新为[{}]", eventEnum.name(), v, k, makeInvoiceStatus.code());
                billMakeInvoiceStatusService.syncMakeInvoiceStatus(k, makeInvoiceStatus);
            });
        } else {
            log.info("收到预制发票事件:{},预制发票ID:{} 未在中间表查询到数据，使用结算单维度进行统计", eventEnum.name(), preInvoice);
            // 历史数据直接关联结算单进行操作
            TXfPreInvoiceEntity preInvoiceEntity = preInvoiceDao.selectById(preInvoice);
            if (null == preInvoiceEntity) {
                return;
            }
            List<TXfPreInvoiceEntity> settlementPreInvoiceList = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getSettlementId, preInvoiceEntity.getSettlementId()).ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode()));
            log.info("收到预制发票事件:{},预制发票ID:{} 结算单关联预制发票数量: {}", eventEnum.name(), preInvoice, settlementPreInvoiceList.size());
            DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = getMakeInvoiceStatus(settlementPreInvoiceList);
            // 结算单关联的业务单
            List<TXfBillDeductEntity> settlementRefBillList = tXfBillDeductDao.selectList(Wrappers.lambdaQuery(TXfBillDeductEntity.class).eq(TXfBillDeductEntity::getRefSettlementNo, preInvoiceEntity.getSettlementNo()));
            log.info("收到预制发票事件:{},预制发票ID:{}->结算单->业务单ID: {} 更新为[{}]", eventEnum.name(), preInvoice, settlementRefBillList.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList()), makeInvoiceStatus.code());
            settlementRefBillList.forEach(v -> billMakeInvoiceStatusService.syncMakeInvoiceStatus(v.getId(), makeInvoiceStatus));
        }
    }

    /**
     * 作废全部预制发票
     *
     * @param preInvoice 当前预制发票
     */
    private void processDiscardAllPreInvoice(Long preInvoice) {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        Map<Long, List<Long>> deductMappingByPreInvoiceId = getDeductMappingByPreInvoiceId(preInvoice);
        if (!org.springframework.util.CollectionUtils.isEmpty(deductMappingByPreInvoiceId)) {
            deductMappingByPreInvoiceId.forEach((k, v) -> {
                List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getId, v));
                DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = getMakeInvoiceStatus(tXfPreInvoiceEntities);
                log.info("收到预制发票作废事件,预制发票ID:{}->业务单ID: {} 更新为[{}]", v, k, makeInvoiceStatus.code());
                billMakeInvoiceStatusService.syncMakeInvoiceStatus(k, makeInvoiceStatus);
            });
        } else {
            log.info("收到预制发票作废事件,预制发票ID:{} 未在中间表查询到数据，使用结算单维度进行统计", preInvoice);
            // 历史数据直接关联结算单进行操作
            TXfPreInvoiceEntity preInvoiceEntity = preInvoiceDao.selectById(preInvoice);
            if (null == preInvoiceEntity) {
                return;
            }
            List<TXfPreInvoiceEntity> settlementPreInvoiceList = preInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getSettlementId, preInvoiceEntity.getSettlementId()));
            log.info("收到预制发票作废事件,预制发票ID:{} 结算单关联预制发票数量: {}", preInvoice, settlementPreInvoiceList.size());
            DeductBillMakeInvoiceStatusEnum makeInvoiceStatus = getMakeInvoiceStatus(settlementPreInvoiceList);
            // 结算单关联的业务单
            List<TXfBillDeductEntity> settlementRefBillList = tXfBillDeductDao.selectList(Wrappers.lambdaQuery(TXfBillDeductEntity.class).eq(TXfBillDeductEntity::getRefSettlementNo, preInvoiceEntity.getSettlementNo()));
            log.info("收到预制发票作废事件,预制发票ID:{}->结算单->业务单ID: {} 更新为[{}]", preInvoice, settlementRefBillList.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList()), makeInvoiceStatus.code());
            settlementRefBillList.forEach(v -> billMakeInvoiceStatusService.syncMakeInvoiceStatus(v.getId(), makeInvoiceStatus));
        }
    }

    /**
     * 通过预制发票列表 获取业务单开具状态
     *
     * @param preInvoiceList
     * @return
     */
    public DeductBillMakeInvoiceStatusEnum getMakeInvoiceStatus(List<TXfPreInvoiceEntity> preInvoiceList) {

        if (CollectionUtils.isEmpty(preInvoiceList)) {
            return DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE;
        }
        int size = preInvoiceList.size();
        Map<Integer, List<TXfPreInvoiceEntity>> preInvoiceStatusMap = preInvoiceList.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getPreInvoiceStatus));
        log.info("预制发票获取业务单开具状态,预制发票列表: {}", preInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList()));
        preInvoiceStatusMap.forEach((k, v) -> log.info("状态: {}, 对应预制发票数量: {}", k, v.size()));

        int uploadRedInvoiceCount = preInvoiceStatusMap.getOrDefault(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode(), Lists.newArrayList()).size();
        int destroyCount = preInvoiceStatusMap.getOrDefault(TXfPreInvoiceStatusEnum.DESTROY.getCode(), Lists.newArrayList()).size();
        int finishSplitCount = preInvoiceStatusMap.getOrDefault(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode(), Lists.newArrayList()).size();
        // 1、全部为 已开红票
        if (size == uploadRedInvoiceCount) {
            return DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE;
        }
        // 2、已开票红票+作废+删除（说明是进行了蓝冲操作，ps: 如果是作废结算单那么就是全部是作废了 或者是 重拆后就会存在删除状态数据）
        if (uploadRedInvoiceCount > 0 && size == (destroyCount + uploadRedInvoiceCount + finishSplitCount)) {
            return DeductBillMakeInvoiceStatusEnum.COMPLETE_MAKE_INVOICE;
        }
        // 3、存在已开票红票 & 已开红票小于总量
        if (uploadRedInvoiceCount > 0 && size > uploadRedInvoiceCount) {
            return DeductBillMakeInvoiceStatusEnum.PART_MAKE_INVOICE;
        }
        // 4、全部已作废/删除
        if (size == (destroyCount + finishSplitCount)) {
            return DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE;
        }

        return DeductBillMakeInvoiceStatusEnum.WAIT_MAKE_INVOICE;


    }


    /**
     * @param preInvoice 当前预制发票ID
     * @return
     */
    private Map<Long, List<Long>> getDeductMappingByPreInvoiceId(Long preInvoice) {
        //通过预制发票ID获取所有业务单ID
        List<TXfDeductPreInvoiceEntity> deductPreInvoiceEntities = this.getBaseMapper().selectList(Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class).select(TXfDeductPreInvoiceEntity::getDeductId).eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, preInvoice));
        if (CollectionUtils.isEmpty(deductPreInvoiceEntities)) {
            return new HashMap<>();
        }
        List<Long> deductIds = deductPreInvoiceEntities.stream().map(TXfDeductPreInvoiceEntity::getDeductId).collect(Collectors.toList());
        //通过业务单ID获取所有业务单ID和预制发票ID关系
        List<TXfDeductPreInvoiceEntity> deductPreInvoiceRelations = this.getBaseMapper().selectList(Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class).select(TXfDeductPreInvoiceEntity::getDeductId, TXfDeductPreInvoiceEntity::getPreInvoiceId).in(TXfDeductPreInvoiceEntity::getDeductId, deductIds));
        //通过业务单ID分组
        return deductPreInvoiceRelations.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getDeductId, Collectors.mapping(TXfDeductPreInvoiceEntity::getPreInvoiceId, Collectors.toList())));
    }

    private void discard(Long preInvoice, Long timestamp) {
        //WALMART-3691 功能放开
        TXfDeductPreInvoiceEntity entity=new TXfDeductPreInvoiceEntity();
        entity.setTriggerTime(new Date(timestamp));
        entity.setDeleted(1);
        entity.setUpdateTime(new Date());
        int update = this.getBaseMapper().update(entity,Wrappers.lambdaUpdate(TXfDeductPreInvoiceEntity.class).eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, preInvoice));
        log.info("预制发票[{}]关联关系作废结果:{}",preInvoice,update);
        if (update>0){
        processDiscardAllPreInvoice(preInvoice);
        }
    }

    private void delete(Long preInvoice, Long timestamp) {
        TXfDeductPreInvoiceEntity entity = new TXfDeductPreInvoiceEntity();
        entity.setTriggerTime(new Date(timestamp));
        entity.setDeleted(1);
        entity.setUpdateTime(new Date());
        int update = this.getBaseMapper().update(entity, Wrappers.lambdaUpdate(TXfDeductPreInvoiceEntity.class).eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, preInvoice));
        log.info("预制发票[{}]关联关系删除结果:{}", preInvoice, update);
//        if (update > 0) {
//            processDiscardAllPreInvoice(preInvoice);
//        }
    }

    private void updateStatus(DeductRedNotificationEvent.DeductRedNotificationModel body, DeductRedNotificationEventEnum eventEnum, long timestamp) {

        LambdaUpdateWrapper<TXfDeductPreInvoiceEntity> updateWrapper = Wrappers.lambdaUpdate(TXfDeductPreInvoiceEntity.class)
                .set(TXfDeductPreInvoiceEntity::getApplyStatus, eventEnum.toRedNotificationApplyStatus().getValue())
                .set(TXfDeductPreInvoiceEntity::getUpdateTime, new Date())
                .eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, body.getPreInvoiceId());
        //WALMART-3631 协议单匹配，撤销后协议单占用关系deleted字段需要更新为1
        if(AgreementRedNotificationStatus.REVOKED.getValue()==eventEnum.toRedNotificationApplyStatus().getValue()){
            updateWrapper.set(TXfDeductPreInvoiceEntity::getDeleted, 1);
        }
        if (CollectionUtils.isNotEmpty(eventEnum.applyStatusParams())) {
            updateWrapper.in(TXfDeductPreInvoiceEntity::getApplyStatus, eventEnum.applyStatusParams().stream().map(AgreementRedNotificationStatus::getValue).collect(Collectors.toList()));
        }
        this.update(updateWrapper);
    }

    /**
     * @Description 红字信息申请成功，回填红字信息
     * @Author pengtao
     * @return
    **/
    private void updateStatusAndRedNotificationInfo(DeductRedNotificationEvent.DeductRedNotificationModel body, DeductRedNotificationEventEnum eventEnum, long timestamp) {

        this.update(Wrappers.lambdaUpdate(TXfDeductPreInvoiceEntity.class)
                        .set(TXfDeductPreInvoiceEntity::getRedNotificationId, body.getRedNotificationId())
                        .set(TXfDeductPreInvoiceEntity::getRedNotificationNo, body.getRedNotificationNo())
                        .set(TXfDeductPreInvoiceEntity::getApplyStatus, eventEnum.toRedNotificationApplyStatus().getValue())
                        .set(TXfDeductPreInvoiceEntity::getUpdateTime, new Date())
                        .eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, body.getPreInvoiceId())
//                .le(TXfDeductPreInvoiceEntity::getTriggerTime,new Date(timestamp))
//                .eq(TXfDeductPreInvoiceEntity::getDeleted,0)
        );
    }

    private void invokeOperationLog(DeductRedNotificationEvent.DeductRedNotificationModel body, DeductRedNotificationEventEnum eventEnum) {
        log.info("保存操作日志:{},eventEnum:{}",JSON.toJSON(body),JSON.toJSON(eventEnum));
        if (EVENT_LOG_RELATION.containsKey(eventEnum)) {

            List<TXfDeductPreInvoiceEntity> list = this.list(Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class).eq(TXfDeductPreInvoiceEntity::getPreInvoiceId, body.getPreInvoiceId()).eq(TXfDeductPreInvoiceEntity::getDeleted, 0));
            if (CollectionUtils.isNotEmpty(list)) {
                TXfPreInvoiceEntity tXfPreInvoiceEntity = tXfPreInvoiceDao.selectById(body.getPreInvoiceId());
                Long deductId = list.get(0).getDeductId();
                TXfBillDeductEntity deduct = getDeductById(deductId);
                Integer status = deduct.getStatus();
                TXfDeductStatusEnum enumByCode = TXfDeductStatusEnum.getEnumByCode(status);
                Integer businessType = deduct.getBusinessType();
                for (TXfDeductPreInvoiceEntity entity : list) {
                    //生成日志
                    this.operateLogService.addDeductLog(entity.getDeductId(), businessType,
                            enumByCode, tXfPreInvoiceEntity.getSettlementNo(),
                            eventEnum, UserUtil.getUserId(), UserUtil.getUserName());
                }
            }
        }
    }


    private void add(long preInvoiceId, boolean applyRequired, long timestamp) {
        AgreementRedNotificationStatus status = applyRequired ? AgreementRedNotificationStatus.APPLY_PENDING : AgreementRedNotificationStatus.NON_REQUIRE;

        //预制发票查找结算单明细ID及金额占用情况
        List<TXfPreBillDetailEntity> details = getSettlementItemIds(preInvoiceId);
        if (CollectionUtils.isEmpty(details)) {
            log.info("预制发票:[{}]对应的结算单明细列表为空", preInvoiceId);
            return;
        }

        //预制发票在结算单ID中的占用情况，然后用结算单明细ID作为key分组,这里面仅有一张预制发票对应的结算单明细关系
        Map<Long, UseAmount> settlementItemGroup = details.stream().collect(Collectors.toMap(TXfPreBillDetailEntity::getSettlementItemId,
                x -> new UseAmount(x.getAmountWithTax(), x.getTaxAmount(), x.getAmountWithoutTax(), null),
                UseAmount::plus
        ));

        // settlementItemId-->deductId-->amount
        Map<Long, Map<Long, UseAmount>> settlementItemInDeductAmount = null;

        Integer type = tXfSettlementExtDao.queryBusinessTypeByPreInvoiceId(preInvoiceId);
        log.info("[DeductPreInvoiceService]当前单据类型:id:{}->type:{}", preInvoiceId, type);
        //索赔单
        if (Objects.equals(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), type)) {
            //结算单明细ID查找索赔单及索赔明细占用情况
            List<DeductSettlementItemRefDto> tXfBillDeductItemRefEntities = tXfBillDeductItemRefExtDao.queryDeductIdBySettlementItemId(settlementItemGroup.keySet());

            settlementItemInDeductAmount = tXfBillDeductItemRefEntities.stream().collect(Collectors.groupingBy(DeductSettlementItemRefDto::getSettlementItemId,
                    Collectors.toMap(DeductSettlementItemRefDto::getDeductId, x -> new UseAmount(x.getAmountWithTax(), x.getTaxAmount(), x.getAmountWithoutTax(), x.getDeductId()), UseAmount::plus)
            ));

        } else if (Objects.equals(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue(), type)) {

            //协议
            List<TXfBillDeductInvoiceDetailEntity> billDeductInvoiceDetailEntities = tXfBillDeductInvoiceDetailDao.selectList(Wrappers.lambdaQuery(TXfBillDeductInvoiceDetailEntity.class).in(TXfBillDeductInvoiceDetailEntity::getSettlementItemId, settlementItemGroup.keySet()));
            System.err.println("-----"+JSON.toJSONString(billDeductInvoiceDetailEntities));
            settlementItemInDeductAmount = billDeductInvoiceDetailEntities.stream().collect(Collectors.groupingBy(TXfBillDeductInvoiceDetailEntity::getSettlementItemId,
                    Collectors.toMap(TXfBillDeductInvoiceDetailEntity::getDeductId, x -> new UseAmount(x.getUseAmountWithTax(), x.getUseTaxAmount(), x.getUseAmountWithoutTax(), x.getDeductId()), UseAmount::plus)
            ));
        } else {
            // EPD
        }


        Optional.ofNullable(settlementItemInDeductAmount).ifPresent(m -> {
            List<TXfDeductPreInvoiceEntity> deductPreInvoiceEntities = build(m, settlementItemGroup, preInvoiceId);
            if (CollectionUtils.isNotEmpty(deductPreInvoiceEntities)) {
                deductPreInvoiceEntities.forEach(x -> {
                            x.setApplyStatus(status.getValue());
                            x.setTriggerTime(new Date(timestamp));
                        }
                );
                System.err.println("222" + JSON.toJSONString(deductPreInvoiceEntities));
                this.saveBatch(deductPreInvoiceEntities);
            }
        });

    }


    private List<TXfPreBillDetailEntity> getSettlementItemIds(Long preInvoiceId) {
        return Optional.ofNullable(preBillDetailService.getDetails(preInvoiceId)).orElse(Collections.emptyList());
    }


    private List<TXfDeductPreInvoiceEntity> build(Map<Long, Map<Long, UseAmount>> settlementItemAmountInDeduct, Map<Long, UseAmount> settlementItemAmountInPreInvoice, Long preInvoiceId) {

        List<UseAmount> summary = new ArrayList<>();
        settlementItemAmountInDeduct.forEach((key, value) -> {
            UseAmount useAmount = settlementItemAmountInPreInvoice.get(key);
            value.forEach((deductId, amount) -> {
                UseAmount real = amount.min(useAmount);
                real.deductId = deductId;
                summary.add(real);
            });
        });

        List<TXfDeductPreInvoiceEntity> entities = new ArrayList<>();
        summary.stream()
                .collect(Collectors.toMap(UseAmount::getDeductId, x -> x, UseAmount::plus))
                .forEach((k, v) -> {
                    TXfBillDeductEntity deductEntity = getDeductById(k);
                    TXfDeductPreInvoiceEntity entity = new TXfDeductPreInvoiceEntity();
                    entity.setId(idSequence.nextId());
                    entity.setBusinessNo(deductEntity.getBusinessNo());
                    entity.setDeductId(k);
                    entity.setAmountWithTax(v.getAmountWithTax());
                    entity.setAmountWithoutTax(v.getAmountWithoutTax());
                    entity.setTaxAmount(v.getTaxAmount());
                    entity.setPreInvoiceId(preInvoiceId);
                    entity.setCreateTime(new Date());
                    entity.setTaxDiff(calculateTaxDiff(v, deductEntity.getTaxRate()));
                    entity.setDeleted(0);
                    entity.setUpdateTime(new Date());
                    //正负税率标记
                    if (deductEntity.getAmountWithTax().compareTo(BigDecimal.ZERO) == 1) {
                        entity.setAmountFlag(new BigDecimal("1"));
                    }else if (deductEntity.getAmountWithTax().compareTo(BigDecimal.ZERO) == -1) {
                        entity.setAmountFlag(new BigDecimal("-1"));
                    }else{
                        entity.setAmountFlag(new BigDecimal("1"));
                    }
                    entities.add(entity);
                });

        return entities;
    }

    private BigDecimal calculateTaxDiff(UseAmount useAmount, BigDecimal deductTaxRate) {
        if (useAmount.taxRate.compareTo(deductTaxRate) != 0) {
            return useAmount.amountWithoutTax.multiply(deductTaxRate).subtract(useAmount.taxAmount).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    private TXfBillDeductEntity getDeductById(Long id) {
        return tXfBillDeductDao.selectOne(Wrappers.lambdaQuery(TXfBillDeductEntity.class).select(TXfBillDeductEntity::getBusinessNo, TXfBillDeductEntity::getStatus, TXfBillDeductEntity::getBusinessType, TXfBillDeductEntity::getTaxRate,TXfBillDeductEntity::getAmountWithTax).eq(TXfBillDeductEntity::getId, id));
    }


    /**
     * 指定结算单明细在预制发票/业务单中占用的金额
     */
    @Getter
    private static class UseAmount {
        BigDecimal amountWithTax;
        BigDecimal taxAmount;
        BigDecimal amountWithoutTax;
        Long deductId;

        BigDecimal taxRate;

        public UseAmount(BigDecimal amountWithTax, BigDecimal taxAmount, BigDecimal amountWithoutTax, Long deductId) {
            this.amountWithTax = amountWithTax.abs();
            this.taxAmount = Optional.ofNullable(taxAmount).orElse(BigDecimal.ZERO).abs();
            this.amountWithoutTax = amountWithoutTax.abs();
            this.deductId = deductId;
            this.taxRate = this.taxAmount.divide(this.amountWithoutTax, 2, RoundingMode.HALF_UP);
        }

        public UseAmount plus(UseAmount other) {
            this.amountWithTax = this.amountWithTax.add(other.amountWithTax);
            this.amountWithoutTax = this.amountWithoutTax.add(other.amountWithoutTax);
            this.taxAmount = this.taxAmount.add(other.taxAmount);
            return this;
        }

        public UseAmount min(UseAmount other) {
            return this.amountWithTax.compareTo(other.amountWithTax) < 0 ? this.clone() : other.clone();
        }

        public UseAmount clone() {
            return new UseAmount(this.getAmountWithTax(), this.getTaxAmount(), this.getAmountWithoutTax(), this.deductId);
        }
    }


}
