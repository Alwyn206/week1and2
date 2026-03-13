import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCache {

    // L1: Memory cache with access-order LRU
    private LinkedHashMap<String, VideoData> L1;
    private final int L1_CAPACITY = 10000;

    // L2: SSD-backed simulated cache
    private HashMap<String, VideoData> L2;
    private HashMap<String, Integer> L2AccessCount;
    private final int L2_PROMOTE_THRESHOLD = 5;

    // L3: Database simulation
    private HashMap<String, VideoData> L3;

    // Statistics
    private int L1Hits = 0, L2Hits = 0, L3Hits = 0, requests = 0;

    public MultiLevelCache() {

        // L1 LinkedHashMap with access order = true
        L1 = new LinkedHashMap<>(L1_CAPACITY, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        L2 = new HashMap<>();
        L2AccessCount = new HashMap<>();
        L3 = new HashMap<>();
    }

    // Simulate adding video to DB
    public void addVideoToDB(String videoId, String content) {
        L3.put(videoId, new VideoData(videoId, content));
    }

    public VideoData getVideo(String videoId) {
        requests++;

        // L1 cache
        if (L1.containsKey(videoId)) {
            L1Hits++;
            return L1.get(videoId);
        }

        // L2 cache
        if (L2.containsKey(videoId)) {
            L2Hits++;
            L2AccessCount.put(videoId, L2AccessCount.getOrDefault(videoId, 0) + 1);

            // Promote to L1 if threshold exceeded
            if (L2AccessCount.get(videoId) >= L2_PROMOTE_THRESHOLD) {
                L1.put(videoId, L2.get(videoId));
                L2AccessCount.put(videoId, 0); // reset
            }

            return L2.get(videoId);
        }

        // L3 database
        if (L3.containsKey(videoId)) {
            L3Hits++;

            VideoData data = L3.get(videoId);

            // Add to L2 cache with access count 1
            L2.put(videoId, data);
            L2AccessCount.put(videoId, 1);

            return data;
        }

        return null; // video not found
    }

    public void getStatistics() {

        double L1HitRate = L1Hits * 100.0 / requests;
        double L2HitRate = L2Hits * 100.0 / requests;
        double L3HitRate = L3Hits * 100.0 / requests;

        double overallHitRate = (L1Hits + L2Hits + L3Hits) * 100.0 / requests;

        System.out.println("L1 Hit Rate: " + String.format("%.2f", L1HitRate) + "%");
        System.out.println("L2 Hit Rate: " + String.format("%.2f", L2HitRate) + "%");
        System.out.println("L3 Hit Rate: " + String.format("%.2f", L3HitRate) + "%");
        System.out.println("Overall Hit Rate: " + String.format("%.2f", overallHitRate) + "%");
    }

    // Test simulation
    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // Add some videos to DB
        cache.addVideoToDB("video_123", "Content 123");
        cache.addVideoToDB("video_999", "Content 999");

        // First request: L1 miss, L2 miss, L3 hit
        System.out.println(cache.getVideo("video_123").content);

        // Second request: L1 hit
        System.out.println(cache.getVideo("video_123").content);

        // L3 hit for new video
        System.out.println(cache.getVideo("video_999").content);

        cache.getStatistics();
    }
}