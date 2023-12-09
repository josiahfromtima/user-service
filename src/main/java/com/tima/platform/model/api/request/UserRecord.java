package com.tima.platform.model.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tima.platform.model.constant.UserType;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */

@JsonIgnoreProperties
public record UserRecord(String publicId, String username, String password, String email, UserType userType)  {}
