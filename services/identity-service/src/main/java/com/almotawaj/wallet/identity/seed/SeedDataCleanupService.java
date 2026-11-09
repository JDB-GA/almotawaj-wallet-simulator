package com.almotawaj.wallet.identity.seed;

import com.almotawaj.wallet.identity.auth.repository.OtpChallengeRepository;
import com.almotawaj.wallet.identity.common.service.FileStorageService;
import com.almotawaj.wallet.identity.config.constants.LogMessages;
import com.almotawaj.wallet.identity.config.constants.SeedConstants;
import com.almotawaj.wallet.identity.internal.WalletClient;
import com.almotawaj.wallet.identity.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.identity.kyc.repository.KycDocumentRepository;
import com.almotawaj.wallet.identity.user.model.User;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeedDataCleanupService {
    private final UserRepository userRepository;
    private final WalletClient walletClient;
    private final KycApplicationRepository applicationRepository;
    private final KycDocumentRepository documentRepository;
    private final OtpChallengeRepository otpChallengeRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public void clean() {
        List<User> users = userRepository.findAllByEmailAddressIn(SeedConstants.accountEmails());
        List<UUID> userIds = users.stream().map(User::getId).toList();
        if (userIds.isEmpty()) {
            return;
        }
        walletClient.cleanSeedData(userIds);

        users.stream().map(User::getPictureKey).filter(Objects::nonNull).forEach(fileStorageService::deleteAfterCommit);
        documentRepository.findAllByApplicationUserIdIn(userIds)
                .forEach(document -> fileStorageService.deleteAfterCommit(document.getStorageKey()));
        applicationRepository.clearReviewersByIdIn(userIds);
        otpChallengeRepository.deleteAllByUserIdIn(userIds);
        documentRepository.deleteAllByApplicationUserIdIn(userIds);
        applicationRepository.deleteAllByUserIdIn(userIds);
        users.forEach(user -> user.getPermissions().clear());
        userRepository.flush();
        userRepository.deleteAllInBatch(users);
        log.info(LogMessages.SEED_DATA_CLEANED, users.size());
    }
}
