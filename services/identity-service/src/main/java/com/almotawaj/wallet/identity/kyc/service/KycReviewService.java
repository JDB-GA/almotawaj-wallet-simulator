package com.almotawaj.wallet.identity.kyc.service;

import com.almotawaj.wallet.identity.audit.AuditAction;
import com.almotawaj.wallet.identity.audit.AuditTargetType;
import com.almotawaj.wallet.identity.audit.AuditService;
import com.almotawaj.wallet.identity.common.model.response.PageResponse;
import com.almotawaj.wallet.identity.common.service.FileStorageService;
import com.almotawaj.wallet.identity.config.constants.ErrorCodes;
import com.almotawaj.wallet.identity.config.constants.ErrorMessages;
import com.almotawaj.wallet.identity.config.constants.LogMessages;
import com.almotawaj.wallet.identity.exception.BusinessRuleException;
import com.almotawaj.wallet.identity.exception.InformationNotFoundException;
import com.almotawaj.wallet.identity.kyc.event.KycReviewedEvent;
import com.almotawaj.wallet.identity.kyc.model.KycApplication;
import com.almotawaj.wallet.identity.kyc.model.KycApplicationStatus;
import com.almotawaj.wallet.identity.kyc.model.KycDocument;
import com.almotawaj.wallet.identity.kyc.model.KycStatus;
import com.almotawaj.wallet.identity.kyc.model.response.KycApplicationSummaryResponse;
import com.almotawaj.wallet.identity.kyc.model.response.KycReviewResponse;
import com.almotawaj.wallet.identity.kyc.repository.KycApplicationRepository;
import com.almotawaj.wallet.identity.kyc.repository.KycDocumentRepository;
import com.almotawaj.wallet.identity.user.repository.UserRepository;
import com.almotawaj.wallet.identity.common.model.DocumentContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycReviewService {
    private final KycApplicationRepository applicationRepository;
    private final KycDocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PageResponse<KycApplicationSummaryResponse> list(KycApplicationStatus status, Pageable pageable) {
        Page<KycApplication> page = status == null
                ? applicationRepository.findAllBy(pageable)
                : applicationRepository.findByStatus(status, pageable);
        return PageResponse.from(page, KycApplicationSummaryResponse::from);
    }

    @Transactional(readOnly = true)
    public KycReviewResponse get(UUID applicationId) {
        return applicationRepository.findWithDetailsById(applicationId)
                .map(KycReviewResponse::from)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.KYC_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public DocumentContent loadDocument(UUID applicationId, UUID documentId) {
        KycDocument document = documentRepository.findByIdAndApplicationId(documentId, applicationId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.DOCUMENT_NOT_FOUND));
        return new DocumentContent(fileStorageService.load(document.getStorageKey()), document.getContentType());
    }

    @Transactional
    public KycReviewResponse approve(UUID applicationId, UUID reviewerId) {
        return decide(applicationId, reviewerId, KycApplicationStatus.APPROVED, null);
    }

    @Transactional
    public KycReviewResponse reject(UUID applicationId, UUID reviewerId, String reason) {
        return decide(applicationId, reviewerId, KycApplicationStatus.REJECTED, reason.strip());
    }

    private KycReviewResponse decide(UUID applicationId, UUID reviewerId, KycApplicationStatus decision, String reason) {
        KycApplication application = applicationRepository.findByIdForUpdate(applicationId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.KYC_NOT_FOUND));
        if (application.getStatus() != KycApplicationStatus.PENDING) {
            throw new BusinessRuleException(ErrorMessages.KYC_ALREADY_REVIEWED, ErrorCodes.KYC_ALREADY_REVIEWED);
        }

        application.setStatus(decision);
        application.setRejectionReason(reason);
        application.setReviewedBy(userRepository.getReferenceById(reviewerId));
        application.setReviewedAt(Instant.now(clock));
        application.getUser().setKycStatus(KycStatus.valueOf(decision.name()));

        log.info(LogMessages.KYC_REVIEWED, reviewerId, applicationId, decision);
        AuditAction action = decision == KycApplicationStatus.APPROVED ? AuditAction.KYC_APPROVED : AuditAction.KYC_REJECTED;
        auditService.record(reviewerId, action, AuditTargetType.KYC_APPLICATION, applicationId, reason);
        eventPublisher.publishEvent(new KycReviewedEvent(
                application.getUser().getId(), application.getUser().getEmailAddress(), decision, reason));
        return KycReviewResponse.from(application);
    }
}
