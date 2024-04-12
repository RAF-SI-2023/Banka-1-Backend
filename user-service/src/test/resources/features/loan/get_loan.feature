Feature: employees can grab info about loans
  Scenario: employee wants to grab all loans in the system
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/loans"
    Then i should get response with status 200

  Scenario: employee wants to grab a specific loan
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/loans/100"
    Then i should get response with status 200
    And i should get the correct loan

  Scenario: employee wants to grab all loans for a user
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/loans/user/101"
    Then i should get response with status 200
    And i should get the correct loans

  Scenario: employee wants to grab all loans for account number
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/loans/account/1234567890"
    Then i should get response with status 200
    And i should get the correct loans
