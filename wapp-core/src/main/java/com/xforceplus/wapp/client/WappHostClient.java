package com.xforceplus.wapp.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xforceplus.wapp.config.WappHostConfig;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Component
public class WappHostClient {
    private final WebClient webClient;
    private final WappHostConfig hostConfig;
    public static final String ALOHA_APP_NAME = "AlohaAppName";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String C_NAME = "c-name";

    public WappHostClient(WappHostConfig hostConfig) {
        final int size = Objects.isNull(hostConfig.getBufferSize()) ? 4 * 1024 * 1024 : hostConfig.getBufferSize() * 1024 * 1024;
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size)).build();
        //跳过SSL 安全校验 沃尔玛自签名证书无法通过校验
        HttpClient secure = HttpClient.create()
                .secure(t -> t.sslContext(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)));
        webClient = WebClient.builder().baseUrl(hostConfig.getBaseUrl())
                .exchangeStrategies(exchangeStrategies)
                .clientConnector(new ReactorClientHttpConnector(secure))
                .build();
        this.hostConfig = hostConfig;
    }

    /**
     * @Description 获取山姆规格型号
     * @return
    **/
    public Mono<List<NbrRsp.SamsNbr>> findSamsByNbrs(Set<String> values) {
        log.info("Sams");
        if (CollectionUtils.isEmpty(values)) {
            return Mono.empty();
        }
        NbrBean.SamsNbrBean bean = new NbrBean.SamsNbrBean(values);
        return findByNbrs(hostConfig.getSamsNbrsUrl(), bean, it -> it.toJavaObject(NbrRsp.SamsNbr.class));
    }

    /**
     * @Description 获取大卖场规格型号
     * @return
     **/
    public Mono<List<NbrRsp.HyperNbr>> findHyperByNbrs(Set<String> values) {
        log.info("Hyper");
        if (CollectionUtils.isEmpty(values)) {
            return Mono.empty();
        }
        NbrBean.HyperNbrBean bean = new NbrBean.HyperNbrBean(values);
        return findByNbrs(hostConfig.getHyperNbrsUrl(), bean, it -> it.toJavaObject(NbrRsp.HyperNbr.class));
    }

    public <I extends NbrBean, T> Mono<List<T>> findByNbrs(String uri, I i, Function<JSONObject, T> func) {
        log.info("findByNbrs入参：{}", JSONObject.toJSONString(i));
        Consumer<HttpHeaders> addHeader = it -> {
            if (StringUtils.isNotBlank(hostConfig.getAppName())) {
                it.add(ALOHA_APP_NAME, hostConfig.getAppName());
            }
            if (StringUtils.isNotBlank(hostConfig.getAppName())) {
                it.add(ACCESS_TOKEN, hostConfig.getAppAccessToken());
            }
            it.add(C_NAME, hostConfig.getCName());
        };

        Function<List<JSONObject>, List<T>> mapResult = it -> CollectionUtils.isEmpty(it) ?
                Lists.newArrayList(func.apply(new JSONObject())) : it.stream().map(func).collect(Collectors.toList());

        return webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(addHeader)
                .body(BodyInserters.fromValue(i))
                .retrieve()
                .bodyToMono(String.class)
                .map(it -> {
                    log.info("findByNbrs出参：{}", it);
                    NbrRsp nbrRsp = JSON.parseObject(it, NbrRsp.class);
                    return mapResult.apply(nbrRsp.getData());
                })
                .timeout(Duration.ofMillis(10 * 1000L))
                .retry(3)
                .doOnError(Exception.class, err -> log.error("查询沃尔玛 Nbrs 异常。入参:{}, msg:{}", i, err.getMessage(), err))
                .onErrorReturn(Lists.newArrayList(func.apply(new JSONObject())));
    }
}
