Feature: individual customer can make his stocks public, other customers can buy the public stocks with offered price
  Scenario: individual customer puts 10 of his stocks to be public
    Given i am logged in as customer with email "user@test.com" and password "admin"
    And add to public amount 10.0 for capital with listingId is 100003 and listingType is "STOCK"
    When i send PUT request to "/capital/addPublic"
    Then i should get response with status 200
    When User calls get on "/capital/stock/id"
    Then i should get response with status 200
    And i should have 10.0 listings of type "stock" public

# 1
  Scenario: individual customer buyer gets all public stocks of other individuals
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    When User calls get on "/capital/public/stock/all"
    Then i should get response with status 200
    And i should get all public stocks of other individual customers

  Scenario: buyer creates contract offer to buy 5 stocks for price 1000 from seller
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    And i want to buy 5.0 stocks
    And i offer him price of 1000.0 RSD
    And seller id is 101
    When user calls POST on "/contract/customer"
    Then i should get response with status 200

  Scenario: seller gets all contracts that he is in
    Given i am logged in as customer with email "user@test.com" and password "admin"
    When User calls get on "/contract/customer/getAllContracts"
    Then i should get response with status 200
    And i should get all contracts i am contributing in as a "individual"

  Scenario: seller denies contract offer that buyer made earlier
    Given i want to deny contract offer with id 1
    And with comment why i denied "Too low offer price"
    When i send PUT request to "/contract/deny/id"
    Then i should get response with status 200
    When User calls get on "/capital/stock/id"
    Then i should get response with status 200
    And i should have 10.0 listings of type "stock" public
# 2
  Scenario: individual customer buyer gets all public stocks of other individuals
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    When User calls get on "/capital/public/stock/all"
    Then i should get response with status 200
    And i should get all public stocks of other individual customers

  Scenario: buyer creates contract offer to buy 5 stocks for price 1500 from user@test.com
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    And i want to buy 5.0 stocks
    And i offer him price of 1500.0 RSD
    And seller id is 101
    When user calls POST on "/contract/customer"
    Then i should get response with status 200

  Scenario: seller gets all contracts that he is in
    Given i am logged in as customer with email "user@test.com" and password "admin"
    When User calls get on "/contract/customer/getAllContracts"
    Then i should get response with status 200
    And i should get all contracts i am contributing in as a "individual"

  Scenario: seller accepts contract offer that buyer made earlier
    Given i want to accept contract offer with id 2
    When i send PUT request to "/contract/accept/id"
    Then i should get response with status 200
    When User calls get on "/capital/stock/id"
    Then i should get response with status 200
    And i should have 10.0 listings of type "stock" public

  Scenario: supervisor gets all contracts
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/contract/supervisor/getAllContracts"
    Then i should get response with status 200
    And i should get all not finalized contracts

  Scenario: supervisor approves the contract offer
    Given i want to accept contract offer with id 2
    When i send PUT request to "/contract/accept/id"
    Then i should get response with status 200

  Scenario: seller sees that he has sold some of his public stock
    Given i am logged in as customer with email "user@test.com" and password "admin"
    When User calls get on "/capital/stock/id"
    Then i should get response with status 200
    And i should have 5.0 listings of type "stock" public
    When User calls get on "/contract/customer/getAllContracts"
    Then i should get response with status 200
    And contract with id 2 should be finalized

############################################################
  # accept from seller, deny from bank
  Scenario: individual customer buyer gets all public stocks of other individuals
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    When User calls get on "/capital/public/stock/all"
    Then i should get response with status 200
    And i should get all public stocks of other individual customers

  Scenario: buyer creates contract offer to buy 5 stocks for price 1200 from user@test.com
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    And i want to buy 2.0 stocks
    And i offer him price of 1200.0 RSD
    And seller id is 101
    When user calls POST on "/contract/customer"
    Then i should get response with status 200

  Scenario: seller gets all contracts that he is in
    Given i am logged in as customer with email "user@test.com" and password "admin"
    When User calls get on "/contract/customer/getAllContracts"
    Then i should get response with status 200
    And i should get all contracts i am contributing in as a "individual"

  Scenario: seller accepts contract offer that buyer made earlier
    Given i want to accept contract offer with id 3
    When i send PUT request to "/contract/accept/id"
    Then i should get response with status 200

  Scenario: supervisor gets all contracts
    Given i am logged in with email "admin@admin.com" and password "admin"
    When User calls get on "/contract/supervisor/getAllContracts"
    Then i should get response with status 200
    And i should get all not finalized contracts

  Scenario: supervisor denies the contract offer
    Given i want to deny contract offer with id 3
    And with comment why i denied "Denying contract"
    When i send PUT request to "/contract/deny/id"
    Then i should get response with status 200

  Scenario: seller gets all contracts that he is in
    Given i am logged in as customer with email "user@test.com" and password "admin"
    When User calls get on "/contract/customer/getAllContracts"
    Then i should get response with status 200
    And contract with id 3 should be denied

#############################################
  Scenario: individual customer wants to put his forex to be public
    Given i am logged in as customer with email "user@test.com" and password "admin"
    And add to public amount 10.0 for capital with listingId is 100001 and listingType is "FOREX"
    When i send PUT request to "/capital/addPublic"
    Then i should get response with status 403

  Scenario: individual customer wants to put his future to be public
    Given i am logged in as customer with email "user@test.com" and password "admin"
    And add to public amount 10.0 for capital with listingId is 100002 and listingType is "FUTURE"
    When i send PUT request to "/capital/addPublic"
    Then i should get response with status 403

##################################################
  Scenario: individual customer wants to see public listings from other businesses (not individuals)
    Given i am logged in as customer with email "user123@test.com" and password "admin"
    When User calls get on "/capital/public/listing/all"
    Then i should get response with status 403
###################################################
  Scenario: Non-seller wants to accept contract (but he is not a seller for that contract)
    Given i want to accept contract offer with id 1
    When i send PUT request to "/contract/accept/id"
    Then i should get response with status 403
