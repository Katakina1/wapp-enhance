package com.xforceplus.wapp.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.SnappyCodecV2;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonClientConfiguration {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private String redisPort;
    @Value("${spring.redis.password}")
    private String redisPassword;
    @Value("${spring.redis.database:0}")
    private Integer database;
    @Value("${spring.redis.timeout:9000}")
    private Integer timeout;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(String.format("redis://%s:%s", redisHost, redisPort));
        if(StringUtils.isNotBlank(redisPassword)) {
            singleServerConfig.setPassword(redisPassword);
        }
        singleServerConfig.setDatabase(database);
        singleServerConfig.setTimeout(timeout);
        singleServerConfig.setTcpNoDelay(true);
        config.setCodec(new SnappyCodecV2(new JsonJacksonCodec()));
        config.setNettyThreads(32);
        return Redisson.create(config);
    }
}
