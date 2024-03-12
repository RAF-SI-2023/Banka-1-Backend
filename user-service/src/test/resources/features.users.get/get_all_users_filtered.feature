Feature: Get all users that fit the given parameters
  Scenario: API should return all users that fit criteria
    Given user provides email "admin@admin.com"
    And user provides first name "admin"
    And user provides last name "admin"
    And user provides position "admin"
    When User calls get on "/user/search"
    Then Response body is the correct JSON list of users