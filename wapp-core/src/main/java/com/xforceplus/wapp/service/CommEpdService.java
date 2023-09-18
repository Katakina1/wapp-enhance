package com.xforceplus.wapp.service;

import static com.xforceplus.wapp.common.utils.CommonUtil.assertFalse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.enums.*;
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
import com.xforceplus.wapp.repository.dao.TDxRecordInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;

import lombok.extern.slf4j.Slf4j;
/**
 * epd 通用逻辑操作
 *
 * @author Xforce
 */
@Service
@Slf4j
public class CommEpdService {
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
    private BlueInvoiceRelationService blueInvoiceRelationService;
    @Autowired
    private PreinvoiceService preinvoiceService;
    @Autowired
    private BillSettlementService billSettlementService;
    @Autowired
    private CommonMessageService commonMessageService;
    
    private final List<Integer> canDestroyStatus;

    public CommEpdService() {
        canDestroyStatus = Arrays.asList(
                TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()
                , TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode()
                , TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode()
                , TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode()
                ,TXfSettlementStatusEnum.WAIT_CONFIRM.getCode()
        );
    }

    /**
     * 作废EPD单 作废结算单 蓝票释放额度 如果有预制发票 作废预制发票
     * EPD单还可以再次使用
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void destroyEpdSettlement(Long settlementId) {
        if (settlementId == null) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //2022-05-05 magaofeng ADD
        if (Objects.equals(tXfSettlementEntity.getSettlementStatus(),TXfSettlementStatusEnum.WAIT_CHECK.getCode())){
            throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经待审核，请等待沃尔玛审核后再操作");
        }

        if (Objects.equals(tXfSettlementEntity.getSettlementStatus(),TXfSettlementStatusEnum.DESTROY.getCode())){
            throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经作废不能再次被作废");
        }

        if (!canDestroyStatus.contains(tXfSettlementEntity.getSettlementStatus())) {
            throw new EnhanceRuntimeException("结算单已上传红票不能操作");
        }

        //EPD单
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        List<TXfPreInvoiceEntity> pPreInvoiceList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);
        log.info("destroyEpdSettlement SETTLEMENT_NO:{}, pPreInvoiceList:{}", tXfSettlementEntity.getSettlementNo() ,JsonUtil.toJsonStr(pPreInvoiceList));
        //2022-05-05 magaofeng ADD 如预制发票待审核，禁止发起操作
        Optional.ofNullable(pPreInvoiceList).ifPresent(x -> x.forEach(tXfPreInvoiceEntity -> {
        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode())) {
        		throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]已经待审核,请等待沃尔玛审核后再操作");
        	}
//        	if(Objects.equals(tXfPreInvoiceEntity.getPreInvoiceStatus(), TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode())) {
//        		throw new EnhanceRuntimeException("结算单["+tXfSettlementEntity.getSettlementNo()+"]正在申请红字信息中,请稍后再操作");
//        	}
        	
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
        
        //修改作废状态====
        //作废结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.DESTROY.getCode());
        updateTXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改EPD单状态
        billDeductList.forEach(billDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(billDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            updateTXfSettlementEntity.setUpdateTime(new Date());
            updateTXfBillDeductEntity.setRefSettlementNo("");
            updateTXfBillDeductEntity.setMakeInvoiceStatus(DeductBillMakeInvoiceStatusEnum.NONE_MAKE_INVOICE.code());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //作废预制发票
        Optional.ofNullable(pPreInvoiceList).ifPresent(x -> x.forEach(tXfPreInvoiceEntity -> {

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
//            if (StringUtils.isNotBlank(tXfPreInvoiceEntity.getRedNotificationNo())) {
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
        
        //2022-08-08新增，修改业务单和结算单的关系
        List<TXfBillSettlementEntity> list = billSettlementService.queryBySettlementNo(tXfSettlementEntity.getSettlementNo(), TXfDeductionBusinessTypeEnum.EPD_BILL.getValue());
        log.info("epd billSettlementService.cancelBillSettlementStatus:{}, list:{}", list);
        if(list != null && list.size() > 0) {
			list.forEach(item -> {
				billSettlementService.cancelBillSettlementStatus(item.getBusinessNo(), item.getSettlementNo(), item.getBusinessType());
			});
        }
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
        if (CollectionUtils.isEmpty(preInvoiceItemList)) {
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


}
