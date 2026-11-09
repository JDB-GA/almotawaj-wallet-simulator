package com.almotawaj.wallet.identity.seed;

import com.almotawaj.wallet.identity.config.constants.ErrorCodes;
import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.LogMessages;
import com.almotawaj.wallet.identity.config.constants.SeedConstants;
import com.almotawaj.wallet.identity.event.AccountEvents;
import com.almotawaj.wallet.identity.exception.BusinessRuleException;
import com.almotawaj.wallet.identity.kyc.service.KycSeeder;
import com.almotawaj.wallet.identity.seed.SeedAccount;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import com.almotawaj.wallet.identity.util.LoginIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DatabaseSeedService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KycSeeder kycSeeder;
    private final AccountEvents accountEvents;
    private final String seedPassword;

    public DatabaseSeedService(UserRepository userRepository, PasswordEncoder passwordEncoder, KycSeeder kycSeeder, AccountEvents accountEvents,
                               @Value(SeedConstants.SEED_PASSWORD_PROPERTY) String seedPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kycSeeder = kycSeeder;
        this.accountEvents = accountEvents;
        this.seedPassword = seedPassword;
    }

    @Transactional
    public void seedBasicData() {
        if (seedPassword.isBlank()) {
            throw new BusinessRuleException(ErrorMessages.SEED_PASSWORD_MISSING, ErrorCodes.SEED_PASSWORD_MISSING);
        }
        User reviewer = null;
        for (SeedAccount account : SeedConstants.ACCOUNTS) {
            User user = findOrCreate(account);
            if (account.email().equals(SeedConstants.REVIEWER_EMAIL)) {
                reviewer = user;
            }
            kycSeeder.seed(user, account, reviewer);
            accountEvents.created(user);
        }
        log.info(LogMessages.SEED_COMPLETED);
    }

    private User findOrCreate(SeedAccount account) {
        return userRepository.findByEmailAddress(account.email()).orElseGet(() -> {
            User user = new User();
            user.setEmailAddress(account.email());
            user.setMobileNumber(LoginIdentifier.normalizeMobile(account.localMobile()));
            user.setRole(account.role());
            user.setPassword(passwordEncoder.encode(seedPassword));
            user.setEmailVerified(true);
            user.setMobileVerified(true);
            user.getPermissions().addAll(account.permissions());
            return userRepository.saveAndFlush(user);
        });
    }
}
