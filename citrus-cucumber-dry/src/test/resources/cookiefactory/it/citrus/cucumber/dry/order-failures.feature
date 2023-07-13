@negative
Feature: Order service failures

  Scenario: Client orders a not existing Cookie flavour
    When I send the order "OrderUnknownFlavour"
    Then I must receive the response "Sorry, your order is invalid! @ignore(200)@" with response code "400"

  Scenario: Client orders without quantity
    When I send the order "OrderWithoutQuantity"
    Then I must receive the response "Sorry, your order is invalid! @ignore(200)@" with response code "400"

