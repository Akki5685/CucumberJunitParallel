package com.example.utils;

import io.cucumber.java.Scenario;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StepExecutionManager {
    // Thread-safe map to track step execution status across scenarios
    private static final Map<String, Map<String, Boolean>> scenarioStepStatus = new ConcurrentHashMap<>();

    // Thread-safe map to track if a scenario has any failed steps
    private static final Map<String, Boolean> scenarioHasFailure = new ConcurrentHashMap<>();

    /**
     * Registers a step as started for the current scenario
     * @param scenario The current scenario
     * @param stepName The name of the step
     */
    public static void startStep(Scenario scenario, String stepName) {
        String scenarioId = getScenarioId(scenario);
        scenarioStepStatus.computeIfAbsent(scenarioId, k -> new HashMap<>())
                .put(stepName, false);
    }

    /**
     * Marks a step as completed for the current scenario
     * @param scenario The current scenario
     * @param stepName The name of the step
     * @param success Whether the step completed successfully
     */
    public static void completeStep(Scenario scenario, String stepName, boolean success) {
        String scenarioId = getScenarioId(scenario);

        // Mark the step as completed
        scenarioStepStatus.computeIfAbsent(scenarioId, k -> new HashMap<>())
                .put(stepName, true);

        // If step failed, mark the scenario as having a failure
        if (!success) {
            scenarioHasFailure.put(scenarioId, true);
        }
    }

    /**
     * Checks if a step should be skipped
     * @param scenario The current scenario
     * @param stepName The name of the step
     * @return true if the step should be skipped, false otherwise
     */
    public static boolean shouldSkipStep(Scenario scenario, String stepName) {
        String scenarioId = getScenarioId(scenario);

        // If the scenario has no failures, don't skip any steps
        if (!scenarioHasFailure.getOrDefault(scenarioId, false)) {
            return false;
        }

        // Get the status map for this scenario
        Map<String, Boolean> stepMap = scenarioStepStatus.getOrDefault(scenarioId, new HashMap<>());

        // If this step has already been executed, skip it
        return stepMap.getOrDefault(stepName, false);
    }

    /**
     * Resets the status for a scenario
     * @param scenario The scenario to reset
     */
    public static void resetScenario(Scenario scenario) {
        String scenarioId = getScenarioId(scenario);
        scenarioStepStatus.remove(scenarioId);
        scenarioHasFailure.remove(scenarioId);
    }

    /**
     * Gets a unique ID for a scenario
     * @param scenario The scenario
     * @return A unique ID string
     */
    private static String getScenarioId(Scenario scenario) {
        return scenario.getId();
    }
}