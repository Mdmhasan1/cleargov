@search
Feature: search functionality

  @tagExample
  Scenario: Verify that user able to search
    Given I go to main page
    And I type on "search button" text "bull"
    And I wait for 20 seconds
    And I click on "First Search Result"


