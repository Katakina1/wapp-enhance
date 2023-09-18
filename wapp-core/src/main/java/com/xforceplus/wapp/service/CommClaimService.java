package com.xforceplus.wapp.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.deduct.service.BillSettlementService;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceDaoService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * .索赔单相关公共逻辑操作
 * <pre>
 * 2022-07-08日问题：
 * .有两个问题 第一个：红字信息不一定撤销成功  第二个：索赔状态需要更新为101，索赔单的结算单字段要置空
 * </pre>
 * @author Xforce
 */
@Service
@Slf4j
public class CommClaimService {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfBillDeductItemRefDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private OperateLogService operateLogService;
    @Autowired
    private TXfRedNotificationDao tXfRedNotificationDao;
    @Autowired
    private BillSettlementService billSettlementService;
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;
    @Autowired
    private RedNotificationOuterService redNotificationOuterService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private PreInvoiceDaoService preInvoiceDaoService;
    @Autowired
    private DeductBlueInvoiceService blueInvoiceService;
    @Autowired
    private StatementServiceImpl statementService;
    @Autowired
    private CommonMessageService commonMessageService;

    /**
     * <pre>
     * .作废整个索赔单流程
     * 1、先查询索赔结算单是否存在
     * 2、判断deduct表审核状态是否为CLAIM_WAIT_CHECK(107, "索赔单:待审核"),
     * 3、查询预制发票信息
     * 4、作废预制发票信息，如果已申请红字信息表，申请作废红字信息表
     * 5、没申请的红字信息表作废
     * 6、//修改结算单状态
     * 7、
     * </pre>
     * @param settlementId 结算单id
     * @return
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void destroyClaimSettlement(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //索赔单 查询待审核状态CLAIM_WAIT_CHECK(107, "索赔单:待审核"),
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper1 = new QueryWrapper<>();
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
        List<TXfBillDeductEntity> billDeductList1 = tXfBillDeductDao.selectList(billDeductEntityWrapper1);
		if (billDeductList1 == null || billDeductList1.size() == 0) {
			throw new EnhanceRuntimeException("索赔单状态不是待审核");
		}

        //索赔单 查询已生成结算单状态,CLAIM_MATCH_SETTLEMENT(106, "索赔单:已生成结算单"),
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper2 = new QueryWrapper<>();
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper2.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
        List<TXfBillDeductEntity> billDeductList2 = tXfBillDeductDao.selectList(billDeductEntityWrapper2);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改预制发票状态
        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
			if (StringUtils.isNotBlank(tXfPreInvoiceEntity.getRedNotificationNo())) {
				updateTXfPreInvoiceEntity.setRedNotificationNo("");
				// 撤销红字信息
				commRedNotificationService.confirmDestroyRedNotification(tXfPreInvoiceEntity.getId(), null);
			}
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);

			commonMessageService.sendPreInvoiceDiscardMessage(Lists.newArrayList(tXfPreInvoiceEntity.getId()));
        });

        //没申请的红字信息表作废
        QueryWrapper<TXfRedNotificationEntity> redNotificationEntityWrapper = new QueryWrapper<>();
        redNotificationEntityWrapper.eq(TXfRedNotificationEntity.BILL_NO, tXfSettlementEntity.getSettlementNo());
        List<Integer> applyIngStatus = new ArrayList<Integer>();
        applyIngStatus.add(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
        redNotificationEntityWrapper.in(TXfRedNotificationEntity.APPLYING_STATUS, applyIngStatus);
        List<TXfRedNotificationEntity> tXfRedNotificationEntityList = tXfRedNotificationDao.selectList(redNotificationEntityWrapper);
		Optional.ofNullable(tXfRedNotificationEntityList).ifPresent(tXfRedNotificationEntity -> tXfRedNotificationEntity.forEach(item->{
					TXfRedNotificationEntity updateTXfRedNotificationEntity = new TXfRedNotificationEntity();
					updateTXfRedNotificationEntity.setId(item.getId());
					updateTXfRedNotificationEntity.setStatus(0);
					updateTXfRedNotificationEntity.setRevertRemark("destroyClaim delete");
					tXfRedNotificationDao.updateById(updateTXfRedNotificationEntity);
		}));

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        updateTXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //释放索赔单明细额度（作废的索赔单）
        List<TXfBillDeductItemRefEntity> tXfBillDeductItemRefEntityList = tXfBillDeductItemRefDao.selectListJoin(tXfSettlementEntity.getSettlementNo(),
                TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode(), null);
        tXfBillDeductItemRefEntityList.forEach(tXfBillDeductItemRefEntity -> {
            TXfBillDeductItemEntity tXfBillDeductItemEntity = tXfBillDeductItemDao.selectById(tXfBillDeductItemRefEntity.getDeductItemId());
            //还原额度
            TXfBillDeductItemEntity updateTXfBillDeductItemEntity = new TXfBillDeductItemEntity();
            updateTXfBillDeductItemEntity.setId(tXfBillDeductItemEntity.getId());
            updateTXfBillDeductItemEntity.setRemainingAmount(tXfBillDeductItemRefEntity.getUseAmount().add(tXfBillDeductItemEntity.getRemainingAmount()));
            tXfBillDeductItemDao.updateById(updateTXfBillDeductItemEntity);
            //删除匹配关系
            TXfBillDeductItemRefEntity updateTXfBillDeductItemRefEntity = new TXfBillDeductItemRefEntity();
            updateTXfBillDeductItemRefEntity.setId(tXfBillDeductItemRefEntity.getId());
            updateTXfBillDeductItemRefEntity.setStatus(1);
            tXfBillDeductItemRefDao.updateById(updateTXfBillDeductItemRefEntity);
        });

        //修改索赔单状态
        //申请中的索赔单修改为：作废
        billDeductList1.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
            updateTXfBillDeductEntity.setRefSettlementNo("");
            updateTXfBillDeductEntity.setMakeInvoiceStatus(DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE.code());
            updateTXfSettlementEntity.setUpdateTime(new Date());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
            //日志
            operateLogService.addDeductLog(tXfBillDeduct.getId(), tXfBillDeduct.getBusinessType(), TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM, "", OperateLogEnum.DESTROY_SETTLEMENT_DEDUCT, "", UserUtil.getUserId(), UserUtil.getUserName());
        });
        //已生成结算单的索赔单修改为：待生成结算单 清空结算单编号
        if(billDeductList2 != null && billDeductList2.size() > 0) {
        	 billDeductList2.forEach(tXfBillDeduct -> {
                 TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
                 updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
                 updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode());
                 updateTXfBillDeductEntity.setUpdateTime(new Date());
                 updateTXfBillDeductEntity.setRefSettlementNo("");
                 updateTXfBillDeductEntity.setMakeInvoiceStatus(DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE.code());
                 tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
             });
        }

        //释放索赔单蓝票（作废的索赔单）
        List<String> billDeductBusinessNoList = billDeductList1.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList());
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper<>();
        tXfBillDeductInvoiceWrapper.in(TXfBillDeductInvoiceEntity.BUSINESS_NO, billDeductBusinessNoList);
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfDeductInvoiceBusinessTypeEnum.CLAIM_BILL.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TDxRecordInvoiceEntity> tDxInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_CODE, tXfBillDeductInvoiceEntity.getInvoiceCode());
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_NO, tXfBillDeductInvoiceEntity.getInvoiceNo());
            TDxRecordInvoiceEntity tDxInvoiceEntity = tDxRecordInvoiceDao.selectOne(tDxInvoiceEntityQueryWrapper);
            if (tDxInvoiceEntity != null) {
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(tDxInvoiceEntity.getId());
				if (tDxInvoiceEntity.getRemainingAmount() != null) {// 2022-08-11防止金额溢出
					updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
					if (updateTDxInvoiceEntity.getRemainingAmount().compareTo(tDxInvoiceEntity.getInvoiceAmount()) > 0) {
						updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getInvoiceAmount());
					}
				} else {
					updateTDxInvoiceEntity.setRemainingAmount(tDxInvoiceEntity.getInvoiceAmount());
				}
                tDxRecordInvoiceDao.updateById(updateTDxInvoiceEntity);
            }
            //删除蓝票关系
            TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoiceEntity.getId());
            updateTXfBillDeductInvoiceEntity.setStatus(1);
            tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
        });

        //2022-08-08新增，修改索赔单和索赔主表的关系
        List<TXfBillSettlementEntity> list = billSettlementService.queryBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
        if(list != null && list.size() > 0) {
			list.forEach(item -> {
				billSettlementService.cancelBillSettlementStatus(item.getBusinessNo(), item.getSettlementNo(), item.getBusinessType());
			});
        }
    }

    /**
     * 索赔结算单撤销流程-V2
     *
     * 已申请红字信息表的预制发票需先撤销，后会自动发起撤销结算单
     * @param settlementId 结算单id
     * @return response result中是需要撤销申请红字信息表的预制发票，为空时表示结算单撤销成功
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public R<List<RedNotificationMain>> destroyClaimSettlementV2(Long settlementId) {
        // 1.查询结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(tXfSettlementEntity, "结算单不存在");

        // 2.校验结算单状态
        String settlementStatusDesc = Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus())).map(TXfSettlementStatusEnum::getDesc).orElse("未知");
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(tXfSettlementEntity.getSettlementStatus()), String.format("该状态下[%s]结算单不支持撤销", settlementStatusDesc));

        // 3.查询预制发票
        R<List<RedNotificationMain>> res = preInvoiceDaoService.destroyPreInvoice(tXfSettlementEntity);
        if (CollectionUtil.isNotEmpty(res.getResult())) {
            log.info("结算单需先撤销红字信息表:{}", tXfSettlementEntity.getSettlementNo());
            res.getResult().forEach(redNotificationMain -> commRedNotificationService.confirmDestroyRedNotification(CommonUtil.toLong(redNotificationMain.getPid()), RedNoEventTypeEnum.DESTROY_SETTLEMENT));
            return R.ok(null, "红字撤销申请已发起");
        }

        // 4.修改结算单状态
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.DESTROY);

        // 5.释放业务单和蓝票
        releaseClaimBillAndBlueInvoice(tXfSettlementEntity);

        // 6.添加日志
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT,
                Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus())).map(TXfSettlementStatusEnum::getDesc).orElse(""),"",
                UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "结算单撤销成功，对应索赔业务单已恢复至待匹配状态");
    }

    /**
     * 释放业务单和蓝票
     */
    public void releaseClaimBillAndBlueInvoice(TXfSettlementEntity tXfSettlementEntity) {
        final Date now = new Date();
        // 1.索赔单 查询已生成结算单状态,CLAIM_MATCH_SETTLEMENT(106, "索赔单:已生成结算单"),
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper1 = new QueryWrapper<>();
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper1.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper1);
        Asserts.isFalse(CollectionUtil.isNotEmpty(billDeductList), "未找到已生成结算单的索赔单");

