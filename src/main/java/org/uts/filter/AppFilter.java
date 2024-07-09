package org.uts.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 系统自定义过滤器
 * */
@WebFilter(urlPatterns="/*", filterName="AppFilter")
@Configuration
@Slf4j
public class AppFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("AppFilter init ...");
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //请求前处理操作
        preHandler(servletRequest, servletResponse);

        // 继续请求处理
        filterChain.doFilter(servletRequest, servletResponse);

        //请求后处理操作
        postHandler(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        log.info("AppFilter destroy ...");
        Filter.super.destroy();
    }

    /**
     * 请求前处理操作
     */
    public void preHandler(ServletRequest servletRequest, ServletResponse servletResponse){
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        //打印请求URI
        log.info("request-url: " + httpServletRequest.getRequestURL());
        ContextHolder.appContext.set(System.currentTimeMillis());

    }

    //请求后处理操作
    public void postHandler(ServletRequest servletRequest, ServletResponse servletResponse){

        //打印请求路径信息
        printRequestUrlInfo(servletRequest, servletResponse);

    }

    /**
     * 打印请求路径信息
     * */
    public void printRequestUrlInfo(ServletRequest servletRequest, ServletResponse servletResponse){
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        Long preTime = (Long)ContextHolder.appContext.get();
        Long postTime = System.currentTimeMillis();
        long costTime = (postTime - preTime) / 1000;
        if(costTime > 3){
            log.info("request-url: " + httpServletRequest.getRequestURL() + " request takes too long, time: " + costTime + " seconds ...");
        }
        else {
            log.info("request-url: " + httpServletRequest.getRequestURL() + " return ...");
        }
    }

}
