package com.xforceplus.wapp.modules.settlement.service;

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
    public String confirmSettlement(String settlementNo, String sellerNo) {

        return StringUtils.EMPTY;
    }
}
