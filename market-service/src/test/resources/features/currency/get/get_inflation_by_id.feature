@Ignore
Feature: users can grab inflation by id
  Scenario: grab inflation by id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/currency/100000/inflation"
    Then i should get inflation with id "100000"