package com.xforceplus.wapp.modules.entryaccount.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xforceplus.evat.common.constant.enums.FlowTypeEnum;
import com.xforceplus.evat.common.domain.bms.BmsFeedbackConfig;
import com.xforceplus.evat.common.utils.TaxRateUtils;
import com.xforceplus.wapp.common.enums.InvoiceTypeEnum;
import com.xforceplus.wapp.common.exception.EnhanceRuntimeException;
import com.xforceplus.wapp.common.utils.BeanUtil;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.common.utils.SignUtils;
import com.xforceplus.wapp.enums.AuthStatusEnum;
import com.xforceplus.wapp.enums.customs.InvoiceCheckEnum;
import com.xforceplus.wapp.modules.backfill.service.RecordInvoiceService;
import com.xforceplus.wapp.modules.customs.convert.BillStatusEnum;
import com.xforceplus.wapp.modules.customs.dto.CustomsEntryRequest;
import com.xforceplus.wapp.modules.customs.service.CustomsDetailService;
import com.xforceplus.wapp.modules.customs.service.CustomsService;
import com.xforceplus.wapp.modules.customs.service.TDxCustomsSummonsService;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.EntryAccountResultDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result.BMSResultDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.result.QueryTaxBillResult;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send.SendBMSDTO;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send.SendQueryTaxBill;
import com.xforceplus.wapp.modules.entryaccount.dto.entryAccoount.send.SendTaxBill;
import com.xforceplus.wapp.modules.entryaccount.enums.PushStatusEnum;
import com.xforceplus.wapp.modules.entryaccount.service.EntryAccountService;
import com.xforceplus.wapp.modules.entryaccount.util.SignUtil;
import com.xforceplus.wapp.modules.xforceapi.HttpClientUtils;
import com.xforceplus.wapp.repository.entity.TDxCustomsDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxCustomsEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceDetailEntity;
import com.xforceplus.wapp.repository.entity.TDxRecordInvoiceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: ChenHang
 * @Date: 2023/6/29 10:10
 */
@Service
@Slf4j
public class EntryAccountServiceImpl implements EntryAccountService {

    @Autowired
    public BmsFeedbackConfig bmsFeedbackConfig;

    @Autowired
    private RecordInvoiceService recordInvoiceService;

    @Autowired
    private TDxRecordInvoiceDetailService recordInvoiceDetailService;

    @Autowired
    private CustomsService customsService;

    @Autowired
    private CustomsDetailService customsDetailService;

    @Autowired
    private TDxCustomsSummonsService customsSummonsService;

    @Autowired
    private SummonsRMSService summonsRMSService;

