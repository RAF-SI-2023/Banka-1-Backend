Feature: Fetching Service Integration Tests For Listing Stocks
#  Scenario: Fetch Listing Stock By Ticker
#    Given the fetching service is running
#    When I fetch listing stock by ticker "ORCL"
#    Then the fetched listing stock "ORCL" should be stored in the database

  Scenario: Fetch N Listing Histories
    Given the fetching service is running
    When I fetch N listing histories
    Then the fetched listing histories should be stored in the database

#  Scenario: Fetch Single Listing History By Ticker
#    Given the fetching service is running
#    When I fetch single listing history by ticker "DT" from an external API
#    Then the fetched listing history for "DT" should be stored in the database

  Scenario: Fetch N Listing Stocks
    Given the fetching service is running
    When I fetch N listing stocks from an external API
    Then the fetched listing stocks should be stored in the database