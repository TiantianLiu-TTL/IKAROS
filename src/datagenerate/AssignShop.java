
package datagenerate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import indoor_entitity.Graph;
import indoor_entitity.IndoorSpace;
import indoor_entitity.Partition;
import indoor_entitity.Shop;
import utilities.DataGenConstant;
import utilities.FilePaths;
import utilities.Functions;
import utilities.RoomType;

/**
 * <h>AssignShop</h>
 * assign shop to the partition. this program will generate all indoor entities, pre-process 
 * the shop information like keyword.
 * 
 * The second steps with dealing with shops from shopsWithIndex.txt
 * 
 * @author feng zijin
 *
 */
public class AssignShop {
	private static String fileInput = FilePaths.FilePathPre + "/shopsWithIRKeywordIndex.txt";
	private static String fileOutput = FilePaths.FilePathPre + "/shopsWithPartition.txt";
	private static ArrayList<Shop> shopList = new ArrayList<Shop>();
	
	static DataGen dataGen = new DataGen();
	static KeywordIndexer keywordIndexer = new KeywordIndexer();
	
	public AssignShop() {}
	
	public static void assignShop() throws IOException {
		dataGen.genAllData();
		
		keywordIndexer.index();
		
		genShopList();
		System.out.println("shopList generated! " + shopList.size() + " shops");
	}
	
	public static void staticAssign() throws IOException {
		Path path = Paths.get(fileOutput);
		Scanner scanner = new Scanner(path);
		
		int partitionNum = DataGenConstant.nFloor * 96;
		while(scanner.hasNextLine() && partitionNum > 0){
			partitionNum--;
			String line = scanner.nextLine();
			String[] temp = line.split("\t");
			
			int parId = Integer.parseInt(temp[0]);
			int mId = Integer.parseInt(temp[1]);
			int originalId = Integer.parseInt(temp[2]);
			
			Partition partition = IndoorSpace.iPartitions.get(parId);
			Shop shop = new Shop(shopList.get(originalId));
			
			int size = shop.getmDescription().split("\t").length - 1;
			if (size > 60 || size < 1) System.out.println("alarm!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
			
			if (shop.getoriginalmID() != originalId) System.out.println("something wrong_AssignShop_staticAssign");
			
			shop.setmFloor(partition.getmFloor());
			shop.setmPartition(partition);
			shop.setmID(mId);
			
			IndoorSpace.iShops.add(shop);
		    
		    IndoorSpace.iPartitions.get(parId).setshop(shop);
		}
		
		// update floors
		int floorSize = IndoorSpace.iFloors.size();
		for (int i = 0; i < floorSize; i ++) {
			IndoorSpace.iFloors.get(i).updatemKeywords();
		}
		
		System.out.println("shops are attached to partitions!" + IndoorSpace.iShops.size() + " = " + Graph.Shops.size());
	}
	
	/**
	 * @throws IOException 
	 * 
	 */
	public static void randomAssign() throws IOException {
		String result = "";
		
//		int iwordSize = 0;
//		for (int i = 0; i < shopList.size(); i ++) {
//			int size = shopList.get(i).getmDescription().split("\t").length - 1;
//			if (size >= 1 && size <= 60) {
//				iwordSize ++;
//				System.out.println(shopList.get(i).getmDescription().split("\t").length - 1);
//			}
//			
//		}
//		
//		System.out.println("iwordSize = " + iwordSize);
		
		// randomly assign shop to each of the partitions
		int partitionSize = IndoorSpace.iPartitions.size();
		for (int i = 0; i < partitionSize; i ++) {
			Partition partition = IndoorSpace.iPartitions.get(i);
			if (partition.getmID() != i) System.out.println("something wrong_AssignShop_assignShop");
			
			if (partition.getmType() == RoomType.STORE) {
				
				int size = 0;
				int index = -1;
				do {
					index = Functions.randInt(0, shopList.size() - 1);
					size = shopList.get(index).getmDescription().split("\t").length - 1;
				} while(size < 1 || size > 60);
				
				Shop shop = new Shop(shopList.get(index));
				
//				System.out.println(i + "\t" + (shop.getmDescription().split("\t").length - 1));
				
				shop.setmFloor(partition.getmFloor());
				shop.setmPartition(partition);
				shop.setmID(DataGenConstant.mID_Shop++);
				
				
				// partition id, shop id, shop original id
				result = result + shop.getmPartition().getmID() + "\t" + shop.getmID() + "\t" + shop.getoriginalmID() + "\n";
				
//				System.out.println("Shop " + shop.getmID() + " " + shop.getmName() + " is attached to partition "
//						+ "" + shop.getmPartition().getmID() + " on floor " + shop.getmFloor() + " = " + shop.getmPartition().getmFloor());
				
			    IndoorSpace.iShops.add(shop);
			    Graph.Shops.put(shop.getmID(), shop);
			    
			    IndoorSpace.iPartitions.get(i).setshop(shop);
			    
			    if (!Graph.Partitions.get(i).equals(partition)) {
			    		System.out.println("somthing wrong_AssignShop_assignShop");
			    } else {
			    		Graph.Partitions.get(i).setshop(shop);
			    }
			}
		}
		
		// update floors
		int floorSize = IndoorSpace.iFloors.size();
		for (int i = 0; i < floorSize; i ++) {
			IndoorSpace.iFloors.get(i).updatemKeywords();
		}
		
		System.out.println("shops are attached to partitions!" + IndoorSpace.iShops.size() + " = " + Graph.Shops.size());
	    
		FileWriter fwDesc = new FileWriter(fileOutput);
		
		fwDesc.write(result);
		
		fwDesc.flush();
		fwDesc.close();
		
		saveDP();
		
	}

	public static void genShopList() throws IOException {
		Path path = Paths.get(fileInput);
		Scanner scanner = new Scanner(path);
		int currID = 0;
		//read line by line
		while(scanner.hasNextLine()){
		    //process each line
		    String line = scanner.nextLine();
		    String[] tempArr = line.split("\t");
		    
		    String keyword = Functions.arrayToString1D(tempArr, 7, tempArr.length);
		    
		    Shop shop = new Shop(currID++, tempArr[1], tempArr[2], tempArr[3], tempArr[4], tempArr[5], tempArr[6], keyword);
		    
		    shopList.add(shop);
		}
		
		
	}
	
	private static void saveDP() {
		try {
			FileWriter fwShop = new FileWriter("/opt/tomcat/webapps/IKAROS_w" + "/Shop.txt");
			Iterator<Shop> itrShop = IndoorSpace.iShops.iterator();
			while (itrShop.hasNext()) {
				fwShop.write(itrShop.next().toString() + "\n");
			}
			fwShop.flush();
			fwShop.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
	public ArrayList<Shop> getShopList(){ 
		return shopList;
	}
	
	public static void main(String[] args) throws IOException {
		AssignShop assignShop = new AssignShop();
		assignShop.assignShop();
		assignShop.randomAssign();
	}
}
