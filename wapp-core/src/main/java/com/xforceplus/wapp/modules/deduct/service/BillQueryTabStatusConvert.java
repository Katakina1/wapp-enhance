package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.dto.param.BillQueryParam;
import com.xforceplus.wapp.common.enums.IQueryTab;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.enums.query.AgreementBillQueryTabEnum;
import com.xforceplus.wapp.enums.query.DeductBillQueryTabEnum;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductBaseResponse;
import com.xforceplus.wapp.modules.deduct.dto.QueryDeductListResponse;
import org.apache.commons.collections.CollectionUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Describe: 业务单状态 转换 tab
 *
 * @Author xiezhongyong
 * @Date 2022-09-14
 */
public class BillQueryTabStatusConvert {


    private static List<Tuple3<Integer, BillQueryParam, IQueryTab>> deductBillList;
    private static List<Tuple3<Integer, BillQueryParam, IQueryTab>> agreementBillBillList;

    static {
        deductBillList = initStatusList(Arrays.asList(DeductBillQueryTabEnum.values()));
        agreementBillBillList = initStatusList(Arrays.asList(AgreementBillQueryTabEnum.values()));
    }

    /**
     * 初始化索赔状态转换数据
     */
    private static List<Tuple3<Integer, BillQueryParam, IQueryTab>> initStatusList(List<IQueryTab> queryTabEnums) {
        // 过滤 全部
        queryTabEnums.stream().filter(v -> !v.code().equals(DeductBillQueryTabEnum.ALL.code())).collect(Collectors.toList());
        List<Tuple3<Integer, BillQueryParam, IQueryTab>> list = new ArrayList<>();
        for (IQueryTab<BillQueryParam> tabEnum : queryTabEnums) {
            for (BillQueryParam queryParam : tabEnum.queryParams()) {
                // 如果业务单状态+结算单状态都不为空说明 是组合状态
                if (CollectionUtils.isNotEmpty(queryParam.getBillStatus()) && CollectionUtils.isNotEmpty(queryParam.getSettlementStatus())) {
                    list.add(Tuples.of(2, queryParam, tabEnum));
                } else if (CollectionUtils.isNotEmpty(queryParam.getBillStatus()) || CollectionUtils.isNotEmpty(queryParam.getSettlementStatus())) {
                    list.add(Tuples.of(1, queryParam, tabEnum));
                }
            }
        }
        // 倒序
        Collections.sort(list, (v1, v2) -> -v1.getT1().compareTo(v2.getT1()));

        return list;

    }

    /**
     * 通过业务类型+业务单状态+结算单状态 获取查询tab状态
     * @param businessType
     * @param billStatus
     * @param settlementStatus
     * @return
     */
    public static IQueryTab getQueryTab(Integer businessType, Integer billStatus, Integer settlementStatus) {
        if (TXfDeductionBusinessTypeEnum.CLAIM_BILL.getValue().equals(businessType)) {
            return getQueryTab(deductBillList, billStatus, settlementStatus);
        } else if (TXfDeductionBusinessTypeEnum.AGREEMENT_BILL.getValue().equals(businessType)) {
            return getQueryTab(agreementBillBillList, billStatus, settlementStatus);
        }
        return null;
    }

    /**
     * 通过业务单状态+结算单状态反推对应的tab (因为存在组合，只有循环匹配，没法通过key-value 获取)
     * @param list
     * @param billStatus
     * @param settlementStatus
     * @return
     */
    private static IQueryTab getQueryTab(List<Tuple3<Integer, BillQueryParam, IQueryTab>> list, Integer billStatus, Integer settlementStatus) {

        Map<Integer, List<Tuple3<Integer, BillQueryParam, IQueryTab>>> listMap = list.stream().collect(Collectors.groupingBy(Tuple2::getT1));


        // 一级(业务单+结算单组合状态)
        List<Tuple3<Integer, BillQueryParam, IQueryTab>> tuple3List = listMap.get(2);
        for (Tuple3<Integer, BillQueryParam, IQueryTab> objects : tuple3List) {
            BillQueryParam queryParam = objects.getT2();
            if (null != billStatus && null != settlementStatus && CollectionUtils.isNotEmpty(queryParam.getBillStatus()) && queryParam.getBillStatus().contains(billStatus) &&
                    CollectionUtils.isNotEmpty(queryParam.getSettlementStatus()) && queryParam.getSettlementStatus().contains(settlementStatus)) {
                return objects.getT3();
            }
        }

        // 二级(业务单状态)
        tuple3List = listMap.get(1);
        for (Tuple3<Integer, BillQueryParam, IQueryTab> objects : tuple3List) {
            BillQueryParam queryParam = objects.getT2();
            if (null != billStatus && CollectionUtils.isNotEmpty(queryParam.getBillStatus()) && queryParam.getBillStatus().contains(billStatus)) {
                return objects.getT3();
            }
        }

        return null;
    }

    /**
     * queryTab 响应转换
     * @param queryTab
     * @return
     */
    public static QueryDeductBaseResponse.QueryTabResp getTabResponse(IQueryTab queryTab){
        return new QueryDeductBaseResponse.QueryTabResp(queryTab.code(), queryTab.message());
    }
}
