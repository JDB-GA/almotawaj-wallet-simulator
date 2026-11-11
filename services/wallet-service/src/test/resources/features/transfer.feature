Feature: Send money
  Scenario: A client sends money to another client
    Given a client "sender@example.com" with balance 100.000
    And a client "receiver@example.com" with balance 000.000
    When "sender@example.com" sends 25.000 to "receiver@example.com"
    Then the response status is 201
    And "sender@example.com" has balance 75.000
    And "receiver@example.com" has balance 25.000
