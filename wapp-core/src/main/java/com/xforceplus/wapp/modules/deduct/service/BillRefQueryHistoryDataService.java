package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemResponse;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.daoExt.BillDeductQueryExtDao;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Describe: 历史业务单关联数据处理
 *
 * @Author xiezhongyong
 * @Date 2022/10/22
 */
@Service
@Slf4j
public class BillRefQueryHistoryDataService {

    @Autowired
    TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    TXfSettlementDao tXfSettlementDao;
    @Autowired
    TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    TXfRedNotificationDao tXfRedNotificationDao;

    @Value("${wapp.history-data.time: 2022-10-10 17:00:00}")
    private String histroyDataTime;

    /**
     * 业务单红字信息 填充
     *
     * @param list
     */
    public void fullBillRedNotification(List<? extends QueryDeductBaseResponse> list, List<String> setRedInfoTabs) {
        for (QueryDeductBaseResponse response : list) {
            if (null != response.getQueryTab() && !setRedInfoTabs.contains(response.getQueryTab().getCode())) {
                continue;
            }
            if (response.getCreateTime().getTime() > getHistoryTime()) {
                log.info("业务单列表红字填充，业务单ID: {}, 业务单创建时间: {}, 不是历史数据，不进行历史数据红字信息填充", response.getId(),
                        response.getCreateTime());
                continue;
            }
            if (CollectionUtils.isNotEmpty(response.getRedNotificationNos()) || CollectionUtils.isNotEmpty(response.getRedNotificationStatus())) {
                log.info("业务单列表查询业务单已经存在红字编号信息，不进行历史数据填充: {}", JsonUtil.toJsonStr(response));
                continue;
            }
            Tuple2<List<String>, List<Integer>> billRedNotification = getBillRedNotification(response.getId());
            if (null == billRedNotification) {
                log.info("业务单明细红字填充，业务单ID: {},未找到红字信息", response.getId());
                continue;
            }
            response.setRedNotificationNos(billRedNotification.getT1());
            response.setRedNotificationStatus(billRedNotification.getT2());
            response.setFullHistoryFlag(Boolean.TRUE);
            log.info("业务单列表查询，通过历史数据填充红字编号: {}", JsonUtil.toJsonStr(response));
        }
    }

    public void fullBillRedNotification2(List<? extends QueryDeductBaseResponse> list, List<String> setRedInfoTabs) {
        if (list == null || list.isEmpty()) {
            return;
        }
        // 排除数据
        List<QueryDeductBaseResponse> refSettlementNoList = list.stream()
                .filter(response -> null == response.getQueryTab() || setRedInfoTabs.contains(response.getQueryTab().getCode()))
                .filter(response -> response.getCreateTime().getTime() <= getHistoryTime())
                .filter(response -> CollectionUtils.isEmpty(response.getRedNotificationNos()) && CollectionUtils
                        .isEmpty(response.getRedNotificationStatus()))
                .collect(Collectors.toList());

        // 按照结算单号分组查询
        Map<String, List<QueryDeductBaseResponse>> refSettlementNoMap =
                refSettlementNoList.stream().collect(Collectors.groupingBy(QueryDeductBaseResponse::getRefSettlementNo));
        for (Map.Entry<String, List<QueryDeductBaseResponse>> item : refSettlementNoMap.entrySet()) {
            String refSettlementNo = item.getKey();
            Tuple2<List<String>, List<Integer>> billRedNotification = getBillRedNotification(refSettlementNo);

            if (null == billRedNotification) {
                log.info("业务单明细红字填充，业务单: {},未找到红字信息", refSettlementNo);
                continue;
            }
            List<QueryDeductBaseResponse> responseList = item.getValue();
            responseList.forEach(response -> {
                        response.setRedNotificationNos(billRedNotification.getT1());
                        response.setRedNotificationStatus(billRedNotification.getT2());
                        response.setFullHistoryFlag(Boolean.TRUE);
                        log.debug("业务单列表查询，通过历史数据填充红字编号: {}", JsonUtil.toJsonStr(response));
                    }
            );
        }
    }

