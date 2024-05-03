Feature: user can grab all listings by type
  Scenario: user wants to grab all listings by type
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/listing/get/forex"
    Then i should get response with status 200
    And "Forex" is not empty

  Scenario: user wants to grab all listings by type
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/listing/get/futures"
    Then i should get response with status 200
    And "Future" is not empty

  Scenario: user wants to grab all listings by type
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/listing/get/stock"
    Then i should get response with status 200
    And "Stock" is not empty

  Scenario: user wants to grab all listings by type
    Given i am logged in with email "admin@admin.com" and password "admin"
    When i send GET request to "/market/listing/get/losee"
    Then i should get response with status 400