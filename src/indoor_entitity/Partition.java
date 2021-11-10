/**
 * 
 */
package indoor_entitity;

import utilities.DataGenConstant;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import textualIndex.ConnectivityTier;
import textualIndex.DistMatrix;
import utilities.Functions;


/**
 * <h>Partition</h>
 * to describe a partition
 * 
 * @author feng zijin
 *
 */
public class Partition extends Rect{
	private int mID;		// the ID
	
	private int mType;	// room = 0; hallway = 1; staircase =2;
	
	private int mFloor; 		// the floor that the partition located on
	
	private Shop shop = null;
	
	private List<Integer> mDoors = new ArrayList<Integer>(); 	// the Doors of this partition
	
	private HashMap<String, D2Ddistance> d2dHashMap = new HashMap<String, D2Ddistance>();		// the distance between the relevant doors of this partition	
		
	private DistMatrix distMatrix;  // the distance between the doors of this partition
	
	private ConnectivityTier connectivityTier = new ConnectivityTier();  // the connectivity tier of this partition
	
	private int[] mKeywords = new int[0]; 	// the keywords of this partition
	
	private int Ikeyword = 0;
	
	/**
	 * Constructor 
	 * 
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param
	 * @param
	 */
	public Partition(double x1, double x2, double y1, double y2, int mType) {
		super(x1, x2, y1, y2);
		this.mFloor = DataGenConstant.mID_Floor;
		this.mID = DataGenConstant.mID_Par++;
		this.mType = mType;
		
//		System.out.println("partition generated with id = " + this.mID + " type = " + this.mType + " on floor = " + this.mFloor);
	}
	
	/**
	 * Constructor
	 * 
	 * @param another
	 */
	public Partition(Partition another) {
		super(another.getX1(), another.getX2(), another.getY1(), another.getY2());
		this.setmType(another.getmType());
		this.setmFloor(another.getmFloor());
	}
	
	/**
	 * add a relevant door of this partition
	 * 
	 * @param doorID
	 */
	public void addDoor(int doorID) {
		if (!this.mDoors.contains(doorID)) {
			this.mDoors.add(doorID);
		}
	}
	
	/**
	 * @return the mType
	 */
	public int getmType() {
		return mType;
	}

	/**
	 * @param mType
	 *            the mType to set
	 */
	public void setmType(int mType) {
		this.mType = mType;
	}

	/**
	 * @return the mFloor
	 */
	public int getmFloor() {
		return mFloor;
	}
	
	/**
	 * @param mFloor
	 *            the mFloor to set
	 */
	public void setmFloor(int mFloor) {
		this.mFloor = mFloor;
	}
	
	/**
	 * @return the mDoors
	 */
	public List<Integer> getmDoors() {
		return mDoors;
	}

	/**
	 * @param mDoors
	 *            the mDoors to set
	 */
	public void setmDoors(List<Integer> mDoors) {
		this.mDoors = mDoors;
	}

	/**
	 * @return the mID
	 */
	public int getmID() {
		return mID;
	}

	/**
	 * @param mID
	 *            the mID to set
	 */
	public void setmID(int mID) {
		this.mID = mID;
	}
	
	/**
	 * @return the distMatrix
	 */
	public DistMatrix getdistMatrix() {
		return distMatrix;
	}

	/**
	 * @param distMatrix
	 *            the distMatrix to set
	 */
	public void setDistMatrix(DistMatrix distMatrix) {
		this.distMatrix = distMatrix;
//		System.out.println(Functions.print2Ddoublearray(this.distMatrix.getMatrix(), 0, this.distMatrix.getMatrix().length
//				, 0, this.distMatrix.getMatrix()[0].length));
	}
	
	/**
	 * @return the connectivityTier
	 */
	public ConnectivityTier getConnectivityTier() {
		return connectivityTier;
	}

	/**
	 * @param connectivityTier
	 *            the connectivityTier to set
	 */
	public void setConnectivityTier(ConnectivityTier connectivityTier) {
		this.connectivityTier = connectivityTier;
	}
	
	/**
	 * @return the shop
	 */
	public Shop getshop() {
		return shop;
	}

