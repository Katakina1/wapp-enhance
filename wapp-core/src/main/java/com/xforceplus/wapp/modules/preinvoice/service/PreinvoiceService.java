package com.xforceplus.wapp.modules.preinvoice.service;
import com.xforceplus.phoenix.split.model.SplitPreInvoiceInfo;
import com.xforceplus.wapp.dto.SplitRuleInfoDTO;
import com.xforceplus.wapp.enums.TXfSettlementStatusEnum;
import com.xforceplus.wapp.modules.company.service.CompanyService;
import com.xforceplus.wapp.repository.dao.TXfSettlementExtDao;
import com.xforceplus.wapp.repository.dao.TXfSettlementItemExtDao;
import com.xforceplus.wapp.repository.entity.TAcOrgEntity;
import com.xforceplus.wapp.repository.entity.TXfPreInvoiceItemEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementEntity;
import com.xforceplus.wapp.repository.entity.TXfSettlementItemEntity;
import com.xforceplus.wapp.service.CommRedNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private CommRedNotificationService commRedNotificationService;
    @Autowired
    private TXfSettlementExtDao tXfSettlementDao;
    @Autowired
    private TXfSettlementItemExtDao tXfSettlementItemDao;
    @Autowired
    CompanyService companyService;
    /**
     * 拆票方法
     * @param settlementNo
     * @param sellerNo
     * @return
     */
    public List<SplitPreInvoiceInfo> splitPreInvoice(String settlementNo, String sellerNo) {
        TXfSettlementEntity tXfSettlementEntity =  tXfSettlementDao.querySettlementByNo( settlementNo,TXfSettlementStatusEnum.WAIT_SPLIT_INVOICE.getCode() );
        List<TXfSettlementItemEntity> tXfSettlementItemEntities = tXfSettlementItemDao.queryItemBySettlementNo(settlementNo);
        TAcOrgEntity tAcOrgEntity = companyService.getOrgInfoByOrgCode(sellerNo, "8");
        //查询待拆票算单主信息，
        //查询结算单明细信息
        //查询开票信息
        //组装拆票请求
        //调用拆票请求
        //保存拆票结果
        //调用红字请求
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
