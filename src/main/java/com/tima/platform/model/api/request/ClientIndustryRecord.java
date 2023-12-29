package com.tima.platform.model.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Builder
public record ClientIndustryRecord(
        @NotNull(message = "User Public Id is Required")
        String userPublicId,
        @NotNull(message = "Selected Industries is Required")
        List<String> selectedIndustries) {}
