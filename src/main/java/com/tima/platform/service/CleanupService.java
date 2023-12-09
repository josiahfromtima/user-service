package com.tima.platform.service;

import com.tima.platform.domain.CustomRefreshToken;
import com.tima.platform.repository.CustomRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.logging.Logger;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */

@Service
@RequiredArgsConstructor
public class CleanupService {
    Logger log = Logger.getLogger(CleanupService.class.getName());
    private final CustomRefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "${app.scheduler.token.cleanup}")
    public void runHangingTokenCleanup() {
        log.info("Running Cleanup Job for expired tokens....");
        getExpiredTokens()
                .doOnNext(customRefreshToken -> log.info("Found " + customRefreshToken))
                .map(refreshTokenRepository::delete)
                .blockLast();
    }

    private Flux<CustomRefreshToken> getExpiredTokens() {
        return refreshTokenRepository.findByExpiresOnBefore(Instant.now());
    }
}
