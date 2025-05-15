package com.example.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {

    public static void setupLocalDriver(String browser) {
        // Configure Selenide for local execution
        Configuration.browser = browser;
        Configuration.timeout = 10000;
        Configuration.screenshots = true;
        Configuration.savePageSource = false;

        // Add browser-specific options if needed
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            Configuration.browserCapabilities = options;
        } else if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--start-maximized");
            Configuration.browserCapabilities = options;
        } else if ("MicrosoftEdge".equalsIgnoreCase(browser)) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            Configuration.browserCapabilities = options;
        }
    }

    public static void setupRemoteDriver(String browser, String version, String platform,
                                         String username, String accessKey) {
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

        // Add browser-specific options if needed
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
        }

        try {
            String hubUrl = "https://" + username + ":" + accessKey + "@hub.lambdatest.com/wd/hub";
            RemoteWebDriver driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
            WebDriverRunner.setWebDriver(driver);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error setting up LambdaTest driver", e);
        }

        // Configure Selenide
        Configuration.timeout = 10000;
        Configuration.screenshots = true;
        Configuration.savePageSource = false;
    }

    public static void quitDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.getWebDriver().quit();
        }
    }
}