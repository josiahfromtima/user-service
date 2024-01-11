package com.tima.platform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.reflect.TypeToken;
import com.tima.platform.converter.AddressConverter;
import com.tima.platform.domain.Address;
import com.tima.platform.domain.Country;
import com.tima.platform.domain.User;
import com.tima.platform.event.AddUserIndustryEvent;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.AddressRequestRecord;
import com.tima.platform.model.api.request.ClientIndustryRecord;
import com.tima.platform.repository.AddressRepository;
import com.tima.platform.repository.CountryRepository;
import com.tima.platform.repository.UserProfileRepository;
import com.tima.platform.repository.UserRepository;
import com.tima.platform.util.AppError;
import com.tima.platform.util.AppUtil;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Service
@RequiredArgsConstructor
public class AddressService {
    private final LoggerHelper log = LoggerHelper.newInstance(CountryService.class.getName());
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CountryRepository countryRepository;
    private final AddUserIndustryEvent userIndustryEvent;

    @Value("${aws.s3.url}")
    private String baseResourceUrl;
    @Value("${aws.s3.resource.profile}")
    private String profileFolder;
    @Value("${aws.s3.resource.document}")
    private String documentFolder;
    @Value("${aws.s3.image-ext}")
    private String defaultFileExtension;

    private static final String ADDRESS_MSG = "Address request executed successfully";
    private static final String INVALID_COUNTRY = "The country selected is invalid";
    private static final String INVALID_USER = "The user id is invalid";
    private static final String INVALID_USER_PROFILE = "No basic user profile found. Invalid user id";
    private static final String ERROR_MSG = "The address record mutation could not be performed";

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> getAddress(String publicId) {
        log.info("Fetching the user address");
        return validateUser(publicId)
                        .flatMap(user -> addressRepository.findById(getOrDefault(user.getId())))
                        .map(AddressConverter::mapToRecord)
                        .map(addressRecord -> AppUtil.buildAppResponse(addressRecord, ADDRESS_MSG))
                        .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER_PROFILE), NOT_FOUND.value())
                );
    }

    public Mono<AppResponse> addAddress(AddressRequestRecord requestRecord) {
        log.info("Address is being added and linked to user");
        return validateCountry(requestRecord.addressRecord().country())
                .flatMap(country -> validateUser(requestRecord.publicId())
                        .flatMap(user -> userProfileRepository.findById(getOrDefault(user.getId())))
                        .flatMap(userProfile -> {
                            userProfile.setProfilePicture(resourceUrl(requestRecord.pictureName(), profileFolder) );
                            userProfile.setRegisteredDocument(resourceUrl(requestRecord.documentName(), documentFolder));
                            return userProfileRepository.save(userProfile);
                        }).flatMap(userProfile -> {
                            Address address = AddressConverter.mapToEntity(requestRecord.addressRecord());
                            address.setUserId(userProfile.getId());
                            log.info("user details ", address);
                            return addressRepository.save(address);
                        })
                        .map(AddressConverter::mapToRecord)
                        .map(addressRecord -> AppUtil.buildAppResponse(addressRecord, ADDRESS_MSG))
                        .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER_PROFILE), BAD_REQUEST.value()))
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> updateAddress(AddressRequestRecord requestRecord) {
        log.info("updating the user address");
        return validateCountry(requestRecord.addressRecord().country())
                .flatMap(country -> validateUser(requestRecord.publicId())
                        .flatMap(user ->  addressRepository.findById(getOrDefault(user.getId()))
                                        .flatMap(address -> {
                                            address.setCity(requestRecord.addressRecord().city());
                                            address.setStreet(requestRecord.addressRecord().street());
                                            address.setState(requestRecord.addressRecord().state());
                                            address.setPostCode(requestRecord.addressRecord().postCode());
                                            address.setCountry(requestRecord.addressRecord().country());
                                            address.setLanguage(requestRecord.addressRecord().language());
                                            return addressRepository.save(address);
                                        })
                            .map(AddressConverter::mapToRecord)
                            .map(addressRecord -> AppUtil.buildAppResponse(addressRecord, ADDRESS_MSG))
                            .switchIfEmpty(handleOnErrorResume(new AppException(ERROR_MSG), BAD_REQUEST.value()))
                        )
                ).onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> deleteAddress(String publicId) {
        log.info("Deleting the user address");
        return validateUser(publicId)
                .flatMap(user ->  addressRepository.deleteById(getOrDefault(user.getId())))
                .then(Mono.fromCallable(() -> AppUtil.buildAppResponse( "User Deleted", ADDRESS_MSG)));
    }

    public Mono<AppResponse> addUserIndustry(String publicId, JsonNode industries) {
        log.info("Adding User Selected Industries");
        return validateUser(publicId)
                .flatMap(user ->  userIndustryEvent.sendUserSelection(ClientIndustryRecord.builder()
                                .userPublicId(publicId)
                                .selectedIndustries(json(industries.at("/industries").toString()))
                        .build()))
                .map(b -> AppUtil.buildAppResponse( "User Selection Accepted", ADDRESS_MSG))
                .onErrorResume(t ->
                        handleOnErrorResume(new AppException(AppError.massage(t.getMessage())), BAD_REQUEST.value()));
    }

    private Mono<Country> validateCountry(String name) {
        return countryRepository.findByName(name)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_COUNTRY), NOT_FOUND.value()));
    }
    private Mono<User> validateUser(String publicId) {
        return userRepository.findByPublicId(publicId)
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), NOT_FOUND.value()));
    }

    private static List<String> json(String value) {
        return AppUtil.gsonInstance().fromJson(value, new TypeToken<List<String>>(){}.getType());
    }


    private int getOrDefault(Integer value) {
        return Objects.isNull(value) ? 0 : value;
    }

    private String resourceUrl(String file, String folder) {
        return baseResourceUrl +
                folder +
                checkExt(file);
    }

    private String checkExt(String file) {
        if(file.contains(".jpeg") || file.contains(".jpg") || file.contains(".png")) return file;
        else return file + defaultFileExtension;
    }
}
