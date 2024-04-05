Feature: Get a specific user by email
  Scenario: API should return the user with the matching email
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/get/admin@admin.com"
    Then Response body is the correct user JSON