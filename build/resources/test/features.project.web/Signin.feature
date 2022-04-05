@Sign_in
  Feature: Test to sign in to the back office
  @Sign_in
    Scenario: Verify that user able to sign in
      Given I go to main page
       And I wait for 20 seconds
       Given I click on "Sign In"
       Then I type on "EMAIL" text "root"
       And I type on "PASSWORD" text "Yx'Tn9W5"
       And I click on "SignIn Button"
       And I type on "Name" text "Abbottstown"
    
    