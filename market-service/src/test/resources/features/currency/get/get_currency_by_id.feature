Feature: users can grab currency by id
  Scenario: grab currency by id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/currency/100000"
    Then i should get response with status 200
    And Response body is the correct exchange JSON