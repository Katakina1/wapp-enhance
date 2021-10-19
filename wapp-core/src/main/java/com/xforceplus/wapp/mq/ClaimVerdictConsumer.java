package com.xforceplus.wapp.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * 监听问题清单列表那里的索赔不定单操作审核消息事件
 */
@Slf4j
@Component
public class ClaimVerdictConsumer {

    @Autowired
    private ClaimService claimService;

    @JmsListener(destination = "${activemq.queue-name.enhance_claim_verdict_queue}")
    public void onMessage(String message) {
        log.info("--------处理索赔不定案消息-------------");
        try {
            if (StringUtils.isBlank(message)) {
                log.error("处理索赔不定案消息为空");
                return;
            }
            log.info("处理索赔不定案消费者收到的报文为:" + message);
            JSONObject enhanceClaimVerdictMap = JSON.parseObject(message);
            log.info("处理索赔不定案消费者收到的业务报文为:" + message);
            //1通过 2不通过
            String operationType = enhanceClaimVerdictMap.getString("operationType");
            String businessNo = enhanceClaimVerdictMap.getString("businessNo");
            if (StringUtils.equals("1", operationType)) {
                claimService.agreeClaimVerdict(Long.valueOf(businessNo));
            } else {
                claimService.rejectClaimVerdict(Long.valueOf(businessNo));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
