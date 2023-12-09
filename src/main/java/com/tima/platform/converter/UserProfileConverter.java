package com.tima.platform.converter;

import com.tima.platform.domain.UserProfile;
import com.tima.platform.model.api.request.UserProfileRecord;

import java.util.List;
import java.util.Objects;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public class UserProfileConverter {
    private UserProfileConverter() {}

    public static synchronized UserProfile mapToEntity(UserProfileRecord profileRecord) {
        return UserProfile.builder()
                .userId(profileRecord.userid())
                .companyName(profileRecord.companyName())
                .userType(profileRecord.userType())
                .firstName(getOrDefault(profileRecord.firstName(), ""))
                .middleName(getOrDefault(profileRecord.middleName(), ""))
                .lastName(getOrDefault(profileRecord.lastName(), ""))
                .profilePicture(profileRecord.profilePicture())
                .language(profileRecord.language())
                .website(profileRecord.website())
                .email(profileRecord.email())
                .phoneNumber(profileRecord.phoneNumber())
                .notificationSetting(profileRecord.notificationSetting())
                .registeredDocument(profileRecord.registeredDocument())
                .build();
    }

    public static synchronized UserProfileRecord mapToRecord(UserProfile profile) {
        return  UserProfileRecord.builder()
                .createdOn(profile.getCreatedOn())
                .firstName(profile.getFirstName())
                .middleName(profile.getMiddleName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .email(profile.getEmail())
                .userType(profile.getUserType())
                .companyName(profile.getCompanyName())
                .website(profile.getWebsite())
                .language(profile.getLanguage())
                .profilePicture(profile.getProfilePicture())
                .registeredDocument(profile.getRegisteredDocument())
                .notificationSetting((String) profile.getNotificationSetting())
                .build();
    }

    public static synchronized List<UserProfileRecord> mapToRecordList(List<UserProfile> profiles) {
        return profiles
                .stream()
                .map(UserProfileConverter::mapToRecord)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrDefault(Object value, T t){
        if( Objects.isNull(value) ) {
            return t;
        }
        return (T) value;
    }

}
