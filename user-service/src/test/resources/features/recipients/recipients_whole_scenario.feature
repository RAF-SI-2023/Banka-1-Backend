Feature: customer can create, view, edit and delete recipients
  Scenario: employee creates a new recipient
    Given customer is logged in with email "user@test.com" and password "admin"
    And recipient first name is "mika"
    And recipient last name is "mikic"
    And recipient bank account number is "0987654321"
    When user calls POST on "/recipients/add"
    Then i should get response with status 200

    Scenario: customer wants to edit his recipient
      Given customer is logged in with email "user@test.com" and password "admin"
        And recipient first name is "aleksa"
        And recipient last name is "aleksic"
        And recipient bank account number is "1234567777"
      And customer wants to change recipient first name to "mika"
      When i send PUT request to "/recipients/edit"
      Then i should get response with status 200
      And recipient first name should be "mika"

      Scenario: customer wants to delete his recipient
        Given customer is logged in with email "user@test.com" and password "admin"
        When i send DELETE request to "/recipients/remove/"
        Then i should get response with status 200

        Scenario: customer wants to view his recipient
          Given customer is logged in with email "user@test.com" and password "admin"
          When User calls get on "/recipients/getAll"
          Then i should get response with status 200
          And i should NOT have recipient mika mikic "0987654321" in response
