1. User Input Flow:
    - Starts by asking for relative's phone number
    - Handles invalid/non-responsive input scenarios
    - Added clear check counter (3 total checks)

2. Emergency Handling:
    - Separate emergency numbers for different scenarios:
        - Hardcoded number for timeouts/errors
        - User-provided number for "sick" responses
    - Clear confirmation flow for relative notification

3. Timeout Management:
    - 3-minute timeout for both initial response and relative notification
    - Proper resource cleanup with ExecutorServices

4. Exit Conditions:
    - Program automatically exits after 3 checks (6 hours total)
    - Proper shutdown of scheduler and resources

5. Error Handling:
    - Improved error messages and status reporting
    - Separate error handling for different alert types

To Use:
1. Compile: javac HealthMonitor.java
2. Run: java HealthMonitor
3. Follow prompts and responses

macOS Permissions Required:
1. Allow Terminal/Messages integration:
    - System Preferences → Security & Privacy → Privacy → Automation
    - Enable "Messages" for your terminal emulator
2. Contacts access (if using contact names instead of numbers)

Features:
- Three automatic exit points after 3 checks
- Configurable emergency numbers
- Clear response validation
- Proper thread and resource management
- Detailed status messages