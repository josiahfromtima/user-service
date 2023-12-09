package com.tima.platform.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
public class MMAccessToken {
    @JsonProperty("access_token")
    private String accessToken;
    private Long expiresIn;
}