        // 2.释放索赔单明细额度（作废的索赔单）
        List<TXfBillDeductItemRefEntity> tXfBillDeductItemRefEntityList = tXfBillDeductItemRefDao
                .selectListJoin(tXfSettlementEntity.getSettlementNo(), TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode(), 0);
        tXfBillDeductItemRefEntityList.forEach(tXfBillDeductItemRefEntity -> {
            log.info("释放索赔单明细额度 {}", JSON.toJSONString(tXfBillDeductItemRefEntity));
            TXfBillDeductItemEntity tXfBillDeductItemEntity = tXfBillDeductItemDao.selectById(
                    tXfBillDeductItemRefEntity.getDeductItemId());
            // 还原额度  使用金额-diff金额+剩余可用金额
            TXfBillDeductItemEntity billDeductItemEntityU = new TXfBillDeductItemEntity();
            billDeductItemEntityU.setId(tXfBillDeductItemEntity.getId());
            billDeductItemEntityU.setRemainingAmount(tXfBillDeductItemRefEntity.getUseAmount().subtract(tXfBillDeductItemRefEntity.getDiffAmount())
                    .add(tXfBillDeductItemEntity.getRemainingAmount()));
            billDeductItemEntityU.setUpdateTime(now);
            tXfBillDeductItemDao.updateById(billDeductItemEntityU);
            // 删除匹配关系
            TXfBillDeductItemRefEntity billDeductItemRefEntityU = new TXfBillDeductItemRefEntity();
            billDeductItemRefEntityU.setId(tXfBillDeductItemRefEntity.getId());
            billDeductItemRefEntityU.setStatus(1);
            billDeductItemRefEntityU.setUpdateTime(now);
            tXfBillDeductItemRefDao.updateById(billDeductItemRefEntityU);
        });

