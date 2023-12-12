package com.tima.platform.repository;

import com.tima.platform.domain.Country;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
public interface CountryRepository extends ReactiveCrudRepository<Country, Integer> {
    Mono<Country> findByName(String countryName);
}
