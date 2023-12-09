package com.tima.platform.model.api.request.signin;

import lombok.Builder;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Builder
public record SignInRequest(String encodedCredentials) {}
