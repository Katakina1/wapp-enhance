package com.xforceplus.wapp.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Optional;

/**
 * @author mashaopeng@xforceplus.com
 */
@Slf4j
@Component
public class WappDb2Client {
    private final WebClient webClient;
    private final static String ITEM_NO_UPC_URL = "/item-oe/findByNbrs";

    public WappDb2Client(@Value("${wapp.db2.base-url}") String db2Url) {
        webClient = WebClient.builder().baseUrl(db2Url).build();
    }

    public Optional<String> getItemNo(String upc) {
        return webClient.post().uri(ITEM_NO_UPC_URL).contentType(MediaType.APPLICATION_JSON)
                .syncBody(String.format("{\"nbrs\":[%s]}", upc)).retrieve()
                .bodyToMono(UpcRsp.class).timeout(Duration.ofMillis(30 * 1000L)).retry(3)
                .doOnError(WebClientResponseException.class, err -> {
                    log.info("请求沃尔玛获取[{}]税编异常,status:{},msg:{}", upc, err.getRawStatusCode(), err.getResponseBodyAsString());
                    throw new RuntimeException(err.getMessage());
                })
                .onErrorReturn(new UpcRsp())
                .blockOptional()
                .map(it -> {
                    if (!"1".equalsIgnoreCase(it.getCode())) {
                        log.error("请求沃尔玛获取[{}]税编异常:{}", upc, String.format("%s:%s", it.getCode(), it.getMessage()));
                        return null;
                    }
                    log.info("请求沃尔玛获取UPC[{}]对应ItemNo关系,结果:{}", upc, it);
                    return it.getResult().getItemNo();
                });
    }
}
