package com.xforceplus.wapp.modules.rednotification.service;

import com.google.common.collect.Lists;
import com.xforceplus.wapp.common.enums.ApplyType;
import com.xforceplus.wapp.common.enums.RedNoApplyingStatus;
import com.xforceplus.wapp.common.utils.JsonUtil;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationEntity;
import com.xforceplus.wapp.repository.entity.TXfRedNotificationLogEntity;
import com.xforceplus.wapp.sequence.IDSequence;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Describe: 红字操作(申请，撤销)防重复操作
 * PS： 辅助作用，内部异常自行处理，不影响正常业务逻辑执行
 * 由于前面业务处理时会出现重复申请，目前已经准备生产，为了降低修改风险，所以直接加排除逻辑
 *
 * @Author xiezhongyong
 * @Date 2022-09-30
 */
@Slf4j
@Service
public class RedNotificationAssistService {

    @Autowired
    RedisTemplate<String,String> redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    IDSequence iDSequence;
    @Autowired
    RedNotificationLogService redNotificationLogService;

    @Value("${wapp.rednotification.rep:true}")
    private Boolean repSwitch;

    private static Map<Integer, List<Integer>> filterStatusMap = new HashMap<>();

    static {
        // 红字申请需要排除的状态
        filterStatusMap.put(ApplyType.APPLY.getValue(),
                Arrays.asList(
                        RedNoApplyingStatus.APPLYING.getValue(),
                        RedNoApplyingStatus.APPLIED.getValue()));
        // 红字撤销需要排除的状态
        filterStatusMap.put(ApplyType.ROLL_BACK.getValue(),
                Arrays.asList());
    }


    /**
     * 重复申请等待时间
     */
    private static final String REP_WAIT_TIME_KEY = "WAPP:RED:REP_WAIT_TIME:%s:%s";

    /**
     * 校验是否重复操作（申请，撤销），如果存在重复直接remove
     *
     * @param notificationList
     * @param applyType
     * @throws Exception
     */
    public void checkRepOperator(@NonNull List<TXfRedNotificationEntity> notificationList, @NonNull ApplyType applyType) {
        try {
            log.info("红字操作repSwitch: {}", repSwitch);
            if (!repSwitch) {
                return;
            }
            if (CollectionUtils.isEmpty(notificationList)) {
                return;
            }
            List<Long> ids = notificationList.stream().map(TXfRedNotificationEntity::getId).collect(Collectors.toList());
            log.info("校验是否重复操作[{}] 红字ID列表: {}", applyType.getDesc(), ids);

            List<Tuple2<Long, String>> notificationIds = Lists.newArrayList();
            Iterator<TXfRedNotificationEntity> iterator = notificationList.iterator();
            while (iterator.hasNext()) {
                TXfRedNotificationEntity notification = iterator.next();
                String key = String.format(REP_WAIT_TIME_KEY, applyType.getValue(), notification.getId());
                RLock lock = redissonClient.getLock(key);
                // 拿到锁后锁5分钟，如果收到税件回调后会立马释放（5 分钟只是相对的，如果5分钟税件还未返回，锁会自动失效，不绝对排除重复申请可能）
                boolean tryLockRs = lock.tryLock(100, 1000 * 60 * 5, TimeUnit.MILLISECONDS);
                log.info("红字ID: {},获取申请分布式锁:{}结果:{}", notification.getId(), key, tryLockRs);
                if (!tryLockRs) {
                    notificationIds.add(Tuples.of(notification.getId(), "存在重复申请"));
                    // 移除，防止重复操作
                    iterator.remove();
                    log.error("[{}]存在重复操作，已移除：{}", applyType.getDesc(), JsonUtil.toJsonStr(notification));
                    continue;
                }
                // 如果查询到已经申请、撤销的数据，需要排除的状态
                List<Integer> excludeStatusList = filterStatusMap.getOrDefault(applyType.getValue(), new ArrayList<>());
                if (excludeStatusList.contains(notification.getApplyingStatus())) {
                    notificationIds.add(Tuples.of(notification.getId(), String.format("当前红字状态为：%s,不支持操作", notification.getApplyingStatus())));
                    // 已申请的数据移除，防止重复操作
                    iterator.remove();
                    log.error("[{}]已发起申请，已移除：{}", applyType.getDesc(), JsonUtil.toJsonStr(notification));
                    continue;
                }

            }
            if (CollectionUtils.isNotEmpty(notificationIds)) {
                log.info("校验是否重复操作（申请，撤销）结束，红字ID列表存在重复数据：{}", ids);
                // 保存操作流水
                saveOperateLog(notificationIds, applyType);
            }

        } catch (Exception e) {
            log.error("校验是否重复操作[] 异常：", applyType.getDesc(), e);
        }

    }


    /**
     * 释放防重复操作锁
     *
     * @param notificationIds
     */
    public void clearLockByStr(@NonNull List<String> notificationIds, @NonNull ApplyType applyType) {
        try {
            List<Long> ids = notificationIds.stream().map(Long::parseLong).collect(Collectors.toList());
            clearLock(ids, applyType);
        } catch (Exception e) {
            log.error("clearLockByStr释放红字{}防重复操作锁异常: {}", applyType.getDesc(), e);
        }
    }

    /**
     * 释放防重复操作锁
     *
     * @param notificationIds
     */
    public void clearLock(@NonNull List<Long> notificationIds, @NonNull ApplyType applyType) {
        try {
            log.info("红字操作repSwitch: {}", repSwitch);
            if (!repSwitch) {
                return;
            }
            if (CollectionUtils.isEmpty(notificationIds)) {
                return;
            }
            List<String> keys = notificationIds.stream().map(v -> String.format(REP_WAIT_TIME_KEY, applyType.getValue(), v)).collect(Collectors.toList());
            redisTemplate.delete(keys);
            log.info("clearLock释放红字{}防重复操作锁成功: {}", applyType.getDesc(), keys);
        } catch (Exception e) {
            log.error("clearLock释放红字{}防重复操作锁异常: {}", applyType.getDesc(), e);
        }
    }

    /**
     * 保存操作记录
     *
     * @param notificationIds
     * @param applyType
     */
    private void saveOperateLog(@NonNull List<Tuple2<Long, String>> notificationIds, @NonNull ApplyType applyType) {
        if (CollectionUtils.isEmpty(notificationIds)) {
            return;
        }
        String serialNo = String.valueOf(iDSequence.nextId());
        ArrayList<TXfRedNotificationLogEntity> logList = Lists.newArrayList();
        // ================ 插入申请流水=============
        for (Tuple2<Long, String> tuple2 : notificationIds) {
            TXfRedNotificationLogEntity logEntity = new TXfRedNotificationLogEntity();
            logEntity.setApplyId(tuple2.getT1());
            logEntity.setStatus(3);
            logEntity.setApplyType(applyType.getValue());
            logEntity.setProcessRemark(String.format("%s 存在重复操作，不发起税件请求(%s)", applyType.getDesc(), tuple2.getT2()));
            logEntity.setRedNotificationNo("");
            logEntity.setCreateDate(new Date());
            logEntity.setUpdateDate(new Date());
            logEntity.setSerialNo(serialNo);
            logEntity.setId(iDSequence.nextId());
            logList.add(logEntity);
        }

        redNotificationLogService.saveBatch(logList);
    }
}
