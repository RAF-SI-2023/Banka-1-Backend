#Feature: employee can create and view orders
#  Scenario: employee wants to create a new order for a stock
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    And order type is "BUY"
#    And listingId is 100003
#    And listingType is "STOCK"
#    And contractSize is 30
#    And limitValue is 100.0
#    And stopValue is 90.0
#    And allOrNone is True
#    When user calls POST on "/orders"
#    Then i should get response with status 200
#
#  Scenario: employee wants to create a new order for a future
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    And order type is "BUY"
#    And listingId is 100002
#    And listingType is "FUTURE"
#    And contractSize is 30
#    And limitValue is 100.0
#    And stopValue is 90.0
#    And allOrNone is True
#    When user calls POST on "/orders"
#    Then i should get response with status 200
#
#  Scenario: employee wants to create a new order for a forex
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    And order type is "BUY"
#    And listingId is 100001
#    And listingType is "FOREX"
#    And contractSize is 30
#    And limitValue is 100.0
#    And stopValue is 90.0
#    And allOrNone is True
#    When user calls POST on "/orders"
#    Then i should get response with status 200
#
#  @Ignore
#  Scenario: employee wants to grab orders for all employees
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    When User calls get on "/orders/supervisor/getAll"
#    Then i should get response with status 200
#    And response should contain all above orders
#
#  @Ignore
#  Scenario: employee wants to grab all his orders
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    When User calls get on "/orders/getAll"
#    Then i should get response with status 200
#    And response should contain all above orders