Feature: users can create payments and grab info about them later
  Scenario: customer requests his otp code
    Given customer is logged in with email "user@test.com" and password "admin"
    When user calls POST on "/payment/sendCode"
    Then i should get response with status 200
    And i should have my OTP code set

  Scenario: customer creates a new payment with his otp code
    Given i am logged in with email "admin@admin.com" and password "admin"
    And customer got his OTP code
    And sender account number is "1234567890"
    And receiver name is "test"
    And receiver account number is "0987654321"
    And amount is "100"
    And paymentCode is "test"
    And model is "test"
    And referenceNumber is "test"
    And paymentPurpose is "test"
    When user calls POST on "/payment"
    Then i should get response with status 200

    Scenario: employee gets all payments
      Given i am logged in with email "admin@admin.com" and password "admin"
      When User calls get on "/payment/getAll/1234567890"
      Then i should get response with status 200
      And response should contain payment with receiver account number "0987654321"

