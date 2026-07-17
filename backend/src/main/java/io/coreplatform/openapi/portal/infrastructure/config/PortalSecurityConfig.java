package io.coreplatform.openapi.portal.infrastructure.config;

import io.coreplatform.openapi.portal.infrastructure.config.PortalAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PortalSecurityConfig {

    private final PortalJwtService portalJwtService;

    @Bean
    public FilterRegistrationBean<PortalAuthFilter> portalAuthFilter() {
        FilterRegistrationBean<PortalAuthFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PortalAuthFilter(portalJwtService));
        registration.addUrlPatterns("/api/v1/portal/*");
        registration.setOrder(1);
        return registration;
    }
}