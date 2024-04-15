Feature: customers and employees can interact with bank accounts
  Scenario: employee creates a new personal bank account
    Given i am logged in with email "admin@admin.com" and password "admin"
    And new accountType is "CURRENT"
    And new accountName is "My Account"
    And new accountBalance is "1000"
    And new avaliableBalance is "1000"
    And new currencyCode is "TST"
    And new subtype is "PERSONAL"
    And new maintenanceFee is 100.0
    And new customerId is 101
    When user calls POST on "/account/create"
    Then i should get response with status 200

  Scenario: employee creates a new business bank account
    Given i am logged in with email "admin@admin.com" and password "admin"
    And new accountType is "BUSINESS"
    And new accountName is "BUSINESS Account"
    And new accountBalance is "1000"
    And new avaliableBalance is "1000"
    And new currencyCode is "TST"
    And new subtype is "PERSONAL"
    And new maintenanceFee is 100.0
    And new companyId is 1
    When user calls POST on "/account/create"
    Then i should get response with status 200

  Scenario: employee wants to grab all bank accounts he created
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/account/getCreator/100"
    Then i should get response with status 200
    And i should get both bank account i created

  Scenario: employee can grab all company accounts
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/account/getCompany/1"
    Then i should get response with status 200
    And i should get company bank account i created

  Scenario: employee can grab all personal accounts from user
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/account/getCustomer/101"
    Then i should get response with status 200
    And i should get personal bank account i created

  Scenario: employee can grab all cards from customer
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/account/getAllCards/101"
    Then i should get response with status 200

  Scenario: employee can grab all cards using bank account
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/account/getAllCards/0123456789"
    Then i should get response with status 200

  Scenario: customer can edis his account name
    Given customer is logged in with email "user@test.com" and password "admin"
    And bankaccountNumber is "1234567890"
    And new bank account name is "test12345"
    When i send PUT request to "/account"
    Then i should get response with status 200
    And bank account name should be changed
