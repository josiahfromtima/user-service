package com.tima.platform.service.template;

import com.tima.platform.exception.AppException;
import com.tima.platform.model.constant.UserType;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public abstract class UserSignupTemplate<S,T, R> {
    public final Mono<R> createUser(S supplier) {
        return createUserAccount(supplier)
                .flatMap(newAccount ->
                                assignDefaultPermissionToUser(newAccount, supplier)
                                                .flatMap(newUserAccount ->
                                                        sendActivationMail(newUserAccount, supplier))
                );
    }

    protected abstract Mono<T> createUserAccount(S accountToCreate) throws AppException;
    protected abstract Mono<T> assignDefaultPermissionToUser(T newAccount, S accountToCreate) throws AppException;
    protected abstract Mono<R> sendActivationMail(T newAccount, S accountToCreate) throws AppException;

}
