package com.tima.platform.model.api.request;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/9/24
 */
@Builder
public record PasswordUpdateRecord(String publicId, String newPassword, String hash, String salt) {}
