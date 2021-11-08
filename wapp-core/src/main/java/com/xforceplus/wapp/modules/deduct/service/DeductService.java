package com.xforceplus.wapp.modules.deduct.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.ExcelExportUtil;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.export.dto.DeductBillExportDto;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.dto.DeductDetailResponse;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.model.*;
import com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService;
import com.xforceplus.wapp.modules.ftp.service.FtpUtilService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.rednotification.service.ExportCommonService;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.models.TaxCode;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.threadpool.ThreadPoolManager;
import com.xforceplus.wapp.threadpool.callable.ExportDeductCallable;
import com.xforceplus.wapp.util.CodeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xforceplus.wapp.modules.exportlog.service.ExcelExportLogService.SERVICE_TYPE;

/**
 * 类描述：扣除单通用方法
 *
 * @ClassName DeductionService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 11:38
 */
@Service
@Slf4j
public class DeductService   {
    @Autowired
    protected TXfBillDeductExtDao  tXfBillDeductExtDao;

    @Autowired
    protected TXfBillDeductDao  tXfBillDeductDao;

    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    protected TXfBillDeductItemExtDao tXfBillDeductItemExtDao;

    @Autowired
    private TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;
    @Autowired
    protected TXfSettlementDao tXfSettlementDao;
    @Autowired
    protected TXfSettlementItemDao tXfSettlementItemDao;
    @Autowired
    protected   IDSequence idSequence;

    @Autowired
    protected TaxCodeServiceImpl taxCodeService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    protected BlueInvoiceService blueInvoiceService;
    @Autowired
    protected TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private ExcelExportLogService excelExportLogService;
    @Autowired
    private FtpUtilService ftpUtilService;
    @Autowired
    private ExportCommonService exportCommonService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;

    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    protected DefaultSettingServiceImpl defaultSettingService;
    @Autowired
    protected OverdueServiceImpl overdueService;

