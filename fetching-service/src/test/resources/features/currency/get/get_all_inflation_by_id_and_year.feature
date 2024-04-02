Feature: users can grab inflation by id and year
  Scenario: grab inflation by id and year
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/currency/100000/inflation"
    Then i should get response with status 200