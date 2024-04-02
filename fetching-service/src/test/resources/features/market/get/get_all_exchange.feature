Feature: user can grab all exchanges
  Scenario: user wants to grab all exchanges
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/exchange"
    Then i should get response with status 200
