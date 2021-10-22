package com.xforceplus.wapp.modules.settlement.service;

import com.xforceplus.wapp.enums.TXfAmountSplitRuleEnum;
import com.xforceplus.wapp.enums.XFDeductionBusinessTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 类描述：
 *
 * @ClassName SettlementService
 * @Description TODO
 * @Author ZZW
 * @Date 2021/10/12 13:59
 */
@Service
public class SettlementService {

    /**
     * 确认结算单
     * @param settlementNo
     * @param sellerNo
     * @return
     */
    public String confirmSettlement(String settlementNo, String sellerNo, TXfAmountSplitRuleEnum amountSplitRuleEnum) {
        // 查询结算单下的 协议单 epd单状态是否是正常的，否则数据回撤
        //调用拆票请求
        return StringUtils.EMPTY;
    }
}
