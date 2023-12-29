package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record CountryRecord(@NotNull(message = "Country Name is Required")
                            String name,
                            @NotNull(message = "Country Languages is Required")
                            List<String> language,
                            @NotNull(message = "Country Currency is Required")
                            String currency) {}