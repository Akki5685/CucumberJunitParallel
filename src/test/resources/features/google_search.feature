Feature: Google Search Functionality

  @chrome
  Scenario: Search for Cucumber
    Given I am on the Google search page
    When I search for "Cucumber testing framework"
    Then I should see search results

  @firefox
  Scenario: Search for Selenide
    Given I am on the Google search page
    When I search for "Selenide testing framework"
    Then I should see search results

  @edge
  Scenario: Search for LambdaTest
    Given I am on the Google search page
    When I search for "LambdaTest automation"
    Then I should see search results