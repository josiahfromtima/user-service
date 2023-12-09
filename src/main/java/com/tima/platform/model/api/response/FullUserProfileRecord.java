package com.tima.platform.model.api.response;

import com.tima.platform.model.api.request.UserProfileRecord;
import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
@Builder
public record FullUserProfileRecord(String username, String publicId, UserProfileRecord profile) {}
