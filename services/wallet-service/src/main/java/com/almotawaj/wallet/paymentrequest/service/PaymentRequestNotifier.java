package com.almotawaj.wallet.paymentrequest.service;

import com.almotawaj.wallet.config.constants.NotificationConstants;
import com.almotawaj.wallet.notification.service.NotificationHub;
import com.almotawaj.wallet.paymentrequest.model.response.PaymentRequestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRequestNotifier {
    private final NotificationHub notificationHub;

    public void created(PaymentRequestResponse request) {
        sendAfterCommit(request.payerId(), NotificationConstants.PAYMENT_REQUESTED, request);
        sendAfterCommit(request.requesterId(), NotificationConstants.PAYMENT_REQUEST_UPDATED, request);
    }

    public void updated(PaymentRequestResponse request) {
        sendAfterCommit(request.requesterId(), NotificationConstants.PAYMENT_REQUEST_UPDATED, request);
        sendAfterCommit(request.payerId(), NotificationConstants.PAYMENT_REQUEST_UPDATED, request);
    }

    private void sendAfterCommit(UUID userId, String eventName, PaymentRequestResponse request) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationHub.send(userId, eventName, request);
            }
        });
    }
}
