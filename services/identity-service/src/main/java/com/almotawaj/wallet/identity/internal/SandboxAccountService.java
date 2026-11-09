package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.config.constants.InternalConstants;
import com.almotawaj.wallet.identity.kyc.model.KycStatus;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.model.UserRole;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SandboxAccountService {
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;

    @Transactional
    public List<UUID> create(SandboxAccountRequest request) {
        return IntStream.range(0, request.count())
                .mapToObj(index -> userRepository.save(sandboxUser(request)).getId())
                .toList();
    }

    @Transactional
    public void delete(List<UUID> userIds) {
        userRepository.deleteAllByIdInBatch(userIds);
    }

    private static User sandboxUser(SandboxAccountRequest request) {
        UserRole role = request.role();
        User user = new User();
        user.setEmailAddress(InternalConstants.SANDBOX_EMAIL.formatted(UUID.randomUUID()));
        user.setMobileNumber(InternalConstants.SANDBOX_MOBILE_PREFIX
                + RANDOM.ints(InternalConstants.SANDBOX_MOBILE_DIGITS, 0, 10)
                .mapToObj(Integer::toString).reduce("", String::concat));
        user.setPassword(InternalConstants.SANDBOX_DISABLED_PASSWORD);
        user.setRole(role);
        user.setDisplayName(request.displayName());
        user.setEmailVerified(true);
        user.setKycStatus(role == UserRole.CLIENT ? KycStatus.APPROVED : KycStatus.NOT_SUBMITTED);
        return user;
    }
}
