package com.tima.platform.model.constant;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Builder
public record NotificationSetting(boolean campaignUpdateAlert,
                                  boolean emailAlert, boolean paymentUpdateAlert) {}
