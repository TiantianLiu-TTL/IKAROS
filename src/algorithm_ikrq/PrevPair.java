/**
 * 
 */
package algorithm_ikrq;

/**
 * @author feng zijin
 *
 */
public class PrevPair {
	public int par;
	public int door;
	
	public PrevPair(int par, int door) {
		this.par = par;
		this.door = door;
	}
	
	public String toString() {
		return par + ", " + door;
	}
	
}
