package com.tima.platform.resource.address;

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
 * @Date: 12/11/23
 */
@Configuration
public class UserAddressResourceConfig {
    public static final String API_V1_URL = "/v1";
    public static final String SIGNED_URL = API_V1_URL + "/signed/url";
    public static final String COUNTRY_BASE = API_V1_URL + "/countries";
    public static final String INDUSTRIES_BASE = API_V1_URL + "/industries/{publicId}";
    public static final String ADDRESS_BASE = API_V1_URL + "/address";
    public static final String PROFILE_PICTURE = SIGNED_URL + "/pics/{keyName}/{extension}";
    public static final String USER_DOCUMENT = SIGNED_URL + "/docs/{keyName}/{extension}";
    public static final String COUNTRY_BY_NAME = COUNTRY_BASE + "/name/{name}";
    public static final String POST_COUNTRY = COUNTRY_BASE ;
    public static final String POST_COUNTRY_LIST = COUNTRY_BASE + "/multiple";
    public static final String PUT_COUNTRY = COUNTRY_BASE;
    public static final String DELETE_COUNTRY_BY_NAME = COUNTRY_BASE + "/{name}";
    public static final String GET_ADDRESS = ADDRESS_BASE;
    public static final String UPDATE_ADDRESS = ADDRESS_BASE;
    public static final String DELETE_ADDRESS = ADDRESS_BASE;
    @Bean
    public RouterFunction<ServerResponse> addressEndpointHandler(UserAddressResourceHandler handler) {
        return route()
                .GET(PROFILE_PICTURE, accept(MediaType.APPLICATION_JSON), handler::getSignedProfilePicture)
                .GET(USER_DOCUMENT, accept(MediaType.APPLICATION_JSON), handler::getSignedUserDocument)
                .GET(COUNTRY_BASE, accept(MediaType.APPLICATION_JSON), handler::getCountries)
                .GET(COUNTRY_BY_NAME, accept(MediaType.APPLICATION_JSON), handler::getCountry)
                .GET(GET_ADDRESS, accept(MediaType.APPLICATION_JSON), handler::getUserAddress)
                .POST(POST_COUNTRY, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::saveNewCountry)
                .POST(POST_COUNTRY_LIST, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::saveCountries)
                .POST(ADDRESS_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addUserAddress)
                .PUT(PUT_COUNTRY, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateCountry)
                .PUT(INDUSTRIES_BASE, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::addUserIndustrySelection)
                .PUT(UPDATE_ADDRESS, accept(MediaType.APPLICATION_JSON)
                        .and(contentType(MediaType.APPLICATION_JSON)), handler::updateUserAddress)
                .DELETE(DELETE_COUNTRY_BY_NAME, accept(MediaType.APPLICATION_JSON), handler::deleteCountry)
                .DELETE(DELETE_ADDRESS, accept(MediaType.APPLICATION_JSON), handler::deleteUserAddress)
                .build();
    }
}
