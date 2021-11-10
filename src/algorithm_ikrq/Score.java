package algorithm_ikrq;

import java.util.ArrayList;

import utilities.Constant;

/**
 *calculate route relevance score and ranking score
 * @author Tiantian
 */

public class Score {
    double relScore = 1;
    double rankScore = 0;
    ArrayList<ArrayList<double []>> allCandWords = new ArrayList<ArrayList<double []>>();
    ArrayList<Integer> Wr = new ArrayList<>();;
    ArrayList<Integer> Wc = new ArrayList<>();
    double dist;
    double distCon;
    
    ArrayList<double[]> previous = new ArrayList<double[]>();
    
    ArrayList<Integer> new_Wr = new ArrayList<Integer>();
    
    // for calculate route relevance score

    public Score(ArrayList<ArrayList<double []>> allCandWords, ArrayList<Integer> Wr){
//    		System.out.println("Score1");
        this.allCandWords = allCandWords;
        this.Wr = Wr;
    }
    
    public Score(ArrayList<ArrayList<double []>> allCandWords, ArrayList<Integer> Wr, double dist, double distCon){
//    		System.out.println("Score2");
        this.allCandWords = allCandWords;
        this.Wr = Wr;
        this.dist = dist;
        this.distCon = distCon;
    }
    
    public Score(Score old_score, ArrayList<Integer> new_routeWords, double dist) {
//    		System.out.println("Score3");
    		ArrayList<double[]> old_previous = new ArrayList<double[]>();
    		old_previous = old_score.previous;
    		this.previous = (ArrayList<double[]>) old_previous.clone();
    		
    		this.allCandWords = old_score.allCandWords;
    		
    		ArrayList<Integer> temp = new ArrayList<Integer>();
    		ArrayList<Integer> old = new ArrayList<Integer>();
    		old = old_score.Wr;
    		temp = (ArrayList<Integer>) old.clone();
    		
    		temp.addAll(new_routeWords);
    		this.new_Wr.addAll(new_routeWords);
    		this.Wr = temp;
    		
    		this.dist = dist;
    		this.distCon = old_score.distCon;
    }
    
    public Score(Score old_score, int new_routeWord, double dist) {
    	// copy previous
		ArrayList<double[]> old_previous = new ArrayList<double[]>();
		old_previous = old_score.previous;
		
		for (int i = 0; i < old_previous.size(); i ++) {
			double[] temp = new double[2];
			temp = old_previous.get(i);
			this.previous.add(temp);
		}
    	
		// copy allcandwords
		this.allCandWords = new ArrayList<ArrayList<double[]>>();
		ArrayList<ArrayList<double []>> old_allCandWords = new ArrayList<ArrayList<double []>>();
		old_allCandWords = old_score.allCandWords;
		
		for (int i = 0; i < old_allCandWords.size(); i ++) {
			ArrayList<double[]> arr = new ArrayList<double[]>();
			
			for (int j = 0; j < old_allCandWords.get(i).size(); j ++) {
				double[] temp = new double[2];
				temp = old_allCandWords.get(i).get(j);
				arr.add(temp);
			}
			
			this.allCandWords.add(arr);
		}
		
		// copy Wr
		ArrayList<Integer> temp_Wr = new ArrayList<Integer>();
		
		for (int i = 0; i < old_score.Wr.size(); i ++) {
			temp_Wr.add(old_score.Wr.get(i));
		}
		
		// add new wr
		if (new_routeWord != -99999) {
			temp_Wr.add(new_routeWord);
			this.new_Wr.add(new_routeWord);
		}
		
		this.Wr = temp_Wr;
		this.dist = dist;
		this.distCon = old_score.distCon;
    }
    
    public void initPrevious() {
		for(int i = 0; i < allCandWords.size(); i++) {
			double[] temp = new double[2];
			temp[0] = 0;
			temp[1] = 0 - (Constant.large);
			previous.add(temp);
		}
    }
    
    // calculate route relevance score;
    public double calRelScore() {
		for (int i = 0; i < new_Wr.size(); i ++) {
			int word = new_Wr.get(i);
			
			// find which candidate keyword the word belong to
			for (int j = 0; j < allCandWords.size(); j++) {
				boolean found = false;
	            double maxRe = 0;
	            int wc;
	            
	            int temp = -99999;
	            for (int k = 0; k < allCandWords.get(j).size(); k ++) {
            		double[] candWordRe =  allCandWords.get(j).get(k);
            		
            		if ((int)candWordRe[0] == word) {
            			found = true;
            			maxRe = candWordRe[1];
            			temp = k;
            			break;
            		}
	            }
	            
	            if (found) {
    	            if (previous.get(j)[1] < maxRe) {
	            		previous.get(j)[0] = word;
	            		previous.get(j)[1] = maxRe;
    	            }
	            }
	        }
		}
    	
    	int coverNum = 0;
        double sumMaxRe = 0;
        
        for (int i = 0; i < allCandWords.size(); i++) {
            boolean found = false;
            double maxRe = 0;
            int wc = 0;
            
            for (int j = 0; j < allCandWords.get(i).size(); j++) {
                double[] candWordRe =  allCandWords.get(i).get(j);
                
                for (int m = 0; m < Wr.size(); m++) {
                    if (candWordRe[0] == Wr.get(m)) {
                    	found = true;
                        
                        if (candWordRe[1] > maxRe) {
                            maxRe = candWordRe[1];
                            wc = (int) candWordRe[0];
                        }
                    }
                }
            }
            
            if (found) {
                coverNum ++;
                sumMaxRe += maxRe;
            }
            
            Wc.add(wc);
        }

        if (coverNum == 0) {
            relScore = 1;
        }
        else {
            relScore = coverNum + sumMaxRe / coverNum;
        }

        return relScore;
    }

// calculate ranking score;
    public double calRankScore() {
        if (distCon == 0) {
            rankScore = 0;
        } else {
            rankScore = 0.5 * (relScore / (Wc.size() + 1)) + (1 - 0.5) * (1 - dist / distCon);
        }

        return rankScore;
    }

    public ArrayList<Integer> getWc() {
    	ArrayList<Integer> result = new ArrayList<Integer>();
    	
    	for (int i = 0; i < Wc.size(); i ++) {
    		result.add(Wc.get(i));
    	}
    	
        return result;
    }

    public static void main(String arg []) {
        //test
        //generate candidate keyword list
        double [] cw = {-111, 1};
        double [] cw1 = {-234, 0.5};
        double [] cw2 = {-133, 0.9};
        ArrayList<double[]> cwr1 = new ArrayList<>();
        ArrayList<double[]> cwr2 = new ArrayList<>();
        cwr1.add(cw);
        cwr1.add(cw1);
        cwr2.add(cw2);
        ArrayList<ArrayList<double[]>> allCwr = new ArrayList<>();
        allCwr.add(cwr1);
        allCwr.add(cwr2);

        //generate route keyword list
        ArrayList<Integer> rouW = new ArrayList<>();
        rouW.add(-111);
        rouW.add(-133);

        Score s = new Score(allCwr, rouW, 100, 500);

        System.out.println(s.calRelScore());
        System.out.println(s.calRankScore());

    }
}