    /**
     * 接收索赔明细
     * 会由不同线程调用，每次调用，数据不会重复，由上游保证
     * @param
     * @return
     */
    public boolean receiveItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo ) {
        List<TXfBillDeductItemEntity> list =  transferBillItemData(claimBillItemDataList,batchNo);
        for (TXfBillDeductItemEntity tXfBillDeductItemEntity : list) {
            try {
                tXfBillDeductItemExtDao.insert(tXfBillDeductItemEntity);
            } catch (Exception e) {
                log.error("索赔单明细插入异常：{}  数据 {}",e,tXfBillDeductItemEntity);
            }
        }
        return true;
    }

    public List<TXfBillDeductItemEntity> transferBillItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo) {
        Date date = new Date();
        List<TXfBillDeductItemEntity> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(claimBillItemDataList)) {
            throw new EnhanceRuntimeException("","传入的单据明细数据为空");
        }
        for (ClaimBillItemData claimBillItemData : claimBillItemDataList) {
            TXfBillDeductItemEntity tmp = new TXfBillDeductItemEntity();
            if (Objects.isNull(claimBillItemData)) {
                continue;
            }
            BeanUtil.copyProperties(claimBillItemData, tmp);
            tmp.setGategoryNbr(defaultValue(claimBillItemData.getCategoryNbr()));
            tmp.setVnpkQuantity(defaultValue(claimBillItemData.getVnpkQuantity()).intValue());
            tmp.setPurchaserNo(defaultValue(claimBillItemData.getStoreNbr()));
            tmp.setDeptNbr(defaultValue(claimBillItemData.getDeptNbr()));
            tmp.setCreateTime(date);
            tmp.setId(idSequence.nextId());
            tmp.setRemainingAmount(defaultValue(claimBillItemData.getAmountWithoutTax()));
            tmp.setGoodsNoVer("33.0");
            tmp.setUpdateTime(tmp.getCreateTime());
            tmp.setAmountWithoutTax(defaultValue(claimBillItemData.getAmountWithoutTax()).setScale(2,RoundingMode.HALF_UP));
            tmp.setTaxAmount(defaultValue(claimBillItemData.getAmountWithoutTax()).multiply(defaultValue(claimBillItemData.getTaxRate())).setScale(2, RoundingMode.HALF_UP));
            tmp.setAmountWithTax(tmp.getAmountWithoutTax().add(tmp.getTaxAmount()));
            tmp.setSourceId(defaultValue(claimBillItemData.getId()));
            tmp.setBatchNo(StringUtils.EMPTY);
            tmp.setPrice(defaultValue(claimBillItemData.getPrice()));
            tmp.setUnit(defaultValue(claimBillItemData.getUnit()));
            tmp.setVnpkCost(defaultValue(claimBillItemData.getVnpkCost()));
            tmp.setQuantity(defaultValue(claimBillItemData.getQuantity()));
            tmp.setVerdictDate(claimBillItemData.getVerdictDate());
            tmp.setClaimNo(defaultValue(claimBillItemData.getClaimNo()));
            tmp.setUpc(defaultValue(claimBillItemData.getUpc()));
            tmp = fixTaxCode(tmp);
            list.add(tmp);
        }
        return list;
    }
    /**
     * 补充商品
     * @param entity
     * @return
     */
    public TXfBillDeductItemEntity fixTaxCode(  TXfBillDeductItemEntity entity) {
        if (StringUtils.isEmpty(entity.getItemNo())) {
            return entity;
        }
        try {
            Optional<TaxCode> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemNo());
            if (taxCodeOptional.isPresent()) {
                TaxCode taxCode = taxCodeOptional.get();
                entity.setGoodsTaxNo(taxCode.getGoodsTaxNo());
                entity.setTaxPre(taxCode.getTaxPre());
                entity.setTaxPreCon(taxCode.getTaxPreCon());
                entity.setZeroTax(taxCode.getZeroTax());
                entity.setItemShortName(taxCode.getSmallCategoryName());
            }
        } catch (Exception e) {
            log.error("查询税编异常：{}  异常 {} ）", entity.getItemNo(), e);
            entity.setGoodsTaxNo(StringUtils.EMPTY);
        }
        return entity;
    }
    /**
     * 补充商品
     * @param entity
     * @return
     */
    public TXfSettlementItemEntity fixTaxCode(  TXfSettlementItemEntity entity) {
        if (StringUtils.isEmpty(entity.getItemCode())) {
            return entity;
        }
        try {
            if (StringUtils.isNotEmpty(entity.getGoodsTaxNo())) {
                return entity;
            }
            Optional<TaxCode> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemCode());
            if (taxCodeOptional.isPresent()) {
                TaxCode taxCode = taxCodeOptional.get();
                entity.setGoodsTaxNo(taxCode.getGoodsTaxNo());
                entity.setTaxPre(taxCode.getTaxPre());
                entity.setTaxPreCon(taxCode.getTaxPreCon());
                entity.setZeroTax(taxCode.getZeroTax());
                entity.setItemShortName(taxCode.getSmallCategoryName());
            }
        } catch (Exception e) {
            log.error("查询税编异常：{}  异常 {} ）", entity.getItemCode(), e);
            entity.setGoodsTaxNo(StringUtils.EMPTY);
        }
        return entity;
    }
    /**
     * 接收索赔 协议 EPD主信息数据
     * @param deductBillBaseDataList
     * @param deductionEnum
     * @return
     */
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseDataList, TXfDeductionBusinessTypeEnum deductionEnum) {
        List<TXfBillDeductEntity> list = transferBillData(deductBillBaseDataList, deductionEnum);
        for (TXfBillDeductEntity tXfBillDeductEntity : list) {
            try {
                unlockAndCancel(deductionEnum, tXfBillDeductEntity);
                /**
                 * TODO 可以把购销对完整信息 提前保存，后续就也需要了
                 */
                TAcOrgEntity tAcSellerOrgEntity = queryOrgInfo(tXfBillDeductEntity.getSellerNo(), true);
                TAcOrgEntity tAcPurcharserOrgEntity = queryOrgInfo(tXfBillDeductEntity.getPurchaserNo(), false);
                tXfBillDeductEntity.setPurchaserName(tAcPurcharserOrgEntity.getCompany());
                tXfBillDeductEntity.setSellerName(tAcSellerOrgEntity.getCompany());
                tXfBillDeductExtDao.insert(tXfBillDeductEntity);
                //日志
                saveCreateDeductLog(tXfBillDeductEntity);
            } catch (Exception e) {
                log.error("{} 数据保存失败 异常{} 单据数据：{} ", deductionEnum.getDes(), e,tXfBillDeductEntity);
            }

        }
        return true;
    }

    protected void saveCreateDeductLog(TXfBillDeductEntity tXfBillDeductEntity) {
        if (Objects.equals(tXfBillDeductEntity.getBusinessType(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue())) {
            operateLogService.add(tXfBillDeductEntity.getId(), OperateLogEnum.CREATE_AGREEMENT,
                    TXfDeductStatusEnum.getEnumByCode(tXfBillDeductEntity.getStatus()).getDesc(),
                    0L,"系统");
        } else if (Objects.equals(tXfBillDeductEntity.getBusinessType(), TXfDeductionBusinessTypeEnum.EPD_BILL.getValue())) {
            operateLogService.add(tXfBillDeductEntity.getId(), OperateLogEnum.CREATE_EPD,
                    TXfDeductStatusEnum.getEnumByCode(tXfBillDeductEntity.getStatus()).getDesc(),
                    0L,"系统");
        }
    }

    public List<TXfBillDeductEntity> transferBillData(List<DeductBillBaseData> deductBillDataList ,  TXfDeductionBusinessTypeEnum deductionEnum) {
        if (CollectionUtils.isEmpty(deductBillDataList)) {
            log.error("{} 传入的单据数据为空 保存失败 ！！！！", deductionEnum.getDes()   );
            throw new EnhanceRuntimeException("","传入的单据数据为空");
        }
        Date date = new Date();
        List<TXfBillDeductEntity> list = new ArrayList<>();
        Optional<DeductionHandleEnum> optionalDedcutionHandleEnum = DeductionHandleEnum.getHandleEnum( deductionEnum);
        if (!optionalDedcutionHandleEnum.isPresent()) {
            log.error("{} 无效的单据类型 保存失败 ！！！！！", deductionEnum.getDes()   );
            throw new EnhanceRuntimeException("","无效的单据类型");
        }
        DeductionHandleEnum dedcutionHandleEnum = optionalDedcutionHandleEnum.get();
        for (DeductBillBaseData deductBillBaseData : deductBillDataList) {
            TAcOrgEntity purchaserOrgEntity = queryOrgInfo(deductBillBaseData.getPurchaserNo(),false);
            if (Objects.nonNull( purchaserOrgEntity)) {
                deductBillBaseData.setPurchaserName(defaultValue(purchaserOrgEntity.getOrgName()));
            }
            TXfBillDeductEntity tmp = dedcutionHandleEnum.function.apply(deductBillBaseData);
            tmp.setCreateTime(date);
            tmp.setUpdateTime(tmp.getCreateTime());
            tmp.setId(idSequence.nextId());
            list.add(tmp);
         }
        return list;
    }

    /**
     * 自动取消和解锁 自动解锁当天新增的EPD 协议单
     * @param deductionEnum
     * @return true - 操作成功，不存在失败的场景
     */
    public boolean unlockAndCancel(TXfDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity) {
        if (Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL)
                || Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.EPD_BILL)) {
            // 获取协议单号
            String reference = tXfBillDeductEntity.getAgreementReference();
            if (Objects.nonNull(reference)) {
                // 查找相同协议号的数据，取第一页数据，分页大小没有特殊要求，默认设置成10
                Page<TXfBillDeductEntity> pages = tXfBillDeductExtDao
                        .selectPage(new Page<>(1, 10),
                                new QueryWrapper<TXfBillDeductEntity>()
                                        .lambda()
                                        .eq(TXfBillDeductEntity::getBusinessType, deductionEnum.getValue())
                                        .eq(TXfBillDeductEntity::getAgreementReference, reference)
                        );
                log.info("根据单据id={}的匹配到的拥有相同的reference={}的单据数量为{}", tXfBillDeductEntity.getId(), reference, pages.getTotal());
                if (pages.getTotal() > 0) {
                    //  如果返回个数大于1，则取第一条记录
                    TXfBillDeductEntity target = pages.getRecords().get(0);
                    if (sameParties(tXfBillDeductEntity, target)) {
                        if (Objects.equals(TXfDeductStatusEnum.LOCK.getCode(), target.getLockFlag())){
                            boolean result = updateBillStatus(deductionEnum, target, TXfDeductStatusEnum.UNLOCK);
                            log.info("解锁source单据id={}的target单据的id={}, result={}", tXfBillDeductEntity.getId(), target.getId(), result);
                        }
                        tXfBillDeductEntity.setStatus(target.getStatus());
                    } else {
                        log.info("source单据id={}与target单据的id={}购销对不一致，跳过解锁取消逻辑", tXfBillDeductEntity.getId(), target.getId());
                    }
                }
            } else {
                log.warn("非法的单据id={}，协议号agreement reference为空，跳过解锁取消逻辑", tXfBillDeductEntity.getId());
            }
        }
        return true;
    }

    /**
     * 判断两个单据的交易双方是否一致
     *
     * @param source
     * @param target
     * @return
     */
    private boolean sameParties(TXfBillDeductEntity source, TXfBillDeductEntity target){
        if (Objects.isNull(source) || Objects.isNull(target)) {
            return false;
        }
        return Objects.equals(source.getPurchaserNo(), target.getPurchaserNo())
                && Objects.equals(source.getSellerNo(), target.getSellerNo());
    }

    /**
     * 更新协议单或EPD单
     *
     * @param deductionEnum {@link TXfDeductionBusinessTypeEnum} 单据类型
     * @param tXfBillDeductEntity {@link TXfBillDeductEntity} 单据实体
     * @param status {@link TXfDeductStatusEnum} 业务单状态
     * @return {boolean} true-更新成功, false-更新失败
     */
    public boolean updateBillStatus(TXfDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity, TXfDeductStatusEnum status) {
        if(tXfBillDeductEntity.getId() == null){
            log.error("Id不能为空");
            return false;
        }
        if(tXfBillDeductEntity.getStatus() == null){
            log.error("结算单状态不能为空");
            return false;
        }
        if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.CLAIM_DESTROY.equals(status)){
                log.info("索赔单不能撤销");
                return false;
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                log.info("索赔单不能锁定或解锁");
                return false;
            }
        }else if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.AGREEMENT_DESTROY.equals(status)){
                if(!TXfDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE.getCode().equals(tXfBillDeductEntity.getStatus())){
                    log.info("只有待匹配结算单的协议单才能撤销");
                    return false;
                }
                deleteBillDeductItemRef(tXfBillDeductEntity.getId());
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                if(!TXfDeductStatusEnum.AGREEMENT_NO_MATCH_BLUE_INVOICE.getCode().equals(tXfBillDeductEntity.getStatus())){
                    log.info("只有待匹配结算单的协议单才能锁定或解锁");
                    return false;
                }
            }
        }else if(TXfDeductionBusinessTypeEnum.EPD_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.EPD_DESTROY.equals(status)){
                if(!TXfDeductStatusEnum.EPD_NO_MATCH_BLUE_INVOICE.getCode().equals(tXfBillDeductEntity.getStatus())) {
                    log.info("只有待匹配结算单的EPD才能撤销");
                    return false;
                }
                deleteBillDeductItemRef(tXfBillDeductEntity.getId());
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                if(!TXfDeductStatusEnum.EPD_NO_MATCH_BLUE_INVOICE.getCode().equals(tXfBillDeductEntity.getStatus())) {
                    log.info("只有待匹配结算单的EPD才能锁定或解锁");
                    return false;
                }
            }
        }else {
            log.info("非法结算单类型");
            return false;
        }

        if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
            tXfBillDeductEntity.setLockFlag(status.getCode());
        }else{
           tXfBillDeductEntity.setStatus(status.getCode());
        }
        int count = tXfBillDeductExtDao.updateById(tXfBillDeductEntity);
        //添加操作日志
        addOperateLog(tXfBillDeductEntity.getId(),deductionEnum,status);
        return count >0;
    }

    /**
     * 删除业务单明细关系
     * @param tXfBillDeductId
     * @return
     */
    void deleteBillDeductItemRef(Long tXfBillDeductId){
        TXfBillDeductItemRefEntity refEntity = new TXfBillDeductItemRefEntity();
        refEntity.setStatus(1);
        UpdateWrapper<TXfBillDeductItemRefEntity> refWrapper = new UpdateWrapper<>();
        refWrapper.eq(TXfBillDeductItemRefEntity.DEDUCT_ID,tXfBillDeductId);
        tXfBillDeductItemRefDao.update(refEntity,refWrapper);
    }

    void addOperateLog(Long id, TXfDeductionBusinessTypeEnum typeEnum, TXfDeductStatusEnum statusEnum){
        OperateLogEnum logEnum = null;
        if(TXfDeductStatusEnum.AGREEMENT_DESTROY.equals(statusEnum)){
            logEnum = OperateLogEnum.CANCEL_AGREEMENT;
        }else if(TXfDeductStatusEnum.EPD_DESTROY.equals(statusEnum)){
            logEnum = OperateLogEnum.CANCEL_EPD;
        }else if(TXfDeductStatusEnum.UNLOCK.equals(statusEnum)){
            if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum)){
                logEnum = OperateLogEnum.UNLOCK_AGREEMENT;
            }else{
                logEnum = OperateLogEnum.UNLOCK_EPD;
            }
        }else if(TXfDeductStatusEnum.LOCK.equals(statusEnum)){
            if(TXfDeductionBusinessTypeEnum.EPD_BILL.equals(typeEnum)){
                logEnum = OperateLogEnum.LOCK_EPD;
            }else{
                logEnum = OperateLogEnum.LOCK_AGREEMENT;
            }
        }else{
            log.info("无需添加操作日志");
        }
        operateLogService.add(id,logEnum,"",UserUtil.getUserId(),UserUtil.getUserName());

    }


    /**
     * 结算单转换操作
     * @param tXfBillDeductEntities
     * @return
     */
     public TXfSettlementEntity trans2Settlement(List<TXfBillDeductEntity> tXfBillDeductEntities, TXfDeductionBusinessTypeEnum deductionBusinessTypeEnum) {
        if (CollectionUtils.isEmpty(tXfBillDeductEntities)) {
            log.warn("业务单合并结算单失败：{} 业务单集合为空",deductionBusinessTypeEnum.getDes());
            throw new RuntimeException(" 业务单集合为空，结算单生成失败");
        }
        String purchaserNo = tXfBillDeductEntities.get(0).getPurchaserNo();
        String sellerNo = tXfBillDeductEntities.get(0).getSellerNo();
        BigDecimal taxRate = tXfBillDeductEntities.get(0).getTaxRate();
        Integer type = deductionBusinessTypeEnum.getValue();
        Integer status = TXfDeductStatusEnum.CLAIM_NO_MATCH_SETTLEMENT.getCode();
        TXfSettlementEntity tXfSettlementEntity = new TXfSettlementEntity();
        TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaserNo,false);
        TAcOrgEntity sellerOrgEntity = queryOrgInfo(sellerNo, true);
        BigDecimal amountWithoutTax = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        for (TXfBillDeductEntity tmp : tXfBillDeductEntities) {
            amountWithoutTax = amountWithoutTax.add(tmp.getAmountWithoutTax());
            amountWithTax = amountWithTax.add(tmp.getAmountWithTax());
            taxAmount = taxAmount.add(tmp.getTaxAmount());
        }
        tXfSettlementEntity.setAmountWithoutTax(amountWithoutTax.negate());
        tXfSettlementEntity.setAmountWithTax(amountWithTax.negate());
        tXfSettlementEntity.setTaxAmount(taxAmount.negate());
        tXfSettlementEntity.setSellerNo(sellerNo);
        tXfSettlementEntity.setSellerTaxNo(sellerOrgEntity.getTaxNo());
        tXfSettlementEntity.setSellerAddress(defaultValue(sellerOrgEntity.getAddress()));
        tXfSettlementEntity.setSellerBankAccount(sellerOrgEntity.getAccount());
        tXfSettlementEntity.setSellerBankName(sellerOrgEntity.getBank());
        tXfSettlementEntity.setSellerName(defaultValue(sellerOrgEntity.getOrgName()));
        tXfSettlementEntity.setSellerTel(defaultValue(sellerOrgEntity.getPhone()));
        tXfSettlementEntity.setPurchaserNo(purchaserNo);
        tXfSettlementEntity.setRemark(StringUtils.EMPTY);
        tXfSettlementEntity.setPurchaserTaxNo(purchaserOrgEntity.getTaxNo());
        tXfSettlementEntity.setPurchaserAddress(purchaserOrgEntity.getAddress());
        tXfSettlementEntity.setPurchaserBankAccount(purchaserOrgEntity.getAccount());
        tXfSettlementEntity.setPurchaserBankName(purchaserOrgEntity.getBank());
        tXfSettlementEntity.setPurchaserName(defaultValue(purchaserOrgEntity.getOrgName()));
        tXfSettlementEntity.setPurchaserTel(defaultValue(purchaserOrgEntity.getPhone()) );
        tXfSettlementEntity.setAvailableAmount(tXfSettlementEntity.getAmountWithoutTax());
        tXfSettlementEntity.setTaxRate(taxRate);
        tXfSettlementEntity.setId(idSequence.nextId());
        tXfSettlementEntity.setBatchNo(StringUtils.EMPTY);
        tXfSettlementEntity.setSettlementNo(CodeGenerator.generateCode(deductionBusinessTypeEnum));
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_BLUE_INVOICE.getCode());
        tXfSettlementEntity.setCreateTime(DateUtils.getNow());
        tXfSettlementEntity.setUpdateTime(tXfSettlementEntity.getCreateTime());
        tXfSettlementEntity.setUpdateUser(0L);
        tXfSettlementEntity.setCreateUser(0L);
        tXfSettlementEntity.setInvoiceType(InvoiceTypeEnum.SPECIAL_INVOICE.getValue());
        tXfSettlementEntity.setSettlementType(deductionBusinessTypeEnum.getValue());
        tXfSettlementEntity.setPriceMethod(0);
        /**
         * 索赔单 直接生成 结算单
         */
         BigDecimal taxRateTotal = BigDecimal.ZERO;
         Integer tmpStatus = TXfSettlementItemFlagEnum.NORMAL.getCode();
        if (deductionBusinessTypeEnum == TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
            List<TXfBillDeductItemEntity> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryItemsByBill(purchaserNo,sellerNo,type,status);
            for (TXfBillDeductItemEntity tXfBillDeductItemEntity : tXfBillDeductItemEntities) {
                TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
                BeanUtils.copyProperties(tXfBillDeductItemEntity,tXfSettlementItemEntity);
                tXfSettlementItemEntity.setItemName(tXfBillDeductItemEntity.getCnDesc());
                tXfSettlementItemEntity.setQuantity(tXfBillDeductItemEntity.getQuantity().negate());
                tXfSettlementItemEntity.setTaxRate(tXfBillDeductItemEntity.getTaxRate());
                tXfSettlementItemEntity.setItemCode(tXfBillDeductItemEntity.getItemNo());
                tXfSettlementItemEntity.setAmountWithoutTax(tXfBillDeductItemEntity.getAmountWithoutTax().negate());
                tXfSettlementItemEntity.setTaxAmount(tXfSettlementItemEntity.getAmountWithoutTax().multiply(tXfBillDeductItemEntity.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
                tXfSettlementItemEntity.setAmountWithTax(tXfSettlementItemEntity.getTaxAmount().add(tXfSettlementItemEntity.getAmountWithoutTax()));
                tXfSettlementItemEntity.setQuantityUnit(tXfBillDeductItemEntity.getUnit());
                tXfSettlementItemEntity.setItemSpec(tXfBillDeductItemEntity.getCnDesc());
                tXfSettlementItemEntity.setCreateTime(DateUtils.getNow());
                tXfSettlementItemEntity.setUpdateTime(tXfSettlementItemEntity.getCreateTime());
                tXfSettlementItemEntity.setItemStatus(0);
                tXfSettlementItemEntity.setUnitPrice(tXfBillDeductItemEntity.getPrice());
                tXfSettlementItemEntity.setId(idSequence.nextId());
                tXfSettlementItemEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
                tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
                tXfSettlementItemEntity.setCreateUser(0L);
                tXfSettlementItemEntity.setUnitPriceWithTax(tXfSettlementItemEntity.getAmountWithTax().divide(tXfSettlementItemEntity.getQuantity(), 6, RoundingMode.HALF_UP));
                tXfSettlementItemEntity.setUpdateUser(tXfSettlementItemEntity.getCreateUser());
                tXfSettlementItemEntity.setThridId(tXfBillDeductItemEntity.getId());
                tXfSettlementItemEntity = checkItem(tXfSettlementItemEntity);
                if (tmpStatus < tXfSettlementItemEntity.getItemFlag() ) {
                    tmpStatus = tXfSettlementItemEntity.getItemFlag();
                }
                tXfSettlementItemDao.insert(tXfSettlementItemEntity);
                taxRateTotal = taxRateTotal.add(tXfBillDeductItemEntity.getTaxRate());
            }
        }else{
            tXfSettlementEntity.setTaxRate(tXfBillDeductEntities.get(0).getTaxRate());
            taxRateTotal = tXfSettlementEntity.getTaxRate();
        }
         /**
          * 部分匹配 索赔单明细 需要确认数据单据，如果不需要确认，进入拆票流程，状态是 待拆票
          */
          if(tmpStatus == TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode()){
             tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
         }
         else if(tmpStatus == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()){
             tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
         }
         else if(tmpStatus == TXfSettlementItemFlagEnum.NORMAL.getCode()){
             tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
         }
         if (taxRateTotal.compareTo(BigDecimal.ZERO) == 0) {
             tXfSettlementEntity.setInvoiceType(InvoiceTypeEnum.GENERAL_INVOICE.getValue());
         }
         tXfSettlementDao.insert(tXfSettlementEntity);
         //日志
         operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
                 TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()).getDesc(),
                 0L,"系统");
          tXfSettlementEntity.setAmountWithoutTax(tXfSettlementEntity.getAmountWithoutTax().negate());
          tXfSettlementEntity.setTaxAmount(tXfSettlementEntity.getTaxAmount().negate());
          tXfSettlementEntity.setAmountWithTax(tXfSettlementEntity.getAmountWithTax().negate());

         return tXfSettlementEntity;
    }

    public TAcOrgEntity queryOrgInfo(String no, boolean iseller) {
        TAcOrgEntity res ;
        if (iseller) {
            res =  companyService.getOrgInfoByOrgCode(no, "8");
        }else{
            res = companyService.getOrgInfoByOrgCode(no, "5");
        }

        return res;
    }

    static String defaultValue(String value) {
        return StringUtils.isEmpty(value) ? StringUtils.EMPTY : value;
    }
    static   BigDecimal defaultValue(BigDecimal value) {
        return Objects.isNull(value) ? BigDecimal.ZERO : value;
    }
    private Integer defaultValue(Integer value) { return Objects.isNull(value) ? 0 : value; }
    static Long defaultValue(Long value) {  return Objects.isNull(value) ? 0L : value; }


    enum DeductionHandleEnum {
        CLAIM_BILL(TXfDeductionBusinessTypeEnum.CLAIM_BILL, x -> {
            ClaimBillData tmp = (ClaimBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());

            tXfBillDeductEntity.setAmountWithoutTax(defaultValue(x.getAmountWithoutTax()));
            tXfBillDeductEntity.setAmountWithTax( defaultValue(x.getAmountWithTax()));
            tXfBillDeductEntity.setTaxAmount(tXfBillDeductEntity.getAmountWithTax().subtract(tXfBillDeductEntity.getAmountWithoutTax()));

            tXfBillDeductEntity.setDeductInvoice(tmp.getInvoiceReference());
             return tXfBillDeductEntity;
        }) ,
        AGREEMENT_BILL(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL, x -> {
            AgreementBillData tmp = (AgreementBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setAgreementDocumentNumber(defaultValue(tmp.getDocumentNo()));
            tXfBillDeductEntity.setAgreementDocumentType(defaultValue(tmp.getDocumentType()) );
            tXfBillDeductEntity.setAgreementMemo(defaultValue(tmp.getMemo()));
            tXfBillDeductEntity.setAgreementReasonCode(defaultValue(tmp.getReasonCode()));
            tXfBillDeductEntity.setAgreementReference(defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setAgreementTaxCode(defaultValue(tmp.getTaxCode()));
            tXfBillDeductEntity.setDeductInvoice(StringUtils.EMPTY);
            tXfBillDeductEntity.setVerdictDate(tmp.getDeductDate());
            tXfBillDeductEntity.setBusinessNo(defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setAmountWithTax( defaultValue(x.getAmountWithTax()));
            tXfBillDeductEntity.setTaxAmount(defaultValue(x.getTaxAmount()));
            tXfBillDeductEntity.setAmountWithoutTax(tXfBillDeductEntity.getAmountWithTax().subtract(tXfBillDeductEntity.getTaxAmount()));
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
            return tXfBillDeductEntity;
        }),
        EPD_BILL(TXfDeductionBusinessTypeEnum.EPD_BILL, x -> {
            EPDBillData tmp = (EPDBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setAgreementMemo(defaultValue(tmp.getMemo()));
            tXfBillDeductEntity.setVerdictDate(tmp.getDeductDate());
            tXfBillDeductEntity.setAgreementReasonCode(defaultValue(tmp.getReasonCode()));
            tXfBillDeductEntity.setAgreementReference(defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setAgreementTaxCode(defaultValue(tmp.getTaxCode()));
            tXfBillDeductEntity.setAgreementDocumentType(defaultValue(tmp.getDocumentType()));
            tXfBillDeductEntity.setAgreementMemo(defaultValue(tmp.getDocumentNo()));
            tXfBillDeductEntity.setAmountWithTax( defaultValue(x.getAmountWithTax()));
            tXfBillDeductEntity.setAmountWithoutTax(tXfBillDeductEntity.getAmountWithTax().divide(BigDecimal.ONE.add(defaultValue(tXfBillDeductEntity.getTaxRate())), 2, RoundingMode.HALF_UP));
            tXfBillDeductEntity.setTaxAmount(tXfBillDeductEntity.getAmountWithTax().subtract(tXfBillDeductEntity.getAmountWithoutTax()));
            tXfBillDeductEntity.setBusinessNo( defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            return tXfBillDeductEntity;
        });

        private TXfDeductionBusinessTypeEnum deductionEnum;
        private Function<DeductBillBaseData,   TXfBillDeductEntity> function;

        DeductionHandleEnum(TXfDeductionBusinessTypeEnum deductionEnum, Function<DeductBillBaseData,   TXfBillDeductEntity> function) {
            this.deductionEnum = deductionEnum;
            this.function = function;
        }

        public static Optional <DeductionHandleEnum> getHandleEnum(TXfDeductionBusinessTypeEnum xfDeductionEnum) {
            DeductionHandleEnum[] dedcutionHandleEnums = DeductionHandleEnum.values();
            for (DeductionHandleEnum tmp : dedcutionHandleEnums) {
                if (tmp.deductionEnum == xfDeductionEnum) {
                    return Optional.of(tmp);
                }
            }
            return Optional.empty();
        }
    }

    private static TXfBillDeductEntity dataTrans(DeductBillBaseData deductBillBaseData) {
        TXfBillDeductEntity tXfBillDeductEntity = new TXfBillDeductEntity();
        BeanUtils.copyProperties(deductBillBaseData, tXfBillDeductEntity);
        tXfBillDeductEntity.setAgreementDocumentNumber(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementDocumentType(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementMemo(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementReasonCode(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementReference(StringUtils.EMPTY);
        tXfBillDeductEntity.setRefSettlementNo(StringUtils.EMPTY);
        tXfBillDeductEntity.setAgreementTaxCode(StringUtils.EMPTY);
        tXfBillDeductEntity.setDeductInvoice(StringUtils.EMPTY);
        tXfBillDeductEntity.setLockFlag(TXfDeductStatusEnum.UNLOCK.getCode());
        tXfBillDeductEntity.setSourceId(defaultValue(deductBillBaseData.getId()));
        tXfBillDeductEntity.setSellerName(defaultValue(deductBillBaseData.getSellerName()));
        tXfBillDeductEntity.setPurchaserNo(defaultValue(deductBillBaseData.getPurchaserNo()));
        tXfBillDeductEntity.setSellerNo(defaultValue(deductBillBaseData.getSellerNo()));
        tXfBillDeductEntity.setBusinessNo(defaultValue(deductBillBaseData.getBusinessNo()));
        tXfBillDeductEntity.setBatchNo(defaultValue(deductBillBaseData.getBatchNo()));
        tXfBillDeductEntity.setTaxRate(defaultValue(deductBillBaseData.getTaxRate()));
        tXfBillDeductEntity.setPurchaserName(defaultValue(deductBillBaseData.getPurchaserName()));
        return tXfBillDeductEntity;
    }

    public TXfBillDeductEntity getDeductById(Long id){
        return tXfBillDeductDao.selectById(id);
    }


    /**
     * 业务单列表
     * @param request
     * @return PageResult
     */
    public PageResult<QueryDeductListResponse> queryPageList(QueryDeductListRequest request){
        Integer offset = (request.getPageNo() -1) * request.getPageSize();
        Integer next = request.getPageSize();
        int count = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), request.getKey());
        List<TXfBillDeductExtEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryBillPage(offset,next,request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), request.getKey());
        List<QueryDeductListResponse> response = new ArrayList<>();
        BeanUtil.copyList(tXfBillDeductEntities,response,QueryDeductListResponse.class);
        //key为1和2 添加红字信息编号
        if(DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue().equals(request.getKey()) || DeductBillTabEnum.APPLYED_RED_NO.getValue().equals(request.getKey())){
            this.redNotificationNo(response);
        }
        return PageResult.of(response,count, request.getPageNo(), request.getPageSize());
    }

    /**
     * 业务单列表tab
     * @param request
     * @return PageResult
     */
    public List<JSONObject> queryPageTab(QueryDeductListRequest request){
        List<JSONObject> list = new ArrayList<>();
        int key0 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.TO_BE_MATCH.getValue());
        JSONObject jsonObject0 = new JSONObject();
        jsonObject0.put("key",DeductBillTabEnum.TO_BE_MATCH.getValue());
        jsonObject0.put("count",key0);
        jsonObject0.put("desc",DeductBillTabEnum.TO_BE_MATCH.getDesc());
        list.add(jsonObject0);
        int key1 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue());
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("key",DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue());
        jsonObject1.put("count",key1);
        jsonObject1.put("desc",DeductBillTabEnum.MATCHED_TO_BE_MAKE.getDesc());
        list.add(jsonObject1);
        int key2 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.APPLYED_RED_NO.getValue());
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key",DeductBillTabEnum.APPLYED_RED_NO.getValue());
        jsonObject2.put("count",key2);
        jsonObject2.put("desc",DeductBillTabEnum.APPLYED_RED_NO.getDesc());
        list.add(jsonObject2);
        int key3 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.MAKEED.getValue());
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("key",DeductBillTabEnum.MAKEED.getValue());
        jsonObject3.put("count",key3);
        jsonObject3.put("desc",DeductBillTabEnum.MAKEED.getDesc());
        list.add(jsonObject3);
        int key4 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.CANCELED.getValue());
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("key",DeductBillTabEnum.CANCELED.getValue());
        jsonObject4.put("count",key4);
        jsonObject4.put("desc",DeductBillTabEnum.CANCELED.getDesc());
        list.add(jsonObject4);
        return list;
    }

    /**
     * 业务单明细
     * @param id
     * @return DeductDetailResponse
     */
    public DeductDetailResponse getDeductDetailById(Long id){
        TXfBillDeductEntity deductById = getDeductById(id);
        DeductDetailResponse response = new DeductDetailResponse();
        if(deductById != null){
            QueryWrapper<TXfBillDeductItemRefEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TXfBillDeductItemRefEntity.DEDUCT_ID,id);
            wrapper.eq(TXfBillDeductItemRefEntity.STATUS,0);
            List<TXfBillDeductItemRefEntity> tXfBillDeductItemRefEntities = tXfBillDeductItemRefDao.selectList(wrapper);
            List<DeductBillItemModel> deductBillItemList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(tXfBillDeductItemRefEntities)){
                for (TXfBillDeductItemRefEntity tXfBillDeductItemRefEntity : tXfBillDeductItemRefEntities) {
                    TXfBillDeductItemEntity itemEntity =  tXfBillDeductItemDao.selectById(tXfBillDeductItemRefEntity.getDeductItemId());
                    DeductBillItemModel deductBillItemModel;
                    if(itemEntity != null){
                        deductBillItemModel = new DeductBillItemModel();
                        BeanUtil.copyProperties(itemEntity,deductBillItemModel);
                        deductBillItemList.add(deductBillItemModel);
                    }
                }
                response.setDeductBillItemList(deductBillItemList);
            }
            response.setVerdictDate(deductById.getVerdictDate());
            response.setBusinessNo(deductById.getBusinessNo());
            response.setPurchaserNo(deductById.getPurchaserNo());
            response.setSellerNo(deductById.getSellerNo());
        }
        return response;
    }

    public boolean export(DeductExportRequest request) {
        final Long userId = UserUtil.getUserId();
        TXfDeductionBusinessTypeEnum typeEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class,request.getBusinessType()).get();
        DeductBillExportDto dto = new DeductBillExportDto();
        dto.setType(typeEnum);
        dto.setRequest(request);
        dto.setUserId(userId);
        dto.setLoginName(UserUtil.getLoginName());
        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
        excelExportlogEntity.setCreateDate(new Date());
        //这里的userAccount是userid
        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
        excelExportlogEntity.setUserName(dto.getLoginName());
        excelExportlogEntity.setConditions(JSON.toJSONString(request));
        excelExportlogEntity.setStartDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
        excelExportlogEntity.setServiceType(SERVICE_TYPE);
        boolean count = this.excelExportLogService.save(excelExportlogEntity);
        dto.setLogId(excelExportlogEntity.getId());
        ExportDeductCallable callable = new ExportDeductCallable(this,dto);
        ThreadPoolManager.submitCustomL1(callable);
        return count;
    }

    public boolean doExport(DeductBillExportDto exportDto){
        boolean flag = true;
        DeductExportRequest request = exportDto.getRequest();
        TXfDeductionBusinessTypeEnum typeEnum = exportDto.getType();
        //这里的userAccount是userid
        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
        excelExportlogEntity.setEndDate(new Date());
        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
        //这里的userAccount是userName
        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
        messagecontrolEntity.setContent(getSuccContent());
        //主信息
        List<QueryDeductListResponse> queryDeductListResponse = getExportMainData(request);
        if(CollectionUtils.isEmpty(queryDeductListResponse)){
            log.info("业务单导出--未查到数据");
            return false;
        }
        final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), exportDto.getType().getDes());
        ExcelWriter excelWriter;
        ByteArrayInputStream in = null;
        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            excelWriter = EasyExcel.write(out).excelType(ExcelTypeEnum.XLSX).build();
            //创建一个sheet
            WriteSheet writeSheet = EasyExcel.writerSheet(0, "主信息").build();
            List exportList = new LinkedList();
            if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)){
                BeanUtil.copyList(queryDeductListResponse,exportList,ExportClaimBillModel.class);
                writeSheet.setClazz(ExportClaimBillModel.class);
            }else if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum)){
                BeanUtil.copyList(queryDeductListResponse,exportList,ExportAgreementBillModel.class);
                writeSheet.setClazz(ExportAgreementBillModel.class);
            }else{
                BeanUtil.copyList(queryDeductListResponse,exportList,ExportEPDBillModel.class);
                writeSheet.setClazz(ExportEPDBillModel.class);
            }
            excelWriter.write(exportList, writeSheet);
            //只有索赔单有明细信息
            if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)){
                List<ExportClaimBillItemModel> exportItem = getExportItem(queryDeductListResponse.stream().map(QueryDeductListResponse::getId).collect(Collectors.toList()));
                //创建一个新的sheet
                WriteSheet writeSheet1 = EasyExcel.writerSheet(1, "明细信息").build();
                writeSheet1.setClazz(ExportClaimBillItemModel.class);
                excelWriter.write(exportItem, writeSheet1);
            }
            excelWriter.finish();
            //推送sftp
            String ftpFilePath = ftpPath + "/" + excelFileName;
            in = new ByteArrayInputStream(out.toByteArray());
            ftpUtilService.uploadFile(ftpPath, excelFileName, in);
            messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
            excelExportlogEntity.setFilepath(ftpFilePath);
            messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(exportDto.getType().getDes()+"导出失败:" + e.getMessage(), e);
            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
            excelExportlogEntity.setErrmsg(e.getMessage());
            messagecontrolEntity.setTitle( exportDto.getType().getDes() + "导出失败");
            messagecontrolEntity.setContent(exportCommonService.getFailContent(e.getMessage()));
            flag = false;
        } finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            excelExportLogService.updateById(excelExportlogEntity);
            commonMessageService.sendMessage(messagecontrolEntity);
        }
        return flag;
    }



    private QueryWrapper<TXfBillDeductEntity>  getQueryWrapper(QueryDeductListRequest   request){
        QueryWrapper<TXfBillDeductEntity> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(request.getBusinessNo())){
            wrapper.eq(TXfBillDeductEntity.BUSINESS_NO,request.getBusinessNo());
        }
        if(StringUtils.isNotEmpty(request.getPurchaserNo())){
            wrapper.eq(TXfBillDeductEntity.PURCHASER_NO,request.getPurchaserNo());
        }
        if(StringUtils.isNotEmpty(request.getSellerName())){
            wrapper.eq(TXfBillDeductEntity.SELLER_NAME,request.getSellerName());
        }
        if(StringUtils.isNotEmpty(request.getSellerNo())){
            wrapper.eq(TXfBillDeductEntity.SELLER_NO,request.getSellerNo());
        }
        if(request.getBusinessType() != null){
            wrapper.eq(TXfBillDeductEntity.BUSINESS_TYPE,request.getBusinessType());
        }
