/**
 * 
 */
package textualIndex;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import datagenerate.AssignShop;
import indoor_entitity.D2Ddistance;
import indoor_entitity.Floor;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import utilities.Functions;

/**
 * <h>DistMatrix</h>
 * represent the exit to exit (exit2exit) distance
 * 
 * @author feng zijin
 *
 */
public class DistMatrix {
	private double[][] matrix;
	private int mID;			// partition id
	private boolean isPartition;		// is partition or floor
	
	public DistMatrix(int mID, boolean isPartition) {
		this.mID = mID;
		this.isPartition = isPartition;
		createMatrix();
	}
	
	/**
	 * create the exit2exit for this partition
	 */
	private void createMatrix() {
		
		if (isPartition) {
			Partition partition = IndoorSpace.iPartitions.get(mID);
			int doorNum = partition.getmDoors().size();
			
			matrix = new double[doorNum][doorNum];
			
//			System.out.println(partition.getmID() + " has door = " + IndoorSpace.iPartitions.get(partition.getmID()).getmDoors().size());
//			System.out.println("D2dHashMap size = " + partition.getD2dHashMap().size() + " matrix size = " + matrix.length + " * "
//					+ "" + matrix[0].length);
			
			Iterator it = partition.getD2dHashMap().entrySet().iterator();
		    while (it.hasNext()) {
		    		HashMap.Entry pair = (HashMap.Entry)it.next();
		        
		        // get the key and value
		        String key = (String) pair.getKey();
		        int[] doorID = Functions.keyReverseConventer(key);
		        D2Ddistance d2Ddistance = (D2Ddistance) pair.getValue();
		        double distance = d2Ddistance.getDistance();
				
		        // find the index of the door stored in partition
				int index1 = partition.getDoorIndex(doorID[0]);
				int index2 = partition.getDoorIndex(doorID[1]);
				
				// put the distance into matrix
				if(index1 != -1 && index2 != -1 && matrix[index1][index2] == 0.0) {
					matrix[index1][index2] = distance;
					matrix[index2][index1] = distance;
				} else {
					System.out.println("something wrong_DistMatrix_createMatrix");
				}
		    }
			
//			System.out.println(Functions.print2Ddoublearray(matrix, 0, matrix.length, 0, matrix[0].length));
			
		} else {			// if it is floor 
			
			Floor floor = IndoorSpace.iFloors.get(mID);
			int doorNum = floor.getmDoors().size();
			
			matrix = new double[doorNum][doorNum];
			
//			System.out.println(floor.getmID() + " has door = " + IndoorSpace.iFloors.get(floor.getmID()).getmDoors().size());
//			System.out.println("D2dHashMap size = " + floor.getD2dHashMap().size() + " matrix size = " + matrix.length + " * "
//					+ "" + matrix[0].length);
			
			Iterator it = floor.getD2dHashMap().entrySet().iterator();
		    while (it.hasNext()) {
		    		HashMap.Entry pair = (HashMap.Entry)it.next();
		        
		        // get the key and value
		        String key = (String) pair.getKey();
		        int[] doorID = Functions.keyReverseConventer(key);
		        D2Ddistance d2Ddistance = (D2Ddistance) pair.getValue();
		        double distance = d2Ddistance.getDistance();
				
		        // find the index of the door stored in partition
				int index1 = floor.getDoorIndex(doorID[0]);
				int index2 = floor.getDoorIndex(doorID[1]);
				
				// put the distance into matrix
				if(index1 != -1 && index2 != -1 && matrix[index1][index2] == 0.0) {
					matrix[index1][index2] = distance;
					matrix[index2][index1] = distance;
				} else {
					System.out.println("something wrong_DistMatrix_createMatrix");
				}
		    }
			
//			System.out.println(Functions.print2Ddoublearray(matrix, 0, matrix.length, 0, matrix[0].length));
		}
		
		
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
	 * @return the matrix
	 */
	public double[][] getMatrix() {
		return matrix;
	}

	/**
	 * @param matrix
	 *            the matrix to set
	 */
	public void setMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * @param d1
	 * @param d2
	 * @return the distance
	 */
	public double getDistance(int d1, int d2) {
		Partition partition = IndoorSpace.iPartitions.get(mID);
		int index1 = partition.getDoorIndex(d1);
		int index2 = partition.getDoorIndex(d2);
		
		if (index1 < 0 || index2 < 0 || index1 > matrix.length || index2 > matrix[0].length) return -1;
		
//		System.out.print("return " + matrix[index1][index2] + " ");
//		System.out.println(IndoorSpace.iDoors.get(d1).toString() + " to " + IndoorSpace.iDoors.get(d2).toString());
		
		return matrix[index1][index2];
	}
	
	public static void main(String[] args) throws IOException {
		// generate indoor space and assign shop
		AssignShop assignShop = new AssignShop();
		assignShop.assignShop();
		
		DistMatrix distMatrix = new DistMatrix(130,true);
	}
}
