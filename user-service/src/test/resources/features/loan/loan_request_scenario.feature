Feature: employee can create and manage loan requests
  Scenario: employee can create a loan request
    Given i am logged in with email "admin@admin.com" and password "admin"
    And loanType is "PERSONAL"
    And loanAmount is 1000.0
    And currency is "TST"
    And loanPurpose is "test"
    And monthlyIncomeAmount is "428048"
    And monthlyIncomeCurrency is "TST"
    And permanentEmployee is true
    And employmentPeriod is "50"
    And loanTerm is "24"
    And branchOffice is "test"
    And phoneNumber is "1111111111"
    And accountNumber is "1234567890"
    When user calls POST on "/loan/requests"
    Then i should get response with status 200
    And response should be correct loanRequestDto

    Scenario: employee can get all loan requests
      Given i am logged in with email "admin@admin.com" and password "admin"
      When User calls get on "/loan/requests"
      Then i should get response with status 200

    Scenario: employee can change loan request using id
      Given i am logged in with email "admin@admin.com" and password "admin"
      And i know which loan id i am changing
      And i want to change status to "ACCEPTED"
        When i send PUT request to "/loan/requests/"
        Then i should get response with status 200
      And loan request status should be "ACCEPTED"