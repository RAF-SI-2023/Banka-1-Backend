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
    When User calls get on "/employee/getAll"
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

  @Ignore
  Scenario: admin can filter employees
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/search?email=drugizaposleni@gmail.rs"
    Then i should get response with status 200
    And response should contain correct employee

  Scenario: employee wants to reset his password
    Given i am logged in with email "drugizaposleni@gmail.rs" and password "lepasela"
    When user calls POST on "/employee/reset/drugizaposleni@gmail.rs"
    Then i should get response with status 200

  Scenario: employee enters his new password
    Given employee wants new password to be "lepogore"
    And employee got his reset password token using email
    When user calls POST on "/employee/newpassword/token"
    Then i should get response with status 200
    And my account should be activated and password should be set to "lepogore"

  Scenario: admin wants to edit a employee
    Given i am logged in with email "admin@admin.com" and password "admin"
    And i want to edit employee with email "drugizaposleni@gmail.rs"
    And i want to set employees first name to "Aleksa"
    And i want to set employees last name to "Aleksic"
    When i send PUT request to "/employee/"
    Then i should get response with status 200
    And employee first name should be "Aleksa"
    And employee last name should be "Aleksic"

  Scenario: admin can change employee permissions
    Given i am logged in with email "admin@admin.com" and password "admin"
    And i want to change employee permisions with email "drugizaposleni@gmail.rs"
    And i want to give him permission "addUser"
    And i want to add him permissions
    When user calls POST on "/employee/permission/employeeId"
    Then i should get response with status 200
    And employee should have permission "addUser"


  Scenario: admin can change employee permissions1
    Given i am logged in with email "drugizaposleni@gmail.rs" and password "lepogore"
    And employee has first name "Petar"
    And employee has last name "Petrovic"
    And employee has phone number "512312321"
    And employee has email "drugizaposleni1@gmail.rs"
    And employee has jmbg "0254203483"
    And employee has position "AGENT"
    And employee is active
    And employee order limit is "10000.0"
    And employee requireApproval is false
    When user calls POST on "/employee/createEmployee"
    Then i should get response with status 200

  Scenario: admin can change employee permissions
    Given i am logged in with email "admin@admin.com" and password "admin"
    And i want to change employee permisions with email "drugizaposleni@gmail.rs"
    And i want to give him permission "addUser"
    And i want to remove him permissions
    When user calls POST on "/employee/permission/employeeId"
    Then i should get response with status 200

  Scenario: admin can change employee permissions1
    Given i am logged in with email "drugizaposleni@gmail.rs" and password "lepogore"
    And employee has first name "Petar"
    And employee has last name "Petrovic"
    And employee has phone number "512312321"
    And employee has email "drugizaposleni1@gmail.rs"
    And employee has jmbg "0254203483"
    And employee has position "AGENT"
    And employee is active
    And employee order limit is "10000.0"
    And employee requireApproval is false
    When user calls POST on "/employee/createEmployee"
    Then i should get response with status 403

  Scenario: admin can change employee permissions
    Given i am logged in with email "admin@admin.com" and password "admin"
    And i want to change employee permisions with email "drugizaposleni@gmail.rs"
    And i want to give him permission "addUser"
    And i want to add him permissions
    When user calls POST on "/employee/permission/employeeId"
    Then i should get response with status 200
    And employee should have permission "addUser"

  Scenario: admin can grab all permissions of a employee using id
    Given i am logged in with email "admin@admin.com" and password "admin"
    And admin knows id of the employee
    When User calls get on "/employee/permissions/employeeId/id"
    Then i should get response with status 200
    And response should contain all permissions of the employee

  Scenario: admin can grab all permissions of a employee using email
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/permissions/email/drugizaposleni@gmail.rs"
    Then i should get response with status 200
    And response should contain all permissions of the employee

  Scenario: admin wants to reset current limit for employee
    Given i am logged in with email "admin@admin.com" and password "admin"
    And admin knows id of the employee
    When i send PUT request to "/employee/limits/reset/id"
    Then i should get response with status 200
    And employee limit should be reset

  Scenario: admin wants to set a order limit for employee
    Given i am logged in with email "admin@admin.com" and password "admin"
    And admin knows id of the employee
    And limit is "300"
    And approvalRequired is True
    When i send PUT request to "/employee/limits/newLimit"
    Then i should get response with status 200
    And employee limit should be set to "300"

  Scenario: admin wants to grab all limits for all employees
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/limits/getAll"
    Then i should get response with status 200
    And response should contain limit of user with email "drugizaposleni@gmail.com"

  @Ignore
  Scenario: admin can remove a employee
    Given i am logged in with email "admin@admin.com" and password "admin"
    And admin knows id of the employee
    When i send DELETE request to "/employee/remove/id"
    Then i should get response with status 200
    And user with email "drugizaposleni@gmail.com" should not exist anymore
