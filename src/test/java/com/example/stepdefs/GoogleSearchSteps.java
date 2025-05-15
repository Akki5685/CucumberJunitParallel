package com.example.stepdefs;

import com.aventstack.extentreports.Status;
import com.example.pages.GooglePage;
import com.example.utils.DebugManager;
import com.example.utils.ExtentReportLogger;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoogleSearchSteps {

    private final GooglePage googlePage = new GooglePage();

    @Given("I am on the Google search page")
    public void i_am_on_the_google_search_page() {
        try {
            googlePage.navigateToGoogle();
            ExtentReportLogger.logInfo("Navigated to Google search page");
        } catch (Exception e) {
            ExtentReportLogger.logFail("Failed to navigate to Google: " + e.getMessage());

            throw e;
        }
    }

    @When("I search for {string}")
    public void i_search_for(String searchTerm) {
        try {
            ExtentReportLogger.logInfo("About to search for: " + searchTerm);
            googlePage.searchFor(searchTerm);
            ExtentReportLogger.addScreenshot("Search term entered: " + searchTerm);
        } catch (Exception e) {
            ExtentReportLogger.logFail("Failed to search: " + e.getMessage());


            throw e;
        }
    }

    @Then("I should see search results")
    public void i_should_see_search_results() {
        try {
            boolean resultsDisplayed = googlePage.areSearchResultsDisplayed();

            if (resultsDisplayed) {
                ExtentReportLogger.logPass("Search results are displayed successfully");
                ExtentReportLogger.addScreenshot(Status.PASS, "Search results displayed");
            } else {
                ExtentReportLogger.logFail("Search results are not displayed");
                ExtentReportLogger.addScreenshot(Status.FAIL, "Search results not displayed");


                throw new AssertionError("Search results are not displayed");
            }
        } catch (Exception e) {
            ExtentReportLogger.logFail("Failed to verify results: " + e.getMessage());


            throw e;
        }
    }
}