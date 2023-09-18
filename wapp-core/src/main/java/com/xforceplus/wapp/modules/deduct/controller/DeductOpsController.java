package com.xforceplus.wapp.modules.deduct.controller;

import com.xforceplus.wapp.annotation.EnhanceApi;
import com.xforceplus.wapp.common.dto.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Describe: 业务单运维接口(可以提前清理业务单导出分布式锁)
 *
 * @Author xiezhongyong
 * @Date 2022/10/11
 */
@Api(tags = "deduct-ops")
@RestController
@RequestMapping(value = EnhanceApi.BASE_PATH + "/deduct/ops")
public class DeductOpsController {
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    protected Logger logger = LoggerFactory.getLogger(getClass());


    @ApiOperation(value = "删除")
    @PostMapping(value = "/redis/delete")
    public R set(String key) {
        redisTemplate.delete(key);
        return R.ok();
    }


}
