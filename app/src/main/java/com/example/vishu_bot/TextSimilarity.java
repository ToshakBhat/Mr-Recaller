package com.example.vishu_bot;
import java.util.HashMap;
import java.util.Map;

public class TextSimilarity {

    public static double getCosineSimilarity(String text1, String text2) {
        Map<String, Integer> vector1 = getTermFrequencyVector(text1);
        Map<String, Integer> vector2 = getTermFrequencyVector(text2);

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String term : vector1.keySet()) {
            if (vector2.containsKey(term)) {
                dotProduct += vector1.get(term) * vector2.get(term);
            }
            norm1 += Math.pow(vector1.get(term), 2);
        }

        for (String term : vector2.keySet()) {
            norm2 += Math.pow(vector2.get(term), 2);
        }

        if (norm1 == 0 || norm2 == 0) {
            return 0.0; // Handle division by zero
        }

        double similarityScore = dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return similarityScore * 100; // Multiply by 100 to get percentage
    }

    private static Map<String, Integer> getTermFrequencyVector(String text) {
        Map<String, Integer> vector = new HashMap<>();
        String[] tokens = text.toLowerCase().split("\\s+"); // Split text into tokens

        for (String token : tokens) {
            vector.put(token, vector.getOrDefault(token, 0) + 1);
        }

        return vector;
    }
}

