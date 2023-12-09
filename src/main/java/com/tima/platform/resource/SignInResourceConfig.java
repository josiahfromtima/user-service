package com.tima.platform.resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Configuration
public class SignInResourceConfig {
    public static final String API_V1_URL = "/v1";

    public static final String POST_SIGN_IN = API_V1_URL + "/login";
    public static final String POST_REFRESH_TOKEN = API_V1_URL + "/login/reconnect";
    @Bean
    public RouterFunction<ServerResponse> signInEndpointHandler(SignInResourceHandler handler) {
        return route()
                .POST(POST_SIGN_IN, accept(MediaType.APPLICATION_JSON), handler::login)
                .POST(POST_REFRESH_TOKEN, accept(MediaType.APPLICATION_JSON), handler::getNewAccessToken)
                .build();
    }

}
