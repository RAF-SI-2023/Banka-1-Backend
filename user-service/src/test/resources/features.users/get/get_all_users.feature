Feature: Get all users
  Scenario: API should return all users
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/user/getAll"
    Then Response status is "200 OK"
    And Response body is the correct JSON list of users