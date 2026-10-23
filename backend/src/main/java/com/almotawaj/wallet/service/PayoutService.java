package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.event.PayoutBatchCreatedEvent;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.PayoutBatch;
import com.almotawaj.wallet.model.PayoutItem;
import com.almotawaj.wallet.model.User;
import com.almotawaj.wallet.model.request.CreatePayoutRequest;
import com.almotawaj.wallet.model.request.PayoutItemRequest;
import com.almotawaj.wallet.model.response.PageResponse;
import com.almotawaj.wallet.model.response.PayoutBatchResponse;
import com.almotawaj.wallet.model.response.PayoutItemResponse;
import com.almotawaj.wallet.repository.PayoutBatchRepository;
import com.almotawaj.wallet.repository.PayoutItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayoutService {
    private final PayoutBatchRepository batchRepository;
    private final PayoutItemRepository itemRepository;
    private final WalletProvisioner provisioner;
    private final RecipientResolver recipientResolver;
    private final WalletHolderNames names;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PayoutBatchResponse create(UUID merchantId, CreatePayoutRequest request) {
        User merchant = provisioner.getOrCreate(merchantId).getUser();
        Set<UUID> recipients = new HashSet<>();
        List<PayoutItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (PayoutItemRequest line : request.items()) {
            User recipient = recipientResolver.resolve(merchantId, line.recipient());
            if (!recipients.add(recipient.getId())) {
                throw new BusinessRuleException(ErrorMessages.PAYOUT_RECIPIENT_DUPLICATE, ErrorCodes.PAYOUT_RECIPIENT_DUPLICATE);
            }
            PayoutItem item = new PayoutItem();
            item.setRecipient(recipient);
            item.setAmount(line.amount());
            items.add(item);
            total = total.add(line.amount());
        }

        PayoutBatch batch = new PayoutBatch();
        batch.setMerchant(merchant);
        batch.setItemCount(items.size());
        batch.setTotalAmount(total);
        batchRepository.save(batch);
        items.forEach(item -> item.setBatch(batch));
        itemRepository.saveAll(items);
        batchRepository.flush();

        auditService.record(merchantId, AuditAction.PAYOUT_BATCH_CREATED, AuditTargetType.PAYOUT_BATCH, batch.getId(), null);
        eventPublisher.publishEvent(new PayoutBatchCreatedEvent(batch.getId()));
        log.info(LogMessages.PAYOUT_BATCH_CREATED, merchantId, batch.getId(), items.size());
        return PayoutBatchResponse.from(batch, items.stream().map(this::toItemResponse).toList());
    }

    @Transactional(readOnly = true)
    public PayoutBatchResponse get(UUID merchantId, UUID batchId) {
        PayoutBatch batch = batchRepository.findByIdAndMerchantId(batchId, merchantId).orElseThrow(() ->
                new InformationNotFoundException(ErrorMessages.PAYOUT_BATCH_NOT_FOUND, ErrorCodes.PAYOUT_BATCH_NOT_FOUND));
        return PayoutBatchResponse.from(batch, itemRepository.findByBatchId(batchId).stream().map(this::toItemResponse).toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<PayoutBatchResponse> list(UUID merchantId, Pageable pageable) {
        return PageResponse.from(batchRepository.findByMerchantId(merchantId, pageable), batch -> PayoutBatchResponse.from(batch, List.of()));
    }

    private PayoutItemResponse toItemResponse(PayoutItem item) {
        return PayoutItemResponse.from(item, names.maskedName(item.getRecipient()));
    }
}
