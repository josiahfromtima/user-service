package com.tima.platform.resource.account;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.config.CustomValidator;
import com.tima.platform.model.api.ApiResponse;
import com.tima.platform.model.api.request.PasswordRestRecord;
import com.tima.platform.model.api.request.PasswordUpdateRecord;
import com.tima.platform.model.api.request.UserInfluencerRecord;
import com.tima.platform.model.api.request.UserRecord;
import com.tima.platform.model.api.request.signin.UserBrandRecord;
import com.tima.platform.service.UserProfileService;
import com.tima.platform.service.UserSignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.tima.platform.model.api.ApiResponse.buildServerResponse;

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
    private final CustomValidator validator;

    @Value("${email.activation.template}")
    private String activationMailTemplateId;
    @Value("${email.password-reset.template}")
    private String passwordResetTemplateId;

    public Mono<ServerResponse> createUserAccount(ServerRequest request)  {
        Mono<UserRecord> userProfileDtoMono = request.bodyToMono(UserRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("[{}] Create New User Requested", request.remoteAddress().orElse(null));

        return userProfileDtoMono
                .map(signUpService::createUser)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getUserType(ServerRequest request)  {
        log.info("[{}] Get User Type Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(signUpService.getUserType());
    }

    public Mono<ServerResponse> resendOtp(ServerRequest request)  {
        Mono<UserRecord> rendOtpMono = request.bodyToMono(UserRecord.class);
        log.info("[{}] Resend Otp Requested", request.remoteAddress().orElse(null));
        return rendOtpMono
                .map(userDto -> signUpService.resendOtp(userDto.username(), userDto.email(), activationMailTemplateId ))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> passwordReset(ServerRequest request)  {
        String email = request.pathVariable("email");
        log.info("[{}] Send Password Reset Otp Requested", email);
        return buildServerResponse(signUpService.resetPasswordRequest(email, passwordResetTemplateId));
    }


    public Mono<ServerResponse> verifyOtp(ServerRequest request)  {
        String enteredOtp = request.pathVariable("otp");
        log.info("[{}] Verify OTP Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(signUpService.verifyOtp(enteredOtp));
    }


    public Mono<ServerResponse> updatePassword(ServerRequest request)  {
        Mono<PasswordRestRecord> passwordRestRecordMono = request.bodyToMono(PasswordRestRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Password/PIN Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(publicId ->  passwordRestRecordMono
                        .flatMap(restRecord -> signUpService.changePassword(publicId, restRecord)) )
                .flatMap(ApiResponse::buildServerResponse);
    }


    public Mono<ServerResponse> updatePublicPassword(ServerRequest request)  {
        Mono<UserRecord> userCredentialsMono = request.bodyToMono(UserRecord.class);
        PasswordUpdateRecord headers = ApiResponse.getHashAndSalt(request);
        log.info("[{}] Update Password from Public Requested", request.remoteAddress().orElse(null));
        return userCredentialsMono
                .map(userRecord -> signUpService.changePassword(userRecord.publicId(), userRecord.password(), headers))
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> updateBrandProfile(ServerRequest request)  {
        Mono<UserBrandRecord> userCredentialsMono = request.bodyToMono(UserBrandRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Brand User Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(publicId -> userCredentialsMono.map(profile ->
                                userProfileService.updateProfile(publicId, profile) )
                ).flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> updateInfluenceProfile(ServerRequest request)  {
        Mono<UserInfluencerRecord> userCredentialsMono = request.bodyToMono(UserInfluencerRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update Influencer User Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .flatMap(publicId -> userCredentialsMono.map(profile ->
                        userProfileService.updateProfile(publicId, profile) )
                ).flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> createBrandProfile(ServerRequest request)  {
        Mono<UserBrandRecord> userProfileMono = request.bodyToMono(UserBrandRecord.class)
                .doOnNext(validator::validateEntries);
        log.info("[{}] Create User Brand Profile Requested", request.remoteAddress().orElse(null));
        return userProfileMono
                .map( userProfileService::createUserProfile )
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> createInfluenceProfile(ServerRequest request)  {
        Mono<UserInfluencerRecord> userProfileMono
                = request.bodyToMono(UserInfluencerRecord.class).doOnNext(validator::validateEntries);
        log.info("[{}] Create User Influence Profile Requested", request.remoteAddress().orElse(null));
        return userProfileMono
                .map( userProfileService::createUserProfile )
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getUserProfile(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Fetch User Profile Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(userProfileService::getUserProfile)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> getUserProfileByAdmin(ServerRequest request)  {
        String publicId = request.pathVariable("publicId");
        log.info("[{}] Fetch User Profile By Admin Requested", request.remoteAddress().orElse(null));
        return buildServerResponse(userProfileService.getUserProfileByAdmin(publicId));
    }
    public Mono<ServerResponse> deActivateUser(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] De-Activated User Account Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(signUpService::deActivateUser)
                .flatMap(ApiResponse::buildServerResponse);
    }

    public Mono<ServerResponse> updateProfilePicture(ServerRequest request)  {
        String pictureName = request.pathVariable("pictureName");
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update User Account Profile Picture Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(publicId -> userProfileService.updateProfilePicture(publicId, pictureName))
                .flatMap(ApiResponse::buildServerResponse);
    }
    public Mono<ServerResponse> updateUserDocument(ServerRequest request)  {
        String pictureName = request.pathVariable("documentName");
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("[{}] Update User Account Document Requested", request.remoteAddress().orElse(null));
        return jwtAuthToken
                .map(ApiResponse::getPublicIdFromToken)
                .map(publicId -> userProfileService.updateDocument(publicId, pictureName))
                .flatMap(ApiResponse::buildServerResponse);
    }
}
