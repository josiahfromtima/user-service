package com.tima.platform.resource.address;

import com.tima.platform.config.AuthTokenConfig;
import com.tima.platform.model.api.request.AddressRequestRecord;
import com.tima.platform.model.api.request.CountryRecordList;
import com.tima.platform.model.api.response.CountryRecord;
import com.tima.platform.service.AddressService;
import com.tima.platform.service.CountryService;
import com.tima.platform.service.aws.s3.AwsS3Service;
import com.tima.platform.util.LoggerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/11/23
 */
@Service
@RequiredArgsConstructor
public class UserAddressResourceHandler {
    LoggerHelper log = LoggerHelper.newInstance(UserAddressResourceHandler.class.getName());

    private final AwsS3Service awsS3Service;
    private final CountryService countryService;
    private final AddressService addressService;
    @Value("${aws.s3.resource.profile}")
    private String profileFolder;
    @Value("${aws.s3.resource.document}")
    private String documentFolder;

    /**
     *  This section is the user generated signed URL
     */
    public Mono<ServerResponse> getSignedProfilePicture(ServerRequest request)  {
        String keyName = request.pathVariable("keyName");
        log.info("Get Signed Profile Picture URL Requested ", request.remoteAddress().orElse(null));

        try {
            return awsS3Service.getSignedUrl(profileFolder, keyName)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> getSignedUserDocument(ServerRequest request)  {
        String keyName = request.pathVariable("keyName");
        log.info("Get Signed Signed Document URL Requested", request.remoteAddress().orElse(null));

        try {
            return awsS3Service.getSignedUrl(documentFolder, keyName)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    /**
     *  This section marks the system country activities
     */
    public Mono<ServerResponse> getCountries(ServerRequest request)  {
        log.info("Get Registered Countries Requested", request.remoteAddress().orElse(null));

        try {
            return countryService.getCountries()
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> getCountry(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Get Registered Countries Requested", request.remoteAddress().orElse(null));

        try {
            return countryService.getCountry(name)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> saveNewCountry(ServerRequest request)  {
        Mono<CountryRecord> recordMono = request.bodyToMono(CountryRecord.class);
        log.info("Registered a new country Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(countryService::createCountry)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> saveCountries(ServerRequest request)  {
        Mono<CountryRecordList> recordMonos = request.bodyToMono(CountryRecordList.class);
        log.info("Save Multiple Countries Requested", request.remoteAddress().orElse(null));

        try {
            return recordMonos.flatMap(countryRecordList ->
                            countryService.createCountries(countryRecordList.countries()) )
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> updateCountry(ServerRequest request)  {
        Mono<CountryRecord> recordMono = request.bodyToMono(CountryRecord.class);
        log.info("Update Country Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(countryService::updateCountries)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }
    public Mono<ServerResponse> deleteCountry(ServerRequest request)  {
        String name = request.pathVariable("name");
        log.info("Delete Country Requested", request.remoteAddress().orElse(null));

        try {
            return countryService.deleteCountry(CountryRecord.builder().name(name).build())
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    /**
     *  This section marks the user address activities
     */
    public Mono<ServerResponse> addUserAddress(ServerRequest request)  {
        Mono<AddressRequestRecord> recordMono = request.bodyToMono(AddressRequestRecord.class);
        log.info("Add a new user address Requested", request.remoteAddress().orElse(null));

        try {
            return recordMono.flatMap(addressService::addAddress)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> getUserAddress(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Get user address Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(addressService::getAddress)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> updateUserAddress(ServerRequest request)  {
        Mono<AddressRequestRecord> recordMono = request.bodyToMono(AddressRequestRecord.class);
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Update user address Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(publicId -> recordMono.flatMap(requestRecord ->
                                    addressService.updateAddress(AddressRequestRecord.builder()
                                                    .publicId(publicId)
                                                    .addressRecord(requestRecord.addressRecord())
                                            .build()) )
                     )
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    public Mono<ServerResponse> deleteUserAddress(ServerRequest request)  {
        Mono<JwtAuthenticationToken> jwtAuthToken = AuthTokenConfig.authenticatedToken(request);
        log.info("Delete user address Requested", request.remoteAddress().orElse(null));

        try {
            return jwtAuthToken.map(this::getPublicIdFromToken)
                    .flatMap(addressService::deleteAddress)
                    .flatMap(ServerResponse.ok()::bodyValue)
                    .switchIfEmpty(ServerResponse.badRequest().build());
        } catch (Exception e) {
            return ServerResponse.badRequest().build();
        }
    }

    private String getPublicIdFromToken(JwtAuthenticationToken jwtToken) {
        return jwtToken.getTokenAttributes()
                .getOrDefault("public_id", "")
                .toString();
    }

}
