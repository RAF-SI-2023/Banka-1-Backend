Feature: user can grab options by ticker
  Scenario: user wants to grab options using ticker
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/options/testticker"
    Then i should get response with status 200