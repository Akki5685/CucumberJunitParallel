package com.example.hooks;

import com.codeborne.selenide.Selenide;
import com.example.config.DriverFactory;
import com.example.utils.ExtentReportLogger;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Get execution mode from cucumber.properties
        String executionMode = getProperty("execution.mode", "local");
        boolean isRemoteExecution = "lambdatest".equalsIgnoreCase(executionMode);

        // Default configuration
        String browser = getProperty("browser", "chrome");

        if (isRemoteExecution) {
            // LambdaTest configuration
            String version = getProperty("browser.version", "latest");
            String platform = getProperty("platform", "Windows 10");

            // Get LambdaTest credentials - first from properties, then from system properties
            String lambdaUsername = getProperty("lambdatest.username",
                    System.getProperty("LT_USERNAME", "YOUR_LAMBDATEST_USERNAME"));
            String lambdaAccessKey = getProperty("lambdatest.accesskey",
                    System.getProperty("LT_ACCESS_KEY", "YOUR_LAMBDATEST_ACCESS_KEY"));

            DriverFactory.setupRemoteDriver(browser, version, platform, lambdaUsername, lambdaAccessKey);
            ExtentReportLogger.logInfo("Running test on LambdaTest with browser: " + browser);
        } else {
            // Local execution
            DriverFactory.setupLocalDriver(browser);
            ExtentReportLogger.logInfo("Running test locally with browser: " + browser);
        }
    }

    @AfterStep
    public void afterStep(Scenario scenario) {
        // Take screenshot if step fails
        if (scenario.isFailed()) {
            byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failed Step Screenshot");
            ExtentReportLogger.logWithScreenshot(com.aventstack.extentreports.Status.FAIL,
                    "Step failed", true);
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        // Take final screenshot
        byte[] screenshot = Selenide.screenshot(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "Final Screenshot");

        // Log scenario status
        if (scenario.isFailed()) {
            ExtentReportLogger.logFail("Scenario failed: " + scenario.getName());
        } else {
            ExtentReportLogger.logPass("Scenario passed: " + scenario.getName());
        }

        DriverFactory.quitDriver();
    }

    /**
     * Gets a property value from system properties first, then from cucumber.properties
     */
    private String getProperty(String key, String defaultValue) {
        // First check system properties (highest priority)
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        // Then check cucumber.properties
        value = io.cucumber.core.options.CucumberProperties.create().get(key);
        if (value != null) {
            return value;
        }

        // Return default if not found
        return defaultValue;
    }
}