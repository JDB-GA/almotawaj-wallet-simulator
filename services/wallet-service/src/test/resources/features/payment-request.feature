Feature: Payment request

  Scenario: A client pays a payment request
    Given a client "payer@example.com" with balance 100.000
    And a client "requester@example.com" with balance 0.000
    When "requester@example.com" requests 40.000 from "payer@example.com"
    And "payer@example.com" pays the request
    Then the response status is 201
    And "payer@example.com" has balance 60.000
    And "requester@example.com" has balance 40.000

