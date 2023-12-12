package com.tima.platform.model.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record CountryRecord(String name, List<String> language, String currency) {}