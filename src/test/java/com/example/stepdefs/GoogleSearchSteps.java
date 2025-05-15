package com.example.stepdefs;

import com.example.pages.GooglePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleSearchSteps {

    private final GooglePage googlePage = new GooglePage();

    @Given("I am on the Google search page")
    public void i_am_on_the_google_search_page() {
        googlePage.navigateToGoogle();
    }

    @When("I search for {string}")
    public void i_search_for(String searchTerm) {
        googlePage.searchFor(searchTerm);
    }

    @Then("I should see search results")
    public void i_should_see_search_results() {
        assertTrue(googlePage.areSearchResultsDisplayed(), "Search results are not displayed");
    }
}