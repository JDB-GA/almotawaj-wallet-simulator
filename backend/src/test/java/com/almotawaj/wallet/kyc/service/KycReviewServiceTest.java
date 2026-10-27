package com.almotawaj.wallet.kyc.service;

import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.service.FileStorageService;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.kyc.event.KycReviewedEvent;
import com.almotawaj.wallet.kyc.model.KycApplication;
import com.almotawaj.wallet.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.kyc.model.KycStatus;
import com.almotawaj.wallet.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.kyc.repository.KycDocumentRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KycReviewServiceTest {
    private static final Instant NOW = Instant.parse("2026-10-05T08:00:00Z");
    private static final UUID APPLICATION_ID = UUID.randomUUID();
    private static final UUID REVIEWER_ID = UUID.randomUUID();

    @Mock
    private KycApplicationRepository applicationRepository;
    @Mock
    private KycDocumentRepository documentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private AuditService auditService;

    private KycReviewService service;
    private KycApplication application;
    private User applicant;
    private User reviewer;

    @BeforeEach
    void setUp() {
        service = new KycReviewService(applicationRepository, documentRepository, userRepository,
                fileStorageService, eventPublisher, Clock.fixed(NOW, ZoneOffset.UTC), auditService);
        applicant = new User();
        applicant.setId(UUID.randomUUID());
        applicant.setEmailAddress("client@example.com");
        applicant.setKycStatus(KycStatus.PENDING);
        reviewer = new User();
        reviewer.setId(REVIEWER_ID);
        application = new KycApplication();
        application.setId(APPLICATION_ID);
        application.setUser(applicant);
        when(applicationRepository.findByIdForUpdate(APPLICATION_ID)).thenReturn(Optional.of(application));
    }

    @Test
    void approve_marksApplicationAndUserApprovedAndPublishesEvent() {
        when(userRepository.getReferenceById(REVIEWER_ID)).thenReturn(reviewer);

        service.approve(APPLICATION_ID, REVIEWER_ID);

        assertThat(application.getStatus()).isEqualTo(KycApplicationStatus.APPROVED);
        assertThat(application.getReviewedBy()).isSameAs(reviewer);
        assertThat(application.getReviewedAt()).isEqualTo(NOW);
        assertThat(applicant.getKycStatus()).isEqualTo(KycStatus.APPROVED);
        verify(eventPublisher).publishEvent(new KycReviewedEvent(applicant.getId(), "client@example.com",
                KycApplicationStatus.APPROVED, null));
    }

    @Test
    void reject_storesTrimmedReasonAndMarksUserRejected() {
        when(userRepository.getReferenceById(REVIEWER_ID)).thenReturn(reviewer);

        service.reject(APPLICATION_ID, REVIEWER_ID, "  Blurry CPR copy  ");

        assertThat(application.getStatus()).isEqualTo(KycApplicationStatus.REJECTED);
        assertThat(application.getRejectionReason()).isEqualTo("Blurry CPR copy");
        assertThat(applicant.getKycStatus()).isEqualTo(KycStatus.REJECTED);
        ArgumentCaptor<KycReviewedEvent> event = ArgumentCaptor.forClass(KycReviewedEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().reason()).isEqualTo("Blurry CPR copy");
    }

    @Test
    void decide_rejectsAlreadyReviewedApplication() {
        application.setStatus(KycApplicationStatus.APPROVED);

        assertThatThrownBy(() -> service.reject(APPLICATION_ID, REVIEWER_ID, "Late reason"))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCodes.KYC_ALREADY_REVIEWED);
        assertThat(application.getStatus()).isEqualTo(KycApplicationStatus.APPROVED);
        verify(eventPublisher, never()).publishEvent(any());
    }
}