    /**
     * 定时获取BMS海关缴款书信息, 然后匹配, 只有匹配成功推送到BMS
     * @param customsEntity
     */
    @Override
    public void customerToBMS(TDxCustomsEntity customsEntity) throws Exception{
        // 税单号
        String taxDocNo = customsEntity.getCustomsNo();
        // 从BMS获取海关缴款书明细信息
        String taxBillResultJson = this.queryTaxBill(taxDocNo);
        // 值为空则代表接口请求失败
        if (StringUtils.isEmpty(taxBillResultJson)) {
            log.error("调用BMS获取海关缴款书接口失败");
            // 设置海关票比对状态为比对失败
            customsEntity.setBillStatus(BillStatusEnum.BILL_STATUS_N1.getCode());
            customsService.updateById(customsEntity);
            throw new EnhanceRuntimeException("调用BMS获取海关缴款书接口失败");
        }
        BMSResultDTO bmsResultDTO = JSONObject.parseObject(taxBillResultJson, BMSResultDTO.class);
        // 如果从BMS未查询到海关缴款书明细, 则比对失败
        if (CollectionUtils.isEmpty(bmsResultDTO.getResult())) {
            customsEntity.setBillStatus(BillStatusEnum.BILL_STATUS_N1.getCode());
            customsService.updateById(customsEntity);
            throw new EnhanceRuntimeException("BMS无该海关票明细信息");
        }
        if (bmsResultDTO.isSuccess() && !CollectionUtils.isEmpty(bmsResultDTO.getResult())) {
            // BMS返回缴款书明细
            List<QueryTaxBillResult> queryTaxBillResult = JSONObject.parseArray(JSONObject.toJSONString(bmsResultDTO.getResult()), QueryTaxBillResult.class);
            // 底账获取的海关缴款书税款金额(有效税额))
            BigDecimal recordTaxAmt = customsEntity.getEffectiveTaxAmount();
            // 总税额
            BigDecimal bmsDetailTaxAmt = new BigDecimal(0);
            // 完税价格
            ArrayList<TDxCustomsDetailEntity> customsDetails = new ArrayList<>();
            // 从BMS获取的海关缴款书明细
            for (QueryTaxBillResult taxBillResult : queryTaxBillResult) {
                // 判断填发时间是否一致, 即入参的填发时间与主表的开票日期是否为同一天
                if (!StringUtils.equals(customsEntity.getPaperDrewDate(), DateUtils.strToStrDate3(taxBillResult.getTaxDate()))) {
                    log.info("海关缴款书号开票日期:{}, 与BMS获取的填发日期不一致:{}", customsEntity.getPaperDrewDate(), taxBillResult.getTaxDate());
                    // 设置海关票比对状态为比对失败
                    customsEntity.setBillStatus(BillStatusEnum.BILL_STATUS_N1.getCode());
                    customsService.updateById(customsEntity);
                    throw new EnhanceRuntimeException(MessageFormat.format("与BMS获取的填发日期不一致!, WAPP填发日期：{0}, 获取BMS的填发日期：{1}", customsEntity.getPaperDrewDate(), DateUtils.strToStrDate3(taxBillResult.getTaxDate())));
                }

                BigDecimal taxAmt = taxBillResult.getTaxAmt();
                // 将从BMS获取的税款金额全部相加
                bmsDetailTaxAmt = bmsDetailTaxAmt.add(taxAmt);
                // 存储海关缴款书明细对象
                TDxCustomsDetailEntity customsDetail = TDxCustomsDetailEntity.builder()
                        .customsNo(taxDocNo)
                        .payeeSubject(taxBillResult.getAccountDesc())
                        .contractNo(taxBillResult.getContractNo())
                        .customsDocNo(taxBillResult.getCustomsDocNo())
                        .dutiablePrice(taxBillResult.getDutiablePrice())
                        .billId(taxBillResult.getId())
                        .materialDesc(taxBillResult.getMaterialDesc())
                        .materialId(taxBillResult.getMaterialId())
                        .taxAmount(taxBillResult.getTaxAmt())
                        .paperDrewDate(taxBillResult.getTaxDate())
                        .companyTaxNo(taxBillResult.getTaxNo())
                        .taxRate(taxBillResult.getTaxRate())
                        .createTime(new Date())
                        .updateTime(new Date())
                        .build();
                customsDetails.add(customsDetail);
                customsEntity.setContractNo(taxBillResult.getContractNo());
                customsEntity.setCustomsDocNo(taxBillResult.getCustomsDocNo());
            }

            customsEntity.setTaxAmountDifference(recordTaxAmt.subtract(bmsDetailTaxAmt));
            // 判断底账的税款金额与BMS获取海关缴款书明细税款金额合计是否相等
            if (recordTaxAmt.compareTo(bmsDetailTaxAmt) != 0) {
                log.info("海关缴款书号:{}, 与BMS获取的税款金额总计不一致", taxDocNo);
                // 设置海关票比对状态为比对失败
                customsEntity.setBillStatus(BillStatusEnum.BILL_STATUS_N1.getCode());
                customsService.updateById(customsEntity);
                throw new EnhanceRuntimeException("税款金额总计不一致");
            }
            String taxBillFeedbackResultJson = null;
            // 金额比对一致推送BMS, 多条明细推送多次
            boolean flag = true;
            for (QueryTaxBillResult taxBillResult : queryTaxBillResult) {
                taxBillFeedbackResultJson = this.taxBillFeedback(taxDocNo, taxBillResult.getId());
                if (StringUtils.isEmpty(taxBillFeedbackResultJson)) {
                    log.info("调用BMS接受海关缴款书接口失败");
                    // 设置海关缴款书推送BMS状态为推送失败
                    customsEntity.setPushBmsStatus(PushStatusEnum.fail.getMatchState());
                    customsService.updateById(customsEntity);
                    throw new EnhanceRuntimeException("调用BMS接受海关缴款书接口失败");
                }
                BMSResultDTO taxBillFeedbackDTO = JSONObject.parseObject(taxBillFeedbackResultJson, BMSResultDTO.class);
                if (taxBillFeedbackDTO.isFail()) {
                    flag = false;
                }
            }
            // 所有明细推送成功才会更新
            if (flag) {
                // 设置海关票推送bms状态及时间
                customsEntity.setPushBmsStatus(PushStatusEnum.SUCCESS.getMatchState());
                customsEntity.setPushBmsTime(new Date());
                customsEntity.setBillStatus(BillStatusEnum.BILL_STATUS_1.getCode());
                customsService.updateById(customsEntity);
                // 保存海关缴款书明细
                customsDetailService.saveOrUpdateCustomsDetail(taxDocNo, customsDetails);
            }

        }

        // todo
    }

