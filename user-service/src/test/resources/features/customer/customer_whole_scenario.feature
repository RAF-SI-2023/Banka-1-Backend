Feature: employee can register, view and eidt customer, and customer can activate his account
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

  Scenario: employee confirms his info
    Given customer has email "petarpetrovic@gmail.rs"
    And customer got his bank account from email
    And customer has phone number "0651234567"
    When user calls POST on "/customer/initialActivation"
    Then response should be true

  Scenario: employee confirms his token
    Given customer has email "petarpetrovic@gmail.rs"
    And customer got his token from email
    And customer wants to set his password to "password"
    When user calls POST on "/customer/activate/{token}"
    Then i should get response with status 200
    And customer should have his password set to "password"

  Scenario: employee wants to grab all customers
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/customer/getAll"
    Then i should get response with status 200
    And i should get all customers

  Scenario: employee wants to edit a customer
    Given i am logged in with email "admin@admin.com" and password "admin"
    And i want to edit customer with email "petarpetrovic@gmail.rs"
    And i want to set customers first name to "Milan"
    When i send PUT request to "/customer"
    Then i should get response with status 200
    And customers name should be set to "Milan"
