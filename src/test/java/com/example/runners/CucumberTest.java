package com.example.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example")
@ConfigurationParameter(key = PARALLEL_EXECUTION_ENABLED_PROPERTY_NAME, value = "true")
public class CucumberTest {
    // This class serves as an entry point for JUnit to run Cucumber tests

    static {
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new java.util.Date());
        // Print execution parameters at startup
        System.out.println("=== Test Execution Configuration ===");
         String reportPath = "pretty, html:build/reports/tests/report-" + timestamp + ".html, rerun:target/failedrerun.txt";
        System.setProperty("cucumber.plugin", reportPath);
        System.out.println("Browser: " + System.getProperty("browser", "chrome"));
        System.out.println("Version: " + System.getProperty("version", "latest"));
        System.out.println("Platform: " + System.getProperty("platform", "Windows 10"));
        System.out.println("Build Name: " + System.getProperty("build.name", "Cucumber-JUnit5-Selenide-Parallel"));
        System.out.println("===================================");

            // Disable Selenide screenshots programmatically
            com.codeborne.selenide.Configuration.screenshots = false;
            com.codeborne.selenide.Configuration.savePageSource = false;

            System.out.println("Selenide screenshots disabled");

    }
}