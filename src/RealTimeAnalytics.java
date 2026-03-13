import java.util.*;

public class RealTimeAnalytics {

    // page -> total visits
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // page -> unique users
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Process incoming event
    public void processEvent(String url, String userId, String source) {

        // count page views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // track unique users
        uniqueVisitors.putIfAbsent(url, new HashSet<>());
        uniqueVisitors.get(url).add(userId);

        // track traffic source
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    // Get top pages
    public List<String> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        List<String> top = new ArrayList<>();
        int count = 0;

        while (!pq.isEmpty() && count < 10) {

            Map.Entry<String, Integer> entry = pq.poll();

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            top.add(page + " - " + views + " views (" + unique + " unique)");
            count++;
        }

        return top;
    }

    // Dashboard display
    public void getDashboard() {

        System.out.println("Top Pages:");

        List<String> top = getTopPages();

        int rank = 1;
        for (String page : top) {
            System.out.println(rank + ". " + page);
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (String source : trafficSources.keySet()) {

            int count = trafficSources.get(source);
            double percent = (count * 100.0) / total;

            System.out.println(source + ": " + String.format("%.1f", percent) + "%");
        }
    }

    // Test program
    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent("/article/breaking-news", "user_123", "Google");
        analytics.processEvent("/article/breaking-news", "user_456", "Facebook");
        analytics.processEvent("/sports/championship", "user_777", "Direct");
        analytics.processEvent("/sports/championship", "user_123", "Google");
        analytics.processEvent("/article/breaking-news", "user_789", "Google");

        analytics.getDashboard();
    }
}