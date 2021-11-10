package algorithm_ikrq;
/**
 * given a query keyword list, return its candidate keywords list
 * @author Tiantian
 */


import java.io.IOException;
import java.util.ArrayList;

public class CandidateWords {
    private ArrayList<ArrayList<double[]>> allCandWords = new ArrayList<>();
    private ArrayList<Integer> queryWords;
    private double threshold;

    CandidateWords(ArrayList<Integer> queryWords, double threshold) {
        this.queryWords = queryWords;
        this.threshold = threshold;
    }

    public ArrayList<ArrayList<double[]>> findAllCandWords() throws IOException {
        for (int i = 0; i < queryWords.size(); i++) {
            WordRelationship wr = new WordRelationship(queryWords.get(i), threshold);
            allCandWords.add(wr.transform());
        }

        return allCandWords;
    }
}
