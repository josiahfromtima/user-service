package com.tima.platform.event;

import com.google.gson.Gson;
import com.tima.platform.model.api.request.NotificationSettingRecord;
import com.tima.platform.service.UserProfileService;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Configuration
@RequiredArgsConstructor
public class NotificationSettingEvent {
    LoggerHelper log = LoggerHelper.newInstance(AddUserIndustryEvent.class.getName());
    private final UserProfileService profileService;

    @Bean
    public Consumer<String> setting() {
        return s -> {
            log.info("User App Settings --- {}", s);
            NotificationSettingRecord request = new Gson().fromJson(s, NotificationSettingRecord.class);
            if(request == null) return;
            profileService.updateSettings(request)
                    .subscribe(r -> log.info("Updated Settings: ", r));
        };

    }
}
