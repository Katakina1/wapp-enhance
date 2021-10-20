package com.xforceplus.wapp.modules.preinvoice.service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.xforceplus.phoenix.split.model.*;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.dto.SplitRuleInfoDTO;
import com.xforceplus.wapp.enums.TXfPreInvoiceStatusEnum;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
    private String domainUrl= "http://bill-split-fat.phoenix-t.xforceplus.com/12/invoice/v1/pre-invoices?appId=12&returnMode=sync&taxDeviceType=4";
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private IDSequence idSequence;
    @PostConstruct
    public void initData() {
        splitPreInvoice("settlementNo1853061001646081","172164");
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
        TXfSettlementEntity tXfSettlementEntity =  tXfSettlementDao.querySettlementByNo(0l, settlementNo,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode() );
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(tXfSettlementEntity, tXfSettlementItemEntities, tAcOrgEntity);
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        headers.add("x-userinfo", "tenant-gateway.41-tcenter-prod");


     //   headers.add("Content-Encoding", "UTF-8");

         HttpEntity<String> formEntity = new HttpEntity<>(JSON.toJSONString(createPreInvoiceParam), headers);

        //ResponseEntity<String> res = restTemplate.postForEntity(domainUrl,   formEntity, String.class );
       // String object = res.getBody();
//        JSONObject baseRes =  JSONObject.parseObject(object);
//        if (StringUtils.isEmpty(baseRes.getString("result"))) {
//            return null;
//        }
        String dfd = "[\n" +
                "    {\n" +
                "      \"ruleId\": \"20008\",\n" +
                "      \"preInvoiceMain\": {\n" +
                "        \"batchNo\": null,\n" +
                "        \"outBatchNo\": null,\n" +
                "        \"customerNo\": \"2019042413321442141940223\",\n" +
                "        \"systemOrig\": \"\",\n" +
                "        \"purchaserTenantId\": 10,\n" +
                "        \"purchaserGroupId\": null,\n" +
                "        \"purchaserId\": 9,\n" +
                "        \"purchaserNo\": \"100\",\n" +
                "        \"purchaserName\": \"乐信（上海）贸易有限公司\",\n" +
                "        \"purchaserTaxNo\": \"91310000336466103C\",\n" +
                "        \"purchaserTel\": \"021-64320238\",\n" +
                "        \"purchaserAddress\": \"上海市徐汇区虹梅路1801号A区16楼1602室\",\n" +
                "        \"purchaserBankName\": \"招商银行上海新客站支行\",\n" +
                "        \"purchaserBankAccount\": \"121 9182 0801 0801\",\n" +
                "        \"sellerTenantId\": 77,\n" +
                "        \"sellerGroupId\": 77,\n" +
                "        \"sellerNo\": \"CN28616\",\n" +
                "        \"sellerTaxNo\": \"91310000631343915C\",\n" +
                "        \"sellerName\": \"上海奕方农业科技股份有限公司\",\n" +
                "        \"sellerTel\": \"021-31265533\",\n" +
                "        \"sellerAddress\": \"上海市松江区泖港镇新宾路1258号\",\n" +
                "        \"sellerBankName\": \"杭州银行上海长宁支行\",\n" +
                "        \"sellerBankAccount\": \"3101068688100027434\",\n" +
                "        \"sellerId\": 75,\n" +
                "        \"invoiceType\": \"s\",\n" +
                "        \"businessBillType\": \"AP\",\n" +
                "        \"salesbillType\": \"标准\",\n" +
                "        \"invoiceNo\": null,\n" +
                "        \"invoiceCode\": null,\n" +
                "        \"paperDrawDate\": null,\n" +
                "        \"machineCode\": null,\n" +
                "        \"checkCode\": null,\n" +
                "        \"amountWithoutTax\": 884906.28,\n" +
                "        \"taxAmount\": 115037.82,\n" +
                "        \"amountWithTax\": 999944.1,\n" +
                "        \"remark\": \"发票所属期为2019年05月 价格方式[不含税] 业务单号[APS190500056] 扩展备注内容非常长dfdsa32234i99034>:{:{{:*^%%?()（）{}fsdfiohjfashdoihoirwhrlho3hljndslkfcnkazlsfhnldskah\",\n" +
                "        \"cashierName\": \"曹杰\",\n" +
                "        \"checkerName\": \"钱戎\",\n" +
                "        \"invoicerName\": \"曹秋萍\",\n" +
                "        \"electronicSignature\": null,\n" +
                "        \"ruleId\": 20008,\n" +
                "        \"sysOrgId\": 85,\n" +
                "        \"originInvoiceNo\": \"\",\n" +
                "        \"originInvoiceCode\": \"\",\n" +
                "        \"redNotificationNo\": null,\n" +
                "        \"receiveUserEmail\": \"\",\n" +
                "        \"receiveUserTel\": \"\",\n" +
                "        \"invoiceSignature\": null,\n" +
                "        \"createUserId\": null,\n" +
                "        \"displayPriceQuality\": 0,\n" +
                "        \"saleListFileFlag\": 0,\n" +
                "        \"templateVersion\": 8,\n" +
                "        \"listGoodsName\": \"\",\n" +
                "        \"specialInvoiceFlag\": \"0\",\n" +
                "        \"taxRate\": \"0.13\",\n" +
                "        \"orderNo\": null,\n" +
                "        \"contractNo\": null,\n" +
                "        \"salesbillId\": \"449871000437837832\",\n" +
                "        \"salesbillItemNo\": null,\n" +
                "        \"ext1\": null,\n" +
                "        \"ext2\": null,\n" +
                "        \"ext3\": null,\n" +
                "        \"ext4\": null,\n" +
                "        \"ext5\": null,\n" +
                "        \"ext6\": null,\n" +
                "        \"ext7\": null,\n" +
                "        \"ext8\": null,\n" +
                "        \"ext9\": null,\n" +
                "        \"ext10\": null,\n" +
                "        \"ext11\": null,\n" +
                "        \"ext12\": null,\n" +
                "        \"ext13\": null,\n" +
                "        \"ext14\": null,\n" +
                "        \"ext15\": null,\n" +
                "        \"ext16\": null,\n" +
                "        \"ext17\": null,\n" +
                "        \"ext18\": null,\n" +
                "        \"ext19\": null,\n" +
                "        \"ext20\": null,\n" +
                "        \"ext21\": null,\n" +
                "        \"ext22\": null,\n" +
                "        \"ext23\": null,\n" +
                "        \"ext24\": null,\n" +
                "        \"ext25\": null,\n" +
                "        \"extInfo\": null\n" +
                "      },\n" +
                "      \"preInvoiceItems\": [\n" +
                "        {\n" +
                "          \"preInvoiceId\": null,\n" +
                "          \"goodsTaxNo\": \"1030309020000000000\",\n" +
                "          \"cargoName\": \"*饮料酒精原辅料*椰子风味爆爆珠（果味酱）\",\n" +
                "          \"cargoCode\": \"1#10250001\",\n" +
                "          \"itemSpec\": \"\",\n" +
                "          \"unitPrice\": 165.310345,\n" +
                "          \"quantity\": 5353,\n" +
                "          \"quantityUnit\": \"CAR\",\n" +
                "          \"taxRate\": 0.13,\n" +
                "          \"amountWithoutTax\": 884906.28,\n" +
                "          \"taxAmount\": 115037.82,\n" +
                "          \"amountWithTax\": 999944.1,\n" +
                "          \"discountRate\": null,\n" +
                "          \"discountWithoutTax\": 0,\n" +
                "          \"discountWithTax\": 0,\n" +
                "          \"discountTax\": 0,\n" +
                "          \"floatingAmount\": null,\n" +
                "          \"taxItem\": \"\",\n" +
                "          \"goodsNoVer\": \"32.0\",\n" +
                "          \"taxPre\": \"0\",\n" +
                "          \"taxPreCon\": \"\",\n" +
                "          \"zeroTax\": \"\",\n" +
                "          \"deduction\": 0,\n" +
                "          \"discountFlag\": null,\n" +
                "          \"priceMethod\": \"0\",\n" +
                "          \"printContentFlag\": \"0\",\n" +
                "          \"itemTypeCode\": \"\",\n" +
                "          \"ext1\": null,\n" +
                "          \"ext2\": null,\n" +
                "          \"ext3\": null,\n" +
                "          \"ext4\": null,\n" +
                "          \"ext5\": null,\n" +
                "          \"ext6\": null,\n" +
                "          \"ext7\": null,\n" +
                "          \"ext8\": null,\n" +
                "          \"ext9\": null,\n" +
                "          \"ext10\": null,\n" +
                "          \"ext11\": null,\n" +
                "          \"ext12\": null,\n" +
                "          \"ext13\": null,\n" +
                "          \"ext14\": null,\n" +
                "          \"ext15\": null,\n" +
                "          \"ext16\": null,\n" +
                "          \"ext17\": null,\n" +
                "          \"ext18\": null,\n" +
                "          \"ext19\": null,\n" +
                "          \"ext20\": null,\n" +
                "          \"extInfo\": null,\n" +
                "          \"origin\": 0,\n" +
                "          \"salesbillId\": \"449871000437837832\",\n" +
                "          \"salesbillItemId\": \"449871000442032131\",\n" +
                "          \"salesbillNo\": \"APS190500056\",\n" +
                "          \"salesbillItemNo\": \"1#10250001\",\n" +
                "          \"outterDiscountWithTax\": 0,\n" +
                "          \"outterDiscountWithoutTax\": 0,\n" +
                "          \"outterDiscountTax\": 0,\n" +
                "          \"innerDiscountWithTax\": 0,\n" +
                "          \"innerDiscountWithoutTax\": 0,\n" +
                "          \"innerDiscountTax\": 0,\n" +
                "          \"innerPrepayAmountWithTax\": 0,\n" +
                "          \"innerPrepayAmountWithoutTax\": 0,\n" +
                "          \"innerPrepayAmountTax\": 0,\n" +
                "          \"outterPrepayAmountWithTax\": 0,\n" +
                "          \"outterPrepayAmountWithoutTax\": 0,\n" +
                "          \"outterPrepayAmountTax\": 0\n" +
                "        }\n" +
                "      ],\n" +
                "      \"preInvoiceExt\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"ruleId\": \"20008\",\n" +
                "      \"preInvoiceMain\": {\n" +
                "        \"batchNo\": null,\n" +
                "        \"outBatchNo\": null,\n" +
                "        \"customerNo\": \"2019042413321442141940223\",\n" +
                "        \"systemOrig\": \"\",\n" +
                "        \"purchaserTenantId\": 10,\n" +
                "        \"purchaserGroupId\": null,\n" +
                "        \"purchaserId\": 9,\n" +
                "        \"purchaserNo\": \"100\",\n" +
                "        \"purchaserName\": \"乐信（上海）贸易有限公司\",\n" +
                "        \"purchaserTaxNo\": \"91310000336466103C\",\n" +
                "        \"purchaserTel\": \"021-64320238\",\n" +
                "        \"purchaserAddress\": \"上海市徐汇区虹梅路1801号A区16楼1602室\",\n" +
                "        \"purchaserBankName\": \"招商银行上海新客站支行\",\n" +
                "        \"purchaserBankAccount\": \"121 9182 0801 0801\",\n" +
                "        \"sellerTenantId\": 77,\n" +
                "        \"sellerGroupId\": 77,\n" +
                "        \"sellerNo\": \"CN28616\",\n" +
                "        \"sellerTaxNo\": \"91310000631343915C\",\n" +
                "        \"sellerName\": \"上海奕方农业科技股份有限公司\",\n" +
                "        \"sellerTel\": \"021-31265533\",\n" +
                "        \"sellerAddress\": \"上海市松江区泖港镇新宾路1258号\",\n" +
                "        \"sellerBankName\": \"杭州银行上海长宁支行\",\n" +
                "        \"sellerBankAccount\": \"3101068688100027434\",\n" +
                "        \"sellerId\": 75,\n" +
                "        \"invoiceType\": \"s\",\n" +
                "        \"businessBillType\": \"AP\",\n" +
                "        \"salesbillType\": \"标准\",\n" +
                "        \"invoiceNo\": null,\n" +
                "        \"invoiceCode\": null,\n" +
                "        \"paperDrawDate\": null,\n" +
                "        \"machineCode\": null,\n" +
                "        \"checkCode\": null,\n" +
                "        \"amountWithoutTax\": 884906.28,\n" +
                "        \"taxAmount\": 115037.82,\n" +
                "        \"amountWithTax\": 999944.1,\n" +
                "        \"remark\": \"发票所属期为2019年05月 价格方式[不含税] 业务单号[APS190500056] 扩展备注内容非常长dfdsa32234i99034>:{:{{:*^%%?()（）{}fsdfiohjfashdoihoirwhrlho3hljndslkfcnkazlsfhnldskah\",\n" +
                "        \"cashierName\": \"曹杰\",\n" +
                "        \"checkerName\": \"钱戎\",\n" +
                "        \"invoicerName\": \"曹秋萍\",\n" +
                "        \"electronicSignature\": null,\n" +
                "        \"ruleId\": 20008,\n" +
                "        \"sysOrgId\": 85,\n" +
                "        \"originInvoiceNo\": \"\",\n" +
                "        \"originInvoiceCode\": \"\",\n" +
                "        \"redNotificationNo\": null,\n" +
                "        \"receiveUserEmail\": \"\",\n" +
                "        \"receiveUserTel\": \"\",\n" +
                "        \"invoiceSignature\": null,\n" +
                "        \"createUserId\": null,\n" +
                "        \"displayPriceQuality\": 0,\n" +
                "        \"saleListFileFlag\": 0,\n" +
                "        \"templateVersion\": 8,\n" +
                "        \"listGoodsName\": \"\",\n" +
                "        \"specialInvoiceFlag\": \"0\",\n" +
                "        \"taxRate\": \"0.13\",\n" +
                "        \"orderNo\": null,\n" +
                "        \"contractNo\": null,\n" +
                "        \"salesbillId\": \"449871000437837832\",\n" +
                "        \"salesbillItemNo\": null,\n" +
                "        \"ext1\": null,\n" +
                "        \"ext2\": null,\n" +
                "        \"ext3\": null,\n" +
                "        \"ext4\": null,\n" +
                "        \"ext5\": null,\n" +
                "        \"ext6\": null,\n" +
                "        \"ext7\": null,\n" +
                "        \"ext8\": null,\n" +
                "        \"ext9\": null,\n" +
                "        \"ext10\": null,\n" +
                "        \"ext11\": null,\n" +
                "        \"ext12\": null,\n" +
                "        \"ext13\": null,\n" +
                "        \"ext14\": null,\n" +
                "        \"ext15\": null,\n" +
                "        \"ext16\": null,\n" +
                "        \"ext17\": null,\n" +
                "        \"ext18\": null,\n" +
                "        \"ext19\": null,\n" +
                "        \"ext20\": null,\n" +
                "        \"ext21\": null,\n" +
                "        \"ext22\": null,\n" +
                "        \"ext23\": null,\n" +
                "        \"ext24\": null,\n" +
                "        \"ext25\": null,\n" +
                "        \"extInfo\": null\n" +
                "      },\n" +
                "      \"preInvoiceItems\": [\n" +
                "        {\n" +
                "          \"preInvoiceId\": null,\n" +
                "          \"goodsTaxNo\": \"1030309020000000000\",\n" +
                "          \"cargoName\": \"*饮料酒精原辅料*椰子风味爆爆珠（果味酱）\",\n" +
                "          \"cargoCode\": \"1#10250001\",\n" +
                "          \"itemSpec\": \"\",\n" +
                "          \"unitPrice\": 165.310345,\n" +
                "          \"quantity\": 5353,\n" +
                "          \"quantityUnit\": \"CAR\",\n" +
                "          \"taxRate\": 0.13,\n" +
                "          \"amountWithoutTax\": 884906.28,\n" +
                "          \"taxAmount\": 115037.82,\n" +
                "          \"amountWithTax\": 999944.1,\n" +
                "          \"discountRate\": null,\n" +
                "          \"discountWithoutTax\": 0,\n" +
                "          \"discountWithTax\": 0,\n" +
                "          \"discountTax\": 0,\n" +
                "          \"floatingAmount\": null,\n" +
                "          \"taxItem\": \"\",\n" +
                "          \"goodsNoVer\": \"32.0\",\n" +
                "          \"taxPre\": \"0\",\n" +
                "          \"taxPreCon\": \"\",\n" +
                "          \"zeroTax\": \"\",\n" +
                "          \"deduction\": 0,\n" +
                "          \"discountFlag\": null,\n" +
                "          \"priceMethod\": \"0\",\n" +
                "          \"printContentFlag\": \"0\",\n" +
                "          \"itemTypeCode\": \"\",\n" +
                "          \"ext1\": null,\n" +
                "          \"ext2\": null,\n" +
                "          \"ext3\": null,\n" +
                "          \"ext4\": null,\n" +
                "          \"ext5\": null,\n" +
                "          \"ext6\": null,\n" +
                "          \"ext7\": null,\n" +
                "          \"ext8\": null,\n" +
                "          \"ext9\": null,\n" +
                "          \"ext10\": null,\n" +
                "          \"ext11\": null,\n" +
                "          \"ext12\": null,\n" +
                "          \"ext13\": null,\n" +
                "          \"ext14\": null,\n" +
                "          \"ext15\": null,\n" +
                "          \"ext16\": null,\n" +
                "          \"ext17\": null,\n" +
                "          \"ext18\": null,\n" +
                "          \"ext19\": null,\n" +
                "          \"ext20\": null,\n" +
                "          \"extInfo\": null,\n" +
                "          \"origin\": 0,\n" +
                "          \"salesbillId\": \"449871000437837832\",\n" +
                "          \"salesbillItemId\": \"449871000442032131\",\n" +
                "          \"salesbillNo\": \"APS190500056\",\n" +
                "          \"salesbillItemNo\": \"1#10250001\",\n" +
                "          \"outterDiscountWithTax\": 0,\n" +
                "          \"outterDiscountWithoutTax\": 0,\n" +
                "          \"outterDiscountTax\": 0,\n" +
                "          \"innerDiscountWithTax\": 0,\n" +
                "          \"innerDiscountWithoutTax\": 0,\n" +
                "          \"innerDiscountTax\": 0,\n" +
                "          \"innerPrepayAmountWithTax\": 0,\n" +
                "          \"innerPrepayAmountWithoutTax\": 0,\n" +
                "          \"innerPrepayAmountTax\": 0,\n" +
                "          \"outterPrepayAmountWithTax\": 0,\n" +
                "          \"outterPrepayAmountWithoutTax\": 0,\n" +
                "          \"outterPrepayAmountTax\": 0\n" +
                "        }\n" +
                "      ],\n" +
                "      \"preInvoiceExt\": null\n" +
                "    },\n" +
                "    {\n" +
                "      \"ruleId\": \"20008\",\n" +
                "      \"preInvoiceMain\": {\n" +
                "        \"batchNo\": null,\n" +
                "        \"outBatchNo\": null,\n" +
                "        \"customerNo\": \"2019042413321442141940223\",\n" +
                "        \"systemOrig\": \"\",\n" +
                "        \"purchaserTenantId\": 10,\n" +
                "        \"purchaserGroupId\": null,\n" +
                "        \"purchaserId\": 9,\n" +
                "        \"purchaserNo\": \"100\",\n" +
                "        \"purchaserName\": \"乐信（上海）贸易有限公司\",\n" +
                "        \"purchaserTaxNo\": \"91310000336466103C\",\n" +
                "        \"purchaserTel\": \"021-64320238\",\n" +
                "        \"purchaserAddress\": \"上海市徐汇区虹梅路1801号A区16楼1602室\",\n" +
                "        \"purchaserBankName\": \"招商银行上海新客站支行\",\n" +
                "        \"purchaserBankAccount\": \"121 9182 0801 0801\",\n" +
                "        \"sellerTenantId\": 77,\n" +
                "        \"sellerGroupId\": 77,\n" +
                "        \"sellerNo\": \"CN28616\",\n" +
                "        \"sellerTaxNo\": \"91310000631343915C\",\n" +
                "        \"sellerName\": \"上海奕方农业科技股份有限公司\",\n" +
                "        \"sellerTel\": \"021-31265533\",\n" +
                "        \"sellerAddress\": \"上海市松江区泖港镇新宾路1258号\",\n" +
                "        \"sellerBankName\": \"杭州银行上海长宁支行\",\n" +
                "        \"sellerBankAccount\": \"3101068688100027434\",\n" +
                "        \"sellerId\": 75,\n" +
                "        \"invoiceType\": \"s\",\n" +
                "        \"businessBillType\": \"AP\",\n" +
                "        \"salesbillType\": \"标准\",\n" +
                "        \"invoiceNo\": null,\n" +
                "        \"invoiceCode\": null,\n" +
                "        \"paperDrawDate\": null,\n" +
                "        \"machineCode\": null,\n" +
                "        \"checkCode\": null,\n" +
                "        \"amountWithoutTax\": 576271.85,\n" +
                "        \"taxAmount\": 74915.34,\n" +
                "        \"amountWithTax\": 651187.19,\n" +
                "        \"remark\": \"发票所属期为2019年05月 价格方式[不含税] 业务单号[APS190500056] 扩展备注内容非常长dfdsa32234i99034>:{:{{:*^%%?()（）{}fsdfiohjfashdoihoirwhrlho3hljndslkfcnkazlsfhnldskah\",\n" +
                "        \"cashierName\": \"曹杰\",\n" +
                "        \"checkerName\": \"钱戎\",\n" +
                "        \"invoicerName\": \"曹秋萍\",\n" +
                "        \"electronicSignature\": null,\n" +
                "        \"ruleId\": 20008,\n" +
                "        \"sysOrgId\": 85,\n" +
                "        \"originInvoiceNo\": \"\",\n" +
                "        \"originInvoiceCode\": \"\",\n" +
                "        \"redNotificationNo\": null,\n" +
                "        \"receiveUserEmail\": \"\",\n" +
                "        \"receiveUserTel\": \"\",\n" +
                "        \"invoiceSignature\": null,\n" +
                "        \"createUserId\": null,\n" +
                "        \"displayPriceQuality\": 0,\n" +
                "        \"saleListFileFlag\": 0,\n" +
                "        \"templateVersion\": 8,\n" +
                "        \"listGoodsName\": \"\",\n" +
                "        \"specialInvoiceFlag\": \"0\",\n" +
                "        \"taxRate\": \"0.13\",\n" +
                "        \"orderNo\": null,\n" +
                "        \"contractNo\": null,\n" +
                "        \"salesbillId\": \"449871000437837832\",\n" +
                "        \"salesbillItemNo\": null,\n" +
                "        \"ext1\": null,\n" +
                "        \"ext2\": null,\n" +
                "        \"ext3\": null,\n" +
                "        \"ext4\": null,\n" +
                "        \"ext5\": null,\n" +
                "        \"ext6\": null,\n" +
                "        \"ext7\": null,\n" +
                "        \"ext8\": null,\n" +
                "        \"ext9\": null,\n" +
                "        \"ext10\": null,\n" +
                "        \"ext11\": null,\n" +
                "        \"ext12\": null,\n" +
                "        \"ext13\": null,\n" +
                "        \"ext14\": null,\n" +
                "        \"ext15\": null,\n" +
                "        \"ext16\": null,\n" +
                "        \"ext17\": null,\n" +
                "        \"ext18\": null,\n" +
                "        \"ext19\": null,\n" +
                "        \"ext20\": null,\n" +
                "        \"ext21\": null,\n" +
                "        \"ext22\": null,\n" +
                "        \"ext23\": null,\n" +
                "        \"ext24\": null,\n" +
                "        \"ext25\": null,\n" +
                "        \"extInfo\": null\n" +
                "      },\n" +
                "      \"preInvoiceItems\": [\n" +
                "        {\n" +
                "          \"preInvoiceId\": null,\n" +
                "          \"goodsTaxNo\": \"1030309020000000000\",\n" +
                "          \"cargoName\": \"*饮料酒精原辅料*椰子风味爆爆珠（果味酱）\",\n" +
                "          \"cargoCode\": \"1#10250001\",\n" +
                "          \"itemSpec\": \"\",\n" +
                "          \"unitPrice\": 165.31034136546185,\n" +
                "          \"quantity\": 3486,\n" +
                "          \"quantityUnit\": \"CAR\",\n" +
                "          \"taxRate\": 0.13,\n" +
                "          \"amountWithoutTax\": 576271.85,\n" +
                "          \"taxAmount\": 74915.34,\n" +
                "          \"amountWithTax\": 651187.19,\n" +
                "          \"discountRate\": null,\n" +
                "          \"discountWithoutTax\": 0,\n" +
                "          \"discountWithTax\": 0,\n" +
                "          \"discountTax\": 0,\n" +
                "          \"floatingAmount\": null,\n" +
                "          \"taxItem\": \"\",\n" +
                "          \"goodsNoVer\": \"32.0\",\n" +
                "          \"taxPre\": \"0\",\n" +
                "          \"taxPreCon\": \"\",\n" +
                "          \"zeroTax\": \"\",\n" +
                "          \"deduction\": 0,\n" +
                "          \"discountFlag\": null,\n" +
                "          \"priceMethod\": \"0\",\n" +
                "          \"printContentFlag\": \"0\",\n" +
                "          \"itemTypeCode\": \"\",\n" +
                "          \"ext1\": null,\n" +
                "          \"ext2\": null,\n" +
                "          \"ext3\": null,\n" +
                "          \"ext4\": null,\n" +
                "          \"ext5\": null,\n" +
                "          \"ext6\": null,\n" +
                "          \"ext7\": null,\n" +
                "          \"ext8\": null,\n" +
                "          \"ext9\": null,\n" +
                "          \"ext10\": null,\n" +
                "          \"ext11\": null,\n" +
                "          \"ext12\": null,\n" +
                "          \"ext13\": null,\n" +
                "          \"ext14\": null,\n" +
                "          \"ext15\": null,\n" +
                "          \"ext16\": null,\n" +
                "          \"ext17\": null,\n" +
                "          \"ext18\": null,\n" +
                "          \"ext19\": null,\n" +
                "          \"ext20\": null,\n" +
                "          \"extInfo\": null,\n" +
                "          \"origin\": 0,\n" +
                "          \"salesbillId\": \"449871000437837832\",\n" +
                "          \"salesbillItemId\": \"449871000442032131\",\n" +
                "          \"salesbillNo\": \"APS190500056\",\n" +
                "          \"salesbillItemNo\": \"1#10250001\",\n" +
                "          \"outterDiscountWithTax\": 0,\n" +
                "          \"outterDiscountWithoutTax\": 0,\n" +
                "          \"outterDiscountTax\": 0,\n" +
                "          \"innerDiscountWithTax\": 0,\n" +
                "          \"innerDiscountWithoutTax\": 0,\n" +
                "          \"innerDiscountTax\": 0,\n" +
                "          \"innerPrepayAmountWithTax\": 0,\n" +
                "          \"innerPrepayAmountWithoutTax\": 0,\n" +
                "          \"innerPrepayAmountTax\": 0,\n" +
                "          \"outterPrepayAmountWithTax\": 0,\n" +
                "          \"outterPrepayAmountWithoutTax\": 0,\n" +
                "          \"outterPrepayAmountTax\": 0\n" +
                "        }\n" +
                "      ],\n" +
                "      \"preInvoiceExt\": null\n" +
                "    }\n" +
                "  ]";
        Date date = new Date();
        List<SplitPreInvoiceInfo> splitPreInvoiceInfos = JSON.parseArray(dfd, SplitPreInvoiceInfo.class);
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
        createPreInvoiceParam.setRoutingKey("");

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
