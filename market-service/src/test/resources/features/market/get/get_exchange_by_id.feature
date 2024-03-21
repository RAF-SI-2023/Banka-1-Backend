Feature: user can grab all exchanges
  Scenario: user wants to grab all exchanges
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/exchange/100000"
    Then i should get response with status 200
    And Response body is the correct exchange JSON