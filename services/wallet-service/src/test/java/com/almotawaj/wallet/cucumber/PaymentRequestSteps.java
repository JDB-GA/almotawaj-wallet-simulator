package com.almotawaj.wallet.cucumber;

import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

public class PaymentRequestSteps {
    @Autowired
    private ScenarioState state;
    @Autowired
    private ApiClient apiClient;

    @When("{string} requests {bigdecimal} from {string}")
    public void requests(String requesterEmail, BigDecimal amount, String payerEmail) throws Exception {
        String body = """
                {"payer": "%s", "amount": %s}
                """.formatted(payerEmail, amount);

        state.setResponse(apiClient.post(requesterEmail, "/api/wallet/requests", body));
        state.setRequestId(JsonMapper.builder().build()
                .readTree(state.getResponse().body()).get("id").asString());
    }

    @When("{string} pays the request")
    public void paysTheRequest(String payerEmail) throws Exception {
        String path = "/api/wallet/requests/" + state.getRequestId() + "/pay";
        state.setResponse(apiClient.post(payerEmail, path, ""));
    }
}