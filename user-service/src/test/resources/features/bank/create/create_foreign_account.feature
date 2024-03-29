@Ignore
Feature: user can create foreign account
  Scenario: user can create foreign account
    Given i am logged in with email "admin@admin.com" and password "admin"
    And ownerId is "101"
    And createdByAgentId is "100"
    And currency is "CD1"
    And typeOfAccount is "user"
    And subtypeOfAccount is "user"
    And maintenanceCost is "100.0"
    And defaultCurrency is "true"
    And allowedCurrencies is "CD1"
    When user calls POST on "/balance/foreign_currency/create"
    Then new foreign account should be created