package com.xforceplus.wapp.modules.deduct.service;

import static com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum.AGREEMENT_BILL;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xforceplus.wapp.client.TaxCodeBean;
import com.xforceplus.wapp.common.dto.PageResult;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.DeductBillTabEnum;
import com.xforceplus.wapp.enums.InvoiceStatusEnum;
import com.xforceplus.wapp.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.enums.OperateLogEnum;
import com.xforceplus.wapp.enums.TXfDeductStatusEnum;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementItemFlagEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.backfill.model.InvoiceDetailResponse;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.dto.DeductDetailResponse;
import com.xforceplus.wapp.modules.deduct.dto.DeductExportRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListRequest;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import com.xforceplus.wapp.modules.deduct.model.AgreementBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillData;
import com.xforceplus.wapp.modules.deduct.model.ClaimBillItemData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillBaseData;
import com.xforceplus.wapp.modules.deduct.model.DeductBillItemModel;
import com.xforceplus.wapp.modules.deduct.model.EPDBillData;
import com.xforceplus.wapp.modules.deduct.model.ExportClaimBillItemModel;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.overdue.service.OverdueServiceImpl;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import com.xforceplus.wapp.modules.taxcode.dto.TaxCodeDto;
import com.xforceplus.wapp.modules.taxcode.service.TaxCodeServiceImpl;
import com.xforceplus.wapp.repository.dao.TXfBillDeductDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductExtDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductInvoiceDetailDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemExtDao;
import com.xforceplus.wapp.repository.dao.TXfBillDeductItemRefExtDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemDao;
import com.xforceplus.wapp.repository.dto.TXfBillDeductItemExtDto;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductExtEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductItemRefEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.TransactionalService;
import com.xforceplus.wapp.util.CodeGenerator;
import com.xforceplus.wapp.util.ItemNameUtils;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

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
public class DeductService  {
    @Autowired
    protected TXfBillDeductExtDao  tXfBillDeductExtDao;
    @Autowired
    protected TXfBillDeductDao  tXfBillDeductDao;
    @Autowired
    private TXfBillDeductItemDao tXfBillDeductItemDao;
    @Autowired
    protected TXfBillDeductItemExtDao tXfBillDeductItemExtDao;
    @Autowired
    protected TXfBillDeductItemRefExtDao tXfBillDeductItemRefDao;
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
    protected TXfBillDeductInvoiceDetailDao tXfBillDeductInvoiceDetailDao;
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    protected OperateLogService operateLogService;
    @Autowired
    protected DefaultSettingServiceImpl defaultSettingService;
    @Autowired
    protected OverdueServiceImpl overdueService;
    @Autowired
    private RecordInvoiceService recordInvoiceService;
    @Autowired
    protected TransactionalService transactionalService;
    @Autowired
    protected DeductBatchService deductBatchService;
    @Autowired
    protected DeductItemBatchService deductItemBatchService;
    @Autowired
    private SettlmentItemBatchService settlmentItemBatchService;
    @Autowired
    private ManagerSellerService managerSellerService;
    @Autowired
    private BillRefQueryService billRefQueryService;
    @Autowired
    private BillRefQueryHistoryDataService billRefQueryHistoryDataService;
    @Lazy
    @Autowired
    private BillQueryService billQueryService;



