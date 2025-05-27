package com.example.config;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class DriverFactory {

    private static final String LAMBDA_TEST_USERNAME = System.getProperty("LT_USERNAME", "akhil20495");
    private static final String LAMBDA_TEST_ACCESS_KEY = System.getProperty("LT_ACCESS_KEY", "LT_pW53j2Au3I7O0QkVwEKeGLGKRlsyJpZ7QbD6RCYZzl2DTJC");
    private static final String LAMBDA_TEST_HUB_URL = "https://" + LAMBDA_TEST_USERNAME + ":" + LAMBDA_TEST_ACCESS_KEY + "@hub.lambdatest.com/wd/hub";
    private static final String BUILD_NAME = System.getProperty("build.name", "Cucumber-JUnit5-Selenide-Parallel");

    // Thread-safe driver storage
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();

    // Semaphore for controlling concurrent sessions
    private static Semaphore semaphore;

    // ExtentReports related
    private static ExtentReports extentReports;
    private static ExtentSparkReporter sparkReporter;

    // ThreadLocal for ExtentTest to support parallel tests
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    static {
        int parallelCount = Integer.parseInt(System.getProperty(
                "cucumber.execution.parallel.config.fixed.parallelism", "4"));
        semaphore = new Semaphore(parallelCount);

        System.out.println("Initialized thread pool with " + parallelCount + " concurrent sessions");

        // Initialize ExtentReports once
        sparkReporter = new ExtentSparkReporter("target/extent-report.html");
        sparkReporter.config().setReportName("LambdaTest Parallel Execution Report");
        sparkReporter.config().setDocumentTitle("Test Execution Report");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        // You can add system info if needed
        extentReports.setSystemInfo("Build", BUILD_NAME);
        extentReports.setSystemInfo("Environment", "LambdaTest");
    }

    /**
     * Set up WebDriver with browser configuration
     */
    public static void setupDriver(String browser, String version, String platform, String testName) {
        try {
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

            WebDriver driver = new RemoteWebDriver(new URL(LAMBDA_TEST_HUB_URL), capabilities);

            driverThreadLocal.set(driver);
            driverMap.put(Thread.currentThread().getId(), driver);

            WebDriverRunner.setWebDriver(driver);

            Configuration.timeout = 10000;
            Configuration.screenshots = false;
            Configuration.savePageSource = false;

            // Create ExtentTest instance for this thread/test
            ExtentTest test = extentReports.createTest(testName);
            extentTestThreadLocal.set(test);

        } catch (Exception e) {
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
     * Get ExtentTest for current thread
     */
    public static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }

    /**
     * Quit the WebDriver for the current thread and log test status to ExtentReports
     */
    public static void quitDriver(boolean testPassed) {
        WebDriver driver = driverThreadLocal.get();
        ExtentTest test = extentTestThreadLocal.get();

        if (driver != null) {
            try {
                if (testPassed) {
                    test.pass("Test passed");
                } else {
                    test.fail("Test failed");
                    // Optionally add screenshot on failure
                    // String screenshotPath = captureScreenshot();
                    // test.addScreenCaptureFromPath(screenshotPath);
                }

                if (WebDriverRunner.hasWebDriverStarted() &&
                        WebDriverRunner.getWebDriver() == driver) {
                    WebDriverRunner.closeWindow();
                } else {
                    driver.quit();
                }
            } catch (Exception e) {
                test.warning("Exception during driver quit: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
                driverMap.remove(Thread.currentThread().getId());
                extentTestThreadLocal.remove();

                semaphore.release();
                System.out.println("Thread released: " + Thread.currentThread().getId() +
                        " (Available permits: " + semaphore.availablePermits() + ")");
            }
        }
    }

    public static void setTestStatus(boolean passed) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            try {
                String status = passed ? "passed" : "failed";
                String script = "lambda-status=" + status;

                ((JavascriptExecutor) WebDriverRunner.getWebDriver()).executeScript(script);
            } catch (Exception e) {
                System.err.println("Failed to set test status on LambdaTest: " + e.getMessage());
            }
        }
    }


    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}