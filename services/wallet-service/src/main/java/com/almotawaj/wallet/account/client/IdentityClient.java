package com.almotawaj.wallet.account.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class IdentityClient {
    private static final String TOKEN_HEADER = "X-Internal-Token";
    private final RestClient restClient;

    public IdentityClient(@Value("${identity-url}") String identityUrl,
                          @Value("${internal-api-token}") String internalToken) {
        this.restClient = RestClient.builder()
                .baseUrl(identityUrl)
                .defaultHeader(TOKEN_HEADER, internalToken)
                .build();
    }

    public Optional<AccountSnapshot> findById(UUID id) {
        return call("/internal/accounts/{id}", id);
    }

    public Optional<AccountSnapshot> lookup(String identifier) {
        return call("/internal/accounts?identifier={identifier}", identifier);
    }

    private Optional<AccountSnapshot> call(String uri, Object variable) {
        try {
            return Optional.ofNullable(restClient.get().uri(uri, variable).retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> { })
                    .body(AccountSnapshot.class))
                    .filter(snapshot -> snapshot.id() != null);
        } catch (RestClientException e) {
            log.warn("identity-service call failed: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
