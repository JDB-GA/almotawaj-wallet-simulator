package com.almotawaj.wallet.cucumber;

import com.almotawaj.wallet.payout.model.PayoutBatchStatus;
import com.almotawaj.wallet.payout.repository.PayoutBatchRepository;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PayoutSteps {
    @Autowired
    private ScenarioState state;
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private PayoutBatchRepository batchRepository;

    @When("{string} pays out {bigdecimal} to {string} and {bigdecimal} to {string}")
    public void paysOut(String merchantEmail, BigDecimal firstAmount, String firstRecipient,
                        BigDecimal secondAmount, String secondRecipient) throws Exception {
        String body = """
                {"items": [
                  {"recipient": "%s", "amount": %s},
                  {"recipient": "%s", "amount": %s}
                ]}
                """.formatted(firstRecipient, firstAmount, secondRecipient, secondAmount);

        state.setResponse(apiClient.post(merchantEmail, "/api/merchant/payouts", body));
        state.setRequestId(JsonMapper.builder().build()
                .readTree(state.getResponse().body()).get("id").asString());
    }

    @Then("the payout is completed")
    public void thePayoutIsCompleted() throws Exception {
        UUID batchId = UUID.fromString(state.getRequestId());
        for (int attempt = 0; attempt < 50; attempt++) {
            if (batchRepository.findById(batchId).orElseThrow().getStatus() == PayoutBatchStatus.COMPLETED) {
                return;
            }
            Thread.sleep(100);
        }
        assertThat(batchRepository.findById(batchId).orElseThrow().getStatus()).isEqualTo(PayoutBatchStatus.COMPLETED);
    }
}
