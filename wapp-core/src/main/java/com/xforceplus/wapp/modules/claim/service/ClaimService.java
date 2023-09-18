package com.xforceplus.wapp.modules.claim.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.claim.mapstruct.DeductMapper;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.TDxQuestionPaperEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.service.CommClaimService;
import com.xforceplus.wapp.service.CommRedNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 索赔的结算单相关逻辑操作
 */
@Service
public class ClaimService extends ServiceImpl<TXfBillDeductDao, TXfBillDeductEntity> {

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfBillDeductDao tXfBillDeductDao;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    private TXfSettlementDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    private TXfBillDeductItemRefDao tXfBillDeductItemRefDao;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private CommClaimService commClaimService;
    @Autowired
    private TDxQuestionPaperDao tDxQuestionPaperDao;
    @Autowired
    private DeductMapper deductMapper;
    @Autowired
    private OperateLogService operateLogService;

    /**
     * 申请索赔单不定案
     *
     * @param settlementId     结算单id
     * @param billDeductIdList 索赔单id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyClaimVerdict(Long settlementId, List<Long> billDeductIdList) {
        if (settlementId == null || CollectionUtils.isEmpty(billDeductIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        doApplyVerdict(tXfSettlementEntity, billDeductIdList);
    }

    private void doApplyVerdict(TXfSettlementEntity tXfSettlementEntity, List<Long> billDeductIdList) {
        if (!Objects.equals(tXfSettlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode())) {
            throw new EnhanceRuntimeException("只能待开票状态才能申请不定案");
        }
        //索赔单
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectBatchIds(billDeductIdList);
        billDeductList.forEach(x -> {
            final boolean bool = Objects.equals(x.getStatus(), TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
            if (bool){
                throw new EnhanceRuntimeException("索赔单:["+x.getBusinessNo()+"]已经是提交不定案待审核，不需要重新提交");
            }
        });

        //预制发票
        QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
        wrapper.eq(TXfPreInvoiceEntity.PRE_INVOICE_STATUS,TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode());
        Integer preInvoiceCount = tXfPreInvoiceDao.selectCount(wrapper);
        if(preInvoiceCount > 0){
            throw new EnhanceRuntimeException("结算单存在上传红票，不能提交");
        }
        //修改预制发票状态
//        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
//            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
//            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
//            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.WAIT_CHECK.getCode());
//            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
//        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CHECK.getCode());
        updateTXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改索赔单状态
        billDeductList.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
            updateTXfBillDeductEntity.setUpdateTime(new Date());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });
        // 需要将数据放入到问题列表清单(关联一期)
        saveQuestionPaper(tXfSettlementEntity, billDeductList);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(tXfSettlementEntity.getId());
        operateLogService.add(settlement.getId(), OperateLogEnum.APPLY_VERDICT,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                UserUtil.getUserId(),UserUtil.getUserName());
    }

    /**
     * 驳回申请索赔单不定案
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void rejectClaimVerdict(Long settlementId) {
        //结算单
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.selectById(settlementId);
        if (tXfSettlementEntity == null) {
            throw new EnhanceRuntimeException("结算单不存在");
        }
        //索赔单 查询待审核状态
        QueryWrapper<TXfBillDeductEntity> billDeductEntityWrapper = new QueryWrapper<>();
        billDeductEntityWrapper.eq(TXfBillDeductEntity.REF_SETTLEMENT_NO, tXfSettlementEntity.getSettlementNo());
        billDeductEntityWrapper.eq(TXfBillDeductEntity.STATUS, TXfDeductStatusEnum.CLAIM_WAIT_CHECK.getCode());
        List<TXfBillDeductEntity> billDeductList = tXfBillDeductDao.selectList(billDeductEntityWrapper);
        //预制发票
//        QueryWrapper<TXfPreInvoiceEntity> preInvoiceEntityWrapper = new QueryWrapper<>();
//        preInvoiceEntityWrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_ID, tXfSettlementEntity.getId());
//        List<TXfPreInvoiceEntity> tXfPreInvoiceEntityList = tXfPreInvoiceDao.selectList(preInvoiceEntityWrapper);

        //修改预制发票状态
//        tXfPreInvoiceEntityList.forEach(tXfPreInvoiceEntity -> {
//            TXfPreInvoiceEntity updateTXfPreInvoiceEntity = new TXfPreInvoiceEntity();
//            updateTXfPreInvoiceEntity.setId(tXfPreInvoiceEntity.getId());
//            updateTXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
//            tXfPreInvoiceDao.updateById(updateTXfPreInvoiceEntity);
//        });

        //修改结算单状态
        TXfSettlementEntity updateTXfSettlementEntity = new TXfSettlementEntity();
        updateTXfSettlementEntity.setId(tXfSettlementEntity.getId());
        updateTXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        updateTXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(updateTXfSettlementEntity);

        //修改索赔单状态
        billDeductList.forEach(tXfBillDeduct -> {
            TXfBillDeductEntity updateTXfBillDeductEntity = new TXfBillDeductEntity();
            updateTXfBillDeductEntity.setId(tXfBillDeduct.getId());
            updateTXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_MATCH_SETTLEMENT.getCode());
            updateTXfBillDeductEntity.setUpdateTime(new Date());
            tXfBillDeductDao.updateById(updateTXfBillDeductEntity);
        });

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlementId, OperateLogEnum.REJECT_VERDICT,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                0L,"系统");
    }

    /**
     * 通过申请索赔单不定案
     *
     * @param settlementId 结算单id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void agreeClaimVerdict(Long settlementId) {
        commClaimService.destroyClaimSettlement(settlementId);

        //日志
        TXfSettlementEntity settlement = tXfSettlementDao.selectById(settlementId);
        operateLogService.add(settlementId, OperateLogEnum.PASS_VERDICT,
                TXfSettlementStatusEnum.getTXfSettlementStatusEnum(settlement.getSettlementStatus()).getDesc(),"",
                0L,"系统");
    }

    /**
     * 通过索赔单id申请不定案 需要将所理赔单分组（根据结算单分组）
     * 供应商调用
     *
     * @param billDeductIdList 索赔单ID
     */
    public void applyClaimVerdictByBillDeductId(List<Long> billDeductIdList) {
        if (CollectionUtils.isEmpty(billDeductIdList)) {
            throw new EnhanceRuntimeException("参数异常");
        }
        List<TXfBillDeductEntity> tXfBillDeductEntityList = tXfBillDeductDao.selectBatchIds(billDeductIdList);
        if (CollectionUtils.isEmpty(tXfBillDeductEntityList)) {
            throw new EnhanceRuntimeException("索赔单不存在");
        }
        if (tXfBillDeductEntityList.size() != billDeductIdList.size()) {
            throw new EnhanceRuntimeException("索赔单缺失");
        }
        List<String> settlementNoList = tXfBillDeductEntityList.stream().map(TXfBillDeductEntity::getRefSettlementNo).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(settlementNoList)) {
            throw new EnhanceRuntimeException("您所选择的索赔单没有对应的结算单数据");
        }

        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TXfSettlementEntity.SETTLEMENT_NO, settlementNoList);
        List<TXfSettlementEntity> tXfSettlementEntityList = tXfSettlementDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(tXfSettlementEntityList)) {
            throw new EnhanceRuntimeException("索赔单没有对应的结算单数据");
        }

        Map<String, TXfSettlementEntity> settlementMap = new HashMap<>();
        tXfSettlementEntityList.forEach(x -> {
            settlementMap.put(x.getSettlementNo(), x);
        });
        //分组处理不定案
        Map<String, List<TXfBillDeductEntity>> settlementIdToBillDeductMap = tXfBillDeductEntityList.stream().collect(Collectors.groupingBy(TXfBillDeductEntity::getRefSettlementNo));
        Map<TXfSettlementEntity, List<TXfBillDeductEntity>> settlementBillDeDuctMap = new HashMap<>();

        settlementIdToBillDeductMap.forEach((k, v) -> {
            settlementBillDeDuctMap.put(settlementMap.get(k), v);
        });

        //<结算单id,Array<索赔单id列表>>
        settlementBillDeDuctMap.forEach((key, value) -> {
            List<Long> settlementDeductIdList = value.stream().map(TXfBillDeductEntity::getId).collect(Collectors.toList());
            doApplyVerdict(key, settlementDeductIdList);
        });
    }


    private void saveQuestionPaper(TXfSettlementEntity tXfSettlementEntity, List<TXfBillDeductEntity> billDeductList) {
        TDxQuestionPaperEntity tDxQuestionPaperEntity = new TDxQuestionPaperEntity();
        tDxQuestionPaperEntity.setQuestionType("8001");
        tDxQuestionPaperEntity.setPurchaser(tXfSettlementEntity.getPurchaserName());
        tDxQuestionPaperEntity.setJvcode(tXfSettlementEntity.getPurchaserNo());
        tDxQuestionPaperEntity.setUsercode(tXfSettlementEntity.getSellerNo());
        tDxQuestionPaperEntity.setUsername(tXfSettlementEntity.getSellerName());
        //用来存储结算单id
        tDxQuestionPaperEntity.setInvoiceNo(String.valueOf(tXfSettlementEntity.getId()));
        tDxQuestionPaperEntity.setProblemCause("100704");
        tDxQuestionPaperEntity.setDescription("索赔单号：" + Joiner.on(",").join(billDeductList.stream().map(TXfBillDeductEntity::getBusinessNo).collect(Collectors.toList())));
        tDxQuestionPaperEntity.setCheckstatus("0");
        //流水号
        tDxQuestionPaperEntity.setProblemStream(generateProblemStream(tXfSettlementEntity.getSellerNo()));
        tDxQuestionPaperEntity.setCreatedDate(new Date());
        tDxQuestionPaperDao.insert(tDxQuestionPaperEntity);
    }

    /**
     * 从一期拷贝的代码
     * @param usercode
     * @return
     */
    private String generateProblemStream(String usercode) {
        Date de = new Date();
        TDxQuestionPaperEntity querymaxstream = tDxQuestionPaperDao.queryMaxProblemStream(usercode);
        if (querymaxstream != null) {
            String str2 = querymaxstream.getProblemStream();
            if (!str2.equals(null) && !str2.equals("")) {
                str2 = str2.substring(6, 14);
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String str = df.format(de);
                if (str.equals(str2)) {
                    String str3 = querymaxstream.getProblemStream();
                    Long b = Long.valueOf(str3);
                    b = b + 1;
                    str3 = String.valueOf(b);
                    return str3;
                } else {
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
                    String str4 = usercode + df2.format(de) + "0000";
                    return str4;
                }
            } else {
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                String str = usercode + df.format(de) + "0000";
                return str;
            }
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String str = usercode + df.format(de) + "0000";
            return str;
        }
    }


}
