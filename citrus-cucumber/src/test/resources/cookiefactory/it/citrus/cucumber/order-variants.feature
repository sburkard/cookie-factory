@positive
Feature: Order service variants

  @vanilla
  Scenario: Client orders vanilla Cookies
    Given All queues are empty
    When I send an order for "42" "vanilla" cookies
    Then I must receive the response "Thank you for your order!" with response code "200"
    And "42" Cookies with "vanilla" flavour must be produced

  @chocolate
  Scenario: Client orders chocolate Cookies
    Given All queues are empty
    When I send an order for "84" "chocolate" cookies
    Then I must receive the response "Thank you for your order!" with response code "200"
    And "84" Cookies with "chocolate" flavour must be produced

  @fortune
  Scenario: Client orders fortune Cookies
    Given All queues are empty
    When I send an order for "11" "fortune" cookies
    Then I must receive the response "Thank you for your order!" with response code "200"
    And A fortune must be generated
    And "11" Cookies with "fortune" flavour must be produced

