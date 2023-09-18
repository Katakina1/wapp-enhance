package com.xforceplus.wapp.service;

import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.xforceplus.apollo.core.domain.settlementstatus.SettlementStatus;
import com.xforceplus.wapp.client.CacheClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.AgreementRedNotificationStatus;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.constants.Constants;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.enums.settlement.SettlementApproveStatusEnum;
import com.xforceplus.wapp.enums.settlement.SettlementApproveTypeEnum;
import com.xforceplus.wapp.modules.audit.enums.AuditStatusEnum;
import com.xforceplus.wapp.modules.audit.service.InvoiceAuditService;
import com.xforceplus.wapp.modules.deduct.dto.SettlementCancelRequest;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.preinvoice.service.PreInvoiceDaoService;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationRollbackFailResult;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.settlement.dto.SettlementApproveInfo;
import com.xforceplus.wapp.modules.settlement.dto.SettlementApproveRequest;
import com.xforceplus.wapp.modules.statement.service.StatementServiceImpl;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.util.TransactionUtils;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
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
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.preinvoice.service.PreinvoiceService;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 结算单公共逻辑
 * 1、作废预制发票
 * 2、重新申请预制发票 拆票
 * @author Xforce
 */
@Service
@Slf4j
public class CommSettlementService {
	@Autowired
	private TXfPreInvoiceDao tXfPreInvoiceDao;
	@Autowired
	private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
	@Autowired
	private TXfSettlementDao tXfSettlementDao;
	@Autowired
	private BlueInvoiceRelationService blueInvoiceRelationService;
	@Autowired
	private CommRedNotificationService commRedNotificationService;
	@Autowired
	private PreinvoiceService preinvoiceService;
	@Autowired
	@Lazy
	private RedNotificationOuterService redNotificationOuterService;
	@Autowired
    private RedNotificationMainService redNotificationMainService;
	@Autowired
	private OperateLogService operateLogService;
	@Lazy
	@Autowired
    private PreInvoiceDaoService preInvoiceDaoService;
	@Lazy
	@Autowired
    private CommClaimService commClaimService;
	@Lazy
	@Autowired
    private CommAgreementService commAgreementService;
	@Autowired
    private StatementServiceImpl statementService;
	@Autowired
    private CommonMessageService commonMessageService;
	@Autowired
    private InvoiceAuditService invoiceAuditService;
	@Autowired
    private TXfDeductPreInvoiceDao tXfDeductPreInvoiceDao;
	@Autowired
    private CacheClient cacheClient;
	@Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;

