package com.example.hooks;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.example.config.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Default configuration - can be customized based on tags or scenario names
        String browser = "chrome";
        String version = "latest";
        String platform = "Windows 10";

        // You can extract browser info from scenario tags if needed
        if (scenario.getSourceTagNames().contains("@firefox")) {
            browser = "firefox";
        } else if (scenario.getSourceTagNames().contains("@edge")) {
            browser = "MicrosoftEdge";
        }

        DriverFactory.setupDriver(browser, version, platform);
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        if (scenario.isFailed()) {


                try {
                    // Get the raw screenshot from the WebDriver
                    byte[] screenshot = ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", "Screenshot of failure (alternative method)");
                    System.out.println("Screenshot taken with alternative method");
                } catch (Exception ex) {
                    System.err.println("Alternative screenshot method also failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }


    @After
    public void tearDown(Scenario scenario) {
        DriverFactory.quitDriver();
    }
}