Feature: user with the role of admin can remove a user
  Scenario: admin wants to remove a user
    Given i am logged in with email "admin@admin.com" and password "admin"
    And user i want to delete exists
    When i send DELETE request to remove the user
    Then user is removed from the system