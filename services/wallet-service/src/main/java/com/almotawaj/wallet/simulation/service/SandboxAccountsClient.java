package com.almotawaj.wallet.simulation.service;

import com.almotawaj.wallet.account.model.AccountRole;
import com.almotawaj.wallet.config.constants.ApiPaths;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
public class SandboxAccountsClient {
    private final RestClient restClient;

    public SandboxAccountsClient(@Value(SecurityConstants.IDENTITY_URL_PROPERTY) String identityUrl,
                                 @Value(SecurityConstants.INTERNAL_TOKEN_PROPERTY) String internalToken) {
        this.restClient = RestClient.builder()
                .baseUrl(identityUrl)
                .defaultHeader(SecurityConstants.INTERNAL_TOKEN_HEADER, internalToken)
                .build();
    }

    public List<UUID> create(AccountRole role, int count, String displayName) {
        return restClient.post().uri(ApiPaths.INTERNAL_SANDBOX_ACCOUNTS)
                .body(new SandboxAccountRequest(role, count, displayName))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void delete(Collection<UUID> userIds) {
        restClient.method(HttpMethod.DELETE).uri(ApiPaths.INTERNAL_SANDBOX_ACCOUNTS)
                .body(userIds).retrieve().toBodilessEntity();
    }
}
