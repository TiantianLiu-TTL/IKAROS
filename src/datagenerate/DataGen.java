/**
 * 
 */
package datagenerate;

import utilities.RoomType;
import utilities.DataGenConstant;
import utilities.DoorType;
import utilities.FilePaths;
import utilities.Functions;
import indoor_entitity.Door;
import indoor_entitity.Floor;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import textualIndex.ConnectivityTier;
import textualIndex.DistMatrix;
import indoor_entitity.D2Ddistance;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;




/**
 * <h>DataGen</h>
 * to generate the data
 * 
 * @author feng zijin
 *
 */
public class DataGen {
	
	public static String outputPath = FilePaths.FilePathPre;
	
	/**
	 * generates all the data
	 * 
	 */
	public void genAllData() {
		genIndoorSpace();
		
		duplicateIndoorSpace(DataGenConstant.nFloor);
		
		System.out.println();
		
		int partitionSize = IndoorSpace.iPartitions.size();
		for (int i = 0; i < partitionSize; i ++) {
			IndoorSpace.iPartitions.get(i).getConnectivityTier().setmID(IndoorSpace.iPartitions.get(i).getmID());
		}
		
		for (int i = 0; i < partitionSize; i++) {

			// D2D distance Matrix
			HashMap<String, D2Ddistance> hashMap = IndoorSpace.iPartitions.get(
					i).getD2dHashMap();
			IndoorSpace.iD2D.putAll(hashMap);
			
			// partition's distance matrix
			DistMatrix distMatrix = new DistMatrix(IndoorSpace.iPartitions.get(i).getmID(), true);
			IndoorSpace.iPartitions.get(i).setDistMatrix(distMatrix);
						
			// partition's connectivity tier
			IndoorSpace.iPartitions.get(i).getConnectivityTier().loadData();
		}
		
		int floorSize = IndoorSpace.iFloors.size();
		for (int i = 0; i < floorSize; i ++) {
			
			// D2D distance Matrix
			HashMap<String, D2Ddistance> hashMap = IndoorSpace.iFloors.get(
					i).getD2dHashMap();
			IndoorSpace.iD2D.putAll(hashMap);
			
			// partition's connectivity tier
			ConnectivityTier connectivityTier = new ConnectivityTier(IndoorSpace.iFloors.get(i).getmID());
			IndoorSpace.iFloors.get(i).setConnectivityTier(connectivityTier);
		}

		System.out.println("Partitions's distance matrix and connectivity tier generate finished!");
		
		if (true == saveDP()) {
			System.out.println("Partitions & Doors Generating Finished! " + IndoorSpace.iPartitions.size() + " " + IndoorSpace.iDoors.size());
		}
	}
	
