package com.gurumee.demoboardpostapi.configs.oauth;

import com.gurumee.demoboardpostapi.commons.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class TestAuthServerConfig extends AuthorizationServerConfigurerAdapter {
    private final AppProperties appProperties;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(appProperties.getClientId())
                .secret(passwordEncoder.encode(appProperties.getClientSecret()))
                .authorizedGrantTypes("password")
                .scopes("read", "write")
        ;
    }
}
