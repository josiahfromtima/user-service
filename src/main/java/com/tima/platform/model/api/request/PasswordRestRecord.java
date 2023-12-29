package com.tima.platform.model.api.request;

import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
public record PasswordRestRecord(
        @NotNull(message = "Username is Required")
        String currentPassword,
        @NotNull(message = "Username is Required")
        String newPassword) {}
