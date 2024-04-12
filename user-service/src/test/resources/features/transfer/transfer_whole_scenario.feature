Feature: user can create and get transfers
  Scenario: customer wants to do a transfer to another bank account
    Given customer is logged in with email "user@test.com" and password "admin"
    And customer wants to send money from account "1234567890" to account "0987654321"
    And customer wants to transfer 1000.00
    When user calls POST on "/transfer"
    Then i should get response with status 200

   Scenario: customer wants to get all transfers
    Given customer is logged in with email "user@test.com" and password "admin"
     When User calls get on "/transfer/getAll/1234567890"
     Then i should get response with status 200
     And response should contain transfer i made

     Scenario: customer wants to get transfer using id
       Given customer is logged in with email "user@test.com" and password "admin"
       And customer is aware of transfer id
         When User calls get on "/transfer/"
         Then i should get response with status 200
       And response should contain only the transfer i made