Feature: users with permission want to edit users
  Scenario: user wants to change users first name
    Given i am logged in with email "admin@admin.com" and password "admin"
    And user with email "petarpetrovic@gmail.com" exists
    When i select user with email "petarpetrovic@gmail.com" to change
    And i change first name to "John"
    And i send PUT request to "/customer"
    Then user with email "petarpetrovic@gmail.com" has his first name changed to "John"