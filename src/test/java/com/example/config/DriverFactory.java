package com.example.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    private static final String LAMBDA_TEST_USERNAME ="akhil20495";
    private static final String LAMBDA_TEST_ACCESS_KEY ="LT_pW53j2Au3I7O0QkVwEKeGLGKRlsyJpZ7QbD6RCYZzl2DTJC";
    private static final String LAMBDA_TEST_HUB_URL = "https://" + LAMBDA_TEST_USERNAME + ":" + LAMBDA_TEST_ACCESS_KEY + "@hub.lambdatest.com/wd/hub";

    public static void setupDriver(String browser, String version, String platform) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("version", version);
        capabilities.setCapability("platform", platform);
        capabilities.setCapability("build", "Cucumber-JUnit5-Selenide-Parallel");
        capabilities.setCapability("name", Thread.currentThread().getName());
        capabilities.setCapability("visual", true);
        capabilities.setCapability("video", true);
        capabilities.setCapability("network", true);
        capabilities.setCapability("console", true);

        try {
            RemoteWebDriver driver = new RemoteWebDriver(new URL(LAMBDA_TEST_HUB_URL), capabilities);
            WebDriverRunner.setWebDriver(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error setting up LambdaTest driver", e);
        }

        // Configure Selenide
        Configuration.timeout = 10000;
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
    }

    public static void quitDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.getWebDriver().quit();
        }
    }
}