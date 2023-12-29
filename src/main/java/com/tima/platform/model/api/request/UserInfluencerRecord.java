package com.tima.platform.model.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public record UserInfluencerRecord(
        @NotNull(message = "First Name is Required")
        String firstName,
        @NotNull(message = "Middle Name is Required")
        String middleName,
        @NotNull(message = "Last Name is Required")
        String lastName,
        @NotNull(message = "Phone/Mobile is Required")
        String phoneNumber,
        @NotNull(message = "Email is Required")
        @Email(message = "Email is not valid")
        String email,
        @NotNull(message = "Associated user public Id is Required")
        String publicId) { }
