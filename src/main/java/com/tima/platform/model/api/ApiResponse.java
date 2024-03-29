package com.tima.platform.model.api;

import com.tima.platform.model.api.request.PasswordUpdateRecord;
import com.tima.platform.util.ReportSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/15/23
 */
public class ApiResponse {
    private ApiResponse() {}

    public static Mono<ServerResponse> buildServerResponse(Mono<AppResponse> response) {
        try {
            return response
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        }catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public static String getPublicIdFromToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes()
                .getOrDefault("public_id", "")
                .toString();
    }

    public static PasswordUpdateRecord getHashAndSalt(ServerRequest request) {
        return PasswordUpdateRecord.builder()
                .hash(request.headers().firstHeader("hash"))
                .salt(request.headers().firstHeader("salt"))
                .build();
    }

    public static ReportSettings reportSettings(ServerRequest request) {
        return ReportSettings.instance()
                .page(Integer.parseInt(request.queryParam("page").orElse("0")))
                .size(Integer.parseInt(request.queryParam("size").orElse("10")))
                .sortIn(request.queryParam("sortIn").orElse("asc"))
                .sortBy(request.queryParam("sortBy").orElse("createdOn"));
    }
}
