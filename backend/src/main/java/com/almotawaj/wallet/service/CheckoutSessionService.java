package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.GatewayMessages;
import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.event.CheckoutStatusChangedEvent;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationExistException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.CheckoutSession;
import com.almotawaj.wallet.model.CheckoutStatus;
import com.almotawaj.wallet.model.request.CreateCheckoutSessionRequest;
import com.almotawaj.wallet.model.response.CheckoutSessionResponse;
import com.almotawaj.wallet.model.response.CheckoutViewResponse;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.repository.CheckoutSessionRepository;
import com.almotawaj.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckoutSessionService {
    private final CheckoutSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final CheckoutSessionMapper mapper;
    private final AuditService auditService;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PageResponse<CheckoutSessionResponse> list(UUID merchantId, CheckoutStatus status, Pageable pageable) {
        return PageResponse.from(sessionRepository.findByMerchant(merchantId, status, pageable), mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CheckoutSessionResponse get(UUID merchantId, UUID sessionId) {
        return mapper.toResponse(sessionRepository.findByIdAndMerchantId(sessionId, merchantId).orElseThrow(this::notFound));
    }

    @Transactional(readOnly = true)
    public CheckoutViewResponse view(UUID sessionId) {
        return mapper.toView(sessionRepository.findWithMerchantById(sessionId).orElseThrow(this::notFound));
    }

    @Transactional
    public CheckoutSessionResponse create(UUID merchantId, CreateCheckoutSessionRequest request) {
        if (sessionRepository.existsByMerchantIdAndOrderReference(merchantId, request.orderReference())) {
            throw new InformationExistException(GatewayMessages.ORDER_ALREADY_EXISTS, ErrorCodes.ORDER_ALREADY_EXISTS);
        }
        String description = request.description() == null || request.description().isBlank() ? null : request.description().strip();
        CheckoutSession session = new CheckoutSession();
        session.setMerchant(userRepository.getReferenceById(merchantId));
        session.setOrderReference(request.orderReference());
        session.setAmount(request.amount());
        session.setDescription(description);
        session.setReturnUrl(request.returnUrl());
        session.setExpiresAt(clock.instant().plus(lifetimeOf(request)));
        CheckoutSession saved = sessionRepository.saveAndFlush(session);
        auditService.record(merchantId, AuditAction.CHECKOUT_CREATED, AuditTargetType.CHECKOUT_SESSION, saved.getId(),
                saved.getOrderReference());
        return mapper.toResponse(saved);
    }

    @Transactional
    public CheckoutSessionResponse cancel(UUID merchantId, UUID sessionId) {
        CheckoutSession session = sessionRepository.findByIdForUpdate(sessionId)
                .filter(found -> found.getMerchant().getId().equals(merchantId))
                .orElseThrow(this::notFound);
        if (session.statusAt(clock.instant()) != CheckoutStatus.PENDING) {
            throw new BusinessRuleException(GatewayMessages.CHECKOUT_NOT_PENDING, ErrorCodes.CHECKOUT_NOT_PENDING);
        }
        session.setStatus(CheckoutStatus.CANCELLED);
        eventPublisher.publishEvent(new CheckoutStatusChangedEvent(sessionId));
        auditService.record(merchantId, AuditAction.CHECKOUT_CANCELLED, AuditTargetType.CHECKOUT_SESSION, sessionId,
                session.getOrderReference());
        return mapper.toResponse(session);
    }

    private static Duration lifetimeOf(CreateCheckoutSessionRequest request) {
        return request.expiresInMinutes() == null ? GatewayConstants.SESSION_LIFETIME : Duration.ofMinutes(request.expiresInMinutes());
    }

    private InformationNotFoundException notFound() {
        return new InformationNotFoundException(GatewayMessages.CHECKOUT_NOT_FOUND, ErrorCodes.CHECKOUT_NOT_FOUND);
    }
}
