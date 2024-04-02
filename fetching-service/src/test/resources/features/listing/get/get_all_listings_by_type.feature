Feature: user can grab all listings by type
  Scenario: user wants to grab all listings by type
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/listing/get/forex"
    Then i should get response with status 200