    /**
     * 海关缴款书单条主动获取明细并比对结果
     * @param id
     */
    @Override
    public void activeCustomerToBMS(String id) throws Exception {
        TDxCustomsEntity tDxCustomsEntity = customsService.getById(id);
        this.customerToBMS(tDxCustomsEntity);
    }

    @Override
    public Map<String, List<EntryAccountResultDTO>> entryAccount(List<EntryAccountDTO> entryAccountDTOList) {
        Map<String, List<EntryAccountResultDTO>> map = new ConcurrentHashMap<>();
        List<EntryAccountResultDTO> failList = new ArrayList<>();
        List<EntryAccountResultDTO> successList = new ArrayList<>();
        for (EntryAccountDTO entryAccountDTO : entryAccountDTOList) {
            // 参数校验 非商和商品公共参数非空校验
            this.checkQueryParameter(entryAccountDTO, failList);
            if (!CollectionUtils.isEmpty(failList)) {
                continue;
            }
            String businessSource = entryAccountDTO.getBusinessSource();
            if (StringUtils.isEmpty(businessSource)) {
                failList.add(createEntryAccountResultDTO(entryAccountDTO, "系统来系统来源不能为空!"));
                continue;
            }
            switch (businessSource) {
                // 非商发票入账
                case "S001" :
                    this.nonCommodity(entryAccountDTO, successList, failList);
                    break;
                    // 海关缴款书入账
                case "S002" :
                    this.customs(entryAccountDTO, successList, failList);
                    break;
                default:
                    log.info("系统来源错误！请填写正确的系统来源！");
                    failList.add(createEntryAccountResultDTO(entryAccountDTO, "系统来源错误! 请填写正确的系统来源!"));
            }
        }
        map.put("success", successList);
        map.put("fail", failList);
        return map;
    }

    private boolean checkQueryParameter(EntryAccountDTO entryAccountDTO, List<EntryAccountResultDTO> failList) {
        if(ObjectUtils.isEmpty(entryAccountDTO.getPostDate())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "入账日期不能为空!"));
            return false;
        }
        if(ObjectUtils.isEmpty(entryAccountDTO.getCompanyCode())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "公司代码不能为空!"));
            return false;
        }
