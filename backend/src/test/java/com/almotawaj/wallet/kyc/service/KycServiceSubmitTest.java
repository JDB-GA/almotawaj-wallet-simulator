package com.almotawaj.wallet.kyc.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.service.FileStorageService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.kyc.model.request.KycSubmitRequest;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.kyc.repository.KycDocumentRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KycServiceSubmitTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 4);
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private KycApplicationRepository applicationRepository;
    @Mock
    private KycDocumentRepository documentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private KycApplicationFactory applicationFactory;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private AuditService auditService;

    private KycService kycService;
    private User user;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-04T08:00:00Z"), ZoneOffset.UTC);
        kycService = new KycService(applicationRepository, documentRepository, userRepository,
                applicationFactory, fileStorageService, clock, auditService);
        user = new User();
        user.setId(USER_ID);
        when(userRepository.findByIdForUpdate(USER_ID)).thenReturn(Optional.of(user));
    }

    @Test
    void submit_savesApplicationAndMarksUserPending() {
        KycApplication application = new KycApplication();
        when(applicationFactory.create(any(), any())).thenReturn(application);
        when(applicationRepository.saveAndFlush(application)).thenReturn(application);

        kycService.submit(USER_ID, request(TODAY.minusYears(30), TODAY.plusYears(1)));

        assertThat(user.getKycStatus()).isEqualTo(KycStatus.PENDING);
        verify(applicationRepository).saveAndFlush(application);
    }

    @Test
    void submit_rejectsWhenAlreadyPending() {
        user.setKycStatus(KycStatus.PENDING);

        assertCode(InformationExistException.class, ErrorCodes.KYC_ALREADY_PENDING, request(TODAY.minusYears(30), TODAY.plusYears(1)));
    }

    @Test
    void submit_rejectsWhenAlreadyApproved() {
        user.setKycStatus(KycStatus.APPROVED);

        assertCode(InformationExistException.class, ErrorCodes.KYC_ALREADY_APPROVED, request(TODAY.minusYears(30), TODAY.plusYears(1)));
    }

    @Test
    void submit_rejectsCprUsedByAnotherUser() {
        when(applicationRepository.existsByCprNumberAndStatusInAndUserIdNot(any(), any(), any())).thenReturn(true);

        assertCode(InformationExistException.class, ErrorCodes.CPR_ALREADY_USED, request(TODAY.minusYears(30), TODAY.plusYears(1)));
    }

    @Test
    void submit_rejectsUnderage() {
        assertCode(BusinessRuleException.class, ErrorCodes.KYC_UNDERAGE, request(TODAY.minusYears(18).plusDays(1), TODAY.plusYears(1)));
    }

    @Test
    void submit_rejectsExpiredDocument() {
        assertCode(BusinessRuleException.class, ErrorCodes.DOCUMENT_EXPIRED, request(TODAY.minusYears(30), TODAY));
    }

    private void assertCode(Class<? extends RuntimeException> type, String code, KycSubmitRequest request) {
        assertThatThrownBy(() -> kycService.submit(USER_ID, request))
                .isInstanceOf(type)
                .hasFieldOrPropertyWithValue("code", code);
        verify(applicationRepository, never()).saveAndFlush(any());
    }

    private static KycSubmitRequest request(LocalDate dateOfBirth, LocalDate expiryDate) {
        return new KycSubmitRequest("Ali Hasan", "990101234", dateOfBirth, "BH", "123", "45", "6", null,
                "Manama", expiryDate, expiryDate.plusYears(1), null, null, null);
    }
}
