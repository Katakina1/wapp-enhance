package com.xforceplus.wapp.modules.deduct.service;

import com.xforceplus.wapp.common.dto.R;
import com.xforceplus.wapp.common.utils.DateUtils;
import com.xforceplus.wapp.enums.TXfDeductionBusinessTypeEnum;
import com.xforceplus.wapp.export.dto.BillExportDto;
import com.xforceplus.wapp.modules.sys.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Describe: 业务单导出
 *
 * @Author xiezhongyong
 * @Date 2022/9/18
 */
@Slf4j
public abstract class ExportService<T> {

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    static final String BILL_EXPORT_KEY = "WAPP:BILL:EXPORT:%s:%s";
    @Value("${wapp.bill.export.wait_time:60}")
    Integer billExportWaitTime;
    @Value("${wapp.bill.export.limit:99999}")
    Integer billExportLimit;
    @Value("${wapp.bill.export.pageSize:1000}")
    Integer billExportPageSize;

    /**
     * 导出开思
     *
     * @param request
     * @return
     */
    public abstract R export(T request);

    /**
     * 执行导出T
     *
     * @param exportDto
     * @return
     */
    public abstract boolean doExport(BillExportDto exportDto);

    public String getSuccContent() {
        String createDate = DateUtils.format(new Date());
        String content = "申请时间：" + createDate + "。申请导出成功，可以下载！";
        return content;
    }

    /**
     * 校验导入是否频繁操作(添加分布式锁)
     *
     * @param typeEnum
     * @param userId
     * @return
     */
    public R checkExportLock(TXfDeductionBusinessTypeEnum typeEnum, Long userId) {
        // 单据类型+用户ID
        String key = String.format(BILL_EXPORT_KEY, typeEnum.getValue(), userId);
        RLock lock = redissonClient.getLock(key);
        try {
            if (!lock.tryLock(100, 1000 * 60 * billExportWaitTime, TimeUnit.MILLISECONDS)) {
                return R.fail(String.format("您当前已经存在[%s]导出任务，请稍微再试", typeEnum.getDes()));
            }
        } catch (Exception e) {
            log.error("业务单导出typeEnum:{} 获取分布式锁异常：{}", typeEnum, e);
        }
        return R.ok(key);
    }

    /**
     * 释放锁
     *
     * @param typeEnum
     * @param userId
     */
    public void clearLock(TXfDeductionBusinessTypeEnum typeEnum, Long userId) {
        // 清理分布式锁
        redisTemplate.delete(String.format(BILL_EXPORT_KEY, typeEnum.getValue(), userId));
    }

    /**
     * 通过导出上限及 每页查询数量 返回最大页码
     *
     * @return
     */
    public Integer getPages() {
        return billExportLimit % billExportPageSize == 0 ? billExportLimit / billExportPageSize : billExportLimit / billExportPageSize + 1;
    }


}
