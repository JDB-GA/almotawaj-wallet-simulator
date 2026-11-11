package com.almotawaj.wallet.cucumber;

import com.almotawaj.wallet.account.model.AccountHolder;
import com.almotawaj.wallet.account.repository.AccountHolderRepository;
import com.almotawaj.wallet.config.constants.SecurityConstants;
import com.almotawaj.wallet.support.TestTokens;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@ScenarioScope
public class ApiClient {
    @LocalServerPort
    private int port;
    @Value("${jwt-secret}")
    private String jwtSecret;
    @Autowired
    private AccountHolderRepository userRepository;

    public HttpResponse<String> post(String email, String path, String body) throws Exception {
        AccountHolder user = userRepository.findByEmailAddressIgnoreCase(email).orElseThrow();

        HttpRequest request = HttpRequest
                .newBuilder(URI.create("http://localhost:" + port + path))
                .header("Content-Type", "application/json")
                .header("Cookie", SecurityConstants.AUTH_COOKIE_NAME + "=" +
                        TestTokens.forAccount(user, jwtSecret))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        return HttpClient.newHttpClient().send(request,
                HttpResponse.BodyHandlers.ofString()
        );
    }
}
