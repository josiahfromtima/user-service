package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tima.platform.model.api.request.UserProfileRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FullUserProfileRecord(String username, String publicId, UserProfileRecord profile) {}
