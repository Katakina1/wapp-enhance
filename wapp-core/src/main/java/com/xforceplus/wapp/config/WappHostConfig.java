package com.xforceplus.wapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author mashaopeng@xforceplus.com
 */

@Data
@Component
@ConfigurationProperties(prefix = "wapp.host")
public class WappHostConfig {
    private String baseUrl;
    private Integer bufferSize;
    private String cName;
    private String appName;
    private String appAccessToken;
    private String hyperNbrsUrl;
    private String taxCodeUrl;
    private String samsNbrsUrl;
}
