package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.constant.UserType;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UserProfileRecord (String firstName, String middleName, String lastName, String email,
                                 String phoneNumber, UserType userType, String companyName, String website,
                                 String language, String profilePicture, String registeredDocument,
                                 String notificationSetting, Integer userid, Instant createdOn){}
