package com.tima.platform.model.api.request.signin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessToken implements Serializable {
    @SerializedName("access_token")
    @JsonProperty("access_token")
    private String accessToken;
    @SerializedName("token_type")
    @JsonProperty("token_type")
    private String tokenBearer;
    @SerializedName("refresh_token")
    @JsonProperty("refresh_token")
    private String refreshToken;
    @SerializedName("expires_in")
    @JsonProperty("expires_in")
    private Long timeToLive;
    private String scope;
}
