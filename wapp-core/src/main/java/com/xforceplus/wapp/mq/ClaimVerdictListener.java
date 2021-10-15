package com.xforceplus.wapp.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xforceplus.wapp.modules.claim.service.ClaimService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * 监听问题清单列表那里的索赔不定单操作审核消息事件
 */
@Component
@Slf4j
public class ClaimVerdictListener implements MessageListener {

    @Autowired
    private ClaimService claimService;

    @Override
    public void onMessage(Message message) {
        log.info("--------接受索赔不定案消息-------------");
        try {
            TextMessage msg = (TextMessage) message;
            if (msg == null) {
                log.error("接受索赔不定案消息为空");
                return;
            }
            log.info("消费者收到的报文为:" + msg);
            String text = msg.getText();
            JSONObject enhanceClaimVerdictMap = JSON.parseObject(text);
            log.info("消费者收到的业务报文为:" + text);
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
