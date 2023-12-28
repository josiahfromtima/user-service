package com.tima.platform.event;

import com.tima.platform.model.api.request.ClientIndustryRecord;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/28/23
 */
@Service
@RequiredArgsConstructor
public class AddUserIndustryEvent {
    LoggerHelper log = LoggerHelper.newInstance(AddUserIndustryEvent.class.getName());
    private final StreamBridge streamBridge;
    public Mono<Boolean> sendUserSelection(ClientIndustryRecord industryRecord) {
        log.info("Sending User Industry record request ", industryRecord);
        return Mono.just( streamBridge.send("userIndustry-out-0", industryRecord)
        );
    }
}
