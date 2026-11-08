package com.almotawaj.wallet.payout.service;

import com.almotawaj.wallet.audit.model.AuditAction;
import com.almotawaj.wallet.audit.model.AuditTargetType;
import com.almotawaj.wallet.audit.service.AuditService;
import com.almotawaj.wallet.common.model.response.PageResponse;
import com.almotawaj.wallet.config.constants.ErrorCodes;
import com.almotawaj.wallet.config.constants.ErrorMessages;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.exception.InformationNotFoundException;
import com.almotawaj.wallet.payout.event.PayoutBatchCreatedEvent;
import com.almotawaj.wallet.payout.model.PayoutBatch;
import com.almotawaj.wallet.payout.model.PayoutItem;
import com.almotawaj.wallet.payout.model.request.CreatePayoutRequest;
import com.almotawaj.wallet.payout.model.request.PayoutItemRequest;
import com.almotawaj.wallet.payout.model.response.PayoutBatchResponse;
import com.almotawaj.wallet.payout.model.response.PayoutItemResponse;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import com.almotawaj.wallet.payout.repository.PayoutItemRepository;
import com.almotawaj.wallet.user.model.User;
import com.almotawaj.wallet.wallet.service.RecipientResolver;
import com.almotawaj.wallet.wallet.service.WalletHolderNames;
import com.almotawaj.wallet.wallet.service.WalletProvisioner;
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

    /*
     * Event Publisher from SpringFramework (You have to use listener in the place that you want to trigger)
     * Spring’s message sender. publishEvent(event) announces something happened
     */
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PayoutBatchResponse create(UUID merchantId, CreatePayoutRequest request) {


        User merchant = provisioner.getOrCreate(merchantId).getUser();
        Set<UUID> recipients = new HashSet<>();
        List<PayoutItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;


        /*
         * Loop throw CreatePayoutRequest(the request the items)
         * Will resolve each recipient using the PayoutItemRequest (each item is a line)
         */
        for (PayoutItemRequest line : request.items()) {
            User recipient = recipientResolver.resolve(merchantId, line.recipient());
            if (!recipients.add(recipient.getId())) {
                throw new BusinessRuleException(ErrorMessages.PAYOUT_RECIPIENT_DUPLICATE, ErrorCodes.PAYOUT_RECIPIENT_DUPLICATE);
            }

            /*
             * Create payout item object and set recipient and amount
             * Then add it to the array list of payout items
             * add the amount in the batch total
             */
            PayoutItem item = new PayoutItem();
            item.setRecipient(recipient);
            item.setAmount(line.amount());
            items.add(item);
            total = total.add(line.amount());
        }

        // set the values for the batch object
        PayoutBatch batch = new PayoutBatch();
        batch.setMerchant(merchant);
        batch.setItemCount(items.size());
        batch.setTotalAmount(total);
        batchRepository.save(batch);

        // Set the batch id for each payout item after saving the batch record
        items.forEach(item -> item.setBatch(batch));

        // Save items and flush (Flushes all pending changes to the database)
        itemRepository.saveAll(items);
        batchRepository.flush();

        auditService.record(merchantId, AuditAction.PAYOUT_BATCH_CREATED, AuditTargetType.PAYOUT_BATCH, batch.getId(), null);

        // Event Publisher: publishes the event so that PayoutRunner will listen to this event and apply the needed operations
        eventPublisher.publishEvent(new PayoutBatchCreatedEvent(batch.getId()));
        log.info(LogMessages.PAYOUT_BATCH_CREATED, merchantId, batch.getId(), items.size());

        // Map it as PayoutBatchResponse
        return PayoutBatchResponse.from(batch, items.stream().map(this::toItemResponse).toList());
    }


    /*
     * Finds the batch by BatchId and MerchantId
     * Finds payout items using the BatchId
     * Forms & Returns PayoutBatchResponse
     */
    @Transactional(readOnly = true)
    public PayoutBatchResponse get(UUID merchantId, UUID batchId) {
        PayoutBatch batch = batchRepository.findByIdAndMerchantId(batchId, merchantId).orElseThrow(() ->
                new InformationNotFoundException(ErrorMessages.PAYOUT_BATCH_NOT_FOUND, ErrorCodes.PAYOUT_BATCH_NOT_FOUND));
        List<PayoutItemResponse> items = itemRepository.findByBatchId(batchId).stream().map(this::toItemResponse).toList();

        return PayoutBatchResponse.from(batch, items);
    }

    // Lists the merchant's paginated payout batches without their items
    @Transactional(readOnly = true)
    public PageResponse<PayoutBatchResponse> list(UUID merchantId, Pageable pageable) {
        return PageResponse.from(batchRepository.findByMerchantId(merchantId, pageable), batch -> PayoutBatchResponse.from(batch, List.of()));
    }

    // Maps a PayoutItem to its response, with the recipient's name masked
    private PayoutItemResponse toItemResponse(PayoutItem item) {
        return PayoutItemResponse.from(item, names.maskedName(item.getRecipient()));
    }
}
