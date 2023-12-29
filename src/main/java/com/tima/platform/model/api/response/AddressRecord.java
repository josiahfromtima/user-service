package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record AddressRecord(
        @NotNull(message = "User Street is Required")
        String street,
        @NotNull(message = "User City is Required")
        String city,
        @NotNull(message = "User State is Required")
        String state,
        @NotNull(message = "User Post Code is Required")
        String postCode,
        @NotNull(message = "User Country is Required")
        String country,
        Instant createdOn)
{}
