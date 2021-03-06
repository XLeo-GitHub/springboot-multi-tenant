package org.walter.base.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.walter.base.service.InitTenantIdInterceptor;

@Component
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Autowired
    private InitTenantIdInterceptor initTenantIdInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(initTenantIdInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/ping");
        super.addInterceptors(registry);
    }
}
