package com.tima.platform.service;

import com.tima.platform.converter.CountryConverter;
import com.tima.platform.domain.Country;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.response.CountryRecord;
import com.tima.platform.repository.CountryRepository;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Service
@RequiredArgsConstructor
public class CountryService {
    private final LoggerHelper log = LoggerHelper.newInstance(CountryService.class.getName());
    private final CountryRepository countryRepository;
    private static final String COUNTRY_MSG = "Country request executed successfully";
    private static final String INVALID_COUNTRY = "The country name is invalid";
    private static final String ERROR_MSG = "The country record mutation could not be performed";

    public Mono<AppResponse> getCountries() {
        log.info("Getting ALl Country Records...");
        return countryRepository.findAll()
                .collectList()
                .map(CountryConverter::mapToRecords)
                .map(countryRecords -> AppUtil.buildAppResponse(countryRecords, COUNTRY_MSG));
    }
    public Mono<AppResponse> getCountry(String name) {
        log.info("Getting Country Record by ", name);
        return validateCountry(name)
                .map(CountryConverter::mapToRecord)
                .map(countryRecord -> AppUtil.buildAppResponse(countryRecord, COUNTRY_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> createCountry(CountryRecord countryRecord) {
        log.info("Saving New  Country Records");
        return countryRepository.save(CountryConverter.mapToEntity(countryRecord))
                .map(CountryConverter::mapToRecord)
                .map(savedRecord -> AppUtil.buildAppResponse(savedRecord, COUNTRY_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> updateCountries(CountryRecord countryRecord) {
        log.info("Update Country Record for ", countryRecord.name());
        return validateCountry(countryRecord.name())
                .flatMap(country -> {
                    Country converted = CountryConverter.mapToEntity(countryRecord);
                    country.setCurrency(countryRecord.currency());
                    country.setLanguage(converted.getLanguage());
                    return countryRepository.save(country);
                })
                .map(CountryConverter::mapToRecord)
                .map(updateRecord -> AppUtil.buildAppResponse(updateRecord, COUNTRY_MSG));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> deleteCountry(CountryRecord countryRecord) {
        log.info("Delete Country Record for ", countryRecord.name());
        return validateCountry(countryRecord.name())
                .flatMap(countryRepository::delete)
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse(countryRecord.name() + " Deleted", COUNTRY_MSG)));
    }
    @PreAuthorize(ADMIN)
    public Mono<AppResponse> createCountries(List<CountryRecord> countryRecords) {
        log.info("Creating Country Records for ", countryRecords.size());
        return countryRepository.saveAll(CountryConverter.mapToEntities(countryRecords))
                .collectList()
                .map(CountryConverter::mapToRecords)
                .map(createdRecord -> AppUtil.buildAppResponse(
                        createdRecord.size() + " Countries created",
                        COUNTRY_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                .onErrorResume(throwable -> handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()));
    }

    private Mono<Country> validateCountry(String name) {
        return countryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_COUNTRY), BAD_REQUEST.value()));
    }
}
