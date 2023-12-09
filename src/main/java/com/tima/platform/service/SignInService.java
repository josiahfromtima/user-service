package com.tima.platform.service;

import com.tima.platform.config.client.HttpConnectorService;
import com.tima.platform.domain.CustomRefreshToken;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.signin.AccessToken;
import com.tima.platform.model.api.request.signin.SignInRequest;
import com.tima.platform.model.security.UsernamePasswordToken;
import com.tima.platform.repository.CustomRefreshTokenRepository;
import com.tima.platform.repository.UserRepository;
import com.tima.platform.service.encoding.MessageEncoding;
import com.tima.platform.service.template.UserSignInTemplate;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.cipher.Crypto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.constant.AppConstant.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignInService extends UserSignInTemplate<SignInRequest, AppResponse, UsernamePasswordToken> {
    private final HttpConnectorService connectorService;
    private final UserRepository userRepository;
    private final CustomRefreshTokenRepository refreshTokenRepository;
    @Value("${tima.auth.server.url}")
    private String authServerUrl;
    @Value("${tima.client.id}")
    private String clientId;
    @Value("${tima.client.secret}")
    private String clientSecret;
    @Value("${tima.key}")
    private String aesKey;
    @Value("${tima.iv}")
    private String aesIv;

    private static final String DEACTIVATED_USER_MSG = "User Account is deactivated";

    private static final String USER_ACCOUNT_MSG = "User Request Executed Successfully";
    private static final String REFRESH_TOKEN_ERROR = "Refresh token could be validated. Please contact the administrator";
    private static final String REFRESH_TOKEN_EXPIRED = "Access token has expired. Login again";
    private static final String REFRESH_TOKEN_VALID = "Access token is still valid";

    @Override
    protected Mono<UsernamePasswordToken> processBasicHeader(SignInRequest signInRequest) throws AppException {
        String base64Decoded = MessageEncoding.base64Decoding(
                signInRequest.
                        encodedCredentials()
                        .replace("Basic ", ""));
        String[] tokens = base64Decoded.split(":");
        return Mono.just(UsernamePasswordToken.builder()
                .username(tokens[0])
                .password(tokens[1])
                .build());
    }

    @Override
    protected Mono<UsernamePasswordToken> decodeCredentials(SignInRequest signInRequest)
            throws AppException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        Crypto.setKey(aesKey);
        Crypto.setIv(aesIv.replace("%3D", "="));
        String decodedCredentials = Crypto.decrypt(signInRequest.encodedCredentials());
        String[] tokens = decodedCredentials.split(":");
        return Mono.just(UsernamePasswordToken.builder()
                .username(tokens[0])
                .password(tokens[1])
                .build()) ;
    }

    @Override
    protected Mono<UsernamePasswordToken> validateUser(UsernamePasswordToken credentials) throws AppException {
        return userRepository.findByUsernameAndEnabled(credentials.getUsername(), true)
                .map(user -> credentials)
                .switchIfEmpty( handleOnErrorResume(new AppException(DEACTIVATED_USER_MSG), BAD_REQUEST.value()) );
    }

    @Override
    protected Mono<AppResponse> getAccessToken(UsernamePasswordToken credentials) throws AppException {
        MultiValueMap<String, String> formData = formData(credentials);
        return connectorService.postForm(authServerUrl, formData, headers(formData), AccessToken.class)
                .flatMap(accessToken -> createRefreshToken(accessToken, credentials))
                .map(accessToken -> AppUtil.buildAppResponse(accessToken, USER_ACCOUNT_MSG));
    }

    public Mono<AppResponse> getAccessTokenFromRefreshToken(String refreshToken) {
            return refreshTokenRepository.findById(MessageEncoding.base64Decoding(refreshToken))
                    .flatMap(customRefreshToken -> {
                        try {
                            if(Instant.now().isBefore(customRefreshToken.getExpiresOn()))
                                return handleOnErrorResume(new AppException(REFRESH_TOKEN_VALID), UNAUTHORIZED.value());
                            return decodeCredentials(new SignInRequest(customRefreshToken.getEncodedId()));
                        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                            return handleOnErrorResume(new AppException(REFRESH_TOKEN_ERROR), BAD_REQUEST.value());
                        }
                    }).flatMap(this::getAccessToken)
                    .switchIfEmpty(handleOnErrorResume(new AppException(REFRESH_TOKEN_EXPIRED), UNAUTHORIZED.value()));
    }

    private Mono<AccessToken> createRefreshToken(AccessToken accessToken, UsernamePasswordToken credentials) {
        try {
            String tokenId = AppUtil.getUUID();
            long timeToLive = accessToken.getTimeToLive() == 0 ? 0L : accessToken.getTimeToLive() / 60;
            return encodeAndEncrypt(new SignInRequest(
                    String.format(BASIC_AUTH_PATTERN, credentials.getUsername(), credentials.getPassword()))
            ).flatMap(encryptedCredentials -> refreshTokenRepository.save(CustomRefreshToken.builder()
                    .generatedId(tokenId)
                    .encodedId(encryptedCredentials)
                    .expiresOn(Instant.now().plus(timeToLive, ChronoUnit.MINUTES))
                    .build())
            ).map(customRefreshToken -> {
                accessToken.setRefreshToken(tokenId);
                return accessToken;
            });
        }catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            return handleOnErrorResume(new AppException(REFRESH_TOKEN_ERROR), BAD_REQUEST.value());
        }
    }

    private Mono<String> encodeAndEncrypt(SignInRequest signInRequest)
            throws AppException,
            InvalidAlgorithmParameterException,
            NoSuchPaddingException,
            IllegalBlockSizeException,
            NoSuchAlgorithmException,
            BadPaddingException,
            InvalidKeyException {
        Crypto.setKey(aesKey);
        Crypto.setIv(aesIv.replace("%3D", "="));
        String encryptedCredentials = Crypto.encrypt(signInRequest.encodedCredentials());
        return Mono.just(encryptedCredentials) ;
    }

    private MultiValueMap<String, String> formData(UsernamePasswordToken userCredentials) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.put(GRANT_TYPE, Collections.singletonList("client_credentials"));
        form.put(USERNAME_PARAM, Collections.singletonList(userCredentials.getUsername()));
        form.put(PASSWORD_PARAM, Collections.singletonList(userCredentials.getPassword()));
        return form;
    }

    private Map<String, String> headers(MultiValueMap<String, String> additionalHeaders) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MEDIA_TYPE_FORM_ENCODED);
        headers.put(ACCEPT, MEDIA_TYPE_JSON);
        headers.put(AUTHORIZATION, "Basic " +
                MessageEncoding.base64Encoding(String.format(BASIC_AUTH_PATTERN, clientId, clientSecret)) );
        headers.put(USER_CREDENTIALS_ID, convertToHeaders(additionalHeaders.toSingleValueMap()) );
        additionalHeaders.remove(USERNAME_PARAM);
        additionalHeaders.remove(PASSWORD_PARAM);
        return headers;
    }

    private String convertToHeaders(Map<String, String> credentials) {
        return MessageEncoding.base64Encoding(
                String.format(BASIC_AUTH_PATTERN, credentials.get(USERNAME_PARAM), credentials.get(PASSWORD_PARAM)));
    }

}
