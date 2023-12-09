package com.tima.platform.repository;

import com.tima.platform.domain.CustomRefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/8/23
 */
public interface CustomRefreshTokenRepository extends ReactiveCrudRepository<CustomRefreshToken, String> {
    Flux<CustomRefreshToken> findByExpiresOnBefore(Instant current);
}
