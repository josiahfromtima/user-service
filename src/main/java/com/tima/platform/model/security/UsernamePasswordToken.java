package com.tima.platform.model.security;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Data
@Builder
public class UsernamePasswordToken {
    private String username;
    private String password;

}
