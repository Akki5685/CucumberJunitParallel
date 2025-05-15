package com.example.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter;
import com.aventstack.extentreports.gherkin.model.Scenario;
import com.codeborne.selenide.Selenide;
import org.openqa.selenium.OutputType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportLogger {
    // Thread-local storage to keep track of current test steps
    private static final ThreadLocal<Map<String, ExtentTest>> currentStepMap = ThreadLocal.withInitial(HashMap::new);

    /**
     * Logs an INFO step with message
     * @param message The message to log
     */
    public static void logInfo(String message) {
        try {
            ExtentCucumberAdapter.getCurrentStep().log(Status.INFO, message);
        } catch (Exception e) {
            System.out.println("Failed to log info: " + e.getMessage());
        }
    }

    /**
     * Logs a PASS step with message
     * @param message The message to log
     */
    public static void logPass(String message) {
        try {
            ExtentCucumberAdapter.getCurrentStep().log(Status.PASS, message);
        } catch (Exception e) {
            System.out.println("Failed to log pass: " + e.getMessage());
        }
    }

    /**
     * Logs a FAIL step with message
     * @param message The message to log
     */
    public static void logFail(String message) {
        try {
            ExtentCucumberAdapter.getCurrentStep().log(Status.FAIL, message);
        } catch (Exception e) {
            System.out.println("Failed to log fail: " + e.getMessage());
        }
    }

    /**
     * Logs a WARNING step with message
     * @param message The message to log
     */
    public static void logWarning(String message) {
        try {
            ExtentCucumberAdapter.getCurrentStep().log(Status.WARNING, message);
        } catch (Exception e) {
            System.out.println("Failed to log warning: " + e.getMessage());
        }
    }

    /**
     * Logs a step with screenshot
     * @param status The status (PASS, FAIL, INFO, etc.)
     * @param message The message to log
     * @param takeScreenshot Whether to take a screenshot
     */
    public static void logWithScreenshot(Status status, String message, boolean takeScreenshot) {
        try {
            if (takeScreenshot) {
                String base64Screenshot = Selenide.screenshot(OutputType.BASE64);
                ExtentCucumberAdapter.getCurrentStep().log(
                        status,
                        message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
                );
            } else {
                ExtentCucumberAdapter.getCurrentStep().log(status, message);
            }
        } catch (Exception e) {
            System.out.println("Failed to log with screenshot: " + e.getMessage());
        }
    }

    /**
     * Adds a screenshot to the current step
     * @param message The message to accompany the screenshot
     */
    public static void addScreenshot(String message) {
        try {
            String base64Screenshot = Selenide.screenshot(OutputType.BASE64);
            ExtentCucumberAdapter.getCurrentStep().info(
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot)
                                    .build())
                    .info(message);
        } catch (Exception e) {
            System.out.println("Failed to add screenshot: " + e.getMessage());
        }
    }

    /**
     * Adds a screenshot with a specific status
     * @param status The status to use (PASS, FAIL, INFO, etc.)
     * @param message The message to accompany the screenshot
     */
    public static void addScreenshot(Status status, String message) {
        try {
            String base64Screenshot = Selenide.screenshot(OutputType.BASE64);
            ExtentCucumberAdapter.getCurrentStep().log(
                    status,
                    message,
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
            );
        } catch (Exception e) {
            System.out.println("Failed to add screenshot with status: " + e.getMessage());
        }
    }

    /**
     * Saves the current step for later reference
     * @param stepName A unique name to identify this step
     */
    public static void saveCurrentStep(String stepName) {
        try {
            ExtentTest currentStep = ExtentCucumberAdapter.getCurrentStep();
            currentStepMap.get().put(stepName, currentStep);
        } catch (Exception e) {
            System.out.println("Failed to save current step: " + e.getMessage());
        }
    }

    /**
     * Adds a screenshot to a previously saved step
     * @param stepName The name of the previously saved step
     * @param status The status to use
     * @param message The message to accompany the screenshot
     */
    public static void addScreenshotToSavedStep(String stepName, Status status, String message) {
        try {
            ExtentTest savedStep = currentStepMap.get().get(stepName);
            if (savedStep != null) {
                String base64Screenshot = Selenide.screenshot(OutputType.BASE64);
                savedStep.log(
                        status,
                        message,
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build()
                );
            } else {
                System.out.println("No saved step found with name: " + stepName);
            }
        } catch (Exception e) {
            System.out.println("Failed to add screenshot to saved step: " + e.getMessage());
        }
    }

    /**
     * Creates a custom node in the report
     * @param nodeName The name of the node
     * @param description The description of the node
     * @return The created ExtentTest instance
     */
    public static ExtentTest createNode(String nodeName, String description) {
        try {
            // This uses reflection to access the current test
            Method getExtentTestMethod = ExtentCucumberAdapter.class.getDeclaredMethod("getExtentTest");
            getExtentTestMethod.setAccessible(true);
            ExtentTest currentTest = (ExtentTest) getExtentTestMethod.invoke(null);

            return currentTest.createNode(nodeName, description);
        } catch (Exception e) {
            System.out.println("Failed to create node: " + e.getMessage());
            return null;
        }
    }
}