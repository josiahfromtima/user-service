package com.tima.platform.service;

import com.tima.platform.domain.User;
import com.tima.platform.domain.UserRole;
import com.tima.platform.domain.Verification;
import com.tima.platform.event.EmailEvent;
import com.tima.platform.exception.AppException;
import com.tima.platform.model.api.AppResponse;
import com.tima.platform.model.api.request.PasswordRestRecord;
import com.tima.platform.model.api.request.UserRecord;
import com.tima.platform.model.api.response.NewUserRecord;
import com.tima.platform.model.constant.UserType;
import com.tima.platform.model.event.EmailTemplate;
import com.tima.platform.model.event.GenericRequest;
import com.tima.platform.repository.RoleRepository;
import com.tima.platform.repository.UserRepository;
import com.tima.platform.repository.UserRoleRepository;
import com.tima.platform.repository.VerificationRepository;
import com.tima.platform.service.template.UserSignupTemplate;
import com.tima.platform.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.tima.platform.exception.ApiErrorHandler.handleOnErrorResume;
import static com.tima.platform.model.security.TimaAuthority.ADMIN_BRAND_INFLUENCER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/6/23
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSignUpService extends UserSignupTemplate<UserRecord, User, AppResponse> {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final VerificationRepository verificationRepository;
    private final EmailEvent emailEvent;

    private static final String USER_MSG = "User Action Request Executed Successfully";
    private static final String USER_PASSWORD_MSG = "Password change was successful";

    @Value("${email.activation.template}")
    private String activationMailTemplateId;
    private static final String INVALID_USER = "Invalid username";

    @Override
    protected Mono<User> createUserAccount(UserRecord accountToCreate) throws AppException {
        return userRepository.findByUsername(accountToCreate.username())
                .flatMap(alreadyExist ->
                                handleOnErrorResume(new AppException("01 - Username Already Exist "),
                                        BAD_REQUEST.value()
                         )
                        .map(s -> alreadyExist)
                )
                .switchIfEmpty(userRepository.save(
                                        User.builder()
                                                .username(accountToCreate.username() == null ?
                                                        accountToCreate.email()
                                                        : accountToCreate.username())
                                                .password(encodeSecret(accountToCreate.password()))
                                .build()
                        )
                ).doOnNext(account -> log.info("New User Account {} Created", account.getUsername()));
    }

    @Override
    protected Mono<User> assignDefaultPermissionToUser(User newAccount, UserRecord accountToCreate) throws AppException {
        return roleRepository.findByName(accountToCreate.userType().name())
                .flatMap(role ->
                        userRoleRepository.save(UserRole.builder()
                                .userId(newAccount.getId())
                                .roleId(role.getId())
                                .build()))
                .map(r -> newAccount);
    }

    @Override
    protected Mono<AppResponse> sendActivationMail(User newAccount, UserRecord accountToCreate) throws AppException {
        String otp = AppUtil.generateOTP(6);
        return verificationRepository.save(Verification.builder()
                        .userId(newAccount.getId())
                        .userOtp(otp)
                        .build() )
                .flatMap(r -> emailEvent.sendMail(GenericRequest.builder()
                        .to(accountToCreate.email())
                        .templateId(activationMailTemplateId)
                        .template(EmailTemplate.builder().link(
                                accountToCreate.email())
                                .otp(otp)
                                .build())
                        .build())
                )
                .map(r -> AppUtil.buildAppResponse(new NewUserRecord("New User Account Created",
                        newAccount.getPublicId()), USER_MSG));
    }

    public Mono<AppResponse> verifyOtp(String otp) {
        return verificationRepository.findByUserOtp(otp)
                .flatMap(verification -> {
                    verification.setUserOtp(null);
                    deleteOtp(verification);
                    return activateUser(verification.getUserId())
                            .map(v -> AppUtil.buildAppResponse("Valid OTP", USER_MSG));
                })
                .switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid OTP Entered"), BAD_REQUEST.value())
                );
    }

    public Mono<AppResponse> resendOtp(String username, String email) {
        String newOTP = AppUtil.generateOTP(6);
        return userRepository.findByUsername(username)
                .flatMap(user -> verificationRepository.findByUserId(user.getId())
                        .flatMap(verification -> {
                            verification.setUserOtp(newOTP);
                            return verificationRepository.save(verification)
                                    .map(v -> user);
                        })
                        .flatMap(foundUser -> emailEvent.sendMail(GenericRequest.builder()
                                        .to(email)
                                        .templateId(activationMailTemplateId)
                                        .template(EmailTemplate.builder().link(email).otp(newOTP).build())
                                        .build())
                                .map(r -> AppUtil.buildAppResponse("OTP sent to, " +
                                        foundUser.getUsername(), USER_MSG)
                                )
                        )
                ).switchIfEmpty(
                        handleOnErrorResume(new AppException("Username/Email is not on our record"), BAD_REQUEST.value())
                );
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> changePassword(String publicId, PasswordRestRecord restRecord){
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> {
                    if(passwordEncoder.matches(restRecord.currentPassword(), user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(restRecord.newPassword()));
                        return userRepository.save(user);
                    }else return handleOnErrorResume(new AppException(INVALID_USER), UNAUTHORIZED.value());
                }).map(profileRecord -> AppUtil.buildAppResponse(
                        profileRecord.getUsername() + "'s "+ USER_PASSWORD_MSG, USER_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
    }

    public Mono<AppResponse> changePassword(String publicId, String password){
        return userRepository.findByPublicId(publicId)
                .flatMap(user -> {
                        user.setPassword(passwordEncoder.encode(password));
                        return userRepository.save(user);
                }).map(profileRecord -> AppUtil.buildAppResponse(
                        profileRecord.getUsername() + "'s "+ USER_PASSWORD_MSG, USER_MSG))
                .switchIfEmpty(handleOnErrorResume(new AppException(INVALID_USER), BAD_REQUEST.value()));
    }

    @PreAuthorize(ADMIN_BRAND_INFLUENCER)
    public Mono<AppResponse> deActivateUser(String publicId){
        return userRepository.findByPublicId(publicId)
                .flatMap( user ->  {
                    user.setEnabled(false);
                    return userRepository.save(user)
                            .map(cred -> AppUtil.buildAppResponse("[]",
                                            user.getUsername() + " has been De-Activated"));
                        }
                ).switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid User"), BAD_REQUEST.value())
                );
    }
    private Mono<User> activateUser(Integer id){
        return userRepository.findById(id)
                .flatMap( user ->  {
                    user.setEnabled(true);
                    return userRepository.save(user);
                }).switchIfEmpty(
                        handleOnErrorResume(new AppException("Invalid User"), BAD_REQUEST.value())
                );
    }

    private void deleteOtp(Verification verification) {
        verificationRepository.delete(verification)
                .subscribe();
    }
    public Mono<AppResponse> getUserType(){
        return Mono.just(AppUtil.buildAppResponse(UserType.values(), USER_MSG));
    }

    private String encodeSecret(String secret) {
        return Objects.isNull(secret) ? null : passwordEncoder.encode(secret);
    }

}
