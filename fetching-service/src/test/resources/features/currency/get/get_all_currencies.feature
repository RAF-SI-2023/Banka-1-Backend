Feature: users can grab all inflations
  Scenario: grab all inflations
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/currency"
    Then i should get response with status 200