Feature: admin can create new employees, view them and edit them
  Scenario: admin creates a new employee
    Given i am logged in with email "admin@admin.com" and password "admin"
    And employee has first name "Petar"
    And employee has last name "Petrovic"
    And employee has phone number "512312321"
    And employee has email "drugizaposleni@gmail.rs"
    And employee has jmbg "0254203482"
    And employee has position "AGENT"
    And employee is active
    And employee order limit is "10000.0"
    And employee requireApproval is false
    When user calls POST on "/employee/createEmployee"
    Then i should get response with status 200

  Scenario: new employee wants to activate his account
    Given i got my token using email
    And i want to set my password to "lepasela"
    When user calls POST on "/employee/activate/token"
    Then i should get response with status 200
    And my account should be activated and password should be set to "lepasela"


  Scenario: admin views all employees
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/getAllEmployees"
    Then i should get response with status 200
    And response should contain new employee

  Scenario: admin grabs employee by email
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/get/drugizaposleni@gmail.rs"
    Then i should get response with status 200
    And response should contain correct employee

  Scenario: admin grabs employee by id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/get/100"
    Then i should get response with status 200

  Scenario: admin can filter employees
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/search?email=drugizaposleni@gmail.rs"
    Then i should get response with status 200
    And response should contain correct employee