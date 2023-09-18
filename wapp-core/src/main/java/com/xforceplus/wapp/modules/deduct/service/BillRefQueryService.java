package com.xforceplus.wapp.modules.deduct.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.claim.dto.DeductListResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import com.xforceplus.wapp.modules.settlement.dto.SettlementItemResponse;
import com.xforceplus.wapp.modules.statement.models.PreInvoice;
import com.xforceplus.wapp.repository.dao.TXfDeductPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreBillDetailDao;
import com.xforceplus.wapp.repository.daoExt.BillDeductQueryExtDao;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Describe: 业务单关联查询
 *
 * @Author xiezhongyong
 * @Date 2022/9/15
 */
@Service
@Slf4j
public class BillRefQueryService {

    @Autowired
    private BillDeductQueryExtDao billDeductQueryExtDao;
    @Autowired
    private TXfDeductPreInvoiceDao tXfDeductPreInvoiceDao;
    @Autowired
    private TXfPreBillDetailDao tXfPreBillDetailDao;

    /**
     * 业务单明细红字信息 填充
     *
     * @param deductBillItemList
     */
    public void fullBillItemRedNotification(List<DeductBillItemModel> deductBillItemList) {
        if (CollectionUtils.isEmpty(deductBillItemList)) {
            return;
        }
        // 明细ID
        List<Long> deductItemIdList = deductBillItemList.stream().map(DeductBillItemModel::getId).collect(Collectors.toList());

//        List<TXfBillItemRefDetailExtEntity> billItemRefDetail = billDeductQueryExtDao.getBillItemRefDetail(deductItemIdList);

        List<TXfBillItemRefDetailExtEntity> billItemRefDetail = new ArrayList<>();
        if (deductItemIdList.size() > 1000) {
            int fromIndex = 0;
            List<Long> subDeductItemIdList;
            do {
                if (fromIndex + 1000 > deductItemIdList.size()) {
                    subDeductItemIdList = deductItemIdList.subList( fromIndex, deductItemIdList.size());
                } else {
                    subDeductItemIdList = deductItemIdList.subList(fromIndex, fromIndex + 1000);
                }
                billItemRefDetail.addAll(billDeductQueryExtDao.getBillItemRefDetail(subDeductItemIdList));
                fromIndex += 1000;
            } while (fromIndex < deductItemIdList.size());
        } else {
            billItemRefDetail = billDeductQueryExtDao.getBillItemRefDetail(deductItemIdList);
        }


        if (CollectionUtils.isEmpty(billItemRefDetail)) {
            return;
        }

        // 汇总预制发票ID
        List<Long> preInvoiceIds = billItemRefDetail.stream().map(TXfBillItemRefDetailExtEntity::getPreInvoiceId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(preInvoiceIds)) {
            return;
        }

        // PS： 因为已撤销的预制发票 t_xf_deduct_pre_invoice 表deleted 状态未更新，需要关联预制发票表查询
        List<TXfDeductPreInvoiceEntity> refList = this.forListBillRefByPreInvoiceIds(preInvoiceIds);

        // 批量查询后 通过预制发票ID 获取关联的红字信息
        Map<Long, List<TXfDeductPreInvoiceEntity>> preMap = refList.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getPreInvoiceId));

        // 遍历响应明细时，需要通过明细id 获取对于的预制发票id->通过预制发票id获取 红字信息
        Map<Long, List<TXfBillItemRefDetailExtEntity>> dItemMap = billItemRefDetail.stream().
                collect(Collectors.groupingBy(TXfBillItemRefDetailExtEntity::getDeductItemId));

        for (DeductBillItemModel deductBillItemModel : deductBillItemList) {
            // 获取对应的明细关系
            List<String> redNotificationNos = new ArrayList<>();
            List<Integer> redNotificationStatus = new ArrayList<>();
            if(null == deductBillItemModel || null == deductBillItemModel.getId()) {
                continue;
            }
            List<TXfBillItemRefDetailExtEntity> items = dItemMap.getOrDefault(deductBillItemModel.getId(), Lists.newArrayList());
            for (TXfBillItemRefDetailExtEntity item : items) {
                if (null == item || null == item.getPreInvoiceId()) {
                    continue;
                }

                // 通过关系中的预制发票ID获取需要的红字信息列表
                List<TXfDeductPreInvoiceEntity> refs = preMap.get(item.getPreInvoiceId());

                if (CollectionUtils.isEmpty(refs)) {
                    continue;
                }
                // 红字编号
                redNotificationNos.addAll(
                        refs.stream().map(TXfDeductPreInvoiceEntity::getRedNotificationNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList())
                );
                // 红字状态
                redNotificationStatus.addAll(
                        refs.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(Objects::nonNull).distinct().collect(Collectors.toList())
                );

                // 去重设值
                deductBillItemModel.setRedNotificationNos(redNotificationNos.stream().distinct().collect(Collectors.toList()));
                deductBillItemModel.setRedNotificationStatus(redNotificationStatus.stream().distinct().collect(Collectors.toList()));

            }

        }

    }

    /**
     * 预制发票红字信息 填充
     *
     * @param invoices
     */
    public void fullPreInvoiceRedNotification(List<PreInvoice> invoices) {
        if (CollectionUtils.isEmpty(invoices)) {
            return;
        }
        // 预制发票id
        List<Long> preInvoiceIds = invoices.stream().map(PreInvoice::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(preInvoiceIds)) {
            return;
        }


        // PS： 因为已撤销的预制发票 t_xf_deduct_pre_invoice 表deleted 状态未更新，需要关联预制发票表查询
        List<TXfDeductPreInvoiceEntity> refList = billDeductQueryExtDao.getBillRefByPreInvoiceIds(preInvoiceIds);

        // 批量查询后 通过预制发票ID 获取关联的红字信息
        Map<Long, List<TXfDeductPreInvoiceEntity>> preMap = refList.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getPreInvoiceId));

        for (PreInvoice invoice : invoices) {
            // 通过关系中的预制发票ID获取需要的红字信息列表
            List<TXfDeductPreInvoiceEntity> refs = preMap.get(invoice.getId());

            if (CollectionUtils.isEmpty(refs)) {
                continue;
            }

            // 红字编号(如果已关系表中的编号为准就打开以下注释代码)
            invoice.setRedNotificationNo(refs.stream().map(TXfDeductPreInvoiceEntity::getRedNotificationNo).filter(StringUtils::isNotBlank).findFirst().orElse(null));
            // 红字状态
            invoice.setRedNotificationStatus(refs.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

        }

    }

    /**
     * 结算单明细红字信息 填充
     *
     * @param itemEntities
     */
    public void fullSettlementItem(List<SettlementItemResponse> itemEntities) {
        if (CollectionUtils.isEmpty(itemEntities)) {
            return;
        }
        // 明细ID
        List<Long> itemIds = itemEntities.stream().map(SettlementItemResponse::getId).collect(Collectors.toList());

        LambdaQueryWrapper<TXfPreBillDetailEntity> preSettlementItemmwrapper = new LambdaQueryWrapper<>();
        preSettlementItemmwrapper.in(TXfPreBillDetailEntity::getSettlementItemId, itemIds);
        // 结算单明细-预制发票明细关系
        List<TXfPreBillDetailEntity> preRefSettlementItemList = tXfPreBillDetailDao.selectList(preSettlementItemmwrapper);

        // 汇总预制发票ID
        List<Long> preInvoiceIds = preRefSettlementItemList.stream().map(TXfPreBillDetailEntity::getPreInvoiceId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(preInvoiceIds)) {
            return;
        }

        // PS： 因为已撤销的预制发票 t_xf_deduct_pre_invoice 表deleted 状态未更新，需要关联预制发票表查询
        List<TXfDeductPreInvoiceEntity> refList = billDeductQueryExtDao.getBillRefByPreInvoiceIds(preInvoiceIds);

        // 批量查询后 通过预制发票ID 获取关联的红字信息
        Map<Long, List<TXfDeductPreInvoiceEntity>> preMap = refList.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getPreInvoiceId));

        // 遍历响应明细时，需要通过明细id 获取对于的预制发票id->通过预制发票id获取 红字信息
        Map<Long, List<TXfPreBillDetailEntity>> dItemMap = preRefSettlementItemList.stream().collect(Collectors.groupingBy(TXfPreBillDetailEntity::getSettlementItemId));

        for (SettlementItemResponse itemEntity : itemEntities) {
            // 获取对应的明细关系
            List<String> redNotificationNos = new ArrayList<>();
            List<Integer> redNotificationStatus = new ArrayList<>();
            List<TXfPreBillDetailEntity> items = dItemMap.get(itemEntity.getId());
            for (TXfPreBillDetailEntity item : items) {
                if (null == item || null == item.getPreInvoiceId()) {
                    continue;
                }

                // 通过关系中的预制发票ID获取需要的红字信息列表
                List<TXfDeductPreInvoiceEntity> refs = preMap.get(item.getPreInvoiceId());

                if (CollectionUtils.isEmpty(refs)) {
                    continue;
                }
                // 红字编号
                redNotificationNos.addAll(
                        refs.stream().map(TXfDeductPreInvoiceEntity::getRedNotificationNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList())
                );
                // 红字状态
                redNotificationStatus.addAll(
                        refs.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(Objects::nonNull).distinct().collect(Collectors.toList())
                );

            }
            // 去重设值
            itemEntity.setRedNotificationNos(redNotificationNos.stream().distinct().collect(Collectors.toList()));
            itemEntity.setRedNotificationStatus(redNotificationStatus.stream().distinct().collect(Collectors.toList()));

        }


    }


    /**
     * 业务单红字编号填充(协议)
     *
     * @param list
     */
    public void fullAgreementBillRedNotification(List<DeductListResponse> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> billIds = list.stream().map(DeductListResponse::getId).distinct().collect(Collectors.toList());

        // PS： 因为已撤销的预制发票 t_xf_deduct_pre_invoice 表deleted 状态未更新，需要关联预制发票表查询
        List<TXfDeductPreInvoiceEntity> refList = billDeductQueryExtDao.getBillRefByBillIds(billIds);

        Map<Long, List<TXfDeductPreInvoiceEntity>> refMaps = refList.stream().collect(Collectors.groupingBy(TXfDeductPreInvoiceEntity::getDeductId));

        for (DeductListResponse bill : list) {
            List<TXfDeductPreInvoiceEntity> refs = refMaps.get(bill.getId());
            if (CollectionUtils.isEmpty(refs)) {
                continue;
            }
            // 红字编号
            bill.setRedNotificationNos(refs.stream().map(TXfDeductPreInvoiceEntity::getRedNotificationNo).filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList()));
            // 红字状态
            bill.setRedNotificationStatus(refs.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(Objects::nonNull).distinct().collect(Collectors.toList()));

        }

    }

    /**
     * 分批for 查询（通过预制发票ID 获取对于的红字及业务单关系数据）
     * @param preInvoiceIds
     * @return
     */
    public List<TXfDeductPreInvoiceEntity> forListBillRefByPreInvoiceIds(List<Long> preInvoiceIds) {

        List<TXfDeductPreInvoiceEntity> preInvoiceRefList = new ArrayList<>();
        if (preInvoiceIds.size() > 1000) {
            int fromIndex = 0;
            List<Long> subPreInvoiceRefList;
            do {
                if (fromIndex + 1000 > preInvoiceIds.size()) {
                    subPreInvoiceRefList = preInvoiceIds.subList(fromIndex, preInvoiceIds.size());
                } else {
                    subPreInvoiceRefList = preInvoiceIds.subList(fromIndex, fromIndex + 1000);
                }
                preInvoiceRefList.addAll(billDeductQueryExtDao.getBillRefByPreInvoiceIds(subPreInvoiceRefList));
                fromIndex += 1000;
            } while (fromIndex < preInvoiceIds.size());
        } else {
            preInvoiceRefList = billDeductQueryExtDao.getBillRefByPreInvoiceIds(preInvoiceIds);
        }

        return preInvoiceRefList;
    }


}