    /**
     * 业务单明细红字信息 填充
     *
     * @param deductBillItemList
     */
    public void fullBillItemRedNotification(TXfBillDeductEntity bill, List<DeductBillItemModel> deductBillItemList) {
        if (CollectionUtils.isEmpty(deductBillItemList)) {
            return;
        }

        List<DeductBillItemModel> itemModels = deductBillItemList.stream().filter(v ->
                CollectionUtils.isNotEmpty(v.getRedNotificationNos()) || CollectionUtils.isNotEmpty(v.getRedNotificationStatus())
        ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(itemModels)) {
            log.info("业务单明细红字填充，业务单ID: {}, 存在明细包含红字信息，不是历史数据，不进行历史数据红字信息填充 ：{}", bill.getId(), itemModels);
            return;
        }
        if (bill.getCreateTime().getTime() > getHistoryTime()) {
            log.info("业务单明细红字填充，业务单ID: {},业务单创建时间:{}, 不是历史数据，不进行历史数据红字信息填充", bill.getId(), bill.getCreateTime());
            return;
        }
        Tuple2<List<String>, List<Integer>> billRedNotification = getBillRedNotification(bill.getId());
        if (null == billRedNotification) {
            log.info("业务单明细红字填充，业务单ID: {},未找到红字信息", bill.getId());
            return;
        }
        deductBillItemList.forEach(v -> {
            v.setRedNotificationNos(billRedNotification.getT1());
            v.setRedNotificationStatus(billRedNotification.getT2());
            v.setFullHistoryFlag(Boolean.TRUE);
            log.info("业务单明细查询，通过历史数据填充红字编号: {}", JsonUtil.toJsonStr(v));
        });
    }


    /**
     * 预制发票红字信息 填充
     *
     * @param preInvoices
     */
    public void fullPreInvoiceRedNotification(List<PreInvoice> preInvoices) {
        log.info("历史预制发票红字信息填充: {}", preInvoices);
        if (CollectionUtils.isEmpty(preInvoices)) {
            return;
        }
        // 预制发票id
        List<Long> preInvoiceIds = new ArrayList<>();
        for (PreInvoice preInvoice : preInvoices) {
            if (preInvoice.getCreateTime() > getHistoryTime()) {
                log.info("预制发票红字填充，预制发票ID: {},预制发票创建时间:{}, 不是历史数据，不进行历史数据红字信息填充", preInvoice.getId(),
                        preInvoice.getCreateTime());
                continue;
            }

            if (CollectionUtils.isNotEmpty(preInvoice.getRedNotificationStatus())) {
                log.info("预制发票红字填充，预制发票ID: {},预制发票创建时间:{}, 红字状态不为空，不进行历史逻辑填充", preInvoice.getId(),
                        preInvoice.getCreateTime(), preInvoice.getRedNotificationStatus());
                continue;
            }
            // 0税率无需申请处理，如果有编号存在先不处理
            if (0 == BigDecimal.ZERO.compareTo(preInvoice.getTaxRate()) && StringUtils.isBlank(preInvoice.getRedNotificationNo())) {
                preInvoice.setRedNotificationStatus(Arrays.asList(AgreementRedNotificationStatus.NON_REQUIRE.getValue()));
                continue;
            }
            preInvoiceIds.add(preInvoice.getId());
        }
        if (CollectionUtils.isEmpty(preInvoiceIds)) {
            log.info("历史预制发票红字信息填充, 过滤后数据为空");
            return;
        }
        List<TXfRedNotificationEntity> redNotificationEntityList =
                tXfRedNotificationDao.selectList(Wrappers.lambdaQuery(TXfRedNotificationEntity.class).in(TXfRedNotificationEntity::getPid, preInvoiceIds));


        Map<Long, PreInvoice> preInvoiceMap = preInvoices.stream().collect(Collectors.toMap(PreInvoice::getId,
                Function.identity(), (v1, v2) -> v2));

        for (TXfRedNotificationEntity notificationEntity : redNotificationEntityList) {
            Integer redNotificationStatus = getRedNotificationStatus(notificationEntity);
            PreInvoice preInvoice = preInvoiceMap.get(Long.parseLong(notificationEntity.getPid()));
            if (null != preInvoice) {
                preInvoice.setRedNotificationStatus(Arrays.asList(redNotificationStatus));
            }
        }

        // 以免数据问题进行兜底逻辑
        for (PreInvoice preInvoice : preInvoices) {
            if (CollectionUtils.isEmpty(preInvoice.getRedNotificationStatus()) && StringUtils.isBlank(preInvoice.getRedNotificationNo())) {
                // 有编号 为待申请
                preInvoice.setRedNotificationStatus(Arrays.asList(AgreementRedNotificationStatus.APPLY_PENDING.getValue()));
            } else if (CollectionUtils.isEmpty(preInvoice.getRedNotificationStatus()) && StringUtils.isNotBlank(preInvoice.getRedNotificationNo())) {
                // 无编号 为已申请
                preInvoice.setRedNotificationStatus(Arrays.asList(AgreementRedNotificationStatus.APPLIED.getValue()));
            }
        }

    }


