package com.almotawaj.wallet.identity.internal;

import com.almotawaj.wallet.identity.config.constants.InternalConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;

@Component
public class WalletClient {
    private final RestClient restClient;

    public WalletClient(@Value(InternalConstants.WALLET_URL_PROPERTY) String walletUrl,
                        @Value(InternalConstants.TOKEN_PROPERTY) String internalToken) {
        this.restClient = RestClient.builder()
                .baseUrl(walletUrl)
                .defaultHeader(InternalConstants.TOKEN_HEADER, internalToken)
                .build();
    }

    public void cleanSeedData(List<UUID> userIds) {
        restClient.method(HttpMethod.DELETE).uri(InternalConstants.WALLET_SEED_DATA_PATH).body(userIds)
                .retrieve().toBodilessEntity();
    }
}
