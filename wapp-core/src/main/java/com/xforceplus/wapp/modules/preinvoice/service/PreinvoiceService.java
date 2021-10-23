package com.xforceplus.wapp.modules.preinvoice.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xforceplus.apollo.client.http.HttpClientFactory;
import com.xforceplus.phoenix.split.model.*;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.dto.SplitRuleInfoDTO;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemExtDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommRedNotificationService;
import com.xforceplus.wapp.service.CommSettlementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类描述：
 *
 * @ClassName PreinvoiceService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 13:58
 */
@Service
@Slf4j
public class PreinvoiceService extends ServiceImpl<TXfPreInvoiceDao, TXfPreInvoiceEntity> {

    @Autowired
    private CommSettlementService commSettlementService;

    static final String RULE_INFO = " {\n" +
            "\t\t\"amountSplitRule\": \"2\",\n" +
            "\t\t\"cargoNameLength\": 92,\n" +
            "\t\t\"customRemarkSize\": 200,\n" +
            "\t\t\"ignoreAllowableError\": false,\n" +
            "\t\t\"invoiceItemMaxRow\": 7,\n" +
            "\t\t\"invoiceLimit\": 999999.99,\n" +
            "\t\t\"itemSort\": \"1\",\n" +
            "\t\t\"itemSpecNameLength\": 36,\n" +
            "\t\t\"limitIsAmountWithTax\": false,\n" +
            "\t\t\"mergeBySplitFiled\": false,\n" +
            "\t\t\"priceMethod\": \"WITHOUT_TAX\",\n" +
            "\t\t\"remarkDuplicateFlag\": true,\n" +
            "\t\t\"remarkFiledMetadataBeanList\": [{\n" +
            "\t\t\t\"fieldDisplayName\": \"备注\",\n" +
            "\t\t\t\"fieldGroupIndex\": \"BILL_INFO\",\n" +
            "\t\t\t\"fieldName\": \"remark\"\n" +
            "\t\t}],\n" +
            "\t\t\"ruleId\": 687162585176363000,\n" +
            "\t\t\"saleListOption\": \"1\",\n" +
            "\t\t\"salesListMaxRow\": 2000,\n" +
            "\t\t\"showSpecification\": true,\n" +
            "\t\t\"splitByItemPriceQuantityNon\": false,\n" +
            "\t\t\"splitFiledList\": [\"taxRate\"],\n" +
            "\t\t\"unitPriceAmountOps\": \"0\",\n" +
            "\t\t\"unitPriceScale\": 4,\n" +
            "\t\t\"zeroTaxOption\": \"NOT_PROCESS\"\n" +
            "\t}";
     @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TXfSettlementExtDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemExtDao tXfSettlementItemDao;
    @Autowired
    CompanyService companyService;
    @Autowired
    private DeductService deductService;
    @Autowired
    private HttpClientFactory httpClientFactory;
    // 配置访问域名
    /**
     * 集成平台地址
     */
    @Value("${wapp.integration.host.http}")
    private String janusPath;
    @Value("${wapp.integration.tenant-id:1203939049971830784}")
    public String tenantId;
    @Value("${wapp.integration.action.splitInvoice}")
    private String splitInvoice;
    @Value("${wapp.integration.sign.splitInvoice}")
    private String sign;
    @Value("${wapp.integration.authentication-split}")
    private String authentication;

    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private IDSequence idSequence;
//    @PostConstruct
//    public void initData()   {
//       splitPreInvoice("settlementNo1853061001646081","172164");
//    }
//

