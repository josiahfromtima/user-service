package com.tima.platform.resource.signin;

import com.tima.platform.model.api.request.signin.SignInRequest;
import com.tima.platform.service.SignInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignInResourceHandler {
    private final SignInService signInService;

    private static final String X_FORWARD_FOR = "X-Forwarded-For";

    public Mono<ServerResponse> login(ServerRequest request)  {
        String authBasicHeader = request.headers().firstHeader("Authorization");
        log.info("[{}] Login Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(signInService.login(new SignInRequest(authBasicHeader)));
    }
    public Mono<ServerResponse> getNewAccessToken(ServerRequest request)  {
        String authRefreshHeader = request.headers().firstHeader("Refresh-Token");
        log.info("[{}] Refresh Token Requested", request.headers().firstHeader(X_FORWARD_FOR));
        return buildServerResponse(signInService.getAccessTokenFromRefreshToken(authRefreshHeader));
    }

}
