package com.xforceplus.wapp.service;

import static com.xforceplus.wapp.common.utils.CommonUtil.assertFalse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveTypeEnum;
import com.xforceplus.wapp.modules.deduct.dto.SettlementCancelRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceDaoService;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.deduct.service.BillSettlementService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;

import lombok.extern.slf4j.Slf4j;

/**
 * 协议单相关公共逻辑操作
 * @author Xforce
 */
@Service
@Slf4j
public class CommAgreementService {
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    @Lazy
    private RedNotificationOuterService redNotificationOuterService;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;
    @Autowired
    private BillSettlementService billSettlementService;
    @Autowired
    private OperateLogService operateLogService;
    @Autowired
    private TXfSettlementItemInvoiceDetailDao tXfSettlementItemInvoiceDetailDao;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;
    @Lazy
    @Autowired
    private PreInvoiceDaoService preInvoiceDaoService;
    @Autowired
    private StatementServiceImpl statementService;
    @Autowired
    private CommonMessageService commonMessageService;

    private final List<Integer> canDestroyStatus;

    public CommAgreementService() {

        canDestroyStatus= Arrays.asList(
                TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode()
                ,TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_CONFIRM.getCode()
                ,TXfSettlementStatusEnum.WAIT_CHECK.getCode()
        );
    }

