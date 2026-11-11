package com.almotawaj.wallet.cucumber;

import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class TransferSteps {
    @Autowired
    private ScenarioState state;
    @Autowired
    private ApiClient apiClient;

    @When("{string} sends {bigdecimal} to {string}")
    public void sends(String senderEmail, BigDecimal amount, String recipientEmail) throws Exception {
        String body = """
                {"recipient": "%s", "amount": %s}
                """.formatted(recipientEmail, amount);

        state.setResponse(apiClient.post(senderEmail, "/api/wallet/transfers", body));
    }
}