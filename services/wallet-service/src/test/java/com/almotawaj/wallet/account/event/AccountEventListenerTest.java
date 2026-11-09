package com.almotawaj.wallet.account.event;

import com.almotawaj.wallet.account.service.AccountHolderService;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountEventListenerTest {
    private final AccountHolderService accountHolderService = mock(AccountHolderService.class);
    private final AccountEventListener listener = new AccountEventListener(accountHolderService, JsonMapper.builder().build());

    @Test
    void accountCreated_createsTheWallet() {
        UUID userId = UUID.randomUUID();

        listener.onAccountCreated("{\"eventId\":\"" + UUID.randomUUID() + "\",\"userId\":\"" + userId + "\"}");

        verify(accountHolderService).createWalletIfMissing(userId);
    }
}
