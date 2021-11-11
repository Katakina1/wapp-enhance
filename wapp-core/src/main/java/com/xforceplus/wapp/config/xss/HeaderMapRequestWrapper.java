package com.xforceplus.wapp.config.xss;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Created by SunShiyong on 2021/11/10.
 */
public class HeaderMapRequestWrapper extends HttpServletRequestWrapper {

    /**
     * construct a wrapper for this request
     *
     * @param request
     */
    public HeaderMapRequestWrapper(HttpServletRequest request) {
        super(request);
    }


    private String[] filterArray = new String[]{"\n","\r","％0d","％0D","％0a","％0A"};

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            value = StringUtils.replaceEach(value,filterArray,new String[]{"","","","","",""});
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] parameters = super.getParameterValues(name);
        if (parameters == null || parameters.length == 0) {
            return null;
        }
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = StringUtils.replaceEach((parameters[i]),filterArray,new String[]{"","","","","",""});
        }
        return parameters;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            for (int i = 0; i < values.length; i++) {
                values[i] = StringUtils.replaceEach((values[i]),filterArray,new String[]{"","","","","",""});
            }
            map.put(key, values);
        }
        return map;
    }

    public Cookie[] getCookies() {
        Cookie[] cookies = super.getCookies();
        Optional.ofNullable(cookies).ifPresent(x->{
            for (Cookie cookie : x) {
                cookie.setValue(StringUtils.replaceEach((cookie.getValue()),filterArray,new String[]{"","","","","",""}));
            }
        });
        return cookies;
    }
}
