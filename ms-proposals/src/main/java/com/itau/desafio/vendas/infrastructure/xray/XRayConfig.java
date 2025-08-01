package com.itau.desafio.vendas.infrastructure.xray;

import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;

import jakarta.servlet.Filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnProperty(name = "aws.xray.enabled", havingValue = "true")
public class XRayConfig {

    @Bean
    public FilterRegistrationBean<Filter> xrayFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();

        Filter xrayServletFilter = new AWSXRayServletFilter("ms-proposals");
        registrationBean.setFilter(xrayServletFilter);

        registrationBean.addUrlPatterns("/*");

        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registrationBean;
    }

}