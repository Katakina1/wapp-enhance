package com.xforceplus.wapp.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * @author Xforce
 */
public class RestTemplateSingle {

    private static final RestTemplate gzipInstance = new RestTemplate();

    static{
        gzipInstance.getInterceptors().add(new ClientHttpRequestInterceptor() {
            @Override
            public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
                HttpHeaders httpHeaders = request.getHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_ENCODING, "gzip");
                return execution.execute(request, GzipUtils.compress(body));
            }
        });
    }

    private RestTemplateSingle() {
    }

    public static RestTemplate getGzipInstance() {
        return gzipInstance;
    }
}
