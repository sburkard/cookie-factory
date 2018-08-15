@positive
Feature: Order service variants

  Background:
    Given All queues are empty

  Scenario Outline: Client orders Cookies
    When I send an order for "<quantity>" "<flavour>" cookies
    Then I must receive the response "Thank you for your order!" with response code "200"
    And "<quantity>" Cookies with "<flavour>" flavour must be produced

  Examples:
      | flavour           | quantity |
      | vanilla           | 42       |
      | chocolate         | 11       |


  @fortune
  Scenario: Client orders fortune Cookies
    When I send an order for "11" "fortune" cookies
    Then I must receive the response "Thank you for your order!" with response code "200"
    And A fortune must be generated
    And "11" Cookies with "fortune" flavour must be produced

