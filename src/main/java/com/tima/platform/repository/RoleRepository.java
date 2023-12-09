package com.tima.platform.repository;

import com.tima.platform.domain.Role;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/7/23
 */
public interface RoleRepository extends ReactiveCrudRepository<Role, Integer> {
    Mono<Role> findByName(String name);
}
