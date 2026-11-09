package com.almotawaj.wallet.account.event;

import com.almotawaj.wallet.account.service.AccountHolderService;
import com.almotawaj.wallet.config.constants.KafkaConstants;
import com.almotawaj.wallet.config.constants.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventListener {
    private final AccountHolderService accountHolderService;
    private final JsonMapper jsonMapper;

    @KafkaListener(topics = KafkaConstants.ACCOUNT_EVENTS)
    public void onAccountCreated(String payload) {
        AccountCreated event = jsonMapper.readValue(payload, AccountCreated.class);
        accountHolderService.createWalletIfMissing(event.userId());
        log.info(LogMessages.WALLET_CREATED, event.userId());
    }
}
