package com.itau.desafio.vendas.audit.infrastructure.aws.xray;

import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.spring.aop.AbstractXRayInterceptor;

import jakarta.servlet.Filter;

import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnProperty(name = "aws.xray.enabled", havingValue = "true")
@ConditionalOnProperty(name = "aws.xray.enabled", havingValue = "true")
public class XRayConfig extends AbstractXRayInterceptor {

    @Bean
    public FilterRegistrationBean<Filter> xrayFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        Filter xrayServletFilter = new AWSXRayServletFilter("ms-proposals");
        registrationBean.setFilter(xrayServletFilter);

        registrationBean.addUrlPatterns("/*");

        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

    @Override
    @Pointcut("@annotation(com.itau.desafio.vendas.infrastructure.xray.XRayTrace)")
    protected void xrayEnabledClasses() {
    }
}