//        if(request.getDeductDate() != null){
//            wrapper.apply("format(deduct_date,'yyyy-MM-dd') = {0}",request.getDeductDate());
//        }
        return wrapper;
    }

    /**
     * 匹配蓝票 转换
     * @param res
     * @param settlementNo
     * @param xfDeductionBusinessTypeEnum
     * @return
     */
    public   Integer  matchInfoTransfer(List<BlueInvoiceService.MatchRes> res, String settlementNo, Long id, TXfDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
        Date date = new Date();
        Integer status = TXfSettlementItemFlagEnum.NORMAL.getCode();
        Integer relationType = xfDeductionBusinessTypeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL?TXfInvoiceDeductTypeEnum.SETTLEMENT.getCode():TXfInvoiceDeductTypeEnum.CLAIM.getCode();
            for (BlueInvoiceService.MatchRes matchRes : res) {
                if (xfDeductionBusinessTypeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
                      for(BlueInvoiceService.InvoiceItem invoiceItem:matchRes.getInvoiceItems()){
                        TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
                        tXfSettlementItemEntity.setUnitPrice(invoiceItem.getUnitPrice());
                        tXfSettlementItemEntity.setTaxAmount(invoiceItem.getTaxAmount());
                        tXfSettlementItemEntity.setGoodsTaxNo(invoiceItem.getGoodsNum());
                        tXfSettlementItemEntity.setTaxRate(TaxRateTransferEnum.transferTaxRate(invoiceItem.getTaxRate()));
                        tXfSettlementItemEntity.setAmountWithoutTax(defaultValue(invoiceItem.getDetailAmount()));
                        tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
                        tXfSettlementItemEntity.setQuantity(invoiceItem.getNum());
                        tXfSettlementItemEntity.setUnitPrice(invoiceItem.getUnitPrice());
                        tXfSettlementItemEntity.setUnitPriceWithTax(invoiceItem.getUnitPrice());
                        tXfSettlementItemEntity.setAmountWithTax(invoiceItem.getDetailAmount().add(invoiceItem.getTaxAmount()));

                        tXfSettlementItemEntity.setCreateUser(0l);
                        tXfSettlementItemEntity.setUpdateUser(0l);
                        tXfSettlementItemEntity.setId(idSequence.nextId());
                        tXfSettlementItemEntity.setSettlementNo(settlementNo);
                        tXfSettlementItemEntity.setCreateTime(date);
                        tXfSettlementItemEntity.setUpdateTime(date);
                        tXfSettlementItemEntity.setItemCode(defaultValue(invoiceItem.getGoodsNum()));
                        tXfSettlementItemEntity.setThridId(defaultValue(invoiceItem.getItemId()));
                        tXfSettlementItemEntity.setItemName(invoiceItem.getGoodsName());
                        tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
                        tXfSettlementItemEntity.setGoodsNoVer("33.0");
                        tXfSettlementItemEntity.setZeroTax(StringUtils.EMPTY);
                        tXfSettlementItemEntity.setTaxPre(StringUtils.EMPTY);
                        tXfSettlementItemEntity.setTaxPreCon(StringUtils.EMPTY);
                        tXfSettlementItemEntity = checkItem(  tXfSettlementItemEntity);

                        if (StringUtils.isBlank(tXfSettlementItemEntity.getItemShortName())){
                            final String itemName = tXfSettlementItemEntity.getItemName();
                            final int first = itemName.indexOf("*");
                            final int length = itemName.length();
                            if (first > -1 && length > first+1) {
                                int end = itemName.indexOf("*", first + 1);
                                if (end > -1 && length > end) {
                                    final String shortName = itemName.substring(first + 1, end);
                                    tXfSettlementItemEntity.setItemShortName(shortName);
                                }
                            }
                        }

                        if (status < tXfSettlementItemEntity.getItemFlag() ) {
                            status = tXfSettlementItemEntity.getItemFlag();
                        }
                        tXfSettlementItemDao.insert(tXfSettlementItemEntity);
                    }
              }
            TXfBillDeductInvoiceEntity tXfBillDeductInvoiceEntity = new TXfBillDeductInvoiceEntity();
            tXfBillDeductInvoiceEntity.setId(idSequence.nextId());
            tXfBillDeductInvoiceEntity.setBusinessNo(settlementNo);
            tXfBillDeductInvoiceEntity.setBusinessType(relationType);
            tXfBillDeductInvoiceEntity.setInvoiceCode(matchRes.invoiceCode);
            tXfBillDeductInvoiceEntity.setInvoiceNo(matchRes.invoiceNo);
            tXfBillDeductInvoiceEntity.setCreateTime(date);
            tXfBillDeductInvoiceEntity.setUpdateTime(date);
            tXfBillDeductInvoiceEntity.setThridId(id);
            tXfBillDeductInvoiceEntity.setUseAmount(matchRes.deductedAmount);
            tXfBillDeductInvoiceEntity.setStatus(TXfInvoiceDeductStatusEnum.NORMAL.getCode());
            tXfBillDeductInvoiceDao.insert(tXfBillDeductInvoiceEntity);
        }
        return status;
    }

    /**
     * 结算单明细校验
     * @param tXfSettlementItemEntity
     * @return
     */
    public TXfSettlementItemEntity checkItem(TXfSettlementItemEntity tXfSettlementItemEntity ) {
        tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
        BigDecimal ta = tXfSettlementItemEntity.getQuantity().multiply(tXfSettlementItemEntity.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
        if (ta.compareTo(tXfSettlementItemEntity.getAmountWithoutTax()) != 0) {
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
        }
        tXfSettlementItemEntity = fixTaxCode(tXfSettlementItemEntity );
        if (StringUtils.isEmpty(tXfSettlementItemEntity.getGoodsTaxNo())) {
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode());
        }
        return tXfSettlementItemEntity;
    }

    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    public List<QueryDeductListResponse> getExportMainData(DeductExportRequest request){
        if(CollectionUtils.isNotEmpty(request.getIdList())){
            String idsStr = StringUtils.join(request.getIdList(), ",");
            idsStr = "(" + idsStr + ")";
            request.setIds(idsStr);
        }
        PageResult<QueryDeductListResponse> pageResult = queryPageList(request);
        return pageResult.getRows() ;
    }

    public List<ExportClaimBillItemModel> getExportItem(List<Long> idList){
        List<ExportClaimBillItemModel> response = new ArrayList<>();
        for (Long id : idList) {
            DeductDetailResponse deductDetailById = getDeductDetailById(id);
            if(deductDetailById != null && CollectionUtils.isNotEmpty(deductDetailById.getDeductBillItemList())){
                BeanUtil.copyList(deductDetailById.getDeductBillItemList(),response,ExportClaimBillItemModel.class);
            }
        }
        return response;
    }

    public void redNotificationNo(List<QueryDeductListResponse> list){
        for (QueryDeductListResponse entity : list) {
            QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TXfPreInvoiceEntity.SETTLEMENT_NO,entity.getRefSettlementNo());
            List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = tXfPreInvoiceDao.selectList(wrapper);
            StringBuilder sb =new StringBuilder();
            if(CollectionUtils.isNotEmpty(tXfPreInvoiceEntities)){
                for (int i = 0; i < tXfPreInvoiceEntities.size(); i++) {
                    String redNotificationNo = tXfPreInvoiceEntities.get(i).getRedNotificationNo();
                    if(StringUtils.isNotEmpty(redNotificationNo)){
                        sb.append(redNotificationNo);
                        if(i != tXfPreInvoiceEntities.size()-1){
                            sb.append(",");
                        }
                    }
                }
            }
            entity.setRedNotificationNo(sb.toString());
        }
    }

}
