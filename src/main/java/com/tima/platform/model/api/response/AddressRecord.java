package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record AddressRecord(String street, String city, String state, String postCode, String country, Instant createdOn)
{}
