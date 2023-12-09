package com.tima.platform.resource;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.request.PasswordRestRecord;
import com.tima.platform.model.api.request.UserInfluencerRecord;
import com.tima.platform.model.api.request.UserRecord;
import com.tima.platform.model.api.request.signin.UserBrandRecord;
import com.tima.platform.service.UserProfileService;
import com.tima.platform.service.UserSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountResourceHandler {
    private final UserSignUpService signUpService;
    private final UserProfileService userProfileService;
    private final Validator validator;

    public Mono<ServerResponse> createUserAccount(ServerRequest request)  {
        Mono<UserRecord> userProfileDtoMono = request.bodyToMono(UserRecord.class);
        log.info("[{}] Create New User Requested", request.remoteAddress().orElse(null));

        try {
            return userProfileDtoMono.flatMap(signUpService::createUser)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }


    public Mono<ServerResponse> getUserType(ServerRequest request)  {
        log.info("[{}] Get User Type Requested", request.remoteAddress().orElse(null));

        try {
            return signUpService.getUserType()
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> resendOtp(ServerRequest request)  {
        Mono<UserRecord> rendOtpMono = request.bodyToMono(UserRecord.class);
        log.info("[{}] Resend Otp Requested", request.remoteAddress().orElse(null));

        try {
            return rendOtpMono.flatMap(userDto ->
                    signUpService.resendOtp(userDto.username(), userDto.email() ))
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }


    public Mono<ServerResponse> verifyOtp(ServerRequest request)  {
        String enteredOtp = request.pathVariable("otp");
        log.info("[{}] Verify OTP Requested", request.remoteAddress().orElse(null));

        try {
            return signUpService.verifyOtp(enteredOtp)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }


    public Mono<ServerResponse> updatePassword(ServerRequest request)  {
        Mono<PasswordRestRecord> passwordRestRecordMono = request.bodyToMono(PasswordRestRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Password/PIN Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(publicId ->  passwordRestRecordMono
                                    .flatMap(restRecord -> signUpService.changePassword(publicId, restRecord))
                    )
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }


    public Mono<ServerResponse> updatePublicPassword(ServerRequest request)  {
        Mono<UserRecord> userCredentialsMono = request.bodyToMono(UserRecord.class);
        log.info("[{}] Update Password from Public Requested", request.remoteAddress().orElse(null));

        try {
            return userCredentialsMono.flatMap(userRecord ->
                            signUpService.changePassword(userRecord.publicId(), userRecord.password()) )
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> updateBrandProfile(ServerRequest request)  {
        Mono<UserBrandRecord> userCredentialsMono = request.bodyToMono(UserBrandRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Brand User Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(publicId -> userCredentialsMono.flatMap(profile ->
                            userProfileService.updateProfile(publicId, profile)
                            .flatMap(ServerResponse.ok()::bodyValue)
                            .switchIfEmpty(ServerResponse.badRequest().build())
                    ) );
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> updateInfluenceProfile(ServerRequest request)  {
        Mono<UserInfluencerRecord> userCredentialsMono = request.bodyToMono(UserInfluencerRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Influencer User Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(publicId -> userCredentialsMono.flatMap(profile ->
                            userProfileService.updateProfile(publicId, profile)
                            .flatMap(ServerResponse.ok()::bodyValue)
                            .switchIfEmpty(ServerResponse.badRequest().build())
                    ) );
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> createBrandProfile(ServerRequest request)  {
        Mono<UserBrandRecord> userProfileMono = request.bodyToMono(UserBrandRecord.class).doOnNext(this::validate);
        log.info("[{}] Create User Brand Profile Requested", request.remoteAddress().orElse(null));

        try {
            return userProfileMono
                    .flatMap(userProfileService::createUserProfile)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> createInfluenceProfile(ServerRequest request)  {
        Mono<UserInfluencerRecord> userProfileMono
                = request.bodyToMono(UserInfluencerRecord.class).doOnNext(this::validate);
        log.info("[{}] Create User Influence Profile Requested", request.remoteAddress().orElse(null));

        try {
            return userProfileMono
                    .flatMap(userProfileService::createUserProfile)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> getUserProfile(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Fetch User Profile Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(userProfileService::getUserProfile)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> deActivateUser(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] De-Activated User Account Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(signUpService::deActivateUser)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    private void validate(UserBrandRecord profileDto) {
        Errors errors = new BeanPropertyBindingResult(profileDto, "profileDto");
        validator.validate(profileDto, errors);
        if(errors.hasErrors()) throw new ServerWebInputException(errors.toString());
    }
    private void validate(UserInfluencerRecord profileDto) {
        Errors errors = new BeanPropertyBindingResult(profileDto, "profileDto");
        validator.validate(profileDto, errors);
        if(errors.hasErrors()) throw new ServerWebInputException(errors.toString());
    }

    private String getPublicIdFromToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes()
                .getOrDefault("public_id", "")
                .toString();
    }

}
