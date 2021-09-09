package com.xforceplus.wapp.modules.base.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.xforceplus.wapp.modules.base.entity.AccountCentRequest;
import com.xforceplus.wapp.modules.base.entity.AccountCentResponse;
import com.xforceplus.wapp.modules.base.service.AccountCentService;

import net.sf.json.JSONObject;

/**
 * Created by Daily.zhang on 2018/04/23.
 */
@PropertySource(value = {"classpath:config.properties"})
@Service("accountCentService")
public class AccountCentServiceImpl implements AccountCentService {
    private static final Logger LOGGER  = LoggerFactory.getLogger(AccountCentServiceImpl.class);

    //服务接口定义
    public static final String SAVE_USER = "saveUser";
    public static final String UPDATE_USER = "updateUser";
    public static final String QUERY_USER = "queryUser";
    public static final String DELETE_USER = "deleteUser";
    public static final String RESET_PWD = "resetPwd";

    @Value("${AccountCent.url}")
    private String serviceUrl;

    @Override
    public AccountCentResponse saveUser(AccountCentRequest request) {
        String url = serviceUrl + SAVE_USER;
        return httpPost(request, url);
    }

    @Override
    public AccountCentResponse updateUser(AccountCentRequest request) {
        String url = serviceUrl + UPDATE_USER;
        return httpPost(request, url);
    }

    @Override
    public AccountCentResponse queryUser(AccountCentRequest request) {
        String url = serviceUrl + QUERY_USER;
        return httpPost(request, url);
    }

    @Override
    public AccountCentResponse deleteUser(AccountCentRequest request) {
        String url = serviceUrl + DELETE_USER;
        return httpPost(request, url);
    }

    @Override
    public AccountCentResponse resetPwd(AccountCentRequest request) {
        String url = serviceUrl + RESET_PWD;
        return httpPost(request, url);
    }

    private AccountCentResponse httpPost(AccountCentRequest request, String serviceUrl){
        LOGGER .info("serviceUrl:{}  param:{} request", serviceUrl, request);
        String requestStr = JSONObject.fromObject(request).toString();
        AccountCentResponse response = new AccountCentResponse();
        try {
            String responseStr = null;//HttpRequestUtils.httpPost(requestStr, serviceUrl, 30000);
            LOGGER .info("serviceUrl:{}  param:{} responseStr:{}", serviceUrl, request, responseStr);
            if(responseStr.startsWith("{")){
                JSONObject obj = JSONObject.fromObject(responseStr);
                response = (AccountCentResponse) JSONObject.toBean(obj, AccountCentResponse.class);
            }else {
                response.setCode("9999");
                response.setMessage(responseStr);
            }
        } catch (Exception e) {
            LOGGER .error("serviceUrl:{}  param:{} exception. cause:{}", serviceUrl, request, e.getCause(), e);
            response.setCode("9999");
            response.setMessage(e.getCause().toString());
        }
        return response;
    }
}
