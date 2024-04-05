Feature: Get permissions of a user
  Scenario: API should return permission of a given user using id
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/permissions/employeeId/100"
    Then i should get response with status 200
  Scenario: API should return permission of a given user using email
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/employee/permissions/email/admin@admin.com"
    Then i should get response with status 200