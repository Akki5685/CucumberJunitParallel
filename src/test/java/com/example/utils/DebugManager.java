package com.example.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class DebugManager {
    private static final String DEBUG_FLAG_FILE = "debug_mode.flag";
    private static final String PAUSE_FLAG_FILE = "pause_execution.flag";
    private static final AtomicBoolean debugModeEnabled = new AtomicBoolean(false);

    static {
        // Check if debug mode is enabled at startup
        debugModeEnabled.set(Files.exists(Paths.get(DEBUG_FLAG_FILE)));
    }

    /**
     * Enables debug mode
     */
    public static void enableDebugMode() {
        try {
            new File(DEBUG_FLAG_FILE).createNewFile();
            debugModeEnabled.set(true);
        } catch (IOException e) {
            System.err.println("Failed to enable debug mode: " + e.getMessage());
        }
    }

    /**
     * Disables debug mode
     */
    public static void disableDebugMode() {
        try {
            Files.deleteIfExists(Paths.get(DEBUG_FLAG_FILE));
            debugModeEnabled.set(false);
        } catch (IOException e) {
            System.err.println("Failed to disable debug mode: " + e.getMessage());
        }
    }

    /**
     * Checks if debug mode is enabled
     * @return true if debug mode is enabled, false otherwise
     */
    public static boolean isDebugModeEnabled() {
        // Refresh from file system in case it was changed externally
        debugModeEnabled.set(Files.exists(Paths.get(DEBUG_FLAG_FILE)));
        return debugModeEnabled.get();
    }

    /**
     * Pauses execution when a failure occurs
     * @param scenarioName The name of the scenario
     * @param stepName The name of the step
     * @param errorMessage The error message
     */
    public static void pauseOnFailure(String scenarioName, String stepName, String errorMessage) {
        if (!isDebugModeEnabled()) {
            return;
        }

        try {
            // Create pause flag file with error details
            try (FileWriter writer = new FileWriter(PAUSE_FLAG_FILE)) {
                writer.write("Scenario: " + scenarioName + "\n");
                writer.write("Step: " + stepName + "\n");
                writer.write("Error: " + errorMessage + "\n");
                writer.write("Timestamp: " + System.currentTimeMillis() + "\n");
            }

            System.out.println("\n==== EXECUTION PAUSED FOR DEBUGGING ====");
            System.out.println("Scenario: " + scenarioName);
            System.out.println("Step: " + stepName);
            System.out.println("Error: " + errorMessage);
            System.out.println("Browser is kept open for debugging.");
            System.out.println("Fix the issue, then delete the file '" + PAUSE_FLAG_FILE + "' to continue execution.");
            System.out.println("==========================================\n");

            // Wait until the pause flag file is deleted
            while (Files.exists(Paths.get(PAUSE_FLAG_FILE))) {
                Thread.sleep(1000);
            }

            System.out.println("\n==== RESUMING EXECUTION ====\n");

        } catch (Exception e) {
            System.err.println("Error in pause mechanism: " + e.getMessage());
        }
    }
}