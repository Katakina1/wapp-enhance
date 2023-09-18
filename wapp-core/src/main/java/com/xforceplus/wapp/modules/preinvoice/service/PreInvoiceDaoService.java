package com.xforceplus.wapp.modules.preinvoice.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.ApproveStatus;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.modules.blue.service.BlueInvoiceRelationService;
import com.xforceplus.wapp.modules.deduct.service.DeductBlueInvoiceService;
import com.xforceplus.wapp.modules.deduct.dto.SettlementCancelRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.ApplyOperationRequest;
import com.xforceplus.wapp.modules.preinvoice.dto.PreInvoiceItem;
import com.xforceplus.wapp.modules.preinvoice.dto.SplitAgainRequest;
import com.xforceplus.wapp.modules.preinvoice.mapstruct.PreInvoiceMapper;
import com.xforceplus.wapp.modules.rednotification.mapstruct.RedNotificationMainMapper;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationMain;
import com.xforceplus.wapp.modules.rednotification.model.Response;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationOuterService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.service.CommAgreementService;
import com.xforceplus.wapp.service.CommEpdService;
import com.xforceplus.wapp.service.CommSettlementService;
import com.xforceplus.wapp.service.CommonMessageService;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class PreInvoiceDaoService extends ServiceImpl<TXfPreInvoiceDao, TXfPreInvoiceEntity> {
    @Autowired
    CommAgreementService commAgreementService;
    @Autowired
    CommEpdService commEpdService;
    @Autowired
    CommSettlementService commSettlementService;
    @Autowired
    RedNotificationOuterService redNotificationOuterService;
    @Autowired
    PreInvoiceItemDaoService preInvoiceItemDaoService;
    @Autowired
    PreInvoiceMapper  preInvoiceMapper;
    @Autowired
    TXfSettlementDao tXfSettlementDao;
    @Autowired
    private BlueInvoiceRelationService blueInvoiceRelationService;
    @Autowired
    private RedNotificationMainMapper redNotificationMainMapper;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private DeductBlueInvoiceService deductBlueInvoiceService;

    public R<List<PreInvoiceItem>> applyOperation(ApplyOperationRequest request) {
        int applyOperationType = request.getApplyOperationType();

        // 操作类型 1 修改税编 2 修改限额 3不做任何修改 4 修改商品明细
        switch (applyOperationType){
            case 1:
                //获取重新作废明细重新拆票，重新申请红字信息
                return retryApplyRednotification(request);
            case 2:
            case 3:
                //限额 和 不做任何修改
                try {
                    commSettlementService.againSplitSettlementPreInvoice(Long.parseLong(request.getSettlementId()));
                } catch (EnhanceRuntimeException e) {
                    log.error("重新拆票错误,失败原因:{}",e.getMessage());
                    return R.fail(e.getMessage());
                }
                return R.ok(null,"重新拆票成功");
            case 4:
                //撤销结算单
                return rollBackSettlement(request);
            default:
                return R.fail("操作类型不正确, 1 修改税编 2 修改限额 3不做任何修改 4 修改商品明细");
        }
    }

    /**
     * 判断是否有已存在的红票
     * @param request
     * @return
     */
//    public Response<ExistRedInvoiceResult> existRedInvoice(ApplyOperationRequest request){
//        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(request.getSettlementId());
//        if (tXfSettlementEntity == null) {
//            Response.failed("结算单不存在");
//        }
//
//        ExistRedInvoiceResult existRedInvoiceResult = new ExistRedInvoiceResult();
//        existRedInvoiceResult.setExistRedInvoice(false);
//        String message = "点击确定，本结算单将被撤销，已勾选协议单，已关联蓝票及明细会被释放。你可以重新勾选协议单，再次匹配需要的发票及明细，请确认是否处理？";
//
//        if (tXfSettlementEntity.getSettlementStatus()== TXfSettlementStatusEnum.UPLOAD_HALF_RED_INVOICE.getValue()
//          || tXfSettlementEntity.getSettlementStatus()== TXfSettlementStatusEnum.UPLOAD_RED_INVOICE.getValue()
//        ){
//            existRedInvoiceResult.setExistRedInvoice(true);
//            message ="本结算单已经上传了红字发票，请全部删除";
//        }
//
//        return Response.ok(message ,existRedInvoiceResult);
//
//    }



    // 结算单类型:1索赔单,2:协议单；3:EPD单
    private R<List<PreInvoiceItem>> rollBackSettlement(ApplyOperationRequest request) {
        try {
            switch (request.getSettlementType()) {
                case 2:
                    Long settlementId = CommonUtil.toLong(request.getSettlementId());
                    if (deductBlueInvoiceService.checkSettlementSource(settlementId)) {
                        // 修改结算单为待确认状态后再走新的撤销流程，结算单就不会走审核流程了，而是直接撤销掉
                        SettlementCancelRequest settlementCancelRequest = new SettlementCancelRequest();
                        settlementCancelRequest.setSettlementId(settlementId);
                        settlementCancelRequest.setRevertRemark(null);
                        settlementCancelRequest.setType(0);
                        commSettlementService.destroySettlementV2(settlementCancelRequest);
                    } else {
                        commAgreementService.destroyAgreementSettlement(settlementId);
                    }
                    break;
                case 3:
                    commEpdService.destroyEpdSettlement(Long.parseLong(request.getSettlementId()));
                    break;
                default:
            }
        }catch (Exception e){
            log.error("释放结算单异常",e);
            return R.fail(e.getMessage());
        }

        return  R.ok(Lists.newArrayList());
    }

    private R<List<PreInvoiceItem>> retryApplyRednotification(ApplyOperationRequest request) {
        // 操作类型 1 修改税编 2 修改限额 3 修改商品明细
        //作废的预制发票
        LambdaQueryWrapper<TXfPreInvoiceEntity> tXfPreInvoiceEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tXfPreInvoiceEntityLambdaQueryWrapper
                .eq(TXfPreInvoiceEntity::getSettlementId,request.getSettlementId())
                .in(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.DESTROY.getCode(), TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        List<TXfPreInvoiceEntity> destroyXfPreInvoiceList = getBaseMapper().selectList(tXfPreInvoiceEntityLambdaQueryWrapper);
        List<Long> destroyPreInvoiceId = destroyXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        log.info("获取作废预制发票数量:{},结算单号:{}", destroyPreInvoiceId.size(),request.getSettlementNo());

        //2022-05-05 magaofeng ADD 如预制发票待审核，禁止发起操作
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, request.getSettlementNo());
        List<TXfPreInvoiceEntity> pPreInvoiceList = getBaseMapper().selectList(preInvoiceEntityWrapper);
        log.info("againSplitSettlementPreInvoice SETTLEMENT_NO:{}, pPreInvoiceList:{}", request.getSettlementNo(), JsonUtil.toJsonStr(pPreInvoiceList));
        Optional.ofNullable(pPreInvoiceList).ifPresent(x -> x.forEach(tXfPreInvoiceEntity -> {
        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())) {
        		throw new EnhanceRuntimeException("结算单["+request.getSettlementNo()+"]已经待审核,请等待沃尔玛审核后再操作");
        	}
        }));
        //是否正在申请红字
        //需要判断结算单是否在沃尔玛有待申请状态(如果没有待申请状态的红字信息说明税件神申请败了，这个时候可以重新申请预制发票的红字信息)
        //是否有申请中的红字信息 或者 是否有审核通过
        if(pPreInvoiceList != null && pPreInvoiceList.size() > 0) {
        	// 判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
			pPreInvoiceList.removeIf(item -> {
				return blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode());
			});
        }
        if(pPreInvoiceList != null && pPreInvoiceList.size() > 0) {//判断红字信息表状态
        	List<Long> applyingRedPreInvoiceIdList = pPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
        	log.info("againSplitSettlementPreInvoice SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}", request.getSettlementNo() ,JsonUtil.toJsonStr(applyingRedPreInvoiceIdList));
        	List<TXfRedNotificationEntity> redNotificationlist = redNotificationOuterService.queryRedNotiByPreInvoiceId(applyingRedPreInvoiceIdList);
        	Optional.ofNullable(redNotificationlist).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
        		log.info("againSplitSettlementPreInvoice SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", request.getSettlementNo(), JsonUtil.toJsonStr(txfRedNotificationEntity));
				if ((Objects.equals(txfRedNotificationEntity.getApplyingStatus(),RedNoApplyingStatus.APPLIED.getValue()) && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())))
						|| Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLYING.getValue())) {
					throw new EnhanceRuntimeException("结算单[" + request.getSettlementNo() + "]有正在申请或者已申请红字信息的预制发票");
				}
        		 //如果申请状态是 WAIT_TO_APPROVE(4,"撤销待审核"); 并且不等于ALREADY_ROLL_BACK(4,"已撤销")
				if (Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue()) && !(Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue()))) {
					throw new EnhanceRuntimeException("结算单[" + request.getSettlementNo() + "]红字信息表撤销中,请稍后再操作");
				}
        	}));
        }
        //2022-05-05 end
        //获取正常发票
        LambdaQueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<Integer> invoiceStatusList = Stream.of(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode(),
                        TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode(),
                        TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode(),
                        TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode(),
                        TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode())
                .collect(Collectors.toList());
        preInvoiceEntityLambdaQueryWrapper
                .eq(TXfPreInvoiceEntity::getSettlementId,request.getSettlementId())
                .in(TXfPreInvoiceEntity::getPreInvoiceStatus, invoiceStatusList);
        List<TXfPreInvoiceEntity> tXfPreInvoiceList = getBaseMapper().selectList(preInvoiceEntityLambdaQueryWrapper);
        List<String> preInvoiceId = tXfPreInvoiceList.stream().map(TXfPreInvoiceEntity::getId).map(preId->String.valueOf(preId)).collect(Collectors.toList());
        log.info("获取正常预制发票数量:{},结算单号:{}", preInvoiceId.size(),request.getSettlementNo());
        if(!CollectionUtils.isEmpty(preInvoiceId)) {
            //获取待审核的
            Tuple2<Boolean, List<Long>> listTuple2 = redNotificationOuterService.getWaitApplyPreIds(preInvoiceId);
            if (!listTuple2._1) {
                return R.fail("结算单已申请红字信息,请撤销后操作");
            }
            List<Long> waitApplyPreIds =listTuple2._2;
            log.info("获取待申请的预制发票数量:{},结算单号:{}", waitApplyPreIds.size(),request.getSettlementNo());

            //删除待审核红字信息
            if (!CollectionUtils.isEmpty(waitApplyPreIds)){
                redNotificationOuterService.deleteRednotification(waitApplyPreIds,null);
            }
            // 获取明细重新拆票
            destroyPreInvoiceId.addAll(waitApplyPreIds);
        }

        if (CollectionUtils.isEmpty(destroyPreInvoiceId)){
            return R.fail("未查询到待重新拆票的明细");
        }

        LambdaQueryWrapper<TXfPreInvoiceItemEntity> itemEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        itemEntityLambdaQueryWrapper.in(TXfPreInvoiceItemEntity::getPreInvoiceId,destroyPreInvoiceId);
        List<TXfPreInvoiceItemEntity> invoiceItemEntities = preInvoiceItemDaoService.getBaseMapper().selectList(itemEntityLambdaQueryWrapper);

        List<PreInvoiceItem> preInvoiceItems = preInvoiceMapper.entityToPreInvoiceItemDtoList(invoiceItemEntities);
        return R.ok(preInvoiceItems);
    }


    public Response<String> splitAgain(SplitAgainRequest request) {
        //如果不做任何修改
        if (request.getApplyOperationType() != null && request.getApplyOperationType() == 1){
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = preInvoiceMapper.itemToPreInvoiceEntityList(request.getDetails());
            try {
                switch (request.getSettlementType()) {
                    case 2:
                        commAgreementService.againSplitPreInvoice(request.getSettlementId(),tXfPreInvoiceItemEntities);
                        break;
                    case 3:
                        commEpdService.againSplitPreInvoice(request.getSettlementId(),tXfPreInvoiceItemEntities);
                        break;
                }
            }catch (Exception e){
                log.error("重新拆票异常",e);
                return Response.failed(e.getMessage());
            }
        }else {



        }



        return Response.ok("重新拆票成功");
    }

    /**
     * 删除预制发票
     * @param settlementEntity 结算单信息
     */
    public void deletePreInvoice(TXfSettlementEntity settlementEntity) {
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementEntity.getId());
        preInvoiceEntityWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        List<TXfPreInvoiceEntity> preInvoiceEntityList = this.list(preInvoiceEntityWrapper);

        // 1.预制发票状态判断
        if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
            // 1.1.判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
            preInvoiceEntityList.removeIf(item -> StringUtils.isNotBlank(item.getInvoiceNo()) && StringUtils.isNotBlank(item.getInvoiceCode())
                    && blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode()));
        }

        // 删除
        if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
            List<Long> preInvoiceIdList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            deletePreInvoice(preInvoiceIdList, UserUtil.getUserId());
            commonMessageService.sendPreInvoiceDeleteMessage(preInvoiceIdList);
        }
    }

    /**
     * 删除预制发票（页面不可见）
     * @param preInvoiceIdList 预制发票id集合
     * @param userId 用户id
     */
    public void deletePreInvoice(List<Long> preInvoiceIdList, Long userId) {
        updatePreInvoice(preInvoiceIdList, userId, TXfPreInvoiceStatusEnum.FINISH_SPLIT);
    }

    /**
     * 作废预制发票（页面可见）
     * @param preInvoiceIdList 预制发票id集合
     * @param userId 用户id
     */
    public void destroyPreInvoice(List<Long> preInvoiceIdList, Long userId) {
        updatePreInvoice(preInvoiceIdList, userId, TXfPreInvoiceStatusEnum.DESTROY);
    }

    private void updatePreInvoice(List<Long> preInvoiceIdList, Long userId, TXfPreInvoiceStatusEnum statusEnum) {
        if (CollectionUtils.isEmpty(preInvoiceIdList)) {
            return;
        }

        TXfPreInvoiceEntity preInvoiceEntityU = new TXfPreInvoiceEntity();
        preInvoiceEntityU.setPreInvoiceStatus(statusEnum.getCode());
        preInvoiceEntityU.setUpdateUserId(userId);
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceWrapper = new QueryWrapper<>();
        preInvoiceWrapper.in(TXfPreInvoiceEntity.ID, preInvoiceIdList);
        update(preInvoiceEntityU, preInvoiceWrapper);
    }

    public R<List<RedNotificationMain>> destroyPreInvoice(TXfSettlementEntity settlementEntity) {
        String settlementNo = settlementEntity.getSettlementNo();

        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementEntity.getId());
        preInvoiceEntityWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode());
        preInvoiceEntityWrapper.ne(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, TXfPreInvoiceStatusEnum.DESTROY.getCode());
        List<TXfPreInvoiceEntity> preInvoiceEntityList = list(preInvoiceEntityWrapper);
        log.info("destroyPreInvoice settlementNo:[{}],preInvoiceList:{}", settlementEntity.getSettlementNo(), JSON.toJSONString(preInvoiceEntityList));

        List<TXfRedNotificationEntity> needCancelRedNotificationEntityList = Lists.newArrayList();
        // 1.预制发票状态判断
        if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
            // 1.1.判断预制发票中回填的红票是否被蓝冲，已经被蓝冲的预制发票不在后续处理
            // todo 这里循环查询可以优化成分批查询
            preInvoiceEntityList.removeIf(item -> StringUtils.isNotBlank(item.getInvoiceNo()) && StringUtils.isNotBlank(item.getInvoiceCode())
                    && blueInvoiceRelationService.existsByRedInvoice(item.getInvoiceNo(), item.getInvoiceCode()));
        }
        if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
            // 1.2.预制发票待审核 禁止发起操作
            Asserts.isTrue(preInvoiceEntityList.stream().anyMatch(preInvoice -> Objects.equals(preInvoice.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())), "存在红字信息表撤销中，请勿重复操作");

            // 1.3.判断红字信息表状态 红字信息表申请中或撤销申请中不能操作
            List<Long> preInvoiceIdList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            log.info("destroyPreInvoice SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}", settlementNo, JsonUtil.toJsonStr(preInvoiceIdList));
            List<TXfRedNotificationEntity> redNotificationList = redNotificationOuterService.queryRedNotiByPreInvoiceId(preInvoiceIdList);
            Optional.ofNullable(redNotificationList).ifPresent(x -> x.forEach(txfRedNotificationEntity -> {
                log.info("destroyPreInvoice SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", settlementNo, JsonUtil.toJsonStr(txfRedNotificationEntity));
                Asserts.isTrue(Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.APPLYING.getValue()), "存在红字信息表申请中，申请成功后才可操作");

                if (Objects.equals(RedNoApplyingStatus.APPLIED.getValue(), txfRedNotificationEntity.getApplyingStatus())
                        && !Objects.equals(ApproveStatus.ALREADY_ROLL_BACK.getValue(), txfRedNotificationEntity.getApproveStatus())) {
                    // 已申请
                    needCancelRedNotificationEntityList.add(txfRedNotificationEntity);
                } else if (Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
                        && Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.APPROVE_PASS.getValue())) {
                    // 调用撤销接口失败(终端不在线...)
                    needCancelRedNotificationEntityList.add(txfRedNotificationEntity);
                } else if (Objects.equals(txfRedNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
                        && !Objects.equals(txfRedNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())) {
                    // 撤销中
                    throw new EnhanceRuntimeException(String.format("结算单[%s]红字信息表撤销中,请稍后再操作", settlementNo));
                }
            }));
            if (!CollectionUtils.isEmpty(needCancelRedNotificationEntityList)) {
                // 1.4.需撤销红字信息表
                log.info("destroyPreInvoice needDestroyRedNotification:[{}]", needCancelRedNotificationEntityList.stream().map(TXfRedNotificationEntity::getRedNotificationNo).collect(Collectors.joining(",")));
                List<RedNotificationMain> redNotificationMains = needCancelRedNotificationEntityList.stream()
                        .map(entity -> redNotificationMainMapper.entityToMainInfo(entity)).collect(Collectors.toList());
                return R.ok(redNotificationMains, "已申请红字信息表的预制发票需要先撤销");
            }

            // 2.先删除没开具的红字信息表
            redNotificationOuterService.deleteRednotification(preInvoiceIdList, "撤销结算单");
            // 3.删除结算单之前已作废的预制发票（没有红字信息、作废）避免申请逻辑状态判断问题
            destroyPreInvoice(preInvoiceIdList, UserUtil.getUserId());
            //TODO  4.发送作废删除事件  删除事件消费端代码已注释
            commonMessageService.sendPreInvoiceDiscardMessage(preInvoiceIdList);
        }
        return R.ok(null, "删除预制发票完成");
    }

    /**
     * 结算单下预制发票状态校验（申请中或撤销中）
     * @param settlementEntity 结算单
     */
    public R<Boolean> checkPreInvoice(TXfSettlementEntity settlementEntity) {
        R<Integer> checkRes = checkPreInvoiceAppliedRedNoCount(settlementEntity);
        if (R.OK.equals(checkRes.getCode())) {
            return R.ok(checkRes.getResult() > 0);
        }
        return R.fail(checkRes.getMessage());
    }

    public R<Integer> getAppliedRedNotificationPreInvoiceCount(Long settlementId) {
        TXfSettlementEntity settlementEntity = tXfSettlementDao.selectById(settlementId);
        Asserts.isNull(settlementEntity, "结算单不存在");

        return checkPreInvoiceAppliedRedNoCount(settlementEntity);
    }

    /**
     * 判断结算单下已申请红字的预制发票数量(不能有申请中或撤销中的红字)
     * @param settlementEntity 结算单信息
     */
    public R<Integer> checkPreInvoiceAppliedRedNoCount(TXfSettlementEntity settlementEntity) {
        String settlementNo = settlementEntity.getSettlementNo();
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, settlementEntity.getId());
        preInvoiceEntityWrapper.notIn(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Lists.newArrayList(TXfPreInvoiceStatusEnum.FINISH_SPLIT.getCode(), TXfPreInvoiceStatusEnum.DESTROY.getCode()));
        List<TXfPreInvoiceEntity> preInvoiceEntityList = list(preInvoiceEntityWrapper);
        log.info("checkPreInvoice settlementNo:[{}],preInvoiceList:{}", settlementEntity.getSettlementNo(), JSON.toJSONString(preInvoiceEntityList));

        if (preInvoiceEntityList != null && preInvoiceEntityList.size() > 0) {
            // 预制发票待审核 禁止发起操作
            if (preInvoiceEntityList.stream().anyMatch(preInvoice -> Objects.equals(preInvoice.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode()))) {
                return R.fail("红字信息表撤销中，无法操作");
            }

            // 判断红字信息表状态 红字信息表申请中或撤销申请中不能操作
            List<Long> preInvoiceIdList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            log.info("checkPreInvoice SETTLEMENT_NO:{}, applyingRedPreInvoiceIdList:{}", settlementNo, JsonUtil.toJsonStr(preInvoiceIdList));
            List<TXfRedNotificationEntity> redNotificationList = redNotificationOuterService.queryRedNotiByPreInvoiceId(preInvoiceIdList);
            if (CollectionUtil.isNotEmpty(redNotificationList)) {
                int appliedNum = 0;
                for (TXfRedNotificationEntity redNotificationEntity : redNotificationList) {
                    log.info("checkPreInvoice SETTLEMENT_NO:{}, txfRedNotificationEntity:{}", settlementNo, JsonUtil.toJsonStr(redNotificationEntity));
                    if (RedNoApplyingStatus.APPLYING.getValue().equals(redNotificationEntity.getApplyingStatus())) {
                        return R.fail("红字信息表申请中，无法操作");
                    }

                    if (Objects.equals(RedNoApplyingStatus.APPLIED.getValue(), redNotificationEntity.getApplyingStatus())
                            && !Objects.equals(ApproveStatus.ALREADY_ROLL_BACK.getValue(), redNotificationEntity.getApproveStatus())) {
                        // 已申请
                        appliedNum++;
                    } else if (Objects.equals(redNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
                            && Objects.equals(redNotificationEntity.getApproveStatus(), ApproveStatus.APPROVE_PASS.getValue())) {
                        // 调用撤销接口失败(终端不在线...)
                        appliedNum++;
                    } else if (Objects.equals(redNotificationEntity.getApplyingStatus(), RedNoApplyingStatus.WAIT_TO_APPROVE.getValue())
                            && !Objects.equals(redNotificationEntity.getApproveStatus(), ApproveStatus.ALREADY_ROLL_BACK.getValue())) {
                        // 撤销中
                        return R.fail("红字信息表撤销中，无法操作");
                    }
                }
                return R.ok(appliedNum);
            }
        }
        return R.ok(0);
    }
}

