package com.example.hooks;

import com.codeborne.selenide.Selenide;
import com.example.config.DriverFactory;
import com.example.utils.DebugManager;
import com.example.utils.ExtentReportLogger;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        // Check if debug mode is enabled via system property
        if ("true".equalsIgnoreCase(System.getProperty("debug.mode"))) {
            DebugManager.enableDebugMode();
        }

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
        // Only quit the driver if the scenario passed or debug mode is disabled
        if (!scenario.isFailed() || !DebugManager.isDebugModeEnabled()) {
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
        } else {
            ExtentReportLogger.logInfo("Browser kept open for debugging. Close it manually when done.");
        }
    }
}