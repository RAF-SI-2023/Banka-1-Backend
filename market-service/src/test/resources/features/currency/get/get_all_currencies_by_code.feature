Feature: users can grab all currencies by code
  Scenario: grab all currencies by code
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/currency/code/CD1"
    Then i should get response with status 200