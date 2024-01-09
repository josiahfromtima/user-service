package com.tima.platform.model.api.request;

import lombok.Builder;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 1/9/24
 */
@Builder
public record PublicRequestHash(String headerHash, String headerSalt) {}
