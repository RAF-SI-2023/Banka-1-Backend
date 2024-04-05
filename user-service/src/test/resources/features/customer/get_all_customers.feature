Feature: user can grab all customers
  Scenario: user wants to grab all customers
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/customer/getAll"
    Then i should get response with status 200
    And i should get all customers
