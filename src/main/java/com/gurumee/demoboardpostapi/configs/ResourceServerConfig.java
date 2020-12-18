package com.gurumee.demoboardpostapi.configs;

import com.gurumee.demoboardpostapi.commons.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    private final AppProperties appProperties;

    @Bean
    @Profile({"local", "prod"})
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(appProperties.getCheckTokenEndpointUrl());
        tokenService.setClientId(appProperties.getClientId());
        tokenService.setClientSecret(appProperties.getClientSecret());
        return tokenService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                    .and()
                .authorizeRequests()
                    .antMatchers(HttpMethod.OPTIONS).permitAll()
                    .antMatchers(HttpMethod.GET).permitAll()
                    .mvcMatchers(HttpMethod.GET, "/api/**").permitAll()
                    .mvcMatchers(HttpMethod.POST, "/api/**").access("#oauth2.hasScope('write')")
                    .mvcMatchers(HttpMethod.PUT, "/api/**").access("#oauth2.hasScope('write')")
                    .mvcMatchers(HttpMethod.DELETE, "/api/**").access("#oauth2.hasScope('write')")
                .anyRequest().authenticated()
                    .and()
                .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
        ;
    }
}
