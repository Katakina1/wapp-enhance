package com.xforceplus.wapp.modules.deduct.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.xforceplus.wapp.client.LockClient;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.modules.overdue.service.DefaultSettingServiceImpl;
import com.xforceplus.wapp.modules.rednotification.model.QueryModel;
import com.xforceplus.wapp.modules.rednotification.model.RedNotificationApplyReverseRequest;
import com.xforceplus.wapp.modules.rednotification.model.taxware.GetTerminalResponse;
import com.xforceplus.wapp.modules.rednotification.model.taxware.TaxWareCode;
import com.xforceplus.wapp.modules.rednotification.service.RedNotificationMainService;
import com.xforceplus.wapp.modules.rednotification.service.TaxWareService;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedNotificationScheduler {

    @Autowired
    private DefaultSettingServiceImpl defaultSettingService;
    @Autowired
    private RedNotificationMainService redNotificationService;
    @Autowired
    @Lazy
    private TaxWareService taxWareService;
    @Autowired
    private LockClient lockClient;
    public static String KEY = "red-notification";

    @Autowired
    private RedisTemplate redisTemplate;
    private static String AUTOKEY = "AUTO_APPLY_RED-NOTIFICATION_SWITCH";

    /**
     * 协议单合并结算单
     */
    @Async("taskThreadPoolExecutor")
    @Scheduled(cron = "${task.RedNotificationScheduler-cron}")
    public void hangAutoApply() {
        log.info("RedNotificationScheduler job 开始");
        lockClient.tryLock(KEY, () -> {
            log.info("RedNotificationScheduler job 获取锁");
            try {
                if (defaultSettingService.isHanging()) {
                    log.info("还处于挂起时间内");
                    return;
                }
                Long id = 0L;
                while (Objects.nonNull(id)) {
                    id = autoApply(id);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            log.info("RedNotificationScheduler job 获取锁结束");
        }, -1, 1);
        log.info("RedNotificationScheduler job 结束");
    }

    private Long autoApply(Long id) {
        log.info("自动申请挂起的红字信息，minId: {}", id);
        var queryWrapper = new QueryWrapper<TXfRedNotificationEntity>()
                .select("top 50 *")
                .lambda().gt(TXfRedNotificationEntity::getId, id)
                .eq(TXfRedNotificationEntity::getApplyingStatus, RedNoApplyingStatus.HANG_APPLY.getValue())
                .orderByDesc(TXfRedNotificationEntity::getId);
        List<TXfRedNotificationEntity> list = redNotificationService.getBaseMapper().selectList(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Long maxId = list.get(0).getId();
        Map<String, List<TXfRedNotificationEntity>> collect = list.stream().collect(Collectors.groupingBy(TXfRedNotificationEntity::getPurchaserTaxNo));
        for (Map.Entry<String, List<TXfRedNotificationEntity>> entry : collect.entrySet()) {
            String purchaserTaxNo = entry.getKey();
            List<TXfRedNotificationEntity> value = entry.getValue();
            log.info("自动申请挂起的红字信息,查询的税号：{},数据条数：{}", purchaserTaxNo, value.size());
            // 申请请求
            RedNotificationApplyReverseRequest applyRequest = new RedNotificationApplyReverseRequest();
            // 获取在线终端
            GetTerminalResponse terminalResponse = taxWareService.getTerminal(purchaserTaxNo);
            log.info("获取在线终端结果:{}",terminalResponse);
            if (Objects.equals(TaxWareCode.SUCCESS, terminalResponse.getCode())) {
                //WALMART-2271 根据税号固定税盘设备
                List<String> terminalList = taxWareService.getAppointTerminalList(purchaserTaxNo);
                if(CollectionUtils.isNotEmpty(terminalList)){
                    log.info("获取到指定税号下设备列表信息,terminalUn:{}",JSON.toJSON(terminalList));
                    terminalResponse.getResult().getTerminalList().stream()
                            .filter(it -> terminalList.contains(it.getTerminalUn()))
                            .findFirst()
                            .ifPresent(it -> {
                                applyRequest.setDeviceUn(it.getOnlineDeviceList().get(0).getDeviceUn());
                                applyRequest.setTerminalUn(it.getTerminalUn());
                            });

                    //未获取到对应的设备
                    if(StringUtils.isEmpty(applyRequest.getDeviceUn())){
                        // 终端不在线更新到红字信息表
                        List<Long> redIdList = value.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
                        TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                        record.setApplyRemark("自动申请，无可用税盘");
                        record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                        LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                        updateWrapper.in(TXfRedNotificationEntity::getId, redIdList);
                        redNotificationService.getBaseMapper().update(record, updateWrapper);
                        log.warn("未获取税号{}指定的在线终端", purchaserTaxNo);
                        continue;
                    }
                }else{
                    //不是指定税号走原来逻辑
                    terminalResponse.getResult().getTerminalList().stream()
                            .filter(it -> CollectionUtils.isNotEmpty(it.getOnlineDeviceList())).findFirst()
                            .ifPresent(it -> {
                                applyRequest.setDeviceUn(it.getOnlineDeviceList().get(0).getDeviceUn());
                                applyRequest.setTerminalUn(it.getTerminalUn());
                            });
                }
            }
            if (StringUtils.isNotEmpty(applyRequest.getDeviceUn())) {
                QueryModel queryModel = new QueryModel();
                List<Long> idList = value.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
                queryModel.setIncludes(idList);
                applyRequest.setQueryModel(queryModel);
                redNotificationService.applyByPage(applyRequest, true);
            } else {
                // 终端不在线更新到红字信息表
                List<Long> redIdList = value.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
                TXfRedNotificationEntity record = new TXfRedNotificationEntity();
                record.setApplyRemark("自动申请，税盘不在线");
                record.setApplyingStatus(RedNoApplyingStatus.WAIT_TO_APPLY.getValue());
                LambdaUpdateWrapper<TXfRedNotificationEntity> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.in(TXfRedNotificationEntity::getId, redIdList);
                redNotificationService.getBaseMapper().update(record, updateWrapper);
                log.warn("未获取税号{}的在线终端", purchaserTaxNo);
            }
        }
        return maxId;
    }

    @Scheduled(cron = "${task.RedNotificationAutoApplyScheduler-cron}")
    public void redNotificationAutoApply() {
        log.info("RedNotificationAutoApplyScheduler job 开始");
        if(redisTemplate.hasKey(AUTOKEY) && "on".equals(redisTemplate.opsForValue().get(AUTOKEY).toString())){
            Long id = 0L;
            while (Objects.nonNull(id)) {
                id = autoApply(id);
            }
        }
        log.info("RedNotificationScheduler job 结束");
    }

}

