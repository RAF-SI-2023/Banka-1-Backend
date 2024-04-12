@Ignore
Feature: api wants to grab all permissions
  Scenario: api wants to grab all permissions
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/permission/getAll"
    Then i should get response with status 200