    /**
     * 业务单匹配蓝票
     */
    public List<TXfBillDeductInvoiceEntity> makeDeductInvoice(TXfBillDeductEntity deductEntity
            ,List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList){
        List<TXfBillDeductInvoiceEntity> deductInvoiceEntityList = new ArrayList<>();
        Map<String,TXfBillDeductInvoiceEntity> deductInvoiceEntityMap = new HashMap<>();
        for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailList){
            String invoiceMapKey = deductInvoiceDetail.getInvoiceCode()+deductInvoiceDetail.getInvoiceNo();
            TXfBillDeductInvoiceEntity deductInvoiceEntity = deductInvoiceEntityMap.get(invoiceMapKey);
            if (deductInvoiceEntity == null){
                deductInvoiceEntity = new TXfBillDeductInvoiceEntity();
//                deductInvoiceEntity.setId(idSequence.nextId());
                deductInvoiceEntity.setBusinessNo(deductEntity.getBusinessNo());
                deductInvoiceEntity.setBusinessType(deductEntity.getBusinessType());
                deductInvoiceEntity.setInvoiceCode(deductInvoiceDetail.getInvoiceCode());
                deductInvoiceEntity.setInvoiceNo(deductInvoiceDetail.getInvoiceNo());
                deductInvoiceEntity.setUseAmount(deductInvoiceDetail.getUseAmountWithoutTax());
                deductInvoiceEntity.setStatus(0);
//                deductInvoiceEntity.setThridId(deductInvoiceDetail.getInvoiceId());
                deductInvoiceEntity.setThridId(deductEntity.getId());
                deductInvoiceEntity.setCreateTime(new Date());
                deductInvoiceEntity.setUpdateTime(new Date());
                deductInvoiceEntityMap.put(invoiceMapKey,deductInvoiceEntity);
                deductInvoiceEntityList.add(deductInvoiceEntity);
                continue;
            }
            //累加金额
            deductInvoiceEntity.setUseAmount(deductInvoiceEntity.getUseAmount().add(deductInvoiceDetail.getUseAmountWithoutTax()));
        }
        return deductInvoiceEntityList;
    }

    /**
     * 业务单匹配蓝票
     */
    public List<TXfBillDeductInvoiceEntity> makeDeductInvoice(List<TXfBillDeductInvoiceDetailEntity> deductInvoiceDetailList){
        List<TXfBillDeductInvoiceEntity> invoiceEntityList = new ArrayList<>();
        Map<String,TXfBillDeductInvoiceEntity> invoiceEntityMap = new HashMap<>();
        for (TXfBillDeductInvoiceDetailEntity deductInvoiceDetail : deductInvoiceDetailList){
            String invoiceMapKey = deductInvoiceDetail.getInvoiceCode()+deductInvoiceDetail.getInvoiceNo();
            TXfBillDeductInvoiceEntity invoiceEntity = invoiceEntityMap.get(invoiceMapKey);
            if (invoiceEntity == null){
                invoiceEntity = new TXfBillDeductInvoiceEntity();
                invoiceEntity.setInvoiceCode(deductInvoiceDetail.getInvoiceCode());
                invoiceEntity.setInvoiceNo(deductInvoiceDetail.getInvoiceNo());
                invoiceEntity.setUseAmount(deductInvoiceDetail.getUseAmountWithoutTax());
                invoiceEntity.setThridId(deductInvoiceDetail.getInvoiceId());
                invoiceEntityMap.put(invoiceMapKey,invoiceEntity);
                invoiceEntityList.add(invoiceEntity);
                continue;
            }
            //累加金额
            invoiceEntity.setUseAmount(invoiceEntity.getUseAmount().add(deductInvoiceDetail.getUseAmountWithoutTax()));
        }
        return invoiceEntityList;
    }

    /**
     * 接收索赔明细
     * 会由不同线程调用，每次调用，数据不会重复，由上游保证
     * @param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean receiveItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo ) {
        List<TXfBillDeductItemEntity> list =  transferBillItemData(claimBillItemDataList,batchNo);
        deductItemBatchService.saveBatch(list);
        return true;
    }

    public List<TXfBillDeductItemEntity> transferBillItemData(List<ClaimBillItemData> claimBillItemDataList ,String batchNo) {
        Date date = new Date();
        if (CollectionUtils.isEmpty(claimBillItemDataList)) {
            throw new EnhanceRuntimeException("","传入的单据明细数据为空");
        }
        return claimBillItemDataList.parallelStream().map(claimBillItemData->{
            TXfBillDeductItemEntity tmp = new TXfBillDeductItemEntity();
            if (Objects.isNull(claimBillItemData)) {
                return null;
            }
            BeanUtil.copyProperties(claimBillItemData, tmp);
            tmp.setItemSpec(claimBillItemData.getItemSpec());
            tmp.setGategoryNbr(defaultValue(claimBillItemData.getCategoryNbr()));
            tmp.setVnpkQuantity(defaultValue(claimBillItemData.getVnpkQuantity()).intValue());
            tmp.setPurchaserNo(defaultValue(claimBillItemData.getStoreNbr()));
            tmp.setDeptNbr(defaultValue(claimBillItemData.getDeptNbr()));
            tmp.setCreateTime(date);
            tmp.setId(idSequence.nextId());
            tmp.setGoodsNoVer("33.0");
            tmp.setUpdateTime(tmp.getCreateTime());
            tmp.setAmountWithoutTax(defaultValue(claimBillItemData.getAmountWithoutTax()).setScale(2,RoundingMode.HALF_UP));
            tmp.setTaxAmount(defaultValue(claimBillItemData.getAmountWithoutTax()).multiply(defaultValue(claimBillItemData.getTaxRate())).setScale(2, RoundingMode.HALF_UP));
            tmp.setAmountWithTax(tmp.getAmountWithoutTax().add(tmp.getTaxAmount()));
            tmp.setRemainingAmount(defaultValue(tmp.getAmountWithTax()));//索赔取含税金额 magaofeng@xforcplus.com
            tmp.setSourceId(defaultValue(claimBillItemData.getId()));
            tmp.setBatchNo(StringUtils.EMPTY);
            tmp.setPrice(defaultValue(claimBillItemData.getPrice()));
            tmp.setUnit(defaultValue(claimBillItemData.getUnit()));
            tmp.setVnpkCost(defaultValue(claimBillItemData.getVnpkCost()));
            //WALMART-2225 数据导入数量=0时会影响后面的逻辑处理
            tmp.setQuantity(defaultValue(claimBillItemData.getQuantity()));
            log.info("tmp.setQuantity:{}",tmp.getQuantity());
            if (tmp.getQuantity().compareTo(BigDecimal.ZERO)==0) {
                log.error("传入的单据明细数量不能为空或者0,索赔单编号:{}",claimBillItemData.getClaimNo());
                throw new EnhanceRuntimeException("","传入的单据明细数量不能为空或者0,索赔单编号:"+claimBillItemData.getClaimNo());
            }
            tmp.setVerdictDate(claimBillItemData.getVerdictDate());
            log.info("verdictDate13:{}",claimBillItemData.getVerdictDate());
            tmp.setClaimNo(defaultValue(claimBillItemData.getClaimNo()));
            //log.info("claimBillItemData.getUpc():{}",claimBillItemData.getUpc());
            tmp.setUpc(defaultValue(claimBillItemData.getUpc()));
            fixTaxCode(tmp);
            return tmp;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 接收索赔 协议 EPD主信息数据
     * @param deductBillBaseDataList
     * @param deductionEnum
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean receiveData(List<DeductBillBaseData> deductBillBaseDataList, TXfDeductionBusinessTypeEnum deductionEnum) {
        List<TXfBillDeductEntity> list = transferBillData(deductBillBaseDataList, deductionEnum);
        list.forEach(tXfBillDeductEntity->{
            try {
                //日志
                saveCreateDeductLog(tXfBillDeductEntity);
            } catch (Exception e) {
                log.error(tXfBillDeductEntity.getBusinessNo()+":"+e.getMessage(),e);
                log.error("{} 数据保存失败 异常{} 单据数据：{} ", deductionEnum.getDes(), e,tXfBillDeductEntity);
            }
        });

        Map<String, List<TXfBillDeductEntity>> finalPreDeductMap = buildPreDeductListMap(list,deductionEnum);

        finalPreDeductMap.forEach((k,v)->{
//            doAutoCancel(k,v,list);
            unlockAndCancel(deductionEnum,v);
        });
         deductBatchService.saveBatch(list);
         return true;
    }


    private void doAutoCancel( String key,List<TXfBillDeductEntity> origins, List<TXfBillDeductEntity> deductEntities) {

        //https://jira.xforceplus.com/browse/WALMART-249  需求中暂时只提到了协议取消,仅判断协议是否有作废的
        if (origins.stream().anyMatch(x -> Objects.equals(x.getStatus(), TXfDeductStatusEnum.AGREEMENT_DESTROY.getCode()))) {
            //过滤出购销方以及协议号一致的协议单据
            List<TXfBillDeductEntity> entities = deductEntities.stream().filter(t ->
                    Objects.equals(t.getBusinessType(), AGREEMENT_BILL.getValue()) &&
                            Objects.equals(t.getBusinessNo().concat(t.getSellerNo()).concat(t.getPurchaserNo()), key)
            ).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(entities)) {
                entities.forEach(x -> x.setStatus(TXfDeductStatusEnum.AGREEMENT_DESTROY.getCode()));
            }
        }
    }

    /**
     * 构造以后的单据map
     * @param list
     * @param deductionEnum
     * @return
     */
    private Map<String, List<TXfBillDeductEntity>> buildPreDeductListMap(List<TXfBillDeductEntity> list, TXfDeductionBusinessTypeEnum deductionEnum) {
        if (Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL)
                || Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.EPD_BILL)) {

            String  businessNos = list.parallelStream()
                    .filter(
                            tXfBillDeductEntity->StringUtils.isNotEmpty(tXfBillDeductEntity.getBusinessNo()) && tXfBillDeductEntity.getAmountWithoutTax().compareTo(BigDecimal.ZERO) < 0
                    )
//                    //过滤掉已经被锁定的业务单(可能是由于供应商被锁定)
                    .filter(x->!Objects.equals(TXfDeductStatusEnum.LOCK.getCode(),x.getStatus()))
                    .map(tXfBillDeductEntity -> "'"+tXfBillDeductEntity.getBusinessNo()+"'")
                    .distinct()
                    .collect(Collectors.joining(","));
            if(StringUtils.isNotBlank(businessNos)) {
                List<TXfBillDeductEntity> preDeductList = tXfBillDeductExtDao.selectByBusinessNos(businessNos, deductionEnum.getValue());
                if (CollectionUtils.isNotEmpty(preDeductList)) {
                    return preDeductList.stream().collect(Collectors.groupingBy(t -> t.getBusinessNo().concat(t.getSellerNo()).concat(t.getPurchaserNo())));
                }
            }
        }
        return Collections.emptyMap();
    }

    protected void saveCreateDeductLog(TXfBillDeductEntity tXfBillDeductEntity) {
        OperateLogEnum operateLogEnum = null;
        if (Objects.equals(TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue(), tXfBillDeductEntity.getBusinessType())) {
            operateLogEnum = OperateLogEnum.CREATE_DEDUCT;
        } else if (Objects.equals(tXfBillDeductEntity.getBusinessType(), TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue())) {
            operateLogEnum = OperateLogEnum.CREATE_AGREEMENT;
        } else if (Objects.equals(tXfBillDeductEntity.getBusinessType(), TXfDeductionBusinessTypeEnum.EPD_BILL.getValue())) {
            operateLogEnum = OperateLogEnum.CREATE_EPD;
        }
        operateLogService.addDeductLog(tXfBillDeductEntity.getId(), tXfBillDeductEntity.getBusinessType(), TXfDeductStatusEnum.getEnumByCode(tXfBillDeductEntity.getStatus()), "", operateLogEnum, "", 0L, "系统");
    }

    public List<TXfBillDeductEntity> transferBillData(List<DeductBillBaseData> deductBillDataList ,  TXfDeductionBusinessTypeEnum deductionEnum) {
        if (CollectionUtils.isEmpty(deductBillDataList)) {
            log.error("{} 传入的单据数据为空 保存失败 ！！！！", deductionEnum.getDes()   );
            throw new EnhanceRuntimeException("","传入的单据数据为空");
        }
        Date date = new Date();
        Optional<DeductionHandleEnum> optionalDedcutionHandleEnum = DeductionHandleEnum.getHandleEnum( deductionEnum);
        if (!optionalDedcutionHandleEnum.isPresent()) {
            log.error("{} 无效的单据类型 保存失败 ！！！！！", deductionEnum.getDes()   );
            throw new EnhanceRuntimeException("","无效的单据类型");
        }
        DeductionHandleEnum dedcutionHandleEnum = optionalDedcutionHandleEnum.get();
        Set<String> purchaseNoSet = deductBillDataList.stream().map(deductBillBaseData -> deductBillBaseData.getPurchaserNo()).collect(Collectors.toSet());
        Set<String> sellerNoSet =  deductBillDataList.stream().map(deductBillBaseData -> deductBillBaseData.getSellerNo()).collect(Collectors.toSet());
        Map<String, TAcOrgEntity> purchaseMap = new HashMap<>();
        purchaseNoSet.parallelStream().forEach(purchaseNo->{
            TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaseNo,false);
            purchaseMap.put(purchaseNo, purchaserOrgEntity);
        });
        Map<String, TAcOrgEntity> sellerMap = new HashMap<>();
        sellerNoSet.parallelStream().forEach(sellerNo->{
            //WALMART-3444 供应商编号小于6位会导致查询不到，前面补0
            TAcOrgEntity purchaserOrgEntity = queryOrgInfo(CommonUtil.fillZero(defaultValue(sellerNo)),true);
            sellerMap.put(sellerNo, purchaserOrgEntity);
        });
        List<TXfBillDeductEntity> list = deductBillDataList.parallelStream().map(deductBillBaseData->{
            TAcOrgEntity purchaserOrgEntity = purchaseMap.get(deductBillBaseData.getPurchaserNo());
            if (Objects.nonNull( purchaserOrgEntity)) {
                deductBillBaseData.setPurchaserName(defaultValue(purchaserOrgEntity.getOrgName()));
            }
            TAcOrgEntity tAcSellerOrgEntity = sellerMap.get(deductBillBaseData.getSellerNo());
            if (Objects.nonNull(tAcSellerOrgEntity)) {
                deductBillBaseData.setSellerName(tAcSellerOrgEntity.getOrgName());
                log.info("deductBillBaseData replace BusinessNo:{}, sellername:{}", deductBillBaseData.getBusinessNo(), deductBillBaseData.getSellerName());
            }
            TXfBillDeductEntity tmp = dedcutionHandleEnum.function.apply(deductBillBaseData);
            tmp.setCreateTime(date);
            tmp.setUpdateTime(tmp.getCreateTime());
            tmp.setId(idSequence.nextId());

            //判断供应商是否锁定
            if (AGREEMENT_BILL.equals(deductionEnum) && managerSellerService.findSellerNoLocked(tmp.getSellerNo())) {
                log.info("供应商{}已锁定，生成锁定协议单", tmp.getSellerNo());
                //锁定协议单
                tmp.setLockFlag(TXfDeductStatusEnum.LOCK.getCode());
            }

            log.info("transferBillData:{},SellerName:{}", JSON.toJSONString(tmp), JSON.toJSONString(tAcSellerOrgEntity));
            return tmp;
        }).collect(Collectors.toList());
        return list;
    }

    /**
     * 自动取消和解锁 自动解锁当天新增的EPD 协议单
     * @param deductionEnum
     */
    public void unlockAndCancel(TXfDeductionBusinessTypeEnum deductionEnum, List<TXfBillDeductEntity> preDeductList) {
        if (Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.AGREEMENT_BILL)
                || Objects.equals(deductionEnum, TXfDeductionBusinessTypeEnum.EPD_BILL)) {
            // 获取协议单号
            // 查找相同协议号的数据，取第一页数据，分页大小没有特殊要求，默认设置成10
             if (CollectionUtils.isNotEmpty(preDeductList)) {
                //  如果返回个数大于1，则取第一条记录
//                TXfBillDeductEntity target = preDeductList. get(0);
                 final List<TXfBillDeductEntity> updateEntities = preDeductList.stream().filter(x ->
//                                            sameParties(tXfBillDeductEntity, x)


                                          Objects.equals(TXfDeductStatusEnum.LOCK.getCode(), x.getLockFlag())
                                         //待匹配
                                         && (
                                         Objects.equals(TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode(), x.getStatus())
                                                 ||
                                                 Objects.equals(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode(), x.getStatus())
                                 )
                         )
                         .map(x->{
                             log.info("解锁 单据id={}",  x.getId());
                             TXfBillDeductEntity update=new TXfBillDeductEntity();
                             update.setId(x.getId());
                             update.setLockFlag(TXfDeductStatusEnum.UNLOCK.getCode());
                             return update;
                         })
                         .collect(Collectors.toList());
                 if (CollectionUtils.isNotEmpty(updateEntities)){
                     deductBatchService.updateBatchById(updateEntities);
                 }
//                 if (sameParties(tXfBillDeductEntity, target)) {
//                    if (Objects.equals(TXfDeductStatusEnum.LOCK.getCode(), target.getLockFlag())){
//                        R response = new R();
//                        boolean result = updateBillStatus(deductionEnum, target, TXfDeductStatusEnum.UNLOCK,response);
//                        log.info("解锁source单据id={}的target单据的id={}, result={}", tXfBillDeductEntity.getId(), target.getId(), result);
//                    }
//                    tXfBillDeductEntity.setStatus(target.getStatus());
//                } else {
//                    log.info("source单据id={}与target单据的id={}购销对不一致，跳过解锁取消逻辑", tXfBillDeductEntity.getId(), target.getId());
//                }
            }

        }
    }

    /**
     * .判断两个单据的交易双方是否一致
     *
     * @param source
     * @param target
     * @return
     */
    protected boolean sameParties(TXfBillDeductEntity source, TXfBillDeductEntity target){
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
    public boolean updateBillStatus(TXfDeductionBusinessTypeEnum deductionEnum, TXfBillDeductEntity tXfBillDeductEntity, TXfDeductStatusEnum status, R r) {
        if(tXfBillDeductEntity.getId() == null){
            r.setMessage("Id不能为空");
            return false;
        }
        if(tXfBillDeductEntity.getStatus() == null){
            r.setMessage("结算单状态不能为空");
            return false;
        }
        if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.CLAIM_DESTROY.equals(status)){
                r.setMessage("索赔单不能撤销");
                return false;
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                r.setMessage("索赔单不能锁定或解锁");
                return false;
            }
        }else if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.AGREEMENT_DESTROY.equals(status)){
                if(!TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())){
                    r.setMessage("只有待匹配结算单的协议单才能取消");
                    return false;
                }
                deleteBillDeductItemRef(tXfBillDeductEntity.getId());
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                if(status.getCode().equals(tXfBillDeductEntity.getLockFlag())){
                    r.setMessage("该业务单已"+status.getDesc());
                    return false;
                }
                if(!TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())){
                    r.setMessage("只有待匹配结算单的协议单才能锁定或解锁");
                    return false;
                }
            }
        }else if(TXfDeductionBusinessTypeEnum.EPD_BILL.equals(deductionEnum)){
            if(TXfDeductStatusEnum.EPD_DESTROY.equals(status)){
                if(!TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())) {
                    r.setMessage("只有待匹配结算单的EPD才能取消");
                    return false;
                }
                deleteBillDeductItemRef(tXfBillDeductEntity.getId());
            }
            if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
                if(status.getCode().equals(tXfBillDeductEntity.getLockFlag())){
                    r.setMessage("该业务单已"+status.getDesc());
                    return false;
                }
                if(!TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode().equals(tXfBillDeductEntity.getStatus())) {
                    r.setMessage("只有待匹配结算单的EPD才能锁定或解锁");
                    return false;
                }
            }
        }else {
            r.setMessage("非法结算单类型");
            return false;
        }

        if(TXfDeductStatusEnum.LOCK.equals(status) || TXfDeductStatusEnum.UNLOCK.equals(status) ){
            tXfBillDeductEntity.setLockFlag(status.getCode());
        }else{
           tXfBillDeductEntity.setStatus(status.getCode());
            tXfBillDeductEntity.setUpdateTime(new Date());
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
        operateLogService.addDeductLog(id, typeEnum.getValue(), statusEnum, null, logEnum, "", UserUtil.getUserId(), UserUtil.getUserName());
    }


	/**
	 * 结算单转换操作
	 *
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
		TAcOrgEntity purchaserOrgEntity = queryOrgInfo(purchaserNo, false);
		TAcOrgEntity sellerOrgEntity = queryOrgInfo(sellerNo, true);

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
		tXfSettlementEntity.setPurchaserTel(defaultValue(purchaserOrgEntity.getPhone()));
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
		tXfSettlementEntity.setBusinessType(deductionBusinessTypeEnum.getValue());
		tXfSettlementEntity.setPriceMethod(0);

		log.info("业务单组合生成结算单:[{}]", tXfSettlementEntity.getSettlementNo());
		//重新计算金额等信息
		AtomicReference<BigDecimal> amountWithoutTax = new AtomicReference<>(BigDecimal.ZERO);
		AtomicReference<BigDecimal> amountWithTax = new AtomicReference<>(BigDecimal.ZERO);
		AtomicReference<BigDecimal> taxAmount = new AtomicReference<>(BigDecimal.ZERO);
		/**
		 * 索赔单 直接生成 结算单
		 */
		AtomicInteger tmpStatus = new AtomicInteger(TXfSettlementItemFlagEnum.NORMAL.getCode());
		AtomicReference<BigDecimal> taxRateTotal = new AtomicReference<>(BigDecimal.ZERO);
		if (deductionBusinessTypeEnum == TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
			List<TXfBillDeductItemExtDto> tXfBillDeductItemEntities = tXfBillDeductItemExtDao.queryItemsByBill(purchaserNo, sellerNo, type, status);

            log.info("业务单组合生成结算单:[{}],{}", tXfSettlementEntity.getSettlementNo(), tXfBillDeductItemEntities.size());
			List<TXfSettlementItemEntity> res = tXfBillDeductItemEntities.parallelStream().map(tXfBillDeductItemEntity -> {
						TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
						BeanUtils.copyProperties(tXfBillDeductItemEntity, tXfSettlementItemEntity);
						tXfSettlementItemEntity.setItemName(tXfBillDeductItemEntity.getCnDesc());
						if(tXfBillDeductItemEntity.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
							tXfSettlementItemEntity.setQuantity(tXfBillDeductItemEntity.getQuantity().negate());
						}else {
							tXfSettlementItemEntity.setQuantity(tXfBillDeductItemEntity.getQuantity());
						}
						tXfSettlementItemEntity.setTaxRate(tXfBillDeductItemEntity.getTaxRate());
						tXfSettlementItemEntity.setItemCode(tXfBillDeductItemEntity.getItemNo());
//						tXfSettlementItemEntity.setAmountWithoutTax(tXfBillDeductItemEntity.getAmountWithoutTax().negate());
//						tXfSettlementItemEntity.setTaxAmount(tXfSettlementItemEntity.getAmountWithoutTax().multiply(tXfBillDeductItemEntity.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
//						tXfSettlementItemEntity.setAmountWithTax(tXfSettlementItemEntity.getTaxAmount().add(tXfSettlementItemEntity.getAmountWithoutTax()));
						//重新填充税额，含税金额，计算不含税金额  @TODO 请注意negate()含税
						tXfSettlementItemEntity.setTaxAmount(tXfSettlementItemEntity.getTaxAmount().negate());
						tXfSettlementItemEntity.setAmountWithTax(tXfSettlementItemEntity.getAmountWithTax().negate());
						tXfSettlementItemEntity.setAmountWithoutTax(tXfSettlementItemEntity.getAmountWithTax().subtract(tXfSettlementItemEntity.getTaxAmount()));

						tXfSettlementItemEntity.setQuantityUnit(defaultValue(tXfBillDeductItemEntity.getUnit()));
						tXfSettlementItemEntity.setItemSpec(defaultValue(tXfBillDeductItemEntity.getItemSpec()));
						tXfSettlementItemEntity.setCreateTime(DateUtils.getNow());
						tXfSettlementItemEntity.setUpdateTime(tXfSettlementItemEntity.getCreateTime());
						tXfSettlementItemEntity.setItemStatus(0);
						//2022-07-07修复jira https://jira.xforceplus.com/browse/PRJCENTER-8212
						tXfSettlementItemEntity.setUnitPrice(tXfSettlementItemEntity.getAmountWithoutTax().divide(tXfSettlementItemEntity.getQuantity(), 6, RoundingMode.HALF_UP).abs());
						tXfSettlementItemEntity.setId(idSequence.nextId());
						tXfSettlementItemEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
						tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
						tXfSettlementItemEntity.setCreateUser(0L);
						tXfSettlementItemEntity.setUnitPriceWithTax(tXfSettlementItemEntity.getAmountWithTax().divide(tXfSettlementItemEntity.getQuantity(), 6, RoundingMode.HALF_UP).abs());
						tXfSettlementItemEntity.setUpdateUser(tXfSettlementItemEntity.getCreateUser());
						tXfSettlementItemEntity.setThridId(tXfBillDeductItemEntity.getId());

						//
						tXfSettlementItemEntity.setItemRefId(tXfBillDeductItemEntity.getItemRefId());

						tXfSettlementItemEntity = checkItem(tXfSettlementItemEntity);
						tXfSettlementItemEntity = checkItemName(tXfSettlementItemEntity);
						tmpStatus.accumulateAndGet(tXfSettlementItemEntity.getItemFlag(), (left, right) -> Math.max(left, right));
						taxRateTotal.accumulateAndGet(tXfBillDeductItemEntity.getTaxRate(), (left, right) -> left.add(right));
						return tXfSettlementItemEntity;
					}).filter(Objects::nonNull).collect(Collectors.toList());
			settlmentItemBatchService.saveBatch(res);
			//索赔的总金额从明细获取
			res.parallelStream().forEach(tmp -> {
				amountWithoutTax.accumulateAndGet(tmp.getAmountWithoutTax(), (left, right) -> left.add(right));
				amountWithTax.accumulateAndGet(tmp.getAmountWithTax(), (left, right) -> left.add(right));
				taxAmount.accumulateAndGet(tmp.getTaxAmount(), (left, right) -> left.add(right));
			});

		} else {
			tXfSettlementEntity.setTaxRate(tXfBillDeductEntities.get(0).getTaxRate());
			taxRateTotal.set(tXfSettlementEntity.getTaxRate());
			//协议，EPD从业务单中获取总金额
			tXfBillDeductEntities.parallelStream().forEach(tmp -> {
				amountWithoutTax.accumulateAndGet(tmp.getAmountWithoutTax(), (left, right) -> left.add(right));
				amountWithTax.accumulateAndGet(tmp.getAmountWithTax(), (left, right) -> left.add(right));
				taxAmount.accumulateAndGet(tmp.getTaxAmount(), (left, right) -> left.add(right));
			});
		}
		tXfSettlementEntity.setAmountWithoutTax(amountWithoutTax.get().negate());
		tXfSettlementEntity.setAmountWithTax(amountWithTax.get().negate());
		tXfSettlementEntity.setTaxAmount(taxAmount.get().negate());

		/**
		 * 部分匹配 索赔单明细 需要确认数据单据，如果不需要确认，进入拆票流程，状态是 待拆票
		 */
		if (tmpStatus.get() == TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode()) {
			tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_TAX_CODE.getCode());
		} else if (tmpStatus.get() == TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode()) {
			tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
		} else if (tmpStatus.get() == TXfSettlementItemFlagEnum.NORMAL.getCode()) {
			tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
		}
		if (taxRateTotal.get().compareTo(BigDecimal.ZERO) == 0) {
			tXfSettlementEntity.setInvoiceType(InvoiceTypeEnum.GENERAL_INVOICE.getValue());
		}


		tXfSettlementDao.insert(tXfSettlementEntity);
		// 日志
		operateLogService.add(tXfSettlementEntity.getId(), OperateLogEnum.APPLY_RED_NOTIFICATION,
				TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()).getDesc(), "", 0L, "系统");
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
    Integer defaultValue(Integer value) { return Objects.isNull(value) ? 0 : value; }
    static Long defaultValue(Long value) {  return Objects.isNull(value) ? 0L : value; }


    enum DeductionHandleEnum {
        CLAIM_BILL(TXfDeductionBusinessTypeEnum.CLAIM_BILL, x -> {
            ClaimBillData tmp = (ClaimBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.CLAIM_NO_MATCH_ITEM.getCode());
            tXfBillDeductEntity.setUpdateTime(new Date());

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
            tXfBillDeductEntity.setAgreementReference(defaultValue(tmp.getReferenceType()));
            tXfBillDeductEntity.setAgreementTaxCode(defaultValue(tmp.getTaxCode()));
            tXfBillDeductEntity.setDeductInvoice(StringUtils.EMPTY);
            //入账日期
            tXfBillDeductEntity.setVerdictDate(tmp.getPostingDate());
            log.info("verdictDate17:{}",tmp.getPostingDate());
            tXfBillDeductEntity.setDeductDate(tmp.getDeductDate());
            tXfBillDeductEntity.setBusinessNo(defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setAmountWithTax( defaultValue(x.getAmountWithTax()));
            tXfBillDeductEntity.setTaxAmount(defaultValue(x.getTaxAmount()));
            tXfBillDeductEntity.setAmountWithoutTax(tXfBillDeductEntity.getAmountWithTax().subtract(tXfBillDeductEntity.getTaxAmount()));
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.AGREEMENT_NO_MATCH_SETTLEMENT.getCode());
            tXfBillDeductEntity.setUpdateTime(new Date());
            return tXfBillDeductEntity;
        }),
        EPD_BILL(TXfDeductionBusinessTypeEnum.EPD_BILL, x -> {
            EPDBillData tmp = (EPDBillData) x;
            TXfBillDeductEntity tXfBillDeductEntity = dataTrans(x);
            tXfBillDeductEntity.setAgreementMemo(defaultValue(tmp.getMemo()));
            tXfBillDeductEntity.setVerdictDate(tmp.getPostingDate());
            log.info("verdictDate16:{}",tmp.getPostingDate());
            tXfBillDeductEntity.setDeductDate(tmp.getDeductDate());
            tXfBillDeductEntity.setAgreementReasonCode(defaultValue(tmp.getReasonCode()));
            tXfBillDeductEntity.setAgreementReference(defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setAgreementTaxCode(defaultValue(tmp.getTaxCode()));
            tXfBillDeductEntity.setAgreementDocumentType(defaultValue(tmp.getDocumentType()));
            tXfBillDeductEntity.setAmountWithTax( defaultValue(x.getAmountWithTax()));
            tXfBillDeductEntity.setAmountWithoutTax(tXfBillDeductEntity.getAmountWithTax().divide(BigDecimal.ONE.add(defaultValue(tXfBillDeductEntity.getTaxRate())), 2, RoundingMode.HALF_UP));
            tXfBillDeductEntity.setTaxAmount(tXfBillDeductEntity.getAmountWithTax().subtract(tXfBillDeductEntity.getAmountWithoutTax()));
            tXfBillDeductEntity.setBusinessNo( defaultValue(tmp.getReference()));
            tXfBillDeductEntity.setStatus(TXfDeductStatusEnum.EPD_NO_MATCH_SETTLEMENT.getCode());
            tXfBillDeductEntity.setUpdateTime(new Date());
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
        tXfBillDeductEntity.setSellerNo(CommonUtil.fillZero(defaultValue(deductBillBaseData.getSellerNo())));
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
        //创建时间、入库 >> end
        String createTimeEnd = request.getCreateTimeEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            request.setCreateTimeEnd(format);
        }
        int count = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), request.getKey(),
                request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        List<TXfBillDeductExtEntity> tXfBillDeductEntities = tXfBillDeductExtDao.queryBillPage(offset,next,request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), request.getKey()
        ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        List<QueryDeductListResponse> response = new ArrayList<>();
        BeanUtil.copyList(tXfBillDeductEntities,response,QueryDeductListResponse.class);
        //key为1和2 和3 添加红字信息编号
        if(DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue().equals(request.getKey()) || DeductBillTabEnum.APPLYED_RED_NO.getValue().equals(request.getKey()) ||
                DeductBillTabEnum.MAKEED.getValue().equals(request.getKey())){
            this.fillRedNotificationNo(response);
        }
        //填充待拆票原因
        if(StringUtils.equals(DeductBillTabEnum.NO_SPLIT_INVOICE.getValue(),request.getKey())) {
            fillSettlementRemark(response);
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
        //创建时间、入库 >> end
        String createTimeEnd = request.getCreateTimeEnd();
        if (StringUtils.isNotBlank(createTimeEnd)) {
            final String format = DateUtils.addDayToYYYYMMDD(createTimeEnd, 1);
            request.setCreateTimeEnd(format);
        }
        int key0 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.TO_BE_MATCH.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate()
        );
        JSONObject jsonObject0 = new JSONObject();
        jsonObject0.put("key",DeductBillTabEnum.TO_BE_MATCH.getValue());
        jsonObject0.put("count",key0);
        jsonObject0.put("desc",DeductBillTabEnum.TO_BE_MATCH.getDesc());
        list.add(jsonObject0);

        //调整已匹配待开票的位置 https://jira.xforceplus.com/browse/BIG-1901
        //【协议单管理】-【协议单】，”已匹配待开红字信息表“页签调整至”已申请红字信息“页签前面 https://jira.xforceplus.com/browse/PRJCENTER-8727
        int key1 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate()
        );
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("key",DeductBillTabEnum.MATCHED_TO_BE_MAKE.getValue());
        jsonObject1.put("count",key1);
        jsonObject1.put("desc",DeductBillTabEnum.MATCHED_TO_BE_MAKE.getDesc());
        list.add(jsonObject1);

        int key2 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.APPLYED_RED_NO.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("key",DeductBillTabEnum.APPLYED_RED_NO.getValue());
        jsonObject2.put("count",key2);
        jsonObject2.put("desc",DeductBillTabEnum.APPLYED_RED_NO.getDesc());
        list.add(jsonObject2);

        int key3 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.MAKEED.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("key",DeductBillTabEnum.MAKEED.getValue());
        jsonObject3.put("count",key3);
        jsonObject3.put("desc",DeductBillTabEnum.MAKEED.getDesc());
        list.add(jsonObject3);
        int key4 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.CANCELED.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        JSONObject jsonObject4 = new JSONObject();
        jsonObject4.put("key",DeductBillTabEnum.CANCELED.getValue());
        jsonObject4.put("count",key4);
        jsonObject4.put("desc",DeductBillTabEnum.CANCELED.getDesc());
        list.add(jsonObject4);

        int key9 = tXfBillDeductExtDao.countBillPage(request.getIds(),request.getBusinessNo(), request.getBusinessType(), request.getSellerNo(), request.getSellerName(),
                request.getDeductStartDate(),request.getDeductEndDate(), request.getPurchaserNo(), DeductBillTabEnum.NO_SPLIT_INVOICE.getValue()
                ,request.getCreateTimeEnd(),request.getCreateTimeBegin(),request.getRefSettlementNo(),request.getRedNotificationNo(), request.getTaxRate());
        JSONObject jsonObject9 = new JSONObject();
        jsonObject9.put("key",DeductBillTabEnum.NO_SPLIT_INVOICE.getValue());
        jsonObject9.put("count",key9);
        jsonObject9.put("desc",DeductBillTabEnum.NO_SPLIT_INVOICE.getDesc());
        list.add(jsonObject9);
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
                        deductBillItemModel.setBusinessNo(deductById.getBusinessNo());
                        deductBillItemList.add(deductBillItemModel);
                    }
                }
                // 关联红字信息
                billRefQueryService.fullBillItemRedNotification(deductBillItemList);
                // 历史数据红字编号处理
                billRefQueryHistoryDataService.fullBillItemRedNotification(deductById, deductBillItemList);

                response.setDeductBillItemList(deductBillItemList);
            }
            response.setVerdictDate(deductById.getVerdictDate());
            log.info("verdictDate12:{}",deductById.getVerdictDate());
            response.setBusinessNo(deductById.getBusinessNo());
            response.setPurchaserNo(deductById.getPurchaserNo());
            response.setSellerNo(deductById.getSellerNo());
        }
        return response;
    }

    /**
     * 业务单明细(分页)
     * @param id
     * @param pageNo
     * @param pageSize
     * @return
     */
    public DeductDetailResponse getDeductDetailPageById(Long id,Integer pageNo,Integer pageSize){
        TXfBillDeductEntity deduct = getDeductById(id);
        DeductDetailResponse response = new DeductDetailResponse();
        if(deduct != null){
            Page<TXfBillDeductItemRefEntity> page=new Page<>(pageNo,pageSize);
            QueryWrapper<TXfBillDeductItemRefEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TXfBillDeductItemRefEntity.DEDUCT_ID,id);
            wrapper.eq(TXfBillDeductItemRefEntity.STATUS,0);
            Page<TXfBillDeductItemRefEntity> itemRefPage = tXfBillDeductItemRefDao.selectPage(page,wrapper);
            List<DeductBillItemModel> deductBillItemList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(itemRefPage.getRecords())){
                for (TXfBillDeductItemRefEntity itemRefEntity : itemRefPage.getRecords()) {
                    TXfBillDeductItemEntity itemEntity =  tXfBillDeductItemDao.selectById(itemRefEntity.getDeductItemId());
                    DeductBillItemModel deductBillItemModel;
                    if(itemEntity != null){
                        deductBillItemModel = new DeductBillItemModel();
                        BeanUtil.copyProperties(itemEntity,deductBillItemModel);
                        deductBillItemModel.setBusinessNo(deduct.getBusinessNo());
                        deductBillItemList.add(deductBillItemModel);
                    }
                }
                // 关联红字信息
                billRefQueryService.fullBillItemRedNotification(deductBillItemList);
                // 历史数据红字编号处理
                billRefQueryHistoryDataService.fullBillItemRedNotification(deduct, deductBillItemList);

                response.setDeductBillItemList(deductBillItemList);
            }
            response.setVerdictDate(deduct.getVerdictDate());
            log.info("verdictDate11:{}",deduct.getVerdictDate());
            response.setBusinessNo(deduct.getBusinessNo());
            response.setPurchaserNo(deduct.getPurchaserNo());
            response.setSellerNo(deduct.getSellerNo());
            PageResult.Summary summary = new PageResult.Summary();
            summary.setTotal(itemRefPage.getTotal());
            summary.setPages(itemRefPage.getPages());
            summary.setSize(itemRefPage.getSize());
            response.setSummary(summary);
        }
        return response;
    }
