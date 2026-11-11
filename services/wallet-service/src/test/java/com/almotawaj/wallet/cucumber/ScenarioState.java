package com.almotawaj.wallet.cucumber;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;

@Component
@ScenarioScope
@Getter
@Setter
public class ScenarioState {
    private HttpResponse<String> response;
    private String requestId;
    private int counter;
}
