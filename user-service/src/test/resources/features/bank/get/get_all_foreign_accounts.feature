@Ignore
Feature: user can grab all foreign accounts
  Scenario: user wants to search all foreign accounts
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/balance/foreign_currency"
    Then i should get response with status 200
    And i should get all foreign accounts