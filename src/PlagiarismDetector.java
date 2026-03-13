import java.util.*;

public class PlagiarismDetector {

    // n-gram -> set of document IDs
    private HashMap<String, Set<String>> index = new HashMap<>();

    private int n = 5; // 5-gram

    // Break text into n-grams
    private List<String> generateNGrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - n; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < n; j++) {
                gram.append(words[i + j]).append(" ");
            }

            ngrams.add(gram.toString().trim());
        }

        return ngrams;
    }

    // Add document to database
    public void addDocument(String docId, String content) {

        List<String> grams = generateNGrams(content);

        for (String gram : grams) {

            index.putIfAbsent(gram, new HashSet<>());
            index.get(gram).add(docId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String docId, String content) {

        List<String> grams = generateNGrams(content);

        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {

            if (index.containsKey(gram)) {

                for (String existingDoc : index.get(gram)) {

                    matchCount.put(existingDoc,
                            matchCount.getOrDefault(existingDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (String doc : matchCount.keySet()) {

            int matches = matchCount.get(doc);
            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Matches with " + doc + ": " + matches);
            System.out.println("Similarity: " + similarity + "%");

            if (similarity > 60) {
                System.out.println("⚠ PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }

    // Test program
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "Artificial intelligence is transforming the world of technology and innovation";
        String essay2 = "Artificial intelligence is transforming the world of modern technology and science";
        String essay3 = "Sports and physical activity are important for maintaining good health";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);

        detector.analyzeDocument("essay_123.txt", essay1);
    }
}