	/**
	 * duplicates a number of floors
	 * 
	 * @param floorNumber
	 *            the number of floors in the whole building
	 */
	private void duplicateIndoorSpace(int floorNumber) {
		int curSizePar = IndoorSpace.iPartitions.size();
		int curSizeDoor = IndoorSpace.iDoors.size();
		DataGenConstant.curSizePar = curSizePar;
		DataGenConstant.curSizeDoor = curSizeDoor;
		IndoorSpace.iNumberDoorsPerFloor = curSizeDoor;
		IndoorSpace.iNumberParsPerFloor = curSizePar;
		IndoorSpace.iNumberFloorPerMall = DataGenConstant.nFloor;
		
		System.out.println("per floor door num = " + curSizeDoor + " partition num = " + curSizePar);
		
		// ground floor
		Floor tempFloor = new Floor(0);
		
		for (int parIndex = 0; parIndex < curSizePar; parIndex++) {
			tempFloor.addPartition(IndoorSpace.iPartitions.get(parIndex).getmID());
		}

		tempFloor.addDoor(IndoorSpace.iDoors.get(210).getmID());
		IndoorSpace.iDoors.get(210).addFloor(0);
		IndoorSpace.iDoors.get(210).addFloor(1);
		
		tempFloor.addDoor(IndoorSpace.iDoors.get(212).getmID());
		IndoorSpace.iDoors.get(212).addFloor(0);
		IndoorSpace.iDoors.get(212).addFloor(1);
		
		tempFloor.addDoor(IndoorSpace.iDoors.get(213).getmID());
		IndoorSpace.iDoors.get(213).addFloor(0);
		IndoorSpace.iDoors.get(213).addFloor(1);
		
		tempFloor.addDoor(IndoorSpace.iDoors.get(214).getmID());
		IndoorSpace.iDoors.get(214).addFloor(0);
		IndoorSpace.iDoors.get(214).addFloor(1);
		
		tempFloor.updateCorner();
		IndoorSpace.iFloors.add(tempFloor);
		
		// other than ground floor
		for (int floorIndex = 1; floorIndex < floorNumber; floorIndex++) {
			Floor floor = new Floor(floorIndex);
			
			// for each door on the floor
			for (int doorIndex = 0; doorIndex < curSizeDoor; doorIndex ++) {
				Door curDoor = IndoorSpace.iDoors.get(doorIndex);
				Door newDoor = new Door(curDoor);
				
				newDoor.setmFloor(floorIndex);
				newDoor.setmID(doorIndex + floorIndex * curSizeDoor);
				
				List<Integer> mPars = new ArrayList<Integer>();
				
				// for each partition attached to this door
				if (newDoor.getmType() == DoorType.EXIT) {
					for (int mPar : curDoor.getmPartitions()) {
						mPars.add(mPar + (floorIndex - 1) * curSizePar);
					}
					
					for (int mPar : curDoor.getmPartitions()) {
						mPars.add(mPar + floorIndex * curSizePar);
					}
					
					for (int mPar : curDoor.getmPartitions()) {
						if (floorIndex < floorNumber - 1) mPars.add(mPar + (floorIndex + 1) * curSizePar);
					}
					
				} else {
					for (int mPar : curDoor.getmPartitions()) {
						mPars.add(mPar + floorIndex * curSizePar);
					}
				}
				
				newDoor.setmPartitions(mPars);
				IndoorSpace.iDoors.add(newDoor);
			}
						
			// for each partition on the floor
			for (int parIndex = 0; parIndex < curSizePar; parIndex ++) {
				Partition curPar = IndoorSpace.iPartitions.get(parIndex);
				Partition newPar = new Partition(curPar);
				
				newPar.setmFloor(floorIndex);
				newPar.setmID(parIndex + floorIndex * curSizePar);
				
				List<Integer> mDoors = new ArrayList<Integer>();
				
				// for each door attached to this partition
				if (newPar.getmType() == RoomType.STAIRCASE) {
					
					// for doors on same level floor
					for (int mDoor : curPar.getmDoors()) {
						mDoors.add(mDoor + floorIndex * curSizeDoor);
					}
					
				} else {
					// for doors on same level floor
					for (int mDoor : curPar.getmDoors()) {
						mDoors.add(mDoor + floorIndex * curSizeDoor);
					}
				}
				
				newPar.setmDoors(mDoors);
				floor.addPartition(newPar.getmID());
				IndoorSpace.iPartitions.add(newPar);
			}
			
			
			
			floor.addDoor(IndoorSpace.iDoors.get(210 + floorIndex * curSizeDoor).getmID());
			IndoorSpace.iDoors.get(210 + floorIndex * curSizeDoor).addFloor(floorIndex - 1);
			IndoorSpace.iDoors.get(210 + floorIndex * curSizeDoor).addFloor(floorIndex);
			if (floorIndex < (floorNumber - 1)) IndoorSpace.iDoors.get(210 + floorIndex * curSizeDoor).addFloor(floorIndex + 1);
			
			floor.addDoor(IndoorSpace.iDoors.get(212 + floorIndex * curSizeDoor).getmID());
			IndoorSpace.iDoors.get(212 + floorIndex * curSizeDoor).addFloor(floorIndex - 1);
			IndoorSpace.iDoors.get(212 + floorIndex * curSizeDoor).addFloor(floorIndex);
			if (floorIndex < (floorNumber - 1)) IndoorSpace.iDoors.get(212 + floorIndex * curSizeDoor).addFloor(floorIndex + 1);
			
			floor.addDoor(IndoorSpace.iDoors.get(213 + floorIndex * curSizeDoor).getmID());
			IndoorSpace.iDoors.get(213 + floorIndex * curSizeDoor).addFloor(floorIndex - 1);
			IndoorSpace.iDoors.get(213 + floorIndex * curSizeDoor).addFloor(floorIndex);
			if (floorIndex < (floorNumber - 1)) IndoorSpace.iDoors.get(213 + floorIndex * curSizeDoor).addFloor(floorIndex + 1);//			System.out.println((IndoorSpace.iDoors.get(213 + floorIndex * curSizeDoor).getmID() == (213 + floorIndex * curSizeDoor)) + " " + (213 + floorIndex * curSizeDoor));
			
			floor.addDoor(IndoorSpace.iDoors.get(214 + floorIndex * curSizeDoor).getmID());
			IndoorSpace.iDoors.get(214 + floorIndex * curSizeDoor).addFloor(floorIndex - 1);
			IndoorSpace.iDoors.get(214 + floorIndex * curSizeDoor).addFloor(floorIndex);
			if (floorIndex < (floorNumber - 1)) IndoorSpace.iDoors.get(214 + floorIndex * curSizeDoor).addFloor(floorIndex + 1);
			
			tempFloor.updateCorner();
			IndoorSpace.iFloors.add(floor);
		}
		
//		for (int i = 0; i < curSizePar; i ++) {
//			
//			if (IndoorSpace.iPartitions.get(i).getmType() == RoomType.STAIRCASE) {
//				Partition partition = IndoorSpace.iPartitions.get(i);
//				ArrayList<Integer> doors = new ArrayList<Integer>();
//				doors = (ArrayList<Integer>) partition.getmDoors();
//				
//				for (int j = 0; j < doors.size(); j ++) {
//					if (IndoorSpace.iDoors.get(doors.get(j)).getmType() == DoorType.EXIT) {
//						IndoorSpace.iPartitions.get(i).addDoor(doors.get(j) + curSizeDoor);
//						break;
//					}
//				}
//			}
//		}
		
		for (int i = 0; i < curSizeDoor; i ++) {
			
			if (IndoorSpace.iDoors.get(i).getmType() == DoorType.EXIT) {
				Door door = IndoorSpace.iDoors.get(i);
				List<Integer> partitions = new ArrayList<Integer>();
				partitions = door.getmPartitions();
				
				for (int j = 0; j < partitions.size(); j ++){
					if (IndoorSpace.iPartitions.get(partitions.get(j)).getmType() == RoomType.STAIRCASE) {
						IndoorSpace.iDoors.get(i).addPar(partitions.get(j) + curSizePar);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * generates the indoor space
	 * 
	 */
	private void genIndoorSpace() {
		// partitions
		initParitions();
		
//		System.out.println("==> first reflect");
		parReflection(0, 684);
		
//		System.out.println("==> second reflect");
		parReflection(1, 684);
		
		genOtherHallway();
		
		//staircases
		genStaircase();
		
		// doors
		initDoors();
		
//		System.out.println("==> first reflect");
		doorReflection(0, 684);
		
//		System.out.println("==> second reflect");
		doorReflection(1, 684);
		
		genOtherDoor();
		
		// link partition and door together
		linkGraph();
	}
	
	/**
	 * generates the partitions from the left-top part of a single floor
	 * 
	 */
	private void initParitions() {
		
		// stores
		System.out.println("==> Stores");
		// add the large room x1, x2, y1, y2
		IndoorSpace.iPartitions.add(new Partition(0.0, 288.0, 0.0, 288.0, RoomType.STORE));
		
		for (int i = 0; i < 5; i++) {
			IndoorSpace.iPartitions.add(new Partition(288.0 + 72 * i, 288.0 + 72 * (i + 1), 144.0, 288.0, RoomType.STORE));
		}
		
		for (int i = 0; i < 5; i++) {
			IndoorSpace.iPartitions.add(new Partition(144.0, 288.0, 288.0 + 72 * i, 288.0 + 72 * (i + 1), RoomType.STORE));
		}
		
		for (int i = 0; i < 4; i++) {
			IndoorSpace.iPartitions.add(new Partition(360.0 + 72 * i, 360.0 + 72 * (i + 1), 360.0, 432.0, RoomType.STORE));
		}
		
		for (int i = 0; i < 3; i++) {
			IndoorSpace.iPartitions.add(new Partition(360.0, 432.0, 432.0 + 72 * i, 432.0 + 72 * (i + 1), RoomType.STORE));
		}
		
		IndoorSpace.iPartitions.add(new Partition(576.0, 648.0, 432.0, 504.0, RoomType.STORE));
			
		IndoorSpace.iPartitions.add(new Partition(432.0, 504.0, 576.0, 648.0, RoomType.STORE));
			
		for (int i = 0; i < 2; i++) {
			IndoorSpace.iPartitions.add(new Partition(576.0 + 36 * i, 576.0 + 36 * (i + 1), 522.0, 576.0, RoomType.STORE));
		}
			
		for (int i = 0; i < 2; i++) {
			IndoorSpace.iPartitions.add(new Partition(522.0, 576.0, 576.0 + 36 * i, 576.0 + 36 * (i + 1), RoomType.STORE));
		}
			
		// hallways
		System.out.println("==> Hallways");
		
		IndoorSpace.iPartitions.add(new Partition(288.0, 360.0, 288.0, 360.0, RoomType.HALLWAY));
		
		for (int i = 0; i < 2; i++) {
			IndoorSpace.iPartitions.add(new Partition(360.0 + 144 * i, 360.0 + 144 * (i + 1), 288, 360.0, RoomType.HALLWAY));
		}
		
		for (int i = 0; i < 2; i++) {
			IndoorSpace.iPartitions.add(new Partition(288.0, 360.0, 360 + 144 * i, 360.0 + 144 * (i + 1), RoomType.HALLWAY));
		}
		
		IndoorSpace.iPartitions.add(new Partition(432.0, 576.0, 432.0, 576.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(576.0, 648.0, 504.0, 522.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(504.0, 522.0, 576.0, 648.0, RoomType.HALLWAY));
	}
	
	/**
	 * generate the rest of hallways
	 */
	private void genOtherHallway() {
		System.out.println("==> other hallways");
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 288.0, 360.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 360.0, 576.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(576.0, 792.0, 576.0, 792.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 792.0, 1008.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 1008.0, 1080.0, RoomType.HALLWAY));

		IndoorSpace.iPartitions.add(new Partition(288.0, 360.0, 648.0, 720.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(360.0, 576.0, 648.0, 720.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(792.0, 1008.0, 648.0, 720.0, RoomType.HALLWAY));
		
		IndoorSpace.iPartitions.add(new Partition(1008.0, 1080.0, 648.0, 720.0, RoomType.HALLWAY));
	}

	/**
	 * generate staircase
	 */
	private void genStaircase() {
		System.out.println("==> staircases");
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 144.0, 288.0, RoomType.STAIRCASE));
		
		IndoorSpace.iPartitions.add(new Partition(648.0, 720.0, 1080.0, 1224.0, RoomType.STAIRCASE));
		
		IndoorSpace.iPartitions.add(new Partition(144.0, 288.0, 648.0, 720.0, RoomType.STAIRCASE));
		
		IndoorSpace.iPartitions.add(new Partition(1080.0, 1224.0, 648.0, 720.0, RoomType.STAIRCASE));
	}
	
	/**
	 * generates the doors as entrances for each partition
	 * 
	 */
	public void initDoors() {
		System.out.println("==> doors");

		Iterator<Partition> itr = IndoorSpace.iPartitions.iterator();

		while (itr.hasNext()) {
			Partition curPar = itr.next();
			Door aDoor = null;
			
			int partitionNo = curPar.getmID() + 1;
			
			// on the upper part
			if ((partitionNo == 7) || (partitionNo >= 12 && partitionNo <= 18) || (partitionNo == 20) 
				|| (partitionNo >= 23 && partitionNo <= 24) || (partitionNo == 32)) {
				
				aDoor = new Door((curPar.getX1() + curPar.getX2()) / 2, curPar.getY1(), DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}

			// on the right part
			if ((partitionNo >= 7 && partitionNo <= 17) || (partitionNo == 19) || (partitionNo == 22)
				|| (partitionNo == 25) || (partitionNo == 27) || (partitionNo == 31)) {
				
				aDoor = new Door(curPar.getX2(), (curPar.getY1() + curPar.getY2()) / 2, DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}
			
			// on the bottom part
			if ((partitionNo >= 2 && partitionNo <= 6) || (partitionNo >= 13 && partitionNo <= 14)
				|| (partitionNo == 18) || (partitionNo == 20) || (partitionNo >= 24 && partitionNo <= 25)
				|| (partitionNo == 29) || (partitionNo == 32)) {
				
				aDoor = new Door((curPar.getX1() + curPar.getX2()) / 2, curPar.getY2(), DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}

			// on the left part
			if ((partitionNo == 2) || (partitionNo == 12) || (partitionNo >= 16 && partitionNo <= 19)
				|| (partitionNo >= 21 && partitionNo <= 22) || (partitionNo == 31)) {
//				System.out.print("left part ");
				
				aDoor = new Door(curPar.getX1(), (curPar.getY1() + curPar.getY2()) / 2, DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}
		}
	}
	
	/**
	 * generates the rest of the doors, i.e. floor exits
	 * 
	 */
	private void genOtherDoor(){
		System.out.println("==> other doors");
		
		Iterator<Partition> itr = IndoorSpace.iPartitions.iterator();

		while (itr.hasNext()) {
			Partition curPar = itr.next();
			Door aDoor = null;
			
			int partitionNo = curPar.getmID() + 1;
			
			// on the upper part
			if ((partitionNo >= 129 && partitionNo <= 133) 
				|| (partitionNo >= 138 && partitionNo <= 139)) {
				
				if (partitionNo == 138) aDoor = new Door((curPar.getX1() + curPar.getX2()) / 2, curPar.getY1(), DoorType.EXIT);
				else aDoor = new Door((curPar.getX1() + curPar.getX2()) / 2, curPar.getY1(), DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}

			// on the right part
			if ((partitionNo == 141)) {
				
				aDoor = new Door(curPar.getX2(), (curPar.getY1() + curPar.getY2()) / 2, DoorType.EXIT);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}
			
			// on the bottom part
			if ((partitionNo == 139)) {
				
				aDoor = new Door((curPar.getX1() + curPar.getX2()) / 2, curPar.getY2(), DoorType.EXIT);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}

			// on the left part
			if ((partitionNo == 131) || (partitionNo >= 134 && partitionNo <= 137)
				|| (partitionNo >= 140 && partitionNo <= 141)) {
				
				if (partitionNo == 140) aDoor = new Door(curPar.getX1(), (curPar.getY1() + curPar.getY2()) / 2, DoorType.EXIT);
				else aDoor = new Door(curPar.getX1(), (curPar.getY1() + curPar.getY2()) / 2, DoorType.NORMAL);
				
				if (IndoorSpace.iDoors.contains(aDoor));
				IndoorSpace.iDoors.add(aDoor);
			}			
		}
	}
	
	/**
	 * tests if the door is the entrance of the partition, if it is, link them
	 * together
	 * 
	 */
	public void linkGraph() {

		Iterator<Partition> itrPar = IndoorSpace.iPartitions.iterator();

		while (itrPar.hasNext()) {
			Partition curPar = itrPar.next();
			Iterator<Door> itrDoor = IndoorSpace.iDoors.iterator();
			
			while (itrDoor.hasNext()) {
				Door curDoor = itrDoor.next();
				
				if (curPar.testDoor(curDoor) == true) {
					curPar.addDoor(curDoor.getmID());
					curDoor.addPar(curPar.getmID());
				}
			}
		}
	}
	
	/**
	 * reflects the existing partitions in one axis(parameter), pivot is as well
	 * given
	 * 
	 * @param axis
	 * @param pivot
	 */
	private void parReflection(int axis, int pivot) {
		int curSize = IndoorSpace.iPartitions.size();

		int xa, xb, ya, yb, x0, y0, x1, x2, y1, y2, type;

		for (int i = 0; i < curSize; i++) {

			x1 = (int) IndoorSpace.iPartitions.get(i).getX1();
			x2 = (int) IndoorSpace.iPartitions.get(i).getX2();
			y1 = (int) IndoorSpace.iPartitions.get(i).getY1();
			y2 = (int) IndoorSpace.iPartitions.get(i).getY2();
			type = IndoorSpace.iPartitions.get(i).getmType();

			if (0 == axis) {
				x0 = pivot;
				xa = x0 + (x0 - x2);
				xb = x0 + (x0 - x1);
				ya = y1;
				yb = y2;
			} else if (1 == axis) {
				y0 = pivot;
				xa = x1;
				xb = x2;
				ya = y0 + (y0 - y1);
				yb = y0 + (y0 - y2);
			} else {
				return;
			}

			IndoorSpace.iPartitions.add(new Partition(Math.min(xa, xb), Math.max(xa, xb), Math.min(ya,
					yb), Math.max(ya, yb), type));
		}
	}
	
	private void doorReflection(int axis, int pivot) {
		int curDoorsSize = IndoorSpace.iDoors.size();
		for (int i = 0; i < curDoorsSize; i++) {
			Door curDoor = IndoorSpace.iDoors.get(i);
			Door newDoor = new Door(curDoor.getX(), curDoor.getY(), curDoor.getmType());
			newDoor.reflection(axis, pivot);
			IndoorSpace.iDoors.add(newDoor);
		}
	}
	
	/**
	 * saves the generated doors and partitions
	 * 
	 * @return boolean value if writing files is accomplished successfully.
	 * @exception IOException
	 */
	private boolean saveDP() {
		try {
			FileWriter fwPar = new FileWriter(outputPath + "/Par.txt");
			Iterator<Partition> itrPar = IndoorSpace.iPartitions.iterator();
			while (itrPar.hasNext()) {
				fwPar.write(itrPar.next().toString() + "\n");
			}
			fwPar.flush();
			fwPar.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter fwDoor = new FileWriter(outputPath + "/Door.txt");
			Iterator<Door> itrDoor = IndoorSpace.iDoors.iterator();
			while (itrDoor.hasNext()) {
				fwDoor.write(itrDoor.next().toString() + "\n");
			}
			fwDoor.flush();
			fwDoor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter fwP2D = new FileWriter(outputPath + "/P2D.txt");
			Iterator<Partition> itrPar = IndoorSpace.iPartitions.iterator();
			while (itrPar.hasNext()) {
				fwP2D.write(itrPar.next().toString() + "\n");
			}
			fwP2D.flush();
			fwP2D.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		try {
			FileWriter fwD2P = new FileWriter(outputPath + "/D2P.txt");
			Iterator<Door> itrDoor = IndoorSpace.iDoors.iterator();
			while (itrDoor.hasNext()) {
				Door curDoor = itrDoor.next();
				fwD2P.write(curDoor.toString() + "\n");
			}
			fwD2P.flush();
			fwD2P.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		try {
			FileWriter fwD2D = new FileWriter(outputPath + "/D2D.txt");
			Iterator<Partition> itrPar = IndoorSpace.iPartitions.iterator();
			while (itrPar.hasNext()) {
				fwD2D.write(itrPar.next().d2DtoString() + "\n");
			}
			fwD2D.flush();
			fwD2D.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public static void main(String args[]) {
		DataGen dataGen = new DataGen();
		dataGen.genAllData();
		
		for (int i = 0; i < IndoorSpace.iPartitions.size(); i ++) {
			Partition partition = IndoorSpace.iPartitions.get(i);
			
			System.out.println("<g fill=\"white\" stroke=\"black\" stroke-width=\"1\">");
			System.out.println("<rect x=\"" + partition.getX1() + "\" y=\"" + partition.getY1() + "\" width=\"" 
					+ (partition.getX2() - partition.getX1()) + "\" height=\"" + (partition.getY2() - partition.getY1()) + "\"/>");
			System.out.println("</g>");
		}
	}
}
