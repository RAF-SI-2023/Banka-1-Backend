Feature: users can register to userservice
  Scenario: new user wants to register
    Given i have email "test123@gmail.com"
    And i have firstName "petar"
    And i have lastName "petrovic"
    And i have jmbg "1111111111111"
    And i have position "user"
    And i have phone number "123456789"
    And i am active
    When user calls POST on "/user/createuser"
    Then i should get my id as a response
    And email should be sent to me
