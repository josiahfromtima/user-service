package com.tima.platform.model.api.request;

import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Builder
public record ClientIndustryRecord(String userPublicId, List<String> selectedIndustries) {}
