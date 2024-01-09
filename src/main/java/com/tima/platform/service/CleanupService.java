package com.tima.platform.service;

import com.tima.platform.domain.CustomRefreshToken;
import com.tima.platform.domain.Verification;
import com.tima.platform.repository.CustomRefreshTokenRepository;
import com.tima.platform.repository.VerificationRepository;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */

@Service
@RequiredArgsConstructor
public class CleanupService {
    LoggerHelper log = LoggerHelper.newInstance(CleanupService.class.getName());
    private final CustomRefreshTokenRepository refreshTokenRepository;
    private final VerificationRepository verificationRepository;

    @Scheduled(cron = "${app.scheduler.token.cleanup}")
    public void runHangingTokenCleanup() {
        log.info("Running Cleanup Job for expired tokens....");
        getExpiredTokens()
                .doOnNext(customRefreshToken -> log.info("Found " + customRefreshToken))
                .collectList()
                .subscribe(this::deleteExpiredToken);
    }
    @Scheduled(cron = "${app.scheduler.otp.cleanup}")
    public void oldOtpCleanup() {
        log.info("Running Cleanup Job for old otp....");
        getOldOtp()
                .doOnNext(otp -> log.info("Found " + otp.getType()))
                .collectList()
                .subscribe(this::deleteOtp);
    }

    private Flux<CustomRefreshToken> getExpiredTokens() {
        return refreshTokenRepository.findByExpiresOnBefore(Instant.now());
    }

    private Flux<Verification> getOldOtp() {
        return verificationRepository.findByCreatedOnBefore(Instant.now());
    }

    private void deleteOtp(List<Verification> verification) {
        log.info("Deleting ", verification.size(), " otp");
        verificationRepository.deleteAll(verification)
                .subscribe();
    }

    private void deleteExpiredToken(List<CustomRefreshToken> customRefreshTokens) {
        log.info("Deleting ", customRefreshTokens.size(), " expired tokens");
        refreshTokenRepository.deleteAll(customRefreshTokens)
                .subscribe();
    }
}
