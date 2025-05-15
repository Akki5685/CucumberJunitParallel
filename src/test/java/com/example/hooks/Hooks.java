package com.example.hooks;

import com.example.config.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

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

    @After
    public void tearDown(Scenario scenario) {
        DriverFactory.quitDriver();
    }
}