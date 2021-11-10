/**
 * 
 */
package textualIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import datagenerate.AssignShop;
import indoor_entitity.*;
import utilities.DataGenConstant;
import utilities.DoorType;
import utilities.Functions;
import utilities.RoomType;

/**
 * <h>ConnectivityTier</h>
 * represent the ConnectivityTier for each of the node
 * 
 * @author feng zijin
 *
 */
public class ConnectivityTier {
	private ArrayList<ConnectivityElement> connectivityTierIn = new ArrayList<ConnectivityElement>();		// key is door id
	private ArrayList<ConnectivityElement> connectivityTierOut = new ArrayList<ConnectivityElement>();		// key is door id
	private int mID;			// it is partition id
	
	/**
	 * Constructor
	 * 
	 */
	public ConnectivityTier() {}
	
	/**
	 * Constructor
	 * 
	 */
	public ConnectivityTier(int mID) {
		this.mID = mID;
		this.loadData();
	}
	
	public void loadData() {
		Partition partition_1 = IndoorSpace.iPartitions.get(mID);
		List<Integer> doors_1 = new ArrayList<Integer>();
		doors_1 = partition_1.getmDoors();
		
		for (int i = 0; i < doors_1.size(); i ++) {
			int doorID_1 = doors_1.get(i);
			Door door_1 = IndoorSpace.iDoors.get(doorID_1);
			List<Integer> partitions_2 = new ArrayList<Integer>();
			partitions_2 = door_1.getmPartitions();
			
			if (!partitions_2.contains(mID)) {
				System.out.println("something wrong_ConnectivityTier_loadData1" + " " + mID);
			}
			
			for (int j = 0; j < partitions_2.size(); j ++) {
				int partitionID_2 = partitions_2.get(j);
				
				if (partitionID_2 != mID) {
					
					if (partition_1.getmType() == RoomType.STAIRCASE 
							&& IndoorSpace.iDoors.get(doorID_1).getmType() == DoorType.EXIT
							&& IndoorSpace.iPartitions.get(partitionID_2).getmType() == RoomType.STAIRCASE) {
						
						int doorID_2 = doorID_1 + DataGenConstant.curSizeDoor;
						
						if (partitionID_2 > mID && doorID_2 <= IndoorSpace.iDoors.size()) {
							// out door
							ConnectivityElement element_out = new ConnectivityElement(doorID_1, doorID_2, partitionID_2, false, false);
							addConnectivityElementOut(element_out);
							
							// in door
							ConnectivityElement element_in = new ConnectivityElement(doorID_1, doorID_2, partitionID_2, false, true);
							IndoorSpace.iPartitions.get(partitionID_2).getConnectivityTier().addConnectivityElementIn(element_in);
							
							// add the direction to the door
							IndoorSpace.iDoors.get(doorID_1).addDirection(new Direction(mID, partitionID_2, doorID_1, doorID_2));
						}
						
						doorID_2 = doorID_1 - DataGenConstant.curSizeDoor;
						
						if (partitionID_2 < mID && doorID_2 >= 0) {
							// out door
							ConnectivityElement element_out = new ConnectivityElement(doorID_1, doorID_2, partitionID_2, false, false);
							addConnectivityElementOut(element_out);
							
							// in door
							ConnectivityElement element_in = new ConnectivityElement(doorID_1, doorID_2, partitionID_2, false, true);
							IndoorSpace.iPartitions.get(partitionID_2).getConnectivityTier().addConnectivityElementIn(element_in);
							
							// add the direction to the door
							IndoorSpace.iDoors.get(doorID_1).addDirection(new Direction(mID, partitionID_2, doorID_1, doorID_2));
						}
					} else {
						// out door
						ConnectivityElement element_out = new ConnectivityElement(doorID_1, partitionID_2, true, false);
						addConnectivityElementOut(element_out);
						
						// in door
						ConnectivityElement element_in = new ConnectivityElement(doorID_1, partitionID_2, true, true);
						IndoorSpace.iPartitions.get(partitionID_2).getConnectivityTier().addConnectivityElementIn(element_in);
						
						// add the direction to the door
						IndoorSpace.iDoors.get(doorID_1).addDirection(new Direction(mID, partitionID_2, doorID_1));
					}
					
				}
			}
		}
	}
	
	/**
	 * @param connectivityElement the ConnectivityElement to be added
	 * 
	 */
	public void addConnectivityElementIn(ConnectivityElement connectivityElement) {
//		boolean exist = false;
		
//		for (int i = 0; i < connectivityTierIn.size(); i ++) {
//			ConnectivityElement temp = connectivityTierIn.get(i);
//			
//			if (temp.getIsPartition() == connectivityElement.getIsPartition()) {
//				if (temp.getIsIn() == connectivityElement.getIsIn()
//						&& temp.getmDoorID1() == connectivityElement.getmDoorID1()
//						&& temp.getmDoorID2() == connectivityElement.getmDoorID2()
//						&& temp.getmParID() == connectivityElement.getmParID()) {
//					exist = true;
//				} else exist = false;
//			} else exist = false;
//		}
//		
//		if (!exist) {
			connectivityTierIn.add(connectivityElement);
//		}
	}
	
	/**
	 * @return the connectivity tier in
	 * 
	 */
	public ArrayList<ConnectivityElement> getconnectivityTierIn() {
		return connectivityTierIn;
	}
	
