package com.tima.platform.service.template;

import com.tima.platform.exception.AppException;
import reactor.core.publisher.Mono;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public abstract class UserSignInTemplate<S, R, T> {
    public Mono<R> login(S request) {
        try {
            return processBasicHeader(request)
                    .flatMap(this::validateUser)
                    .flatMap(this::getAccessToken);
        }catch (Exception ex) {
            return handleOnErrorResume(new AppException("Could Not sign In "+ex.getMessage()), BAD_REQUEST.value());
        }
    }

    protected abstract Mono<T> decodeCredentials(S signInRequest) throws AppException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;
    protected abstract Mono<T> processBasicHeader(S signInRequest) throws AppException;
    protected abstract Mono<T> validateUser(T credentials) throws AppException;
    protected abstract Mono<R> getAccessToken(T credentials) throws AppException;
}
