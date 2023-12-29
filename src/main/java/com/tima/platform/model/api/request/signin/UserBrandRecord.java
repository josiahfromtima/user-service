package com.tima.platform.model.api.request.signin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public record UserBrandRecord(
        @NotNull(message = "Company Name is Required")
        String companyName,
        @NotNull(message = "Company Phone Number is Required") String phoneNumber,
        String website,
        @NotNull(message = "Company Email is Required")
        @Email(message = "EMail is not valid")
        String email,
        @NotNull(message = "Associated user public Id is Required")
        String publicId) {}
