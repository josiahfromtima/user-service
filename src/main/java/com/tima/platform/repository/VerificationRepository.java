package com.tima.platform.repository;

import com.tima.platform.domain.Verification;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public interface VerificationRepository extends ReactiveCrudRepository<Verification, Integer> {
    Mono<Verification> findByUserId(Integer id);
    Mono<Verification> findByUserIdAndType(Integer id, String type);
    Mono<Verification> findByUserOtp(String otp);

    Flux<Verification> findByCreatedOnBefore(Instant dateTime);
}