	/**
	 * @param shop
	 *            the shop to set
	 */
	public void setshop(Shop shop) {
		this.shop = shop;
		mKeywords = new int[DataGenConstant.mKeyworSize];		// remove all of the old data
		
		// set mKeywords
		String[] tempArr = shop.getmDescription().split("\t");
		Ikeyword = Integer.parseInt(tempArr[0]);
		int tempArrSize = tempArr.length;
		for (int i = 1; i < tempArrSize; i ++) {
			if (mKeywords[Integer.parseInt(tempArr[i])] < 1) {
				mKeywords[Integer.parseInt(tempArr[i])] ++;
			}
		}
	}
	
	/**
	 * @return the mKeywords
	 */
	public int[] getmKeywords() {
		return mKeywords;
	}

	public int getIkeyword() { return Ikeyword; }

	/**
	 * @param mKeywords
	 *            the mKeywords to set
	 */
	public void setmKeywords(int[] mKeywords) {
		this.mKeywords = mKeywords;
	}
	
	/**
	 * @return the d2dHashMap
	 */
	public HashMap<String, D2Ddistance> getD2dHashMap() {
		d2dHashMap = new HashMap<String, D2Ddistance>();
		Collections.sort(this.mDoors);
		
		int doorSize = this.mDoors.size();
		for (int i = 0; i < doorSize; i++) {
			int index_1 = this.mDoors.get(i);
			Door door1 = IndoorSpace.iDoors.get(index_1);
			
			int doorSize1 = this.mDoors.size();
			for (int j = i + 1; j < doorSize1; j++) {
				int index_2 = this.mDoors.get(j);
				Door door2 = IndoorSpace.iDoors.get(index_2);
				D2Ddistance d2dDist = new D2Ddistance(index_1, index_2, door1.eDist(door2));
				d2dHashMap.put(Functions.keyConventer(index_1, index_2), d2dDist);
			}
		}
		this.setD2dHashMap(d2dHashMap);
		
		return d2dHashMap;
	}

	/**
	 * @param d2dHashMap
	 *            the d2dHashMap to set
	 */
	public void setD2dHashMap(HashMap<String, D2Ddistance> d2dHashMap) {
		this.d2dHashMap = d2dHashMap;
	}
	
	/**
	 * toString
	 * 
	 * @return mID+x1+x2+y1+y2+mFloor+mType+mDoors
	 */
	public String toString() {
		String outputString = this.getmID() + "\t" + this.getX1() + "\t"
				+ this.getX2() + "\t" + this.getY1() + "\t" + this.getY2() + "\t"
				+ this.getmFloor() + "\t" + this.getmType() + " \t";

		Iterator<Integer> itr = this.mDoors.iterator();

		while (itr.hasNext()) {
			outputString = outputString + itr.next() + "\t";
		}

		return outputString;
	}
	
	/**
	 * d2DtoString
	 * 
	 * @return d11 d12 d13 \n d21 d22 ... dnn
	 */
	public String d2DtoString() {
		String outputString = this.mID + "\t";

		Iterator it = this.d2dHashMap.entrySet().iterator();
		
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        outputString = outputString + pair.getKey() + " = " + pair.getValue() + "\t";
	    }
		

		return outputString;
	}
	
	/**
	 * @param doorID
	 * @return the index of the door in mDoors list
	 */
	public int getDoorIndex(int doorID) {
		int result = -1;
		
		int doorSize = this.mDoors.size();
		for(int i = 0; i < doorSize; i++) {
			if (doorID == this.mDoors.get(i)) {
				result = i;
				break;
			}
			 
		}
		
		return result;
	}
	
	public String cornerToString3D() {
		return "("  + this.getX1() + ", " + this.getY1() + ", " + this.getmFloor() + ")"
				+ ", (" + this.getX2() + ", " + this.getY2() + ", " + (this.getmFloor() + 1) + ")";
	}	
	
	public String cornerToString2D() {
		return "("  + this.getX1() + ", " + this.getY1() + ")"
				+ ", (" + this.getX2() + ", " + this.getY2() + ")";
	}	

}
