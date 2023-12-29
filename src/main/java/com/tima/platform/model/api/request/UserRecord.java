package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tima.platform.model.constant.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */

@JsonIgnoreProperties
public record UserRecord(String publicId,
                         @NotNull(message = "Username is Required") String username,
                         @NotNull(message = "Password is Required") String password,
                         @NotNull(message = "Email is Required")
                         @Email(message = "Email should be valid")
                         String email,
                         @NotNull(message = "User Type is Required") UserType userType)  {}