    /**
     * 申请-作废结算单预制发票（红字信息）
     * 1、结算单状态不变
     * 2、预制发票状态改为待审核
     * 供应商调用
     *
     * @param settlementId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyDestroySettlementPreInvoice(Long settlementId,String remark) {
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
                commRedNotificationService.applyDestroyRedNotification(tXfPreInvoiceEntity.getId(),remark);
            });
        }
        //预制发票
        //2、当预制发票申请ing红字信息编码时，直接作废沃尔玛申请单，预制发票改为没有红字信息状态
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper2 = new QueryWrapper<>();
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceEntityWrapper2.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList2 = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper2);
        if (!CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            tXfPreInvoiceEntityList2.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
                commRedNotificationService.deleteRedNotification(tXfPreInvoiceEntity.getId(),remark);
            });
        }

        if (CollectionUtils.isEmpty(tXfPreInvoiceEntityList) && CollectionUtils.isEmpty(tXfPreInvoiceEntityList2)) {
            throw new EnhanceRuntimeException("结算单没有可撤销的预制发票(红字信息)");
        }

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        String desc =  "";
        if(StringUtils.isNotBlank(remark)){
            desc = "【撤销原因："+remark+"】";
        }
        operateLogService.add(settlementId, OperateLogEnum.CANCEL_RED_NOTIFICATION_APPLY,TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),desc,
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
    @Transactional(rollbackFor = Exception.class)
    public void rejectDestroySettlementPreInvoice(Long settlementId,String remark) {
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
        if(!CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            //修改预制发票状态
            tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
                TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
                updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
                updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
                tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
            });
            //日志
            TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
            String desc = "";
            if(StringUtils.isNotBlank(remark)){
                desc = "【驳回原因："+remark+"】";
            }
            operateLogService.add(settlementId, OperateLogEnum.REJECT_CANCEL_RED_NOTIFICATION_APPLY, TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),desc,
                    UserUtil.getUserId(), UserUtil.getUserName());
        }
    }

    /**
     * 通过-作废结算单待审核的预制发票（红字信息）
     * 1、结算单状态不变
     * 2、预制发票状态改为已作废，清空红字信息字段
     * 沃尔玛调用
     *
     * @param settlementId
     */
    @Transactional(rollbackFor = Exception.class)
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
        if(!CollectionUtils.isEmpty(tXfPreInvoiceEntityList)) {
            //修改预制发票状态
            tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
                destroyPreInvoice(tXfPreInvoiceEntity.getId());
            });
            // 发送作废事件消息
            commonMessageService.sendPreInvoiceDiscardMessage(tXfPreInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList()));
            //日志
            TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
            operateLogService.add(settlementId, OperateLogEnum.AGREE_CANCEL_RED_NOTIFICATION_APPLY,
                    TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                    UserUtil.getUserId(), UserUtil.getUserName());
        }
    }

    /**
     * 直接作废预制发票，但是不能作废红字信息（这个时候主要给蓝冲使用的）
     *
     * @param preInvoiceId
     */
    public void destroyPreInvoice(Long preInvoiceId) {
        if (preInvoiceId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
        updateTXfPreInvoiceEntity.setId(preInvoiceId);
        updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        updateTXfPreInvoiceEntity.setRedNotificationNo("");
        tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
    }

    /**
     * 申请作废预制发票,同时申请废红字信息
     * （这个时候主要给删除红票后 再删除红字信息使用）
     *
     * @param preInvoiceId
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyDestroyPreInvoiceAndRedNotification(Long preInvoiceId,String remark) {
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
        commRedNotificationService.applyDestroyRedNotification(preInvoiceId,remark);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(preInvoiceEntity.getSettlementId());
        operateLogService.add(settlement.getId(), OperateLogEnum.CANCEL_RED_NOTIFICATION_APPLY,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                UserUtil.getUserId(),UserUtil.getUserName());
    }

    /**
     * 检查结算单是否能重新拆票（红字信息）
     * 如果不能则会抛出异常提示 调用方捕获异常处理相关后续逻辑
     *
     * @param settlementId
     * @return
     */
    public void checkAgainSplitSettlementPreInvoice(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        QueryWrapper<TXfPreInvoiceEntity> applyingRedPreInvoiceWrapper = new QueryWrapper<>();
        applyingRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        applyingRedPreInvoiceWrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
        List<TXfPreInvoiceEntity> applyingRedPreInvoiceList = tXfPreInvoiceDao.selectList(applyingRedPreInvoiceWrapper);
        List<Long> applyingRedPreInvoiceIdList = applyingRedPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        //是否正在申请红字
        //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件神申请败了，这个时候可以重新申请预制发票的红字信息)
        //是否有申请中的红字信息 或者 是否有审核通过
        if(!CollectionUtils.isEmpty(applyingRedPreInvoiceIdList)) {
            boolean hasApplyWappRed = redNotificationOuterService.isWaitingApplyByPreInvoiceId(applyingRedPreInvoiceIdList);
            if (hasApplyWappRed){
                throw new EnhanceRuntimeException("不能重新申请预制发票(红字信息)，【有正在申请或者已申请红字信息的预制发票】");
            }
        }
    }

    /**
     * 通过预制发票id查询结算单 然后同意作废结算单下面的预制发票
     * 多选预制发票去作废
     * 沃尔玛调用 同意作废红字的时候
     *
     * @param preInvoiceIdList 预制发票id
     */
    @Transactional(rollbackFor = Exception.class)
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
                .map(TXfPreInvoiceEntity::getSettlementId).distinct().collect(Collectors.toList());
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
    @Transactional(rollbackFor = Exception.class)
    public void rejectDestroySettlementPreInvoiceByPreInvoiceId(List<Long> preInvoiceIdList,String remark) {
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
            rejectDestroySettlementPreInvoice(tXfSettlementEntity.getId(),remark);
        });
    }

    /**
     * 供应商调用
     * 结算单的预制发票（没有红字信息、作废状态）重新拆分预制发票（红字信息）
     *
     * @param settlementId
     */
    public void againSplitSettlementPreInvoice(Long settlementId) {
    	log.info("againSplitSettlementPreInvoice:{}", settlementId);
        //检查结算单是否能重新拆票
        checkAgainSplitSettlementPreInvoice(settlementId);
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }

        if (Objects.equals(tXfSettlementEntity.getSettlementStatus(),TXfSettlementStatusEnum.DESTROY.getCode())){
            throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经作废不能操作");
        }
        
        //2022-05-05 magaofeng ADD 如预制发票待审核，禁止发起操作
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> pPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        log.info("againSplitSettlementPreInvoice SETTLEMENT_NO:{}, pPreInvoiceList:{}", tXfSettlementEntity.getSettlementNo() ,JsonUtil.toJsonStr(pPreInvoiceList));
        Optional.ofNullable(pPreInvoiceList).ifPresent(x -> x.forEach(tXfPreInvoiceEntity -> {
        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())) {
        		throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经待审核,请等待沃尔玛审核后再操作");
        	}
        }));
        //是否正在申请红字
        //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件神申请败了，这个时候可以重新申请预制发票的红字信息)
        //是否有申请中的红字信息 或者 是否有审核通过
        if (pPreInvoiceList != null && pPreInvoiceList.size() > 0) {
			// 判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
			pPreInvoiceList.removeIf(item -> {
				return blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode());
			});
		}
        if(pPreInvoiceList != null && pPreInvoiceList.size() > 0) {//判断红字信息表状态
        	List<Long> applyingRedPreInvoiceIdList = pPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
			log.info("destroyEpdSettlement SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}", tXfSettlementEntity.getSettlementNo(), JsonUtil.toJsonStr(applyingRedPreInvoiceIdList));
			List<TXfRedNotificationEntity> redNotificationlist = redNotificationOuterService.queryRedNotiByPreInvoiceId(applyingRedPreInvoiceIdList);
			Optional.ofNullable(redNotificationlist).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
				log.info("destroyEpdSettlement SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", tXfSettlementEntity.getSettlementNo(), JsonUtil.toJsonStr(txfRedNotificationEntity));
				if ((Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLIED.getValue()) && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())))
						|| Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLYING.getValue())) {
					throw new EnhanceRuntimeException("结算单[" + tXfSettlementEntity.getSettlementNo() + "]有正在申请或者已申请红字信息的预制发票");
				}
				// 如果申请状态是 WAIT_TO_APPROVE(4,"撤销待审核"); 并且不等于ALREADY_ROLL_BACK(4,"已撤销")
				if (Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
						&& !(Objects.equals(txfRedNotificationEntity.getApproveStatus(),ApproveStatus.ALREADY_ROLL_BACK.getValue()))) {
					throw new EnhanceRuntimeException("结算单[" + tXfSettlementEntity.getSettlementNo() + "]红字信息表撤销中,请稍后再操作");
				}
			}));
        }
        //2022-05-05 end

        //没有红字信息、作废
        List<Integer> againSplitStatusList = new ArrayList<>();
        againSplitStatusList.add(TXfPreInvoiceStatusEnum.DESTROY.getCode());
        againSplitStatusList.add(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        // 查询（没有红字信息、作废）的预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        preInvoiceWrapper.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, againSplitStatusList);

        List<TXfPreInvoiceEntity> tXfPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceWrapper);
        if (CollectionUtils.isEmpty(tXfPreInvoiceList)) {
            throw new EnhanceRuntimeException("结算单没有可申请的预制发票(红字信息)");
        }
        List<Long> preInvoiceIdList = tXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        //2022-05-15 先删除没开具的红字信息表
        redNotificationOuterService.deleteRednotification(preInvoiceIdList, "2 修改限额 3不做任何修改重新拆票");
        //2022-05-15 end
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

        commonMessageService.sendPreInvoiceDeleteMessage(preInvoiceIdList);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlement.getId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                UserUtil.getUserId(),UserUtil.getUserName());
    }


    /**
     * 判断结算单下红字信息表均已作废，并撤销结算单
     * @param tXfPreInvoiceEntity 撤销红字信息表相关预制发票
     */
    @Transactional(rollbackFor = Exception.class)
    public void destroySettlement(TXfPreInvoiceEntity tXfPreInvoiceEntity) {
        // 重新查询结算单
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(tXfPreInvoiceEntity.getSettlementId());
        if (TXfSettlementStatusEnum.DESTROY.getCode().equals(settlementEntity.getSettlementStatus())) {
            log.info("结算单已经被作废，无需处理:[{}]-[{}]", settlementEntity.getSettlementNo(), tXfPreInvoiceEntity.getId());
            return;
        }

        // 查询预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementEntity.getId());
        preInvoiceWrapper.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Lists.newArrayList(TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode(), TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(), TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode()));
        preInvoiceWrapper.ne(TXfPreInvoiceEntity.RED_NOTIFICATION_NO, StringUtils.EMPTY);
        if (tXfPreInvoiceDao.selectCount(preInvoiceWrapper) > 0) {
            log.warn("结算单下还有未撤销红字信息表的预制发票，无法撤销:[{}]", settlementEntity.getSettlementNo());
            return;
        }
        // true-新  false-历史结算单
        boolean settlementSource = deductBlueInvoiceService.checkSettlementSource(settlementEntity.getId());
        log.info("红字信息表全部撤销后处理开始:[{}]-[{}]", settlementEntity.getSettlementNo(), settlementSource);

        // 作废预制发票
        preInvoiceDaoService.destroyPreInvoice(settlementEntity);
        // 修改结算单状态
        if (SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE.getCode().equals(settlementEntity.getApproveType())) {
            // 删除预制发票
            preInvoiceDaoService.deletePreInvoice(settlementEntity);

            statementService.updateSettlementStatus(settlementEntity.getId(), TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE, SettlementApproveStatusEnum.APPROVE_SUCCESS, null);
            // 重新拆票
            TransactionUtils.invokeAfterCommitIfExistOrImmediately(() -> {
                preinvoiceService.splitPreInvoiceAsync(settlementEntity.getSettlementNo(), settlementEntity.getSellerNo());
            });

            // 日志添加
            operateLogService.add(settlementEntity.getId(), OperateLogEnum.SPLIT_AGAIN_SETTLEMENT_APPLY_PASS, TXfSettlementStatusEnum.WAIT_CHECK.getDesc(),"", UserUtil.getUserId(), UserUtil.getUserName());
        } else {
            // 是否沃尔玛发起撤销
            boolean buyerApprove = true;
            if (SettlementApproveStatusEnum.APPROVING.getCode().equals(settlementEntity.getApproveStatus())) {
                buyerApprove = false;
                // 添加审核通过日志
                operateLogService.add(settlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT_APPLY_PASS, TXfSettlementStatusEnum.WAIT_CHECK.getDesc(), "", UserUtil.getUserId(), UserUtil.getUserName());
            }
            if (!settlementSource) {
                log.info("结算单撤销走旧流程:[{}]", settlementEntity.getSettlementNo());
                if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(settlementEntity.getBusinessType())) {
                    log.info("协议结算单撤销旧流程:[{}]", settlementEntity.getSettlementNo());
                    commAgreementService.destroyAgreementSettlement(settlementEntity.getId());
                } else if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(settlementEntity.getBusinessType())) {
                    log.info("索赔结算单撤销旧流程:[{}]", settlementEntity.getSettlementNo());
                    commClaimService.destroyClaimSettlement(settlementEntity.getId());
                }
                operateLogService.add(settlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), buyerApprove && StringUtils.isNotEmpty(settlementEntity.getRevertRemark()) ? String.format("（原因：%s）", settlementEntity.getRevertRemark()) : "", UserUtil.getUserId(), UserUtil.getUserName());
                return;
            }

            statementService.updateSettlementStatus(settlementEntity.getId(), TXfSettlementStatusEnum.DESTROY, SettlementApproveStatusEnum.APPROVE_SUCCESS, null);
            // 撤销结算单
            if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(settlementEntity.getBusinessType())) {
                commAgreementService.releaseAgreementBillAndBlueInvoice(settlementEntity);
            } else if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(settlementEntity.getBusinessType())) {
                commClaimService.releaseClaimBillAndBlueInvoice(settlementEntity);
            }

            // 日志添加
            operateLogService.add(settlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), buyerApprove && StringUtils.isNotEmpty(settlementEntity.getRevertRemark()) ? String.format("（原因：%s）", settlementEntity.getRevertRemark()) : "", UserUtil.getUserId(), UserUtil.getUserName());
        }
    }

    /**
     * 结算单审核前处理
     * @param settlementId 结算单id
     * @return
     */
    public R<SettlementApproveInfo> approveSettlementBefore(Long settlementId) {
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(settlementEntity, "结算单不存在");
        Asserts.isFalse(SettlementApproveStatusEnum.APPROVING.getCode().equals(settlementEntity.getApproveStatus()), "结算单非待审核状态");

        SettlementApproveInfo settlementApproveInfo = new SettlementApproveInfo();
        BeanUtil.copyProperties(settlementEntity, settlementApproveInfo);
        settlementApproveInfo.setSettlementId(settlementId);
        settlementApproveInfo.setApproveRequestTime(settlementEntity.getApproveRequestTime().getTime());
        if (SettlementApproveTypeEnum.BLUE_FLUSH.getCode().equals(settlementEntity.getApproveType())) {
            // 蓝冲 查询审核数据 一张一张审核
            Tuple2<Long, List<InvoiceAudit>> tuple = invoiceAuditService.search(null, null, settlementEntity.getSettlementNo(), AuditStatusEnum.NOT_AUDIT.getValue(), 1, 1);
            settlementApproveInfo.setInvoiceAuditList(tuple._2());
        } else {
            // 撤销、重拆
            List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceEntity.class)
                    .eq(TXfPreInvoiceEntity::getSettlementId, settlementId)
                    .ne(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode()));
            Asserts.isTrue(CollectionUtil.isEmpty(preInvoiceEntityList), "结算单无预制发票");

            List<Long> preInvoiceList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            List<TXfDeductPreInvoiceEntity> tXfDeductPreInvoiceEntities = tXfDeductPreInvoiceDao.selectList(Wrappers.lambdaQuery(TXfDeductPreInvoiceEntity.class)
                    .select(TXfDeductPreInvoiceEntity::getPreInvoiceId, TXfDeductPreInvoiceEntity::getApplyStatus)
                    .in(TXfDeductPreInvoiceEntity::getPreInvoiceId, preInvoiceList)
                    .groupBy(TXfDeductPreInvoiceEntity::getPreInvoiceId, TXfDeductPreInvoiceEntity::getApplyStatus));
            List<Integer> redNotificationStatusList = tXfDeductPreInvoiceEntities.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).distinct().collect(Collectors.toList());
            settlementApproveInfo.setRedNotificationStatusList(redNotificationStatusList);
            // 存在撤销请求已发送，但是结果一直没有返回的情况，此时中间表(t_xf_deduct_pre_invoice)处于撤销中，但是红字信息表还处于已申请
            // 所以待撤销数量使用红字信息表数据进行统计
            //settlementApproveInfo.setNeedRollbackNum(tXfDeductPreInvoiceEntities.stream().map(TXfDeductPreInvoiceEntity::getApplyStatus).filter(status -> AgreementRedNotificationStatus.APPLIED.getValue() == status || AgreementRedNotificationStatus.REVOCATION_FAILED.getValue() == status).count());
            R<Integer> needRollbackRes = preInvoiceDaoService.checkPreInvoiceAppliedRedNoCount(settlementEntity);
            Asserts.isFalse(R.OK.equals(needRollbackRes.getCode()), needRollbackRes.getMessage());
            settlementApproveInfo.setNeedRollbackNum(needRollbackRes.getResult().longValue());
        }
        return R.ok(settlementApproveInfo);
    }

    /**
     * 结算单下发票蓝冲审核
     * @param settlementEntity 结算单
     * @param request 审核结果
     */
    @Transactional(rollbackFor = Exception.class)
    public R<Boolean> blueFlushApprove(TXfSettlementEntity settlementEntity, SettlementApproveRequest request) {
        Asserts.isEmpty(request.getUuids(), "蓝冲审核信息不能为空");

        // 查询结算单下待审核的红票
        Tuple2<Long, List<InvoiceAudit>> invoiceAuditList = invoiceAuditService.search(null, null, request.getSettlementNo(), AuditStatusEnum.NOT_AUDIT.getValue(), 1, 10);
        Asserts.isTrue(invoiceAuditList._1() == 0, "结算单无待蓝冲审核的红票");

        // 所有申请蓝冲审核的红票审核结束
        boolean allAudited = request.getUuids().size() == invoiceAuditList._1().intValue();

        boolean b = invoiceAuditService.audit(request.getUuids(), Integer.valueOf(1).equals(request.getType()) ? AuditStatusEnum.AUDIT_PASS.getValue() : AuditStatusEnum.AUDIT_FAIL.getValue(), request.getRemark());
        if (b && allAudited) {
            // 恢复到原状态
            QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementEntity.getId());
            wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
            wrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.DESTROY.getCode());
            List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = tXfPreInvoiceDao.selectList(wrapper);
            TXfSettlementStatusEnum settlementStatusEnum;
            if (tXfPreInvoiceEntities.stream().allMatch(t -> TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode().equals(t.getPreInvoiceStatus()))) {
                settlementStatusEnum = TXfSettlementStatusEnum.UPLOAD_RED_INVOICE;
            } else {
                settlementStatusEnum = TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE;
            }
            SettlementApproveStatusEnum settlementApproveStatusEnum = Integer.valueOf(1).equals(request.getType()) ? SettlementApproveStatusEnum.APPROVE_SUCCESS : SettlementApproveStatusEnum.APPROVE_REJECTED;
            statementService.updateSettlementStatus(settlementEntity.getId(), settlementStatusEnum, settlementApproveStatusEnum, request.getRemark());
        }
        return R.ok(b);
    }

    /**
     * 审核通过-发起结算单撤销、重新拆票、蓝冲
     * @param tXfSettlementEntity 结算单
     */
    @Transactional(rollbackFor = Exception.class)
    public R<String> approveSuccessSettlement(TXfSettlementEntity tXfSettlementEntity) {
        log.info("结算单走审核流程:{}-{}", tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getApproveType());
        // 1.作废预制发票
        R<List<RedNotificationMain>> res = preInvoiceDaoService.destroyPreInvoice(tXfSettlementEntity);
        if (CollectionUtil.isNotEmpty(res.getResult())) {
            // 2.发起红字信息表撤销
            log.info("结算单需先撤销红字信息表:{}", tXfSettlementEntity.getSettlementNo());
            res.getResult().forEach(redNotificationMain -> commRedNotificationService.confirmDestroyRedNotification(CommonUtil.toLong(redNotificationMain.getPid()), RedNoEventTypeEnum.DESTROY_SETTLEMENT));

            String key = Constants.DESTROY_SETTLEMENT_RED_NO_PRE + tXfSettlementEntity.getId();
            String value = JSON.toJSONString(res.getResult().stream().map(RedNotificationMain::getPid).collect(Collectors.toList()));
            log.info("撤销红字放入缓存中:[{}]-[{}]", key, value);
            cacheClient.set(key, value, 60 * 60);
            return R.ok(String.valueOf(tXfSettlementEntity.getId()), "红字信息表正在撤销中");
        }

        if (SettlementApproveTypeEnum.AMOUNT_WRONG.getCode().equals(tXfSettlementEntity.getApproveType())) {
            // 添加审核通过日志
            operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT_APPLY_PASS, TXfSettlementStatusEnum.WAIT_CHECK.getDesc(), "", UserUtil.getUserId(), UserUtil.getUserName());
            // true - 新 false - 历史
            boolean settlementSource = deductBlueInvoiceService.checkSettlementSource(tXfSettlementEntity.getId());
            log.info("结算单撤销流程确认:[{}]-[{}]", tXfSettlementEntity.getSettlementNo(), settlementSource);
            if (!settlementSource) {
                if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
                    log.info("索赔结算单撤销旧流程:[{}]", tXfSettlementEntity.getSettlementNo());
                    commClaimService.destroyClaimSettlement(tXfSettlementEntity.getId());
                    operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), "", UserUtil.getUserId(), UserUtil.getUserName());
                    return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
                } else {
                    log.info("协议结算单撤销流流程:[{}]", tXfSettlementEntity.getSettlementNo());
                    commAgreementService.destroyAgreementSettlement(tXfSettlementEntity.getId());
                    operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), "", UserUtil.getUserId(), UserUtil.getUserName());
                    return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
                }
            }
            log.info("结算单审核通过直接作废:{}", tXfSettlementEntity.getSettlementNo());
            // 2.作废结算单状态
            statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.DESTROY, SettlementApproveStatusEnum.APPROVE_SUCCESS, null);

            // 3.释放业务单及蓝票
            if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
                commAgreementService.releaseAgreementBillAndBlueInvoice(tXfSettlementEntity);
            } else if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
                commClaimService.releaseClaimBillAndBlueInvoice(tXfSettlementEntity);
            }

            // 4.添加日志
            operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), "", UserUtil.getUserId(), UserUtil.getUserName());
            return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
        } else if (SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE.getCode().equals(tXfSettlementEntity.getApproveType())) {
            log.info("结算单审核通过直接重新拆票:{}", tXfSettlementEntity.getSettlementNo());
            preInvoiceDaoService.deletePreInvoice(tXfSettlementEntity);
            // 2.修改开票限额
            statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE, SettlementApproveStatusEnum.APPROVE_SUCCESS, null);

            // 3.重新拆票
            TransactionUtils.invokeAfterCommitIfExistOrImmediately(() -> {
                preinvoiceService.splitPreInvoiceAsync(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
            });
            // 日志添加
            operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.SPLIT_AGAIN_SETTLEMENT_APPLY_PASS, TXfSettlementStatusEnum.WAIT_CHECK.getDesc(),"", UserUtil.getUserId(), UserUtil.getUserName());
            return R.ok(null, "结算单重新拆票成功");
        }

        return R.fail("审核类型不支持该审核流程");
    }

    /**
     * 审核驳回-回到待开票状态
     * @param tXfSettlementEntity 结算单信息
     * @param remark 审核备注
     */
    @Transactional(rollbackFor = Exception.class)
    public R<String> approveRejectSettlement(TXfSettlementEntity tXfSettlementEntity, String remark) {
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE, SettlementApproveStatusEnum.APPROVE_REJECTED, remark);

        OperateLogEnum logEnum = SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE.getCode().equals(tXfSettlementEntity.getApproveType()) ? OperateLogEnum.SPLIT_AGAIN_SETTLEMENT_APPLY_REJECT : OperateLogEnum.DESTROY_SETTLEMENT_APPLY_REJECT;
        operateLogService.add(tXfSettlementEntity.getId(), logEnum, TXfSettlementStatusEnum.WAIT_CHECK.getDesc(), StringUtils.isEmpty(remark) ? "" : String.format("（原因：%s）", remark), UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "驳回成功");
    }

    /**
     * 结算单撤销轮训结果
     * @param settlementId 结算单ID
     */
    public R<List<RedNotificationRollbackFailResult>> pollingDestroy(Long settlementId) {
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(settlementEntity, "结算单不存在");

        if (TXfSettlementStatusEnum.DESTROY.getCode().equals(settlementEntity.getSettlementStatus())) {
            return R.ok(null, "结算单已撤销");
        }

        String key = Constants.DESTROY_SETTLEMENT_RED_NO_PRE + settlementId;
        String value = cacheClient.get(key);
        log.info("撤销红字取出缓存中:[{}]-[{}]", settlementId, value);
        if (Objects.isNull(value)) {
            return R.ok(null, "结算单已撤销");
        }

        List<String> pidList = JSON.parseArray(value, String.class);
        // 查询预制发票
        List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectBatchIds(pidList.stream().map(CommonUtil::toLong).collect(Collectors.toList()));
        if (preInvoiceEntityList.stream().allMatch(entity -> StringUtils.isEmpty(entity.getRedNotificationNo()))) {
            return R.ok(null, "红字信息表已撤销完成");
        }

        R<List<RedNotificationRollbackFailResult>> res = redNotificationMainService.getRollbackResult(pidList);
        log.info("pollingDestroy:[{}]", JSON.toJSONString(res));
        if (R.OK.equals(res.getCode())) {
            log.info("撤销红字清除缓存:{}", settlementId);
            cacheClient.clean(key);
        }
        return res;
    }

    /**
     * 结算单撤销流程-V2
     * @param request 结算单撤销请求信息
     */
    @Transactional(rollbackFor = Exception.class)
    public R<String> destroySettlementV2(SettlementCancelRequest request) {
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
        // true - 新 false - 历史
        boolean settlementSource = deductBlueInvoiceService.checkSettlementSource(settlementId);
        log.info("结算单撤销流程确认:[{}]-[{}]", settlementNo, settlementSource);
        // 3.待开票状态 需要填写撤销原因
        if (SettlementApproveTypeEnum.DEFAULT.getCode().equals(type)) {
            // 沃尔玛侧发起撤销
            if (!settlementSource && TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
                log.info("沃尔玛侧索赔结算单撤销旧流程:[{}]", settlementNo);
                commClaimService.destroyClaimSettlement(settlementId);
                operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
                return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
            }
            R<List<RedNotificationMain>> res = preInvoiceDaoService.destroyPreInvoice(tXfSettlementEntity);
            if (CollectionUtil.isNotEmpty(res.getResult())) {
                TXfSettlementEntity settlementEntityU = new TXfSettlementEntity();
                settlementEntityU.setId(settlementId);
                settlementEntityU.setUpdateTime(new Date());
                settlementEntityU.setUpdateUser(UserUtil.getUserId());
                settlementEntityU.setApproveStatus(SettlementApproveStatusEnum.DEFAULT.getCode());
                settlementEntityU.setApproveType(SettlementApproveTypeEnum.AMOUNT_WRONG.getCode());
                settlementEntityU.setRevertRemark(applyReason);
                tXfSettlementDao.updateById(settlementEntityU);
                // 4.发起红字信息表撤销
                log.info("结算单需先撤销红字信息表:{}", settlementNo);
                res.getResult().forEach(redNotificationMain -> commRedNotificationService.confirmDestroyRedNotification(CommonUtil.toLong(redNotificationMain.getPid()), RedNoEventTypeEnum.DESTROY_SETTLEMENT));

                String key = Constants.DESTROY_SETTLEMENT_RED_NO_PRE + settlementId;
                String value = JSON.toJSONString(res.getResult().stream().map(RedNotificationMain::getPid).collect(Collectors.toList()));
                log.info("撤销红字放入缓存中:[{}]-[{}]", key, value);
                cacheClient.set(key, value, 60 * 60);
                return R.ok(String.valueOf(settlementId), "红字信息表正在撤销中");
            }
        } else if (TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode().equals(tXfSettlementEntity.getSettlementStatus())) {
            Asserts.isTrue(org.apache.commons.lang.StringUtils.isBlank(applyReason), "必须填写撤销原因");
            Asserts.isTrue(applyReason.length() > 200, "撤销原因不应超过200字符");

            // 校验红字信息表申请中或撤销中
            R<Boolean> checkRes = preInvoiceDaoService.checkPreInvoice(tXfSettlementEntity);
            Asserts.isTrue(R.FAIL.equals(checkRes.getCode()), checkRes.getMessage());

            if (Objects.nonNull(checkRes.getResult()) && checkRes.getResult()) {
                // 进入待审核状态
                log.info("结算单进入审核状态:{}", settlementNo);
                SettlementApproveTypeEnum approveTypeEnum = SettlementApproveTypeEnum.fromCode(type);
                statementService.updateSettlementStatus(settlementId, TXfSettlementStatusEnum.WAIT_CHECK, applyReason, approveTypeEnum, SettlementApproveStatusEnum.APPROVING, null);

                // 添加日志
                OperateLogEnum logEnum = SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE == approveTypeEnum ? OperateLogEnum.SPLIT_AGAIN_SETTLEMENT_APPLY : OperateLogEnum.DESTROY_SETTLEMENT_APPLY;
                operateLogService.add(tXfSettlementEntity.getId(), logEnum, TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
                return R.ok(null, "撤销申请已发出，请等待沃尔玛审核后再操作");
            }

            if (!settlementSource && TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())
                    && !SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE.getCode().equals(type)) {
                log.info("供应商侧索赔结算单撤销旧流程:[{}]", settlementNo);
                commClaimService.destroyClaimSettlement(settlementId);
                operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
                return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
            }

            preInvoiceDaoService.destroyPreInvoice(tXfSettlementEntity);
            if (SettlementApproveTypeEnum.INVOICE_LIMIT_AMOUNT_UPDATE.getCode().equals(type)) {
                log.info("结算单直接重新拆票:{}", settlementNo);
                // 修改限额，重新拆票
                preInvoiceDaoService.deletePreInvoice(tXfSettlementEntity);

                // 更新预制发票状态
                statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE, applyReason, SettlementApproveTypeEnum.fromCode(type), null, null);

                // 重新拆票
                TransactionUtils.invokeAfterCommitIfExistOrImmediately(() -> {
                    preinvoiceService.splitPreInvoiceAsync(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getSellerNo());
                });
                // 日志添加
                operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.SPLIT_AGAIN_SETTLEMENT, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
                return R.ok(null, "结算单重新拆票成功");
            }
        }
        log.info("结算单直接作废:{}", settlementNo);
        if (!settlementSource && TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
            log.info("协议结算单撤销旧流程:[{}]", settlementNo);
            commAgreementService.destroyAgreementSettlement(settlementId);
            operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
            return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
        }
        // 4.作废结算单状态
        statementService.updateSettlementStatus(tXfSettlementEntity.getId(), TXfSettlementStatusEnum.DESTROY, applyReason, SettlementApproveTypeEnum.fromCode(type), null, null);

        // 5.释放业务单及蓝票
        if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
            commAgreementService.releaseAgreementBillAndBlueInvoice(tXfSettlementEntity);
        } else if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(tXfSettlementEntity.getBusinessType())) {
            commClaimService.releaseClaimBillAndBlueInvoice(tXfSettlementEntity);
        }
        // 6.添加日志
        operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.DESTROY_SETTLEMENT, TXfSettlementStatusEnum.DESTROY.getDesc(), StringUtils.isEmpty(applyReason) ? "" : String.format("（原因：%s）", applyReason), UserUtil.getUserId(), UserUtil.getUserName());
        return R.ok(null, "结算单撤销成功，对应业务单已恢复至待匹配状态");
    }
}
