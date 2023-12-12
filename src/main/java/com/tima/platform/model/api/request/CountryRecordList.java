package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tima.platform.model.api.response.CountryRecord;
import lombok.Builder;

import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Builder
@JsonIgnoreProperties
public record CountryRecordList(List<CountryRecord> countries) {
}
