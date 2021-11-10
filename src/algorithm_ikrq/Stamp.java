package algorithm_ikrq;
/**
 *
 */

import java.util.ArrayList;

/**
 * @author Tiantian
 *
 */
public class Stamp {
    public int v;
    public ArrayList<Integer> Rp = new ArrayList<Integer>(); // route;
    
    public ArrayList<Integer> Pc = new ArrayList<>(); // cover partition;
    public ArrayList<Integer> P = new ArrayList<>(); // route partition;
    
    public ArrayList<Integer> Wc = new ArrayList<>(); // cover word (with the greatest word relevance value) list, the length equals to the length of query keywords;
    public ArrayList<Integer> Wr = new ArrayList<>();// route keywords
    
    public double D = 0; // distance
    public double relScore; // route relevance score;
    public double rankScore; // ranking score;
    
    public Score score = null;
    
    public int life = -2;

    /**
     * Constructor
     *
     */
    public Stamp(int v, ArrayList<Integer> Rp) {
        this.v = v;
        
        for (int i = 0; i < Rp.size(); i++) {
            this.Rp.add(Rp.get(i));
        }
    }

    /**
     * Constructor
     *
     */
    public Stamp(Stamp stamp) {
        this.v = stamp.v;
        for (int i = 0; i < stamp.Rp.size(); i++) {
            this.Rp.add(stamp.Rp.get(i));
        }
        for (int i = 0; i < stamp.Pc.size(); i++) {
            this.Pc.add(stamp.Pc.get(i));
        }
        for (int i = 0; i < stamp.P.size(); i++) {
            this.P.add(stamp.P.get(i));
        }
        for (int i = 0; i < stamp.Wc.size(); i++) {
            this.Wc.add(stamp.Wc.get(i));
        }
        for (int i = 0; i < stamp.Wr.size(); i++) {
            this.Wr.add(stamp.Wr.get(i));
        }
        
        this.D = stamp.D;
        this.relScore = stamp.relScore;
        this.rankScore = stamp.rankScore;
        
        this.score = stamp.score;

        this.life = -2;
    }
    
    public void setScore(Score score) {
    	this.score = score;
    }

    /**
     * set v
     *
     * @param v
     */
    public void setV(int v) {
        this.v = v;
    }

    /**
     * set Rp
     *
     * @param Rp
     */
    public void setRp(ArrayList<Integer> Rp) {
        this.Rp = Rp;
    }

    /**
     * set Wp
     *
     * @param Wc
     */
    public void setWc(ArrayList<Integer> Wc) {
        this.Wc = Wc;
    }

    /**
     * set D
     *
     * @param D
     */
    public void setD(double D) {
        this.D = D;
    }

    /**
     * set relScore
     *
     * @param relScore
     */
    public void setRelScore(double relScore) {
        this.relScore = relScore;
    }

    /**
     * set rankScore
     *
     * @param rankScore
     */
    public void setRankScore(double rankScore) {
        this.rankScore = rankScore;
    }

    public void setWr(ArrayList<Integer> Wr) {
        this.Wr = Wr;
    }

    public void setP(ArrayList<Integer> P) { this.P = P; }

    public void setPc(ArrayList<Integer> Pc) { this.Pc = Pc; }

    public boolean equals(algorithm_ikrq.Stamp another) {
        if (this.v != another.v) return false;
        if (!this.Rp.equals(another.Rp)) return false;
        if (!this.Wc.equals(another.Wc)) return false;
        if (!this.Wr.equals(another.Wr)) return false;
        if (this.D != another.D) return false;
        if (this.relScore != another.relScore) return false;
        if (this.rankScore != another.rankScore) return false;
        if (!this.P.equals(another.P)) return false;
        if (!this.Pc.equals(another.Pc)) return false;
        return true;
    }
}

