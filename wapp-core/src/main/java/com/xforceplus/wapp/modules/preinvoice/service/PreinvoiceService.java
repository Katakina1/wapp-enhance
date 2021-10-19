package com.xforceplus.wapp.modules.preinvoice.service;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xforceplus.phoenix.split.model.*;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.dto.SplitRuleInfoDTO;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceDao;
import com.xforceplus.wapp.repository.dao.TXfPreInvoiceItemDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemExtDao;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommRedNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class PreinvoiceService {
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
    private RestTemplate restTemplate;
    // 配置访问域名
    private String domainUrl= "";
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private IDSequence idSequence;
    @PostConstruct
    public void initData() {

     //   splitPreInvoice("","");
    }

    /**
     * 拆票方法
     * @param settlementNo
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> splitPreInvoice(String settlementNo, String sellerNo) {
        //查询待拆票算单主信息，
        //查询结算单明细信息
        //查询开票信息
        //组装拆票请求
        //调用拆票请求
        //保存拆票结果
        //调用红字请求
        TXfSettlementEntity tXfSettlementEntity =  tXfSettlementDao.querySettlementByNo( settlementNo,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode() );
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(tXfSettlementEntity, tXfSettlementItemEntities, tAcOrgEntity);
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenantId", 0);
        param.put("appId", 3);
        param.put("returnMode", "sync");
        param.put("taxDeviceType", 3);
        ResponseEntity<BaseResponse> res = restTemplate.postForEntity(domainUrl, createPreInvoiceParam, BaseResponse.class, param);
        Object object = res.getBody().getResult();
        List<SplitPreInvoiceInfo> splitPreInvoiceInfos = (List<SplitPreInvoiceInfo>) object;
        for (SplitPreInvoiceInfo splitPreInvoiceInfo : splitPreInvoiceInfos) {
            TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = new ArrayList<>();
            BeanUtil.copyProperties(splitPreInvoiceInfo, tXfPreInvoiceEntity);
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

        return null;
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
        List<BillItem> billItems = new ArrayList<>();
        BeanUtil.copyList( tXfSettlementItemEntities,billItems,BillItem.class);
        String settlementNo = tXfSettlementEntity.getSettlementNo();
        int i = 0;
        for (BillItem billItem : billItems) {
            billItem.setSalesbillId(settlementNo );
            billItem.setSalesbillItemId(settlementNo + i);
            billItem.setSalesbillNo(settlementNo);
            billItem.setSalesbillItemNo(settlementNo + i);
        }
        billInfo.setBillItems(billItems);
        SplitRule splitRule =    JSONObject.parseObject(RULE_INFO, SplitRule.class);
        splitRule.setInvoiceLimit(BigDecimal.valueOf(tAcOrgEntity.getQuota()));
        createPreInvoiceParam.setBillInfo(billInfo);
        createPreInvoiceParam.setRule(splitRule);
        return createPreInvoiceParam;
    }
    /**
     * 重新拆票
     * @param settlementNo
     * @param items
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> reSplitPreInvoice(String settlementNo, String sellerNo, List<TXfPreInvoiceItemEntity> items) {
        return null;
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
}
