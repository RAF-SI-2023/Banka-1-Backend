Feature: Get a specific user by id
  Scenario: API should return the user with the matching id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/100"
    Then Response body is the correct user JSON