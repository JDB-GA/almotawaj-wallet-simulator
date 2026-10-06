package com.almotawaj.wallet.service;

import com.almotawaj.wallet.config.constants.GatewayConstants;
import com.almotawaj.wallet.config.constants.LogMessages;
import com.almotawaj.wallet.config.security.WebhookSigner;
import com.almotawaj.wallet.exception.BusinessRuleException;
import com.almotawaj.wallet.model.MerchantWebhook;
import com.almotawaj.wallet.model.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookSender {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(GatewayConstants.WEBHOOK_TIMEOUT)
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    private final WebhookUrlPolicy urlPolicy;
    private final WebhookSigner signer;
    private final ObjectMapper objectMapper;

    public void send(MerchantWebhook webhook, WebhookPayload payload) throws InterruptedException {
        String body = objectMapper.writeValueAsString(payload);
        for (Duration delay : GatewayConstants.WEBHOOK_RETRY_DELAYS) {
            Thread.sleep(delay.toMillis());
            if (deliver(webhook, payload.event(), body)) {
                return;
            }
        }
        log.warn(LogMessages.WEBHOOK_FAILED, payload.sessionId(), GatewayConstants.WEBHOOK_RETRY_DELAYS.size());
    }

    private boolean deliver(MerchantWebhook webhook, String event, String body) throws InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder(urlPolicy.requireAllowed(webhook.getUrl()))
                    .timeout(GatewayConstants.WEBHOOK_TIMEOUT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(GatewayConstants.WEBHOOK_EVENT_HEADER, event)
                    .header(GatewayConstants.WEBHOOK_SIGNATURE_HEADER, signer.sign(webhook.getId(), body))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            int status = client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
            return HttpStatus.valueOf(status).is2xxSuccessful();
        } catch (IOException | BusinessRuleException | IllegalArgumentException e) {
            return false;
        }
    }
}
