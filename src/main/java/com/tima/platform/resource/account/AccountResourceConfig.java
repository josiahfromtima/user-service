package com.tima.platform.resource.account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Configuration
public class AccountResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String USER_ACCOUNT = API_V1_URL + "/account";
    public static final String USER_PROFILE = API_V1_URL + "/profile";
    public static final String USER_BRAND_ACCOUNT = USER_PROFILE + "/brand";
    public static final String USER_INFLUENCER_ACCOUNT = USER_PROFILE + "/influencer";
    public static final String GET_USER_PROFILE = USER_PROFILE;
    public static final String USER_TYPE = USER_PROFILE + "/types";
    public static final String UPDATE_BRAND_ACCOUNT = USER_PROFILE + "/brand";
    public static final String UPDATE_INFLUENCER_ACCOUNT = USER_PROFILE + "/influencer";
    public static final String UPDATE_USER_PASSWORD = USER_ACCOUNT + "/password";
    public static final String UPDATE_PUBLIC_USER_PASSWORD = USER_ACCOUNT + "/password/update";
    public static final String VERIFY_OTP = USER_ACCOUNT + "/verify/{otp}";
    public static final String RESEND_OTP = USER_ACCOUNT + "/otp/resend";
    public static final String DE_ACTIVATE_USER = USER_ACCOUNT + "/deactivate";
    public static final String UPDATE_PROFILE_PICS = USER_PROFILE + "/picture/{pictureName}";
    public static final String UPDATE_DOCUMENT = USER_PROFILE + "/document/{documentName}";

    @Bean
    public RouterFunction<ServerResponse> profileEndpointHandler(AccountResourceHandler handler) {
        return route()
                .POST(USER_ACCOUNT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::createUserAccount)
                .POST(USER_BRAND_ACCOUNT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::createBrandProfile)
                .POST(USER_INFLUENCER_ACCOUNT, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::createInfluenceProfile)
                .GET(USER_TYPE, accept(MediaType.APPLICATION_JSON), handler::getUserType)
                .GET(GET_USER_PROFILE, accept(MediaType.APPLICATION_JSON), handler::getUserProfile)
                .GET(VERIFY_OTP, accept(MediaType.APPLICATION_JSON), handler::verifyOtp)
                .PUT(DE_ACTIVATE_USER, accept(MediaType.APPLICATION_JSON), handler::deActivateUser)
                .POST(RESEND_OTP, accept(MediaType.APPLICATION_JSON), handler::resendOtp)
                .PUT(UPDATE_BRAND_ACCOUNT, accept(MediaType.APPLICATION_JSON), handler::updateBrandProfile)
                .PUT(UPDATE_INFLUENCER_ACCOUNT, accept(MediaType.APPLICATION_JSON), handler::updateInfluenceProfile)
                .PUT(UPDATE_USER_PASSWORD, accept(MediaType.APPLICATION_JSON), handler::updatePassword)
                .PUT(UPDATE_PUBLIC_USER_PASSWORD, accept(MediaType.APPLICATION_JSON), handler::updatePublicPassword)
                .PUT(UPDATE_PROFILE_PICS, accept(MediaType.APPLICATION_JSON), handler::updateProfilePicture)
                .PUT(UPDATE_DOCUMENT, accept(MediaType.APPLICATION_JSON), handler::updateUserDocument)
                .build();
    }
}
