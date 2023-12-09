package com.tima.platform.model.constant;

import java.util.Arrays;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface AppConstant {
    String MEDIA_TYPE_JSON = "application/json";
    String MEDIA_TYPE_FORM_ENCODED = "application/x-www-form-urlencoded";
    String AUTHORIZATION = "Authorization";
    String CONTENT_TYPE = "Content-Type";
    String ACCEPT = "Accept";
    String GRANT_TYPE = "grant_type";
    String USERNAME_PARAM = "username";
    String PASSWORD_PARAM = "password";
    String SCOPE = "scope";
    String BASIC_AUTH_PATTERN = "%s:%s";
    String ALLOWED_GRANT_TYPES = "authorization_code, refresh_token, client_credentials";
    String AUTHENTICATION_METHODS = "client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt,none";
    String DEFAULT_AUTHENTICATION_METHODS = "client_secret_basic";
    String USER_CREDENTIALS_ID = "Auth-ID";

    String ACTIVATION_MAIL = "emails/activate";
    String PASSWORD_RESET_MAIL = "emails/reset";
    String TOKEN_NAME = "token";
    String USER_CONTEXT = "user";
    String FULL_NAME = "fullName";
    String APP_URL = "appUrl";

    interface Role {
        String DEFAULT = "D";
    }

    default String getGrantTypes() {
        return ALLOWED_GRANT_TYPES;
    }

    default boolean validateGrantType(String grantType) {
        String[] grantTypes = ALLOWED_GRANT_TYPES.split(",");
        return Arrays.stream(grantTypes)
                .anyMatch(s -> s.equalsIgnoreCase( grantType ) );
    }
}
