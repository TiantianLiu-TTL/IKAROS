package algorithm_ikrq;
/**
 * given a word, transform it to identity word with word relevance
 * @author Tiantian
 */

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import utilities.FilePaths;

public class WordRelationship {
    private int wordId;
    private double threshold;
    private ArrayList<double[]> candWords = new ArrayList<>();


    private static String inputFile_r = FilePaths.FilePathPre + "/wordRelationship_regular.txt";
    private static String inputFile_i = FilePaths.FilePathPre + "/wordRelationship_identity.txt";


    public WordRelationship(int wordId, double threshold) {
        this.wordId = wordId;
        this.threshold = threshold;
    }

    public ArrayList<double[]> transform() throws IOException {
        ArrayList wordRelI = readWordRelationshipI();
        ArrayList wordRelR = readWordRelationshipR();

        //if the word is identity word
        if ( -(wordRelI.size()) <= wordId && wordId < 0 ) {
            double [] candWordRe = {wordId, 1};
            candWords.add(candWordRe);
//            System.out.println("transform ok");
        }
        //if the word is regular word
        else if (wordId > 0 && wordId <= wordRelR.size()) {
            ArrayList<Integer> rWords = new ArrayList<>();
            ArrayList<Integer> iWords = new ArrayList<>();
            for (int n = 0; n < wordRelR.size(); n++){
                String [] tempArr = (String[])wordRelR.get(n);
                if (wordId == Integer.parseInt(tempArr[0])) {
                    for (int i = 1; i < tempArr.length; i++) {
                        double [] candWordRe = {Integer.parseInt(tempArr[i]), 1};
                        candWords.add(candWordRe);
                        iWords.add(Integer.parseInt(tempArr[i]));
                        for (int j = 0; j < wordRelI.size(); j++) {
                            String [] temp = (String [])wordRelI.get(j);
                            if (Integer.parseInt(tempArr[i]) == Integer.parseInt(temp[0])) {
                                for (int m = 1; m < temp.length; m++) {
                                    rWords.add(Integer.parseInt(temp[m]));
                                }
                            }
                        }
                    }
                    break;
                }

            }
            for (int i = 0; i < wordRelI.size(); i++) {
                String [] temp = (String [])wordRelI.get(i);
                int curIword = Integer.parseInt(temp[0]);
                if (iWords.contains((curIword))) continue;
                double rel = calRelevance(wordRelI, rWords, curIword);
                if (rel > threshold) {
                    double [] candWordRe = {curIword, rel};
                    candWords.add(candWordRe);
                }

            }
        }
        //if the word is not in our index
        else {
            double[] noCandWord = {0, 0};
            candWords.add(noCandWord);
        }
        return candWords;
    }

    private double calRelevance(ArrayList wordRelI, ArrayList rWords, int curIword) {
        double rel = 0;
        ArrayList<Integer> union = new ArrayList<>();
        ArrayList<Integer> inter = new ArrayList<>();
        for (int i = 0; i < rWords.size(); i++) {
            if (!union.contains(rWords.get(i))) {
                union.add((int) rWords.get(i));
            }
        }
        int index = curIword + wordRelI.size();
        String [] temp = (String [])wordRelI.get(index);
        for (int i = 1; i < temp.length; i++){
            if (!union.contains(Integer.parseInt(temp[i]))) {
                union.add(Integer.parseInt(temp[i]));
            }
            if (rWords.contains(Integer.parseInt(temp[i]))) {
                if (!inter.contains(Integer.parseInt(temp[i]))) {
                    inter.add(Integer.parseInt(temp[i]));
                }
            }
        }
//        for (int i = 0; i < union.size(); i++) {
//            System.out.println(union.get(i));
//        }
//        System.out.println("one finished");

        rel = (double)(inter.size())/(double)(union.size());
        return rel;
    }

    private ArrayList readWordRelationshipI() throws IOException {
        ArrayList<String[]> wordRelI = new ArrayList<>();
        Path path = Paths.get(inputFile_i);
        Scanner scanner = new Scanner(path);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tempArr = line.split("\t");
            wordRelI.add(tempArr);
        }
        return wordRelI;
    }

    private ArrayList readWordRelationshipR() throws IOException {
        ArrayList<String[]> wordRelR = new ArrayList<>();
        Path path = Paths.get(inputFile_r);
        Scanner scanner = new Scanner(path);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tempArr = line.split("\t");
            wordRelR.add(tempArr);
        }
        return wordRelR;
    }

    public ArrayList getCandWords() {return candWords;}

    public static void main (String arg[]) throws IOException {

        WordRelationship wr = new WordRelationship(927, 0.04);
        ArrayList cw = wr.transform();
        for (int i = 0; i < cw.size(); i++)
        {
            double [] cwr = (double [])cw.get(i);
            System.out.println(cwr[0] + "  " + cwr[1]);
        }
    }

}
