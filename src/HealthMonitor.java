import java.util.concurrent.*;
import java.io.*;

public class HealthMonitor {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final String EMERGENCY_NUMBER = "+123123123";
    private static String relativeNumber;
    private static int checkCount = 0;

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter relative's phone number for emergencies: ");
            relativeNumber = reader.readLine().trim();

            if (relativeNumber.isEmpty()) {
                System.out.println("Invalid number! Exiting.");
                System.exit(1);
            }

            System.out.println("Health Monitor started. Will run 3 checks every 2 hours.");
            scheduler.scheduleAtFixedRate(HealthMonitor::healthCheck, 0, 2, TimeUnit.HOURS);

            // Keep program running
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void healthCheck() {
        checkCount++;
        if (checkCount > 3) {
            scheduler.shutdown();
            System.out.println("Program completed 3 checks. Exiting.");
            System.exit(0);
        }

        System.out.println("\n[Check " + checkCount + "/3] Are you okay? Respond with 'im okay' or 'im sick' (3 minutes to respond):");

        ExecutorService inputExecutor = Executors.newSingleThreadExecutor();
        try {
            Future<String> future = inputExecutor.submit(() ->
                    new BufferedReader(new InputStreamReader(System.in)).readLine()
            );

            String response = future.get(3, TimeUnit.MINUTES);
            processResponse(response);
        } catch (TimeoutException e) {
            emergencyAlert(EMERGENCY_NUMBER, "No response to health check");
        } catch (Exception e) {
            emergencyAlert(EMERGENCY_NUMBER, "Error: " + e.getMessage());
        } finally {
            inputExecutor.shutdownNow();
        }
    }

    private static void processResponse(String response) {
        String cleanResponse = response.trim().toLowerCase();
        if (cleanResponse.equals("im okay")) {
            System.out.println("Status confirmed. Next check in 2 hours.");
        } else if (cleanResponse.equals("im sick")) {
            handleSickResponse();
        } else {
            emergencyAlert(EMERGENCY_NUMBER, "Invalid response: " + response);
        }
    }

    private static void handleSickResponse() {
        System.out.println("Do you want to notify your relative? (yes/no)");
        ExecutorService choiceExecutor = Executors.newSingleThreadExecutor();

        try {
            Future<String> future = choiceExecutor.submit(() ->
                    new BufferedReader(new InputStreamReader(System.in)).readLine()
            );

            String choice = future.get(3, TimeUnit.MINUTES).trim().toLowerCase();
            if (choice.equals("yes")) {
                emergencyAlert(relativeNumber, "User reported being sick");
            } else {
                System.out.println("Relative notification canceled.");
            }
        } catch (TimeoutException e) {
            emergencyAlert(relativeNumber, "No response to relative notification");
        } catch (Exception e) {
            emergencyAlert(relativeNumber, "Notification error: " + e.getMessage());
        } finally {
            choiceExecutor.shutdownNow();
        }
    }

    private static void emergencyAlert(String number, String reason) {
        System.out.println("ALERT: " + reason + "! Contacting " + number);

        try {
            String script = String.format(
                    "tell application \"Messages\"\n" +
                            "    send \"EMERGENCY: %s\" to buddy \"%s\" of service \"SMS\"\n" + "end tell", reason, number);

            Process p = Runtime.getRuntime().exec(new String[] {"osascript", "-e", script});
            p.waitFor();
            System.out.println("Emergency message sent to " + number);
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
}
