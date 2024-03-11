Feature: Get a specific user by email
  Scenario: API should return the user with the matching email
    When User calls get on "/user/get/admin@admin.com"
    Then Response body is the correct user JSON