        // 3.修改索赔单状态 已生成结算单的索赔单修改为：待匹配
        billDeductList.forEach(tXfBillDeduct -> {
            log.info("已生成结算单的索赔单修改为待匹配明细 deductId:{}",tXfBillDeduct.getId());
            TXfBillDeductEntity billDeductEntityU = new TXfBillDeductEntity();
            billDeductEntityU.setId(tXfBillDeduct.getId());
            billDeductEntityU.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
            billDeductEntityU.setRefSettlementNo("");
            billDeductEntityU.setMakeInvoiceStatus(DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE.code());
            billDeductEntityU.setUpdateTime(now);
            tXfBillDeductDao.updateById(billDeductEntityU);
            // 日志
            operateLogService.addDeductLog(tXfBillDeduct.getId(), tXfBillDeduct.getBusinessType(), TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM, "", OperateLogEnum.DESTROY_SETTLEMENT_DEDUCT, "", UserUtil.getUserId(), UserUtil.getUserName());
        });

        // 4.释放索赔单蓝票  批量处理，否则会因为参数超长报错
        List<Long> billDeductIdList = billDeductList.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
        ListUtils.partition(billDeductIdList, 1000).forEach(it -> Asserts.isFalse(blueInvoiceService.withdrawBlueInvoiceByDeduct(it), "释放索赔单蓝票失败"));

