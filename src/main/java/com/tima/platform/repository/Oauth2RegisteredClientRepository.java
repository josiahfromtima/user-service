package com.tima.platform.repository;

import com.tima.platform.domain.Oauth2RegisteredClient;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
public interface Oauth2RegisteredClientRepository extends ReactiveCrudRepository<Oauth2RegisteredClient, String> {
    Mono<Oauth2RegisteredClient> findByClientId(String clientId);
}
