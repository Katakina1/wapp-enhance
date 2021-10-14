package com.xforceplus.wapp.modules.preinvoice.service;

import com.xforceplus.phoenix.split.model.PreInvoiceMain;
import com.xforceplus.phoenix.split.model.SplitPreInvoiceInfo;
import com.xforceplus.wapp.dto.SplitRuleInfoDTO;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 拆票方法
     * @param settlementNo
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> splitPreInvoice(String settlementNo, String sellerNo) {
        return null;
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
