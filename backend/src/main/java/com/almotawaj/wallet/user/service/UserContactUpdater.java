package com.almotawaj.wallet.user.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.model.request.UpdateUserContactRequest;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.util.LoginIdentifier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContactUpdater {
    private final UserRepository userRepository;

    public void apply(User target, UpdateUserContactRequest request) {
        if (request.email() != null) {
            changeEmail(target, LoginIdentifier.normalizeEmail(request.email()));
        }
        if (request.mobileNumber() != null) {
            changeMobile(target, LoginIdentifier.normalizeMobile(request.mobileNumber()));
        }
    }

    private void changeEmail(User target, String email) {
        if (email.equals(target.getEmailAddress())) {
            return;
        }
        if (userRepository.existsByEmailAddress(email)) {
            throw new InformationExistException(ErrorMessages.EMAIL_ALREADY_REGISTERED, ErrorCodes.EMAIL_ALREADY_REGISTERED);
        }
        target.setEmailAddress(email);
        target.setEmailVerified(false);
    }

    private void changeMobile(User target, String mobileNumber) {
        if (mobileNumber.equals(target.getMobileNumber())) {
            return;
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new InformationExistException(ErrorMessages.MOBILE_ALREADY_REGISTERED, ErrorCodes.MOBILE_ALREADY_REGISTERED);
        }
        target.setMobileNumber(mobileNumber);
    }
}
