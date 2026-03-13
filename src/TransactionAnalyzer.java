import java.util.*;

class Transaction {
    int id;
    int amount;
    String merchant;
    long timestamp; // epoch milliseconds
    String account;

    Transaction(int id, int amount, String merchant, long timestamp, String account) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.timestamp = timestamp;
        this.account = account;
    }
}

public class TransactionAnalyzer {

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public List<int[]> findTwoSum(int target) {

        Map<Integer, Integer> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement), t.id});
            }

            map.put(t.amount, t.id);
        }

        return result;
    }

    // Two-Sum with 1-hour window
    public List<int[]> findTwoSumTimeWindow(int target, long windowMillis) {

        List<int[]> result = new ArrayList<>();

        // sort by timestamp
        transactions.sort(Comparator.comparingLong(t -> t.timestamp));

        for (int i = 0; i < transactions.size(); i++) {

            Transaction t1 = transactions.get(i);

            for (int j = i + 1; j < transactions.size(); j++) {

                Transaction t2 = transactions.get(j);

                if (t2.timestamp - t1.timestamp > windowMillis)
                    break;

                if (t1.amount + t2.amount == target)
                    result.add(new int[]{t1.id, t2.id});
            }
        }

        return result;
    }

    // Duplicate detection
    public Map<String, Set<String>> detectDuplicates() {

        Map<String, Set<String>> map = new HashMap<>();

        for (Transaction t : transactions) {
            String key = t.amount + "|" + t.merchant;
            map.putIfAbsent(key, new HashSet<>());
            map.get(key).add(t.account);
        }

        // filter duplicates
        Map<String, Set<String>> duplicates = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key).size() > 1)
                duplicates.put(key, map.get(key));
        }

        return duplicates;
    }

    // Simple K-Sum (recursive)
    public List<List<Integer>> findKSum(int target, int k) {

        List<List<Integer>> result = new ArrayList<>();
        Collections.sort(transactions, Comparator.comparingInt(t -> t.amount));
        kSumHelper(0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(int start, int k, int target, List<Integer> path, List<List<Integer>> res) {

        if (k == 2) {
            int left = start, right = transactions.size() - 1;

            while (left < right) {
                int sum = transactions.get(left).amount + transactions.get(right).amount;
                if (sum == target) {
                    List<Integer> temp = new ArrayList<>(path);
                    temp.add(transactions.get(left).id);
                    temp.add(transactions.get(right).id);
                    res.add(temp);
                    left++;
                    right--;
                } else if (sum < target) left++;
                else right--;
            }

            return;
        }

        for (int i = start; i < transactions.size() - k + 1; i++) {

            if (i > start && transactions.get(i).amount == transactions.get(i - 1).amount) continue;

            path.add(transactions.get(i).id);
            kSumHelper(i + 1, k - 1, target - transactions.get(i).amount, path, res);
            path.remove(path.size() - 1);
        }
    }

    // Test
    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", 1675900000000L, "acc1"));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", 1675900900000L, "acc2"));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", 1675901800000L, "acc3"));

        System.out.println("Two-Sum (500): " + analyzer.findTwoSum(500));
        System.out.println("Two-Sum 1h window (500): " + analyzer.findTwoSumTimeWindow(500, 3600000));
        System.out.println("Duplicates: " + analyzer.detectDuplicates());
        System.out.println("K-Sum 3 (1000): " + analyzer.findKSum(3, 1000));
    }
}