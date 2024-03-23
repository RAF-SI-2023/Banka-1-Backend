Feature: Get all users that fit the given parameters
  Scenario: API should return all users that fit criteria
    Given i am logged in with email "admin@admin.com" and password "admin"
    And user provides email "admin@admin.com"
    And user provides first name "admin"
    And user provides last name "admin"
    And user provides position "admin"
    When User calls get on "/user/search"
    Then i should get response with status 200
#    And Response body is the correct JSON list of users