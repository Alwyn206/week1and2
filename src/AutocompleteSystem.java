import java.util.*;

public class AutocompleteSystem {

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String query;
    }

    private TrieNode root = new TrieNode();
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query
    public void addQuery(String query) {

        frequencyMap.put(query, frequencyMap.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
        node.query = query;
    }

    // DFS to collect queries
    private void dfs(TrieNode node, List<String> results) {

        if (node == null) return;

        if (node.isEnd) {
            results.add(node.query);
        }

        for (TrieNode child : node.children.values()) {
            dfs(child, results);
        }
    }

    // Search prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return new ArrayList<>();

            node = node.children.get(c);
        }

        List<String> queries = new ArrayList<>();
        dfs(node, queries);

        // sort by frequency
        queries.sort((a, b) ->
                frequencyMap.get(b) - frequencyMap.get(a));

        return queries.subList(0, Math.min(10, queries.size()));
    }

    // Test
    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        System.out.println(system.search("jav"));
    }
}