Feature: Bulk payout

  Scenario: A merchant pays two clients at once
    Given a merchant "shop@example.com" with balance 100.000
    And a client "first@example.com" with balance 0.000
    And a client "second@example.com" with balance 0.000
    When "shop@example.com" pays out 10.000 to "first@example.com" and 20.000 to "second@example.com"
    Then the response status is 202
    And the payout is completed
    And "shop@example.com" has balance 70.000
    And "first@example.com" has balance 10.000
    And "second@example.com" has balance 20.000
