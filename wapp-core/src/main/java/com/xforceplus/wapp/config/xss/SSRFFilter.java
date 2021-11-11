package com.xforceplus.wapp.config.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by SunShiyong on 2021/11/10.
 * SSRF过滤
 */
public class SSRFFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest  request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //HTTP Response Splitting
        HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {

    }
}
