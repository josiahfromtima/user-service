package com.tima.platform.event;

import com.tima.platform.model.event.GenericRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailEvent {
    private final StreamBridge streamBridge;
    public Mono<Boolean> sendMail(GenericRequest email) {
        log.info("Sending activation mail to {}", email.getTo());
        return Mono.just( streamBridge.send("mailing-out-0", email)
        );
    }
}
