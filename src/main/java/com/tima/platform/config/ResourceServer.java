package com.tima.platform.config;

import com.tima.platform.config.helper.CombinedClaimConverter;
import com.tima.platform.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class ResourceServer {
    private final CombinedClaimConverter converter;
    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws AppException {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .pathMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .anyExchange()
                                .authenticated()
                )
                .oauth2ResourceServer(oAuth2 -> oAuth2.jwt(jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(converter)));
        return http.build();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v1/login",
            "/v1/login/reconnect",
            "/v1/profile/send/**",
            "/v1/account",
            "/v1/account/verify/**",
            "/v1/account/otp/resend",
            "/v1/account/password/update",
            "/v1/profile/brand",
            "/v1/profile/influencer",
            "/v1/profile/types",
            "/v1/auth/new",
            "/v1/captcha/**"
            // other public endpoints of your API may be appended to this array
    };
}
