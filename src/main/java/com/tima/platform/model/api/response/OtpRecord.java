package com.tima.platform.model.api.response;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/15/23
 */
@Builder
public record OtpRecord(String message, String publicId) {}
