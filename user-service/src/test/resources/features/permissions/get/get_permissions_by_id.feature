@Ignore
Feature: api wants to grab permissions of a user by id
  Scenario: api wants to grab permissions of a user by id
    Given I have a user with id 10
    And there is a permission with name "test_permission"
    And user has permission "test_permission"
    When I send a GET request to "/users/10/permissions"
    Then I should get all permission from user