//        if(ObjectUtils.isEmpty(entryAccountDTO.getJvCode())) {
//            failList.add(createEntryAccountResultDTO(entryAccountDTO, "JV不能为空!"));
//            return false;
//        }
        if(ObjectUtils.isEmpty(entryAccountDTO.getTaxAmount())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "税额不能为空!"));
            return false;
        }
        if(ObjectUtils.isEmpty(entryAccountDTO.getTaxCode())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "税码不能为空!"));
            return false;
        }
        if(ObjectUtils.isEmpty(entryAccountDTO.getTaxRate())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "税率不能为空!"));
            return false;
        }
        if(ObjectUtils.isEmpty(entryAccountDTO.getAccNo())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "凭证号不能为空!"));
            return false;
        }
        return true;
    }

    /**
     * 非商入账
     * @param entryAccountDTO
     * @return
     */
    @Override
    @Transactional
    public void nonCommodity(EntryAccountDTO entryAccountDTO, List<EntryAccountResultDTO> successList, List<EntryAccountResultDTO> failList) {
        // 这里的入账是新增的对接BMS的入账发票, 所以设置flowType值为新增的flowType类型 9
        List<TDxRecordInvoiceEntity> recordInvoiceEntityList = recordInvoiceService.queryRecordInvByUuid(entryAccountDTO.getInvoiceCode() + entryAccountDTO.getInvoiceNo());
        // 入账的时候底账必须有发票
        if (CollectionUtils.isEmpty(recordInvoiceEntityList)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该票底账暂无数据!"));
            return;
        }
        TDxRecordInvoiceEntity tDxRecordInvoiceEntity = recordInvoiceEntityList.get(0);
        // 判断发票是否是BMS的非商发票
        if (StringUtils.isNotEmpty(tDxRecordInvoiceEntity.getFlowType()) && !StringUtils.equals(FlowTypeEnum.FLOW_9.getCode(), tDxRecordInvoiceEntity.getFlowType())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该票非BMS扫描非商发票!"));
            return;
        }
        // 如果是已认证 则不允许更新
        if (StringUtils.equals(AuthStatusEnum.AUTH_STATUS_SUCCESS.getCode(), tDxRecordInvoiceEntity.getAuthStatus())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该发票已认证"));
            return;
        }
        // 新增税额校验, 先根据uuid查询明细, 明细存在0.13和13这种税率的数据, 需判断明细税率
        List<TDxRecordInvoiceDetailEntity> detailEntityList = recordInvoiceDetailService.queryByUuidTaxRate(tDxRecordInvoiceEntity.getInvoiceCode() + tDxRecordInvoiceEntity.getInvoiceNo(), null);
        if (CollectionUtils.isEmpty(detailEntityList)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该发票无明细"));
            return;
        }
        // 根据明细的税率为维度区分明细并统计金额, 根据税率将明细进行分组
        Map<String, List<TDxRecordInvoiceDetailEntity>> taxRateMap = detailEntityList.stream().collect(Collectors.groupingBy(TDxRecordInvoiceDetailEntity::getTaxRate));
        List<TDxRecordInvoiceDetailEntity> invoiceDetailEntities = new ArrayList<>();
        for (String taxRate : taxRateMap.keySet()) {
            // 判断taxRate是否小于1, 小于1则将其乘以100转换成非0.XX的税率
            // 将税率转换成非XX的税率
            String strTaxRate = TaxRateUtils.strTaxRateToStr(taxRate);
            BigDecimal dtoTaxRate = entryAccountDTO.getTaxRate();
            // 判断转换后的税率与RMS入参的税率是否一致, 如果一致则明细中存在该税率的发票 取与RMS非商入账税率一致的明细数据
            log.info("RMS非商入账税率为:{}, 明细税率为:{}", dtoTaxRate.toString(), strTaxRate);
            if (dtoTaxRate.compareTo(new BigDecimal(strTaxRate)) != 0) {
                continue;
            }
            invoiceDetailEntities = taxRateMap.get(taxRate);
        }
        if (CollectionUtils.isEmpty(invoiceDetailEntities)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, MessageFormat.format("该发票无{0}税率明细", entryAccountDTO.getTaxRate())));
            return;
        }
        // 汇总明细税额
        BigDecimal taxAmount = new BigDecimal(0);
        // 不含税金额
        BigDecimal invoiceAmount = new BigDecimal(0);
        for (TDxRecordInvoiceDetailEntity detailEntity : invoiceDetailEntities) {
            String goodsName = detailEntity.getGoodsName();
            if (StringUtils.equals(goodsName, "(详见销货清单)") || StringUtils.equals(goodsName, "（详见销货清单）") ||
                    StringUtils.equals(goodsName, "(详见销货清单）") || StringUtils.equals(goodsName, "（详见销货清单)") ||
                    StringUtils.equals(goodsName, "原价合计") || StringUtils.equals(goodsName, "折扣额合计")) {
                continue;
            }
            taxAmount = taxAmount.add(new BigDecimal(detailEntity.getTaxAmount()));
            invoiceAmount = invoiceAmount.add(new BigDecimal(detailEntity.getDetailAmount()));
        }
        // 三个金额校验
        if (taxAmount.compareTo(entryAccountDTO.getTaxAmount()) != 0){
            failList.add(createEntryAccountResultDTO(entryAccountDTO, MessageFormat.format("该发票此税率明细税额汇总不一致, RMS税额:{0}, wapp税额:{1}", entryAccountDTO.getTaxAmount().toString(), taxAmount.toString())));
            return;
        }
        if (invoiceAmount.compareTo(entryAccountDTO.getInvoiceAmount()) != 0){
            failList.add(createEntryAccountResultDTO(entryAccountDTO, MessageFormat.format("该发票此税率明细成本金额汇总不一致, RMS成本金额:{0}, wapp成本金额:{1}", entryAccountDTO.getInvoiceAmount().toString(), invoiceAmount.toString())));
            return;
        }
        if (entryAccountDTO.getTotalAmount().compareTo(taxAmount.add(invoiceAmount)) != 0){
            failList.add(createEntryAccountResultDTO(entryAccountDTO, MessageFormat.format("该发票此税率明细税价合计汇总不一致, RM税价合计:{0}, wapp税价合计:{1}", entryAccountDTO.getTotalAmount().toString(), taxAmount.add(invoiceAmount).toString())));
            return;
        }

        // 获取纸票类型
        List<String> paperInvoiceType = InvoiceTypeEnum.paperInvoiceEnums().stream().map(InvoiceTypeEnum::getResultCode).collect(Collectors.toList());
        // 新增纸票签收状态校验
        if (paperInvoiceType.contains(tDxRecordInvoiceEntity.getInvoiceType())) {

        }

        // 获取专票类型
        List<String> collect = InvoiceTypeEnum.specialInvoiceEnums().stream().map(InvoiceTypeEnum::getResultCode).collect(Collectors.toList());
        // 判断发票flowType值为空 专票推送手工认证模块
        // 保存非商传票清单, 只有专票才会生成传票清单, 在查询的时候限制了发票类型, 这里只有保存或更新相关字段即可
        if (collect.contains(tDxRecordInvoiceEntity.getInvoiceType())) {
            // 设置confirm_status = 1 & 设置新的flow_type = 9(对接RMS非商入账) 设置scan_match_status = 1
            // confirm_status = 1应该是手工认证
            tDxRecordInvoiceEntity.setConfirmStatus("1");
            tDxRecordInvoiceEntity.setScanMatchStatus("1");
            tDxRecordInvoiceEntity.setFlowType("9");
            tDxRecordInvoiceEntity.setRzhYesorno("0");
            tDxRecordInvoiceEntity.setAuthStatus("0");
            tDxRecordInvoiceEntity.setJvcode(entryAccountDTO.getJvCode());
            tDxRecordInvoiceEntity.setCostDeptId(entryAccountDTO.getCostCenter());
            tDxRecordInvoiceEntity.setCompanyCode(entryAccountDTO.getCompanyCode());
            tDxRecordInvoiceEntity.setCertificateNo(entryAccountDTO.getAccNo());
            // todo 暂不确定底账数据获取时是否有设置供应商id
//                tDxRecordInvoiceEntity.setVenderid("");
//                tDxRecordInvoiceEntity.setVendername("");
            recordInvoiceService.updateById(tDxRecordInvoiceEntity);
            // 专票生成传票清单 新建BMS的传票清单表
            summonsRMSService.saveOrUpdateSummons(entryAccountDTO, tDxRecordInvoiceEntity, detailEntityList);
        }

        //todo 专票普票都进行国税入账

        successList.add(createEntryAccountResultDTO(entryAccountDTO, "成功"));
    }

    /**
     * 海关缴款书入账
     * @param entryAccountDTO
     * @return
     */
    @Transactional
    @Override
    public void customs(EntryAccountDTO entryAccountDTO, List<EntryAccountResultDTO> successList, List<EntryAccountResultDTO> failList) {
        if(entryAccountDTO.getAccNo() == null) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "凭证号不能为空!"));
            return;
        }
        // 查询海关缴款书数据
        List<TDxCustomsEntity> tDxCustomsEntities = customsService.queryByCustomsNo(entryAccountDTO.getTaxDocNo());
        if (CollectionUtils.isEmpty(tDxCustomsEntities)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "暂无该海关缴款书信息!"));
            return;
        }
        TDxCustomsEntity tDxCustomsEntity = tDxCustomsEntities.get(0);
        String isCheck = tDxCustomsEntity.getIsCheck();
        List<String> noEntryAccount = InvoiceCheckEnum.noEntryAccount().stream().map(invoiceCheckEnum -> {
            return invoiceCheckEnum.getCode().toString();
        }).collect(Collectors.toList());
        // 判断勾选状态为, 勾选状态-已勾选、勾选中、撤销勾选中、撤销勾选失败、已抵扣勾选、抵扣勾选异常
        if (noEntryAccount.contains(isCheck)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, MessageFormat.format("该海关票{0}, 不允许入账!", InvoiceCheckEnum.getInvoiceCheckEnum(Integer.parseInt(isCheck)).getDesc())));
            return;
        }

        // 判断推送BMS是否成功
        if (!StringUtils.equals(tDxCustomsEntity.getPushBmsStatus(), PushStatusEnum.SUCCESS.getMatchState())) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该海关缴款书未推送BMS!"));
            return;
        }
        // 查询海关缴款书明细, 根据税率生成传票清单
        List<TDxCustomsDetailEntity> detailList = customsDetailService.getByCustomsNo(tDxCustomsEntity.getCustomsNo(), entryAccountDTO.getTaxRate());
        if (CollectionUtils.isEmpty(detailList)) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该条数据无对应的明细!"));
            return;
        }
        BigDecimal taxAmount = new BigDecimal(0);
        for (TDxCustomsDetailEntity tDxCustomsDetailEntity : detailList) {
            taxAmount = taxAmount.add(tDxCustomsDetailEntity.getTaxAmount());
        }
        if (taxAmount.compareTo(entryAccountDTO.getTaxAmount()) != 0) {
            failList.add(createEntryAccountResultDTO(entryAccountDTO, "该条数据与明细税额汇总不一致!"));
            return;
        }

        tDxCustomsEntity.setVoucherAccountTime(entryAccountDTO.getPostDate());
        tDxCustomsEntity.setVoucherNo(entryAccountDTO.getAccNo());
        // 更新缴款书主表凭证号
        customsService.updateById(tDxCustomsEntity);

        // 根据明细的税率为维度区分明细并统计金额, 根据税率将明细进行分组
        Map<BigDecimal, List<TDxCustomsDetailEntity>> taxRateMap = detailList.stream().collect(Collectors.groupingBy(TDxCustomsDetailEntity::getTaxRate));
        
        //保存或更新海关缴款书传票清单
        customsSummonsService.saveOrUpdateCustomsSummons(entryAccountDTO, tDxCustomsEntity, taxRateMap);

        // 调用海关票国税入账接口
        CustomsEntryRequest customsEntryRequest = new CustomsEntryRequest();
        successList.add(createEntryAccountResultDTO(entryAccountDTO, "成功"));
    }

    /**
     * 推送海关缴款书匹配结果
     * @param taxDocNo
     * @return
     */
    private String taxBillFeedback(String taxDocNo, String billId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SendTaxBill sendTaxBill = SendTaxBill.builder()
                .id(billId)
                .taxDocNo(taxDocNo)
                .matchState("01") // 成功状态01
                .build();
        String sign = SignUtils.getBMSSign(timestamp, JSONObject.toJSONString(sendTaxBill), bmsFeedbackConfig.getAppKey());
        SendBMSDTO<SendTaxBill> sendBMSDTO1 = SendBMSDTO.<SendTaxBill>builder()
                .source("wapp")
                .appName("wapp")
                .version("1.0")
                .param(sendTaxBill)
                .timestamp(timestamp)
                .format("json")
                .sign(sign)
                .build();
        String queryJson = JSONObject.toJSONString(sendBMSDTO1);
        log.info("推送海关缴款书比对结果入参:{}", queryJson);
        String result = null;
        try {
            result = HttpClientUtils.postJson(bmsFeedbackConfig.getBmsTaxBillFeedbackUrl(), queryJson);
        } catch (Exception e) {
            log.error("推送海关缴款书比对结果失败:", e);
        }
        log.info("推送海关缴款书比对结果回参:{}", result);
        return result;
    }

    /**
     * 获取BMS海关缴款书信息
     * @param taxDocNo
     * @return
     */
    private String queryTaxBill(String taxDocNo) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        SendQueryTaxBill sendQueryTaxBill = SendQueryTaxBill.builder().taxDocNo(taxDocNo).build();
        String sign = SignUtils.getBMSSign(timestamp, JSONObject.toJSONString(sendQueryTaxBill), bmsFeedbackConfig.getAppKey());
        SendBMSDTO<SendQueryTaxBill> sendBMSDTO = SendBMSDTO.<SendQueryTaxBill>builder()
                .source("wapp")
                .appName("wapp")
                .version("1.0")
                .param(sendQueryTaxBill)
                .timestamp(timestamp)
                .format("json")
                .sign(sign)
                .build();
        String queryJson = JSONObject.toJSONString(sendBMSDTO);
        log.info("获取BMS海关缴款书明细入参:{}", queryJson);
        String result = null;
        try {
            result = HttpClientUtils.postJson(bmsFeedbackConfig.getBmsQueryTaxBillUrl(), queryJson);
        } catch (Exception e) {
            log.error("调用获取BMS海关缴款书明细接口失败:", e);
        }
        log.info("获取BMS海关缴款书明细回参:{}", result);
        return result;
    }

    /**
     * 签名校验
     * @param entryAccountDTO
     * @return
     */
    public Boolean checkSign(EntryAccountDTO entryAccountDTO) {
        Map<String, String> map = JSONObject.parseObject(JSONObject.toJSONString(entryAccountDTO), Map.class);
        // 针对非String类型的字段需重新转字符串赋值进行签名
        map.put("postDate", DateUtils.dateToStr(entryAccountDTO.getPostDate()));
        map.put("taxRate", entryAccountDTO.getTaxRate().toString());
        map.put("taxAmount", entryAccountDTO.getTaxAmount().toString());
        String sign = SignUtil.generateSignature(map);
        // 签名校验
        return StringUtils.equals(sign, entryAccountDTO.getSign());
    }

    public EntryAccountResultDTO createEntryAccountResultDTO(EntryAccountDTO entryAccountDTO, String msg){
        EntryAccountResultDTO entryAccountResultDTO = new EntryAccountResultDTO();
        BeanUtil.copyProperties(entryAccountDTO, entryAccountResultDTO);
        entryAccountResultDTO.setMsg(msg);
        return entryAccountResultDTO;
    }

}