    /**
     * 结算单执行拆票
     * @param settlementNo
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> splitPreInvoice(String settlementNo, String sellerNo)  {
        //查询待拆票算单主信息，
        //查询结算单明细信息
        //查询开票信息
        //组装拆票请求
        //调用拆票请求
        //保存拆票结果
        //调用红字请求
        TXfSettlementEntity tXfSettlementEntity =  tXfSettlementDao.querySettlementByNo(0l, settlementNo,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode() );
        if (Objects.isNull(tXfSettlementEntity)) {
            return Collections.EMPTY_LIST;
        }
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        List<BillItem> billItems = new ArrayList<>();
        BeanUtil.copyList( tXfSettlementItemEntities,billItems,BillItem.class);
        CreatePreInvoiceParam createPreInvoiceParam  =  assembleParam(tXfSettlementEntity, tXfSettlementItemEntities, tAcOrgEntity);

        return doSplit(createPreInvoiceParam, tXfSettlementEntity);
    }

    @Transactional
    public void reFixTaxCode(String settlementNo) {
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        List<TXfSettlementItemEntity> fixTaxList = tXfSettlementItemEntities.stream().filter(x -> StringUtils.isEmpty(x.getGoodsTaxNo())).collect(Collectors.toList());
        List<TXfSettlementItemEntity> fixAmountList = tXfSettlementItemEntities.stream().filter(x -> x.getUnitPrice().multiply(x.getQuantity()).setScale(2, RoundingMode.HALF_UP).compareTo(x.getAmountWithoutTax()) != 0)  .collect(Collectors.toList());
        boolean success = true;
        if (CollectionUtils.isNotEmpty(fixTaxList)) {
            for(TXfSettlementItemEntity tXfSettlementItemEntity:fixTaxList){
                deductService.fixTaxCode(tXfSettlementItemEntity);
                if (StringUtils.isEmpty(tXfSettlementItemEntity.getGoodsTaxNo())) {
                    success = false;
                    continue;
                }else{
                    tXfSettlementItemDao.updateById(tXfSettlementItemEntity);
                }
            }
        }

        if (success) {
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0L, settlementNo, null);
            if (CollectionUtils.isEmpty(fixAmountList)) {
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
            }else{
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
            }
            tXfSettlementDao.updateById(tXfSettlementEntity);
        }
    }


    @Transactional
    public void reCalculation(String settlementNo) {
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        List<TXfSettlementItemEntity> fixAmountList = tXfSettlementItemEntities.stream().filter(x -> x.getUnitPrice().multiply(x.getQuantity()).setScale(2, RoundingMode.HALF_UP).compareTo(x.getAmountWithoutTax()) != 0)  .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fixAmountList)) {
            for(TXfSettlementItemEntity tXfSettlementItemEntity:fixAmountList){
                BigDecimal quantity = tXfSettlementItemEntity.getAmountWithoutTax().divide(tXfSettlementItemEntity.getUnitPrice()).setScale(6, RoundingMode.HALF_UP);
                tXfSettlementItemEntity.setQuantity(quantity);
            }
        }
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0L, settlementNo, null);
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
        tXfSettlementDao.updateById(tXfSettlementEntity);
    }
    /**
     *
     * @param createPreInvoiceParam
     * @param tXfSettlementEntity
     * @return
     */
    public List<SplitPreInvoiceInfo> doSplit (CreatePreInvoiceParam createPreInvoiceParam,TXfSettlementEntity tXfSettlementEntity)   {
        Map<String, String> defaultHeader = new HashMap<>();
        defaultHeader.put("tenantId", tenantId);
        defaultHeader.put("Authentication", authentication);
        defaultHeader.put("accept", "application/json");
        defaultHeader.put("Content-Type", "application/json");
        defaultHeader.put("x-userinfo", "tenant-gateway.41-tcenter-prod");
        defaultHeader.put("uiaSign", sign);
        defaultHeader.put("action", splitInvoice);
        defaultHeader.put("serialNo", tXfSettlementEntity.getSettlementNo());
        defaultHeader.put("rpcType", "http");
        defaultHeader.put("appId ", "walmart");
        String post = "";
        try {
            post = httpClientFactory.post(splitInvoice,defaultHeader, JSON.toJSONString(createPreInvoiceParam),"");
            JSONObject res = JSONObject.parseObject(post);
            if (!res.get("code").equals("BSCTZZ0001") || res.get("result").equals("[]")) {
                log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                throw new RuntimeException("拆票失败+" + res.get("message"));
            }
        } catch (IOException e) {
            log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
            e.printStackTrace();
            throw new RuntimeException("拆票失败");
        }
        // check 拆票失败
        Date date = new Date();
        List<SplitPreInvoiceInfo> splitPreInvoiceInfos = JSON.parseArray(post, SplitPreInvoiceInfo.class);
        for (SplitPreInvoiceInfo splitPreInvoiceInfo : splitPreInvoiceInfos) {
            TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = new ArrayList<>();
            BeanUtil.copyProperties(splitPreInvoiceInfo.getPreInvoiceMain(), tXfPreInvoiceEntity);
            tXfPreInvoiceEntity.setSettlementType(tXfSettlementEntity.getSettlementType());
            tXfPreInvoiceEntity.setSettlementId(tXfSettlementEntity.getId() );
            tXfPreInvoiceEntity.setInvoiceCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setInvoiceNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setCheckCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setPaperDrawDate(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setCreateTime(date);
            tXfPreInvoiceEntity.setUpdateTime(date);
            tXfPreInvoiceEntity.setCreateUserId(0L);
            tXfPreInvoiceEntity.setUpdateUserId(0L);
            tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
            tXfPreInvoiceEntity.setMachineCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceType(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginPaperDrawDate(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setRedNotificationNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setId(idSequence.nextId());
            tXfPreInvoiceDao.insert(tXfPreInvoiceEntity);
            for (PreInvoiceItem preInvoiceItem : splitPreInvoiceInfo.getPreInvoiceItems()) {
                TXfPreInvoiceItemEntity   tXfPreInvoiceItemEntity = new TXfPreInvoiceItemEntity();
                BeanUtil.copyProperties(preInvoiceItem, tXfPreInvoiceItemEntity);

                tXfPreInvoiceItemEntity.setId(idSequence.nextId());
                tXfPreInvoiceItemEntity.setPreInvoiceId(tXfPreInvoiceEntity.getId());
                tXfPreInvoiceItemDao.insert(tXfPreInvoiceItemEntity);
                tXfPreInvoiceItemEntities.add(tXfPreInvoiceItemEntity);
            }
            try {
                PreInvoiceDTO applyProInvoiceRedNotificationDTO = new PreInvoiceDTO();
                applyProInvoiceRedNotificationDTO.setTXfPreInvoiceEntity(tXfPreInvoiceEntity);
                applyProInvoiceRedNotificationDTO.setTXfPreInvoiceItemEntityList(tXfPreInvoiceItemEntities);
                commRedNotificationService.applyAddRedNotification(applyProInvoiceRedNotificationDTO);
            } catch (Exception e) {
                log.error("发起红字信息申请 失败{} 预制发票id：{}",e,tXfPreInvoiceEntity.getId());
            }
        }
        TXfSettlementEntity tmp = new TXfSettlementEntity();
        tmp.setId(tXfSettlementEntity.getId());
        tmp.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tXfSettlementDao.updateById(tmp);
        return splitPreInvoiceInfos;
    }

    /**
     * 拼接拆票请求
     * @param tXfSettlementEntity
     * @param tXfSettlementItemEntities
     * @param tAcOrgEntity
     * @return
     */
    private CreatePreInvoiceParam assembleParam(TXfSettlementEntity tXfSettlementEntity,List<TXfSettlementItemEntity> tXfSettlementItemEntities,TAcOrgEntity tAcOrgEntity) {
        CreatePreInvoiceParam createPreInvoiceParam = new CreatePreInvoiceParam();
        BillInfo billInfo = new BillInfo();
        BeanUtil.copyProperties(tXfSettlementEntity, billInfo);
        billInfo.setPriceMethod(1);
        billInfo.setPurchaserId(0L);
        billInfo.setPurchaserGroupId(0L);
        billInfo.setPurchaserTenantId(0l);
        billInfo.setSellerId(0L);
        billInfo.setSellerTenantId(0l);
        billInfo.setSellerGroupId(0l);
        billInfo.setInvoiceType("s");
        billInfo.setSalesbillId(tXfSettlementEntity.getSettlementNo());

        List<BillItem> billItems = new ArrayList<>();
        BeanUtil.copyList( tXfSettlementItemEntities,billItems,BillItem.class);
        String settlementNo = tXfSettlementEntity.getSettlementNo();
        int i = 0;
        for (BillItem billItem : billItems) {
            billItem.setSalesbillId(settlementNo );
            billItem.setSalesbillItemId(settlementNo + i);
            billItem.setSalesbillNo(settlementNo);
            billItem.setSalesbillItemNo(settlementNo + i);

            billItem.setInnerPrepayAmountTax(BigDecimal.ZERO);
            billItem.setInnerPrepayAmountWithoutTax(BigDecimal.ZERO);
            billItem.setInnerPrepayAmountWithTax(BigDecimal.ZERO);

            billItem.setInnerDiscountTax(BigDecimal.ZERO);
            billItem.setInnerDiscountWithTax(BigDecimal.ZERO);
            billItem.setInnerDiscountWithoutTax(BigDecimal.ZERO);

            billItem.setOutterDiscountTax(BigDecimal.ZERO);
            billItem.setOutterDiscountWithoutTax(BigDecimal.ZERO);
            billItem.setOutterDiscountWithTax(BigDecimal.ZERO);

            billItem.setOutterPrepayAmountTax(BigDecimal.ZERO);
            billItem.setOutterPrepayAmountWithoutTax(BigDecimal.ZERO);
            billItem.setOutterPrepayAmountWithTax(BigDecimal.ZERO);

            billItem.setDeductions(BigDecimal.ZERO);
        }
        billInfo.setBillItems(billItems);
        SplitRule splitRule =    JSONObject.parseObject(RULE_INFO, SplitRule.class);
        splitRule.setInvoiceLimit(BigDecimal.valueOf(tAcOrgEntity.getQuota()));
        createPreInvoiceParam.setBillInfo(billInfo);
        createPreInvoiceParam.setRule(splitRule);
        createPreInvoiceParam.setRoutingKey("12");
        return createPreInvoiceParam;
    }
    /**
     * 重新拆票
     * @param settlementNo
     * @param items
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> reSplitPreInvoice(String settlementNo, String sellerNo, List<TXfPreInvoiceItemEntity> items)   {
        TXfSettlementEntity tXfSettlementEntity =  tXfSettlementDao.querySettlementByNo(0l, settlementNo,null );
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountWithOutTax = BigDecimal.ZERO;
        for (TXfPreInvoiceItemEntity tXfPreInvoiceItemEntity : items) {
            taxAmount = taxAmount.add(tXfPreInvoiceItemEntity.getTaxAmount());
            amountWithTax = amountWithTax.add(tXfPreInvoiceItemEntity.getAmountWithTax());
            amountWithOutTax = amountWithOutTax.add(tXfPreInvoiceItemEntity.getAmountWithoutTax());
        }
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        tXfSettlementEntity.setTaxAmount(taxAmount);
        tXfSettlementEntity.setAmountWithoutTax(amountWithOutTax);
        tXfSettlementEntity.setAmountWithTax(amountWithTax);
        List<TXfSettlementItemEntity> list = new ArrayList<>();
        BeanUtil.copyList( items,list,TXfSettlementItemEntity.class);
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(tXfSettlementEntity, list, tAcOrgEntity);
        return   doSplit(createPreInvoiceParam, tXfSettlementEntity);
    }

    /**
     * 查询拆票规则
     * @param sellerNo
     * @return
     */
    public SplitRuleInfoDTO querySplitInvoiceRule(String sellerNo) {
        return null;
    }

    /**
     * 修改拆票规则
     * @param sellerNo
     * @param ruleInfo
     * @return
     */
    public String updateSplitInvoiceRule(String sellerNo, String ruleInfo) {
        return StringUtils.EMPTY;
    }


    public void applyDestroyPreInvoiceAndRedNotification(String invoiceNo,String invoiceCode){
        LambdaQueryWrapper<TXfPreInvoiceEntity> wrapper=new LambdaQueryWrapper<>();
        wrapper.select(TXfPreInvoiceEntity::getId).eq(TXfPreInvoiceEntity::getInvoiceNo,invoiceNo)
                .eq(TXfPreInvoiceEntity::getInvoiceCode,invoiceCode);
        final TXfPreInvoiceEntity one = super.getOne(wrapper);
        commSettlementService.applyDestroyPreInvoiceAndRedNotification(one.getId());
    }
}
