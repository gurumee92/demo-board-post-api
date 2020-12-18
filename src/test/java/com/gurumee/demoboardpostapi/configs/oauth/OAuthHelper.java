package com.gurumee.demoboardpostapi.configs.oauth;

import com.gurumee.demoboardpostapi.commons.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuthHelper {

    private final ClientDetailsService clientDetailsService;
    private final AuthorizationServerTokenServices tokenServices;
    private final AppProperties appProperties;

    public RequestPostProcessor bearerToken(String id) {
        return mockRequest -> {
            OAuth2AccessToken token = createAccessToken(id);
            mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
            return mockRequest;
        };
    }

    private OAuth2AccessToken createAccessToken(String id) {
        String clientId = appProperties.getClientId();
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        Collection<GrantedAuthority> authorities = client.getAuthorities();
        Set<String> resourceIds = client.getResourceIds();
        Set<String> scopes = client.getScope();

        Map<String, String> requestParameters = Collections.emptyMap();
        boolean approved = true;
        String redirectUrl = null;
        Set<String> responseTypes = Collections.emptySet();
        Map<String, Serializable> extensionProperties = Collections.emptyMap();

        OAuth2Request oAuth2Request = new OAuth2Request(requestParameters, clientId, authorities, approved, scopes,
                resourceIds, redirectUrl, responseTypes, extensionProperties);

        User userPrincipal = new User("test_user" + id, "test_pass", true, true, true, true, authorities);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
        OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);
        return tokenServices.createAccessToken(auth);
    }
}
