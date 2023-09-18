package com.xforceplus.wapp.modules.deduct.model;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.modules.deduct.service.BlueInvoiceService;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfBillDeductEntity;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 类描述：协议单合并对象
 */
@Data
@Slf4j
public class AgreementMergeData{
    private List<Long> invoiceIdList;//如不为空，则匹配的蓝票必须包含在列表范围内
    private List<DeductInvoiceDetailData> originInvoiceDetailList;//如不为空，则匹配的蓝票明细必须包含在列表范围内
    private List<DeductInvoiceDetailData> matchInvoiceDetailList;//如不为空，则匹配的蓝票明细必须包含在列表范围内
    private String settlementNo;
    private Integer plusMinusFlag = 0;//正负混合标记 0-无 1-有
    private Boolean notQueryOil = false;
    private String purchaserNo;
    private TAcOrgEntity purchaserOrg;
    private String sellerNo;
    private TAcOrgEntity sellerOrg;
    private BigDecimal taxRate; //原始税率
    private BigDecimal targetTaxRate;//目标税率
    private String taxCode;//税码
    private List<TXfBillDeductEntity> plusDeductList = new ArrayList<>();//正数协议单列表
    private List<TXfBillDeductEntity> minusDeductList = new ArrayList<>();//负数协议单列表
    private Map<String,List<BlueInvoiceService.MatchRes>> matchResMap = new HashMap<>();//用于事务失败回滚发票占用

    //协议单合并总金额统计
    private BigDecimal amountWithoutTax = new BigDecimal("0.00");
    private BigDecimal amountWithTax = new BigDecimal("0.00");
    private BigDecimal taxAmount = new BigDecimal("0.00");

    //协议单税额差异
    private BigDecimal diffTaxAmount = new BigDecimal("0.00");
    //正数协议单，税额差异多1分业务单列表，根据不含税金额由小到大排序
    private LinkedList<TXfBillDeductEntity> plusOverDiffDeductList = new LinkedList<>();
    //正数协议单，税额差异少1分业务单列表，根据不含税金额由小到大排序
    private LinkedList<TXfBillDeductEntity> plusLowerDiffDeductList = new LinkedList<>();

