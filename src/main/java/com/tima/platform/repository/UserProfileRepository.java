package com.tima.platform.repository;

import com.tima.platform.domain.User;
import com.tima.platform.domain.UserProfile;
import com.tima.platform.model.constant.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Integer> {
    Mono<UserProfile> findByEmail(String email);
    Flux<UserProfile> findAllBy(Pageable pageable);
    Flux<UserProfile> findByUserType(UserType type, Pageable pageable);
}
