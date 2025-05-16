package com.example.config;


import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestConfig implements BeforeAllCallback {

    static {
        // Configure Selenide to take screenshots
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        Configuration.reportsFolder = "build/reports/tests/screenshots";
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        // This ensures the static block is executed
    }
}