    /**
     * 释放协议单 作废结算单 蓝票释放额度 如果有预制发票 作废预制发票
     * 协议单还可以再次使用
     *
     * @param settlementId 结算单id
     * @return
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public void destroyAgreementSettlement(Long settlementId) {
        if (settlementId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        log.info("历史结算单撤销:{}", settlementId);

        if (Objects.equals(tXfSettlementEntity.getSettlementStatus(),TXfSettlementStatusEnum.DESTROY.getCode())){
            throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经作废不能再次被作废");
        }

        if (!canDestroyStatus.contains(tXfSettlementEntity.getSettlementStatus())) {
            throw new EnhanceRuntimeException("结算单已上传红票不能操作");
        }
        //协议单
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> pPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        
        //2022-05-05 magaofeng ADD 如预制发票待审核，禁止发起操作
        log.info("destroyAgreementSettlement SETTLEMENT_NO:{}, pPreInvoiceList:{}", tXfSettlementEntity.getSettlementNo() ,JsonUtil.toJsonStr(pPreInvoiceList));
        Optional.ofNullable(pPreInvoiceList).ifPresent(x -> x.forEach(tXfPreInvoiceEntity -> {
        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())) {
        		throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经待审核,请等待沃尔玛审核后再操作");
        	}
//        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode())) {
//        		throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]正在申请红字信息中,请稍后再操作");
//        	}
        	
        }));
        //是否正在申请红字
        //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件申请败了，这个时候可以重新申请预制发票的红字信息)
        //是否有申请中的红字信息 或者 是否有审核通过
		if (pPreInvoiceList != null && pPreInvoiceList.size() > 0) {
			// 判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
			pPreInvoiceList.removeIf(item -> {
				return blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode());
			});
		}
		if(pPreInvoiceList != null && pPreInvoiceList.size() > 0) {//判断红字信息表状态
			List<Long> applyingRedPreInvoiceIdList = pPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
			log.info("destroyAgreementSettlement SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}",	tXfSettlementEntity.getSettlementNo(), JsonUtil.toJsonStr(applyingRedPreInvoiceIdList));
			List<TXfRedNotificationEntity> redNotificationlist = redNotificationOuterService.queryRedNotiByPreInvoiceId(applyingRedPreInvoiceIdList);
			Optional.ofNullable(redNotificationlist).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
				log.info("destroyAgreementSettlement SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", tXfSettlementEntity.getSettlementNo(), JsonUtil.toJsonStr(txfRedNotificationEntity));
				if ((Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLIED.getValue()) && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())))
						|| Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLYING.getValue())) {
					throw new EnhanceRuntimeException("结算单[" + tXfSettlementEntity.getSettlementNo() + "]有正在申请或者已申请红字信息的预制发票");
				}
				// 如果申请状态是 WAIT_TO_APPROVE(4,"撤销待审核"); 并且不等于ALREADY_ROLL_BACK(4,"已撤销")
				if (Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue()) && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue()))) {
					throw new EnhanceRuntimeException("结算单[" + tXfSettlementEntity.getSettlementNo() + "]红字信息表撤销中,请稍后再操作");
				}
			}));
		}
        //2022-05-05 end
        //修改作废状态====
        //作废结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        updateTXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改协议单状态
        billDeductList.forEach(billDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(billDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
            updateTXfBillDeductEntity.setRefSettlementNo("");
            updateTXfBillDeductEntity.setMakeInvoiceStatus(DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE.code());
            updateTXfBillDeductEntity.setUpdateTime(new Date());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //作废预制发票
        Optional.ofNullable(pPreInvoiceList).ifPresent(x->x.forEach(tXfPreInvoiceEntity -> {

            assertFalse(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(),TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()),
                    "结算单中有预制发票已经申请红字信息表:["+tXfPreInvoiceEntity.getRedNotificationNo()+"],请撤销后重新操作"
            );

            assertFalse(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(),TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()),
                    "结算单存在已开红票[发票号码:"+tXfPreInvoiceEntity.getInvoiceNo()+",发票代码:"+tXfPreInvoiceEntity.getInvoiceCode()+"]，请作废或蓝冲后重新操作"
            );


            if (Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(),TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode())
            		|| Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(),TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode())){
                commRedNotificationService.deleteRedNotification(tXfPreInvoiceEntity.getId(),"作废结算单取消申请红字信息表");
            }

            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
//            if(StringUtils.isNotBlank(tXfPreInvoiceEntity.getRedNotificationNo())) {
//                updateTXfPreInvoiceEntity.setRedNotificationNo("");
//                // 撤销红字信息
//                commRedNotificationService.confirmDestroyRedNotification(tXfPreInvoiceEntity.getId());
//            }
            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);

            commonMessageService.sendPreInvoiceDiscardMessage(Lists.newArrayList(tXfPreInvoiceEntity.getId()));
        }));

        //释放结算单蓝票
        QueryWrapper<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceWrapper = new QueryWrapper<>();
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_NO, tXfSettlementEntity.getSettlementNo());
        tXfBillDeductInvoiceWrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_TYPE, TXfDeductInvoiceBusinessTypeEnum.SETTLEMENT.getType());

        //还原蓝票额度
        List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceList = tXfBillDeductInvoiceDao.selectList(tXfBillDeductInvoiceWrapper);
        tXfBillDeductInvoiceList.forEach(tXfBillDeductInvoiceEntity -> {
            QueryWrapper<TDxRecordInvoiceEntity> tDxInvoiceEntityQueryWrapper = new QueryWrapper<>();
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_CODE, tXfBillDeductInvoiceEntity.getInvoiceCode());
            tDxInvoiceEntityQueryWrapper.eq(TDxRecordInvoiceEntity.INVOICE_NO, tXfBillDeductInvoiceEntity.getInvoiceNo());
            TDxRecordInvoiceEntity txInvoiceEntity = tDxRecordInvoiceDao.selectOne(tDxInvoiceEntityQueryWrapper);
            if(txInvoiceEntity != null) {
                TDxRecordInvoiceEntity updateTDxInvoiceEntity = new TDxRecordInvoiceEntity();
                updateTDxInvoiceEntity.setId(txInvoiceEntity.getId());
                if (txInvoiceEntity.getRemainingAmount() != null) {// 2022-08-11防止金额溢出
					updateTDxInvoiceEntity.setRemainingAmount(txInvoiceEntity.getRemainingAmount().add(tXfBillDeductInvoiceEntity.getUseAmount()));
					if (updateTDxInvoiceEntity.getRemainingAmount().compareTo(txInvoiceEntity.getInvoiceAmount()) > 0) {
						updateTDxInvoiceEntity.setRemainingAmount(txInvoiceEntity.getInvoiceAmount());
					}
				} else {
					updateTDxInvoiceEntity.setRemainingAmount(txInvoiceEntity.getInvoiceAmount());
				}
                tDxRecordInvoiceDao.updateById(updateTDxInvoiceEntity);
            }
            //删除蓝票关系
            TXfBillDeductInvoiceEntity updateTXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            updateTXfBillDeductInvoiceEntity.setId(tXfBillDeductInvoiceEntity.getId());
            updateTXfBillDeductInvoiceEntity.setStatus(1);
            tXfBillDeductInvoiceDao.updateById(updateTXfBillDeductInvoiceEntity);
        });
        
        //2022-08-08新增，修改业务单和结算单的关系
        List<TXfBillSettlementEntity> list = billSettlementService.queryBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
		log.info("agreement billSettlementService.cancelBillSettlementStatus:{}, list:{}", list);
        if(list != null && list.size() > 0) {
			list.forEach(item -> {
				billSettlementService.cancelBillSettlementStatus(item.getBusinessNo(), item.getSettlementNo(), item.getBusinessType());
			});
        }
    }

    /**
     * 协议结算单撤销流程-V2
     * @param request 结算单撤销请求信息
     */
    @Deprecated
    @Transactional(rollbackFor = Exception.class)
    public R<String> destroyAgreementSettlementV2(SettlementCancelRequest request) {
        Long settlementId = request.getSettlementId();
        String applyReason = request.getRevertRemark();
        Integer type = request.getType();
        // 1.查询结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(tXfSettlementEntity, "结算单不存在");

        String settlementNo = tXfSettlementEntity.getSettlementNo();
        // 2.结算单状态校验
        Asserts.isTrue(Objects.equals(tXfSettlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.WAIT_CHECK.getCode()), String.format("结算单[%s]已经待审核，请等待沃尔玛审核后再操作", settlementNo));
        Asserts.isTrue(Objects.equals(tXfSettlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.DESTROY.getCode()), String.format("结算单[%s]已经作废不能再次被作废", settlementNo));
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(tXfSettlementEntity.getSettlementStatus()), "结算单已上传红票不能操作");