	/**
	 * @param connectivityElement the ConnectivityElement to be added
	 * 
	 */
	public void addConnectivityElementOut(ConnectivityElement connectivityElement) {
//		boolean exist = false;
//		
//		for (int i = 0; i < connectivityTierOut.size(); i ++) {
//			ConnectivityElement temp = connectivityTierOut.get(i);
//			
//			if (temp.getIsPartition() == connectivityElement.getIsPartition()) {
//				if (temp.getIsIn() == connectivityElement.getIsIn()
//						&& temp.getmDoorID1() == connectivityElement.getmDoorID1()
//						&& temp.getmDoorID2() == connectivityElement.getmDoorID2()
//						&& temp.getmParID() == connectivityElement.getmParID()) {
//					exist = true;
//					System.out.println("hey!!!");
//				} else exist = false;
//			} else exist = false;
//		}
//		
//		if (!exist) {
			connectivityTierOut.add(connectivityElement);
//		}
	}
	
	/**
	 * @return the connectivity tier out
	 * 
	 */
	public ArrayList<ConnectivityElement> getconnectivityTierOut() {
		return connectivityTierOut;
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
	 * @return a list of door id that can leave the partition
	 */
	public ArrayList<Integer> getP2DLeave(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i < connectivityTierOut.size(); i ++){
			ConnectivityElement connectivityElement = connectivityTierOut.get(i);
			result.add(connectivityElement.getmDoorID1());
		}
		
//		Collections.sort(result);
		
		return result;
	}
	
	/**
	 * @return a list of door id that can enter the partition
	 */
	public ArrayList<Integer> getP2DEnter(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int i = 0; i < this.connectivityTierIn.size(); i ++) {
			ConnectivityElement connectivityElement = connectivityTierIn.get(i);
			
			if (connectivityElement.getIsPartition()) {
				if (!result.contains(connectivityElement.getmDoorID1())) result.add(connectivityElement.getmDoorID1());
			} else {
				if (!result.contains(connectivityElement.getmDoorID2())) result.add(connectivityElement.getmDoorID2());
			}
		}
		
//		Collections.sort(result);
		
		return result;
	}
	
	/**
	 * @return a list of partition id that can leave from the partition
	 */
	public ArrayList<Integer> getP2PLeave(){
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i < connectivityTierOut.size(); i ++){
			ConnectivityElement connectivityElement = connectivityTierOut.get(i);
			result.add(connectivityElement.getmParID());
		}
		
		return result;
	}
	

	
	/**
	 * test
	 * 
	 */
	public static void main(String[] args) throws IOException {
		AssignShop assignShop = new AssignShop();
		assignShop.assignShop();
		
		for (int i = 0; i < IndoorSpace.iPartitions.size(); i ++) {
			Partition partition = IndoorSpace.iPartitions.get(i);
			System.out.println("v" + partition.getmID() + " on floor " + partition.getmFloor());
//			if (partition.getmType() == RoomType.STAIRCASE) System.out.println("v" + partition.getmID() + " is " + partition.getmType() + " doors: " + Functions.printIntegerList((ArrayList<Integer>) partition.getmDoors()));
//			System.out.println("v" + partition.getmID() + " is " + partition.getmType() + " doors: " + Functions.printIntegerList((ArrayList<Integer>) partition.getmDoors()));
//			System.out.println("\t p2dLeave: " + partition.getConnectivityTier().getP2DLeave());
//			System.out.println("\t p2dEnter: " + partition.getConnectivityTier().getP2DEnter());
//			System.out.println("\t p2dLeave: " + partition.getConnectivityTier().getP2DLeave());
//			System.out.println("\t p2pLeave: " + partition.getConnectivityTier().getP2PLeave());
		}
		
		for (int i = 0; i < IndoorSpace.iDoors.size(); i ++) {
			Door door = IndoorSpace.iDoors.get(i);
			System.out.println("d" + door.getmID() + " on floor " + door.getmFloor());
//			if (door.getmType() == DoorType.EXIT) System.out.println("d" + door.getmID() + " is " + door.getmType() + " partitions: " + Functions.printIntegerList((ArrayList<Integer>) door.getmPartitions()));
//			System.out.println("d" + door.getmID() + " is " + door.getmType() + " partitions: " + Functions.printIntegerList((ArrayList<Integer>) door.getmPartitions()));
//			System.out.println("\t directed to: " + door.getD2PLeave());
		}
		
//		for (int i = 0; i < IndoorSpace.iPartitions.size(); i ++) {
//			Partition partition_1 = IndoorSpace.iPartitions.get(i);
//			ArrayList<ConnectivityElement> connectivityTierOut = new ArrayList<ConnectivityElement>();
//			connectivityTierOut = partition_1.getConnectivityTier().getconnectivityTierOut();
//			System.out.println("v" + partition_1.getmID() + "(" + connectivityTierOut.size() + "): " + partition_1.getmDoors());
//			
//			if (connectivityTierOut.size() != partition_1.getmDoors().size()) System.out.println("something_wrong_ConnectivityTier_loadData");
//			
//			for (int j = 0; j < connectivityTierOut.size(); j ++) {
//				ConnectivityElement connectivityElement = connectivityTierOut.get(j);
//				
//				if (!connectivityElement.getIsPartition()) {
//					System.out.println("\t - d" + connectivityElement.getmDoorID1() + "(" + IndoorSpace.iDoors.get(connectivityElement.getmDoorID1()).getmType() + ")"
//					+ " - d" + connectivityElement.getmDoorID2() + "(" + IndoorSpace.iDoors.get(connectivityElement.getmDoorID2()).getmType() + ")"
//					+ " - v" + connectivityElement.getmParID());
//				} else {
//					System.out.println("\t - d" + connectivityElement.getmDoorID1() + " - v" + connectivityElement.getmParID());
//				}
//			}
//		}
	}
}