    /**
     * 获取业务单红字编号及状态
     *
     * @param billId
     * @return
     */
    private Tuple2<List<String>, List<Integer>> getBillRedNotification(Long billId) {

        TXfBillDeductEntity bill = tXfBillDeductDao.selectById(billId);
        if (null == bill || StringUtils.isEmpty(bill.getRefSettlementNo())) {
            return null;
        }
        List<TXfSettlementEntity> settlementEntities =
                tXfSettlementDao.selectList(Wrappers.lambdaQuery(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, bill.getRefSettlementNo()));
        if (CollectionUtils.isEmpty(settlementEntities)) {
            return null;
        }
        List<TXfPreInvoiceEntity> preInvoiceEntityList =
                tXfPreInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getSettlementId, settlementEntities.stream().map(TXfSettlementEntity::getId).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(preInvoiceEntityList)) {
            return null;
        }
        List<TXfRedNotificationEntity> redNotificationEntityList =
                tXfRedNotificationDao.selectList(Wrappers.lambdaQuery(TXfRedNotificationEntity.class).in(TXfRedNotificationEntity::getPid, preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList())));

        Tuple2<List<String>, List<Integer>> tuple2 = notificationListData(redNotificationEntityList);

        return tuple2;
    }

    private Tuple2<List<String>, List<Integer>> getBillRedNotification(String refSettlementNo) {

        if (StringUtils.isBlank(refSettlementNo)) {
            return null;
        }
        List<TXfSettlementEntity> settlementEntities =
                tXfSettlementDao.selectList(Wrappers.lambdaQuery(TXfSettlementEntity.class).eq(TXfSettlementEntity::getSettlementNo, refSettlementNo));
        if (CollectionUtils.isEmpty(settlementEntities)) {
            return null;
        }
        List<TXfPreInvoiceEntity> preInvoiceEntityList =
                tXfPreInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).in(TXfPreInvoiceEntity::getSettlementId, settlementEntities.stream().map(TXfSettlementEntity::getId).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(preInvoiceEntityList)) {
            return null;
        }
        List<TXfRedNotificationEntity> redNotificationEntityList =
                tXfRedNotificationDao.selectList(Wrappers.lambdaQuery(TXfRedNotificationEntity.class).in(TXfRedNotificationEntity::getPid, preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList())));

        Tuple2<List<String>, List<Integer>> tuple2 = notificationListData(redNotificationEntityList);
        return tuple2;
    }


    /**
     * 统计红字信息状态和编号
     *
     * @param redNotificationEntityList
     * @return
     */
    private Tuple2<List<String>, List<Integer>> notificationListData(List<TXfRedNotificationEntity> redNotificationEntityList) {
        List<String> redNotificationNos = new ArrayList<>();
        List<Integer> redNotificationStatus = new ArrayList<>();
        for (TXfRedNotificationEntity notificationEntity : redNotificationEntityList) {
            if (StringUtils.isNotBlank(notificationEntity.getRedNotificationNo())) {
                redNotificationNos.add(notificationEntity.getRedNotificationNo());
            }
            Integer notificationStatus = getRedNotificationStatus(notificationEntity);
            if (null != notificationStatus) {
                redNotificationStatus.add(notificationStatus);
            }

        }
        // 去重设值
        return Tuples.of(redNotificationNos.stream().distinct().collect(Collectors.toList())
                , redNotificationStatus.stream().distinct().collect(Collectors.toList()));
    }


    /**
     * 获取红字信息显示状态
     *
     * @param notificationEntity
     * @return
     */
    private Integer getRedNotificationStatus(TXfRedNotificationEntity notificationEntity) {

        RedNoApplyingStatus applyingStatus = RedNoApplyingStatus.fromValue(notificationEntity.getApplyingStatus());

        if (RedNoApplyingStatus.APPLIED == applyingStatus && ApproveStatus.ALREADY_ROLL_BACK.getValue().equals(notificationEntity.getApproveStatus())) {
            return AgreementRedNotificationStatus.REVOKED.getValue();
        }

        switch (applyingStatus) {
            case WAIT_TO_APPLY:
                return AgreementRedNotificationStatus.APPLY_PENDING.getValue();
            case APPLYING:
                return AgreementRedNotificationStatus.APPLYING.getValue();
            case APPLIED:
                return AgreementRedNotificationStatus.APPLIED.getValue();
            case WAIT_TO_APPROVE:
                return AgreementRedNotificationStatus.IN_REVOCATION.getValue();
            default:
                return null;
        }

    }

    public Long getHistoryTime() {
        Date date = DateUtils.strToDateLong(histroyDataTime);
        return date.getTime();
    }


}
