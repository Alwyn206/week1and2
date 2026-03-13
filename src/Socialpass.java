import java.util.*;

public class Socialpass {

    // username -> userId
    private HashMap<String, Integer> users = new HashMap<>();

    // username -> attempt count
    private HashMap<String, Integer> attempts = new HashMap<>();

    // Check username availability
    public boolean checkAvailability(String username) {

        // track attempt frequency
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    // Register user
    public void registerUser(String username, int userId) {
        users.put(username, userId);
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;

            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // replace _ with .
        String alt = username.replace("_", ".");
        if (!users.containsKey(alt)) {
            suggestions.add(alt);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String maxUser = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                maxUser = entry.getKey();
            }
        }

        return maxUser + " (" + max + " attempts)";
    }

    // Test program
    public static void main(String[] args) {

        Socialpass system = new Socialpass();

        system.registerUser("john_doe", 101);
        system.registerUser("admin", 1);

        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        System.out.println(system.suggestAlternatives("john_doe"));

        System.out.println(system.getMostAttempted());
    }
}