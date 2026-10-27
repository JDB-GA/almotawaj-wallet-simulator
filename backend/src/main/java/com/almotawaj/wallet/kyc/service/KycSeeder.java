package com.almotawaj.wallet.kyc.service;

import com.almotawaj.wallet.config.constants.SeedConstants;
import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.seed.model.SeedAccount;
import com.almotawaj.wallet.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class KycSeeder {
    private final KycApplicationRepository applicationRepository;
    private final Clock clock;

    public void seed(User user, SeedAccount account, User reviewer) {
        if (account.kycStatus() == null || applicationRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId()).isPresent()) {
            return;
        }
        KycApplication application = new KycApplication();
        application.setUser(user);
        application.setStatus(account.kycStatus());
        application.setFullName(account.fullName());
        application.setCprNumber(account.cprNumber());
        application.setDateOfBirth(SeedConstants.DATE_OF_BIRTH);
        application.setNationality(SeedConstants.NATIONALITY);
        application.setBlock(SeedConstants.BLOCK);
        application.setRoad(SeedConstants.ROAD);
        application.setBuilding(SeedConstants.BUILDING);
        application.setArea(SeedConstants.AREA);
        if (account.kycStatus() != KycApplicationStatus.PENDING) {
            application.setReviewedBy(reviewer);
            application.setReviewedAt(clock.instant());
        }
        if (account.kycStatus() == KycApplicationStatus.REJECTED) {
            application.setRejectionReason(SeedConstants.REJECTION_REASON);
        }
        applicationRepository.save(application);
        user.setKycStatus(KycStatus.valueOf(account.kycStatus().name()));
    }
}
