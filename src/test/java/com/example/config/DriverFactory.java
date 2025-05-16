package com.example.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class DriverFactory {

    private static final String LAMBDA_TEST_USERNAME = System.getProperty("LT_USERNAME", "YOUR_LAMBDATEST_USERNAME");
    private static final String LAMBDA_TEST_ACCESS_KEY = System.getProperty("LT_ACCESS_KEY", "YOUR_LAMBDATEST_ACCESS_KEY");
    private static final String LAMBDA_TEST_HUB_URL = "https://" + LAMBDA_TEST_USERNAME + ":" + LAMBDA_TEST_ACCESS_KEY + "@hub.lambdatest.com/wd/hub";
    private static final String BUILD_NAME = System.getProperty("build.name", "Cucumber-JUnit5-Selenide-Parallel");

    // Thread-safe driver storage
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();

    // Semaphore for controlling concurrent sessions
    private static Semaphore semaphore;

    static {
        // Initialize semaphore with parallel count from system property
        int parallelCount = Integer.parseInt(System.getProperty(
                "cucumber.execution.parallel.config.fixed.parallelism", "4"));
        semaphore = new Semaphore(parallelCount);

        System.out.println("Initialized thread pool with " + parallelCount + " concurrent sessions");
    }

    /**
     * Set up WebDriver with browser configuration
     */
    public static void setupDriver(String browser, String version, String platform) {
        setupDriver(browser, version, platform, Thread.currentThread().getName());
    }

    /**
     * Set up WebDriver with browser configuration and specific test name
     */
    public static void setupDriver(String browser, String version, String platform, String testName) {
        try {
            // Acquire permit (will block if max threads reached)
            semaphore.acquire();
            System.out.println("Thread acquired: " + Thread.currentThread().getId() +
                    " (Available permits: " + semaphore.availablePermits() + ")");

            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("browserName", browser);
            capabilities.setCapability("version", version);
            capabilities.setCapability("platform", platform);
            capabilities.setCapability("build", BUILD_NAME);
            capabilities.setCapability("name", testName);
            capabilities.setCapability("visual", true);
            capabilities.setCapability("video", true);
            capabilities.setCapability("network", true);
            capabilities.setCapability("console", true);

            // Create RemoteWebDriver
            WebDriver driver = new RemoteWebDriver(new URL(LAMBDA_TEST_HUB_URL), capabilities);

            // Store in ThreadLocal and map for tracking
            driverThreadLocal.set(driver);
            driverMap.put(Thread.currentThread().getId(), driver);

            // Set driver for Selenide
            WebDriverRunner.setWebDriver(driver);

            // Configure Selenide
            Configuration.timeout = 10000;
            Configuration.screenshots = true;
            Configuration.savePageSource = false;

        } catch (Exception e) {
            // Release permit if setup fails
            semaphore.release();
            throw new RuntimeException("Error setting up LambdaTest driver", e);
        }
    }

    /**
     * Get the WebDriver for the current thread
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quit the WebDriver for the current thread
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                // Close Selenide's WebDriver if it's the same as our ThreadLocal driver
                if (WebDriverRunner.hasWebDriverStarted() &&
                        WebDriverRunner.getWebDriver() == driver) {
                    WebDriverRunner.closeWindow();
                } else {
                    driver.quit();
                }
            } finally {
                // Clean up ThreadLocal and map
                driverThreadLocal.remove();
                driverMap.remove(Thread.currentThread().getId());

                // Release the permit
                semaphore.release();
                System.out.println("Thread released: " + Thread.currentThread().getId() +
                        " (Available permits: " + semaphore.availablePermits() + ")");
            }
        }
    }

    /**
     * Quit all WebDrivers across all threads
     */
    public static void quitAllDrivers() {
        driverMap.forEach((threadId, driver) -> {
            if (driver != null) {
                driver.quit();
            }
        });
        driverMap.clear();
        driverThreadLocal.remove();

        // Also close Selenide's WebDriver if it's still open
        if (WebDriverRunner.hasWebDriverStarted()) {
            WebDriverRunner.closeWindow();
        }
    }

    /**
     * Set test status on LambdaTest (pass/fail)
     */
    /**
     * Set test status on LambdaTest (pass/fail)
     */
    public static void setTestStatus(boolean passed) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            try {
                String status = passed ? "passed" : "failed";
                String script = "lambda-status=" + status;

                // Cast WebDriver to JavascriptExecutor before calling executeScript
                ((org.openqa.selenium.JavascriptExecutor) WebDriverRunner.getWebDriver())
                        .executeScript(script);

            } catch (Exception e) {
                System.err.println("Failed to set test status on LambdaTest: " + e.getMessage());
            }
        }
    }
}