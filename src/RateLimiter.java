import java.util.*;

public class RateLimiter {

    class TokenBucket {

        int tokens;
        int maxTokens;
        int refillRate; // tokens per hour
        long lastRefillTime;

        TokenBucket(int maxTokens, int refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // refill tokens
        void refill() {

            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillTime;

            int tokensToAdd = (int)(elapsed / 3600000.0 * refillRate);

            if (tokensToAdd > 0) {
                tokens = Math.min(maxTokens, tokens + tokensToAdd);
                lastRefillTime = now;
            }
        }

        boolean allowRequest() {

            refill();

            if (tokens > 0) {
                tokens--;
                return true;
            }

            return false;
        }
    }

    private HashMap<String, TokenBucket> clients = new HashMap<>();

    private int maxRequests = 1000;

    // Check rate limit
    public synchronized String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(maxRequests, maxRequests));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        }

        return "Denied (0 requests remaining, retry later)";
    }

    // Get status
    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "No usage yet";
        }

        int used = bucket.maxTokens - bucket.tokens;

        return "{used: " + used +
                ", limit: " + bucket.maxTokens +
                ", remaining: " + bucket.tokens + "}";
    }

    // Test
    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit("abc123"));
        }

        System.out.println(limiter.getRateLimitStatus("abc123"));
    }
}