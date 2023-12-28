package com.tima.platform.model.api.request;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
public record NotificationSettingRecord(String publicId, boolean campaignUpdateAlert,
                                        boolean emailAlert, boolean paymentUpdateAlert) {}
