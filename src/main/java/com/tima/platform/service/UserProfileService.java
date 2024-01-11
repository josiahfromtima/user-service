package com.tima.platform.service;

import com.tima.platform.converter.UserProfileConverter;
import com.tima.platform.domain.User;
import com.tima.platform.domain.UserProfile;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.NotificationSettingRecord;
import com.tima.platform.model.api.request.UserInfluencerRecord;
import com.tima.platform.model.api.request.UserProfileRecord;
import com.tima.platform.model.api.request.signin.UserBrandRecord;
import com.tima.platform.model.api.response.FullUserProfileRecord;
import com.tima.platform.model.constant.NotificationSetting;
import com.tima.platform.model.constant.UserType;
import com.tima.platform.repository.UserProfileRepository;
import com.tima.platform.repository.UserRepository;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.*;
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
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;

    @Value("${aws.s3.url}")
    private String baseResourceUrl;
    @Value("${aws.s3.resource.profile}")
    private String profileFolder;
    @Value("${aws.s3.resource.document}")
    private String documentFolder;
    @Value("${aws.s3.image-ext}")
    private String defaultFileExtension;

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
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> createUserProfile(UserBrandRecord userProfile) {
        return checkUserExistence(userProfile.publicId())
                .doOnNext(System.out::println)
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
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND)
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
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_INFLUENCER)
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
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getUserProfile(String publicId) {
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
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> getUserProfileByAdmin(String userPublicId) {
        return userRepository.findByPublicId(userPublicId)
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

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> updateProfilePicture(String publicId, String pictureName) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> getUserProfileFromDB(user.getId()))
                .flatMap(userProfile -> {
                            userProfile.setProfilePicture(resourceUrl(pictureName, profileFolder));
                            return profileRepository.save(userProfile);
                })
                .map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> updateDocument(String publicId, String documentName) {
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> getUserProfileFromDB(user.getId()))
                .flatMap(userProfile -> {
                            userProfile.setRegisteredDocument(resourceUrl(documentName, documentFolder));
                            return profileRepository.save(userProfile);
                })
                .map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value()));
    }

    public Mono<AppResponse> updateSettings(NotificationSettingRecord setting) {
        return userRepository.findByPublicId(setting.publicId())
                .flatMap(user -> getUserProfileFromDB(user.getId()))
                .flatMap(userProfile -> {
                            userProfile.setNotificationSetting(toJson(NotificationSetting.builder()
                                    .campaignUpdateAlert(setting.campaignUpdateAlert())
                                    .emailAlert(setting.emailAlert())
                                    .paymentUpdateAlert(setting.paymentUpdateAlert())
                                    .build()));
                            return profileRepository.save(userProfile);
                })
                .map(UserProfileConverter::mapToRecord)
                .map(profileRecord -> AppUtil.buildAppResponse(profileRecord, USER_PROFILE_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
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

    private String toJson(NotificationSetting items) {
        return AppUtil.gsonInstance().toJson(items);
    }

    private Mono<Boolean> checkForProfile(User user) {
        return profileRepository.findById(getOrDefault(user.getId()))
                .map(userProfile -> true)
                .switchIfEmpty( Mono.just(false) );
    }

    private String resourceUrl(String file, String folder) {
        return baseResourceUrl +
                folder +
                checkExt(file);
    }

    private String checkExt(String file) {
        if(file.contains(".jpeg") || file.contains(".jpg") || file.contains(".png")) return file;
        else return file + defaultFileExtension;
    }

    private Integer getOrDefault(Integer id) {
        return Objects.isNull(id) ? -1 : id;
    }
}
