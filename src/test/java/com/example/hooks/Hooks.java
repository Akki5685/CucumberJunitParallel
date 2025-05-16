package com.example.hooks;

import com.example.config.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName() + " on thread " + Thread.currentThread().getId());

        // Get browser parameters from system properties or default values
        String browser = System.getProperty("browser", "chrome");
        String version = System.getProperty("version", "latest");
        String platform = System.getProperty("platform", "Windows 10");

        // Override browser based on scenario tags if needed
        if (scenario.getSourceTagNames().contains("@firefox")) {
            browser = "firefox";
        } else if (scenario.getSourceTagNames().contains("@edge")) {
            browser = "MicrosoftEdge";
        }

        // Set up driver with scenario name for better reporting
        DriverFactory.setupDriver(browser, version, platform, scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        try {
            // Take screenshot if scenario failed
            if (scenario.isFailed() && DriverFactory.getDriver() instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", scenario.getName() + "_failure");
            }

            // Set test status on LambdaTest
            DriverFactory.setTestStatus(!scenario.isFailed());

        } finally {
            // Always quit the driver
            DriverFactory.quitDriver();
            System.out.println("Finished scenario: " + scenario.getName() + " on thread " + Thread.currentThread().getId());
        }
    }
}