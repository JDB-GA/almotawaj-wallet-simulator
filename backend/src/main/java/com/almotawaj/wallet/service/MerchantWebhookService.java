package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.security.WebhookSigner;
import com.almotawaj.wallet.model.AuditAction;
import com.almotawaj.wallet.model.AuditTargetType;
import com.almotawaj.wallet.model.MerchantWebhook;
import com.almotawaj.wallet.model.request.UpdateWebhookRequest;
import com.almotawaj.wallet.model.response.WebhookResponse;
import com.almotawaj.wallet.repository.MerchantWebhookRepository;
import com.almotawaj.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantWebhookService {
    private final MerchantWebhookRepository webhookRepository;
    private final UserRepository userRepository;
    private final WebhookUrlPolicy urlPolicy;
    private final WebhookSigner signer;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public WebhookResponse get(UUID merchantId) {
        return webhookRepository.findByMerchantId(merchantId).map(this::toResponse).orElseGet(WebhookResponse::none);
    }

    @Transactional
    public WebhookResponse set(UUID merchantId, UpdateWebhookRequest request) {
        String url = urlPolicy.requireAllowed(request.url().strip()).toString();
        MerchantWebhook webhook = webhookRepository.findByMerchantId(merchantId).orElseGet(() -> {
            MerchantWebhook created = new MerchantWebhook();
            created.setMerchant(userRepository.getReferenceById(merchantId));
            return created;
        });
        webhook.setUrl(url);
        MerchantWebhook saved = webhookRepository.saveAndFlush(webhook);
        auditService.record(merchantId, AuditAction.WEBHOOK_UPDATED, AuditTargetType.USER, merchantId, url);
        return toResponse(saved);
    }

    @Transactional
    public void remove(UUID merchantId) {
        webhookRepository.findByMerchantId(merchantId).ifPresent(webhook -> {
            webhookRepository.delete(webhook);
            auditService.record(merchantId, AuditAction.WEBHOOK_REMOVED, AuditTargetType.USER, merchantId, null);
        });
    }

    private WebhookResponse toResponse(MerchantWebhook webhook) {
        return new WebhookResponse(webhook.getUrl(), signer.secretFor(webhook.getId()), webhook.getUpdatedAt());
    }
}