//
//    public boolean export(DeductExportRequest request) {
//        final Long userId = UserUtil.getUserId();
//		TXfDeductionBusinessTypeEnum typeEnum = ValueEnum.getEnumByValue(TXfDeductionBusinessTypeEnum.class, Integer.parseInt(request.getBusinessType())).get();
//        DeductBillExportDto dto = new DeductBillExportDto();
//        dto.setType(typeEnum);
//        dto.setRequest(request);
//        dto.setUserId(userId);
//        dto.setLoginName(UserUtil.getLoginName());
//        TDxExcelExportlogEntity excelExportlogEntity = new TDxExcelExportlogEntity();
//        excelExportlogEntity.setCreateDate(new Date());
//        //这里的userAccount是userid
//        excelExportlogEntity.setUserAccount(dto.getUserId().toString());
//        excelExportlogEntity.setUserName(dto.getLoginName());
//        excelExportlogEntity.setConditions(JSON.toJSONString(request));
//        excelExportlogEntity.setStartDate(new Date());
//        excelExportlogEntity.setExportStatus(ExcelExportLogService.REQUEST);
//        excelExportlogEntity.setServiceType(SERVICE_TYPE);
//        boolean count = this.excelExportLogService.save(excelExportlogEntity);
//        dto.setLogId(excelExportlogEntity.getId());
//        ExportDeductCallable callable = new ExportDeductCallable(this,dto);
//        ThreadPoolManager.submitCustomL1(callable);
//        return count;
//    }
//
//    public boolean doExport(DeductBillExportDto exportDto){
//        boolean flag = true;
//        DeductExportRequest request = exportDto.getRequest();
//        TXfDeductionBusinessTypeEnum typeEnum = exportDto.getType();
//        //这里的userAccount是userid
//        final TDxExcelExportlogEntity excelExportlogEntity = excelExportLogService.getById(exportDto.getLogId());
//        excelExportlogEntity.setEndDate(new Date());
//        excelExportlogEntity.setExportStatus(ExcelExportLogService.OK);
//        TDxMessagecontrolEntity messagecontrolEntity = new TDxMessagecontrolEntity();
//        //这里的userAccount是userName
//        messagecontrolEntity.setUserAccount(exportDto.getLoginName());
//        messagecontrolEntity.setContent(getSuccContent());
//        //主信息
//        DeductExportRequest deductExportRequest = com.xforceplus.wapp.util.BeanUtils.copyProperties(request, DeductExportRequest.class);
//        List<QueryDeductListResponse> queryDeductListResponse =billQueryService.queryPageList(deductExportRequest).getRows();
//        if(CollectionUtils.isEmpty(queryDeductListResponse)){
//            log.info("业务单导出--未查到数据");
//            return false;
//        }
//        final String excelFileName = ExcelExportUtil.getExcelFileName(exportDto.getUserId(), exportDto.getType().getDes());
//        ExcelWriter excelWriter;
//        ByteArrayInputStream in = null;
//        String ftpPath = ftpUtilService.pathprefix + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
//        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
//            excelWriter = EasyExcel.write(out).excelType(ExcelTypeEnum.XLSX).build();
//            //创建一个sheet
//            WriteSheet writeSheet = EasyExcel.writerSheet(0, "主信息").build();
//            WriteSheet itemWriteSheet = null;
//            List exportList = new LinkedList<>();
//
//            //只有索赔单有明细信息
//            if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)){
//                //创建一个新的sheet
//                itemWriteSheet = EasyExcel.writerSheet(1, "明细信息").build();
//            }
//            // 前端包含全部全选导出（之前未实现该功能，目前使用循环查询导出,最多导出100w）
//            for (int i = 1; i <=1000 ; i++) {
//                DeductExportRequest exportRequest = com.xforceplus.wapp.util.BeanUtils.copyProperties(request, DeductExportRequest.class);
//                exportRequest.setPageNo(i);
//                exportRequest.setPageSize(1000);
//                List<QueryDeductListResponse> queryList =billQueryService.queryPageList(exportRequest).getRows();
//
//                if (CollectionUtils.isEmpty(queryList)) {
//                    break;
//                }
//
//                if(TXfDeductionBusinessTypeEnum.CLAIM_BILL.equals(typeEnum)){
//                    exportList = com.xforceplus.wapp.util.BeanUtils.copyList(queryList, ExportClaimBillModel.class);
//                    writeSheet.setClazz(ExportClaimBillModel.class);
//                    excelWriter.write(exportList, writeSheet);
//                    // 明细处理
//                    List<ExportClaimBillItemModel> exportItem = getExportItem(queryList.stream().map(QueryDeductListResponse::getId).collect(Collectors.toList()));
//                    itemWriteSheet.setClazz(ExportClaimBillItemModel.class);
//                    excelWriter.write(exportItem, itemWriteSheet);
//                }else if(TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.equals(typeEnum)){
//                    exportList =  com.xforceplus.wapp.util.BeanUtils.copyList(queryList, ExportAgreementBillModel.class);
//                    writeSheet.setClazz(ExportAgreementBillModel.class);
//                    excelWriter.write(exportList, writeSheet);
//                }else{
//                    exportList =  com.xforceplus.wapp.util.BeanUtils.copyList(queryList, ExportEPDBillModel.class);
//                    writeSheet.setClazz(ExportEPDBillModel.class);
//                    excelWriter.write(exportList, writeSheet);
//                }
//
//            }
//
//
//            excelWriter.finish();
//            //推送sftp
//            String ftpFilePath = ftpPath + "/" + excelFileName;
//            in = new ByteArrayInputStream(out.toByteArray());
//            ftpUtilService.uploadFile(ftpPath, excelFileName, in);
//            messagecontrolEntity.setUrl(exportCommonService.getUrl(excelExportlogEntity.getId()));
//            excelExportlogEntity.setFilepath(ftpFilePath);
//            messagecontrolEntity.setTitle(exportDto.getType().getDes() + "导出成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(exportDto.getType().getDes()+"导出失败:" + e.getMessage(), e);
//            excelExportlogEntity.setExportStatus(ExcelExportLogService.FAIL);
//            excelExportlogEntity.setErrmsg(e.getMessage());
//            messagecontrolEntity.setTitle( exportDto.getType().getDes() + "导出失败");
//            messagecontrolEntity.setContent(exportCommonService.getFailContent(e.getMessage()));
//            flag = false;
//        } finally {
//            if(in != null){
//                try {
//                    in.close();
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                }
//            }
//            excelExportLogService.updateById(excelExportlogEntity);
//            commonMessageService.sendMessage(messagecontrolEntity);
//        }
//        return flag;
//    }

    /**
     * 根据业务单号查询发票，当是红票时传结算单号，当是蓝票时并且是索赔单时传业务单号
     * @param businessNo
     * @param invoiceColor
     * @return List
     */
    public List<InvoiceDetailResponse>  queryDeductInvoiceList(String businessNo,String invoiceColor){
        List<InvoiceDetailResponse> response = new ArrayList<>();
        if("0".equals(invoiceColor)){
            response = recordInvoiceService.queryInvoicesBySettlementNo(businessNo,InvoiceStatusEnum.INVOICE_STATUS_NORMAL.getCode(),invoiceColor,null);
        }else{
            QueryWrapper<TXfBillDeductInvoiceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq(TXfBillDeductInvoiceEntity.BUSINESS_NO,businessNo);
            wrapper.eq(TXfBillDeductInvoiceEntity.STATUS, 0);
            List<TXfBillDeductInvoiceEntity> tXfBillDeductInvoiceEntities = tXfBillDeductInvoiceDao.selectList(wrapper);
            Set<String> uuidSet = new HashSet<>();
            for (TXfBillDeductInvoiceEntity invoiceDetail : tXfBillDeductInvoiceEntities) {
                String uuid = invoiceDetail.getInvoiceCode() + invoiceDetail.getInvoiceNo();
                if (uuidSet.contains(uuid)){
                    continue;
                }
                uuidSet.add(uuid);
                InvoiceDetailResponse invoiceDetailResponse = recordInvoiceService.queryInvoiceByUuid(invoiceDetail.getInvoiceCode() + invoiceDetail.getInvoiceNo());
                if(invoiceDetailResponse != null){
                    response.add(invoiceDetailResponse);
                }
            }
        }
        return response;
    }

    /**
     * 根据业务单ID查询发票
     * @param deductId
     * @return List
     */
    public List<InvoiceDetailResponse>  queryDeductInvoiceList(Long deductId){
        List<InvoiceDetailResponse> response = new ArrayList<>();
        QueryWrapper<TXfBillDeductInvoiceDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(TXfBillDeductInvoiceDetailEntity.DEDUCT_ID,deductId);
        wrapper.eq(TXfBillDeductInvoiceDetailEntity.STATUS,0);
        List<TXfBillDeductInvoiceDetailEntity> invoiceDetailList = tXfBillDeductInvoiceDetailDao.selectList(wrapper);
        InvoiceDetailResponse invoiceDetailResponse;
        Set<String> uuidSet = new HashSet<>();
        for (TXfBillDeductInvoiceDetailEntity invoiceDetail : invoiceDetailList) {
            String uuid = invoiceDetail.getInvoiceCode() + invoiceDetail.getInvoiceNo();
            if (uuidSet.contains(uuid)){
                continue;
            }
            uuidSet.add(uuid);
            invoiceDetailResponse = recordInvoiceService.queryInvoiceByUuid(uuid);
            if(invoiceDetailResponse != null){
                response.add(invoiceDetailResponse);
            }
        }

        // 历史数据查询兼容
        if(CollectionUtils.isEmpty(response)) {
            return queryHistoryDeductInvoiceList(deductId);
        }

        return response;
    }

    /**
     * 历史数据参考蓝票兼容
     * @param deductId
     * @return
     */
    private List<InvoiceDetailResponse> queryHistoryDeductInvoiceList(Long deductId) {
        TXfBillDeductEntity deduct = getDeductById(deductId);
        Asserts.isNull(deduct, "业务单不存在");

        if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(deduct.getBusinessType())) {
            // 执行老的索赔逻辑，通过业务单编号查询蓝票
            return queryDeductInvoiceList(deduct.getBusinessNo(), "1");
        }
        if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(deduct.getBusinessType())) {
            // 改过后的逻辑，t_xf_bill_deduct_invoice 表 business_no 由存储结算单编号变成了业务单编号
            List<InvoiceDetailResponse> invoiceDetailResponses = queryDeductInvoiceList(deduct.getBusinessNo(), "1");
            if (CollectionUtils.isNotEmpty(invoiceDetailResponses)) {
                return invoiceDetailResponses;
            }
            if (StringUtils.isEmpty(deduct.getRefSettlementNo())) {
                return Lists.newArrayList();
            }
            // 执行老的协议逻辑，通过结算单号查询，最老的逻辑是 t_xf_bill_deduct_invoice 表 business_no 存储的是结算单号
            return queryDeductInvoiceList(deduct.getRefSettlementNo(), "1");
        }

        return Lists.newArrayList();
    }

    /**
     * .非索赔数据匹配蓝票 转换
     * @param res
     * @param settlementNo
     * @param xfDeductionBusinessTypeEnum
     * @return
     */
   /* public  Integer  matchInfoTransfer(List<BlueInvoiceService.MatchRes> res, String settlementNo, Long id, TXfDeductionBusinessTypeEnum xfDeductionBusinessTypeEnum) {
		log.info("matchInfoTransfer settlementNo:{}, res:{}", settlementNo, JSON.toJSONString(res));
        Date date = new Date();
        Integer status = TXfSettlementItemFlagEnum.NORMAL.getCode();
        Integer relationType = xfDeductionBusinessTypeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL?TXfInvoiceDeductTypeEnum.SETTLEMENT.getCode():TXfInvoiceDeductTypeEnum.CLAIM.getCode();
        if (CollectionUtils.isEmpty(res)) {
            throw new NoSuchInvoiceException("匹配的发票明细为空");
        }else{
            BlueInvoiceService.MatchRes ma = res.get(0);
            if(Objects.isNull(ma)  ){
                 throw new NoSuchInvoiceException("匹配的发票明细为空");
            }
        }
        for (BlueInvoiceService.MatchRes matchRes : res) {
            if (xfDeductionBusinessTypeEnum != TXfDeductionBusinessTypeEnum.CLAIM_BILL) {
                if(CollectionUtils.isEmpty( matchRes.getInvoiceItems())){
                    log.error("蓝票匹配明细为空，发票号码 {} 发票代码{},",matchRes.invoiceNo,matchRes.invoiceCode);
                    throw new NoSuchInvoiceException("匹配的发票明细为空");
                }
                Map<String, TaxCodeBean> map = queryTaxCode(matchRes.getInvoiceItems());
                for(BlueInvoiceService.InvoiceItem invoiceItem:matchRes.getInvoiceItems()){
                    TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
                    tXfSettlementItemEntity.setIsOil(Optional.ofNullable(matchRes.getIsOil()).orElse(0));
                    tXfSettlementItemEntity.setTaxAmount(defaultValue(invoiceItem.getTaxAmount()).negate());
                    tXfSettlementItemEntity.setGoodsTaxNo(invoiceItem.getGoodsNum());
                    tXfSettlementItemEntity.setTaxRate(TaxRateTransferEnum.transferTaxRate(invoiceItem.getTaxRate()));
                    tXfSettlementItemEntity.setAmountWithoutTax(defaultValue(invoiceItem.getDetailAmount()).negate());
                    tXfSettlementItemEntity.setRemark(StringUtils.EMPTY);
                    tXfSettlementItemEntity.setQuantity(defaultValue(invoiceItem.getNum()).negate());
                    tXfSettlementItemEntity.setUnitPrice(defaultValue(invoiceItem.getUnitPrice()));
                    tXfSettlementItemEntity.setAmountWithTax(tXfSettlementItemEntity.getAmountWithoutTax().add(tXfSettlementItemEntity.getTaxAmount()));
                    tXfSettlementItemEntity.setCreateUser(0l);
                    tXfSettlementItemEntity.setUpdateUser(0l);
                    tXfSettlementItemEntity.setId(idSequence.nextId());
                    tXfSettlementItemEntity.setSettlementNo(settlementNo);
                    tXfSettlementItemEntity.setCreateTime(date);
                    tXfSettlementItemEntity.setUpdateTime(date);
                    tXfSettlementItemEntity.setItemCode(defaultValue(invoiceItem.getGoodsNum()));
                    tXfSettlementItemEntity.setThridId(defaultValue(invoiceItem.getItemId()));
                    tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
                    tXfSettlementItemEntity.setItemName(defaultValue(invoiceItem.getGoodsName()));
                    tXfSettlementItemEntity.setGoodsNoVer("33.0");
                    tXfSettlementItemEntity.setZeroTax(StringUtils.EMPTY);
                    tXfSettlementItemEntity.setTaxPre(StringUtils.EMPTY);
                    tXfSettlementItemEntity.setTaxPreCon(StringUtils.EMPTY);
                    tXfSettlementItemEntity.setItemSpec(defaultValue(invoiceItem.getModel()));
                    tXfSettlementItemEntity.setQuantityUnit(defaultValue(invoiceItem.getUnit()));
                    tXfSettlementItemEntity = fixTaxCode(tXfSettlementItemEntity, map);
                    tXfSettlementItemEntity = checkItem(  tXfSettlementItemEntity);

                    if (status < tXfSettlementItemEntity.getItemFlag() ) {
                        status = tXfSettlementItemEntity.getItemFlag();
                    }
                    tXfSettlementItemEntity.setUnitPriceWithTax(defaultValue(invoiceItem.getUnitPrice()).multiply(BigDecimal.ONE.add(tXfSettlementItemEntity.getTaxRate())).setScale(15,RoundingMode.HALF_UP));
                    log.info("保存结算单明细：{}",tXfSettlementItemEntity);
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
            log.info("保存业务单匹配蓝票：{}",tXfBillDeductInvoiceEntity);
            tXfBillDeductInvoiceDao.insert(tXfBillDeductInvoiceEntity);
        }
        return status;
    }*/

    /**
     * .根据简称查询税编信息
     * @param invoiceItems
     * @return
     */
    public Map<String, TaxCodeBean> queryTaxCode(List<BlueInvoiceService.InvoiceItem> invoiceItems) {
        Map<String, BlueInvoiceService.InvoiceItem> map = invoiceItems.stream().collect(Collectors.toMap(BlueInvoiceService.InvoiceItem::getGoodsName, invoiceItem -> invoiceItem,(o,n)->n));
        Map<String, TaxCodeBean> res =   Maps.newHashMap();
        for (String key : map.keySet()) {
            List<String> splitInfo = ItemNameUtils.splitItemName(key);
            if (splitInfo.size() != 2) {
                continue;
            }
            //2022-07-15修改，客户邮件确认：根据税编大类名称匹配，税率取原蓝票上的明细税率。
            val either = taxCodeService.searchTaxCode(null, null, splitInfo.get(0));
            if (either.isRight()) {
            	List<TaxCodeBean> taxCodeBeans = either.get();
            	log.info("queryTaxCode:{}", JSON.toJSONString(taxCodeBeans));
                if (CollectionUtils.isNotEmpty(taxCodeBeans)) {
                    TaxCodeBean taxCodeBean = taxCodeBeans.get(0);
                    res.put(splitInfo.get(0), taxCodeBean);
                }
            }

        }
        return res;
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
        if (StringUtils.isEmpty(tXfSettlementItemEntity.getGoodsTaxNo()) && BigDecimal.ZERO.compareTo(tXfSettlementItemEntity.getTaxRate()) != 0) {
            tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode());
        }
        return tXfSettlementItemEntity;
    }

    /**
     * 结算单明细校验
     * @param tXfSettlementItemEntity
     * @return
     */
	public TXfSettlementItemEntity checkItemName(TXfSettlementItemEntity tXfSettlementItemEntity) {
		StringBuffer stringBuffer = new StringBuffer("*");
		if (StringUtils.isEmpty(tXfSettlementItemEntity.getItemShortName())) {
			return tXfSettlementItemEntity;
		}
		if (tXfSettlementItemEntity.getItemName().contains("*")) {
			return tXfSettlementItemEntity;
		}
		stringBuffer.append(tXfSettlementItemEntity.getItemShortName()).append("*").append(tXfSettlementItemEntity.getItemName());
		tXfSettlementItemEntity.setItemName(stringBuffer.toString());
		return tXfSettlementItemEntity;
	}

    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    public List<QueryDeductListResponse> getExportMainData(DeductExportRequest request){
//        PageResult<QueryDeductListResponse> pageResult = queryPageList(request);
        PageResult<QueryDeductListResponse> pageResult = billQueryService.queryPageList(request);
        return pageResult.getRows() ;
    }

	/**
	 * 补充商品
	 *
	 * @param entity
	 * @return
	 */
	public String fixTaxCode(TXfBillDeductItemEntity entity) {
		if (StringUtils.isEmpty(entity.getItemNo())) {
			log.info("{},{}-->itemNo为空", entity.getClaimNo(), entity.getItemShortName());
			return "itemNo为空";
		}
		try {
			//Sams Item9位便后且以62/63/64开头，以9结尾的SAMs Item号，需去掉开头两位“6X”和末尾的9。剩余的父如果以0开头，标号从第一个非零数字开始保留截取
			if ((entity.getItemNo().startsWith("62") || entity.getItemNo().startsWith("63") || entity.getItemNo().startsWith("64")) && entity.getItemNo().endsWith("9")) {
				entity.setItemNo(entity.getItemNo().substring(2, entity.getItemNo().length() - 1).replaceAll("^(0+)", ""));
			}
			Optional<TaxCodeDto> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemNo());
			if (taxCodeOptional.isPresent()) {
				TaxCodeDto taxCode = taxCodeOptional.get();
				entity.setGoodsTaxNo(taxCode.getGoodsTaxNo());
				entity.setTaxPre(taxCode.getTaxPre());
				entity.setTaxPreCon(taxCode.getTaxPreCon());
				entity.setZeroTax(taxCode.getZeroTax());
				entity.setItemShortName(taxCode.getItemShortName());
			} else {
				log.info("{}未匹配到税编", entity.getItemNo());
                return "未匹配到税编[" + entity.getItemNo() + "]";
			}
		} catch (Exception e) {
			log.error("查询税编异常：{}  异常 {} ）", entity.getItemNo(), e);
			entity.setGoodsTaxNo(StringUtils.EMPTY);
            return "查询税编["+entity.getItemNo()+"]异常";
		}
		return "";
	}
    /**
     * .补充商品
     * @param entity
     * @return
     */
    public TXfSettlementItemEntity fixTaxCode(TXfSettlementItemEntity entity) {
        if (StringUtils.isEmpty(entity.getItemCode())) {
            return entity;
        }
        try {
            if (StringUtils.isNotEmpty(entity.getGoodsTaxNo())) {
                return entity;
            }
            Optional<TaxCodeDto> taxCodeOptional = taxCodeService.getTaxCodeByItemNo(entity.getItemCode());
            if (taxCodeOptional.isPresent()) {
                TaxCodeDto taxCode = taxCodeOptional.get();
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
     * .补充商品
     * @param entity
     * @return
     */
    public TXfSettlementItemEntity fixTaxCode(TXfSettlementItemEntity entity, Map<String, TaxCodeBean> map) {
        String itemName = entity.getItemName();
        List<String> splitInfo = ItemNameUtils.splitItemName(itemName);
        if (splitInfo.size() == 2) {
            String itemShortName = splitInfo.get(0);
            //WALMART-2292
            if(StringUtils.isNotBlank(itemShortName)){
                itemShortName = itemShortName.trim();
            }
            entity.setItemShortName(itemShortName);
            if (map.containsKey(itemShortName)) {
                TaxCodeBean taxCodeBean = map.get(itemShortName);
                entity.setGoodsTaxNo(taxCodeBean.getTaxCode());
                entity.setGoodsNoVer(taxCodeBean.getTaxCodeVersion());
                entity.setTaxPreCon(defaultValue(taxCodeBean.getSpecialManagement()));
             }
        }
        return entity;
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

    public void fillRedNotificationNo(List<QueryDeductListResponse> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> settlementNoList = list.stream().map(QueryDeductListResponse::getRefSettlementNo).distinct().collect(Collectors.toList());
        List<TXfPreInvoiceEntity> resultList = new ArrayList<>();
        ListUtils.partition(settlementNoList, 2000).forEach(itemList -> {
            QueryWrapper<TXfPreInvoiceEntity> wrapper = new QueryWrapper<>();
            wrapper.in(TXfPreInvoiceEntity.SETTLEMENT_NO, itemList);
            wrapper.in(TXfPreInvoiceEntity.PRE_INVOICE_STATUS, Stream.of(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode()
                    ,TXfPreInvoiceStatusEnum.UPLOAD_RED_INVOICE.getCode()).collect(Collectors.toList()));

            List<TXfPreInvoiceEntity> tXfPreInvoiceEntities = tXfPreInvoiceDao.selectList(wrapper);
            resultList.addAll(tXfPreInvoiceEntities);
        });

        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        Map<String, Set<String>> deductSettlementNo2RedNotificationNoMap =
                resultList.stream().collect(Collectors.groupingBy(TXfPreInvoiceEntity::getSettlementNo,
                        Collectors.mapping(TXfPreInvoiceEntity::getRedNotificationNo, Collectors.toSet())));
        for (QueryDeductListResponse entity : list) {
            Set<String> set = deductSettlementNo2RedNotificationNoMap.get(entity.getRefSettlementNo());
            if (CollectionUtils.isEmpty(set)) {
                continue;
            }
            entity.setRedNotificationNo(Joiner.on(",").join(set));
        }
    }

    public void fillSettlementRemark(List<QueryDeductListResponse> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> settlementNoList = list.stream().map(QueryDeductListResponse::getRefSettlementNo).distinct().collect(Collectors.toList());
        List<TXfSettlementEntity> resultList = new ArrayList<>();
        ListUtils.partition(settlementNoList, 2000).forEach(itemList -> {
            QueryWrapper<TXfSettlementEntity> wrapper = new QueryWrapper<>();
            wrapper.in(TXfSettlementEntity.SETTLEMENT_NO, itemList);
            List<TXfSettlementEntity> settlementEntityList = tXfSettlementDao.selectList(wrapper);
            resultList.addAll(settlementEntityList);
        });

        if (CollectionUtils.isEmpty(resultList)) {
            return;
        }
        Map<String, String> deductSettlementNo2RemarkMap = new HashMap<>();
        resultList.forEach(settlement -> {
            deductSettlementNo2RemarkMap.put(settlement.getSettlementNo(), settlement.getRemark());
        });
        for (QueryDeductListResponse entity : list) {
            entity.setSettlementRemark(deductSettlementNo2RemarkMap.get(entity.getRefSettlementNo()));
        }
    }

}
