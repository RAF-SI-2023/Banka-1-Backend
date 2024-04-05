Feature: employee can register customer, and customer can activate his account
  Scenario: employee creates new customer and bank account for customer
    Given i am logged in with email "admin@admin.com" and password "admin"
    And customer has first name "Petar"
    And customer has last name "Petrovic"
    And customer has date of birth of "11-09-2001"
    And customer has address "Bulevar Kralja Aleksandra 73"
    And customer has phone number "0651234567"
    And customer has email "petarpetrovic@gmail.rs"
    And customer has jmbg "1508213802838"
    And customer is male
    And accountType is "CURRENT"
    And account currency is "TST"
    And maintenance cost is "1000"
    When user calls POST on "/customer/createNewCustomer"
    Then response should be true

  Scenario: customer confirms his info
    Given customer has email "petarpetrovic@gmail.rs"
    And customer got his bank account from email
    And customer has phone number "0651234567"
    When user calls POST on "/customer/initialActivation"
    Then response should be true

  Scenario: customer confirms his token
    Given customer has email "petarpetrovic@gmail.rs"
    And customer got his token from email
    And customer wants to set his password to "password"
    When user calls POST on "/customer/activate/{token}"
    Then i should get response with status 200
    And customer should have his password set to "password"
