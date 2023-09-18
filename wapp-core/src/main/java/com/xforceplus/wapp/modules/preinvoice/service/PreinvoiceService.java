package com.xforceplus.wapp.modules.preinvoice.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.xforceplus.phoenix.split.model.*;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.enums.DeductRedNotificationEventEnum;
import com.xforceplus.wapp.common.enums.ValueEnum;
import com.xforceplus.wapp.common.event.DeductRedNotificationEvent;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.Asserts;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.CommonUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.dto.PreInvoiceDTO;
import com.xforceplus.wapp.enums.*;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.modules.deduct.service.DeductService;
import com.xforceplus.wapp.modules.log.controller.OperateLogService;
import com.xforceplus.wapp.modules.rednotification.exception.RRException;
import com.xforceplus.wapp.modules.rednotification.model.taxware.*;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import com.xforceplus.wapp.repository.dao.*;
import com.xforceplus.wapp.repository.entity.*;
import com.xforceplus.wapp.sequence.IDSequence;
import com.xforceplus.wapp.service.CommRedNotificationService;
import com.xforceplus.wapp.service.CommSettlementService;
import com.xforceplus.wapp.service.CommonMessageService;
import com.xforceplus.wapp.util.ItemNameUtils;
import com.xforceplus.wapp.util.RestTemplateSingle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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

    private static final long ONE_MB_SIZE = 200 * 1024L;

    static final String Claim_RULE_INFO = " {\n" +
            "\t\t\"amountSplitRule\": \"2\",\n" +
            "\t\t\"cargoNameLength\": 92,\n" +
            "\t\t\"customRemarkSize\": 200,\n" +
            "\t\t\"ignoreAllowableError\": false,\n" +
            "\t\t\"invoiceItemMaxRow\": 8,\n" +
            "\t\t\"invoiceLimit\": 9999.99,\n" +
            "\t\t\"itemSort\": \"2\",\n" +
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
            "\t\t\"invoiceMaxErrorAmount\": 1.26,\n" +
            "\t\t\"saleListOption\": \"1\",\n" +
            "\t\t\"salesListMaxRow\": 2000,\n" +
            "\t\t\"showSpecification\": true,\n" +
            "\t\t\"splitByItemPriceQuantityNon\": false,\n" +
            "\t\t\"splitFiledList\": [\"taxRate\"],\n" +
            "\t\t\"unitPriceAmountOps\": \"0\",\n" +
            "\t\t\"unitPriceScale\": 15,\n" +
            "\t\t\"zeroTaxOption\": \"NOT_PROCESS\"\n" +
            "\t}";

    static final String EPD_AGREEMENT_RULE_INFO = " {\n" +
            "\t\t\"amountSplitRule\": \"2\",\n" +
            "\t\t\"cargoNameLength\": 92,\n" +
            "\t\t\"customRemarkSize\": 200,\n" +
            "\t\t\"ignoreAllowableError\": false,\n" +
            "\t\t\"invoiceItemMaxRow\": 8,\n" +
            "\t\t\"invoiceLimit\": 9999.99,\n" +
            "\t\t\"itemSort\": \"2\",\n" +
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
            "\t\t\"invoiceMaxErrorAmount\": 0.05,\n" +
            "\t\t\"saleListOption\": \"1\",\n" +
            "\t\t\"salesListMaxRow\": 2000,\n" +
            "\t\t\"showSpecification\": true,\n" +
            "\t\t\"splitByItemPriceQuantityNon\": false,\n" +
            "\t\t\"splitFiledList\": [\"taxRate\"],\n" +
            "\t\t\"unitPriceAmountOps\": \"0\",\n" +
            "\t\t\"unitPriceScale\": 15,\n" +
            "\t\t\"zeroTaxOption\": \"NOT_PROCESS\"\n" +
            "\t}";

    @Autowired
    @Lazy
    private CommSettlementService commSettlementService;
    @Autowired
    @Lazy
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TXfSettlementExtDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemExtDao tXfSettlementItemDao;
    @Autowired
    private CompanyService companyService;
    @Autowired
    @Lazy
    private DeductService deductService;
    @Autowired
    private TXfPreInvoiceDao tXfPreInvoiceDao;
    @Autowired
    private TXfPreInvoiceItemDao tXfPreInvoiceItemDao;
    @Autowired
    private TXfSettlementItemInvoiceDetailDao tXfSettlementItemInvoiceDetailDao;
    @Autowired
    private IDSequence idSequence;
    @Autowired
    private TXfBillDeductInvoiceDao tXfBillDeductInvoiceDao;
    @Autowired
    private TDxRecordInvoiceDao tDxRecordInvoiceDao;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PreBillDetailService preBillDetailService;
    @Autowired
    private CommonMessageService commonMessageService;
    @Autowired
    private OperateLogService operateLogService;
    @Autowired
    @Lazy
    private TaxWareService taxWareService;

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
    @Value("${wapp.integration.authentication}")
    private String authentication;

    @Autowired
    private LockClient lockClient;

    @Async
    public void splitPreInvoiceAsync(String settlementNo, String sellerNo) {
        splitPreInvoice(settlementNo, sellerNo);
    }

    /**
     * 结算单执行拆票
     *
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
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0l, settlementNo, TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
        if (Objects.isNull(tXfSettlementEntity)) {
            return Collections.EMPTY_LIST;
        }
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(tXfSettlementEntity, tXfSettlementItemEntities, tAcOrgEntity);

        return splitAndSaveInvoice(createPreInvoiceParam, tXfSettlementEntity, tAcOrgEntity, true);
    }

    /**
     * 结算单拆票
     *
     * @param settlementEntity
     * @param redNotificationApply true-申请红字，false-不申请红字
     * @return
     */
    public R splitPreInvoice(TXfSettlementEntity settlementEntity, boolean redNotificationApply) {
        // 结算单状态校验
        Asserts.isTrue(Objects.equals(settlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.DESTROY.getCode()), String.format("结算单[%s]已经作废不能操作", settlementEntity.getSettlementNo()));
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(settlementEntity.getSettlementStatus()), "结算单已上传红票不能操作");
        // 查询结算单明细
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementEntity.getSettlementNo());
        // 查询公司信息--拆票信息
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(settlementEntity.getSellerNo(), "8");
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(settlementEntity, tXfSettlementItemEntities, tAcOrgEntity);
        // 发起拆票
        splitAndSaveInvoice(createPreInvoiceParam, settlementEntity, tAcOrgEntity, redNotificationApply);
        return R.ok(null, "拆票成功");
    }

    /**
     * 发起红字信息表申请
     *
     * @param settlementEntity
     * @return
     */
    public R redNotificationApply(TXfSettlementEntity settlementEntity) {
        // 结算单状态校验
        Asserts.isTrue(Objects.equals(settlementEntity.getSettlementStatus(), TXfSettlementStatusEnum.DESTROY.getCode()), String.format("结算单[%s]已经作废不能操作", settlementEntity.getSettlementNo()));
        Asserts.isFalse(TXfSettlementStatusEnum.isCanDestroy(settlementEntity.getSettlementStatus()), "结算单已上传红票不能操作");
        // 查询暂未申请红字信心表的预制发票
        LambdaQueryWrapper<TXfPreInvoiceEntity> queryWrapper = Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).eq(TXfPreInvoiceEntity::getSettlementId, settlementEntity.getId())
                .eq(TXfPreInvoiceEntity::getPreInvoiceStatus, TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        List<TXfPreInvoiceEntity> preInvoiceEntityList = tXfPreInvoiceDao.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(preInvoiceEntityList)) {
            // 查询预制发票明细
            List<Long> preInvoiceIdList = preInvoiceEntityList.stream().map(TXfPreInvoiceEntity::getId).collect(Collectors.toList());
            LambdaQueryWrapper<TXfPreInvoiceItemEntity> preInvoiceItemQuery = Wrappers.lambdaQuery(TXfPreInvoiceItemEntity.class).in(TXfPreInvoiceItemEntity::getPreInvoiceId, preInvoiceIdList);
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = tXfPreInvoiceItemDao.selectList(preInvoiceItemQuery);
            Map<Long, List<TXfPreInvoiceItemEntity>> preInvoiceItemMap = tXfPreInvoiceItemEntities.stream().collect(Collectors.groupingBy(TXfPreInvoiceItemEntity::getPreInvoiceId));

            for (TXfPreInvoiceEntity preInvoiceEntity : preInvoiceEntityList) {
                List<TXfPreInvoiceItemEntity> preInvoiceItemEntityList = preInvoiceItemMap.get(preInvoiceEntity.getId());

                PreInvoiceDTO applyProInvoiceRedNotificationDTO = new PreInvoiceDTO();
                applyProInvoiceRedNotificationDTO.setTXfPreInvoiceEntity(preInvoiceEntity);
                applyProInvoiceRedNotificationDTO.setTXfPreInvoiceItemEntityList(preInvoiceItemEntityList);
                try {
                    commRedNotificationService.applyAddRedNotification(applyProInvoiceRedNotificationDTO);
                } catch (Exception e) {
                    log.error("发起红字信息申请失败,预制发票id:{}", preInvoiceEntity.getId(), e);
                }
            }
        }
        return R.ok(null, "红字信息表申请请求已发出");
    }

    @Transactional(rollbackFor = Exception.class)
    public R applyRedNotification(Long preInvoiceId) {
        TXfPreInvoiceEntity preInvoiceEntity = tXfPreInvoiceDao.selectById(preInvoiceId);
        Asserts.isNull(preInvoiceEntity, "预制发票不存在");
        Asserts.isFalse(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode().equals(preInvoiceEntity.getPreInvoiceStatus()), "预制发票红字信息表申请中/已申请，无法再次申请");

        LambdaQueryWrapper<TXfPreInvoiceItemEntity> preInvoiceItemQuery = Wrappers.lambdaQuery(TXfPreInvoiceItemEntity.class).eq(TXfPreInvoiceItemEntity::getPreInvoiceId, preInvoiceId);
        List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = tXfPreInvoiceItemDao.selectList(preInvoiceItemQuery);
        Asserts.isEmpty(tXfPreInvoiceItemEntities, "预制发票明细不存在");

        PreInvoiceDTO applyProInvoiceRedNotificationDTO = new PreInvoiceDTO();
        applyProInvoiceRedNotificationDTO.setTXfPreInvoiceEntity(preInvoiceEntity);
        applyProInvoiceRedNotificationDTO.setTXfPreInvoiceItemEntityList(tXfPreInvoiceItemEntities);
        try {
            commRedNotificationService.applyAddRedNotification(applyProInvoiceRedNotificationDTO);
        } catch (Exception e) {
            log.error("发起红字信息申请失败,预制发票id:{}", preInvoiceEntity.getId(), e);
            return R.fail("申请提交失败，请重试");
        }
        return R.ok(null, "红字信息表申请中，请等待，稍后请刷新页面查看结果");
    }

    @Transactional(rollbackFor = Exception.class)
    public void reFixTaxCode(String settlementNo) {
        boolean success = true;
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        for (TXfSettlementItemEntity tmp : tXfSettlementItemEntities) {
            tmp = deductService.fixTaxCode(tmp);
            Integer tmpStatus = tmp.getItemFlag();
            if (StringUtils.isNotEmpty(tmp.getGoodsTaxNo())) {
                if (tmp.getUnitPrice().multiply(tmp.getQuantity()).setScale(2, RoundingMode.HALF_UP).compareTo(tmp.getAmountWithoutTax()) == 0) {
                    tmp.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
                } else {
                    tmp.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_CONFIRM_AMOUNT.getCode());
                }
            } else {
                success = false;
                tmp.setItemFlag(TXfSettlementItemFlagEnum.WAIT_MATCH_TAX_CODE.getCode());
            }
            if (tmpStatus != tmp.getItemFlag()) {
                tmp = deductService.checkItemName(tmp);
                tXfSettlementItemDao.updateById(tmp);
            }
        }
        List<TXfSettlementItemEntity> fixAmountList = tXfSettlementItemEntities.stream().filter(x -> x.getUnitPrice().multiply(x.getQuantity()).setScale(2, RoundingMode.HALF_UP).compareTo(x.getAmountWithoutTax()) != 0).collect(Collectors.toList());
        /**
         * .如果税编补充完成，判断结算单下一步专题
         */
        if (success) {
            TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0L, settlementNo, null);
            if (CollectionUtils.isEmpty(fixAmountList) && !TXfDeductionBusinessTypeEnum.AGREEMENT_BILL
                    .getValue().equals(tXfSettlementEntity.getBusinessType())) {
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
            } else {
                tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_CONFIRM.getCode());
            }
            tXfSettlementEntity.setUpdateTime(new Date());
            tXfSettlementDao.updateById(tXfSettlementEntity);
        }
    }


    /**
     * .重新修复单价和数量，并且修改结算单的状态变更为待拆票
     *
     * @param settlementNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void reCalculation(String settlementNo) {
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        List<TXfSettlementItemEntity> fixAmountList = tXfSettlementItemEntities.stream().filter(x -> x.getUnitPrice().multiply(x.getQuantity()).setScale(2, RoundingMode.HALF_UP).compareTo(x.getAmountWithoutTax()) != 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(fixAmountList)) {
            for (TXfSettlementItemEntity tXfSettlementItemEntity : fixAmountList) {
                tXfSettlementItemEntity.setQuantity(BigDecimal.ZERO);
                if (tXfSettlementItemEntity.getUnitPrice().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal quantity = tXfSettlementItemEntity.getAmountWithoutTax().divide(tXfSettlementItemEntity.getUnitPrice(), 6, RoundingMode.HALF_UP);
                    tXfSettlementItemEntity.setQuantity(quantity);
                }
                tXfSettlementItemEntity.setItemFlag(TXfSettlementItemFlagEnum.NORMAL.getCode());
                tXfSettlementItemDao.updateById(tXfSettlementItemEntity);
            }
        }
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0L, settlementNo, null);
        tXfSettlementEntity.setSettlementStatus(TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode());
        tXfSettlementEntity.setUpdateTime(new Date());
        tXfSettlementDao.updateById(tXfSettlementEntity);
    }

    /**
     * .调用拆票服务
     *
     * @param createPreInvoiceParam
     * @param tXfSettlementEntity
     * @return
     */
    public List<SplitPreInvoiceInfo> doSplit(CreatePreInvoiceParam createPreInvoiceParam, TXfSettlementEntity tXfSettlementEntity, TAcOrgEntity tAcOrgEntity) {
        // 默认航信单盘
        Integer taxDeviceType = Optional.ofNullable(tAcOrgEntity)
                .map(entity -> Optional.ofNullable(entity.getTaxDeviceType()).orElse(TaxDeviceTypeEnum.HX_DEVICE.code()))
                .orElse(TaxDeviceTypeEnum.HX_DEVICE.code());
        log.info("@@结算单ID:{},SettlementNo:{}, taxDeviceType :{}", tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo(), taxDeviceType);
        String key = "wapp-enhance:settlement-split:" + tXfSettlementEntity.getId() + ":invoice-type-" + createPreInvoiceParam.getBillInfo().getInvoiceType();
        final List<SplitPreInvoiceInfo> result = lockClient.tryLock(key, () -> {
            String post = "";
            JSONObject res = null;
            //WALMART-2097
            for(int i = 0;i<2;i++){
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("tenantId", tenantId);
                    headers.add("Authentication", authentication);
                    headers.add("accept", "application/json");
                    headers.add("Accept-Encoding", "deflate");
                    headers.add("Content-Type", "application/json");
                    headers.add("x-userinfo", "tenant-gateway.41-tcenter-prod");
                    headers.add("uiaSign", sign);
                    headers.add("action", splitInvoice);
                    headers.add("serialNo", tXfSettlementEntity.getSettlementNo());
                    headers.add("rpcType", "http");
                    headers.add("appId", "walmart");
                    headers.add("taxDeviceType", String.valueOf(taxDeviceType));
                    // headers.add("Content-Encoding", "gzip");
                    HttpEntity<CreatePreInvoiceParam> entity = new HttpEntity<>(createPreInvoiceParam, headers);
                    ResponseEntity<String> rs = null;
                    String paramstr = JSON.toJSONString(createPreInvoiceParam);
                    log.info("结算单[{}]拆票报文入参:[{}]", tXfSettlementEntity.getSettlementNo(), paramstr);
                    log.info("结算单[{}]拆票报文大小(length:{})(byte:{})", tXfSettlementEntity.getSettlementNo(), paramstr.length(), paramstr.getBytes(StandardCharsets.UTF_8).length);
                    //大于1M gzip压缩
                    if (paramstr.getBytes(StandardCharsets.UTF_8).length > ONE_MB_SIZE) {
                        log.info("@@结算单ID:{},SettlementNo:{},body len:{}", tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo(), paramstr.length() / 1024);
                        rs = RestTemplateSingle.getGzipInstance().postForEntity(janusPath, entity, String.class);
                    } else {
                        rs = restTemplate.postForEntity(janusPath, entity, String.class);
                    }
                    post = rs.getBody();
                    res = JSONObject.parseObject(post);
                    log.info("@@结算单ID:{} ,SettlementNo:{},拆票出参:{}", tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo(), post);
                    if (!"BSCTZZ0001".equals(res.get("code")) || "[]".equals(res.get("result"))) {
                        log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                        // 保存拆票失败原因
                        String desc = "拆票失败+" + res.get("message");
                        saveSplitInvoiceFailRemark(tXfSettlementEntity.getSettlementNo(), desc);
                        throw new RuntimeException(desc);
                    }
                    //正常请求一次跳出循环
                    break;
                } catch (RestClientResponseException e) {
                    res = JSON.parseObject(e.getResponseBodyAsString());
                    if (!"BSCTZZ0001".equals(res.get("code")) || "[]".equals(res.get("result"))) {
                        log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                        String desc = "拆票失败+" + res.get("message");
                        saveSplitInvoiceFailRemark(tXfSettlementEntity.getSettlementNo(), desc);
                        throw new RuntimeException(desc);
                    }
                } catch (Exception e) {
                    log.error("结算单：{} 拆票失败，结果：{}", tXfSettlementEntity.getSettlementNo(), post);
                    String desc = "拆票失败+" + e.getMessage();
                    if(i!=1){
                        Thread.sleep(1000L);
                        log.info("调用拆票失败，进行二次重试,结算单ID:{} ,SettlementNo:{}",tXfSettlementEntity.getId(), tXfSettlementEntity.getSettlementNo());
                        continue;
                    }
                    throw new RuntimeException(desc);
                }
            }

            return JSON.parseArray(res.getString("result"), SplitPreInvoiceInfo.class);
        }, -1, 1);
        if (result != null) {
            return result;
        }

        // 添加业务单日志履历-拆票异常
        operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.getTXfSettlementStatusEnum(tXfSettlementEntity.getSettlementStatus()), OperateLogEnum.SETTLEMENT_SPLIT_PRE_INVOICE_FAILED, "", 0L, "系统");
        throw new EnhanceRuntimeException("该单据正在拆票中，如需重拆请稍后重试该操作");
    }


    /**
     * .保存预制发票
     *
     * @param splitPreInvoiceInfos 拆票信息
     * @param tXfSettlementEntity  结算单信息
     * @param redNotificationApply true-即刻申请红字信息表 false-暂不申请
     */
    @Transactional(rollbackFor = Exception.class)
    public List<PreInvoiceDTO> savePreInvoiceInfo(List<SplitPreInvoiceInfo> splitPreInvoiceInfos, TXfSettlementEntity tXfSettlementEntity, boolean redNotificationApply) {
        // check 拆票失败
        Date date = new Date();
        List<PreInvoiceDTO> preInvoiceDTOS = new ArrayList<>();
        List<TXfPreBillDetailEntity> preBillDetailEntityList = Lists.newArrayList();
        List<DeductRedNotificationEvent.DeductRedNotificationModel> deductRedNotificationModels = new ArrayList<>();
        for (SplitPreInvoiceInfo splitPreInvoiceInfo : splitPreInvoiceInfos) {
            TXfPreInvoiceEntity tXfPreInvoiceEntity = new TXfPreInvoiceEntity();
            List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = new ArrayList<>();
            BeanUtil.copyProperties(splitPreInvoiceInfo.getPreInvoiceMain(), tXfPreInvoiceEntity);
            if (StringUtils.equals(splitPreInvoiceInfo.getPreInvoiceMain().getSpecialInvoiceFlag(), "2")) {
                tXfPreInvoiceEntity.setIsOil(1);
            } else {
                tXfPreInvoiceEntity.setIsOil(0);
            }
            tXfPreInvoiceEntity.setSettlementType(tXfSettlementEntity.getSettlementType());
            tXfPreInvoiceEntity.setSettlementId(tXfSettlementEntity.getId());
            tXfPreInvoiceEntity.setInvoiceCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setInvoiceNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setCheckCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setPaperDrewDate(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setGoodsListFlag(splitPreInvoiceInfo.getPreInvoiceMain().getSaleListFileFlag().intValue());
            tXfPreInvoiceEntity.setCreateTime(date);
            tXfPreInvoiceEntity.setUpdateTime(date);
            tXfPreInvoiceEntity.setCreateUserId(0L);
            tXfPreInvoiceEntity.setUpdateUserId(0L);
            tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
            tXfPreInvoiceEntity.setMachineCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceCode(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginInvoiceType(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setOriginPaperDrewDate(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setRedNotificationNo(StringUtils.EMPTY);
            tXfPreInvoiceEntity.setId(idSequence.nextId());
            tXfPreInvoiceEntity.setSettlementNo(tXfSettlementEntity.getSettlementNo());
            //发票类型转换
            InvoiceTypeEnum invoiceTypeEnum = InvoiceTypeEnum.getByXfValue(splitPreInvoiceInfo.getPreInvoiceMain().getInvoiceType());
            if (invoiceTypeEnum != null) {
                tXfPreInvoiceEntity.setInvoiceType(invoiceTypeEnum.getValue());
            }
            // 如果是成品油，则补充原发票号码代码,  匹配关系没有原发票号码代码，需要加字段，明天就发布了，先从底账表查出来吧
            if (Optional.ofNullable(tXfPreInvoiceEntity.getIsOil()).orElse(0) == 1) {
                List<TXfBillDeductInvoiceEntity> list = new LambdaQueryChainWrapper<>(tXfBillDeductInvoiceDao)
                        .eq(TXfBillDeductInvoiceEntity::getBusinessNo, tXfSettlementEntity.getSettlementNo())
                        .eq(TXfBillDeductInvoiceEntity::getStatus, "0")
                        .list();
                if (CollectionUtils.isNotEmpty(list)) {
                    TXfBillDeductInvoiceEntity originInvoice = list.get(0);
                    tXfPreInvoiceEntity.setOriginInvoiceCode(originInvoice.getInvoiceCode());
                    tXfPreInvoiceEntity.setOriginInvoiceNo(originInvoice.getInvoiceNo());
                    new LambdaQueryChainWrapper<>(tDxRecordInvoiceDao)
                            .eq(TDxRecordInvoiceEntity::getUuid, originInvoice.getInvoiceCode() + originInvoice.getInvoiceNo())
                            .oneOpt().ifPresent(it -> {
                                tXfPreInvoiceEntity.setOriginInvoiceType(it.getInvoiceType());
                                tXfPreInvoiceEntity.setOriginPaperDrewDate(new SimpleDateFormat("yyyyMMdd").format(it.getInvoiceDate()));
                            });
                }
            }

            //生成预制发票明细
            for (PreInvoiceItem preInvoiceItem : splitPreInvoiceInfo.getPreInvoiceItems()) {
                TXfPreInvoiceItemEntity tXfPreInvoiceItemEntity = new TXfPreInvoiceItemEntity();
                BeanUtil.copyProperties(preInvoiceItem, tXfPreInvoiceItemEntity);
                tXfPreInvoiceItemEntity.setId(idSequence.nextId());
                tXfPreInvoiceItemEntity.setPreInvoiceId(tXfPreInvoiceEntity.getId());
                //如果是成品油单价、数量设置为0
                if (Optional.ofNullable(tXfPreInvoiceEntity.getIsOil()).orElse(0) == 1) {
                    tXfPreInvoiceItemEntity.setUnitPrice(BigDecimal.ZERO);
                    tXfPreInvoiceItemEntity.setQuantity(BigDecimal.ZERO);
                }
                if (tXfPreInvoiceItemEntity.getUnitPrice().compareTo(BigDecimal.ZERO) == 0 || tXfPreInvoiceItemEntity.getQuantity().compareTo(BigDecimal.ZERO) == 0) {
                    tXfPreInvoiceItemEntity.setUnitPrice(null);
                    tXfPreInvoiceItemEntity.setQuantity(null);
                    tXfPreInvoiceItemEntity.setQuantityUnit("");
                }
                tXfPreInvoiceItemEntity.setCreateTime(new Date());
                tXfPreInvoiceItemEntity.setUpdateTime(tXfPreInvoiceItemEntity.getCreateTime());
                tXfPreInvoiceItemDao.insert(tXfPreInvoiceItemEntity);
                tXfPreInvoiceItemEntities.add(tXfPreInvoiceItemEntity);
                // 预制发票明细同结算单明细关联关系
                TXfPreBillDetailEntity preBillDetailEntity = new TXfPreBillDetailEntity();
                BeanUtil.copyProperties(tXfPreInvoiceItemEntity, preBillDetailEntity);
                preBillDetailEntity.setId(idSequence.nextId());
                preBillDetailEntity.setPreInvoiceItemId(tXfPreInvoiceItemEntity.getId());
                preBillDetailEntity.setSettlementId(tXfSettlementEntity.getId());
                preBillDetailEntity.setSettlementItemId(CommonUtil.toLong(preInvoiceItem.getSalesbillItemId()));
                preBillDetailEntity.setSettlementNo(preInvoiceItem.getSalesbillNo());
                preBillDetailEntity.setSettlementItemNo(preInvoiceItem.getSalesbillItemNo());
                preBillDetailEntityList.add(preBillDetailEntity);
            }

            // 发送状态更新消息-预制发票新建
            DeductRedNotificationEvent.DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationEvent.DeductRedNotificationModel();
            deductRedNotificationModel.setPreInvoiceId(tXfPreInvoiceEntity.getId());
            deductRedNotificationModel.setApplyRequired(tXfPreInvoiceEntity.getTaxRate().compareTo(BigDecimal.ZERO) != 0);
            deductRedNotificationModels.add(deductRedNotificationModel);
            /**
             * 0税率 不申请红字信息单
             */
            if (tXfPreInvoiceEntity.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
                tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
                tXfPreInvoiceDao.insert(tXfPreInvoiceEntity);
                continue;
            } else {
                if (redNotificationApply) {
                    tXfPreInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.APPLY_RED_NOTIFICATION_ING.getCode());
                }
                tXfPreInvoiceDao.insert(tXfPreInvoiceEntity);
            }
            PreInvoiceDTO applyProInvoiceRedNotificationDTO = new PreInvoiceDTO();
            applyProInvoiceRedNotificationDTO.setTXfPreInvoiceEntity(tXfPreInvoiceEntity);
            applyProInvoiceRedNotificationDTO.setTXfPreInvoiceItemEntityList(tXfPreInvoiceItemEntities);
            preInvoiceDTOS.add(applyProInvoiceRedNotificationDTO);

        }
        // 保存关联关系
        preBillDetailService.saveBatch(preBillDetailEntityList);

        TXfSettlementEntity tmp = new TXfSettlementEntity();
        tmp.setId(tXfSettlementEntity.getId());
        tmp.setSettlementStatus(TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        tmp.setUpdateTime(new Date());
        tXfSettlementDao.updateById(tmp);
        // 添加业务单日志履历
        operateLogService.addDeductLog(tXfSettlementEntity.getSettlementNo(), tXfSettlementEntity.getBusinessType(), TXfSettlementStatusEnum.NO_UPLOAD_RED_INVOICE, OperateLogEnum.SETTLEMENT_SPLIT_PRE_INVOICE_SUCCESS, "", 0L, "系统");

        deductRedNotificationModels.forEach(x -> commonMessageService.sendMessage(DeductRedNotificationEventEnum.PRE_INVOICE_CREATED, x));
        return preInvoiceDTOS;
    }

    /**
     * .拼接拆票请求
     *
     * @param tXfSettlementEntity
     * @param tXfSettlementItemEntities
     * @param tAcOrgEntity
     * @return
     */
    private CreatePreInvoiceParam assembleParam(TXfSettlementEntity tXfSettlementEntity, List<TXfSettlementItemEntity> tXfSettlementItemEntities, TAcOrgEntity tAcOrgEntity) {
        CreatePreInvoiceParam createPreInvoiceParam = new CreatePreInvoiceParam();
        BillInfo billInfo = new BillInfo();
        BeanUtil.copyProperties(tXfSettlementEntity, billInfo);
        billInfo.setPriceMethod(1);
        billInfo.setPurchaserId(0L);
        billInfo.setPurchaserGroupId(0L);
        billInfo.setPurchaserTenantId(0L);

        billInfo.setSellerId(0L);
        billInfo.setSellerTenantId(0L);
        billInfo.setSellerGroupId(0L);
        billInfo.setInvoiceType(ValueEnum.getEnumByValue(InvoiceTypeEnum.class, tXfSettlementEntity.getInvoiceType()).get().getXfValue());
        billInfo.setSalesbillId(String.valueOf(tXfSettlementEntity.getId()));

        List<BillItem> billItems = new ArrayList<>();

        String settlementNo = tXfSettlementEntity.getSettlementNo();
        for (TXfSettlementItemEntity tXfSettlementItemEntity : tXfSettlementItemEntities) {
            BillItem billItem = new BillItem();
            BeanUtils.copyProperties(tXfSettlementItemEntity, billItem);
            //成品油
            if (Objects.equals(Optional.ofNullable(tXfSettlementItemEntity.getIsOil()).orElse(0), 1)) {
                //拆分code, 0=成品油, 1=非成品油-该判断作废，平台逻辑调整
//                billItem.setSplitCode("0");-该判断作废，平台逻辑调整
                billItem.setItemTypeCode("cpy");
                //WALMART-1989 处理生成成品油拆票提示单价为负数问题
                billItem.setQuantity(BigDecimal.ONE.negate());
                billItem.setUnitPrice(billItem.getAmountWithoutTax().abs());
            } else {
                if (tXfSettlementItemEntity.getUnitPrice() == null || tXfSettlementItemEntity.getQuantity() == null) {
                    billItem.setQuantity(BigDecimal.ZERO);
                    billItem.setUnitPrice(BigDecimal.ZERO);
                }
                //billItem.setSplitCode("1");//该判断作废，平台逻辑调整
            }
            billItem.setSalesbillId(String.valueOf(tXfSettlementEntity.getId()));
            billItem.setSalesbillItemId(String.valueOf(tXfSettlementItemEntity.getId()));
            billItem.setSalesbillNo(settlementNo);
            billItem.setSalesbillItemNo(tXfSettlementItemEntity.getSalesbillItemNo());

            billItem.setInnerPrepayAmountTax(BigDecimal.ZERO);
            billItem.setInnerPrepayAmountWithoutTax(BigDecimal.ZERO);
            billItem.setInnerPrepayAmountWithTax(BigDecimal.ZERO);

            billItem.setInnerDiscountTax(BigDecimal.ZERO);
            billItem.setInnerDiscountWithTax(BigDecimal.ZERO);
            billItem.setInnerDiscountWithoutTax(BigDecimal.ZERO);

            billItem.setOutterDiscountTax(BigDecimal.ZERO);
            billItem.setOutterDiscountWithoutTax(BigDecimal.ZERO);
            billItem.setOutterDiscountWithTax(BigDecimal.ZERO);
            String name = tXfSettlementItemEntity.getItemName();
            List<String> list = ItemNameUtils.splitItemName(name);
            if (CollectionUtils.isNotEmpty(list)) {
                billItem.setItemName(list.get(1));
                billItem.setItemShortName(list.get(0));
            }
            billItem.setOutterPrepayAmountTax(BigDecimal.ZERO);
            billItem.setOutterPrepayAmountWithoutTax(BigDecimal.ZERO);
            billItem.setOutterPrepayAmountWithTax(BigDecimal.ZERO);
            billItem.setDeductions(BigDecimal.ZERO);
            billItems.add(billItem);
        }
        billInfo.setBillItems(billItems);
        SplitRule splitRule = null;//2022-08-17
        if (Objects.equals(tXfSettlementEntity.getSettlementType(), TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue())) {
            splitRule = JSONObject.parseObject(Claim_RULE_INFO, SplitRule.class);
        } else {
            splitRule = JSONObject.parseObject(EPD_AGREEMENT_RULE_INFO, SplitRule.class);
        }
        if (Objects.nonNull(tAcOrgEntity) && tAcOrgEntity.getTaxDeviceType() != null && Objects.equals(2, tAcOrgEntity.getTaxDeviceType())) {
            splitRule.setInvoiceMaxErrorAmount(new BigDecimal("0.06"));
        }
        splitRule.setInvoiceLimit(BigDecimal.valueOf((Objects.isNull(tAcOrgEntity) || Objects.isNull(tAcOrgEntity.getQuota())) ? 9999.99 : tAcOrgEntity.getQuota()));
        createPreInvoiceParam.setBillInfo(billInfo);
        createPreInvoiceParam.setRule(splitRule);
        createPreInvoiceParam.setRoutingKey("12");
        return createPreInvoiceParam;
    }

    /**
     * .重新拆票
     *
     * @param settlementNo
     * @param items
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> reSplitPreInvoice(String settlementNo, String sellerNo, List<TXfPreInvoiceItemEntity> items) {
        TXfSettlementEntity tXfSettlementEntity = tXfSettlementDao.querySettlementByNo(0L, settlementNo, null);
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal amountWithTax = BigDecimal.ZERO;
        BigDecimal amountWithOutTax = BigDecimal.ZERO;
        List<TXfSettlementItemEntity> list = new ArrayList<>();
        final List<Long> preInvoiceIds = items.stream().map(TXfPreInvoiceItemEntity::getPreInvoiceId).distinct().collect(Collectors.toList());
        final Map<Long, Integer> oilMap = isOilByPreInvoiceId(preInvoiceIds);
        List<TXfPreBillDetailEntity> details = preBillDetailService.getDetails(preInvoiceIds);
        // 预制发票明细id ==  结算单明细id
        Map<Long, Long> itemIdMap = details.parallelStream().collect(Collectors.toMap(TXfPreBillDetailEntity::getPreInvoiceItemId, TXfPreBillDetailEntity::getSettlementItemId));
        for (TXfPreInvoiceItemEntity tXfPreInvoiceItemEntity : items) {
            TXfSettlementItemEntity tXfSettlementItemEntity = new TXfSettlementItemEntity();
            taxAmount = taxAmount.add(tXfPreInvoiceItemEntity.getTaxAmount());
            amountWithTax = amountWithTax.add(tXfPreInvoiceItemEntity.getAmountWithTax());
            amountWithOutTax = amountWithOutTax.add(tXfPreInvoiceItemEntity.getAmountWithoutTax());
            BeanUtils.copyProperties(tXfPreInvoiceItemEntity, tXfSettlementItemEntity);
            tXfSettlementItemEntity.setItemName(tXfPreInvoiceItemEntity.getCargoName());
            //WALMART-2292 生成结算单明细，税编简称携带空格处理
            tXfSettlementItemEntity.setItemShortName(tXfPreInvoiceItemEntity.getCargoName().trim());
            tXfSettlementItemEntity.setItemCode(tXfPreInvoiceItemEntity.getCargoCode());
            tXfSettlementItemEntity.setQuantityUnit(tXfPreInvoiceItemEntity.getQuantityUnit());
            // 获取结算单明细id
            tXfSettlementItemEntity.setId(Optional.ofNullable(itemIdMap.get(tXfPreInvoiceItemEntity.getId())).orElse(0L));
            final Integer isOil = Optional.ofNullable(oilMap.get(tXfPreInvoiceItemEntity.getPreInvoiceId())).orElse(0);
            tXfSettlementItemEntity.setIsOil(isOil);
            list.add(tXfSettlementItemEntity);
        }
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        tXfSettlementEntity.setTaxAmount(taxAmount);
        tXfSettlementEntity.setAmountWithoutTax(amountWithOutTax);
        tXfSettlementEntity.setAmountWithTax(amountWithTax);
        //拼接拆票请求
        CreatePreInvoiceParam createPreInvoiceParam = assembleParam(tXfSettlementEntity, list, tAcOrgEntity);
        //拆票及保存预制发票
        return splitAndSaveInvoice(createPreInvoiceParam, tXfSettlementEntity, tAcOrgEntity, true);
    }

    public void applyDestroyPreInvoiceAndRedNotification(String invoiceNo, String invoiceCode, String remark) {
        LambdaQueryWrapper<TXfPreInvoiceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(TXfPreInvoiceEntity::getId).eq(TXfPreInvoiceEntity::getInvoiceNo, invoiceNo)
                .eq(TXfPreInvoiceEntity::getInvoiceCode, invoiceCode);
        final TXfPreInvoiceEntity one = super.getOne(wrapper);
        commSettlementService.applyDestroyPreInvoiceAndRedNotification(one.getId(), remark);
    }

    private Map<Long, Integer> isOilByPreInvoiceId(List<Long> preInvoiceIds) {
        if (CollectionUtils.isNotEmpty(preInvoiceIds)) {
            final LambdaQueryWrapper<TXfPreInvoiceEntity> in = Wrappers.lambdaQuery(TXfPreInvoiceEntity.class).select(TXfPreInvoiceEntity::getId, TXfPreInvoiceEntity::getIsOil).in(TXfPreInvoiceEntity::getId, preInvoiceIds);
            return Optional.ofNullable(this.list(in)).map(x -> x.stream().collect(Collectors.toMap(TXfPreInvoiceEntity::getId, TXfPreInvoiceEntity::getIsOil)))
                    .orElse(Collections.emptyMap())
                    ;
        }
        return Collections.emptyMap();
    }

    private void saveSplitInvoiceFailRemark(String settlementNo, String remark) {
        TXfSettlementEntity tmp = new TXfSettlementEntity();
        tmp.setRemark(remark);
        tmp.setUpdateTime(new Date());

        QueryWrapper<TXfSettlementEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TXfSettlementEntity.SETTLEMENT_NO, settlementNo);

        tXfSettlementDao.update(tmp, queryWrapper);
    }

    /**
     * .根据0税率和非0税率重建拆票请求体
     *
     * @param createPreInvoiceParam
     * @return
     */
    private List<CreatePreInvoiceParam> rebuildCreatePreInvoiceParam(CreatePreInvoiceParam createPreInvoiceParam) {
        String createPreInvoiceParamJson = JSON.toJSONString(createPreInvoiceParam);
        List<CreatePreInvoiceParam> list = new ArrayList<>();
        List<BillItem> zeroTaxRateBillItemList = new ArrayList<>();
        List<BillItem> noZeroTaxRateBillItemList = new ArrayList<>();
        List<BillItem> billItemList = createPreInvoiceParam.getBillInfo().getBillItems();
        for (BillItem billItem : billItemList) {
            if (billItem.getTaxRate().compareTo(BigDecimal.ZERO) == 0) {
                zeroTaxRateBillItemList.add(billItem);
            } else {
                noZeroTaxRateBillItemList.add(billItem);
            }
        }
        if (CollectionUtils.isNotEmpty(zeroTaxRateBillItemList)) {
            CreatePreInvoiceParam newCreatePreInvoiceParam = JSON.parseObject(createPreInvoiceParamJson, CreatePreInvoiceParam.class);
            BillInfo billInfo = newCreatePreInvoiceParam.getBillInfo();
            billInfo.setInvoiceType(InvoiceTypeEnum.GENERAL_INVOICE.getXfValue());
            billInfo.setBillItems(zeroTaxRateBillItemList);
            newCreatePreInvoiceParam.setBillInfo(billInfo);
            list.add(newCreatePreInvoiceParam);
        }
        if (CollectionUtils.isNotEmpty(noZeroTaxRateBillItemList)) {
            CreatePreInvoiceParam newCreatePreInvoiceParam = JSON.parseObject(createPreInvoiceParamJson, CreatePreInvoiceParam.class);
            BillInfo billInfo = newCreatePreInvoiceParam.getBillInfo();
            billInfo.setInvoiceType(InvoiceTypeEnum.SPECIAL_INVOICE.getXfValue());
            billInfo.setBillItems(noZeroTaxRateBillItemList);
            newCreatePreInvoiceParam.setBillInfo(billInfo);
            list.add(newCreatePreInvoiceParam);
        }
        return list;
    }

    /**
     * @Description 拆票及保存预制发票
     * @Author pengtao
     * @return
    **/
    private List<SplitPreInvoiceInfo> splitAndSaveInvoice(CreatePreInvoiceParam createPreInvoiceParam, TXfSettlementEntity tXfSettlementEntity, TAcOrgEntity tAcOrgEntity, boolean redNotificationApply) {
        // 根据0税率和非0税率重建拆票请求体
        List<SplitPreInvoiceInfo> newSplitPreInvoiceInfoList = new ArrayList<>();
        //重建拆票请求体
        List<CreatePreInvoiceParam> createPreInvoiceParamList = rebuildCreatePreInvoiceParam(createPreInvoiceParam);
        for (CreatePreInvoiceParam newCreatePreInvoiceParam : createPreInvoiceParamList) {
            //调用拆票服务
            List<SplitPreInvoiceInfo> splitPreInvoiceInfoList = doSplit(newCreatePreInvoiceParam, tXfSettlementEntity, tAcOrgEntity);
            if (CollectionUtils.isNotEmpty(splitPreInvoiceInfoList)) {
                newSplitPreInvoiceInfoList.addAll(splitPreInvoiceInfoList);
            }
        }
        // 保存预制发票
        List<PreInvoiceDTO> preInvoiceList = savePreInvoiceInfo(newSplitPreInvoiceInfoList, tXfSettlementEntity, redNotificationApply);
        if (!redNotificationApply) {
            // true - 申请红字信息表 false - 不申请
            return newSplitPreInvoiceInfoList;
        }
        for (PreInvoiceDTO preInvoiceDTO : preInvoiceList) {
            try {
                //生成红字信息表
                commRedNotificationService.applyAddRedNotification(preInvoiceDTO);
            } catch (Exception e) {
                log.error("发起红字信息申请失败,预制发票id:" + preInvoiceDTO.getTXfPreInvoiceEntity().getId(), e);
            }
        }
        return newSplitPreInvoiceInfoList;
    }

    /**
     * 蓝冲生成新的红字预制发票
     *
     * @param originRedPreInvoice 原预制发票信息
     * @return
     */
    public void makeNewRedPreInvoice(TXfPreInvoiceEntity originRedPreInvoice) {
        Date now = new Date();
        TXfPreInvoiceEntity preInvoiceEntity = JSON.parseObject(JSON.toJSONString(originRedPreInvoice), TXfPreInvoiceEntity.class);
        preInvoiceEntity.setId(idSequence.nextId());
        preInvoiceEntity.setInvoiceCode("");
        preInvoiceEntity.setInvoiceNo("");
        preInvoiceEntity.setMachineCode("");
        preInvoiceEntity.setPaperDrewDate("");
        preInvoiceEntity.setCheckCode("");
        preInvoiceEntity.setRedNotificationNo("-");
        preInvoiceEntity.setCreateTime(now);
        preInvoiceEntity.setUpdateTime(now);
        if (BigDecimal.ZERO.compareTo(preInvoiceEntity.getTaxRate()) == 0) {
            preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_UPLOAD_RED_INVOICE.getCode());
        } else {
            // 置为[待申请状态]，不自动发起红字申请
            preInvoiceEntity.setPreInvoiceStatus(TXfPreInvoiceStatusEnum.NO_APPLY_RED_NOTIFICATION.getCode());
        }

        // 明细
        List<TXfPreInvoiceItemEntity> tXfPreInvoiceItemEntities = tXfPreInvoiceItemDao.selectList(Wrappers.lambdaQuery(TXfPreInvoiceItemEntity.class)
                .eq(TXfPreInvoiceItemEntity::getPreInvoiceId, originRedPreInvoice.getId()));
        // 关联关系
        List<TXfPreBillDetailEntity> preBillDetailList = preBillDetailService.getDetails(originRedPreInvoice.getId());
        Map<Long, TXfPreBillDetailEntity> preBillDetailMap = preBillDetailList.stream().collect(Collectors.toMap(TXfPreBillDetailEntity::getPreInvoiceItemId, a -> a));

        List<TXfPreInvoiceItemEntity> newItemList = Lists.newArrayList();
        List<TXfPreBillDetailEntity> newDetailList = Lists.newArrayList();
        for (TXfPreInvoiceItemEntity item : tXfPreInvoiceItemEntities) {
            TXfPreInvoiceItemEntity newPreInvoiceItem = JSON.parseObject(JSON.toJSONString(item), TXfPreInvoiceItemEntity.class);
            newPreInvoiceItem.setId(idSequence.nextId());
            newPreInvoiceItem.setPreInvoiceId(preInvoiceEntity.getId());
            newPreInvoiceItem.setCreateTime(now);
            newPreInvoiceItem.setUpdateTime(now);
            newItemList.add(newPreInvoiceItem);

            TXfPreBillDetailEntity tXfPreBillDetailEntity = preBillDetailMap.get(item.getId());
            Optional.ofNullable(tXfPreBillDetailEntity)
                    .ifPresent(preBillDetail -> {
                        TXfPreBillDetailEntity newPreBillDetail = JSON.parseObject(JSON.toJSONString(preBillDetail), TXfPreBillDetailEntity.class);
                        newPreBillDetail.setId(idSequence.nextId());
                        newPreBillDetail.setPreInvoiceId(preInvoiceEntity.getId());
                        newPreBillDetail.setPreInvoiceItemId(newPreInvoiceItem.getId());
                        newDetailList.add(newPreBillDetail);
                    });
        }

        tXfPreInvoiceDao.insert(preInvoiceEntity);
        newItemList.forEach(item -> tXfPreInvoiceItemDao.insert(item));
        if (CollectionUtil.isNotEmpty(newDetailList)) {
            preBillDetailService.saveBatch(newDetailList);
        }

        DeductRedNotificationEvent.DeductRedNotificationModel deductRedNotificationModel = new DeductRedNotificationEvent.DeductRedNotificationModel();
        deductRedNotificationModel.setPreInvoiceId(preInvoiceEntity.getId());
        deductRedNotificationModel.setApplyRequired(preInvoiceEntity.getTaxRate().compareTo(BigDecimal.ZERO) != 0);
        commonMessageService.sendMessage(DeductRedNotificationEventEnum.PRE_INVOICE_CREATED, deductRedNotificationModel);
    }

    public void updateItem(String itemNo, String sellerNo, String goodsTaxNo) {
        if (StringUtils.isEmpty(goodsTaxNo)) {
            return;
        }
        tXfPreInvoiceItemDao.updateItem(itemNo, sellerNo, goodsTaxNo);
    }

    /**
     * 根据预制发票ID生成红字预览
     *
     * @param preInvoiceId
     * @return
     */
    public R<Map<String, String>> preViewRedPdf(Long preInvoiceId) {
        //1.根据预制发票获取主信息和明细信息
        TXfPreInvoiceEntity preInvoiceEntity = tXfPreInvoiceDao.selectById(preInvoiceId);
        if (preInvoiceEntity == null) {
            return R.fail("未获取到预制发票主信息");
        }
        QueryWrapper<TXfPreInvoiceItemEntity> itemEntityQ = new QueryWrapper<>();
        itemEntityQ.in(TXfPreInvoiceItemEntity.PRE_INVOICE_ID, preInvoiceId);
        List<TXfPreInvoiceItemEntity> itemEntityList = tXfPreInvoiceItemDao.selectList(itemEntityQ);
        if (CollectionUtils.isEmpty(itemEntityList)) {
            return R.fail("未获取到预制发票明细");
        }
        //2.请求税件生成预览红字PDF
        RedNotificationGeneratePdfRequest request = new RedNotificationGeneratePdfRequest();
        //头信息
        RequestHead requestHead = new RequestHead();
        requestHead.setDebug(null);
        requestHead.setTenantId(taxWareService.tenantId);
        requestHead.setTenantName(taxWareService.tenantName);
        request.setHead(requestHead);
        //设置serialNo
        request.setSerialNo(String.valueOf(idSequence.nextId()));
        //红字主信息
        RedGeneratePdfInfo redInfo = new RedGeneratePdfInfo();
        redInfo.setApplicant(0);
        redInfo.setDate(DateUtil.format(new Date(), "yyyyMMdd"));
        redInfo.setOriginInvoiceCode("");
        redInfo.setOriginInvoiceNo("");
        //WALMART-2274 成品油才获取原号码代码
        if(Objects.nonNull(preInvoiceEntity.getIsOil())&&preInvoiceEntity.getIsOil()==1){
            //获取原蓝字号码代码
            QueryWrapper<TXfSettlementItemInvoiceDetailEntity> setInvoItemEntityQ = new QueryWrapper<>();
            setInvoItemEntityQ.eq(TXfSettlementItemInvoiceDetailEntity.SETTLEMENT_ID, preInvoiceEntity.getSettlementId());
            List<TXfSettlementItemInvoiceDetailEntity> tXfSetItemInvoDetails= tXfSettlementItemInvoiceDetailDao.selectList(setInvoItemEntityQ);
            if (CollectionUtils.isEmpty(tXfSetItemInvoDetails)) {
                return R.fail("未获取到结算单明细发票明细关系数据");
            }
            redInfo.setOriginInvoiceCode(tXfSetItemInvoDetails.stream().findFirst().get().getInvoiceCode());
            redInfo.setOriginInvoiceNo(tXfSetItemInvoDetails.stream().findFirst().get().getInvoiceNo());
            log.info("根据预制发票ID:{}生成红字预览,成品油获取原号码:{}代码:{}",preInvoiceId,redInfo.getOriginInvoiceNo(),redInfo.getOriginInvoiceCode());
        }

        redInfo.setPurchaseTaxNo(preInvoiceEntity.getPurchaserTaxNo());
        redInfo.setPurchaserName(preInvoiceEntity.getPurchaserName());
        redInfo.setRedNotificationNo("");
        redInfo.setSellerName(preInvoiceEntity.getSellerName());
        redInfo.setSellerTaxNo(preInvoiceEntity.getSellerTaxNo());
        redInfo.setTotalAmountWithoutTax(preInvoiceEntity.getAmountWithoutTax().toPlainString());
        redInfo.setTotalTaxAmount(preInvoiceEntity.getTaxAmount().toPlainString());
        //红字明细信息
        List<RedGeneratePdfDetailInfo> detailInfoList = new ArrayList<>();
        RedGeneratePdfDetailInfo detailInfo;
        for (TXfPreInvoiceItemEntity itemEntity : itemEntityList) {
            detailInfo = new RedGeneratePdfDetailInfo();
            detailInfo.setAmountWithoutTax(itemEntity.getAmountWithoutTax().toPlainString());
            detailInfo.setCargoName(itemEntity.getCargoName());
            if (itemEntity.getQuantity() != null) {
                detailInfo.setQuantity(itemEntity.getQuantity().toPlainString());
            }
            if (itemEntity.getUnitPrice() != null) {
                detailInfo.setUnitPrice(itemEntity.getUnitPrice().toPlainString());
            }
            detailInfo.setTaxAmount(itemEntity.getTaxAmount().toPlainString());
            detailInfo.setTaxRate(itemEntity.getTaxRate().toPlainString());
            detailInfoList.add(detailInfo);
        }

        if (itemEntityList.size() > RedNotificationMainService.MAX_DETAIL_SIZE) {
            RedGeneratePdfDetailInfo merge = merge(itemEntityList);
            redInfo.setDetails(Lists.newArrayList(merge));
        } else {
            redInfo.setDetails(detailInfoList);
        }

        request.setRedInfo(redInfo);
        log.info("税件红字预览请求参数：{}", JSON.toJSONString(request));
        TaxWareResponse taxWareResponse = taxWareService.generatePdf(request);
        log.info("税件红字预览回复内容：{}", JSON.toJSONString(taxWareResponse));
        if (taxWareResponse.getCode() != null && !Objects.equals(taxWareResponse.getCode(), TaxWareCode.SUCCESS)) {
            throw new RRException(taxWareResponse.getMessage());
        }
        TaxWareResponse.ResultDTO result = taxWareResponse.getResult();
        if (result == null && StringUtils.isBlank(result.getPdfUrl())) {
            return R.fail("生成失败");
        }
        Map<String, String> pdfMap = new HashMap<>();
        pdfMap.put("pdfUrl", result.getPdfUrl() + "&cd=inline");
        return R.ok(pdfMap);
    }

    private RedGeneratePdfDetailInfo merge(List<TXfPreInvoiceItemEntity> itemEntityList) {
        BigDecimal sumAmountWithoutTax = BigDecimal.ZERO;
        BigDecimal sumTaxAmount = BigDecimal.ZERO;
        boolean isMixedRate = false;
        BigDecimal taxRate = null;
        for (TXfPreInvoiceItemEntity itemEntity : itemEntityList) {
            sumAmountWithoutTax = sumAmountWithoutTax.add(itemEntity.getAmountWithoutTax());
            sumTaxAmount = sumTaxAmount.add(itemEntity.getTaxAmount());
            if (Objects.isNull(taxRate)) {
                taxRate = itemEntity.getTaxRate();
            } else {
                if (!isMixedRate && !taxRate.equals(itemEntity.getTaxRate())) {
                    isMixedRate = true;
                }
            }
        }

        RedGeneratePdfDetailInfo combineEntity = new RedGeneratePdfDetailInfo();
        combineEntity.setAmountWithoutTax(sumAmountWithoutTax.toPlainString());
        combineEntity.setTaxAmount(sumTaxAmount.toPlainString());
        combineEntity.setCargoName("详见对应正数发票及清单");
        if (!isMixedRate) {
            combineEntity.setTaxRate(taxRate.toPlainString());
        }
        return combineEntity;
    }
}