    public void calDiffInfo(){
        //初始化
        diffTaxAmount = new BigDecimal("0.00");
        plusOverDiffDeductList = new LinkedList<>();
        plusLowerDiffDeductList = new LinkedList<>();
        //计算正数协议单税额差异
        BigDecimal diffTemp;
        for (TXfBillDeductEntity plusDeduct : plusDeductList){
            diffTemp = plusDeduct.getTaxAmount().subtract(plusDeduct.getAmountWithoutTax()
                    .multiply(plusDeduct.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
            if (BigDecimal.ZERO.compareTo(diffTemp) == 0){
                //税额无差异
                continue;
            }
            if (BigDecimal.ZERO.compareTo(diffTemp) < 0){
                //税额差异多1分
                plusOverDiffDeductList.add(plusDeduct);
            }else {
                //税额差异少1分
                plusLowerDiffDeductList.add(plusDeduct);
            }
            diffTaxAmount = diffTaxAmount.add(diffTemp);
        }

        //计算负数协议单税额差异
        for (TXfBillDeductEntity minusDeduct : minusDeductList){
            diffTemp = minusDeduct.getTaxAmount().subtract(minusDeduct.getAmountWithoutTax()
                    .multiply(minusDeduct.getTaxRate()).setScale(2, RoundingMode.HALF_UP));
            if (BigDecimal.ZERO.compareTo(diffTemp) == 0){
                //税额无差异
                continue;
            }
            diffTaxAmount = diffTaxAmount.add(diffTemp);
        }

        //按不含税金额由小到大排序
        plusOverDiffDeductList.stream().sorted((Comparator.comparing(item -> item.getAmountWithoutTax())));
        plusLowerDiffDeductList.stream().sorted((Comparator.comparing(item -> item.getAmountWithoutTax())));
    }

    public AgreementMergeData() {
    }

    public AgreementMergeData(String purchaserNo, String sellerNo, BigDecimal taxRate) {
        this.purchaserNo = purchaserNo;
        this.sellerNo = sellerNo;
        this.taxRate = taxRate;
    }

    public void addPlusDeduct(TXfBillDeductEntity plusDeduct){
        this.plusDeductList.add(plusDeduct);
    }

    public void addMinusDeduct(TXfBillDeductEntity minusDeduct){
        this.minusDeductList.add(minusDeduct);
    }

    public void addAmountWithoutTax(BigDecimal amountWithoutTax){
        this.amountWithoutTax = this.amountWithoutTax.add(amountWithoutTax);
    }
    public void addAmountWithTax(BigDecimal amountWithTax){
        this.amountWithTax = this.amountWithTax.add(amountWithTax);
    }
    public void addTaxAmount(BigDecimal taxAmount){
        this.taxAmount = this.taxAmount.add(taxAmount);
    }

    /**
     * 添加蓝票匹配记录
     * @param mapKey 业务单-传deductId 结算单-传settlementNo
     * @param matchResList
     */
	public void addMatchResList(String mapKey, List<BlueInvoiceService.MatchRes> matchResList) {
		this.matchResMap.put(mapKey, matchResList);
	}

    public List<TXfBillDeductEntity> getMergeDeductList(){
        List<TXfBillDeductEntity> mergeDeductList = new ArrayList<>();
        mergeDeductList.addAll(this.plusDeductList);
        mergeDeductList.addAll(this.minusDeductList);
        return mergeDeductList;
    }

    public List<BlueInvoiceService.MatchRes> getMatchResList(String mapKey){
        return this.matchResMap.get(mapKey);
    }

    public List<BlueInvoiceService.MatchRes> getMergeMatchResList(){
        //按发票号码代码组合匹配记录
        Map<String,BlueInvoiceService.MatchRes> invoiceMatchResMap = new HashMap<>();
        Iterator<String> deductIter =  matchResMap.keySet().iterator();
        while (deductIter.hasNext()){
            mergeMatchResList(invoiceMatchResMap,matchResMap.get(deductIter.next()));
        }
        //合并最终返回列表
        List<BlueInvoiceService.MatchRes> matchResList =  Lists.newArrayList(invoiceMatchResMap.values());
        //如果用户指定了明细，则使用用户指定的明细占用信息
        if (CollectionUtils.isNotEmpty(originInvoiceDetailList)){
            for (BlueInvoiceService.MatchRes matchRes : matchResList){
//                for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()) {
//                    for (DeductInvoiceDetailData detailData : originInvoiceDetailList) {
//                        if (invoiceItem.getItemId().equals(detailData.getInvoiceDetailId())){
//                            log.info("用户指定明细占用信息覆盖 执行前：{}", JSON.toJSONString(invoiceItem));
//                            invoiceItem.setMatchedDetailAmount(detailData.getMatchedDetailAmount());
//                            invoiceItem.setMatchedTaxAmount(detailData.getMatchedTaxAmount());
//                            invoiceItem.setMatchedNum(detailData.getMatchedNum());
//                            invoiceItem.setMatchedUnitPrice(detailData.getMatchedUnitPrice());
//                            invoiceItem.setLeftDetailAmount(detailData.getLeftDetailAmount());
//                            invoiceItem.setLeftNum(detailData.getLeftNum());
//                            log.info("用户指定明细占用信息覆盖 执行后：{}", JSON.toJSONString(invoiceItem));
//                        }
//                    }
//                }
            }
        }

        return matchResList;
    }

    public static void mergeMatchResList(Map<String,BlueInvoiceService.MatchRes> invoiceMatchResMap, List<BlueInvoiceService.MatchRes> matchResList){
        String invoiceCodeNoKey;
        BlueInvoiceService.MatchRes invoiceCodeNoValue;
        for (BlueInvoiceService.MatchRes matchRes : matchResList){
            invoiceCodeNoKey = matchRes.getInvoiceCode()+matchRes.getInvoiceNo();
            invoiceCodeNoValue = invoiceMatchResMap.get(invoiceCodeNoKey);
            if (invoiceCodeNoValue == null){
                invoiceMatchResMap.put(invoiceCodeNoKey,matchRes);
                continue;
            }
            //合并抵扣金额
            invoiceCodeNoValue.setDeductedAmount(invoiceCodeNoValue.getDeductedAmount().add(matchRes.getDeductedAmount()));
            //合并明细信息
            for (BlueInvoiceService.InvoiceItem invoiceItem : matchRes.getInvoiceItems()){
                boolean isItemMerged = false;
//                for (BlueInvoiceService.InvoiceItem baseInvoiceItem : invoiceCodeNoValue.getInvoiceItems()){
//                    if (invoiceItem.getItemId().equals(baseInvoiceItem.getItemId())){
//                        isItemMerged = true;
//                        //执行明细匹配金额合并
//                        //计算占用不含税
//                        baseInvoiceItem.setMatchedDetailAmount(baseInvoiceItem.getMatchedDetailAmount().add(invoiceItem.getMatchedDetailAmount()));
//                        if (invoiceItem.getLeftDetailAmount().compareTo(baseInvoiceItem.getLeftDetailAmount())<0){
//                            //剩余金额取最小
//                            baseInvoiceItem.setLeftDetailAmount(invoiceItem.getLeftDetailAmount());
//                        }
//
//                        //计算占用税额
//                        baseInvoiceItem.setMatchedTaxAmount(baseInvoiceItem.getMatchedTaxAmount().add(invoiceItem.getMatchedTaxAmount()));
//
//                        //计算占用数量和单价
//                        if (baseInvoiceItem.getMatchedNum() != null) {
//                            baseInvoiceItem.setMatchedNum(baseInvoiceItem.getMatchedNum().add(invoiceItem.getMatchedNum()));
//                            if (invoiceItem.getLeftNum().compareTo(baseInvoiceItem.getLeftNum())<0){
//                                //剩余数量取最小
//                                baseInvoiceItem.setLeftNum(invoiceItem.getLeftNum());
//                            }
//                            //计算占用单价
//                            baseInvoiceItem.setMatchedUnitPrice(baseInvoiceItem.getMatchedDetailAmount().divide(baseInvoiceItem.getMatchedNum(), 15, RoundingMode.HALF_UP));
//                            BigDecimal originUnitPrice = new BigDecimal(baseInvoiceItem.getUnitPrice());
//                            if (baseInvoiceItem.getMatchedUnitPrice().compareTo(originUnitPrice)>0){
//                                //如果匹配单价超过原始单价，则单价使用原始单价
//                                baseInvoiceItem.setMatchedUnitPrice(originUnitPrice);
//                            }
//                        }
//                    }
//                }
                if (!isItemMerged){
                    //没有执行合并，说明是新的明细被匹配，直接添加到明细列表
                    invoiceCodeNoValue.getInvoiceItems().add(invoiceItem);
                }
            }
        }
    }

    public List<BlueInvoiceService.MatchRes> getAllMatchResList(){
        List<BlueInvoiceService.MatchRes> allMatchResList = new ArrayList<>();
        for (String mapKey : matchResMap.keySet()){
            allMatchResList.addAll(matchResMap.get(mapKey));
        }
        return allMatchResList;
    }

    public void exchangeAmount() {
        if (Objects.nonNull(targetTaxRate) && targetTaxRate.compareTo(taxRate) != 0) {
            // 要根据转换后的税率计算出待匹配的金额
            Function<List<TXfBillDeductEntity>/*业务单列表*/, Tuple2<BigDecimal/*amountWithoutTaxSum*/, BigDecimal/*taxAmountSum*/>>
                    computeTaxRateExchangeFunc = list -> list.stream()
                    .peek(it -> {
                        BigDecimal amountWithoutTax = it.getAmountWithTax().divide(BigDecimal.ONE.add(targetTaxRate), 2, RoundingMode.HALF_UP);
                        it.setAmountWithoutTax(amountWithoutTax);
                        it.setTaxRate(targetTaxRate);
                        it.setTaxAmount(it.getAmountWithTax().subtract(amountWithoutTax));
                    }).map(it -> Tuple.of(it.getAmountWithoutTax(), it.getTaxAmount()))
                    .reduce((i, u) -> Tuple.of(i._1.add(u._1), i._2.add(u._2))).orElse(Tuple.of(BigDecimal.ZERO,BigDecimal.ZERO));
            Tuple2<BigDecimal, BigDecimal> plusAmountSum = computeTaxRateExchangeFunc.apply(plusDeductList);
            Tuple2<BigDecimal, BigDecimal> minusAmountSum = computeTaxRateExchangeFunc.apply(minusDeductList);
            amountWithoutTax = plusAmountSum._1.add(minusAmountSum._1);
            taxAmount = plusAmountSum._2.add(minusAmountSum._2);
            calDiffInfo();
        }
    }


    @Override
    public String toString() {
        return "AgreementMergeData{" +
                "plusMinusFlag=" + plusMinusFlag +
                ", amountWithoutTax=" + amountWithoutTax.toPlainString() +
                ", amountWithTax=" + amountWithTax.toPlainString() +
                ", taxAmount=" + taxAmount.toPlainString() +
                ", diffTaxAmount=" + diffTaxAmount.toPlainString() +
                ", purchaserNo='" + purchaserNo + '\'' +
                ", sellerNo='" + sellerNo + '\'' +
                ", taxRate=" + taxRate.toPlainString() +
                ", plusDeductList.size=" + plusDeductList.size() +
                ", minusDeductList.size=" + minusDeductList.size() +
                '}';
    }
}
