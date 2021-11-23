package com.xforceplus.wapp.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.SnappyCodecV2;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * redisson配置
 *
 * @author Xforce
 */
@Configuration
public class RedissonClientConfiguration {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        if (redisProperties.getSentinel() != null) {
            return redissonSentinelClient(redisProperties);
        }
        return redissonSingleClient(redisProperties);
    }

    /**
     * 哨兵模式配置
     *
     * @param redisProperties
     * @return
     */
    private RedissonClient redissonSentinelClient(RedisProperties redisProperties) {
        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers();
        sentinelServersConfig.setMasterName(redisProperties.getSentinel().getMaster());
        String[] sentinelAddressesWithSchema = new String[redisProperties.getSentinel().getNodes().size()];
        for (int i = 0; i < redisProperties.getSentinel().getNodes().size(); i++) {
            sentinelAddressesWithSchema[i] = String.format("redis://%s", redisProperties.getSentinel().getNodes().get(i));
        }
        sentinelServersConfig.addSentinelAddress(sentinelAddressesWithSchema);
        sentinelServersConfig.setDatabase(redisProperties.getDatabase());
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            sentinelServersConfig.setPassword(redisProperties.getPassword());
        }
        sentinelServersConfig.setTcpNoDelay(true);
        config.setCodec(new SnappyCodecV2(new JsonJacksonCodec()));
        config.setNettyThreads(32);
        return Redisson.create(config);
    }

    /**
     * 单机模式配置
     *
     * @param redisProperties
     * @return
     */
    private RedissonClient redissonSingleClient(RedisProperties redisProperties) {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort()));
        if (StringUtils.isNotBlank(redisProperties.getPassword())) {
            singleServerConfig.setPassword(redisProperties.getPassword());
        }
        singleServerConfig.setDatabase(redisProperties.getDatabase());
        singleServerConfig.setTcpNoDelay(true);
        config.setCodec(new SnappyCodecV2(new JsonJacksonCodec()));
        config.setNettyThreads(32);
        return Redisson.create(config);
    }
}
