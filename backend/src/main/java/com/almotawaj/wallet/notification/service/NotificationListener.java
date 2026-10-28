package com.almotawaj.wallet.notification.service;

import com.almotawaj.wallet.config.constants.NotificationConstants;
import com.almotawaj.wallet.voucher.event.VoucherSoldEvent;
import com.almotawaj.wallet.voucher.model.response.VoucherSoldNotification;
import com.almotawaj.wallet.voucher.model.response.VoucherStockNotification;
import com.almotawaj.wallet.wallet.event.MoneyReceivedEvent;
import com.almotawaj.wallet.wallet.model.response.MoneyReceivedNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationHub hub;

    // Listens to MoneyReceivedEvent record
    @TransactionalEventListener
    public void onMoneyReceived(MoneyReceivedEvent event) {
        hub.send(event.recipientUserId(), NotificationConstants.MONEY_RECEIVED,
                new MoneyReceivedNotification(event.amount(), event.senderName(), event.reference()));
    }

    // Listens to VoucherSoldEvent record
    @TransactionalEventListener
    public void onVoucherSold(VoucherSoldEvent event) {
        hub.send(event.merchantId(), NotificationConstants.VOUCHER_SOLD,
                new VoucherSoldNotification(event.voucherId(), event.title(), event.price(), event.remaining()));
        hub.broadcast(NotificationConstants.VOUCHER_STOCK_CHANGED, new VoucherStockNotification(event.voucherId(), event.remaining()));
    }
}
