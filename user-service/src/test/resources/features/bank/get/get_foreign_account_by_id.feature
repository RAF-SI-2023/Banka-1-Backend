Feature: user can search for a foreign account by id
  Scenario: user wants to find foreign account by id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/balance/foreign_currency/100"
    Then i should get response with status 200