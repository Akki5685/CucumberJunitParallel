# Debug Mode Instructions

This framework includes a debug mode that keeps the browser open when a test fails, allowing you to:
1. Inspect the browser state
2. Fix the code issue
3. Continue execution from the point of failure

## How to Enable Debug Mode

### Option 1: Command Line
Run tests with the debug mode system property:
mvn clean test -Ddebug.mode=true -Dcucumber.filter.tags="@chrome"


### Option 2: Create Debug Flag File
Create an empty file named `debug_mode.flag` in the project root directory.

## How Debug Mode Works

1. When a test step fails and debug mode is enabled:
    - The browser stays open
    - Execution pauses
    - A file named `pause_execution.flag` is created with error details
    - Console displays instructions

2. After fixing the issue:
    - Delete the `pause_execution.flag` file
    - Execution will automatically resume from the point of failure

3. To disable debug mode:
    - Delete the `debug_mode.flag` file
    - Or run with `-Ddebug.mode=false`

## Notes
- In parallel execution, each thread will pause independently
- The browser will remain open until you manually close it or the test completes
- Extent Reports will still be generated correctly
  How This Implementation Works:
  Debug Mode Toggle: Enable/disable debug mode via system property or flag file.
  Pause Mechanism: When a step fails in debug mode, execution pauses and waits for you to fix the issue.
  Resume Control: Delete the pause flag file to resume execution from the point of failure.
  Browser Preservation: The browser stays open during debugging so you can inspect the state.
  Reporting: Extent Reports continue to work normally, capturing the full test execution.
  To use this feature:

Run your tests with debug mode enabled:
mvn clean test -Ddebug.mode=true -Dcucumber.filter.tags="@chrome"
When a test fails, the browser will stay open and execution will pause.
Fix the issue in your code.
Delete the pause_execution.flag file to resume execution.