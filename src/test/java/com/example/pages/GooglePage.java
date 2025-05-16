package com.example.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class GooglePage {

    private final SelenideElement searchBox = $(By.xpath("//textArea[@aria-label='Search']"));
    private final SelenideElement searchButton = $("input[name='btnK']");
    private final SelenideElement resultsStats = $("#result-stats");

    public void navigateToGoogle() {
        open("https://www.google.com");
    }

    public void searchFor(String searchTerm) {
        searchBox.setValue(searchTerm);
        searchBox.pressEnter();
        // Alternative: searchButton.click();
    }

    public boolean areSearchResultsDisplayed() {
        return resultsStats.shouldBe(Condition.visible).isDisplayed();
    }
}