Feature: user can activate his account by clicking the link sent by email
  Scenario: user clicks the link given to him
    Given I am a user that wants to set password to "password"
    When I go to "/user/set-password/testtoken"
    Then I should have my password set to "password"