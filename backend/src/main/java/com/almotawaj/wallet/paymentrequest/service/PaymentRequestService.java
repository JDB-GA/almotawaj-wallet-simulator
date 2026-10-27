package com.almotawaj.wallet.paymentrequest.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.paymentrequest.model.PaymentRequest;
import com.almotawaj.wallet.paymentrequest.model.PaymentRequestStatus;
import com.almotawaj.wallet.paymentrequest.model.request.CreatePaymentRequest;
import com.almotawaj.wallet.paymentrequest.model.response.PaymentRequestResponse;
import com.almotawaj.wallet.paymentrequest.repository.PaymentRequestRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.user.repository.UserRepository;
import com.almotawaj.wallet.wallet.model.request.TransferRequest;
import com.almotawaj.wallet.wallet.model.response.WalletTransactionResponse;
import com.almotawaj.wallet.wallet.service.RecipientResolver;
import com.almotawaj.wallet.wallet.service.TransferService;
import com.almotawaj.wallet.wallet.service.WalletAccessPolicy;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentRequestService {
    private final PaymentRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RecipientResolver recipientResolver;
    private final WalletAccessPolicy accessPolicy;
    private final TransferService transferService;
    private final PaymentRequestNotifier notifier;
    private final WalletHolderNames names;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public PageResponse<PaymentRequestResponse> list(UUID userId, Pageable pageable) {
        ensureWalletHolder(userId);
        return PageResponse.from(requestRepository.findByRequesterIdOrPayerId(userId, pageable),
                request -> PaymentRequestResponse.from(request, names));
    }

    @Transactional
    public PaymentRequestResponse create(UUID requesterId, CreatePaymentRequest request) {
        ensureWalletHolder(requesterId);
        User payer = recipientResolver.resolve(requesterId, request.payer());
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setRequester(userRepository.getReferenceById(requesterId));
        paymentRequest.setPayer(payer);
        paymentRequest.setAmount(request.amount());
        paymentRequest.setNote(request.note() == null || request.note().isBlank() ? null : request.note().strip());

        PaymentRequestResponse response = PaymentRequestResponse.from(requestRepository.saveAndFlush(paymentRequest), names);
        record(requesterId, AuditAction.PAYMENT_REQUEST_CREATED, response.id());
        notifier.created(response);
        return response;
    }

    @Transactional
    public WalletTransactionResponse pay(UUID payerId, UUID requestId) {
        PaymentRequest request = pending(requestRepository.findByIdAndPayerIdForUpdate(requestId, payerId));
        WalletTransactionResponse transaction = transferService.transfer(payerId,
                new TransferRequest(request.getRequester().getEmailAddress(), request.getAmount(), request.getNote()));
        close(request, PaymentRequestStatus.PAID, payerId, AuditAction.PAYMENT_REQUEST_PAID);
        return transaction;
    }

    @Transactional
    public PaymentRequestResponse decline(UUID payerId, UUID requestId) {
        ensureWalletHolder(payerId);
        PaymentRequest request = pending(requestRepository.findByIdAndPayerIdForUpdate(requestId, payerId));
        return close(request, PaymentRequestStatus.DECLINED, payerId, AuditAction.PAYMENT_REQUEST_DECLINED);
    }

    @Transactional
    public PaymentRequestResponse cancel(UUID requesterId, UUID requestId) {
        ensureWalletHolder(requesterId);
        PaymentRequest request = pending(requestRepository.findByIdAndRequesterIdForUpdate(requestId, requesterId));
        return close(request, PaymentRequestStatus.CANCELLED, requesterId, AuditAction.PAYMENT_REQUEST_CANCELLED);
    }

    private void ensureWalletHolder(UUID userId) {
        accessPolicy.ensureCanUseWallet(userRepository.findById(userId)
                .orElseThrow(() -> new InformationNotFoundException(ErrorMessages.USER_NOT_FOUND)));
    }

    private PaymentRequest pending(Optional<PaymentRequest> found) {
        PaymentRequest request = found.orElseThrow(() -> new InformationNotFoundException(
                ErrorMessages.PAYMENT_REQUEST_NOT_FOUND, ErrorCodes.PAYMENT_REQUEST_NOT_FOUND));
        if (request.getStatus() != PaymentRequestStatus.PENDING) {
            throw new BusinessRuleException(ErrorMessages.PAYMENT_REQUEST_NOT_PENDING, ErrorCodes.PAYMENT_REQUEST_NOT_PENDING);
        }
        return request;
    }

    private PaymentRequestResponse close(PaymentRequest request, PaymentRequestStatus status, UUID actorId, AuditAction action) {
        request.setStatus(status);
        PaymentRequestResponse response = PaymentRequestResponse.from(requestRepository.saveAndFlush(request), names);
        record(actorId, action, request.getId());
        notifier.updated(response);
        return response;
    }

    private void record(UUID actorId, AuditAction action, UUID requestId) {
        auditService.record(actorId, action, AuditTargetType.PAYMENT_REQUEST, requestId, null);
    }
}
