package com.example.hooks;

import com.example.config.DriverFactory;
import com.aventstack.extentreports.ExtentTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.AfterAll;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class Hooks {

    @Before
    public void setUp(Scenario scenario) {
        System.out.println("Starting scenario: " + scenario.getName() + " on thread " + Thread.currentThread().getId());

        String browser = System.getProperty("browser", "chrome");
        String version = System.getProperty("version", "latest");
        String platform = System.getProperty("platform", "Windows 10");

              // Setup driver and create ExtentTest inside DriverFactory
        DriverFactory.setupDriver(browser, version, platform, scenario.getName());

        // Log test start in ExtentReports
        ExtentTest test = DriverFactory.getExtentTest();
        if (test != null) {
            test.info("Starting scenario: " + scenario.getName());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        ExtentTest test = DriverFactory.getExtentTest();

        try {
            if (scenario.isFailed()) {
                // Attach screenshot to Cucumber report
                if (DriverFactory.getDriver() instanceof TakesScreenshot) {
                    byte[] screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", scenario.getName() + "_failure");

                    // Attach screenshot to ExtentReports
                    if (test != null) {
                        String base64Screenshot = ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BASE64);
                        test.fail("Scenario failed. Screenshot below:")
                                .addScreenCaptureFromBase64String(base64Screenshot, scenario.getName());
                    }
                } else if (test != null) {
                    test.fail("Scenario failed but screenshot not available.");
                }
                test.fail("Scenario failed: " + scenario.getName());
            } else {
                if (test != null) {
                    test.pass("Scenario passed: " + scenario.getName());
                }
            }

            // Set LambdaTest status
            DriverFactory.setTestStatus(!scenario.isFailed());

        } catch (Exception e) {
            if (test != null) {
                test.warning("Exception in tearDown: " + e.getMessage());
            }
        } finally {
            // Quit driver and release resources
            DriverFactory.quitDriver(!scenario.isFailed());
            System.out.println("Finished scenario: " + scenario.getName() + " on thread " + Thread.currentThread().getId());
        }
    }

    @AfterAll
    public static void afterAll() {
        DriverFactory.flushReports();
        System.out.println("ExtentReports flushed and report generated.");
    }
}