        // 3.待开票状态 需要填写撤销原因
        if (TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(tXfSettlementEntity.getSettlementStatus())) {
            Asserts.isTrue(StringUtils.isBlank(applyReason), "请先填写撤销原因");
            Asserts.isTrue(applyReason.length() > 200, "撤销原因不应超过200字符");

            // 进入待审核状态
            statementService.updateSettlementStatus(settlementId, TXfSettlementStatusEnum.WAIT_CHECK, applyReason, SettlementApproveTypeEnum.fromCode(type), SettlementApproveStatusEnum.APPROVING, null);
            return R.ok(null, "撤销申请已发出，请等待沃尔玛审核后再操作");
        }

        // 4.作废结算单状态
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.DESTROY);

        // 5.释放业务单及蓝票
        releaseAgreementBillAndBlueInvoice(tXfSettlementEntity);
        // 6.添加日志
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT,
                TXfSettlementStatusEnum.DESTROY.getDesc(),"",
                UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "结算单撤销成功，对应协议业务单已恢复至待匹配状态");
    }


    /**
     * 协议结算单删除
     * @param tXfSettlementEntity 结算单信息
     */
    @Transactional(rollbackFor = Exception.class)
    public R<String> deleteAgreementSettlement(TXfSettlementEntity tXfSettlementEntity) {
        // 2.结算单状态校验
        if (TXfSettlementStatusEnum.DESTROY.getCode().equals(tXfSettlementEntity.getSettlementStatus())) {
            return R.ok(null, "结算单删除成功，对应协议业务单已恢复至待匹配状态");
        }
        if (!TXfSettlementStatusEnum.isCanDestroy(tXfSettlementEntity.getSettlementStatus())) {
            return R.ok(null, "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
        }

        // 3.预制发票删除
        R<Boolean> checkRes = preInvoiceDaoService.checkPreInvoice(tXfSettlementEntity);
        if (Objects.nonNull(checkRes.getResult()) && checkRes.getResult()) {
            return R.ok(null, "存在协议单已生成结算单，请刷新关闭当前页并刷新列表重试");
        }
        preInvoiceDaoService.deletePreInvoice(tXfSettlementEntity);

        // 4.删除结算单状态
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.DELETED);

        // 5.释放业务单及蓝票
        releaseAgreementBillAndBlueInvoice(tXfSettlementEntity);
        // 6.添加日志
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DELETE_SETTLEMENT,
                TXfSettlementStatusEnum.DELETED.getDesc(),"",
                UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "结算单删除成功，对应协议业务单已恢复至待匹配状态");
    }

    /**
     * 结算单回到待确认状态
     * @param tXfSettlementEntity 结算单信息
     * @return
     */
    public R<String> backToWaitConfirm(TXfSettlementEntity tXfSettlementEntity) {
        // 1.结算单状态校验
        Asserts.isTrue(Objects.equals(tXfSettlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.DESTROY.getCode()), String.format("结算单[%s]已经作废不能操作", tXfSettlementEntity.getSettlementNo()));
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(tXfSettlementEntity.getSettlementStatus()), "结算单已上传红票不能操作");

        // 2.删除预制发票
        R<Boolean> checkRes = preInvoiceDaoService.checkPreInvoice(tXfSettlementEntity);
        Asserts.isTrue(Objects.nonNull(checkRes.getResult()) && checkRes.getResult(), "结算单已申请红字信息表");
        preInvoiceDaoService.deletePreInvoice(tXfSettlementEntity);

        // 3.回到待确认状态
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.WAIT_CONFIRM);
        return R.ok(null, "结算单回到待确认状态");
    }

    /**
     * 释放协议业务单和蓝票
     */
    public void releaseAgreementBillAndBlueInvoice(TXfSettlementEntity settlementEntity){
        //1.获取协议单列表并校验不为空
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, settlementEntity.getSettlementNo());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);
        if (CollectionUtils.isEmpty(billDeductList)){
            log.warn("待释放协议单列表为空 settlementId:{}",settlementEntity.getId());
            throw new EnhanceRuntimeException("待释放协议单列表为空");
        }

        //2.释放结算单明细与蓝票明细占用关系
        QueryWrapper<TXfSettlementItemInvoiceDetailEntity> settlementItemInvoiceDetailQ = new QueryWrapper<>();
        settlementItemInvoiceDetailQ.eq(TXfSettlementItemInvoiceDetailEntity.SETTLEMENT_ID,settlementEntity.getId());
        settlementItemInvoiceDetailQ.eq(TXfSettlementItemInvoiceDetailEntity.STATUS,0);
        TXfSettlementItemInvoiceDetailEntity settlementItemInvoiceDetailU = new TXfSettlementItemInvoiceDetailEntity();
        settlementItemInvoiceDetailU.setStatus(1);
        settlementItemInvoiceDetailU.setUpdateTime(new Date());
        tXfSettlementItemInvoiceDetailDao.update(settlementItemInvoiceDetailU,settlementItemInvoiceDetailQ);

        //3.释放业务单蓝票占用关系
        List<Long> deductIdList = billDeductList.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
        deductBlueInvoiceService.withdrawBlueInvoiceByDeduct(deductIdList);

        //4.释放业务单状态
        QueryWrapper<TXfBillDeductEntity> billDeductQ = new QueryWrapper<>();
        billDeductQ.in(TXfBillDeductEntity.ID,deductIdList);
        TXfBillDeductEntity billDeductU = new TXfBillDeductEntity();
        billDeductU.setStatus(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
        billDeductU.setRefSettlementNo(StringUtils.EMPTY);
        billDeductU.setUpdateTime(new Date());
        tXfBillDeductDao.update(billDeductU,billDeductQ);

        billDeductList.forEach(deduct -> {
            // 添加日志
            operateLogService.addDeductLog(deduct.getId(), deduct.getBusinessType(), TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT, "", OperateLogEnum.DESTROY_SETTLEMENT_DEDUCT, "", UserUtil.getUserId(), UserUtil.getUserName());
        });

        // 5.修改协议单和协议主表的关系
        List<TXfBillSettlementEntity> list = billSettlementService.queryBySettlementNo(settlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue());
        Optional.ofNullable(list).filter(CollectionUtil::isNotEmpty).ifPresent(tXfBillSettlementEntities -> {
            tXfBillSettlementEntities.forEach(item -> billSettlementService.cancelBillSettlementStatus(item.getBusinessNo(), item.getSettlementNo(), item.getBusinessType()));
        });
    }

    /**
     * 这个主要是针对作废的预制发票明细处理
     * 修改后的结算单的中的部分预制发票明细重新去拆票（申请红字信息），删除之前的预制发票
     *
     * @param settlementId
     * @param preInvoiceItemList
     */
    public void againSplitPreInvoice(Long settlementId, List<TXfPreInvoiceItemEntity> preInvoiceItemList) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        if(CollectionUtils.isEmpty(preInvoiceItemList)){
            throw new EnhanceRuntimeException("结算单无数据可拆分预制发票");
        }
        preinvoiceService.reSplitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo(), preInvoiceItemList);
        //删除之前的预制发票，避免申请逻辑状态判断问题
        List<Long> preInvoiceIdList = preInvoiceItemList.stream().map(TXfPreInvoiceItemEntity::getPreInvoiceId).collect(Collectors.toList());

        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());

        QueryWrapper<TXfPreInvoiceEntity> deletePreInvoiceWrapper = new QueryWrapper<>();
        deletePreInvoiceWrapper.in(TXfPreInvoiceEntity.ID, preInvoiceIdList);
        tXfPreInvoiceDao.update(updateTXfPreInvoiceEntity, deletePreInvoiceWrapper);

        commonMessageService.sendPreInvoiceDeleteMessage(preInvoiceIdList);
    }

    /**
     * 协议单[确认]按钮相关逻辑，这个主要是针对结算单明细拆票
     * 结算单明细拆成预制发票（红字信息）
     * 底层逻辑调用产品服务(拆票、申请红字信息)
     *
     * @param settlementId
     */
    @Transactional(rollbackFor = Exception.class)
    public void splitPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        preinvoiceService.splitPreInvoice(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
    }

    public R splitAgain(Long settlementId) {
        // 查询结算单
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(settlementEntity, "结算单不存在");

        // 结算单状态校验
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(settlementEntity.getSettlementStatus()), "结算单已上传红票不能操作");

        // 删除预制发票
        R<Boolean> checkRes = preInvoiceDaoService.checkPreInvoice(settlementEntity);
        Asserts.isTrue(Objects.nonNull(checkRes.getResult()) && checkRes.getResult(), "结算单已申请红字信息表");
        preInvoiceDaoService.deletePreInvoice(settlementEntity);

        // 回到待拆票状态
        statementService.updateSettlementStatus(settlementEntity.getId(), TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE);

        // 重新拆票
        return preinvoiceService.splitPreInvoice(settlementEntity,false);
    }

}