        // 5.修改索赔单和索赔主表的关系
        List<TXfBillSettlementEntity> list = billSettlementService.queryBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue());
        Optional.ofNullable(list).filter(CollectionUtil::isNotEmpty).ifPresent(tXfBillSettlementEntities -> {
            tXfBillSettlementEntities.forEach(item -> billSettlementService.cancelBillSettlementStatus(item.getBusinessNo(), item.getSettlementNo(), item.getBusinessType()));
        });
    }



    /**
     * 基本逻辑来源于
     * @see CommSettlementService#againSplitSettlementPreInvoice(Long)
     *
     * 不过，索赔单预制发票已申请红字信息表可以先撤销再进行重新拆票
     * @param settlementId 结算单id
     */
    public R<String> againSplitSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(tXfSettlementEntity, "结算单不存在");

        final String settlementNo = tXfSettlementEntity.getSettlementNo();
        // 校验结算单是否可以重新拆票
        // 0.只支持索赔单类型
        Asserts.isFalse(Objects.equals(tXfSettlementEntity.getBusinessType(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue()),
                "该操作只支持索赔单类型");
        // 1.非待开票状态结算单不可操作
        Asserts.isFalse(Objects.equals(tXfSettlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()),
                String.format("结算单[%s]不是待开票状态，无法操作", settlementNo));

        // 2.如预制发票待审核，禁止发起操作
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, settlementNo);
        preInvoiceEntityWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        log.info("againSplitClaimSettlementPreInvoice SETTLEMENT_NO:{}, pPreInvoiceList:{}", settlementNo,JsonUtil.toJsonStr(preInvoiceEntityList));

        List<TXfRedNotificationEntity> needCancelRedNotificationEntityList = Lists.newArrayList();
        // 3.预制发票状态判断
        if (preInvoiceEntityList != null && preInvoiceEntityList.size() > 0) {
            // 3.1.判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
            preInvoiceEntityList.removeIf(item -> StringUtils.isNotBlank(item.getInvoiceNo())
                    && StringUtils.isNotBlank(item.getInvoiceCode())
                    && blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode()));
        }
        if (preInvoiceEntityList != null && preInvoiceEntityList.size() > 0) {
            // 3.2.预制发票待审核 禁止发起操作
            Asserts.isTrue(preInvoiceEntityList.stream().anyMatch(preInvoice -> Objects.equals(preInvoice.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())), String.format("结算单[%s]已经待审核，请等待沃尔玛审核后再操作", settlementNo));
            // 3.3.已开票不能操作（预制发票已上传发票）
            Asserts.isTrue(preInvoiceEntityList.stream().anyMatch(preInvoice -> Objects.equals(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode(), preInvoice.getPreInvoiceStatus())), String.format("结算单[%s]已上传发票，请删除发票后再进行操作", settlementNo));

            // 3.4.判断红字信息表状态 红字信息表申请中或撤销申请中不能操作 已申请的可以先撤销再重拆
            List<Long> applyingRedPreInvoiceIdList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            log.info("againSplitClaimSettlementPreInvoice SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}", settlementNo, JsonUtil.toJsonStr(applyingRedPreInvoiceIdList));
            List<TXfRedNotificationEntity> redNotificationList = redNotificationOuterService.queryRedNotiByPreInvoiceId(applyingRedPreInvoiceIdList);
            Optional.ofNullable(redNotificationList).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
                log.info("againSplitClaimSettlementPreInvoice SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", settlementNo, JsonUtil.toJsonStr(txfRedNotificationEntity));
                Asserts.isTrue(Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLYING.getValue()), String.format("结算单[%s]有正在申请红字信息的预制发票", settlementNo));
                // 如果申请状态是 WAIT_TO_APPROVE(4,"撤销待审核"); 并且不等于ALREADY_ROLL_BACK(4,"已撤销")
                Asserts.isTrue(Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
                                && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())),
                        String.format("结算单[%s]红字信息表撤销中,请稍后再操作", settlementNo));

                if (Objects.equals(RedNoApplyingStatus.APPLIED.getValue(), txfRedNotificationEntity.getApplyingStatus())
                        && !Objects.equals(ApproveStatus.ALREADY_ROLL_BACK.getValue(), txfRedNotificationEntity.getApproveStatus())) {
                    needCancelRedNotificationEntityList.add(txfRedNotificationEntity);
                }
            }));
            if (!CollectionUtils.isEmpty(needCancelRedNotificationEntityList)) {
                // 3.5.撤销红字信息表
                log.info("已申请红字信息表的预制发票需要优先撤销申请:[{}]", needCancelRedNotificationEntityList.stream().map(TXfRedNotificationEntity::getRedNotificationNo).collect(Collectors.joining(",")));
                needCancelRedNotificationEntityList.forEach(redNotificationInfo ->
                        commRedNotificationService.confirmDestroyRedNotification(CommonUtil.toLong(redNotificationInfo.getPid()), RedNoEventTypeEnum.SPLIT_AGAIN));
                return R.ok(null, "预制发票红字信息表正在撤销中，请稍后查看拆票结果");
            }

            Asserts.isFalse(preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getAmountWithTax).reduce(BigDecimal.ZERO, BigDecimal::add).compareTo(tXfSettlementEntity.getAmountWithTax().negate()) == 0, String.format("结算单[%s]没有可申请的预制发票(红字信息)", settlementNo));
            // 3.6.先删除没开具的红字信息表
            redNotificationOuterService.deleteRednotification(applyingRedPreInvoiceIdList, "修改开票信息");
            // 3.7.删除结算单之前已作废的预制发票（没有红字信息、作废）避免申请逻辑状态判断问题
            preInvoiceDaoService.deletePreInvoice(applyingRedPreInvoiceIdList, UserUtil.getUserId());
            // 3.8.发送作废删除事件
            commonMessageService.sendPreInvoiceDeleteMessage(applyingRedPreInvoiceIdList);
        }
        // 6.重新拆票
        // 6.1.修改结算单为待拆票
        TXfSettlementEntity settlementEntityU = new TXfSettlementEntity();
        settlementEntityU.setId(tXfSettlementEntity.getId());
        settlementEntityU.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
        settlementEntityU.setUpdateTime(new Date());
        tXfSettlementDao.updateById(settlementEntityU);
        // 6.2.重新拆票
        preinvoiceService.splitPreInvoice(settlementNo, tXfSettlementEntity.getSellerNo());
        // 7.日志添加
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
                Optional.ofNullable(TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()))
                        .map(TXfSettlementStatusEnum::getDesc).orElse(""),"",
                UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "重新拆票成功");
    }

    /**
     * 判断结算单下红字信息表均已作废后发起重新拆票
     * @param tXfPreInvoiceEntity 红字信息表作废相关预制发票信息
     */
    public void splitAgain(TXfPreInvoiceEntity tXfPreInvoiceEntity) {
        // 重新查询预制发票信息
        TXfPreInvoiceEntity preInvoiceEntity = tXfPreInvoiceDao.selectById(tXfPreInvoiceEntity.getId());
        if (Objects.equals(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode(), preInvoiceEntity.getPreInvoiceStatus())) {
            log.info("结算单已经重新拆票，无需再次处理:[{}]-[{}]", preInvoiceEntity.getSettlementNo(), preInvoiceEntity.getId());
            return;
        }

        // 查询（没有红字信息、作废）的预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, preInvoiceEntity.getSettlementId());
        preInvoiceWrapper.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Lists.newArrayList(TXfPreInvoiceStatusEnum.DESTROY.getCode(), TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(), TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode()));
        List<TXfPreInvoiceEntity> tXfPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceWrapper);
        if (tXfPreInvoiceList.stream().anyMatch(preInvoice -> StringUtils.isNotBlank(preInvoice.getRedNotificationNo()))) {
            log.warn("结算单下还有未撤销红字信息表的预制发票，无法重新拆票:[{}]", preInvoiceEntity.getSettlementNo());
            return;
        }
        // 删除结算单之前已作废的预制发票（没有红字信息、作废）避免申请逻辑状态判断问题
        List<Long> preInvoiceIdList = tXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        preInvoiceDaoService.deletePreInvoice(preInvoiceIdList, UserUtil.getUserId());
        // 发送作废删除事件
        commonMessageService.sendPreInvoiceDeleteMessage(preInvoiceIdList);

        // 重新拆票
        TXfSettlementEntity settlementEntityU = new TXfSettlementEntity();
        settlementEntityU.setId(preInvoiceEntity.getSettlementId());
        settlementEntityU.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
        settlementEntityU.setUpdateTime(new Date());
        tXfSettlementDao.updateById(settlementEntityU);
        preinvoiceService.splitPreInvoice(preInvoiceEntity.getSettlementNo(), preInvoiceEntity.getSellerNo());
        // 日志添加
        operateLogService.add(preInvoiceEntity.getSettlementId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
                TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getDesc(),"",
                UserUtil.getUserId(), UserUtil.getUserName());
    }
}