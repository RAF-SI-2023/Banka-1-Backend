#@Ignore
#Feature: user can grab all listings by history
#  Scenario: user wants to grab all listings by history
#    Given i am logged in with email "admin@admin.com" and password "admin"
#    And i have a ticker "testticker"
#    And i have a timestamp from "1710044796" and to "1710944796"
#    When i send GET request to "/market/listing/history/"
#    Then i should get response with status 200
