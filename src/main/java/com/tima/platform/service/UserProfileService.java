package com.tima.platform.service;

import com.tima.platform.converter.UserProfileConverter;
import com.tima.platform.domain.User;
import com.tima.platform.domain.UserProfile;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.PasswordRestRecord;
import com.tima.platform.model.api.request.UserInfluencerRecord;
import com.tima.platform.model.api.request.UserProfileRecord;
import com.tima.platform.model.api.request.signin.UserBrandRecord;
import com.tima.platform.model.api.response.FullUserProfileRecord;
import com.tima.platform.model.constant.NotificationSetting;
import com.tima.platform.model.constant.UserType;
import com.tima.platform.repository.UserProfileRepository;
import com.tima.platform.repository.UserRepository;
import com.tima.platform.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String INVALID_USER = "Unauthorized User Action";
    private static final String USER_PROFILE_MSG = "User Profile Detail Executed Successfully";
    private static final String DUPLICATE_CREATION = "User with public Id already exist";

    public Mono<AppResponse> createUserProfile(UserInfluencerRecord userProfile) {
        return checkUserExistence(userProfile.publicId())
                .flatMap(user -> profileRepository.save(UserProfileConverter
                        .mapToEntity(UserProfileRecord.builder()
                                .firstName(userProfile.firstName())
                                .middleName(userProfile.middleName())
                                .lastName(userProfile.lastName())
                                .phoneNumber(userProfile.phoneNumber())
                                .email(userProfile.email())
                                .userType(UserType.INFLUENCER)
                                .notificationSetting(AppUtil.gsonInstance()
                                        .toJson(NotificationSetting.builder().build()) )
                                .userid(user.getId())
                                .build()))
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    public Mono<AppResponse> createUserProfile(UserBrandRecord userProfile) {
        return checkUserExistence(userProfile.publicId())
                .flatMap(user -> profileRepository.save(UserProfileConverter
                        .mapToEntity(UserProfileRecord.builder()
                                .companyName(userProfile.companyName())
                                .website(userProfile.website())
                                .email(userProfile.email())
                                .phoneNumber(userProfile.phoneNumber())
                                .userType(UserType.BRAND)
                                .notificationSetting(AppUtil.gsonInstance()
                                        .toJson(NotificationSetting.builder().build()) )
                                .userid(user.getId())
                                .build()))
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    public Mono<AppResponse> updateProfile(String publicId, UserBrandRecord userRecord) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> getUserProfileFromDB(user.getId())
                        .flatMap(userProfile -> {
                            userProfile.setCompanyName(userRecord.companyName());
                            userProfile.setEmail(userRecord.email());
                            userProfile.setPhoneNumber(userRecord.phoneNumber());
                            userProfile.setWebsite(userRecord.website());
                            return profileRepository.save(userProfile);
                        })
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    public Mono<AppResponse> updateProfile(String publicId, UserInfluencerRecord userRecord) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> getUserProfileFromDB(user.getId())
                        .flatMap(userProfile -> {
                            userProfile.setFirstName(userRecord.firstName());
                            userProfile.setMiddleName(userRecord.middleName());
                            userProfile.setLastName(userRecord.lastName());
                            userProfile.setPhoneNumber(userRecord.phoneNumber());
                            userProfile.setEmail(userRecord.email());
                            return profileRepository.save(userProfile);
                        })
                ).map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    public Mono<AppResponse> getUserProfile(String publicId) {
        System.out.println(publicId);
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> getUserProfileFromDB(user.getId())
                        .map(userProfile -> FullUserProfileRecord.builder()
                                .username(user.getUsername())
                                .publicId(user.getPublicId())
                                .profile(UserProfileConverter.mapToRecord(userProfile))
                                .build()
                        )
                ).map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }
    private Mono<UserProfile> getUserProfileFromDB(Integer id) {
        return profileRepository.findById(getOrDefault(id));
    }

    private Mono<User> checkUserExistence(String publicId) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> checkForProfile(user)
                        .flatMap(isFound -> isFound ?
                                handleOnErrorResume(new AppException(DUPLICATE_CREATION), BAD_REQUEST.value())
                                : Mono.just(user))
                );
    }

    private Mono<Boolean> checkForProfile(User user) {
        return profileRepository.findById(getOrDefault(user.getId()))
                .map(userProfile -> true)
                .switchIfEmpty( Mono.just(false) );
    }

    private Integer getOrDefault(Integer id) {
        return Objects.isNull(id) ? -1 : id;
    }
}
