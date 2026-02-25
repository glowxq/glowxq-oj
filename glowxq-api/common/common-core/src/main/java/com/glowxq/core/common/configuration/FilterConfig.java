package com.glowxq.core.common.configuration;

import com.glowxq.core.common.filter.RRLogFilter;
import com.glowxq.core.common.filter.RepeatableFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter配置
 *
 * @author Lion Li
 */
@Configuration
public class FilterConfig {



    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean repeatableFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RepeatableFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FilterRegistrationBean RRLogFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RRLogFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(FilterRegistrationBean.LOWEST_PRECEDENCE);
        return registration;
    }
}
